package com.app.gym.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.app.gym.events.token.refresh.RotateParallelEvent;
import com.app.gym.events.token.refresh.TokenTheftDetectedEvent;
import com.app.gym.servicios.workflow.AuthWorkflowService;


@Component
public class RefreshTokenEventsListener {
    private final AuthWorkflowService authWorkflowService;

    public RefreshTokenEventsListener(AuthWorkflowService authWorkflowService){
        this.authWorkflowService = authWorkflowService;
    }

    @EventListener
    @Async
    public void handleTokenTheft(TokenTheftDetectedEvent event){
        authWorkflowService.executeTokenTheft(event.idAccount(), event.createdByIp(), event.deviceInfo());
    }

    @EventListener
    @Async
    public void handleRotateParallel(RotateParallelEvent event){
        authWorkflowService.executeRotateParallel(event.idAccount(), event.createdByIp(), event.deviceInfo());
    }
}
