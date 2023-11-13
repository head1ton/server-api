package ai.serverapi.order.service;

import ai.serverapi.order.dto.request.CompleteOrderRequest;
import ai.serverapi.order.dto.request.TempOrderRequest;
import ai.serverapi.order.dto.response.CompleteOrderResponse;
import ai.serverapi.order.dto.response.OrderResponse;
import ai.serverapi.order.dto.response.PostTempOrderResponse;
import ai.serverapi.order.dto.response.TempOrderResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    PostTempOrderResponse postTempOrder(
        TempOrderRequest tempOrderRequest,
        HttpServletRequest request
    );

    TempOrderResponse getTempOrder(Long orderId, HttpServletRequest request);

    CompleteOrderResponse completeOrder(
        CompleteOrderRequest completeOrderRequest,
        HttpServletRequest request);

    OrderResponse getOrderListBySeller(Pageable pageable, String search, String status,
        HttpServletRequest request);
}
