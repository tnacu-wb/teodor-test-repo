import { BookingChannelCriteria } from './booking-channel-criteria';

export interface ConfirmAmendLogicCriteria {
  bookingChannel: BookingChannelCriteria;
  tempBookingRef: string;
  originalBookingRef: string;
  token?: string;
  paymentOptionSelected?: string;
  environment: string;
  emailAddress?: string;
  ccuiExtraItems: CcuiExtraItems;
  paymentRequest: PaymentCcuiRequest;
  paymentOption?: string;
  subPaymentType?: string;
  preCheckIn?: string[];
}

interface CcuiExtraItems {
  sendMail?: boolean;
  cardPresent?: boolean;
  addressCompanyName?: string;
  accountCompanyItems?: AccountCompanyItems;
  nonguaranteedItems?: NonguaranteedItems;
  businessItems?: BusinessItems;
}

interface AccountCompanyItems {
  companyNumber?: string;
  charges?: string;
  companyId?: string;
}

interface PaymentCcuiRequest {
  requestId?: string;
  payment?: PaymentCcui;
}

interface PaymentCcui {
  type: string;
  subType: string;
  card?: CardCcui;
  billing?: BillingAddressCcui;
}

interface CardCcui {
  cardHolderFirstName: string;
  cardHolderLastName: string;
  cardHolderAddress: AddressCcuiRequest;
}

interface AddressCcuiRequest {
  country?: string;
  addressLine1?: string;
  addressLine2?: string;
  addressLine3?: string;
  addressLine4?: string;
  cityName?: string;
  postalCode?: string;
  companyName?: string;
  addressType?: string;
}

interface BillingAddressCcui {
  address: AddressCcuiRequest;
}

interface BusinessItems {
  purchaseOrderNumber: string;
  customReferenceNumber: string;
  businessAllowances?: BusinessAllowance[];
  businessNotes?: string;
}

interface BusinessAllowance {
  budget: number;
  allowance: string;
  isAuthorised: boolean;
}

interface NonguaranteedItems {
  typeOfCaller: string;
}
