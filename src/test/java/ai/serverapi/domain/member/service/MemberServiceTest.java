package ai.serverapi.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ai.serverapi.BaseTest;
import ai.serverapi.config.base.MessageVo;
import ai.serverapi.domain.member.dto.JoinDto;
import ai.serverapi.domain.member.dto.LoginDto;
import ai.serverapi.domain.member.dto.PatchMemberDto;
import ai.serverapi.domain.member.dto.PutSellerDto;
import ai.serverapi.domain.member.entity.Member;
import ai.serverapi.domain.member.entity.Recipient;
import ai.serverapi.domain.member.entity.Seller;
import ai.serverapi.domain.member.enums.RecipientInfoStatus;
import ai.serverapi.domain.member.repository.MemberRepository;
import ai.serverapi.domain.member.repository.SellerRepository;
import ai.serverapi.domain.member.vo.LoginVo;
import ai.serverapi.domain.member.vo.RecipientListVo;
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
        JoinDto joinDto = new JoinDto(email, password, "수정자", "수정할꺼야", "19941030");
        joinDto.passwordEncoder(passwordEncoder);

        memberRepository.save(Member.of(joinDto));
        // 멤버 로그인
        LoginDto loginDto = new LoginDto(email, password);
        LoginVo login = memberAuthService.login(loginDto);

        request.removeHeader(AUTHORIZATION);
        request.addHeader(AUTHORIZATION, "Bearer " + login.accessToken());

        String changeBirth = "19941030";
        String changeName = "수정함";
        String changePassword = "password2";
        PatchMemberDto patchMemberDto = new PatchMemberDto(changeBirth, changeName,
            changePassword, "수정되버림", null);

        MessageVo messageVo = memberService.patchMember(patchMemberDto, request);

        assertThat(messageVo.message()).contains("회원 정보 수정 성공");
    }

    @Test
    @DisplayName("수령인 정보 불러오기 성공")
    void getRecipientList() {
        LoginDto loginDto = new LoginDto(MEMBER_EMAIL, PASSWORD);
        LoginVo login = memberAuthService.login(loginDto);
        Member member = memberRepository.findByEmail(MEMBER_EMAIL).get();

        Recipient recipient1 = Recipient.of(member, "수령인1", "주소", "01012341234",
            RecipientInfoStatus.NORMAL);
        Recipient recipient2 = Recipient.of(member, "수령인2", "주소2", "01012341234",
            RecipientInfoStatus.NORMAL);

        member.getRecipientList().add(recipient1);
        member.getRecipientList().add(recipient2);

        request.removeHeader(AUTHORIZATION);
        request.addHeader(AUTHORIZATION, "Bearer " + login.accessToken());

        RecipientListVo recipient = memberService.getRecipient(request);

        assertThat(recipient.list().get(0).name()).isEqualTo(recipient2.getName());
    }

    @Test
    @DisplayName("판매자 정보 수정 성공")
    void putSeller() {
        LoginDto loginDto = new LoginDto(SELLER_EMAIL, PASSWORD);
        LoginVo login = memberAuthService.login(loginDto);

        request.removeHeader(AUTHORIZATION);
        request.addHeader(AUTHORIZATION, "Bearer " + login.accessToken());

        String changeCompany = "변경 회사명";
        PutSellerDto putSellerDto = new PutSellerDto(changeCompany, "01012341234", "변경된 주소",
            "mail@gmail.com");

        MessageVo messageVo = memberService.putSeller(putSellerDto, request);

        assertThat(messageVo.message()).contains("수정 성공");
        Member member = memberRepository.findByEmail(SELLER_EMAIL).get();
        Seller seller = sellerRepository.findByMember(member).get();
        assertThat(seller.getCompany()).isEqualTo(changeCompany);
    }
}
