package ai.serverapi.controller.member;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ai.serverapi.BaseTest;
import ai.serverapi.domain.dto.member.kakao.KakaoLoginResponseDto;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class OAuthControllerDocs extends BaseTest {

    private static MockWebServer mockWebServer;
    private final String PREFIX = "/api/oauth";
    @MockBean(name = "kakaoClient")
    private WebClient kakaoClient;
    @MockBean(name = "kakaoApiClient")
    private WebClient kakaoApiClient;
    private static WebClient webClient;

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
        //given
        given(kakaoClient.post()).willReturn(webClient.post());
        KakaoLoginResponseDto dto = KakaoLoginResponseDto.builder()
                                                         .access_token("access token")
                                                         .expires_in(1L)
                                                         .refresh_token("refresh token")
                                                         .refresh_token_expires_in(2L)
                                                         .build();
        mockWebServer.enqueue(
            new MockResponse().setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                              .setBody(objectMapper.writeValueAsString(dto)));

        //when
        ResultActions resultActions = mockMvc.perform(
            get(PREFIX + "/kakao").param("code", "kakao_code")
        ).andDo(print());

        //then
        resultActions.andExpect(status().is2xxSuccessful());

        //docs
        resultActions.andDo(docs.document(
            queryParameters(
                parameterWithName("code").description("kakao login code")
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.type").type(JsonFieldType.STRING).description("token type"),
                fieldWithPath("data.access_token").type(JsonFieldType.STRING)
                                                  .description("카카오에서 발급된 access token"),
                fieldWithPath("data.refresh_token").type(JsonFieldType.STRING)
                                                   .description("카카오에서 발급된 refresh token"),
                fieldWithPath("data.access_token_expired").type(JsonFieldType.NUMBER)
                                                          .description("access token expired"),
                fieldWithPath("data.refresh_token_expired").type(JsonFieldType.NUMBER)
                                                           .description("refresh token expired")
            )
        ));
    }

    @Test
    @DisplayName(PREFIX + "/kakao/login")
    void oauthKakaoLogin() throws Exception {
        given(kakaoApiClient.post()).willReturn(webClient.post());

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

        ResultActions resultActions = mockMvc.perform(
            get(PREFIX + "/kakao/login").param("access_token", "kakao_access_token")
        ).andDo(print());

        resultActions.andExpect(status().is2xxSuccessful());

        resultActions.andDo(docs.document(
            queryParameters(
                parameterWithName("access_token").description("kakao login access token")
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.type").type(JsonFieldType.STRING).description("token type"),
                fieldWithPath("data.access_token").type(JsonFieldType.STRING)
                                                  .description("ServerAPI에서 발급된 access token"),
                fieldWithPath("data.refresh_token").type(JsonFieldType.STRING)
                                                   .description("ServerAPI에서 발급된 refresh token"),
                fieldWithPath("data.access_token_expired").type(JsonFieldType.NUMBER).description(
                    "ServerAPI에서 발급된 access token expired")
            )
        ));
    }
}
