package ai.serverapi.member.controller.response;

import ai.serverapi.member.domain.entity.RecipientEntity;
import ai.serverapi.member.enums.RecipientInfoStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@JsonInclude(Include.NON_NULL)
@JsonNaming(SnakeCaseStrategy.class)
@AllArgsConstructor
@Getter
@Builder
public class RecipientResponse {

    private Long recipientId;
    private String name;
    private String zonecode;
    private String address;
    private String addressDetail;
    private String tel;
    private RecipientInfoStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static RecipientResponse from(RecipientEntity recipientEntity) {
        return RecipientResponse.builder()
                                .recipientId(recipientEntity.getId())
                                .name(recipientEntity.getName())
                                .zonecode(recipientEntity.getZonecode())
                                .address(recipientEntity.getAddress())
                                .addressDetail(recipientEntity.getAddressDetails())
                                .tel(recipientEntity.getTel())
                                .status(recipientEntity.getStatus())
                                .createdAt(recipientEntity.getCreatedAt())
                                .modifiedAt(recipientEntity.getModifiedAt())
                                .build();
    }
}
