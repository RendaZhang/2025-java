package com.renda.taskmanager.client;

import com.renda.taskmanager.config.FeignConfig;
import com.renda.taskmanager.fallback.GlobalFeignFallbackHandler;
import com.renda.taskmanager.util.SpringContextHolder;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "user-service",
        path = "api/users",
        configuration = FeignConfig.class)
public interface UserClient {

    @GetMapping("/hello")
    @CircuitBreaker(name = "userClientHello", fallbackMethod = "helloFallback")
    String hello();

    default String helloFallback(Throwable t) {
        return SpringContextHolder.getBean(GlobalFeignFallbackHandler.class).fallbackString(t);
    }

}
