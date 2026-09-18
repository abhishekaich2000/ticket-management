package com.ticket.management.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect 
@Component 
@Slf4j 
public class LoggingAndPerformanceAspect {

    @Around ("execution(* com.ticket.management.service..*(..))")
    public Object logAndMeasureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable{
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        log.warn("Method {} executed in {} ms", joinPoint.getSignature(), executionTime);
        return result;
    }
}
