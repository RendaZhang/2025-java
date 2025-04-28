package com.renda.taskmanager.client;

import com.renda.taskmanager.config.FeignConfig;
import com.renda.taskmanager.fallback.GlobalFeignFallbackHandler;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "user-service",
        path = "api/users",
        configuration = FeignConfig.class)
public interface UserClient {

    GlobalFeignFallbackHandler fallbackHandler = new GlobalFeignFallbackHandler();

    @GetMapping("/hello")
    @CircuitBreaker(name = "userClientHello", fallbackMethod = "globalFallback")
    String hello();

    default String globalFallback(Throwable t) {
        return fallbackHandler.fallbackString(t);
    }

}
