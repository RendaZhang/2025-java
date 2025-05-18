package com.renda.common.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * Define a pointcut to match all methods in the service package
     */
    @Pointcut("execution(* com.renda..service.*.*(..))")
    public void serviceMethods() {
    }

    /**
     * Define a pointcut to match all methods in the controller package
     */
    @Pointcut("execution(* com.renda..controller.*.*(..))")
    public void controllerMethods() {
    }

    /**
     * Before Advice
     */
    @Before("serviceMethods()")
    public void beforeAdvice(JoinPoint joinPoint) {
        logger.info("[Before Advice] Starting Method: {}", joinPoint.getSignature().getName());
    }

    /**
     * After Advice
     */
    @After("serviceMethods()")
    public void afterAdvice(JoinPoint joinPoint) {
        logger.info("[After Advice] Completed Method: {}", joinPoint.getSignature().getName());
    }

    /**
     * After Returning advice (executed after the method successfully returns)
     */
    @AfterReturning(value = "serviceMethods()", returning = "result")
    public void afterReturningAdvice(JoinPoint joinPoint, Object result) {
        logger.info("[After Returning Advice] Method: {}, Return Value: {}",
                joinPoint.getSignature().getName(), result);
    }

    /**
     * AfterThrowing advice (executed after the method throws an exception)
     */
    @AfterThrowing(value = "serviceMethods()", throwing = "ex")
    public void afterThrowingAdvice(JoinPoint joinPoint, Exception ex) {
        logger.info("[After Throwing Advice] Method: {} threw an exception: {}",
                joinPoint.getSignature().getName(), ex.getMessage());
    }

    /**
     * Around advice (to measure method execution time)
     */
    @Around("serviceMethods()")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - startTime;
        logger.info("[Around Advice] Method: {} executed in {}ms", joinPoint.getSignature().getName(), duration);
        return result;
    }

    /**
     * Around advice for controller methods (to log request and response)
     */
    @Around("controllerMethods()")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            logger.info("[Controller Request] Method: {}, URI: {}, Params: {}, Body: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getQueryString(),
                    Arrays.toString(joinPoint.getArgs()));
        } else {
            logger.error("[Controller Request] No HTTP request context available for method: {}", joinPoint.getSignature().getName());
        }

        Object result = joinPoint.proceed();

        logger.info("[Controller Response] Method: {}, Response: {}", joinPoint.getSignature().getName(), result);

        return result;
    }
}
