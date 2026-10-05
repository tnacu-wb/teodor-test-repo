import type { Address } from '@whitbread-eos/api';

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

export interface BIGuest {
  givenName: string;
  surName: string;
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

export interface BookingInformation {
  currencyCode: string;
  hotelId: string;
  policyCode: string;
  totalCost: string;
  newTotal: string;
  balanceOutstanding: string;
  previousTotal: string;
  reservationByIdList: BIReservationListItem[];
  bookingFlowId: string;
}

export interface BIResponse {
  bookingInformation: BookingInformation;
}

export interface BIQueryInput {
  basketReference: string;
  language: string;
  country: string;
}

export interface ReservationBilling {
  address: Address;
  email: string;
  firstName: string;
  lastName: string;
  telephone: string;
  title: string;
  landline?: string;
}

export interface AccessibleRoom {
  isAccessible: boolean;
  phoneNumber: string;
}

export interface GuestCountUpdateRateCode {
  adultsNumber: number[];
  childrenNumber: number[];
}
