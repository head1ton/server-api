package ai.serverapi.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ai.serverapi.BaseTest;
import ai.serverapi.global.base.MessageVo;
import ai.serverapi.member.domain.Member;
import ai.serverapi.member.domain.Recipient;
import ai.serverapi.member.domain.Seller;
import ai.serverapi.member.dto.request.JoinRequest;
import ai.serverapi.member.dto.request.LoginRequest;
import ai.serverapi.member.dto.request.PatchMemberRequest;
import ai.serverapi.member.dto.request.PutSellerRequest;
import ai.serverapi.member.dto.response.LoginResponse;
import ai.serverapi.member.dto.response.RecipientListResponse;
import ai.serverapi.member.enums.RecipientInfoStatus;
import ai.serverapi.member.repository.MemberRepository;
import ai.serverapi.member.repository.SellerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
class MemberServiceTest extends BaseTest {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberAuthService memberAuthService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private SellerRepository sellerRepository;
    private final MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    @DisplayName("회원 정보 수정에 성공")
    void patchMemberSuccess1() {
        // 멤버 생성
        String email = "patch@gmail.com";
        String password = "password";
        JoinRequest joinRequest = new JoinRequest(email, password, "수정자", "수정할꺼야", "19941030");
        joinRequest.passwordEncoder(passwordEncoder);

        memberRepository.save(Member.of(joinRequest));
        // 멤버 로그인
        LoginRequest loginRequest = new LoginRequest(email, password);
        LoginResponse login = memberAuthService.login(loginRequest);

        request.removeHeader(AUTHORIZATION);
        request.addHeader(AUTHORIZATION, "Bearer " + login.accessToken());

        String changeBirth = "19941030";
        String changeName = "수정함";
        String changePassword = "password2";
        PatchMemberRequest patchMemberRequest = new PatchMemberRequest(changeBirth, changeName,
            changePassword, "수정되버림", null);

        MessageVo messageVo = memberService.patchMember(patchMemberRequest, request);

        assertThat(messageVo.message()).contains("회원 정보 수정 성공");
    }

    @Test
    @DisplayName("수령인 정보 불러오기 성공")
    void getRecipientList() throws Exception {
        LoginRequest loginRequest = new LoginRequest(MEMBER_EMAIL, PASSWORD);
        LoginResponse login = memberAuthService.login(loginRequest);
        Member member = memberRepository.findByEmail(MEMBER_EMAIL).get();

        Recipient recipient1 = Recipient.of(member, "수령인1", "1234", "주소", "상세주소", "01012341234",
            RecipientInfoStatus.NORMAL);
        Thread.sleep(10L);
        Recipient recipient2 = Recipient.of(member, "수령인2", "1234", "주소2", "상세주소", "01012341234",
            RecipientInfoStatus.NORMAL);

        member.getRecipientList().add(recipient1);
        member.getRecipientList().add(recipient2);

        request.removeHeader(AUTHORIZATION);
        request.addHeader(AUTHORIZATION, "Bearer " + login.accessToken());

        RecipientListResponse recipient = memberService.getRecipient(request);

        assertThat(recipient.list().get(0).getName()).isEqualTo(recipient2.getName());
    }

    @Test
    @DisplayName("판매자 정보 수정 성공")
    void putSeller() {
        LoginRequest loginRequest = new LoginRequest(SELLER_EMAIL, PASSWORD);
        LoginResponse login = memberAuthService.login(loginRequest);

        request.removeHeader(AUTHORIZATION);
        request.addHeader(AUTHORIZATION, "Bearer " + login.accessToken());

        String changeCompany = "변경 회사명";
        PutSellerRequest putSellerRequest = new PutSellerRequest(changeCompany, "01012341234",
            "1234", "변경된 주소",
            "mail@gmail.com");

        MessageVo messageVo = memberService.putSeller(putSellerRequest, request);

        assertThat(messageVo.message()).contains("수정 성공");
        Member member = memberRepository.findByEmail(SELLER_EMAIL).get();
        Seller seller = sellerRepository.findByMember(member).get();
        assertThat(seller.getCompany()).isEqualTo(changeCompany);
    }
}
