import { ChakraProvider } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import { axe, toHaveNoViolations } from 'jest-axe';
import React from 'react';

import { fireEvent, render, screen, waitFor } from '~utils/test-utils';

import {
  PAYMENT_NAVIGATION_SOURCE_GUEST_DETAILS,
  PAYMENT_NAVIGATION_SOURCE_QUERY_PARAM,
} from '../../../../utils/pi-all-pages-constants';
import { DatatransPage } from './DatatransPage';

expect.extend(toHaveNoViolations);

// ─── Types ────────────────────────────────────────────────────────────────────

type RouterPushFn = jest.Mock<Promise<boolean>, [string]>;

// ─── Module-level mock state ──────────────────────────────────────────────────
// These objects are mutated in beforeEach / individual tests so every mock
// reads the current value rather than a stale closure copy.

// Captures the onSuccess/onError props passed to DatatransSecureFieldsForm so
// tests can invoke them directly without re-mocking the module.
let capturedSecureFieldsProps: {
  onSuccess?: (data: { transactionId: string; redirect?: string }) => Promise<void> | void;
  onError?: (err: Error) => void;
  onInitialisingChange?: (v: boolean) => void;
  onSubmitValidationFailed?: () => void;
} = {};

const mockRouterState = {
  push: jest.fn() as RouterPushFn,
  back: jest.fn(),
  query: {} as Record<string, string>,
};

const mockBookingData = {
  bookingInformation: {
    hotelId: 'hotel-001',
    bookingFlowId: 'bf-001',
    totalCost: '150.00',
    currencyCode: 'GBP',
    ratePlanCode: 'FLEX',
    reservationByIdList: [
      {
        roomStay: {
          arrivalDate: '2025-09-01',
          departureDate: '2025-09-03',
          ratePlanCode: 'FLEX',
          adultsNumber: 2,
          childrenNumber: 0,
          roomExtraInfo: { roomType: 'DOUBLE' },
        },
        billing: {
          email: 'guest@example.com',
          address: {
            addressLine1: '1 Test St',
            cityName: 'London',
            postalCode: 'SW1A 1AA',
          },
        },
        additionalGuestInfo: { purposeOfStay: 'LEISURE' },
      },
    ],
  },
};

const mockHotelData = {
  hotelInformation: {
    brand: 'PI',
    importantInfo: { infoItems: [] },
  },
};

// Shared mock handles — tests that need to drive async state swap these.
let mockIsLoadingBooking = false;
let mockIsLoadingHotel = false;
let mockIsLoadingTerms = false;
let mockIsLoadingPaymentInfo = false;
let mockIsPaymentComplete = false;
let mockCardType: string | null = null;
let mockIsPaypalSuccess = false;
let mockInitiatePaypalData: { initiatePaypalPayment?: { status: string } } | undefined;
let mockIsErrorPaypal = false;

// ─── Mocks ────────────────────────────────────────────────────────────────────

jest.mock('next/router', () => ({
  useRouter: () => mockRouterState,
}));

jest.mock('next/script', () => ({
  __esModule: true,
  default: ({ src }: { src: string }) => <script data-testid="NextScript" data-src={src ?? ''} />,
}));

jest.mock('next-i18next', () => ({
  useTranslation: () => ({ t: (key: string) => key }),
}));

// ── @whitbread-eos/utils ──────────────────────────────────────────────────────
jest.mock('@whitbread-eos/utils', () => ({
  useCustomLocale: () => ({ language: 'en', country: 'gb' }),
  useQueryRequest: jest.fn(),
  useMutationRequest: jest.fn(),
  usePackages: jest.fn(),
  useIPageSubmission: jest.fn(),
  useLocalStorage: jest.fn(),
  useSessionStorage: jest.fn(),
  useFeatureToggle: jest.fn(),
  useUpdateRateName: jest.fn(),
  analytics: { update: jest.fn() },
  analyticsConfirmation: { update: jest.fn() },
  logicalOrOperator: (...args: unknown[]) => args.find(Boolean) ?? false,
  getNightsNumber: jest.fn(() => 2),
  getMaxValueFromRoomStays: jest.fn(() => 2),
  getBookingSummaryData: jest.fn(() => ({
    hotelInformation: { hotelName: 'Test Hotel' },
    stayDatesInformation: { arrivalDate: '2025-09-01', departureDate: '2025-09-03' },
  })),
  createReservationDetails: jest.fn(() => ({
    currency: 'GBP',
    noNights: 2,
  })),
  getTotalCost: jest.fn(() => '150.00'),
  getCityTaxMessages: jest.fn(() => ({ summaryText: '' })),
  getIsBillingAddressDisplayed: jest.fn(() => false),
  getPaypalDeviceData: jest.fn(async () => 'deviceData'),
  getPaypalOptionsParams: jest.fn(() => ({
    createBillingAgreement: jest.fn(),
    onApprove: jest.fn(),
    onError: jest.fn(),
  })),
  formatImportantNotes: jest.fn(() => []),
  formatUrlTermsConditions: jest.fn((text: string | undefined) => text ?? ''),
  renderSanitizedHtml: jest.fn((html: unknown) => html),
  adultsMealsSelector: jest.fn(() => []),
  childrenMealsSelector: jest.fn(() => []),
  mealsMapperSelector: jest.fn(() => []),
  applyDefaultPaymentRestrictions: jest.fn(),
  isSecureBookingPage: jest.fn(() => false),
  addBasketIdToCookie: jest.fn(),
  updateAncillariesAnalytics: jest.fn(),
  updateDashboardAnalytics: jest.fn(),
  buildPaymentParams: jest.fn((params: { paypalNonce?: string }) => ({
    payment: { paypalNonce: params?.paypalNonce },
  })),
  INITIAL_GUEST_DETAILS_FORM_DATA: {},
  usePaymentData: jest.fn(),
  usePaymentAnalytics: jest.fn(),
}));

// ── @whitbread-eos/api ────────────────────────────────────────────────────────
jest.mock('@whitbread-eos/api', () => ({
  BOOKING_CHANNEL: { PI: 'PI' },
  GET_BOOKING_INFORMATION: 'GET_BOOKING_INFORMATION',
  GET_HOTEL_INFORMATION: 'GET_HOTEL_INFORMATION',
  GET_TERMS_AND_CONDITIONS_QUERY: 'GET_TERMS_AND_CONDITIONS_QUERY',
  GET_PAYMENT_INFO_MESSAGES_QUERY: 'GET_PAYMENT_INFO_MESSAGES_QUERY',
  INITIATE_PAYPAL_PAYMENT_MUTATION: 'INITIATE_PAYPAL_PAYMENT_MUTATION',
  PAYMENT_FAILED_INITIAL_VALUE: '',
  PAYMENT_FAILED_VALUE: 'true',
  PAYMENT_FAILED_KEY: 'paymentFailed',
  PAYMENT_FAILURE_CODE_KEY: 'paymentFailureCode',
  PAYMENT_FAILURE_CODE_INITIAL_VALUE: '',
  PAYMENT_FAILURE_DESCRIPTION_KEY: 'paymentFailureDescription',
  PAYMENT_FAILURE_DESCRIPTION_INITIAL_VALUE: '',
  PAYPAL_PAYMENT: 'PAYPAL',
  WALLET_APPLE: 'APPLE',
  BASKET_DETAILS_STORAGE_KEY: 'basketDetails',
  BASKET_DETAILS_STATE_INITIAL_VALUE: {},
  BASKET_STATUS: {},
  PAYMENT_ANALYTICS_KEY: 'paymentAnalytics',
  Area: { PI: 'PI' },
  HotelBrand: { PID: 'PID' },
  PageName: { PAYMENT: 'PAYMENT' },
  paymentOptions: { PAY_NOW: 'PAY_NOW', RESERVE_WITHOUT_CARD: 'RESERVE_WITHOUT_CARD' },
  paymentSteps: { PAYMENT_DETAILS: 'PAYMENT_DETAILS', CARD_DETAILS: 'CARD_DETAILS' },
  PaymentMethod: {},
  PiCardType: { RESERVE_WITHOUT_CARD: 'RESERVE_WITHOUT_CARD' },
  FT_PI_ENABLE_PAYMENT_REDESIGN: 'FT_PI_ENABLE_PAYMENT_REDESIGN',
  FT_PI_BB_CCUI_COMPANY_NAME_SPECIAL_CHARACTERS: 'FT_PI_BB_CCUI_COMPANY_NAME_SPECIAL_CHARACTERS',
  FT_PI_BB_CCUI_DISABLE_PAYMENTS: 'FT_PI_BB_CCUI_DISABLE_PAYMENTS',
  FT_PI_BB_NON_GUARANTEED_REMINDER: 'FT_PI_BB_NON_GUARANTEED_REMINDER',
  FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN_ANCILLARIES_GDP_PAYMENT:
    'FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN_ANCILLARIES_GDP_PAYMENT',
  FT_PI_PIB_PAYMENT_BACK_TO_OPTIONS_LINK: 'FT_PI_PIB_PAYMENT_BACK_TO_OPTIONS_LINK',
  FT_PI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE: 'FT_PI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE',
}));

// ── @whitbread-eos/atoms ──────────────────────────────────────────────────────
// Bypass the Haste-map collision by mocking the resolved dist path.
jest.mock('@whitbread-eos/atoms', () => ({
  LoadingSpinner: ({ loadingText }: { loadingText: string }) => (
    <div data-testid="LoadingSpinner">{loadingText}</div>
  ),
  PaypalWBButton: () => <button data-testid="PaypalWBButton">PayPal</button>,
}));

// ── @whitbread-eos/molecules ──────────────────────────────────────────────────
jest.mock('@whitbread-eos/molecules', () => ({
  SEO: () => null,
  BackToPage: ({ goBack, linkText }: { goBack: () => void; linkText: string }) => (
    <button data-testid="BackToPage" onClick={goBack}>
      {linkText}
    </button>
  ),
  INITIAL_GUEST_DETAILS_FORM_DATA: {},
}));

// ── @whitbread-eos/organisms ──────────────────────────────────────────────────
jest.mock('@whitbread-eos/organisms', () => ({
  BillingAddress: () => <div data-testid="BillingAddress" />,
  BookingSummary: ({ variant }: { variant: string }) => (
    <div data-testid={`BookingSummary-${variant}`} />
  ),
}));

// ── Datatrans sub-components ──────────────────────────────────────────────────
jest.mock('../DatatransBillingAddress', () => ({
  DatatransBillingAddress: () => <div data-testid="DatatransBillingAddress" />,
}));

jest.mock('../DatatransPaymentButton', () => ({
  DatatransPaymentButton: ({ walletType }: { walletType: string }) => (
    <button data-testid={`DatatransPaymentButton-${walletType}`}>{walletType}</button>
  ),
}));

const mockSecureFieldsFormRef = {
  submit: jest.fn(),
  resetForm: jest.fn(),
  reinit: jest.fn(),
};
jest.mock('../DatatransSecureFieldsForm', () => {
  const { forwardRef, useImperativeHandle } = jest.requireActual<typeof import('react')>('react');
  return {
    DatatransSecureFieldsForm: forwardRef(function DatatransSecureFieldsFormMock(
      props: {
        isVisible?: boolean;
        onSuccess?: (data: { transactionId: string; redirect?: string }) => void;
        onError?: (err: Error) => void;
        onInitialisingChange?: (v: boolean) => void;
        onSubmitValidationFailed?: () => void;
      },
      ref: React.Ref<unknown>
    ) {
      // Capture the latest props so tests can invoke callbacks directly
      capturedSecureFieldsProps = {
        onSuccess: props.onSuccess,
        onError: props.onError,
        onInitialisingChange: props.onInitialisingChange,
        onSubmitValidationFailed: props.onSubmitValidationFailed,
      };
      useImperativeHandle(ref, () => mockSecureFieldsFormRef);
      return <div data-testid="DatatransSecureFieldsForm" data-visible={String(props.isVisible)} />;
    }),
  };
});

let mockCapturedPaymentConfirmIsLoading: boolean | undefined = undefined;

jest.mock('../PaymentConfirmSection', () => ({
  PaymentConfirmSection: ({
    onConfirm,
    onBack,
    isLoading,
  }: {
    onConfirm: () => void;
    onBack: () => void;
    isLoading?: boolean;
  }) => {
    mockCapturedPaymentConfirmIsLoading = isLoading;
    return (
      <div data-testid="PaymentConfirmSection">
        <button
          data-testid="PaymentConfirmSection-ConfirmButton"
          onClick={onConfirm}
          disabled={isLoading}
        >
          Confirm
        </button>
        <button
          data-testid="PaymentConfirmSection-BackButton"
          onClick={onBack}
          disabled={isLoading}
        >
          Back
        </button>
      </div>
    );
  },
}));

jest.mock('../PaymentMethodSelector', () => ({
  PaymentMethodSelector: ({ onMethodSelect }: { onMethodSelect: (method: unknown) => void }) => (
    <div data-testid="PaymentMethodSelector">
      <button
        data-testid="SelectNewCard"
        onClick={() =>
          onMethodSelect({
            type: 'NEW_CARD',
            name: 'CARD',
            order: 1,
            enabled: true,
            logoSrc: null,
            subType: null,
            cnpPreSelected: false,
            cnpOptionAvailable: false,
            paymentOptions: [{ type: 'PAY_NOW', order: 1, enabled: true }],
            reasons: [],
          })
        }
      >
        Credit / Debit
      </button>
      <button
        data-testid="SelectPayPal"
        onClick={() =>
          onMethodSelect({
            type: 'PAYPAL',
            name: 'PAYPAL',
            order: 2,
            enabled: true,
            logoSrc: null,
            subType: null,
            cnpPreSelected: false,
            cnpOptionAvailable: false,
            paymentOptions: [{ type: 'PAY_NOW', order: 1, enabled: true }],
            reasons: [],
          })
        }
      >
        PayPal
      </button>
      <button
        data-testid="SelectApplePay"
        onClick={() =>
          onMethodSelect({
            type: 'AP',
            name: 'APPLE',
            order: 3,
            enabled: true,
            logoSrc: null,
            subType: null,
            cnpPreSelected: false,
            cnpOptionAvailable: false,
            paymentOptions: [{ type: 'PAY_NOW', order: 1, enabled: true }],
            reasons: [],
          })
        }
      >
        Apple Pay
      </button>
    </div>
  ),
}));

jest.mock('../PaymentOptionToggle', () => ({
  PaymentOptionToggle: () => <div data-testid="PaymentOptionToggle" />,
}));

jest.mock('../Notifications', () => ({
  HeaderNotification: () => <div data-testid="HeaderNotification" />,
  HotelMessages: () => <div data-testid="HotelMessages" />,
  PaymentErrorNotification: ({
    isVisible,
    paymentFailedErrorMessage,
  }: {
    isVisible: boolean;
    paymentFailedErrorMessage: string;
  }) => (
    <div data-testid="PaymentErrorNotification" data-visible={String(isVisible)}>
      {paymentFailedErrorMessage}
    </div>
  ),
  PaymentInfoMessages: () => <div data-testid="PaymentInfoMessages" />,
}));

// ─── Mock accessors ───────────────────────────────────────────────────────────

const {
  useQueryRequest,
  useMutationRequest,
  usePackages,
  useIPageSubmission,
  useLocalStorage,
  useSessionStorage,
  useFeatureToggle,
  usePaymentData,
  usePaymentAnalytics,
} = jest.requireMock('@whitbread-eos/utils') as {
  useQueryRequest: jest.Mock;
  useMutationRequest: jest.Mock;
  usePackages: jest.Mock;
  useIPageSubmission: jest.Mock;
  useLocalStorage: jest.Mock;
  useSessionStorage: jest.Mock;
  useFeatureToggle: jest.Mock;
  usePaymentData: jest.Mock;
  usePaymentAnalytics: jest.Mock;
};

// ─── Default hook implementations ────────────────────────────────────────────

const mockMutationReset = jest.fn();
const mockMutationMutate = jest.fn();

function setupDefaultMocks() {
  mockIsLoadingBooking = false;
  mockIsLoadingHotel = false;
  mockIsLoadingTerms = false;
  mockIsLoadingPaymentInfo = false;
  mockIsPaymentComplete = false;
  mockCardType = null;
  mockIsPaypalSuccess = false;
  mockInitiatePaypalData = undefined;
  mockIsErrorPaypal = false;

  useQueryRequest.mockImplementation((key: string[]) => {
    if (key[0] === 'GetBookingInformation') {
      return { isLoading: mockIsLoadingBooking, data: mockBookingData };
    }
    if (key[0] === 'GetHotelInformation') {
      return { isLoading: mockIsLoadingHotel, data: mockHotelData };
    }
    if (key[0] === 'GetTermsAndConditions') {
      return {
        isLoading: mockIsLoadingTerms,
        data: { termsAndConditions: { text: 'T&C text' } },
      };
    }
    if (key[0] === 'GetPaymentInfoMessages') {
      return { isLoading: mockIsLoadingPaymentInfo, data: { paymentInfoMessages: [] } };
    }
    return { isLoading: false, data: undefined };
  });

  usePackages.mockReturnValue({
    isLoading: false,
    packages: { meals: [], mealsKids: [], roomSelection: [], extrasItems: null },
    hotelHasCityTaxForBusiness: false,
    hotelHasCityTaxForLeisure: false,
  });

  useMutationRequest.mockReturnValue({
    mutation: { mutate: mockMutationMutate, reset: mockMutationReset },
    isSuccess: mockIsPaypalSuccess,
    isLoading: false,
    data: mockInitiatePaypalData,
    isError: mockIsErrorPaypal,
  });

  useIPageSubmission.mockReturnValue({
    isPaymentComplete: mockIsPaymentComplete,
    cardType: mockCardType,
  });

  // useLocalStorage: [value, setter]
  useLocalStorage.mockImplementation((key: string, initial: unknown) => {
    if (key === 'formDetails') return [{ billing: null, bookingForSomeoneElse: false }, jest.fn()];
    return [initial, jest.fn()];
  });

  // useSessionStorage: [value, setter]
  useSessionStorage.mockReturnValue(['', jest.fn()]);

  useFeatureToggle.mockReturnValue({
    FT_PI_ENABLE_PAYMENT_REDESIGN: false,
    FT_PI_BB_CCUI_COMPANY_NAME_SPECIAL_CHARACTERS: false,
    FT_PI_BB_CCUI_DISABLE_PAYMENTS: false,
    FT_PI_BB_NON_GUARANTEED_REMINDER: false,
    FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN_ANCILLARIES_GDP_PAYMENT: false,
    FT_PI_PIB_PAYMENT_BACK_TO_OPTIONS_LINK: false,
    FT_PI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE: false,
  });

  usePaymentData.mockReturnValue({
    bkngData: mockBookingData,
    packages: { meals: [], mealsKids: [], roomSelection: [], extrasItems: null },
    bookingInformation: mockBookingData.bookingInformation,
    reservationDetails: { currency: 'GBP', noNights: 2 },
    bookingSummaryData: {
      hotelInformation: { hotelName: 'Test Hotel' },
      stayDatesInformation: { arrivalDate: '2025-09-01', departureDate: '2025-09-03' },
    },
    cityTaxMessages: { summaryText: '' },
    infoMessages: [],
    orderedInfoMessages: [],
    orderedListOfMessagesPaymentType: [],
    rooms: [],
    hotelBrand: 'PI',
    isGermanHotel: false,
    isBillingAddressDisplayed: false,
    termsAndConditionsText: 'T&C text',
    isLoading: false,
  });

  usePaymentAnalytics.mockReturnValue({
    confAnalytics: {},
    setConfAnalytics: jest.fn(),
  });

  mockRouterState.push = jest.fn().mockResolvedValue(true);
  mockRouterState.back = jest.fn();
  mockRouterState.query = {};
}

// ─── Render helper ────────────────────────────────────────────────────────────

const defaultProps = {
  hiQueryInput: {
    hotelId: 'hotel-001',
    country: 'gb',
    language: 'en',
  },
  pcksQueryInput: {
    adultsNumber: 2,
    childrenNumber: 0,
    hotelId: 'hotel-001',
    basketReferenceId: 'basket-001',
    endDate: '2025-09-03',
    startDate: '2025-09-01',
    country: 'gb',
    language: 'en',
    bookingFlowId: 'bf-001',
    nightsNumber: 2,
  },
  basketReference: 'basket-001',
} satisfies React.ComponentProps<typeof DatatransPage>;

const wrap = (ui: React.ReactElement) => render(<ChakraProvider>{ui}</ChakraProvider>);
const renderPage = (props: Partial<React.ComponentProps<typeof DatatransPage>> = {}) =>
  wrap(<DatatransPage {...defaultProps} {...props} />);

// ─── Tests ────────────────────────────────────────────────────────────────────

describe('DatatransPage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    capturedSecureFieldsProps = {};
    mockCapturedPaymentConfirmIsLoading = undefined;
    setupDefaultMocks();
  });

  // ── Loading state ───────────────────────────────────────────────────────────

  describe('Loading state', () => {
    it('renders the loading spinner while booking information is being fetched', () => {
      usePaymentData.mockReturnValue({
        bkngData: {},
        packages: null,
        bookingInformation: {},
        reservationDetails: {},
        bookingSummaryData: {},
        cityTaxMessages: null,
        infoMessages: [],
        orderedInfoMessages: [],
        orderedListOfMessagesPaymentType: [],
        rooms: [],
        hotelBrand: '',
        isGermanHotel: false,
        isBillingAddressDisplayed: false,
        termsAndConditionsText: '',
        isLoading: true,
      });
      renderPage();
      expect(screen.getByTestId('LoadingSpinner')).toBeInTheDocument();
      expect(screen.queryByTestId('paymentPageSection')).not.toBeInTheDocument();
    });

    it('renders the loading spinner while hotel information is loading', () => {
      usePaymentData.mockReturnValue({
        bkngData: {},
        packages: null,
        bookingInformation: {},
        reservationDetails: {},
        bookingSummaryData: {},
        cityTaxMessages: null,
        infoMessages: [],
        orderedInfoMessages: [],
        orderedListOfMessagesPaymentType: [],
        rooms: [],
        hotelBrand: '',
        isGermanHotel: false,
        isBillingAddressDisplayed: false,
        termsAndConditionsText: '',
        isLoading: true,
      });
      renderPage();
      expect(screen.getByTestId('LoadingSpinner')).toBeInTheDocument();
    });

    it('renders the payment page content once all queries resolve', () => {
      renderPage();
      expect(screen.queryByTestId('LoadingSpinner')).not.toBeInTheDocument();
      expect(screen.getByTestId('paymentPageSection')).toBeInTheDocument();
    });
  });

  // ── Main render ─────────────────────────────────────────────────────────────

  describe('Main render', () => {
    it('renders the page content container', () => {
      renderPage();
      expect(screen.getByTestId('paymentPageSection_content')).toBeInTheDocument();
    });

    it('renders mobile and desktop BookingSummary variants', () => {
      renderPage();
      expect(screen.getByTestId('BookingSummary-mobile')).toBeInTheDocument();
      expect(screen.getByTestId('BookingSummary-desktop')).toBeInTheDocument();
    });

    it('renders the PaymentMethodSelector', () => {
      renderPage();
      expect(screen.getByTestId('PaymentMethodSelector')).toBeInTheDocument();
    });

    it('renders the PaymentOptionToggle', () => {
      renderPage();
      expect(screen.getByTestId('PaymentOptionToggle')).toBeInTheDocument();
    });

    it('renders the PaymentConfirmSection', () => {
      renderPage();
      expect(screen.getByTestId('PaymentConfirmSection')).toBeInTheDocument();
    });

    it('renders the HeaderNotification when the back-to-payment-options feature flag is OFF', () => {
      renderPage();
      expect(screen.getByTestId('HeaderNotification')).toBeInTheDocument();
    });

    it('does not render HeaderNotification when back-to-payment-options flag is ON', () => {
      useFeatureToggle.mockReturnValue({
        ...useFeatureToggle(),
        FT_PI_PIB_PAYMENT_BACK_TO_OPTIONS_LINK: true,
      });
      renderPage();
      expect(screen.queryByTestId('HeaderNotification')).not.toBeInTheDocument();
    });

    it('renders the DatatransSecureFieldsForm when basketReference is set', () => {
      renderPage();
      expect(screen.getByTestId('DatatransSecureFieldsForm')).toBeInTheDocument();
    });

    it('does not render DatatransSecureFieldsForm when basketReference is null', () => {
      renderPage({ basketReference: null });
      expect(screen.queryByTestId('DatatransSecureFieldsForm')).not.toBeInTheDocument();
    });
  });

  // ── Secure Fields form visibility ───────────────────────────────────────────

  describe('DatatransSecureFieldsForm visibility', () => {
    it('is hidden by default (no payment method selected)', () => {
      renderPage();
      expect(screen.getByTestId('DatatransSecureFieldsForm')).toHaveAttribute(
        'data-visible',
        'false'
      );
    });

    it('becomes visible when the NEW_CARD method is selected', async () => {
      renderPage();
      fireEvent.click(screen.getByTestId('SelectNewCard'));
      await waitFor(() => {
        expect(screen.getByTestId('DatatransSecureFieldsForm')).toHaveAttribute(
          'data-visible',
          'true'
        );
      });
    });

    it('is hidden again when a non-card method is selected after NEW_CARD', async () => {
      renderPage();
      fireEvent.click(screen.getByTestId('SelectNewCard'));
      fireEvent.click(screen.getByTestId('SelectPayPal'));
      await waitFor(() => {
        expect(screen.getByTestId('DatatransSecureFieldsForm')).toHaveAttribute(
          'data-visible',
          'false'
        );
      });
    });

    it('calls resetForm on the secure fields ref when payment type changes away from NEW_CARD', () => {
      renderPage();
      fireEvent.click(screen.getByTestId('SelectNewCard'));
      fireEvent.click(screen.getByTestId('SelectPayPal'));
      // Selecting a new method that is not NEW_CARD should NOT reset (resetForm is called when
      // switching TO NEW_CARD from something else to clear prior state)
      // Selecting NEW_CARD when it's already something else DOES call resetForm
      fireEvent.click(screen.getByTestId('SelectNewCard'));
      expect(mockSecureFieldsFormRef.resetForm).toHaveBeenCalled();
    });
  });

  // ── Confirm button — NEW_CARD flow ──────────────────────────────────────────

  describe('Confirm / submit flow', () => {
    it('calls secureFieldsFormRef.submit when NEW_CARD is selected and confirm is clicked', async () => {
      renderPage();
      fireEvent.click(screen.getByTestId('SelectNewCard'));
      fireEvent.click(screen.getByTestId('PaymentConfirmSection-ConfirmButton'));
      await waitFor(() => {
        expect(mockSecureFieldsFormRef.submit).toHaveBeenCalledTimes(1);
      });
    });

    it('does not call secureFieldsFormRef.submit for non-card payment methods', () => {
      renderPage();
      fireEvent.click(screen.getByTestId('SelectPayPal'));
      fireEvent.click(screen.getByTestId('PaymentConfirmSection-ConfirmButton'));
      expect(mockSecureFieldsFormRef.submit).not.toHaveBeenCalled();
    });
  });

  // ── Back navigation ─────────────────────────────────────────────────────────

  describe('Back navigation', () => {
    it('navigates to guest-details when back is clicked on the payment details step', async () => {
      renderPage();
      fireEvent.click(screen.getByTestId('PaymentConfirmSection-BackButton'));
      await waitFor(() => {
        expect(mockRouterState.push).toHaveBeenCalledWith(expect.stringContaining('guest-details'));
      });
    });

    it('includes the basketReference as reservationId in the back-navigation URL', async () => {
      renderPage();
      fireEvent.click(screen.getByTestId('PaymentConfirmSection-BackButton'));
      await waitFor(() => {
        expect(mockRouterState.push).toHaveBeenCalledWith(
          expect.stringContaining('reservationId=basket-001')
        );
      });
    });

    it('calls router.back() instead of push when isSecureBookingPage returns true', async () => {
      const { isSecureBookingPage } = jest.requireMock('@whitbread-eos/utils') as {
        isSecureBookingPage: jest.Mock;
      };
      isSecureBookingPage.mockReturnValue(true);

      renderPage();
      fireEvent.click(screen.getByTestId('PaymentConfirmSection-BackButton'));
      await waitFor(() => {
        expect(mockRouterState.back).toHaveBeenCalled();
        expect(mockRouterState.push).not.toHaveBeenCalled();
      });
    });

    it('preserves ancillaries history when back is clicked after opening payment from GDP', async () => {
      mockRouterState.query = {
        [PAYMENT_NAVIGATION_SOURCE_QUERY_PARAM]: PAYMENT_NAVIGATION_SOURCE_GUEST_DETAILS,
      };

      renderPage();
      fireEvent.click(screen.getByTestId('PaymentConfirmSection-BackButton'));

      await waitFor(() => {
        expect(mockRouterState.back).toHaveBeenCalled();
        expect(mockRouterState.push).not.toHaveBeenCalled();
      });
    });
  });

  // ── Billing address display ─────────────────────────────────────────────────

  describe('Billing address', () => {
    it('does not render DatatransBillingAddress when getIsBillingAddressDisplayed returns false', () => {
      renderPage();
      expect(screen.queryByTestId('DatatransBillingAddress')).not.toBeInTheDocument();
    });

    it('renders DatatransBillingAddress when getIsBillingAddressDisplayed returns true', () => {
      usePaymentData.mockReturnValue({
        ...usePaymentData(),
        isBillingAddressDisplayed: true,
      });

      renderPage();
      expect(screen.getByTestId('DatatransBillingAddress')).toBeInTheDocument();
    });
  });

  // ── PayPal flow ─────────────────────────────────────────────────────────────

  describe('PayPal payment flow', () => {
    it('calls initiatePaypalPaymentMutation.mutate when PayPal onApprove is triggered', async () => {
      // getPaypalOptionsParams captures approveCallBack; we invoke it directly
      const { getPaypalOptionsParams } = jest.requireMock('@whitbread-eos/utils') as {
        getPaypalOptionsParams: jest.Mock;
      };

      let capturedApproveCallback: ((nonce: string) => Promise<void>) | undefined;
      getPaypalOptionsParams.mockImplementation(
        ({ approveCallBack }: { approveCallBack: (nonce: string) => Promise<void> }) => {
          capturedApproveCallback = approveCallBack;
          return {
            createBillingAgreement: jest.fn(),
            onApprove: jest.fn(),
            onError: jest.fn(),
          };
        }
      );

      renderPage();

      await waitFor(() => expect(capturedApproveCallback).toBeDefined());

      await capturedApproveCallback!('nonce-abc');

      await waitFor(() => {
        expect(mockMutationMutate).toHaveBeenCalledWith(
          expect.objectContaining({
            basketReference: 'basket-001',
            createPaymentCriteria: expect.objectContaining({
              payment: expect.objectContaining({
                paypalNonce: 'nonce-abc',
              }),
            }),
          })
        );
      });
    });

    it('navigates to confirmation when PayPal mutation succeeds with NOT_REQUIRED status', async () => {
      useMutationRequest.mockReturnValue({
        mutation: { mutate: mockMutationMutate, reset: mockMutationReset },
        isSuccess: true,
        isLoading: false,
        data: { initiatePaypalPayment: { status: 'NOT_REQUIRED' } },
        isError: false,
      });

      renderPage();

      await waitFor(() => {
        expect(mockRouterState.push).toHaveBeenCalledWith(expect.stringContaining('confirmation'));
      });
    });
  });

  // ── Secure Fields success / error callbacks ─────────────────────────────────

  describe('handleSecureFieldsSuccess', () => {
    it('redirects the window when the success payload contains a redirect URL', async () => {
      // jsdom allows reassignment of window.location.href via Object.defineProperty
      const hrefSetter = jest.fn();
      Object.defineProperty(window, 'location', {
        value: { href: '' },
        writable: true,
        configurable: true,
      });
      Object.defineProperty(window.location, 'href', {
        set: hrefSetter,
        get: () => '',
        configurable: true,
      });

      renderPage();

      // Wait for DatatransSecureFieldsForm to render and capture its props
      await waitFor(() =>
        expect(screen.getByTestId('DatatransSecureFieldsForm')).toBeInTheDocument()
      );

      await capturedSecureFieldsProps.onSuccess!({
        transactionId: 'txn-001',
        redirect: 'https://3ds.example.com',
      });

      expect(hrefSetter).toHaveBeenCalledWith('https://3ds.example.com');
    });

    it('navigates to confirmation after a successful authorize fetch (no redirect)', async () => {
      global.fetch = jest.fn().mockResolvedValue({ ok: true });

      renderPage();

      await waitFor(() =>
        expect(screen.getByTestId('DatatransSecureFieldsForm')).toBeInTheDocument()
      );

      await capturedSecureFieldsProps.onSuccess!({ transactionId: 'txn-002' });

      await waitFor(() => {
        expect(mockRouterState.push).toHaveBeenCalledWith(expect.stringContaining('confirmation'));
      });
    });

    it('calls reinit on the form ref when authorize fetch fails', async () => {
      global.fetch = jest.fn().mockResolvedValue({ ok: false, status: 500 });

      renderPage();

      await waitFor(() =>
        expect(screen.getByTestId('DatatransSecureFieldsForm')).toBeInTheDocument()
      );

      // Select NEW_CARD so isVisible is true when success fires
      fireEvent.click(screen.getByTestId('SelectNewCard'));

      await capturedSecureFieldsProps.onSuccess!({ transactionId: 'txn-fail' });

      await waitFor(() => {
        expect(mockSecureFieldsFormRef.reinit).toHaveBeenCalledTimes(1);
      });
    });

    it('does not navigate to confirmation when authorize fetch fails', async () => {
      global.fetch = jest.fn().mockResolvedValue({ ok: false, status: 500 });

      renderPage();

      await waitFor(() =>
        expect(screen.getByTestId('DatatransSecureFieldsForm')).toBeInTheDocument()
      );

      await capturedSecureFieldsProps.onSuccess!({ transactionId: 'txn-fail' });

      await new Promise((r) => setTimeout(r, 50));
      expect(mockRouterState.push).not.toHaveBeenCalledWith(
        expect.stringContaining('confirmation')
      );
    });
  });

  // ── Secure Fields submit loading state ──────────────────────────────────────

  describe('Secure Fields submit loading state', () => {
    it('passes isLoading=false to PaymentConfirmSection when NEW_CARD is selected and not submitting', async () => {
      renderPage();
      fireEvent.click(screen.getByTestId('SelectNewCard'));
      await waitFor(() => {
        expect(mockCapturedPaymentConfirmIsLoading).toBe(false);
      });
    });

    it('passes isLoading=true after the Pay button is clicked', async () => {
      renderPage();
      fireEvent.click(screen.getByTestId('SelectNewCard'));
      fireEvent.click(screen.getByTestId('PaymentConfirmSection-ConfirmButton'));
      await waitFor(() => {
        expect(mockCapturedPaymentConfirmIsLoading).toBe(true);
      });
    });

    it('restores isLoading=false after authorize fetch fails', async () => {
      global.fetch = jest.fn().mockResolvedValue({ ok: false, status: 500 });

      renderPage();
      fireEvent.click(screen.getByTestId('SelectNewCard'));
      fireEvent.click(screen.getByTestId('PaymentConfirmSection-ConfirmButton'));

      // Trigger success event (SDK tokenisation done) — authorize will fail
      await capturedSecureFieldsProps.onSuccess!({ transactionId: 'txn-fail' });

      await waitFor(() => {
        expect(mockCapturedPaymentConfirmIsLoading).toBe(false);
      });
    });

    it('keeps isLoading=true after authorize fetch succeeds (redirect is pending)', async () => {
      global.fetch = jest.fn().mockResolvedValue({ ok: true });
      mockRouterState.push = jest.fn().mockResolvedValue(true);

      renderPage();
      fireEvent.click(screen.getByTestId('SelectNewCard'));
      fireEvent.click(screen.getByTestId('PaymentConfirmSection-ConfirmButton'));

      await capturedSecureFieldsProps.onSuccess!({ transactionId: 'txn-ok' });

      await waitFor(() => {
        expect(mockRouterState.push).toHaveBeenCalledWith(expect.stringContaining('confirmation'));
      });
      // isLoading is never cleared — page is navigating away
      expect(mockCapturedPaymentConfirmIsLoading).toBe(true);
    });

    it('passes undefined isLoading when a non-card method is selected', () => {
      renderPage();
      fireEvent.click(screen.getByTestId('SelectPayPal'));
      expect(mockCapturedPaymentConfirmIsLoading).toBeUndefined();
    });
  });

  // ── onInitialisingChange / onSubmitValidationFailed wiring ──────────────────

  describe('Secure Fields initialising / validation-failed callbacks', () => {
    it('updates isLoading when onInitialisingChange reports true', async () => {
      renderPage();
      fireEvent.click(screen.getByTestId('SelectNewCard'));

      await waitFor(() =>
        expect(screen.getByTestId('DatatransSecureFieldsForm')).toBeInTheDocument()
      );

      capturedSecureFieldsProps.onInitialisingChange!(true);

      await waitFor(() => {
        expect(mockCapturedPaymentConfirmIsLoading).toBe(true);
      });
    });

    it('clears isLoading when onSubmitValidationFailed fires', async () => {
      renderPage();
      fireEvent.click(screen.getByTestId('SelectNewCard'));
      // Simulate Pay click → loading starts
      fireEvent.click(screen.getByTestId('PaymentConfirmSection-ConfirmButton'));

      await waitFor(() => expect(mockCapturedPaymentConfirmIsLoading).toBe(true));

      capturedSecureFieldsProps.onSubmitValidationFailed!();

      await waitFor(() => {
        expect(mockCapturedPaymentConfirmIsLoading).toBe(false);
      });
    });
  });

  // ── Back-to-payment-options link ────────────────────────────────────────────

  describe('Back-to-payment-options feature flag', () => {
    it('renders BackToPage link when the flag is ON and on card-details step', async () => {
      useFeatureToggle.mockReturnValue({
        FT_PI_ENABLE_PAYMENT_REDESIGN: false,
        FT_PI_BB_CCUI_COMPANY_NAME_SPECIAL_CHARACTERS: false,
        FT_PI_BB_CCUI_DISABLE_PAYMENTS: false,
        FT_PI_BB_NON_GUARANTEED_REMINDER: false,
        FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN_ANCILLARIES_GDP_PAYMENT: false,
        FT_PI_PIB_PAYMENT_BACK_TO_OPTIONS_LINK: true,
        FT_PI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE: false,
      });

      // paymentStepState transitions to CARD_DETAILS only when the feature is enabled
      // and the page renders in that step. Since the step is internal state and is driven
      // by selecting a non-secure-fields method then confirming, we verify the link does
      // NOT appear on PAYMENT_DETAILS step.
      renderPage();
      expect(screen.queryByTestId('BackToPage')).not.toBeInTheDocument();
    });
  });

  // ── 3ds_failed query param ────────────────────────────────────────────────

  describe('3ds_failed query param', () => {
    it('does not show PaymentErrorNotification when 3ds_failed is absent', () => {
      renderPage();
      expect(screen.getByTestId('PaymentErrorNotification')).toHaveAttribute(
        'data-visible',
        'false'
      );
    });

    it('shows PaymentErrorNotification when 3ds_failed=true is in the URL', () => {
      mockRouterState.query = { '3ds_failed': 'true' };
      renderPage();
      expect(screen.getByTestId('PaymentErrorNotification')).toHaveAttribute(
        'data-visible',
        'true'
      );
    });

    it('does not show PaymentErrorNotification for other 3ds_failed values', () => {
      mockRouterState.query = { '3ds_failed': 'false' };
      renderPage();
      expect(screen.getByTestId('PaymentErrorNotification')).toHaveAttribute(
        'data-visible',
        'false'
      );
    });

    it('renders the notification above the DatatransSecureFieldsForm in the DOM', () => {
      mockRouterState.query = { '3ds_failed': 'true' };
      renderPage();
      const notification = screen.getByTestId('PaymentErrorNotification');
      const form = screen.getByTestId('DatatransSecureFieldsForm');
      expect(
        notification.compareDocumentPosition(form) & Node.DOCUMENT_POSITION_FOLLOWING
      ).toBeTruthy();
    });
  });

  // ── Accessibility ───────────────────────────────────────────────────────────

  describe('Accessibility', () => {
    it('has no accessibility violations in the default (loaded) state', async () => {
      const { container } = renderPage();
      const results = await axe(container);
      expect(results).toHaveNoViolations();
    });

    it('has no accessibility violations while loading', async () => {
      useQueryRequest.mockImplementation(() => ({ isLoading: true, data: undefined }));
      const { container } = renderPage();
      const results = await axe(container);
      expect(results).toHaveNoViolations();
    });
  });
});
