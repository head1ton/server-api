package ai.serverapi.order.service;

import ai.serverapi.order.controller.request.CompleteOrderRequest;
import ai.serverapi.order.controller.request.TempOrderRequest;
import ai.serverapi.order.controller.response.CompleteOrderResponse;
import ai.serverapi.order.controller.response.OrderInfoResponse;
import ai.serverapi.order.controller.response.PostTempOrderResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface OrderService {

    PostTempOrderResponse postTempOrder(
        TempOrderRequest tempOrderRequest,
        HttpServletRequest request
    );

    OrderInfoResponse getOrderInfo(Long orderId, HttpServletRequest request);

    CompleteOrderResponse completeOrder(
        CompleteOrderRequest completeOrderRequest,
        HttpServletRequest request);

//    OrderResponse getOrderListBySeller(Pageable pageable, String search, String status,
//        HttpServletRequest request);
}
