package ai.serverapi.order.repository.port;

import ai.serverapi.order.domain.entity.OrderOptionEntity;
import ai.serverapi.order.domain.model.OrderOption;
import ai.serverapi.order.repository.OrderOptionJpaRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderOptionRepositoryImpl implements OrderOptionRepository {

    private final OrderOptionJpaRepository orderOptionJpaRepository;

    @Override
    public void saveAll(final Iterable<OrderOption> orderOptionList) {
        List<OrderOptionEntity> orderOptionEntityList = new ArrayList<>();
        for (OrderOption o : orderOptionList) {
            orderOptionEntityList.add(OrderOptionEntity.from(o));
        }

        orderOptionJpaRepository.saveAll(orderOptionEntityList).forEach(OrderOptionEntity::toModel);
    }

    @Override
    public OrderOption save(final OrderOption createOrderOption) {
        return orderOptionJpaRepository.save(OrderOptionEntity.from(createOrderOption)).toModel();
    }
}
