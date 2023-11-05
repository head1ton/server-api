package ai.serverapi.domain.member.vo;

import ai.serverapi.config.base.BaseVo;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RecipientListVo extends BaseVo {

    private List<RecipientVo> list;
}
