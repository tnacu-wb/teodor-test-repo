import '@testing-library/jest-dom';
import { render, screen, waitFor } from '@testing-library/react';
import { Currency, LOCALES } from '@whitbread-eos/api';
import { getUserDetails, getCompanyDetails, getCompanySpending } from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';
import React from 'react';

import { Welcome, WelcomeSkeleton } from './welcome';

jest.mock('@whitbread-eos/utils/server', () => ({
  getUserDetails: jest.fn(),
  getCompanyDetails: jest.fn(),
  getCompanySpending: jest.fn(),
  getPathForLocale: jest.fn((locale, path) => `/${locale}/${path}`),
  ID_TOKEN_COOKIE: 'id_token',
  cn: jest.fn((...classes) => classes.filter(Boolean).join(' ')),
}));

const mockGetUserDetails = getUserDetails as jest.MockedFunction<typeof getUserDetails>;
const mockGetCompanyDetails = getCompanyDetails as jest.MockedFunction<typeof getCompanyDetails>;
const mockGetCompanySpending = getCompanySpending as jest.MockedFunction<typeof getCompanySpending>;

const mockT = (key: string) => key;

jest.mock('next/headers', () => ({
  cookies: jest.fn(() => ({
    get: jest.fn(() => ({ value: 'mock-token' })),
  })),
}));

const mockCookies = cookies as jest.Mock;

describe('Welcome', () => {
  const mockUserDetails = {
    companyId: 'company-1',
    contactDetail: { firstName: 'John' },
    business: { accessLevel: 'SUPER' },
  };
  const mockCompanyDetails = {
    requestedCompany: {
      companyDetails: { companyName: 'Test Company' },
    },
  };
  const mockSpendingResponse = {
    data: {
      getCompanySpending: {
        companySpendingDtoList: [{ bookingValue: 1234.56, bookingCurrency: 'EUR' }],
      },
    },
  };

  beforeEach(() => {
    jest.clearAllMocks();
    mockCookies.mockReturnValue({
      get: jest.fn(() => ({ value: 'mock-token' })),
    });
  });

  it('renders WelcomeSkeleton', () => {
    render(<WelcomeSkeleton />);
    expect(document.querySelectorAll('.animate-pulse')).toHaveLength(2);
  });

  it('renders welcome message and spending for travel manager (EN, EUR)', async () => {
    mockGetUserDetails.mockResolvedValue(mockUserDetails);
    mockGetCompanyDetails.mockResolvedValue(mockCompanyDetails);
    mockGetCompanySpending.mockResolvedValue(mockSpendingResponse);

    render(await Welcome({ parentDataTestId: 'test', locale: LOCALES.EN, t: mockT }));

    await waitFor(() => {
      expect(screen.getByTestId('test-Welcome-Title')).toHaveTextContent(
        'homepage.home.welcome John'
      );
      expect(screen.getByTestId('test-Welcome-SubTitle')).toHaveTextContent('Test Company');
      expect(screen.getByTestId('test-Welcome-SpendingLink')).toBeInTheDocument();
      expect(screen.getByTestId('test-Welcome-SpendingLink')).toHaveTextContent(
        'homepage.home.spent 1,234.56 €'
      );
    });
  });

  it('renders welcome message and spending for travel manager (DE, GBP)', async () => {
    mockGetUserDetails.mockResolvedValue({
      ...mockUserDetails,
      business: { accessLevel: 'SUPER' },
    });
    mockGetCompanyDetails.mockResolvedValue(mockCompanyDetails);
    mockGetCompanySpending.mockResolvedValue({
      data: {
        getCompanySpending: {
          companySpendingDtoList: [{ bookingValue: 1000, bookingCurrency: 'GBP' }],
        },
      },
    });

    render(await Welcome({ parentDataTestId: 'test', locale: LOCALES.DE, t: mockT }));

    await waitFor(() => {
      expect(screen.getByTestId('test-Welcome-Title')).toHaveTextContent(
        'homepage.home.welcome John'
      );
      expect(screen.getByTestId('test-Welcome-SubTitle')).toHaveTextContent('Test Company');
      expect(screen.getByTestId('test-Welcome-SpendingLinkDe')).toBeInTheDocument();
      expect(screen.getByTestId('test-Welcome-SpendingLinkDe')).toHaveTextContent('1.000,00');
    });
  });

  it('renders fallback subtitle for non-travel manager', async () => {
    mockGetUserDetails.mockResolvedValue({
      ...mockUserDetails,
      business: { accessLevel: 'USER' },
    });
    mockGetCompanyDetails.mockResolvedValue(mockCompanyDetails);

    render(await Welcome({ parentDataTestId: 'test', locale: LOCALES.EN, t: mockT }));

    await waitFor(() => {
      expect(screen.getByTestId('test-Welcome-SubTitle')).toHaveTextContent(
        'homepage.home.welcome.your Test Company homepage.home.welcome.account'
      );
    });
  });

  it('renders euro spending snippet first for DE locale when currency is EUR', async () => {
    mockGetUserDetails.mockResolvedValue(mockUserDetails);
    mockGetCompanyDetails.mockResolvedValue(mockCompanyDetails);
    mockGetCompanySpending.mockResolvedValue({
      data: {
        getCompanySpending: {
          companySpendingDtoList: [{ bookingValue: 987.65, bookingCurrency: Currency.EUR_NAME }],
        },
      },
    });

    render(await Welcome({ parentDataTestId: 'test', locale: LOCALES.DE, t: mockT }));

    await waitFor(() => {
      expect(screen.getByTestId('test-Welcome-SpendingLinkDe')).toHaveTextContent(
        '987,65 € homepage.home.spent'
      );
    });
  });

  it('falls back to welcome copy when spending request fails', async () => {
    mockGetUserDetails.mockResolvedValue(mockUserDetails);
    mockGetCompanyDetails.mockResolvedValue(mockCompanyDetails);
    mockGetCompanySpending.mockRejectedValue(new Error('network error'));

    render(await Welcome({ parentDataTestId: 'test', locale: LOCALES.EN, t: mockT }));

    await waitFor(() => {
      expect(screen.getByTestId('test-Welcome-SubTitle')).toHaveTextContent(
        'homepage.home.welcome.your Test Company homepage.home.welcome.account'
      );
    });
  });
});
