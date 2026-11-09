package com.app.gym.events.account.builders;

import com.app.gym.dtos.audit.RequestContext;
import com.app.gym.events.account.admin.AccountDisabledEvent;
import com.app.gym.events.account.admin.AccountEnabledEvent;
import com.app.gym.events.account.admin.AccountLockedEvent;
import com.app.gym.events.account.admin.AccountUnlockedEvent;
import com.app.gym.events.account.admin.CreateAccountAdminEvent;
import com.app.gym.events.account.admin.PasswordChangedAdminEvent;
import com.app.gym.events.account.admin.UpdateAccountAdminEvent;
import com.app.gym.seguridad.audit.RequestAuditContext;

import java.util.Objects;

import org.springframework.lang.NonNull;

/* 
* En este builder son construidos los eventos que de acciones ejecutadas solo por ADMINISTRADORES. Como cambiar la contraseña de una account (desde un rol admin),
* actualizar una cuenta, etc...
*/
public class AccountAdminEventBuilder {
    private Long idAccount;
    private String adminEmail;
    private String ip;
    private String userAgent;
    private String details;

    private AccountAdminEventBuilder() {}

    public static AccountAdminEventBuilder create() {
        RequestContext ctx = RequestAuditContext.get();
        return new AccountAdminEventBuilder()
                .withContext(ctx);
    }

    public static AccountAdminEventBuilder withManualContext(String ip, String userAgent) {
        return new AccountAdminEventBuilder()
            .withIp(ip)
            .withUserAgent(userAgent);
    }

    public AccountAdminEventBuilder withContext(RequestContext ctx) {
        this.ip = ctx.ip();
        this.userAgent = ctx.userAgent();
        return this;
    }

    public AccountAdminEventBuilder withIp(String ip) {
        this.ip = ip;
        return this;
    }
    
    public AccountAdminEventBuilder withUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public AccountAdminEventBuilder forAccount(Long idAccount) {
        this.idAccount = idAccount;
        return this;
    }

    public AccountAdminEventBuilder byAdmin(String adminEmail) {
        this.adminEmail = adminEmail;
        return this;
    }

    public AccountAdminEventBuilder withDetails(String details) {
        this.details = details; // Puede ser null en algunos eventos.
        return this;
    }

    @NonNull
    public CreateAccountAdminEvent buildCreateAccount() {
        validate();
        return new CreateAccountAdminEvent(idAccount, adminEmail, ip, userAgent);
    }

    @NonNull
    public UpdateAccountAdminEvent buildUpdateAccount() {
        validate();
        return new UpdateAccountAdminEvent(idAccount, adminEmail, ip, userAgent);
    }

    @NonNull
    public PasswordChangedAdminEvent buildPasswordChanged() {
        validate();
        return new PasswordChangedAdminEvent(idAccount, adminEmail, ip, userAgent);
    }

    @NonNull
    public AccountDisabledEvent buildDisabled() {
        validateWithDetails();
        return new AccountDisabledEvent(idAccount, adminEmail, ip, userAgent, details);
    }

    @NonNull
    public AccountEnabledEvent buildEnabled() {
        validateWithDetails();
        return new AccountEnabledEvent(idAccount, adminEmail, ip, userAgent, details);
    }

    @NonNull
    public AccountLockedEvent buildLocked() {
        validateWithDetails();
        return new AccountLockedEvent(idAccount, adminEmail, ip, userAgent, details);
    }

    @NonNull
    public AccountUnlockedEvent buildUnlocked() {
        validateWithDetails();
        return new AccountUnlockedEvent(idAccount, adminEmail, ip, userAgent, details);
    }

    private void validate() {
        Objects.requireNonNull(idAccount, "Account ID must be set");
        Objects.requireNonNull(adminEmail, "Admin email must be set");
        Objects.requireNonNull(ip, "IP must be set");
        Objects.requireNonNull(userAgent, "UserAgent must be set");
    }

    private void validateWithDetails(){
        Objects.requireNonNull(details, "Details must be set when an account is disabled.");
        validate();
    }
}
