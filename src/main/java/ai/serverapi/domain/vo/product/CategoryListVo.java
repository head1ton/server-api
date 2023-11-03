package ai.serverapi.domain.vo.product;

import ai.serverapi.domain.vo.BaseVo;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CategoryListVo extends BaseVo {

    private List<CategoryVo> list;
}
