package ai.serverapi.product.repository.port;

import ai.serverapi.product.domain.entity.ProductEntity;
import ai.serverapi.product.domain.model.Product;
import ai.serverapi.product.repository.ProductJpaRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public List<Product> findAllById(final Iterable<Long> ids) {
        return productJpaRepository.findAllById(ids).stream()
                                   .map(ProductEntity::toModel)
                                   .collect(Collectors.toList());
    }

    @Override
    public Product findById(final Long productId) {
        ProductEntity productEntity = productJpaRepository.findById(productId).orElseThrow(
            () -> new IllegalArgumentException("유효하지 않은 상품입니다.")
        );
        return productEntity.toModel();
    }

    @Override
    public Product save(final Product product) {
        return productJpaRepository.save(ProductEntity.from(product)).toModel();
    }
}
