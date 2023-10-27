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
import ai.serverapi.service.member.MemberAuthService;
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
    private final String PREFIX = "/api/member";
    @Autowired
    private MemberAuthService memberAuthService;

    @Test
    @DisplayName(PREFIX)
    public void member() throws Exception {
        String email = "test4@gmail.com";
        String password = "password";
        String name = "name";
        String nickname = "nick";
        String birth = "19941030";
        JoinDto joinDto = new JoinDto(email, password, name, nickname, birth);
        memberAuthService.join(joinDto);

        LoginDto loginDto = new LoginDto(email, password);
        LoginVo loginVo = memberAuthService.login(loginDto);

        ResultActions resultActions = mockMvc.perform(
            get(PREFIX)
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
