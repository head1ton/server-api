package ai.serverapi.service.member;

import ai.serverapi.common.security.TokenProvider;
import ai.serverapi.domain.dto.member.PatchMemberDto;
import ai.serverapi.domain.dto.member.PostBuyerInfoDto;
import ai.serverapi.domain.dto.member.PutBuyerInfoDto;
import ai.serverapi.domain.entity.member.BuyerInfo;
import ai.serverapi.domain.entity.member.Member;
import ai.serverapi.domain.entity.member.MemberApplySeller;
import ai.serverapi.domain.enums.Role;
import ai.serverapi.domain.enums.member.MemberApplySellerStatus;
import ai.serverapi.domain.vo.MessageVo;
import ai.serverapi.domain.vo.member.BuyerInfoVo;
import ai.serverapi.domain.vo.member.MemberVo;
import ai.serverapi.repository.member.BuyerInfoRepository;
import ai.serverapi.repository.member.MemberApplySellerRepository;
import ai.serverapi.repository.member.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
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

    private final BuyerInfoRepository buyerInfoRepository;

    private final MemberRepository memberRepository;
    private final MemberApplySellerRepository memberApplySellerRepository;
    private final TokenProvider tokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
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
        member.patchMemberRole(Role.SELLER);
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
    public MessageVo postBuyerInfo(final PostBuyerInfoDto postBuyerInfoDto,
        final HttpServletRequest request) {
        Member member = getMember(request);

        if (member.getBuyerInfo() != null) {
            throw new IllegalArgumentException("이미 구매자 정보를 등록하셨습니다.");
        }

        BuyerInfo buyerInfo = buyerInfoRepository.save(
            BuyerInfo.of(null, postBuyerInfoDto.getName(), postBuyerInfoDto.getEmail(),
                postBuyerInfoDto.getTel()));
        member.putBuyerInfo(buyerInfo);

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

    public BuyerInfoVo getBuyerInfo(final HttpServletRequest request) {
        Member member = getMember(request);
        BuyerInfo buyerInfo = Optional.ofNullable(member.getBuyerInfo())
                                      .orElse(BuyerInfo.ofEmpty());
        return BuyerInfoVo.builder()
                          .id(buyerInfo.getId())
                          .email(buyerInfo.getEmail())
                          .name(buyerInfo.getName())
                          .tel(buyerInfo.getTel())
                          .createdAt(buyerInfo.getCreatedAt())
                          .modifiedAt(buyerInfo.getModifiedAt())
                          .build();
    }

    @Transactional
    public MessageVo putBuyerInfo(final PutBuyerInfoDto putBuyerInfoDto) {
        Long buyerInfoId = putBuyerInfoDto.getId();
        BuyerInfo buyerInfo = buyerInfoRepository.findById(buyerInfoId).orElseThrow(
            () -> new IllegalArgumentException("유효하지 않은 구매자 정보입니다."));

        buyerInfo.put(putBuyerInfoDto.getName(), putBuyerInfoDto.getEmail(),
            putBuyerInfoDto.getTel());
        return MessageVo.builder()
                        .message("구매자 정보 수정 성공")
                          .build();
    }
}
