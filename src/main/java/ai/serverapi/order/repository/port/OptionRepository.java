package ai.serverapi.order.repository.port;

import ai.serverapi.product.domain.model.Option;

public interface OptionRepository {

    Option save(Option option);

    Option findById(Long id);
}
