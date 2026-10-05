import '@testing-library/jest-dom';
import { render, screen, waitFor } from '@testing-library/react';
import { LOCALES, Currency, Scheme, PayAccountStatus } from '@whitbread-eos/api';

import { SpendingSummary, SpendingSummarySkeleton } from './spending-summary';

const defaultAccount = {
  tetheredGuid: 'guid-123',
  accountName: 'Account',
  accountNumber: '123456789',
  registrationRoles: [],
  scheme: 'GB' as Scheme,
};

const baseBalance = {
  schemeCustomerId: 1,
  tetheredGuid: defaultAccount.tetheredGuid,
  accountName: 'Account',
  accountNumber: defaultAccount.accountNumber,
  registrationRoles: [],
  scheme: defaultAccount.scheme,
  outstanding: { amount: 100, currencyCode: Currency.GBP_CODE, currencySymbol: '£' },
  newTransactions: { amount: 200, currencyCode: Currency.GBP_CODE, currencySymbol: '£' },
  available: { amount: 300, currencyCode: Currency.GBP_CODE, currencySymbol: '£' },
  creditLimit: { amount: 1000, currencyCode: Currency.GBP_CODE, currencySymbol: '£' },
  currentBalance: { amount: 400, currencyCode: Currency.GBP_CODE, currencySymbol: '£' },
  interimPayments: { amount: 0, currencyCode: Currency.GBP_CODE, currencySymbol: '£' },
};

const mockGetSpendingSummary = jest.fn();
const mockGetAccountInfo = jest.fn();
const mockGetPayApplications = jest.fn();
const mockGetUserRoles = jest.fn();
const mockGetRegistrationDetails = jest.fn();
const mockGetDetailsFromToken = jest.fn();
const mockUseTranslationServer = jest.fn();
const mockGetCountryLanguage = jest.fn();
const mockGetPayApplicationLabels = jest.fn();

type MockWordlineButtonProps = {
  baseDataTestId: string;
  postUrl?: string;
} & Record<string, unknown>;

const mockWordlineButton = jest.fn(({ baseDataTestId }: MockWordlineButtonProps) => (
  <button data-testid={`${baseDataTestId}-Mock`} />
));

jest.mock('~components/innBusiness/RolesCheck', () => ({
  RolesCheck: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="RolesCheck">{children}</div>
  ),
}));

jest.mock('~components/innBusiness/NoAccountBanner', () => ({
  NoAccountBanner: () => <div data-testid="NoAccountBanner">No Account</div>,
}));

jest.mock('~components/innBusiness/WordlineButton', () => ({
  WordlineButton: (props: MockWordlineButtonProps) => mockWordlineButton(props),
}));

jest.mock('./spending-progress', () => ({
  SpendingProgress: () => <div data-testid="SpendingSummary-Progress" />,
  SpendingProgressSkeleton: () => <div data-testid="ProgressSkeleton" />,
}));

jest.mock('./spending-legend', () => ({
  SpendingLegend: () => <div data-testid="SpendingSummary-Legend" />,
  SpendingLegendSkeleton: () => <div data-testid="LegendSkeleton" />,
}));

jest.mock('./spending-balance', () => ({
  SpendingBalance: () => <div data-testid="SpendingSummary-Balance" />,
  SpendingBalanceSkeleton: () => <div data-testid="BalanceSkeleton" />,
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  getCountryLanguageByLocale: (...args: unknown[]) => mockGetCountryLanguage(...args),
  getTranslations: (...args: unknown[]) => mockUseTranslationServer(...args),
  getSpendingSummaryV2: (...args: unknown[]) => mockGetSpendingSummary(...args),
  getUserRolesForAccount: (...args: unknown[]) => mockGetUserRoles(...args),
  getAccountInfo: (...args: unknown[]) => mockGetAccountInfo(...args),
  getDetailsFromToken: (...args: unknown[]) => mockGetDetailsFromToken(...args),
  getPayApplications: (...args: unknown[]) => mockGetPayApplications(...args),
  getAccountRegistrationRoleDetails: (...args: unknown[]) => mockGetRegistrationDetails(...args),
  getPayApplicationLabels: (...args: unknown[]) => mockGetPayApplicationLabels(...args),
  TranslationProvider: ({ children }: { children: React.ReactNode }) => <>{children}</>,
  cn: (...classes: Array<string | null | undefined | false>) => classes.filter(Boolean).join(' '),
}));

const baseProps = {
  locale: LOCALES.EN,
  account: defaultAccount,
  token: 'token',
  wlReturnUrl: '/return',
};

const ORIGINAL_POST_URL = process.env.NEXT_PUBLIC_WORLDLINE_POST_URL;
const ORIGINAL_POST_URL_DE = process.env.NEXT_PUBLIC_WORLDLINE_POST_URL_DE;

beforeAll(() => {
  process.env.NEXT_PUBLIC_WORLDLINE_POST_URL = 'https://wl.example/gb';
  process.env.NEXT_PUBLIC_WORLDLINE_POST_URL_DE = 'https://wl.example/de';
});

afterAll(() => {
  process.env.NEXT_PUBLIC_WORLDLINE_POST_URL = ORIGINAL_POST_URL;
  process.env.NEXT_PUBLIC_WORLDLINE_POST_URL_DE = ORIGINAL_POST_URL_DE;
});

beforeEach(() => {
  jest.clearAllMocks();
  mockGetCountryLanguage.mockReturnValue({ language: 'en' });
  mockUseTranslationServer.mockResolvedValue({ t: (key: string) => key });
  mockGetPayApplicationLabels.mockResolvedValue({
    'application.sent.failed.message': 'Something went wrong',
    'application.sent.tryAgain': 'Try again',
  });
  mockGetSpendingSummary.mockResolvedValue({
    data: { getAccountBalanceSummaryV2: baseBalance },
  });
  mockGetUserRoles.mockResolvedValue({ wl: [], bb: [] });
  mockGetAccountInfo.mockResolvedValue({ status: PayAccountStatus.Active });
  mockGetPayApplications.mockResolvedValue({
    data: { getPayApplications: { applications: [] } },
  });
  mockGetRegistrationDetails.mockReturnValue({
    isOnlyCardHolder: false,
    isOnlyFinanceUser: false,
    isCardHolderAndFinanceUser: false,
    isOnlyCostCenter: false,
    isCardHolderAndCostCenterUser: false,
  });
  mockGetDetailsFromToken.mockReturnValue({ isTravelManager: true });
});

describe('SpendingSummary', () => {
  it('renders full summary when balance present', async () => {
    render(await SpendingSummary(baseProps));

    expect(screen.getByTestId('SpendingSummary-Container')).toBeInTheDocument();
    expect(screen.getByTestId('SpendingSummary-Progress')).toBeInTheDocument();
    expect(screen.getByTestId('SpendingSummary-Legend')).toBeInTheDocument();
    expect(screen.getByTestId('SpendingSummary-Balance')).toBeInTheDocument();
    expect(mockWordlineButton).toHaveBeenCalledWith(
      expect.objectContaining({
        baseDataTestId: 'SpendingSummary-Manage-credit-limit',
        postUrl: 'https://wl.example/gb',
      })
    );
  });

  it('renders inline error when WL call fails', async () => {
    mockGetSpendingSummary.mockResolvedValueOnce(null);

    render(await SpendingSummary(baseProps));
    await waitFor(() => {
      expect(screen.getByTestId('SpendingSummary-Error')).toBeInTheDocument();
      expect(screen.getByText('application.sent.failed.message')).toBeInTheDocument();
      expect(screen.getByRole('button', { name: 'application.sent.tryAgain' })).toBeInTheDocument();
    });
  });

  it('hides manage credit button when account suspended', async () => {
    mockGetAccountInfo.mockResolvedValue({ status: PayAccountStatus.Suspended });

    render(await SpendingSummary(baseProps));

    expect(
      mockWordlineButton.mock.calls.find(
        ([props]) => props.baseDataTestId === 'SpendingSummary-Manage-credit-limit'
      )
    ).toBeUndefined();
    expect(
      mockWordlineButton.mock.calls.filter(
        ([props]) => props.baseDataTestId === 'SpendingSummary-Make-a-payment'
      )
    ).toHaveLength(1);
  });

  it('returns empty fragment when showBanner false', async () => {
    const { container } = render(await SpendingSummary({ ...baseProps, showBanner: false }));

    expect(container).toBeEmptyDOMElement();
  });

  it('returns empty fragment when account closed', async () => {
    mockGetAccountInfo.mockResolvedValue({ status: PayAccountStatus.Closed });

    const { container } = render(await SpendingSummary(baseProps));

    expect(container).toBeEmptyDOMElement();
  });

  it('returns empty fragment when current balance missing or mismatched', async () => {
    mockGetSpendingSummary.mockResolvedValueOnce({
      data: { getAccountBalanceSummaryV2: null },
    });
    let { container } = render(await SpendingSummary(baseProps));
    expect(container).toBeEmptyDOMElement();

    mockGetSpendingSummary.mockResolvedValueOnce({
      data: {
        getAccountBalanceSummaryV2: { ...baseBalance, tetheredGuid: 'different' },
      },
    });
    ({ container } = render(await SpendingSummary(baseProps)));
    expect(container).toBeEmptyDOMElement();
  });

  it('renders no account banner when account missing and no applications', async () => {
    render(await SpendingSummary({ ...baseProps, account: null }));

    expect(screen.getByTestId('NoAccountBanner')).toBeInTheDocument();
  });

  it('suppresses banner when IB pay on and active application exists', async () => {
    mockGetPayApplications.mockResolvedValue({
      data: {
        getPayApplications: {
          applications: [{ status: 'Active' }],
        },
      },
    });

    const { queryByTestId } = render(
      await SpendingSummary({ ...baseProps, account: null, isIBPayOn: true })
    );

    expect(queryByTestId('NoAccountBanner')).toBeNull();
  });

  it('uses DE post url when account scheme de', async () => {
    render(
      await SpendingSummary({
        ...baseProps,
        account: { ...defaultAccount, scheme: 'DE' as Scheme },
      })
    );

    expect(
      mockWordlineButton.mock.calls.find(
        ([props]) => props.baseDataTestId === 'SpendingSummary-Manage-credit-limit'
      )?.[0]?.postUrl
    ).toBe('https://wl.example/de');
  });

  it('skips rendering for cost centre users', async () => {
    mockGetRegistrationDetails.mockReturnValue({
      isOnlyCardHolder: false,
      isOnlyFinanceUser: false,
      isCardHolderAndFinanceUser: false,
      isOnlyCostCenter: true,
      isCardHolderAndCostCenterUser: false,
    });

    const { container } = render(await SpendingSummary(baseProps));

    expect(container).toBeEmptyDOMElement();
    expect(mockGetSpendingSummary).not.toHaveBeenCalled();
  });

  it('skips rendering for only card holders', async () => {
    mockGetRegistrationDetails.mockReturnValue({
      isOnlyCardHolder: true,
      isOnlyFinanceUser: false,
      isCardHolderAndFinanceUser: false,
      isOnlyCostCenter: false,
      isCardHolderAndCostCenterUser: false,
    });

    const { container } = render(await SpendingSummary(baseProps));

    expect(container).toBeEmptyDOMElement();
    expect(mockGetSpendingSummary).not.toHaveBeenCalled();
  });

  it('renders external link payment button when account suspended', async () => {
    mockGetAccountInfo.mockResolvedValue({ status: PayAccountStatus.SuspendedHold });

    render(await SpendingSummary(baseProps));

    const makePaymentCall = mockWordlineButton.mock.calls.find(
      ([props]) => props.baseDataTestId === 'SpendingSummary-Make-a-payment'
    );

    expect(makePaymentCall?.[0]).toMatchObject({
      variant: 'default',
      icon: null,
    });
    expect(makePaymentCall?.[0].iconSvg).toBeTruthy();
    expect(
      mockWordlineButton.mock.calls.find(
        ([props]) => props.baseDataTestId === 'SpendingSummary-Manage-credit-limit'
      )
    ).toBeUndefined();
  });
});

describe('SpendingSummarySkeleton', () => {
  it('renders skeleton placeholders', () => {
    const { getByTestId } = render(<SpendingSummarySkeleton t={(key) => key} />);

    expect(getByTestId('SpendingSummary-Skeleton')).toBeInTheDocument();
    expect(screen.getByText('homepage.home.innbusinessPay.spendingSummary')).toBeInTheDocument();
  });
});
