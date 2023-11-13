package ai.serverapi.member.dto.response;

import ai.serverapi.member.domain.Member;
import ai.serverapi.member.enums.Role;
import ai.serverapi.member.enums.SnsJoinType;
import ai.serverapi.member.enums.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
@AllArgsConstructor
@Getter
public class MemberResponse {

    private Long memberId;
    private String email;
    private String nickname;
    private String name;
    private Role role;
    private SnsJoinType snsType;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                             .memberId(member.getId())
                             .email(member.getEmail())
                             .nickname(member.getNickname())
                             .name(member.getName())
                             .role(member.getRole())
                             .snsType(member.getSnsType())
                             .status(member.getStatus())
                             .createdAt(member.getCreatedAt())
                             .modifiedAt(member.getModifiedAt())
                             .build();
    }
}
