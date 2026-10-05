import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { SpendingBalance, SpendingBalanceSkeleton } from './spending-balance';

const mockProps = {
  locale: LOCALES.EN,
  isAccountSuspended: false,
  currentBalance: {
    schemeCustomerId: 123,
    tetheredGuid: 'test-123',
    accountName: 'whibtread Digital',
    accountNumber: '123456789',
    registrationRoles: ['ACCOUNT_HOLDER', 'CARD_HOLDER'],
    scheme: 'GB',
    outstanding: { currencySymbol: '£', currencyCode: 'GBP', amount: -101 },
    newTransactions: { currencySymbol: '£', currencyCode: 'GBP', amount: 0 },
    available: { currencySymbol: '£', currencyCode: 'GBP', amount: 101 },
    creditLimit: { currencySymbol: '£', currencyCode: 'GBP' },
    currentBalance: { currencySymbol: '£', currencyCode: 'GBP' },
    interimPayments: { currencySymbol: '£', currencyCode: 'GBP' },
  } as any,
  baseDataTestId: 'test123',
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getPathForLocale: () => {
      return '/';
    },
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    getTranslations: () => {
      return {
        t: (str: string) => str,
      };
    },
    getCommonIcons: () => ({}),
  };
});

jest.mock('~components/innBusiness/TextWithInfoTooltip', () => {
  return {
    TextWithInfoTooltip: ({ children }: { children: React.ReactNode }) => (
      <div data-testid="test123-InfoTooltip">{children}</div>
    ),
  };
});

describe('SpendingBalance', () => {
  beforeEach(() => {
    jest.resetAllMocks();
  });

  it('should render the SpendingBalance component', async () => {
    const { getAllByTestId } = render(await SpendingBalance({ ...mockProps }));

    expect(getAllByTestId('test123-InfoTooltip')[0]).toBeInTheDocument();
  });

  it('should render the SpendingBalance component with amount', async () => {
    mockProps.currentBalance.outstanding.amount = 100;
    mockProps.currentBalance.currentBalance.amount = -100;
    mockProps.currentBalance.interimPayments.amount = 100;
    const { getAllByTestId } = render(await SpendingBalance({ ...mockProps }));

    expect(getAllByTestId('test123-InfoTooltip')[0]).toBeInTheDocument();
  });

  it('should render the SpendingBalance component with account suspended', async () => {
    mockProps.isAccountSuspended = true;
    const { getAllByTestId } = render(await SpendingBalance({ ...mockProps }));

    expect(getAllByTestId('test123-InfoTooltip')[0]).toBeInTheDocument();
  });
});

describe('SpendingBalanceSkeleton Component', () => {
  const mockSkeletonProps = {
    t: jest.fn(),
  };

  it('should render skeleton component', () => {
    const { container } = render(<SpendingBalanceSkeleton {...mockSkeletonProps} />);
    expect(container.firstChild).toBeInTheDocument();
  });
});
