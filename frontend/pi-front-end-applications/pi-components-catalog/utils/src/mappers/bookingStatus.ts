import { BC_RESERVATION_STATUS, BOOKING_TYPE } from '@whitbread-eos/api';
import { format } from 'date-fns';

export function mappingBookingStatus(bookingStatus: string, departureDate: string) {
  switch (bookingStatus) {
    case BC_RESERVATION_STATUS.PREPAID:
    case BC_RESERVATION_STATUS.ARRIVED:
    case BC_RESERVATION_STATUS.CHECKEDIN:
    case BC_RESERVATION_STATUS.FUTURE:
      if (departureDate < format(new Date(), 'yyyy-MM-dd')) {
        return BOOKING_TYPE.PAST;
      }
      return BOOKING_TYPE.UPCOMING;
    case BC_RESERVATION_STATUS.UNARRIVED:
    case BC_RESERVATION_STATUS.RESERVED:
      return BOOKING_TYPE.UPCOMING;
    case BC_RESERVATION_STATUS.NOSHOW:
    case BC_RESERVATION_STATUS.RELEASED:
    case BC_RESERVATION_STATUS.PAST:
      return BOOKING_TYPE.PAST;
    case BC_RESERVATION_STATUS.CANCELLED:
      return BOOKING_TYPE.CANCELLED;
    default:
      return '';
  }
}
