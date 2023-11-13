package ai.serverapi.product.repository;

import ai.serverapi.product.domain.Category;
import ai.serverapi.product.dto.response.ProductResponse;
import ai.serverapi.product.enums.ProductStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ProductCustomRepository {

    Page<ProductResponse> findAll(Pageable pageable, String search, ProductStatus productStatus,
        Category category, Long memberId);

    List<ProductResponse> findAllByIdList(List<Long> productIdList);
}
