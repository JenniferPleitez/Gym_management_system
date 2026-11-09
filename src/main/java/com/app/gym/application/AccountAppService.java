package com.app.gym.application;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.gym.dtos.account.AccountRequestAdminDTO;
import com.app.gym.dtos.account.AccountResponseDTO;
import com.app.gym.dtos.account.AccountUpdateAdminDTO;
import com.app.gym.dtos.account.UpdateEmailDto;
import com.app.gym.dtos.account.UpdateLockDTO;
import com.app.gym.dtos.account.UpdatePasswordAdminDTO;
import com.app.gym.dtos.account.UpdatePasswordDto;
import com.app.gym.dtos.account.UpdateStatusDTO;
import com.app.gym.events.EventBuilders;
import com.app.gym.mappers.AccountMapper;
import com.app.gym.modelos.Account;
import com.app.gym.servicios.AccountService;
import com.app.gym.servicios.AccountVerificationTokenService;
import com.app.gym.utils.CurrentUser;

@Service
public class AccountAppService {
    
    private final AccountService accountService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AccountVerificationTokenService accountVerificationTokenService;
    private final AccountMapper accountMapper;
    private final CurrentUser currentUser;

    public AccountAppService(AccountService accountService,
                            AccountVerificationTokenService accountVerificationTokenService,
                            ApplicationEventPublisher applicationEventPublisher,
                            AccountMapper accountMapper,
                            CurrentUser currentUser){
        this.accountService = accountService;
        this.accountVerificationTokenService = accountVerificationTokenService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.accountMapper = accountMapper;
        this.currentUser = currentUser;
    }

    // ----------------------------------------------------------------
    // Create account (Only admins)
    // ----------------------------------------------------------------
    @Transactional
    public AccountResponseDTO createAccount(AccountRequestAdminDTO accountRequestDto){
        String adminEmail = currentUser.getAccountEmail();
        Account createdAccount = accountService.createAccount(accountRequestDto);

        applicationEventPublisher.publishEvent(
            EventBuilders.accountAdmin()
            .forAccount(createdAccount.getIdAccount())
            .byAdmin(adminEmail)
            .buildCreateAccount()
        );

        return accountMapper.toResponseDto(createdAccount);
    }

    @Transactional
    public AccountResponseDTO updateAccount(AccountUpdateAdminDTO accountUpdateAdminDto){
        String adminEmail = currentUser.getAccountEmail();
        Account updatedAccount = accountService.updateAccoundByAdmin(accountUpdateAdminDto.publicId(), accountUpdateAdminDto);

        applicationEventPublisher.publishEvent(
            EventBuilders.accountAdmin()
            .forAccount(updatedAccount.getIdAccount())
            .byAdmin(adminEmail)
            .buildUpdateAccount()
        );

        return accountMapper.toResponseDto(updatedAccount);
    }

    @Transactional
    public String updatePassword(UpdatePasswordDto updatePasswordDto){
        Long accountId = currentUser.getAccountId();
        accountService.updatePassword(accountId, updatePasswordDto.newPassword());

        applicationEventPublisher.publishEvent(
            EventBuilders.account()
            .forAccount(accountId)
            .buildPasswordChanged()
        );

        return("Password has been successfully updated");
    }

    @Transactional
    public String updateEmail(UpdateEmailDto updateEmailDto){
        Long accountId = currentUser.getAccountId();
        accountService.updateEmail(accountId, updateEmailDto.email());
        accountService.incrementarTokenVersion(accountId);

        String token = accountVerificationTokenService.createVerificationToken(accountId, updateEmailDto.email());
        accountVerificationTokenService.saveTokenToCache(accountId, token);

        applicationEventPublisher.publishEvent(
            EventBuilders.accountToken()
            .forAccount(accountId)
            .withToken(token)
            .buildUpdateEmail()
        );

        return("Email has been successfully updated. Please, verify your new email address.");
    }

    @Transactional
    public String updatePasswordAdmin(UpdatePasswordAdminDTO updatePasswordAdminDTO){
        String adminEmail = currentUser.getAccountEmail();
        Long targetId = currentUser.getAccountIdByPublicId(updatePasswordAdminDTO.publicId());

        accountService.updatePassword(targetId, updatePasswordAdminDTO.newPassword());

        applicationEventPublisher.publishEvent(
            EventBuilders.accountAdmin()
            .forAccount(targetId)
            .byAdmin(adminEmail)
            .buildPasswordChanged()
        );

        return("Password has been successfully updated by an administrator");
    }

    @Transactional
    public AccountResponseDTO updateAccountStatus(UpdateStatusDTO updateStatusDTO){
        String adminEmail = currentUser.getAccountEmail();
        Account updatedStatusAccount = accountService.actualizarEstadoCuenta(updateStatusDTO.publicId(), updateStatusDTO.enabled());

        if(!updateStatusDTO.enabled()){
            accountService.incrementarTokenVersion(updatedStatusAccount.getIdAccount());

            applicationEventPublisher.publishEvent(
                EventBuilders.accountAdmin()
                .forAccount(updatedStatusAccount.getIdAccount())
                .byAdmin(adminEmail)
                .withDetails(updateStatusDTO.reason())
                .buildDisabled()
            );

            return accountMapper.toResponseDto(updatedStatusAccount);
        }

        applicationEventPublisher.publishEvent(
            EventBuilders.accountAdmin()
            .forAccount(updatedStatusAccount.getIdAccount())
            .byAdmin(adminEmail)
            .withDetails(updateStatusDTO.reason())
            .buildEnabled()
        );

        return accountMapper.toResponseDto(updatedStatusAccount);
    }

    @Transactional
    public AccountResponseDTO updateAccountLock(UpdateLockDTO updateLockDTO){
        String adminEmail = currentUser.getAccountEmail();
        Account updatedLockedAccount = accountService.actualizarBloqueoCuenta(updateLockDTO.publicId(), updateLockDTO.locked());

        if(updateLockDTO.locked()){
            accountService.incrementarTokenVersion(updatedLockedAccount.getIdAccount());

            applicationEventPublisher.publishEvent(
                EventBuilders.accountAdmin()
                .forAccount(updatedLockedAccount.getIdAccount())
                .byAdmin(adminEmail)
                .withDetails(updateLockDTO.reason())
                .buildLocked()
            );

            return accountMapper.toResponseDto(updatedLockedAccount);
        }

        applicationEventPublisher.publishEvent(
            EventBuilders.accountAdmin()
            .forAccount(updatedLockedAccount.getIdAccount())
            .byAdmin(adminEmail)
            .withDetails(updateLockDTO.reason())
            .buildUnlocked()
        );

        return accountMapper.toResponseDto(updatedLockedAccount);
    }

    @Transactional(readOnly = true)
    public List<AccountResponseDTO> getAccounts(){
        return accountMapper.toResponseDto(accountService.listarUsuarios());
    }

    @Transactional(readOnly = true)
    public AccountResponseDTO getAccountById(Long id){
        return accountMapper.toResponseDto(accountService.getAccountById(id));
    }
}
