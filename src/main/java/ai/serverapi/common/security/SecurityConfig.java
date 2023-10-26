package ai.serverapi.common.security;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final AuthService authService;
    private final ObjectPostProcessor<Object> objectPostProcessor;

    private static final String[] SETTING_LIST = {
        "/h2-console/**", "/h2-console", "/favicon.ico", "/docs.html", "/docs.html/**"
    };

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
                        antMatcher("/docs.html"),
                        antMatcher("/docs.html/**")
                    ).permitAll()
                    .requestMatchers(antMatcher("/member/**")).permitAll()
                    .requestMatchers(antMatcher("/v1/**")).hasRole("USER")
                    .requestMatchers(antMatcher("/v2/**")).hasRole("SELLER")
            )
            .exceptionHandling(c -> c.authenticationEntryPoint(null).accessDeniedHandler(null))
            .apply(new JwtSecurityConfig(authService));
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}