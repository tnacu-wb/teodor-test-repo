/**
 * address: {
       line1: 'Imperials, Ongar Road',
       line2: 'Cooksmill Green',
       line3: null,
       line4: 'CHELMSFORD',
       line5: null,
       postCode: 'CM1 3SR',
       countryCode: 'GB',
       countryCodeISO: 'GB',
       type: null,
       companyName: null
     }
 */
export interface AddressInfoInputData {
  addressLine1?: string;
  addressLine2?: string;
  addressLine3?: string;
  addressLine4?: string;
  addressLine5?: string;
  country?: string;
}

export class AddressInfoInput {
  [key: string]: unknown;
  addressLine1?: string;
  addressLine2?: string;
  addressLine3?: string;
  addressLine4?: string;
  addressLine5?: string;
  country?: string;

  constructor(data: AddressInfoInputData = {}) {
    Object.assign(this, data);
  }

  static fromRequest(data: AddressInfoInputData): AddressInfoInput {
    return new AddressInfoInput(data);
  }
}
