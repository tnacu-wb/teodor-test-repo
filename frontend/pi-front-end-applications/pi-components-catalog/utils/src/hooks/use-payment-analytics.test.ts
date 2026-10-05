import { act, renderHook } from '@testing-library/react';
import {
  PAYMENT_ANALYTICS_KEY,
  PaymentOption,
  paymentOptions,
  paymentSteps,
} from '@whitbread-eos/api';

import { usePaymentAnalytics } from './use-payment-analytics';

// ---------------------------------------------------------------------------
// Mocks
// ---------------------------------------------------------------------------

jest.mock('../services/analyticsService/analytics', () => ({
  __esModule: true,
  default: { update: jest.fn() },
}));

jest.mock('../services/analyticsService/dashboardAnalytics', () => ({
  __esModule: true,
  default: jest.fn(),
}));

jest.mock('../helpers/payment', () => ({
  ...jest.requireActual('../helpers/payment'),
  isSecureBookingPage: jest.fn(),
}));

const mockAnalytics = jest.requireMock('../services/analyticsService/analytics').default;
const mockUpdateDashboardAnalytics = jest.requireMock(
  '../services/analyticsService/dashboardAnalytics'
).default;
const mockIsSecureBookingPage = jest.requireMock('../helpers/payment').isSecureBookingPage;

const sessionStorageMock = (() => {
  let store: Record<string, string> = {};
  return {
    getItem: (key: string) => store[key] ?? null,
    setItem: (key: string, value: string) => {
      store[key] = value;
    },
    removeItem: (key: string) => {
      delete store[key];
    },
    clear: () => {
      store = {};
    },
  };
})();

const localStorageMock = (() => {
  let store: Record<string, string> = {};
  return {
    getItem: (key: string) => store[key] ?? null,
    setItem: (key: string, value: string) => {
      store[key] = value;
    },
    removeItem: (key: string) => {
      delete store[key];
    },
    clear: () => {
      store = {};
    },
  };
})();

Object.defineProperty(window, 'sessionStorage', { value: sessionStorageMock });
Object.defineProperty(window, 'localStorage', { value: localStorageMock });

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

const defaultPaymentDetail: PaymentOption = {
  type: paymentOptions.PAY_NOW,
  order: 1,
  enabled: true,
};
const rwcPaymentDetail: PaymentOption = {
  type: paymentOptions.RESERVE_WITHOUT_CARD,
  order: 2,
  enabled: true,
};

function buildParams(overrides: Partial<Parameters<typeof usePaymentAnalytics>[0]> = {}) {
  return {
    paymentStepState: paymentSteps.PAYMENT_DETAILS,
    basketReference: 'BASKET-123',
    paymentCardSelected: 'VISA',
    selectedPaymentDetail: defaultPaymentDetail,
    disablePaymentOptions: false,
    isPaypalSuccess: false,
    initiatePaypalPaymentMutationData: undefined,
    routerQuery: {},
    isSecureBookingFeatureEnabled: false,
    bkngData: undefined,
    packages: undefined,
    updateAncillariesAnalytics: jest.fn(),
    ...overrides,
  };
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('usePaymentAnalytics', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    sessionStorageMock.clear();
    localStorageMock.clear();
    mockIsSecureBookingPage.mockReturnValue(false);
  });

  describe('render-time hasPaymentFailure reset', () => {
    it('calls analytics.update({ hasPaymentFailure: undefined }) on every render', () => {
      const { rerender } = renderHook(() => usePaymentAnalytics(buildParams()));
      expect(mockAnalytics.update).toHaveBeenCalledWith({ hasPaymentFailure: undefined });

      rerender();
      // Called once per render
      expect(
        mockAnalytics.update.mock.calls.filter((c: any[]) => 'hasPaymentFailure' in c[0]).length
      ).toBeGreaterThanOrEqual(2);
    });
  });

  describe('confAnalytics session storage', () => {
    it('initialises with empty object when sessionStorage has no entry', () => {
      const { result } = renderHook(() => usePaymentAnalytics(buildParams()));
      expect(result.current.confAnalytics).toEqual({});
    });

    it('reads an existing sessionStorage entry as the initial value', () => {
      const existing = { paymentCardSelected: 'MC', paymentTakenNow: 'true' };
      sessionStorageMock.setItem(PAYMENT_ANALYTICS_KEY, JSON.stringify(existing));
      const { result } = renderHook(() => usePaymentAnalytics(buildParams()));
      expect(result.current.confAnalytics).toEqual(existing);
    });
  });

  describe('payment step effect', () => {
    it('records card-details step analytics and sets 3cpVisited in localStorage', () => {
      renderHook(() =>
        usePaymentAnalytics(
          buildParams({
            paymentStepState: paymentSteps.CARD_DETAILS,
            paymentCardSelected: 'VISA',
            selectedPaymentDetail: defaultPaymentDetail,
          })
        )
      );

      expect(localStorageMock.getItem('3cpVisited')).toBe('BASKET-123');
      expect(mockAnalytics.update).toHaveBeenCalledWith({
        paymentCardSelected: 'VISA',
        paymentTakenNow: 'true',
      });
    });

    it('sets paymentTakenNow to "false" when selectedPaymentDetail is not PAY_NOW', () => {
      const payLaterDetail: PaymentOption = { type: 'PAY_ON_ARRIVAL', order: 1, enabled: true };
      renderHook(() =>
        usePaymentAnalytics(
          buildParams({
            paymentStepState: paymentSteps.CARD_DETAILS,
            selectedPaymentDetail: payLaterDetail,
          })
        )
      );

      expect(mockAnalytics.update).toHaveBeenCalledWith(
        expect.objectContaining({ paymentTakenNow: 'false' })
      );
    });

    it('records RESERVE_WITHOUT_CARD on payment-details step', () => {
      renderHook(() =>
        usePaymentAnalytics(
          buildParams({
            paymentStepState: paymentSteps.PAYMENT_DETAILS,
            selectedPaymentDetail: rwcPaymentDetail,
          })
        )
      );

      expect(mockAnalytics.update).toHaveBeenCalledWith({
        paymentCardSelected: paymentOptions.RESERVE_WITHOUT_CARD,
        paymentTakenNow: 'false',
      });
    });

    it('does NOT fire card-details analytics when basketReference is null', () => {
      renderHook(() =>
        usePaymentAnalytics(
          buildParams({
            paymentStepState: paymentSteps.CARD_DETAILS,
            basketReference: null,
          })
        )
      );

      expect(mockAnalytics.update).not.toHaveBeenCalledWith(
        expect.objectContaining({
          paymentCardSelected: expect.anything(),
          paymentTakenNow: expect.anything(),
        })
      );
    });

    it('does NOT fire RESERVE_WITHOUT_CARD analytics when basketReference is null', () => {
      renderHook(() =>
        usePaymentAnalytics(
          buildParams({
            paymentStepState: paymentSteps.PAYMENT_DETAILS,
            basketReference: null,
            selectedPaymentDetail: rwcPaymentDetail,
          })
        )
      );

      expect(mockAnalytics.update).not.toHaveBeenCalledWith(
        expect.objectContaining({ paymentCardSelected: paymentOptions.RESERVE_WITHOUT_CARD })
      );
    });

    it('does NOT fire RESERVE_WITHOUT_CARD analytics when payment type differs', () => {
      renderHook(() =>
        usePaymentAnalytics(
          buildParams({
            paymentStepState: paymentSteps.PAYMENT_DETAILS,
            selectedPaymentDetail: defaultPaymentDetail,
          })
        )
      );

      expect(mockAnalytics.update).not.toHaveBeenCalledWith(
        expect.objectContaining({ paymentCardSelected: paymentOptions.RESERVE_WITHOUT_CARD })
      );
    });

    it('re-fires analytics when paymentStepState changes', () => {
      const params = buildParams({ paymentStepState: paymentSteps.PAYMENT_DETAILS });
      const { rerender } = renderHook((p) => usePaymentAnalytics(p), { initialProps: params });

      const nextParams = buildParams({
        paymentStepState: paymentSteps.CARD_DETAILS,
        paymentCardSelected: 'MC',
      });
      rerender(nextParams);

      expect(mockAnalytics.update).toHaveBeenCalledWith(
        expect.objectContaining({ paymentCardSelected: 'MC' })
      );
    });
  });

  describe('payment outage effect', () => {
    it('fires outage analytics when disablePaymentOptions is true', () => {
      renderHook(() => usePaymentAnalytics(buildParams({ disablePaymentOptions: true })));

      expect(mockAnalytics.update).toHaveBeenCalledWith({
        paymentCardSelected: paymentOptions.RESERVE_WITHOUT_CARD,
        paymentOutage: true,
        cardType: paymentOptions.RESERVE_WITHOUT_CARD,
      });
    });

    it('does NOT fire outage analytics when disablePaymentOptions is false', () => {
      renderHook(() => usePaymentAnalytics(buildParams({ disablePaymentOptions: false })));

      expect(mockAnalytics.update).not.toHaveBeenCalledWith(
        expect.objectContaining({ paymentOutage: expect.anything() })
      );
    });

    it('re-fires outage analytics when disablePaymentOptions flips to true', () => {
      const params = buildParams({ disablePaymentOptions: false });
      const { rerender } = renderHook((p) => usePaymentAnalytics(p), { initialProps: params });

      rerender(buildParams({ disablePaymentOptions: true }));

      expect(mockAnalytics.update).toHaveBeenCalledWith(
        expect.objectContaining({ paymentOutage: true })
      );
    });
  });

  describe('PayPal success effect', () => {
    it('fires paypal analytics when status is NOT_REQUIRED', () => {
      renderHook(() =>
        usePaymentAnalytics(
          buildParams({
            isPaypalSuccess: true,
            initiatePaypalPaymentMutationData: {
              initiatePaypalPayment: { status: 'NOT_REQUIRED' },
            },
            selectedPaymentDetail: defaultPaymentDetail,
          })
        )
      );

      expect(mockAnalytics.update).toHaveBeenCalledWith({
        paypal: true,
        paymentTakenNow: 'true',
      });
    });

    it('does NOT fire paypal analytics when isPaypalSuccess is false', () => {
      renderHook(() =>
        usePaymentAnalytics(
          buildParams({
            isPaypalSuccess: false,
            initiatePaypalPaymentMutationData: {
              initiatePaypalPayment: { status: 'NOT_REQUIRED' },
            },
          })
        )
      );

      expect(mockAnalytics.update).not.toHaveBeenCalledWith(
        expect.objectContaining({ paypal: true })
      );
    });

    it('does NOT fire paypal analytics when status is not NOT_REQUIRED', () => {
      renderHook(() =>
        usePaymentAnalytics(
          buildParams({
            isPaypalSuccess: true,
            initiatePaypalPaymentMutationData: {
              initiatePaypalPayment: { status: 'PENDING' },
            },
          })
        )
      );

      expect(mockAnalytics.update).not.toHaveBeenCalledWith(
        expect.objectContaining({ paypal: true })
      );
    });

    it('sets paymentTakenNow to "false" when selectedPaymentDetail is not PAY_NOW', () => {
      renderHook(() =>
        usePaymentAnalytics(
          buildParams({
            isPaypalSuccess: true,
            initiatePaypalPaymentMutationData: {
              initiatePaypalPayment: { status: 'NOT_REQUIRED' },
            },
            selectedPaymentDetail: rwcPaymentDetail,
          })
        )
      );

      expect(mockAnalytics.update).toHaveBeenCalledWith(
        expect.objectContaining({ paypal: true, paymentTakenNow: 'false' })
      );
    });

    it('re-fires analytics when initiatePaypalPaymentMutationData changes to NOT_REQUIRED', () => {
      const params = buildParams({
        isPaypalSuccess: true,
        initiatePaypalPaymentMutationData: { initiatePaypalPayment: { status: 'PENDING' } },
      });
      const { rerender } = renderHook((p) => usePaymentAnalytics(p), { initialProps: params });

      rerender(
        buildParams({
          isPaypalSuccess: true,
          initiatePaypalPaymentMutationData: { initiatePaypalPayment: { status: 'NOT_REQUIRED' } },
        })
      );

      expect(mockAnalytics.update).toHaveBeenCalledWith(expect.objectContaining({ paypal: true }));
    });
  });

  describe('secure-booking dashboard effect', () => {
    it('calls updateDashboardAnalytics when isSecureBookingPage returns true', () => {
      mockIsSecureBookingPage.mockReturnValue(true);
      renderHook(() =>
        usePaymentAnalytics(
          buildParams({
            routerQuery: { 'secure-booking': 'true' },
            isSecureBookingFeatureEnabled: true,
          })
        )
      );

      expect(mockUpdateDashboardAnalytics).toHaveBeenCalledWith({ secureBookingAction: true });
    });

    it('does NOT call updateDashboardAnalytics when isSecureBookingPage returns false', () => {
      mockIsSecureBookingPage.mockReturnValue(false);
      renderHook(() => usePaymentAnalytics(buildParams()));

      expect(mockUpdateDashboardAnalytics).not.toHaveBeenCalled();
    });

    it('re-fires when routerQuery changes to a secure-booking query', () => {
      mockIsSecureBookingPage.mockReturnValue(false);
      const params = buildParams();
      const { rerender } = renderHook((p) => usePaymentAnalytics(p), { initialProps: params });

      mockIsSecureBookingPage.mockReturnValue(true);
      rerender(
        buildParams({
          routerQuery: { 'secure-booking': 'true' },
          isSecureBookingFeatureEnabled: true,
        })
      );

      expect(mockUpdateDashboardAnalytics).toHaveBeenCalledWith({ secureBookingAction: true });
    });
  });

  describe('ancillaries analytics effect', () => {
    it('fires updateAncillariesAnalytics when bkngData and packages are both present', () => {
      const updateAncillariesAnalytics = jest.fn();
      const bkngData = { bookingInformation: { hotelId: 'H1' } };
      const packages = { meals: [] };
      renderHook(() =>
        usePaymentAnalytics(buildParams({ bkngData, packages, updateAncillariesAnalytics }))
      );
      expect(updateAncillariesAnalytics).toHaveBeenCalledWith(
        bkngData.bookingInformation,
        packages
      );
    });

    it('does NOT fire when bkngData is absent', () => {
      const updateAncillariesAnalytics = jest.fn();
      renderHook(() =>
        usePaymentAnalytics(
          buildParams({ bkngData: undefined, packages: { meals: [] }, updateAncillariesAnalytics })
        )
      );
      expect(updateAncillariesAnalytics).not.toHaveBeenCalled();
    });

    it('does NOT fire when packages is absent', () => {
      const updateAncillariesAnalytics = jest.fn();
      renderHook(() =>
        usePaymentAnalytics(
          buildParams({
            bkngData: { bookingInformation: { hotelId: 'H1' } },
            packages: undefined,
            updateAncillariesAnalytics,
          })
        )
      );
      expect(updateAncillariesAnalytics).not.toHaveBeenCalled();
    });

    it('re-fires when bkngData changes', () => {
      const updateAncillariesAnalytics = jest.fn();
      const packages = { meals: [] };
      const params = buildParams({
        bkngData: { bookingInformation: { hotelId: 'H1' } },
        packages,
        updateAncillariesAnalytics,
      });
      const { rerender } = renderHook((p) => usePaymentAnalytics(p), { initialProps: params });
      rerender(
        buildParams({
          bkngData: { bookingInformation: { hotelId: 'H2' } },
          packages,
          updateAncillariesAnalytics,
        })
      );
      expect(updateAncillariesAnalytics).toHaveBeenCalledTimes(2);
    });
  });

  describe('return value', () => {
    it('exposes confAnalytics and setConfAnalytics', () => {
      const { result } = renderHook(() => usePaymentAnalytics(buildParams()));
      expect(result.current).toHaveProperty('confAnalytics');
      expect(result.current).toHaveProperty('setConfAnalytics');
      expect(typeof result.current.setConfAnalytics).toBe('function');
    });

    it('setConfAnalytics updates confAnalytics', () => {
      const { result } = renderHook(() => usePaymentAnalytics(buildParams()));
      act(() => {
        result.current.setConfAnalytics({ paymentCardSelected: 'AMEX', paymentTakenNow: 'true' });
      });
      expect(result.current.confAnalytics).toEqual({
        paymentCardSelected: 'AMEX',
        paymentTakenNow: 'true',
      });
    });
  });
});
