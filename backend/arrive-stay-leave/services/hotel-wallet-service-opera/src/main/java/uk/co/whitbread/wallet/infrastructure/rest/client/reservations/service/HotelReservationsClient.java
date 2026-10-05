package uk.co.whitbread.wallet.infrastructure.rest.client.reservations.service;

import static uk.co.whitbread.wallet.infrastructure.rest.client.config.WalletConstants.ARRIVAL_DATE;
import static uk.co.whitbread.wallet.infrastructure.rest.client.config.WalletConstants.CHANNEL;
import static uk.co.whitbread.wallet.infrastructure.rest.client.config.WalletConstants.COUNTRY;
import static uk.co.whitbread.wallet.infrastructure.rest.client.config.WalletConstants.LANGUAGE;
import static uk.co.whitbread.wallet.infrastructure.rest.client.config.WalletConstants.LAST_NAME;
import static uk.co.whitbread.wallet.infrastructure.rest.client.config.WalletConstants.MOBILE;
import static uk.co.whitbread.wallet.infrastructure.rest.client.config.WalletConstants.RESERVATION_NUMBER;
import static uk.co.whitbread.wallet.infrastructure.rest.client.config.WalletConstants.SUBCHANNEL;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.generated.models.reservation.FindBookingResponseDto;
import uk.co.whitbread.hotel.generated.models.reservation.ReservationByBasketRefResponseDto;
import uk.co.whitbread.wallet.domain.model.in.WalletRequest;
import uk.co.whitbread.wallet.infrastructure.exceptions.HotelReservationException;
import uk.co.whitbread.wallet.infrastructure.rest.client.reservations.service.properties.HotelReservationsProperties;
import uk.co.whitbread.wallet.infrastructure.rest.utils.WebClientUtils;

@Component
@Slf4j
public class HotelReservationsClient {

  private final WebClient reservationWebClient;
  private final HotelReservationsProperties hotelReservationsProperties;

  public HotelReservationsClient(@Qualifier("reservationWebClient") WebClient reservationWebClient,
      HotelReservationsProperties hotelReservationsProperties) {
    this.reservationWebClient = reservationWebClient;
    this.hotelReservationsProperties = hotelReservationsProperties;
  }

  public FindBookingResponseDto getBasketReference(WalletRequest walletRequest) {
    return reservationWebClient.get().uri(
            uriBuilder -> uriBuilder.path(hotelReservationsProperties.getBasketReferenceEndpoint())
                .queryParam(RESERVATION_NUMBER, walletRequest.getReservationNumber())
                .queryParam(ARRIVAL_DATE, walletRequest.getArrivalDate())
                .queryParam(LAST_NAME, walletRequest.getLastName())
                .queryParam(LANGUAGE, walletRequest.getLanguage())
                .queryParam(COUNTRY, walletRequest.getCountry())
                .queryParam(SUBCHANNEL, MOBILE)
                .queryParam(CHANNEL, walletRequest.getChannel()).build()).retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorStatusAndHeaders(log, response);
          return response.bodyToMono(HotelReservationException.class);
        })
        .bodyToMono(FindBookingResponseDto.class)
        .doOnError(e -> ExceptionLogger.log(log, e,
            String.format(
                "Error while trying to get basket reference from hotel reservation service for reservation number=%s",
                walletRequest.getReservationNumber())))
        .block();
  }

  public ReservationByBasketRefResponseDto getReservationDetails(String basketReference) {
    return reservationWebClient.get().uri(uriBuilder -> uriBuilder.path(
            hotelReservationsProperties.getReservationEndpoint() + basketReference).build()).retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorStatusAndHeaders(log, response);
          return response.bodyToMono(HotelReservationException.class);
        })
        .bodyToMono(ReservationByBasketRefResponseDto.class)
        .doOnError(e -> ExceptionLogger.log(log, e, String.format(
            "Error while trying to get reservation details from hotel reservation service for basket reference=%s",
            basketReference)))
        .block();
  }
}
