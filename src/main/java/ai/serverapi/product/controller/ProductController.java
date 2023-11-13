package ai.serverapi.product.controller;

import ai.serverapi.global.base.Api;
import ai.serverapi.global.base.MessageVo;
import ai.serverapi.global.base.ResultCode;
import ai.serverapi.product.controller.request.AddViewCntRequest;
import ai.serverapi.product.controller.response.CategoryListResponse;
import ai.serverapi.product.controller.response.ProductBasketListResponse;
import ai.serverapi.product.controller.response.ProductListResponse;
import ai.serverapi.product.controller.response.ProductResponse;
import ai.serverapi.product.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api-prefix}/product")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Api<ProductListResponse>> getProductList(
        @PageableDefault(size = 10, page = 0) Pageable pageable,
        @RequestParam(required = false, name = "search") String search,
        @RequestParam(required = false, name = "status", defaultValue = "normal") String status,
        @RequestParam(required = false, name = "category_id", defaultValue = "0") Long categoryId,
        @RequestParam(required = false, name = "seller_id", defaultValue = "0") Long sellerId
    ) {
        return ResponseEntity.ok(
            new Api<>(
                ResultCode.SUCCESS.code,
                ResultCode.SUCCESS.message,
                productService.getProductList(pageable, search, status,
                                        categoryId, sellerId))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Api<ProductResponse>> getProduct(@PathVariable("id") Long id) {
        return ResponseEntity.ok(
            new Api<>(
                ResultCode.SUCCESS.code,
                ResultCode.SUCCESS.message,
                productService.getProduct(id))
        );
    }

    @GetMapping("/category")
    public ResponseEntity<Api<CategoryListResponse>> getCategoryList() {
        return ResponseEntity.ok(
            new Api<>(
                ResultCode.SUCCESS.code,
                ResultCode.SUCCESS.message,
                productService.getCategoryList())
        );
    }

    @PatchMapping("/cnt")
    public ResponseEntity<Api<MessageVo>> addViewCnt(
        @RequestBody @Validated AddViewCntRequest addViewCntRequest,
        BindingResult bindingResult
    ) {
        return ResponseEntity.ok(
            new Api<>(
                ResultCode.SUCCESS.code,
                ResultCode.SUCCESS.message,
                productService.addViewCnt(addViewCntRequest))
        );
    }

    @GetMapping("/basket")
    public ResponseEntity<Api<ProductBasketListResponse>> getProductBasket(
        @RequestParam(name = "product_id") List<Long> productIdList) {
        return ResponseEntity.ok(
            new Api<>(
                ResultCode.SUCCESS.code,
                ResultCode.SUCCESS.message,
                productService.getProductBasket(productIdList))
        );
    }
}
