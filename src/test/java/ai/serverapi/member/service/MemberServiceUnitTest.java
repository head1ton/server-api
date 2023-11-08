package ai.serverapi.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import ai.serverapi.global.base.MessageVo;
import ai.serverapi.global.s3.S3Service;
import ai.serverapi.global.security.TokenProvider;
import ai.serverapi.member.domain.Introduce;
import ai.serverapi.member.domain.Member;
import ai.serverapi.member.domain.Seller;
import ai.serverapi.member.dto.request.JoinRequest;
import ai.serverapi.member.dto.request.PatchMemberRequest;
import ai.serverapi.member.dto.request.PostIntroduceRequest;
import ai.serverapi.member.dto.request.PostRecipientRequest;
import ai.serverapi.member.dto.request.PostSellerRequest;
import ai.serverapi.member.dto.request.PutSellerRequest;
import ai.serverapi.member.enums.IntroduceStatus;
import ai.serverapi.member.repository.IntroduceRepository;
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
import org.springframework.core.env.Environment;
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
    @Mock
    private IntroduceRepository introduceRepository;
    @Mock
    private Environment env;
    @Mock
    private S3Service s3Service;

    @Test
    @DisplayName("회원이 존재하지 않을경우 회원 수정 실패")
    void patchMemberFail1() {
        // given
        PatchMemberRequest patchMemberRequest = new PatchMemberRequest(null, null, null, null,
            null);

        given(tokenProvider.getMemberId(request)).willReturn(0L);
        // when
        Throwable throwable = catchThrowable(
            () -> memberService.patchMember(patchMemberRequest, request));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 회원");
    }

    @Test
    @DisplayName("회원이 존재하지 않을 경우 수령인 정보 등록에 실패")
    void postRecipientFail1() {
        PostRecipientRequest recipient = new PostRecipientRequest();

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
        PostSellerRequest sellerDto = new PostSellerRequest();

        Throwable throwable = catchThrowable(() -> memberService.postSeller(sellerDto, request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 회원");
    }

    @Test
    @DisplayName("이미 판매자 정보를 등록한 경우 등록에 실패")
    void postSellerFail2() {
        PostSellerRequest sellerDto = new PostSellerRequest("회사명", "010-1234-1234", "1234", "회사 주소",
            "상세 주소",
            "email@gmail.com");
        BDDMockito.given(tokenProvider.getMemberId(request)).willReturn(0L);
        JoinRequest joinRequest = new JoinRequest("join@gmail.com", "password", "name", "nick",
            "19941030");
        Member member = Member.of(joinRequest);
        BDDMockito.given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        BDDMockito.given(sellerRepository.findByMember(any())).willReturn(
            Optional.of(
                Seller.of(member, "회사명", "010-1234-1234", "1234", "회사 주소", "상세 주소",
                    "email@gmail.com")));

        Throwable throwable = catchThrowable(() -> memberService.postSeller(sellerDto, request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("판매자 신청을 완료");
    }

    @Test
    @DisplayName("회원이 존재하지 않을 경우 판매자 정보 수정에 실패")
    void putSellerFail1() {
        PutSellerRequest sellerDto = new PutSellerRequest();

        Throwable throwable = catchThrowable(() -> memberService.putSeller(sellerDto, request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 회원");
    }

    @Test
    @DisplayName("회원이 존재하지 않을 경우 판매자 정보 수정에 실패")
    void putSellerFail2() {
        PutSellerRequest sellerDto = new PutSellerRequest();

        BDDMockito.given(tokenProvider.getMemberId(request)).willReturn(0L);
        JoinRequest joinRequest = new JoinRequest("join@mail.com", "password", "name", "nick",
            "19941030");
        Member member = Member.of(joinRequest);
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
        JoinRequest joinRequest = new JoinRequest("join@gmail.com", "password", "name", "nick",
            "19941030");
        Member member = Member.of(joinRequest);
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        Throwable throwable = catchThrowable(() -> memberService.getSeller(request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 판매자");
    }

    @Test
    @DisplayName("회원이 존재하지 않을 경우 소개 등록에 실패")
    void postIntroduceFail1() {
        PostIntroduceRequest postIntroduceRequest = new PostIntroduceRequest("제목",
            "https://s3.com/html/test1.html");

        Throwable throwable = catchThrowable(
            () -> memberService.postIntroduce(postIntroduceRequest, request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 회원");
    }

    @Test
    @DisplayName("판매자 정보가 존재하지 않을 경우 소개 등록에 실패")
    void postIntroduceFail2() {
        BDDMockito.given(tokenProvider.getMemberId(request)).willReturn(0L);
        JoinRequest joinRequest = new JoinRequest("join@gmail.com", "password", "name", "nick",
            "19941030");
        Member member = Member.of(joinRequest);

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        PostIntroduceRequest postIntroduceRequest = new PostIntroduceRequest("제목",
            "https://s3.com/html/test1.html");

        Throwable throwable = catchThrowable(
            () -> memberService.postIntroduce(postIntroduceRequest, request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("유효하지 않은 판매자");
    }

    @Test
    @DisplayName("판매자 정보가 존재하는 경우 Update로 수정")
    void postIntroduceSuccess() {
        given(tokenProvider.getMemberId(request)).willReturn(0L);

        JoinRequest joinRequest = new JoinRequest("join@gmail.com", "password", "name", "nick",
            "19941030");
        Member member = Member.of(joinRequest);

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        PostIntroduceRequest postIntroduceRequest = new PostIntroduceRequest("제목",
            "https://s3.com/html/test1.html");

        Seller seller = Seller.of(member, "company", "01012341234", "123", "address", "상세 주소",
            "mail@gmail.com");
        given(sellerRepository.findByMember(member)).willReturn(Optional.of(seller));

        Introduce introduce = Introduce.of(seller, "subject", "url", IntroduceStatus.USE);
        given(introduceRepository.findBySeller(any())).willReturn(Optional.of(introduce));

        MessageVo messageVo = memberService.postIntroduce(postIntroduceRequest, request);

        assertThat(messageVo.message()).contains("성공");
    }

    @Test
    @DisplayName("소개 페이지를 등록하지 않았을 때 소개글 불러오기 실패")
    void getIntroduceFail1() {
        given(tokenProvider.getMemberId(request)).willReturn(0L);

        JoinRequest joinRequest = new JoinRequest("join@gmail.com", "password", "name", "nick",
            "19941030");
        Member member = Member.of(joinRequest);

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        given(sellerRepository.findByMember(any())).willReturn(
            Optional.of(Seller.of(member, "", "", "", "", "", "")));

        Throwable throwable = catchThrowable(() -> memberService.getIntroduce(request));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                             .hasMessageContaining("소개 페이지를 먼저 등록");
    }

    @Test
    @DisplayName("소개 페이지 불러오기 성공")
    void getIntroduceSuccess() {
        JoinRequest joinRequest = new JoinRequest("join@gmail.com", "password", "name", "nick",
            "19941030");
        String html = "<html></html>";
        Member member = Member.of(joinRequest);
        Seller seller = Seller.of(member, "", "", "", "", "", "");

        given(tokenProvider.getMemberId(request)).willReturn(0L);
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        given(sellerRepository.findByMember(any())).willReturn(Optional.of(seller));

        given(introduceRepository.findBySeller(any())).willReturn(Optional.of(
            Introduce.of(seller, "subject",
                "https://cherryandplum.s3.ap-northeast-2.amazonaws.com/html/1/20230815/172623_0.html",
                IntroduceStatus.USE)));
        given(env.getProperty(eq("cloud.s3.bucket"))).willReturn("cherryandplum");
        given(s3Service.getObject(anyString(), anyString())).willReturn(html);

        String introduce = memberService.getIntroduce(request);

        assertThat(introduce).isEqualTo(html);
    }

    @Test
    @DisplayName("소개 페이지 정보가 존재하지 않을 경우 실패")
    void getIntroduce2Fail1() {
        assertThatThrownBy(() -> memberService.getIntroduce(1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("유효하지 않은 판매자입니다.");
    }

}
