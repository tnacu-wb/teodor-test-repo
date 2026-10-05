import '@testing-library/jest-dom';
import { waitFor } from '@testing-library/react';
import {
  Area,
  BASKET_STATUS,
  BC_RESERVATION_STATUS,
  BOOKING_TYPE,
  paymentOptions,
} from '@whitbread-eos/api';

import { render } from '../../../utils/test-utils';
import BookingInfoCardComponent, { Props } from './BookingInfoCard.component';
import BookingInfoCard from './BookingInfoCard.container';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useFeatureToggle: jest.fn(() => ({
    release_pi_bb_ccui_choose_room_type: true,
  })),
}));

const mockProps: Props = {
  arrivalDate: '',
  bookingSurname: '',
  handleResendConfirmationAction: jest.fn(),
  isAmendPage: false,
  isAmendSuccessful: false,
  basketReference: 'AQP_some_randon_uuid',
  bookingReference: 'AQPR1437',
  operaConfNumber: null,
  area: 'pi' as Area,
  bookingStatus: BC_RESERVATION_STATUS.COMPLETED,
  getBookingStatus: jest.fn(),
  paymentOption: paymentOptions.ACCOUNT_TO_COMPANY,
  dpaInfo: { dpaOverride: false, dpaPassed: false },
  setDpaInfo: jest.fn(),
  setIsAgentOverrideModalVisible: jest.fn(),
  overridenUserInfo: {
    reservationOverrideReasons: {
      callerName: 'test testsdfsd',
      managerName: 'test manager',
      reasonName: 'DUP',
      reasonCode: 'DUP',
    },
    reservationOverridden: true,
  },
  manageBookingParams: {
    data: {
      manageBooking: { isCancellable: true, isAmendable: true, isRuleCompliant: true },
    },
    isError: false,
    isLoading: false,
    error: 'Error',
  },
  paymentStatus: BASKET_STATUS.COMPLETED,
  isChangedPaymentApplied: false,
  upsellType: 'dinner',
};

const mockUseQueryRequest = {
  isLoading: false,
  isError: false,
  data: {
    bookingInformation: {},
    hotelInformation: {
      address: {},
    },
    manageBooking: { isCancellable: true },
  },
  error: '',
};
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: (val: string) => val,
  useQueryRequest: () => mockUseQueryRequest,
  useMutationRequest: () => ({
    mutation: jest.fn(),
  }),
}));

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    query: '',
  }),
}));

describe('BookingInfoCard', () => {
  it('it should render the BookingInfoCard component with the confirmationId', async () => {
    const { getByText } = render(
      <BookingInfoCardComponent
        {...{ ...mockProps, isAmendPage: true, bookingStatus: BC_RESERVATION_STATUS.CANCELLED }}
      />
    );

    await waitFor(() => {
      expect(getByText('dashboard.bookings.bookingReference')).toBeInTheDocument();
      expect(getByText('AQPR1437')).toBeInTheDocument();
    });
  });

  it('it should render the BookingInfoCard component ', () => {
    const { getByText } = render(<BookingInfoCardComponent {...mockProps} />);

    expect(getByText('dashboard.bookings.bookingReference')).toBeInTheDocument();
    expect(getByText('AQPR1437')).toBeInTheDocument();
  });

  it('it should render the BookingInfoCard component with loading true', () => {
    mockUseQueryRequest.isLoading = true;
    const { getByText } = render(<BookingInfoCard {...mockProps} />);

    expect(getByText('booking.loading')).toBeInTheDocument();
  });
  it('it should render the BookingInfoCard component with go to OPERA UI notification', () => {
    mockProps.operaConfNumber = '321321321';
    mockProps.manageBookingParams.data.manageBooking.isAmendable = false;
    mockProps.manageBookingParams.data.manageBooking.isCancellable = false;
    mockProps.bookingType = BOOKING_TYPE.UPCOMING;
    mockProps.area = Area.CCUI;
    const { getByText } = render(<BookingInfoCardComponent {...mockProps} />);

    expect(getByText('ccui.managebooking.notification.goToOpera')).toBeInTheDocument();
  });
  it('it should render the error message if isErrorFindOrCopyBooking is true', () => {
    mockProps.manageBookingParams.data.manageBooking.isAmendable = true;
    mockProps.manageBookingParams.data.manageBooking.isCancellable = true;
    mockProps.area = Area.CCUI;
    mockProps.isErrorFindOrCopyBooking = true;
    const { getByText } = render(<BookingInfoCardComponent {...mockProps} />);

    expect(getByText('errors.sorry')).toBeInTheDocument();
  });
  it('it should render the BookingInfoCard component with change payment notification', () => {
    mockProps.bookingType = BOOKING_TYPE.UPCOMING;
    mockProps.area = Area.CCUI;
    mockProps.isChangedPaymentApplied = true;
    mockProps.basketStatus = BASKET_STATUS.COMPLETED;
    mockProps.paymentOption = paymentOptions.ACCOUNT_TO_COMPANY;
    const { getByText } = render(<BookingInfoCardComponent {...mockProps} />);

    expect(getByText('ccui.manageBooking.notification.paymentMethodUpdated')).toBeInTheDocument();
  });
});
