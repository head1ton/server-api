package ai.serverapi.common.aop;

import ai.serverapi.domain.dto.ErrorApi;
import ai.serverapi.domain.enums.ResultCode;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class ControllerLogAspect {

    @Around("execution(* ai.serverapi.controller..*.*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        String type = pjp.getSignature().getDeclaringTypeName();
        String method = pjp.getSignature().getName();
        String requestURI = ((ServletRequestAttributes) requestAttributes).getRequest()
                                                                          .getRequestURI();

        log.info("[logging] Controller ... requestUri = [{}] package = [{}], method = [{}]",
            requestURI, type, method);

        Object[] args = pjp.getArgs();
        for (Object arg : args) {
            if (arg instanceof final BindingResult bindingResult) {
                if (bindingResult.hasErrors()) {
                    List<String> errors = new ArrayList<>();
                    for (FieldError error : bindingResult.getFieldErrors()) {
                        log.warn("[parameter : {}] [message = {}]", error.getField(),
                            error.getDefaultMessage());
                        errors.add(String.format("%s", error.getDefaultMessage()));
                    }

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                         .body(
                                             ErrorApi.<String>builder()
                                                     .code(ResultCode.BAD_REQUEST.CODE)
                                                     .message(ResultCode.BAD_REQUEST.MESSAGE)
                                                     .errors(errors)
                                                     .build()
                                         );
                }
            }
        }

        return pjp.proceed();
    }

}
