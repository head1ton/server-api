package ai.serverapi.controller.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import ai.serverapi.BaseTest;
import ai.serverapi.domain.dto.member.JoinDto;
import ai.serverapi.domain.dto.member.LoginDto;
import ai.serverapi.domain.enums.ResultCode;
import ai.serverapi.domain.vo.member.LoginVo;
import ai.serverapi.repository.member.MemberRepository;
import ai.serverapi.service.member.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

@Slf4j
@SpringBootTest
public class MemberControllerDocs extends BaseTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("/member/join")
    @Order(1)
    public void join() throws Exception {
        String email = "tester@gmail.com";
        String password = "password";
        String name = "name";
        String nickname = "nick";
        String birth = "19941030";
        JoinDto joinDto = new JoinDto(email, password, name, nickname, birth);

        ResultActions resultActions = mockMvc.perform(
            post("/member/join").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(joinDto))
        ).andDo(print());

        resultActions.andDo(docs.document(
            requestFields(
                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                fieldWithPath("birth").type(JsonFieldType.STRING).description("생년월일 ex) 19941030")
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
    @DisplayName("/member/login")
    @Order(2)
    public void login() throws Exception {
        String email = "tester@gmail.com";
        String password = "password";

        LoginDto loginDto = new LoginDto(email, password);

        ResultActions resultActions = mockMvc.perform(
            post("/member/login").contentType(MediaType.APPLICATION_JSON)
                                 .content(objectMapper.writeValueAsString(loginDto))
        ).andDo(print());

//        log.debug(resultActions.toString());

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
    @DisplayName("/member/refresh/{refresh_token}")
    @Order(3)
    public void refresh() throws Exception {
        String email = "test3@gmail.com";
        String password = "password";
        String name = "name";
        String nickname = "nick";
        String birth = "19941030";
        JoinDto joinDto = new JoinDto(email, password, name, nickname, birth);
        memberService.join(joinDto);
        LoginDto loginDto = new LoginDto(email, password);
        LoginVo loginVo = memberService.login(loginDto);

        ResultActions resultActions = mockMvc.perform(
            get("/member/refresh/{refresh_token}", loginVo.getRefreshToken())
        ).andDo(print());

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

    @Test
    @DisplayName("/member")
    public void member() throws Exception {
        String email = "test4@gmail.com";
        String password = "password";
        String name = "name";
        String nickname = "nick";
        String birth = "19941030";
        JoinDto joinDto = new JoinDto(email, password, name, nickname, birth);
        memberService.join(joinDto);

        LoginDto loginDto = new LoginDto(email, password);
        LoginVo loginVo = memberService.login(loginDto);

        ResultActions resultActions = mockMvc.perform(
            get("/member")
                .header(AUTHORIZATION, "Bearer " + loginVo.getAccessToken())
        ).andDo(print());

        resultActions.andDo(docs.document(
            requestHeaders(
                headerWithName(AUTHORIZATION).description("access token")
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.member_id").type(JsonFieldType.NUMBER).description("member id"),
                fieldWithPath("data.email").type(JsonFieldType.STRING).description("email"),
                fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("nickname"),
                fieldWithPath("data.name").type(JsonFieldType.STRING).description("name"),
                fieldWithPath("data.role").type(JsonFieldType.STRING).description("role"),
                fieldWithPath("data.created_at").type(JsonFieldType.STRING).description("생성일"),
                fieldWithPath("data.modified_at").type(JsonFieldType.STRING).description("수정일")
            )
        ));
    }
}
