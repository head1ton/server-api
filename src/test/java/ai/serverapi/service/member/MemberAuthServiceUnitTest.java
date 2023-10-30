package ai.serverapi.service.member;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ai.serverapi.domain.dto.member.KakaoLoginResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class MemberAuthServiceUnitTest {

    @InjectMocks
    private MemberAuthService memberAuthService;
    @Mock
    private Environment env;
    private WebClient webClientMock;
    private RequestBodyUriSpec requestBodyUriMock;
    private RequestBodySpec requestBodyMock;
    private RequestHeadersSpec requestHeadersMock;
    private ResponseSpec responseMock;

    @BeforeEach
    void mockWebClient() {
        requestBodyUriMock = mock(WebClient.RequestBodyUriSpec.class);
        requestHeadersMock = mock(WebClient.RequestHeadersSpec.class);
        requestBodyMock = mock(WebClient.RequestBodySpec.class);
        responseMock = mock(WebClient.ResponseSpec.class);
        webClientMock = mock(WebClient.class);
    }

    @Test
    @DisplayName("kakaoTest")
    void kakaoTest() {
        String expectedUri = "https://kauth.kakao.com/oauth/token";
        BDDMockito.given(eq(env.getProperty("kakao.client_id"))).willReturn("kakao client id");
        when(webClientMock.post()).thenReturn(requestBodyUriMock);
        when(requestBodyUriMock.uri(eq(expectedUri))).thenReturn(requestBodyMock);
        when(requestBodyMock.bodyValue(any())).thenReturn(requestHeadersMock);
        when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        when(responseMock.bodyToMono(KakaoLoginResponseDto.class)).thenReturn(
            Mono.just(KakaoLoginResponseDto.builder().build()));

        memberAuthService.loginKakao("kakao login token");
    }
}
