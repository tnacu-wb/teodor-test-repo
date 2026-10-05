import '@testing-library/jest-dom';
import { Area } from '@whitbread-eos/api';

import { fireEvent, render } from '../../../../utils/test-utils';
import BookingHistoryCancelBookingModal, {
  Props,
} from './BookingHistoryCancelBookingModal.container';

type useMutationRequestType = {
  isLoading: boolean;
  isError: boolean;
  error: string | { message: string };
  data:
    | undefined
    | null
    | {
        cancelBooking: {
          bookingReference: null;
          cancellationId: string;
        };
      };
};

const mockUseMutationRequest: useMutationRequestType = {
  isLoading: false,
  isError: false,
  error: '',
  data: undefined,
};

const mockProps: Props = {
  isModalVisible: true,
  onModalClose: jest.fn(),
  refetchManageBooking: jest.fn(),
  basketReference: 'AKQR322158',
  area: Area.PI,
  hotelName: 'Liverpool (West Derby)',
  bookedFor: 'Missy Cooper',
  arrivalDate: '2023-11-20',
  noNights: 1,
  hotelId: 'LIVSTA',
  bookingChannel: {
    channel: 'PI',
    subchannel: 'WEB',
    language: 'EN',
  },
  bookingReference: '',
  bookedBy: 'The Booker',
};

jest.mock('next/config', () => () => ({
  publicRuntimeConfig: {
    NEXT_PUBLIC_ASSETS_URL: 'https://secure2.premierinn.com',
  },
}));

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    locale: 'en',
  }),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useMutationRequest: () => mockUseMutationRequest,
}));

const clearRequestMockups = (): void => {
  mockUseMutationRequest.isLoading = false;
  mockUseMutationRequest.isError = false;
  mockUseMutationRequest.error = '';
  mockUseMutationRequest.data = undefined;
};

describe('BookingHistoryCancelBookingModal.container before mutation tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  beforeEach(() => {
    clearRequestMockups();
  });

  it('should display the modal when modal container renders first time', async () => {
    const { getByTestId } = render(<BookingHistoryCancelBookingModal {...mockProps} />);
    expect(getByTestId('CancelBookingModalContainer')).toBeInTheDocument();
  });

  it('should display the bookedFor & hotelName when modal container renders first time', async () => {
    const { getByText } = render(<BookingHistoryCancelBookingModal {...mockProps} />);
    expect(getByText('Missy Cooper')).toBeInTheDocument();
    expect(getByText('Liverpool (West Derby)')).toBeInTheDocument();
  });

  it('should display 3 buttons (cancel booking & keep booking btns) when modal container renders first time', async () => {
    const { getAllByRole } = render(<BookingHistoryCancelBookingModal {...mockProps} />);
    const buttons = getAllByRole('button');

    expect(buttons.length).toBe(3);
    expect(buttons[1].textContent).toEqual('dashboard.bookings.cancelButton');
    expect(buttons[2].textContent).toEqual('dashboard.bookings.keepBookingButton');
  });

  it('should call onModalClose fn when close modal btn is clicked', async () => {
    const { getAllByRole } = render(<BookingHistoryCancelBookingModal {...mockProps} />);
    const buttons = getAllByRole('button');

    fireEvent.click(buttons[0]);
    expect(mockProps.onModalClose).toBeCalled();
  });

  it('should call onModalClose fn when keep booking btn is clicked', async () => {
    const { getAllByRole } = render(<BookingHistoryCancelBookingModal {...mockProps} />);
    const buttons = getAllByRole('button');

    fireEvent.click(buttons[2]);
    expect(mockProps.onModalClose).toBeCalled();
  });
});

describe('BookingHistoryCancelBookingModal.container after mutation tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  beforeEach(() => {
    clearRequestMockups();
  });

  it('should render cancellationId if mutation was successed', async () => {
    mockUseMutationRequest.data = {
      cancelBooking: {
        bookingReference: null,
        cancellationId: 'BELC150394632',
      },
    };
    const { getByText } = render(<BookingHistoryCancelBookingModal {...mockProps} />);
    expect(getByText('BELC150394632')).toBeInTheDocument();
  });

  it('should show notification when cancel was done', async () => {
    mockUseMutationRequest.data = {
      cancelBooking: {
        bookingReference: null,
        cancellationId: 'BELC150394632',
      },
    };

    const { getByText } = render(<BookingHistoryCancelBookingModal {...mockProps} />);
    expect(getByText('dashboard.bookings.bookingCancelledNotification')).toBeInTheDocument();
  });

  it('should render error message', async () => {
    mockUseMutationRequest.isError = true;
    mockUseMutationRequest.error = { message: 'Error!' };
    mockUseMutationRequest.data = null;

    const { getByText } = render(<BookingHistoryCancelBookingModal {...mockProps} />);

    expect(getByText('Error!')).toBeInTheDocument();
  });
});
