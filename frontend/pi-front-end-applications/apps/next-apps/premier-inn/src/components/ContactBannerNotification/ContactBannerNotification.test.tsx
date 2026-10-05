import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import ContactBannerNotification from './ContactBannerNotification';

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  Alert: () => <svg data-testid="AlertIcon" />,
  Error: () => <svg data-testid="ErrorIcon" />,
  Info: () => <svg data-testid="InfoIcon" />,
  Success: () => <svg data-testid="SuccessIcon" />,
  Notification: ({ description, isInnerHTML, status, svg, variant }: any) => (
    <div
      data-inner-html={isInnerHTML ? 'true' : 'false'}
      data-testid="ContactBannerNotification"
      data-variant={variant}
      role={status === 'error' || status === 'warning' ? 'alert' : 'status'}
    >
      {svg}
      <span>{description}</span>
    </div>
  ),
}));

const contactBanner = {
  date: '10 July 2026',
  enabledPages: ['/gb/en/contact-us.html'],
  text: 'Call us before {date}',
  type: 'info',
};

describe('ContactBannerNotification', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    window.history.pushState({}, '', '/gb/en/contact-us.html?ARRdd=10');
  });

  it('renders the banner when the current page is enabled', () => {
    const { getByTestId } = render(<ContactBannerNotification contactBanner={contactBanner} />);

    expect(getByTestId('ContactBannerNotification')).toHaveTextContent(
      'Call us before 10 July 2026'
    );
    expect(getByTestId('ContactBannerNotification')).toHaveAttribute('data-variant', 'info');
    expect(getByTestId('InfoIcon')).toBeInTheDocument();
  });

  it('renders the banner using window location pathname without query params', () => {
    window.history.pushState({}, '', '/gb/en/contact-us.html');

    const { getByTestId } = render(<ContactBannerNotification contactBanner={contactBanner} />);

    expect(getByTestId('ContactBannerNotification')).toHaveTextContent(
      'Call us before 10 July 2026'
    );
  });

  it('does not render when the current page is not enabled', () => {
    window.history.pushState({}, '', '/gb/en/search.html');

    const { queryByTestId } = render(<ContactBannerNotification contactBanner={contactBanner} />);

    expect(queryByTestId('ContactBannerNotification')).not.toBeInTheDocument();
  });

  it('does not render when the banner text is missing', () => {
    const { queryByTestId } = render(
      <ContactBannerNotification contactBanner={{ ...contactBanner, text: '' }} />
    );

    expect(queryByTestId('ContactBannerNotification')).not.toBeInTheDocument();
  });

  it('normalises whitespace and replaces the date placeholder case-insensitively', () => {
    const { getByTestId } = render(
      <ContactBannerNotification
        contactBanner={{ ...contactBanner, text: 'Call   us\nby {DATE}' }}
      />
    );

    expect(getByTestId('ContactBannerNotification')).toHaveTextContent('Call us by 10 July 2026');
  });

  it('passes HTML content through to the Notification component', () => {
    const { getByTestId } = render(
      <ContactBannerNotification
        contactBanner={{ ...contactBanner, text: 'Visit <a href="/contact-us">contact us</a>' }}
      />
    );

    expect(getByTestId('ContactBannerNotification')).toHaveAttribute('data-inner-html', 'true');
    expect(getByTestId('ContactBannerNotification')).toHaveTextContent(
      'Visit <a href="/contact-us">contact us</a>'
    );
  });

  it.each([
    ['alert', 'alert', 'alert', 'AlertIcon'],
    ['error', 'error', 'alert', 'ErrorIcon'],
    ['success', 'success', 'status', 'SuccessIcon'],
    ['unknown', 'info', 'status', 'InfoIcon'],
  ])(
    'maps %s banner type to the expected notification props',
    (type, variant, role, iconTestId) => {
      const { getByRole, getByTestId } = render(
        <ContactBannerNotification contactBanner={{ ...contactBanner, type }} />
      );

      expect(getByRole(role)).toHaveAttribute('data-variant', variant);
      expect(getByTestId(iconTestId)).toBeInTheDocument();
    }
  );
});
