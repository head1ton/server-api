package ai.serverapi.global.util;

import ai.serverapi.global.security.TokenProvider;
import ai.serverapi.member.domain.entity.MemberEntity;
import ai.serverapi.member.repository.MemberJpaRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberUtil {

    private final TokenProvider tokenProvider;
    private final MemberJpaRepository memberJpaRepository;

    public MemberEntity getMember(HttpServletRequest request) {
        String token = tokenProvider.resolveToken(request);
        Long memberId = tokenProvider.getMemberId(token);
        return memberJpaRepository.findById(memberId)
                                  .orElseThrow(
                                      () -> new IllegalArgumentException("유효하지 않은 회원입니다."));
    }

}