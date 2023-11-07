package ai.serverapi.product.domain;

import ai.serverapi.member.domain.Seller;
import ai.serverapi.product.dto.request.ProductRequest;
import ai.serverapi.product.dto.request.PutProductRequest;
import ai.serverapi.product.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
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
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

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
    private Long viewCnt;
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public Product(
        final Seller seller,
        final Category category,
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
        final String image3,
        final Status status,
        final LocalDateTime createdAt,
        final LocalDateTime modifiedAt) {
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
        this.viewCnt = 0L;
        this.status = status;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static Product of(
        final Seller seller,
        final Category category,
        final ProductRequest productRequest) {

        LocalDateTime now = LocalDateTime.now();
        Status status = Status.valueOf(productRequest.getStatus().toUpperCase());

        return new Product(
            seller,
            category,
            productRequest.getMainTitle(),
            productRequest.getMainExplanation(),
            productRequest.getProductMainExplanation(),
            productRequest.getProductSubExplanation(),
            productRequest.getOriginPrice(),
            productRequest.getPrice(),
            productRequest.getPurchaseInquiry(),
            productRequest.getOrigin(),
            productRequest.getProducer(),
            productRequest.getMainImage(),
            productRequest.getImage1(),
            productRequest.getImage2(),
            productRequest.getImage3(),
            status,
            now,
            now
        );
    }

    public void put(final PutProductRequest putProductRequest) {
        LocalDateTime now = LocalDateTime.now();
        Status status = Status.valueOf(putProductRequest.getStatus().toUpperCase());
        this.mainTitle = putProductRequest.getMainTitle();
        this.mainExplanation = putProductRequest.getMainExplanation();
        this.productMainExplanation = putProductRequest.getProductMainExplanation();
        this.productSubExplanation = putProductRequest.getProductSubExplanation();
        this.originPrice = putProductRequest.getOriginPrice();
        this.price = putProductRequest.getPrice();
        this.purchaseInquiry = putProductRequest.getPurchaseInquiry();
        this.origin = putProductRequest.getOrigin();
        this.producer = putProductRequest.getProducer();
        this.mainImage = putProductRequest.getMainImage();
        this.image1 = putProductRequest.getImage1();
        this.image2 = putProductRequest.getImage2();
        this.image3 = putProductRequest.getImage3();
        this.status = status;
        this.modifiedAt = now;
    }

    public void putCategory(final Category category) {
        this.category = category;
    }

    public void addViewCnt() {
        this.viewCnt++;
    }
}
