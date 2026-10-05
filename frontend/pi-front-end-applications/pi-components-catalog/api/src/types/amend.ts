import { Area } from '../enums';
import { PaymentMethod, UserType, PaymentOption, HotelInformation, RoomCodes } from './graphql';
import { SearchRoomOccupancyLimitationsType } from './search';
import { HeaderInformationData } from './staticContent';

export type BookingConfirmationType = {
  bookingFlowId: string;
  hotelId: string;
  hotelName: string;
  currencyCode: string;
  reservationByIdList: AmendReservation[];
  totalCost: number;
  newTotal: number;
  previousTotal: number;
  cityTaxTotal?: number;
  channel: string;
  companyId: number;
};

export type StayDatesType = {
  hotelName: string;
  arrivalDate: Date;
  departureDate: Date;
  maxNights: number;
  maxArrivalDate: number;
  maxRooms: number;
  originalArrivalDate: Date;
  originalDepartureDate: Date;
};

export type StayDatesLabels = {
  arrivalDate: string;
  nightsLabel: string;
  nightOption: string;
  nightsOption: string;
  checkOut: string;
  hotel: string;
  yourStayDatesTitle: string;
  numberOfNightsErrorMessage: string;
  invalidNights: string;
};

export type PrivacyPolicyQueryParamsType = {
  hotelId: string;
  language: string;
  country: string;
  rateCode: string;
};

export type AmendRoomsAndGuestsData = {
  reservations: AmendReservation[];
  currencyCode: string;
};

export type AmendRoomStay = {
  adultsNumber: number;
  childrenNumber: number;
  arrivalDate: string;
  departureDate: string;
  ratePlanCode: string;
  roomExtraInfo: {
    roomName: string;
    roomType: string;
    groupId?: string;
  };
  roomPrice: number;
  accessibleRoom: AmendAccessibleRoom;
};

export interface AmendAccessibleRoom {
  isAccessible: boolean;
  phoneNumber: string;
}

export type AmendRoomDetailsType = {
  children: number;
  adults: number;
  roomType: string;
  operaRoomType: string;
  roomTypeCode: string;
};

export type IsHotelAvailableType = {
  displayNotification: boolean;
  available: boolean;
};

export type AmendRooms = {
  adultsNumber: number;
  childrenNumber: number;
  roomType: string;
  cotRequired: boolean;
};

export type AmendRoomCodesTypes = {
  DB: string;
  DIS: string;
  FAM: string;
  SB: string;
  TWIN: string;
  PB: string;
  RB: string;
  WETTWN: string;
  WETDBL: string;
  LOWDBL: string;
  LOWTWN: string;
  FMQUAD: string;
  FMTRPL: string;
  FMTHRE: string;
  EXTDBL: string;
  PPLDBL: string;
  SINGLE: string;
  DOUBLE: string;
  TWINRM: string;
  ZPLDBL: string;
};

export type AmendHotelAvailabilityData = {
  arrival: string;
  bookingChannel: {
    channel: string;
    language: string;
    subchannel: string;
  };
  country: string;
  departure: string;
  hotelId: string;
  language: string;
  rooms: AmendRooms[];
  rateCode: string;
  ratePlanCodes?: string[] | [];
  channel?: string;
  companyId?: string;
  originalBasketReference?: string;
};

export type ReservationGuestList = {
  givenName: string;
  surName: string;
  nameTitle: string;
  email?: string;
  address: {
    addressLine1?: string;
    addressLine2?: string;
    addressLine3?: string;
    postalCode?: string;
    cityName?: string;
    countryCode?: string;
  };
};

export type ReservationLeadGuestType = {
  title: string;
  firstName: string;
  lastName: string;
  emailAddress?: string;
  addressLine1: string;
  addressLine2?: string;
  addressLine3?: string;
  postalCode?: string;
  city?: string;
  cityName?: string;
  countryCode?: string;
};

export type AmendReservationBilling = {
  address: {
    addressLine1: string;
    addressLine2: string;
    addressLine3: string;
    addressLine4: string;
    companyName?: string;
    country: string;
    postalCode: string;
  };
  email: string;
};

export type AmendReservation = {
  reservationId: string;
  reservationGuestList: ReservationGuestList[];
  roomStay: AmendRoomStay;
  billing: AmendReservationBilling;
  preCheckInStatus?: boolean;
  deRegCardCompleted?: boolean;
};

export type AmendLeadGuestLabels = {
  title: string;
  firstName: string;
  lastName: string;
  email: string;
  guestTitle: string;
  addressLine1: string;
  addressLine2?: string;
  addressLine3?: string;
  postalCode?: string;
  city?: string;
  country?: string;
};

export type AmendRoomAvailabilityLabels = {
  adult: string;
  adults: string;
  child: string;
  children: string;
  addRoom: string;
  guests: string;
  roomAvailable: string;
  roomsUnavailable: string;
  roomsUnavailableDescription: string;
  checkRoomAvailability: string;
  leadGuest: string;
  cancelBtn: string;
  update: string;
  roomSuccessfullyAdded: string;
  roomSuccessfullyUpdated: string;
  cityTaxNotIncluded: string;
};

export type AmendResetNotificationLabels = {
  title: string;
  description: string;
};

export type AmendRoomsAndGuestsLabels = {
  roomModalLabels: AmendRoomModalLabels;
  removeRoomModalLabels: AmendRemoveRoomModalLabels;
  edit: string;
  roomLabel: string;
  remove: string;
};

export type AmendRoomModalLabels = {
  roomDropdownLabels: RoomCodes;
  roomDropdownRoomCodes: RoomCodes;
  roomAvailabilityLabels: AmendRoomAvailabilityLabels;
  leadGuestLabels: AmendLeadGuestLabels;
  leadGuestValidationLabels: AmendLeadGuestValidationLabels;
  notificationLabels: AmendResetNotificationLabels;
};

export type BookingSummaryLabels = {
  title: string;
  confirmChangesLabel: string;
  mealsLabel: string;
  extrasLabel: string;
  previousTotalLabel: string;
  totalCostLabel: string;
  continueToPaymentLabel: string;
  expandDetail: string;
};

export type SummaryOfPaymentsLabels = {
  balancePaid: string;
  payOnArrival: string;
  totalCost: string;
  refund: string;
  refundTerms: string;
  nonRefundable: string;
  balanceAuthorised: string;
  donation: string;
  additionalAmount: string;
};

export type TemporaryBookingConfirmationData = {
  data: BookingConfirmationType | undefined;
  tempBasketIsLoading: boolean;
  tempBasketIsError: boolean;
  tempBasketError: null | { message: string };
};

export type CopyBookingMutationResponse = { copyBooking: { copyBasketReference: string } };

export type AmendRemoveRoomModalLabels = {
  title: string;
  confirmLabel: string;
  notificationLabel: string;
  removeModalRoom: string;
  cancelModalRoom: string;
  roomSuccessfullRemoved: string;
};

export type LeadGuestDetailsType = {
  title: string;
  firstName: string;
  lastName: string;
  emailAddress: string;
  addressLine1: string;
  addressLine2?: string;
  addressLine3?: string;
  postalCode?: string;
  city?: string;
  country?: string;
};

export type AmendRoomType = {
  adultsNumber: number;
  childrenNumber: number;
  roomType: string;
  title: string;
  firstName: string;
  lastName: string;
  emailAddress: string;
  addressLine1: string;
  addressLine2?: string;
  addressLine3?: string;
  postalCode?: string;
  city?: string;
  country?: string;
  cotRequired: boolean;
  specialRequests?: string[];
};

export type AmendLeadGuestValidationLabels = {
  titleError: string;
  firstNameRequiredError: string;
  firstNameMinError: string;
  lastNameRequiredError: string;
  firstNameInvalidError: string;
  lastNameInvalidError: string;
  emailInvalidError: string;
  addressLine1RequiredError?: string;
  addressLine1MaxError?: string;
  postalCodeRequiredErrorGB?: string;
  postalCodeRequiredErrorDE?: string;
  postalCodeInvalidErrorGB?: string;
  postalCodeInvalidErrorDE?: string;
  postalCodeMaxError?: string;
  cityRequiredErrorDE?: string;
  cityMaxErrorDE?: string;
};

export type PaymentOptionsType = {
  payNow: boolean;
  payOnArrival: boolean;
};

export type PaymentCardDetailsType = {
  cardNumberMasked: string;
  token: string;
  expirationDate: string;
  cardType: string;
  cardHolderName: string;
  cardNumberLast4Digits: string;
  cardLogoSrc: string;
  cardName: string;
};

export type SummaryOfPaymentsType = {
  charitable: number;
  previousTotal: number;
  balancePaid: number;
  payOnArrival: number;
  refund: number;
  nonRefundable: number;
  totalCost: number;
  balanceAuthorised: number;
  paymentOptions: PaymentOptionsType;
  paymentCardDetails: PaymentCardDetailsType;
};

export type employeeStayRulesResponseType = {
  globalConfig: {
    maxRoomsLim: {
      maxRooms: number;
      maxRoomsAmend: number;
    };
  };
};

export type stayRulesResponseType = {
  maxNightsLimitation: {
    maxNights: number;
  };
  globalConfig: {
    maxRoomsLim: {
      maxRooms: number;
      maxRoomsAmend: number;
    };
  };
  maxArrivalDateLimitation: {
    maxArrivalDate: number;
  };
};

export type AmendContainerData = {
  bookingConfirmationData: BookingConfirmationType;
  headerInformationData: HeaderInformationData;
  stayRulesData: stayRulesResponseType;
  employeeStayRulesData?: employeeStayRulesResponseType; /// optional as we dont want it in BB
  RoomOccupancyLimitationsData: SearchRoomOccupancyLimitationsType;
  brand: string;
  amendStayDates: {
    amendStayDatesIsError: boolean;
    amendStayDatesIsSuccess: boolean;
    amendStayDatesIsLoading: boolean;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    amendStayDatesMutation: any;
  };
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  addNewRoomMutation: any;
  addNewRoomIsSuccess: boolean;
  addNewRoomIsLoading: boolean;
  amendEditRoom: {
    amendEditRoomIsSuccess: boolean;
    amendEditRoomIsLoading: boolean;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    amendEditRoomMutation: any;
  };
  removeRoom: {
    removeRoomIsSuccess: boolean;
    removeRoomIsLoading: boolean;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    removeRoomMutation: any;
  };
  saveReservation: {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    amendSaveReservationMutation: any;
    amendSaveReservationIsLoading: boolean;
    amendSaveReservationIsSuccess: boolean;
  };
  confirmAmend: {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    confirmAmendMutation: any;
    confirmAmendIsLoading: boolean;
    confirmAmendIsSuccess: boolean;
    confirmAmendIsError: boolean;
  };
  hotelInformation: {
    hotelInformation: HotelInformation;
  };
};

export type AmendCookieData = {
  reservationId: string;
  arrivalDate: string;
  surname: string;
  bookingChannel: string;
  companyId?: string;
  sessionId?: string;
  employeeId?: string;
  accessLevel?: string;
};

export type MealPackageSelection = {
  id: string;
  noOfSelections: number;
};

export type MealRoomSelection = {
  packagesSelection: MealPackageSelection[];
};

export type BBReservationLeadGuestType = {
  title: string;
  firstName: string;
  lastName: string;
  emailAddress: string;
  composedName: string;
  id: string;
};

export type BBLeadGuestDetailsType = {
  bbGuestDetails: BBReservationLeadGuestType[];
};

export type AmendConfInput = {
  basketReference: string;
  bookingReference: string;
  language: string;
  country: string;
  token?: string;
};

export type AnalyticsMeal = {
  isAdded: boolean;
  id: string;
};

export type AmendCookieValue = {
  token: string | null;
  basketReference: string | null;
  bookingReference: string | null;
};
export type AmendConfirmationResponseSuccess = {
  confirmAmendLogic: {
    payment: {
      status: string;
      paymentRequiredDetails: {
        paymentRedirect: string;
      };
    };
  };
};

export type CCUIAmendConfirmationResponseSuccess = {
  confirmAmend: {
    previousTotal: number;
    newTotal: number;
    balanceOutstanding: number;
  };
};

export interface AmendPaymentProps {
  isBillingAddressDisplayed: boolean;
  selectedPaymentDetail: PaymentOption;
  setSelectedPaymentDetail: React.Dispatch<React.SetStateAction<PaymentOption>>;
  selectedPaymentType: PaymentMethod;
  hotelName: string;
  errorMessagePayment: string;
  setSelectedPaymentType: React.Dispatch<React.SetStateAction<PaymentMethod>>;
  basketReference: string;
  variant: Area;
  userType: UserType;
}

export type ConfirmAmendResult = {
  totalCost: number;
  newTotal: number;
  iframeContent: string;
};

export type AMEND_DEPOSIT_FOLIOS_EXCEPTION = 'AMEND_DEPOSIT_FOLIOS_EXCEPTION';
export type AMEND_CONFIRM_EXCEPTION = 'AMEND_CONFIRM_EXCEPTION';
export type AMEND_REFUND_EXCEPTION = 'AMEND_REFUND_EXCEPTION';
export type AMEND_REVERT_EXCEPTION = 'AMEND_REVERT_EXCEPTION';

export type ConfirmAmendError = {
  debugMessage: string;
  errCode: number;
  globalErrTextTemplate:
    | AMEND_DEPOSIT_FOLIOS_EXCEPTION
    | AMEND_CONFIRM_EXCEPTION
    | AMEND_REFUND_EXCEPTION
    | AMEND_REVERT_EXCEPTION;
};

export type AmendConfirmationErrorLS =
  | AMEND_DEPOSIT_FOLIOS_EXCEPTION
  | AMEND_CONFIRM_EXCEPTION
  | AMEND_REFUND_EXCEPTION
  | AMEND_REVERT_EXCEPTION
  | '';
