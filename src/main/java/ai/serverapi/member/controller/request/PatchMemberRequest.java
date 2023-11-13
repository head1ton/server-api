package ai.serverapi.member.controller.request;

import ai.serverapi.member.enums.MemberStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatchMemberRequest {

    private String birth;
    private String name;
    private String password;
    private String nickname;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

}
