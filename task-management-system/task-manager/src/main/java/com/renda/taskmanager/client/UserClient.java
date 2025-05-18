package com.renda.taskmanager.client;

import com.renda.common.dto.CommonResponseDto;
import com.renda.common.util.SpringContextHolder;
import com.renda.common.fallback.GlobalFeignFallbackHandler;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "user-service",
        path = "api/users")
public interface UserClient {

    @GetMapping("/hello")
    @CircuitBreaker(name = "userClientHello", fallbackMethod = "helloFallback")
    ResponseEntity<CommonResponseDto<String>> hello();

    default ResponseEntity<CommonResponseDto<Void>> helloFallback(Throwable t) {
        return SpringContextHolder.getBean(GlobalFeignFallbackHandler.class).fallbackResponse(t);
    }

}
