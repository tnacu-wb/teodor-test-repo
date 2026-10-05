/**
 * Get the booking flow ID based on the hotel brand for Business Booker.
 * @param {string} brand - The hotel brand (PI, ZIP, PID, HUB)
 * @returns {string} - The corresponding booking flow ID
 */
export function getBookingFlowId(brand: string): string {
  if (brand === 'PID') {
    return 'booking-business-ct';
  }

  if (brand === 'PI' || brand === 'ZIP') {
    return 'booking-business';
  }

  if (brand === 'HUB') {
    return 'booking-business-hub';
  }

  // Default fallback for any other brand
  return 'booking-business';
}
