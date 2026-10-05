package uk.co.whitbread.payments.service;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.ClientTokenRequest;

public interface PaypalTokenService {

    String generateToken(BraintreeGateway braintreeGateway, ClientTokenRequest clientTokenRequest);
}
