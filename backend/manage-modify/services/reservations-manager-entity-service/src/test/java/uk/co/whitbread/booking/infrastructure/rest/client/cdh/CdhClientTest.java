package uk.co.whitbread.booking.infrastructure.rest.client.cdh;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.booking.domain.model.exceptions.ErrorCode;
import uk.co.whitbread.booking.infrastructure.rest.client.cdh.exceptions.CdhEmptyResponseException;
import uk.co.whitbread.shared.cdh.BookingDataService;
import uk.co.whitbread.shared.cdh.model.bookings.UpcomingBookingsResponse;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CdhClientTest {

  private static final String COMPANY_ID = "companyId";
  private static final String EMPLOYEE_ID = "employeeId";
  private static final String EMAIL = "mail@mail.com";
  private static final String ACCESS_CONTEXT = "InnBusiness";

  @InjectMocks
  private CdhClient cdhClient;

  @Mock
  private BookingDataService bookingDataServiceMock;

  @Test
  void getUpcomingBookings_WhenCalled_ThenRequestIsSentCorrectly() {
    var expectedResponse = UpcomingBookingsResponse.builder().bookings(2).build();
    var expectedResponseOptional = Optional.of(expectedResponse);
    when(bookingDataServiceMock.getUpcomingBookings(COMPANY_ID, EMPLOYEE_ID, EMAIL, ACCESS_CONTEXT))
          .thenReturn(expectedResponseOptional);


    var result = cdhClient.getUpcomingBookings(COMPANY_ID, EMPLOYEE_ID, EMAIL);

    assertEquals(expectedResponse, result);
    verify(bookingDataServiceMock).getUpcomingBookings(COMPANY_ID, EMPLOYEE_ID, EMAIL, ACCESS_CONTEXT);
  }

  @Test
  void getUpcomingBookings_WhenCdhReturnsEmptyResponse_ThenExceptionIsThrown() {
    Optional<UpcomingBookingsResponse> expectedResponseOptional = Optional.empty();
    when(bookingDataServiceMock.getUpcomingBookings(COMPANY_ID, EMPLOYEE_ID, EMAIL, ACCESS_CONTEXT))
          .thenReturn(expectedResponseOptional);

    var result = assertThrows(CdhEmptyResponseException.class,
          () -> cdhClient.getUpcomingBookings(COMPANY_ID, EMPLOYEE_ID, EMAIL));

    assertNotNull(result.getDebugMessage());
    assertTrue(result.getDebugMessage().contains(EMPLOYEE_ID));
    assertTrue(result.getDebugMessage().contains(COMPANY_ID));
    assertEquals(ErrorCode.CDH_EMPTY_RESPONSE_EXCEPTION.getCode(), result.getErrorCode());
    assertEquals(ErrorCode.CDH_EMPTY_RESPONSE_EXCEPTION.getMessage(), result.getGlobalErrTextTemplate());
  }

}