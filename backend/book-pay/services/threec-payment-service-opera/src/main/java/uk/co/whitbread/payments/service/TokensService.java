package uk.co.whitbread.payments.service;

import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.model.CreateTokenRequest;
import uk.co.whitbread.payments.model.CreateTokenResponse;
import uk.co.whitbread.payments.model.PaypalClientTokenResponse;
import uk.co.whitbread.payments.model.UpdateTokenRequest;
import uk.co.whitbread.payments.model.UpdateTokenResponse;

public interface TokensService {
    Mono<CreateTokenResponse> createToken(CreateTokenRequest createTokenRequest);

    Mono<UpdateTokenResponse> updateToken(UpdateTokenRequest updateTokenRequest);

    Mono<PaypalClientTokenResponse> createPaypalClientToken(String countryCode);
}
