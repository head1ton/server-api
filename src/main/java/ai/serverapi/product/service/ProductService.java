package ai.serverapi.product.service;

import ai.serverapi.global.base.MessageVo;
import ai.serverapi.product.controller.request.AddViewCntRequest;
import ai.serverapi.product.controller.request.ProductRequest;
import ai.serverapi.product.controller.request.PutProductRequest;
import ai.serverapi.product.controller.response.CategoryListResponse;
import ai.serverapi.product.controller.response.ProductBasketListResponse;
import ai.serverapi.product.controller.response.ProductListResponse;
import ai.serverapi.product.controller.response.ProductResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    ProductResponse postProduct(
        final ProductRequest productRequest,
        final HttpServletRequest request);

    ProductBasketListResponse getProductBasket(List<Long> productIdList);

    ProductListResponse getProductList(
        final Pageable pageable,
        final String search,
        final String status,
        final Long categoryId,
        final Long sellerId);

    ProductResponse getProduct(final Long id);

    ProductResponse putProduct(final PutProductRequest putProductRequest);

    ProductListResponse getProductListBySeller(
        final Pageable pageable,
        final String search,
        final String status,
        final Long categoryId,
        final HttpServletRequest request);

    CategoryListResponse getCategoryList();

    MessageVo addViewCnt(AddViewCntRequest addViewCntRequest);
}
