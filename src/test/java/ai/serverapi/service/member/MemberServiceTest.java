package ai.serverapi.service.member;

import static ai.serverapi.CommonTest.MEMBER_EMAIL;
import static ai.serverapi.CommonTest.PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ai.serverapi.domain.dto.member.JoinDto;
import ai.serverapi.domain.dto.member.LoginDto;
import ai.serverapi.domain.dto.member.PatchMemberDto;
import ai.serverapi.domain.dto.member.PostBuyerDto;
import ai.serverapi.domain.dto.member.PutBuyerDto;
import ai.serverapi.domain.entity.member.Member;
import ai.serverapi.domain.entity.member.Recipient;
import ai.serverapi.domain.enums.member.RecipientInfoStatus;
import ai.serverapi.domain.vo.MessageVo;
import ai.serverapi.domain.vo.member.BuyerVo;
import ai.serverapi.domain.vo.member.LoginVo;
import ai.serverapi.domain.vo.member.RecipientListVo;
import ai.serverapi.repository.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberAuthService memberAuthService;
    @Autowired
    private MemberService memberService;
    private final MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    @DisplayName("회원 구매자 정보 등록에 성공")
    void postBuyerSuccess1() {
        String email = "buyer@gmail.com";
        String password = "password";
        JoinDto joinDto = new JoinDto(email, password, "구매자", "구매자야", "19991010");
        joinDto.passwordEncoder(passwordEncoder);
        memberRepository.save(Member.of(joinDto));
        LoginDto loginDto = new LoginDto(email, password);
        LoginVo login = memberAuthService.login(loginDto);
        request.addHeader(AUTHORIZATION, "Bearer " + login.getAccessToken());

        PostBuyerDto postBuyerDto = new PostBuyerDto("구매할 사람", "buyer@gmail.com",
            "01012341234");

        MessageVo messageVo = memberService.postBuyer(postBuyerDto, request);

        assertThat(messageVo.getMessage()).contains("구매자 정보 등록 성공");
    }

    @Test
    @DisplayName("회원 구매자 정보 불러오기에 성공")
    void getBuyerSuccess1() {
        String email = "buyer2@gmail.com";
        String password = "password";
        JoinDto joinDto = new JoinDto(email, password, "구매자정보", "구매자 정보 등록",
            "19941010");
        joinDto.passwordEncoder(passwordEncoder);
        Member member = memberRepository.save(Member.of(joinDto));

        LoginDto loginDto = new LoginDto(email, password);
        LoginVo login = memberAuthService.login(loginDto);

        request.addHeader(AUTHORIZATION, "Bearer " + login.getAccessToken());

        PostBuyerDto postBuyerDto = new PostBuyerDto("구매할 사람", "buyer@gmail.com",
            "01012341234");
        memberService.postBuyer(postBuyerDto, request);
        BuyerVo buyer = memberService.getBuyer(request);

        assertThat(buyer.getEmail()).isEqualTo(postBuyerDto.getEmail());
        assertThat(buyer.getTel()).isEqualTo(postBuyerDto.getTel());
        assertThat(buyer.getName()).isEqualTo(postBuyerDto.getName());
    }

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

        request.addHeader(AUTHORIZATION, "Bearer " + login.getAccessToken());

        String changeBirth = "19941030";
        String changeName = "수정함";
        String changePassword = "password2";
        PatchMemberDto patchMemberDto = new PatchMemberDto(changeBirth, changeName,
            changePassword, "수정되버림", null);

        MessageVo messageVo = memberService.patchMember(patchMemberDto, request);

        assertThat(messageVo.getMessage()).contains("회원 정보 수정 성공");
    }

    @Test
    @DisplayName("회원 구매자 정보 수정에 성공")
    void putBuyerSuccess1() {
        String email = "buyer3@gmail.com";
        String password = "password";
        JoinDto joinDto = new JoinDto(email, password, "구매자 정보", "구매자 정보 등록",
            "19991010");
        joinDto.passwordEncoder(passwordEncoder);
        Member member = memberRepository.save(Member.of(joinDto));
        LoginDto loginDto = new LoginDto(email, password);
        LoginVo login = memberAuthService.login(loginDto);
        request.addHeader(AUTHORIZATION, "Bearer " + login.getAccessToken());

        PostBuyerDto postBuyerDto = new PostBuyerDto("구매할 사람", "buyer@gmail.com",
            "01012341234");
        memberService.postBuyer(postBuyerDto, request);
        BuyerVo originBuyer = memberService.getBuyer(request);

        PutBuyerDto putBuyerDto = new PutBuyerDto(originBuyer.getId(), "수정된 사람",
            "buyer@gmail.com", "01011112222");

        MessageVo messageVo = memberService.putBuyer(putBuyerDto);

        assertThat(messageVo.getMessage()).contains("수정 성공");
    }

    @Test
    @DisplayName("수령인 정보 불러오기 성공")
    @Transactional
    void getRecipientList() {
        LoginDto loginDto = new LoginDto(MEMBER_EMAIL.getVal(), PASSWORD.getVal());
        LoginVo login = memberAuthService.login(loginDto);
        Member member = memberRepository.findByEmail(MEMBER_EMAIL.getVal()).get();

        Recipient recipient1 = Recipient.of(member, "수령인1", "주소", "01012341234",
            RecipientInfoStatus.NORMAL);
        Recipient recipient2 = Recipient.of(member, "수령인2", "주소2", "01012341234",
            RecipientInfoStatus.NORMAL);

        member.getRecipientList().add(recipient1);
        member.getRecipientList().add(recipient2);

        request.addHeader(AUTHORIZATION, "Bearer " + login.getAccessToken());

        RecipientListVo recipient = memberService.getRecipient(request);

        assertThat(recipient.getList().get(0).getName()).isEqualTo(recipient2.getName());
    }
}
