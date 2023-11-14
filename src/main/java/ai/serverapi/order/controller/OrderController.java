package ai.serverapi.order.controller;

import ai.serverapi.global.base.Api;
import ai.serverapi.global.base.ResultCode;
import ai.serverapi.order.controller.request.CompleteOrderRequest;
import ai.serverapi.order.controller.request.TempOrderRequest;
import ai.serverapi.order.controller.response.CompleteOrderResponse;
import ai.serverapi.order.controller.response.OrderInfoResponse;
import ai.serverapi.order.controller.response.PostTempOrderResponse;
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
                                 new Api<>(
                                     ResultCode.POST.code,
                                     ResultCode.POST.message,
                                     orderService.postTempOrder(tempOrderRequest, request))
                             );
    }

    @GetMapping("/temp/{order_id}")
    public ResponseEntity<Api<OrderInfoResponse>> getOrderInfo(
        @PathVariable(name = "order_id") Long orderId,
        HttpServletRequest request) {
        return ResponseEntity.ok(
            new Api<>(
                ResultCode.SUCCESS.code,
                ResultCode.SUCCESS.message,
                orderService.getOrderInfo(orderId, request))
        );
    }

    @PatchMapping("/complete")
    public ResponseEntity<Api<CompleteOrderResponse>> completeOrder(
        @RequestBody @Validated CompleteOrderRequest completeOrderRequest,
        HttpServletRequest request,
        BindingResult bindingResult) {
        return ResponseEntity.ok(
            new Api<>(
                ResultCode.SUCCESS.code,
                ResultCode.SUCCESS.message,
                orderService.completeOrder(completeOrderRequest, request))
        );
    }

//    @GetMapping("/seller")
//    public ResponseEntity<Api<OrderResponse>> getOrderBySeller(
//        @PageableDefault(size = 10, page = 0) Pageable pageable,
//        @RequestParam(required = false, name = "search") String search,
//        @RequestParam(required = false, name = "status", defaultValue = "complete") String status,
//        HttpServletRequest request) {
//        return ResponseEntity.ok(
//            new Api<>(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message,
//                orderService.getOrderListBySeller(pageable, search, status, request))
//        );
//    }
}
