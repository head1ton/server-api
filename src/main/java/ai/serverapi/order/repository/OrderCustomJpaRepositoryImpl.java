package ai.serverapi.order.repository;

import ai.serverapi.global.querydsl.QuerydslConfig;
import ai.serverapi.order.domain.entity.QOrderEntity;
import ai.serverapi.order.domain.entity.QOrderItemEntity;
import ai.serverapi.order.domain.vo.OrderVo;
import ai.serverapi.order.enums.OrderStatus;
import ai.serverapi.product.domain.entity.QOptionEntity;
import ai.serverapi.product.domain.entity.QProductEntity;
import ai.serverapi.product.domain.entity.SellerEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderCustomJpaRepositoryImpl implements OrderCustomJpaRepository {

    private final QuerydslConfig q;

    @Override
    public Page<OrderVo> findAllBySeller(final Pageable pageable, final String search,
        final OrderStatus status,
        final SellerEntity sellerEntity) {
        QProductEntity product = QProductEntity.productEntity;
        QOrderEntity order = QOrderEntity.orderEntity;
        QOrderItemEntity orderItem = QOrderItemEntity.orderItemEntity;
        QOptionEntity option = QOptionEntity.optionEntity;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(order.status.eq(OrderStatus.COMPLETE));
        builder.and(product.seller.id.eq(sellerEntity.getId()));

        List<OrderVo> content = q.query()
                                 .select(Projections.constructor(OrderVo.class, order))
                                 .from(order)
                                 .join(orderItem)
                                 .on(order.id.eq(orderItem.order.id))
                                 .join(product)
                                 .on(product.id.eq(orderItem.product.id))
                                 .leftJoin(option)
                                 .on(orderItem.option.id.eq(option.id))
                                 .where(builder)
                                 .orderBy(order.createdAt.desc())
                                 .offset(pageable.getOffset())
                                 .limit(pageable.getPageSize())
                                 .fetch();

        long total = q.query()
                      .from(order)
                      .join(orderItem)
                      .on(order.id.eq(orderItem.order.id))
                      .join(product)
                      .on(product.id.eq(orderItem.product.id))
                      .leftJoin(option)
                      .on(orderItem.option.id.eq(option.id))
                      .where(builder)
                      .stream()
                      .count();

        return new PageImpl<>(content, pageable, total);
    }
}
