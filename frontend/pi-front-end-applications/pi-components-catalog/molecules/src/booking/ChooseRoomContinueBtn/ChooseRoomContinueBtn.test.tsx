import '@testing-library/jest-dom';
import type { HIRoomType } from '@whitbread-eos/api';
import { ROOM_TYPE, RoomClass } from '@whitbread-eos/api';

import { render } from '../../utils/test-utils';
import ChooseRoomContinueBtnComponent from './ChooseRoomContinueBtn.component';
import ChooseRoomContinueBtnContainer, {
  ChooseRoomContinueBtnProps,
} from './ChooseRoomContinueBtn.container';

const baseDataTestId = 'ChooseBathroom';

const mockedProps = {
  dataTestId: baseDataTestId,
  selectedPMSRoomTypes: [ROOM_TYPE.LOWERED_DOUBLE],
  selectedSpecialRequests: [['TWDS', 'LOWB']],
  bookRsvIsLoading: false,
  bookRsvIsError: false,
  bookRsvError: null,
  onBookReservation: jest.fn(),
  handleBooking: () => null,
  isDisabledContinueBtn: false,
};

const mockBasketDetailsState = {
  phoneNumber: '',
  bookingFlow: {
    bookingFlowItems: [{ bookingId: '', rateCode: '' }],
  },
  hotelId: 'DLONEU',
  roomTypeInformationResponse: {
    errorRoomTypeInformation: null,
    isErrorRoomTypeInformation: false,
    isLoadingRoomTypeInformation: false,
    dataRoomTypeInformation: {
      roomTypeInformation: {
        roomTypes: [
          {
            roomCategory: '',
            roomTypeCode: [],
            roomDescription: '',
            roomImage: '',
            roomLabel: '',
          },
        ],
      },
    },
  },
  arrival: '2022-08-13',
  departure: '2022-08-14',
  numberOfUnits: 1,
  numberOfNights: 2,
  roomClass: RoomClass.ST,
  rateName: 'Flex',
  selectedRate: {
    ratePlanCode: 'FLEXRATE',
    roomTypes: [
      {
        roomType: 'TWIN',
        adults: 2,
        children: 0,
        cotRequested: false,
        rooms: [
          {
            pmsRoomType: 'WINCMB',
            silentSubstitution: false,
            roomClass: 'ST',
            cotAvailable: false,
            roomPriceBreakdown: {
              totalNetAmount: 115.33,
              currencyCode: 'GBP',
              dailyPrices: [
                {
                  date: '2022-08-13',
                  netPrice: 75.0,
                },
                {
                  date: '2022-08-14',
                  netPrice: 40.33,
                },
              ],
            },
            specialRequests: ['TWDS', 'LOWB'],
          },
        ],
      } as HIRoomType,
    ],
  },
};

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));
jest.mock('@whitbread-eos/api', () => ({
  ...jest.requireActual('@whitbread-eos/api'),
  BASKET_DETAILS_STORAGE_KEY: '',
  BASKET_DETAILS_STATE_INITIAL_VALUE: '',
}));

const mockUseLocalStorage = jest.fn();
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useLocalStorage: () => mockUseLocalStorage(),
  useCustomLocale: () => ({
    locale: 'en',
  }),
}));

const mockChooseRoomContainerProps: ChooseRoomContinueBtnProps = {
  channel: 'PI',
  handleBooking: () => null,
  bookRsvError: '',
  bookRsvIsError: false,
  isDisabledContinueBtn: false,
  bookRsvIsLoading: false,
  selectedPMSRoomTypes: ['TWDS'],
  selectedSpecialRequests: [['TWDS', 'LOWB']],
};
describe('ChooseRoomContinueBtn', () => {
  beforeAll(() => {
    mockUseRouter.mockReturnValue({
      locale: 'gb',
    });

    mockUseLocalStorage.mockReturnValue([mockBasketDetailsState, jest.fn()]);
  });

  it('should render ChooseRoomContinueBtn', function () {
    const { getByTestId } = render(<ChooseRoomContinueBtnComponent {...mockedProps} />);
    expect(getByTestId('ChooseBathroom-ContinueButton')).toBeInTheDocument();
  });

  it('should display CTA button text', function () {
    const { getByText } = render(<ChooseRoomContinueBtnComponent {...mockedProps} />);
    expect(getByText('booking.summary.continue')).toBeInTheDocument();
  });

  it('should display error message if an error occured', function () {
    const { getByText } = render(
      <ChooseRoomContinueBtnComponent
        {...mockedProps}
        bookRsvIsError={true}
        bookRsvError={{ message: 'Error' }}
      />
    );
    expect(getByText('Error')).toBeInTheDocument();
  });

  it('should call choose room container', function () {
    const { getByText } = render(
      <ChooseRoomContinueBtnContainer {...mockChooseRoomContainerProps} />
    );
    expect(getByText('booking.summary.continue')).toBeInTheDocument();
  });

  it('should call choose room container with hotelId Undefined', function () {
    mockBasketDetailsState.hotelId = undefined;

    const { queryByText } = render(
      <ChooseRoomContinueBtnContainer {...mockChooseRoomContainerProps} />
    );
    expect(queryByText('booking.summary.continue')).toBeFalsy();
  });
});
