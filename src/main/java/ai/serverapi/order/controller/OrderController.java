package ai.serverapi.order.controller;

import ai.serverapi.global.base.Api;
import ai.serverapi.global.base.ResultCode;
import ai.serverapi.order.dto.request.CompleteOrderRequest;
import ai.serverapi.order.dto.request.TempOrderRequest;
import ai.serverapi.order.dto.response.CompleteOrderResponse;
import ai.serverapi.order.dto.response.PostTempOrderResponse;
import ai.serverapi.order.dto.response.TempOrderResponse;
import ai.serverapi.order.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api-prefix}/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Api<PostTempOrderResponse>> postOrder(
        @RequestBody @Validated TempOrderRequest tempOrderRequest,
        HttpServletRequest request,
        BindingResult bindingResult
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(
                                 Api.<PostTempOrderResponse>builder()
                                    .code(ResultCode.POST.code)
                                    .message(ResultCode.POST.message)
                                    .data(orderService.postTempOrder(tempOrderRequest, request))
                                    .build()
                             );
    }

    @GetMapping("/temp/{order_id}")
    public ResponseEntity<Api<TempOrderResponse>> getTempOrder(
        @PathVariable(name = "order_id") Long orderId,
        HttpServletRequest request) {
        return ResponseEntity.ok(
            Api.<TempOrderResponse>builder()
               .code(ResultCode.SUCCESS.code)
               .message(ResultCode.SUCCESS.message)
               .data(orderService.getTempOrder(orderId, request))
               .build()
        );
    }

    @PatchMapping("/complete")
    public ResponseEntity<Api<CompleteOrderResponse>> completeOrder(
        @RequestBody @Validated CompleteOrderRequest completeOrderRequest,
        HttpServletRequest request,
        BindingResult bindingResult) {
        return ResponseEntity.ok(
            Api.<CompleteOrderResponse>builder()
               .code(ResultCode.SUCCESS.code)
               .message(ResultCode.SUCCESS.message)
               .data(orderService.completeOrder(completeOrderRequest, request))
               .build());
    }
}
