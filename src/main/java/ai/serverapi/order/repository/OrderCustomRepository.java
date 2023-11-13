package ai.serverapi.order.repository;

import ai.serverapi.member.domain.Seller;
import ai.serverapi.order.dto.response.OrderVo;
import ai.serverapi.order.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface OrderCustomRepository {

    Page<OrderVo> findAllBySeller(Pageable pageable, String search, OrderStatus status,
        Seller seller);

}
