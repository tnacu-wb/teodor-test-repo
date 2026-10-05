import { ChakraProvider } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import { axe, toHaveNoViolations } from 'jest-axe';
import React from 'react';

import { act, render, screen, waitFor } from '~utils/test-utils';

import { DatatransPaymentButton } from './DatatransPaymentButton';

expect.extend(toHaveNoViolations);

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

type EventMap = Record<string, (...args: unknown[]) => void>;

/** Creates a fresh PaymentButton SDK mock instance and returns it together
 *  with a helper that fires a registered event by name. */
function makePaymentButtonMock() {
  const eventHandlers: EventMap = {};

  const instance = {
    init: jest.fn(),
    on: jest.fn((event: string, cb: (...args: unknown[]) => void) => {
      eventHandlers[event] = cb;
    }),
    create: jest.fn(),
  };

  const Constructor = jest.fn(() => instance);

  const fire = (event: string, ...args: unknown[]) => {
    eventHandlers[event]?.(...args);
  };

  return { Constructor, instance, fire };
}

const defaultProps = {
  basketId: 'basket-001',
  amount: '120.00',
  currencyCode: 'GBP',
  walletType: 'APPLE_PAY' as const,
  onAuthorization: jest.fn(),
  onError: jest.fn(),
};

const renderComponent = (props = {}) =>
  render(
    <ChakraProvider>
      <DatatransPaymentButton {...defaultProps} {...props} />
    </ChakraProvider>
  );

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('DatatransPaymentButton', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    // Start each test without the SDK on window
    delete (window as Window & { PaymentButton?: unknown }).PaymentButton;
    // Silence process.env between tests
    process.env.NEXT_PUBLIC_DATATRANS_MERCHANT_ID = 'test-merchant-id';
    process.env.NEXT_PUBLIC_GOOGLE_PAY_MERCHANT_ID = 'test-google-merchant-id';
  });

  afterEach(() => {
    delete (window as Window & { PaymentButton?: unknown }).PaymentButton;
  });

  // -------------------------------------------------------------------------
  describe('Container rendering', () => {
    it('renders the container with the correct data-testid for APPLE_PAY', () => {
      renderComponent({ walletType: 'APPLE_PAY' });
      expect(screen.getByTestId('DatatransPaymentButton-APPLE_PAY-Container')).toBeInTheDocument();
    });

    it('renders the container with the correct data-testid for GOOGLE_PAY', () => {
      renderComponent({ walletType: 'GOOGLE_PAY' });
      expect(screen.getByTestId('DatatransPaymentButton-GOOGLE_PAY-Container')).toBeInTheDocument();
    });
  });

  // -------------------------------------------------------------------------
  describe('SDK polling', () => {
    it('sets isScriptReady immediately when window.PaymentButton is already present on mount', async () => {
      const { Constructor, instance, fire } = makePaymentButtonMock();
      (window as Window & { PaymentButton?: unknown }).PaymentButton = Constructor;

      renderComponent();

      await waitFor(() => expect(Constructor).toHaveBeenCalledTimes(1));
      fire('init');
      expect(instance.create).toHaveBeenCalledTimes(1);
    });

    it('sets isScriptReady after polling detects window.PaymentButton', async () => {
      jest.useFakeTimers();
      const { Constructor, instance, fire } = makePaymentButtonMock();

      renderComponent();

      // SDK not yet available — constructor should not have been called
      expect(Constructor).not.toHaveBeenCalled();

      // Simulate SDK arriving on window after 200 ms
      act(() => {
        (window as Window & { PaymentButton?: unknown }).PaymentButton = Constructor;
        jest.advanceTimersByTime(200);
      });

      await waitFor(() => expect(Constructor).toHaveBeenCalledTimes(1));
      fire('init');
      expect(instance.create).toHaveBeenCalledTimes(1);

      jest.useRealTimers();
    });
  });

  // -------------------------------------------------------------------------
  describe('Initialization', () => {
    it('calls paymentButton.init() with merchantId and both wallet configs', async () => {
      const { Constructor, instance } = makePaymentButtonMock();
      (window as Window & { PaymentButton?: unknown }).PaymentButton = Constructor;

      renderComponent();

      await waitFor(() => expect(instance.init).toHaveBeenCalledTimes(1));

      const initArg = instance.init.mock.calls[0][0] as Record<string, unknown>;
      expect(initArg.merchantId).toBe('test-merchant-id');
      expect(initArg.useApplePay).toBe(true);
      expect(initArg.useGooglePay).toBe(true);
      expect(initArg).toHaveProperty('applePayConfiguration');
      expect(initArg).toHaveProperty('googlePayConfiguration');
    });

    it('includes googlePayMerchantId inside googlePayConfiguration when env var is set', async () => {
      const { Constructor, instance } = makePaymentButtonMock();
      (window as Window & { PaymentButton?: unknown }).PaymentButton = Constructor;

      renderComponent();

      await waitFor(() => expect(instance.init).toHaveBeenCalledTimes(1));

      const initArg = instance.init.mock.calls[0][0] as Record<string, unknown>;
      const gpConfig = initArg.googlePayConfiguration as Record<string, unknown>;
      expect(gpConfig.merchantId).toBe('test-google-merchant-id');
    });

    it('calls paymentButton.init() without googlePayMerchantId when env var is absent', async () => {
      delete process.env.NEXT_PUBLIC_GOOGLE_PAY_MERCHANT_ID;
      const { Constructor, instance } = makePaymentButtonMock();
      (window as Window & { PaymentButton?: unknown }).PaymentButton = Constructor;

      renderComponent();

      await waitFor(() => expect(instance.init).toHaveBeenCalledTimes(1));

      const initArg = instance.init.mock.calls[0][0] as Record<string, unknown>;
      const gpConfig = initArg.googlePayConfiguration as Record<string, unknown>;
      expect(gpConfig.merchantId).toBeUndefined();
    });

    it('calls onError and skips init when merchantId is not configured', async () => {
      delete process.env.NEXT_PUBLIC_DATATRANS_MERCHANT_ID;
      const onError = jest.fn();
      const { Constructor, instance } = makePaymentButtonMock();
      (window as Window & { PaymentButton?: unknown }).PaymentButton = Constructor;

      renderComponent({ onError });

      await waitFor(() =>
        expect(onError).toHaveBeenCalledWith(new Error('Datatrans merchant ID is not configured'))
      );
      expect(instance.init).not.toHaveBeenCalled();
    });

    it('does not reinitialise when script is already ready (isInitializedRef guard)', async () => {
      const { Constructor, instance } = makePaymentButtonMock();
      (window as Window & { PaymentButton?: unknown }).PaymentButton = Constructor;

      const { rerender } = render(
        <ChakraProvider>
          <DatatransPaymentButton {...defaultProps} amount="100.00" />
        </ChakraProvider>
      );

      await waitFor(() => expect(Constructor).toHaveBeenCalledTimes(1));

      // Re-render with a changed prop — guard must prevent a second init
      rerender(
        <ChakraProvider>
          <DatatransPaymentButton {...defaultProps} amount="200.00" />
        </ChakraProvider>
      );

      // Still only one instance and one init call
      expect(Constructor).toHaveBeenCalledTimes(1);
      expect(instance.init).toHaveBeenCalledTimes(1);
    });
  });

  // -------------------------------------------------------------------------
  describe('"init" event — button creation', () => {
    it('calls paymentButton.create() with the container element and correct payment object', async () => {
      const { Constructor, instance, fire } = makePaymentButtonMock();
      (window as Window & { PaymentButton?: unknown }).PaymentButton = Constructor;

      renderComponent();

      await waitFor(() => expect(instance.init).toHaveBeenCalledTimes(1));
      fire('init');

      expect(instance.create).toHaveBeenCalledTimes(1);
      const [containerArg, paymentArg] = instance.create.mock.calls[0] as [
        HTMLElement,
        Record<string, unknown>,
      ];
      expect(containerArg).toBeInstanceOf(HTMLElement);

      const details = (
        paymentArg as { details: { total: { amount: { value: string; currency: string } } } }
      ).details;
      expect(details.total.amount.value).toBe('120.00');
      expect(details.total.amount.currency).toBe('GBP');

      const transaction = (paymentArg as { transaction: { refno: string } }).transaction;
      expect(transaction.refno).toBe('basket-001');
    });
  });

  // -------------------------------------------------------------------------
  describe('"authorization" event', () => {
    it('calls onAuthorization with the authorization data', async () => {
      const onAuthorization = jest.fn();
      const { Constructor, instance, fire } = makePaymentButtonMock();
      (window as Window & { PaymentButton?: unknown }).PaymentButton = Constructor;

      renderComponent({ onAuthorization });

      await waitFor(() => expect(instance.init).toHaveBeenCalledTimes(1));
      fire('authorization', { transactionId: 'txn-abc123' });

      expect(onAuthorization).toHaveBeenCalledWith({ transactionId: 'txn-abc123' });
    });

    it('uses the latest onAuthorization reference after a prop update', async () => {
      const firstCallback = jest.fn();
      const secondCallback = jest.fn();
      const { Constructor, instance, fire } = makePaymentButtonMock();
      (window as Window & { PaymentButton?: unknown }).PaymentButton = Constructor;

      const { rerender } = render(
        <ChakraProvider>
          <DatatransPaymentButton {...defaultProps} onAuthorization={firstCallback} />
        </ChakraProvider>
      );

      await waitFor(() => expect(instance.init).toHaveBeenCalledTimes(1));

      rerender(
        <ChakraProvider>
          <DatatransPaymentButton {...defaultProps} onAuthorization={secondCallback} />
        </ChakraProvider>
      );

      fire('authorization', { transactionId: 'txn-xyz' });

      expect(firstCallback).not.toHaveBeenCalled();
      expect(secondCallback).toHaveBeenCalledWith({ transactionId: 'txn-xyz' });
    });
  });

  // -------------------------------------------------------------------------
  describe('"error" event', () => {
    it('calls onError with the original Error instance when the SDK fires one', async () => {
      const onError = jest.fn();
      const { Constructor, instance, fire } = makePaymentButtonMock();
      (window as Window & { PaymentButton?: unknown }).PaymentButton = Constructor;

      renderComponent({ onError });

      await waitFor(() => expect(instance.init).toHaveBeenCalledTimes(1));

      const sdkError = new Error('SDK internal error');
      fire('error', sdkError);

      expect(onError).toHaveBeenCalledWith(sdkError);
    });

    it('wraps a non-Error SDK payload in a new Error containing the walletType', async () => {
      const onError = jest.fn();
      const { Constructor, instance, fire } = makePaymentButtonMock();
      (window as Window & { PaymentButton?: unknown }).PaymentButton = Constructor;

      renderComponent({ onError, walletType: 'GOOGLE_PAY' });

      await waitFor(() => expect(instance.init).toHaveBeenCalledTimes(1));
      fire('error', { code: 'CANCELLED' });

      expect(onError).toHaveBeenCalledWith(new Error('GOOGLE_PAY payment failed'));
    });

    it('uses the latest onError reference after a prop update', async () => {
      const firstError = jest.fn();
      const secondError = jest.fn();
      const { Constructor, instance, fire } = makePaymentButtonMock();
      (window as Window & { PaymentButton?: unknown }).PaymentButton = Constructor;

      const { rerender } = render(
        <ChakraProvider>
          <DatatransPaymentButton {...defaultProps} onError={firstError} />
        </ChakraProvider>
      );

      await waitFor(() => expect(instance.init).toHaveBeenCalledTimes(1));

      rerender(
        <ChakraProvider>
          <DatatransPaymentButton {...defaultProps} onError={secondError} />
        </ChakraProvider>
      );

      fire('error', new Error('late error'));

      expect(firstError).not.toHaveBeenCalled();
      expect(secondError).toHaveBeenCalledTimes(1);
    });
  });

  // -------------------------------------------------------------------------
  describe('Accessibility', () => {
    it('has no accessibility violations', async () => {
      const { container } = renderComponent();
      expect(await axe(container)).toHaveNoViolations();
    });
  });
});
