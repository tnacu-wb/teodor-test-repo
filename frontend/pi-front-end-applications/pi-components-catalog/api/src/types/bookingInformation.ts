import { ReservationBilling } from './bookingConfirmation';
import type { AccessibleRoom, BookingInformation } from './graphql';
import { AddressGuestInput } from './guestDetails';

export interface BIResponse {
  bookingInformation: BookingInformation;
}

export interface GuestCountUpdateRateCode {
  adultsNumber: number[];
  childrenNumber: number[];
}
export interface BIRoomStay {
  adultsNumber: number;
  arrivalDate: string;
  childrenNumber: number;
  departureDate: string;
  ratePlanCode: string;
  rateName: string;
  roomType: string;
  infoMessages: string[];
  rateExtraInfo: BIRateExtraInformation;
  roomExtraInfo: BIRoomTypeExtraInformation;
  accessibleRoom: AccessibleRoom;
}
export interface BIRateExtraInformation {
  rateName: string;
  rateClassification: string;
  rateOrder: string;
  rateDescription: string;
  rateLongDescription: string;
  rateNotes: string;
}

export interface BIRoomTypeExtraInformation {
  roomType: string;
  roomName: string;
  roomDescription: string;
}
export interface BIGuest {
  givenName: string;
  surName: string;
}

export interface BIReservationGuest {
  givenName: string;
  surName: string;
  address?: AddressGuestInput;
  email?: string;
  isAccompanyingGuest?: boolean;
  nameTitle?: string;
}
export interface BIAmounts {
  amount: number;
  currencyCode: string;
}

export interface BIDepositPolicies {
  policyCode: string;
  amountPaid: BIAmounts;
  amountDue: BIAmounts;
}

export interface BIReservationListItem {
  roomStay: BIRoomStay;
  reservationGuestList: BIGuest[];
  depositPolicies: BIDepositPolicies[];
  billing: ReservationBilling;
  reservationId?: string;
}

export interface BIStorageInterface {
  setItem: (key: string, value: any, additionalParams: any) => Promise<void>;
  getItem: (key: string) => Promise<string | null>;
  removeItem: (key: string) => Promise<void>;
}

export interface BIServerSideCookieOptions {
  httpOnly?: boolean;
  domain?: string;
  maxAge?: number;
}
