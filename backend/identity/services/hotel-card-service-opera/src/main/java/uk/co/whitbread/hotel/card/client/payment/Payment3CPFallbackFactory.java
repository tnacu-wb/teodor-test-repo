package uk.co.whitbread.hotel.card.client.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import uk.co.whitbread.hotel.card.client.payment.model.CreateTokenRequest;
import uk.co.whitbread.hotel.card.client.payment.model.CreateTokenResponse;
import uk.co.whitbread.hotel.card.exceptions.ThreeCPClientException;
import uk.co.whitbread.hotel.card.generated.models.payments.AuthorizeScaRequestDto;
import uk.co.whitbread.hotel.card.generated.models.payments.PaymentResponseDto;
import uk.co.whitbread.hotel.card.generated.models.payments.SaveCardRequestDto;

@Slf4j
@Component
public class Payment3CPFallbackFactory implements FallbackFactory<Payment3CP> {

  @Override
  public Payment3CP create(Throwable throwable) {
    return new Payment3CP() {
      @Override
      public CreateTokenResponse createToken(CreateTokenRequest createTokenRequest) {
        log.warn("Fallback for createToken due to exception: {}", throwable.getMessage());
        throw new ThreeCPClientException(throwable.getMessage());
      }

      @Override
      public PaymentResponseDto initiateSaveCard(SaveCardRequestDto saveCardRequestDto) {
        log.warn("Fallback for initiateSaveCard due to exception: {}", throwable.getMessage());
        throw new ThreeCPClientException(throwable.getMessage());
      }

      @Override
      public PaymentResponseDto initiateAuthorizeSca(AuthorizeScaRequestDto authorizeScaRequestDto) {
        log.warn("Fallback for initiateAuthorizeSca due to exception: {}", throwable.getMessage());
        throw new ThreeCPClientException(throwable.getMessage());
      }
    };
  }
}
