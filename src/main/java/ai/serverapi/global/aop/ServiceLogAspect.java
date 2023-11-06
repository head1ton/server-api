package ai.serverapi.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ServiceLogAspect {

    @Around("execution(* ai.serverapi..*.service..*.*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        log.debug("[logging] Service ... method = [{}]", pjp.getSignature().toShortString());

        return pjp.proceed();
    }
}
