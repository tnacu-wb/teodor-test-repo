export class CancellationPolicies {
  hotelId: string;
  basketRef?: string;
  ratePlanCode?: string;
  arrivalDate?: string;

  constructor(data: any) {
    this.hotelId = data.hotelId;
    this.basketRef = data.basketRef;
    this.ratePlanCode = data.ratePlanCode;
    this.arrivalDate = data.arrivalDate;
  }
}
