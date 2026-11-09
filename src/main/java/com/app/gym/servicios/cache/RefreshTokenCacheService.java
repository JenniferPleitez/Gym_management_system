package com.app.gym.servicios.cache;

import java.time.Duration;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.app.gym.modelos.properties.TokenProperties;
import com.app.gym.servicios.RedisService;

@Service
public class RefreshTokenCacheService {

    private final TokenProperties tokenProperties;
    private final RedisService redisService;
    
    private final Duration refreshTokenTtl;

    public RefreshTokenCacheService(TokenProperties tokenProperties, RedisService redisService){
        this.tokenProperties = tokenProperties;
        this.redisService = redisService;
        this.refreshTokenTtl = Duration.ofDays(tokenProperties.refreshTokenTtl());
    }

    public void revokeRedisTokensByAccount(Long accountId) {

        String setKey = buildUserHashSet(accountId); // set de hashes

        Set<String> hashes = redisService.getSetMembers(setKey);
        if (hashes != null) {
            for (String hash : hashes) {
                redisService.deleteValue(buildRefreshKey(hash));
            }
        }

        // borramos el set de hashes vacio.
        redisService.deleteValue(setKey);
    }

    public void revokeRedisTokenExpired(String hash){
        redisService.deleteValue(buildRefreshKey(hash));
    }

    public void saveHashCache(String hash, Long id){
        String idStr = String.valueOf(id);
        redisService.setValue(buildRefreshKey(hash), idStr, refreshTokenTtl);
    }

    // TODO: encriptar el newRaw antes de guardarlo como reemplazo.
    public void saveReplacedCache(String hash, String newRaw){
        redisService.setValue(buildReplacedKey(hash), newRaw, getParellelWindow());
    }

    public void saveUserHashSet(Long id, String hash){
        redisService.addToSet(buildUserHashSet(id), hash, refreshTokenTtl);
    }

    // TODO: desencriptar el raw antes de retornarlo.
    public String getReplacedRaw(String hash){
        return redisService.getValue(buildReplacedKey(hash));
    }

    public String getIdForRawHash(String rawHash){
        return redisService.getValue(buildRefreshKey(rawHash));
    }

    public void deleteHashCache(String hash){
        redisService.deleteValue(buildRefreshKey(hash));
    }

    // Helpers
    private String buildRefreshKey(String hash){
        return tokenProperties.refreshHashKey() + hash;
    }

    private String buildReplacedKey(String hash) {
        return tokenProperties.refreshReplacedKey() + hash;
    }

    private String buildUserHashSet(Long id) {
        return tokenProperties.refreshIdsPrefix() + id;
    }

    private Duration getParellelWindow(){
        return Duration.ofMillis(tokenProperties.parellelWindow());
    }

    // Getter
    public Duration getRefreshTokenTtl(){
        return this.refreshTokenTtl;
    }
}
