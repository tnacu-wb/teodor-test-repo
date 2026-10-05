import { ChakraProvider } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import { axe, toHaveNoViolations } from 'jest-axe';
import React from 'react';

import { fireEvent, render, screen, waitFor } from '~utils/test-utils';

import { PaymentConfirmSection } from './PaymentConfirmSection';

expect.extend(toHaveNoViolations);

// ─── Mocks ───────────────────────────────────────────────────────────────────

jest.mock('next-i18next', () => ({
  useTranslation: () => ({ t: (key: string) => key }),
}));

jest.mock('@whitbread-eos/utils', () => ({
  useCustomLocale: () => ({ country: 'gb', language: 'en' }),
  useQueryRequest: jest.fn(),
  formatUrlTermsConditions: jest.fn((text: string | undefined) => text ?? ''),
  renderSanitizedHtml: jest.fn((html: string) => html),
}));

jest.mock('@whitbread-eos/api', () => ({
  BOOKING_CHANNEL: { PI: 'PI' },
  GET_TERMS_AND_CONDITIONS_QUERY: 'GET_TERMS_AND_CONDITIONS_QUERY',
  PAYPAL_PAYMENT: 'PAYPAL',
}));

// @whitbread-eos/atoms has a Haste-map collision with scripts/config-templates/package.json
// (both declare the same package name). Mocking by the resolved filesystem path bypasses
// the Haste name lookup entirely and avoids the "_assertNoDuplicates" error.
jest.mock('@whitbread-eos/atoms', () => ({
  // PencePrice: renders a minimal testable stub so assertions can target price output
  PencePrice: ({
    price,
    currency,
    'data-testid': testId,
  }: {
    price: string;
    currency: string;
    'data-testid'?: string;
  }) => <span data-testid={testId ?? 'pence-price'}>{`${currency} ${price}`}</span>,
  PaypalWBButton: () => <button data-testid="PaypalWBButton">PayPal</button>,
}));

jest.mock('../DatatransPaymentButton/DatatransPaymentButton', () => ({
  DatatransPaymentButton: ({ walletType }: { walletType: string }) => (
    <button data-testid={`DatatransPaymentButton-${walletType}`}>
      {walletType === 'APPLE_PAY' ? 'Apple Pay' : 'Google Pay'}
    </button>
  ),
}));

// ─── Helpers ─────────────────────────────────────────────────────────────────

const { useQueryRequest } = jest.requireMock('@whitbread-eos/utils') as {
  useQueryRequest: jest.Mock;
};

const setTermsLoaded = (text?: string) =>
  useQueryRequest.mockReturnValue({
    isLoading: false,
    data: text ? { termsAndConditions: { text } } : undefined,
  });

const setTermsLoading = () => useQueryRequest.mockReturnValue({ isLoading: true, data: undefined });

const baseProps = {
  hotelId: 'hotel-123',
  ratePlanCode: 'FLEX',
  totalAmount: 120.5,
  currency: 'GBP',
  language: 'en',
  onConfirm: jest.fn(),
  onBack: jest.fn(),
};

const cardPaymentType = {
  name: 'CARD',
  type: 'NEW_CARD',
  enabled: true,
  order: 1,
  cnpOptionAvailable: false,
  cnpPreSelected: false,
};

const paypalPaymentType = {
  name: 'PAYPAL',
  type: 'PAYPAL',
  enabled: true,
  order: 2,
  cnpOptionAvailable: false,
  cnpPreSelected: false,
};

const makeWalletPaymentType = (type: 'AP' | 'GP') => ({
  name: type === 'AP' ? 'APPLE' : 'GOOGLE',
  type,
  enabled: true,
  order: 3,
  cnpOptionAvailable: false,
  cnpPreSelected: false,
});

const applePayButtonProps = {
  basketId: 'basket-123',
  amount: '120.50',
  currencyCode: 'GBP',
  walletType: 'APPLE_PAY' as const,
  onAuthorization: jest.fn(),
  onError: jest.fn(),
};

const googlePayButtonProps = { ...applePayButtonProps, walletType: 'GOOGLE_PAY' as const };

const wrap = (ui: React.ReactElement) => render(<ChakraProvider>{ui}</ChakraProvider>);

// ─── Tests ───────────────────────────────────────────────────────────────────

describe('PaymentConfirmSection', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    setTermsLoaded('By booking you agree to our <a href="/terms">T&amp;Cs</a>');
  });

  // ── Terms & conditions ─────────────────────────────────────────────────────

  describe('Terms and conditions', () => {
    it('renders T&C text once the query resolves', async () => {
      wrap(<PaymentConfirmSection {...baseProps} />);
      await waitFor(() => {
        expect(screen.getByTestId('PaymentConfirmSection-Terms')).toHaveTextContent(
          'By booking you agree to our'
        );
      });
    });

    it('renders an empty T&C area while loading', () => {
      setTermsLoading();
      wrap(<PaymentConfirmSection {...baseProps} />);
      expect(screen.getByTestId('PaymentConfirmSection-Terms')).toHaveTextContent('');
    });

    it('calls useQueryRequest with the correct variables', () => {
      wrap(<PaymentConfirmSection {...baseProps} />);
      expect(useQueryRequest).toHaveBeenCalledWith(
        ['GetTermsAndConditions', 'hotel-123', 'gb', 'en', 'FLEX', 'PI'],
        'GET_TERMS_AND_CONDITIONS_QUERY',
        expect.objectContaining({
          hotelId: 'hotel-123',
          country: 'gb',
          language: 'en',
          rateCode: 'FLEX',
          bookingChannel: 'PI',
        })
      );
    });
  });

  // ── Amount display ─────────────────────────────────────────────────────────

  describe('Amount display', () => {
    it('renders the total amount via PencePrice', () => {
      wrap(<PaymentConfirmSection {...baseProps} />);
      expect(screen.getByTestId('PaymentConfirmSection-Amount')).toHaveTextContent('GBP 120.50');
    });

    it('does not display decimals when totalAmount is a whole number', () => {
      wrap(<PaymentConfirmSection {...baseProps} totalAmount={50} />);
      expect(screen.getByTestId('PaymentConfirmSection-Amount')).toHaveTextContent('GBP 50');
      expect(screen.getByTestId('PaymentConfirmSection-Amount')).not.toHaveTextContent('50.00');
    });

    it('displays decimals when totalAmount has a non-zero fractional part', () => {
      wrap(<PaymentConfirmSection {...baseProps} totalAmount={99.99} />);
      expect(screen.getByTestId('PaymentConfirmSection-Amount')).toHaveTextContent('GBP 99.99');
    });

    it('does not display decimals for a round number like 200', () => {
      wrap(<PaymentConfirmSection {...baseProps} totalAmount={200} />);
      expect(screen.getByTestId('PaymentConfirmSection-Amount')).not.toHaveTextContent('200.00');
      expect(screen.getByTestId('PaymentConfirmSection-Amount')).toHaveTextContent('GBP 200');
    });

    it('renders the amount subtext element', () => {
      wrap(<PaymentConfirmSection {...baseProps} />);
      expect(screen.getByTestId('PaymentConfirmSection-AmountSubtext')).toBeInTheDocument();
    });
  });

  // ── Confirm button (standard / card) ──────────────────────────────────────

  describe('Confirm button — standard payment method', () => {
    it('renders the confirm button when no selectedPaymentType is provided', () => {
      wrap(<PaymentConfirmSection {...baseProps} />);
      expect(screen.getByTestId('PaymentConfirmSection-ConfirmButton')).toBeInTheDocument();
    });

    it('renders the confirm button for a card-type payment method', () => {
      wrap(<PaymentConfirmSection {...baseProps} selectedPaymentType={cardPaymentType} />);
      expect(screen.getByTestId('PaymentConfirmSection-ConfirmButton')).toBeInTheDocument();
    });

    it('calls onConfirm when the confirm button is clicked', () => {
      const onConfirm = jest.fn();
      wrap(<PaymentConfirmSection {...baseProps} onConfirm={onConfirm} />);
      fireEvent.click(screen.getByTestId('PaymentConfirmSection-ConfirmButton'));
      expect(onConfirm).toHaveBeenCalledTimes(1);
    });

    it('confirm button is disabled when isLoading is true', () => {
      wrap(<PaymentConfirmSection {...baseProps} isLoading />);
      expect(screen.getByTestId('PaymentConfirmSection-ConfirmButton')).toBeDisabled();
    });
  });

  // ── PayPal button ─────────────────────────────────────────────────────────

  describe('PayPal button', () => {
    it('renders the PayPal button when selectedPaymentType is PAYPAL and paypalOptions are provided', () => {
      wrap(
        <PaymentConfirmSection
          {...baseProps}
          selectedPaymentType={paypalPaymentType}
          paypalOptions={{ onApprove: jest.fn(), createOrder: jest.fn() } as never}
        />
      );
      expect(screen.getByTestId('PaypalWBButton')).toBeInTheDocument();
    });

    it('renders nothing in the CTA slot when PayPal is selected but paypalOptions are absent', () => {
      wrap(<PaymentConfirmSection {...baseProps} selectedPaymentType={paypalPaymentType} />);
      expect(screen.queryByTestId('PaypalWBButton')).not.toBeInTheDocument();
      expect(screen.queryByTestId('PaymentConfirmSection-ConfirmButton')).not.toBeInTheDocument();
    });

    it('does not render the standard confirm button when PayPal is selected', () => {
      wrap(
        <PaymentConfirmSection
          {...baseProps}
          selectedPaymentType={paypalPaymentType}
          paypalOptions={{ onApprove: jest.fn(), createOrder: jest.fn() } as never}
        />
      );
      expect(screen.queryByTestId('PaymentConfirmSection-ConfirmButton')).not.toBeInTheDocument();
    });
  });

  // ── Apple Pay / Google Pay ─────────────────────────────────────────────────

  describe('Wallet buttons (Apple Pay / Google Pay)', () => {
    it('renders the Apple Pay button when type is AP and datatransPaymentButtonProps are provided', () => {
      wrap(
        <PaymentConfirmSection
          {...baseProps}
          selectedPaymentType={makeWalletPaymentType('AP')}
          datatransPaymentButtonProps={applePayButtonProps}
        />
      );
      expect(screen.getByTestId('DatatransPaymentButton-APPLE_PAY')).toBeInTheDocument();
    });

    it('renders the Google Pay button when type is GP and datatransPaymentButtonProps are provided', () => {
      wrap(
        <PaymentConfirmSection
          {...baseProps}
          selectedPaymentType={makeWalletPaymentType('GP')}
          datatransPaymentButtonProps={googlePayButtonProps}
        />
      );
      expect(screen.getByTestId('DatatransPaymentButton-GOOGLE_PAY')).toBeInTheDocument();
    });

    it('falls back to the standard confirm button when AP is selected but datatransPaymentButtonProps are absent', () => {
      wrap(
        <PaymentConfirmSection {...baseProps} selectedPaymentType={makeWalletPaymentType('AP')} />
      );
      expect(screen.queryByTestId('DatatransPaymentButton-APPLE_PAY')).not.toBeInTheDocument();
      expect(screen.getByTestId('PaymentConfirmSection-ConfirmButton')).toBeInTheDocument();
    });

    it('does not render the standard confirm button when Apple Pay is active', () => {
      wrap(
        <PaymentConfirmSection
          {...baseProps}
          selectedPaymentType={makeWalletPaymentType('AP')}
          datatransPaymentButtonProps={applePayButtonProps}
        />
      );
      expect(screen.queryByTestId('PaymentConfirmSection-ConfirmButton')).not.toBeInTheDocument();
    });
  });

  // ── Back button ────────────────────────────────────────────────────────────

  describe('Back button', () => {
    it('renders the back button', () => {
      wrap(<PaymentConfirmSection {...baseProps} />);
      expect(screen.getByTestId('PaymentConfirmSection-BackButton')).toBeInTheDocument();
    });

    it('calls onBack when clicked', () => {
      const onBack = jest.fn();
      wrap(<PaymentConfirmSection {...baseProps} onBack={onBack} />);
      fireEvent.click(screen.getByTestId('PaymentConfirmSection-BackButton'));
      expect(onBack).toHaveBeenCalledTimes(1);
    });

    it('back button is disabled when isLoading is true', () => {
      wrap(<PaymentConfirmSection {...baseProps} isLoading />);
      expect(screen.getByTestId('PaymentConfirmSection-BackButton')).toBeDisabled();
    });

    it('back button is enabled when isLoading is false', () => {
      wrap(<PaymentConfirmSection {...baseProps} isLoading={false} />);
      expect(screen.getByTestId('PaymentConfirmSection-BackButton')).not.toBeDisabled();
    });
  });

  // ── data-testid prefix ─────────────────────────────────────────────────────

  describe('data-testid prefix', () => {
    it('uses the default prefix on root and all named child elements', () => {
      wrap(<PaymentConfirmSection {...baseProps} />);
      expect(screen.getByTestId('PaymentConfirmSection')).toBeInTheDocument();
      expect(screen.getByTestId('PaymentConfirmSection-Terms')).toBeInTheDocument();
      expect(screen.getByTestId('PaymentConfirmSection-AmountRow')).toBeInTheDocument();
      expect(screen.getByTestId('PaymentConfirmSection-AmountGroup')).toBeInTheDocument();
      expect(screen.getByTestId('PaymentConfirmSection-Amount')).toBeInTheDocument();
      expect(screen.getByTestId('PaymentConfirmSection-AmountSubtext')).toBeInTheDocument();
      expect(screen.getByTestId('PaymentConfirmSection-ConfirmButton')).toBeInTheDocument();
      expect(screen.getByTestId('PaymentConfirmSection-BackButton')).toBeInTheDocument();
    });

    it('uses a custom prefix when data-testid prop is provided', () => {
      wrap(<PaymentConfirmSection {...baseProps} data-testid="CustomSection" />);
      expect(screen.getByTestId('CustomSection')).toBeInTheDocument();
      expect(screen.getByTestId('CustomSection-Terms')).toBeInTheDocument();
      expect(screen.getByTestId('CustomSection-ConfirmButton')).toBeInTheDocument();
      expect(screen.getByTestId('CustomSection-BackButton')).toBeInTheDocument();
    });
  });

  // ── Accessibility ──────────────────────────────────────────────────────────

  describe('Accessibility', () => {
    it('has no accessibility violations in the default state', async () => {
      const { container } = wrap(<PaymentConfirmSection {...baseProps} />);
      expect(await axe(container)).toHaveNoViolations();
    });

    it('has no accessibility violations in the loading state', async () => {
      const { container } = wrap(<PaymentConfirmSection {...baseProps} isLoading />);
      expect(await axe(container)).toHaveNoViolations();
    });
  });
});
