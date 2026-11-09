package com.app.gym.servicios;

import java.time.OffsetDateTime;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.app.gym.events.EventBuilders;
import com.app.gym.modelos.RefreshToken;
import com.app.gym.repositorios.RefreshTokenRepositorio;
import com.app.gym.servicios.cache.RefreshTokenCacheService;

@Service
public class SecurityIncidentService {
    
    private final RefreshTokenRepositorio refreshTokenRepositorio;
    private final RefreshTokenCacheService refreshTokenCacheService;
    private final AccountService accountService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public SecurityIncidentService(RefreshTokenRepositorio refreshTokenRepositorio, 
                                ApplicationEventPublisher applicationEventPublisher, 
                                RefreshTokenCacheService refreshTokenCacheService,
                                AccountService accountService ){
        this.refreshTokenRepositorio = refreshTokenRepositorio;
        this.applicationEventPublisher = applicationEventPublisher;
        this.refreshTokenCacheService = refreshTokenCacheService;
        this.accountService = accountService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handledTokenTheft(RefreshToken old, OffsetDateTime now){
        Long idAccount = old.getAccount().getIdAccount();

        refreshTokenRepositorio.revokeAllByAccount(idAccount);
        refreshTokenCacheService.revokeRedisTokensByAccount(idAccount);
        accountService.incrementarTokenVersion(idAccount);

        applicationEventPublisher.publishEvent(
            EventBuilders.refreshToken()
            .forAccount(idAccount)
            .withManualContext(old.getCreatedByIp(), old.getDeviceInfo())
            .buildRefreshTheft()
        );
    }

    @Transactional
    public void handledRotateParallel(Long id){
        applicationEventPublisher.publishEvent(
            EventBuilders.refreshToken()
            .forAccount(id)
            .buildRotateParallel()
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revokeTokenExpired(RefreshToken old, String hash){
        old.setRevoked(true);
        refreshTokenRepositorio.save(old);
        refreshTokenCacheService.revokeRedisTokenExpired(hash);
    }
}
