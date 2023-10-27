package ai.serverapi.common.security;

import ai.serverapi.domain.entity.member.Member;
import ai.serverapi.domain.enums.Role;
import ai.serverapi.repository.member.MemberRepository;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@SpringBootTest
@Slf4j
class AuthServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("test")
    public void test() {

        Member member = memberRepository.findById(1L).orElseThrow(() ->
            new UsernameNotFoundException("유효하지 않은 회원입니다."));

        Role role = member.getRole();
        Set<String> roleSet = new HashSet<>();

        String roleListToString = Role.valueOf(role.roleName).roleList;
        log.debug("roleListToString : " + roleListToString);
        String[] roleList = roleListToString.split(",");
        log.debug("roleList : " + roleList);

        for (String r : roleList) {
            roleSet.add(r.trim());
        }
    }

}