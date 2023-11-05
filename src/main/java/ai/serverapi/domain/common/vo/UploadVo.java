package ai.serverapi.domain.common.vo;

import ai.serverapi.config.base.BaseVo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UploadVo extends BaseVo {

    private String imageUrl;
}
