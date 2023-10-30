package ai.serverapi.controller.member;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import ai.serverapi.BaseTest;
import ai.serverapi.domain.dto.member.KakaoLoginResponseDto;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class OAuthControllerTest extends BaseTest {

    private static final String PREFIX = "/api/oauth";
    private static WebClient webClient;
    private static MockWebServer mockWebServer;
    @MockBean
    private WebClient kakaoClient;

    @BeforeAll
    static void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        webClient = WebClient.create("http://localhost:" + mockWebServer.getPort());
    }

    @AfterAll
    static void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName(PREFIX + "/kakao")
    void oauthKakao() throws Exception {
        BDDMockito.given(kakaoClient.post()).willReturn(webClient.post());

        KakaoLoginResponseDto dto = KakaoLoginResponseDto.builder()
                                                         .access_token("access token")
                                                         .expires_in(1L)
                                                         .refresh_token("refresh token")
                                                         .refresh_token_expires_in(2L)
                                                         .build();

        mockWebServer.enqueue(
            new MockResponse().setHeader("Content-Type", MediaType.APPLICATION_JSON)
                              .setBody(objectMapper.writeValueAsString(dto)));

        mockMvc.perform(
            RestDocumentationRequestBuilders.get(PREFIX + "/kakao")
                                            .param("code", "kakao login token")
        ).andDo(print());
    }
}
