package ai.serverapi.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import ai.serverapi.config.security.TokenProvider;
import ai.serverapi.domain.member.dto.PatchMemberDto;
import ai.serverapi.domain.member.dto.PostBuyerDto;
import ai.serverapi.domain.member.dto.PostRecipientDto;
import ai.serverapi.domain.member.dto.PutBuyerDto;
import ai.serverapi.domain.member.entity.Buyer;
import ai.serverapi.domain.member.entity.Member;
import ai.serverapi.domain.member.repository.BuyerRepository;
import ai.serverapi.domain.member.repository.MemberRepository;
import ai.serverapi.domain.member.vo.BuyerVo;
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
    private BuyerRepository buyerRepository;
    @Mock
    private TokenProvider tokenProvider;

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
                             .hasMessageContaining("존재하지 않는 회원");
    }

    @Test
    @DisplayName("회원이 존재하지 않을 경우 구매자 정보 입력에 실패")
    void postBuyerFail1() {
        PostBuyerDto postBuyerDto = new PostBuyerDto("홍길동",
            "buyer_info@gmail.com",
            "01012341234");

        BDDMockito.given(tokenProvider.getMemberId(request)).willReturn(0L);

        Throwable throwable = catchThrowable(
            () -> memberService.postBuyer(postBuyerDto, request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("존재하지 않는 회원");
    }

    @Test
    @DisplayName("이미 구매자 정보를 입력했을 경우 입력에 실패")
    void postBuyerFail2() {
        PostBuyerDto postBuyerDto = new PostBuyerDto("홍길동", "buyer_info@gmail.com",
            "01012341234");

        BDDMockito.given(tokenProvider.getMemberId(request)).willReturn(1L);
        Member member = new Member(1L, null, null, null, null, null, null, null, null, null, null);

        member.putBuyer(Buyer.of(null, "구매자", "buyer@gmail.com", "01012341234"));
        BDDMockito.given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        Throwable throwable = catchThrowable(
            () -> memberService.postBuyer(postBuyerDto, request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("이미 구매자 정보를 등록");

    }

    @Test
    @DisplayName("회원이 존재하지 않을 경우 구매자 정보 불러오기에 실패")
    void getBuyerFail1() {
        BDDMockito.given(tokenProvider.getMemberId(request)).willReturn(0L);

        Throwable throwable = catchThrowable(() -> memberService.getBuyer(request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("존재하지 않는 회원");
    }

    @Test
    @DisplayName("구매자 정보 존재하지 않을 경우 빈값으로 불러오기에 성공")
    void getBuyerSuccess1() {
        BDDMockito.given(tokenProvider.getMemberId(request)).willReturn(0L);
        BDDMockito.given(memberRepository.findById(anyLong())).willReturn(Optional.of(
            new Member(1L, null, null, null, null, null, null, null, null, null, null)));

        BuyerVo buyerInfo = memberService.getBuyer(request);

        assertThat(buyerInfo.getName()).isEqualTo("");
        assertThat(buyerInfo.getEmail()).isEqualTo("");
        assertThat(buyerInfo.getTel()).isEqualTo("");
    }

    @Test
    @DisplayName("구매자 정보가 존재하지 않을 경우 수정에 실패")
    void putBuyerFail1() {
        PutBuyerDto putBuyerDto = new PutBuyerDto();

        Throwable throwable = catchThrowable(
            () -> memberService.putBuyer(putBuyerDto));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 구매자 정보");
    }

    @Test
    @DisplayName("회원이 존재하지 않을 경우 수령인 정보 등록에 실패")
    void postRecipientFail1() {
        PostRecipientDto recipient = new PostRecipientDto();

        Throwable throwable = catchThrowable(
            () -> memberService.postRecipient(recipient, request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("존재하지 않는 회원");
    }

    @Test
    @DisplayName("회원이 존재하지 않을 경우 수령인 정보 불러오기에 실패")
    void getRecipientFail1() {
        Throwable throwable = catchThrowable(() -> memberService.getRecipient(request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("존재하지 않는 회원");

    }

}