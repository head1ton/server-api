package ai.serverapi.domain.member.vo;

import ai.serverapi.config.base.BaseVo;
import ai.serverapi.domain.member.enums.RecipientInfoStatus;
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
