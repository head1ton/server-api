package ai.serverapi.order.repository;

import ai.serverapi.global.querydsl.QuerydslConfig;
import ai.serverapi.member.domain.Seller;
import ai.serverapi.order.domain.QOrder;
import ai.serverapi.order.domain.QOrderItem;
import ai.serverapi.order.dto.response.OrderResponse;
import ai.serverapi.order.enums.OrderStatus;
import ai.serverapi.product.domain.QOption;
import ai.serverapi.product.domain.QProduct;
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
public class OrderCustomRepository {

    private final QuerydslConfig q;

    public Page<OrderResponse> findAllBySeller(Pageable pageable, String search, OrderStatus status,
        Seller seller) {
        QProduct product = QProduct.product;
        QOrder order = QOrder.order;
        QOrderItem orderItem = QOrderItem.orderItem;
        QOption option = QOption.option;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(order.status.eq(OrderStatus.COMPLETE));
        builder.and(product.seller.id.eq(seller.getId()));

        List<OrderResponse> content = q.query()
                                       .select(Projections.constructor(OrderResponse.class, order))
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
