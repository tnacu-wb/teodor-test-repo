import {
  AddressGuestInput,
  BCReservationListItem,
  Card,
  PaymentMethod,
  PaymentOption,
  paymentOptions as PaymentType,
  PAYPAL_PAYMENT,
  WALLET_APPLE,
} from '@whitbread-eos/api';
import { v4 as uuidv4 } from 'uuid';

import { getBillingAddress, getDefaultDataFromBooking } from '../getters';

// eslint-disable-next-line @typescript-eslint/no-explicit-any
type AnyRecord = Record<string, any>;

// ── Sub-functions ─────────────────────────────────────────────────────────────

export interface GetBookingForSomeoneElseInput {
  isRemovePIIDataFromLocalStorageEnabled: boolean;
  reservationByIdList: BCReservationListItem[];
  basketReference: string;
  language: string;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  hotelBrand: any;
  isGermanHotel: boolean;
  formData: AnyRecord | null | undefined;
}

/** Resolves whether the booking is on behalf of someone else. */
export function getBookingForSomeoneElse({
  isRemovePIIDataFromLocalStorageEnabled,
  reservationByIdList,
  basketReference,
  language,
  hotelBrand,
  isGermanHotel,
  formData,
}: GetBookingForSomeoneElseInput): boolean {
  if (isRemovePIIDataFromLocalStorageEnabled) {
    const defaultData = getDefaultDataFromBooking(
      reservationByIdList,
      {},
      basketReference,
      language,
      hotelBrand,
      isGermanHotel,
      true
    );
    return defaultData?.bookingForSomeoneElse ?? false;
  }
  return formData?.bookingForSomeoneElse ?? false;
}

/**
 * Builds the sanitised card object for the payment request.
 * Renames `logoSrc` → `logoUrl` and `cardHolderName` → `cardholderName`,
 * and strips `cardNumber` and `cardName` (PII / sensitive data).
 */
export function buildCardFromPaymentMethod(
  card: Card
): Record<string, string | boolean | undefined> {
  const { logoSrc: logoUrl, cardHolderName: cardholderName, ...cardDetails } = card;
  return Object.keys(cardDetails).reduce<Record<string, string | boolean | undefined>>(
    (prev, key) => {
      if (!['cardNumber', 'cardName'].includes(key)) {
        prev[key] = cardDetails[key as keyof typeof cardDetails] as string;
      }
      return prev;
    },
    { logoUrl, cardholderName }
  );
}

// ── Main types & functions ────────────────────────────────────────────────────

export interface BuildPaymentParamsInput {
  /** Per-call payment arguments */
  billingAddress?: AddressGuestInput;
  paymentType?: string;
  paypalNonce?: string;
  paypalDeviceData?: unknown;

  /** Booking context */
  hotelId: string | undefined;
  hotelName: string | undefined;
  arrivalDate: string | undefined;
  departureDate: string | undefined;
  rooms: Array<{ adultsNumber: number; rate: string; type: string }> | undefined;
  reservationByIdList: BCReservationListItem[];
  basketReference: string;
  language: string;
  country: string;

  /** Form / billing */
  billing: AnyRecord | null | undefined;
  formData: AnyRecord | null | undefined;
  isBillingAddressDisplayed: boolean;

  /** Payment selection */
  selectedPaymentType: PaymentMethod;
  selectedPaymentDetail: PaymentOption;

  /** Feature flags */
  isRemovePIIDataFromLocalStorageEnabled: boolean;
  /** Pre-computed result of isSecureBookingPage() */
  isSecureBooking: boolean;

  /** Hotel */
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  hotelBrand: any;
  isGermanHotel: boolean;
}

/** Returns the payment type string used in the payment request. */
export function updateSelectedPaymentOption(
  selectedPaymentType: Pick<PaymentMethod, 'name' | 'type'>
): string {
  if (selectedPaymentType?.name === PaymentType.RESERVE_WITHOUT_CARD) {
    return PaymentType.RESERVE_WITHOUT_CARD;
  }
  return selectedPaymentType.type === 'AP' || selectedPaymentType.type === 'GP'
    ? WALLET_APPLE
    : selectedPaymentType.name;
}

/** Builds the createPaymentCriteria object for all payment flows. */
export function buildPaymentParams({
  billingAddress,
  paymentType,
  paypalNonce,
  paypalDeviceData,
  hotelId,
  hotelName,
  arrivalDate,
  departureDate,
  rooms,
  reservationByIdList,
  basketReference,
  language,
  country,
  billing,
  formData,
  isBillingAddressDisplayed,
  selectedPaymentType,
  selectedPaymentDetail,
  isRemovePIIDataFromLocalStorageEnabled,
  isSecureBooking,
  hotelBrand,
  isGermanHotel,
}: BuildPaymentParamsInput) {
  const bookerIsNotGuest = getBookingForSomeoneElse({
    isRemovePIIDataFromLocalStorageEnabled,
    reservationByIdList,
    basketReference,
    language,
    hotelBrand,
    isGermanHotel,
    formData,
  });

  const billingObj = {
    ...(billing ?? {}),
    landline: undefined,
    address: getBillingAddress({
      countryRouter: country,
      isBillingAddressDisplayed,
      billingAddress,
      billing,
    }),
    differentBillingAddress: isRemovePIIDataFromLocalStorageEnabled
      ? false
      : formData?.billing?.differentBillingAddress,
    bookerIsNotGuest,
  };

  const card = selectedPaymentType?.card
    ? buildCardFromPaymentMethod(selectedPaymentType.card)
    : undefined;

  const bookingRequest = {
    businessSite: {
      identifier: hotelId,
      name: hotelName,
      type: 'HOTEL',
      location: hotelId,
    },
    channel: 'PI',
    journey: 'BOOKING',
    language,
    rooms,
    arrivalDate,
    departureDate,
    type:
      selectedPaymentType?.name === PaymentType.RESERVE_WITHOUT_CARD
        ? PaymentType.RESERVE_WITHOUT_CARD
        : selectedPaymentDetail?.type,
  };

  const paymentRequest = {
    billing: billingObj,
    card,
    environment: window.location.origin,
    subType: PAYPAL_PAYMENT === paymentType ? 'MIT' : 'ECOMM',
    type: updateSelectedPaymentOption(selectedPaymentType),
    pibaCardPresent: false,
    ...(PAYPAL_PAYMENT === paymentType && { paypalNonce, paypalDeviceData }),
  };

  const createPaymentCriteria = {
    booking: bookingRequest,
    payment: paymentRequest,
    hotelId,
    requestId: uuidv4(),
  };

  if (isSecureBooking) {
    return { ...createPaymentCriteria, isSecureBooking: true };
  }
  return createPaymentCriteria;
}
