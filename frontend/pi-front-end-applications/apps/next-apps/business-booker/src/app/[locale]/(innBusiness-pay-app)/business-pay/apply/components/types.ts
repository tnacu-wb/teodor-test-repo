import { Scheme, AddressInfo } from '@whitbread-eos/api';

export enum PayApplicationStep {
  LANDING = 'LANDING',
  YOUR_DETAILS = 'YOUR_DETAILS',
  COMPANY_DETAILS_BUSINESS_TYPE = 'COMPANY_DETAILS_BUSINESS_TYPE',
  COMPANY_DETAILS = 'COMPANY_DETAILS',
  COMPANY_DETAILS_ADDITIONAL_DETAILS = 'COMPANY_DETAILS_ADDITIONAL_DETAILS',
  CARD_DETAILS = 'CARD_DETAILS',
  CARD_DETAILS_ADD_CARD = 'CARD_DETAILS_ADD_CARD',
  PAYMENT_DETAILS = 'PAYMENT_DETAILS',
  PAYMENT_DETAILS_DIRECT_DEBIT = 'PAYMENT_DETAILS_DIRECT_DEBIT',
  SUMMARY = 'SUMMARY',
  APPLICATION_SENT = 'APPLICATION_SENT',
  APPLICATION_SENT_FAILED = 'APPLICATION_SENT_FAILED',
}

export type PayApplicationState = {
  hostedPageGuid: string;
  directDebitOption: string;
  applicationGuid: string;
  applicationId: string;
  accountName: string;
  scheme: Scheme;
  isSubmitted?: boolean;
  contactDetails: {
    title: string;
    foreName: string;
    lastName: string;
    position: string;
    telephone: string;
    mobile: string;
    email: string;
  };
  companyDetails: {
    registrationAddress: AddressInfo | null;
    correspondenceAddress: AddressInfo | null;
    companyType: string;
    charityNumber: string;
    dateOfBirth: { day: string; month: string; year: string };
    timeTradingId: { value: string; displayValue: string } | string;
    nameOfEmployee: {
      titleEmployee: string;
      firstNameEmployee: string;
      lastNameEmployee: string;
    };
    companyRegNum: string;
    parentCompanyName: string;
    partnerDetails: {
      numberOfPartners: string;
      title: string;
      foreName: string;
      lastName: string;
      dateOfBirth: { day: string; month: string; year: string };
    };
    estMonthlySpend: string;
    hotelBrandPolicy: string;
  };
  cardDetails: {
    myCard?: boolean;
    cardName?: string;
    cardOwnerName?: string;
    emailAddress?: string;
    cardGuid?: string;
    creditLimit?: {
      value?: number;
      currencyCode?: string;
    };
  }[];
  participants?: {
    initiator?: boolean;
    participantId?: number;
    delegated?: boolean;
    terms?: boolean;
    directDebit?: boolean;
    email?: string;
    shared?: string;
    name?: string;
  }[];
};
