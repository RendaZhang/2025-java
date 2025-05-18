package com.renda.common.fallback;

import com.renda.common.dto.CommonResponseDto;
import com.renda.common.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GlobalFeignFallbackHandler {

    public String fallbackString(Throwable t) {
        log.error("Fallback triggered: {}", t.toString());
        return "Fallback response: " + t.getMessage();
    }

    public ResponseEntity<CommonResponseDto<Void>> fallbackResponse(Throwable t) {
        log.error("Feign fallback triggered: {}", t.toString());
        return ResponseUtils.error(503, "Service temporarily unavailable: " + t.getMessage());
    }

}
