package com.app.gym.servicios.cache;

import java.time.Duration;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.app.gym.modelos.properties.TokenProperties;
import com.app.gym.servicios.RedisService;

@Service
public class AccessTokenCacheService {
    private final TokenProperties tokenProperties;
    private final RedisService redisService;

    private final Duration TOKEN_TTL;

    public AccessTokenCacheService(TokenProperties tokenProperties,
                                    RedisService redisService){
        this.tokenProperties = tokenProperties;
        this.redisService = redisService;
        this.TOKEN_TTL = Duration.ofMinutes(tokenProperties.accessTokenTtl());
    }
    //---------- SAVING ----------
    public void cacheAccountTokenVersion(UUID publicId, Integer tokenVersion){
        String key = buildVersionKey(publicId);
        redisService.setValue(key, String.valueOf(tokenVersion), TOKEN_TTL.plusMinutes(5)); // Extra buffer time para evitar expiraciones prematuras
    }

    //---------- GETTING ----------
    public String getTokenVersion(UUID publicId){
        String key = buildVersionKey(publicId);
        return redisService.getValue(key);
    }

    //---------- DELETING ----------
    public void deleteTokenVersion(Long accountId){
        String key = buildVersionKey(accountId);
        redisService.deleteValue(key);
    }

    //---------- HELPERS ----------
    private String buildVersionKey(UUID publicId){
        return tokenProperties.userVersionKey() + publicId;
    }

    private String buildVersionKey(Long accountId){
        return tokenProperties.userVersionKey() + accountId;
    }

    //---------- GETTERS ----------
    public Duration getTokenTtl() {
        return TOKEN_TTL;
    }
}
