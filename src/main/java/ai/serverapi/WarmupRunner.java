package ai.serverapi;

import ai.serverapi.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WarmupRunner implements ApplicationListener<ApplicationReadyEvent> {

    public static boolean isWarmup = false;
    private final ProductService productService;

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        Pageable pageable = Pageable.ofSize(10);
        productService.getProductList(pageable, "", "normal", 0L, 0L);
        isWarmup = true;
    }
}
