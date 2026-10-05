import '@testing-library/jest-dom';
import { render, screen, within } from '@testing-library/react';
import { Currency, LOCALES } from '@whitbread-eos/api';
import { getTranslations } from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';

import { Company } from './company';

const mockProps = {
  locale: LOCALES.EN,
  token: '123',
  icons: { icon: 'test' },
};

const mockToken = {
  email: 'test@test.com',
  companyId: 'test',
  business: {
    tethered: false,
  },
};

const mockGetPayApplicationLabels = jest.fn();
const mockCookieData = {
  value: mockToken,
};

const mockReponse = {
  data: {
    getCompanySpending: {
      companySpendingDtoList: [
        {
          year: 2024,
          month: '9',
          bookingValue: 3000,
          bookingCurrency: Currency.GBP_NAME,
        },
      ],
    },
  },
};

const mockGetCompanySpending = jest.fn().mockReturnValue(mockReponse);

const mockCookieStore = {
  get: () => mockCookieData,
} as unknown as ReturnType<typeof cookies>;

jest.mock('next/headers', () => ({
  cookies: () => mockCookieStore,
  headers: () => ({
    get: () => 'test',
  }),
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  cn: jest.fn(),
  getCountryLanguageByLocale: jest.fn().mockReturnValue({ language: 'en' }),
  getTranslations: jest.fn(),
  useTranslation: () => {
    return {
      t: (str: string) => str,
    };
  },
  getPathForLocale: () => '/',
  formatIBAssetsUrl: () => {
    return '/';
  },
  getUserDetails: () => {
    return {
      companyId: 'test',
      business: {
        accessLevel: 'SUPER',
      },
    };
  },
  getCompanySpending: () => mockGetCompanySpending(),
  getPayApplicationLabels: (...args: unknown[]) => mockGetPayApplicationLabels(...args),
  TranslationProvider: ({ children }: { children: React.ReactNode }) => <>{children}</>,
}));

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

jest.mock('./components/CardList/card-list', () => ({
  __esModule: true,
  default: () => null,
}));

jest.mock('./company-spending-client', () => {
  return {
    CompanySpendingClient: () => <div data-testid="CompanySpendingClient" />,
  };
});

jest.mock('../Analytics/analytics', () => {
  const CompanySpendingAnalyticsMock = () => <div data-testid="CompanySpendingAnalyticsMock" />;
  CompanySpendingAnalyticsMock.displayName = 'CompanySpendingAnalyticsMock';
  return CompanySpendingAnalyticsMock;
});

describe('SpendingTabs', () => {
  beforeEach(() => {
    (getTranslations as jest.Mock).mockResolvedValue({
      t: (key: string) => key,
    });
    mockGetPayApplicationLabels.mockResolvedValue({
      'application.sent.failed.message': 'Something went wrong',
      'application.sent.tryAgain': 'Try again',
    });
  });

  it('should render the Company component', async () => {
    render(await Company({ ...mockProps, locale: LOCALES.EN }));
    const container = await screen.findByTestId('CompanyTab-container');
    expect(container).toBeInTheDocument();
  });

  it('should render the Company component with no data', async () => {
    mockGetCompanySpending.mockReturnValue({
      data: {
        getCompanySpending: {
          companySpendingDtoList: [],
        },
      },
    });
    render(await Company({ ...mockProps, locale: LOCALES.DE }));
    const container = await screen.findByTestId('CompanyTab-container');
    expect(container).toBeInTheDocument();
  });

  it('renders inline error when WL call fails', async () => {
    mockGetCompanySpending.mockReturnValueOnce(null);

    render(await Company({ ...mockProps, locale: LOCALES.EN }));

    const spentThisMonthError = await screen.findByTestId('CompanyTab-Spent-This-Month-Error');
    const spendOverTimeError = await screen.findByTestId('CompanyTab-Spend-Over-Time-Error');

    expect(
      within(spentThisMonthError).getByText('application.sent.failed.message')
    ).toBeInTheDocument();
    expect(
      within(spendOverTimeError).getByText('application.sent.failed.message')
    ).toBeInTheDocument();
    expect(
      within(spentThisMonthError).getByRole('button', { name: 'application.sent.tryAgain' })
    ).toBeInTheDocument();
    expect(
      within(spendOverTimeError).getByRole('button', { name: 'application.sent.tryAgain' })
    ).toBeInTheDocument();
  });
});
