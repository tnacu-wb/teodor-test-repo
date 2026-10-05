import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { fireEvent, waitFor } from '@testing-library/react';
import { Area } from '@whitbread-eos/api';

import { render } from '../../../utils/test-utils';
import {
  mockBookingConfirmationAuthenticatedMock,
  mockBookingConfirmationData,
} from '../BookingInfoCard/mockResponse';
import BookingInfoCardHeader from './BookingInfoCardHeader.component';
import BookingInfoCardHeaderContainer from './BookingInfoCardHeader.container';

const mockUserData = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useMutationRequest: () => ({
    mutation: jest.fn(),
  }),
  useUserData: () => mockUserData(),
  getAuthCookie: jest.fn(),
}));

// Mock fetchQuery on QueryClient prototype
beforeAll(() => {
  jest
    .spyOn(ReactQuery.QueryClient.prototype, 'fetchQuery')
    .mockImplementation(async (options: any) => {
      const queryKey = options.queryKey || options;
      const key = Array.isArray(queryKey) ? queryKey[0] : queryKey;

      switch (key) {
        case 'getBookingConfirmation':
          return mockBookingConfirmationData;
        case 'getBookingConfirmationAuthenticated':
          return mockBookingConfirmationAuthenticatedMock;
        case 'GetHotelInformation':
          return { hotelInformation: { brand: 'PI', address: ['St', 'Tower'] } };
        default:
          return {};
      }
    });

  jest.spyOn(ReactQuery.QueryClient.prototype, 'prefetchQuery').mockResolvedValue(undefined as any);
});

afterAll(() => {
  jest.restoreAllMocks();
});

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const mockedProps = {
  area: Area.PI,
  checkInLabel: '3pm - Mon 28 Nov 2022',
  checkOutLabel: '12pm - Tue 29 Nov 2022',
  hotelName: 'Manchester Old Trafford',
  t: (value: string) => value,
  isLoading: false,
  isError: false,
  error: null,
  shouldRenderDashboardButton: false,
  bookingStatus: 'Upcoming',
};

describe('BookingInfoCardHeader - Container', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockUserData.mockReturnValue({ isLoggedIn: false });
  });

  it('should render BookingInfoCardHeaderContainer with default params (PI)', async () => {
    const { getByTestId } = render(
      <BookingInfoCardHeaderContainer
        area={undefined as any}
        basketReference={undefined as any}
        bookingReference={undefined as any}
        shouldRenderDashboardButton={undefined as any}
      />
    );
    expect(getByTestId('BookingInfoCardHeader-Wrapper')).toBeInTheDocument();
    expect(getByTestId('BookingInfoCardHeader-Title')).toBeInTheDocument();
    expect(getByTestId('BookingInfoCardHeader-HotelName')).toBeInTheDocument();
    expect(getByTestId('BookingInfoCardHeader-CheckInDate')).toBeInTheDocument();
    expect(getByTestId('BookingInfoCardHeader-CheckOutDate')).toBeInTheDocument();
  });

  it('should render BookingInfoCardHeaderContainer with area CCUI', async () => {
    const { getByTestId } = render(
      <BookingInfoCardHeaderContainer
        bookingReference="AWM8159458"
        basketReference="AWM-782e1dee-e75e-4f85-9741-c7acf3d31d6c"
        shouldRenderDashboardButton={true}
        area={Area.CCUI}
      />
    );
    await waitFor(() => {
      expect(getByTestId('BookingInfoCardHeader-Wrapper')).toBeInTheDocument();
    });
  });

  it('should render BookingInfoCardHeaderContainer loading', async () => {
    const { getByTestId } = render(
      <BookingInfoCardHeaderContainer
        bookingReference="bookingRef"
        basketReference="basketRef"
        shouldRenderDashboardButton={true}
        area={Area.PI}
      />
    );

    const loadingSpinner = getByTestId('Loading-BookingInfoCardHeader');
    await waitFor(() => {
      expect(loadingSpinner).toBeInTheDocument();
    });
  });
});

describe('BookingInfoCardHeader - Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockUserData.mockReturnValue({ isLoggedIn: false });
  });

  it('renders BookingInfoCardHeader component corectly', () => {
    const { getByTestId } = render(<BookingInfoCardHeader {...mockedProps} />);
    expect(getByTestId('BookingInfoCardHeader-Wrapper')).toBeInTheDocument();
    expect(getByTestId('BookingInfoCardHeader-Title')).toBeInTheDocument();
    expect(getByTestId('BookingInfoCardHeader-HotelName')).toBeInTheDocument();
    expect(getByTestId('BookingInfoCardHeader-CheckInDate')).toBeInTheDocument();
    expect(getByTestId('BookingInfoCardHeader-CheckOutDate')).toBeInTheDocument();
  });

  it('should have the corect format for check in label', async () => {
    const { getByTestId } = render(<BookingInfoCardHeader {...mockedProps} />);
    expect(getByTestId('BookingInfoCardHeader-CheckInDate')).toHaveTextContent(
      '3pm - Mon 28 Nov 2022'
    );
  });

  it('should have the corect format for check out label', async () => {
    const { getByTestId } = render(<BookingInfoCardHeader {...mockedProps} />);
    expect(getByTestId('BookingInfoCardHeader-CheckOutDate')).toHaveTextContent(
      '12pm - Tue 29 Nov 2022'
    );
  });

  it('should render Back to dashboard button when isAmendPage = true (PI)', async () => {
    const amendMockedProps = { ...mockedProps, shouldRenderDashboardButton: true, area: Area.PI };
    const { getByTestId } = render(<BookingInfoCardHeader {...amendMockedProps} />);

    const backBtn = getByTestId('BookingInfoCardHeader-BackToDashboardButton');
    fireEvent.click(backBtn);
    expect(backBtn).toBeInTheDocument();
  });

  it('should render back to dashboard button when isAmend === true and area === ccui', () => {
    const amendMockedProps = {
      ...mockedProps,
      shouldRenderDashboardButton: true,
      area: Area.CCUI,
    };
    const { getByTestId } = render(<BookingInfoCardHeader {...amendMockedProps} />);

    const backBtn = getByTestId('BookingInfoCardHeader-CCUIBackToDashboardButton');
    fireEvent.click(backBtn);
    expect(backBtn).toBeInTheDocument();
  });

  it('should back to homepage button when isAmend === true and area === ccui', () => {
    const amendMockedProps = {
      ...mockedProps,
      shouldRenderDashboardButton: true,
      area: Area.CCUI,
    };
    const { getByTestId } = render(<BookingInfoCardHeader {...amendMockedProps} />);

    const backBtn = getByTestId('BookingInfoCardHeader-CCUIBackToHomepageButton');
    fireEvent.click(backBtn);
    expect(backBtn).toBeInTheDocument();
  });

  it('should render Back to dashboard button when isAmendPage = true (BB)', async () => {
    mockUserData.mockReturnValue({ isLoggedIn: true });

    const amendMockedProps = { ...mockedProps, shouldRenderDashboardButton: true, area: Area.BB };
    const { getByTestId } = render(<BookingInfoCardHeader {...amendMockedProps} />);

    const backBtn = getByTestId('BookingInfoCardHeader-BackToDashboardButton');
    fireEvent.click(backBtn);
    expect(backBtn).toBeInTheDocument();
  });

  it('should show booking status in the BIC header when isBICHeaderBookingStatusEnabled is true', () => {
    const { getByText } = render(
      <BookingInfoCardHeader {...mockedProps} isBICHeaderBookingStatusEnabled={true} />
    );
    expect(getByText('dashboard.bookings.upcoming')).toBeInTheDocument();
  });

  it('should NOT show booking status in the BIC header when isBICHeaderBookingStatusEnabled is false', () => {
    const { queryByText } = render(
      <BookingInfoCardHeader {...mockedProps} isBICHeaderBookingStatusEnabled={false} />
    );
    expect(queryByText('dashboard.bookings.upcoming')).not.toBeInTheDocument();
  });
});
