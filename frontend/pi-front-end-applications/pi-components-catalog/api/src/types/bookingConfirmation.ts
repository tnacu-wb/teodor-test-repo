import { AccessibleRoom, Address } from './graphql';
import { ReservationOverrideReasons } from './override';

export interface BCRoomStay {
  adultsNumber: number;
  arrivalDate: string;
  childrenNumber: number;
  departureDate: string;
  ratePlanCode: string;
  roomType: string;
  cot: boolean;
  roomPrice: number;
  ratesPerNight: BCRatePerNight[];
  rateExtraInfo: BCRateExtraInformation;
  roomExtraInfo: BCRoomTypeExtraInformation;
  accessibleRoom: AccessibleRoom;
  checkInTime: string;
  checkOutTime: string;
}

export interface BCRatePerNight {
  pricePerNight: number;
  arrivalDate: string;
}

export interface BCGuest {
  givenName: string;
  surName: string;
  nameTitle: string;
}

export interface BCRateExtraInformation {
  rateName: string;
  rateDescription: string;
  rateLongDescription: string;
}

export interface BCRoomTypeExtraInformation {
  roomType: string;
  roomName: string;
  roomDescription: string;
}

export interface BCAmounts {
  amount: number;
  currencyCode: string;
}

export interface BCDepositPolicies {
  policyCode: string;
  amountPaid: BCAmounts;
  amountDue: BCAmounts;
}

export interface BCReservationListItem {
  reservationId: string;
  additionalGuestInfo: GuestInfos;
  roomStay: BCRoomStay;
  reservationGuestList: BCGuest[];
  reservationPackageList: BCReservationPackageListItem[];
  depositPolicies: BCDepositPolicies[];
  billing: ReservationBilling;
  paymentCard: ReservationPaymentCardInfo;
  reservationOverrideReasons?: ReservationOverrideReasons;
  reservationOverridden: string;
  guaranteeCode: string;
  reservationStatus: string;
}

export interface GuestInfos {
  purposeOfStay: string;
}

export interface BCReservationPackageListItem {
  computedPrice: number;
  description: string;
  totalQuantity: number;
  unitPrice: number;
}

export interface UpgradeToFlex {
  amount: null | number;
  currency: null | string;
  flexRateCode: null | string;
}

export interface BookingSpinnerConfig {
  order: string;
  seconds: string;
  text: string;
}

export interface BookingConfirmation {
  balanceOutstanding: string;
  bookingFlowId: string;
  currencyCode: string;
  hotelId: string;
  hotelName: string;
  infoMessages: string[];
  newTotal: string;
  policyCode: string;
  totalCost: string;
  previousTotal: string;
  reservationByIdList: BCReservationListItem[];
  upgradeToFlex: UpgradeToFlex;
  bookingSpinnerConfig: BookingSpinnerConfig[];
  rateMessage?: string;
  bookingReference?: string;
  basketReference?: string;
  cityTaxTotal?: number;
}

export interface BCResponse {
  bookingConfirmation: BookingConfirmation;
}

export interface BCAuthResponse {
  bookingConfirmationAuthenticated: BookingConfirmation;
}

export interface BCQueryInput {
  basketReference: string;
  language: string;
  country: string;
  bookingChannel: string;
  flow?: string;
}

export interface BCAuthQueryInput {
  bookingReference: string;
  language: string;
  country: string;
  bookingChannel: string;
}

export interface ReservationBilling {
  address: Address;
  email: string;
  firstName: string;
  lastName: string;
  telephone: string;
  title?: string;
  landline?: string;
}

export interface ReservationPaymentCardInfo {
  cardNumberMasked: string;
}
