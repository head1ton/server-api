package ai.serverapi.common.advice;

import ai.serverapi.domain.dto.ErrorApi;
import ai.serverapi.domain.dto.ErrorDto;
import ai.serverapi.domain.enums.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class CommonAdvice {

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> illegalArgumentException(IllegalArgumentException e,
        HttpServletRequest request) {
        List<ErrorDto> errors = new ArrayList<>();
        errors.add(ErrorDto.builder().point("").detail(e.getMessage()).build());

        ProblemDetail pb = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400),
            "입력 값을 확인해 주세요.");
        pb.setInstance(URI.create(request.getRequestURI()));
        pb.setType(URI.create("/docs/docs.html"));
        pb.setTitle("BAD REQUEST");
        pb.setProperty("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(pb);
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> missingServletRequestParameterException(
        MissingServletRequestParameterException e, HttpServletRequest request) {
        List<ErrorDto> errors = new ArrayList<>();
        errors.add(ErrorDto.builder().point(e.getParameterName()).detail(
            String.format("Please check parameter : %s (%s)", e.getParameterName(),
                e.getParameterType())).build());

        ProblemDetail pb = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400),
            "입력 값을 확인해 주세요.");
        pb.setInstance(URI.create(request.getRequestURI()));
        pb.setType(URI.create("/docs/docs.html"));
        pb.setTitle("BAD REQUEST");
        pb.setProperty("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(pb);
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> noHandlerFoundException(NoHandlerFoundException e,
        HttpServletRequest request) {
        List<ErrorDto> errors = new ArrayList<>();
        errors.add(ErrorDto.builder().point("").detail("NOT FOUND").build());

        ProblemDetail pb = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404),
            "URL을 찾을 수 없습니다.");
        pb.setInstance(URI.create(request.getRequestURI()));
        pb.setType(URI.create("/docs/docs.html"));
        pb.setTitle("NOT FOUND");
        pb.setProperty("errors", errors);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(pb);
    }

}
