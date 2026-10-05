import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { LOCALES, type CurrentBalanceItem, type CustomerAccountDetails } from '@whitbread-eos/api';

import { SpendingLegend, SpendingLegendSkeleton } from './spending-legend';

type MockUpcomingSpendingProps = {
  baseDataTestId: string;
  locale: LOCALES;
  account: CustomerAccountDetails;
  reachesCreditLimit: boolean;
  wlReturnUrl: string;
};

type MockCreditLimitExceedProps = {
  baseDataTestId: string;
  locale: LOCALES;
};

type SpendingLegendOverrides = Partial<{
  currentBalance: Partial<CurrentBalanceItem>;
  account: CustomerAccountDetails | null;
  isAccountSuspended: boolean;
  wlReturnUrl: string;
}>;

type SpendingLegendProps = {
  locale: LOCALES;
  currentBalance: CurrentBalanceItem;
  baseDataTestId: string;
  account: CustomerAccountDetails | null;
  isAccountSuspended: boolean;
  wlReturnUrl: string;
};

const mockUseTranslationServer = jest.fn<{ t: (key: string) => string }, []>(() => {
  return {
    t: (str: string) => str,
  };
});

const mockUpcomingSpending = jest.fn((props: MockUpcomingSpendingProps) => {
  const { baseDataTestId } = props;
  return <div data-testid={`${baseDataTestId}-UpcomingMock`} />;
});

const mockCreditLimitExceed = jest.fn((props: MockCreditLimitExceedProps) => {
  const { baseDataTestId } = props;
  return <span data-testid={`${baseDataTestId}-CreditLimitExceedMock`}>Credit Limit Exceed</span>;
});

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => '/',
    getCommonIcons: () => [],
    getTranslations: (...args: Parameters<typeof mockUseTranslationServer>) =>
      mockUseTranslationServer(...args),
  };
});

jest.mock(
  '~components/innBusiness/TextWithInfoTooltip',
  () => {
    return { TextWithInfoTooltip: () => null };
  },
  { virtual: true }
);

jest.mock('./upcoming-spending.tsx', () => {
  return {
    UpcomingSpending: (props: MockUpcomingSpendingProps) => mockUpcomingSpending(props),
    UpcomingSpendingSkeleton: () => <div data-testid="UpcomingSkeleton" />,
  };
});

jest.mock('./credit-limit-exceed.tsx', () => {
  return {
    CreditLimitExceed: (props: MockCreditLimitExceedProps) => mockCreditLimitExceed(props),
  };
});

const baseCurrentBalance = {
  schemeCustomerId: 783978,
  tetheredGuid: '73379a1f-7b4c-4d81-b059-c85f8db8dded',
  accountName: 'whibtread Digital',
  accountNumber: '3089503200100352',
  registrationRoles: ['ACCOUNT_HOLDER', 'CARD_HOLDER'],
  scheme: 'GB',
  outstanding: {
    amount: -2592.21,
    currencyCode: 'GBP',
    currencySymbol: '£',
  },
  newTransactions: {
    amount: -1408.74,
    currencyCode: 'GBP',
    currencySymbol: '£',
  },
  available: {
    amount: 15099.05,
    currencyCode: 'GBP',
    currencySymbol: '£',
  },
  creditLimit: {
    amount: 17000,
    currencyCode: 'GBP',
    currencySymbol: '£',
  },
  currentBalance: {
    amount: 1900.95,
    currencyCode: 'GBP',
    currencySymbol: '£',
  },
  interimPayments: {
    amount: 0,
    currencyCode: 'GBP',
    currencySymbol: '£',
  },
} as CurrentBalanceItem;

const createProps = (overrides: SpendingLegendOverrides = {}): SpendingLegendProps => {
  const clonedCurrentBalance = JSON.parse(JSON.stringify(baseCurrentBalance)) as CurrentBalanceItem;
  const overrideBalance: Partial<CurrentBalanceItem> = overrides.currentBalance ?? {};

  const defaultAccount = {
    tetheredGuid: '73379a1f-7b4c-4d81-b059-c85f8db8dded',
    accountName: 'whibtread Digital',
    accountNumber: '3089503200100352',
    registrationRoles: ['ACCOUNT_HOLDER', 'CARD_HOLDER'],
    scheme: 'GB',
  } as CustomerAccountDetails;

  const account: CustomerAccountDetails | null = Object.prototype.hasOwnProperty.call(
    overrides,
    'account'
  )
    ? (overrides.account ?? null)
    : defaultAccount;

  const currentBalance: CurrentBalanceItem = {
    ...clonedCurrentBalance,
    ...overrideBalance,
    outstanding: {
      ...clonedCurrentBalance.outstanding,
      ...(overrideBalance.outstanding ?? {}),
    },
    newTransactions: {
      ...clonedCurrentBalance.newTransactions,
      ...(overrideBalance.newTransactions ?? {}),
    },
    available: {
      ...clonedCurrentBalance.available,
      ...(overrideBalance.available ?? {}),
    },
    creditLimit: {
      ...clonedCurrentBalance.creditLimit,
      ...(overrideBalance.creditLimit ?? {}),
    },
  } as CurrentBalanceItem;

  return {
    locale: LOCALES.EN,
    currentBalance,
    baseDataTestId: 'BaseId',
    account,
    isAccountSuspended:
      overrides.isAccountSuspended === undefined ? false : overrides.isAccountSuspended,
    wlReturnUrl: overrides.wlReturnUrl ?? '',
  };
};

describe('SpendingLegend', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders legend structure', async () => {
    const props = createProps();
    render(await SpendingLegend(props));

    expect(screen.getByTestId(`${props.baseDataTestId}-Legend`)).toBeInTheDocument();
    expect(screen.getByTestId(`${props.baseDataTestId}-SpentAmount`)).toHaveTextContent(
      '-£2,592.21'
    );
    expect(screen.getByTestId(`${props.baseDataTestId}-NewTransactionsAmount`)).toHaveTextContent(
      '-£1,408.74'
    );
    expect(screen.getAllByTestId(`${props.baseDataTestId}-RemainingAmount`)[0]).toHaveTextContent(
      '£15,099.05'
    );
    expect(screen.getByTestId(`${props.baseDataTestId}-RemainingAmountMobile`)).toHaveTextContent(
      '£15,099.05'
    );
  });

  it('renders upcoming spending when account active', async () => {
    const props = createProps();
    render(await SpendingLegend(props));

    expect(mockUpcomingSpending).toHaveBeenCalledWith(
      expect.objectContaining({
        baseDataTestId: props.baseDataTestId,
        reachesCreditLimit: false,
        account: props.account,
      })
    );
    expect(screen.getByTestId(`${props.baseDataTestId}-UpcomingMock`)).toBeInTheDocument();
  });

  it('passes credit limit flag when remaining tight', async () => {
    const props = createProps({
      currentBalance: {
        available: { amount: 5, currencySymbol: '£', currencyCode: 'GBP' },
        creditLimit: { amount: 50, currencySymbol: '£', currencyCode: 'GBP' },
      },
    });

    render(await SpendingLegend(props));

    expect(mockUpcomingSpending).toHaveBeenCalledWith(
      expect.objectContaining({
        reachesCreditLimit: true,
      })
    );
  });

  it('renders credit limit messaging when suspended', async () => {
    const props = createProps({ isAccountSuspended: true });
    render(await SpendingLegend(props));

    expect(screen.getByTestId(`${props.baseDataTestId}-RemainingAmount`)).toHaveTextContent(
      'homepage.home.innbusinessPay.notAvailable'
    );
    expect(mockUpcomingSpending).not.toHaveBeenCalled();
    expect(mockCreditLimitExceed).toHaveBeenCalledWith(
      expect.objectContaining({ baseDataTestId: props.baseDataTestId })
    );
    expect(screen.getByTestId(`${props.baseDataTestId}-CreditLimitExceedMock`)).toBeInTheDocument();
  });

  it('omits async sections when account missing', async () => {
    const props = createProps({ account: null });
    render(await SpendingLegend(props));

    expect(mockUpcomingSpending).not.toHaveBeenCalled();
    expect(mockCreditLimitExceed).not.toHaveBeenCalled();
  });
});

describe('SpendingLegendSkeleton', () => {
  it('renders placeholders', () => {
    const tMock = jest.fn();
    const { container } = render(<SpendingLegendSkeleton t={tMock} />);

    expect(container.firstChild).toBeInTheDocument();
    expect(tMock).toHaveBeenCalledWith('homepage.home.innbusinessPay.spent');
    expect(tMock).toHaveBeenCalledWith('homepage.home.innbusinessPay.newTransactions');
    expect(tMock).toHaveBeenCalledWith('homepage.home.innbusinessPay.remainingSpend');
  });
});
