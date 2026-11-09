package com.app.gym.events.auth.builders;

import com.app.gym.dtos.audit.RequestContext;
import com.app.gym.events.auth.login.LoginFailedEvent;
import com.app.gym.events.auth.login.LoginSuccessEvent;
import com.app.gym.events.auth.login.LogoutAllEvent;
import com.app.gym.seguridad.audit.RequestAuditContext;

import java.util.Objects;

import org.springframework.lang.NonNull;

public class LoginEventBuilder {
    private Long idAccount;
    private String subject;
    private String ip;
    private String userAgent;

    private LoginEventBuilder() {}

    public static LoginEventBuilder create() {
        RequestContext ctx = RequestAuditContext.get();
        return new LoginEventBuilder()
                .withContext(ctx);
    }

    public static LoginEventBuilder withManualContext(String ip, String userAgent) {
        return new LoginEventBuilder()
            .withIp(ip)
            .withUserAgent(userAgent);
    }

    public LoginEventBuilder withContext(RequestContext ctx) {
        this.ip = ctx.ip();
        this.userAgent = ctx.userAgent();
        return this;
    }

    public LoginEventBuilder withIp(String ip) {
        this.ip = ip;
        return this;
    }
    
    public LoginEventBuilder withUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public LoginEventBuilder forAccount(Long idAccount) {
        this.idAccount = idAccount;
        return this;
    }

    public LoginEventBuilder withSubject(String subject) {
        this.subject = subject;
        return this;
    }

    @NonNull
    public LoginSuccessEvent buildLoginSuccess() {
        validateWithId();
        return new LoginSuccessEvent(idAccount, ip, userAgent);
    }

    @NonNull
    public LoginFailedEvent buildLoginFailed() {
        validateWithSubject();
        return new LoginFailedEvent(subject, ip, userAgent);
    }

    @NonNull
    public LogoutAllEvent buildLogoutAll() {
        validateWithId();    
        return new LogoutAllEvent(idAccount, ip, userAgent);
    }

    // -------- HELPERS VALIDADORES --------

    public void validate(){
        Objects.requireNonNull(ip, "IP must be set");
        Objects.requireNonNull(userAgent, "UserAgent must be set");
    }

    public void validateWithId(){
        Objects.requireNonNull(idAccount, "Account ID must be set");
        validate();
    }

    public void validateWithSubject(){
        Objects.requireNonNull(subject, "Subject must be set");
        validate();
    }
}
