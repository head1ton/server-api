package ai.serverapi.domain.product.vo;

import ai.serverapi.config.base.BaseVo;
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
