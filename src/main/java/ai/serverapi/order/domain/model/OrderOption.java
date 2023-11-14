package ai.serverapi.order.domain.model;

import ai.serverapi.order.controller.response.OrderOptionResponse;
import ai.serverapi.product.domain.model.Option;
import ai.serverapi.product.enums.OptionStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderOption {

    private Long id;
    private Long optionId;
    private String name;
    private int extraPrice;
    private int ea;
    private OptionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static OrderOption create(List<Option> optionList, Long targetId) {
        Option option = optionList.stream().filter(o -> o.getId().equals(targetId)).findFirst()
                                  .orElseThrow(() -> new IllegalArgumentException(
                                      "유효하지 않은 옵션입니다. option id = " + targetId));
        return OrderOption.builder()
                          .id(option.getId())
                          .optionId(option.getId())
                          .name(option.getName())
                          .extraPrice(option.getExtraPrice())
                          .ea(option.getEa())
                          .status(option.getStatus())
                          .createdAt(option.getCreatedAt())
                          .modifiedAt(option.getModifiedAt())
                          .build();
    }

    public OrderOptionResponse toResponse() {
        return OrderOptionResponse.builder()
                                  .optionId(optionId)
                                  .name(name)
                                  .extraPrice(extraPrice)
                                  .status(status)
                                  .createdAt(createdAt)
                                  .modifiedAt(modifiedAt)
                                  .build();
    }
}