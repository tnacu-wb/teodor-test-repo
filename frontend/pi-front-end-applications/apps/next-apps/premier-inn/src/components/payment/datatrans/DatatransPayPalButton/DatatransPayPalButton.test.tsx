import { ChakraProvider } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import { axe, toHaveNoViolations } from 'jest-axe';
import React from 'react';

import { act, render, screen, waitFor } from '~utils/test-utils';

import { DatatransPayPalButton } from './DatatransPayPalButton';

expect.extend(toHaveNoViolations);

// ─── next/script mock ────────────────────────────────────────────────────────
// Controllable flag: set `global.__simulateScriptError = true` before rendering
// to make the mock fire onError instead of onReady.

jest.mock('next/script', () => {
  const MockScript = ({
    onReady,
    onError,
  }: {
    src: string;
    onReady?: () => void;
    onError?: () => void;
  }) => {
    // Access the flag via the module-level variable (closure over the test file scope)
    React.useEffect(() => {
      if ((global as Record<string, unknown>).__simulateScriptError) {
        onError?.();
      } else {
        onReady?.();
      }
    }, []); // eslint-disable-line react-hooks/exhaustive-deps
    return null;
  };
  MockScript.displayName = 'MockScript';
  return MockScript;
});

// ─── Helpers ────────────────────────────────────────────────────────────────

const defaultProps = {
  basketId: 'basket-abc',
  amount: '100.00',
  currencyCode: 'GBP',
  onAuthorization: jest.fn(),
  onError: jest.fn(),
};

const renderComponent = (props: Partial<typeof defaultProps> = {}) =>
  render(
    <ChakraProvider>
      <DatatransPayPalButton {...defaultProps} {...props} />
    </ChakraProvider>
  );

// ─── PayPalButton SDK mock factory ──────────────────────────────────────────

type EventCallback = (...args: unknown[]) => void;

function makePayPalButtonSDK() {
  const listeners: Record<string, EventCallback> = {};

  const sdk = {
    init: jest.fn(),
    on: jest.fn((event: string, cb: EventCallback) => {
      listeners[event] = cb;
    }),
    create: jest.fn(),
    _emit: (event: string, ...args: unknown[]) => {
      listeners[event]?.(...args);
    },
  };

  return sdk;
}

// ─── Tests ───────────────────────────────────────────────────────────────────

describe('DatatransPayPalButton', () => {
  const originalEnv = process.env;

  beforeEach(() => {
    jest.clearAllMocks();
    (global as Record<string, unknown>).__simulateScriptError = false;
    process.env = {
      ...originalEnv,
      NEXT_PUBLIC_DATATRANS_PAYPAL_BUTTON_URL:
        'https://pay.datatrans.com/upp/payment/js/paypal-button.js',
      NEXT_PUBLIC_DATATRANS_MERCHANT_ID: 'merchant-123',
    };
  });

  afterEach(() => {
    process.env = originalEnv;
    delete (window as Window & typeof globalThis).PayPalButton;
    delete (global as Record<string, unknown>).__simulateScriptError;
  });

  // ─── Early-exit guard ──────────────────────────────────────────────────────

  describe('when scriptUrl env var is not set', () => {
    it('renders nothing', () => {
      process.env.NEXT_PUBLIC_DATATRANS_PAYPAL_BUTTON_URL = '';
      renderComponent();
      expect(screen.queryByTestId('DatatransPayPalButton-Container')).not.toBeInTheDocument();
    });
  });

  // ─── Container rendering ──────────────────────────────────────────────────

  describe('container element', () => {
    it('renders the container div with the correct data-testid', () => {
      renderComponent();
      expect(screen.getByTestId('DatatransPayPalButton-Container')).toBeInTheDocument();
    });
  });

  // ─── merchantId guard ────────────────────────────────────────────────────

  describe('when merchantId env var is not set', () => {
    it('calls onError with a configuration error after the script loads', async () => {
      process.env.NEXT_PUBLIC_DATATRANS_MERCHANT_ID = '';
      const sdk = makePayPalButtonSDK();
      (window as Window & typeof globalThis).PayPalButton =
        sdk as unknown as typeof window.PayPalButton;
      const onError = jest.fn();

      renderComponent({ onError });

      await waitFor(() => {
        expect(onError).toHaveBeenCalledWith(
          expect.objectContaining({ message: 'Datatrans merchant ID is not configured' })
        );
      });
    });
  });

  // ─── SDK initialisation ───────────────────────────────────────────────────

  describe('SDK initialisation', () => {
    it('calls PayPalButton.init with the correct merchant config', async () => {
      const sdk = makePayPalButtonSDK();
      (window as Window & typeof globalThis).PayPalButton =
        sdk as unknown as typeof window.PayPalButton;

      renderComponent();

      await waitFor(() => {
        expect(sdk.init).toHaveBeenCalledWith(
          expect.objectContaining({
            merchantId: 'merchant-123',
            currencyCode: 'GBP',
            amount: '100.00',
            autoSettle: false,
            createAlias: false,
            reqType: 'CAA',
          })
        );
      });
    });

    it('calls PayPalButton.create with the container element on the "init" event', async () => {
      const sdk = makePayPalButtonSDK();
      (window as Window & typeof globalThis).PayPalButton =
        sdk as unknown as typeof window.PayPalButton;

      renderComponent();

      await waitFor(() => expect(sdk.init).toHaveBeenCalled());

      act(() => {
        sdk._emit('init');
      });

      await waitFor(() => {
        expect(sdk.create).toHaveBeenCalledWith(
          screen.getByTestId('DatatransPayPalButton-Container'),
          expect.objectContaining({
            paypaloptions: expect.objectContaining({
              button: expect.objectContaining({ color: 'gold', label: 'paypal' }),
            }),
            transaction: expect.objectContaining({
              refno: 'basket-abc',
              returnAddress: true,
            }),
          })
        );
      });
    });

    it('does not re-register listeners when props change (isInitializedRef guard)', async () => {
      const sdk = makePayPalButtonSDK();
      (window as Window & typeof globalThis).PayPalButton =
        sdk as unknown as typeof window.PayPalButton;

      const { rerender } = renderComponent({ amount: '100.00' });

      await waitFor(() => expect(sdk.init).toHaveBeenCalledTimes(1));

      rerender(
        <ChakraProvider>
          <DatatransPayPalButton {...defaultProps} amount="200.00" />
        </ChakraProvider>
      );

      // Still exactly once — the isInitializedRef guard prevents re-registration
      await waitFor(() => expect(sdk.init).toHaveBeenCalledTimes(1));
    });
  });

  // ─── Authorization event ─────────────────────────────────────────────────

  describe('"authorization" event', () => {
    it('calls onAuthorization with the event data', async () => {
      const sdk = makePayPalButtonSDK();
      (window as Window & typeof globalThis).PayPalButton =
        sdk as unknown as typeof window.PayPalButton;
      const onAuthorization = jest.fn();

      renderComponent({ onAuthorization });

      await waitFor(() =>
        expect(sdk.on).toHaveBeenCalledWith('authorization', expect.any(Function))
      );

      act(() => {
        sdk._emit('authorization', { transactionId: 'txn-xyz' });
      });

      expect(onAuthorization).toHaveBeenCalledWith({ transactionId: 'txn-xyz' });
    });

    it('uses the latest onAuthorization ref when the prop changes between mount and event', async () => {
      const sdk = makePayPalButtonSDK();
      (window as Window & typeof globalThis).PayPalButton =
        sdk as unknown as typeof window.PayPalButton;
      const firstHandler = jest.fn();
      const secondHandler = jest.fn();

      const { rerender } = render(
        <ChakraProvider>
          <DatatransPayPalButton {...defaultProps} onAuthorization={firstHandler} />
        </ChakraProvider>
      );

      await waitFor(() =>
        expect(sdk.on).toHaveBeenCalledWith('authorization', expect.any(Function))
      );

      rerender(
        <ChakraProvider>
          <DatatransPayPalButton {...defaultProps} onAuthorization={secondHandler} />
        </ChakraProvider>
      );

      act(() => {
        sdk._emit('authorization', { transactionId: 'txn-xyz' });
      });

      expect(firstHandler).not.toHaveBeenCalled();
      expect(secondHandler).toHaveBeenCalledWith({ transactionId: 'txn-xyz' });
    });
  });

  // ─── Error event ─────────────────────────────────────────────────────────

  describe('"error" event', () => {
    it('calls onError with the Error instance when the SDK emits an Error', async () => {
      const sdk = makePayPalButtonSDK();
      (window as Window & typeof globalThis).PayPalButton =
        sdk as unknown as typeof window.PayPalButton;
      const onError = jest.fn();

      renderComponent({ onError });

      await waitFor(() => expect(sdk.on).toHaveBeenCalledWith('error', expect.any(Function)));

      const sdkError = new Error('PayPal SDK error');
      act(() => {
        sdk._emit('error', sdkError);
      });

      expect(onError).toHaveBeenCalledWith(sdkError);
    });

    it('wraps a non-Error SDK error value in a generic Error', async () => {
      const sdk = makePayPalButtonSDK();
      (window as Window & typeof globalThis).PayPalButton =
        sdk as unknown as typeof window.PayPalButton;
      const onError = jest.fn();

      renderComponent({ onError });

      await waitFor(() => expect(sdk.on).toHaveBeenCalledWith('error', expect.any(Function)));

      act(() => {
        sdk._emit('error', { code: 'FUNDING_CANCELLED' });
      });

      expect(onError).toHaveBeenCalledWith(new Error('PayPal payment failed'));
    });
  });

  // ─── Script load failure ─────────────────────────────────────────────────

  describe('Script load failure', () => {
    it('calls onError via onErrorRef when the script fails to load', async () => {
      (global as Record<string, unknown>).__simulateScriptError = true;
      const onError = jest.fn();

      renderComponent({ onError });

      await waitFor(() => {
        expect(onError).toHaveBeenCalledWith(
          expect.objectContaining({ message: 'Failed to load Datatrans PayPal Button script' })
        );
      });
    });
  });

  // ─── Accessibility ────────────────────────────────────────────────────────

  describe('Accessibility', () => {
    it('has no accessibility violations', async () => {
      const { container } = renderComponent();
      expect(await axe(container)).toHaveNoViolations();
    });
  });
});
