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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class CustomEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final AuthenticationException authException) throws IOException, ServletException {

        PrintWriter writer = response.getWriter();
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        List<String> errorList = new ArrayList<>();
        errorList.add("UNAUTHORIZED");
        ErrorApi<String> errorApi = ErrorApi.<String>builder()
                                            .code(ResultCode.UNAUTHORIZED.CODE)
                                            .message(ResultCode.UNAUTHORIZED.MESSAGE)
                                            .errors(errorList)
                                            .build();

        ObjectMapper objectMapper = new ObjectMapper();
        writer.write(objectMapper.writeValueAsString(errorApi));
    }
}
