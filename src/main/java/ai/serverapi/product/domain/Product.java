package ai.serverapi.product.domain;

import ai.serverapi.member.domain.Seller;
import ai.serverapi.product.dto.request.ProductRequest;
import ai.serverapi.product.dto.request.PutProductRequest;
import ai.serverapi.product.enums.ProductStatus;
import ai.serverapi.product.enums.ProductType;
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
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
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
    private ProductStatus status;
    private int ea;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Option> optionList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private ProductType type;

    public Product(
        final Seller seller,
        final Category category,
        final ProductRequest productRequest) {
        LocalDateTime now = LocalDateTime.now();
        int ea = Optional.of(productRequest.getEa()).orElse(0);
        ProductStatus status = ProductStatus.valueOf(
            productRequest.getStatus().toUpperCase());
        ProductType type = ProductType.valueOf(productRequest.getType().toUpperCase());
        this.seller = seller;
        this.category = category;
        this.mainTitle = productRequest.getMainTitle();
        this.mainExplanation = productRequest.getMainExplanation();
        this.productMainExplanation = productRequest.getProductMainExplanation();
        this.productSubExplanation = productRequest.getProductSubExplanation();
        this.originPrice = productRequest.getOriginPrice();
        this.price = productRequest.getPrice();
        this.purchaseInquiry = productRequest.getPurchaseInquiry();
        this.origin = productRequest.getOrigin();
        this.producer = productRequest.getProducer();
        this.mainImage = productRequest.getMainImage();
        this.image1 = productRequest.getImage1();
        this.image2 = productRequest.getImage2();
        this.image3 = productRequest.getImage3();
        this.viewCnt = 0L;
        this.status = status;
        this.type = type;
        this.ea = ea;
        this.createdAt = now;
        this.modifiedAt = now;
    }

    public static Product of(
        final Seller seller,
        final Category category,
        final ProductRequest productRequest) {

        return new Product(seller, category, productRequest);
    }

    public void put(final PutProductRequest putProductRequest) {
        LocalDateTime now = LocalDateTime.now();
        int ea = Optional.of(putProductRequest.getEa()).orElse(0);
        ProductStatus productStatus = ProductStatus.valueOf(putProductRequest.getStatus().toUpperCase());
        ProductType type = ProductType.valueOf(
            Optional.ofNullable(putProductRequest.getType()).orElse("NORMAL").toUpperCase());
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
        this.status = productStatus;
        this.type = type;
        this.ea = ea;
        this.modifiedAt = now;
    }

    public void putCategory(final Category category) {
        this.category = category;
    }

    public void addViewCnt() {
        this.viewCnt++;
    }

    public void addOptionsList(Option option) {
        this.optionList.add(option);
    }

    public void addAllOptionsList(final List<Option> options) {
        this.optionList.addAll(options);
    }

    public void minusEa(int ea) {
        this.ea -= ea;
    }
}
