package uk.co.whitbread.payment.orchestrator.infrastructure.rest.client.reservation;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.BasketNotFoundException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.DatatransGatewayException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.GatewayException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.ServiceUnavailableException;
import uk.co.whitbread.payment.orchestrator.domain.model.BasketStatus;
import uk.co.whitbread.payment.orchestrator.domain.model.Reservation;
import uk.co.whitbread.payment.orchestrator.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.BasketProperties;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.ReservationProperties;
import uk.co.whitbread.payment.orchestrator.infrastructure.rest.client.ErrorBodyReader;
import uk.co.whitbread.payment.orchestrator.infrastructure.rest.client.reservation.dto.BasketResponse;
import uk.co.whitbread.payment.orchestrator.infrastructure.rest.client.reservation.dto.ReservationByBasketResponse;

/**
 * REST client that retrieves reservation data from the Hotel Reservation Entity Service.
 *
 * <p>Implements {@link BasketOutPort} by calling
 * {@code GET /v1/reservations/basket/{basketReference}} and mapping the response
 * to the {@link Reservation} domain model.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationServiceClient implements BasketOutPort {

  @Qualifier("reservationRestClient")
  private final RestClient reservationRestClient;
  @Qualifier("basketRestClient")
  private final RestClient basketRestClient;
  private final ReservationProperties properties;
  private final BasketProperties basketProperties;
  private final ReservationResponseMapper mapper;

  @Override
  public Reservation getReservation(String basketId) {
    log.info("Fetching reservation for basketId={}", basketId);

    if (basketId == null || basketId.isBlank()) {
      throw new IllegalArgumentException("basketId must not be null or blank");
    }

    try {
      ReservationByBasketResponse response = reservationRestClient.get()
          .uri(properties.getReservationEndpoint(), basketId)
          .accept(MediaType.APPLICATION_JSON)
          .retrieve()
          .onStatus(status -> status.value() == 404, (request, clientResponse) -> {
            throw new BasketNotFoundException("No basket found for ID " + basketId);
          })
          .onStatus(HttpStatusCode::isError, (request, clientResponse) -> {
            log.error("Reservation service error: status={}, body={}",
                clientResponse.getStatusCode(), ErrorBodyReader.read(clientResponse));
            throw new GatewayException(
                "Reservation service returned " + clientResponse.getStatusCode());
          })
          .body(ReservationByBasketResponse.class);

      if (response == null) {
        throw new ServiceUnavailableException(
            "Reservation service returned empty response");
      }

      Reservation reservation = mapper.toDomain(basketId, response);

      if (reservation.bookingReference() == null
          || reservation.bookingReference().isBlank()) {
        throw new DatatransGatewayException(
            "Reservation response missing bookingReference for basket " + basketId);
      }
      if (reservation.totalCostOfStay() == null) {
        throw new DatatransGatewayException(
            "Reservation response missing totalCostOfStay for basket " + basketId);
      }
      if (reservation.currencyCode() == null || reservation.currencyCode().isBlank()) {
        throw new DatatransGatewayException(
            "Reservation response missing currencyCode for basket " + basketId);
      }

      log.info("Reservation retrieved: basketId={}, bookingRef={}, amount={} {}",
          basketId, reservation.bookingReference(),
          reservation.totalCostOfStay(), reservation.currencyCode());

      return reservation;

    } catch (ResourceAccessException e) {
      log.error("Reservation service unreachable for basketId={}: {}",
          basketId, e.getMessage(), e);
      throw new ServiceUnavailableException(
          "Hotel Reservation Entity Service is unreachable: " + e.getMessage());
    }
  }

  @Override
  public BasketStatus getBasketStatus(String basketId) {
    if (basketId == null || basketId.isBlank()) {
      throw new IllegalArgumentException("basketId must not be null or blank");
    }

    try {
      BasketResponse response = basketRestClient.get()
          .uri(basketProperties.getBasketEndpoint(), basketId)
          .accept(MediaType.APPLICATION_JSON)
          .retrieve()
          .onStatus(status -> status.value() == 404, (request, clientResponse) -> {
            throw new BasketNotFoundException("No basket found for ID " + basketId);
          })
          .onStatus(HttpStatusCode::isError, (request, clientResponse) -> {
            log.error("Basket status read failed: status={}, body={}",
                clientResponse.getStatusCode(), ErrorBodyReader.read(clientResponse));
            throw new GatewayException(
                "Basket service returned " + clientResponse.getStatusCode()
                    + " when reading basket " + basketId);
          })
          .body(BasketResponse.class);

      if (response == null) {
        throw new ServiceUnavailableException("Basket service returned empty response");
      }

      // An unrecognised status is not an error here: the workflow treats UNKNOWN as "still
      // pending" and keeps waiting, which is the safe reading of a status we cannot interpret.
      BasketStatus basketStatus = BasketStatus.fromValue(response.status());
      log.info("Basket status retrieved [basketId={}, status={}]", basketId, basketStatus);
      return basketStatus;

    } catch (ResourceAccessException e) {
      log.error("Basket service unreachable [basketId={}]: {}", basketId, e.getMessage(), e);
      throw new ServiceUnavailableException(
          "Basket service is unreachable: " + e.getMessage());
    }
  }

  @Override
  public void changeBasketStatus(String bookingReference, String status) {
    log.info("Changing basket status [bookingReference={}, status={}]",
        bookingReference, status);

    if (bookingReference == null || bookingReference.isBlank()) {
      throw new IllegalArgumentException("bookingReference must not be null or blank");
    }

    try {
      basketRestClient.put()
          .uri(basketProperties.getChangeStatusEndpoint(), bookingReference)
          .contentType(MediaType.APPLICATION_JSON)
          .body(Map.of("status", status))
          .retrieve()
          .onStatus(status2 -> status2.value() == 404, (request, clientResponse) -> {
            throw new BasketNotFoundException(
                "No basket found for bookingReference " + bookingReference);
          })
          .onStatus(HttpStatusCode::isError, (request, clientResponse) -> {
            log.error("Basket status change failed: status={}, body={}",
                clientResponse.getStatusCode(), ErrorBodyReader.read(clientResponse));
            throw new GatewayException(
                "Basket service returned " + clientResponse.getStatusCode()
                    + " when changing status to " + status);
          })
          .toBodilessEntity();

      log.info("Basket status changed successfully [bookingReference={}, status={}]",
          bookingReference, status);

    } catch (ResourceAccessException e) {
      log.error("Basket service unreachable [bookingReference={}, status={}]: {}",
          bookingReference, status, e.getMessage(), e);
      throw new ServiceUnavailableException(
          "Basket service is unreachable: " + e.getMessage());
    }
  }
}
