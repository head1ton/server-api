package ai.serverapi.domain.member.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import ai.serverapi.ControllerBaseTest;
import ai.serverapi.config.base.ResultCode;
import ai.serverapi.domain.member.dto.JoinDto;
import ai.serverapi.domain.member.dto.LoginDto;
import ai.serverapi.domain.member.service.MemberAuthService;
import ai.serverapi.domain.member.vo.LoginVo;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MemberAuthControllerDocs extends ControllerBaseTest {

    private final static String PREFIX = "/api/auth";
    @Autowired
    private MemberAuthService memberAuthService;

    @Test
    @DisplayName(PREFIX + "/join")
    void join() throws Exception {
        String email = "mercury@gmail.com";
        String password = "password";
        String name = "name";
        String nickname = "nick";
        String birth = "19941030";
        JoinDto joinDto = new JoinDto(email, password, name, nickname, birth);

        ResultActions resultActions = mockMvc.perform(
            post(PREFIX + "/join").contentType(MediaType.APPLICATION_JSON)
                                  .content(objectMapper.writeValueAsString(joinDto))
        ).andDo(print());

        String contentAsString = resultActions.andReturn().getResponse()
                                              .getContentAsString(StandardCharsets.UTF_8);
        assertThat(contentAsString).contains(ResultCode.POST.code);

        resultActions.andDo(docs.document(
            requestFields(
                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                fieldWithPath("birth").type(JsonFieldType.STRING).description("생년월일 ex) 19941030")
                                      .optional()
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.name").type(JsonFieldType.STRING).description("이름"),
                fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("닉네임"),
                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일")
            )
        ));
    }

    @Test
    @DisplayName(PREFIX + "/login")
    void login() throws Exception {
        String email = "mercury@gmail.com";
        String password = "password";

        LoginDto loginDto = new LoginDto(email, password);

        ResultActions resultActions = mockMvc.perform(
            post(PREFIX + "/login").contentType(MediaType.APPLICATION_JSON)
                                   .content(objectMapper.writeValueAsString(loginDto))
        ).andDo(print());

        String contentAsString = resultActions.andReturn().getResponse()
                                              .getContentAsString(StandardCharsets.UTF_8);
        assertThat(contentAsString).contains(ResultCode.SUCCESS.code);

        resultActions.andDo(docs.document(
            requestFields(
                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.type").type(JsonFieldType.STRING).description("token type"),
                fieldWithPath("data.access_token").type(JsonFieldType.STRING)
                                                  .description("access token"),
                fieldWithPath("data.refresh_token").type(JsonFieldType.STRING)
                                                   .description("refresh token (기한 : 발급일로부터 7일)"),
                fieldWithPath("data.access_token_expired").type(JsonFieldType.NUMBER)
                                                          .description("access token expired")
            )
        ));
    }

    @Test
    @DisplayName(PREFIX + "/refresh/{refresh_token}")
    void refresh() throws Exception {
        String email = "test3@gmail.com";
        String password = "password";
        String name = "name";
        String nickname = "nick";
        String birth = "19941030";
        JoinDto joinDto = new JoinDto(email, password, name, nickname, birth);
        memberAuthService.join(joinDto);
        LoginDto loginDto = new LoginDto(email, password);
        LoginVo loginVo = memberAuthService.login(loginDto);

        ResultActions resultActions = mockMvc.perform(
            get(PREFIX + "/refresh/{refresh_token}", loginVo.refreshToken())
        ).andDo(print());

        String contentAsString = resultActions.andReturn().getResponse()
                                              .getContentAsString(StandardCharsets.UTF_8);
        assertThat(contentAsString).contains(ResultCode.SUCCESS.code);

        resultActions.andDo(docs.document(
            pathParameters(
                parameterWithName("refresh_token").description("refresh token")
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.type").type(JsonFieldType.STRING).description("token type"),
                fieldWithPath("data.access_token").type(JsonFieldType.STRING)
                                                  .description("access token"),
                fieldWithPath("data.refresh_token").type(JsonFieldType.STRING)
                                                   .description("refresh token (기한 : 발급일로부터 7일)"),
                fieldWithPath("data.access_token_expired").type(JsonFieldType.NUMBER)
                                                          .description("access token expired")
            )
        ));
    }
}
