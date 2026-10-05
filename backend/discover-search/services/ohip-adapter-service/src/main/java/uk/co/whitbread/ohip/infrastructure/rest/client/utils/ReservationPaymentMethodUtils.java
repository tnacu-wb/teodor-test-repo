package uk.co.whitbread.ohip.infrastructure.rest.client.utils;

import lombok.experimental.UtilityClass;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.properties.ReservationOhipProperties;

@UtilityClass
public class ReservationPaymentMethodUtils {
  /**
   * This method determines if the payment method of a reservation from indicated hotel should be changed or remain the
   * same.
   * If the return flag is TRUE then the payment method of Reservation for specified hotel must be set to NON-digital.
   * In this case, it allows the front desk (using Opera UI) to perform refunds and payments. If the return flag is
   * FALSE, then the payment method must remain as it is (digital), unchanged.
   *
   * @param hotelId The hotel Id that must be examined if its reservation payment type must be changed or not.
   * @return a flag that indicates if payment type must be changed or not.
   */
  public static boolean hasHotelPaymentMethodNonDigital(ReservationOhipProperties reservationOhipProperties,
                                                        String hotelId) {
    var nonDigitalPaymentHotels = reservationOhipProperties.getNonDigitalPaymentHotels();
    return nonDigitalPaymentHotels.contains(hotelId.toUpperCase()) || nonDigitalPaymentHotels.isEmpty();
  }
}
