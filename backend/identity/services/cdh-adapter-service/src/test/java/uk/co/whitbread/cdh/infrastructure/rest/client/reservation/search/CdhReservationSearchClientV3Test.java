package uk.co.whitbread.cdh.infrastructure.rest.client.reservation.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.cdh.domain.model.booking.in.ReservationSearchCriteria;
import uk.co.whitbread.cdh.domain.model.booking.out.ReservationSearch;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.CustomerDataHubClient;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.CdhApiProperties;

@ExtendWith(MockitoExtension.class)
class CdhReservationSearchClientV3Test {

  @InjectMocks
  private CdhReservationSearchClientV3 cdhReservationSearchClientV3;

  @Mock
  private CdhApiProperties cdhApiProperties;

  @Mock
  private CustomerDataHubClient customerDataHubClient;

  @Test
  void getReservationSearch_success() {
    var criteria = ReservationSearchCriteria.builder()
        .lastName("test")
        .emailAddress("test@email.com")
        .bookingsDatabaseSearch(true)
        .build();
    var expectedResponse = ReservationSearch.builder().totalResults(2).build();
    var expectedUrl = "https://cdh-api.test.com/BookingServices/V3/ReservationSearch";

    when(cdhApiProperties.getHost()).thenReturn("https://cdh-api.test.com");
    when(cdhApiProperties.getReservationSearchEndpointV3()).thenReturn("/BookingServices/V3/ReservationSearch");
    when(customerDataHubClient.postCdh(expectedUrl, criteria)).thenReturn(expectedResponse);

    ReservationSearch result = cdhReservationSearchClientV3.getReservationSearch(criteria);

    assertNotNull(result);
    assertEquals(2, result.getTotalResults());
    verify(customerDataHubClient).postCdh(expectedUrl, criteria);
  }

  @Test
  void getReservationSearch_nullResponse() {
    var criteria = ReservationSearchCriteria.builder()
        .lastName("unknown")
        .build();
    var expectedUrl = "https://cdh-api.test.com/BookingServices/V3/ReservationSearch";

    when(cdhApiProperties.getHost()).thenReturn("https://cdh-api.test.com");
    when(cdhApiProperties.getReservationSearchEndpointV3()).thenReturn("/BookingServices/V3/ReservationSearch");
    when(customerDataHubClient.postCdh(expectedUrl, criteria)).thenReturn(null);

    ReservationSearch result = cdhReservationSearchClientV3.getReservationSearch(criteria);

    assertNull(result);
    verify(customerDataHubClient).postCdh(expectedUrl, criteria);
  }
}

