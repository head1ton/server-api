package ai.serverapi.domain.member.service;

import ai.serverapi.config.base.MessageVo;
import ai.serverapi.config.security.TokenProvider;
import ai.serverapi.domain.member.dto.PatchMemberDto;
import ai.serverapi.domain.member.dto.PostBuyerDto;
import ai.serverapi.domain.member.dto.PostRecipientDto;
import ai.serverapi.domain.member.dto.PutBuyerDto;
import ai.serverapi.domain.member.entity.Buyer;
import ai.serverapi.domain.member.entity.Member;
import ai.serverapi.domain.member.entity.MemberApplySeller;
import ai.serverapi.domain.member.entity.Recipient;
import ai.serverapi.domain.member.enums.MemberApplySellerStatus;
import ai.serverapi.domain.member.enums.RecipientInfoStatus;
import ai.serverapi.domain.member.enums.Role;
import ai.serverapi.domain.member.repository.BuyerRepository;
import ai.serverapi.domain.member.repository.MemberApplySellerRepository;
import ai.serverapi.domain.member.repository.MemberRepository;
import ai.serverapi.domain.member.repository.RecipientRepository;
import ai.serverapi.domain.member.vo.BuyerVo;
import ai.serverapi.domain.member.vo.MemberVo;
import ai.serverapi.domain.member.vo.RecipientListVo;
import ai.serverapi.domain.member.vo.RecipientVo;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final BuyerRepository buyerRepository;
    private final MemberRepository memberRepository;
    private final MemberApplySellerRepository memberApplySellerRepository;
    private final TokenProvider tokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RecipientRepository recipientInfoRepository;
    private static final String TYPE = "Bearer ";

    public MemberVo member(final HttpServletRequest request) {
        Member findMember = getMember(request);

        return MemberVo.builder()
                       .memberId(findMember.getId())
                       .email(findMember.getEmail())
                       .role(findMember.getRole())
                       .createdAt(findMember.getCreatedAt())
                       .modifiedAt(findMember.getModifiedAt())
                       .name(findMember.getName())
                       .nickname(findMember.getNickname())
                       .snsType(findMember.getSnsType())
                       .build();
    }

    @Transactional
    public MessageVo applySeller(final HttpServletRequest request) {
        Long memberId = tokenProvider.getMemberId(request);

        MemberApplySeller saveMemberApply = memberApplySellerRepository.save(
            MemberApplySeller.of(memberId));

        permitSeller(memberId, saveMemberApply);    // 자동 승인으로 처리

        return MessageVo.builder()
                        .message("임시적으로 SELLER 즉시 승인")
                        .build();
    }

    private void permitSeller(final Long memberId, final MemberApplySeller saveMemberApply) {
        saveMemberApply.patchApplyStatus(MemberApplySellerStatus.PERMIT);
        Member member = memberRepository.findById(memberId).orElseThrow(() ->
            new IllegalArgumentException("존재하지 않는 회원입니다."));
        member.patchMemberRole(
            Role.SELLER);    // 임시적으로 SELLER 승인을 했기에 다시 엑세스 토큰을 생성하게 해야함.(아니면 상품등록 안됨)
    }

    @Transactional
    public MessageVo patchMember(
        final PatchMemberDto patchMemberDto,
        final HttpServletRequest request) {

        Member member = getMember(request);

        String birth = Optional.ofNullable(patchMemberDto.getBirth()).orElse("").replaceAll("-", "")
                               .trim();
        String name = Optional.ofNullable(patchMemberDto.getName()).orElse("").trim();
        String nickname = Optional.ofNullable(patchMemberDto.getNickname()).orElse("").trim();
        String password = Optional.ofNullable(patchMemberDto.getPassword()).orElse("").trim();
        if (!password.isEmpty()) {
            password = passwordEncoder.encode(password);
        }
        member.patchMember(birth, name, nickname, password);
        return MessageVo.builder()
                        .message("회원 정보 수정 성공")
                        .build();
    }

    @Transactional
    public MessageVo postBuyer(final PostBuyerDto postBuyerDto,
        final HttpServletRequest request) {
        Member member = getMember(request);

        if (member.getBuyer() != null) {
            throw new IllegalArgumentException("이미 구매자 정보를 등록하셨습니다.");
        }

        Buyer buyer = buyerRepository.save(
            Buyer.of(null, postBuyerDto.getName(), postBuyerDto.getEmail(),
                postBuyerDto.getTel()));
        member.putBuyer(buyer);

        return MessageVo.builder()
                        .message("구매자 정보 등록 성공")
                        .build();
    }

    private Member getMember(final HttpServletRequest request) {
        Long memberId = tokenProvider.getMemberId(request);
        Member member = memberRepository.findById(memberId).orElseThrow(
            () -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        return member;
    }

    public BuyerVo getBuyer(final HttpServletRequest request) {
        Member member = getMember(request);
        Buyer buyer = Optional.ofNullable(member.getBuyer())
                              .orElse(Buyer.ofEmpty());
        return BuyerVo.builder()
                      .id(buyer.getId())
                      .email(buyer.getEmail())
                      .name(buyer.getName())
                      .tel(buyer.getTel())
                      .createdAt(buyer.getCreatedAt())
                      .modifiedAt(buyer.getModifiedAt())
                      .build();
    }

    @Transactional
    public MessageVo putBuyer(final PutBuyerDto putBuyerDto) {
        Long buyerId = putBuyerDto.getId();
        Buyer buyer = buyerRepository.findById(buyerId).orElseThrow(
            () -> new IllegalArgumentException("유효하지 않은 구매자 정보입니다."));

        buyer.put(putBuyerDto.getName(), putBuyerDto.getEmail(),
            putBuyerDto.getTel());
        return MessageVo.builder()
                        .message("구매자 정보 수정 성공")
                          .build();
    }

    @Transactional
    public MessageVo postRecipient(final PostRecipientDto postRecipientDto,
        final HttpServletRequest request) {
        Member member = getMember(request);
        recipientInfoRepository.save(
            Recipient.of(member, postRecipientDto.getName(), postRecipientDto.getAddress(),
                postRecipientDto.getTel(),
                RecipientInfoStatus.NORMAL));
        return MessageVo.builder()
                        .message("수령인 정보 등록 성공")
                        .build();
    }

    public RecipientListVo getRecipient(final HttpServletRequest request) {
        Member member = getMember(request);

        List<Recipient> recipientList = member.getRecipientList();
        List<RecipientVo> list = new LinkedList<>();

        for (Recipient r : recipientList) {
            list.add(RecipientVo.builder()
                                .id(r.getId())
                                .address(r.getAddress())
                                .status(r.getStatus())
                                .name(r.getName())
                                .tel(r.getTel())
                                .createdAt(r.getCreatedAt())
                                .modifiedAt(r.getModifiedAt())
                                .build());
        }

        Collections.reverse(list);

        return RecipientListVo.builder()
                              .list(list)
                              .build();
    }
}