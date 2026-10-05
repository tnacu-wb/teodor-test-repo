package uk.co.whitbread.cdh.infrastructure.rest.client.reservation.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.cdh.domain.model.booking.in.ReservationSearchCriteria;
import uk.co.whitbread.cdh.domain.model.booking.out.ReservationInvoicesResponse;
import uk.co.whitbread.cdh.domain.model.booking.out.ReservationSearch;
import uk.co.whitbread.cdh.domain.model.feature.FeatureFlag;
import uk.co.whitbread.cdh.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.cdh.domain.ports.secondary.CdhReservationSearchOutPort;

@Slf4j
@Component
@RequiredArgsConstructor
public class CdhReservationSearchOutPortImpl implements CdhReservationSearchOutPort {

  private final CdhReservationSearchClientV1 cdhReservationSearchClientV1;
  private final CdhReservationSearchClientV2 cdhReservationSearchClientV2;
  private final CdhReservationInvoicesClientV1 cdhReservationInvoicesClientV1;
  private final CdhReservationSearchClientV3 cdhReservationSearchClientV3;

  private final UnleashWrapper<FeatureFlag> unleashWrapper;

  @Override
  public ReservationSearch getReservationById(String reservationId) {

    var reservationSearchCriteria = ReservationSearchCriteria.builder()
        .bookingReference(reservationId)
        .bookingsDatabaseSearch(Boolean.FALSE)
        .build();
    var response = unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation())
        ? cdhReservationSearchClientV3.getReservationSearch(reservationSearchCriteria)
        : cdhReservationSearchClientV1.getReservationById(reservationId);

    if (response.getResults() == null
        && response.getTotalResults() == null) {
      response.setTotalResults(0);
      response.setSearchResults(0);
      response.setTotalSize(0);
    }
    return response;
  }

  @Override
  public ReservationSearch getReservationSearch(ReservationSearchCriteria reservationSearchCriteria) {

    var response = unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation())
        ? cdhReservationSearchClientV3.getReservationSearch(reservationSearchCriteria)
        : cdhReservationSearchClientV2.getReservationSearch(reservationSearchCriteria);

    if (response.getResults() == null && response.getTotalResults() == null) {
      response.setTotalResults(0);
      response.setSearchResults(0);
      response.setTotalSize(0);
    }
    return response;
  }

  @Override
  public ReservationInvoicesResponse getBookingInvoices(String bookingReference, String accessedBy,
      String accessContext) {
    ReservationInvoicesResponse response = cdhReservationInvoicesClientV1.getBookingInvoices(
        bookingReference, accessedBy, accessContext);

    if (response == null) {
      return ReservationInvoicesResponse.builder()
          .numberOfResults(0)
          .invoices(null)
          .notFound(null)
          .build();
    }
    return response;
  }

}
