package com.app.gym.servicios.workflow;

import org.springframework.stereotype.Service;

import com.app.gym.servicios.AccountService;
import com.app.gym.servicios.cache.AccountCacheService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CacheInvalidationWorklow {
    private final AccountService accountService;;
    private final AccountCacheService accountCacheService;

    public CacheInvalidationWorklow(AccountCacheService accountCacheService, AccountService accountService){
        this.accountCacheService = accountCacheService;
        this.accountService = accountService;
    }

    public void executeCacheInvalidateAndRecache(Long accountId){
        try{accountCacheService.invalidateAndRecache(
                accountId,
                () -> accountService.getAccountById(accountId)
            );
        } catch (Exception e)
        {
            log.error("Error attempting to invalidate and recache the account");
            executeInvalidateCache(accountId);
        }
    }

    public void executeInvalidateCache(Long accountId){
        accountCacheService.invalidate(accountId);
    }
}
