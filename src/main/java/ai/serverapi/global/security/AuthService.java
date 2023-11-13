package ai.serverapi.global.security;

import ai.serverapi.member.domain.entity.MemberEntity;
import ai.serverapi.member.enums.MemberRole;
import ai.serverapi.member.repository.MemberJpaRepository;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements UserDetailsService {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        log.debug("회원 인증 처리");

        MemberEntity memberEntity = memberJpaRepository.findByEmail(username).orElseThrow(() ->
            new UsernameNotFoundException("유효하지 않은 회원입니다."));

        MemberRole memberRole = memberEntity.getRole();
        Set<String> roleSet = new HashSet<>();
        String roleListToString = MemberRole.valueOf(memberRole.roleName).roleList;
        String[] roleList = roleListToString.split(",");

        for (String r : roleList) {
            roleSet.add(r.trim());
        }

        String[] roles = Arrays.copyOf(roleSet.toArray(), roleSet.size(), String[].class);

        return User.builder()
                   .username(String.valueOf(memberEntity.getId()))
                   .password(memberEntity.getPassword())
                   .roles(roles)
                   .build();
    }
}
