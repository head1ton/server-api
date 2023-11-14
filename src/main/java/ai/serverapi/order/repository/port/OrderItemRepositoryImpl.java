package ai.serverapi.order.repository.port;

import ai.serverapi.order.domain.entity.OrderItemEntity;
import ai.serverapi.order.domain.model.OrderItem;
import ai.serverapi.order.repository.OrderItemJpaRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderItemRepository {

    private final OrderItemJpaRepository orderItemJpaRepository;

    @Override
    public OrderItem save(final OrderItem orderItem) {
        return orderItemJpaRepository.save(OrderItemEntity.from(orderItem)).toModel();
    }

    @Override
    public void saveAll(final Iterable<OrderItem> orderItemList) {
        List<OrderItemEntity> orderItemEntityList = new ArrayList<>();
        for (OrderItem o : orderItemList) {
            orderItemEntityList.add(OrderItemEntity.from(o));
        }
        orderItemJpaRepository.saveAll(orderItemEntityList).forEach(OrderItemEntity::toModel);
    }
}
