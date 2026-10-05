import '@testing-library/jest-dom';
import { render, screen, fireEvent } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { useInnBusinessLogin } from '@whitbread-eos/utils';
import React from 'react';

import { LoginForm } from './login-form';

jest.mock('@whitbread-eos/utils', () => ({
  useInnBusinessLogin: jest.fn(),
  getPathForLocale: jest.fn((locale: string, path: string) => `/${locale}/${path}`),
  getCountryLanguageByLocale: jest.fn(() => ({ language: 'en', country: 'gb' })),
  setPageAnalytics: jest.fn(),
  analytics: {
    update: jest.fn(),
  },
  cn: (...args: string[]) => args.filter(Boolean).join(' '),
  findError: () => undefined,
  formatIBAssetsUrl: () => '/',
  useTranslation: () => {
    return {
      t: (str: string) => str,
    };
  },
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  getCommonIcons: () => [],
  getTranslations: () => {
    return {
      t: (str: string) => str,
    };
  },
  getPathForLocale: () => {
    return '/en-gb/account/register';
  },
}));

jest.mock('@whitbread-eos/layout', () => ({
  WizardPage: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="wizard-page">{children}</div>
  ),
}));

jest.mock('next/navigation', () => ({
  usePathname: jest.fn(() => '/en-gb/account/login'),
}));

describe('LoginForm', () => {
  const mockHandleLogin = jest.fn();
  const mockUseInnBusinessLogin = {
    isError: false,
    isSubmitting: false,
    handleLogin: jest.fn((email, password) => {
      mockHandleLogin(email, password);
    }),
  };

  beforeEach(() => {
    jest.clearAllMocks();
    (useInnBusinessLogin as jest.Mock).mockReturnValue(mockUseInnBusinessLogin);
  });

  const defaultProps = {
    icons: { 'icon.notification.error': 'error-icon-url' },
    baseDataTestId: 'login-form',
    locale: LOCALES.EN,
    secureUrl: 'https://secure-url.com',
  };

  it('renders the LoginForm component', () => {
    render(<LoginForm {...defaultProps} />);
    expect(screen.getByTestId('login-form-Form')).toBeInTheDocument();
    expect(screen.getByTestId('email-Form-Input')).toBeInTheDocument();
    expect(screen.getByTestId('password-Form-Input')).toBeInTheDocument();
    expect(screen.getByTestId('login-form-Button')).toBeInTheDocument();
  });

  it('displays an error alert when login fails', () => {
    (useInnBusinessLogin as jest.Mock).mockReturnValue({
      ...mockUseInnBusinessLogin,
      isError: true,
    });

    render(<LoginForm {...defaultProps} />);
    expect(screen.getByTestId('login-form-error-alert')).toBeInTheDocument();
    expect(screen.getByText('auth.signin.submit.error.label')).toBeInTheDocument();
  });

  it('disables the login button while submitting', () => {
    (useInnBusinessLogin as jest.Mock).mockReturnValue({
      ...mockUseInnBusinessLogin,
      isSubmitting: true,
    });

    render(<LoginForm {...defaultProps} />);
    const loginButton = screen.getByTestId('login-form-Button');
    expect(loginButton).toBeDisabled();
  });

  it('validates email and password fields', async () => {
    render(<LoginForm {...defaultProps} />);
    const submitButton = screen.getByTestId('login-form-Button');

    // Try to submit with empty fields
    fireEvent.click(submitButton);

    // Form should not call handleLogin with empty fields (validation should prevent it)
    expect(mockHandleLogin).not.toHaveBeenCalled();
  });

  it('renders a link to create an account', () => {
    render(<LoginForm {...defaultProps} />);
    const createAccountLink = screen.getByTestId('login-form-CreateAccount');
    expect(createAccountLink).toHaveAttribute('href', '/en-gb/account/register');
    expect(createAccountLink).toHaveTextContent('auth.signin.create.link');
  });

  it('renders a forgotten password link', () => {
    render(<LoginForm {...defaultProps} />);
    const forgotPasswordLink = screen.getByTestId('login-form-ForgotPassword');
    expect(forgotPasswordLink).toBeInTheDocument();
    expect(forgotPasswordLink).toHaveTextContent('auth.signin.forgotten.link');
  });

  it('renders the iframe with correct src and data-testid', () => {
    render(<LoginForm {...defaultProps} />);
    const iframe = screen.getByTestId('login-form-Iframe');
    expect(iframe).toBeInTheDocument();
    expect(iframe).toHaveAttribute(
      'src',
      'https://secure-url.com/gb/en/business-booker/common/login.html'
    );
  });

  it('renders LoginFormSkeleton while page is loading', () => {
    jest.spyOn(React, 'useEffect').mockImplementation(() => undefined); // prevents setIsPageLoaded(true)

    render(<LoginForm {...defaultProps} />);

    expect(screen.getByTestId('login-form-Skeleton')).toBeInTheDocument();
  });
});
