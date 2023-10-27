package ai.serverapi.common.aop;

import ai.serverapi.domain.dto.ErrorApi;
import ai.serverapi.domain.dto.ErrorDto;
import ai.serverapi.domain.enums.ResultCode;
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

    @Around("execution(* ai.serverapi.controller..*.*(..))")
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
            if (arg instanceof final BindingResult bindingResult) {
                if (bindingResult.hasErrors()) {
                    List<ErrorDto> errors = new ArrayList<>();
                    for (FieldError error : bindingResult.getFieldErrors()) {
                        errors.add(ErrorDto.builder().point(error.getField())
                                           .detail(error.getDefaultMessage()).build());
                    }

                    ProblemDetail pb = ProblemDetail.forStatusAndDetail(
                        HttpStatusCode.valueOf(404), "잘못된 입력입니다.");
                    pb.setInstance(URI.create(requestURI));
                    pb.setType(URI.create(docs));
                    pb.setTitle("BAD REQUEST");
                    pb.setProperty("errors", errors);

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                         .body(pb);
                }
            }
        }

        return pjp.proceed();
    }

}
