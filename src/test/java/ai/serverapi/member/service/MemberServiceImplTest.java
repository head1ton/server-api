package ai.serverapi.member.service;

import static ai.serverapi.Base.MEMBER_EMAIL;
import static ai.serverapi.Base.MEMBER_LOGIN;
import static ai.serverapi.Base.SELLER_EMAIL;
import static ai.serverapi.Base.SELLER_LOGIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ai.serverapi.global.base.MessageVo;
import ai.serverapi.member.controller.request.JoinRequest;
import ai.serverapi.member.controller.request.LoginRequest;
import ai.serverapi.member.controller.request.PatchMemberRequest;
import ai.serverapi.member.controller.request.PutSellerRequest;
import ai.serverapi.member.controller.response.LoginResponse;
import ai.serverapi.member.controller.response.RecipientListResponse;
import ai.serverapi.member.domain.entity.MemberEntity;
import ai.serverapi.member.domain.entity.RecipientEntity;
import ai.serverapi.member.enums.RecipientInfoStatus;
import ai.serverapi.member.repository.MemberJpaRepository;
import ai.serverapi.member.repository.SellerJpaRepository;
import ai.serverapi.product.domain.entity.SellerEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@SqlGroup({
    @Sql(scripts = {"/sql/init.sql",
        "/sql/introduce.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
})
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
@Transactional(readOnly = true)
@Execution(ExecutionMode.CONCURRENT)
class MemberServiceImplTest {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private MemberJpaRepository memberJpaRepository;
    @Autowired
    private MemberAuthServiceImpl memberAuthService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private SellerJpaRepository sellerJpaRepository;
    MockHttpServletRequest request = new MockHttpServletRequest();

    @AfterEach
    void cleanUp() {
        memberJpaRepository.deleteAll();
        sellerJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("회원 정보 수정에 성공")
    void patchMemberSuccess1() {
        // 멤버 생성
        String email = "patch@gmail.com";
        String password = "password";
        String changePassword = "password2";
        String changeName = "수정함";
        String changeBirth = "19941030";

        JoinRequest joinRequest = JoinRequest.builder()
                                             .email(email)
                                             .password(password)
                                             .name("수정자")
                                             .nickname("수정할꺼야")
                                             .birth("19941030")
                                             .build();
        joinRequest.passwordEncoder(passwordEncoder);

        memberJpaRepository.save(MemberEntity.of(joinRequest));
        // 멤버 로그인
        LoginRequest loginRequest = LoginRequest.builder().email(email).password(password).build();
        LoginResponse login = memberAuthService.login(loginRequest);

        request.removeHeader(AUTHORIZATION);
        request.addHeader(AUTHORIZATION, "Bearer " + login.getAccessToken());

        PatchMemberRequest patchMemberRequest = PatchMemberRequest.builder()
                                                                  .birth(changeBirth)
                                                                  .name(changeName)
                                                                  .password(changePassword)
                                                                  .nickname("수정되버림")
                                                                  .build();

        MessageVo messageVo = memberService.patchMember(patchMemberRequest, request);

        assertThat(messageVo.message()).contains("회원 정보 수정 성공");
    }

    @Test
    @DisplayName("수령인 정보 불러오기 성공")
    void getRecipientList() throws Exception {

        MemberEntity memberEntity = memberJpaRepository.findByEmail(MEMBER_EMAIL).get();

        RecipientEntity recipientEntity1 = RecipientEntity.of(memberEntity, "수령인1", "1234", "주소",
            "상세주소", "01012341234",
            RecipientInfoStatus.NORMAL);
        Thread.sleep(10L);
        RecipientEntity recipientEntity2 = RecipientEntity.of(memberEntity, "수령인2", "1234", "주소2",
            "상세주소", "01012341234",
            RecipientInfoStatus.NORMAL);

        memberEntity.getRecipientList().add(recipientEntity1);
        memberEntity.getRecipientList().add(recipientEntity2);

        request.removeHeader(AUTHORIZATION);
        request.addHeader(AUTHORIZATION, "Bearer " + MEMBER_LOGIN.getAccessToken());

        RecipientListResponse recipient = memberService.getRecipient(request);

        assertThat(recipient.getList().get(0).getName()).isEqualTo(recipientEntity2.getName());
    }

    @Test
    @DisplayName("판매자 정보 수정 성공")
    void putSeller() {

        request.removeHeader(AUTHORIZATION);
        request.addHeader(AUTHORIZATION, "Bearer " + SELLER_LOGIN.getAccessToken());

        String changeCompany = "변경 회사명";
        PutSellerRequest putSellerRequest = PutSellerRequest.builder()
                                                            .company(changeCompany)
                                                            .tel("01012341234")
                                                            .zonecode("1234")
                                                            .address("변경된 주소")
                                                            .addressDetail("상세 주소")
                                                            .email("mail@gmail.com")
                                                            .build();

        MessageVo messageVo = memberService.putSeller(putSellerRequest, request);

        assertThat(messageVo.message()).contains("수정 성공");
        MemberEntity memberEntity = memberJpaRepository.findByEmail(SELLER_EMAIL).get();
        SellerEntity sellerEntity = sellerJpaRepository.findByMember(memberEntity).get();
        assertThat(sellerEntity.getCompany()).isEqualTo(changeCompany);
    }
}
