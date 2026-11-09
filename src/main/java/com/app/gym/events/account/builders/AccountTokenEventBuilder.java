package com.app.gym.events.account.builders;

import com.app.gym.dtos.audit.RequestContext;
import com.app.gym.events.account.lifecycle.CreateAccountEvent;
import com.app.gym.events.account.token.RequestResetPasswordEvent;
import com.app.gym.events.account.token.ResendAccountVerificationEvent;
import com.app.gym.events.account.token.UpdateEmailEvent;
import com.app.gym.seguridad.audit.RequestAuditContext;

import java.util.Objects;

import org.springframework.lang.NonNull;

/* 
* En este builder son construidos los eventos que contienen un Token, como resetear la contraseña, actualizar el email (para verificar el nuevo),
* etc...
*/
public class AccountTokenEventBuilder {
    private Long idAccount;
    private String subject;
    private String token;
    private String ip;
    private String userAgent;

    private AccountTokenEventBuilder() {}

    public static AccountTokenEventBuilder create() {
        RequestContext ctx = RequestAuditContext.get();
        return new AccountTokenEventBuilder()
                .withContext(ctx);
    }

    public AccountTokenEventBuilder withContext(RequestContext ctx) {
        this.ip = ctx.ip();
        this.userAgent = ctx.userAgent();
        return this;
    }

    public AccountTokenEventBuilder forAccount(Long idAccount) {
        this.idAccount = idAccount;
        return this;
    }
    
    public AccountTokenEventBuilder bySubject(String subject) {
        this.subject = subject;
        return this;
    }

    public AccountTokenEventBuilder withToken(String token) {
        this.token = token;
        return this;
    }

    @NonNull
    public CreateAccountEvent buildCreateAccount() {
        validateWithId();
        return new CreateAccountEvent(idAccount, token, ip, userAgent);
    }

    @NonNull
    public ResendAccountVerificationEvent buildResendVerification() {
        validateWithId();
        return new ResendAccountVerificationEvent(idAccount, token, ip, userAgent);
    }

    @NonNull
    public RequestResetPasswordEvent buildRequestResetPassword() {
        validateWithSubject(subject);
        return new RequestResetPasswordEvent(subject, token, ip, userAgent);
    }

    @NonNull
    public UpdateEmailEvent buildUpdateEmail() {
        validateWithId();
        return new UpdateEmailEvent(idAccount, token, ip, userAgent);
    }

    private void validate() {
        Objects.requireNonNull(token, "Token must be set");
        Objects.requireNonNull(ip, "IP must be set");
        Objects.requireNonNull(userAgent, "UserAgent must be set");
    }

    private void validateWithId(){
        Objects.requireNonNull(idAccount, "Account ID must be set");
        validate();
    }

    private void validateWithSubject(String email){
        Objects.requireNonNull(email, "Subject must be set");
        validate();
    }
}
