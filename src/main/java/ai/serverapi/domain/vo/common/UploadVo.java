package ai.serverapi.domain.vo.common;

import ai.serverapi.domain.vo.BaseVo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UploadVo extends BaseVo {

    private String imageUrl;
}
