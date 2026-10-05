export class GuestAddress {
  addressType?: string;
  postalCode?: string;
  addressLine1?: string;
  addressLine2?: string;
  addressLine3?: string;
  addressLine4?: string;
  countryCode?: string;
  cityName?: string;
  companyName?: string;
  addressId?: string;

  constructor(data: any) {
    this.addressType = data.addressType;
    this.postalCode = data.postalCode;
    this.addressLine1 = data.addressLine1;
    this.addressLine2 = data.addressLine2;
    this.addressLine3 = data.addressLine3;
    this.addressLine4 = data.addressLine4;
    this.countryCode = data.countryCode;
    this.cityName = data.cityName;
    this.companyName = data.companyName;
    this.addressId = data.addressId;
  }
}
