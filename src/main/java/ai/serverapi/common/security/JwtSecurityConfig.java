package ai.serverapi.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class JwtSecurityConfig extends
    SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final AuthService authService;

    @Override
    public void configure(final HttpSecurity builder) throws Exception {
        AuthFilter authFilter = new AuthFilter(authService);
        builder.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
