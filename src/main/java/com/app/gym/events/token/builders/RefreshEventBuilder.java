package com.app.gym.events.token.builders;

import java.util.Objects;

import org.springframework.lang.NonNull;

import com.app.gym.dtos.audit.RequestContext;
import com.app.gym.events.token.refresh.RefreshRotatedEvent;
import com.app.gym.events.token.refresh.RotateParallelEvent;
import com.app.gym.events.token.refresh.TokenTheftDetectedEvent;
import com.app.gym.events.token.refresh.TokensRevokedEvent;
import com.app.gym.seguridad.audit.RequestAuditContext;

public class RefreshEventBuilder {
    private Long idAccount;
    private String ip;
    private String userAgent;

    private RefreshEventBuilder() {} 

    public static RefreshEventBuilder create() {
        RequestContext ctx = RequestAuditContext.get();
        return new RefreshEventBuilder()
            .withContext(ctx);
    }

    public RefreshEventBuilder withManualContext(String ip, String userAgent) {
        return new RefreshEventBuilder()
            .withIp(ip)
            .withUserAgent(userAgent);
    }
    
    public RefreshEventBuilder withContext(RequestContext ctx) {
        this.ip = ctx.ip();
        this.userAgent = ctx.userAgent();
        return this;
    }

    public RefreshEventBuilder withIp(String ip) {
        this.ip = ip;
        return this;
    }
    
    public RefreshEventBuilder withUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }
    
    public RefreshEventBuilder forAccount(Long idAccount) {
        this.idAccount = idAccount;
        return this;
    }

    @NonNull
    public RefreshRotatedEvent buildRefreshRotate() { 
        validate();
        return new RefreshRotatedEvent(idAccount, ip, userAgent);
    }

    @NonNull
    public RotateParallelEvent buildRotateParallel() { 
        validate();
        return new RotateParallelEvent(idAccount, ip, userAgent);
    }

    @NonNull
    public TokensRevokedEvent buildRefreshRevoked() { 
        validate();
        return new TokensRevokedEvent(idAccount, ip, userAgent);
    }

    @NonNull
    public TokenTheftDetectedEvent buildRefreshTheft() { 
        validate();
        return new TokenTheftDetectedEvent(idAccount, ip, userAgent);
    }

    private void validate() {
        Objects.requireNonNull(idAccount, "Account ID must be set");
        Objects.requireNonNull(ip, "IP must be set");
        Objects.requireNonNull(userAgent, "UserAgent must be set");
    }
}
