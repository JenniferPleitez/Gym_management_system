package com.app.gym.servicios;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.gym.excepciones.AppSecurityException;
import com.app.gym.excepciones.RecursoNoEncontradoExcepcion;
import com.app.gym.excepciones.TokenExpiradoExcepcion;
import com.app.gym.excepciones.TokenInvalidoExcepcion;
import com.app.gym.modelos.Account;
import com.app.gym.modelos.RefreshToken;
import com.app.gym.repositorios.RefreshTokenRepositorio;
import com.app.gym.seguridad.audit.RequestAuditContext;
import com.app.gym.servicios.cache.RefreshTokenCacheService;
import com.app.gym.utils.TokenGenerator;
import com.app.gym.utils.hashers.TokenHasher;


@Service
public class RefreshTokenService {

    private final RefreshTokenRepositorio refreshTokenRepositorio;
    private final SecurityIncidentService securityIncidentService;
    private final RefreshTokenCacheService refreshTokenCacheService;
    private final TokenHasher tokenHasher;
    
    public RefreshTokenService(RefreshTokenRepositorio refreshTokenRepositorio, 
                            SecurityIncidentService securityIncidentService,
                            RefreshTokenCacheService refreshTokenCacheService,
                            TokenHasher tokenHasher) {
        this.refreshTokenRepositorio = refreshTokenRepositorio;
        this.securityIncidentService = securityIncidentService;
        this.refreshTokenCacheService = refreshTokenCacheService;
        this.tokenHasher = tokenHasher;
    }

    @Transactional
    public String createRefreshToken(Account account) {
        String raw = TokenGenerator.generateSecureToken();
        String hash = tokenHasher.hashToken(raw);
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        RefreshToken refreshToken = toEntity(
            account, 
            hash, 
            now.plus(refreshTokenCacheService.getRefreshTokenTtl()), 
            RequestAuditContext.getIp(), 
            RequestAuditContext.getUserAgent(), 
            false
        );

        refreshTokenRepositorio.save(refreshToken);

        // Almacenar el hash en Redis para validaciones rápidas
        refreshTokenCacheService.saveHashCache(hash, refreshToken.getIdRefreshToken());

        // guardar hash por set por usuario. Esto es para: revocar todos los tokens de un usuario rápidamente.
        refreshTokenCacheService.saveUserHashSet(account.getIdAccount(), hash);

        return raw;
    }

    @Transactional
    public String rotateRefreshToken(String presentedRaw) {
        String hash = tokenHasher.hashToken(presentedRaw);
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        RefreshToken old = refreshTokenRepositorio.findByTokenHash(hash)
                .orElseThrow(() -> new TokenInvalidoExcepcion("Refresh token inválido"));

        //Si el token fue revocado, revisamos si es rotación paralela legal o si se está intentando reutilizar un token revocado
        if (old.isRevoked()) {
            String replacedRaw = refreshTokenCacheService.getReplacedRaw(hash);

            if(replacedRaw != null){
                securityIncidentService.handledRotateParallel(old.getAccount().getIdAccount()/*, old.getCreatedByIp(), old.getDeviceInfo()*/);
                return replacedRaw;
            }

            securityIncidentService.handledTokenTheft(old, now);
            throw new AppSecurityException("Possible malicious token use detected. Please log in again.");
        }

        if(old.getExpiresAt().isBefore(now)){
            securityIncidentService.revokeTokenExpired(old, hash);
            throw new TokenExpiradoExcepcion("Expired refresh token");
        }

        String newRaw = TokenGenerator.generateSecureToken();
        String newHash = tokenHasher.hashToken(newRaw);
        
        RefreshToken nuevo = toEntity(old.getAccount(), newHash, now.plus(refreshTokenCacheService.getRefreshTokenTtl()), 
                                        old.getCreatedByIp(), old.getDeviceInfo(), false);

        refreshTokenRepositorio.save(nuevo);

        old.setRevoked(true);
        old.setReplacedBy(nuevo);
        refreshTokenRepositorio.save(old);

        refreshTokenCacheService.deleteHashCache(hash);
        refreshTokenCacheService.saveHashCache(hash, nuevo.getIdRefreshToken());
        // TODO: encriptar el newRaw antes de guardarlo como reemplazo.
        refreshTokenCacheService.saveReplacedCache(hash, newRaw);
        refreshTokenCacheService.saveUserHashSet(old.getAccount().getIdAccount(), newHash);

        return newRaw;
    }

    @Transactional
    public void revokeRefreshTokenByRaw(String token) {
        String rawHash = tokenHasher.hashToken(token);

        String idStr = refreshTokenCacheService.getIdForRawHash(rawHash);

        if (idStr == null) {
            throw new TokenExpiradoExcepcion("Expired or invalid refresh token");
        }

        RefreshToken refreshToken = refreshTokenRepositorio.findByTokenHash(rawHash)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Invalid refresh token."));

        securityIncidentService.revokeTokenExpired(refreshToken, rawHash);
    }

    @Transactional
    public void revokeAllByAccount(Long accountId) {
        refreshTokenRepositorio.revokeAllByAccount(accountId);
        refreshTokenCacheService.revokeRedisTokensByAccount(accountId);
    }

    // obtener últimos inicios de sesión (device info)
    @Transactional(readOnly = true)
    public List<RefreshToken> findLatestByAccount(Long accountId, int limit) {
        return refreshTokenRepositorio.findLatestByAccount(accountId, PageRequest.of(0, limit));
    }

    @NonNull
    private RefreshToken toEntity(Account account, String hash, OffsetDateTime expiresAt, String ip, String deviceInfo, Boolean revoked) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setAccount(account);
        refreshToken.setTokenHash(hash);
        refreshToken.setExpiresAt(expiresAt);
        refreshToken.setCreatedByIp(ip);
        refreshToken.setDeviceInfo(deviceInfo);
        refreshToken.setRevoked(false);
        return refreshToken;
    }

    @Transactional(readOnly = true)
    public Account getAccountFromRefreshToken(String presentedRaw) {
        String hash = tokenHasher.hashToken(presentedRaw);

        RefreshToken refreshToken = refreshTokenRepositorio.findByTokenHash(hash)
                .orElseThrow(() -> new TokenInvalidoExcepcion("Invalid refresh token"));

        return refreshToken.getAccount();
    }
}
