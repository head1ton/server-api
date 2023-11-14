package ai.serverapi.order.repository;

import ai.serverapi.order.controller.vo.OrderVo;
import ai.serverapi.order.enums.OrderStatus;
import ai.serverapi.product.domain.entity.SellerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface OrderCustomJpaRepository {

    Page<OrderVo> findAllBySeller(Pageable pageable, String search, OrderStatus status,
        SellerEntity sellerEntity);

}
