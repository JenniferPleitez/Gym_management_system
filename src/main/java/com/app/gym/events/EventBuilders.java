package com.app.gym.events;

import com.app.gym.events.account.builders.AccountEventBuilder;
import com.app.gym.events.account.builders.AccountTokenEventBuilder;
import com.app.gym.events.account.builders.AccountAdminEventBuilder;
import com.app.gym.events.auth.builders.LoginEventBuilder;
import com.app.gym.events.token.builders.RefreshEventBuilder;

public class EventBuilders {
    
    public static AccountEventBuilder account() {
        return AccountEventBuilder.create();
    }
    
    public static AccountAdminEventBuilder accountAdmin() {
        return AccountAdminEventBuilder.create();
    }
    
    public static AccountTokenEventBuilder accountToken() {
        return AccountTokenEventBuilder.create();
    }
    
    public static LoginEventBuilder Authentication() {
        return LoginEventBuilder.create();
    }

    public static RefreshEventBuilder refreshToken() {
        return RefreshEventBuilder.create();
    }
}
