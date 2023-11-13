package ai.serverapi.member.dto.response;

import ai.serverapi.member.domain.Recipient;
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

    public static RecipientResponse from(Recipient recipient) {
        return RecipientResponse.builder()
                                .recipientId(recipient.getId())
                                .name(recipient.getName())
                                .zonecode(recipient.getZonecode())
                                .address(recipient.getAddress())
                                .addressDetail(recipient.getAddressDetails())
                                .tel(recipient.getTel())
                                .status(recipient.getStatus())
                                .createdAt(recipient.getCreatedAt())
                                .modifiedAt(recipient.getModifiedAt())
                                .build();
    }
}
