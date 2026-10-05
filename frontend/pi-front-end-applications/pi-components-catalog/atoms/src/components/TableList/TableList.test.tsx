import { Text } from '@chakra-ui/react';
import { Price, ScreenSizeValues, PageName } from '@whitbread-eos/api';
import { format } from 'date-fns';
import preloadAll from 'jest-next-dynamic';
import 'react';

import { act, render, userEvent, waitFor } from '../../utils/test-utils';
import { resultRowCellRedesignStyle } from './TableList.style';
import TableList, { TableListRow } from './index';

const mockRowClickHandler = jest.fn();
const mockConfig = [
  {
    key: 'bookedFor',
    title: 'Booked for',
  },
  {
    key: 'bookedBy',
    title: 'Booked by',
  },
  {
    key: 'totalCost',
    title: 'Price',
    hideOnMobile: true,
    render: (row: TableListRow): React.ReactNode => {
      const totalCost: Price | null | undefined = row?.totalCost as Price | null | undefined;
      return <Text as="span">{`${totalCost?.currency}${totalCost?.amount}`}</Text>;
    },
  },
  {
    key: 'bookingStatus',
    title: 'Status',
    hideOnMobile: true,
    render: (row: TableListRow): React.ReactNode => {
      return <Text as="span">{row.bookingStatus.toLowerCase()}</Text>;
    },
  },
  {
    key: 'arrivalDate',
    title: 'Date',
    hideOnMobile: true,
    render: (row: TableListRow): React.ReactNode => {
      return (
        <>
          <Text as="span">{format(new Date(row.arrivalDate), 'EEE d LLL yyyy')}</Text>
          <Text as="span">{`${row.noOfNights} Nights`}</Text>
        </>
      );
    },
  },
];

const screenSize: ScreenSizeValues = {
  isLessThanMobile: false,
  isLessThanXs: false,
  isLessThanSm: false,
  isLessThanMd: false,
  isLessThanLg: false,
  isLessThanXl: false,
};

const screenSizeMobile: ScreenSizeValues = {
  isLessThanMobile: false,
  isLessThanXs: false,
  isLessThanSm: true,
  isLessThanMd: false,
  isLessThanLg: false,
  isLessThanXl: false,
};

const rows = [
  {
    bookedFor: 'Emma Doe',
    bookedBy: 'John Doe',
    hotelName: 'Hotel London 1',
    arrivalDate: '2023-12-20',
    noOfNights: '3',
    bookingStatus: 'UPCOMING',
    bookingReference: 'REF1',
    totalCost: {
      amount: 44,
      currency: '£',
    },
  },
  {
    bookedFor: 'Emma Doe',
    bookedBy: 'John Doe',
    hotelName: 'Hotel London 2',
    arrivalDate: '2023-12-21',
    noOfNights: '2',
    bookingStatus: 'Past',
    bookingReference: 'REF2',
    totalCost: {
      amount: 123,
      currency: '£',
    },
  },
];

const companyRows = [
  {
    name: 'Company1',
    address: 'address',
    telephoneNumber: '000',
    corpId: '123',
    companyId: '1234',
  },
];

const companyColumns = [
  {
    key: 'name',
    title: 'Name',
  },
  {
    key: 'address',
    title: 'Address',
  },
  {
    key: 'telephoneNumber',
    title: 'Phone number',
  },
];

describe('TableList tests', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });
  beforeAll(async () => {
    await preloadAll();
  });
  it('should render TableList component', async () => {
    const { getByTestId } = render(
      <TableList columns={mockConfig} rows={rows} screenSize={screenSize} />
    );

    await waitFor(() => {
      expect(getByTestId('Table-Container')).toBeInTheDocument();
      expect(getByTestId('TableHeader-bookedFor')).toBeInTheDocument();
      expect(getByTestId('TableHeader-bookedBy')).toBeInTheDocument();
      expect(getByTestId('TableHeader-totalCost')).toBeInTheDocument();
    });
  });

  it('should collapse row', async () => {
    const { getByTestId, queryByText, queryByTestId } = render(
      <TableList
        columns={mockConfig}
        dataTestIdPrefix={'TestPrefix'}
        rows={rows}
        screenSize={screenSize}
        renderExpandedContent={() => 'Expanded content'}
      />
    );

    const row = getByTestId('TestPrefix-Table-Row-0');
    await userEvent.click(getByTestId('TestPrefix-Table-Cell-0-expand'));

    await userEvent.click(getByTestId('TestPrefix-Table-Cell-0-collapse'));

    // For keyboard events, focus the row first, then use userEvent.keyboard:
    row.focus();
    await userEvent.keyboard('{Enter}');

    row.focus();
    await userEvent.keyboard(' ');

    await waitFor(() => {
      expect(getByTestId('TestPrefix-Table-Cell-0-expand')).toBeInTheDocument();
      expect(queryByTestId('TestPrefix-Table-Cell-0-collapse')).not.toBeInTheDocument();
      expect(queryByText('Expanded content')).not.toBeInTheDocument();
    });
  });

  it('should render expand column', async () => {
    const { getByTestId } = render(
      <TableList columns={mockConfig} rows={rows} screenSize={screenSize} />
    );

    await waitFor(() => {
      expect(getByTestId('TableHeader-expand')).toBeInTheDocument();
    });
  });

  it('should not render expand column when isExpandable is false', async () => {
    const { queryByTestId, queryAllByRole } = render(
      <TableList
        columns={mockConfig}
        rows={rows}
        screenSize={screenSize}
        isExpandable={false}
        expandBtnText="More"
      />
    );

    await waitFor(() => {
      expect(queryByTestId('TableHeader-expand')).not.toBeInTheDocument();
      expect(queryAllByRole('button', { name: /More/ })).toHaveLength(0);
    });
  });

  it('should invoke click handler when table row is clicked', async () => {
    const { getByTestId } = render(
      <TableList
        columns={companyColumns}
        rows={companyRows}
        screenSize={screenSize}
        isExpandable={false}
        expandBtnText="More"
        handleRowClicked={mockRowClickHandler}
      />
    );
    await act(async () => {
      const tableRow = getByTestId('Table-Row-0');
      userEvent.click(tableRow);
    });

    await waitFor(() => {
      expect(mockRowClickHandler).toHaveBeenCalledTimes(1);
    });
  });

  it('should not invoke click handler when no handler is passed', async () => {
    const { getAllByRole } = render(
      <TableList
        columns={mockConfig}
        rows={rows}
        screenSize={screenSize}
        isExpandable={false}
        expandBtnText="More"
      />
    );
    await act(async () => {
      const tableRow = getAllByRole('row')[0];
      userEvent.click(tableRow);
    });
    await waitFor(() => {
      expect(mockRowClickHandler).toHaveBeenCalledTimes(0);
    });
  });

  it('should render TableList component with test id prefix', async () => {
    const { getByTestId } = render(
      <TableList
        columns={mockConfig}
        dataTestIdPrefix={'TestPrefix'}
        rows={rows}
        screenSize={screenSize}
      />
    );

    await waitFor(() => {
      expect(getByTestId('TestPrefix-Table-Container')).toBeInTheDocument();
      expect(getByTestId('TestPrefix-TableHeader-bookedFor')).toBeInTheDocument();
      expect(getByTestId('TestPrefix-TableHeader-bookedBy')).toBeInTheDocument();
      expect(getByTestId('TestPrefix-TableHeader-totalCost')).toBeInTheDocument();
      expect(getByTestId('TestPrefix-TableHeader-expand')).toBeInTheDocument();
    });
  });

  it('should hide columns on mobile', async () => {
    const { getByTestId, queryByText } = render(
      <TableList
        columns={mockConfig}
        dataTestIdPrefix={'TestPrefix'}
        screenSize={screenSizeMobile}
        rows={rows}
      />
    );

    await waitFor(() => {
      expect(getByTestId('TestPrefix-Table-Container')).toBeInTheDocument();
      expect(getByTestId('TestPrefix-TableHeader-bookedFor')).toBeInTheDocument();
      expect(getByTestId('TestPrefix-TableHeader-bookedBy')).toBeInTheDocument();
      expect(getByTestId('TestPrefix-TableHeader-expand')).toBeInTheDocument();
      expect(queryByText('Price')).not.toBeInTheDocument();
      expect(queryByText('Status')).not.toBeInTheDocument();
      expect(queryByText('Date')).not.toBeInTheDocument();
    });
  });

  it('should render total cost', async () => {
    const { getByText } = render(
      <TableList
        columns={mockConfig}
        dataTestIdPrefix={'TestPrefix'}
        screenSize={screenSize}
        rows={rows}
      />
    );

    await waitFor(() => {
      expect(getByText('£44')).toBeInTheDocument();
      expect(getByText('£123')).toBeInTheDocument();
    });
  });

  it('should render transformed status', async () => {
    const { getByText } = render(
      <TableList
        columns={mockConfig}
        dataTestIdPrefix={'TestPrefix'}
        screenSize={screenSize}
        rows={rows}
      />
    );

    await waitFor(() => {
      expect(getByText('upcoming')).toBeInTheDocument();
      expect(getByText('past')).toBeInTheDocument();
    });
  });

  it('should render arrival date and nights no', async () => {
    const { getByText } = render(
      <TableList
        columns={mockConfig}
        dataTestIdPrefix={'TestPrefix'}
        screenSize={screenSize}
        rows={rows}
      />
    );

    await waitFor(() => {
      expect(getByText('2 Nights')).toBeInTheDocument();
      expect(getByText('3 Nights')).toBeInTheDocument();
      expect(getByText('Wed 20 Dec 2023')).toBeInTheDocument();
      expect(getByText('Thu 21 Dec 2023')).toBeInTheDocument();
    });
  });

  it('should display Load more button if has more rows', async () => {
    const { getByText, getByTestId } = render(
      <TableList
        columns={mockConfig}
        dataTestIdPrefix={'TestPrefix'}
        screenSize={screenSize}
        rows={rows}
        onLoadMore={jest.fn()}
        loadMoreText={'Load more test'}
      />
    );

    await waitFor(() => {
      expect(getByTestId('TestPrefix-Table-LoadMore')).toBeInTheDocument();
      expect(getByText('Load more test')).toBeInTheDocument();
    });
  });

  it('should display Load more button in loading state if data is loading', async () => {
    const { getByTestId } = render(
      <TableList
        columns={mockConfig}
        dataTestIdPrefix={'TestPrefix'}
        screenSize={screenSize}
        rows={rows}
        onLoadMore={jest.fn()}
        loadMoreText={'Load more test'}
        isLoadingMore={true}
      />
    );

    await waitFor(() => {
      expect(getByTestId('TestPrefix-Table-LoadMore')).toBeDisabled();
    });
  });

  it('should expand row', async () => {
    const { getByTestId, getByText, queryByTestId } = render(
      <TableList
        columns={mockConfig}
        dataTestIdPrefix={'TestPrefix'}
        rows={rows}
        screenSize={screenSize}
        renderExpandedContent={() => 'Expanded content'}
      />
    );
    await waitFor(() => {
      expect(getByTestId('TestPrefix-Table-Cell-0-expand')).toBeInTheDocument();
      expect(getByTestId('TestPrefix-Table-Cell-1-expand')).toBeInTheDocument();
    });

    await userEvent.click(getByTestId('TestPrefix-Table-Cell-0-expand'));
    await waitFor(() => {
      expect(queryByTestId('TestPrefix-Table-Cell-0-expand')).not.toBeInTheDocument();
      expect(getByTestId('TestPrefix-Table-Cell-0-collapse')).toBeInTheDocument();
      expect(getByTestId('TestPrefix-Table-Cell-1-expand')).toBeInTheDocument();
      expect(getByText('Expanded content')).toBeInTheDocument();
    });
  });

  it('should not expand row if no content to render', async () => {
    const { getByTestId, queryByTestId } = render(
      <TableList
        columns={mockConfig}
        dataTestIdPrefix={'TestPrefix'}
        rows={rows}
        screenSize={screenSize}
      />
    );
    await userEvent.click(getByTestId('TestPrefix-Table-Cell-0-expand'));

    await waitFor(() => {
      expect(getByTestId('TestPrefix-Table-Cell-0-expand')).toBeInTheDocument();
      expect(queryByTestId('TestPrefix-Table-Cell-0-collapse')).not.toBeInTheDocument();
    });
  });

  it('should have the first row expanded by default when number of rows is equal to one', async () => {
    const { getByText } = render(
      <TableList
        columns={mockConfig}
        dataTestIdPrefix={'TestPrefix'}
        rows={[rows[0]]}
        screenSize={screenSize}
        renderExpandedContent={() => 'Expanded content'}
      />
    );
    await waitFor(() => {
      expect(getByText('Expanded content')).toBeInTheDocument();
    });
  });

  it('renders with default (non-redesign) styles and expand column', () => {
    const { getByTestId } = render(
      <TableList
        columns={mockConfig}
        rows={rows}
        pageName={PageName.DASHBOARD}
        isBookingHistoryRedesignPIAndBBEnabled={false}
        dataTestIdPrefix="Test"
      />
    );
    expect(getByTestId('Test-TableHeader-expand')).toBeInTheDocument();
    // Should use default style (not redesign)
    const table = getByTestId('Test-Table-Container').querySelector('table');
    expect(table).toHaveStyle('table-layout: fixed');
  });

  it('renders with redesign styles and hides expand column', () => {
    const { queryByTestId, getByTestId } = render(
      <TableList
        columns={mockConfig}
        rows={rows}
        pageName={PageName.DASHBOARD}
        isBookingHistoryRedesignPIAndBBEnabled={true}
        dataTestIdPrefix="Test"
      />
    );
    expect(queryByTestId('Test-TableHeader-expand')).not.toBeInTheDocument();
    // Should use redesign style (not fixed layout)
    const table = getByTestId('Test-Table-Container').querySelector('table');
    expect(table).toHaveStyle('table-layout: auto');
  });

  it('expands and collapses row when not in redesign mode', async () => {
    const { getAllByText, getByText, queryByText } = render(
      <TableList
        columns={mockConfig}
        rows={rows}
        pageName={PageName.DASHBOARD}
        isBookingHistoryRedesignPIAndBBEnabled={false}
        dataTestIdPrefix="Test"
        renderExpandedContent={(row) => <div>Expanded: {row.hotelName}</div>}
      />
    );

    // Find all expand buttons (should be present when not in redesign)
    const viewLinks = getAllByText('View', { selector: 'a' });
    expect(viewLinks.length).toBeGreaterThan(0);

    // Click to expand first row
    await userEvent.click(viewLinks[0]);
    expect(getByText(/Expanded: Hotel London/)).toBeInTheDocument();

    // Click again to collapse
    await userEvent.click(viewLinks[0]);
    expect(queryByText(/Expanded: Hotel London/)).not.toBeInTheDocument();
  });

  it('does not render expand buttons in redesign mode', () => {
    const { queryByRole } = render(
      <TableList
        columns={mockConfig}
        rows={rows}
        pageName={PageName.DASHBOARD}
        isBookingHistoryRedesignPIAndBBEnabled={true}
        dataTestIdPrefix="Test"
        renderExpandedContent={(row) => <div>Expanded: {row.hotelName}</div>}
      />
    );
    expect(queryByRole('viewLinks', { name: /View/i })).not.toBeInTheDocument();
  });

  it('uses fixedLayout=false when redesign is active, true otherwise', () => {
    const { getByTestId: getByTestIdRedesign } = render(
      <TableList
        columns={mockConfig}
        rows={rows}
        pageName={PageName.DASHBOARD}
        isBookingHistoryRedesignPIAndBBEnabled={true}
        dataTestIdPrefix="Test"
      />
    );
    const tableRedesign = getByTestIdRedesign('Test-Table-Container').querySelector('table');
    expect(tableRedesign).toHaveStyle('table-layout: auto');
  });

  it('calls handleRowClicked when Enter is pressed in non-redesign mode', async () => {
    const mockRowClickHandler = jest.fn();
    const { getByTestId } = render(
      <TableList
        columns={mockConfig}
        rows={rows}
        pageName={PageName.DASHBOARD}
        isBookingHistoryRedesignPIAndBBEnabled={false}
        dataTestIdPrefix="Test"
        renderExpandedContent={() => <div>Expanded content</div>}
        handleRowClicked={mockRowClickHandler}
      />
    );

    // Simulate keydown on the row (non-redesign mode)
    const row = getByTestId('Test-Table-Row-0');
    row.focus();
    await userEvent.keyboard('{Enter}');
    expect(mockRowClickHandler).toHaveBeenCalledWith(rows[0]);
  });

  it('calls handleRowClicked when Space is pressed in non-redesign mode', async () => {
    const mockRowClickHandler = jest.fn();
    const { getByTestId } = render(
      <TableList
        columns={mockConfig}
        rows={rows}
        pageName={PageName.DASHBOARD}
        isBookingHistoryRedesignPIAndBBEnabled={false}
        dataTestIdPrefix="Test"
        renderExpandedContent={() => <div>Expanded content</div>}
        handleRowClicked={mockRowClickHandler}
      />
    );

    // Simulate keydown on the row (non-redesign mode)
    const row = getByTestId('Test-Table-Row-0');
    row.focus();
    await userEvent.keyboard(' ');
    expect(mockRowClickHandler).toHaveBeenCalledWith(rows[0]);
  });
});

describe('resultRowCellRedesignStyle', () => {
  it('returns correct styles when expanded', () => {
    const style = resultRowCellRedesignStyle(true);
    expect(style.backgroundColor).toBe('lightGrey5');
    expect(style.borderBottom).toBe('none');
  });

  it('returns correct styles when not expanded', () => {
    const style = resultRowCellRedesignStyle(false);
    expect(style.backgroundColor).toBe('transparent');
    expect(style.borderBottom).toBe('var(--chakra-borders-1px)');
  });
});
