package com.app.gym;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

import com.app.gym.modelos.properties.AccountProperties;
import com.app.gym.modelos.properties.GoogleAuthProperties;
import com.app.gym.modelos.properties.RateLimitProperties;
import com.app.gym.modelos.properties.TokenProperties;

@SpringBootApplication
@EnableConfigurationProperties({TokenProperties.class, RateLimitProperties.class, 
								AccountProperties.class, GoogleAuthProperties.class})
@EnableAsync
public class GymApplication {

	public static void main(String[] args) {
		SpringApplication.run(GymApplication.class, args);
	}

}
