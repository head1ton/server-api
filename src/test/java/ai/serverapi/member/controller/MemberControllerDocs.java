package ai.serverapi.member.controller;

import static ai.serverapi.Base.MEMBER_EMAIL;
import static ai.serverapi.Base.MEMBER_LOGIN;
import static ai.serverapi.Base.SELLER2_LOGIN;
import static ai.serverapi.Base.SELLER_LOGIN;
import static ai.serverapi.Base.objectMapper;
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

import ai.serverapi.RestdocsBaseTest;
import ai.serverapi.global.base.ResultCode;
import ai.serverapi.global.s3.S3Service;
import ai.serverapi.member.controller.request.JoinRequest;
import ai.serverapi.member.controller.request.LoginRequest;
import ai.serverapi.member.controller.request.PatchMemberRequest;
import ai.serverapi.member.controller.request.PostIntroduceRequest;
import ai.serverapi.member.controller.request.PostRecipientRequest;
import ai.serverapi.member.controller.request.PostSellerRequest;
import ai.serverapi.member.controller.request.PutSellerRequest;
import ai.serverapi.member.controller.response.LoginResponse;
import ai.serverapi.member.domain.entity.MemberEntity;
import ai.serverapi.member.domain.entity.RecipientEntity;
import ai.serverapi.member.enums.MemberRole;
import ai.serverapi.member.enums.RecipientInfoStatus;
import ai.serverapi.member.repository.IntroduceJpaRepository;
import ai.serverapi.member.repository.MemberJpaRepository;
import ai.serverapi.member.repository.RecipientJpaRepository;
import ai.serverapi.member.repository.SellerJpaRepository;
import ai.serverapi.member.service.MemberAuthServiceImpl;
import ai.serverapi.member.service.MemberServiceImpl;
import ai.serverapi.product.repository.CategoryJpaRepository;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@SqlGroup({
    @Sql(scripts = {"/sql/init.sql",
        "/sql/introduce.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
})
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
@Execution(ExecutionMode.CONCURRENT)
class MemberControllerDocs extends RestdocsBaseTest {

    @Autowired
    private MemberJpaRepository memberJpaRepository;
    @Autowired
    private MemberServiceImpl memberService;
    @Autowired
    private MemberAuthServiceImpl memberAuthService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private RecipientJpaRepository recipientJpaRepository;
    @Autowired
    private CategoryJpaRepository categoryJpaRepository;
    @Autowired
    private IntroduceJpaRepository introduceJpaRepository;
    @Autowired
    private SellerJpaRepository sellerJpaRepository;
    @MockBean
    private S3Service s3Service;
    private final static String PREFIX = "/api/member";

    @AfterEach
    void cleanUp() {
        categoryJpaRepository.deleteAll();
        recipientJpaRepository.deleteAll();
        introduceJpaRepository.deleteAll();
        sellerJpaRepository.deleteAll();
        memberJpaRepository.deleteAll();
    }

    @Test
    @DisplayName(PREFIX + " (GET)")
    void member() throws Exception {

        ResultActions resultActions = mock.perform(
            get(PREFIX)
                .header(AUTHORIZATION, "Bearer " + MEMBER_LOGIN.getAccessToken())
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
                                              MemberRole.MEMBER, MemberRole.SELLER)),
                fieldWithPath("data.status").type(JsonFieldType.STRING).description("상태값"),
                fieldWithPath("data.created_at").type(JsonFieldType.STRING).description("생성일"),
                fieldWithPath("data.modified_at").type(JsonFieldType.STRING).description("수정일")
            )
        ));
    }

    @Test
    @DisplayName(PREFIX + "/seller (POST)")
    void postSeller() throws Exception {
        String email = "earth@gmail.com";
        String password = "password";

        JoinRequest joinRequest = JoinRequest.builder()
                                             .email(email)
                                             .password(password)
                                             .name("name")
                                             .nickname("nick")
                                             .birth("19941030")
                                             .build();
        joinRequest.passwordEncoder(passwordEncoder);
        memberJpaRepository.save(MemberEntity.of(joinRequest));
        LoginRequest loginRequest = LoginRequest.builder()
                                                .email(email)
                                                .password(password)
                                                .build();
        LoginResponse loginResponse = memberAuthService.login(loginRequest);

        PostSellerRequest postSellerRequest = PostSellerRequest.builder()
                                                               .company("판매자 이름")
                                                               .tel("010-1234-1234")
                                                               .zonecode("1234")
                                                               .address("제주도 서귀포시 서귀포면 한라산길")
                                                               .addressDetail("상세 주소")
                                                               .email("mail@gmail.com")
                                                               .build();

        ResultActions resultActions = mock.perform(
            post(PREFIX + "/seller")
                .header(AUTHORIZATION, "Bearer " + loginResponse.getAccessToken())
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

        ResultActions resultActions = mock.perform(
            get(PREFIX + "/seller")
                .header(AUTHORIZATION, "Bearer " + SELLER_LOGIN.getAccessToken())
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

        PutSellerRequest putSellerRequest = PutSellerRequest.builder()
                                                            .company("변경된 판매자 이름")
                                                            .tel("010-1234-1234")
                                                            .zonecode("1234")
                                                            .address("강원도 철원군 철원면 백두산길 128")
                                                            .addressDetail("상세 주소")
                                                            .email("mail@gmail.com")
                                                            .build();

        ResultActions resultActions = mock.perform(
            put(PREFIX + "/seller")
                .header(AUTHORIZATION, "Bearer " + SELLER_LOGIN.getAccessToken())
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
        String email = "patch1@gmail.com";
        String password = "password";
        String changePassword = "password2";
        String changeName = "수정함";
        String changeBirth = "19941030";

        JoinRequest joinRequest = JoinRequest.builder()
                                             .email(email)
                                             .password(password)
                                             .name("수정자")
                                             .nickname("수정할거야")
                                             .birth("19991010")
                                             .build();

        joinRequest.passwordEncoder(passwordEncoder);
        memberJpaRepository.save(MemberEntity.of(joinRequest));
        LoginRequest loginRequest = new LoginRequest(email, password);
        LoginResponse loginResponse = memberAuthService.login(loginRequest);

        PatchMemberRequest patchMemberRequest = PatchMemberRequest.builder()
                                                                  .nickname("수정되어버림2")
                                                                  .password(changePassword)
                                                                  .birth(changeBirth)
                                                                  .name(changeName)
                                                                  .build();

        ResultActions resultActions = mock.perform(
            patch(PREFIX)
                .header(AUTHORIZATION, "Bearer " + loginResponse.getAccessToken())
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

        PostRecipientRequest postRecipientRequest = PostRecipientRequest.builder()
                                                                        .name("수령인")
                                                                        .zonecode("1234")
                                                                        .address("주소")
                                                                        .addressDetail("상세주소")
                                                                        .tel("01012341234")
                                                                        .build();

        ResultActions resultActions = mock.perform(
            post(PREFIX + "/recipient")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(postRecipientRequest))
                .header(AUTHORIZATION, "Bearer " + MEMBER_LOGIN.getAccessToken())
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

        MemberEntity memberEntity = memberJpaRepository.findByEmail(MEMBER_EMAIL).get();

        RecipientEntity recipientEntity1 = RecipientEntity.of(memberEntity, "수령인1", "1234", "주소1",
            "상세 주소", "01012341234",
            RecipientInfoStatus.NORMAL);
        RecipientEntity recipientEntity2 = RecipientEntity.of(memberEntity, "수령인2", "1234", "주소2",
            "상세 주소", "01011112222",
            RecipientInfoStatus.NORMAL);
        RecipientEntity saveRecipient1Entity = recipientJpaRepository.save(recipientEntity1);
        RecipientEntity saveRecipient2Entity = recipientJpaRepository.save(recipientEntity2);
        memberEntity.getRecipientList().add(saveRecipient1Entity);
        memberEntity.getRecipientList().add(saveRecipient2Entity);

        ResultActions resultActions = mock.perform(
            get(PREFIX + "/recipient")
                .header(AUTHORIZATION, "Bearer " + MEMBER_LOGIN.getAccessToken())
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

        PostIntroduceRequest postIntroduceRequest = PostIntroduceRequest.builder()
                                                                        .subject("제목")
                                                                        .url(
                                                                            "https://www.s3.com/teat.html")
                                                                        .build();

        ResultActions resultActions = mock.perform(
            post(PREFIX + "/seller/introduce")
                .header(AUTHORIZATION, "Bearer " + SELLER_LOGIN.getAccessToken())
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

        ResultActions resultActions = mock.perform(
            get(PREFIX + "/seller/introduce")
                .header(AUTHORIZATION, "Bearer " + SELLER2_LOGIN.getAccessToken())
        );

        resultActions.andExpect(status().is2xxSuccessful());

        resultActions.andDo(docs.document(
            requestHeaders(
                headerWithName(AUTHORIZATION).description("access token")
            )
        ));
    }
}
