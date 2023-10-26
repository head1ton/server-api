package ai.serverapi.service.member;

import ai.serverapi.domain.dto.member.JoinDto;
import ai.serverapi.domain.entity.member.Member;
import ai.serverapi.domain.vo.member.JoinVo;
import ai.serverapi.repository.member.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public JoinVo join(final JoinDto joinDto) {
        joinDto.passwordEncoder(passwordEncoder);
        Optional<Member> findMember = memberRepository.findByEmail(joinDto.getEmail());
        if (findMember.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }

        Member member = memberRepository.save(Member.createMember(joinDto));
        return JoinVo.builder()
                     .email(member.getEmail())
                     .name(member.getName())
                     .nickname(member.getNickname())
                     .build();
    }
}
