import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { getPathForLocale } from '@whitbread-eos/utils/server';

import { AuthGuard } from './auth-guard';

const mockCookieWatcherResult = {
  value: true,
};

jest.mock('@whitbread-eos/utils', () => ({
  useCookieWatcher: jest.fn(() => mockCookieWatcherResult.value),
  ID_TOKEN_COOKIE: 'id_token_cookie',
  PIB_MANUAL_LOGOUT_FLAG: 'pib_manual_logout',
  getCountryLanguageByLocale: jest.fn(() => ({ language: 'en', country: 'gb' })),
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  getPathForLocale: jest.fn(() => '/mocked-login-path'),
}));

const sessionStore: Record<string, string> = {};

const sessionStorageMock = {
  getItem: jest.fn((key: string) => sessionStore[key] ?? null),
  setItem: jest.fn((key: string, value: string) => {
    sessionStore[key] = value;
  }),
  removeItem: jest.fn((key: string) => {
    delete sessionStore[key];
  }),
};

Object.defineProperty(window, 'sessionStorage', {
  value: sessionStorageMock,
});

describe('AuthGuard', () => {
  const secureUrl = 'https://secure.example.com';
  const locale = LOCALES.EN;

  beforeEach(() => {
    jest.clearAllMocks();
    Object.keys(sessionStore).forEach((key) => delete sessionStore[key]);
  });

  it('renders the hidden iframe with correct src and attributes', () => {
    mockCookieWatcherResult.value = true;

    render(<AuthGuard secureUrl={secureUrl} locale={locale} />);

    const iframe = screen.getByTestId('InnBusiness-Iframe');
    expect(iframe).toBeInTheDocument();
    expect(iframe).toHaveAttribute('src', `${secureUrl}/gb/en/business-booker/common/login.html`);
    expect(iframe).toHaveStyle({ display: 'none' });
    expect(iframe).toHaveAttribute('id', 'authIframe');
    expect(iframe).toHaveAttribute('title', 'InnBusiness authentication');
  });

  it('does not redirect if authenticated', () => {
    mockCookieWatcherResult.value = true;
    delete (window as any).location;
    (window as any).location = { href: '' };

    render(<AuthGuard secureUrl={secureUrl} locale={locale} />);
    expect(window.location.href).toBe('');
    expect(getPathForLocale).not.toHaveBeenCalled();
  });

  it('redirects to login with timeout=true if not authenticated and no manual logout flag', () => {
    mockCookieWatcherResult.value = false;
    delete (window as any).location;
    window.location = { href: '' } as any;

    render(<AuthGuard secureUrl={secureUrl} locale={locale} />);
    expect(getPathForLocale).toHaveBeenCalledWith(locale, 'account/login?timeout=true');
    expect(window.location.href).toBe('/mocked-login-path');
    expect(sessionStorageMock.removeItem).toHaveBeenCalledWith('pib_manual_logout');
  });

  it('redirects to login without timeout parameter if manual logout flag is set', () => {
    mockCookieWatcherResult.value = false;
    sessionStore['pib_manual_logout'] = 'true';
    delete (window as any).location;
    window.location = { href: '' } as any;

    render(<AuthGuard secureUrl={secureUrl} locale={locale} />);
    expect(sessionStorageMock.getItem).toHaveBeenCalledWith('pib_manual_logout');
    expect(getPathForLocale).toHaveBeenCalledWith(locale, 'account/login');
    expect(window.location.href).toBe('/mocked-login-path');
    expect(sessionStorageMock.removeItem).toHaveBeenCalledWith('pib_manual_logout');
  });
});
