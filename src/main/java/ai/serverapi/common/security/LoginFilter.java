package ai.serverapi.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request,
        final HttpServletResponse response) throws AuthenticationException {
        log.info("인증 요청");

        return getAuthenticationManager().authenticate(
            new UsernamePasswordAuthenticationToken(String.valueOf(1L), "password", new HashSet<>())
        );
    }

    @Override
    protected void successfulAuthentication(final HttpServletRequest request,
        final HttpServletResponse response, final FilterChain chain,
        final Authentication authResult)
        throws IOException, ServletException {
        log.info("인증 성공");
    }

    @Override
    protected void unsuccessfulAuthentication(final HttpServletRequest request,
        final HttpServletResponse response, final AuthenticationException failed)
        throws IOException, ServletException {
        log.info("인증 실패");
        super.unsuccessfulAuthentication(request, response, failed);
    }
}
