package ai.serverapi.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import ai.serverapi.config.security.TokenProvider;
import ai.serverapi.domain.member.dto.JoinDto;
import ai.serverapi.domain.member.dto.PatchMemberDto;
import ai.serverapi.domain.member.dto.PostRecipientDto;
import ai.serverapi.domain.member.dto.PostSellerDto;
import ai.serverapi.domain.member.entity.Member;
import ai.serverapi.domain.member.entity.Seller;
import ai.serverapi.domain.member.repository.MemberRepository;
import ai.serverapi.domain.member.repository.SellerRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

@ExtendWith({MockitoExtension.class})
class MemberServiceUnitTest {
    @InjectMocks
    private MemberService memberService;
    HttpServletRequest request = new MockHttpServletRequest();
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private SellerRepository sellerRepository;

    @Test
    @DisplayName("회원이 존재하지 않을경우 회원 수정 실패")
    void patchMemberFail1() {
        // given
        PatchMemberDto patchMemberDto = new PatchMemberDto(null, null, null, null, null);

        given(tokenProvider.getMemberId(request)).willReturn(0L);
        // when
        Throwable throwable = catchThrowable(
            () -> memberService.patchMember(patchMemberDto, request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 회원");
    }

    @Test
    @DisplayName("회원이 존재하지 않을 경우 수령인 정보 등록에 실패")
    void postRecipientFail1() {
        PostRecipientDto recipient = new PostRecipientDto();

        Throwable throwable = catchThrowable(
            () -> memberService.postRecipient(recipient, request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 회원");
    }

    @Test
    @DisplayName("회원이 존재하지 않을 경우 수령인 정보 불러오기에 실패")
    void getRecipientFail1() {
        Throwable throwable = catchThrowable(() -> memberService.getRecipient(request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 회원");

    }

    @Test
    @DisplayName("회원이 존재하지 않을 경우 판매자 정보 등록에 실패")
    void postSellerFail1() {
        PostSellerDto sellerDto = new PostSellerDto();

        Throwable throwable = catchThrowable(() -> memberService.postSeller(sellerDto, request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 회원");
    }

    @Test
    @DisplayName("이미 판매자 정보를 등록한 경우 등록에 실패")
    void postSellerFail2() {
        PostSellerDto sellerDto = new PostSellerDto("회사명", "010-1234-1234", "회사 주소",
            "email@gmail.com");
        BDDMockito.given(tokenProvider.getMemberId(request)).willReturn(0L);
        JoinDto joinDto = new JoinDto("join@gmail.com", "password", "name", "nick", "19941030");
        Member member = Member.of(joinDto);
        BDDMockito.given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        BDDMockito.given(sellerRepository.findByMember(any())).willReturn(
            Optional.of(Seller.of(member, "회사명", "010-1234-1234", "회사 주소", "email@gmail.com")));

        Throwable throwable = catchThrowable(() -> memberService.postSeller(sellerDto, request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("판매자 신청을 완료");
    }

}
