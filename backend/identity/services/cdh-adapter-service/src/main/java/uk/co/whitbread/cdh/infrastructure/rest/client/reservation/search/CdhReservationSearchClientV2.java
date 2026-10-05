package uk.co.whitbread.cdh.infrastructure.rest.client.reservation.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uk.co.whitbread.cdh.domain.model.booking.in.ReservationSearchCriteria;
import uk.co.whitbread.cdh.domain.model.booking.out.ReservationSearch;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.CustomerDataHubClient;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.CdhApiProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class CdhReservationSearchClientV2 {

  private final CdhApiProperties cdhApiProperties;
  private static final String BOOKING_SERVICES_V2_ENDPOINT = "/BookingServices/V2/";
  private static final String RESERVATION_SEARCH = "ReservationSearch";
  private final CustomerDataHubClient customerDataHubClient;

  public ReservationSearch getReservationSearch(ReservationSearchCriteria reservationSearchCriteria) {

    final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(
            cdhApiProperties.getHost() + BOOKING_SERVICES_V2_ENDPOINT + RESERVATION_SEARCH);

    return customerDataHubClient.postCdh(uriBuilder.build().toUriString(),
            reservationSearchCriteria);
  }
}
