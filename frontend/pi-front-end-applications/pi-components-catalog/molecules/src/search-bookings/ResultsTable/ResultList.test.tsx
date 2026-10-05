import { QueryClient } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { render, fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import ResultList from './ResultList.component';

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: () => <div />,
}));

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    router: {
      locale: 'en',
    },
  }),
}));

const mockHeaderTitles = [
  {
    id: 'BookedFor',
    text: 'bookedFor',
  },
  {
    id: 'BookedBy',
    text: 'bookedBy',
  },
];

const mockRows = [{ bookingReference: 'AKU1160178', cells: [] }];
const mockQueryClient = new QueryClient();

const baseDataTestId = 'SearchBookingsPage';
const props = {
  baseDataTestId,
  t: (key: string) => {
    switch (key) {
      case 'ccui.manageBooking.resultList.bic.operaErrorMessage':
        return 'BIC error';
      case 'ccui.manageBooking.resultList.bic.bartErrorMessage':
        return 'BIC error';
      case 'dashboard.bookings.bookingReference':
        return 'Booking Reference';
      default:
        return 'default';
    }
  },
  rows: mockRows,
  headerTitles: mockHeaderTitles,
  bartCard: jest.fn(),
  operaCard: jest.fn(),
  queryClient: mockQueryClient,
  changePage: jest.fn(),
  isSuccess: true,
  hasMore: false,
  limitExceeded: false,
  language: 'gb',
};

describe('Table List', () => {
  it('should render the component', () => {
    const { getByTestId } = render(<ResultList {...props} />);
    expect(getByTestId('SearchBookingsPage-Table-Container')).toBeInTheDocument();
    expect(getByTestId('SearchBookingsPage-TableHeader-BookedFor')).toBeInTheDocument();
    expect(getByTestId('SearchBookingsPage-TableHeader-BookedBy')).toBeInTheDocument();
  });
});

describe('ResultList with isBookingHistoryRedesignCCUIEnabled featureToggle ', () => {
  it('does not set header height to 4rem when isBookingHistoryRedesignCCUIEnabled is true', () => {
    const { container } = render(
      <ResultList {...props} isBookingHistoryRedesignCCUIEnabled={true} />
    );
    const thead = container.querySelector('thead');
    // Check that height is not set to 4rem
    expect(thead).not.toHaveStyle('height: 4rem');
  });
  const rows = [
    {
      bookingReference: 'REF123',
      cells: [
        { id: 'Hotel', value: 'Test Hotel' },
        { id: 'BookedFor', value: 'John Doe' },
        { id: 'BookedBy', value: 'Jane Doe' },
        { id: 'Date', value: '2023-09-13' },
        { id: 'Status', value: 'Upcoming', label: 'Upcoming' },
      ],
      rowNr: 1,
    },
  ];
  const propsWithRows = { ...props, rows };

  it('renders hotel cell with redesign styles and booking reference', () => {
    const { getByTestId } = render(
      <ResultList {...propsWithRows} isBookingHistoryRedesignCCUIEnabled={true} />
    );
    const hotelCell = getByTestId('SearchBookingsPage-Table-Cell-Hotel-0');
    expect(hotelCell).toBeInTheDocument();
    expect(hotelCell.querySelector('.hotel-name')).toHaveTextContent('Test Hotel');
    expect(hotelCell.querySelector('.booking-reference')).toHaveTextContent(
      'Booking Reference REF123'
    );
  });

  it('renders status cell with redesign styles and expand/collapse icon', () => {
    const { getByTestId, container } = render(
      <ResultList {...propsWithRows} isBookingHistoryRedesignCCUIEnabled={true} />
    );
    const statusCell = getByTestId('SearchBookingsPage-Table-Cell-Status-0');
    expect(statusCell).toBeInTheDocument();
    expect(statusCell.querySelector('.booking-status')).toHaveTextContent('Upcoming');
    // Should show ChevronDown initially (as opened by default for first row)
    expect(container.querySelector('svg')).toBeInTheDocument();
    expect(getByTestId('SearchBookingsPage-chevron-up-0')).toBeInTheDocument();
  });

  it('toggles expanded state and renders card when clicked', async () => {
    const { queryByTestId, getByTestId } = render(
      <ResultList {...propsWithRows} isBookingHistoryRedesignCCUIEnabled={true} />
    );

    // Before click, ChevronDown should be present, ChevronUp should not
    expect(getByTestId('SearchBookingsPage-chevron-up-0')).toBeInTheDocument();
    expect(queryByTestId('SearchBookingsPage-chevron-down-0')).not.toBeInTheDocument();

    // Click the ChevronDown icon to collapse (as opened by default for first row)
    const chevronUp = queryByTestId('SearchBookingsPage-chevron-up-0');
    if (chevronUp) {
      await userEvent.click(chevronUp);
    }

    // After click, ChevronDown should be present, ChevronUp should not
    expect(getByTestId('SearchBookingsPage-chevron-down-0')).toBeInTheDocument();
    expect(queryByTestId('SearchBookingsPage-chevron-up-0')).not.toBeInTheDocument();
  });

  it('renders expand/collapse cell with Link and Chevron icons when redesign is disabled', async () => {
    const { getByTestId } = render(
      <ResultList {...propsWithRows} isBookingHistoryRedesignCCUIEnabled={false} />
    );
    // The expand/collapse cell should be present
    const link = getByTestId('hdp_basketHideBreakdownLink');
    expect(link).toBeInTheDocument();
    // Should show ChevronUp initially (as first row card expanded by default)
    expect(link.querySelector('svg')).toBeInTheDocument();
    expect(link).toHaveTextContent('Close');
    // Click to expand
    await userEvent.click(link);
    // Should show ChevronDown after click
    expect(link).toHaveTextContent('Open');
    expect(link.querySelector('svg')).toBeInTheDocument();
  });

  const statusCases = [
    ['Upcoming', 'upcoming'],
    ['Cancelled', 'cancelled'],
    ['Past', 'past'],
    ['CheckedIn', 'checkedin'],
  ];

  it.each(statusCases)('applies correct class for "%s" status', (statusValue, expectedClass) => {
    const rows = [
      {
        bookingReference: 'REF123',
        cells: [
          { id: 'Hotel', value: 'Test Hotel' },
          { id: 'BookedFor', value: 'John Doe' },
          { id: 'BookedBy', value: 'Jane Doe' },
          { id: 'Date', value: '2023-09-13' },
          { id: 'Status', value: statusValue, label: statusValue },
        ],
        rowNr: 0,
      },
    ];
    const propsWithRows = { ...props, rows };
    const { getByTestId } = render(
      <ResultList {...propsWithRows} isBookingHistoryRedesignCCUIEnabled={true} />
    );
    const statusCell = getByTestId('SearchBookingsPage-Table-Cell-Status-0');
    const statusSpan = statusCell.querySelector('.booking-status');
    expect(statusSpan).toHaveClass(`booking-status--${expectedClass}`);
  });

  it('does not render status <Text> when value is empty', () => {
    const rows = [
      {
        bookingReference: 'REF123',
        cells: [
          { id: 'Hotel', value: 'Test Hotel' },
          { id: 'BookedFor', value: 'John Doe' },
          { id: 'BookedBy', value: 'Jane Doe' },
          { id: 'Date', value: '2023-09-13' },
          { id: 'Status', value: '', label: '' },
        ],
        rowNr: 0,
      },
    ];
    const propsWithRows = { ...props, rows };
    const { getByTestId } = render(
      <ResultList {...propsWithRows} isBookingHistoryRedesignCCUIEnabled={true} />
    );
    const statusCell = getByTestId('SearchBookingsPage-Table-Cell-Status-0');
    expect(statusCell.querySelector('.booking-status')).toBeNull();
  });
});

describe('ResultRow keyboard accessibility', () => {
  const rows = [
    {
      bookingReference: 'REF123',
      cells: [
        { id: 'Hotel', value: 'Test Hotel' },
        { id: 'BookedFor', value: 'John Doe' },
        { id: 'BookedBy', value: 'Jane Doe' },
        { id: 'Date', value: '2023-09-13' },
        { id: 'Status', value: 'Upcoming', label: 'Upcoming' },
      ],
      rowNr: 1,
    },
  ];
  const propsWithRows = { ...props, rows };
  it('expands/collapses row with Enter and Space when redesign is enabled', () => {
    const { getByTestId, queryByTestId } = render(
      <ResultList {...propsWithRows} isBookingHistoryRedesignCCUIEnabled={true} />
    );
    const row = getByTestId('SearchBookingsPage-Table-Row-0');

    // Before click, ChevronDown should be present, ChevronUp should not
    expect(getByTestId('SearchBookingsPage-chevron-up-0')).toBeInTheDocument();
    expect(queryByTestId('SearchBookingsPage-chevron-down-0')).not.toBeInTheDocument();

    // Press Enter to expand
    fireEvent.keyDown(row, { key: 'Enter', code: 'Enter', charCode: 13 });

    // After keypress on row, ChevronDown should be present, ChevronUp should not
    expect(getByTestId('SearchBookingsPage-chevron-down-0')).toBeInTheDocument();
    expect(queryByTestId('SearchBookingsPage-chevron-up-0')).not.toBeInTheDocument();

    // Press Space to collapse
    fireEvent.keyDown(row, { key: ' ', code: 'Space', charCode: 32 });
    // ChevronUp once again should be present, ChevronDown should not
    expect(getByTestId('SearchBookingsPage-chevron-up-0')).toBeInTheDocument();
    expect(queryByTestId('SearchBookingsPage-chevron-down-0')).not.toBeInTheDocument();
  });
});
