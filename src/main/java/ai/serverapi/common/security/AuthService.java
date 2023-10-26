package ai.serverapi.common.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        log.info("회원 인증 처리");

        return User.builder()
                   .username(String.valueOf(1L))
                   .password("$2a$10$HCA6Vr7SCVW6FRg6Thk2BeXAbhgFd/MfeR1m6EaeXA64sN0S5vaXG")
                   .roles("USER", "SELLER")
                   .build();
    }
}
