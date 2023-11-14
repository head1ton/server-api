package ai.serverapi.order.repository.port;


import ai.serverapi.order.domain.entity.OrderEntity;
import ai.serverapi.order.domain.model.Order;
import ai.serverapi.order.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order save(final Order order) {
        return orderJpaRepository.save(OrderEntity.from(order)).toModel();
    }

    @Override
    public Order findById(final Long orderId) {
        return orderJpaRepository.findById(orderId).orElseThrow(
            () -> new IllegalArgumentException("유효하지 않은 주문 번호입니다.")).toModel();
    }
}
