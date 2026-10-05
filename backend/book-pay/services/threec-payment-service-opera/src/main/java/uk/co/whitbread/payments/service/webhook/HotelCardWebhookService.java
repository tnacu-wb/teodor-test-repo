package uk.co.whitbread.payments.service.webhook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.client.HotelCardClient;
import uk.co.whitbread.payments.mapper.PaymentCardMapper;
import uk.co.whitbread.payments.model.PaymentResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelCardWebhookService {

  private final HotelCardClient hotelCardClient;
  private final PaymentCardMapper paymentCardMapper;

  public Mono<ResponseEntity<Void>> saveOrUpdateCard(PaymentResponse paymentResponse) {
    var paymentCardDTO = paymentCardMapper.toPaymentCardDTO(paymentResponse);
    return hotelCardClient.saveOrUpdateCard(paymentCardDTO)
        .doOnSuccess(resp -> log.info("Card saved/updated successfully for payment id: {}", paymentResponse.getPaymentId()))
        .onErrorResume(ex -> {
          log.warn("Error encountered during saving/updating card for payment id {}: {}", paymentResponse.getPaymentId()
              , ex.getMessage());
          return Mono.empty();
        });
  }
}
