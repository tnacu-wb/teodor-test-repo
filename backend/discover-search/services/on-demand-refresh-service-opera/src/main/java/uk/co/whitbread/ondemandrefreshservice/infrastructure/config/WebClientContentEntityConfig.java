package uk.co.whitbread.ondemandrefreshservice.infrastructure.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebClientContentEntityConfig {

    @Value("${config.service.content-entity.baseOrigin}")
    private String contentEntityBaseOrigin;

    @Bean
    public WebClient contentEntityWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(contentEntityBaseOrigin)
                .filter(logRequest())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.debug("Request: {} {}, Headers: {}", clientRequest.method(), clientRequest.url(),
                    clientRequest.headers());
            return Mono.just(clientRequest);
        });
    }
}
