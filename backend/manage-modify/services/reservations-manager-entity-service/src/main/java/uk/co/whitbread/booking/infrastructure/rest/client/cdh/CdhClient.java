package uk.co.whitbread.booking.infrastructure.rest.client.cdh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.booking.domain.model.exceptions.ErrorCode;
import uk.co.whitbread.booking.infrastructure.rest.client.cdh.exceptions.CdhEmptyResponseException;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.shared.cdh.BookingDataService;
import uk.co.whitbread.shared.cdh.model.bookings.UpcomingBookingsResponse;

@Component
@Slf4j
@RequiredArgsConstructor
public class CdhClient {

  private static final String ACCESS_CONTEXT = "InnBusiness";
  private final BookingDataService bookingDataService;

  public UpcomingBookingsResponse getUpcomingBookings(String companyId, String employeeId,
        String email) {
    log.info("Retrieve upcoming bookings from CDH for companyId: {}, employeeId:{}.", companyId,
          employeeId);

    return this.bookingDataService
          .getUpcomingBookings(companyId, employeeId, email, ACCESS_CONTEXT)
          .orElseThrow(() -> {
            var exception = new CdhEmptyResponseException(ErrorCode.CDH_EMPTY_RESPONSE_EXCEPTION.getMessage(),
                  String.format(
                        "An empty response has been returned from CDH while querying upcoming "
                              + "bookings for companyId=%s and employeeId=%s.", companyId,
                        employeeId),
                  ErrorCode.CDH_EMPTY_RESPONSE_EXCEPTION.getCode());
            ExceptionLogger.log(log, exception);
            return exception;
          });
  }
}
