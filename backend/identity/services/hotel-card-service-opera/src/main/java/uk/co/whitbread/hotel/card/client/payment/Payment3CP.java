package uk.co.whitbread.hotel.card.client.payment;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uk.co.whitbread.hotel.card.client.payment.model.CreateTokenRequest;
import uk.co.whitbread.hotel.card.client.payment.model.CreateTokenResponse;
import uk.co.whitbread.hotel.card.generated.models.payments.AuthorizeScaRequestDto;
import uk.co.whitbread.hotel.card.generated.models.payments.PaymentResponseDto;
import uk.co.whitbread.hotel.card.generated.models.payments.SaveCardRequestDto;

/**
 * Feign client for accessing 3cp service.
 */
@FeignClient(value = "${feign.threecPayment.name:threecPayment}", url = "${feign.threecPayment.url}",
    fallbackFactory = Payment3CPFallbackFactory.class)
public interface Payment3CP {
  @PostMapping(value = "/tokens",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  CreateTokenResponse createToken(@Valid @RequestBody CreateTokenRequest createTokenRequest);

  @PostMapping(value = "/payments/cards/iframe",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  PaymentResponseDto initiateSaveCard(@Valid @RequestBody SaveCardRequestDto saveCardRequestDto);

  @PostMapping(value = "/payments/cards/iframe/sca",
          consumes = {MediaType.APPLICATION_JSON_VALUE})
  PaymentResponseDto initiateAuthorizeSca(@Valid @RequestBody AuthorizeScaRequestDto authorizeScaRequestDto);
}
