package ai.serverapi.order.controller.vo;

import ai.serverapi.product.domain.entity.SellerEntity;
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
public class SellerVo {

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

    public static SellerVo of(final SellerEntity sellerEntity) {
        return new SellerVo(sellerEntity.getId(), sellerEntity.getEmail(),
            sellerEntity.getCompany(),
            sellerEntity.getZonecode(), sellerEntity.getAddress(), sellerEntity.getAddressDetail(),
            sellerEntity.getTel());
    }
}
