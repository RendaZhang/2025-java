package com.renda.taskmanager.config;

import feign.Logger;
import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignConfig {

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

}
