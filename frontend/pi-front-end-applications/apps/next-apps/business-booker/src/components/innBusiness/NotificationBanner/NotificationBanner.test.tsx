import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { usePathname } from 'next/navigation';

import { NotificationBanner } from './NotificationBanner';

jest.mock('next/navigation', () => ({
  usePathname: jest.fn(),
}));

jest.mock('@whitbread-eos/atoms/ui', () => ({
  Notification: ({ type, message }: { type: string; message: string }) => (
    <div data-testid="notification" data-type={type}>
      {message}
    </div>
  ),
}));

jest.mock('@whitbread-eos/utils', () => ({
  formatIBAssetsUrl: (url: string) => url,
}));

const mockUsePathname = usePathname as jest.Mock;

const mockIcons = {
  'icon.notification.info': '/icons/info.svg',
  'icon.notification.success': '/icons/success.svg',
};

const enabledPages = ['/gb/en/contact-us.html', '/en-gb/contact-us'];

describe('NotificationBanner', () => {
  beforeEach(() => {
    mockUsePathname.mockReturnValue('/gb/en/contact-us.html');
  });

  it('renders nothing when contactBanner is null', () => {
    const { container } = render(<NotificationBanner contactBanner={null} icons={mockIcons} />);
    expect(container).toBeEmptyDOMElement();
  });

  it('renders nothing when text is empty', () => {
    const { container } = render(
      <NotificationBanner contactBanner={{ text: '', enabledPages }} icons={mockIcons} />
    );
    expect(container).toBeEmptyDOMElement();
  });

  it('renders nothing when currentPathname is not in enabledPages', () => {
    mockUsePathname.mockReturnValue('/gb/en/homepage');
    const { container } = render(
      <NotificationBanner
        contactBanner={{ text: 'Some message', enabledPages }}
        icons={mockIcons}
      />
    );
    expect(container).toBeEmptyDOMElement();
  });

  it('renders the notification when the page is enabled', () => {
    render(
      <NotificationBanner
        contactBanner={{ text: 'Some message', type: 'info', enabledPages }}
        icons={mockIcons}
      />
    );
    expect(screen.getByTestId('notification')).toBeInTheDocument();
    expect(screen.getByTestId('notification')).toHaveTextContent('Some message');
  });

  it('uses info as default type when type is not provided', () => {
    render(
      <NotificationBanner
        contactBanner={{ text: 'Some message', enabledPages }}
        icons={mockIcons}
      />
    );
    expect(screen.getByTestId('notification')).toHaveAttribute('data-type', 'info');
  });

  it('replaces {Date} placeholder with the date value', () => {
    render(
      <NotificationBanner
        contactBanner={{
          text: '{Date} We cannot process Visa card transactions.',
          date: '31st March 2026',
          enabledPages,
        }}
        icons={mockIcons}
      />
    );
    expect(screen.getByTestId('notification')).toHaveTextContent(
      '31st March 2026 We cannot process Visa card transactions.'
    );
  });

  it('removes {Date} and trims when date is empty', () => {
    render(
      <NotificationBanner
        contactBanner={{
          text: '{Date} We cannot process Visa card transactions.',
          date: '',
          enabledPages,
        }}
        icons={mockIcons}
      />
    );
    expect(screen.getByTestId('notification')).toHaveTextContent(
      'We cannot process Visa card transactions.'
    );
  });

  it('replaces {Date} when it appears in the middle of text', () => {
    render(
      <NotificationBanner
        contactBanner={{
          text: 'Issue since {Date} — service ongoing.',
          date: '31st March 2026',
          enabledPages,
        }}
        icons={mockIcons}
      />
    );
    expect(screen.getByTestId('notification')).toHaveTextContent(
      'Issue since 31st March 2026 — service ongoing.'
    );
  });
});
