import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { Currency, LOCALES } from '@whitbread-eos/api';

import { YourSpending } from './your-spending';

const mockProps = {
  locale: LOCALES.EN,
  token: '123',
  icons: { icon: 'test' },
};

const mockReponse = {
  data: {
    getEmployeeSpend: {
      employeeSpendDtoList: [
        {
          year: 2026,
          month: '4',
          bookingValue: 3000,
          bookingCurrency: Currency.GBP_NAME,
        },
      ],
    },
  },
};

const mockGetYourSpending = jest.fn().mockReturnValue(mockReponse);

const tMock = (key: string) => {
  const translations: Record<string, string> = {
    'spending.reporting.graph.title': 'Spend over time',
  };
  return translations[key] || key;
};

jest.mock('next/headers', () => ({
  headers: () => ({
    get: () => 'test',
  }),
}));

jest.mock('@whitbread-eos/utils', () => ({
  cn: jest.fn(),
  useTranslation: () => ({
    t: tMock,
  }),
  useOutsideClick: jest.fn(() => ({
    isOpen: false,
    elementRef: { current: null },
    iconRef: { current: null },
  })),
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  cn: jest.fn(),
  getCountryLanguageByLocale: jest.fn().mockReturnValue({ language: 'en' }),
  getTranslations: jest.fn(),
  useTranslation: () => ({
    t: tMock,
  }),
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
  getYourSpending: () => mockGetYourSpending(),
}));

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

jest.mock('~components/innBusiness/SomethingWentWrong', () => ({
  SomethingWentWrong: ({ testId }: { testId?: string }) => (
    <div data-testid={testId || 'SomethingWentWrong'}>Something went wrong</div>
  ),
}));

jest.mock('./your-spending-client', () => {
  return {
    YourSpendingClient: () => <div data-testid="YourSpendingClient">Spend over time</div>,
  };
});

jest.mock('../../components/Analytics/analytics', () => {
  const YourSpendingAnalyticsMock = () => <div data-testid="YourSpendingAnalyticsMock" />;
  YourSpendingAnalyticsMock.displayName = 'YourSpendingAnalyticsMock';
  return YourSpendingAnalyticsMock;
});

describe('YourSpending', () => {
  it('should render the YourSpending component', async () => {
    mockProps.locale = LOCALES.DE;
    mockGetYourSpending.mockReturnValue(mockReponse);
    render(await YourSpending({ ...mockProps }));
    const container = await screen.findByTestId('YourSpendingTab-container');
    expect(container).toBeInTheDocument();
    expect(screen.getByTestId('YourSpendingClient')).toBeInTheDocument();
    expect(screen.getByText('Spend over time')).toBeInTheDocument();
  });

  it('should render SomethingWentWrong components when yourSpending data is null', async () => {
    mockGetYourSpending.mockReturnValue(null);
    render(await YourSpending({ ...mockProps }));
    const container = await screen.findByTestId('YourSpendingTab-container');
    expect(container).toBeInTheDocument();
    expect(screen.getByTestId('YourSpendingTab-Spent-This-Month-Error')).toBeInTheDocument();
    expect(screen.getByTestId('YourSpendingTab-Spend-Over-Time-Error')).toBeInTheDocument();
    expect(screen.queryByTestId('YourSpendingClient')).not.toBeInTheDocument();
  });

  it('should render SomethingWentWrong components when yourSpending has errors', async () => {
    mockGetYourSpending.mockReturnValue({
      errors: [{ message: 'Something went wrong' }],
      data: null,
    });
    render(await YourSpending({ ...mockProps }));
    const container = await screen.findByTestId('YourSpendingTab-container');
    expect(container).toBeInTheDocument();
    expect(screen.getByTestId('YourSpendingTab-Spent-This-Month-Error')).toBeInTheDocument();
    expect(screen.getByTestId('YourSpendingTab-Spend-Over-Time-Error')).toBeInTheDocument();
    expect(screen.queryByTestId('YourSpendingClient')).not.toBeInTheDocument();
  });
});
