import '@testing-library/jest-dom';
import { render, screen, fireEvent } from '@testing-library/react';

import type { DataTableColumn, DataTableRow } from '~components/innBusiness/DataTable/data-table';

import TransactionsExpandableRow from './transactions-expandable-row';

jest.mock('@whitbread-eos/utils/server', () => ({
  useTranslation: () => ({
    t: (key: string) => {
      const translations: Record<string, string> = {
        'icon.expand-icon': '/icons/expand.svg',
        'icon.collapse-icon': '/icons/collapse.svg',
      };
      return translations[key] || key;
    },
  }),
  formatIBAssetsUrl: (url: string) => url,
  cn: (...classes: any[]) => classes.filter(Boolean).join(' '),
  getLocaleByPathname: jest.fn(),
}));

jest.mock('@whitbread-eos/atoms/ui', () => ({
  TableRow: ({ children, onClick, className, ...props }: any) => (
    <tr onClick={onClick} className={className} {...props}>
      {children}
    </tr>
  ),
  TableCell: ({ children, className, colSpan, ...props }: any) => (
    <td className={className} colSpan={colSpan} {...props}>
      {children}
    </td>
  ),
}));

jest.mock('next/image', () => ({
  __esModule: true,
  default: ({ src, alt, ...props }: any) => <img src={src} alt={alt} {...props} />,
}));

jest.mock('~components/innBusiness/DataTable/data-table-page', () => ({
  cellStyle: 'mocked-cell-style',
}));

const mockColumns: DataTableColumn[] = [
  {
    id: 'date',
    label: 'Date',
    className: 'w-[180px]',
    render: (_, row) => <span>{row.date}</span>,
  },
  {
    id: 'location',
    label: 'Location',
    className: 'w-[200px]',
    render: (_, row) => <span>{row.location}</span>,
  },
  {
    id: 'grossValue',
    label: 'Gross value',
    className: 'w-[150px] text-right',
    render: (_, row) => {
      const amount = row.grossValue;
      if (amount && typeof amount === 'object') {
        return `${amount.currencySymbol} ${Math.abs(amount.amount).toFixed(2)}`;
      }
      return <span>{row.grossValue}</span>;
    },
  },
];

const mockRow: DataTableRow = {
  date: '2025-04-13T19:15:00',
  location: 'Premier Inn London',
  grossValue: {
    amount: 125.5,
    currencyCode: 'GBP',
    currencySymbol: '£',
  },
  itemisations: JSON.stringify({
    type: 'itemisations',
    items: [
      {
        description: 'Room Charge',
        guestName: 'John Doe',
        amount: 100.0,
        currencyCode: 'GBP',
        currencySymbol: '£',
      },
      {
        description: 'Breakfast',
        amount: 25.5,
        currencyCode: 'GBP',
        currencySymbol: '£',
      },
    ],
  }),
};

describe('TransactionsExpandableRow', () => {
  const defaultProps = {
    baseDataTestId: 'TransactionsTable',
    columns: mockColumns,
    row: mockRow,
    rowData: 'test-row-data',
    rowIndex: 0,
    expendableKey: 'itemisations',
    isMobileView: false,
  };

  it('renders row with expand icon by default', () => {
    render(<TransactionsExpandableRow {...defaultProps} />);

    expect(screen.getByTestId('TransactionsTable-row-0')).toBeInTheDocument();
    const icon = screen.getByTestId('Expand-Collapse-Icon');
    expect(icon).toHaveAttribute('src', '/icons/expand.svg');
    expect(icon).toHaveAttribute('alt', 'expand');
  });

  it('toggles expand/collapse on row click', () => {
    render(<TransactionsExpandableRow {...defaultProps} />);

    const row = screen.getByTestId('TransactionsTable-row-0');
    const icon = screen.getByTestId('Expand-Collapse-Icon');

    expect(icon).toHaveAttribute('src', '/icons/expand.svg');
    expect(screen.queryByTestId('TransactionsTable-row-expanded')).not.toBeInTheDocument();

    fireEvent.click(row);
    expect(icon).toHaveAttribute('src', '/icons/collapse.svg');
    expect(icon).toHaveAttribute('alt', 'collapse');
    expect(screen.getByTestId('TransactionsTable-row-expanded')).toBeInTheDocument();

    fireEvent.click(row);
    expect(icon).toHaveAttribute('src', '/icons/expand.svg');
    expect(screen.queryByTestId('TransactionsTable-row-expanded')).not.toBeInTheDocument();
  });

  it('renders itemisations in desktop view when expanded', () => {
    render(<TransactionsExpandableRow {...defaultProps} />);

    const row = screen.getByTestId('TransactionsTable-row-0');
    fireEvent.click(row);

    const expandedRow = screen.getByTestId('TransactionsTable-row-expanded');
    expect(expandedRow).toBeInTheDocument();
    expect(expandedRow).toHaveTextContent('Room Charge');
    expect(expandedRow).toHaveTextContent('(John Doe)');
    expect(expandedRow).toHaveTextContent('Breakfast');
  });

  it('renders itemisations in mobile view when expanded', () => {
    render(<TransactionsExpandableRow {...defaultProps} isMobileView={true} />);

    const row = screen.getByTestId('TransactionsTable-row-0');
    fireEvent.click(row);

    const expandedRow = screen.getByTestId('TransactionsTable-row-expanded');
    expect(expandedRow).toBeInTheDocument();
    expect(expandedRow).toHaveTextContent('Room Charge');
    expect(expandedRow).toHaveTextContent('(John Doe)');
    expect(expandedRow).toHaveTextContent('Breakfast');
  });

  it('formats currency with symbol correctly', () => {
    render(<TransactionsExpandableRow {...defaultProps} />);

    const row = screen.getByTestId('TransactionsTable-row-0');
    fireEvent.click(row);

    const expandedRow = screen.getByTestId('TransactionsTable-row-expanded');
    expect(expandedRow).toHaveTextContent('£100.00');
    expect(expandedRow).toHaveTextContent('£25.50');
  });

  it('formats currency without symbol using Intl fallback', () => {
    const rowWithoutSymbol: DataTableRow = {
      ...mockRow,
      itemisations: JSON.stringify({
        type: 'itemisations',
        items: [
          {
            description: 'Room Charge',
            amount: 100.0,
            currencyCode: 'EUR',
          },
        ],
      }),
    };

    render(<TransactionsExpandableRow {...defaultProps} row={rowWithoutSymbol} />);

    const row = screen.getByTestId('TransactionsTable-row-0');
    fireEvent.click(row);

    expect(screen.getByTestId('TransactionsTable-row-expanded')).toBeInTheDocument();
  });

  it('handles invalid JSON in expendable data gracefully', () => {
    const rowWithInvalidJSON: DataTableRow = {
      ...mockRow,
      itemisations: 'invalid-json-data',
    };

    render(<TransactionsExpandableRow {...defaultProps} row={rowWithInvalidJSON} />);

    const row = screen.getByTestId('TransactionsTable-row-0');
    fireEvent.click(row);

    const expandedRow = screen.getByTestId('TransactionsTable-row-expanded');
    expect(expandedRow).toHaveTextContent('invalid-json-data');
  });

  it('handles non-itemisations type data', () => {
    const rowWithOtherData: DataTableRow = {
      ...mockRow,
      itemisations: JSON.stringify({
        type: 'other',
        data: 'some other data',
      }),
    };

    render(<TransactionsExpandableRow {...defaultProps} row={rowWithOtherData} />);

    const row = screen.getByTestId('TransactionsTable-row-0');
    fireEvent.click(row);

    const expandedRow = screen.getByTestId('TransactionsTable-row-expanded');
    expect(expandedRow).toBeInTheDocument();
  });

  it('renders all columns in the main row', () => {
    render(<TransactionsExpandableRow {...defaultProps} />);

    expect(screen.getByTestId('TransactionsTable-row-date-0')).toBeInTheDocument();
    expect(screen.getByTestId('TransactionsTable-row-location-0')).toBeInTheDocument();
    expect(screen.getByTestId('TransactionsTable-row-grossValue-0')).toBeInTheDocument();
  });

  it('displays guest name when available in itemisations', () => {
    render(<TransactionsExpandableRow {...defaultProps} />);

    const row = screen.getByTestId('TransactionsTable-row-0');
    fireEvent.click(row);

    expect(screen.getByText('(John Doe)')).toBeInTheDocument();
  });

  it('does not display guest name when not available in itemisations', () => {
    render(<TransactionsExpandableRow {...defaultProps} />);

    const row = screen.getByTestId('TransactionsTable-row-0');
    fireEvent.click(row);

    const expandedRow = screen.getByTestId('TransactionsTable-row-expanded');
    expect(expandedRow).toHaveTextContent('Breakfast');

    expect(screen.queryAllByText(/^\(.*\)$/)).toHaveLength(1);
  });

  it('applies cursor-pointer and hover styles to main row', () => {
    render(<TransactionsExpandableRow {...defaultProps} />);

    const row = screen.getByTestId('TransactionsTable-row-0');
    expect(row).toHaveClass('cursor-pointer');
    expect(row).toHaveClass('hover:bg-gray-50');
  });
});
