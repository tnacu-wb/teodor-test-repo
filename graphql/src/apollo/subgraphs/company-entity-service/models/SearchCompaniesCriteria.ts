export class SearchCompaniesCriteria {
  companyName?: string;
  hotelId?: string;
  negotiatedRateCompanies?: boolean;
  arNumber?: string;
  offset?: number;
  limit?: number;
  continuationToken?: string;

  constructor(data: any) {
    this.companyName = data.companyName;
    this.hotelId = data.hotelId;
    this.negotiatedRateCompanies = data.negotiatedRateCompanies;
    this.arNumber = data.arNumber;
    this.offset = data.offset;
    this.limit = data.limit;
    this.continuationToken = data.continuationToken;
  }
}
