package ai.serverapi.product.dto.response;

import ai.serverapi.member.domain.Seller;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Getter
@Builder
public class SellerResponse {

    @NotNull
    private Long sellerId;
    @NotNull
    private String email;
    @NotNull
    private String company;
    @NotNull
    private String zonecode;
    @NotNull
    private String address;
    @NotNull
    private String addressDetail;
    @NotNull
    private String tel;

    public static SellerResponse from(final Seller seller) {
        return SellerResponse.builder()
                             .sellerId(seller.getId())
                             .email(seller.getEmail())
                             .company(seller.getCompany())
                             .zonecode(seller.getZonecode())
                             .address(seller.getAddress())
                             .addressDetail(seller.getAddressDetail())
                             .tel(seller.getTel())
                             .build();
    }
}
