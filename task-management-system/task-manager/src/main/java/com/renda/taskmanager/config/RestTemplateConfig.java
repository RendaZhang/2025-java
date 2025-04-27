package com.renda.taskmanager.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    @LoadBalanced // Enable Spring Cloud Loadbalancer / Ribbon
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
