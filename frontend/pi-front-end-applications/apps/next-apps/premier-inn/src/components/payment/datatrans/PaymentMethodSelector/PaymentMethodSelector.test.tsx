import { ChakraProvider } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import { axe, toHaveNoViolations } from 'jest-axe';
import React from 'react';

import { fireEvent, render, screen, waitFor } from '~utils/test-utils';

import { PaymentMethodSelector } from './PaymentMethodSelector';

expect.extend(toHaveNoViolations);

// ─── Mocks ───────────────────────────────────────────────────────────────────

jest.mock('@whitbread-eos/utils', () => ({
  useCustomLocale: () => ({ language: 'en', country: 'gb' }),
  useQueryRequest: jest.fn(),
  formatAssetsUrl: (src: string) => src,
  isApplePayConfigured: jest.fn(() => true),
  isGooglePayConfigured: jest.fn(() => Promise.resolve(true)),
}));

jest.mock('@whitbread-eos/api', () => ({
  GET_PAYMENT_METHODS_QUERY: 'GET_PAYMENT_METHODS_QUERY',
  Area: { PI: 'pi' },
}));

const { useQueryRequest } = jest.requireMock('@whitbread-eos/utils') as {
  useQueryRequest: jest.Mock;
};

const { isApplePayConfigured, isGooglePayConfigured } = jest.requireMock(
  '@whitbread-eos/utils'
) as {
  isApplePayConfigured: jest.Mock;
  isGooglePayConfigured: jest.Mock;
};

const makeMethod = (
  overrides: Partial<{
    name: string;
    type: string;
    order: number;
    enabled: boolean;
    logoSrc: string | null;
    acceptedCardTypes: { type: string; name: string; logoSrc: string }[];
  }>
) => ({
  name: 'CARD',
  type: 'NEW_CARD',
  order: 1,
  enabled: true,
  logoSrc: null,
  subType: null,
  cnpPreSelected: false,
  cnpOptionAvailable: false,
  acceptedCardTypes: [{ type: 'VS', name: 'Visa', logoSrc: 'https://example.com/visa.png' }],
  paymentOptions: [{ type: 'PAY_NOW', order: 1, enabled: true }],
  reasons: [],
  ...overrides,
});

const mockPaymentMethods = [
  makeMethod({ name: 'CARD', type: 'NEW_CARD', order: 1, enabled: true, logoSrc: null }),
  makeMethod({ name: 'APPLE', type: 'AP', order: 2, enabled: true, logoSrc: '/apple.jpg' }),
  makeMethod({ name: 'GOOGLE', type: 'GP', order: 3, enabled: true, logoSrc: '/google.png' }),
  makeMethod({ name: 'PIBA', type: 'NEW_PIBA', order: 4, enabled: false, logoSrc: null }),
];

const renderComponent = (selectedId: string | null, onChange = jest.fn()) =>
  render(
    <ChakraProvider>
      <PaymentMethodSelector
        basketReference="basket-123"
        selectedId={selectedId}
        onChange={onChange}
      />
    </ChakraProvider>
  );

describe('PaymentMethodSelector', () => {
  beforeEach(() => {
    useQueryRequest.mockReturnValue({
      data: { paymentMethods: mockPaymentMethods },
      isLoading: false,
      isError: false,
    });
    (isApplePayConfigured as jest.Mock).mockReturnValue(true);
    (isGooglePayConfigured as jest.Mock).mockResolvedValue(true);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('Loading state', () => {
    it('renders a spinner while loading', () => {
      useQueryRequest.mockReturnValue({ data: undefined, isLoading: true, isError: false });
      renderComponent(null);
      expect(screen.getByTestId('PaymentMethodSelector-Loading')).toBeInTheDocument();
      expect(screen.queryByRole('radiogroup')).not.toBeInTheDocument();
    });
  });

  describe('ARIA roles and structure', () => {
    it('renders a radiogroup container', () => {
      renderComponent('NEW_CARD');
      expect(screen.getByRole('radiogroup')).toBeInTheDocument();
    });

    // Wallet tiles (Apple Pay, Google Pay) appear after an async availability check
    it('renders a radio for each payment method', async () => {
      renderComponent('NEW_CARD');
      await waitFor(() => expect(screen.getAllByRole('radio')).toHaveLength(4));
    });

    it('applies aria-checked="true" only to the selected tile', async () => {
      renderComponent('AP');
      await waitFor(() => expect(screen.getByLabelText('Apple Pay')).toBeInTheDocument());
      expect(screen.getByLabelText('Apple Pay')).toHaveAttribute('aria-checked', 'true');
      expect(screen.getByLabelText('Credit / Debit')).toHaveAttribute('aria-checked', 'false');
      expect(screen.getByLabelText('Google Pay')).toHaveAttribute('aria-checked', 'false');
      expect(screen.getByLabelText('Business Pay')).toHaveAttribute('aria-checked', 'false');
    });

    it('applies aria-disabled="true" to the disabled method', () => {
      renderComponent('NEW_CARD');
      expect(screen.getByLabelText('Business Pay')).toHaveAttribute('aria-disabled', 'true');
    });

    it('applies aria-disabled="false" to enabled methods', () => {
      renderComponent('NEW_CARD');
      expect(screen.getByLabelText('Credit / Debit')).toHaveAttribute('aria-disabled', 'false');
    });
  });

  describe('Query integration', () => {
    it('passes basketReference, language, country and clientChannel to useQueryRequest', () => {
      renderComponent(null);
      expect(useQueryRequest).toHaveBeenCalledWith(
        ['getPaymentMethods', 'en', 'gb', 'basket-123'],
        'GET_PAYMENT_METHODS_QUERY',
        expect.objectContaining({
          basketReference: 'basket-123',
          language: 'en',
          country: 'gb',
          clientChannel: 'PI',
        }),
        expect.any(Object)
      );
    });

    it('renders tiles sorted by order from the API response', async () => {
      renderComponent(null);
      await waitFor(() => expect(screen.getAllByRole('radio')).toHaveLength(4));
      const tiles = screen.getAllByRole('radio');
      expect(tiles[0]).toHaveAttribute('aria-label', 'Credit / Debit');
      expect(tiles[1]).toHaveAttribute('aria-label', 'Apple Pay');
      expect(tiles[2]).toHaveAttribute('aria-label', 'Google Pay');
      expect(tiles[3]).toHaveAttribute('aria-label', 'Business Pay');
    });
  });

  describe('Selected state', () => {
    it('renders a checkmark on the selected tile', () => {
      renderComponent('NEW_CARD');
      expect(screen.getByTestId('PaymentMethodSelector-Checkmark-NEW_CARD')).toBeInTheDocument();
    });

    it('does not render a checkmark on unselected tiles', async () => {
      renderComponent('NEW_CARD');
      await waitFor(() => expect(screen.getAllByRole('radio')).toHaveLength(4));
      expect(screen.queryByTestId('PaymentMethodSelector-Checkmark-AP')).not.toBeInTheDocument();
      expect(screen.queryByTestId('PaymentMethodSelector-Checkmark-GP')).not.toBeInTheDocument();
    });

    it('renders no checkmark when selectedId is null', () => {
      renderComponent(null);
      expect(screen.queryByTestId(/PaymentMethodSelector-Checkmark/)).not.toBeInTheDocument();
    });
  });

  describe('Interaction', () => {
    it('calls onChange with the method type when an enabled tile is clicked', async () => {
      const onChange = jest.fn();
      renderComponent('NEW_CARD', onChange);
      await waitFor(() =>
        expect(screen.getByTestId('PaymentMethodSelector-Tile-AP')).toBeInTheDocument()
      );
      fireEvent.click(screen.getByTestId('PaymentMethodSelector-Tile-AP'));
      expect(onChange).toHaveBeenCalledTimes(1);
      expect(onChange).toHaveBeenCalledWith('AP');
    });

    it('does not call onChange when a disabled tile is clicked', () => {
      const onChange = jest.fn();
      renderComponent('NEW_CARD', onChange);
      fireEvent.click(screen.getByTestId('PaymentMethodSelector-Tile-NEW_PIBA'));
      expect(onChange).not.toHaveBeenCalled();
    });

    it('calls onChange when Enter is pressed on an enabled tile', async () => {
      const onChange = jest.fn();
      renderComponent('NEW_CARD', onChange);
      await waitFor(() =>
        expect(screen.getByTestId('PaymentMethodSelector-Tile-GP')).toBeInTheDocument()
      );
      fireEvent.keyDown(screen.getByTestId('PaymentMethodSelector-Tile-GP'), {
        key: 'Enter',
        code: 'Enter',
      });
      expect(onChange).toHaveBeenCalledWith('GP');
    });

    it('calls onChange when Space is pressed on an enabled tile', async () => {
      const onChange = jest.fn();
      renderComponent('NEW_CARD', onChange);
      await waitFor(() =>
        expect(screen.getByTestId('PaymentMethodSelector-Tile-AP')).toBeInTheDocument()
      );
      fireEvent.keyDown(screen.getByTestId('PaymentMethodSelector-Tile-AP'), {
        key: ' ',
        code: 'Space',
      });
      expect(onChange).toHaveBeenCalledWith('AP');
    });

    it('calls onMethodSelect with the full PaymentMethod object when an enabled tile is clicked', async () => {
      const onChange = jest.fn();
      const onMethodSelect = jest.fn();
      render(
        <ChakraProvider>
          <PaymentMethodSelector
            basketReference="basket-123"
            selectedId="NEW_CARD"
            onChange={onChange}
            onMethodSelect={onMethodSelect}
          />
        </ChakraProvider>
      );
      await waitFor(() =>
        expect(screen.getByTestId('PaymentMethodSelector-Tile-AP')).toBeInTheDocument()
      );
      fireEvent.click(screen.getByTestId('PaymentMethodSelector-Tile-AP'));
      expect(onMethodSelect).toHaveBeenCalledTimes(1);
      expect(onMethodSelect).toHaveBeenCalledWith(
        expect.objectContaining({ name: 'APPLE', type: 'AP' })
      );
    });
  });

  describe('Labels', () => {
    it('renders the correct human-readable label for each method name', async () => {
      renderComponent(null);
      await waitFor(() =>
        expect(screen.getByTestId('PaymentMethodSelector-Label-AP')).toBeInTheDocument()
      );
      expect(screen.getByTestId('PaymentMethodSelector-Label-NEW_CARD')).toHaveTextContent(
        'Credit / Debit'
      );
      expect(screen.getByTestId('PaymentMethodSelector-Label-AP')).toHaveTextContent('Apple Pay');
      expect(screen.getByTestId('PaymentMethodSelector-Label-GP')).toHaveTextContent('Google Pay');
      expect(screen.getByTestId('PaymentMethodSelector-Label-NEW_PIBA')).toHaveTextContent(
        'Business Pay'
      );
    });
  });

  describe('Icon rendering', () => {
    it('renders an img for each method tile', async () => {
      renderComponent(null);
      await waitFor(() =>
        expect(screen.getByTestId('PaymentMethodSelector-Tile-AP')).toBeInTheDocument()
      );
      const appleTile = screen.getByTestId('PaymentMethodSelector-Tile-AP');
      expect(appleTile.querySelector('img')).toBeTruthy();
    });

    it('derives the icon src from method.type via resolveImageSrc', () => {
      renderComponent(null);
      const cardTile = screen.getByTestId('PaymentMethodSelector-Tile-NEW_CARD');
      const img = cardTile.querySelector('img');
      expect(img).toBeTruthy();
      // resolveImageSrc always returns /images/datatrans/<type>.svg — never acceptedCardTypes logoSrc
      expect(img?.getAttribute('src')).toBe('/images/datatrans/NEW_CARD.svg');
    });

    it('derives the apple pay icon src from method.type AP', async () => {
      renderComponent(null);
      await waitFor(() =>
        expect(screen.getByTestId('PaymentMethodSelector-Tile-AP')).toBeInTheDocument()
      );
      const img = screen.getByTestId('PaymentMethodSelector-Tile-AP').querySelector('img');
      expect(img?.getAttribute('src')).toBe('/images/datatrans/AP.svg');
    });
  });

  describe('Wallet availability filtering', () => {
    it('hides Apple Pay tile when isApplePayConfigured returns false', async () => {
      (isApplePayConfigured as jest.Mock).mockReturnValue(false);
      renderComponent('NEW_CARD');
      // Only CARD, GOOGLE, PIBA should render (3 tiles)
      await waitFor(() => expect(screen.getAllByRole('radio')).toHaveLength(3));
      expect(screen.queryByTestId('PaymentMethodSelector-Tile-AP')).not.toBeInTheDocument();
    });

    it('hides Google Pay tile when isGooglePayConfigured resolves to false', async () => {
      (isGooglePayConfigured as jest.Mock).mockResolvedValue(false);
      renderComponent('NEW_CARD');
      // Only CARD, APPLE, PIBA should render (3 tiles)
      await waitFor(() => expect(screen.getAllByRole('radio')).toHaveLength(3));
      expect(screen.queryByTestId('PaymentMethodSelector-Tile-GP')).not.toBeInTheDocument();
    });

    it('hides both wallet tiles when neither Apple nor Google Pay is available', async () => {
      (isApplePayConfigured as jest.Mock).mockReturnValue(false);
      (isGooglePayConfigured as jest.Mock).mockResolvedValue(false);
      renderComponent('NEW_CARD');
      // Only CARD and PIBA remain
      await waitFor(() => expect(screen.getAllByRole('radio')).toHaveLength(2));
      expect(screen.queryByTestId('PaymentMethodSelector-Tile-AP')).not.toBeInTheDocument();
      expect(screen.queryByTestId('PaymentMethodSelector-Tile-GP')).not.toBeInTheDocument();
    });
  });

  describe('Auto-select behaviour', () => {
    it('calls onChange and onMethodSelect with the first enabled method when selectedId is stale', async () => {
      const onChange = jest.fn();
      const onMethodSelect = jest.fn();
      render(
        <ChakraProvider>
          <PaymentMethodSelector
            basketReference="basket-123"
            selectedId="STALE_ID"
            onChange={onChange}
            onMethodSelect={onMethodSelect}
          />
        </ChakraProvider>
      );
      await waitFor(() => {
        expect(onChange).toHaveBeenCalledWith('NEW_CARD');
        expect(onMethodSelect).toHaveBeenCalledWith(
          expect.objectContaining({ name: 'CARD', type: 'NEW_CARD' })
        );
      });
    });

    it('does not auto-select when the current selectedId is valid and enabled', () => {
      const onChange = jest.fn();
      const onMethodSelect = jest.fn();
      render(
        <ChakraProvider>
          <PaymentMethodSelector
            basketReference="basket-123"
            selectedId="NEW_CARD"
            onChange={onChange}
            onMethodSelect={onMethodSelect}
          />
        </ChakraProvider>
      );
      expect(onChange).not.toHaveBeenCalled();
      expect(onMethodSelect).not.toHaveBeenCalled();
    });

    it('auto-selects and updates setSelectedPaymentDetail when selectedId is null', async () => {
      const onChange = jest.fn();
      const onMethodSelect = jest.fn();
      const setSelectedPaymentDetail = jest.fn();
      render(
        <ChakraProvider>
          <PaymentMethodSelector
            basketReference="basket-123"
            selectedId={null}
            onChange={onChange}
            onMethodSelect={onMethodSelect}
            selectedPaymentDetail={{ type: 'default', order: 0, enabled: false }}
            setSelectedPaymentDetail={setSelectedPaymentDetail}
          />
        </ChakraProvider>
      );
      await waitFor(() => {
        expect(onChange).toHaveBeenCalledWith('NEW_CARD');
        expect(setSelectedPaymentDetail).toHaveBeenCalledWith(
          expect.objectContaining({ type: 'PAY_NOW', enabled: true })
        );
      });
    });
  });

  describe('Custom data-testid', () => {
    it('uses the provided data-testid as prefix', () => {
      render(
        <ChakraProvider>
          <PaymentMethodSelector
            basketReference="basket-123"
            selectedId={null}
            onChange={jest.fn()}
            data-testid="CustomSelector"
          />
        </ChakraProvider>
      );
      expect(screen.getByTestId('CustomSelector')).toBeInTheDocument();
      expect(screen.getByTestId('CustomSelector-Tile-NEW_CARD')).toBeInTheDocument();
    });
  });

  describe('Accessibility', () => {
    it('has no accessibility violations', async () => {
      const { container } = renderComponent('NEW_CARD');
      const results = await axe(container);
      expect(results).toHaveNoViolations();
    });
  });
});
