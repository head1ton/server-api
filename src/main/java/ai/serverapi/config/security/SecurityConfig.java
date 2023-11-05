package ai.serverapi.config.security;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import ai.serverapi.member.domain.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final CustomEntryPoint entryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .headers(c -> c.frameOptions(FrameOptionsConfig::disable).disable())
            .authorizeHttpRequests(auth ->
                auth
                    .requestMatchers(
                        antMatcher("/h2-console/**"),
                        antMatcher("/h2-console"),
                        antMatcher("/favicon.ico"),
                        antMatcher("/docs/docs.html"),
                        antMatcher("/docs/docs.html/**")
                    ).permitAll()
                    .requestMatchers(antMatcher("/api/auth/**")).permitAll()
                    .requestMatchers(antMatcher(PathRequest.toH2Console().toString())).permitAll()
                    .requestMatchers(antMatcher("/api/member/**")).hasRole(Role.MEMBER.roleName)
                    .requestMatchers(antMatcher("/api/seller/**")).hasRole(Role.SELLER.roleName)
                    .anyRequest().permitAll()
            )
            .exceptionHandling(c -> c.authenticationEntryPoint(entryPoint).accessDeniedHandler(
                accessDeniedHandler)) // 로그인 401, 403 에러 처리
            .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .apply(new JwtSecurityConfig(tokenProvider));
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}