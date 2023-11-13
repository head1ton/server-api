package ai.serverapi.product.controller;

import ai.serverapi.global.base.Api;
import ai.serverapi.global.base.ResultCode;
import ai.serverapi.product.controller.request.ProductRequest;
import ai.serverapi.product.controller.request.PutProductRequest;
import ai.serverapi.product.controller.response.ProductListResponse;
import ai.serverapi.product.controller.response.ProductResponse;
import ai.serverapi.product.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api-prefix}/seller/product")
public class SellerProductController {

    private final ProductService productService;

    @GetMapping("")
    public ResponseEntity<Api<ProductListResponse>> getProductList(
        @PageableDefault(size = 10, page = 0) Pageable pageable,
        @RequestParam(required = false, name = "search") String search,
        @RequestParam(required = false, name = "status", defaultValue = "normal") String status,
        @RequestParam(required = false, name = "category_id", defaultValue = "0") Long categoryId,
        HttpServletRequest request
    ) {
        return ResponseEntity.ok(
            new Api<>(
                ResultCode.SUCCESS.code,
                ResultCode.SUCCESS.message,
                productService.getProductListBySeller(pageable, search,
                                        status, categoryId, request))
        );
    }

    @PostMapping("")
    public ResponseEntity<Api<ProductResponse>> postProduct(
        @RequestBody @Validated ProductRequest productRequest,
        HttpServletRequest request,
        BindingResult bindingResult) {
        return ResponseEntity.ok(
            new Api<>(
                ResultCode.POST.code,
                ResultCode.POST.message,
                productService.postProduct(productRequest, request))
        );
    }

    @PutMapping("")
    public ResponseEntity<Api<ProductResponse>> putProduct(
        @RequestBody @Validated PutProductRequest putProductRequest,
        BindingResult bindingResult) {
        return ResponseEntity.ok(
            new Api<>(
                ResultCode.SUCCESS.code,
                ResultCode.SUCCESS.message,
                productService.putProduct(putProductRequest))
        );
    }
}
