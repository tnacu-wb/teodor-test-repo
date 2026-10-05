import {
  Area,
  BusinessCardType,
  CardName,
  DonationPackage,
  PackageSelection,
  PaymentMethod,
  PiCardSubType,
  PiCardType,
  paymentOptions,
  BASKET_STATUS,
  FT_PI_BB_NON_GUARANTEED_REMINDER,
} from '@whitbread-eos/api';
import { PackagesCriteria, QueryHotelInformationArgs } from '@whitbread-eos/api';

import { validateArrivalDate } from '../validators';

export function displayMethodType(method: PaymentMethod, variant?: Area) {
  const { type, name, card, subType } = method;
  switch (true) {
    case type === PiCardType.NEW_PIBA && name === CardName.PIBA_UK:
      return 'cc.NEW_PIBA_UK';
    case type === PiCardType.NEW_PIBA && name === CardName.PIBA_EU:
      return 'cc.NEW_PIBA_EURO';
    case type === PiCardType.NEW_PIBA && subType === PiCardSubType.PIBA_UK:
      return 'cc.NEW_PIBA';
    case type === PiCardType.NEW_PIBA && subType === PiCardSubType.PIBA_EU:
      return 'cc.NEW_PIBA_EURO';
    case type === PiCardType.ACCOUNT_COMPANY:
      return 'cc.ACCOUNT_TO_COMPANY';
    case type === PiCardType.RESERVE_WITHOUT_CARD:
      return 'paymentOptions.RESERVE_WITHOUT_CARD';
    case type === PiCardType.AMEND_SAVED_CARD:
      return name;
    case type === PiCardType.SAVED_CARD:
      if (variant === Area.PI) {
        return card?.cardName;
      }
      return card?.cardType === BusinessCardType.BUSINESS_PERSONAL_STORED_CARD
        ? 'booking.payment.storedcard.personal'
        : 'booking.payment.storedcard.central';

    case type === PiCardType.PAYPAL_PAYMENT:
      return `booking.payment.paypal`;
    default:
      return `cc.${type}`;
  }
}

export function displayPaymentHelpText(method: PaymentMethod) {
  const { type, card } = method;
  switch (type) {
    case PiCardType.NEW_CARD:
    case PiCardType.NEW_PIBA:
      return 'terms.infoNewCard';
    case PiCardType.SAVED_CARD:
      return card?.cardType === BusinessCardType.BUSINESS_CENTRALLY_STORED_CARD
        ? 'terms.infoSavedPibaCard'
        : 'cc.cvvRequired.info';

    case PiCardType.APGP:
      return `terms.infoAPGP`;
    case PiCardType.PAYPAL_PAYMENT:
      return `terms.infoPayPal`;
    default:
      return `cc.cvvRequired.info`;
  }
}

export function isApplePayConfigured(): boolean {
  // Check if the ApplePaySession object is available

  return (
    window !== undefined &&
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    (window as any).ApplePaySession?.canMakePayments()
  );
}

export async function isGooglePayConfigured(): Promise<boolean> {
  // Check if the PaymentRequest API is available and Google Pay is supported on this device.
  // Uses the same W3C PaymentRequest approach as the Datatrans Payment Button library.
  // Returns a Promise to match the async nature of canMakePayment().
  //
  // allowedAuthMethods: both PAN_ONLY and CRYPTOGRAM_3DS are the only values Google Pay
  // supports — using both is required by the spec and is not a business decision.
  //
  // allowedCardNetworks: intentionally broad (all Datatrans-supported networks) so that
  // canMakePayment() returns true for any Google Pay user regardless of their card brand.
  // This is an availability check only — actual accepted card networks are enforced by
  // Datatrans at transaction time, not here.
  if (typeof window === 'undefined' || !('PaymentRequest' in window)) {
    return false;
  }
  try {
    const request = new PaymentRequest(
      [
        {
          supportedMethods: 'https://google.com/pay',
          data: {
            apiVersion: 2,
            apiVersionMinor: 0,
            allowedPaymentMethods: [
              {
                type: 'CARD',
                parameters: {
                  allowedAuthMethods: ['PAN_ONLY', 'CRYPTOGRAM_3DS'],
                  allowedCardNetworks: ['AMEX', 'DISCOVER', 'MASTERCARD', 'VISA'],
                },
              },
            ],
          },
        },
      ],
      { total: { label: 'Total', amount: { currency: 'GBP', value: '0' } } }
    );
    return await request.canMakePayment();
  } catch {
    return false;
  }
}

export function getDonationForBooking(
  packages: PackageSelection | undefined,
  donationPackages: DonationPackage[] | undefined
) {
  const defaultDonation = {
    unitPrice: 0,
    code: '',
  };
  if (!packages || !donationPackages) {
    return defaultDonation;
  }
  return (
    donationPackages.find((pckg: DonationPackage) => pckg?.code === packages?.id) ?? defaultDonation
  );
}
export function getTotalCost(
  ratePlanCode: string,
  totalCost: string,
  currency: string,
  packages?: PackageSelection,
  donationPackages?: DonationPackage[],
  selectedDonation?: DonationPackage,
  variant?: Area
) {
  const defaultTotalCost = {
    name: ratePlanCode,
    totalCost: { amount: totalCost, currency: currency },
  };

  if (variant === Area.PI) {
    const bookingDonation = getDonationForBooking(packages, donationPackages);

    if (packages && packages?.id !== '' && packages?.id === selectedDonation?.code) {
      return defaultTotalCost;
    } else {
      return {
        name: ratePlanCode,
        totalCost: {
          amount: String(
            +totalCost - (bookingDonation?.unitPrice ?? 0) + (selectedDonation?.unitPrice ?? 0)
          ),
          currency: currency,
        },
      };
    }
  }
  return defaultTotalCost;
}

// Function to sort payment images for tab designc
export function sortImagesByOrder(imageList: { [key: string]: string }) {
  // Sorts payment images by order, currently setting the logic for sort order in FE
  const sortOrder = ['MC', 'VS', 'PP', 'AP', 'GP', 'AX'];
  const sortedImages: { [key: string]: string } = {};
  sortOrder.forEach((key) => {
    if (imageList[key] !== undefined) {
      sortedImages[key] = imageList[key];
    }
  });
  for (const key in imageList) {
    if (sortedImages[key] === undefined) {
      sortedImages[key] = imageList[key];
    }
  }

  return sortedImages;
}

export type secureBookingType = {
  reservationId?: string;
  'secure-booking'?: string;
};

export type PaymentTypes = { RESERVE_WITHOUT_CARD: string; PAY_ON_ARRIVAL: string };

/** Disable reserve without card and default to pay on arrival payment option */
export function applyDefaultPaymentRestrictions(
  paymentOptions: PaymentMethod,
  PaymentType: PaymentTypes,
  query: secureBookingType,
  isSecureBookingFeatureEnabled: boolean
) {
  if (
    !!isSecureBookingFeatureEnabled &&
    !!paymentOptions &&
    !!paymentOptions?.paymentOptions &&
    !!paymentOptions?.paymentOptions?.length &&
    !!query?.reservationId &&
    !!query['secure-booking'] &&
    query['secure-booking'] === 'true'
  ) {
    // Remove RESERVE_WITHOUT_CARD options in-place
    paymentOptions.paymentOptions = paymentOptions.paymentOptions.filter(
      (option) => option.type !== PaymentType.RESERVE_WITHOUT_CARD
    );
    return paymentOptions;
  }
}

export function isSecureBookingPage(query: secureBookingType, isFeatureEnabled: boolean): boolean {
  return (
    !!isFeatureEnabled &&
    !!query?.reservationId &&
    !!query['secure-booking'] &&
    query['secure-booking'] === 'true'
  );
}

export function getBookingConfirmationMessage(
  t: (str: string) => string,
  query: secureBookingType,
  isFeatureEnabled: boolean
): string | undefined {
  const isSecureBooking = isSecureBookingPage(query as secureBookingType, isFeatureEnabled);
  return isSecureBooking
    ? `${t('nonguaranteed.booking.bookingSecure')},`
    : t('booking.confirmation.thankyouForBookingWithoutCustomerName');
}

export interface BookingDataType {
  basketReference: string;
  bookingReference: string;
  arrivalDate: string;
  area: Area;
  paymentOption: string;
  bookingStatus: string;
  hotelId?: string;
  hotelInfo?: {
    hotelId: string;
    bookingFlowId: string;
  };
}

export function shouldDisplaySecureBooking(
  data: BookingDataType,
  hotelBrand: string,
  isFeatureFlagEnabled: boolean
): boolean {
  const { arrivalDate, area, paymentOption, bookingStatus } = data;

  if (
    !isFeatureFlagEnabled ||
    ![Area.PI, Area.BB].includes(area) ||
    bookingStatus === BASKET_STATUS.CANCELLED ||
    !validateArrivalDate(arrivalDate, hotelBrand)
  ) {
    return false;
  }

  switch (paymentOption) {
    case paymentOptions.RESERVE_WITHOUT_CARD:
      return true;
    case paymentOptions.PAY_ON_ARRIVAL:
    case paymentOptions.PAY_NOW:
      return !!(
        bookingStatus === BASKET_STATUS.PAY_PENDING || bookingStatus === BASKET_STATUS.SECURE_FAILED
      );
    default:
      return false;
  }
}

export function getHotelBrand(data: any) {
  const queries = data?.dehydratedState?.queries || [];
  for (const query of queries) {
    const brand = query?.state?.data?.hotelInformation?.brand;
    if (brand) {
      return brand;
    }
  }
  return null;
}

export function getBasketStatus(data: any) {
  const queries = data?.dehydratedState?.queries || [];
  for (const query of queries) {
    const basket = query?.state?.data?.basket;
    if (basket) {
      return basket;
    }
  }
  return null;
}

interface DynamicObject {
  [key: string]: boolean;
}
interface LoadedData {
  hiQueryInput: QueryHotelInformationArgs;
  pcksQueryInput: PackagesCriteria;
  basketReference: string | null;
}

export async function isValidSecureBooking(
  featureToggles: DynamicObject,
  loadedData: LoadedData,
  area: string
) {
  const isSecureBookingFeatureEnabled = featureToggles[FT_PI_BB_NON_GUARANTEED_REMINDER];

  const hotelBrand = getHotelBrand(loadedData);

  const basket = getBasketStatus(loadedData);

  const paymentOption = basket?.paymentOption;
  const basketStatus = basket?.status;
  const secureBookingData = {
    basketReference: loadedData?.pcksQueryInput?.basketReferenceId as string,
    arrivalDate: loadedData.pcksQueryInput?.startDate as string,
    area,
    paymentOption,
    bookingStatus: basketStatus,
    hotelInfo: {
      hotelId: loadedData?.pcksQueryInput?.hotelId,
      bookingFlowId: loadedData?.pcksQueryInput?.bookingFlowId,
    },
  };

  const isBookingValid = shouldDisplaySecureBooking(
    secureBookingData as BookingDataType,
    hotelBrand as string,
    isSecureBookingFeatureEnabled as boolean
  );

  return {
    error: !isBookingValid,
  };
}
