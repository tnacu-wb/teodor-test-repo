package uk.co.whitbread.company.employee.config;

import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.http.codec.json.JacksonJsonEncoder;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.shared.cdh.CustomerDataHubClient;
import uk.co.whitbread.shared.cdh.model.CdhAccessContext;
import uk.co.whitbread.shared.cdh.model.CdhHeaders;
import uk.co.whitbread.shared.cdh.oauth.OAuthProvider;

@Slf4j
@Configuration
public class CdhClientOverrideConfig {

    private static final int MAX_IN_MEMORY_SIZE = 16 * 1024 * 1024;

    @Bean
    JsonMapper cdhJsonMapper() {
        return JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .build();
    }

    @Bean("companyEmployeeCdhWebClient")
    WebClient companyEmployeeCdhWebClient(@Qualifier("cdhHttpClient") HttpClient cdhHttpClient,
                                          OAuthProvider oauthProvider,
                                          JsonMapper cdhJsonMapper) {
        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(cdhHttpClient))
            .filter(logRequest())
            .filter(logResponse())
            .filter(retryOn401Function(oauthProvider))
            .exchangeStrategies(ExchangeStrategies.builder()
                .codecs(configurer -> {
                    configurer.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE);
                    configurer.defaultCodecs().jacksonJsonEncoder(new JacksonJsonEncoder(cdhJsonMapper));
                    configurer.defaultCodecs().jacksonJsonDecoder(new JacksonJsonDecoder(cdhJsonMapper));
                })
                .build())
            .defaultHeaders(headers -> headers.addAll(createDefaultHeaders()))
            .build();
    }

    @Bean
    @Primary
    CustomerDataHubClient customerDataHubClient(
        @Qualifier("companyEmployeeCdhWebClient") WebClient cdhWebClient,
        OAuthProvider oauthProvider
    ) {
        return new CustomerDataHubClient(cdhWebClient, oauthProvider);
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            log.info("{} {} {} {} \n--Headers {}", request.method().name(), request.url().getHost(),
                request.url().getPath(), request.url().getQuery(), request.headers());
            return Mono.just(request);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            log.info("CDH API Response: {}", response.statusCode());
            return Mono.just(response);
        });
    }

    private ExchangeFilterFunction retryOn401Function(OAuthProvider oauthProvider) {
        return (request, next) -> next.exchange(request)
            .flatMap(response -> response.statusCode().equals(HttpStatus.UNAUTHORIZED)
                ? retryWithFreshToken(request, next, oauthProvider)
                : Mono.just(response));
    }

    private Mono<ClientResponse> retryWithFreshToken(ClientRequest request, ExchangeFunction next,
                                                     OAuthProvider oauthProvider) {
        log.debug("Got 401 UNAUTHORIZED Response - Refreshing Auth Token");
        String newBearerToken = oauthProvider.getNewBearerToken();
        ClientRequest newRequest = ClientRequest.from(request)
            .headers(setBearerAuth(newBearerToken))
            .build();
        logRequest().filter(newRequest, next);
        return next.exchange(newRequest);
    }

    private HttpHeaders createDefaultHeaders() {
        HttpHeaders defaultHeaders = new HttpHeaders();
        defaultHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
        defaultHeaders.add(CdhHeaders.ACCESS_CONTEXT.getHeader(), CdhAccessContext.PI.name());
        return defaultHeaders;
    }

    private Consumer<HttpHeaders> setBearerAuth(String bearerToken) {
        return headers -> headers.setBearerAuth(bearerToken);
    }
}
