package ai.serverapi.product.domain.model;

import ai.serverapi.order.controller.request.TempOrderDto;
import ai.serverapi.product.enums.ProductStatus;
import ai.serverapi.product.enums.ProductType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Product {

    private final Long id;
    private final Seller seller;
    private final Category category;
    private final String mainTitle;
    private final String mainExplanation;
    private final String productMainExplanation;
    private final String productSubExplanation;
    private final int originPrice;
    private final int price;
    private final String purchaseInquiry;
    private final String origin;
    private final String producer;
    private final String mainImage;
    private final String image1;
    private final String image2;
    private final String image3;
    private final Long viewCnt;
    private final ProductStatus status;
    private int ea;
    private List<Option> optionList = new ArrayList<>();
    private final ProductType type;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    @Builder
    public Product(Long id, Seller seller, Category category, String mainTitle,
        String mainExplanation,
        String productMainExplanation, String productSubExplanation, int originPrice, int price,
        String purchaseInquiry, String origin, String producer, String mainImage, String image1,
        String image2, String image3, Long viewCnt, ProductStatus status, int ea,
        LocalDateTime createdAt, LocalDateTime modifiedAt, List<Option> optionList,
        ProductType type) {
        this.id = id;
        this.seller = seller;
        this.category = category;
        this.mainTitle = mainTitle;
        this.mainExplanation = mainExplanation;
        this.productMainExplanation = productMainExplanation;
        this.productSubExplanation = productSubExplanation;
        this.originPrice = originPrice;
        this.price = price;
        this.purchaseInquiry = purchaseInquiry;
        this.origin = origin;
        this.producer = producer;
        this.mainImage = mainImage;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.viewCnt = viewCnt;
        this.status = status;
        this.ea = ea;
        this.optionList = optionList;
        this.type = type;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public void checkInStock(TempOrderDto tempOrderDto) {
        int ea = tempOrderDto.getEa();
        if (this.type == ProductType.NORMAL) {
            if (this.ea < ea) {
                throw new IllegalArgumentException(
                    String.format("재고가 부족합니다.! 현재 재고 = %s개", this.ea));
            }
        } else if (this.type == ProductType.OPTION) {
            Long optionId = tempOrderDto.getOptionId();

            Option option = this.optionList.stream()
                                           .filter(o -> o.getId().equals(optionId))
                                           .findFirst()
                                           .orElseThrow(() -> new IllegalArgumentException(
                                               String.format("유효하지 않은 옵션입니다. option id = %s",
                                                   optionId)));
            option.checkInStock(tempOrderDto);
        }
    }

    public void checkInStock(final int ea, final Long optionId) {
        if (this.type == ProductType.NORMAL) {
            if (this.ea < ea) {
                throw new IllegalArgumentException(
                    String.format("재고가 부족합니다! 현재 재고 = %s개", this.ea));
            }
        } else if (this.type == ProductType.OPTION) {
            Option option = this.optionList.stream()
                                           .filter(o -> o.getId().equals(optionId))
                                           .findFirst()
                                           .orElseThrow(() -> new IllegalArgumentException(
                                               String.format("유효하지 않은 옵션입니다. option id = %s",
                                                   optionId)));
            option.checkInStock(ea);
        }
    }

    public void minusEa(int ea) {
        if (this.type == ProductType.NORMAL) {
            this.ea -= ea;
        }
    }
}
