import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { Area } from '@whitbread-eos/api';
import React from 'react';

import { fireEvent, render, waitFor } from '../../../../utils/test-utils';
import {
  mockBookingConfirmationAuthenticatedMock,
  mockBookingConfirmationData,
} from '../mockResponse';
import type { Props } from './CancelBookingModal.component';
import CancelBookingModal from './CancelBookingModal.component';
import CancelBookingModalContainer from './CancelBookingModal.container';

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
  formatAssetsUrl: (val: string) => val,
  useCustomLocale: () => ({
    locale: 'en',
  }),
  getAuthCookie: () => mockAuthCookie(),
  useMutationRequest: () => mockedMutationRequest(),
}));

const defaultUseQueryClient = () => ({
  fetchQuery: jest.fn(async (options: any) => {
    const queryKey = options.queryKey || options;
    const key = Array.isArray(queryKey) ? queryKey[0] : queryKey;

    switch (key) {
      case 'getBookingConfirmation':
        return Promise.resolve(mockBookingConfirmationData);
      case 'getBookingConfirmationAuthenticated':
        return Promise.resolve(mockBookingConfirmationAuthenticatedMock);
      case 'GetHotelInformation':
        return Promise.resolve({});
      default:
        return Promise.resolve({});
    }
  }),
  prefetchQuery: jest.fn().mockResolvedValue(undefined),
});

jest.mock('@tanstack/react-query', () => {
  const original: typeof ReactQuery = jest.requireActual('@tanstack/react-query');
  return {
    ...original,
    useQueryClient: () => defaultUseQueryClient(),
  };
});

const keepBookingMock = jest.fn();
const closeButtonMock = jest.fn();
const cancelBookingButtonMock = jest.fn();
const backToDashboardButtonMock = jest.fn();
const mockAuthCookie = jest.fn();
const mockedMutationRequest = jest.fn();

const mockUseMutationResponse = {
  mutation: {
    mutate: jest.fn(),
  },
  data: {
    cancelReservation: {
      basketReference: 'AKU2084403',
    },
  },
  isSuccess: true,
  isError: false,
};

const mockProps = {
  hotelName: 'hotelName',
  area: 'pi',
  error: { message: 'error' },
  isLoading: false,
  isError: false,
  isModalVisible: true,
  onClickKeepBooking: keepBookingMock,
  onModalClose: closeButtonMock,
  onClickCancelBooking: cancelBookingButtonMock,
  onClickBack: backToDashboardButtonMock,
  cancelReservationData: undefined,
  bookedFor: 'first last',
  arrivalDate: 'Monday 20 June 2023',
  noNights: 1,
  backBtnUrl: 'home.html',
  backBtnText: 'amend.anonymousBackButtonText',
  bookingReference: 'AQPR1437',
} as unknown as Props;

describe('CancelBookingModal rendered from Manage booking', () => {
  beforeEach(async () => {
    jest.clearAllMocks();
  });

  it('it should render the CancelBookingModal with default props', () => {
    const { getByText } = render(<CancelBookingModal {...mockProps} />);
    expect(getByText('dashboard.bookings.cancelModalTitle')).toBeInTheDocument();
    expect(getByText('dashboard.bookings.cancelModalDescription')).toBeInTheDocument();
    expect(getByText('first last')).toBeInTheDocument();
    expect(getByText('hotelName')).toBeInTheDocument();
    // expect(getByText(`dashboard.bookings.bookerLabel: first last`)).toBeInTheDocument(); Removed based on DNRQ-37390 decision
    expect(getByText('Monday 20 June 2023, 1 dashboard.bookings.night')).toBeInTheDocument();
    expect(getByText('dashboard.bookings.cancelButton')).toBeInTheDocument();
    expect(getByText('dashboard.bookings.keepBookingButton')).toBeInTheDocument();
  });

  it('it should render the CancelBookingModal and click keep booking', () => {
    const { getByText } = render(<CancelBookingModal {...mockProps} />);

    const keepBookingButton = getByText('dashboard.bookings.keepBookingButton');
    fireEvent.click(keepBookingButton);
    expect(keepBookingMock).toBeCalled();
  });

  it('it should render the CancelBookingModal with 2 nights', () => {
    const { getByText } = render(<CancelBookingModal {...mockProps} noNights={2} />);

    expect(getByText('Monday 20 June 2023, 2 dashboard.bookings.nights')).toBeInTheDocument();
  });

  it('it should render the CancelBookingModal and click close modal', () => {
    const { getByTestId } = render(<CancelBookingModal {...mockProps} />);

    const closeModal = getByTestId('ModalCloseButton');
    fireEvent.click(closeModal);
    expect(closeButtonMock).toBeCalled();
  });

  it('it should render the CancelBookingModal when cancelReservationData null', () => {
    const { getByText } = render(
      <CancelBookingModal {...mockProps} cancelReservationData={null} />
    );

    expect(getByText('dashboard.bookings.bookingCancelledErrorTitle')).toBeInTheDocument();
    expect(getByText('dashboard.bookings.bookingCancelledError')).toBeInTheDocument();
  });

  it('it should render the CancelBookingModal when cancelReservationData a valid string', () => {
    const { getByText, queryByText } = render(
      <CancelBookingModal {...mockProps} cancelReservationData="test" />
    );

    expect(getByText('dashboard.bookings.bookingCancelledNotification')).toBeInTheDocument();
    expect(getByText('AQPR1437')).toBeInTheDocument();
    expect(queryByText('test')).not.toBeInTheDocument();
    expect(getByText('amend.anonymousBackButtonText')).toBeInTheDocument();
  });

  it('it should render the CancelBookingModal when data is null', () => {
    mockProps.arrivalDate = '';
    mockProps.noNights = 0;
    const { getByText } = render(<CancelBookingModal {...mockProps} />);

    expect(getByText(', 0 dashboard.bookings.night')).toBeInTheDocument();
  });

  it('it should render the CancelBookingModal when isError true', () => {
    const { getByText } = render(<CancelBookingModal {...mockProps} isError={true} />);

    expect(getByText('error')).toBeInTheDocument();
  });

  it('it should not render the Back to Homepage button if area is ccui', () => {
    const { queryByTestId } = render(<CancelBookingModal {...mockProps} area={Area.CCUI} />);
    expect(queryByTestId('CancelBookingModalBackButton')).not.toBeInTheDocument();
  });
});

describe('CancelBookingModal rendered from Booking History', () => {
  beforeEach(async () => {
    jest.clearAllMocks();
  });

  beforeAll(() => {
    mockProps.bookingReference = '';
  });

  it('it should render the CancelBookingModal when cancelReservationData a valid string & backButtonText for Back button', () => {
    const { getByText } = render(
      <CancelBookingModal
        {...mockProps}
        cancelReservationData="test"
        backBtnText="amend.backButtonText"
      />
    );

    expect(getByText('dashboard.bookings.bookingCancelledNotification')).toBeInTheDocument();
    expect(getByText('test')).toBeInTheDocument();
    expect(getByText('amend.backButtonText')).toBeInTheDocument();
  });

  it('it should render the CancelBookingModal and click Back to dashboard button to close modal', () => {
    const { getByText } = render(
      <CancelBookingModal
        {...mockProps}
        cancelReservationData="test"
        backBtnText="amend.backButtonText"
      />
    );

    const backToDashboardButton = getByText('amend.backButtonText');
    fireEvent.click(backToDashboardButton);
    expect(backToDashboardButtonMock).toBeCalled();
  });
});

describe('<CancelBookingModalContainer>', () => {
  beforeEach(async () => {
    jest.clearAllMocks();

    mockAuthCookie.mockReturnValue(null);
    mockedMutationRequest.mockReturnValue(mockUseMutationResponse);
  });

  it('should render CancelBookingModalContainer (getBookingConfirmationAuthenticated)', async () => {
    mockAuthCookie.mockReturnValue('token');

    const { getByTestId } = render(
      <CancelBookingModalContainer
        refetchManageBooking={jest.fn()}
        basketReference="AKU2084403"
        {...mockProps}
      />
    );
    await waitFor(() => expect(getByTestId('ModalContent')).toBeInTheDocument());
  });

  it('should render CancelBookingModalContainer (getBookingConfirmation)', async () => {
    const { getByTestId } = render(
      <CancelBookingModalContainer
        refetchManageBooking={jest.fn()}
        basketReference="AKU2084403"
        {...mockProps}
      />
    );
    await waitFor(() => expect(getByTestId('ModalContent')).toBeInTheDocument());
  });

  it('should trigger cancelBooking with success', async () => {
    mockedMutationRequest.mockReturnValue({
      ...mockUseMutationResponse,
      data: {
        cancelReservation: {
          basketReference: null,
        },
      },
    });

    const { findByText } = render(
      <CancelBookingModalContainer
        refetchManageBooking={jest.fn()}
        basketReference="AKU2084403"
        {...mockProps}
      />
    );

    const onClickCancelBtn = await findByText('dashboard.bookings.cancelButton');
    await waitFor(() => {
      expect(onClickCancelBtn).not.toBeDisabled();
      fireEvent.click(onClickCancelBtn);
    });
  });

  it('should trigger cancelBooking with error', async () => {
    mockedMutationRequest.mockReturnValue({
      ...mockUseMutationResponse,
      data: {
        cancelReservation: {
          basketReference: null,
        },
      },
      isSuccess: false,
      isError: true,
    });

    const { findByText } = render(
      <CancelBookingModalContainer
        refetchManageBooking={jest.fn()}
        basketReference="AKU2084403"
        {...mockProps}
      />
    );

    const onClickCancelBtn = await findByText('dashboard.bookings.cancelButton');
    await waitFor(() => {
      expect(onClickCancelBtn).not.toBeDisabled();
      fireEvent.click(onClickCancelBtn);
    });
  });

  it('should trigger onClickKeepBooking', async () => {
    mockedMutationRequest.mockReturnValue({
      ...mockUseMutationResponse,
      data: {
        cancelReservation: {
          basketReference: null,
        },
      },
    });

    const { findByText } = render(
      <CancelBookingModalContainer
        refetchManageBooking={jest.fn()}
        basketReference="AKU2084403"
        {...mockProps}
      />
    );

    const onClickCancelBtn = await findByText('dashboard.bookings.keepBookingButton');
    await waitFor(() => {
      fireEvent.click(onClickCancelBtn);
      expect(onClickCancelBtn).not.toBeDisabled();
    });
  });

  it('should handle errors in bookingConfirmationQuery)', async () => {
    // eslint-disable-next-line @typescript-eslint/no-require-imports
    jest.spyOn(await require('@tanstack/react-query'), 'useQueryClient').mockReturnValue({
      fetchQuery: async () => {
        throw new Error('Mock error');
      },
    });

    const { getByText } = render(
      <CancelBookingModalContainer
        refetchManageBooking={jest.fn()}
        basketReference="AKU2084403"
        {...mockProps}
      />
    );
    waitFor(() => {
      expect(getByText('Mock error')).toBeInTheDocument();
    });
  });

  it('should handle errors in getHotelDetailsRequest)', async () => {
    // eslint-disable-next-line @typescript-eslint/no-require-imports
    jest.spyOn(await require('@tanstack/react-query'), 'useQueryClient').mockReturnValue({
      ...defaultUseQueryClient(),
      fetchQuery: async (options: any) => {
        const key = Array.isArray(options.queryKey) ? options.queryKey[0] : null;
        if (key === 'GetHotelInformation') {
          throw new Error('Hotel Details Error');
        }
        return defaultUseQueryClient().fetchQuery(options);
      },
    });

    const { getByText } = render(
      <CancelBookingModalContainer
        refetchManageBooking={jest.fn()}
        basketReference="AKU2084403"
        {...mockProps}
      />
    );
    await waitFor(() => {
      expect(getByText('Hotel Details Error')).toBeInTheDocument();
    });
  });
});
