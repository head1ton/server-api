package ai.serverapi.product.repository;

import ai.serverapi.global.querydsl.QuerydslConfig;
import ai.serverapi.product.domain.Category;
import ai.serverapi.product.domain.QProduct;
import ai.serverapi.product.dto.response.ProductResponse;
import ai.serverapi.product.enums.Status;
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

    public Page<ProductResponse> findAll(Pageable pageable, String search, Status status,
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

        List<ProductResponse> content = q.query()
                                         .select(Projections.constructor(ProductResponse.class,
                                       product
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
