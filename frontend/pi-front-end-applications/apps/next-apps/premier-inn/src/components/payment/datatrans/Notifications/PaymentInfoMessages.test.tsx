import { ChakraProvider } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import { axe, toHaveNoViolations } from 'jest-axe';
import React from 'react';

import { render, screen } from '~utils/test-utils';

import { PaymentInfoMessages } from './PaymentInfoMessages';

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

const messages = [
  { index: 0, messagesNotif: 'Info message one' },
  { index: 1, messagesNotif: 'Info message two' },
];

// ─── Tests ───────────────────────────────────────────────────────────────────

describe('PaymentInfoMessages', () => {
  it('returns null when messages array is empty', () => {
    wrap(<PaymentInfoMessages messages={[]} />);
    expect(screen.queryByTestId('PaymentType-InfoMessages')).not.toBeInTheDocument();
  });

  it('renders the container with the correct data-testid when messages exist', () => {
    wrap(<PaymentInfoMessages messages={messages} />);
    expect(screen.getByTestId('PaymentType-InfoMessages')).toBeInTheDocument();
  });

  it('renders one notification per message', () => {
    wrap(<PaymentInfoMessages messages={messages} />);
    expect(screen.getAllByTestId('Notification')).toHaveLength(2);
  });

  it('renders info variant notifications', () => {
    wrap(<PaymentInfoMessages messages={[messages[0]]} />);
    expect(screen.getByTestId('Notification')).toHaveAttribute('data-variant', 'info');
    expect(screen.getByTestId('Notification')).toHaveAttribute('data-status', 'info');
  });

  it('passes each message text to its notification', () => {
    wrap(<PaymentInfoMessages messages={messages} />);
    const notifications = screen.getAllByTestId('Notification');
    expect(notifications[0]).toHaveTextContent('Info message one');
    expect(notifications[1]).toHaveTextContent('Info message two');
  });

  it('renders with a single message', () => {
    wrap(<PaymentInfoMessages messages={[messages[0]]} />);
    expect(screen.getAllByTestId('Notification')).toHaveLength(1);
  });

  it('renders the info icon for each notification', () => {
    wrap(<PaymentInfoMessages messages={messages} />);
    expect(screen.getAllByTestId('InfoIcon')).toHaveLength(2);
  });

  it('has no accessibility violations', async () => {
    const { container } = wrap(<PaymentInfoMessages messages={messages} />);
    expect(await axe(container)).toHaveNoViolations();
  });
});
