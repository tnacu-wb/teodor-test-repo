import '@testing-library/jest-dom';
import { act, fireEvent, render } from '@testing-library/react';
import { CompanySpending, LOCALES } from '@whitbread-eos/api';

import { SpendOverTime } from './spend-over-time';

const mockProps = {
  locale: LOCALES.EN,
  dataTestId: 'test123',
  spending: [] as CompanySpending[],
  showDownload: true,
  account: {
    accountName: 'test four',
    accountNumber: '12345',
    schemeCustomerId: 12345,
    tetheredGuid: 'test-123',
    registrationRoles: ['ACCOUNT_HOLDER', 'CARD_HOLDER'],
    errorCode: '123',
    scheme: 'GB',
    apiUserGuid: 'test-123',
  } as any,
  language: 'en',
  token: '1234',
  fromMonthYear: '04-2024',
  toMonthYear: '04-2025',
  icons: {
    'icon.arrow.left.purple': 'mockArrowLeftIcon',
    'icon.notification.error': 'mockErrorIcon',
  } as any,
  scheme: 'GB',
  defaultCurrencyCode: 'EUR',
};

jest.mock('@whitbread-eos/utils', () => ({
  cn: jest.fn(),
  useTranslation: () => {
    return {
      t: (str: string) => str,
    };
  },
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  cn: jest.fn(),
  formatIBAssetsUrl: () => {
    return '/';
  },
  getSpendOverTimeCSV: () => {
    return 'test';
  },
}));

class ResizeObserver {
  observe() {
    return true;
  }
  unobserve() {
    return true;
  }
  disconnect() {
    return true;
  }
}

describe('SpendOverTime', () => {
  beforeEach(() => {
    jest.resetAllMocks();
    (window as any).ResizeObserver = ResizeObserver;
  });

  it('should render the SpendOverTime component', async () => {
    const { getByTestId } = render(<SpendOverTime {...mockProps} />);

    expect(getByTestId('test123-Spend-Over-Time')).toBeInTheDocument();
  });

  it('should render the SpendOverTime component with data and DE', async () => {
    mockProps.locale = LOCALES.DE;
    mockProps.spending = [
      {
        year: 2025,
        month: '4',
        bookingValue: 1000,
        bookingCurrency: null as any,
      },
      {
        year: 2024,
        month: '1',
        bookingValue: 1000,
        bookingCurrency: 'EUR',
      },
      {
        year: 2024,
        month: '2',
        bookingValue: 2000,
        bookingCurrency: 'GBP',
      },
      {
        year: 2024,
        month: '3',
        bookingValue: 2000,
        bookingCurrency: 'GBP',
      },
      {
        year: 2024,
        month: '4',
        bookingValue: 2500,
        bookingCurrency: 'GBP',
      },
      {
        year: 2024,
        month: '5',
        bookingValue: -2000,
        bookingCurrency: 'GBP',
      },
      {
        year: 2024,
        month: '6',
        bookingValue: 2000,
        bookingCurrency: 'GBP',
      },
    ];
    const { getByTestId } = render(<SpendOverTime {...mockProps} />);

    const csvDownloadButton = getByTestId('test123-DownloadButton');

    act(() => {
      fireEvent.click(csvDownloadButton);
    });

    expect(getByTestId('test123-Spend-Over-Time')).toBeInTheDocument();
  });

  it('should render the SpendOverTime component with no scheme', async () => {
    const { getByTestId } = render(<SpendOverTime {...{ ...mockProps, scheme: '' }} />);

    expect(getByTestId('test123-Spend-Over-Time')).toBeInTheDocument();
  });

  it('should render the SpendOverTime component with DE scheme', async () => {
    mockProps.icons = null;
    mockProps.locale = LOCALES.DE;
    mockProps.spending = [];
    const { getByTestId } = render(<SpendOverTime {...{ ...mockProps, scheme: 'DE' }} />);

    expect(getByTestId('test123-Spend-Over-Time')).toBeInTheDocument();
  });
});
