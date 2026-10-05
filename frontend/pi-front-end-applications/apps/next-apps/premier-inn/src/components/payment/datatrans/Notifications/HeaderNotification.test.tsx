import { ChakraProvider } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import { axe, toHaveNoViolations } from 'jest-axe';
import React from 'react';

import { render, screen } from '~utils/test-utils';

import { HeaderNotification } from './HeaderNotification';

expect.extend(toHaveNoViolations);

// ─── Mocks ───────────────────────────────────────────────────────────────────

jest.mock('next-i18next', () => ({
  useTranslation: () => ({
    t: (key) => key,
  }),
}));

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

// ─── Tests ───────────────────────────────────────────────────────────────────

describe('HeaderNotification', () => {
  it('renders the notification', () => {
    wrap(<HeaderNotification />);
    expect(screen.getByTestId('Notification')).toBeInTheDocument();
  });

  it('renders an info variant notification', () => {
    wrap(<HeaderNotification />);
    expect(screen.getByTestId('Notification')).toHaveAttribute('data-variant', 'info');
    expect(screen.getByTestId('Notification')).toHaveAttribute('data-status', 'info');
  });

  it('renders the info icon', () => {
    wrap(<HeaderNotification />);
    expect(screen.getByTestId('InfoIcon')).toBeInTheDocument();
  });

  it('displays the translated header notification key', () => {
    wrap(<HeaderNotification />);
    // useTranslation mock returns the key as-is
    expect(screen.getByTestId('Notification')).toHaveTextContent('booking.header.notification');
  });

  it('has no accessibility violations', async () => {
    const { container } = wrap(<HeaderNotification />);
    expect(await axe(container)).toHaveNoViolations();
  });
});
