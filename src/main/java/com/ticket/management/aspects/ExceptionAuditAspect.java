package com.ticket.management.aspects;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect 
@Component
@Slf4j 
public class ExceptionAuditAspect {

    @AfterThrowing (
        pointcut="execution(* com.ticket.management..*(..))" + 
                 " && !execution(* com.ticket.management.aspects..*(..))" + 
                "&& !execution(* com.ticket.management.security.*Filter.*(..))",
        throwing="exception"
    )
    public void logException(JoinPoint joinPoint, Throwable exception) {
        String methodName = joinPoint.getSignature().getName();
        Object[] methodArgs = joinPoint.getArgs();

        log.error("Exception in method: {} with arguments: {}." +
            "Exception message: {} with exception type: {}", methodName,
             Arrays.toString(methodArgs), exception.getMessage(), 
             exception.getClass().getSimpleName());

        // TODO: monitoring and alerts
    }
}
