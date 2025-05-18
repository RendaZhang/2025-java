package com.renda.common.interceptor;

import com.renda.common.config.AuthConfigProperties;
import com.renda.common.config.ServiceAuthConfig;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class GlobalFeignAuthInterceptor implements RequestInterceptor {

    private final AuthConfigProperties authConfig;

    @Override
    public void apply(RequestTemplate template) {
        String serviceId = extractServiceId(template.feignTarget().name());

        ServiceAuthConfig config = authConfig.getServices().get(serviceId);
        if (config != null) {
            switch (config.getType().toLowerCase()) {
                case "basic" -> {
                    String credential = config.getUsername() + ":" + config.getPassword();
                    String encoded = Base64.getEncoder().encodeToString(credential.getBytes(StandardCharsets.UTF_8));
                    template.header("Authorization", "Basic " + encoded);
                }
                case "bearer" -> {
                    template.header("Authorization", "Bearer " + config.getToken());
                }
                case "custom" -> {
                    template.header(config.getHeader(), config.getValue());
                }
            }
        }
    }

    private String extractServiceId(String name) {
        // Standardize the serviceId by removing colons and spaces
        return name != null ? name.trim().toLowerCase() : "";
    }
}
