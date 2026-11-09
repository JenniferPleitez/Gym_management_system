package com.app.gym.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.app.gym.events.auth.login.LoginFailedEvent;
import com.app.gym.events.auth.login.LoginSuccessEvent;
import com.app.gym.events.auth.login.LogoutAllEvent;
import com.app.gym.events.token.refresh.RefreshRotatedEvent;
import com.app.gym.servicios.workflow.AuthWorkflowService;

@Component
public class LoginEventsListener {
    private final AuthWorkflowService authWorkflowService;

    public LoginEventsListener(AuthWorkflowService authWorkflowService){
        this.authWorkflowService = authWorkflowService;
    }

    @EventListener
    @Async
    public void handleLoginSucces(LoginSuccessEvent event){
        authWorkflowService.executeSuccessfulLogin(event.idAccount(), event.ip(), event.userAgent());
    }

    @EventListener
    @Async
    public void handleLoginFailed(LoginFailedEvent event){
        authWorkflowService.executeFailedLogin(event.subject(), event.ip(), event.userAgent());
    }

    @EventListener
    @Async
    public void handleRefreshRotate(RefreshRotatedEvent event){
        authWorkflowService.executeLogRotateRefresh(event.idAccount(), event.ip(), event.userAgent());
    }

    @EventListener
    @Async
    public void handleLogoutAll(LogoutAllEvent event){
        authWorkflowService.executeLogoutAll(event.idAccount(), event.ip(), event.userAgent());
    }
}
