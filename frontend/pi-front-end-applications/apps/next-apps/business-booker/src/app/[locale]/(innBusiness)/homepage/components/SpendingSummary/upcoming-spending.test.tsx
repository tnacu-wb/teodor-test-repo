import '@testing-library/jest-dom';
import { act, fireEvent, render } from '@testing-library/react';
import { LOCALES, Currency } from '@whitbread-eos/api';

import { UpcomingSpending, UpcomingSpendingSkeleton } from './upcoming-spending';

const mockCookies = jest.fn();
const mockWordlineButton = jest.fn(({ baseDataTestId }: { baseDataTestId: string }) => (
  <button data-testid={`${baseDataTestId}-Mock`} />
));

jest.mock('next/headers', () => ({
  cookies: jest.fn(),
}));

jest.mock('next/image', () => ({
  __esModule: true,
  default: (props: any) => {
    // eslint-disable-next-line @next/next/no-img-element, jsx-a11y/alt-text
    return <img {...props} />;
  },
}));

jest.mock('~components/innBusiness/TextWithInfoTooltip', () => ({
  TextWithInfoTooltip: () => <div data-testid="TooltipMock" />,
}));

jest.mock('~components/innBusiness/WordlineButton', () => ({
  WordlineButton: (props: any) => {
    mockWordlineButton(props);
    return <button data-testid={`${props.baseDataTestId}-Mock`} />;
  },
}));

const mockUseTranslationServer = jest.fn();
const mockGetCommonIcons = jest.fn();
const mockGetUpcomingSpending = jest.fn();
const mockFormatIBAssetsUrl = jest.fn((url: string) => `formatted-${url}`);
const mockGetCountryLanguageByLocale = jest.fn(() => ({ language: 'en' }));

jest.mock('@whitbread-eos/utils/server', () => ({
  ID_TOKEN_COOKIE: 'id-token',
  getCountryLanguageByLocale: jest.fn(),
  getTranslations: jest.fn(),
  getUpcomingSpending: jest.fn(),
  getCommonIcons: jest.fn(),
  formatIBAssetsUrl: jest.fn(),
  cn: (...classes: Array<string | false | null | undefined>) => classes.filter(Boolean).join(' '),
}));

const {
  getTranslations,
  getUpcomingSpending,
  getCommonIcons,
  formatIBAssetsUrl,
  getCountryLanguageByLocale,
} = jest.requireMock('@whitbread-eos/utils/server');
const { cookies } = jest.requireMock('next/headers');

const baseAccount = {
  accountNumber: '123456789',
  tetheredGuid: 'guid',
  scheme: 'GB',
} as any;

const baseUpcoming = {
  currency: Currency.GBP_CODE,
  expectedSpendToday: 1000,
  expectedSpendTodayDate: '2024-08-01T00:00:00.000Z',
  expectedNextBilling: 2000,
  expectedNextBillingStartDate: '2024-08-02T00:00:00.000Z',
  expectedNextBillingEndDate: '2024-08-10T00:00:00.000Z',
  expectedNextPeriod: 3000,
  expectedNextPeriodStartDate: '2024-08-15T00:00:00.000Z',
  expectedNextPeriodEndDate: '2024-08-22T00:00:00.000Z',
};

const ORIGINAL_TZ = process.env.TZ;
const ORIGINAL_POST_URL = process.env.NEXT_PUBLIC_WORLDLINE_POST_URL;
const ORIGINAL_POST_URL_DE = process.env.NEXT_PUBLIC_WORLDLINE_POST_URL_DE;

beforeAll(() => {
  process.env.TZ = 'UTC';
});

afterAll(() => {
  process.env.TZ = ORIGINAL_TZ;
});

beforeEach(() => {
  jest.useFakeTimers().setSystemTime(new Date('2024-08-05T12:00:00.000Z'));

  process.env.NEXT_PUBLIC_WORLDLINE_POST_URL = 'https://wl.example/gb';
  process.env.NEXT_PUBLIC_WORLDLINE_POST_URL_DE = 'https://wl.example/de';

  mockCookies.mockReturnValue({
    get: jest.fn(() => ({ value: 'token-value' })),
  });
  cookies.mockImplementation(mockCookies);

  mockUseTranslationServer.mockResolvedValue({
    t: (key: string) => key,
  });
  getTranslations.mockImplementation(mockUseTranslationServer);

  mockGetCommonIcons.mockResolvedValue({});
  getCommonIcons.mockImplementation(mockGetCommonIcons);

  mockGetUpcomingSpending.mockResolvedValue({
    data: { getAccountUpcomingSpending: baseUpcoming },
  });
  getUpcomingSpending.mockImplementation(mockGetUpcomingSpending);

  mockFormatIBAssetsUrl.mockImplementation((url: string) => `formatted-${url}`);
  formatIBAssetsUrl.mockImplementation(mockFormatIBAssetsUrl);

  mockGetCountryLanguageByLocale.mockReturnValue({ language: 'en' });
  getCountryLanguageByLocale.mockImplementation(mockGetCountryLanguageByLocale);

  mockWordlineButton.mockClear();
});

afterEach(() => {
  jest.useRealTimers();
  process.env.NEXT_PUBLIC_WORLDLINE_POST_URL = ORIGINAL_POST_URL;
  process.env.NEXT_PUBLIC_WORLDLINE_POST_URL_DE = ORIGINAL_POST_URL_DE;
  jest.clearAllMocks();
});

describe('UpcomingSpending', () => {
  it('returns empty fragment when no data available', async () => {
    mockGetUpcomingSpending.mockResolvedValueOnce({
      data: { getAccountUpcomingSpending: null },
    });

    const { container } = render(
      await UpcomingSpending({
        baseDataTestId: 'Base',
        locale: LOCALES.EN,
        account: baseAccount,
        wlReturnUrl: '/return',
      })
    );

    expect(mockGetUpcomingSpending).toHaveBeenCalledWith(
      'token-value',
      baseAccount.accountNumber,
      baseAccount.tetheredGuid
    );
    expect(container).toBeEmptyDOMElement();
    expect(mockWordlineButton).not.toHaveBeenCalled();
  });

  it('renders spending cards with standard styling', async () => {
    const { getByTestId, getByText } = render(
      await UpcomingSpending({
        baseDataTestId: 'Base',
        locale: LOCALES.EN,
        account: baseAccount,
        wlReturnUrl: '/return',
      })
    );

    await act(async () => {
      fireEvent.click(getByTestId('Base-UpcomingWrapper').querySelector('button') as HTMLElement);
    });
    const wrapper = getByTestId('Base-UpcomingWrapper');
    expect(wrapper).toHaveClass('bg-alertYellow1');
    expect(getByText('homepage.home.upcoming.spending.heading')).toBeInTheDocument();
    expect(getByText(/^homepage\.home\.upcoming\.spending\.description/)).toBeInTheDocument();
    expect(getByText('£1,000.00')).toBeInTheDocument();
    expect(getByText('£2,000.00')).toBeInTheDocument();
    expect(getByText('£3,000.00')).toBeInTheDocument();

    expect(mockWordlineButton).toHaveBeenCalledWith(
      expect.objectContaining({
        baseDataTestId: 'Base-Inline-Manage-credit-limit',
        page: 'IncreaseCreditLimit.aspx',
        returnUrl: '/return',
        postUrl: 'https://wl.example/gb',
      })
    );
    expect(mockWordlineButton).toHaveBeenCalledWith(
      expect.objectContaining({
        baseDataTestId: 'Base-Inline-Make-a-payment',
        page: 'CardPayment.aspx',
      })
    );
  });

  it('renders credit limit alert with icon and previous day timestamp before 5am', async () => {
    jest.setSystemTime(new Date('2024-08-05T03:00:00.000Z'));
    const getHoursSpy = jest.spyOn(Date.prototype, 'getHours').mockReturnValue(3);

    mockGetCommonIcons.mockResolvedValueOnce({
      'icon.notification.alert': '/alert.svg',
    });
    const account = { ...baseAccount, scheme: 'DE' };

    try {
      const { getByTestId, container, getByText } = render(
        await UpcomingSpending({
          baseDataTestId: 'Base',
          locale: LOCALES.EN,
          account,
          reachesCreditLimit: true,
          wlReturnUrl: '/return',
        })
      );

      await act(async () => {
        fireEvent.click(getByTestId('Base-UpcomingWrapper').querySelector('button') as HTMLElement);
      });
      expect(getByTestId('Base-UpcomingWrapper')).toHaveClass('bg-tooltipError');
      expect(container.querySelector('img')).toBeInTheDocument();
      expect(mockFormatIBAssetsUrl).toHaveBeenCalledWith('/alert.svg');
      expect(getByText('homepage.home.upcoming.spending.creditToBeReached')).toBeInTheDocument();
      expect(
        getByText(/^homepage\.home\.upcoming\.spending\.creditToBeReachedDescription/)
      ).toBeInTheDocument();
      expect(getByText((content) => content.includes('04 August 2024'))).toBeInTheDocument();
      expect(mockWordlineButton).toHaveBeenCalledWith(
        expect.objectContaining({
          postUrl: 'https://wl.example/de',
        })
      );
    } finally {
      getHoursSpy.mockRestore();
    }
  });

  it('renders euro currency values with German locale formatting after 5am', async () => {
    mockGetCountryLanguageByLocale.mockReturnValueOnce({ language: 'de' });
    mockGetUpcomingSpending.mockResolvedValueOnce({
      data: {
        getAccountUpcomingSpending: {
          ...baseUpcoming,
          currency: Currency.EUR_CODE,
          expectedSpendToday: 1234,
          expectedNextBilling: 5678,
          expectedNextPeriod: 91011,
        },
      },
    });

    const formatEuro = (value: number) =>
      `${Math.abs(value).toLocaleString(LOCALES.DE, {
        maximumFractionDigits: 2,
        minimumFractionDigits: 2,
      })} €`;

    const { getByTestId, getByText } = render(
      await UpcomingSpending({
        baseDataTestId: 'Base',
        locale: LOCALES.DE,
        account: baseAccount,
        wlReturnUrl: '/return',
      })
    );

    await act(async () => {
      fireEvent.click(getByTestId('Base-UpcomingWrapper').querySelector('button') as HTMLElement);
    });

    expect(getCountryLanguageByLocale).toHaveBeenCalledWith(LOCALES.DE);
    expect(getCommonIcons).toHaveBeenCalledWith('de');
    expect(getByText(formatEuro(1234))).toBeInTheDocument();
    expect(getByText(formatEuro(5678))).toBeInTheDocument();
    expect(getByText(formatEuro(91011))).toBeInTheDocument();
    expect(
      getByText((content) => content.includes('homepage.home.innbusinessPay.lastUpdatedHour'))
    ).toBeInTheDocument();
    expect(getByText((content) => content.includes('05 August 2024'))).toBeInTheDocument();
  });
});

describe('UpcomingSpendingSkeleton', () => {
  it('renders skeleton wrapper', () => {
    const { getByTestId } = render(<UpcomingSpendingSkeleton />);
    expect(getByTestId('UpcomingSpending-Skeleton')).toBeInTheDocument();
  });
});
