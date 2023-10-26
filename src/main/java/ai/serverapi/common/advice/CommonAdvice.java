package ai.serverapi.common.advice;

import ai.serverapi.domain.dto.ErrorApi;
import ai.serverapi.domain.enums.ResultCode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "ai.serverapi")
public class CommonAdvice {

    @ExceptionHandler
    public ResponseEntity<ErrorApi<String>> illegalArgumentException(IllegalArgumentException e) {
        List<String> errors = new ArrayList<String>();
        errors.add(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(
                                 ErrorApi.<String>builder()
                                         .code(ResultCode.BAD_REQUEST.CODE)
                                         .message(ResultCode.BAD_REQUEST.MESSAGE)
                                         .errors(errors)
                                         .build()
                             );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorApi<String>> missingServletRequestParameterException(
        MissingServletRequestParameterException e) {
        List<String> errors = new ArrayList<String>();
        errors.add(String.format("Please check parameter : %s (%s)", e.getParameterName(),
            e.getParameterType()));

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
