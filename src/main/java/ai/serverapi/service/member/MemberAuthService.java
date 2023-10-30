package ai.serverapi.service.member;

import ai.serverapi.common.security.TokenProvider;
import ai.serverapi.domain.dto.member.JoinDto;
import ai.serverapi.domain.dto.member.KakaoLoginResponseDto;
import ai.serverapi.domain.dto.member.LoginDto;
import ai.serverapi.domain.entity.member.Member;
import ai.serverapi.domain.enums.Role;
import ai.serverapi.domain.vo.member.JoinVo;
import ai.serverapi.domain.vo.member.LoginVo;
import ai.serverapi.repository.member.MemberRepository;
import io.jsonwebtoken.Claims;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberAuthService {

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 30;   // 30분
    private static final String AUTHORITIES_KEY = "auth";
    private static final String TYPE = "Bearer ";
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final WebClient kakaoClient;
    private final Environment env;

    @Transactional
    public JoinVo join(final JoinDto joinDto) {
        joinDto.passwordEncoder(passwordEncoder);
        Optional<Member> findMember = memberRepository.findByEmail(joinDto.getEmail());
        if (findMember.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }

        Member member = memberRepository.save(Member.of(joinDto));
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
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        String originAccessToken = Optional.ofNullable(ops.get(refreshToken)).orElse("").toString();
        Claims claims = tokenProvider.parseClaims(originAccessToken);
        log.debug("claims : " + claims);

        String sub = claims.get("sub").toString();
        long now = (new Date()).getTime();
        Date accessTokenExpired = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);

        Member member = memberRepository.findById(Long.parseLong(sub)).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 회원입니다."));

        Role role = Role.valueOf(member.getRole().roleName);
        String[] roleSplitList = role.roleList.split(",");
        List<String> trimRoleList = Arrays.stream(roleSplitList)
                                          .map(r -> String.format("ROLE_%s", r.trim()))
                                          .toList();

        String roleList = trimRoleList.toString().replace("[", "").replace("]", "")
                                      .replace(" ", "");

        String accessToken = tokenProvider.createAccessToken(String.valueOf(member.getId()),
            roleList, accessTokenExpired);

        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return LoginVo.builder()
                      .type(TYPE)
                      .accessToken(accessToken)
                      .accessTokenExpired(accessTokenExpired.getTime())
                      .refreshToken(refreshToken)
                      .build();
    }

    @Transactional
    public LoginVo authKakao(final String code) {
        log.info("code = {}", code);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("client_id", env.getProperty("kakao.client_id"));
        map.add("redirect_url", env.getProperty("kakao.redirect_url"));
        map.add("code", code);

        KakaoLoginResponseDto kakaoToken = Optional.ofNullable(
            kakaoClient.post().uri(("/oauth/token"))
                                                      .header(HttpHeaders.CONTENT_TYPE,
                                                          "application/x-www-form-urlencoded;charset=UTF-8")
                                                      .body(BodyInserters.fromFormData(map))
                                                      .retrieve()
                       .onStatus(HttpStatusCode::isError,
                           response -> response.bodyToMono(String.class)
                                               .handle(
                                                   (error, sink) -> sink.error(
                                                       new RuntimeException(
                                                           error))))
                                                      .bodyToMono(KakaoLoginResponseDto.class)
                       .block()
        ).orElse(new KakaoLoginResponseDto("", "", 0L, 0L));

        return LoginVo.builder()
                      .type(TYPE)
                      .accessToken(kakaoToken.access_token)
                      .accessTokenExpired(kakaoToken.expires_in)
                      .refreshToken(kakaoToken.refresh_token)
                      .refreshTokenExpired(kakaoToken.refresh_token_expires_in)
                      .build();
    }

    public LoginVo loginKakao(final String accessToken) {
        throw new UnsupportedOperationException("Unsupported loginKakao");
    }
}
