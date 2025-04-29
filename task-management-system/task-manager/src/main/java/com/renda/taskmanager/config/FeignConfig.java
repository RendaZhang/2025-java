package com.renda.taskmanager.config;

import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Configuration
public class FeignConfig {

    @Value("${spring-security.auth.username}")
    private String username;

    @Value("${spring-security.auth.password}")
    private String password;

    public Request.Options feignOptions() {
        return new Request.Options(
                2, TimeUnit.MILLISECONDS,
                5, TimeUnit.MILLISECONDS,
                true);
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor userServiceAuthInterceptor() {

        return requestTemplate -> {
            String credentials = username + ":" + password;
            String base64Creds = Base64.getEncoder().encodeToString(credentials.getBytes());
            requestTemplate.header("Authorization", "Basic " + base64Creds);
        };
    }

}
