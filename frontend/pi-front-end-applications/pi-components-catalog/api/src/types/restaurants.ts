export interface BookingConfirmation {
  adultsNumber: number;
  childrenNumber: number;
  departureDate: string;
  firstname: string;
  lastname: string;
  email: string;
  phoneNo: number;
  bookingReference: string;
  specialRequest: string;
}

export interface BCResponse {
  bookingConfirmation: BookingConfirmation;
}
