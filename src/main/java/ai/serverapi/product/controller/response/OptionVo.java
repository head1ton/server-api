package ai.serverapi.product.controller.response;

import ai.serverapi.product.domain.entity.OptionEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Getter
public class OptionVo {

    private Long optionId;
    private String name;
    private int extraPrice;
    private int ea;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;


    public OptionVo(final OptionEntity optionEntity) {
        this.optionId = optionEntity.getId();
        this.name = optionEntity.getName();
        this.extraPrice = optionEntity.getExtraPrice();
        this.ea = optionEntity.getEa();
        this.createdAt = optionEntity.getCreatedAt();
        this.modifiedAt = optionEntity.getModifiedAt();
    }

    public static List<OptionVo> getVoList(final List<OptionEntity> optionEntityList) {
        List<OptionVo> optionVoList = new ArrayList<>();
        for (OptionEntity optionEntity : optionEntityList) {
            optionVoList.add(new OptionVo(optionEntity));
        }
        return optionVoList;
    }

    public static OptionVo of(final OptionEntity optionEntity) {
        Optional<OptionEntity> optionalOption = Optional.ofNullable(optionEntity);
        return optionalOption.isPresent() ? new OptionVo(optionEntity) : null;
    }
}
