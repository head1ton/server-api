package ai.serverapi.member.domain.model;

import ai.serverapi.member.enums.MemberApplySellerStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberApplySeller {

    private Long id;
    private Long memberId;
    private MemberApplySellerStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
