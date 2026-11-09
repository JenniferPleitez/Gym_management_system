package com.app.gym.servicios;

import java.time.Duration;
import java.time.OffsetDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.gym.excepciones.TokenExpiradoExcepcion;
import com.app.gym.excepciones.TokenInvalidoExcepcion;
import com.app.gym.modelos.AccountVerificationToken;
import com.app.gym.repositorios.AccountRepositorio;
import com.app.gym.repositorios.AccountVerificationTokenRepositorio;
import com.app.gym.seguridad.audit.RequestAuditContext;
import com.app.gym.servicios.cache.VerificationTokenCacheService;
import com.app.gym.utils.TokenGenerator;
import com.app.gym.utils.hashers.TokenHasher;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountVerificationTokenService {

    private final AccountVerificationTokenRepositorio verificationTokenRepositorio;
    private final TokenHasher tokenHasher;
    private final RateLimitService rateLimitService;
    private final AccountRepositorio accountRepositorio;
    private final VerificationTokenCacheService verificationTokenCacheService;

    public AccountVerificationTokenService(AccountVerificationTokenRepositorio verificationTokenRepositorio, 
                                            TokenHasher tokenHasher,
                                            RateLimitService rateLimitService,
                                            AccountRepositorio accountRepositorio,
                                            VerificationTokenCacheService verificationTokenCacheService) {
        this.verificationTokenRepositorio = verificationTokenRepositorio;
        this.tokenHasher = tokenHasher;
        this.rateLimitService = rateLimitService;
        this.accountRepositorio = accountRepositorio;
        this.verificationTokenCacheService = verificationTokenCacheService;
    }

    /* Valida un token de verificación de email. El ID fue previamente comprobado en Redis antes de pasarlo a este método y 
    aquí se re confirma (también en la DB)*/
    @Transactional
    public void verifyEmailToken(String realToken, Long accountId) {
        String tokenHash = tokenHasher.hashToken(realToken);
        
        String expectedToken = verificationTokenCacheService.getVerificationCache(accountId);

        if (expectedToken == null || !expectedToken.equals(tokenHash)) {
            throw new TokenExpiradoExcepcion("Expired or invalid token. Please, request a new one");
        }

        // Validar contra PostgreSQL.
        AccountVerificationToken dbToken = verificationTokenRepositorio.findByTokenHash(tokenHash)
                .orElseThrow(() -> new TokenInvalidoExcepcion("Token not found"));

        if (dbToken.isUsed() || dbToken.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new TokenInvalidoExcepcion("This token has already been used or has expired.");
        }

        // Marcar el token como usado
        dbToken.setUsed(true);
        verificationTokenRepositorio.save(dbToken);
    }

    // Validación previa que nos indica si el token es valido (porque si no está en caché es porque ya expiró) y además nos retorna el ID para seguir el flujo.
    public Long getInternalIdByValidToken(String realToken){
        String accountIdStr = verificationTokenCacheService.getTokenCache(realToken);

        if (accountIdStr == null) {
            throw new TokenExpiradoExcepcion("Expired or invalid token. Plesae, request a new one.");
        }

        Long accountId = Long.valueOf(accountIdStr);
        return accountId;
    }

    // Elimina las entradas de caché de Redis después de la verificación exitosa
    public void deleteCachesAfterVerification(Long accountId, String token) {
        verificationTokenCacheService.deleteVerificationCache(accountId);
        verificationTokenCacheService.deleteTokenCache(token);
    }

    // =================================================================
    // ENVIO DE TOKEN PARA VERIFICACIÓN DE CUENTA
    // =================================================================

    // Crea un nuevo token de verificación de cuenta.
    // TODO: Implementar que antes de crear un token se revise en Redis si hay alguno valido y devolver ese antes de crear otro.
    // TODO: Para eso se tiene que buscar la forma de retornar el token encriptado sin encriptar para que sea valido (como el TODO en refresh token en caso de parallel)
    @Transactional
    public String createVerificationToken(long id, String email) {
        OffsetDateTime now = OffsetDateTime.now();
        Duration tokenTtl = verificationTokenCacheService.getVerificationTtl();

        rateLimitService.checkAndIncrementVerifyEmailAttempts(email);
        rateLimitService.checkAndIncrementIpRateLimit(RequestAuditContext.getIp());

        String realToken = TokenGenerator.generateSecureToken();
        String tokenHash = tokenHasher.hashToken(realToken);
        OffsetDateTime expiresAt = now.plus(tokenTtl);

        AccountVerificationToken dbToken = new AccountVerificationToken();
        dbToken.setAccount(accountRepositorio.getReferenceById(id));
        dbToken.setTokenHash(tokenHash);
        dbToken.setExpiresAt(expiresAt);
        dbToken.setUsed(false);
        verificationTokenRepositorio.save(dbToken);

        return realToken;
    }

    @Transactional
    public void invalidateAllActiveToken(long id){
        verificationTokenRepositorio.invalidateAllActiveTokens(id, OffsetDateTime.now()); // Invalida tokens activos en PostgreSQL
    }

    // invalida cualquier token de Redis anterior para la cuenta cuenta dada (por eso pide accountId)
    public void removeOldTokenFromCache(Long accountId){
        verificationTokenCacheService.removeOldTokenFromCache(accountId);
    }

    // guarda los tokens recién creado en la caché de Redis. Son 2 tokens, porque uno es para buscar por accountId y otro por el token real.
    // El primer token es para validar rápidamente si un token es válido y obtener el accountId, y el segundo es para validar que el token coincide con el accountId.
    public void saveTokenToCache(Long accountId, String token){
        verificationTokenCacheService.saveTokenToCache(accountId, token);
    }
}
