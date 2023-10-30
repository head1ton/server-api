package ai.serverapi.service.member;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import ai.serverapi.common.security.TokenProvider;
import ai.serverapi.domain.dto.member.KakaoLoginResponseDto;
import ai.serverapi.repository.member.MemberRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class MemberAuthServiceUnitTest {

    @InjectMocks
    private MemberAuthService memberAuthService;
    @Mock
    private Environment env;
    private static MockWebServer mockWebServer;
    private final ObjectMapper objectMapper = new ObjectMapper();
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
            env);
    }

    @Test
    @DisplayName("kakaoTest")
    void kakaoTest() throws Exception {

        given(env.getProperty(eq("kakao.client_id"))).willReturn("kakao client id");
        KakaoLoginResponseDto dto = KakaoLoginResponseDto.builder()
                                                         .access_token("access token")
                                                         .expires_in(1L)
                                                         .refresh_token("refresh token")
                                                         .refresh_token_expires_in(2L)
                                                         .build();

        mockWebServer.enqueue(
            new MockResponse().setHeader("Content-Type", MediaType.APPLICATION_JSON)
                              .setBody(objectMapper.writeValueAsString(dto)));
        mockWebServer.enqueue(
            new MockResponse().setHeader("Content-Type", MediaType.APPLICATION_JSON)
                              .setBody(objectMapper.writeValueAsString(dto)));

        memberAuthService.loginKakao("kakao login token");
    }
}
