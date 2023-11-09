package ai.serverapi.global.advice;

import ai.serverapi.global.base.ErrorDto;
import ai.serverapi.global.exception.DuringProcessException;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class CommonAdvice {

    @Value("${docs}")
    private String docs;
    private static final String ERRORS = "errors";

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> illegalArgumentException(IllegalArgumentException e,
        HttpServletRequest request) {
        List<ErrorDto> errors = new ArrayList<>();
        errors.add(new ErrorDto("", e.getMessage()));

        ProblemDetail pb = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400),
            "입력 값을 확인해 주세요.");
        pb.setInstance(URI.create(request.getRequestURI()));
        pb.setType(URI.create(docs));
        pb.setTitle(HttpStatus.BAD_REQUEST.name());
        pb.setProperty(ERRORS, errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(pb);
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> methodArgumentNotValidException(
        MethodArgumentNotValidException e, HttpServletRequest request) {
        List<ErrorDto> errors = new ArrayList<>();
        ProblemDetail pb = ProblemDetail.forStatusAndDetail(
            HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), "입력 값을 확인해주세요.");
        Optional<BindingResult> bindingResultOptional = Optional.ofNullable(e.getBindingResult());

        if (bindingResultOptional.isPresent()) {
            BindingResult bindingResult = bindingResultOptional.get();
            Optional<FieldError> fieldErrorOptional = Optional.ofNullable(
                bindingResult.getFieldError());
            if (fieldErrorOptional.isPresent()) {
                FieldError fieldError = e.getBindingResult().getFieldError();
                errors.add(new ErrorDto(Optional.ofNullable(fieldError.getField()).orElse(""),
                    Optional.ofNullable(fieldError.getDefaultMessage()).orElse("")));
            }
        }

        pb.setInstance(URI.create(request.getRequestURI()));
        pb.setType(URI.create(docs));
        pb.setTitle(HttpStatus.BAD_REQUEST.name());
        pb.setProperty(ERRORS, errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(pb);
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> httpMessageNotReadableException(
        HttpMessageNotReadableException e, HttpServletRequest request) {
        List<ErrorDto> errors = new ArrayList<>();

        errors.add(new ErrorDto("", e.getMessage()));

        ProblemDetail pb = ProblemDetail.forStatusAndDetail(
            HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), "입력 값을 확인해주세요.");
        pb.setInstance(URI.create(request.getRequestURI()));
        pb.setType(URI.create(docs));
        pb.setTitle(HttpStatus.BAD_REQUEST.name());
        pb.setProperty(ERRORS, errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(pb);
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> missingServletRequestParameterException(
        MissingServletRequestParameterException e, HttpServletRequest request) {
        List<ErrorDto> errors = new ArrayList<>();

        errors.add(new ErrorDto(e.getParameterName(),
            String.format("please check parameter : %s (%s)", e.getParameterName(),
                e.getParameterType())));

        ProblemDetail pb = ProblemDetail.forStatusAndDetail(
            HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()),
            "입력 값을 확인해 주세요.");
        pb.setInstance(URI.create(request.getRequestURI()));
        pb.setType(URI.create(docs));
        pb.setTitle(HttpStatus.BAD_REQUEST.name());
        pb.setProperty(ERRORS, errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(pb);
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> noHandlerFoundException(NoHandlerFoundException e,
        HttpServletRequest request) {
        List<ErrorDto> errors = new ArrayList<>();

        errors.add(new ErrorDto("", "NOT FOUND"));

        ProblemDetail pb = ProblemDetail.forStatusAndDetail(
            HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()),
            "URL을 찾을 수 없습니다.");
        pb.setInstance(URI.create(request.getRequestURI()));
        pb.setType(URI.create(docs));
        pb.setTitle(HttpStatus.NOT_FOUND.name());
        pb.setProperty(ERRORS, errors);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(pb);
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> sizeLimitExceededException(SizeLimitExceededException e,
        HttpServletRequest request) {
        List<ErrorDto> errors = new ArrayList<>();

        errors.add(new ErrorDto("size limit",
            String.format("max : %d, your request : %d", e.getPermittedSize(), e.getActualSize())));

        ProblemDetail pb = ProblemDetail.forStatusAndDetail(
            HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), "요청하신 파일의 크기가 너무 큽니다.");
        pb.setInstance(URI.create(request.getRequestURI()));
        pb.setType(URI.create(docs));
        pb.setTitle(HttpStatus.BAD_REQUEST.name());
        pb.setProperty(ERRORS, errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(pb);
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> fileSizeLimitExceededException(
        FileSizeLimitExceededException e, HttpServletRequest request) {
        List<ErrorDto> errors = new ArrayList<>();

        errors.add(new ErrorDto("file size limit",
            String.format("max : %d, your request : %d", e.getPermittedSize(), e.getActualSize())));

        ProblemDetail pb = ProblemDetail.forStatusAndDetail(
            HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), "요청하신 파일의 크기가 너무 큽니다.");
        pb.setInstance(URI.create(request.getRequestURI()));
        pb.setType(URI.create(docs));
        pb.setTitle(HttpStatus.BAD_REQUEST.name());
        pb.setProperty(ERRORS, errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(pb);
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> duringProcessException(
        DuringProcessException e,
        HttpServletRequest request
    ) {
        List<ErrorDto> errors = new ArrayList<>();

        errors.add(new ErrorDto("", String.format("%s", e.getMessage())));

        ProblemDetail pb = ProblemDetail.forStatusAndDetail(
            HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
            "서버 측 에러 발생. 관리자에게 문의해주세요!"
        );
        pb.setInstance(URI.create(request.getRequestURI()));
        pb.setType(URI.create(docs));
        pb.setTitle(HttpStatus.BAD_REQUEST.name());
        pb.setProperty(ERRORS, errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(pb);
    }

}
