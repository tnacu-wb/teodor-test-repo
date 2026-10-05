package uk.co.whitbread.cdh.infrastructure.rest.client.reservation.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uk.co.whitbread.cdh.domain.model.booking.out.ReservationSearch;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.CustomerDataHubClient;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.CdhApiProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class CdhReservationSearchClientV1 {

  public static final String BOOKING_REFERENCE = "BookingReference";
  private final CdhApiProperties cdhApiProperties;
  private static final String BOOKING_SERVICES_V1_ENDPOINT = "/BookingServices/V1/";
  private static final String RESERVATION_SEARCH = "ReservationSearch";
  private final CustomerDataHubClient customerDataHubClient;

  public ReservationSearch getReservationById(String reservationId) {

    final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(
            cdhApiProperties.getHost() + BOOKING_SERVICES_V1_ENDPOINT + RESERVATION_SEARCH);
    uriBuilder.queryParam(BOOKING_REFERENCE, reservationId);

    return customerDataHubClient.getCdh(uriBuilder.build().toUriString(), ReservationSearch.class);
  }
}
