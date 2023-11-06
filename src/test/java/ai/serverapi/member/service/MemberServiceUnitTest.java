package ai.serverapi.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import ai.serverapi.config.security.TokenProvider;
import ai.serverapi.member.domain.dto.JoinDto;
import ai.serverapi.member.domain.dto.PatchMemberDto;
import ai.serverapi.member.domain.dto.PostIntroduceDto;
import ai.serverapi.member.domain.dto.PostRecipientDto;
import ai.serverapi.member.domain.dto.PostSellerDto;
import ai.serverapi.member.domain.dto.PutSellerDto;
import ai.serverapi.member.domain.entity.Member;
import ai.serverapi.member.domain.entity.Seller;
import ai.serverapi.member.repository.MemberRepository;
import ai.serverapi.member.repository.SellerRepository;
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

    @Test
    @DisplayName("회원이 존재하지 않을 경우 판매자 정보 수정에 실패")
    void putSellerFail1() {
        PutSellerDto sellerDto = new PutSellerDto();

        Throwable throwable = catchThrowable(() -> memberService.putSeller(sellerDto, request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 회원");
    }

    @Test
    @DisplayName("회원이 존재하지 않을 경우 판매자 정보 수정에 실패")
    void putSellerFail2() {
        PutSellerDto sellerDto = new PutSellerDto();

        BDDMockito.given(tokenProvider.getMemberId(request)).willReturn(0L);
        JoinDto joinDto = new JoinDto("join@mail.com", "password", "name", "nick", "19941030");
        Member member = Member.of(joinDto);
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        Throwable throwable = catchThrowable(() -> memberService.putSeller(sellerDto, request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 판매자");
    }

    @Test
    @DisplayName("회원이 존재하지 않을 경우 판매자 정보 조회에 실패")
    void getSellerFail1() {
        Throwable throwable = catchThrowable(() -> memberService.getSeller(request));
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 회원");
    }

    @Test
    @DisplayName("회원이 존재하지 않을 경우 판매자 정보 조회에 실패")
    void getSellerFail2() {
        given(tokenProvider.getMemberId(request)).willReturn(0L);
        JoinDto joinDto = new JoinDto("join@gmail.com", "password", "name", "nick", "19941030");
        Member member = Member.of(joinDto);
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        Throwable throwable = catchThrowable(() -> memberService.getSeller(request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 판매자");
    }

    @Test
    @DisplayName("회원이 존재하지 않을 경우 소개 등록에 실패")
    void postIntroduceFail1() {
        PostIntroduceDto postIntroduceDto = new PostIntroduceDto("https://s3.com/html/test1.html");

        Throwable throwable = catchThrowable(
            () -> memberService.postIntroduce(postIntroduceDto, request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 회원");
    }

    @Test
    @DisplayName("판매자 정보가 존재하지 않을 경우 소개 등록에 실패")
    void postIntroduceFail2() {
        BDDMockito.given(tokenProvider.getMemberId(request)).willReturn(0L);
        JoinDto joinDto = new JoinDto("join@gmail.com", "password", "name", "nick", "19941030");
        Member member = Member.of(joinDto);

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        PostIntroduceDto postIntroduceDto = new PostIntroduceDto("https://s3.com/html/test1.html");

        Throwable throwable = catchThrowable(
            () -> memberService.postIntroduce(postIntroduceDto, request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 판매자");
    }

}
