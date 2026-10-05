import { getBillingAddress, getDefaultDataFromBooking } from '../getters';
import {
  buildCardFromPaymentMethod,
  buildPaymentParams,
  getBookingForSomeoneElse,
  updateSelectedPaymentOption,
} from './payment-params';

// ── Module mocks ──────────────────────────────────────────────────────────────

jest.mock('@whitbread-eos/api', () => ({
  paymentOptions: {
    RESERVE_WITHOUT_CARD: 'RESERVE_WITHOUT_CARD',
    PAY_NOW: 'PAY_NOW',
  },
  PAYPAL_PAYMENT: 'PAYPAL',
  WALLET_APPLE: 'WALLET_APPLE',
}));

jest.mock('../getters', () => ({
  getBillingAddress: jest.fn((opts: { billingAddress?: unknown }) => opts.billingAddress ?? null),
  getDefaultDataFromBooking: jest.fn(() => ({ bookingForSomeoneElse: false })),
}));

jest.mock('uuid', () => ({ v4: jest.fn(() => 'mocked-uuid') }));

const mockGetBillingAddress = getBillingAddress as jest.Mock;
const mockGetDefaultDataFromBooking = getDefaultDataFromBooking as jest.Mock;

// ── Shared fixtures ───────────────────────────────────────────────────────────

const basePaymentType = {
  name: 'CREDIT_CARD',
  type: 'CREDIT_CARD',
  subType: '',
  order: 1,
  logoSrc: '',
  paymentOptions: [],
  enabled: true,
  cnpPreSelected: false,
  cnpOptionAvailable: false,
  reasons: [],
};

const baseInput = {
  hotelId: 'hotel-001',
  hotelName: 'Test Hotel',
  arrivalDate: '2025-09-01',
  departureDate: '2025-09-03',
  rooms: [{ adultsNumber: 2, rate: 'FLEX', type: 'DOUBLE' }],
  reservationByIdList: [],
  basketReference: 'basket-123',
  language: 'en',
  country: 'gb',
  billing: { email: 'test@example.com', address: { addressLine1: '1 Test St' } },
  formData: { bookingForSomeoneElse: false },
  isBillingAddressDisplayed: false,
  selectedPaymentType: basePaymentType,
  selectedPaymentDetail: { type: 'PAY_NOW', order: 1, enabled: true },
  isRemovePIIDataFromLocalStorageEnabled: false,
  isSecureBooking: false,
  hotelBrand: 'PI',
  isGermanHotel: false,
};

beforeEach(() => {
  jest.clearAllMocks();
  mockGetBillingAddress.mockImplementation(
    (opts: { billingAddress?: unknown }) => opts.billingAddress ?? null
  );
  mockGetDefaultDataFromBooking.mockReturnValue({ bookingForSomeoneElse: false });
  Object.defineProperty(window, 'location', {
    value: { origin: 'https://example.com' },
    writable: true,
  });
});

// ── getBookingForSomeoneElse ──────────────────────────────────────────────────

describe('getBookingForSomeoneElse', () => {
  const base = {
    isRemovePIIDataFromLocalStorageEnabled: false,
    reservationByIdList: [],
    basketReference: 'b-1',
    language: 'en',
    hotelBrand: 'PI',
    isGermanHotel: false,
    formData: { bookingForSomeoneElse: false },
  };

  it('reads from formData when flag is false', () => {
    expect(getBookingForSomeoneElse({ ...base, formData: { bookingForSomeoneElse: true } })).toBe(
      true
    );
  });

  it('returns false from formData when bookingForSomeoneElse is missing', () => {
    expect(getBookingForSomeoneElse({ ...base, formData: null })).toBe(false);
  });

  it('reads from getDefaultDataFromBooking when flag is true', () => {
    mockGetDefaultDataFromBooking.mockReturnValue({ bookingForSomeoneElse: true });
    expect(
      getBookingForSomeoneElse({ ...base, isRemovePIIDataFromLocalStorageEnabled: true })
    ).toBe(true);
  });

  it('returns false when getDefaultDataFromBooking returns falsy bookingForSomeoneElse', () => {
    mockGetDefaultDataFromBooking.mockReturnValue({ bookingForSomeoneElse: false });
    expect(
      getBookingForSomeoneElse({ ...base, isRemovePIIDataFromLocalStorageEnabled: true })
    ).toBe(false);
  });

  it('passes reservationByIdList, basketReference, language, hotelBrand, isGermanHotel to getDefaultDataFromBooking', () => {
    getBookingForSomeoneElse({
      isRemovePIIDataFromLocalStorageEnabled: true,
      reservationByIdList: [],
      basketReference: 'b-xyz',
      language: 'de',
      hotelBrand: 'PID',
      isGermanHotel: true,
      formData: {},
    });
    expect(mockGetDefaultDataFromBooking).toHaveBeenCalledWith(
      [],
      {},
      'b-xyz',
      'de',
      'PID',
      true,
      true
    );
  });
});

// ── buildCardFromPaymentMethod ────────────────────────────────────────────────

describe('buildCardFromPaymentMethod', () => {
  const rawCard = {
    cardHolderName: 'Jane Smith',
    cardNumber: '4111111111111111',
    cardName: 'My Card',
    cardType: 'VISA',
    cnpRequired: false,
    expiryMonth: '11',
    expiryYear: '2027',
    logoSrc: 'visa.png',
    token: 'tok_abc',
    type: 'VISA',
  };

  it('maps logoSrc → logoUrl', () => {
    expect(buildCardFromPaymentMethod(rawCard)).toMatchObject({ logoUrl: 'visa.png' });
  });

  it('maps cardHolderName → cardholderName', () => {
    expect(buildCardFromPaymentMethod(rawCard)).toMatchObject({ cardholderName: 'Jane Smith' });
  });

  it('strips cardNumber', () => {
    expect(buildCardFromPaymentMethod(rawCard)).not.toHaveProperty('cardNumber');
  });

  it('strips cardName', () => {
    expect(buildCardFromPaymentMethod(rawCard)).not.toHaveProperty('cardName');
  });

  it('retains token, expiryMonth, expiryYear, cardType, cnpRequired', () => {
    expect(buildCardFromPaymentMethod(rawCard)).toMatchObject({
      token: 'tok_abc',
      expiryMonth: '11',
      expiryYear: '2027',
      cardType: 'VISA',
      cnpRequired: false,
    });
  });
});

// ── updateSelectedPaymentOption ───────────────────────────────────────────────

describe('updateSelectedPaymentOption', () => {
  it('returns RESERVE_WITHOUT_CARD when name is RESERVE_WITHOUT_CARD', () => {
    expect(updateSelectedPaymentOption({ name: 'RESERVE_WITHOUT_CARD', type: 'CREDIT_CARD' })).toBe(
      'RESERVE_WITHOUT_CARD'
    );
  });

  it('returns WALLET_APPLE when type is AP', () => {
    expect(updateSelectedPaymentOption({ name: 'Apple Pay', type: 'AP' })).toBe('WALLET_APPLE');
  });

  it('returns WALLET_APPLE when type is GP', () => {
    expect(updateSelectedPaymentOption({ name: 'Google Pay', type: 'GP' })).toBe('WALLET_APPLE');
  });

  it('returns the name for a regular card type', () => {
    expect(updateSelectedPaymentOption({ name: 'VISA', type: 'CREDIT_CARD' })).toBe('VISA');
  });

  it('returns the name for PayPal', () => {
    expect(updateSelectedPaymentOption({ name: 'PAYPAL', type: 'PAYPAL' })).toBe('PAYPAL');
  });
});

// ── buildPaymentParams ────────────────────────────────────────────────────────

describe('buildPaymentParams', () => {
  // ── new card ──────────────────────────────────────────────────────────────

  describe('new card (no card object)', () => {
    const input = { ...baseInput, selectedPaymentType: { ...basePaymentType, type: 'NEW_CARD' } };

    it('omits card from the payment request', () => {
      expect(buildPaymentParams(input).payment.card).toBeUndefined();
    });

    it('sets pibaCardPresent to false', () => {
      expect(buildPaymentParams(input).payment.pibaCardPresent).toBe(false);
    });

    it('sets subType to ECOMM', () => {
      expect(buildPaymentParams(input).payment.subType).toBe('ECOMM');
    });
  });

  // ── saved card ────────────────────────────────────────────────────────────

  describe('saved card', () => {
    const savedCard = {
      cardHolderName: 'John Doe',
      cardNumber: '4111111111111111',
      cardName: 'My Saved Card',
      cardType: 'VISA',
      cnpRequired: false,
      expiryMonth: '12',
      expiryYear: '2026',
      logoSrc: 'visa.png',
      token: 'tok_123',
      type: 'VISA',
    };
    const input = {
      ...baseInput,
      selectedPaymentType: { ...basePaymentType, type: 'SAVED_CARD', card: savedCard },
    };

    it('includes card in the payment request', () => {
      expect(buildPaymentParams(input).payment.card).toBeDefined();
    });

    it('maps logoSrc → logoUrl and cardHolderName → cardholderName', () => {
      expect(buildPaymentParams(input).payment.card).toMatchObject({
        logoUrl: 'visa.png',
        cardholderName: 'John Doe',
      });
    });

    it('strips cardNumber and cardName', () => {
      const { card } = buildPaymentParams(input).payment;
      expect(card).not.toHaveProperty('cardNumber');
      expect(card).not.toHaveProperty('cardName');
    });
  });

  // ── reserve-without-card ──────────────────────────────────────────────────

  describe('reserve-without-card', () => {
    const rwcType = {
      ...basePaymentType,
      name: 'RESERVE_WITHOUT_CARD',
      type: 'RESERVE_WITHOUT_CARD',
    };

    it('sets booking type to RESERVE_WITHOUT_CARD', () => {
      expect(buildPaymentParams({ ...baseInput, selectedPaymentType: rwcType }).booking.type).toBe(
        'RESERVE_WITHOUT_CARD'
      );
    });

    it('sets payment type to RESERVE_WITHOUT_CARD', () => {
      expect(buildPaymentParams({ ...baseInput, selectedPaymentType: rwcType }).payment.type).toBe(
        'RESERVE_WITHOUT_CARD'
      );
    });
  });

  // ── PayPal ────────────────────────────────────────────────────────────────

  describe('PayPal payment', () => {
    const paypalInput = {
      ...baseInput,
      paymentType: 'PAYPAL',
      paypalNonce: 'nonce-abc',
      paypalDeviceData: { deviceData: 'dd' },
    };

    it('sets subType to MIT', () => {
      expect(buildPaymentParams(paypalInput).payment.subType).toBe('MIT');
    });

    it('includes paypalNonce and paypalDeviceData', () => {
      expect(buildPaymentParams(paypalInput).payment).toMatchObject({
        paypalNonce: 'nonce-abc',
        paypalDeviceData: { deviceData: 'dd' },
      });
    });

    it('does not include paypalNonce for non-PayPal payments', () => {
      expect(buildPaymentParams(baseInput).payment).not.toHaveProperty('paypalNonce');
    });
  });

  // ── secure booking ────────────────────────────────────────────────────────

  describe('secure booking flag', () => {
    it('appends isSecureBooking: true when flag is true', () => {
      expect(buildPaymentParams({ ...baseInput, isSecureBooking: true })).toMatchObject({
        isSecureBooking: true,
      });
    });

    it('does not add isSecureBooking when flag is false', () => {
      expect(buildPaymentParams({ ...baseInput, isSecureBooking: false })).not.toHaveProperty(
        'isSecureBooking'
      );
    });
  });

  // ── piba (pibaCardPresent) ────────────────────────────────────────────────

  describe('pibaCardPresent', () => {
    it('is always false regardless of payment method', () => {
      ['NEW_CARD', 'SAVED_CARD', 'CREDIT_CARD', 'RESERVE_WITHOUT_CARD'].forEach((type) => {
        expect(
          buildPaymentParams({ ...baseInput, selectedPaymentType: { ...basePaymentType, type } })
            .payment.pibaCardPresent
        ).toBe(false);
      });
    });
  });

  // ── billing resolution ────────────────────────────────────────────────────

  describe('billing / bookerIsNotGuest', () => {
    it('reads bookingForSomeoneElse from formData when flag is false', () => {
      const result = buildPaymentParams({
        ...baseInput,
        isRemovePIIDataFromLocalStorageEnabled: false,
        formData: { bookingForSomeoneElse: true },
      });
      expect(result.payment.billing.bookerIsNotGuest).toBe(true);
    });

    it('reads bookingForSomeoneElse from getDefaultDataFromBooking when flag is true', () => {
      mockGetDefaultDataFromBooking.mockReturnValue({ bookingForSomeoneElse: true });
      const result = buildPaymentParams({
        ...baseInput,
        isRemovePIIDataFromLocalStorageEnabled: true,
      });
      expect(result.payment.billing.bookerIsNotGuest).toBe(true);
    });

    it('forces differentBillingAddress to false when flag is true', () => {
      const result = buildPaymentParams({
        ...baseInput,
        isRemovePIIDataFromLocalStorageEnabled: true,
        formData: { billing: { differentBillingAddress: true } },
      });
      expect(result.payment.billing.differentBillingAddress).toBe(false);
    });
  });

  // ── invariant fields ──────────────────────────────────────────────────────

  describe('invariant fields', () => {
    it('sets channel to PI and journey to BOOKING', () => {
      const { booking } = buildPaymentParams(baseInput);
      expect(booking.channel).toBe('PI');
      expect(booking.journey).toBe('BOOKING');
    });

    it('uses window.location.origin as environment', () => {
      expect(buildPaymentParams(baseInput).payment.environment).toBe('https://example.com');
    });

    it('uses uuidv4 for requestId', () => {
      expect(buildPaymentParams(baseInput).requestId).toBe('mocked-uuid');
    });

    it('includes hotelId at the top level', () => {
      expect(buildPaymentParams(baseInput).hotelId).toBe('hotel-001');
    });

    it('falls back to selectedPaymentDetail.type for booking type when name is not RESERVE_WITHOUT_CARD', () => {
      const result = buildPaymentParams({
        ...baseInput,
        selectedPaymentDetail: { type: 'PAY_NOW', order: 1, enabled: true },
      });
      expect(result.booking.type).toBe('PAY_NOW');
    });
  });
});
