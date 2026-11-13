/**
 * creates the confirm reservation input object for the api request
 */
export class ConfirmReservationInput {
  [key: string]: unknown;
  hotelId?: string;
  paymentOption?: string;
  reservationId?: string;

  /**
   * Confirm Reservation Input constructor
   * @param data object data
   * @param data.hotelId the id of the hotel
   * @param data.reservationId the id of the created reservation
   * @param data.paymentOption payment option, accepted values: [PAY_ON_ARRIVAL, RESERVE_WITHOUT_CARD, PAY_NOW]
   */
  constructor(data: { hotelId?: string; reservationId?: string; paymentOption?: string } = {}) {
    this.hotelId = data.hotelId;
    this.reservationId = data.reservationId;
    this.paymentOption = data.paymentOption;
  }

  static fromRequest(data: { hotelId?: string; reservationId?: string; paymentOption?: string }): ConfirmReservationInput {
    return new ConfirmReservationInput(data);
  }
}
