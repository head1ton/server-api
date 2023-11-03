package ai.serverapi.controller.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ai.serverapi.BaseTest;
import ai.serverapi.domain.dto.member.JoinDto;
import ai.serverapi.domain.dto.member.LoginDto;
import ai.serverapi.domain.dto.member.PatchMemberDto;
import ai.serverapi.domain.dto.member.PostBuyerInfoDto;
import ai.serverapi.domain.dto.member.PutBuyerInfoDto;
import ai.serverapi.domain.entity.member.BuyerInfo;
import ai.serverapi.domain.entity.member.Member;
import ai.serverapi.domain.enums.ResultCode;
import ai.serverapi.domain.enums.Role;
import ai.serverapi.domain.vo.member.LoginVo;
import ai.serverapi.repository.member.BuyerInfoRepository;
import ai.serverapi.repository.member.MemberRepository;
import ai.serverapi.service.member.MemberAuthService;
import ai.serverapi.service.member.MemberService;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MemberControllerDocs extends BaseTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberAuthService memberAuthService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private BuyerInfoRepository buyerInfoRepository;
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

    @Test
    @DisplayName(PREFIX)
    void patchMember() throws Exception {
        String email = "patch@gmail.com";
        String password = "password";
        String changePassword = "password2";
        String changeName = "수정함";
        String changeBirth = "19941030";

        JoinDto joinDto = new JoinDto(email, password, "수정자", "수정할거야", "19991010");
        joinDto.passwordEncoder(passwordEncoder);
        memberRepository.save(Member.of(joinDto));
        LoginDto loginDto = new LoginDto(email, password);
        LoginVo loginVo = memberAuthService.login(loginDto);

        PatchMemberDto patchMemberDto = new PatchMemberDto(changeBirth, changeName, changePassword,
            "수정되버림", null);

        ResultActions resultActions = mockMvc.perform(
            patch(PREFIX)
                .header(AUTHORIZATION, "Bearer " + loginVo.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(patchMemberDto))
        ).andDo(print());

        String contentAsString = resultActions.andReturn().getResponse()
                                              .getContentAsString(StandardCharsets.UTF_8);
        assertThat(contentAsString).contains(ResultCode.SUCCESS.code);

        resultActions.andDo(docs.document(
            requestHeaders(
                headerWithName(AUTHORIZATION).description("access token")
            ),
            requestFields(
                fieldWithPath("birth").type(JsonFieldType.STRING).description("생일").optional(),
                fieldWithPath("name").type(JsonFieldType.STRING).description("이름").optional(),
                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임").optional(),
                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호").optional(),
                fieldWithPath("status").type(JsonFieldType.STRING)
                                       .description("회원 상태(아직 사용하지 않음) ex)탈퇴").optional()
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.message").type(JsonFieldType.STRING).description("결과 메세지")
            )
        ));
    }

    @Test
    @DisplayName(PREFIX + "/buyer-info (POST)")
    void postBuyerInfo() throws Exception {
        LoginDto loginDto = new LoginDto(MEMBER_EMAIL, PASSWORD);
        LoginVo loginVo = memberAuthService.login(loginDto);

        PostBuyerInfoDto postBuyerInfoDto = new PostBuyerInfoDto("구매할 사람", "buyer@gmail.com",
            "01012341234");

        ResultActions perform = mockMvc.perform(
            post(PREFIX + "/buyer-info")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(postBuyerInfoDto))
                .header(AUTHORIZATION, "Bearer " + loginVo.getAccessToken())
        );

        perform.andExpect(status().is2xxSuccessful());

        perform.andDo(docs.document(
            requestHeaders(
                headerWithName(AUTHORIZATION).description("access token")
            ),
            requestFields(
                fieldWithPath("name").type(JsonFieldType.STRING).description("구매자 이름"),
                fieldWithPath("email").type(JsonFieldType.STRING).description("구매자 email"),
                fieldWithPath("tel").type(JsonFieldType.STRING).description("구매자 전화번호")
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.message").type(JsonFieldType.STRING).description("결과 메세지")
            )
        ));
    }

    @Test
    @DisplayName(PREFIX + "/buyer-info (GET)")
    void getBuyerInfo() throws Exception {
        LoginDto loginDto = new LoginDto(MEMBER_EMAIL, PASSWORD);
        LoginVo loginVo = memberAuthService.login(loginDto);
        Member member = memberRepository.findByEmail(MEMBER_EMAIL).get();
        member.putBuyerInfo(BuyerInfo.of(null, "구매자", "buyer-info@gmail.com", "01012341234"));

        ResultActions resultActions = mockMvc.perform(
            get(PREFIX + "/buyer-info")
                .header(AUTHORIZATION, "Bearer " + loginVo.getAccessToken())
        );

        resultActions.andExpect(status().is2xxSuccessful());

        resultActions.andDo(docs.document(
            requestHeaders(
                headerWithName(AUTHORIZATION).description("access token")
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("구매자 번호"),
                fieldWithPath("data.name").type(JsonFieldType.STRING).description("구매자 이름"),
                fieldWithPath("data.email").type(JsonFieldType.STRING).description("구매자 email"),
                fieldWithPath("data.tel").type(JsonFieldType.STRING).description("구매자 연락처"),
                fieldWithPath("data.created_at").type(JsonFieldType.STRING).description("생성일"),
                fieldWithPath("data.modified_at").type(JsonFieldType.STRING).description("수정일")
            )
        ));
    }

    @Test
    @DisplayName(PREFIX + "/buyer-info (PUT)")
    void putBuyerInfo() throws Exception {
        LoginDto loginDto = new LoginDto(MEMBER_EMAIL, PASSWORD);
        LoginVo login = memberAuthService.login(loginDto);
        BuyerInfo buyerInfo = buyerInfoRepository.save(
            BuyerInfo.of(null, "구매자", "buyer@gmail.com", "01012341234"));
        PutBuyerInfoDto putBuyerInfoDto = new PutBuyerInfoDto(buyerInfo.getId(), "수정자",
            "put-buyer@gmail.com", "01011112222");

        ResultActions resultActions = mockMvc.perform(
            put(PREFIX + "/buyer-info")
                .header(AUTHORIZATION, "Bearer " + login.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(putBuyerInfoDto))
        );

        resultActions.andExpect(status().is2xxSuccessful());

        resultActions.andDo(docs.document(
            requestHeaders(
                headerWithName(AUTHORIZATION).description("access token")
            ),
            requestFields(
                fieldWithPath("id").type(JsonFieldType.NUMBER).description("구매자 정보 id"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("구매자 이름"),
                fieldWithPath("email").type(JsonFieldType.STRING).description("구매자 email"),
                fieldWithPath("tel").type(JsonFieldType.STRING).description("구매자 전화번호")
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.message").type(JsonFieldType.STRING).description("결과 메세지")
            )
        ));
    }
}
