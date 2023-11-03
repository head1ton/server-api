package ai.serverapi.service.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.BDDMockito.given;

import ai.serverapi.common.security.TokenProvider;
import ai.serverapi.domain.dto.member.PatchMemberDto;
import ai.serverapi.domain.dto.member.PostBuyerInfoDto;
import ai.serverapi.repository.member.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
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

}
