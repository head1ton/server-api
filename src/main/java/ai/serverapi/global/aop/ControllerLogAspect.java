package ai.serverapi.global.aop;

import ai.serverapi.global.base.ErrorDto;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
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

    @Value("${docs}")
    private String docs;

    @Around("execution(* ai.serverapi..*.controller..*.*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        String type = pjp.getSignature().getDeclaringTypeName();
        String method = pjp.getSignature().getName();
        String requestURI = ((ServletRequestAttributes) requestAttributes).getRequest()
                                                                          .getRequestURI();

        log.debug("[logging] Controller ... requestUri = [{}] package = [{}], method = [{}]",
            requestURI, type, method);

        Object[] args = pjp.getArgs();
        for (Object arg : args) {
            if (arg instanceof final BindingResult bindingResult && bindingResult.hasErrors()) {
                List<ErrorDto> errors = new ArrayList<>();
                for (FieldError error : bindingResult.getFieldErrors()) {
                    errors.add(new ErrorDto(error.getField(), error.getDefaultMessage()));
                }

                ProblemDetail pb = ProblemDetail.forStatusAndDetail(
                    HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), "잘못된 입력입니다.");
                pb.setInstance(URI.create(requestURI));
                pb.setType(URI.create(docs));
                pb.setTitle(HttpStatus.BAD_REQUEST.name());
                pb.setProperty("errors", errors);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                     .body(pb);
            }
        }

        return pjp.proceed();
    }

}
