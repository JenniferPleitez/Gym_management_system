package com.app.gym.servicios.cache;

import java.time.Duration;

import org.springframework.stereotype.Service;

import com.app.gym.modelos.properties.RateLimitProperties;
import com.app.gym.servicios.RedisService;
import com.app.gym.utils.hashers.KeyHasher;

@Service
public class RateLimitCacheService {
    private final RedisService redisService;
    private final RateLimitProperties rateLimitProperties;
    private final KeyHasher keyHasher;

    private final String GLOBAL_PREFIX;

    public RateLimitCacheService(RedisService redisService, RateLimitProperties rateLimitProperties, KeyHasher keyHasher) {
        this.redisService = redisService;
        this.rateLimitProperties = rateLimitProperties;
        this.keyHasher = keyHasher;
        this.GLOBAL_PREFIX = rateLimitProperties.globalPrefix();
    }

    // --------- INCREMENTING ---------
    public void incrementLoginFailCount(String email) {
        String key = buildLoginFailKey(email);
        Long currentCount = redisService.incrementValue(key);
        setExpiration(currentCount, key, getLoginFailWindow());
    }

    public void incrementPwdResetFailCount(String email) {
        String key = buildPwdResetKey(email);
        Long currentCount = redisService.incrementValue(key);
        setExpiration(currentCount, key, getPwdResetWindow());
    }

    public void incrementIpCount(String ip) {
        String key = buildIpKey(ip);
        Long currentCount = redisService.incrementValue(key);
        setExpiration(currentCount, key, getIpRateLimitWindow());
    }

    public void incrementVerifyEmailCount(String email) {
        String key = buildVerifyEmailKey(email);
        Long currentCount = redisService.incrementValue(key);
        setExpiration(currentCount, key, getVerifyEmailWindow());
    }

    public void incrementPwdChangeCount(Long id) {
        String key = buildPwdChangeKey(id);
        Long currentCount = redisService.incrementValue(key);
        setExpiration(currentCount, key, getPwdChangeWindow());
    }

    // --------- SETTING ---------
    public void setExpiration(Long currentCount, String key, Duration ttl) {
        // Set expiration only when the key is newly created
        if (currentCount == 1L) {
            redisService.setExpiration(key, ttl);
        }
    }

    // --------- GETTING ---------
    public String getLoginFailCount(String email) {
        String key = buildLoginFailKey(email);
        return redisService.getValue(key);
    }

    public String getPwdResetCount(String email) {
        String key = buildPwdResetKey(email);
        return redisService.getValue(key);
    }

    public String getVerifyEmailCount(String email) {
        String key = buildVerifyEmailKey(email);
        return redisService.getValue(key);
    }

    public String getPwdChangeCount(Long id) {
        String key = buildPwdChangeKey(id);
        return redisService.getValue(key);
    }

    public String getIpRateLimitCount(String ip) {
        String key = buildIpKey(ip);
        return redisService.getValue(key);
    }

    // --------- DELETING ---------
    public void resetLoginFailCount(String email) {
        String key = buildLoginFailKey(email);
        redisService.deleteValue(key);
    }

    // --------- HELPERS ---------
    private String buildLoginFailKey(String email) {
        String prefix = GLOBAL_PREFIX + rateLimitProperties.loginFailPrefix();
        String hashedEmail = hashEmail(email);
        return prefix + hashedEmail;
    }

    private String buildPwdResetKey(String email) {
        String prefix = GLOBAL_PREFIX + rateLimitProperties.pwdResetPrefix();
        String hashedEmail = hashEmail(email);
        return prefix + hashedEmail;
    }

    private String buildIpKey(String ip) {
        String prefix = GLOBAL_PREFIX + rateLimitProperties.ipPrefix();
        String hashedIp = keyHasher.hashKey(ip.trim());
        return prefix + hashedIp;
    }

    private String buildVerifyEmailKey(String email) {
        String prefix = GLOBAL_PREFIX + rateLimitProperties.verifyEmailPrefix();
        String hashedEmail = hashEmail(email);
        return prefix + hashedEmail;
    }

    private String buildPwdChangeKey(Long id) {
        String prefix = GLOBAL_PREFIX + rateLimitProperties.changePasswordPrefix();
        return prefix + id;
    }

    private Duration getLoginFailWindow() {
        return Duration.ofMinutes(rateLimitProperties.loginFailWindow());
    }

    private Duration getPwdResetWindow() {
        return Duration.ofMinutes(rateLimitProperties.pwdResetWindow());
    }

    private Duration getIpRateLimitWindow() {
        return Duration.ofMinutes(rateLimitProperties.ipWindow());
    }

    private Duration getVerifyEmailWindow() {
        return Duration.ofMinutes(rateLimitProperties.verifyEmailWindow());
    }

    private Duration getPwdChangeWindow() {
        return Duration.ofMinutes(rateLimitProperties.changePasswordWindow());
    }

    private String hashEmail(String email) {
        return keyHasher.hashKey(email.toLowerCase().trim());
    }

    // --------- GETTERS ---------
    public int getLoginFailLimit() {
        return rateLimitProperties.loginFailLimit();
    }

    public int getPwdResetLimit() {
        return rateLimitProperties.pwdResetLimit();
    }

    public int getVerifyEmailLimit() {
        return rateLimitProperties.verifyEmailLimit();
    }

    public int getPwdChangeLimit() {
        return rateLimitProperties.changePasswordLimit();
    }

    public int getIpLimit() {
        return rateLimitProperties.ipLimit();
    }
}
