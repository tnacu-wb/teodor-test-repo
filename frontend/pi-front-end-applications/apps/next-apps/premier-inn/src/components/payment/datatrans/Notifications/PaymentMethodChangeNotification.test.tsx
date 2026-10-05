import { ChakraProvider } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import { axe, toHaveNoViolations } from 'jest-axe';
import React from 'react';

import { render, screen } from '~utils/test-utils';

import { PaymentMethodChangeNotification } from './PaymentMethodChangeNotification';

expect.extend(toHaveNoViolations);

// ─── Mocks ───────────────────────────────────────────────────────────────────

jest.mock('next-i18next', () => ({
  useTranslation: () => ({
    t: (key) => key,
  }),
}));

jest.mock('@whitbread-eos/atoms', () => ({
  Notification: ({ description, variant, status, prefixDataTestId, svg }) => (
    <div
      data-testid={prefixDataTestId ? `${prefixDataTestId}-Notification` : 'Notification'}
      data-variant={variant}
      data-status={status}
    >
      {svg}
      {description}
    </div>
  ),
  Alert: () => <svg data-testid="AlertIcon" />,
}));

const wrap = (ui: React.ReactElement) => render(<ChakraProvider>{ui}</ChakraProvider>);

// ─── Tests ───────────────────────────────────────────────────────────────────

describe('PaymentMethodChangeNotification', () => {
  it('returns null when isVisible is false', () => {
    wrap(<PaymentMethodChangeNotification isVisible={false} />);
    expect(screen.queryByTestId('PaypalPaymentType-InfoMessages')).not.toBeInTheDocument();
  });

  it('renders when isVisible is true', () => {
    wrap(<PaymentMethodChangeNotification isVisible={true} />);
    expect(screen.getByTestId('PaypalPaymentType-InfoMessages')).toBeInTheDocument();
  });

  it('renders an alert variant notification', () => {
    wrap(<PaymentMethodChangeNotification isVisible={true} />);
    expect(screen.getByTestId('Notification')).toHaveAttribute('data-variant', 'alert');
    expect(screen.getByTestId('Notification')).toHaveAttribute('data-status', 'warning');
  });

  it('displays the translated notification title key', () => {
    wrap(<PaymentMethodChangeNotification isVisible={true} />);
    expect(screen.getByTestId('Notification')).toHaveTextContent(
      'booking.payment.paynow.notification.title'
    );
  });

  it('displays the translated notification message key', () => {
    wrap(<PaymentMethodChangeNotification isVisible={true} />);
    expect(screen.getByTestId('Notification')).toHaveTextContent(
      'booking.payment.paynow.notification.message'
    );
  });

  it('renders the alert icon', () => {
    wrap(<PaymentMethodChangeNotification isVisible={true} />);
    expect(screen.getByTestId('AlertIcon')).toBeInTheDocument();
  });

  it('does not render when switching from visible to not visible', () => {
    const { rerender } = wrap(<PaymentMethodChangeNotification isVisible={true} />);
    expect(screen.getByTestId('PaypalPaymentType-InfoMessages')).toBeInTheDocument();

    rerender(
      <ChakraProvider>
        <PaymentMethodChangeNotification isVisible={false} />
      </ChakraProvider>
    );
    expect(screen.queryByTestId('PaypalPaymentType-InfoMessages')).not.toBeInTheDocument();
  });

  it('has no accessibility violations when visible', async () => {
    const { container } = wrap(<PaymentMethodChangeNotification isVisible={true} />);
    expect(await axe(container)).toHaveNoViolations();
  });
});
