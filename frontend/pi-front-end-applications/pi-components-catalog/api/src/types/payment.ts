import {
  CardStatus,
  CcuiCardType,
  CountryEnum,
  EckohCardPresence,
  EckohCountry,
  EckohPayMethod,
  EckohTimeOfPay,
  LanguageEnum,
} from '../enums';
import type { PaymentMethod, PaymentOption } from './graphql';

export interface BusinessSite {
  identifier: string;
  name: string;
  type: string;
  location: string;
}

export interface GuestRequest {
  name: string;
  previousBookings: number;
  registered: boolean;
  registeredSince: string;
}

export interface RoomType {
  adults?: number;
  rate?: string;
  type?: string;
}

export interface BookingRequest {
  businessSite?: BusinessSite;
  channel?: string;
  journey?: string;
  language?: string;
  rooms?: RoomType[];
  type?: string;
  arrivalDate?: string;
  departureDate?: string;
  leadGuest?: GuestRequest;
}

export type ReservationDetailType = {
  endDate: string;
  ratePlanCode: string | undefined;
};

export type BookingInfoType = {
  hotelId: string;
  adults: number;
  children: number;
  nrNights: number;
  ratePlanCode: string;
  totalCost: string | number;
};

export type BillingAddressFormData = {
  companyName?: string;
  addressLine1: string;
  addressLine2?: string;
  addressLine3?: string;
  addressLine4?: string;
  addressType?: string;
  postalCode: string;
  cityName: string;
  postcodeAddress: string;
  addressSelection: string;
  billingAddressSelection: string;
  countryCode: string;
};

export interface InitiateIframeMutationInterface {
  initiateEckohPayment: { paymentId: string | undefined };
}

export type CardType = {
  type: string;
  name: string;
  logoSrc: string | undefined;
};

export type BusinessAllowancePrice = {
  amount: number;
  currency?: string;
};

export type MaxDinnerBudget = {
  ukWide: BusinessAllowancePrice;
  greaterLondon?: BusinessAllowancePrice;
  ireland?: BusinessAllowancePrice;
};

export type BusinessBookerBookingAllowances = {
  allowAlcohol: boolean;
  allowCarParking: boolean;
  allowAdditionalCosts: boolean;
  allowPremierSaverRates?: boolean;
  allowIndividualCards?: boolean;
  maxNumberOfNights?: number;
  maxDinnerBudgets: MaxDinnerBudget;
};

export type PaymentMethods = {
  paymentMethods: PaymentMethod[];
};

export type MessagesPaymentType = {
  paymentType: string;
  messages: string;
};

export interface RateClassification {
  rateClassification: string;
  rateDescription: string;
  rateName: string;
}

export type BookingType = 'PAY_ON_ARRIVAL' | 'PAY_NOW';

export interface CcuiConfirmBookingBillingAddress {
  lastName: string;
  firstName: string;
  email: string;
  title: string;
  telephone?: string;
  differentBillingAddress?: boolean;
  address: {
    addressLine1: string;
    addressLine2?: string | null;
    addressLine3?: string | null;
    addressLine4?: string | null;
    cityName?: string | null;
    postalCode: string;
    country: string;
    state?: string | null;
    companyName?: string;
    addressType?: string;
  };
  bookerIsNotGuest?: boolean;
}

export interface CcuiConfirmBookingData {
  paymentOption: CcuiCardType;
  subPaymentType: string | null;
  ccuiExtraItems?: {
    sendMail?: boolean | null;
    cardPresent?: boolean | null;
    accountCompanyItems?: {
      companyNumber?: string | null;
      companyId?: string | null;
      charges?: string | null;
      businessItems?: {
        businessAllowances?: {
          allowance?: string;
          budget?: number;
          isAuthorised?: boolean;
        }[];
        customReferenceNumber?: string;
        purchaseOrderNumber?: string;
      } | null;
    };
    nonguaranteedItems?: {
      typeOfCaller?: string | null;
    };
    addressCompanyName?: string | null;
    businessItems?: {
      businessAllowances?: {
        allowance?: string;
        budget?: number;
        isAuthorised?: boolean;
      }[];
      customReferenceNumber?: string;
      purchaseOrderNumber?: string;
    };
  };
  paymentRequest: {
    requestId: number; //random int
    payment: {
      type: string;
      subType: string;
      billing: CcuiConfirmBookingBillingAddress;
      card: {
        cardHolderLastName: string;
        cardHolderFirstName: string;
        cardHolderAddress: {
          addressLine1: string;
          addressLine2?: string | null;
          addressLine3?: string | null;
          addressLine4?: string | null;
          cityName?: string | null;
          postalCode: string;
          country: string;
          addressType?: string;
        };
      };
    };
    booking: {
      type: BookingType;
      journey: string; // BOOKING - by default
      channel: string; // CCC - by default
      language: string;
      businessSite: {
        identifier: string;
        type: string;
        name: string;
        location: string;
      };
    };
  };
}

export type CardHolderType = {
  firstName: string;
  lastName: string;
};

export type BookersReferencesDetailsType = {
  purchaseOrderNumber: string;
  companyReference: string;
};

export class EckohParameters {
  property: string; // hotel name, 50chars 0-9,A-Z,a-z
  cId: string; // client id
  aId: string; // agent email
  lang: LanguageEnum;
  reservationId: string; // reservation id, 50 chars
  timeofpay: EckohTimeOfPay;
  paymethod: EckohPayMethod;
  cardpresence: EckohCardPresence;
  hotelCountry: string;
  merchantId: string; // merchantId used for tokenisation
  env: string; // ccui_(dev/sit/pt/uat/oat/prod/qa)

  constructor(
    hotelName: string,
    clientId: string,
    agentEmail: string,
    language: LanguageEnum,
    reservationId: string,
    timeOfPay: string,
    paymentMethod: CcuiCardType,
    cardPresence: CardStatus,
    hotelCountry: CountryEnum,
    merchantId: string,
    environment: string
  ) {
    this.property = hotelName;
    this.cId = clientId;
    this.aId = agentEmail;
    this.lang = language;
    this.reservationId = reservationId;
    this.merchantId = merchantId;
    this.env = environment;

    switch (timeOfPay) {
      case 'PAY_ON_ARRIVAL':
        this.timeofpay = EckohTimeOfPay.PAY_ON_ARRIVAL;
        break;
      case 'PAY_NOW':
      default:
        this.timeofpay = EckohTimeOfPay.PAY_NOW;
        break;
    }

    switch (paymentMethod) {
      case CcuiCardType.PIBAGB:
        this.paymethod = EckohPayMethod.PIBA_GB;
        break;
      case CcuiCardType.PIBADE:
        this.paymethod = EckohPayMethod.PIBA_DE;
        break;
      case CcuiCardType.NEW_CARD:
      default:
        this.paymethod = EckohPayMethod.CREDIT_CARD_DEBIT_CARD;
        break;
    }

    switch (cardPresence) {
      case CardStatus.CARD_PRESENT:
        this.cardpresence = EckohCardPresence.CARD_PRESENT;
        break;
      case CardStatus.CARD_NOT_PRESENT:
        this.cardpresence = EckohCardPresence.CARD_NOT_PRESENT;
        break;
    }

    switch (hotelCountry) {
      case CountryEnum.GB:
        this.hotelCountry = EckohCountry.GB;
        break;
      case CountryEnum.DE:
        this.hotelCountry = EckohCountry.DE;
        break;
      default:
        this.hotelCountry = EckohCountry.IE;
        break;
    }
  }
}

export type BusinessAllowance = {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  totalDinnerBudgetPersonNight: any;
  isAlcoholDinner: boolean;
  carParking: boolean;
  wifi: boolean;
  additionalCharges: boolean;
};

export type BusinessAllowanceCCUItype = {
  totalDinnerBudgetPersonNight: string;
  isAlcoholDinner: boolean;
  carParking: boolean;
  ultimateWifi: boolean;
  mealDeal: boolean;
  premierInnBreakfast: boolean;
  continentalBreakfast: boolean;
};

export interface DonationPackage {
  code: string;
  currency?: string;
  unitPrice: number;
}

export interface DonationsData {
  donations: Donation;
}

export interface Donation {
  name: string;
  imageSrc: string;
  description: string;
  informationBox: string;
  donationPackages: [DonationPackage];
}

export type RatePlanTotalCost = {
  name: string;
  totalCost: {
    amount: string;
    currency: string;
  };
};

export type TermsAndCondition = {
  termsAndConditions: {
    rate?: string;
    text: string;
  };
};

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
  country?: string;
  addressPostalCode?: string;
  billingAddressSelection?: string;
};

export type CentrallyStoredCardBillingAddress = {
  addressLine1: string;
  addressLine2?: string;
  addressLine3?: string;
  addressLine4?: string;
  country?: string | number;
  postCode: string;
  addressType?: string;
};

export type PaymentError = {
  path: string[];
  data?: null;
  errorType?: string;
  errorInfo?: PaymentErrorInfo;
  locations: {
    line: number;
    column: number;
    sourceName: string | null;
  }[];
  message: string;
};

export type PaymentErrorInfo = {
  errCode: number;
  debugMessage: string;
  globalErrTextTemplate: string;
};
export interface PaymentErrorResponse {
  data: null;
  errors: PaymentError[];
}

export interface PibaConditionalObj {
  [key: string]: string;
}

export type PaymentAnalytics = {
  paymentSessionID?: string;
  paymentTemplateID?: string;
  cardNotPresent?: boolean;
  paymentCardSelected?: string;
  paymentTakenNow?: string;
  cardType?: string;
  echoID?: string;
  paymentLoadTime?: string;
  piba?: string;
  alcoholAllowed?: boolean;
  carParkingAllowed?: boolean;
  wifiAccessAllowed?: boolean;
  dinnerAllowance?: boolean;
  otherChargesAllowed?: boolean;
  pageName?: string;
  paypal?: boolean;
  basketReference?: string | null;
};

export interface PreAuthorisedChargesData {
  id: number | string;
  label: string;
  key: string;
}

export interface PaymentMethodsAvailable extends PaymentMethod {
  paymentOptionsAvailable?: PaymentOption[];
}
