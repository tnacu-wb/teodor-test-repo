/**
 * GuestAddress used in Booker object for Create Reservation Guest.
 */
export interface GuestAddressDetails {
  addressType?: string;
  postalCode?: string;
  addressLine1?: string;
  addressLine2?: string;
  addressLine3?: string;
  addressLine4?: string;
  countryCode?: string;
  cityName?: string;
  companyName?: string;
}

export class GuestAddresses {
  private constructor() {}

  static readonly DEFAULT_GUEST_ADDRESS: GuestAddressDetails = {
    addressType: 'HOME',
    postalCode: 'EC1N 2TD',
    addressLine1: 'TestAdressLine1',
    addressLine2: 'TestAdressLine2',
    addressLine3: 'TestAdressLine3',
    addressLine4: '',
    countryCode: 'GB',
    cityName: 'LONDON',
  };

  static readonly DEFAULT_GUEST_ADDRESS_DE: GuestAddressDetails = {
    addressType: 'HOME',
    postalCode: '13231',
    addressLine1: 'TestAdressLine1',
    addressLine2: 'TestAdressLine2',
    addressLine3: 'TestAdressLine3',
    addressLine4: 'BERLIN',
    countryCode: 'DE',
    cityName: 'BERLIN',
  };

  static readonly DEFAULT_GUEST_ADDRESS_IT: GuestAddressDetails = {
    addressType: 'HOME',
    postalCode: '10156',
    addressLine1: 'TestAdressLine1',
    addressLine2: 'TestAdressLine2',
    addressLine3: 'TestAdressLine3',
    addressLine4: 'TORINO',
    countryCode: 'IT',
    cityName: 'TORINO',
  };

  static readonly DEFAULT_GUEST_ADDRESS_AD: GuestAddressDetails = {
    addressType: 'HOME',
    postalCode: 'AD600',
    addressLine1: 'TestAdressLine1',
    addressLine2: 'TestAdressLine2',
    addressLine3: 'TestAdressLine3',
    addressLine4: 'Andorra La Vella',
    countryCode: 'AD',
    cityName: 'Andorra La Vella',
  };

  static readonly DEFAULT_GUEST_ADDRESS_DZ: GuestAddressDetails = {
    addressType: 'HOME',
    postalCode: '10156',
    addressLine1: 'TestAdressLine1',
    addressLine2: 'TestAdressLine2',
    addressLine3: 'TestAdressLine3',
    addressLine4: 'ALGERIS',
    countryCode: 'DZ',
    cityName: 'ALGERIS',
  };

  static readonly DEFAULT_BUSINESS_ADDRESS: GuestAddressDetails = {
    companyName: 'TestCompany',
    addressType: 'BUSINESS',
    postalCode: 'EC1N 2TD',
    addressLine1: 'TestAdressLine1',
    addressLine2: 'TestAdressLine2',
    addressLine3: 'TestAdressLine3',
    addressLine4: 'LONDON',
    countryCode: 'GB',
    cityName: 'LONDON',
  };

  static readonly DEFAULT_BUSINESS_ADDRESS_DE: GuestAddressDetails = {
    companyName: 'TestCompany',
    addressType: 'BUSINESS',
    postalCode: '13231',
    addressLine1: 'TestAdressLine1',
    addressLine2: 'TestAdressLine2',
    addressLine3: 'TestAdressLine3',
    addressLine4: 'BERLIN',
    countryCode: 'DE',
    cityName: 'BERLIN',
  };
}
