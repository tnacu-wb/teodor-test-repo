package uk.co.whitbread.cdh.infrastructure.rest.client.reservation.search;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uk.co.whitbread.cdh.domain.model.booking.in.ReservationInvoicesRequest;
import uk.co.whitbread.cdh.domain.model.booking.in.ReservationInvoicesRequestInvoiceDetail;
import uk.co.whitbread.cdh.domain.model.booking.out.ReservationInvoicesResponse;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.CustomerDataHubClient;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.CdhApiProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class CdhReservationInvoicesClientV1 {

  private static final String BOOKING_SERVICES_V1_ENDPOINT = "/BookingServices/V1/";
  private static final String INVOICES = "Invoices";

  private static final String SOURCE = "Source";
  private static final String SOURCE_DIG = "DIG";

  private final CdhApiProperties cdhApiProperties;
  private final CustomerDataHubClient customerDataHubClient;

  public ReservationInvoicesResponse getBookingInvoices(String bookingReference, String accessedBy,
      String accessContext) {

    final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + BOOKING_SERVICES_V1_ENDPOINT + INVOICES);

    uriBuilder.queryParam(SOURCE, SOURCE_DIG);

    ReservationInvoicesRequest request = ReservationInvoicesRequest.builder()
        .invoiceDetails(List.of(
            ReservationInvoicesRequestInvoiceDetail.builder()
                .bookingReference(bookingReference)
                .build()
        ))
        .build();

    return customerDataHubClient.postCdh(
        uriBuilder.build().toUriString(),
        request,
        ReservationInvoicesResponse.class,
        accessedBy,
        accessContext);
  }
}
