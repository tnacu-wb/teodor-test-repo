package uk.co.whitbread.booking.infrastructure.rest.client.cdh;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.booking.domain.model.upcoming.out.UpcomingBookingsCdhResponse;
import uk.co.whitbread.booking.infrastructure.rest.client.cdh.mapper.UpcomingBookingsCdhResponseMapper;
import uk.co.whitbread.shared.cdh.model.bookings.UpcomingBookingsResponse;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CdhOutPortImplTest {

  private static final String COMPANY_ID = "companyId";
  private static final String EMPLOYEE_ID = "employeeId";
  private static final String EMAIL = "mail@mail.com";

  @InjectMocks
  private CdhOutPortImpl cdhOutPort;

  @Mock
  private CdhClient cdhClientMock;

  @Mock
  private UpcomingBookingsCdhResponseMapper upcomingBookingsCdhResponseMapperMock;

  @Test
  void getUpcomingBookings_WhenCalled_ThenClientAndModelVerified() {

    var upcomingBookingsCdhResponse = UpcomingBookingsCdhResponse.builder().bookings(2).build();
    var upcomingBookingsResponseFromCdh = UpcomingBookingsResponse.builder().bookings(2).build();
    when(cdhClientMock.getUpcomingBookings(COMPANY_ID, EMPLOYEE_ID, EMAIL))
          .thenReturn(upcomingBookingsResponseFromCdh);
    when(upcomingBookingsCdhResponseMapperMock.toModel(upcomingBookingsResponseFromCdh))
          .thenReturn(upcomingBookingsCdhResponse);

    var result = cdhOutPort.getUpcomingBookings(COMPANY_ID,
          EMPLOYEE_ID, EMAIL);

    assertEquals(upcomingBookingsCdhResponse, result);
    verify(cdhClientMock).getUpcomingBookings(COMPANY_ID, EMPLOYEE_ID, EMAIL);
    verify(upcomingBookingsCdhResponseMapperMock).toModel(upcomingBookingsResponseFromCdh);
  }
}