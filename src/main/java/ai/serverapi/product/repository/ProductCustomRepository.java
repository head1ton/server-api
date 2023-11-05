package ai.serverapi.product.repository;

import ai.serverapi.config.querydsl.QuerydslConfig;
import ai.serverapi.product.domain.entity.Category;
import ai.serverapi.product.domain.entity.QProduct;
import ai.serverapi.product.domain.enums.Status;
import ai.serverapi.product.domain.vo.CategoryVo;
import ai.serverapi.product.domain.vo.ProductVo;
import ai.serverapi.product.domain.vo.SellerVo;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductCustomRepository {

    private final QuerydslConfig q;

    public Page<ProductVo> findAll(Pageable pageable, String search, Status status,
        Category category, Long memberId) {
        QProduct product = QProduct.product;
        BooleanBuilder builder = new BooleanBuilder();

        search = Optional.ofNullable(search).orElse("").trim();
        memberId = Optional.ofNullable(memberId).orElse(0L);
        Optional<Category> optionalCategory = Optional.ofNullable(category);

        if (!search.isEmpty()) {
            builder.and(product.mainTitle.contains(search));
        }
        if (memberId != 0L) {
            builder.and(product.seller.member.id.eq(memberId));
        }
        if (optionalCategory.isPresent()) {
            builder.and(product.category.eq(category));
        }

        builder.and(product.status.eq(status));

        List<ProductVo> content = q.query()
                                   .select(Projections.constructor(ProductVo.class,
                                       product.id,
                                       product.mainTitle,
                                       product.mainExplanation,
                                       product.productMainExplanation,
                                       product.productSubExplanation,
                                       product.originPrice,
                                       product.price,
                                       product.purchaseInquiry,
                                       product.origin,
                                       product.producer,
                                       product.mainImage,
                                       product.image1,
                                       product.image2,
                                       product.image3,
                                       product.viewCnt,
                                       product.status,
                                       product.createdAt,
                                       product.modifiedAt,
                                       Projections.constructor(SellerVo.class,
                                           product.seller.id,
                                           product.seller.email,
                                           product.seller.company,
                                           product.seller.address,
                                           product.seller.tel),
                                       Projections.constructor(CategoryVo.class,
                                           product.category.id,
                                           product.category.name,
                                           product.category.createdAt,
                                           product.category.modifiedAt
                                       )
                                   ))
                                   .from(product)
                                   .where(builder)
                                   .orderBy(product.createdAt.desc())
                                   .offset(pageable.getOffset())
                                   .limit(pageable.getPageSize())
                                   .fetch();

        long total = q.query().from(product).where(builder).stream().count();

        return new PageImpl<>(content, pageable, total);
    }
}