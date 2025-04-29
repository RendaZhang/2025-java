package com.renda.taskmanager.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

    @Value("${spring-security.auth.username}")
    private String username;

    @Value("${spring-security.auth.password}")
    private String password;

    @Bean
    @LoadBalanced // Enable Spring Cloud Loadbalancer / Ribbon
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        String base64Credential = Base64.getEncoder()
                .encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));

        ClientHttpRequestInterceptor authInterceptor = (request, body, execution) -> {
            request.getHeaders().add("Authorization", "Basic " + base64Credential);
            return execution.execute(request, body);
        };

        return builder.additionalInterceptors(authInterceptor).build();
    }

}
