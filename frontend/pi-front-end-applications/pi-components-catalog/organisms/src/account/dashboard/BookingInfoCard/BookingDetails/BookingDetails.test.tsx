import '@testing-library/jest-dom';
import { waitFor } from '@testing-library/react';

import { render, RenderOptions } from '../../../../utils/test-utils';
import BookingDetails from './BookingDetails.component';
import BookingDetailsContainer, {
  BookingDetailsProp,
  Props as BookingDetailsContainerProps,
} from './BookingDetails.container';

const props: BookingDetailsContainerProps = {
  arrivalDate: '',
  basketReference: null,
  bookingReference: '',
  bookingSurname: '',
  isAmendSuccessful: false,
  overridenUserInfo: undefined,
  paymentOption: '',
  bookingStatus: '',
  shouldShowTypeOfBooking: false,
  distBookingChannel: 'test',
  gdsReferenceNumber: 'test12',
  sourcePms: 'opera',
  dpaInfo: { dpaOverride: false, dpaPassed: false },
  setDpaInfo: jest.fn(),
};

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    locale: 'en',
    query: {
      bookingReference: 'AQPR1437',
    },
  }),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQueryRequest: () => ({
    isLoading: false,
    isError: false,
    data: {
      bookingInformation: {},
      manageBooking: { isCancellable: true },
    },
    error: '',
  }),
  useMutationRequest: () => ({
    mutation: jest.fn(),
  }),
}));

const bookingDetails: BookingDetailsProp = {
  donationPkg: undefined,
  rateType: '',
  shouldDisplayCityTaxMessage: false,
  currencyCode: 'GBP',
  previousTotal: '0',
  paymentOption: '',
  totalCost: '0',
  balanceOutstanding: '0',
  hotelId: '',
  newTotal: '',

  roomDetails: [
    {
      noAdults: 1,
      noNights: 1,
      leadGuestName: 'name',
      roomType: 'double',
      noChildren: 1,
      cot: false,
      roomPrice: 1,
      childrenMealDescription: [],
      adultMealDescription: [{ price: 1, noSelections: 1, id: '1', title: 'mealName' }],
    },
    {
      noAdults: 1,
      noNights: 1,
      leadGuestName: 'name',
      roomType: 'double',
      noChildren: 1,
      cot: false,
      roomPrice: 1,
      childrenMealDescription: [],
      adultMealDescription: [{ price: 1, noSelections: 1, id: '1', title: 'mealName' }],
    },
  ],
};
describe('BookingDetails', () => {
  it('it should render the BookingDetails component with the roomDetails', async () => {
    const { getByText, getAllByTestId } = render(
      <BookingDetails bookingDetails={bookingDetails} {...props} />,
      {
        initialAppData: { screenSize: 'mobile' },
      } as Omit<RenderOptions, 'wrapper'>
    );
    expect(getByText('booking.summary.room 1')).toBeInTheDocument();
    const bookingDetailsWrapper = getAllByTestId('BookingDetailWrapper');
    await waitFor(() => {
      expect(bookingDetailsWrapper[0]).toBeInTheDocument();
      expect(bookingDetailsWrapper[0]).toHaveStyle('margin-top:lg');
    });
  });

  it('it should should not show type of booking ', () => {
    const { queryByTestId } = render(
      <BookingDetails bookingDetails={{ ...bookingDetails }} {...props} />
    );

    expect(queryByTestId('operaCardReservationInfo')).not.toBeInTheDocument();
  });

  it('it should should show loading spinner  ', () => {
    const { getByText } = render(<BookingDetailsContainer {...props} />);

    expect(getByText('booking.loading')).toBeInTheDocument();
  });

  it('should render when isRemovePIIDataFromLocalStorageEnabled is true', () => {
    const { getByText } = render(
      <BookingDetails
        bookingDetails={bookingDetails}
        {...props}
        isRemovePIIDataFromLocalStorageEnabled={true}
      />
    );

    expect(getByText('booking.summary.room 1')).toBeInTheDocument();
  });

  it('should include priceBOProsecco in extrasPackageRoom', async () => {
    const bookingDetailsWithProsecco: BookingDetailsProp = {
      ...bookingDetails,
      roomDetails: [
        {
          ...bookingDetails.roomDetails[0],
          extrasPackageRoom: {
            reservationId: 'RES1',
            priceBOProsecco: 1,
            packagesList: [],
          },
        },
      ],
    };
    const { getByTestId } = render(
      <BookingDetails bookingDetails={bookingDetailsWithProsecco} {...props} />
    );
    expect(getByTestId('roomPriceLabel')).toHaveTextContent('£1.00');
  });
});
