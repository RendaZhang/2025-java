package com.renda.taskmanager.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GlobalFeignFallbackHandler {

    public String fallbackString(Throwable t) {
        log.error("Fallback triggered: {}", t.toString());
        return "Fallback response: " + t.getMessage();
    }

}
