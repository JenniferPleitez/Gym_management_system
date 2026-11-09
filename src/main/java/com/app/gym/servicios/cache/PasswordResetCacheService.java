package com.app.gym.servicios.cache;

import java.time.Duration;

import org.springframework.stereotype.Service;

import com.app.gym.modelos.properties.TokenProperties;
import com.app.gym.servicios.RedisService;
import com.app.gym.utils.hashers.TokenHasher;

@Service
public class PasswordResetCacheService {
    private final TokenProperties tokenProperties;
    private final RedisService redisService;
    private final TokenHasher tokenHasher;

    private final Duration TOKEN_TTL;

    public PasswordResetCacheService(TokenProperties tokenProperties,
                                    RedisService redisService,
                                    TokenHasher tokenHasher){
        this.tokenProperties = tokenProperties;
        this.redisService = redisService;
        this.tokenHasher = tokenHasher;
        this.TOKEN_TTL = Duration.ofMinutes(tokenProperties.verifyTokenTtl());
    }

    //---------- SAVING ----------

    public void saveAccountCache(Long accountId, String token){
        String hashedToken = tokenHasher.hashToken(token);
        String key = buildAccountKey(accountId);
        redisService.setValue(key, hashedToken, TOKEN_TTL);
    }

    public void saveTokenCache(String token, Long accountId){
        String key = buildTokenKey(token);
        redisService.setValue(key, String.valueOf(accountId), TOKEN_TTL);
    }

    public void saveTokenToCache(Long accountId, String token){
        saveAccountCache(accountId, token);
        saveTokenCache(token, accountId);
    }
    
    //---------- GETTING ----------

    public String getAccountCache(Long accountId){
        String key = buildAccountKey(accountId);
        return redisService.getValue(key);
    }

    public String getTokenCache(String token){
        String key = buildTokenKey(token);
        return redisService.getValue(key);
    }

    //---------- DELETING ----------

    public void deleteTokenCache(String token){
        String key = buildTokenKey(token);
        redisService.deleteValue(key);
    }

    public void deleteAccountCache(Long accountId){
        String key = buildAccountKey(accountId);
        redisService.deleteValue(key);
    }

    public void removeOldTokenFromCache(Long accountId){
        String oldToken = getAccountCache(accountId);
        if (oldToken != null) {
            deleteTokenCache(oldToken);
        }
    }

    // Helpers
    private String buildTokenKey(String token){
        String hashedToken = tokenHasher.hashToken(token);
        return tokenProperties.pwdResetTokenPrefix() + hashedToken;
    }

    private String buildAccountKey(Long accountId){
        return tokenProperties.pwdResetPrefix() + accountId;
    }

    // Getter
    public Duration getPasswordResetTtl(){
        return TOKEN_TTL;
    }
}
