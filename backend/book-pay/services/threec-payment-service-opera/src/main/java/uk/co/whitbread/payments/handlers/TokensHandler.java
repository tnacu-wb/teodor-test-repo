package uk.co.whitbread.payments.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.model.CreateTokenRequest;
import uk.co.whitbread.payments.model.UpdateTokenRequest;
import uk.co.whitbread.payments.service.TokensService;
import uk.co.whitbread.payments.service.impl.DefaultValidationService;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokensHandler {

    private final TokensService tokensService;
    private final DefaultValidationService defaultValidationService;
    private static final String COUNTRY_CODE = "countryCode";

    public Mono<ServerResponse> createToken(ServerRequest request) {
        return request.bodyToMono(CreateTokenRequest.class)
                .doOnNext(defaultValidationService::validate)
                .flatMap(tokensService::createToken)
                .flatMap(createTokenResponse -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(createTokenResponse)));
    }

    public Mono<ServerResponse> updateToken(ServerRequest request) {
        return request.bodyToMono(UpdateTokenRequest.class)
                .doOnNext(defaultValidationService::validate)
                .flatMap(tokensService::updateToken)
                .flatMap(updateTokenResponse -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(updateTokenResponse)));
    }

    public Mono<ServerResponse> createPaypalClientToken(ServerRequest request) {
        String countryCode = request.queryParam(COUNTRY_CODE).orElse("gb");
        return tokensService.createPaypalClientToken(countryCode)
                .flatMap(createPayPalClientTokenResponse -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(createPayPalClientTokenResponse)));
    }
}