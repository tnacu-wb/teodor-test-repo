import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { getTranslations, getAccountRegistrationRoleDetails } from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';

import { InnBusinessPay } from './inn-business-pay';

const mockGetAccountRegistrationRoles = jest.fn();
const mockProps = {
  token: '123',
  locale: LOCALES.EN,
  searchParamAccount: '123',
  language: 'en' as any,
  country: 'gb',
  isTravelManager: true,
};

let mockToken = {
  email: 'test@test.com',
  companyId: 'test',
  business: {
    tethered: false,
  },
};

const mockCookieData = {
  value: mockToken,
};

const mockCookieStore = {
  get: () => mockCookieData,
} as unknown as ReturnType<typeof cookies>;

const mockGetAccountList = jest.fn().mockReturnValue([
  {
    accountName: 'test',
    accountNumber: '1234',
    tetheredGuid: '6e4e8cc1-4e74-4ba6-97fb-1c75e88aba64',
    registrationRoles: ['ACCOUNT_HOLDER', 'CARD_HOLDER'],
    errorCode: null,
    scheme: 'GB',
  },
]);

const mockReponse = {
  data: {
    getAccountSpending: {
      accountSpendingDtoList: [
        {
          year: 2024,
          month: '9',
          bookingValue: 3000,
        },
      ],
    },
  },
};

const mockGetAccountSpending = jest.fn().mockReturnValue(mockReponse);
const mockGetPayApplicationLabels = jest.fn();
jest.mock('next/navigation', () => ({
  useRouter: jest.fn().mockReturnValue({
    pathname: '/test-path',
    query: {},
    push: jest.fn(),
    replace: jest.fn(),
    prefetch: jest.fn().mockResolvedValue(undefined),
    asPath: '/test-path',
    route: '/test-path',
  }),
  useParams: jest.fn().mockReturnValue({ locale: LOCALES.EN }),
}));
jest.mock('next/headers', () => ({
  cookies: () => mockCookieStore,
  headers: () => ({
    get: () => 'test',
  }),
}));

jest.mock('../../../homepage/components/SpendingSummary', () => ({
  SpendingSummary: jest.fn(() => <div data-testid="SpendingSummary">SpendingSummary</div>),
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  cn: jest.fn(),
  appPreCheck: jest.fn(),
  getAccountRegistrationRoleDetails: jest.fn(),
  getCountryLanguageByLocale: jest.fn().mockReturnValue({ language: 'en', country: 'GB' }),
  getLocaleByPathname: () => {
    return LOCALES.EN;
  },
  getPathForLocale: () => '/',
  getTranslations: jest.fn(),
  getAccountList: () => mockGetAccountList(),
  getUserDetails: jest.fn().mockResolvedValue({
    business: {
      accessLevel: 'test',
    },
  }),
  getWorldlineReturnUrl: (str: string) => str,
  getAccountInfo: jest.fn(() => {
    return {
      status: 'Current',
    };
  }),
  useTranslation: () => {
    return {
      t: (str: string) => str,
    };
  },
  getAccountSpending: () => mockGetAccountSpending(),
  getCommonIcons: () => [],
  formatIBAssetsUrl: () => {
    return '/';
  },
  getUpcomingSpending: () => {
    return {
      data: {
        getAccountUpcomingSpending: {},
      },
    };
  },
  getPayApplications: jest.fn().mockResolvedValue({
    data: {
      getPayApplications: {
        applications: [],
      },
    },
  }),
  getPayApplicationLabels: (...args: unknown[]) => mockGetPayApplicationLabels(...args),
  TranslationProvider: ({ children }: { children: React.ReactNode }) => <>{children}</>,
}));

jest.mock('~components/innBusiness/AccountHolder/account-holder', () => ({
  AccountHolder: () => null,
}));

jest.mock('../../../homepage/components/AccountSelector/account-selector', () => ({
  AccountSelector: () => null,
}));

jest.mock('./components/CardList/card-list', () => ({
  __esModule: true,
  default: () => null,
}));

jest.mock('~components/innBusiness/SuspendedNotification', () => ({
  __esModule: true,
  default: () => <div data-testid="Notifications-AccountSuspended">SuspendedNotification</div>,
}));

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

describe('SpendingTabs', () => {
  beforeEach(() => {
    (getTranslations as jest.Mock).mockResolvedValue({
      t: (key: string) => key,
    });
    getAccountRegistrationRoleDetails.mockImplementation(mockGetAccountRegistrationRoles);
    mockGetPayApplicationLabels.mockResolvedValue({
      'application.sent.failed.message': 'Something went wrong',
      'application.sent.tryAgain': 'Try again',
    });
  });

  it('renders InnBusinessPay component', async () => {
    getAccountRegistrationRoleDetails.mockImplementation(() => {
      return {
        isOnlyCardHolder: false,
        isOnlyFinanceUser: false,
        isCardHolderAndFinanceUser: false,
        isOnlyCostCenter: false,
        isCardHolderAndCostCenterUser: false,
      };
    });
    const { queryByTestId } = render(await InnBusinessPay({ ...mockProps }));
    expect(queryByTestId('InnBusinessPayTab-container')).toBeInTheDocument();
    expect(queryByTestId('InnBusinessPayTab-Spend-Over-Time')).toBeInTheDocument();
  });

  it('renders spend over time inline error when spending call fails', async () => {
    getAccountRegistrationRoleDetails.mockImplementation(() => {
      return {
        isOnlyCardHolder: false,
        isOnlyFinanceUser: false,
        isCardHolderAndFinanceUser: false,
        isOnlyCostCenter: false,
        isCardHolderAndCostCenterUser: false,
      };
    });
    mockGetAccountSpending.mockImplementationOnce(() => null);

    render(await InnBusinessPay({ ...mockProps }));

    expect(await screen.findByTestId('InnBusinessPayTab-Spend-Over-Time')).toBeInTheDocument();
    expect(
      await screen.findByTestId('InnBusinessPayTab-Spend-Over-Time-Error')
    ).toBeInTheDocument();
  });

  it('renders InnBusinessPay component with DE scheme', async () => {
    getAccountRegistrationRoleDetails.mockImplementation(() => {
      return {
        isOnlyCardHolder: true,
        isOnlyFinanceUser: false,
        isCardHolderAndFinanceUser: false,
        isOnlyCostCenter: false,
        isCardHolderAndCostCenterUser: false,
      };
    });
    mockGetAccountList.mockReturnValue([
      {
        accountName: 'test',
        accountNumber: '1234',
        tetheredGuid: '6e4e8cc1-4e74-4ba6-97fb-1c75e88aba64',
        registrationRoles: ['ACCOUNT_HOLDER', 'CARD_HOLDER'],
        errorCode: null,
        scheme: 'DE',
      },
    ]);
    render(await InnBusinessPay({ ...mockProps }));
    expect(await screen.findByTestId('InnBusinessPayTab-container')).toBeInTheDocument();
  });

  it('renders InnBusinessPay component for cost center user', async () => {
    mockGetAccountList.mockReturnValue([
      {
        accountName: 'test',
        accountNumber: '1234',
        tetheredGuid: '6e4e8cc1-4e74-4ba6-97fb-1c75e88aba64',
        registrationRoles: ['COST_CENTRE_USER'],
        errorCode: null,
        scheme: 'DE',
      },
    ]);
    getAccountRegistrationRoleDetails.mockImplementation(() => {
      return {
        isOnlyCardHolder: false,
        isOnlyFinanceUser: false,
        isCardHolderAndFinanceUser: false,
        isOnlyCostCenter: true,
        isCardHolderAndCostCenterUser: false,
      };
    });
    const { queryByTestId } = render(await InnBusinessPay({ ...mockProps }));
    expect(queryByTestId('InnBusinessPayTab-container')).toBeInTheDocument();
    expect(queryByTestId('InnBusinessPayTab-Spend-Over-Time')).not.toBeInTheDocument();
  });
  it('renders InnBusinessPay component with no account', async () => {
    getAccountRegistrationRoleDetails.mockImplementation(() => {
      return {
        isOnlyCardHolder: false,
        isOnlyFinanceUser: false,
        isCardHolderAndFinanceUser: false,
        isOnlyCostCenter: true,
        isCardHolderAndCostCenterUser: false,
      };
    });
    mockToken = {} as any;
    mockGetAccountList.mockReturnValue([]);
    mockGetAccountSpending.mockReturnValue([]);
    render(await InnBusinessPay({ ...mockProps }));
    expect(await screen.findByTestId('InnBusinessPayTab-container')).toBeInTheDocument();
  });
});
