export class PartialAddressCriteria {
  searchTerm: string;
  countryCode: string;
  constructor(data: any) {
    this.searchTerm = data.searchTerm;
    this.countryCode = data.countryCode;
  }
}
