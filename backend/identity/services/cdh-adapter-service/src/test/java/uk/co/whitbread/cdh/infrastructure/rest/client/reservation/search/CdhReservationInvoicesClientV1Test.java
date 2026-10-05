package uk.co.whitbread.cdh.infrastructure.rest.client.reservation.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.cdh.domain.model.booking.in.ReservationInvoicesRequest;
import uk.co.whitbread.cdh.domain.model.booking.out.ReservationInvoicesResponse;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.CustomerDataHubClient;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.CdhApiProperties;

@ExtendWith(MockitoExtension.class)
class CdhReservationInvoicesClientV1Test {

  private static final String HOST = "http://localhost:8080";
  private static final String BOOKING_REFERENCE = "ABC123";
  private static final String ACCESSED_BY = "tester@example.com";
  private static final String ACCESS_CONTEXT = "PI";
  private static final String EXPECTED_URL =
      "http://localhost:8080/BookingServices/V1/Invoices?Source=DIG";

  @Mock
  private CustomerDataHubClient customerDataHubClient;

  @Mock
  private CdhApiProperties cdhApiProperties;

  @InjectMocks
  private CdhReservationInvoicesClientV1 cdhReservationInvoicesClientV1;

  @Test
  void testGetBookingInvoices() {
    // Arrange
    ReservationInvoicesResponse expectedResponse = ReservationInvoicesResponse.builder()
        .numberOfResults(1)
        .build();

    when(cdhApiProperties.getHost()).thenReturn(HOST);
    when(customerDataHubClient.postCdh(anyString(), any(ReservationInvoicesRequest.class),
        eq(ReservationInvoicesResponse.class), eq(ACCESSED_BY), eq(ACCESS_CONTEXT)))
        .thenReturn(expectedResponse);

    // Act
    ReservationInvoicesResponse response =
        cdhReservationInvoicesClientV1.getBookingInvoices(BOOKING_REFERENCE, ACCESSED_BY,
            ACCESS_CONTEXT);

    // Assert
    assertNotNull(response);
    assertEquals(expectedResponse, response);

    ArgumentCaptor<ReservationInvoicesRequest> requestCaptor =
        ArgumentCaptor.forClass(ReservationInvoicesRequest.class);

    verify(customerDataHubClient).postCdh(eq(EXPECTED_URL), requestCaptor.capture(),
        eq(ReservationInvoicesResponse.class), eq(ACCESSED_BY), eq(ACCESS_CONTEXT));

    ReservationInvoicesRequest capturedRequest = requestCaptor.getValue();
    assertNotNull(capturedRequest);
    assertNotNull(capturedRequest.getInvoiceDetails());
    assertEquals(1, capturedRequest.getInvoiceDetails().size());
    assertEquals(BOOKING_REFERENCE,
        capturedRequest.getInvoiceDetails().getFirst().getBookingReference());
  }
}
