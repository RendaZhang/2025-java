package com.renda.common.config;

import lombok.Data;

@Data
public class ServiceAuthConfig {
    private String type;     // basic / bearer / custom
    private String username; // for basic
    private String password; // for basic
    private String token;    // for bearer
    private String header;   // for custom
    private String value;    // for custom
}
