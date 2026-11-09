package com.app.gym.modelos.account;

public interface AccountSecurityView {
    Long getIdAccount();
    boolean isEnabled();
    boolean isLocked();
    boolean isEmailVerified();
}
