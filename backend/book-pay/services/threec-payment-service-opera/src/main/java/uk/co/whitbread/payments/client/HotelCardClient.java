package uk.co.whitbread.payments.client;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.exception.ErrorCodes;
import uk.co.whitbread.payments.exception.HotelCardServiceException;
import uk.co.whitbread.payments.model.card.PaymentCardDTO;
import uk.co.whitbread.payments.properties.HotelCardProperties;

@Slf4j
@Service
public class HotelCardClient {

  private final WebClient webClient;
  private final HotelCardProperties hotelCardProperties;

  HotelCardClient(@Qualifier("hotelCard") WebClient webClient, HotelCardProperties hotelCardProperties) {
    this.webClient = webClient;
    this.hotelCardProperties = hotelCardProperties;
  }

  /**
   * Call hotel card service to save/update a card.
   *
   * @param paymentCard request for Opera hotel card service
   */
  @Timed(description = "time taken to call hotel-card-service-opera save/update card", value = "hotel.card.save.card")
  public Mono<ResponseEntity<Void>> saveOrUpdateCard(PaymentCardDTO paymentCard) {

    return webClient
        .put()
        .uri(hotelCardProperties.getSaveCardEndpoint())
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .body(BodyInserters.fromValue(paymentCard))
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse ->
            clientResponse.bodyToMono(String.class)
                .handle((error, sink) -> {
                  log.info(error);
                  sink.error(new HotelCardServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                      "Error encountered during saving payment card.", ErrorCodes.CONNECTION_ERROR));
                }))

        .toBodilessEntity()
        .doOnError(ex -> log.error("Error encountered during saving payment card: {}", ex.getMessage()));
  }
}