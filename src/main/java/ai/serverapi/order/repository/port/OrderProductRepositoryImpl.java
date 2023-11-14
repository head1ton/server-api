package ai.serverapi.order.repository.port;

import ai.serverapi.order.domain.entity.OrderProductEntity;
import ai.serverapi.order.domain.model.OrderProduct;
import ai.serverapi.order.repository.OrderProductJpaRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderProductRepositoryImpl implements OrderProductRepository {

    private final OrderProductJpaRepository orderProductJpaRepository;

    @Override
    public void saveAll(final Iterable<OrderProduct> orderProductList) {
        List<OrderProductEntity> orderProductEntityList = new ArrayList<>();
        for (OrderProduct o : orderProductList) {
            orderProductEntityList.add(OrderProductEntity.from(o));
        }

        orderProductJpaRepository.saveAll(orderProductEntityList)
                                 .forEach(OrderProductEntity::toModel);
    }

    @Override
    public OrderProduct save(final OrderProduct createOrderProduct) {
        return orderProductJpaRepository.save(OrderProductEntity.from(createOrderProduct))
                                        .toModel();
    }
}
