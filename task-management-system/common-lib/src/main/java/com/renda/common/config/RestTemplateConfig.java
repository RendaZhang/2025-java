package com.renda.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

    private final AuthConfigProperties authConfig;

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .additionalInterceptors((request, body, execution) -> {
                    ServiceAuthConfig config = authConfig.getServices().get(
                            extractServiceId(request.getURI().getHost()));

                    if (config != null) {
                        switch (config.getType().toLowerCase()) {
                            case "basic" -> {
                                String credential = config.getUsername() + ":" + config.getPassword();
                                String encoded = Base64.getEncoder()
                                        .encodeToString(credential.getBytes(StandardCharsets.UTF_8));
                                request.getHeaders().set("Authorization", "Basic " + encoded);
                            }
                            case "bearer" -> {
                                request.getHeaders().setBearerAuth(config.getToken());
                            }
                            case "custom" -> {
                                request.getHeaders().set(config.getHeader(), config.getValue());
                            }
                        }
                    }
                    return execution.execute(request, body);
                })
                .build();
    }

    private String extractServiceId(String host) {
        if (host == null) return "";
        // Keep only the service-id part (remove the port)
        String service = host.contains(":") ? host.split(":")[0] : host;
        return service.toLowerCase();
    }
}
