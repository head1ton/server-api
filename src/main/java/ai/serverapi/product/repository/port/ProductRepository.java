package ai.serverapi.product.repository.port;

import ai.serverapi.product.domain.model.Product;
import java.util.List;

public interface ProductRepository {

    List<Product> findAllById(Iterable<Long> ids);

    Product findById(Long productId);

    Product save(Product product);
}
