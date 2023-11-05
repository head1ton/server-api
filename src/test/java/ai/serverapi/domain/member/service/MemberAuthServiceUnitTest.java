package ai.serverapi.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import ai.serverapi.config.mail.MyMailSender;
import ai.serverapi.config.security.TokenProvider;
import ai.serverapi.domain.member.dto.JoinDto;
import ai.serverapi.domain.member.dto.kakao.KakaoLoginResponseDto;
import ai.serverapi.domain.member.entity.Member;
import ai.serverapi.domain.member.enums.SnsJoinType;
import ai.serverapi.domain.member.repository.MemberRepository;
import ai.serverapi.domain.member.vo.LoginVo;
import io.jsonwebtoken.Jwts;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class MemberAuthServiceUnitTest {

    @InjectMocks
    private MemberAuthService memberAuthService;
    @Mock
    private Environment env;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private RedisTemplate redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOperations;
    @Mock
    private MyMailSender myMailSender;
    private static MockWebServer mockWebServer;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String TYPE = "Bearer";

    @BeforeAll
    static void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        final WebClient webClient = WebClient.create(baseUrl);
        memberAuthService = new MemberAuthService(memberRepository,
            passwordEncoder, authenticationManagerBuilder, tokenProvider, redisTemplate, webClient,
            webClient, env, myMailSender);
    }

    @Test
    @DisplayName("이미 가입한 회원은 회원 가입 불가")
    void joinFail1() {
        JoinDto joinDto = new JoinDto("join-1@gmailcom", "password", "홍길동", "닉네임", "19991005");
        given(memberRepository.findByEmail(anyString())).willReturn(
            Optional.of(Member.of(joinDto)));

        Throwable throwable = catchThrowable(() -> memberAuthService.join(joinDto));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("이미 존재하는 회원");
    }

    @Test
    @DisplayName("유효하지 않은 토큰은 refresh 실패")
    void refreshFail1() {
        //given
        String refreshToken = "refresh";
        given(tokenProvider.validateToken(anyString())).willReturn(false);
        //when
        Throwable throwable = catchThrowable(() -> memberAuthService.refresh(refreshToken));
        //then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 토큰");
    }

    @Test
    @DisplayName("유효하지 않은 회원은 refresh 실패")
    void refreshFail2() {
        //given
        String refreshToken = "refresh";
        given(tokenProvider.validateToken(anyString())).willReturn(true);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "1");
        given(tokenProvider.parseClaims(anyString())).willReturn(Jwts.claims(claims));
        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));
        //when
        Throwable throwable = catchThrowable(() -> memberAuthService.refresh(refreshToken));
        //then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 회원");
    }

    @Test
    @DisplayName("kakao auth success")
    void kakaoAuthSuccess() throws Exception {

        given(env.getProperty("kakao.client_id")).willReturn("kakao client id");
        KakaoLoginResponseDto dto = KakaoLoginResponseDto.builder()
                                                         .access_token("access token")
                                                         .expires_in(1L)
                                                         .refresh_token("refresh token")
                                                         .refresh_token_expires_in(2L)
                                                         .build();

        mockWebServer.enqueue(
            new MockResponse().setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                              .setBody(objectMapper.writeValueAsString(dto)));

        LoginVo kakaoLoginToken = memberAuthService.authKakao("kakao login code");

        assertThat(kakaoLoginToken).isNotNull();
    }

    @Test
    @DisplayName("kakao 이메일이 존재하지 않는 회원은 fail")
    void kakaoLoginFail() throws Exception {
        String kakaoReturnString = "{\n" +
            "    \"id\": 1928719116,\n" +
            "    \"connected_at\": \"2023-08-31T12:14:37Z\",\n" +
            "    \"for_partner\": {\n" +
            "        \"uuid\": \"VmdSZlJhUWBVeUt_R3VZaFhrUmco\"\n" +
            "    },\n" +
            "    \"properties\": {\n" +
            "        \"nickname\": \"머리만1톤 ㅡ Hwan\",\n" +
            "        \"profile_image\": \"http://k.kakaocdn.net/dn/bRBnQ0/btrDqeGorfQ/HOVR16HLKoIKgK0xdLmQt1/img_640x640.jpg\",\n"
            +
            "        \"thumbnail_image\": \"http://k.kakaocdn.net/dn/bRBnQ0/btrDqeGorfQ/HOVR16HLKoIKgK0xdLmQt1/img_110x110.jpg\"\n"
            +
            "    },\n" +
            "    \"kakao_account\": {\n" +
            "        \"profile_nickname_needs_agreement\": false,\n" +
            "        \"profile_image_needs_agreement\": false,\n" +
            "        \"profile\": {\n" +
            "            \"nickname\": \"머리만1톤 ㅡ Hwan\",\n" +
            "            \"thumbnail_image_url\": \"http://k.kakaocdn.net/dn/bRBnQ0/btrDqeGorfQ/HOVR16HLKoIKgK0xdLmQt1/img_110x110.jpg\",\n"
            +
            "            \"profile_image_url\": \"http://k.kakaocdn.net/dn/bRBnQ0/btrDqeGorfQ/HOVR16HLKoIKgK0xdLmQt1/img_640x640.jpg\",\n"
            +
            "            \"is_default_image\": false\n" +
            "        },\n" +
            "        \"has_email\": false,\n" +
            "        \"email_needs_agreement\": false,\n" +
            "        \"is_email_valid\": true,\n" +
            "        \"is_email_verified\": true,\n" +
            "        \"email\": \"head10000ton@gmail.com\",\n" +
            "        \"has_age_range\": true,\n" +
            "        \"age_range_needs_agreement\": false,\n" +
            "        \"age_range\": \"40~49\",\n" +
            "        \"has_birthday\": true,\n" +
            "        \"birthday_needs_agreement\": false,\n" +
            "        \"birthday\": \"0719\",\n" +
            "        \"birthday_type\": \"SOLAR\",\n" +
            "        \"has_gender\": true,\n" +
            "        \"gender_needs_agreement\": false,\n" +
            "        \"gender\": \"male\"\n" +
            "    }\n" +
            "}";

        mockWebServer.enqueue(
            new MockResponse().setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                              .setBody(kakaoReturnString));

        Throwable throwable = catchThrowable(
            () -> memberAuthService.loginKakao("kakao_access_token"));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("이메일이 존재하지 않는 회원입니다.");
    }

    @Test
    @DisplayName("kakao 전달 받은 내용이 없어 fail")
    void kakaoLoginFail2() throws Exception {
        String kakaoReturnString = "";
        mockWebServer.enqueue(
            new MockResponse().setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                              .setBody(kakaoReturnString));

        Throwable throwable = catchThrowable(
            () -> memberAuthService.loginKakao("kakao_access_token"));

        assertThat(throwable).isInstanceOf(IllegalStateException.class)
                             .hasMessageContaining("카카오에서 반환 받은 값이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("kakao login success")
    void kakaoLoginSuccess() throws Exception {
        String kakaoReturnString = "{\n" +
            "    \"id\": 1928719116,\n" +
            "    \"connected_at\": \"2023-08-31T12:14:37Z\",\n" +
            "    \"for_partner\": {\n" +
            "        \"uuid\": \"VmdSZlJhUWBVeUt_R3VZaFhrUmco\"\n" +
            "    },\n" +
            "    \"properties\": {\n" +
            "        \"nickname\": \"머리만1톤 ㅡ Hwan\",\n" +
            "        \"profile_image\": \"http://k.kakaocdn.net/dn/bRBnQ0/btrDqeGorfQ/HOVR16HLKoIKgK0xdLmQt1/img_640x640.jpg\",\n"
            +
            "        \"thumbnail_image\": \"http://k.kakaocdn.net/dn/bRBnQ0/btrDqeGorfQ/HOVR16HLKoIKgK0xdLmQt1/img_110x110.jpg\"\n"
            +
            "    },\n" +
            "    \"kakao_account\": {\n" +
            "        \"profile_nickname_needs_agreement\": false,\n" +
            "        \"profile_image_needs_agreement\": false,\n" +
            "        \"profile\": {\n" +
            "            \"nickname\": \"머리만1톤 ㅡ Hwan\",\n" +
            "            \"thumbnail_image_url\": \"http://k.kakaocdn.net/dn/bRBnQ0/btrDqeGorfQ/HOVR16HLKoIKgK0xdLmQt1/img_110x110.jpg\",\n"
            +
            "            \"profile_image_url\": \"http://k.kakaocdn.net/dn/bRBnQ0/btrDqeGorfQ/HOVR16HLKoIKgK0xdLmQt1/img_640x640.jpg\",\n"
            +
            "            \"is_default_image\": false\n" +
            "        },\n" +
            "        \"has_email\": true,\n" +
            "        \"email_needs_agreement\": false,\n" +
            "        \"is_email_valid\": true,\n" +
            "        \"is_email_verified\": true,\n" +
            "        \"email\": \"head1ton@gmail.com\",\n" +
            "        \"has_age_range\": true,\n" +
            "        \"age_range_needs_agreement\": false,\n" +
            "        \"age_range\": \"40~49\",\n" +
            "        \"has_birthday\": true,\n" +
            "        \"birthday_needs_agreement\": false,\n" +
            "        \"birthday\": \"0719\",\n" +
            "        \"birthday_type\": \"SOLAR\",\n" +
            "        \"has_gender\": true,\n" +
            "        \"gender_needs_agreement\": false,\n" +
            "        \"gender\": \"male\"\n" +
            "    }\n" +
            "}";

        mockWebServer.enqueue(
            new MockResponse().setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                              .setBody(kakaoReturnString));

        BDDMockito.given(memberRepository.findByEmail(anyString()))
                  .willReturn(Optional.ofNullable(null));
        BDDMockito.given(tokenProvider.generateTokenDto(any())).willReturn(
            new LoginVo(TYPE, "access token", "refresh token", 1L, null)
        );

        String snsId = "snsId";
        JoinDto joinDto = new JoinDto("kakao@email.com", snsId, "카카오회원", "카카오회원", null);
        BDDMockito.given(memberRepository.save(any())).willReturn(Member.of(joinDto, snsId,
            SnsJoinType.KAKAO));
        BDDMockito.given(authenticationManagerBuilder.getObject())
                  .willReturn(mockAuthenticationManager());

        Map<String, Object> claims = new HashMap<>();
        claims.put("exp", "1");

        BDDMockito.given(tokenProvider.parseClaims(anyString())).willReturn(Jwts.claims(claims));
        BDDMockito.given(redisTemplate.opsForValue()).willReturn(valueOperations);

        LoginVo loginVo = memberAuthService.loginKakao("kakao_access_token");

        assertThat(loginVo).isNotNull();
    }

    private AuthenticationManager mockAuthenticationManager() {
        return authentication -> new Authentication() {

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return null;
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(final boolean isAuthenticated)
                throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return null;
            }

        };
    }


}
