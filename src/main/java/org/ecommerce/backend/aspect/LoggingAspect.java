package org.ecommerce.backend.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
    @Pointcut("execution(* org.ecommerce.backend.service..*(..))")
    public void servicePointcut() {}

    @AfterThrowing(value = "servicePointcut()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        log.error("❌ Exception in method: {} with message: {}",
                joinPoint.getSignature().toLongString(),
                ex.getMessage(), ex);
    }}
