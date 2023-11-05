package ai.serverapi.member.controller;

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

import ai.serverapi.ControllerBaseTest;
import ai.serverapi.config.base.ResultCode;
import ai.serverapi.member.domain.dto.JoinDto;
import ai.serverapi.member.domain.dto.LoginDto;
import ai.serverapi.member.domain.dto.PatchMemberDto;
import ai.serverapi.member.domain.dto.PostRecipientDto;
import ai.serverapi.member.domain.dto.PostSellerDto;
import ai.serverapi.member.domain.dto.PutSellerDto;
import ai.serverapi.member.domain.entity.Member;
import ai.serverapi.member.domain.entity.Recipient;
import ai.serverapi.member.domain.enums.RecipientInfoStatus;
import ai.serverapi.member.domain.enums.Role;
import ai.serverapi.member.domain.vo.LoginVo;
import ai.serverapi.member.repository.MemberRepository;
import ai.serverapi.member.repository.RecipientRepository;
import ai.serverapi.member.service.MemberAuthService;
import ai.serverapi.member.service.MemberService;
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
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MemberControllerDocs extends ControllerBaseTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberAuthService memberAuthService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private RecipientRepository recipientRepository;
    private final static String PREFIX = "/api/member";
    private final static String EMAIL = "earth@gmail.com";
    private final static String PASSWORD = "password";

    @Test
    @DisplayName(PREFIX + " (GET)")
    void member() throws Exception {

        LoginDto loginDto = new LoginDto(MEMBER_EMAIL, PASSWORD);
        LoginVo loginVo = memberAuthService.login(loginDto);

        ResultActions resultActions = mockMvc.perform(
            get(PREFIX)
                .header(AUTHORIZATION, "Bearer " + loginVo.accessToken())
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
                fieldWithPath("data.status").type(JsonFieldType.STRING).description("상태값"),
                fieldWithPath("data.created_at").type(JsonFieldType.STRING).description("생성일"),
                fieldWithPath("data.modified_at").type(JsonFieldType.STRING).description("수정일")
            )
        ));
    }

    @Test
    @DisplayName(PREFIX + "/seller (POST)")
    void postSeller() throws Exception {

        JoinDto joinDto = new JoinDto(EMAIL, passwordEncoder.encode(PASSWORD), "name", "nick",
            "19941030");
        memberRepository.save(Member.of(joinDto));
        LoginDto loginDto = new LoginDto(EMAIL, PASSWORD);
        LoginVo loginVo = memberAuthService.login(loginDto);

        PostSellerDto postSellerDto = new PostSellerDto("판매자 이름", "010-1234-1234",
            "제주도 서귀포시 서귀포면 한라산길", "mail@gmail.com");

        ResultActions resultActions = mockMvc.perform(
            post(PREFIX + "/seller")
                .header(AUTHORIZATION, "Bearer " + loginVo.accessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(postSellerDto))
        ).andDo(print());

        String contentAsString = resultActions.andReturn().getResponse()
                                              .getContentAsString(StandardCharsets.UTF_8);
        assertThat(contentAsString).contains(ResultCode.POST.code);

        resultActions.andDo(docs.document(
            requestHeaders(
                headerWithName(AUTHORIZATION).description("access token")
            ),
            requestFields(
                fieldWithPath("company").type(JsonFieldType.STRING).description("회사명"),
                fieldWithPath("tel").type(JsonFieldType.STRING).description("회사 연락처"),
                fieldWithPath("address").type(JsonFieldType.STRING).description("회사 주소"),
                fieldWithPath("email").type(JsonFieldType.STRING).description("회사 이메일").optional()
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.message").type(JsonFieldType.STRING).description("성공")
            )
        ));
    }

    @Test
    @DisplayName(PREFIX + "/seller (GET)")
    void getSeller() throws Exception {
        LoginDto loginDto = new LoginDto(SELLER_EMAIL, PASSWORD);
        LoginVo loginVo = memberAuthService.login(loginDto);

        ResultActions resultActions = mockMvc.perform(
            get(PREFIX + "/seller")
                .header(AUTHORIZATION, "Bearer " + loginVo.accessToken())
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
                fieldWithPath("data.seller_id").type(JsonFieldType.NUMBER).description("seller id"),
                fieldWithPath("data.email").type(JsonFieldType.STRING).description("email"),
                fieldWithPath("data.company").type(JsonFieldType.STRING).description("회사명"),
                fieldWithPath("data.address").type(JsonFieldType.STRING).description("회사 주소"),
                fieldWithPath("data.tel").type(JsonFieldType.STRING).description("회사 연락처")
            )
        ));
    }

    @Test
    @DisplayName(PREFIX + "/seller (PUT)")
    void putSeller() throws Exception {
        LoginDto loginDto = new LoginDto(SELLER_EMAIL, PASSWORD);
        LoginVo loginVo = memberAuthService.login(loginDto);

        PutSellerDto putSellerDto = new PutSellerDto("변경된 판매자 이름", "010-1234-1234",
            "강원도 철원군 철원면 백두산길 128", "mail@gmail.com");

        ResultActions resultActions = mockMvc.perform(
            put(PREFIX + "/seller")
                .header(AUTHORIZATION, "Bearer " + loginVo.accessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(putSellerDto))
        ).andDo(print());

        String contentAsString = resultActions.andReturn().getResponse()
                                              .getContentAsString(StandardCharsets.UTF_8);
        assertThat(contentAsString).contains(ResultCode.SUCCESS.code);

        resultActions.andDo(docs.document(
            requestHeaders(
                headerWithName(AUTHORIZATION).description("access token")
            ),
            requestFields(
                fieldWithPath("company").type(JsonFieldType.STRING).description("회사명"),
                fieldWithPath("tel").type(JsonFieldType.STRING).description("회사 연락처"),
                fieldWithPath("address").type(JsonFieldType.STRING).description("회사 주소"),
                fieldWithPath("email").type(JsonFieldType.STRING).description("회사 이메일").optional()
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.message").type(JsonFieldType.STRING).description("성공")
            )
        ));
    }

    @Test
    @DisplayName(PREFIX + " (PATCH)")
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
                .header(AUTHORIZATION, "Bearer " + loginVo.accessToken())
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
    @DisplayName(PREFIX + "/recipient (POST)")
    void postRecipient() throws Exception {
        LoginDto loginDto = new LoginDto(MEMBER_EMAIL, PASSWORD);
        LoginVo loginVo = memberAuthService.login(loginDto);

        PostRecipientDto postRecipientDto = new PostRecipientDto("수령인", "recipient@gmail.com",
            "01012341234");

        ResultActions resultActions = mockMvc.perform(
            post(PREFIX + "/recipient")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(postRecipientDto))
                .header(AUTHORIZATION, "Bearer " + loginVo.accessToken())
        );

        resultActions.andExpect(status().is2xxSuccessful());

        resultActions.andDo(docs.document(
            requestHeaders(
                headerWithName(AUTHORIZATION).description("access token")
            ),
            requestFields(
                fieldWithPath("name").type(JsonFieldType.STRING).description("수령인 이름"),
                fieldWithPath("address").type(JsonFieldType.STRING).description("수령인 주소"),
                fieldWithPath("tel").type(JsonFieldType.STRING).description("수령인 전화번호")
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.message").type(JsonFieldType.STRING).description("결과 메세지")
            )
        ));
    }

    @Test
    @DisplayName(PREFIX + "/recipient (GET)")
    @Transactional
    void getRecipient() throws Exception {
        LoginDto loginDto = new LoginDto(MEMBER_EMAIL, PASSWORD);
        LoginVo loginVo = memberAuthService.login(loginDto);
        Member member = memberRepository.findByEmail(MEMBER_EMAIL).get();

        Recipient recipient1 = Recipient.of(member, "수령인1", "주소1", "01012341234",
            RecipientInfoStatus.NORMAL);
        Recipient recipient2 = Recipient.of(member, "수령인2", "주소2", "01011112222",
            RecipientInfoStatus.NORMAL);
        Recipient saveRecipient1 = recipientRepository.save(recipient1);
        Recipient saveRecipient2 = recipientRepository.save(recipient2);
        member.getRecipientList().add(saveRecipient1);
        member.getRecipientList().add(saveRecipient2);

        ResultActions resultActions = mockMvc.perform(
            get(PREFIX + "/recipient")
                .header(AUTHORIZATION, "Bearer " + loginVo.accessToken())
        );

        resultActions.andExpect(status().is2xxSuccessful());

        resultActions.andDo(docs.document(
            requestHeaders(
                headerWithName(AUTHORIZATION).description("access token")
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.list[].id").type(JsonFieldType.NUMBER).description("수령인 id"),
                fieldWithPath("data.list[].name").type(JsonFieldType.STRING).description("수령인 이름"),
                fieldWithPath("data.list[].address").type(JsonFieldType.STRING)
                                                    .description("수령인 주소"),
                fieldWithPath("data.list[].tel").type(JsonFieldType.STRING).description("수령인 연락처"),
                fieldWithPath("data.list[].status").type(JsonFieldType.STRING).description("상태값"),
                fieldWithPath("data.list[].created_at").type(JsonFieldType.STRING)
                                                       .description("생성일"),
                fieldWithPath("data.list[].modified_at").type(JsonFieldType.STRING)
                                                        .description("수정일")
            )
        ));
    }
}