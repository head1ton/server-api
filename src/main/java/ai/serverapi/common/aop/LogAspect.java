package ai.serverapi.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class LogAspect {

    @Around("execution(* ai.serverapi.service..*.*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        String requestURI = ((ServletRequestAttributes) requestAttributes).getRequest()
                                                                          .getRequestURI();
        String method = ((ServletRequestAttributes) requestAttributes).getRequest().getMethod();

        log.info("[ requestUri = ({}) {}, method = {} ]", method, requestURI,
            pjp.getSignature().toShortString());

        return pjp.proceed();
    }

}
