package ai.serverapi.member.service;

import ai.serverapi.global.base.MessageVo;
import ai.serverapi.global.s3.S3Service;
import ai.serverapi.global.security.TokenProvider;
import ai.serverapi.member.domain.Introduce;
import ai.serverapi.member.domain.Member;
import ai.serverapi.member.domain.MemberApplySeller;
import ai.serverapi.member.domain.Recipient;
import ai.serverapi.member.domain.Seller;
import ai.serverapi.member.dto.request.PatchMemberRequest;
import ai.serverapi.member.dto.request.PostIntroduceRequest;
import ai.serverapi.member.dto.request.PostRecipientRequest;
import ai.serverapi.member.dto.request.PostSellerRequest;
import ai.serverapi.member.dto.request.PutSellerRequest;
import ai.serverapi.member.dto.response.MemberResponse;
import ai.serverapi.member.dto.response.RecipientListResponse;
import ai.serverapi.member.dto.response.RecipientResponse;
import ai.serverapi.member.enums.IntroduceStatus;
import ai.serverapi.member.enums.MemberApplySellerStatus;
import ai.serverapi.member.enums.RecipientInfoStatus;
import ai.serverapi.member.enums.Role;
import ai.serverapi.member.repository.IntroduceRepository;
import ai.serverapi.member.repository.MemberApplySellerRepository;
import ai.serverapi.member.repository.MemberRepository;
import ai.serverapi.member.repository.RecipientRepository;
import ai.serverapi.member.repository.SellerRepository;
import ai.serverapi.product.dto.response.SellerResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final IntroduceRepository introduceRepository;
    private final SellerRepository sellerRepository;
    private final MemberRepository memberRepository;
    private final MemberApplySellerRepository memberApplySellerRepository;
    private final TokenProvider tokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RecipientRepository recipientInfoRepository;
    private final Environment env;
    private final S3Service s3Service;
    private static final String TYPE = "Bearer ";

    public MemberResponse member(final HttpServletRequest request) {
        Member findMember = getMember(request);

        return new MemberResponse(findMember.getId(),
            findMember.getEmail(),
            findMember.getNickname(),
            findMember.getName(),
            findMember.getRole(),
            findMember.getSnsType(),
            findMember.getStatus(),
            findMember.getCreatedAt(),
            findMember.getModifiedAt());
    }

    private void permitSeller(final Member member, final MemberApplySeller memberApplySeller) {
        memberApplySeller.patchApplyStatus(MemberApplySellerStatus.PERMIT);

        member.patchMemberRole(
            Role.SELLER);    // 임시적으로 SELLER 승인을 했기에 다시 엑세스 토큰을 생성하게 해야함.(아니면 상품등록 안됨)
    }

    @Transactional
    public MessageVo patchMember(
        final PatchMemberRequest patchMemberRequest,
        final HttpServletRequest request) {

        Member member = getMember(request);

        String birth = Optional.ofNullable(patchMemberRequest.getBirth()).orElse("")
                               .replace("-", "")
                               .trim();
        String name = Optional.ofNullable(patchMemberRequest.getName()).orElse("").trim();
        String nickname = Optional.ofNullable(patchMemberRequest.getNickname()).orElse("").trim();
        String password = Optional.ofNullable(patchMemberRequest.getPassword()).orElse("").trim();
        if (!password.isEmpty()) {
            password = passwordEncoder.encode(password);
        }
        member.patchMember(birth, name, nickname, password);
        return new MessageVo("회원 정보 수정 성공");
    }

    private Member getMember(final HttpServletRequest request) {
        Long memberId = tokenProvider.getMemberId(request);
        return memberRepository.findById(memberId).orElseThrow(
            () -> new IllegalArgumentException("유효하지 않은 회원입니다."));
    }

    @Transactional
    public MessageVo postRecipient(final PostRecipientRequest postRecipientRequest,
        final HttpServletRequest request) {
        Member member = getMember(request);
        recipientInfoRepository.save(
            Recipient.of(member, postRecipientRequest.getName(), postRecipientRequest.getZonecode(),
                postRecipientRequest.getAddress(),
                postRecipientRequest.getAddressDetail(),
                postRecipientRequest.getTel(),
                RecipientInfoStatus.NORMAL));
        return new MessageVo("수령인 정보 등록 성공");
    }

    public RecipientListResponse getRecipient(final HttpServletRequest request) {
        Member member = getMember(request);

        List<Recipient> recipientList = member.getRecipientList();
        List<RecipientResponse> list = new LinkedList<>();

        recipientList.sort((r1, r2) -> {
            if (r1.getModifiedAt().isAfter(r2.getModifiedAt())) {
                return -1;
            }
            return 1;
        });

        // 수령인 정보는 1개만 반환하도록 변경
        if (!recipientList.isEmpty()) {
            Recipient r = recipientList.get(0);
            list.add(
                new RecipientResponse(
                    r.getId(), r.getName(), r.getZonecode(), r.getAddress(),
                    r.getAddressDetails(), r.getTel(),
                    r.getStatus(), r.getCreatedAt(), r.getModifiedAt()));
        }

        return new RecipientListResponse(list);
    }

    @Transactional
    public MessageVo postSeller(PostSellerRequest postSellerRequest, HttpServletRequest request) {
        Long memberId = tokenProvider.getMemberId(request);
        Member member = memberRepository.findById(memberId).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 회원입니다."));

        Optional<Seller> optionalSeller = sellerRepository.findByMember(member);
        if (optionalSeller.isPresent()) {
            throw new IllegalArgumentException("이미 판매자 신청을 완료했습니다.");
        }

        Seller seller = Seller.of(member, postSellerRequest.getCompany(),
            postSellerRequest.getTel(),
            postSellerRequest.getZonecode(),
            postSellerRequest.getAddress(), postSellerRequest.getEmail());
        sellerRepository.save(seller);

        MemberApplySeller saveMemberApply = memberApplySellerRepository.save(
            MemberApplySeller.of(memberId));
        permitSeller(member, saveMemberApply);

        return new MessageVo("판매자 정보 등록 성공");
    }

    @Transactional
    public MessageVo putSeller(PutSellerRequest putSellerRequest, HttpServletRequest request) {
        Seller seller = getSellerByRequest(request);

        seller.put(putSellerRequest);

        return new MessageVo("판매자 정보 수정 성공");
    }

    public SellerResponse getSeller(HttpServletRequest request) {
        Seller seller = getSellerByRequest(request);

        return new SellerResponse(seller.getId(), seller.getEmail(), seller.getCompany(),
            seller.getZonecode(),
            seller.getAddress(), seller.getTel());
    }

    @Transactional
    public MessageVo postIntroduce(PostIntroduceRequest postIntroduceRequest,
        HttpServletRequest request) {
        Seller seller = getSellerByRequest(request);

        Optional<Introduce> introduceFindBySeller = introduceRepository.findBySeller(seller);

        if (introduceFindBySeller.isEmpty()) {
            introduceRepository.save(
                Introduce.of(seller, postIntroduceRequest.getSubject(),
                    postIntroduceRequest.getUrl(),
                    IntroduceStatus.USE));
        } else {
            Introduce introduce = introduceFindBySeller.get();
            introduce.changeUrl(introduce.getUrl());
        }

        return new MessageVo("소개글 등록 성공");
    }

    private Seller getSellerByRequest(final HttpServletRequest request) {
        Long memberId = tokenProvider.getMemberId(request);
        Member member = memberRepository.findById(memberId).orElseThrow(
            () -> new IllegalArgumentException("유효하지 않은 회원입니다."));

        return sellerRepository.findByMember(member).orElseThrow(
            () -> new IllegalArgumentException("유효하지 않은 판매자입니다. 판매자 신청을 먼저 해주세요."));
    }

    public String getIntroduce(HttpServletRequest request) {
        Seller seller = getSellerByRequest(request);
        return getSellerIntroduceHtml(seller);
    }

    public String getIntroduce(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId).orElseThrow(
            () -> new IllegalArgumentException("유효하지 않은 판매자입니다."));
        return getSellerIntroduceHtml(seller);
    }

    private String getSellerIntroduceHtml(final Seller seller) {
        Introduce introduce = introduceRepository.findBySeller(seller).orElseThrow(
            () -> new IllegalArgumentException("소개 페이지를 먼저 등록해주세요."));

        String url = introduce.getUrl();
        url = url.substring(url.indexOf("s3.ap-northeast-2.amazonaws.com")
            + "s3.ap-northeast-2.amazonaws.com".length() + 1);

        String bucket = env.getProperty("cloud.s3.bucket");

        return s3Service.getObject(url, bucket);
    }
}
