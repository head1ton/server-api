package ai.serverapi.service.member;

import ai.serverapi.common.security.TokenProvider;
import ai.serverapi.domain.dto.member.JoinDto;
import ai.serverapi.domain.dto.member.LoginDto;
import ai.serverapi.domain.entity.member.Member;
import ai.serverapi.domain.vo.member.JoinVo;
import ai.serverapi.domain.vo.member.LoginVo;
import ai.serverapi.repository.member.MemberRepository;
import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public JoinVo join(final JoinDto joinDto) {
        joinDto.passwordEncoder(passwordEncoder);
        Optional<Member> findMember = memberRepository.findByEmail(joinDto.getEmail());
        if (findMember.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }

        Member member = memberRepository.save(Member.createMember(joinDto));
        return JoinVo.builder()
                     .email(member.getEmail())
                     .name(member.getName())
                     .nickname(member.getNickname())
                     .build();
    }

    public LoginVo login(final LoginDto loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken = loginDto.toAuthentication();

        Authentication authenticate = authenticationManagerBuilder.getObject().authenticate(
            authenticationToken);

        LoginVo loginVo = tokenProvider.generateTokenDto(authenticate);

        // token redis 저장
        String accessToken = loginVo.getAccessToken();
        String refreshToken = loginVo.getRefreshToken();
        Claims claims = tokenProvider.parseClaims(refreshToken);
        long refreshTokenExpired = Long.parseLong(claims.get("exp").toString());

        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set(refreshToken, accessToken);
        redisTemplate.expireAt(refreshToken, new Date(refreshTokenExpired * 1000L));

        return loginVo;
    }


    public LoginVo refresh(final String refreshToken) {
        throw new UnsupportedOperationException("Unsupported refresh");
    }
}
