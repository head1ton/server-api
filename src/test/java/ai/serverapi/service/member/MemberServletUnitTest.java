package ai.serverapi.service.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.anyLong;

import ai.serverapi.common.security.TokenProvider;
import ai.serverapi.domain.dto.member.PatchMemberDto;
import ai.serverapi.domain.entity.member.Member;
import ai.serverapi.repository.member.MemberRepository;
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
class MemberServletUnitTest {

    @InjectMocks
    private MemberService memberService;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private MemberRepository memberRepository;
    private final MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    @DisplayName("회원이 존재하지 않을 경우 회원 수정 실패")
    void patchMemberFail1() {
        PatchMemberDto patchMemberDto = new PatchMemberDto(0L, null, null, null, null, null);

        BDDMockito.given(tokenProvider.getMemberId(request)).willReturn(0L);

        Throwable throwable = catchThrowable(
            () -> memberService.patchMember(patchMemberDto, request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("존재하지 않는 회원");
    }

    @Test
    @DisplayName("회원이 정보가 다를 경우 회원 수정 실패")
    void patchMemberFail2() {
        PatchMemberDto patchMemberDto = new PatchMemberDto(0L, null, null, null, null, null);

        BDDMockito.given(tokenProvider.getMemberId(request)).willReturn(0L);
        BDDMockito.given(memberRepository.findById(anyLong()))
                  .willReturn(Optional.of(
                      new Member(1L, "mail@mail.com", "", "", "", "", null, null, null, null,
                          null)));

        Throwable throwable = catchThrowable(
            () -> memberService.patchMember(patchMemberDto, request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("다른 회원의 정보를 수정할 수 없습니다.");
    }
}
