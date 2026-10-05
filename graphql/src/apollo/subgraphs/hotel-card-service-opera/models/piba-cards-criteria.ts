export class PIBACardsCriteria {
  userId: string;
  includeCancelledCards?: boolean;
  showMyCards?: boolean;
  pageNumber?: number;
  maxRows?: number;

  constructor(data: PIBACardsCriteria) {
    this.userId = data.userId;
    this.includeCancelledCards = data.includeCancelledCards;
    this.showMyCards = data.showMyCards;
    this.pageNumber = data.pageNumber;
    this.maxRows = data.maxRows;
  }
}
