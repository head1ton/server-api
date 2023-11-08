package ai.serverapi.order.dto.response;

import ai.serverapi.member.domain.Seller;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(Include.NON_NULL)
@AllArgsConstructor
@Getter
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

    public static SellerResponse of(final Seller seller) {
        return new SellerResponse(seller.getId(), seller.getEmail(), seller.getCompany(),
            seller.getZonecode(), seller.getAddress(), seller.getAddressDetail(), seller.getTel());
    }
}
