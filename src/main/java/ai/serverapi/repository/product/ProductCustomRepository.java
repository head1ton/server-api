package ai.serverapi.repository.product;

import ai.serverapi.common.querydsl.QuerydslConfig;
import ai.serverapi.domain.entity.product.QProduct;
import ai.serverapi.domain.vo.product.ProductVo;
import ai.serverapi.domain.vo.product.SellerVo;
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

    public Page<ProductVo> findAll(Pageable pageable, String search) {
        QProduct product = QProduct.product;
        BooleanBuilder builder = new BooleanBuilder();

        search = Optional.ofNullable(search).orElse("").trim();

        if (!search.isEmpty()) {
            builder.and(product.mainTitle.contains(search));
        }

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
                                       product.member.createdAt,
                                       product.member.modifiedAt,
                                       Projections.constructor(SellerVo.class,
                                           product.member.id,
                                           product.member.email,
                                           product.member.nickname,
                                           product.member.name)
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
