package ai.serverapi.service.member;

import ai.serverapi.domain.dto.member.JoinDto;
import ai.serverapi.domain.entity.member.Member;
import ai.serverapi.domain.vo.member.JoinVo;
import ai.serverapi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public JoinVo join(final JoinDto joinDto) {
        Member member = memberRepository.save(Member.createMember(joinDto));
        return JoinVo.builder()
                     .email(member.getEmail())
                     .name(member.getName())
                     .nickname(member.getNickname())
                     .build();
    }
}
