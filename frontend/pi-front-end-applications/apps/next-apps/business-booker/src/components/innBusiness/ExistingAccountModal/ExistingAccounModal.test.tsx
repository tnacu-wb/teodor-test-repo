import '@testing-library/jest-dom';
import { render, screen, fireEvent } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import React from 'react';

import ExistingAccountModal from './ExistingAccountModal';

const mockGetCountryLanguageByLocale = jest.fn();
const mockUseFeatureToggle = jest.fn();

jest.mock('@whitbread-eos/utils/server', () => ({
  // Server utils (if any needed)
}));

jest.mock('@whitbread-eos/utils', () => ({
  useFeatureToggle: () => mockUseFeatureToggle(),
  cn: (...args: string[]) => args.filter(Boolean).join(' '),
  findError: () => undefined,
  useTranslation: () => ({
    t: (key: string) => {
      const translations: Record<string, string> = {
        'notifications.notification.account.exists.title': 'Existing account detected',
        'notifications.notification.account.exists.description':
          'Your email address is already associated as an existing account holder. Do you want to link an account instead?',
        'notifications.notification.account.exists.link.linkAccount': 'Link an existing account',
        'notifications.notification.account.exists.link.newApp': 'Start a new application',
      };
      return translations[key] || key;
    },
  }),
  getPathForLocale: jest.fn((locale: string, path: string) => `/${locale}${path}`),
  getCountryLanguageByLocale: () => mockGetCountryLanguageByLocale(),
}));

describe('ExistingAccountModal', () => {
  const mockOnClose = jest.fn();

  const defaultProps = {
    isModalOpen: true,
    locale: LOCALES.EN,
    onClose: mockOnClose,
  };

  beforeEach(() => {
    jest.clearAllMocks();

    mockGetCountryLanguageByLocale.mockReturnValue({ language: 'en', country: 'GB' });
    mockUseFeatureToggle.mockReturnValue({ FT_IB_PAY_PIBA_EURO: false });
  });

  it('should render the Existing Account modal when open', () => {
    render(<ExistingAccountModal {...defaultProps} />);
    expect(screen.getByText('Existing account detected')).toBeInTheDocument();
    expect(
      screen.getByText(
        'Your email address is already associated as an existing account holder. Do you want to link an account instead?'
      )
    ).toBeInTheDocument();
    expect(screen.getByText('Link an existing account')).toBeInTheDocument();
    expect(screen.getByText('Start a new application')).toBeInTheDocument();
  });

  it('should not render the Existing Account modal when closed', () => {
    render(<ExistingAccountModal {...defaultProps} isModalOpen={false} />);
    expect(screen.queryByText('Existing account detected')).not.toBeInTheDocument();
  });

  it('should call onClose when the cancel button is clicked', () => {
    render(<ExistingAccountModal {...defaultProps} />);
    fireEvent.click(screen.getByTestId('Dialog-X-Close-Button'));
    expect(mockOnClose).toHaveBeenCalledTimes(1);
  });

  it('should show both buttons for non-German language', () => {
    render(<ExistingAccountModal {...defaultProps} />);
    expect(screen.getByText('Link an existing account')).toBeInTheDocument();
    expect(screen.getByText('Start a new application')).toBeInTheDocument();
  });

  it('should show both buttons for German language when PIBA Euro is enabled', () => {
    mockGetCountryLanguageByLocale.mockReturnValue({ language: 'DE', country: 'DE' });
    mockUseFeatureToggle.mockReturnValue({ FT_IB_PAY_PIBA_EURO: true });

    render(<ExistingAccountModal {...defaultProps} />);

    expect(screen.getByText('Link an existing account')).toBeInTheDocument();
    expect(screen.getByText('Start a new application')).toBeInTheDocument();
  });
});
