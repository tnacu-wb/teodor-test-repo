package uk.co.whitbread.payments.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.ObjectAppendingMarker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.client.ThreeCPaymentClient;
import uk.co.whitbread.payments.exception.ErrorCodes;
import uk.co.whitbread.payments.exception.PaymentServiceException;
import uk.co.whitbread.payments.model.CreateTokenRequest;
import uk.co.whitbread.payments.model.CreateTokenResponse;
import uk.co.whitbread.payments.model.PaypalClientTokenResponse;
import uk.co.whitbread.payments.model.UpdateTokenRequest;
import uk.co.whitbread.payments.model.UpdateTokenResponse;
import uk.co.whitbread.payments.properties.CardTypeProperties;
import uk.co.whitbread.payments.service.TokensService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultTokensService implements TokensService {

    private final ThreeCPaymentClient threeCPaymentClient;

    @Value("${3c.response.tokenisationResponseCodes}")
    private final List<Integer> tokenisationResponseCodes;
    private final CardTypeProperties cardTypeProperties;

    @Override
    public Mono<CreateTokenResponse> createToken(CreateTokenRequest createTokenRequest) {
        log.info("Incoming request to create token with requestId {}.", createTokenRequest.getRequestId());
        return threeCPaymentClient.createToken(createTokenRequest)
                .map(response -> {
                    log.info("Response :: Create token return code {}", response.getReturnCode());
                    if (!tokenisationResponseCodes.contains(response.getReturnCode())) {
                        throw new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                                String.format("Error encountered during token create [%s].", response.getReturnText()), ErrorCodes.PROVIDER_ERROR);
                    } else {
                        log.info("Successfully created token.");
                        return response;
                    }
                })
                .map(response -> CreateTokenResponse.builder()
                    .token(response.getToken())
                    .cardType(
                        cardTypeProperties.getCardTypes().getOrDefault(response.getCardType(), response.getCardType()))
                    .build());
    }

    @Override
    public Mono<UpdateTokenResponse> updateToken(UpdateTokenRequest updateTokenRequest) {
        log.info(new ObjectAppendingMarker("updateTokenRequest", updateTokenRequest.getRequestId()),
                "Incoming request to update token with requestId {}.", updateTokenRequest.getRequestId());
        return threeCPaymentClient.updateToken(updateTokenRequest)
                .map(response -> {
                    log.info("Response :: Update token return code {}", response.getReturnCode());
                    if (!tokenisationResponseCodes.contains(response.getReturnCode())) {
                        throw new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                                String.format("Error encountered during token update [%s].", response.getReturnText()),
                                ErrorCodes.PROVIDER_ERROR);
                    } else {
                        log.info(new ObjectAppendingMarker("updateTokenResponse", response), "Successfully updated token.");
                        return response;
                    }
                })
                .map(response -> UpdateTokenResponse.builder()
                        .token(response.getToken())
                        .build());
    }

    @Override
    public Mono<PaypalClientTokenResponse> createPaypalClientToken(String countryCode) {
        return threeCPaymentClient.createPaypalClientToken(countryCode)
                .map(response -> {
                    if (response.getClientId()==null || response.getClientId().isEmpty()) {
                        throw new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                                String.format("Error encountered during paypal token create for the country code [%s].", countryCode),
                                ErrorCodes.PROVIDER_ERROR);
                    } else {
                        log.info(new ObjectAppendingMarker("createPaypalClientToken", response.getClientId()), "Successfully paypal client token is generated.");
                        return response;
                    }
                });
    }
}
