package com.app.gym.servicios.cache;

import java.time.Duration;

import org.springframework.stereotype.Service;

import com.app.gym.modelos.properties.TokenProperties;
import com.app.gym.servicios.RedisService;
import com.app.gym.utils.hashers.TokenHasher;

@Service
public class VerificationTokenCacheService {
    private final TokenProperties tokenProperties;
    private final RedisService redisService;
    private final TokenHasher tokenHasher;

    private final Duration verificationTtl;

    public VerificationTokenCacheService(TokenProperties tokenProperties,
                                        RedisService redisService,
                                        TokenHasher tokenHasher){
        this.tokenProperties = tokenProperties;
        this.redisService = redisService;
        this.tokenHasher = tokenHasher;
        this.verificationTtl = Duration.ofMinutes(tokenProperties.verifyTokenTtl());
    }

    //---------- SAVING ----------

    public void saveVerificationCache(Long accountId, String token){
        String hashedToken = tokenHasher.hashToken(token);
        String key = buildVerificationKey(accountId);
        redisService.setValue(key, hashedToken, getVerificationTtl());
    }

    public void saveTokenCache(String token, Long accountId){
        String key = buildTokenKey(token);
        redisService.setValue(key, String.valueOf(accountId), getVerificationTtl());
    }

    public void saveTokenToCache(Long accountId, String token){
        saveVerificationCache(accountId, token);
        saveTokenCache(token, accountId);
    }

    //---------- GETTING ----------

    public String getTokenCache(String token){
        String key = buildTokenKey(token);
        return redisService.getValue(key);
    }

    public String getVerificationCache(Long accountId){
        String key = buildVerificationKey(accountId);
        return redisService.getValue(key);
    }

    //---------- DELETING ----------

    public void deleteTokenCache(String token){
        String key = buildTokenKey(token);
        redisService.deleteValue(key);
    }

    public void deleteVerificationCache(Long accountId){
        String key = buildVerificationKey(accountId);
        redisService.deleteValue(key);
    }

    public void removeOldTokenFromCache(Long accountId){
        String oldToken = getVerificationCache(accountId);
        if (oldToken != null) {
            deleteTokenCache(oldToken);
        }
    }

    // Helpers
    private String buildVerificationKey(Long accountId){
        return tokenProperties.verifyPrefix() + accountId;
    }

    private String buildTokenKey(String token){
        String hashedToken = tokenHasher.hashToken(token);
        return tokenProperties.verifyTokenPrefix() + hashedToken;
    }

    // Getter
    public Duration getVerificationTtl() {
        return verificationTtl;
    }
}
