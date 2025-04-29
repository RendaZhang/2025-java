package com.renda.taskmanager.fallback;

import com.renda.taskmanager.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
public class GlobalFeignFallbackHandler {

    public String fallbackString(Throwable t) {
        log.error("Fallback triggered: {}", t.toString());
        return "Fallback response: " + t.getMessage();
    }

    public ResponseEntity<ErrorResponseDto> fallbackResponse(Throwable t) {
        log.error("Feign fallback triggered: {}", t.toString());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .status(503)
                .message("User-service is unavailable: " + t.getMessage())
                .fieldErrors(Collections.emptyList())
                .build();

        return ResponseEntity.status(503).body(error);
    }

}
