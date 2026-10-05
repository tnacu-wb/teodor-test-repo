package uk.co.whitbread.hotel.account.client.payment;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uk.co.whitbread.hotel.account.client.payment.model.CreateTokenRequest;
import uk.co.whitbread.hotel.account.client.payment.model.CreateTokenResponse;

/**
 * Feign client for accessing 3cp service.
 */
@FeignClient(value = "${feign.threecPayment.name:threecPayment}", url = "${feign.threecPayment.url:http://localhost:9001}",
        fallbackFactory = Payment3CPFallbackFactory.class)
public interface Payment3CP {
    @PostMapping(value = "/tokens",
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    CreateTokenResponse createToken(@Valid @RequestBody CreateTokenRequest createTokenRequest);
}
