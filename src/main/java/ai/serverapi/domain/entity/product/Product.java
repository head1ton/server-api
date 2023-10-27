package ai.serverapi.domain.entity.product;

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

    public Product(
        Member member,
        String mainTitle,
        String mainExplanation,
        String productMainExplanation,
        String productSubExplanation) {
        this.member = member;
        this.mainTitle = mainTitle;
        this.mainExplanation = mainExplanation;
        this.productMainExplanation = productMainExplanation;
        this.productSubExplanation = productSubExplanation;
    }

    public static Product of(
        final Member member,
        final String mainTitle,
        final String mainExplanation,
        final String productMainExplanation,
        final String productSubExplanation) {
        return new Product(member, mainTitle, mainExplanation, productMainExplanation,
            productSubExplanation);
    }

}
