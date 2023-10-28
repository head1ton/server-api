package ai.serverapi.domain.entity.product;

import ai.serverapi.domain.dto.product.ProductDto;
import ai.serverapi.domain.entity.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private Member member;
    private String mainTitle;
    private String mainExplanation;
    private String productMainExplanation;
    private String productSubExplanation;
    private int originPrice;
    private int price;
    private String purchaseInquiry;
    private String origin;
    private String producer;
    private String mainImage;
    private String image1;
    private String image2;
    private String image3;

    public Product(
        final Member member,
        final String mainTitle,
        final String mainExplanation,
        final String productMainExplanation,
        final String productSubExplanation,
        final int originPrice,
        final int price,
        final String purchaseInquiry,
        final String origin,
        final String producer,
        final String mainImage,
        final String image1,
        final String image2,
        final String image3) {
        this.member = member;
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
    }

    public static Product of(
        final Member member,
        final ProductDto productDto) {
        return new Product(
            member,
            productDto.getMainTitle(),
            productDto.getMainExplanation(),
            productDto.getProductMainExplanation(),
            productDto.getProductSubExplanation(),
            productDto.getOriginPrice(),
            productDto.getPrice(),
            productDto.getPurchaseInquiry(),
            productDto.getOrigin(),
            productDto.getProducer(),
            productDto.getMainImage(),
            productDto.getImage1(),
            productDto.getImage2(),
            productDto.getImage3()
        );
    }

}
