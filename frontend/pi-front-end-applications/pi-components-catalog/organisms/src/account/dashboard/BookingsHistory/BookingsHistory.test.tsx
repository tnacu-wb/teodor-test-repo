import { fireEvent, waitFor } from '@testing-library/react';
import { Area, BOOKING_CHANNEL, BOOKING_SUBCHANNEL, Channel } from '@whitbread-eos/api';

import { render } from '../../../utils/test-utils';
import BookingsHistory from './BookingsHistory';
import { mockMultipleBookings } from './mockData';

const mockBookingHistoryInfoCardContainer = jest.fn(() => (
  <div data-testid="BookingInfoCardContainer" />
));

jest.mock('../BookingInfoCard', () => ({
  BookingHistoryInfoCardContainer: (props: unknown) =>
    mockBookingHistoryInfoCardContainer(props as Record<string, unknown>),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),

  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
}));

const mockProps = {
  screenSize: {
    isLessThanMobile: false,
    isLessThanXs: false,
    isLessThanSm: false,
    isLessThanMd: false,
    isLessThanLg: false,
    isLessThanXl: true,
  },
  onFetch: jest.fn(),
  pageSize: 10,
  tableConfig: [],
  bookingChannel: {
    channel: BOOKING_CHANNEL.PI as unknown as Channel,
    subchannel: BOOKING_SUBCHANNEL.WEB,
    language: 'EN',
  },
  area: Area.PI,
  isBookingHistoryRedesignPIAndBBEnabled: false,
};

const mockBookings = [
  {
    arrivalDate: '2023-03-16',
    bookedBy: null,
    leadGuest: 'Mr John Doe',
    bookingReference: 'AFZR474300',
    bookingStatus: 'FUTURE',
    hotelName: 'Manchester Central',
    sourceSystem: 'BART',
    noOfNights: '2',
    totalCost: {
      amount: 196,
      currency: 'GBP',
    },
  },
];
const mockUseRouter = jest.fn();

jest.mock('next/router', () => ({
  useRouter() {
    return {
      push: jest.fn(),
      events: {
        on: jest.fn(),
        off: jest.fn(),
      },
      beforePopState: jest.fn(() => null),
      prefetch: jest.fn(() => null),
    };
  },
}));

describe('BookingsHistory', () => {
  beforeEach(() => {
    mockBookingHistoryInfoCardContainer.mockClear();
    mockUseRouter.mockReturnValue({
      locale: 'en',
      query: { language: 'en', country: 'gb', viewType: '2', SORT: '1' },
      push: jest.fn(),
    });
  });

  it('should not display no data message', async () => {
    mockProps.onFetch.mockReturnValue({
      bookingHistory: {
        continuationToken: undefined,
        pageIndex: 1,
        pageSize: 10,
        totalSize: 0,
        totals: {
          cancelled: 0,
          checkedIn: 0,
          past: 0,
          upcoming: 0,
        },
        bookings: [],
      },
    });
    const { getByTestId } = render(<BookingsHistory {...mockProps} />);

    await waitFor(() => {
      expect(getByTestId('AlertDescription')).toBeInTheDocument();
    });
  });

  it('should not display no data message', async () => {
    mockProps.onFetch.mockReturnValueOnce({
      bookingHistory: {
        continuationToken: undefined,
        pageIndex: 1,
        pageSize: 10,
        totalSize: 11,
        totals: {
          checkedIn: 0,
          past: 0,
          upcoming: 11,
        },
        bookings: mockBookings,
      },
    });

    const { getByTestId, queryByTestId } = render(
      <BookingsHistory {...mockProps} isBookingHistoryRedesignPIAndBBEnabled />
    );

    await waitFor(() => {
      expect(queryByTestId('AlertDescription')).not.toBeInTheDocument();
      expect(getByTestId('MyDashboard-findButton')).toBeInTheDocument();
      expect(getByTestId('MyDashboard-Table-Container')).toBeInTheDocument();
    });
  });

  it('should display no results notification when a filter is selected but getBookingHistory results array is empty', async () => {
    mockProps.onFetch
      .mockReturnValueOnce({
        bookingHistory: {
          continuationToken: undefined,
          pageIndex: 1,
          pageSize: 10,
          totalSize: 11,
          totals: {
            cancelled: 0,
            checkedIn: 0,
            past: 0,
            upcoming: 11,
          },
          bookings: mockMultipleBookings,
        },
      })
      .mockReturnValueOnce({
        bookingHistory: {
          continuationToken: undefined,
          pageIndex: 1,
          pageSize: 10,
          totalSize: 11,
          totals: {
            cancelled: 0,
            checkedIn: 0,
            past: 0,
            upcoming: 11,
          },
          bookings: [],
        },
      });

    const { getByTestId, findByTestId } = render(<BookingsHistory {...mockProps} />);

    fireEvent.change(await findByTestId('input-bookingFilter'), { target: { value: 'Test' } });

    fireEvent.click(await getByTestId('MyDashboard-findButton'));

    expect(await findByTestId('input-bookingFilter')).toBeInTheDocument();
  });

  it('should call onClear after a search was made', async () => {
    mockProps.onFetch.mockReturnValueOnce({
      bookingHistory: {
        continuationToken: undefined,
        pageIndex: 1,
        pageSize: 10,
        totalSize: 11,
        totals: {
          cancelled: 0,
          checkedIn: 0,
          past: 0,
          upcoming: 11,
        },
        bookings: mockMultipleBookings,
      },
    });

    const { getByTestId } = render(<BookingsHistory {...mockProps} />);

    await waitFor(() => {
      fireEvent.change(getByTestId('input-bookingFilter'), { target: { value: 'Test' } });
    });
    await waitFor(() => {
      fireEvent.click(getByTestId('MyDashboard-findButton'));
    });
    await waitFor(() => {
      fireEvent.click(getByTestId('MyDashboard-clearButton'));
    });
    await waitFor(() => {
      expect(mockProps.onFetch).toBeCalledWith({
        pageIndex: 1,
        filterType: '',
        filterValue: '',
        continuationToken: undefined,
      });
    });
  });
  it('should call load more and match the nr of rows with the number of bookings', async () => {
    mockProps.onFetch
      .mockReturnValueOnce({
        bookingHistory: {
          continuationToken: undefined,
          pageIndex: 1,
          pageSize: 10,
          totalSize: 11,
          totals: {
            cancelled: 0,
            checkedIn: 0,
            past: 0,
            upcoming: 11,
          },
          bookings: mockMultipleBookings,
        },
      })
      .mockReturnValueOnce({
        bookingHistory: {
          continuationToken: undefined,
          pageIndex: 2,
          pageSize: 10,
          totalSize: 1,
          totals: {
            cancelled: 0,
            checkedIn: 0,
            past: 0,
            upcoming: 12,
          },
          bookings: mockBookings,
        },
      });
    const { container, getByTestId } = render(<BookingsHistory {...mockProps} />);

    await waitFor(() => {
      fireEvent.click(getByTestId('MyDashboard-Table-LoadMore'));
    });

    await waitFor(() => {
      expect(container.querySelectorAll('[data-testid^="MyDashboard-Table-Row-"]').length).toBe(11);
    });
  });
  it('should display Table component and Filters when booking history request is done', async () => {
    mockProps.onFetch.mockReturnValueOnce({
      bookingHistory: {
        continuationToken: null,
        pageIndex: 1,
        pageSize: 10,
        totalSize: 11,
        totals: {
          cancelled: 0,
          checkedIn: 0,
          past: 0,
          upcoming: 11,
        },
        bookings: mockBookings,
      },
    });

    const { queryByTestId } = render(<BookingsHistory {...mockProps} />);
    waitFor(async () => {
      expect(queryByTestId('MyDashboard-findButton')).toBeInTheDocument();
      expect(queryByTestId('MyDashboard-Table-Container')).toBeInTheDocument();
    });
  });

  it('should pass booking data to card container without room type artifacts in parent', async () => {
    mockProps.onFetch.mockReturnValueOnce({
      bookingHistory: {
        continuationToken: undefined,
        pageIndex: 1,
        pageSize: 10,
        totalSize: 1,
        totals: {
          cancelled: 0,
          checkedIn: 0,
          past: 0,
          upcoming: 1,
        },
        bookings: mockBookings,
      },
    });

    render(<BookingsHistory {...mockProps} />);
    await waitFor(() => {
      expect(mockBookingHistoryInfoCardContainer).toHaveBeenCalled();
    });

    const cardProps = mockBookingHistoryInfoCardContainer.mock.calls[0][0] as Record<
      string,
      unknown
    >;
    expect(cardProps.bookingReference).toBe('AFZR474300');
    expect(cardProps).not.toHaveProperty('roomTypeLabels');
  });

  it('should display notification message', async () => {
    const { getByText } = render(<BookingsHistory {...mockProps} />);
    waitFor(() => {
      expect(getByText('dashboard.bookings.searchNoResults')).toBeInTheDocument();
    });
  });

  it('should render BookingInfoCard', async () => {
    const { getByTestId } = render(<BookingsHistory {...mockProps} />);
    waitFor(() => {
      expect(getByTestId('BookingInfoCardContainer')).toBeInTheDocument();
    });
  });
});
