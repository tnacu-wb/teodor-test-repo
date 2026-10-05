import { ChakraProvider } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import React from 'react';

import { render, screen, waitFor } from '~utils/test-utils';

import {
  PAYMENT_NAVIGATION_SOURCE_GUEST_DETAILS,
  PAYMENT_NAVIGATION_SOURCE_QUERY_PARAM,
} from '../../utils/pi-all-pages-constants';
import { DatatransWith3dsReturn, DatatransPageWrapper } from './page.pi.datatrans';

// ─── Mocks ────────────────────────────────────────────────────────────────────

const mockRouterReplace = jest.fn();
const mockRouterQuery = jest.fn(() => ({}));

jest.mock('next/router', () => ({
  useRouter: () => ({ replace: mockRouterReplace, query: mockRouterQuery() }),
}));

jest.mock('@whitbread-eos/utils', () => ({
  useCustomLocale: () => ({ language: 'en', country: 'gb' }),
  usePaymentPaypal: () => ({ injectPaypalProvider: false, paypalPaymentData: null }),
}));

jest.mock('~components/payment/datatrans/DatatransPage', () => ({
  DatatransPage: () => <div data-testid="DatatransPage" />,
}));

jest.mock('@whitbread-eos/atoms', () => ({
  LoadingSpinner: ({ loadingText }: { loadingText: string }) => (
    <div data-testid="LoadingSpinner">{loadingText}</div>
  ),
}));

jest.mock('next-i18next', () => ({
  useTranslation: () => ({ t: (key: string) => key }),
}));

// jsdom doesn't support window.location assignment — replace with a writable mock.
const mockLocationAssign = jest.fn();
Object.defineProperty(window, 'location', {
  value: { href: '', assign: mockLocationAssign },
  writable: true,
  configurable: true,
});

// ─── Helpers ──────────────────────────────────────────────────────────────────

const validHiQueryInput = { hotelId: 'hotel-001', country: 'gb', language: 'en' };
const validPcksQueryInput = {
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
};

const wrap = (ui: React.ReactElement) => render(<ChakraProvider>{ui}</ChakraProvider>);

const render3dsReturn = (basketReference: string | null = 'basket-001') =>
  wrap(<DatatransWith3dsReturn basketReference={basketReference} />);

const renderWrapper = (
  overrides: Partial<React.ComponentProps<typeof DatatransPageWrapper>> = {}
) =>
  wrap(
    <DatatransPageWrapper
      hiQueryInput={validHiQueryInput}
      pcksQueryInput={validPcksQueryInput}
      basketReference="basket-001"
      {...overrides}
    />
  );

// ─── Tests ────────────────────────────────────────────────────────────────────

describe('DatatransWith3dsReturn', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    global.fetch = jest.fn();
    sessionStorage.clear();
    window.location.href = '';
    mockRouterQuery.mockReturnValue({});
  });

  it('renders a loading spinner while authorize is in flight', () => {
    (global.fetch as jest.Mock).mockResolvedValueOnce({ ok: true, status: 200 });
    render3dsReturn();
    expect(screen.getByTestId('LoadingSpinner')).toBeInTheDocument();
  });

  it('calls POST /api/payments/authorize with the basketId', async () => {
    (global.fetch as jest.Mock).mockResolvedValueOnce({ ok: true, status: 200 });
    render3dsReturn();
    await waitFor(() => {
      expect(global.fetch).toHaveBeenCalledWith(
        '/api/payments/authorize',
        expect.objectContaining({
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ basketId: 'basket-001' }),
        })
      );
    });
  });

  it('does not call authorize when basketReference is null', () => {
    render3dsReturn(null);
    expect(global.fetch).not.toHaveBeenCalled();
  });

  describe('authorize succeeds', () => {
    it('redirects to the confirmation path', async () => {
      (global.fetch as jest.Mock).mockResolvedValueOnce({ ok: true, status: 200 });
      render3dsReturn();
      await waitFor(() => {
        expect(mockRouterReplace).toHaveBeenCalledWith(
          expect.stringContaining('/confirmation?reservationId=basket-001')
        );
      });
    });

    it('includes bookingFlowId in the confirmation path when stored in sessionStorage', async () => {
      sessionStorage.setItem('bookingFlowId', 'bf-test');
      (global.fetch as jest.Mock).mockResolvedValueOnce({ ok: true, status: 200 });
      render3dsReturn();
      await waitFor(() => {
        expect(mockRouterReplace).toHaveBeenCalledWith(
          expect.stringContaining('/bf-test/confirmation')
        );
      });
    });
  });

  describe('authorize fails (non-ok response)', () => {
    it('navigates to the payment page with 3ds_failed=true via hard reload', async () => {
      (global.fetch as jest.Mock).mockResolvedValueOnce({ ok: false, status: 401 });
      render3dsReturn();
      await waitFor(() => {
        expect(window.location.href).toContain('3ds_failed=true');
      });
    });

    it('preserves guest-details source when navigating to payment after 3DS failure', async () => {
      mockRouterQuery.mockReturnValue({
        [PAYMENT_NAVIGATION_SOURCE_QUERY_PARAM]: PAYMENT_NAVIGATION_SOURCE_GUEST_DETAILS,
      });
      (global.fetch as jest.Mock).mockResolvedValueOnce({ ok: false, status: 401 });

      render3dsReturn();

      await waitFor(() => {
        expect(window.location.href).toContain('3ds_failed=true');
        expect(window.location.href).toContain(
          `${PAYMENT_NAVIGATION_SOURCE_QUERY_PARAM}=${PAYMENT_NAVIGATION_SOURCE_GUEST_DETAILS}`
        );
      });
    });

    it('does not call router.replace on failure', async () => {
      (global.fetch as jest.Mock).mockResolvedValueOnce({ ok: false, status: 401 });
      render3dsReturn();
      await waitFor(() => expect(window.location.href).toContain('3ds_failed'));
      expect(mockRouterReplace).not.toHaveBeenCalled();
    });
  });

  describe('authorize fails (network error)', () => {
    it('navigates to the payment page with 3ds_failed=true via hard reload', async () => {
      (global.fetch as jest.Mock).mockRejectedValueOnce(new Error('Network failure'));
      render3dsReturn();
      await waitFor(() => {
        expect(window.location.href).toContain('3ds_failed=true');
      });
    });
  });

  describe('double-invoke guard (authorizeCalledRef)', () => {
    it('only calls authorize once even when the effect runs twice', async () => {
      (global.fetch as jest.Mock).mockResolvedValue({ ok: true, status: 200 });
      const { rerender } = render3dsReturn();
      rerender(
        <ChakraProvider>
          <DatatransWith3dsReturn basketReference="basket-001" />
        </ChakraProvider>
      );
      await waitFor(() => expect(mockRouterReplace).toHaveBeenCalled());
      expect(global.fetch).toHaveBeenCalledTimes(1);
    });
  });
});

// ─────────────────────────────────────────────────────────────────────────────

describe('DatatransPageWrapper', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    global.fetch = jest.fn();
    sessionStorage.clear();
  });

  describe('isDatatransReturn=true', () => {
    it('renders the loading spinner (delegates to DatatransWith3dsReturn)', () => {
      (global.fetch as jest.Mock).mockResolvedValue({ ok: true });
      renderWrapper({ isDatatransReturn: true });
      expect(screen.getByTestId('LoadingSpinner')).toBeInTheDocument();
      expect(screen.queryByTestId('DatatransPage')).not.toBeInTheDocument();
    });

    it('calls authorize', async () => {
      (global.fetch as jest.Mock).mockResolvedValueOnce({ ok: true, status: 200 });
      renderWrapper({ isDatatransReturn: true });
      await waitFor(() => expect(global.fetch).toHaveBeenCalledTimes(1));
    });
  });

  describe('isDatatransReturn=false (default)', () => {
    it('does not call authorize', () => {
      renderWrapper();
      expect(global.fetch).not.toHaveBeenCalled();
    });

    it('renders DatatransPage when both inputs are provided', () => {
      renderWrapper();
      expect(screen.getByTestId('DatatransPage')).toBeInTheDocument();
    });

    it('renders nothing when hiQueryInput is null', () => {
      renderWrapper({ hiQueryInput: null });
      expect(screen.queryByTestId('DatatransPage')).not.toBeInTheDocument();
    });

    it('renders nothing when pcksQueryInput is null', () => {
      renderWrapper({ pcksQueryInput: null });
      expect(screen.queryByTestId('DatatransPage')).not.toBeInTheDocument();
    });
  });
});
