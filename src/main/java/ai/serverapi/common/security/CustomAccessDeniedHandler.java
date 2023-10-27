package ai.serverapi.common.security;

import ai.serverapi.domain.dto.ErrorApi;
import ai.serverapi.domain.enums.ResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final AccessDeniedException accessDeniedException) throws IOException, ServletException {

        PrintWriter writer = response.getWriter();
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        List<String> errorList = new ArrayList<>();
        errorList.add("FORBIDDEN");
        ErrorApi<String> errorApi = ErrorApi.<String>builder()
                                            .code(ResultCode.FORBIDDEN.CODE)
                                            .message(ResultCode.FORBIDDEN.MESSAGE)
                                            .errors(errorList)
                                            .build();

        ObjectMapper objectMapper = new ObjectMapper();
        writer.write(objectMapper.writeValueAsString(errorApi));
    }
}
