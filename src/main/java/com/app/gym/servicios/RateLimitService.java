package com.app.gym.servicios;

import org.springframework.stereotype.Service;

import com.app.gym.excepciones.RateLimitException;
import com.app.gym.servicios.cache.RateLimitCacheService;

@Service
public class RateLimitService {

    private final RateLimitCacheService rateLimitCacheService;

    public RateLimitService(RateLimitCacheService rateLimitCacheService) {
        this.rateLimitCacheService = rateLimitCacheService;
    }

    // ---------------- MÉTODO GENÉRICO PARA VERIFICAR RATE LIMITS ----------------

    public void checkRateLimit(String countStr, int limit) {
        int count = countStr != null ? Integer.parseInt(countStr) : 0;
        if (count > limit) throw new RateLimitException("Many attempts. Please try again later.");
    }

    // ---------------- MÉTODOS ESPECÍFICOS PARA LOGIN FAILS ----------------

    public void registerLoginFailure(String email){
        rateLimitCacheService.incrementLoginFailCount(email);
    }

    public void checkLoginAttemps(String email){
        int limit = rateLimitCacheService.getLoginFailLimit();
        String countStr = rateLimitCacheService.getLoginFailCount(email);
        checkRateLimit(countStr, limit);
    }

    public void resetLoginFailures(String email){
        rateLimitCacheService.resetLoginFailCount(email);
    }

    // ---------------- MÉTODOS ESPECÍFICOS PARA PASSWORD RESET ----------------
    public void checkAndIncrementPasswordResetAttempts(String email){
        int limit = rateLimitCacheService.getPwdResetLimit();
        String countStr = rateLimitCacheService.getPwdResetCount(email);
        checkRateLimit(countStr, limit);

        rateLimitCacheService.incrementPwdResetFailCount(email);
    }

    // ---------------- MÉTODOS ESPECÍFICOS PARA VERIFY EMAIL ----------------
    public void checkAndIncrementVerifyEmailAttempts(String email){
        int limit = rateLimitCacheService.getVerifyEmailLimit();
        String countStr = rateLimitCacheService.getVerifyEmailCount(email);
        checkRateLimit(countStr, limit);

        rateLimitCacheService.incrementVerifyEmailCount(email);
    }

    // --------------- MÉTODO ESPECÍFICO PARA PASSWORD CHANGE ----------------
    public void checkAndIncrementPasswordChangeAttempts(Long id){
        int limit = rateLimitCacheService.getPwdChangeLimit();
        String countStr = rateLimitCacheService.getPwdChangeCount(id);
        checkRateLimit(countStr, limit);

        rateLimitCacheService.incrementPwdChangeCount(id);
    }

    // ---------------- MÉTODOS ESPECÍFICOS PARA IP RATE LIMITING ----------------
    public void checkAndIncrementIpRateLimit(String ip){
        int limit = rateLimitCacheService.getIpLimit();
        String countStr = rateLimitCacheService.getIpRateLimitCount(ip);
        checkRateLimit(countStr, limit);

        rateLimitCacheService.incrementIpCount(ip);
    }
}
