import { useMediaQuery } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import { waitFor } from '@testing-library/react';
import {
  Area,
  BC_RESERVATION_STATUS,
  BOOKING_TYPE,
  paymentOptions,
  SOURCE_SYSTEM,
  BookingChannelCriteria,
  BASKET_STATUS,
} from '@whitbread-eos/api';

import { render, screen } from '../../../utils/test-utils';
import BookingHistoryInfoCardComponent from './BookingHistoryInfoCard.component';

const mockProps = {
  bookingSurname: 'surname',
  basketReference: 'UUID',
  bookingReference: 'AWM7115824',
  baseDataTestId: 'BookingHistoryDetails',
  area: 'pi' as Area,
  bookingStatus: BC_RESERVATION_STATUS.COMPLETED,
  paymentOption: paymentOptions.PAY_ON_ARRIVAL,
  skipContainerRendering: true,
  hotelId: 'HOTELID',
  onCancelBooking: jest.fn(),
  invoiceSentMsg: {
    displaySentInvoiceMsg: false,
    notificationMessage: 'We have sent you the invoice to your email address.',
  },
  handleResendInvoiceAction: jest.fn(),
  handleDownloadInvoiceAction: jest.fn(),
  handleResendConfirmationAction: jest.fn(),
  bookingChannel: {
    channel: 'PI',
    subchannel: 'WEB',
    language: 'EN',
  } as BookingChannelCriteria,
  sourceSystem: SOURCE_SYSTEM.BART,
};

const mockBookingDetails = {
  shouldDisplayCityTaxMessage: false,
  hotelId: 'MANOLD',
  paymentOption: 'PAY_ON_ARRIVAL',
  currencyCode: 'GBP',
  bookedBy: 'Test Booker',
  totalCost: '4036.96',
  previousTotal: '0',
  balanceOutstanding: '4036.96',
  newTotal: '4036.96',
  donationPkg: {
    code: 'ZCHRY3',
    currency: 'GBP',
    unitPrice: 5,
  },
  rateType: 'FLEXRATE',
  roomDetails: [
    {
      leadGuestName: 'bau bau',
      roomType: 'Family Room',
      roomPrice: 1998,
      adultMealDescription: [
        {
          title: 'Premier Inn Breakfast',
          id: 'BFADBF',
          price: 9.99,
          noSelections: 1,
        },
      ],
      childrenMealDescription: [
        {
          title: 'Free breakfast for kids',
          id: 'BFCHDF',
          noSelections: 1,
        },
      ],
      mealPrice: 0,
      cot: false,
      noAdults: 1,
      noChildren: 1,
      noNights: 2,
    },
    {
      leadGuestName: 'hau hau',
      roomType: 'Double Room',
      roomPrice: 1998,
      adultMealDescription: [
        {
          title: 'Continental Breakfast',
          id: 'BFADCT',
          price: 7.99,
          noSelections: 1,
        },
      ],
      childrenMealDescription: [],
      mealPrice: 0,
      cot: false,
      noAdults: 1,
      noChildren: 0,
      noNights: 2,
    },
  ],
  cancellationInfoResponse: {
    amendable: true,
    cancelable: true,
    ruleCompliant: true,
    aemLabelKey: '',
  },
  bookedFor: 'Test Test',
  arrivalDate: '2022-08-29',
  noNights: 2,
  hotelName: 'London Cathbury hotel',
  dinnerAllowance: {
    amount: 3333,
    currency: 'GBP',
  },
};

const mockResponse = {
  data: {
    hotelInformation: {
      brand: 'pi',
      address: 'Address',
      galleryImages: [
        {
          thumbnailSrc:
            'https://secure2.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/W/WEMPTI/London_Wembley_Stadium002.jpg',
          alt: 'Hotel image',
        },
        {
          thumbnailSrc:
            'https://secure2.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/W/WEMPTI/London_Wembley_Stadium002.jpg',
          alt: 'Hotel image',
        },
      ],
      links: {
        detailsPage: '/england/greater-london/london/hub-london-kings-cross',
      },
      parkingDescription:
        'Chargeable on-site parking is available operating on a first come, first served basis at £8 per 24 hours on non-event days and £20 on event days. Parking is managed by Horizon.',
    },
  },
};

const mockUseQueryRequest = {
  isLoading: false,
  isError: false,
  data: {
    basket: {
      status: BASKET_STATUS.PAY_PENDING,
    },
    bookingInformation: { bookingFlowId: 'booking-a1' },
  },
  error: '',
};

jest.mock('@chakra-ui/react', () => {
  const actual = jest.requireActual('@chakra-ui/react');
  return {
    ...actual,
    useMediaQuery: jest.fn().mockReturnValue([false]), // default non-mobile
  };
});

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    locale: 'gb',
    query: '',
  }),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: (val: string) => val,
  graphQLRequest: jest.fn(),
  shouldDisplaySecureBooking: () => true,
  useQueryRequest: () => mockUseQueryRequest,
  useFeatureToggle: jest.fn().mockReturnValue({
    ['release_pi_bb_non_guaranteed_reminder']: true,
  }),
}));

describe('BookingHistoryInfoCard', () => {
  beforeEach(() => {
    mockProps.invoiceSentMsg.displaySentInvoiceMsg = false;
  });

  it('it should render the BookingInfoCard component and find the booking actions section', async () => {
    const { getByTestId } = render(
      <BookingHistoryInfoCardComponent
        {...mockProps}
        {...mockResponse}
        bookingDetails={mockBookingDetails}
        basketStatus={BOOKING_TYPE.UPCOMING}
      />
    );
    await waitFor(() => {
      expect(getByTestId('BookingActions-Container')).toBeInTheDocument();
    });
  });

  it('it should render the BookingInfoCard component and find the hotel details section', async () => {
    const { getByTestId } = render(
      <BookingHistoryInfoCardComponent
        {...mockProps}
        {...mockResponse}
        bookingDetails={mockBookingDetails}
        basketStatus={BOOKING_TYPE.UPCOMING}
      />
    );
    await waitFor(() => {
      expect(getByTestId('HotelDetails-Container')).toBeInTheDocument();
    });
  });

  it('it should render the BookingInfoCard component and find the booking details section', async () => {
    const { getByTestId } = render(
      <BookingHistoryInfoCardComponent
        {...mockProps}
        {...mockResponse}
        bookingDetails={mockBookingDetails}
        basketStatus={BOOKING_TYPE.UPCOMING}
      />
    );
    await waitFor(() => {
      expect(getByTestId('BookingHistoryDetails-Container')).toBeInTheDocument();
    });
  });

  it('should display the notification message', async () => {
    mockProps.invoiceSentMsg.displaySentInvoiceMsg = true;
    const { getByTestId } = render(
      <BookingHistoryInfoCardComponent
        {...mockProps}
        {...mockResponse}
        bookingDetails={mockBookingDetails}
        basketStatus={BOOKING_TYPE.UPCOMING}
      />
    );
    await waitFor(() => {
      expect(getByTestId('BookingHistoryDetails-Notification-Invoice')).toBeInTheDocument();
    });
  });

  it('should display the download invoice error notification', () => {
    const { getByTestId } = render(
      <BookingHistoryInfoCardComponent
        {...mockProps}
        {...mockResponse}
        bookingDetails={mockBookingDetails}
        basketStatus={BOOKING_TYPE.PAST}
        downloadInvoiceError="Failed to download invoice. Please try again."
      />
    );
    expect(getByTestId('BookingHistoryDetails-Notification-Download-Error')).toBeInTheDocument();
  });

  it('should render booked by from bookingDetails in read only mode', async () => {
    render(
      <BookingHistoryInfoCardComponent
        {...mockProps}
        {...mockResponse}
        bookingDetails={mockBookingDetails}
        basketStatus={BOOKING_TYPE.UPCOMING}
        isReadOnly={true}
        hotelName="London Cathbury hotel"
      />
    );

    await waitFor(() => {
      expect(screen.getByText('Test Booker')).toBeInTheDocument();
    });
  });

  it('should not display the download invoice error notification when there is no error', () => {
    const { queryByTestId } = render(
      <BookingHistoryInfoCardComponent
        {...mockProps}
        {...mockResponse}
        bookingDetails={mockBookingDetails}
        basketStatus={BOOKING_TYPE.PAST}
        downloadInvoiceError={undefined}
      />
    );
    expect(
      queryByTestId('BookingHistoryDetails-Notification-Download-Error')
    ).not.toBeInTheDocument();
  });

  it('it should render the BookingInfoCard component with dinner allowance', async () => {
    const { getByTestId } = render(
      <BookingHistoryInfoCardComponent
        {...mockProps}
        {...mockResponse}
        bookingDetails={mockBookingDetails}
        basketStatus={BOOKING_TYPE.UPCOMING}
      />
    );
    const text = getByTestId('dinner-allowance-text');
    await waitFor(() => {
      expect(text.textContent).toEqual('dashboard.bookings.dinnerAllowance - £3333.00');
    });
  });

  it('it should render cancelled BookingInfoCard component without dinner allowance', async () => {
    const { queryByTestId } = render(
      <BookingHistoryInfoCardComponent
        {...mockProps}
        {...mockResponse}
        bookingDetails={mockBookingDetails}
        basketStatus={BOOKING_TYPE.CANCELLED}
      />
    );
    await waitFor(() => {
      expect(queryByTestId('dinner-allowance-text')).not.toBeInTheDocument();
    });
  });

  it('renders SecureBookingButton', async () => {
    mockBookingDetails.arrivalDate = new Date(Date.now() + 86400000).toString();
    mockBookingDetails.paymentOption = paymentOptions.RESERVE_WITHOUT_CARD;
    render(
      <BookingHistoryInfoCardComponent
        {...mockProps}
        {...mockResponse}
        bookingDetails={mockBookingDetails}
        basketStatus={BASKET_STATUS.PAY_PENDING}
      />
    );

    const button = screen.getByTestId('SecureBooking-Button');
    expect(button).toBeInTheDocument();
  });

  it('renders SecureBookingButton on desktop view', async () => {
    (useMediaQuery as jest.Mock).mockReturnValue([false]); // desktop

    render(
      <BookingHistoryInfoCardComponent
        {...mockProps}
        {...mockResponse}
        bookingDetails={mockBookingDetails}
        basketStatus={BASKET_STATUS.PAY_PENDING}
      />
    );

    expect(await screen.findByTestId('SecureBooking-Button')).toBeInTheDocument();
  });

  it('renders SecureBookingButton on mobile view', async () => {
    (useMediaQuery as jest.Mock).mockReturnValue([true]); // mobile

    render(
      <BookingHistoryInfoCardComponent
        {...mockProps}
        {...mockResponse}
        bookingDetails={mockBookingDetails}
        basketStatus={BASKET_STATUS.PAY_PENDING}
      />
    );

    expect(await screen.findByTestId('SecureBooking-Button')).toBeInTheDocument();
  });
});
