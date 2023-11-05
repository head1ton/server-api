package ai.serverapi.product.controller;

import ai.serverapi.config.base.Api;
import ai.serverapi.config.base.ResultCode;
import ai.serverapi.product.domain.dto.ProductDto;
import ai.serverapi.product.domain.dto.PutProductDto;
import ai.serverapi.product.domain.vo.ProductListVo;
import ai.serverapi.product.domain.vo.ProductVo;
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
    public ResponseEntity<Api<ProductListVo>> getProductList(
        @PageableDefault(size = 10, page = 0) Pageable pageable,
        @RequestParam(required = false, name = "search") String search,
        @RequestParam(required = false, name = "status", defaultValue = "normal") String status,
        @RequestParam(required = false, name = "category_id", defaultValue = "0") Long categoryId,
        HttpServletRequest request
    ) {
        return ResponseEntity.ok(Api.<ProductListVo>builder()
                                    .code(ResultCode.SUCCESS.code)
                                    .message(ResultCode.SUCCESS.message)
                                    .data(productService.getProductListBySeller(pageable, search,
                                        status, categoryId, request))
                                    .build());
    }

    @PostMapping("")
    public ResponseEntity<Api<ProductVo>> postProduct(
        @RequestBody @Validated ProductDto productDto,
        HttpServletRequest request,
        BindingResult bindingResult) {
        return ResponseEntity.ok(Api.<ProductVo>builder()
                                      .code(ResultCode.POST.code)
                                      .message(ResultCode.POST.message)
                                      .data(productService.postProduct(productDto, request))
                                      .build());
    }

    @PutMapping("")
    public ResponseEntity<Api<ProductVo>> putProduct(
        @RequestBody @Validated PutProductDto putProductDto,
        BindingResult bindingResult) {
        return ResponseEntity.ok(Api.<ProductVo>builder()
                                    .code(ResultCode.SUCCESS.code)
                                    .message(ResultCode.SUCCESS.message)
                                    .data(productService.putProduct(putProductDto))
                                    .build());
    }
}
