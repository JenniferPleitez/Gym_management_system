package com.app.gym.events.account.builders;

import java.util.Objects;

import org.springframework.lang.NonNull;

import com.app.gym.dtos.audit.RequestContext;
import com.app.gym.events.account.lifecycle.AccountVerifiedEmail;
import com.app.gym.events.account.lifecycle.PasswordChangedEvent;
import com.app.gym.events.account.lifecycle.ResetPasswordTokenVerifiedEvent;
import com.app.gym.seguridad.audit.RequestAuditContext;

/* 
* En este builder son construidos los eventos que de acciones ejecutadas por los usuarios sobre su propia cuenta.
* Como cambiar la contraseña, verificar su cuenta (luego de registrarse y por medio del correo que recibió), etc...
*/
public class AccountEventBuilder {
    private Long idAccount;
    String newPassword; 
    private String ip;
    private String userAgent;
    private String details;

    private AccountEventBuilder() {} 

    public static AccountEventBuilder create() {
        RequestContext ctx = RequestAuditContext.get();
        return new AccountEventBuilder()
            .withContext(ctx);
    }

    public static AccountEventBuilder withManualContext(String ip, String userAgent) {
        return new AccountEventBuilder()
            .withIp(ip)
            .withUserAgent(userAgent);
    }
    
    public AccountEventBuilder withContext(RequestContext ctx) {
        this.ip = ctx.ip();
        this.userAgent = ctx.userAgent();
        return this;
    }

    public AccountEventBuilder withIp(String ip) {
        this.ip = ip;
        return this;
    }
    
    public AccountEventBuilder withUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }
    
    public AccountEventBuilder forAccount(Long idAccount) {
        this.idAccount = idAccount;
        return this;
    }
    
    public AccountEventBuilder withDetails(String details) {
        this.details = details; // Puede ser null en algunos eventos.
        return this;
    }

    public AccountEventBuilder withPassword(String password) {
        this.newPassword = password; // Solo para el evento de reseteo de contraseña.
        return this;
    }
    
    @NonNull
    public AccountVerifiedEmail buildVerified() {
        validate();
        return new AccountVerifiedEmail(idAccount, ip, userAgent);
    }

    @NonNull
    public PasswordChangedEvent buildPasswordChanged() { // Contraseña cambiada estando autenticado.
        validate();
        return new PasswordChangedEvent(idAccount, ip, userAgent);
    }

    @NonNull
    public ResetPasswordTokenVerifiedEvent buildPasswordReset() { //Reseteo de contraseña desde "Olvidé mi contraseña".
        Objects.requireNonNull(newPassword, "The new password must be set.");
        validate();
        return new ResetPasswordTokenVerifiedEvent(idAccount, newPassword, ip, userAgent);
    }
    
    private void validate() {
        Objects.requireNonNull(idAccount, "Account ID must be set");
        Objects.requireNonNull(ip, "IP must be set");
        Objects.requireNonNull(userAgent, "UserAgent must be set");
    }
}
