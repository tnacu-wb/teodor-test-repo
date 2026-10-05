import { Cell } from './searchBookings';

export type SearchAccountForm = {
  firstName: string;
  lastName: string;
  companyName: string;
  email: string;
  address: string;
  postalCode: string;
  mobileNumber: string;
  landlineNumber: string;
};

export type SearchAccountResult = {
  title: string;
  firstName: string;
  lastName: string;
  companyName: string;
  email: string;
  homePostalCode: string;
  mobile: string;
  landline: string;
  customerAccountId: string;
  companyPostalCode: string;
};

export type SelectedRowType = {
  [key: number]: Cell;
  rowNr: number;
  accountId: string;
} | null;

export type DataForUpdateFormType = {
  title: string;
  firstName: string;
  lastName: string;
  companyName: string;
  email: string;
  address: string;
  postalCode: string;
  mobileNumber: string;
  landlineNumber: string;
};

export type SearchAccountsType = {
  customerAccountId: string;
  contactDetail: {
    title: string;
    firstName: string;
    lastName: string;
    email: string;
    telephone: string;
    mobile: string;
    nationality: string;
    passport: {
      number: string;
      countryOfIssue: string;
    };
    address: {
      line1: string;
      line2: string;
      line3: string;
      line4: string;
      line5: string;
      postCode: string;
      countryCode: string;
      type: string;
      companyName: string;
    };
  };
  paymentPreference: {
    electronicInvoiceRequired: boolean;
  };
  additionalGuests: Array<unknown>;
  bookingPreference: {
    roomRequirements: {
      type: string;
      adults: number;
      children: number;
      cotRequired: boolean;
    };
    reason: string;
    foodPreference: number;
    preselectWifi: boolean;
  };
  guestHistoryNumber: string;
};
