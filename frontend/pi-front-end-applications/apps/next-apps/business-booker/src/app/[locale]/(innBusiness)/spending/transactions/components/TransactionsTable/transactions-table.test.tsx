import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { getAccountTransactions } from '@whitbread-eos/utils/server';

import TransactionsTable from './transactions-table';

const mockGetAccountTransactions = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  useTranslation: () => ({
    t: (key: string) => {
      const translations: Record<string, string> = {
        'spending.transactions.outstanding.title': 'Outstanding transactions',
        'spending.transactions.outstanding.tooltip': 'Transactions tooltip info',
        'spending.transactions.noTransactions.title': 'No transactions in your history... yet!',
        'spending.transactions.noTransactions.description':
          'When you make a transaction, it will appear here',
        'spending.transactions.header.date': 'Date',
        'spending.transactions.header.cardHolder': 'Card holder',
        'spending.transactions.header.cardNo': 'Card no.',
        'spending.transactions.header.location': 'Location',
        'spending.transactions.header.purchaseOrder': 'Purchase order',
        'spending.transactions.header.customerRef': 'Customer ref.',
        'spending.transactions.header.grossValue': 'Gross value',
        'icon.expand-icon': '/icons/expand.svg',
      };
      return translations[key] || key;
    },
  }),
  cn: jest.fn((...args: any[]) => args.filter(Boolean).join(' ')),
  formatIBAssetsUrl: (url: string) => url,
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  getAccountTransactions: jest.fn(),
  useTranslation: () => ({
    t: (key: string) => {
      const translations: Record<string, string> = {
        'spending.transactions.outstanding.title': 'Outstanding transactions',
        'spending.transactions.outstanding.tooltip': 'Transactions tooltip info',
        'spending.transactions.noTransactions.title': 'No transactions in your history... yet!',
        'spending.transactions.noTransactions.description':
          'When you make a transaction, it will appear here',
        'spending.transactions.header.date': 'Date',
        'spending.transactions.header.cardHolder': 'Card holder',
        'spending.transactions.header.cardNo': 'Card no.',
        'spending.transactions.header.location': 'Location',
        'spending.transactions.header.purchaseOrder': 'Purchase order',
        'spending.transactions.header.customerRef': 'Customer ref.',
        'spending.transactions.header.grossValue': 'Gross value',
        'icon.expand-icon': '/icons/expand.svg',
      };
      return translations[key] || key;
    },
  }),
  cn: (...args: any[]) => args.filter(Boolean).join(' '),
  formatIBAssetsUrl: (url: string) => url,
}));

jest.mock('next/navigation', () => ({
  useRouter: () => ({
    replace: jest.fn(),
  }),
  usePathname: () => '/spending/transactions',
  useSearchParams: () => new URLSearchParams(),
}));

jest.mock('next/image', () => ({
  __esModule: true,
  default: ({ src, alt, width, height }: any) => (
    <img src={src} alt={alt} width={width} height={height} data-testid="next-image" />
  ),
}));

jest.mock('~components/innBusiness/DataTable/data-table-body', () => ({
  __esModule: true,
  default: jest.fn(({ rows }: any) => (
    <tbody data-testid="MockDataTableBody">
      {rows.length > 0 ? <tr data-testid="items-rendered" /> : null}
    </tbody>
  )),
}));

jest.mock('~components/innBusiness/DataTable/data-table-pagination', () => ({
  DataTablePagination: jest.fn(({ pageIndex, totalPages }: any) => (
    <div data-testid="MockDataTablePagination">{`${pageIndex}/${totalPages}`}</div>
  )),
}));

jest.mock('~components/innBusiness/TextWithInfoTooltip', () => ({
  TextWithInfoTooltip: ({ mainText }: { mainText: string }) => <span>{mainText}</span>,
}));

jest.mock(
  './transactions-expandable-row',
  () => () => (<div data-testid="MockExpandableRow" />) as any
);

jest.mock('../Analytics/analytics', () => {
  const TransactionsAnalyticsMock = () => <div data-testid="TransactionsAnalyticsMock" />;
  TransactionsAnalyticsMock.displayName = 'TransactionsAnalyticsMock';
  return TransactionsAnalyticsMock;
});

const mockTransactions = [
  {
    invoiceDate: '2025-04-21',
    invoiceNo: '414210',
    transactionDate: '2025-04-13T19:15:00',
    netAmount: {
      amount: -7.92,
      currencyCode: 'GBP',
      currencySymbol: '£',
    },
    taxAmount: {
      amount: -1.58,
      currencyCode: 'GBP',
      currencySymbol: '£',
    },
    grossAmount: {
      amount: -9.5,
      currencyCode: 'GBP',
      currencySymbol: '£',
    },
    location: 'Cardiff North',
    pan: '30895001*******0025',
    cardName: 'Mr Ramesh Patil',
    purchaseOrderReference: '76770302',
    customerOwnRef: 'REF123',
    salesOrderNumber: '76770302',
    lineItems: [
      {
        grossAmount: {
          amount: -9.5,
          currencyCode: 'GBP',
          currencySymbol: '£',
        },
        description: 'Accommodation',
        guestName: 'Seema Patil',
        invoiceLineItem: 2,
        netAmount: {
          amount: -7.92,
          currencyCode: 'GBP',
          currencySymbol: '£',
        },
        quantity: 1,
        taxAmount: {
          amount: -1.58,
          currencyCode: 'GBP',
          currencySymbol: '£',
        },
      },
    ],
  },
];

const mockIcons = {
  'icon.expand-icon': '/icons/expand.svg',
  'icon.collapse-icon': '/icons/collapse.svg',
};

const mockRequestParameters = {
  scheme: 'test-scheme',
  schemeCustomerId: 123,
  tetheredUserGuid: 'test-guid',
  searchCriteria: {
    dateSearch: {
      dateFrom: '2024-01-01',
      dateTo: '2025-09-18',
      transactionTypes: 'Both',
    },
  },
};

beforeEach(() => {
  jest.clearAllMocks();
  mockGetAccountTransactions.mockResolvedValue({
    data: {
      viewAccountTransactions: {
        response: {
          transactions: mockTransactions,
        },
        pagingResult: {
          totalRecordCount: mockTransactions.length,
        },
      },
    },
  });

  getAccountTransactions.mockImplementation(mockGetAccountTransactions);
});

describe('TransactionsTable', () => {
  const defaultProps = {
    icons: mockIcons,
    locale: LOCALES.EN,
    token: 'test-token',
    requestParameters: mockRequestParameters,
    pageSize: 15,
    pageIndex: 1,
  };

  it('renders the section header and table', async () => {
    render(await TransactionsTable(defaultProps));

    expect(screen.getByText('Outstanding transactions')).toBeInTheDocument();
    expect(screen.getByTestId('MockDataTableBody')).toBeInTheDocument();
    expect(screen.getByTestId('items-rendered')).toBeInTheDocument();
  });

  it('renders the no results state when no transactions returned', async () => {
    mockGetAccountTransactions.mockResolvedValueOnce({
      data: {
        viewAccountTransactions: {
          response: {
            transactions: [],
          },
          pagingResult: {
            totalRecordCount: 0,
          },
        },
      },
    });

    const { getByText } = render(await TransactionsTable(defaultProps));

    expect(getByText('No transactions in your history... yet!')).toBeInTheDocument();
  });

  it('renders pagination when more than one page of transactions', async () => {
    mockGetAccountTransactions.mockResolvedValueOnce({
      data: {
        viewAccountTransactions: {
          response: {
            transactions: mockTransactions,
          },
          pagingResult: {
            totalRecordCount: 45,
          },
        },
      },
    });

    render(await TransactionsTable({ ...defaultProps, pageIndex: 2 }));

    expect(screen.getByTestId('MockDataTablePagination')).toHaveTextContent('2/3');
  });
});
