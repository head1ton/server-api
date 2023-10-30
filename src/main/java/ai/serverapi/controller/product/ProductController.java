package ai.serverapi.controller.product;

import ai.serverapi.domain.dto.Api;
import ai.serverapi.domain.dto.product.ProductDto;
import ai.serverapi.domain.enums.ResultCode;
import ai.serverapi.domain.vo.product.ProductVo;
import ai.serverapi.service.product.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api-prefix}/seller/product")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Api<ProductVo>> postProduct(
        @RequestBody @Validated ProductDto productDto,
        HttpServletRequest request,
        BindingResult bindingResult) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(Api.<ProductVo>builder()
                                      .code(ResultCode.POST.code)
                                      .message(ResultCode.POST.message)
                                      .data(productService.postProduct(productDto, request))
                                      .build());
    }
}
