export type AddressGuestInput = {
  addressLine1: string;
  addressLine2?: string;
  addressLine3?: string;
  addressLine4?: string;
  cityName?: string;
  addressType?: string;
  companyName?: string;
  postalCode: string;
  countryCode?: string | number;
  addressPostalCode?: string;
};

export interface GDPersonalDetails extends AddressGuestInput {
  title: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  landline: string;
}

export type GuestDetailsFormStateLocalType = {
  addressLine1: string;
  addressLine2: string;
  addressLine3: string;
  addressLine4: string;
  addressType?: string;
  postcodeAddress: string;
  addressSelection: string;
  cityName: string;
  companyName: string;
  countryCode: string;
  email: string;
  firstName: string;
  landline: string;
  lastName: string;
  manualAddressToggle: string;
  phone: string | number;
  postalCode: string;
  reasonForStay: string;
  title: string;
  basketReferenceId: string;
  updateProfileConsent: boolean;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  leadGuest: any;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  bookingForSomeoneElse: any;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  [key: string]: any;
};

export type GuestDetailsFormStateLocalResetType = {
  addressLine1?: string;
  addressLine2?: string;
  addressLine3?: string;
  addressLine4?: string;
  addressType?: string;
  postcodeAddress?: string;
  addressSelection?: string;
  cityName?: string;
  companyName?: string;
  countryCode?: string;
  email?: string;
  firstName?: string;
  landline?: string;
  lastName?: string;
  manualAddressToggle?: string;
  phone?: string | number;
  postalCode?: string;
  reasonForStay?: string;
  title?: string;
  basketReferenceId?: string;
  [key: string]: string | number | undefined;
};

export type backendDataType = {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  hiData: any;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  rooms: any;
};

export type validateFormParams = {
  t: (id: string) => string;
  bkndData: backendDataType;
  currentLang: string | undefined;
  isRegisterSelected?: boolean;
  isSingleRoomRedesignEnabled?: boolean;
  isMultiRoomRedesignEnabled?: boolean;
  isBookingForSomeoneElse?: boolean;
  isGermanHotel?: boolean;
  isBillingAddressEnabled?: boolean;
  isAdditionalInformationEnabled?: boolean;
  isCompanyNameAdvanceEnabled?: boolean;
  isAccompanyingGuestEnabled?: boolean;
  isConsolidateMobileLandlineEnabled?: boolean;
};

export interface ManualGuest {
  title?: string;
  firstName?: string;
  lastName?: string;
  emailAddress?: string;
}

export interface Guest {
  id: string;
  title: string;
  firstName: string;
  lastName: string;
  emailAddress: string;
  composedName: string;
}

export interface Suggestion {
  employee: Guest;
  prettyFormatDisplay: string;
}

export type TitleDropdownOption = {
  id: string;
  label: string;
};
