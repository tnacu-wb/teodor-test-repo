import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { Currency, CustomerAccountDetails, LOCALES, Scheme } from '@whitbread-eos/api';

import StatementsTotal, { StatementsTotalSkeleton } from './statements-total';

const mockAccount: CustomerAccountDetails = {
  tetheredGuid: '12345',
  scheme: 'GB' as Scheme,
};

const originalWorldlinePostUrlDe = process.env.NEXT_PUBLIC_WORLDLINE_POST_URL_DE;

const wordlineButtonPropsMock = jest.fn();

const getAccountInfoMock: jest.Mock<
  {
    statementValue: { value: number; currencyCode: string };
    outStandingBalance: { value: number; currencyCode: string };
  },
  unknown[]
> = jest.fn(() => ({
  statementValue: {
    value: 100,
    currencyCode: 'GBP',
  },
  outStandingBalance: {
    value: 50,
    currencyCode: 'GBP',
  },
}));

const getSpendingSummaryV2Mock: jest.Mock<
  {
    data: {
      getAccountBalanceSummaryV2: {
        tetheredGuid: string;
        interimPayments: { amount: number; currencyCode: string };
        outstanding: { amount: number; currencyCode: string };
      };
    };
  },
  unknown[]
> = jest.fn(() => ({
  data: {
    getAccountBalanceSummaryV2: {
      tetheredGuid: '12345',
      interimPayments: {
        amount: 20,
        currencyCode: 'GBP',
      },
      outstanding: {
        amount: 50,
        currencyCode: 'GBP',
      },
    },
  },
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  cn: jest.fn(),
  getCountryLanguageByLocale: jest.fn(() => ({ language: 'en' })),
  getWorldlineReturnUrl: jest.fn(),
  getTranslations: jest.fn(() => ({
    t: (key: string) => key,
  })),
  useTranslation: jest.fn(() => ({
    t: (key: string) => key,
  })),
  getAccountInfo: (...args: unknown[]) => getAccountInfoMock(...args),
  formatIBAssetsUrl: jest.fn(() => '/'),
  getSpendingSummaryV2: (...args: unknown[]) => getSpendingSummaryV2Mock(...args),
}));

const formatAmountMock = jest.fn((amount: number, currency: string, locale: LOCALES) => {
  return locale === LOCALES.DE
    ? `${amount.toFixed(2)} ${currency}`
    : `${currency} ${amount.toFixed(2)}`;
});

jest.mock('~components/innBusiness/WordlineButton', () => ({
  WordlineButton: (props: any) => {
    wordlineButtonPropsMock(props);
    return (
      <button
        data-testid={`${props.baseDataTestId}-Button`}
        onClick={() =>
          props.analyticsLogEvent && (window as any)._satellite?.track(props.analyticsLogEvent)
        }
      >
        {props.text}
      </button>
    );
  },
}));

jest.mock('../../utils/format-amount', () => ({
  formatAmount: (amount: number, currency: string, locale: LOCALES) =>
    formatAmountMock(amount, currency, locale),
}));

jest.mock('next/headers', () => ({
  cookies: jest.fn(() => ({
    get: jest.fn(() => ({ value: 'mocked-token' })),
  })),
}));

describe('StatementsTotal', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (window as any)._satellite = { track: jest.fn() };
    getAccountInfoMock.mockImplementation(() => ({
      statementValue: {
        value: 100,
        currencyCode: Currency.GBP_CODE,
      },
      outStandingBalance: {
        value: 50,
        currencyCode: Currency.GBP_CODE,
      },
    }));
    getSpendingSummaryV2Mock.mockImplementation(() => ({
      data: {
        getAccountBalanceSummaryV2: {
          tetheredGuid: '12345',
          interimPayments: {
            amount: 20,
            currencyCode: 'GBP',
          },
          outstanding: {
            amount: 50,
            currencyCode: 'GBP',
          },
        },
      },
    }));
  });

  it('renders correctly', async () => {
    const { getByTestId, getByText } = render(
      await StatementsTotal({
        locale: LOCALES.EN,
        icons: {},
        baseDataTestId: 'statements-total',
        account: mockAccount,
      })
    );

    expect(getByTestId('statements-total')).toBeInTheDocument();

    expect(
      getByText('spending.statementsInvoicesPayments.statements.latestStatementValue.label')
    ).toBeInTheDocument();
    expect(
      getByText('spending.statementsInvoicesPayments.statements.interimPayments.label')
    ).toBeInTheDocument();
    expect(
      getByText('spending.statementsInvoicesPayments.statements.outstandingBalance.label')
    ).toBeInTheDocument();

    expect(
      getByText('spending.statementsInvoicesPayments.statements.button.makePayment')
    ).toBeInTheDocument();
  });

  it('calls formatAmount with correct parameters', async () => {
    await StatementsTotal({
      locale: LOCALES.EN,
      icons: {},
      baseDataTestId: 'statements-total',
      account: mockAccount,
    });

    expect(formatAmountMock).toHaveBeenCalledWith(20, Currency.GBP_CODE, LOCALES.EN);
    expect(formatAmountMock).toHaveBeenCalledWith(100, Currency.GBP_CODE, LOCALES.EN);
    expect(formatAmountMock).toHaveBeenCalledWith(50, Currency.GBP_CODE, LOCALES.EN);
  });

  it('calls satellite tracking on Make a payment button click', async () => {
    const { getByTestId } = render(
      await StatementsTotal({
        locale: LOCALES.EN,
        icons: {},
        baseDataTestId: 'statements-total',
        account: mockAccount,
      })
    );

    const makePaymentButton = getByTestId('statements-total-Make-a-payment-Button');
    makePaymentButton.click();

    expect((window as any)._satellite.track).toHaveBeenCalledWith('makePayment');
  });

  it('renders skeleton when loading', async () => {
    const { getByText } = render(<StatementsTotalSkeleton t={(key: string) => key} />);

    expect(getByText('Latest statement value')).toBeInTheDocument();
    expect(getByText('Interim payments')).toBeInTheDocument();
    expect(getByText('Outstanding balance')).toBeInTheDocument();
  });

  it('passes euro worldline configuration for DE scheme', async () => {
    process.env.NEXT_PUBLIC_WORLDLINE_POST_URL_DE = 'https://de.worldline';
    const euroAccount: CustomerAccountDetails = {
      ...mockAccount,
      scheme: 'DE' as Scheme,
    };

    render(
      await StatementsTotal({
        locale: LOCALES.EN,
        icons: {},
        baseDataTestId: 'statements-total',
        account: euroAccount,
      })
    );

    expect(wordlineButtonPropsMock).toHaveBeenCalledWith(
      expect.objectContaining({
        page: 'InterimPayment.aspx',
        postUrl: 'https://de.worldline',
      })
    );
  });

  it('formats interim and outstanding amounts as EUR when summary returns euros', async () => {
    getSpendingSummaryV2Mock.mockImplementation(() => ({
      data: {
        getAccountBalanceSummaryV2: {
          tetheredGuid: '12345',
          interimPayments: {
            amount: 45,
            currencyCode: 'EUR',
          },
          outstanding: {
            amount: 60,
            currencyCode: 'EUR',
          },
        },
      },
    }));

    await StatementsTotal({
      locale: LOCALES.EN,
      icons: {},
      baseDataTestId: 'statements-total',
      account: mockAccount,
    });

    expect(formatAmountMock).toHaveBeenCalledWith(45, Currency.EUR_CODE, LOCALES.EN);
    expect(formatAmountMock).toHaveBeenCalledWith(60, Currency.EUR_CODE, LOCALES.EN);
  });
});

afterAll(() => {
  process.env.NEXT_PUBLIC_WORLDLINE_POST_URL_DE = originalWorldlinePostUrlDe;
});
