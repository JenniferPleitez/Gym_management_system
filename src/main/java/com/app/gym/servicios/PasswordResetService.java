package com.app.gym.servicios;

import java.time.Duration;
import java.time.OffsetDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.gym.excepciones.TokenExpiradoExcepcion;
import com.app.gym.excepciones.TokenInvalidoExcepcion;
import com.app.gym.modelos.PasswordResetToken;
import com.app.gym.repositorios.AccountRepositorio;
import com.app.gym.repositorios.PasswordResetTokenRepositorio;
import com.app.gym.seguridad.audit.RequestAuditContext;
import com.app.gym.servicios.cache.PasswordResetCacheService;
import com.app.gym.utils.TokenGenerator;
import com.app.gym.utils.hashers.TokenHasher;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepositorio passwordResetTokenRepositorio;
    private final TokenHasher tokenHasher;
    private final RateLimitService rateLimitService;
    private final PasswordResetCacheService passwordResetCacheService;
    private final AccountRepositorio accountRepositorio;

    public PasswordResetService(PasswordResetTokenRepositorio passwordResetTokenRepositorio,
                                TokenHasher tokenHasher,
                                RateLimitService rateLimitService,
                                PasswordResetCacheService passwordResetCacheService,
                                AccountRepositorio accountRepositorio) {
        this.passwordResetTokenRepositorio = passwordResetTokenRepositorio;
        this.tokenHasher = tokenHasher;
        this.rateLimitService = rateLimitService;
        this.passwordResetCacheService = passwordResetCacheService;
        this.accountRepositorio = accountRepositorio;
    }

    // Verifica que el token para cambiar la contraseña sea valido. Antes de entrar a este método se validó el accountId que tenga su caché valido en Redis,
    // si no dio error, se pasa a este método donde vuelve a revisar pero ahora que esté el token como tal (hasheado en redis) y en la DB, si todo está bien, continua el flujo
    @Transactional
    public void resetPasswordToken(String realToken, Long accountId) {
        String tokenHash = tokenHasher.hashToken(realToken);

        // Validar contra Redis
        String expectedToken = passwordResetCacheService.getAccountCache(accountId);
        if (expectedToken == null || !expectedToken.equals(tokenHash)) throw new TokenInvalidoExcepcion("Invalid or expired password reset token.");

        // Validar contra PostgreSQL
        PasswordResetToken dbToken = passwordResetTokenRepositorio.findByTokenHash(tokenHash)
                .orElseThrow(() -> new TokenInvalidoExcepcion("Invalid or expired password reset token."));

        if (dbToken.isUsed() || dbToken.getExpiresAt().isBefore(OffsetDateTime.now())){
            throw new TokenInvalidoExcepcion("Invalid or expired password reset token.");
        }

        dbToken.setUsed(true);
        passwordResetTokenRepositorio.save(dbToken);
    }

    // Validación previa que nos indica si el token es valido (porque si no está en caché es porque ya expiró) y además nos retorna el ID para seguir el flujo.
    public Long getInternalIdByValidToken(String realToken){
        String accountIdStr = passwordResetCacheService.getTokenCache(realToken);

        if (accountIdStr == null) {
            throw new TokenExpiradoExcepcion("Expired or invalid token. Plesae, request a new one.");
        }

        Long accountId = Long.valueOf(accountIdStr);
        return accountId;
    }

    // Elimina el token de reseteo de contraseña de la cache de Redis después de que se haya usado.
    public void deleteTokenFromCache(Long accountId, String token){
        passwordResetCacheService.deleteTokenCache(token);
        passwordResetCacheService.deleteAccountCache(accountId);
    }

    // =================================================================
    // CREACIÓN DE TOKEN PARA REINICIO DE CONTRASEÑA
    // =================================================================

    // proceso de reseteo de contraseña para un email.
    @Transactional
    public String createPasswordResetToken(String email, long accountId) {
        OffsetDateTime now = OffsetDateTime.now();
        Duration tokenTtl = passwordResetCacheService.getPasswordResetTtl();

        rateLimitService.checkAndIncrementPasswordResetAttempts(email);
        rateLimitService.checkAndIncrementIpRateLimit(RequestAuditContext.getIp());

        String realToken = TokenGenerator.generateSecureToken();
        String tokenHash = tokenHasher.hashToken(realToken);
        OffsetDateTime expiresAt = now.plus(tokenTtl);

        PasswordResetToken dbToken = new PasswordResetToken();
        dbToken.setAccount(accountRepositorio.getReferenceById(accountId));
        dbToken.setTokenHash(tokenHash);
        dbToken.setExpiresAt(expiresAt);
        dbToken.setUsed(false);
        passwordResetTokenRepositorio.save(dbToken);

        return realToken;
    }

    public void removeOldAndSaveNewToken(Long id, String token){
        passwordResetCacheService.removeOldTokenFromCache(id);
        passwordResetCacheService.saveTokenToCache(id, token);
    }

    @Transactional
    public void invalidateAllActiveTokens(Long accountId){
        passwordResetTokenRepositorio.invalidateAllActiveTokens(accountId, OffsetDateTime.now());
    }
}
