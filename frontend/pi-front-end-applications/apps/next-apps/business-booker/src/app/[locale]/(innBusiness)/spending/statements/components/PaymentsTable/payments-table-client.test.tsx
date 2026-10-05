import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { Currency, LOCALES } from '@whitbread-eos/api';

import PaymentsTableClient from './payments-table-client';

jest.mock('@whitbread-eos/utils/server', () => ({
  useTranslation: () => ({ t: (key: string) => key }),
  getAccountPayments: jest.fn(),
  cn: jest.fn((...args: any[]) => args.join(' ')),
  formatIBAssetsUrl: jest.fn((path: string) => path),
}));

jest.mock('next/image', () => ({
  __esModule: true,
  default: ({ alt, ...props }: any) => <img alt={alt} {...props} />,
}));

const mockAccount = {
  accountNumber: '123',
  tetheredGuid: 'guid',
} as any;

const initialItems = [
  {
    id: '1',
    paymentDate: '12/07/2025',
    paymentFailed: false,
    paymentValue: { value: '100.00', currencyCode: Currency.GBP_CODE, currencySymbol: '£' },
    paymentDescription: 'desc',
  },
  {
    id: '2',
    paymentDate: '12/07/2025',
    paymentFailed: true,
    paymentValue: { value: '200.00', currencyCode: Currency.EUR_CODE, currencySymbol: '£' },
    paymentDescription: 'desc2',
  },
  {
    id: '3',
    paymentDate: '12/07/2025',
    paymentFailed: false,
    paymentValue: { value: '300.00', currencyCode: Currency.GBP_CODE, currencySymbol: '£' },
    paymentDescription: 'desc3',
  },
];

describe('PaymentsTableClient', () => {
  const token = 'token';
  const pageSize = 2;
  const locale = LOCALES.EN;

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders status as Failed or Successful', () => {
    render(
      <PaymentsTableClient
        baseDataTestId="test"
        token={token}
        account={mockAccount}
        initialItems={initialItems}
        pageSize={pageSize}
        locale={locale}
        icons={{ success: 'success-icon', failed: 'failed-icon' }}
      />
    );

    expect(
      screen.getAllByText('statementsInvoicesPayments.payments.table.row.status.failed')[0]
    ).toBeInTheDocument();
    expect(
      screen.getAllByText('statementsInvoicesPayments.payments.table.row.status.successful')[0]
    ).toBeInTheDocument();
  });

  it('renders amount with correct currency formatting', () => {
    render(
      <PaymentsTableClient
        baseDataTestId="test"
        token={token}
        account={mockAccount}
        initialItems={initialItems}
        pageSize={pageSize}
        locale={locale}
        icons={{ success: 'success-icon', failed: 'failed-icon' }}
      />
    );

    expect(screen.getByText('£100.00')).toBeInTheDocument();
    expect(screen.getByText('200.00 €')).toBeInTheDocument();
  });

  it('renders payment description', () => {
    render(
      <PaymentsTableClient
        baseDataTestId="test"
        token={token}
        account={mockAccount}
        initialItems={initialItems}
        pageSize={pageSize}
        locale={locale}
        icons={{ success: 'success-icon', failed: 'failed-icon' }}
      />
    );

    expect(screen.getByText('desc')).toBeInTheDocument();
    expect(screen.getByText('desc2')).toBeInTheDocument();
  });
});
