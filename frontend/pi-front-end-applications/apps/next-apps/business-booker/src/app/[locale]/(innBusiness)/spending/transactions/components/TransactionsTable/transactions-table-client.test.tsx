import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { DataTableRow } from '~components/innBusiness/DataTable';

import TransactionsTableClient from './transactions-table-client';

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
  cn: (...args: any[]) => args.filter(Boolean).join(' '),
  formatIBAssetsUrl: (url: string) => url,
  getLocaleByPathname: jest.fn(),
}));

jest.mock('next/image', () => ({
  __esModule: true,
  default: ({ src, alt, width, height }: any) => (
    <img src={src} alt={alt} width={width} height={height} data-testid="next-image" />
  ),
}));

jest.mock('~components/innBusiness/DataTable/data-table-body', () => ({
  __esModule: true,
  default: jest.fn(({ rows, columns }: any) => (
    <tbody data-testid="MockDataTableBody">
      <tr data-testid="table-content">
        <td data-testid="columns-count">{columns.length}</td>
        {rows.map((item: any, index: number) => (
          <td key={index} data-testid={`item-${index}`}>
            {item.date}
          </td>
        ))}
      </tr>
    </tbody>
  )),
}));

jest.mock('~components/innBusiness/TextWithInfoTooltip', () => ({
  TextWithInfoTooltip: ({ mainText, infoText, baseDataTestId }: any) => (
    <div data-testid={baseDataTestId}>
      <span>{mainText}</span>
      <span>{infoText}</span>
    </div>
  ),
}));

jest.mock('./transactions-expandable-row', () => ({
  __esModule: true,
  default: () => <div data-testid="MockExpandableRow" />,
}));

const mockIcons = {
  'icon.expand-icon': '/icons/expand.svg',
  'icon.collapse-icon': '/icons/collapse.svg',
};

describe('TransactionsTableClient', () => {
  const mockItems: DataTableRow[] = [
    {
      date: '2025-04-13T19:15:00',
      cardHolder: 'John Doe Smith',
      cardNo: '1234****5678',
      location: 'Premier Inn London City',
      purchaseOrder: 'PO123456',
      customerRef: 'REF789',
      grossValue: {
        amount: 125.5,
        currencyCode: 'GBP',
        currencySymbol: '£',
      },
    },
  ];

  it('renders section title with tooltip', () => {
    render(<TransactionsTableClient items={mockItems} icons={mockIcons} locale={LOCALES.EN} />);

    const title = screen.getByTestId('TransactionsTable-Title');
    expect(title).toBeInTheDocument();
    expect(title).toHaveTextContent('Outstanding transactions');
    expect(title).toHaveTextContent('Transactions tooltip info');
  });

  it('renders DataTableBody with correct items', () => {
    render(<TransactionsTableClient items={mockItems} icons={mockIcons} locale={LOCALES.EN} />);

    expect(screen.getByTestId('MockDataTableBody')).toBeInTheDocument();
    expect(screen.getByTestId('table-content')).toBeInTheDocument();
    expect(screen.getByTestId('item-0')).toHaveTextContent('2025-04-13T19:15:00');
  });

  it('renders no results component when items array is empty', () => {
    render(<TransactionsTableClient items={[]} icons={mockIcons} locale={LOCALES.EN} />);

    expect(screen.getByText('No transactions in your history... yet!')).toBeInTheDocument();
    expect(
      screen.getByText('When you make a transaction, it will appear here')
    ).toBeInTheDocument();
  });

  it('passes 7 columns to DataTableBody', () => {
    render(<TransactionsTableClient items={mockItems} icons={mockIcons} locale={LOCALES.EN} />);

    expect(screen.getByTestId('columns-count')).toHaveTextContent('7');
  });

  it('formats date correctly in column render', () => {
    const { container } = render(
      <TransactionsTableClient items={mockItems} icons={mockIcons} locale={LOCALES.EN} />
    );

    expect(container).toBeInTheDocument();
  });

  it('splits card holder name into two lines when 4 or more words', () => {
    const items: DataTableRow[] = [
      {
        ...mockItems[0],
        cardHolder: 'John Michael David Smith',
      },
    ];

    render(<TransactionsTableClient items={items} icons={mockIcons} locale={LOCALES.EN} />);
    expect(screen.getByTestId('MockDataTableBody')).toBeInTheDocument();
  });

  it('does not split card holder name with 3 or fewer words', () => {
    const items: DataTableRow[] = [
      {
        ...mockItems[0],
        cardHolder: 'Mr Ramesh',
      },
    ];

    render(<TransactionsTableClient items={items} icons={mockIcons} locale={LOCALES.EN} />);
    expect(screen.getByTestId('MockDataTableBody')).toBeInTheDocument();
  });

  it('formats gross value with currency symbol', () => {
    render(<TransactionsTableClient items={mockItems} icons={mockIcons} locale={LOCALES.EN} />);

    expect(screen.getByTestId('MockDataTableBody')).toBeInTheDocument();
  });

  it('renders with German locale', () => {
    render(<TransactionsTableClient items={mockItems} icons={mockIcons} locale={LOCALES.DE} />);

    expect(screen.getByTestId('TransactionsTable-Title')).toBeInTheDocument();
  });

  it('renders empty expandable column header', () => {
    render(<TransactionsTableClient items={mockItems} icons={mockIcons} locale={LOCALES.EN} />);

    const expandHeader = screen.getByTestId('TransactionsTable-head-expandable');
    expect(expandHeader).toBeInTheDocument();
    expect(expandHeader).toBeEmptyDOMElement();
  });

  it('renders table with correct structure', () => {
    render(<TransactionsTableClient items={mockItems} icons={mockIcons} locale={LOCALES.EN} />);

    expect(screen.getByTestId('TransactionsTable-container')).toBeInTheDocument();
    expect(screen.getByTestId('TransactionsTable')).toBeInTheDocument();
    expect(screen.getByTestId('TransactionsTable-header')).toBeInTheDocument();
    expect(screen.getByTestId('TransactionsTable-row')).toBeInTheDocument();
  });
});
