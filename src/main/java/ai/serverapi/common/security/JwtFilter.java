package ai.serverapi.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
        final HttpServletResponse response,
        final FilterChain filterChain) throws ServletException, IOException {
        log.info("다른 곳에서 작동하는 권한 인증 필터");
    }
}
