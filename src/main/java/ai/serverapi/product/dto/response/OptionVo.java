package ai.serverapi.product.dto.response;

import ai.serverapi.product.domain.Option;
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


    public static List<OptionVo> getVoList(final List<Option> optionList) {
        List<OptionVo> optionVoList = new ArrayList<>();
        for (Option option : optionList) {
            optionVoList.add(new OptionVo(option));
        }
        return optionVoList;
    }

    public OptionVo(final Option option) {
        this.optionId = option.getId();
        this.name = option.getName();
        this.extraPrice = option.getExtraPrice();
        this.ea = option.getEa();
        this.createdAt = option.getCreatedAt();
        this.modifiedAt = option.getModifiedAt();
    }

    public static OptionVo of(final Option option) {
        Optional<Option> optionalOption = Optional.ofNullable(option);
        return optionalOption.isPresent() ? new OptionVo(option) : null;
    }
}
