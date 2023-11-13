package ai.serverapi.product.repository;

import ai.serverapi.product.controller.response.ProductResponse;
import ai.serverapi.product.domain.entity.CategoryEntity;
import ai.serverapi.product.enums.ProductStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ProductCustomJpaRepository {

    Page<ProductResponse> findAll(Pageable pageable, String search, ProductStatus productStatus,
        CategoryEntity categoryEntity, Long memberId);

    List<ProductResponse> findAllByIdList(List<Long> productIdList);
}
