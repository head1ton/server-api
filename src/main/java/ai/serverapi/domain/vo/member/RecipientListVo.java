package ai.serverapi.domain.vo.member;

import ai.serverapi.domain.vo.BaseVo;
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
