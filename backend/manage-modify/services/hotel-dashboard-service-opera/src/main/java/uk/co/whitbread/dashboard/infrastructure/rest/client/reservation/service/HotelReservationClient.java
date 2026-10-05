package uk.co.whitbread.dashboard.infrastructure.rest.client.reservation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.dashboard.infrastructure.rest.client.exception.HotelReservationException;
import uk.co.whitbread.dashboard.infrastructure.rest.client.reservation.model.FindBookingResponseDto;
import uk.co.whitbread.dashboard.infrastructure.rest.client.reservation.model.ManageBookingResponseDto;
import uk.co.whitbread.dashboard.infrastructure.rest.client.reservation.service.properties.HotelReservationProperties;

@Component
@RequiredArgsConstructor
@Slf4j
public class HotelReservationClient {

  public static final String PI_CHANNEL = "PI";
  public static final String MOBILE_SUBCHANNEL = "MOBILE";

  private final WebClient hotelReservationWebClient;
  private final HotelReservationProperties hotelReservationProperties;

  public FindBookingResponseDto findBooking(final String lastName, final LocalDate arrivalDate,
      final String bookingReference, final String language) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("resNo", bookingReference);
    queryParams.add("arrivalDate", String.valueOf(arrivalDate));
    queryParams.add("lastName", lastName);
    queryParams.add("channel", PI_CHANNEL);
    queryParams.add("subchannel", MOBILE_SUBCHANNEL);
    queryParams.add("language", language);

    return hotelReservationWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(hotelReservationProperties.getFindBookingEndpoint()).queryParams(queryParams)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(HotelReservationException.class))
        .bodyToMono(FindBookingResponseDto.class)
        .doOnError(
            e -> ExceptionLogger.log(log, e, String.format("Error while trying to find booking for lastName = %s, "
                    + "arrivalDate = %s, bookingReference = %s and language = %s",
                lastName, arrivalDate, bookingReference, language)))
        .block();
  }

  public ManageBookingResponseDto getManageBookingInfo(final String basketReference, final String hotelId,
      final String token, final String language, LocalDateTime userDateTime) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("basketReference", basketReference);
    queryParams.add("hotelId", hotelId);
    queryParams.add("token", token);
    queryParams.add("channel", PI_CHANNEL);
    queryParams.add("subchannel", MOBILE_SUBCHANNEL);
    queryParams.add("language", language);
    queryParams.add("userDateTime", getZoneFormattedDateTime(userDateTime));

    return hotelReservationWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(hotelReservationProperties.getCancelInformationEndpoint())
            .queryParams(queryParams).build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(HotelReservationException.class))
        .bodyToMono(ManageBookingResponseDto.class)
        .doOnError(
            e -> ExceptionLogger.log(log, e,
                String.format("Error while trying to get booking management information for basket = %s, "
                    + "hotelId = %s and language = %s", basketReference, hotelId, language)))
        .block();
  }

  private static String getZoneFormattedDateTime(LocalDateTime userDateTime) {
    ZonedDateTime zonedDateTime = userDateTime.atZone(ZoneId.systemDefault());
    return zonedDateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
  }
}
