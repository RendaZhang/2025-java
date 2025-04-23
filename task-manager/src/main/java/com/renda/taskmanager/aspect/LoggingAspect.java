package com.renda.taskmanager.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    /**
     * Define a pointcut to match all methods in the service package
     */
    @Pointcut("execution(* com.renda.taskmanager.service.*.*(..))")
    public void serviceMethods() {
    }

    /**
     * Before Advice
     */
    @Before("serviceMethods()")
    public void beforeAdvice(JoinPoint joinPoint) {
        System.out.println("[Before Advice] Starting Method: " + joinPoint.getSignature().getName());
    }

    /**
     * After Advice
     */
    @After("serviceMethods()")
    public void afterAdvice(JoinPoint joinPoint) {
        System.out.println("[After Advice] Completed Method: " + joinPoint.getSignature().getName());
    }

    /**
     * After Returning advice (executed after the method successfully returns)
     */
    @AfterReturning(value = "serviceMethods()", returning = "result")
    public void afterReturningAdvice(JoinPoint joinPoint, Object result) {
        System.out.println("[After Returning Advice] Method: " + joinPoint.getSignature().getName()
                + ", Return Value: " + result);
    }

    /**
     * AfterThrowing advice (executed after the method throws an exception)
     */
    @AfterThrowing(value = "serviceMethods()", throwing = "ex")
    public void afterThrowingAdvice(JoinPoint joinPoint, Exception ex) {
        System.out.println("[After Throwing Advice] Method: " + joinPoint.getSignature().getName()
                + " threw an exception: " + ex.getMessage());
    }

    /**
     * Around advice (to measure method execution time)
     */
    @Around("serviceMethods()")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("[Around Advice] Method: " + joinPoint.getSignature().getName()
                + " executed in " + duration + "ms");
        return result;
    }

}
