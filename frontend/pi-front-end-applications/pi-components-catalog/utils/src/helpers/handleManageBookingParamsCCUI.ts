const REPEAT_BOOKING_PATH = 'repeat-booking';
const MANAGE_BOOKING_PATH = 'bookings';
export function handleManageBookingParamsCCUI() {
  if (
    !window?.location?.pathname?.includes(REPEAT_BOOKING_PATH) &&
    !window?.location?.pathname?.includes(MANAGE_BOOKING_PATH)
  ) {
    sessionStorage?.removeItem('ccuiPrevSearchCriteria');
  }
}
