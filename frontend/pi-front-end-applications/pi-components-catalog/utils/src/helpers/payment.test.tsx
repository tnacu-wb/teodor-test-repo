import {
  BusinessCardType,
  PiCardType,
  CardName,
  PiCardSubType,
  Area,
  type PaymentMethod,
  paymentOptions,
  BASKET_STATUS,
  FT_PI_BB_NON_GUARANTEED_REMINDER,
} from '@whitbread-eos/api';

import { validateArrivalDate } from '../validators';
import {
  displayMethodType,
  getDonationForBooking,
  getTotalCost,
  isApplePayConfigured,
  isGooglePayConfigured,
  displayPaymentHelpText,
  sortImagesByOrder,
  applyDefaultPaymentRestrictions,
  isSecureBookingPage,
  type secureBookingType,
  getBookingConfirmationMessage,
  type BookingDataType,
  shouldDisplaySecureBooking,
  getHotelBrand,
  isValidSecureBooking,
  getBasketStatus,
} from './payment';

jest.mock('../validators');

jest.mock('../logger/queriesLogger', () => {
  return {
    __esModule: true,
    default: jest.fn().mockImplementation(() => ({
      fetchQuery: jest.fn().mockResolvedValue({
        basket: {
          paymentOption: 'RESERVE_WITHOUT_CARD',
          status: 'COMPLETED',
        },
      }),
    })),
  };
});

const mockedValidateArrivalDate = validateArrivalDate as jest.Mock;

describe('displayMethodType', () => {
  it('should return "cc.NEW_PIBA_UK" for a new PIBA UK card', () => {
    const method = {
      type: PiCardType.NEW_PIBA,
      name: CardName.PIBA_UK,
      card: null,
      subType: null,
      order: 1,
      paymentOptions: [],
      enabled: true,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
    };
    expect(displayMethodType(method)).toEqual('cc.NEW_PIBA_UK');
  });

  it('should return "cc.NEW_PIBA_EURO" for a new PIBA EU card', () => {
    const method = {
      type: PiCardType.NEW_PIBA,
      name: CardName.PIBA_EU,
      card: null,
      subType: null,
      order: 1,
      paymentOptions: [],
      enabled: true,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
    };
    expect(displayMethodType(method)).toEqual('cc.NEW_PIBA_EURO');
  });

  it('should return "cc.NEW_PIBA" for a new PIBA UK card with subType', () => {
    const method = {
      type: PiCardType.NEW_PIBA,
      name: null,
      card: null,
      subType: PiCardSubType.PIBA_UK,
      order: 1,
      paymentOptions: [],
      enabled: true,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
    };
    expect(displayMethodType(method)).toEqual('cc.NEW_PIBA');
  });

  it('should return "cc.NEW_PIBA_EURO" for a new PIBA EU card with subType', () => {
    const method = {
      type: PiCardType.NEW_PIBA,
      name: null,
      card: null,
      subType: PiCardSubType.PIBA_EU,
      order: 1,
      paymentOptions: [],
      enabled: true,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
    };
    expect(displayMethodType(method)).toEqual('cc.NEW_PIBA_EURO');
  });

  it('should return "cc.ACCOUNT_TO_COMPANY" for an account to company payment method', () => {
    const method = {
      type: PiCardType.ACCOUNT_COMPANY,
      name: null,
      card: null,
      subType: null,
      order: 1,
      paymentOptions: [],
      enabled: true,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
    };
    expect(displayMethodType(method)).toEqual('cc.ACCOUNT_TO_COMPANY');
  });

  it('should return "cc.NON_GUARANTEED_BOOKING" for a reserve without card payment method', () => {
    const method = {
      type: PiCardType.RESERVE_WITHOUT_CARD,
      name: null,
      card: null,
      subType: null,
      order: 1,
      paymentOptions: [],
      enabled: true,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
    };
    expect(displayMethodType(method)).toEqual('paymentOptions.RESERVE_WITHOUT_CARD');
  });

  it('should return the card name for an amend saved card payment method', () => {
    const method = {
      type: PiCardType.AMEND_SAVED_CARD,
      name: 'Visa',
      card: null,
      subType: null,
      order: 1,
      paymentOptions: [],
      enabled: true,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
    };
    expect(displayMethodType(method)).toEqual('Visa');
  });

  it('should return "booking.payment.storedcard.personal" for a stored card payment method with a personal card', () => {
    const method = {
      type: PiCardType.SAVED_CARD,
      name: null,
      card: {
        cardType: BusinessCardType.BUSINESS_PERSONAL_STORED_CARD,
        token: '',
        expiryMonth: '10',
        expiryYear: '2042',
        type: '',
        logoSrc: '',
        cardHolderName: 'test',
        cnpRequired: false,
      },
      subType: null,
      order: 1,
      paymentOptions: [],
      enabled: true,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
    };
    expect(displayMethodType(method)).toEqual('booking.payment.storedcard.personal');
  });

  it('should return "booking.payment.storedcard.central" for a stored card payment method with a central card', () => {
    const method = {
      type: PiCardType.SAVED_CARD,
      name: null,
      card: {
        cardType: BusinessCardType.BUSINESS_CENTRALLY_STORED_CARD,
        token: '',
        expiryMonth: '10',
        expiryYear: '2042',
        type: '',
        logoSrc: '',
        cardHolderName: 'test',
        cnpRequired: false,
      },
      subType: null,
      order: 1,
      paymentOptions: [],
      enabled: true,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
    };
    expect(displayMethodType(method)).toEqual('booking.payment.storedcard.central');
  });

  it('should return "booking.payment.paypal" for a PayPal payment method', () => {
    const method = {
      type: PiCardType.PAYPAL_PAYMENT,
      name: null,
      card: null,
      subType: null,
      order: 1,
      paymentOptions: [],
      enabled: true,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
    };
    expect(displayMethodType(method)).toEqual('booking.payment.paypal');
  });

  it('should return the payment method type for an unknown payment method', () => {
    const method = {
      type: 'unknown',
      name: null,
      card: null,
      subType: null,
      order: 1,
      paymentOptions: [],
      enabled: true,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
    };
    expect(displayMethodType(method)).toEqual('cc.unknown');
  });
});

describe('isApplePayConfigured', () => {
  it('returns false when ApplePaySession is not available', () => {
    // Mocking window without ApplePaySession
    global.window = {};
    const result = isApplePayConfigured();
    expect(result).toBe(undefined);
  });
  it('returns false when ApplePaySession is available but cannot make payments', () => {
    // Mocking window and creating a mock ApplePaySession that cannot make payments
    global.window = {
      ApplePaySession: {
        canMakePayments: () => false,
      },
    };
    const result = isApplePayConfigured();
    expect(result).toBe(undefined);
  });
});
// Clean up the global window object after the tests
afterAll(() => {
  global.window = undefined;
});

describe('isGooglePayConfigured', () => {
  afterEach(() => {
    // Remove any PaymentRequest mock set during the test
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    delete (window as any).PaymentRequest;
  });

  it('returns false when window is undefined', async () => {
    // PaymentRequest not present on jsdom window by default
    const result = await isGooglePayConfigured();
    expect(result).toBe(false);
  });

  it('returns false when PaymentRequest is not available on window', async () => {
    // Explicitly ensure PaymentRequest is absent
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    delete (window as any).PaymentRequest;
    const result = await isGooglePayConfigured();
    expect(result).toBe(false);
  });

  it('returns true when PaymentRequest is available and canMakePayment resolves to true', async () => {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    (window as any).PaymentRequest = jest.fn().mockImplementation(() => ({
      canMakePayment: jest.fn().mockResolvedValue(true),
    }));
    const result = await isGooglePayConfigured();
    expect(result).toBe(true);
  });

  it('returns false when PaymentRequest is available but canMakePayment resolves to false', async () => {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    (window as any).PaymentRequest = jest.fn().mockImplementation(() => ({
      canMakePayment: jest.fn().mockResolvedValue(false),
    }));
    const result = await isGooglePayConfigured();
    expect(result).toBe(false);
  });

  it('returns false when PaymentRequest constructor throws', async () => {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    (window as any).PaymentRequest = jest.fn().mockImplementation(() => {
      throw new Error('PaymentRequest not supported');
    });
    const result = await isGooglePayConfigured();
    expect(result).toBe(false);
  });

  it('returns false when canMakePayment rejects', async () => {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    (window as any).PaymentRequest = jest.fn().mockImplementation(() => ({
      canMakePayment: jest.fn().mockRejectedValue(new Error('Network error')),
    }));
    const result = await isGooglePayConfigured();
    expect(result).toBe(false);
  });
});

describe('getDonationForBooking function', () => {
  const defaultDonation = { unitPrice: 0, code: '' };
  it('returns default values when packages or donationPackages are undefined', () => {
    const result = getDonationForBooking(undefined, undefined);
    expect(result).toEqual(defaultDonation);
  });
  it('returns default values when no matching donation package is found', () => {
    const packages = { id: 'testId', noOfSelections: 1 };
    const donationPackages = [{ code: 'code', unitPrice: 10 }];
    const result = getDonationForBooking(packages, donationPackages);
    expect(result).toEqual(defaultDonation);
  });
  it('returns the matching donation package', () => {
    const packages = { id: 'testIdv2', noOfSelections: 1 };
    const donationPackages = [{ code: 'testIdv2', unitPrice: 20 }];
    const result = getDonationForBooking(packages, donationPackages);
    expect(result).toEqual({ unitPrice: 20, code: 'testIdv2' });
  });
  it('returns default values when donationPackages is an empty array', () => {
    const packages = { id: 'testIdv2', noOfSelections: 1 };
    const donationPackages = [];
    const result = getDonationForBooking(packages, donationPackages);
    expect(result).toEqual(defaultDonation);
  });
});

describe('getTotalCost function', () => {
  const defaultTotalCost = {
    name: 'ratePlanCode',
    totalCost: { amount: '10', currency: 'EUR' },
  };
  it('returns default total cost when variant is not PI', () => {
    const result = getTotalCost('ratePlanCode', '10', 'EUR');
    expect(result).toEqual(defaultTotalCost);
  });
  it('returns default total cost when variant is PI and packages are undefined', () => {
    const result = getTotalCost(
      'ratePlanCode',
      '10',
      'EUR',
      undefined,
      undefined,
      undefined,
      Area.PI
    );
    expect(result).toEqual(defaultTotalCost);
  });
  it('returns default total cost when selectedDonation matches packages.id', () => {
    const packages = { id: 'testIdv2', noOfSelections: 1 };
    const donationPackages = [{ code: 'testIdv2', unitPrice: 20 }];
    const selectedDonation = { code: 'testIdv2', unitPrice: 30 };
    const result = getTotalCost(
      'ratePlanCode',
      '10',
      'EUR',
      packages,
      donationPackages,
      selectedDonation,
      Area.PI
    );
    expect(result).toEqual(defaultTotalCost);
  });

  it('calculate total cost based on donation adjustments when selectedDonation doesnt match packages.id', () => {
    const packages = { id: 'testIdv2', noOfSelections: 1 };
    const donationPackages = [{ code: 'testIdv2', unitPrice: 20 }];
    const selectedDonation = { code: 'testIdv3', unitPrice: 30 };
    const result = getTotalCost(
      'ratePlanCode',
      '10',
      'EUR',
      packages,
      donationPackages,
      selectedDonation,
      Area.PI
    );
    expect(result).toEqual({ name: 'ratePlanCode', totalCost: { amount: '20', currency: 'EUR' } });
  });
  it('returns default total cost when donationPackages is an empty array', () => {
    const packages = { id: 'testIdv2', noOfSelections: 1 };
    const donationPackages = [];
    const result = getTotalCost(
      'ratePlanCode',
      '10',
      'EUR',
      packages,
      donationPackages,
      undefined,
      Area.PI
    );
    expect(result).toEqual(defaultTotalCost);
  });
});

describe('displayPaymentHelpText function', () => {
  it('should return "terms.infoNewCard" for a new PIBA UK card', () => {
    const method = {
      type: PiCardType.NEW_PIBA,
      name: CardName.PIBA_UK,
      card: undefined,
      subType: undefined,
      order: 1,
      paymentOptions: [],
      enabled: true,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
    };

    expect(displayPaymentHelpText(method)).toEqual('terms.infoNewCard');
  });

  it('should return "terms.infoNewCard" for a new UK card', () => {
    const method = {
      type: PiCardType.NEW_CARD,
      name: CardName.PIBA_UK,
      card: undefined,
      subType: undefined,
      order: 1,
      paymentOptions: [],
      enabled: true,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
    };

    expect(displayPaymentHelpText(method)).toEqual('terms.infoNewCard');
  });

  it('should return "terms.infoSavedPibaCard" for a stored card payment method with a piba or business saved card', () => {
    const method = {
      type: PiCardType.SAVED_CARD,
      name: PiCardType.SAVED_CARD,
      card: {
        cardType: BusinessCardType.BUSINESS_CENTRALLY_STORED_CARD,
        token: '',
        expiryMonth: '10',
        expiryYear: '2042',
        type: '',
        logoSrc: '',
        cardHolderName: 'test',
        cnpRequired: false,
        cardNumber: '111111',
      },
      subType: undefined,
      order: 1,
      paymentOptions: [],
      enabled: true,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
    };
    expect(displayPaymentHelpText(method)).toEqual('terms.infoSavedPibaCard');
  });

  it('should return "cc.cvvRequired.info" for a personal stored card payment method', () => {
    const method = {
      type: PiCardType.SAVED_CARD,
      name: PiCardType.SAVED_CARD,
      card: {
        cardType: BusinessCardType.BUSINESS_PERSONAL_STORED_CARD,
        token: '',
        expiryMonth: '10',
        expiryYear: '2042',
        type: '',
        logoSrc: '',
        cardHolderName: 'test',
        cnpRequired: false,
        cardNumber: '111111',
      },
      subType: undefined,
      order: 1,
      paymentOptions: [],
      enabled: true,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
    };
    expect(displayPaymentHelpText(method)).toEqual('cc.cvvRequired.info');
  });

  it('for a PayPal payment method', () => {
    const method = {
      type: PiCardType.PAYPAL_PAYMENT,
      name: PiCardType.PAYPAL_PAYMENT,
      card: undefined,
      subType: undefined,
      order: 1,
      paymentOptions: [],
      enabled: true,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
    };
    expect(displayPaymentHelpText(method)).toEqual('terms.infoPayPal');
  });

  it('For a APGP payment method', () => {
    const method = {
      type: PiCardType.APGP,
      name: PiCardType.APGP,
      card: undefined,
      subType: undefined,
      order: 1,
      paymentOptions: [],
      enabled: true,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
    };
    expect(displayPaymentHelpText(method)).toEqual('terms.infoAPGP');
  });

  it('should return the payment method text for an unknown payment method', () => {
    const method = {
      type: 'unknown',
      name: 'unknown',
      card: undefined,
      subType: undefined,
      order: 1,
      paymentOptions: [],
      enabled: true,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
    };
    expect(displayPaymentHelpText(method)).toEqual('cc.cvvRequired.info');
  });
});

describe('sortImagesByOrder function', () => {
  const imageList = {
    MC: 'Mastercard.jpg',
    AX: 'AX.jpg',
    VS: 'Visa_Debit.jpg',
    PP: 'paypal-logo-2.png',
    AP: '/content/dam/global/booking/ApplePay.jpg',
    GP: '/content/dam/global/booking/GooglePay.png',
    MT: '/content/dam/global/booking/Maestro.png',
  };

  const sortedList = {
    MC: 'Mastercard.jpg',
    VS: 'Visa_Debit.jpg',
    PP: 'paypal-logo-2.png',
    AP: '/content/dam/global/booking/ApplePay.jpg',
    GP: '/content/dam/global/booking/GooglePay.png',
    AX: 'AX.jpg',
    MT: '/content/dam/global/booking/Maestro.png',
  };

  it('returns sorted images based on sort order', () => {
    const result = sortImagesByOrder(imageList);
    expect(result).toEqual(sortedList);
  });
});

const PaymentType = {
  RESERVE_WITHOUT_CARD: paymentOptions.RESERVE_WITHOUT_CARD,
  PAY_ON_ARRIVAL: paymentOptions.PAY_ON_ARRIVAL,
};

describe('applyDefaultPaymentRestrictions', () => {
  const isSecureBookingFeatureEnabled = true;

  it('should remove RESERVE_WITHOUT_CARD when secure booking is true', () => {
    const mockPaymentOptions = {
      paymentOptions: [
        { type: PaymentType.RESERVE_WITHOUT_CARD, enabled: true },
        { type: PaymentType.PAY_ON_ARRIVAL, enabled: false },
        { type: 'OTHER_TYPE', enabled: true },
      ],
    };

    const query = {
      reservationId: '1234',
      'secure-booking': 'true',
    };

    const isSecureBookingFeatureEnabled = true;

    const result = applyDefaultPaymentRestrictions(
      mockPaymentOptions,
      PaymentType,
      query,
      isSecureBookingFeatureEnabled
    );

    expect(result?.paymentOptions).toEqual([
      { type: PaymentType.PAY_ON_ARRIVAL, enabled: false },
      { type: 'OTHER_TYPE', enabled: true },
    ]);
  });

  it('should return undefined if paymentOptions is missing', () => {
    const result = applyDefaultPaymentRestrictions(
      undefined as any,
      PaymentType,
      {
        reservationId: '1234',
        'secure-booking': 'true',
      },
      isSecureBookingFeatureEnabled
    );
    expect(result).toBeUndefined();
  });

  it('should return undefined if secure-booking is not "true"', () => {
    const mockPaymentOptions = {
      paymentOptions: [
        { type: PaymentType.RESERVE_WITHOUT_CARD, enabled: true },
        { type: PaymentType.PAY_ON_ARRIVAL, enabled: false },
      ],
    };

    const query = {
      reservationId: '1234',
      'secure-booking': 'false',
    };

    const result = applyDefaultPaymentRestrictions(
      mockPaymentOptions as PaymentMethod,
      PaymentType,
      query,
      isSecureBookingFeatureEnabled
    );

    expect(result).toBeUndefined();
  });

  it('should return undefined if reservationId is missing', () => {
    const mockPaymentOptions = {
      paymentOptions: [
        { type: PaymentType.RESERVE_WITHOUT_CARD, enabled: true },
        { type: PaymentType.PAY_ON_ARRIVAL, enabled: false },
      ],
    };

    const query = {
      'secure-booking': 'true',
    };

    const result = applyDefaultPaymentRestrictions(
      mockPaymentOptions as PaymentMethod,
      PaymentType,
      query,
      isSecureBookingFeatureEnabled
    );

    expect(result).toBeUndefined();
  });
});

describe('isSecureBookingPage', () => {
  const isSecureBookingFeatureEnabled = true;
  it('should return true when reservationId and secure-booking=true are present', () => {
    const query: secureBookingType = {
      reservationId: 'ABC123',
      'secure-booking': 'true',
    };
    expect(isSecureBookingPage(query, isSecureBookingFeatureEnabled)).toBe(true);
  });

  it('should return false when secure-booking is not "true"', () => {
    const query: secureBookingType = {
      reservationId: 'ABC123',
      'secure-booking': 'false',
    };
    expect(isSecureBookingPage(query, isSecureBookingFeatureEnabled)).toBe(false);
  });

  it('should return false when secure-booking is missing', () => {
    const query: secureBookingType = {
      reservationId: 'ABC123',
    };
    expect(isSecureBookingPage(query, isSecureBookingFeatureEnabled)).toBe(false);
  });

  it('should return false when reservationId is missing', () => {
    const query: secureBookingType = {
      'secure-booking': 'true',
    };
    expect(isSecureBookingPage(query, isSecureBookingFeatureEnabled)).toBe(false);
  });

  it('should return false when both reservationId and secure-booking are missing', () => {
    const query: secureBookingType = {};
    expect(isSecureBookingPage(query, isSecureBookingFeatureEnabled)).toBe(false);
  });
});

describe('getBookingConfirmationMessage', () => {
  const t = (str) => str;
  it('should return nonguaranteed.booking.bookingSecure, when feature flag is on and query string has secure booking true', () => {
    const query: secureBookingType = {
      reservationId: 'ABC123',
      'secure-booking': 'true',
    };
    const isSecureBookingFeatureEnabled = true;
    expect(getBookingConfirmationMessage(t, query, isSecureBookingFeatureEnabled)).toBe(
      'nonguaranteed.booking.bookingSecure,'
    );
  });

  it('should return booking.confirmation.thankyouForBookingWithoutCustomerName when feature flag is of and query string has secure booking true', () => {
    const query: secureBookingType = {
      reservationId: 'ABC123',
      'secure-booking': 'true',
    };
    const isSecureBookingFeatureEnabled = false;
    expect(getBookingConfirmationMessage(t, query, isSecureBookingFeatureEnabled)).toBe(
      'booking.confirmation.thankyouForBookingWithoutCustomerName'
    );
  });
});

const baseData = {
  arrivalDate: '2025-06-09',
  area: Area.PI,
  paymentOption: PaymentType.RESERVE_WITHOUT_CARD,
  bookingStatus: BASKET_STATUS.PAY_PENDING,
};

const hotelBrand = 'PI';

describe('shouldDisplaySecureBooking', () => {
  beforeEach(() => {
    mockedValidateArrivalDate.mockReset();
  });

  it('returns true when all conditions are met', () => {
    mockedValidateArrivalDate.mockReturnValue(true);

    const result = shouldDisplaySecureBooking(baseData as BookingDataType, hotelBrand, true);
    expect(result).toBe(true);
  });

  it('returns false when feature flag is disabled', () => {
    mockedValidateArrivalDate.mockReturnValue(true);

    const result = shouldDisplaySecureBooking(baseData as BookingDataType, hotelBrand, false);
    expect(result).toBe(false);
  });

  it('returns false when area is not PI or BB', () => {
    mockedValidateArrivalDate.mockReturnValue(true);

    const result = shouldDisplaySecureBooking(
      { ...(baseData as BookingDataType), area: Area.CCUI as Area },
      hotelBrand,
      true
    );
    expect(result).toBe(false);
  });

  it('returns true when basket status is secure failed', () => {
    mockedValidateArrivalDate.mockReturnValue(true);
    baseData.bookingStatus = BASKET_STATUS.SECURE_FAILED;
    const result = shouldDisplaySecureBooking(
      { ...(baseData as BookingDataType), area: Area.PI as Area },
      hotelBrand,
      true
    );
    expect(result).toBe(true);
  });

  it('returns false when paymentOption is not RESERVE_WITHOUT_CARD and bookingStatus is COMPLETED', () => {
    mockedValidateArrivalDate.mockReturnValue(true);

    const result = shouldDisplaySecureBooking(
      {
        ...baseData,
        paymentOption: PaymentType.PAY_ON_ARRIVAL,
        bookingStatus: BASKET_STATUS.COMPLETED,
      } as BookingDataType,
      hotelBrand,
      true
    );
    expect(result).toBe(false);
  });

  it('returns false when paymentOption is not RESERVE_WITHOUT_CARD and bookingStatus is CANCELLED', () => {
    mockedValidateArrivalDate.mockReturnValue(true);

    const result = shouldDisplaySecureBooking(
      {
        ...baseData,
        paymentOption: PaymentType.PAY_ON_ARRIVAL,
        bookingStatus: BASKET_STATUS.CANCELLED,
      } as BookingDataType,
      hotelBrand,
      true
    );
    expect(result).toBe(false);
  });

  it('returns false when validateArrivalDate returns false', () => {
    mockedValidateArrivalDate.mockReturnValue(false);

    const result = shouldDisplaySecureBooking(baseData as BookingDataType, hotelBrand, true);
    expect(result).toBe(false);
  });

  it('returns true if paymentOption is not RESERVE_WITHOUT_CARD but bookingStatus is valid (not COMPLETED or CANCELLED)', () => {
    mockedValidateArrivalDate.mockReturnValue(true);

    const result = shouldDisplaySecureBooking(
      {
        ...baseData,
        paymentOption: PaymentType.PAY_ON_ARRIVAL,
        bookingStatus: BASKET_STATUS.PAY_PENDING,
      } as BookingDataType,
      hotelBrand,
      true
    );
    expect(result).toBe(true);
  });

  it('returns true if paymentOption is RESERVE_WITHOUT_CARD even when bookingStatus is COMPLETED', () => {
    mockedValidateArrivalDate.mockReturnValue(true);

    const result = shouldDisplaySecureBooking(
      {
        ...baseData,
        paymentOption: PaymentType.RESERVE_WITHOUT_CARD,
        bookingStatus: BASKET_STATUS.COMPLETED,
      } as BookingDataType,
      hotelBrand,
      true
    );
    expect(result).toBe(true);
  });

  it('returns false if paymentOption is RESERVE_WITHOUT_CARD and bookingStatus is CANCELLED', () => {
    mockedValidateArrivalDate.mockReturnValue(true);

    const result = shouldDisplaySecureBooking(
      {
        ...baseData,
        paymentOption: PaymentType.RESERVE_WITHOUT_CARD,
        bookingStatus: BASKET_STATUS.CANCELLED,
      } as BookingDataType,
      hotelBrand,
      true
    );
    expect(result).toBe(false);
  });
});

describe('getHotelBrand', () => {
  const data = {
    dehydratedState: {
      queries: [
        {
          state: {
            data: {
              hotelInformation: {
                brand: 'PID',
              },
            },
          },
        },
      ],
    },
  };
  it('returns the hotel brand if present in query', () => {
    const result = getHotelBrand(data);
    expect(result).toBe('PID');
  });

  it('returns the null if not present in query', () => {
    data.dehydratedState.queries[0].state.data.hotelInformation.brand = '';
    const result = getHotelBrand(data);
    expect(result).toBe(null);
  });
});

describe('isValidSecureBooking', () => {
  const loadedData = {
    pcksQueryInput: {
      basketReferenceId: 'GAA-cb51bf9e-9dc6-427d-b1a9-0fb47350e261',
      startDate: new Date().toISOString(),
      hotelId: 'FRAMTI',
      bookingFlowId: 'booking-flow-id',
    },
    dehydratedState: {
      queries: [
        {
          state: {
            data: {
              hotelInformation: {
                hotelBrand: 'PID',
              },
              basket: {
                paymentOption: PaymentType.RESERVE_WITHOUT_CARD,
                status: BASKET_STATUS.PAY_PENDING,
              },
            },
          },
        },
      ],
    },
  };

  const featureToggles = { [FT_PI_BB_NON_GUARANTEED_REMINDER]: true };

  it('should return error false for RESERVE_WITHOUT_CARD and valid booking', async () => {
    const result = await isValidSecureBooking(featureToggles, loadedData as any, 'pi');
    expect(result).toEqual({ error: false });
  });

  it('should return error true when feature flag is disabled', async () => {
    const result = await isValidSecureBooking(
      { [FT_PI_BB_NON_GUARANTEED_REMINDER]: false },
      loadedData as any,
      'pi'
    );
    expect(result).toEqual({ error: true });
  });

  it('should return error true when basket is CANCELLED', async () => {
    const data = {
      ...loadedData,
      dehydratedState: {
        queries: [
          {
            state: {
              data: {
                ...loadedData.dehydratedState.queries[0].state.data,
                basket: {
                  paymentOption: PaymentType.RESERVE_WITHOUT_CARD,
                  status: BASKET_STATUS.CANCELLED,
                },
              },
            },
          },
        ],
      },
    };

    const result = await isValidSecureBooking(featureToggles, data as any, 'pi');
    expect(result).toEqual({ error: true });
  });

  it('should return error false for PAY_ON_ARRIVAL with PAY_PENDING', async () => {
    const data = {
      ...loadedData,
      dehydratedState: {
        queries: [
          {
            state: {
              data: {
                ...loadedData.dehydratedState.queries[0].state.data,
                basket: {
                  paymentOption: PaymentType.PAY_ON_ARRIVAL,
                  status: BASKET_STATUS.PAY_PENDING,
                },
              },
            },
          },
        ],
      },
    };

    const result = await isValidSecureBooking(featureToggles, data as any, 'pi');
    expect(result).toEqual({ error: false });
  });

  it('should return error true for PAY_ON_ARRIVAL with COMPLETED', async () => {
    const data = {
      ...loadedData,
      dehydratedState: {
        queries: [
          {
            state: {
              data: {
                ...loadedData.dehydratedState.queries[0].state.data,
                basket: {
                  paymentOption: PaymentType.PAY_ON_ARRIVAL,
                  status: BASKET_STATUS.COMPLETED,
                },
              },
            },
          },
        ],
      },
    };

    const result = await isValidSecureBooking(featureToggles, data as any, 'pi');
    expect(result).toEqual({ error: true });
  });

  it('should return error true for unsupported paymentOption', async () => {
    const data = {
      ...loadedData,
      dehydratedState: {
        queries: [
          {
            state: {
              data: {
                ...loadedData.dehydratedState.queries[0].state.data,
                basket: {
                  paymentOption: 'UNKNOWN',
                  status: BASKET_STATUS.PAY_PENDING,
                },
              },
            },
          },
        ],
      },
    };

    const result = await isValidSecureBooking(featureToggles, data as any, 'pi');
    expect(result).toEqual({ error: true });
  });
});

describe('getBasketStatus', () => {
  it('should return first basket if multiple queries contain basket', () => {
    const mockData = {
      dehydratedState: {
        queries: [
          {
            state: {
              data: {
                basket: {
                  paymentOption: PaymentType.RESERVE_WITHOUT_CARD,
                  status: BASKET_STATUS.COMPLETED,
                },
              },
            },
          },
        ],
      },
    };

    expect(getBasketStatus(mockData)).toEqual({
      paymentOption: PaymentType.RESERVE_WITHOUT_CARD,
      status: BASKET_STATUS.COMPLETED,
    });
  });
});
