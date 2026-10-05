export class AmendConfirmationPricesRequest {
  tempBookingRef: string;
  originalBookingRef: string;
  token: string;

  constructor(data: any) {
    this.tempBookingRef = data.tempBookingRef;
    this.originalBookingRef = data.originalBookingRef;
    this.token = data.token;
  }
}
