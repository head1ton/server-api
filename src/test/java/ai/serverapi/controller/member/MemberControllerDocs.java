package ai.serverapi.controller.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import ai.serverapi.BaseTest;
import ai.serverapi.domain.dto.member.JoinDto;
import ai.serverapi.domain.dto.member.LoginDto;
import ai.serverapi.domain.enums.ResultCode;
import ai.serverapi.domain.enums.Role;
import ai.serverapi.domain.vo.member.LoginVo;
import ai.serverapi.service.member.MemberAuthService;
import ai.serverapi.service.member.MemberService;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

@Slf4j
@SpringBootTest
class MemberControllerDocs extends BaseTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberAuthService memberAuthService;
    private final static String PREFIX = "/api/member";
    private final static String EMAIL = "earth@gmail.com";
    private final static String PASSWORD = "password";

    @Test
    @DisplayName(PREFIX)
    void member() throws Exception {

        LoginDto loginDto = new LoginDto(EMAIL, PASSWORD);
        LoginVo loginVo = memberAuthService.login(loginDto);

        ResultActions resultActions = mockMvc.perform(
            get(PREFIX)
                .header(AUTHORIZATION, "Bearer " + loginVo.getAccessToken())
        ).andDo(print());

        String contentAsString = resultActions.andReturn().getResponse()
                                              .getContentAsString(StandardCharsets.UTF_8);
        assertThat(contentAsString).contains(ResultCode.SUCCESS.code);

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
                fieldWithPath("data.role").type(JsonFieldType.STRING)
                                          .description(String.format("권한 (일반 유저 : %s, 판매자 : %s)",
                                              Role.MEMBER, Role.SELLER)),
                fieldWithPath("data.created_at").type(JsonFieldType.STRING).description("생성일"),
                fieldWithPath("data.modified_at").type(JsonFieldType.STRING).description("수정일")
            )
        ));
    }

    @Test
    @DisplayName(PREFIX + "/seller")
    void applySeller() throws Exception {

        JoinDto joinDto = new JoinDto(EMAIL, PASSWORD, "name", "nick", "19941030");
        memberAuthService.join(joinDto);
        LoginDto loginDto = new LoginDto(EMAIL, PASSWORD);
        LoginVo loginVo = memberAuthService.login(loginDto);

        ResultActions resultActions = mockMvc.perform(
            post(PREFIX + "/seller")
                .header(AUTHORIZATION, "Bearer " + loginVo.getAccessToken())
        ).andDo(print());

        String contentAsString = resultActions.andReturn().getResponse()
                                              .getContentAsString(StandardCharsets.UTF_8);
        assertThat(contentAsString).contains(ResultCode.SUCCESS.code);

        resultActions.andDo(docs.document(
            requestHeaders(
                headerWithName(AUTHORIZATION).description("access token")
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.message").type(JsonFieldType.STRING).description("성공")
            )
        ));
    }
}
