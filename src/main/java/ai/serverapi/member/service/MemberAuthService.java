package ai.serverapi.member.service;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ai.serverapi.global.mail.MyMailSender;
import ai.serverapi.global.security.TokenProvider;
import ai.serverapi.member.domain.dto.JoinDto;
import ai.serverapi.member.domain.dto.LoginDto;
import ai.serverapi.member.domain.dto.kakao.KakaoLoginResponseDto;
import ai.serverapi.member.domain.dto.kakao.KakaoMemberResponseDto;
import ai.serverapi.member.domain.entity.Member;
import ai.serverapi.member.domain.enums.Role;
import ai.serverapi.member.domain.enums.SnsJoinType;
import ai.serverapi.member.domain.vo.JoinVo;
import ai.serverapi.member.domain.vo.LoginVo;
import ai.serverapi.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    private final WebClient kakaoApiClient;
    private final Environment env;
    private final MyMailSender myMailSender;

    @Transactional
    public JoinVo join(final JoinDto joinDto) {
        joinDto.passwordEncoder(passwordEncoder);
        Optional<Member> findMember = memberRepository.findByEmail(joinDto.getEmail());
        if (findMember.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }

        Member member = memberRepository.save(Member.of(joinDto));
        // 메일 계정 변경해야 함
//        myMailSender.send("언제나 환영합니다!", "<html><h1>회원 가입에 감사드립니다.</h1></html>", member.getEmail());

        return new JoinVo(member.getName(), member.getNickname(), member.getEmail());
    }

    public LoginVo login(final LoginDto loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken = loginDto.toAuthentication();

        Authentication authenticate = authenticationManagerBuilder.getObject().authenticate(
            authenticationToken);

        LoginVo loginVo = tokenProvider.generateTokenDto(authenticate);

        // token redis 저장
        saveRedisToken(loginVo);

        return loginVo;
    }

    private void saveRedisToken(final LoginVo loginVo) {
        String accessToken = loginVo.accessToken();
        String refreshToken = loginVo.refreshToken();
        Claims claims = tokenProvider.parseClaims(refreshToken);
        long refreshTokenExpired = Long.parseLong(claims.get("exp").toString());

        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set(refreshToken, accessToken);
        redisTemplate.expireAt(refreshToken, new Date(refreshTokenExpired * 1000L));
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

        return new LoginVo(TYPE, accessToken, refreshToken, accessTokenExpired.getTime(), null);
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

        return new LoginVo(TYPE, kakaoToken.access_token, kakaoToken.refresh_token,
            kakaoToken.expires_in, kakaoToken.refresh_token_expires_in);
    }

    @Transactional
    public LoginVo loginKakao(final String accessToken) {
        KakaoMemberResponseDto info = kakaoApiClient.post().uri(("/v2/user/me"))
                                                    .header(HttpHeaders.CONTENT_TYPE,
                                                        "application/x-www-form-urlencoded;charset=utf-8")
                                                    .header(AUTHORIZATION, TYPE + accessToken)
                                                    .retrieve()
                                                    .bodyToMono(KakaoMemberResponseDto.class)
                                                    .blockOptional()
                                                    .orElseThrow(() -> new IllegalStateException(
                                                        "카카오에서 반환 받은 값이 존재하지 않습니다."));

        boolean hasEmail = info.kakao_account.has_email;
        if (!hasEmail) {
            throw new IllegalArgumentException("이메일이 존재하지 않는 회원입니다. SNS 인증 먼저 진행해 주세요.");
        }

        String email = info.kakao_account.email;
        String snsId = String.valueOf(info.id);
        Optional<Member> findMember = memberRepository.findByEmail(email);

        Member member;

        if (findMember.isEmpty()) {
            JoinDto joinDto = new JoinDto(email, snsId, info.kakao_account.profile.nickname,
                info.kakao_account.profile.nickname, null);
            joinDto.passwordEncoder(passwordEncoder);
            member = memberRepository.save(Member.of(joinDto, snsId, SnsJoinType.KAKAO));

            // 메일링.. 계정 변경 요함
//            myMailSender.send("방문을 환영합니다!!", "<html><h1>회원 가입에 감사드립니다.</h1></html>", member.getEmail());
        } else {
            member = findMember.get();
        }

        Role role = Role.valueOf(member.getRole().roleName);

        String[] roleSplitList = role.roleList.split(",");
        List<SimpleGrantedAuthority> grantedList = new LinkedList<>();
        for (String r : roleSplitList) {
            grantedList.add(new SimpleGrantedAuthority(r));
        }

        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(email, snsId,
            grantedList);
        Authentication authenticate = authenticationManagerBuilder.getObject().authenticate(
            authenticationToken);

        LoginVo loginVo = tokenProvider.generateTokenDto(authenticate);

        saveRedisToken(loginVo);

        return loginVo;
    }
}
