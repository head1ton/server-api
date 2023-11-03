package ai.serverapi.service.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import ai.serverapi.common.security.TokenProvider;
import ai.serverapi.domain.dto.member.PatchMemberDto;
import ai.serverapi.domain.dto.member.PostBuyerInfoDto;
import ai.serverapi.domain.dto.member.PutBuyerInfoDto;
import ai.serverapi.domain.entity.member.BuyerInfo;
import ai.serverapi.domain.entity.member.Member;
import ai.serverapi.domain.vo.member.BuyerInfoVo;
import ai.serverapi.repository.member.BuyerInfoRepository;
import ai.serverapi.repository.member.MemberRepository;
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
    private BuyerInfoRepository buyerInfoRepository;
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
    void postBuyerInfoFail1() {
        PostBuyerInfoDto postBuyerInfoDto = new PostBuyerInfoDto("홍길동",
            "buyer_info@gmail.com",
            "01012341234");

        BDDMockito.given(tokenProvider.getMemberId(request)).willReturn(0L);

        Throwable throwable = catchThrowable(
            () -> memberService.postBuyerInfo(postBuyerInfoDto, request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("존재하지 않는 회원");
    }

    @Test
    @DisplayName("이미 구매자 정보를 입력했을 경우 입력에 실패")
    void postBuyerInfoFail2() {
        PostBuyerInfoDto postBuyerInfoDto = new PostBuyerInfoDto("홍길동", "buyer_info@gmail.com",
            "01012341234");

        BDDMockito.given(tokenProvider.getMemberId(request)).willReturn(1L);
        Member member = new Member(1L, null, null, null, null, null, null, null, null, null, null);

        member.putBuyerInfo(BuyerInfo.of(null, "구매자", "buyer@gmail.com", "01012341234"));
        BDDMockito.given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        Throwable throwable = catchThrowable(
            () -> memberService.postBuyerInfo(postBuyerInfoDto, request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("이미 구매자 정보를 등록");

    }

    @Test
    @DisplayName("회원이 존재하지 않을 경우 구매자 정보 불러오기에 실패")
    void getBuyerInfoFail1() {
        BDDMockito.given(tokenProvider.getMemberId(request)).willReturn(0L);

        Throwable throwable = catchThrowable(() -> memberService.getBuyerInfo(request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("존재하지 않는 회원");
    }

    @Test
    @DisplayName("구매자 정보 존재하지 않을 경우 빈값으로 불러오기에 성공")
    void getBuyerInfoSuccess1() {
        BDDMockito.given(tokenProvider.getMemberId(request)).willReturn(0L);
        BDDMockito.given(memberRepository.findById(anyLong())).willReturn(Optional.of(
            new Member(1L, null, null, null, null, null, null, null, null, null, null)));

        BuyerInfoVo buyerInfo = memberService.getBuyerInfo(request);

        assertThat(buyerInfo.getName()).isEqualTo("");
        assertThat(buyerInfo.getEmail()).isEqualTo("");
        assertThat(buyerInfo.getTel()).isEqualTo("");
    }

    @Test
    @DisplayName("구매자 정보가 존재하지 않을 경우 수정에 실패")
    void putBuyerInfoFail1() {
        PutBuyerInfoDto putBuyerInfoDto = new PutBuyerInfoDto();

        Throwable throwable = catchThrowable(
            () -> memberService.putBuyerInfo(putBuyerInfoDto));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 구매자 정보");
    }

}
