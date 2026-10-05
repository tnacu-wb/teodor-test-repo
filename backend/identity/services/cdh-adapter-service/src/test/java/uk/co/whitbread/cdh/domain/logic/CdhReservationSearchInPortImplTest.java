package uk.co.whitbread.cdh.domain.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.cdh.domain.model.booking.in.ReservationSearchCriteria;
import uk.co.whitbread.cdh.domain.model.booking.out.ReservationSearch;
import uk.co.whitbread.cdh.domain.ports.secondary.CdhReservationSearchOutPort;

@ExtendWith(MockitoExtension.class)
class CdhReservationSearchInPortImplTest {

  @InjectMocks
  private CdhReservationSearchInPortImpl cdhReservationSearchInPort;
  @Mock
  private CdhReservationSearchOutPort cdhReservationSearchOutPort;

  @Test
  void testGetReservationSearch() {

    when(cdhReservationSearchOutPort.getReservationSearch(ReservationSearchCriteria.builder()
            .bookingReference(null).build())).thenReturn(ReservationSearch.builder()
            .totalResults(1).build());

    var response = cdhReservationSearchInPort.getReservationSearch(ReservationSearchCriteria.builder().bookingReference(null).build());

    assertNotNull(response);
    assertEquals(1, response.getTotalResults().intValue());
  }


  @Test
  void testGetReservationSearchById() {

    when(cdhReservationSearchOutPort.getReservationById("testId")).thenReturn(ReservationSearch.builder()
        .totalResults(1).build());

    var response = cdhReservationSearchInPort.getReservationById("testId");

    assertNotNull(response);
    assertEquals(1, response.getTotalResults().intValue());
  }
}
