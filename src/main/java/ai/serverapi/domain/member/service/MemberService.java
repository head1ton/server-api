package ai.serverapi.domain.member.service;

import ai.serverapi.config.base.MessageVo;
import ai.serverapi.config.security.TokenProvider;
import ai.serverapi.domain.member.dto.PatchMemberDto;
import ai.serverapi.domain.member.dto.PostRecipientDto;
import ai.serverapi.domain.member.dto.PostSellerDto;
import ai.serverapi.domain.member.entity.Member;
import ai.serverapi.domain.member.entity.MemberApplySeller;
import ai.serverapi.domain.member.entity.Recipient;
import ai.serverapi.domain.member.entity.Seller;
import ai.serverapi.domain.member.enums.MemberApplySellerStatus;
import ai.serverapi.domain.member.enums.RecipientInfoStatus;
import ai.serverapi.domain.member.enums.Role;
import ai.serverapi.domain.member.repository.MemberApplySellerRepository;
import ai.serverapi.domain.member.repository.MemberRepository;
import ai.serverapi.domain.member.repository.RecipientRepository;
import ai.serverapi.domain.member.repository.SellerRepository;
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

    private final SellerRepository sellerRepository;
    private final MemberRepository memberRepository;
    private final MemberApplySellerRepository memberApplySellerRepository;
    private final TokenProvider tokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RecipientRepository recipientInfoRepository;
    private static final String TYPE = "Bearer ";

    public MemberVo member(final HttpServletRequest request) {
        Member findMember = getMember(request);

        return new MemberVo(findMember.getId(),
            findMember.getEmail(),
            findMember.getNickname(),
            findMember.getName(),
            findMember.getRole(),
            findMember.getSnsType(),
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
        return new MessageVo("회원 정보 수정 성공");
    }

    private Member getMember(final HttpServletRequest request) {
        Long memberId = tokenProvider.getMemberId(request);
        return memberRepository.findById(memberId).orElseThrow(
            () -> new IllegalArgumentException("유효하지 않은 회원입니다."));
    }

    @Transactional
    public MessageVo postRecipient(final PostRecipientDto postRecipientDto,
        final HttpServletRequest request) {
        Member member = getMember(request);
        recipientInfoRepository.save(
            Recipient.of(member, postRecipientDto.getName(), postRecipientDto.getAddress(),
                postRecipientDto.getTel(),
                RecipientInfoStatus.NORMAL));
        return new MessageVo("수령인 정보 등록 성공");
    }

    public RecipientListVo getRecipient(final HttpServletRequest request) {
        Member member = getMember(request);

        List<Recipient> recipientList = member.getRecipientList();
        List<RecipientVo> list = new LinkedList<>();

        for (Recipient r : recipientList) {
            list.add(new RecipientVo(r.getId(), r.getName(), r.getAddress(), r.getTel(),
                r.getStatus(), r.getCreatedAt(), r.getModifiedAt()));
        }

        Collections.reverse(list);

        return new RecipientListVo(list);
    }

    @Transactional
    public MessageVo postSeller(PostSellerDto postSellerDto, HttpServletRequest request) {
        Long memberId = tokenProvider.getMemberId(request);
        Member member = memberRepository.findById(memberId).orElseThrow(() ->
            new IllegalArgumentException("유효하지 않은 회원입니다."));

        Seller seller = Seller.of(member, postSellerDto.getCompany(), postSellerDto.getTel(),
            postSellerDto.getAddress(), postSellerDto.getEmail());
        sellerRepository.save(seller);

        MemberApplySeller saveMemberApply = memberApplySellerRepository.save(
            MemberApplySeller.of(memberId));
        permitSeller(member, saveMemberApply);

        return new MessageVo("판매자 정보 등록 성공");
    }
}
