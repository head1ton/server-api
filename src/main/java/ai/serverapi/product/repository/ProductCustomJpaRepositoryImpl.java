package ai.serverapi.product.repository;

import ai.serverapi.global.querydsl.QuerydslConfig;
import ai.serverapi.product.controller.response.ProductResponse;
import ai.serverapi.product.domain.entity.CategoryEntity;
import ai.serverapi.product.domain.entity.ProductEntity;
import ai.serverapi.product.domain.entity.QOptionEntity;
import ai.serverapi.product.domain.entity.QProductEntity;
import ai.serverapi.product.enums.ProductStatus;
import com.querydsl.core.BooleanBuilder;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductCustomJpaRepositoryImpl implements ProductCustomJpaRepository {

    private final QuerydslConfig q;

    @Override
    public Page<ProductResponse> findAll(Pageable pageable, String search,
        ProductStatus productStatus, CategoryEntity categoryEntity, Long memberId) {
        QProductEntity product = QProductEntity.productEntity;
        QOptionEntity option = QOptionEntity.optionEntity;
        BooleanBuilder builder = new BooleanBuilder();

        search = Optional.ofNullable(search).orElse("").trim();
        memberId = Optional.ofNullable(memberId).orElse(0L);
        Optional<CategoryEntity> optionalCategory = Optional.ofNullable(categoryEntity);

        if (!search.isEmpty()) {
            builder.and(product.mainTitle.contains(search));
        }
        if (memberId != 0L) {
            builder.and(product.seller.member.id.eq(memberId));
        }
        if (optionalCategory.isPresent()) {
            builder.and(product.category.eq(categoryEntity));
        }

        builder.and(product.status.eq(productStatus));

        List<ProductEntity> fetch = q.query()
                                     .selectFrom(product)
                                     .join(product.category).fetchJoin()
                                     .join(product.seller).fetchJoin()
                                     .leftJoin(product.optionList, option).fetchJoin()
                                     .where(builder)
                                     .distinct()
                                     .orderBy(product.createdAt.desc())
                                     .offset(pageable.getOffset())
                                     .limit(pageable.getPageSize())
                                     .fetch();

        List<ProductResponse> content = fetch.stream().map(
                                                 ProductResponse::from)
                                             .collect(Collectors.toList());

        long total = q.query().from(product).where(builder).stream().count();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<ProductResponse> findAllByIdList(final List<Long> productIdList) {

        QProductEntity product = QProductEntity.productEntity;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(product.id.in(productIdList));

        List<ProductEntity> fetch = q.query().selectFrom(product).where(builder).fetch();

        List<ProductResponse> content = fetch.stream().map(ProductResponse::from)
                                             .collect(Collectors.toList());

        return content;
    }
}
