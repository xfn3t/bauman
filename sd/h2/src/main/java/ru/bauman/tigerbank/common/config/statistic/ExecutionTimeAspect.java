package ru.bauman.tigerbank.common.config.statistic;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExecutionTimeAspect {

	@Around("@annotation(ru.bauman.tigerbank.common.config.Measured)")
	public Object measureTime(ProceedingJoinPoint joinPoint) throws Throwable {
		long start = System.currentTimeMillis();
		Object result = joinPoint.proceed();
		long elapsed = System.currentTimeMillis() - start;
		log.info("Method {} executed in {} ms", joinPoint.getSignature(), elapsed);
		return result;
	}
}