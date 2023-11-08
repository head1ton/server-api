package ai.serverapi.member.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
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
import ai.serverapi.global.base.ResultCode;
import ai.serverapi.global.s3.S3Service;
import ai.serverapi.member.domain.Member;
import ai.serverapi.member.domain.Recipient;
import ai.serverapi.member.dto.request.JoinRequest;
import ai.serverapi.member.dto.request.LoginRequest;
import ai.serverapi.member.dto.request.PatchMemberRequest;
import ai.serverapi.member.dto.request.PostIntroduceRequest;
import ai.serverapi.member.dto.request.PostRecipientRequest;
import ai.serverapi.member.dto.request.PostSellerRequest;
import ai.serverapi.member.dto.request.PutSellerRequest;
import ai.serverapi.member.dto.response.LoginResponse;
import ai.serverapi.member.enums.RecipientInfoStatus;
import ai.serverapi.member.enums.Role;
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
import org.springframework.boot.test.mock.mockito.MockBean;
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
    @MockBean
    private S3Service s3Service;
    private final static String PREFIX = "/api/member";
    private final static String EMAIL = "earth@gmail.com";
    private final static String PASSWORD = "password";

    @Test
    @DisplayName(PREFIX + " (GET)")
    void member() throws Exception {

        LoginRequest loginRequest = new LoginRequest(MEMBER_EMAIL, PASSWORD);
        LoginResponse loginResponse = memberAuthService.login(loginRequest);

        ResultActions resultActions = mockMvc.perform(
            get(PREFIX)
                .header(AUTHORIZATION, "Bearer " + loginResponse.accessToken())
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

        JoinRequest joinRequest = new JoinRequest(EMAIL, passwordEncoder.encode(PASSWORD), "name",
            "nick",
            "19941030");
        memberRepository.save(Member.of(joinRequest));
        LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);
        LoginResponse loginResponse = memberAuthService.login(loginRequest);

        PostSellerRequest postSellerRequest = new PostSellerRequest("판매자 이름", "010-1234-1234",
            "1234",
            "제주도 서귀포시 서귀포면 한라산길", "상세 주소", "mail@gmail.com");

        ResultActions resultActions = mockMvc.perform(
            post(PREFIX + "/seller")
                .header(AUTHORIZATION, "Bearer " + loginResponse.accessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(postSellerRequest))
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
                fieldWithPath("zonecode").type(JsonFieldType.STRING).description("회사 우편번호"),
                fieldWithPath("address").type(JsonFieldType.STRING).description("회사 주소"),
                fieldWithPath("address_detail").type(JsonFieldType.STRING).description("회사 상세 주소"),
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
        LoginRequest loginRequest = new LoginRequest(SELLER_EMAIL, PASSWORD);
        LoginResponse loginResponse = memberAuthService.login(loginRequest);

        ResultActions resultActions = mockMvc.perform(
            get(PREFIX + "/seller")
                .header(AUTHORIZATION, "Bearer " + loginResponse.accessToken())
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
                fieldWithPath("data.zonecode").type(JsonFieldType.STRING).description("우편번호"),
                fieldWithPath("data.address").type(JsonFieldType.STRING).description("회사 주소"),
                fieldWithPath("data.address_detail").type(JsonFieldType.STRING)
                                                    .description("회사 상세 주소"),
                fieldWithPath("data.tel").type(JsonFieldType.STRING).description("회사 연락처")
            )
        ));
    }

    @Test
    @DisplayName(PREFIX + "/seller (PUT)")
    void putSeller() throws Exception {
        LoginRequest loginRequest = new LoginRequest(SELLER_EMAIL, PASSWORD);
        LoginResponse loginResponse = memberAuthService.login(loginRequest);

        PutSellerRequest putSellerRequest = new PutSellerRequest("변경된 판매자 이름", "010-1234-1234",
            "1234",
            "강원도 철원군 철원면 백두산길 128", "상세 주소", "mail@gmail.com");

        ResultActions resultActions = mockMvc.perform(
            put(PREFIX + "/seller")
                .header(AUTHORIZATION, "Bearer " + loginResponse.accessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(putSellerRequest))
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
                fieldWithPath("zonecode").type(JsonFieldType.STRING).description("회사 우편번호"),
                fieldWithPath("address").type(JsonFieldType.STRING).description("회사 주소"),
                fieldWithPath("address_detail").type(JsonFieldType.STRING).description("회사 상세 주소"),
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

        JoinRequest joinRequest = new JoinRequest(email, password, "수정자", "수정할거야", "19991010");
        joinRequest.passwordEncoder(passwordEncoder);
        memberRepository.save(Member.of(joinRequest));
        LoginRequest loginRequest = new LoginRequest(email, password);
        LoginResponse loginResponse = memberAuthService.login(loginRequest);

        PatchMemberRequest patchMemberRequest = new PatchMemberRequest(changeBirth, changeName,
            changePassword,
            "수정되버림", null);

        ResultActions resultActions = mockMvc.perform(
            patch(PREFIX)
                .header(AUTHORIZATION, "Bearer " + loginResponse.accessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(patchMemberRequest))
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
        LoginRequest loginRequest = new LoginRequest(MEMBER_EMAIL, PASSWORD);
        LoginResponse loginResponse = memberAuthService.login(loginRequest);

        PostRecipientRequest postRecipientRequest = new PostRecipientRequest(
            "수령인",
            "1234",
            "주소",
            "상세주소",
            "01012341234");

        ResultActions resultActions = mockMvc.perform(
            post(PREFIX + "/recipient")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(postRecipientRequest))
                .header(AUTHORIZATION, "Bearer " + loginResponse.accessToken())
        );

        resultActions.andExpect(status().is2xxSuccessful());

        resultActions.andDo(docs.document(
            requestHeaders(
                headerWithName(AUTHORIZATION).description("access token")
            ),
            requestFields(
                fieldWithPath("name").type(JsonFieldType.STRING).description("수령인 이름"),
                fieldWithPath("zonecode").type(JsonFieldType.STRING).description("우편 주소"),
                fieldWithPath("address").type(JsonFieldType.STRING).description("수령인 주소"),
                fieldWithPath("addressDetail").type(JsonFieldType.STRING).description("수령인 상세주소"),
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
        LoginRequest loginRequest = new LoginRequest(MEMBER_EMAIL, PASSWORD);
        LoginResponse loginResponse = memberAuthService.login(loginRequest);
        Member member = memberRepository.findByEmail(MEMBER_EMAIL).get();

        Recipient recipient1 = Recipient.of(member, "수령인1", "1234", "주소1", "상세 주소", "01012341234",
            RecipientInfoStatus.NORMAL);
        Recipient recipient2 = Recipient.of(member, "수령인2", "1234", "주소2", "상세 주소", "01011112222",
            RecipientInfoStatus.NORMAL);
        Recipient saveRecipient1 = recipientRepository.save(recipient1);
        Recipient saveRecipient2 = recipientRepository.save(recipient2);
        member.getRecipientList().add(saveRecipient1);
        member.getRecipientList().add(saveRecipient2);

        ResultActions resultActions = mockMvc.perform(
            get(PREFIX + "/recipient")
                .header(AUTHORIZATION, "Bearer " + loginResponse.accessToken())
        );

        resultActions.andExpect(status().is2xxSuccessful());

        resultActions.andDo(docs.document(
            requestHeaders(
                headerWithName(AUTHORIZATION).description("access token")
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.list[].recipient_id").type(JsonFieldType.NUMBER)
                                                         .description("수령인 id"),
                fieldWithPath("data.list[].name").type(JsonFieldType.STRING).description("수령인 이름"),
                fieldWithPath("data.list[].zonecode").type(JsonFieldType.STRING)
                                                     .description("우편 주소"),
                fieldWithPath("data.list[].address").type(JsonFieldType.STRING)
                                                    .description("수령인 주소"),
                fieldWithPath("data.list[].address_detail").type(JsonFieldType.STRING)
                                                           .description("수령인 상세주소"),
                fieldWithPath("data.list[].tel").type(JsonFieldType.STRING).description("수령인 연락처"),
                fieldWithPath("data.list[].status").type(JsonFieldType.STRING).description("상태값"),
                fieldWithPath("data.list[].created_at").type(JsonFieldType.STRING)
                                                       .description("생성일"),
                fieldWithPath("data.list[].modified_at").type(JsonFieldType.STRING)
                                                        .description("수정일")
            )
        ));
    }

    @Test
    @DisplayName(PREFIX + "/seller/introduce (POST)")
    void postSellerIntroduce() throws Exception {
        LoginRequest loginRequest = new LoginRequest(SELLER_EMAIL, PASSWORD);
        LoginResponse loginResponse = memberAuthService.login(loginRequest);

        PostIntroduceRequest postIntroduceRequest = new PostIntroduceRequest("제목",
            "https://www.s3.com/teat.html");

        ResultActions resultActions = mockMvc.perform(
            post(PREFIX + "/seller/introduce")
                .header(AUTHORIZATION, "Bearer " + loginResponse.accessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(postIntroduceRequest))
        );

        resultActions.andExpect(status().is2xxSuccessful());

        resultActions.andDo(docs.document(
            requestHeaders(
                headerWithName(AUTHORIZATION).description("access token")
            ),
            requestFields(
                fieldWithPath("subject").type(JsonFieldType.STRING).description("제목"),
                fieldWithPath("url").type(JsonFieldType.STRING).description("url")
            ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                fieldWithPath("data.message").type(JsonFieldType.STRING).description("등록 결과 메세지")
            )
        ));
    }

    @Test
    @DisplayName(PREFIX + "/seller/introduce (GET)")
    void getSellerIntroduce() throws Exception {
        LoginRequest loginRequest = new LoginRequest(SELLER2_EMAIL, PASSWORD);
        LoginResponse loginResponse = memberAuthService.login(loginRequest);

        given(s3Service.getObject(anyString(), anyString())).willReturn(
            "<!doctype html>\n" +
                "<html>\n" +
                "\n" +
                "<head>\n" +
                "\t<title>watermelon</title>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "\t<H2>example 1-2</H2>\n" +
                "\t<HR>\n" +
                "\texample 1-2\n" +
                "</body>\n" +
                "\n" +
                "</html>");

        ResultActions resultActions = mockMvc.perform(
            get(PREFIX + "/seller/introduce")
                .header(AUTHORIZATION, "Bearer " + loginResponse.accessToken())
        );

        resultActions.andExpect(status().is2xxSuccessful());

        resultActions.andDo(docs.document(
            requestHeaders(
                headerWithName(AUTHORIZATION).description("access token")
            )
        ));
    }
}
