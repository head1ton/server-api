package ai.serverapi.domain.product.controller;

import ai.serverapi.config.base.Api;
import ai.serverapi.config.base.ResultCode;
import ai.serverapi.domain.product.service.ProductService;
import ai.serverapi.domain.product.vo.CategoryListVo;
import ai.serverapi.domain.product.vo.ProductListVo;
import ai.serverapi.domain.product.vo.ProductVo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api-prefix}/product")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Api<ProductListVo>> getProductList(
        @PageableDefault(size = 10, page = 0) Pageable pageable,
        @RequestParam(required = false, name = "search") String search
    ) {
        return ResponseEntity.ok(Api.<ProductListVo>builder()
                                    .code(ResultCode.SUCCESS.code)
                                    .message(ResultCode.SUCCESS.message)
                                    .data(productService.getProductList(pageable, search))
                                    .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Api<ProductVo>> getProduct(@PathVariable("id") Long id) {
        return ResponseEntity.ok(Api.<ProductVo>builder()
                                    .code(ResultCode.SUCCESS.code)
                                    .message(ResultCode.SUCCESS.message)
                                    .data(productService.getProduct(id))
                                    .build());
    }

    @GetMapping("/category")
    public ResponseEntity<Api<CategoryListVo>> getCategoryList() {
        return ResponseEntity.ok(Api.<CategoryListVo>builder()
                                    .code(ResultCode.SUCCESS.code)
                                    .message(ResultCode.SUCCESS.message)
                                    .data(productService.getCategoryList())
                                    .build());
    }
}