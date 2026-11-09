package com.app.gym.utils;

import java.util.UUID;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import com.app.gym.excepciones.UnauthorizedException;
import com.app.gym.modelos.Account;
import com.app.gym.servicios.AccountService;
import com.app.gym.servicios.cache.AccountCacheService;

import lombok.extern.slf4j.Slf4j;

@Component
@RequestScope  // instancia por petición HTTP
@Slf4j
public class CurrentUser {
    private final AccountService accountService;
    private final AccountCacheService accountCacheService;

    private Account account;
    
    public CurrentUser(AccountService accountService, AccountCacheService accountCacheService) {
        this.accountService = accountService;
        this.accountCacheService = accountCacheService;
    }

    public Account getAccount() {
        if (account == null) {
            loadAccount();
        }

        return account;
    } 

    public Long getAccountIdByPublicId(UUID publicId){
        Long internalId = accountCacheService.getInternalIdByPublicId(publicId);
        
        if (internalId != null) {
            return internalId;
        }
        
        // Si no está entonces cargamos la cuenta completa y cacheamos el id
        Account account = getAccount(); 
        return account.getIdAccount();
    }

    public Long getAccountId() {
        UUID publicId = getAuthenticatedPublicId();
        
        Long internalId = accountCacheService.getInternalIdByPublicId(publicId);
        
        if (internalId != null) {
            return internalId;
        }
        
        // Si no está entonces cargamos la cuenta completa y cacheamos el id
        Account account = getAccount(); 
        return account.getIdAccount();
    }

    public String getAccountEmail(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        validateAuthentication(auth);

        Account account = getAccount();
        return account.getEmail();
    }
    
    // Intenta cargar la cuenta desde Redis y en caso no este lo hace desde la DB.
    private void loadAccount() {
        UUID publicId = getAuthenticatedPublicId();

        Account cachedAccount = accountCacheService.getAccountByPublicId(publicId);
        
        if (cachedAccount != null) {
            log.debug("Account obtained from Redis: {}", publicId);
            this.account = cachedAccount;
            return;
        }
        
        log.warn("There is no account in Redis, searching in DB: {}", publicId);
        this.account = accountService.findByPublicId(publicId);
        
        // Guardar en Redis para futuras requests
        accountCacheService.saveAccountCache(this.account);
        
        log.debug("Cuenta cached in Redis: {}", publicId);
    }

    private void validateAuthentication(Authentication authentication){
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UnauthorizedException("There is no authenticated account");
        }
    }

    private UUID getAuthenticatedPublicId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        validateAuthentication(auth);

        return UUID.fromString(auth.getName());
    }

}
