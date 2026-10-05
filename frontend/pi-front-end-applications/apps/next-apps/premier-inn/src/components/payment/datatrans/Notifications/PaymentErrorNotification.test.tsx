import { ChakraProvider } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import { axe, toHaveNoViolations } from 'jest-axe';
import React from 'react';

import { render, screen } from '~utils/test-utils';

import { PaymentErrorNotification } from './PaymentErrorNotification';

expect.extend(toHaveNoViolations);

// ─── Mocks ───────────────────────────────────────────────────────────────────

jest.mock('@whitbread-eos/utils', () => ({
  renderSanitizedHtml: jest.fn((html) => html),
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
  Info: ({ color }) => <svg data-testid="InfoIcon" data-color={color} />,
}));

const wrap = (ui: React.ReactElement) => render(<ChakraProvider>{ui}</ChakraProvider>);

// ─── Fixtures ─────────────────────────────────────────────────────────────────

const errorMessage = 'Your payment was declined. Please try again.';

// ─── Tests ───────────────────────────────────────────────────────────────────

describe('PaymentErrorNotification', () => {
  it('returns null when isVisible is false', () => {
    wrap(<PaymentErrorNotification isVisible={false} paymentFailedErrorMessage={errorMessage} />);
    expect(screen.queryByTestId('Payment-Failed-Error-Notification')).not.toBeInTheDocument();
  });

  it('renders when isVisible is true', () => {
    wrap(<PaymentErrorNotification isVisible={true} paymentFailedErrorMessage={errorMessage} />);
    expect(screen.getByTestId('Payment-Failed-Error-Notification')).toBeInTheDocument();
  });

  it('renders an error variant notification', () => {
    wrap(<PaymentErrorNotification isVisible={true} paymentFailedErrorMessage={errorMessage} />);
    expect(screen.getByTestId('Payment-Failed-Error-Notification')).toHaveAttribute(
      'data-variant',
      'error'
    );
    expect(screen.getByTestId('Payment-Failed-Error-Notification')).toHaveAttribute(
      'data-status',
      'error'
    );
  });

  it('displays the provided error message', () => {
    wrap(<PaymentErrorNotification isVisible={true} paymentFailedErrorMessage={errorMessage} />);
    expect(screen.getByTestId('Payment-Failed-Error-Notification')).toHaveTextContent(errorMessage);
  });

  it('renders an info icon with the error colour variable', () => {
    wrap(<PaymentErrorNotification isVisible={true} paymentFailedErrorMessage={errorMessage} />);
    expect(screen.getByTestId('InfoIcon')).toHaveAttribute(
      'data-color',
      'var(--chakra-colors-error)'
    );
  });

  it('does not render when switching from visible to not visible', () => {
    const { rerender } = wrap(
      <PaymentErrorNotification isVisible={true} paymentFailedErrorMessage={errorMessage} />
    );
    expect(screen.getByTestId('Payment-Failed-Error-Notification')).toBeInTheDocument();

    rerender(
      <ChakraProvider>
        <PaymentErrorNotification isVisible={false} paymentFailedErrorMessage={errorMessage} />
      </ChakraProvider>
    );
    expect(screen.queryByTestId('Payment-Failed-Error-Notification')).not.toBeInTheDocument();
  });

  it('has no accessibility violations when visible', async () => {
    const { container } = wrap(
      <PaymentErrorNotification isVisible={true} paymentFailedErrorMessage={errorMessage} />
    );
    expect(await axe(container)).toHaveNoViolations();
  });
});
