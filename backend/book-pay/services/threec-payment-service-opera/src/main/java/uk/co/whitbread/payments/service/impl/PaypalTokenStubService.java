package uk.co.whitbread.payments.service.impl;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.ClientTokenRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.payments.model.PaypalClientTokenStubResponse;
import uk.co.whitbread.payments.service.PaypalTokenService;

import java.util.Objects;

@Service
@Profile("opera-perf")
@Slf4j
public class PaypalTokenStubService implements PaypalTokenService {

    private final WebClient webClient;

    public PaypalTokenStubService(@Qualifier("stub")WebClient webClient){
        this.webClient = webClient;
    }

    @Override
    public String generateToken(BraintreeGateway braintreeGateway, ClientTokenRequest clientTokenRequest) {
        log.info("PaypalClientToken :: Generating Paypal token via mock service.");
        PaypalClientTokenStubResponse paypalClientTokenStubResponse =  webClient
                                .get()
                                .uri("/paypal/token")
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .retrieve()
                                .bodyToMono(PaypalClientTokenStubResponse.class)
                                .block();
        return Objects.nonNull(paypalClientTokenStubResponse) ? paypalClientTokenStubResponse.getToken() : "";
    }
}
