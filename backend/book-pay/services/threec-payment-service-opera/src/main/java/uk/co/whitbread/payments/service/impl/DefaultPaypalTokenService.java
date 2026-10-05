package uk.co.whitbread.payments.service.impl;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.ClientTokenRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import uk.co.whitbread.payments.service.PaypalTokenService;

@Service
@Profile("!opera-perf")
@Slf4j
@RequiredArgsConstructor
public class DefaultPaypalTokenService implements PaypalTokenService {

    @Override
    public String generateToken(BraintreeGateway braintreeGateway, ClientTokenRequest clientTokenRequest) {
        log.info("PaypalClientToken :: Generating Paypal token via braintreeGateway SDK library..");
        return braintreeGateway.clientToken().generate(clientTokenRequest);
    }

}
