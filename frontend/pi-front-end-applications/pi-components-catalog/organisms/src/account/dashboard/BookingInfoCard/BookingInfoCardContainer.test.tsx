import '@testing-library/jest-dom';
import { Area, BASKET_STATUS, BOOKING_TYPE } from '@whitbread-eos/api';
import { graphQLRequest } from '@whitbread-eos/utils';
import React, { ComponentProps } from 'react';

import { render, waitFor, userEvent } from '../../../utils/test-utils';
import BookingInfoCard, { Props } from './BookingInfoCard.container';
import {
  mockBookingConfirmationAuthenticatedResponse,
  mockOverrideReasonsResponse,
} from './mockResponse';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useFeatureToggle: jest.fn(() => ({
    release_pi_bb_ccui_choose_room_type: true,
    release_pi_bb_ccui_show_meals_package: true,
  })),
}));

jest.mock('next/config', () => () => ({
  publicRuntimeConfig: {
    NEXT_PUBLIC_ASSETS_URL: 'https://secure2.premierinn.com',
  },
}));

const mockPush = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    locale: 'en',
    push: mockPush,
  }),
}));
const mockUseLocalStorage = jest.fn();
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useLocalStorage: () => mockUseLocalStorage(),
  useQuery: () => mockUseQueryRequest,
  useQueryRequest: mockUseQueryRequest,
  useBookingConfimationData: () => ({
    bookingData: mockBookingConfirmationAuthenticatedResponse.data.bookingConfirmationAuthenticated,
    bookingError: mockBookingConfirmationAuthenticatedResponse.error,
    bookingIsError: mockBookingConfirmationAuthenticatedResponse.isError,
    bookingIsLoading: mockBookingConfirmationAuthenticatedResponse.isLoading,
    bookingIsSuccess: mockBookingConfirmationAuthenticatedResponse.isSuccess,
    bookingRefetch: jest.fn(),
  }),
  useMutationRequest: () => ({
    mutation: {
      mutateAsync: jest.fn().mockResolvedValue({
        copyBooking: { copyBasketReference: 'copied-basket-reference' },
      }),
    },
  }),
  graphQLRequest: jest.fn().mockResolvedValue({
    findBooking: { token: 'booking-token' },
  }),
}));

function mockUseQueryRequest(queryKey: any) {
  const key = queryKey[0];
  if (typeof key === 'string') {
    if (key === 'getOverrideReasons') {
      return mockOverrideReasonsResponse;
    }
    if (key === 'manageBookingDashBoard') {
      return {
        data: {
          ...mockedManageBookingData,
        },
      };
    }
    if (key === 'basket') {
      return getBasketStatus;
    }
  }
}

const getBasketStatus = {
  data: { basket: { status: '' } },
};
const mockedManageBookingData = {
  manageBooking: {
    isCancellable: true,
  },
};

const clearRequestMockups = (): void => {
  mockOverrideReasonsResponse.isError = false;
  mockOverrideReasonsResponse.isLoading = false;
  mockOverrideReasonsResponse.error = { message: '' };
  mockBookingConfirmationAuthenticatedResponse.error = { message: '' };
  mockBookingConfirmationAuthenticatedResponse.isError = false;
  mockBookingConfirmationAuthenticatedResponse.isLoading = false;
};
jest.mock('./BookingInfoCard.component', () => {
  const BookingInfoCardComponent = ({
    handleChangePayment,
  }: Partial<ComponentProps<typeof BookingInfoCardComponent>>) => (
    <div data-testid="booking-card-component">
      <button onClick={handleChangePayment}>Change payment method</button>
    </div>
  );
  return BookingInfoCardComponent;
});

jest.mock('./AgentOverrideModal', () => {
  const AgentOverrideModalComponent = () => <div data-testid="agent-override-modal-component" />;
  return AgentOverrideModalComponent;
});

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

const mockProps: Props = {
  bookingReference: 'AWM1495858',
  area: Area.PI,
  retriggerSearch: jest.fn(),
  basketReference: '',
  showFreeFoodKids: true,
  upsellType: 'dinner',
  freeBreakfastOption: true,
};
const mockLocalStorageGetItem = jest.fn();
const mockLocalStorageSetItem = jest.fn();

describe('BookingInfoCard Container', () => {
  beforeEach(() => {
    clearRequestMockups();
    const isBackFlag = false;
    mockUseLocalStorage.mockReturnValue([isBackFlag, jest.fn()]);

    global.sessionStorage = {
      getItem(): string | null {
        return null;
      },
      key(): string | null {
        return null;
      },
      length: 0,
      removeItem: () => jest.fn(),
      setItem: () => jest.fn(),
      clear: () => jest.fn(),
    };

    Object.defineProperty(window, 'localStorage', {
      value: {
        getItem: (...args: string[]) => mockLocalStorageGetItem(...args),
        setItem: (...args: string[]) => mockLocalStorageSetItem(...args),
      },
    });
    mockLocalStorageGetItem.mockReturnValue(false);
  });

  it('should render BookingInfoCardContainer', () => {
    const { getByTestId } = render(
      <BookingInfoCard
        {...{ ...mockProps, basketReference: 'AWM-782e1dee-e75e-4f85-9741-c7acf3d31d6c' }}
      />
    );
    expect(getByTestId('BookingInfoCard-Container')).toBeInTheDocument();
  });

  it('should render BookingInfoCardComponent', () => {
    const { getByTestId } = render(
      <BookingInfoCard {...{ ...mockProps, basketReference: null }} />
    );
    expect(getByTestId('booking-card-component')).toBeInTheDocument();
  });

  it('should render BookingInfoCard when isRemovePIIDataFromLocalStorageEnabled is true', () => {
    const { getByTestId } = render(
      <BookingInfoCard
        {...{
          ...mockProps,
          area: Area.CCUI,
          bookingType: BOOKING_TYPE.UPCOMING,
          isRemovePIIDataFromLocalStorageEnabled: true,
        }}
      />
    );
    expect(getByTestId('booking-card-component')).toBeInTheDocument();
  });

  it('should render Overriden Notification if the reservation has been overriden', () => {
    const { getByTestId } = render(
      <BookingInfoCard {...{ ...mockProps, area: Area.CCUI, bookingType: BOOKING_TYPE.UPCOMING }} />
    );
    expect(getByTestId('BookingInfoCard-OverridenNotification')).toBeInTheDocument();
  });

  it('should render AgentOverrideInfo', () => {
    const { getByTestId } = render(<BookingInfoCard {...{ ...mockProps, area: Area.CCUI }} />);
    expect(getByTestId('BookingInfoCard-AgentOverrideInfo')).toBeInTheDocument();
  });

  it('should render Loading spinner if basketInfo is loading ', () => {
    mockBookingConfirmationAuthenticatedResponse.isLoading = true;
    const { getByText } = render(<BookingInfoCard {...{ ...mockProps, area: Area.CCUI }} />);
    expect(getByText('booking.loading')).toBeInTheDocument();
  });

  it('should render Notification when isError true', () => {
    mockBookingConfirmationAuthenticatedResponse.isError = true;
    mockBookingConfirmationAuthenticatedResponse.error = 'Mock Error';

    const { getByText } = render(<BookingInfoCard {...mockProps} />);
    expect(getByText('Mock Error')).toBeInTheDocument();
  });

  it('should trigger handleChangePayment() when paymentOption is NON', async () => {
    mockBookingConfirmationAuthenticatedResponse.data.bookingConfirmationAuthenticated.reservationByIdList[0].guaranteeCode =
      'NON';
    const { getByText } = render(<BookingInfoCard {...{ ...mockProps, area: Area.CCUI }} />);
    const changePaymentButton = getByText('Change payment method');
    userEvent.click(changePaymentButton);

    await waitFor(() => {
      expect(mockPush).toHaveBeenCalledWith(expect.stringContaining('amend/payment'));
    });
  });

  it('should set the ref of isErrorCopyOrFindBooking to true if findBooking encounters an error', async () => {
    (graphQLRequest as jest.Mock).mockRejectedValueOnce({ findBooking: new Error('error') });

    const mockRef = { current: false };

    // Spy on useRef and return our mockRef
    jest.spyOn(React, 'useRef').mockReturnValue(mockRef);

    const { getByText } = render(<BookingInfoCard {...{ ...mockProps, area: Area.CCUI }} />);

    // Trigger the function that leads to the try-catch block being executed
    const changePaymentButton = getByText('Change payment method');
    userEvent.click(changePaymentButton);

    // Assert that ref.current is true after the error is caught
    await waitFor(() => {
      expect(graphQLRequest).toHaveBeenCalled();
      expect(mockRef.current).toBe(true);
    });
  });

  it('should call seIsChangePaymentApplied when bookingData and isBackFlag are TRUE and payment status is completed', () => {
    mockBookingConfirmationAuthenticatedResponse.data.bookingConfirmationAuthenticated.reservationByIdList[0].guaranteeCode =
      'CC';
    const isBackFlag = true;
    mockUseLocalStorage.mockReturnValue([isBackFlag, jest.fn()]);
    const isChangedPaymentApplied = false;
    const seIsChangePaymentApplied = jest.fn();
    getBasketStatus.data.basket.status = BASKET_STATUS.COMPLETED;
    jest
      .spyOn(React, 'useState')
      .mockImplementation(() => [isChangedPaymentApplied, seIsChangePaymentApplied]);

    render(
      <BookingInfoCard
        {...{
          ...mockProps,
          area: Area.CCUI,
        }}
      />
    );
    expect(seIsChangePaymentApplied).toHaveBeenCalledWith(true);
  });
});
