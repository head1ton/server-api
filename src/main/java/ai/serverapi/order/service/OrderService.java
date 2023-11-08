package ai.serverapi.order.service;

import ai.serverapi.global.util.MemberUtil;
import ai.serverapi.member.domain.Member;
import ai.serverapi.order.dto.request.TempOrderRequest;
import ai.serverapi.order.dto.response.TempOrderResponse;
import ai.serverapi.order.repository.OrderItemRepository;
import ai.serverapi.order.repository.OrderRepository;
import ai.serverapi.product.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final MemberUtil memberUtil;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository ordersDetailRepository;

    @Transactional
    public TempOrderResponse postTempOrder(
        TempOrderRequest tempOrderRequest,
        HttpServletRequest request
    ) {
        Member member = memberUtil.getMember(request);

        return new TempOrderResponse(1L);
    }
}
