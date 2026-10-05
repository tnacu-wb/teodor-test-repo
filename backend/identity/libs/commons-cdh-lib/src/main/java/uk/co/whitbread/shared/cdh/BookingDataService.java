package uk.co.whitbread.shared.cdh;

import static uk.co.whitbread.shared.cdh.properties.UriPaths.BOOKING_SERVICES_ENDPOINT;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.BOOKING_SERVICES_ENDPOINT_V2;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.RESERVATION_SEARCH;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.UPCOMING_BOOKINGS_ENDPOINT;
import static uk.co.whitbread.shared.cdh.utils.RequestUtils.buildHeaders;
import static uk.co.whitbread.shared.cdh.utils.RequestUtils.queryParamsToMap;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import uk.co.whitbread.shared.cdh.exception.CDHException;
import uk.co.whitbread.shared.cdh.model.GetAccountBookingsQueryParamsV2;
import uk.co.whitbread.shared.cdh.model.GetCustomerAccountBookingsQueryParams;
import uk.co.whitbread.shared.cdh.model.ReservationSearchResponse;
import uk.co.whitbread.shared.cdh.model.bookings.UpcomingBookingsResponse;
import uk.co.whitbread.shared.cdh.properties.CdhApiOauthProperties;
import uk.co.whitbread.shared.cdh.properties.CdhApiProperties;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingDataService {

  private final CustomerDataHubClient cdhClient;
  private final CdhApiProperties cdhApiProperties;
  private final CdhApiOauthProperties cdhApiOauthProperties;

  /**
   * Get customer account booking history.
   *
   * @param queryParams wrapper over query parameters
   * @param accessedBy  information about who is making the request
   * @return list of bookings matching the query criteria
   */
  public Optional<ReservationSearchResponse> getCustomerAccountBookings(
      GetCustomerAccountBookingsQueryParams queryParams, String accessedBy) {

    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + BOOKING_SERVICES_ENDPOINT + RESERVATION_SEARCH);
    builder.queryParams(queryParamsToMap(queryParams));

    return cdhClient.getCDH(builder.build().toUriString(),
        buildHeaders(cdhApiOauthProperties.getBookingSubscriptionKey(), accessedBy),
        ReservationSearchResponse.class);
  }

  /**
   * Get customer account booking history, using V2 Bookings API
   *
   * @param queryParams wrapper over query parameters
   * @param accessedBy  information about who is making the request
   * @return list of bookings matching the query criteria
   */

  public ReservationSearchResponse getAccountBookingsV2(
      GetAccountBookingsQueryParamsV2 queryParams, String accessedBy) {

    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + BOOKING_SERVICES_ENDPOINT_V2 + RESERVATION_SEARCH);

    try {
      return cdhClient.postCDH(builder.build().toUriString(), queryParams,
          buildHeaders(cdhApiOauthProperties.getBookingSubscriptionKey(), accessedBy),
          GetAccountBookingsQueryParamsV2.class, ReservationSearchResponse.class);
    } catch (CDHException e) {
      if (HttpStatus.NOT_FOUND.value() == e.getStatus()) {
        log.info("404 NOT_FOUND status received while fetching booking history from CDH");
      } else {
        throw e;
      }
    }
    return null;
  }

  /**
   * Get upcoming bookings for companyId and employeeId.
   *
   * @param companyId     ID of the company
   * @param employeeId    ID of the employee
   * @param accessedBy    information about who is making the request
   * @param accessContext information about who is making the request
   *
   * @return list of applications for specific user
   */
  public Optional<UpcomingBookingsResponse> getUpcomingBookings(String companyId, String employeeId,
        String accessedBy, String accessContext) {

    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
          cdhApiProperties.getHost() + UPCOMING_BOOKINGS_ENDPOINT);

    return cdhClient.getCDH(
          builder.buildAndExpand(employeeId, companyId).toUriString(),
          buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy, accessContext),
          UpcomingBookingsResponse.class
    );
  }
}
