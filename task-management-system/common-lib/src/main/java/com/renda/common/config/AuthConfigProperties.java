package com.renda.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "auth")
@Getter
@Setter
public class AuthConfigProperties {
    private Map<String, ServiceAuthConfig> services;
}
