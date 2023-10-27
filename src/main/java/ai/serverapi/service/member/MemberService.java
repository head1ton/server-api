package ai.serverapi.service.member;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ai.serverapi.common.security.TokenProvider;
import ai.serverapi.domain.dto.member.JoinDto;
import ai.serverapi.domain.dto.member.LoginDto;
import ai.serverapi.domain.entity.member.Member;
import ai.serverapi.domain.entity.member.MemberApplySeller;
import ai.serverapi.domain.enums.Role;
import ai.serverapi.domain.enums.member.MemberApplySellerStatus;
import ai.serverapi.domain.vo.MessageVo;
import ai.serverapi.domain.vo.member.JoinVo;
import ai.serverapi.domain.vo.member.LoginVo;
import ai.serverapi.domain.vo.member.MemberVo;
import ai.serverapi.repository.member.MemberApplySellerRepository;
import ai.serverapi.repository.member.MemberRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final MemberApplySellerRepository memberApplySellerRepository;
    private static final String TYPE = "Bearer ";

    public MemberVo member(final HttpServletRequest request) {
        String token = tokenProvider.resolveToken(request);
        Long memberId = tokenProvider.getMemberId(token);

        Member findMember = memberRepository.findById(memberId).orElseThrow(
            () -> new IllegalArgumentException("존재하지 않는 회원입니다.")
        );

        return MemberVo.builder()
                       .memberId(findMember.getId())
                       .email(findMember.getEmail())
                       .role(findMember.getRole())
                       .createdAt(findMember.getCreatedAt())
                       .modifiedAt(findMember.getModifiedAt())
                       .name(findMember.getName())
                       .nickname(findMember.getNickname())
                       .snsType(findMember.getSnsType())
                       .build();
    }

    @Transactional
    public MessageVo applySeller(final HttpServletRequest request) {
        String token = tokenProvider.resolveToken(request);
        Long memberId = tokenProvider.getMemberId(token);

        MemberApplySeller saveMemberApply = memberApplySellerRepository.save(
            MemberApplySeller.of(memberId));

        permitSeller(memberId, saveMemberApply);    // 자동 승인으로 처리

        return MessageVo.builder()
                        .message("임시적으로 SELLER 즉시 승인")
                        .build();
    }

    private void permitSeller(final Long memberId, final MemberApplySeller saveMemberApply) {
        saveMemberApply.patchApplyStatus(MemberApplySellerStatus.PERMIT);
        Member member = memberRepository.findById(memberId).orElseThrow(() ->
            new IllegalArgumentException("존재하지 않는 회원입니다."));
        member.patchMemberRole(Role.SELLER);
    }
}
