import { ChakraProvider } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import { axe, toHaveNoViolations } from 'jest-axe';
import React from 'react';

import { render, screen } from '~utils/test-utils';

import { HotelMessages } from './HotelMessages';

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
  { infoMsg: '<p>Important notice</p>', indexOrder: 0 },
  { infoMsg: '<p>Secondary notice</p>', indexOrder: 1 },
];

// ─── Tests ───────────────────────────────────────────────────────────────────

describe('HotelMessages', () => {
  it('returns null when messages array is empty', () => {
    wrap(<HotelMessages messages={[]} />);
    expect(screen.queryByTestId('BookingSummary-InfoMessages')).not.toBeInTheDocument();
  });

  it('renders the container with the correct data-testid when messages exist', () => {
    wrap(<HotelMessages messages={messages} />);
    expect(screen.getByTestId('BookingSummary-InfoMessages')).toBeInTheDocument();
  });

  it('renders one notification per message', () => {
    wrap(<HotelMessages messages={messages} />);
    expect(screen.getAllByTestId('Notification')).toHaveLength(2);
  });

  it('skips entries where infoMsg is an empty string', () => {
    const withEmpty = [
      { infoMsg: '<p>Valid</p>', indexOrder: 0 },
      { infoMsg: '', indexOrder: 1 },
    ];
    wrap(<HotelMessages messages={withEmpty} />);
    expect(screen.getAllByTestId('Notification')).toHaveLength(1);
  });

  it('renders info variant notifications', () => {
    wrap(<HotelMessages messages={[messages[0]]} />);
    expect(screen.getByTestId('Notification')).toHaveAttribute('data-variant', 'info');
    expect(screen.getByTestId('Notification')).toHaveAttribute('data-status', 'info');
  });

  it('passes the sanitized html to the notification description', () => {
    wrap(<HotelMessages messages={[{ infoMsg: 'Hello world', indexOrder: 0 }]} />);
    expect(screen.getByTestId('Notification')).toHaveTextContent('Hello world');
  });

  it('renders the info icon for each notification', () => {
    wrap(<HotelMessages messages={messages} />);
    expect(screen.getAllByTestId('InfoIcon')).toHaveLength(2);
  });

  it('has no accessibility violations', async () => {
    const { container } = wrap(<HotelMessages messages={messages} />);
    expect(await axe(container)).toHaveNoViolations();
  });
});
