package uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.exceptions.CdhBookingInvoiceException;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.ReservationInvoicesResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.service.properties.CdhAdapterProperties;
import uk.co.whitbread.booking.infrastructure.rest.utils.WebClientUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Slf4j
@Component
@RequiredArgsConstructor
public class CdhAdapterClient {

  private static final String BOOKING_INVOICES_ACCESSED_BY = "test.user@whitbread.com";
  private static final String BOOKING_INVOICES_ACCESS_CONTEXT = "PI";

  private final CdhAdapterProperties cdhAdapterProperties;
  private final WebClient cdhAdapterWebClient;

  public ReservationInvoicesResponseDto getBookingInvoices(String bookingReference) {

    return cdhAdapterWebClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path(cdhAdapterProperties.getBookingInvoicesEndpoint())
            .queryParam("bookingReference", bookingReference)
            .queryParam("accessedBy", BOOKING_INVOICES_ACCESSED_BY)
            .queryParam("accessContext", BOOKING_INVOICES_ACCESS_CONTEXT)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(CdhBookingInvoiceException.class);
        })
        .bodyToMono(ReservationInvoicesResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            "Error while retrieving reservation details from CDH"))
        .block();
  }

}
