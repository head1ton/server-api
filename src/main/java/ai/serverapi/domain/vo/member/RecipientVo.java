package ai.serverapi.domain.vo.member;

import ai.serverapi.domain.enums.member.RecipientInfoStatus;
import ai.serverapi.domain.vo.BaseVo;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RecipientVo extends BaseVo {

    private Long id;
    private String name;
    private String address;
    private String tel;
    private RecipientInfoStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
