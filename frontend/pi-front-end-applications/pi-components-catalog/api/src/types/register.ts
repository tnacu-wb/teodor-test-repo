export type AddressInput = {
  addressLine1: string;
  addressLine2?: string;
  addressLine3?: string;
  addressLine4?: string;
  cityName: string;
  postalCode: string;
  countryCode?: string | number;
  addressPostalCode?: string;
};

export interface RegisterPersonalDetails extends AddressInput {
  title: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  password: string;
  companyName?: string;
  addressSelection: string;
  acceptFutureMailing: boolean;
  googleCaptcha?: string;
}

export type validateRegisterFormParams = {
  t: (id: string) => string;
  currentLang: string | undefined;
  isCompanyNameAdvanceEnabled?: boolean;
};
