package ai.serverapi.service.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ai.serverapi.domain.dto.member.JoinDto;
import ai.serverapi.domain.dto.member.LoginDto;
import ai.serverapi.domain.dto.member.PatchMemberDto;
import ai.serverapi.domain.dto.member.PostBuyerInfoDto;
import ai.serverapi.domain.entity.member.Member;
import ai.serverapi.domain.vo.MessageVo;
import ai.serverapi.domain.vo.member.LoginVo;
import ai.serverapi.repository.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
    @DisplayName("회원 구매자 정보 등록에 성공")
    void postBuyerInfoSuccess1() {
        String email = "buyer-info@gmail.com";
        String password = "password";
        JoinDto joinDto = new JoinDto(email, password, "구매자", "구매자야", "19991010");
        joinDto.passwordEncoder(passwordEncoder);
        memberRepository.save(Member.of(joinDto));
        LoginDto loginDto = new LoginDto(email, password);
        LoginVo login = memberAuthService.login(loginDto);
        request.addHeader(AUTHORIZATION, "Bearer " + login.getAccessToken());

        PostBuyerInfoDto postBuyerInfoDto = new PostBuyerInfoDto(null, "구매할 사람", "buyer@gmail.com",
            "01012341234");

        MessageVo messageVo = memberService.postBuyerInfo(postBuyerInfoDto, request);

        assertThat(messageVo.getMessage()).contains("구매자 정보 등록 성공");
    }
}
