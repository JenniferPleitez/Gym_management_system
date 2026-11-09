package com.app.gym.servicios.cache;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.app.gym.dtos.auth.CacheAccountDTO;
import com.app.gym.excepciones.InvalidCacheDataException;
import com.app.gym.mappers.AccountMapper;
import com.app.gym.modelos.Account;
import com.app.gym.modelos.enums.CacheErrorKind;
import com.app.gym.modelos.properties.AccountProperties;
import com.app.gym.modelos.properties.TokenProperties;
import com.app.gym.servicios.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.core.instrument.Timer.Sample;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountCacheService {
    private final RedisService redisService;
    private final TokenProperties tokenProperties;
    private final AccountProperties accountProperties;
    private final ObjectMapper objectMapper;
    private final AccountMapper accountMapper;
    private final CacheMetricsService metricsService;

    private final Executor cacheExecutor;
    private final Duration TOKEN_TTL;
    private final String GLOBAL_PREFIX;

    public AccountCacheService(RedisService redisService, 
                                TokenProperties tokenProperties, 
                                AccountProperties accountProperties, 
                                ObjectMapper objectMapper,
                                AccountMapper accountMapper,
                                CacheMetricsService metricsService,
                                @Qualifier("cacheExecutor") Executor cacheExecutor){
        this.redisService = redisService;
        this.tokenProperties = tokenProperties;
        this.accountProperties = accountProperties;
        this.objectMapper = objectMapper;
        this.accountMapper = accountMapper;
        this.metricsService = metricsService;

        this.cacheExecutor = cacheExecutor;
        this.TOKEN_TTL = Duration.ofMinutes(tokenProperties.accessTokenTtl());
        this.GLOBAL_PREFIX = accountProperties.globalPrefix();
    }

    //---------- INCREMENTING ----------
    public void incrementRedisTokenVersion(UUID publicId, Integer tokenVersion){
        String key = buildVersionKey(publicId);
        redisService.setValue(key, String.valueOf(tokenVersion), TOKEN_TTL.plusMinutes(5)); // Extra buffer time para evitar expiraciones prematuras
    }

    //--------- GET CACHING ACCOUNT ---------
    public Account getAccountByPublicId(UUID publicId) {
        Long accountId = getInternalIdByPublicId(publicId);
        String key = buildAccountKey(accountId);
        String json = redisService.getValue(key);

        if (json == null) {
            metricsService.recordCacheMiss();
            return null;
        }

        try {
            // Deserializar Account
            CacheAccountDTO cachedAccount = objectMapper.readValue(json, CacheAccountDTO.class);

            validateCacheDTO(cachedAccount);
            metricsService.recordCacheHit();
            return accountMapper.cacheToAccount(cachedAccount);
            
        } catch (Exception e) {
            log.error("Error reading account from Redis: {}", publicId, e);
            // Si falló es porque el caché está corrupto, mejor lo eliminamos y hacemos fallback sin romper el flujo para que luego se revise en la db
            deleteCacheAccount(accountId);
            deleteIdAccount(publicId);
            metricsService.recordCacheError(CacheErrorKind.DESERIALIZATION);
            return null;
        }
    }

    public Long getInternalIdByPublicId(UUID publicId) {
        String key = buildIdKey(publicId);
        String idStr = redisService.getValue(key);
        return idStr != null ? Long.parseLong(idStr) : null;
    }

    //--------- SAVING ACCOUNT ----------
    public void saveAccountCache(Account account){
        UUID publicId = account.getPublicId(); 
        Long accountId = account.getIdAccount(); 

        String key = buildAccountKey(accountId);
        boolean existed = redisService.existsKey(key);

        try{
            CacheAccountDTO dto = accountMapper.accountToCache(account);

            String json = objectMapper.writeValueAsString(dto);
            redisService.setValue(key, json, getAccountTtl());
            
            // cacheamos el internalId
            cacheIdMapping(publicId, account.getIdAccount());

            if(!existed) {
                metricsService.recordCacheWrite();
            } else {
                metricsService.recordCacheUpdate();
            }
            
        } catch(JsonProcessingException e){
            log.error("Error serializing account: {} : {}", account.getPublicId(), e);
            metricsService.recordCacheError(CacheErrorKind.SERIALIZATION);
        }
    }

    //--------- SAVING ACCOUNT ID ---------
    public void cacheIdMapping(UUID publicId, Long id) {
        String idStr = id.toString();
        String key = buildIdKey(publicId);

        redisService.setValue(key, idStr, getAccountTtl());
    }

    // =============================================
    // INVALIDACIÓN INMEDIATA (Síncrona)
    // =============================================
    public void invalidateImmediately(UUID publicId, Long accountId) {
        try {
            deleteCacheAccount(accountId);
            deleteIdAccount(publicId);
        } catch (Exception e) {
            log.error("Critical error invalidating cache: {}", accountId, e);
            metricsService.recordCacheError(CacheErrorKind.INVALIDATION);
        }
    }

    // =============================================
    // INVALIDACIÓN NORMAL (Asíncrona)
    // =============================================
    public void invalidate(Long accountId) {
        try {
            deleteCacheAccount(accountId);
        } catch (Exception e) {
            log.warn("Error invalidating cache: (no critical): {}", accountId);
        }
    }

    // =============================================
    // INVALIDACIÓN + RECACHEO 
    // =============================================
    
    public void invalidateAndRecache(Long accountId, Supplier<Account> accountLoader) {
        invalidate(accountId);
        
        Sample timerSample = metricsService.startCacheLoadTimer();

            CompletableFuture
                .supplyAsync(accountLoader, cacheExecutor)
                .thenAccept(account -> {
                    if (account != null) {
                        saveAccountCache(account);
                    }
                })
                .exceptionally(ex -> {
                    log.error("Error recaching account {}", accountId, ex);
                    metricsService.recordCacheError(CacheErrorKind.RECACHING);
                    return null;
                })
                .thenRun(() -> metricsService.stopCacheLoadTimer(timerSample));
    }

    // =============================================
    // UPDATE PARCIAL DE CACHE
    // =============================================
    public void updateLastLoginOnly(Long accountId, OffsetDateTime lastLogin, Integer expectedTokenVersion) {
        String key = buildAccountKey(accountId);
        String json = redisService.getValue(key);
        
        if (json != null) {
            try {
                CacheAccountDTO dto = objectMapper.readValue(json, CacheAccountDTO.class);

                // Para evitar race conditions. Si el token version no concuerda con el serializado en el dto, se invalida para no revivir caché viejo en caso de
                // que un thread invalide o actualice la cuenta y este método vuelva a revivirla con los datos anteriores
                if (!expectedTokenVersion.equals(dto.tokenVersion())) {
                    log.debug(
                        "Skip cache update for {} due tokenVersion mismatch (expected={}, cached={})",
                        accountId, expectedTokenVersion, dto.tokenVersion()
                    );
                    invalidate(accountId);
                    return;
                }

                CacheAccountDTO updatedAccountDTO = accountMapper.updateLastLogin(dto, lastLogin);
                
                // Guardar de nuevo
                String updatedJson = objectMapper.writeValueAsString(updatedAccountDTO);
                redisService.setValue(key, updatedJson, getAccountTtl());
                log.debug("Cache updated (Only lastLogin): {}", accountId);
                
            } catch (Exception e) {
                log.warn("The cache could not be updated (lastlogin): {}", accountId);
                metricsService.recordCacheError(CacheErrorKind.UPDATING);
                invalidate(accountId);
            }
        }
    }

    //-------- DELETING ALL ACCOUNT DATA FROM CACHE ----------
    public void deleteCacheAccount(Long accountId){
        String key = buildAccountKey(accountId);

        metricsService.recordCacheEviction();
        redisService.deleteValue(key);
    }

    public void deleteIdAccount(UUID publicId){
        String idKey = buildIdKey(publicId);

        redisService.deleteValue(idKey);
    }

    //---------- HELPERS ----------
    private String buildVersionKey(UUID publicId){
        return tokenProperties.userVersionKey() + publicId;
    }
    
    private String buildAccountKey(Long accountId){
        String prefix = GLOBAL_PREFIX + accountProperties.cachePrefix();
        return prefix + accountId;
    }

    private String buildIdKey(UUID publicId){
        String prefix = GLOBAL_PREFIX + accountProperties.idPrefix();
        return prefix + publicId;
    }

    private Duration getAccountTtl(){
        return Duration.ofMinutes(accountProperties.cacheTtl());
    }

    // HELPER FOR VALIDATION
    private void validateCacheDTO(CacheAccountDTO dto) {
        if (dto.idAccount() == null || dto.publicId() == null || dto.email() == null || dto.tokenVersion() == null) {
            throw new InvalidCacheDataException("Invalid cache dto: missing required fields");
        }
    }
}
