import { AddressGuestInput } from '@whitbread-eos/api';

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
};

export type backendDataType = {
  hiData: any;
  rooms: any;
};

export type validateFormParams = {
  t: (id: string) => string;
  bkndData: backendDataType;
  currentLang: string | undefined;
};
