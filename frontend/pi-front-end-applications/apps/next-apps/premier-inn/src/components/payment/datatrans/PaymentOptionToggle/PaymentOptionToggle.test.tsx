import { ChakraProvider } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import { paymentOptions } from '@whitbread-eos/api';
import { axe, toHaveNoViolations } from 'jest-axe';
import React from 'react';

import { fireEvent, render, screen } from '~utils/test-utils';

import { PaymentOptionToggle, PaymentOptionToggleProps } from './PaymentOptionToggle';

expect.extend(toHaveNoViolations);

// ─── Mocks ───────────────────────────────────────────────────────────────────

jest.mock('@whitbread-eos/api', () => ({
  paymentOptions: {
    PAY_NOW: 'PAY_NOW',
    PAY_ON_ARRIVAL: 'PAY_ON_ARRIVAL',
    RESERVE_WITHOUT_CARD: 'RESERVE_WITHOUT_CARD',
  },
}));

// ─── Fixtures ────────────────────────────────────────────────────────────────

/**
 * Both PAY_NOW and PAY_ON_ARRIVAL enabled — the only state in which the toggle renders.
 * Shape matches PaymentOption from @whitbread-eos/api: { type, order, enabled }
 */
const bothEnabled = [
  { type: paymentOptions.PAY_NOW, order: 1, enabled: true },
  { type: paymentOptions.PAY_ON_ARRIVAL, order: 2, enabled: true },
];

const renderComponent = (
  selectedType: string,
  onChange = jest.fn(),
  overrides: Partial<PaymentOptionToggleProps> = {}
) =>
  render(
    <ChakraProvider>
      <PaymentOptionToggle
        options={bothEnabled}
        selectedType={selectedType}
        onChange={onChange}
        {...overrides}
      />
    </ChakraProvider>
  );

// ─── Tests ───────────────────────────────────────────────────────────────────

describe('PaymentOptionToggle', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });

  // ── Null-render guard ────────────────────────────────────────────────────
  describe('null render guard — renders nothing unless both options are enabled', () => {
    it('renders null when only PAY_NOW is enabled', () => {
      render(
        <ChakraProvider>
          <PaymentOptionToggle
            options={[{ type: paymentOptions.PAY_NOW, order: 1, enabled: true }]}
            selectedType={paymentOptions.PAY_NOW}
            onChange={jest.fn()}
          />
        </ChakraProvider>
      );
      expect(screen.queryByTestId('PaymentOptionToggle')).not.toBeInTheDocument();
    });

    it('renders null when only PAY_ON_ARRIVAL is enabled', () => {
      render(
        <ChakraProvider>
          <PaymentOptionToggle
            options={[{ type: paymentOptions.PAY_ON_ARRIVAL, order: 1, enabled: true }]}
            selectedType={paymentOptions.PAY_ON_ARRIVAL}
            onChange={jest.fn()}
          />
        </ChakraProvider>
      );
      expect(screen.queryByTestId('PaymentOptionToggle')).not.toBeInTheDocument();
    });

    it('renders null when both options are present but both disabled', () => {
      render(
        <ChakraProvider>
          <PaymentOptionToggle
            options={[
              { type: paymentOptions.PAY_NOW, order: 1, enabled: false },
              { type: paymentOptions.PAY_ON_ARRIVAL, order: 2, enabled: false },
            ]}
            selectedType={paymentOptions.PAY_NOW}
            onChange={jest.fn()}
          />
        </ChakraProvider>
      );
      expect(screen.queryByTestId('PaymentOptionToggle')).not.toBeInTheDocument();
    });

    it('renders null when PAY_NOW is enabled but PAY_ON_ARRIVAL is disabled', () => {
      render(
        <ChakraProvider>
          <PaymentOptionToggle
            options={[
              { type: paymentOptions.PAY_NOW, order: 1, enabled: true },
              { type: paymentOptions.PAY_ON_ARRIVAL, order: 2, enabled: false },
            ]}
            selectedType={paymentOptions.PAY_NOW}
            onChange={jest.fn()}
          />
        </ChakraProvider>
      );
      expect(screen.queryByTestId('PaymentOptionToggle')).not.toBeInTheDocument();
    });

    it('renders null when options array is empty', () => {
      render(
        <ChakraProvider>
          <PaymentOptionToggle
            options={[]}
            selectedType={paymentOptions.PAY_NOW}
            onChange={jest.fn()}
          />
        </ChakraProvider>
      );
      expect(screen.queryByTestId('PaymentOptionToggle')).not.toBeInTheDocument();
    });

    it('renders when both PAY_NOW and PAY_ON_ARRIVAL are enabled', () => {
      renderComponent(paymentOptions.PAY_NOW);
      expect(screen.getByTestId('PaymentOptionToggle')).toBeInTheDocument();
    });
  });

  // ── Structure ────────────────────────────────────────────────────────────
  describe('Structure', () => {
    it('renders the default title', () => {
      renderComponent(paymentOptions.PAY_NOW);
      expect(screen.getByTestId('PaymentOptionToggle-Title')).toHaveTextContent(
        'When would you like to pay?'
      );
    });

    it('renders a custom title when provided', () => {
      renderComponent(paymentOptions.PAY_NOW, jest.fn(), { title: 'Choose payment time' });
      expect(screen.getByTestId('PaymentOptionToggle-Title')).toHaveTextContent(
        'Choose payment time'
      );
    });

    it('renders the toggle bar', () => {
      renderComponent(paymentOptions.PAY_NOW);
      expect(screen.getByTestId('PaymentOptionToggle-Toggle')).toBeInTheDocument();
    });

    it('renders a button for each option', () => {
      renderComponent(paymentOptions.PAY_NOW);
      expect(
        screen.getByTestId(`PaymentOptionToggle-Option-${paymentOptions.PAY_NOW}`)
      ).toBeInTheDocument();
      expect(
        screen.getByTestId(`PaymentOptionToggle-Option-${paymentOptions.PAY_ON_ARRIVAL}`)
      ).toBeInTheDocument();
    });

    it('renders the default option labels from DEFAULT_OPTION_CONFIG', () => {
      renderComponent(paymentOptions.PAY_NOW);
      expect(screen.getByText('Pay now')).toBeInTheDocument();
      expect(screen.getByText('Pay on arrival')).toBeInTheDocument();
    });
  });

  // ── ARIA roles ────────────────────────────────────────────────────────────
  describe('ARIA roles', () => {
    it('renders the toggle bar as a radiogroup', () => {
      renderComponent(paymentOptions.PAY_NOW);
      expect(screen.getByRole('radiogroup')).toBeInTheDocument();
    });

    it('renders each option as a radio button', () => {
      renderComponent(paymentOptions.PAY_NOW);
      expect(screen.getAllByRole('radio')).toHaveLength(2);
    });

    it('sets aria-label on the radiogroup matching the title', () => {
      renderComponent(paymentOptions.PAY_NOW);
      expect(screen.getByRole('radiogroup')).toHaveAttribute(
        'aria-label',
        'When would you like to pay?'
      );
    });

    it('sets aria-label on the radiogroup matching a custom title', () => {
      renderComponent(paymentOptions.PAY_NOW, jest.fn(), { title: 'Pick a time' });
      expect(screen.getByRole('radiogroup')).toHaveAttribute('aria-label', 'Pick a time');
    });

    it('sets aria-checked="true" only on the selected option (PAY_NOW)', () => {
      renderComponent(paymentOptions.PAY_NOW);
      expect(
        screen.getByTestId(`PaymentOptionToggle-Option-${paymentOptions.PAY_NOW}`)
      ).toHaveAttribute('aria-checked', 'true');
      expect(
        screen.getByTestId(`PaymentOptionToggle-Option-${paymentOptions.PAY_ON_ARRIVAL}`)
      ).toHaveAttribute('aria-checked', 'false');
    });

    it('sets aria-checked="true" only on the selected option (PAY_ON_ARRIVAL)', () => {
      renderComponent(paymentOptions.PAY_ON_ARRIVAL);
      expect(
        screen.getByTestId(`PaymentOptionToggle-Option-${paymentOptions.PAY_ON_ARRIVAL}`)
      ).toHaveAttribute('aria-checked', 'true');
      expect(
        screen.getByTestId(`PaymentOptionToggle-Option-${paymentOptions.PAY_NOW}`)
      ).toHaveAttribute('aria-checked', 'false');
    });

    it('marks no option as selected when selectedType does not match any option', () => {
      renderComponent('UNKNOWN_TYPE');
      screen.getAllByRole('radio').forEach((radio) => {
        expect(radio).toHaveAttribute('aria-checked', 'false');
      });
    });
  });

  // ── "default" selectedType normalisation ──────────────────────────────────
  describe('"default" selectedType normalisation', () => {
    it('treats selectedType "default" as PAY_ON_ARRIVAL for aria-checked', () => {
      renderComponent('default');
      expect(
        screen.getByTestId(`PaymentOptionToggle-Option-${paymentOptions.PAY_ON_ARRIVAL}`)
      ).toHaveAttribute('aria-checked', 'true');
      expect(
        screen.getByTestId(`PaymentOptionToggle-Option-${paymentOptions.PAY_NOW}`)
      ).toHaveAttribute('aria-checked', 'false');
    });

    it('renders the PAY_ON_ARRIVAL description when selectedType is "default"', () => {
      renderComponent('default');
      // DEFAULT_OPTION_CONFIG gives PAY_ON_ARRIVAL a description; it should be shown
      expect(screen.getByTestId('PaymentOptionToggle-Description')).toBeInTheDocument();
    });
  });

  // ── Description ───────────────────────────────────────────────────────────
  describe('Description', () => {
    it('renders the PAY_ON_ARRIVAL description from the default config', () => {
      renderComponent(paymentOptions.PAY_ON_ARRIVAL);
      expect(screen.getByTestId('PaymentOptionToggle-Description')).toBeInTheDocument();
    });

    it('renders the PAY_NOW description from the default config', () => {
      renderComponent(paymentOptions.PAY_NOW);
      expect(screen.getByTestId('PaymentOptionToggle-Description')).toBeInTheDocument();
    });

    it('renders a ReactNode description from a custom optionConfig', () => {
      renderComponent(paymentOptions.PAY_NOW, jest.fn(), {
        optionConfig: [
          {
            type: paymentOptions.PAY_NOW,
            label: 'Pay now',
            description: <strong data-testid="rich-desc">Bold text</strong>,
          },
          {
            type: paymentOptions.PAY_ON_ARRIVAL,
            label: 'Pay on arrival',
          },
        ],
      });
      expect(screen.getByTestId('rich-desc')).toBeInTheDocument();
    });

    it('does not render a description when the selected option has none in optionConfig', () => {
      renderComponent(paymentOptions.PAY_ON_ARRIVAL, jest.fn(), {
        optionConfig: [
          { type: paymentOptions.PAY_NOW, label: 'Pay now' },
          { type: paymentOptions.PAY_ON_ARRIVAL, label: 'Pay on arrival' },
        ],
      });
      expect(screen.queryByTestId('PaymentOptionToggle-Description')).not.toBeInTheDocument();
    });
  });

  // ── Custom optionConfig ───────────────────────────────────────────────────
  describe('Custom optionConfig', () => {
    const customConfig = [
      {
        type: paymentOptions.PAY_NOW,
        label: 'Now',
        description: 'Charged today',
      },
      {
        type: paymentOptions.PAY_ON_ARRIVAL,
        label: 'Later',
        description: 'Charged on arrival',
      },
    ];

    it('uses custom labels from optionConfig', () => {
      renderComponent(paymentOptions.PAY_NOW, jest.fn(), { optionConfig: customConfig });
      expect(screen.getByText('Now')).toBeInTheDocument();
      expect(screen.getByText('Later')).toBeInTheDocument();
    });

    it('does not render the default labels when optionConfig is overridden', () => {
      renderComponent(paymentOptions.PAY_NOW, jest.fn(), { optionConfig: customConfig });
      expect(screen.queryByText('Pay now')).not.toBeInTheDocument();
      expect(screen.queryByText('Pay on arrival')).not.toBeInTheDocument();
    });

    it('shows the description for the selected option from custom optionConfig', () => {
      renderComponent(paymentOptions.PAY_NOW, jest.fn(), { optionConfig: customConfig });
      expect(screen.getByTestId('PaymentOptionToggle-Description')).toHaveTextContent(
        'Charged today'
      );
    });

    it('switches description when a different option is selected', () => {
      renderComponent(paymentOptions.PAY_ON_ARRIVAL, jest.fn(), { optionConfig: customConfig });
      expect(screen.getByTestId('PaymentOptionToggle-Description')).toHaveTextContent(
        'Charged on arrival'
      );
    });
  });

  // ── Interaction ───────────────────────────────────────────────────────────
  describe('Interaction', () => {
    it('calls onChange with PAY_ON_ARRIVAL when that option is clicked', () => {
      const onChange = jest.fn();
      renderComponent(paymentOptions.PAY_NOW, onChange);
      fireEvent.click(
        screen.getByTestId(`PaymentOptionToggle-Option-${paymentOptions.PAY_ON_ARRIVAL}`)
      );
      expect(onChange).toHaveBeenCalledTimes(1);
      expect(onChange).toHaveBeenCalledWith(paymentOptions.PAY_ON_ARRIVAL);
    });

    it('calls onChange with PAY_NOW when that option is clicked', () => {
      const onChange = jest.fn();
      renderComponent(paymentOptions.PAY_ON_ARRIVAL, onChange);
      fireEvent.click(screen.getByTestId(`PaymentOptionToggle-Option-${paymentOptions.PAY_NOW}`));
      expect(onChange).toHaveBeenCalledTimes(1);
      expect(onChange).toHaveBeenCalledWith(paymentOptions.PAY_NOW);
    });

    it('calls onChange when the already-selected option is clicked again', () => {
      const onChange = jest.fn();
      renderComponent(paymentOptions.PAY_NOW, onChange);
      fireEvent.click(screen.getByTestId(`PaymentOptionToggle-Option-${paymentOptions.PAY_NOW}`));
      expect(onChange).toHaveBeenCalledWith(paymentOptions.PAY_NOW);
    });

    it('does not throw when onChange is called for any option', () => {
      const onChange = jest.fn();
      renderComponent(paymentOptions.PAY_NOW, onChange);
      expect(() => {
        fireEvent.click(
          screen.getByTestId(`PaymentOptionToggle-Option-${paymentOptions.PAY_ON_ARRIVAL}`)
        );
      }).not.toThrow();
    });
  });

  // ── Custom data-testid ────────────────────────────────────────────────────
  describe('Custom data-testid', () => {
    it('uses the provided data-testid as prefix for all child test ids', () => {
      renderComponent(paymentOptions.PAY_NOW, jest.fn(), { 'data-testid': 'CustomToggle' });
      expect(screen.getByTestId('CustomToggle')).toBeInTheDocument();
      expect(screen.getByTestId('CustomToggle-Title')).toBeInTheDocument();
      expect(screen.getByTestId('CustomToggle-Toggle')).toBeInTheDocument();
      expect(
        screen.getByTestId(`CustomToggle-Option-${paymentOptions.PAY_NOW}`)
      ).toBeInTheDocument();
      expect(
        screen.getByTestId(`CustomToggle-Option-${paymentOptions.PAY_ON_ARRIVAL}`)
      ).toBeInTheDocument();
    });
  });

  // ── Accessibility ─────────────────────────────────────────────────────────
  describe('Accessibility', () => {
    it('has no accessibility violations when PAY_NOW is selected', async () => {
      const { container } = renderComponent(paymentOptions.PAY_NOW);
      expect(await axe(container)).toHaveNoViolations();
    });

    it('has no accessibility violations when PAY_ON_ARRIVAL is selected', async () => {
      const { container } = renderComponent(paymentOptions.PAY_ON_ARRIVAL);
      expect(await axe(container)).toHaveNoViolations();
    });

    it('has no accessibility violations with a custom optionConfig', async () => {
      const { container } = renderComponent(paymentOptions.PAY_NOW, jest.fn(), {
        optionConfig: [
          { type: paymentOptions.PAY_NOW, label: 'Pay now', description: 'Charged today' },
          {
            type: paymentOptions.PAY_ON_ARRIVAL,
            label: 'Pay on arrival',
            description: 'Charged at hotel',
          },
        ],
      });
      expect(await axe(container)).toHaveNoViolations();
    });
  });
});
