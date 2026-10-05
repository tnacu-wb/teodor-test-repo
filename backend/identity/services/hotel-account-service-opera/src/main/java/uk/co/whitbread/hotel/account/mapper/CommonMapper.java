package uk.co.whitbread.hotel.account.mapper;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.account.model.BookingStatus;
import uk.co.whitbread.hotel.account.utils.BookingUtils;

@Mapper(componentModel = "spring")
public interface CommonMapper {

  String CANCELLED = "Cancelled";

  default BookingStatus mapBookingStatus(String bookingStatus, String departureDate) {
    if (StringUtils.isEmpty(bookingStatus)) {
      return null;
    }

    switch (bookingStatus.trim().replaceAll(" ", "").toUpperCase()) {
      case "UNARRIVED":
      case "PREPAID":
      case "FUTURE":
      case "DUEIN":
      case "RESERVED":
        if(BookingUtils.isPastBooking(departureDate)){
          return BookingStatus.PAST;
        }
        return BookingStatus.FUTURE;
      case "REQUESTED":
      case "WAITLISTED":
        return BookingStatus.FUTURE;

      case "ARRIVED":
      case "CHECKEDIN":
      case "INHOUSE":
      case "DUEOUT":
      case "PENDINGCHECKOUT":
        return BookingStatus.CHECKED_IN;

      case "RELEASED":
      case "NOSHOW":
      case "CHECKEDOUT":
      case "PAST":
        return BookingStatus.PAST;

      case "CANCELLED":
        return BookingStatus.CANCELLED;

      default:
        return null;
    }
  }

}
