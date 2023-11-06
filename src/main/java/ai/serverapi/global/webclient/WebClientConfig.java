package ai.serverapi.global.webclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient kakaoClient() {
        return WebClient.builder().baseUrl("https://kauth.kakao.com").build();
    }

    @Bean
    public WebClient kakaoApiClient() {
        return WebClient.builder().baseUrl("https://kapi.kakao.com").build();
    }
}
