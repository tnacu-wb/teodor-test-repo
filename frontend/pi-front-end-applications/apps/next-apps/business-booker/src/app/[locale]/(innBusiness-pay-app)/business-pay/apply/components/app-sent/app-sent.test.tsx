import '@testing-library/jest-dom';
import { render, screen, fireEvent } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import React from 'react';

import { getContactInfoEmailCaptionKey } from './app-sent';
import ApplicationSent from './app-sent';

// Mock dependencies
jest.mock('@whitbread-eos/atoms/ui', () => ({
  Button: ({ children, ...props }: any) => <button {...props}>{children}</button>,
  SanitizedContent: ({ children }: any) => <div>{children}</div>,
}));
jest.mock('@whitbread-eos/layout', () => ({
  useWizardContext: () => ({
    wizardState: {
      accountName: 'Test Company',
      applicationId: 12345,
      contactDetails: { email: 'test@example.com' },
    },
    icons: { 'icon.checkmark-white-purple': '/check.svg' },
  }),
}));
jest.mock('@whitbread-eos/utils/server', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
  getLocaleByPathname: () => 'en',
  formatIBAssetsUrl: (url: string) => url,
  cn: (...args: string[]) => args.join(' '),
  getPathForLocale: (_locale: string, path: string) => `/${path}`,
}));
jest.mock('next/image', () => {
  const MockImage = (props: any) => <img {...props} />;
  MockImage.displayName = 'MockNextImage';
  return MockImage;
});
jest.mock('next/link', () => {
  const MockLink = ({ children, ...props }: any) => <a {...props}>{children}</a>;
  MockLink.displayName = 'MockNextLink';
  return MockLink;
});
jest.mock('next/navigation', () => ({
  usePathname: () => '/en/apply',
}));

jest.mock('./../../../../../(innBusiness)/business-pay/components/AppDetailsCard', () => ({
  AppDetailsCard: (props: any) => <div data-testid="AppDetailsCard">{JSON.stringify(props)}</div>,
}));

describe('ApplicationSent', () => {
  it('renders the main container and title', () => {
    render(<ApplicationSent />);
    expect(screen.getByTestId('AppSuccessfullySubmitted-container')).toBeInTheDocument();
    expect(screen.getByTestId('AppSuccessfullySubmitted-title')).toBeInTheDocument();
  });

  it('renders the checkmark icon', () => {
    render(<ApplicationSent />);
    expect(screen.getByTestId('CheckIcon')).toBeInTheDocument();
  });

  it('renders AppDetailsCard with correct props', () => {
    render(<ApplicationSent />);
    expect(screen.getByTestId('AppDetailsCard')).toBeInTheDocument();
    expect(screen.getByTestId('AppDetailsCard').textContent).toContain('Test Company');
    expect(screen.getByTestId('AppDetailsCard').textContent).toContain('12345');
    expect(screen.getByTestId('AppDetailsCard').textContent).toContain('test@example.com');
  });

  it('renders the contact info and sanitized content', () => {
    render(<ApplicationSent />);
    expect(screen.getByText('application.sent.getInTouch')).toBeInTheDocument();
  });

  it('renders the back to home link and button', () => {
    render(<ApplicationSent />);
    expect(screen.getByTestId('AppSuccessfullySubmitted-back-to-home-link')).toBeInTheDocument();
    expect(screen.getByTestId('AppSuccessfullySubmitted-back-to-home-button')).toBeInTheDocument();
  });

  it('disables the button after click', () => {
    render(<ApplicationSent />);
    const button = screen.getByTestId('AppSuccessfullySubmitted-back-to-home-button');
    fireEvent.click(button);
    expect(button).toBeDisabled();
  });
});

describe('getContactInfoEmail', () => {
  it('returns DE email for de-de locale', () => {
    expect(getContactInfoEmailCaptionKey(LOCALES.DE)).toBe(
      'payApp.innBusiness.contactEmail.germany'
    );
  });

  it('returns EN email for en-gb locale', () => {
    expect(getContactInfoEmailCaptionKey(LOCALES.EN)).toBe('payApp.innBusiness.contactEmail.uk');
    expect(getContactInfoEmailCaptionKey()).toBe('payApp.innBusiness.contactEmail.uk');
  });
});
