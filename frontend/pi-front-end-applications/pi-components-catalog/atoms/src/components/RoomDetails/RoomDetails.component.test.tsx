import { fireEvent } from '@testing-library/dom';
import '@testing-library/jest-dom';
import { act } from '@testing-library/react';
import { paymentOptions } from '@whitbread-eos/api';
import { getLocalStorageMock } from '@whitbread-eos/utils';

import { render } from '../../utils/test-utils';
import RoomDetails from './RoomDetails.component';

const mockUseFeatureSwitch = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: () => ({
    locale: 'en',
  }),
  useFeatureSwitch: () => mockUseFeatureSwitch(),
}));

const mockedActualYear = new Date().getFullYear();

const mockData = {
  data: {
    roomDetails: [
      {
        roomReservationStartDate: `15 Apr 2023 ${mockedActualYear}`,
        roomReservationEndDate: `16 Apr ${mockedActualYear}`,
        leadGuestTitle: 'Mr.',
        leadGuestName: 'Sergio Ponte',
        roomType: 'Twin',
        roomTypeDescription: 'double bed and sofa bed Room',
        rateType: 'Flex',
        rateTypeDescription:
          "You can amend or cancel your booking any time up to 1pm on the day you're due to arrive",
        roomGroup: '1 Adult',
        roomPrice: 100,
        roomTotalPrice: 118.0,
        ratesPerNight: [
          { pricePerNight: 123, startDate: `${mockedActualYear}-04-15`, cityTaxPerNight: 2 },
        ],
        packages: [{ computedPrice: 1, description: 'Meal 1', totalQuantity: 1, unitPrice: 1 }],
        adultMealDescription: ['1 Adult Breakfast'],
        childrenMealDescription: [],
        mealPrice: 13.5,
      },
      {
        roomReservationStartDate: `25 Aug ${mockedActualYear}`,
        roomReservationEndDate: `26 Aug ${mockedActualYear}`,
        leadGuestTitle: 'Ms.',
        leadGuestName: 'Billie Eilish',
        roomType: 'Twin',
        roomTypeDescription: 'double bed and sofa bed Room',
        rateType: 'Flex',
        rateTypeDescription:
          "You can amend or cancel your booking any time up to 1pm on the day you're due to arrive",
        roomGroup: '1 Adult',
        roomPrice: 100,
        roomTotalPrice: 118.0,
        ratesPerNight: [
          { pricePerNight: 123, startDate: `${mockedActualYear}-08-25`, cityTaxPerNight: 2 },
        ],
        packages: [{ computedPrice: 1, description: 'Meal 1', totalQuantity: 1, unitPrice: 1 }],
        adultMealDescription: ['1 Adult Breakfast'],
        childrenMealDescription: [],
        mealPrice: 13.5,
      },
    ],
    currency: 'GBP',
    bookingTotalCost: 1234,
    donations: {
      amount: '10.00',
      currency: 'GBP',
    },
    selectedPaymentOption: paymentOptions.RESERVE_WITHOUT_CARD,
    taxesMessage: 'Price includes taxes and fees',
  },
  basketReference: 'MAH-6dd62772-4c14-4628-aa83-b6b18b5b7317',
  currentLang: 'en',
  t: (key: string) => {
    switch (key) {
      case 'booking.roomdetails.title':
        return 'Room Details';
      case 'booking.confirmation.leadGuest':
        return 'Lead Guest';
      case 'booking.confirmation.yourRoom':
        return 'Your Room';
      case 'booking.confirmation.yourRate':
        return 'Your rate';
      case 'bbooking.confirmation.yourGroup':
        return 'Your group';
      case 'booking.hotel.summary.roomTotal':
        return 'Room total:';
      case 'booking.hotel.summary.arriving':
        return 'Arriving';
      case 'booking.confirmation.checkoutTime':
        return 'Check out before 12 pm';
      case 'booking.hotel.summary.checkout':
        return 'Leaving';
      case 'booking.hotel.summary.meals':
        return 'Meals';
      case 'booking.confirmation.roomTotalMD':
        return 'Room [roomNumber] total price:';
      case 'booking.confirmation.room':
        return 'Room [roomNumber]';
      case 'booking.confirmation.paymentTaken':
        return 'Thank you, prepayment has been taken.';
      case 'booking.confirmation.reservedWithCard':
        return 'Thank you, your payment will be taken on arrival';
      case 'booking.confirmation.reservedWithoutCard':
        return 'Thank you for your booking. We are looking forward to your visit!';
      case 'booking.roomdetails.expandAll':
        return 'Expand all';
      case 'booking.roomdetails.collapseAll':
        return 'Colapse all';
      default:
        return 'RoomCard default';
    }
  },
};

describe('RoomDetails', () => {
  beforeAll(() => {
    mockUseFeatureSwitch.mockReturnValue(false);
  });

  it('should render RoomDetails corectly', function () {
    const { getByText, getByTestId } = render(<RoomDetails {...mockData} />);

    expect(getByText('Room Details')).toBeInTheDocument();
    expect(getByTestId('RoomCardHeader-room1')).toBeInTheDocument();
    expect(getByTestId('RoomCardHeader-room2')).toBeInTheDocument();
    expect(getByText('15 Apr - 16 Apr')).toBeInTheDocument();
    expect(getByText('25 Aug - 26 Aug')).toBeInTheDocument();
  });

  it('should render correct paymentConfText for pay now', function () {
    mockData.data.selectedPaymentOption = paymentOptions.PAY_NOW;
    const { getByText } = render(<RoomDetails {...mockData} />);

    expect(getByText('Thank you, prepayment has been taken.')).toBeInTheDocument();
  });

  it('should render correct paymentConfText for pay on arrival', function () {
    mockData.data.selectedPaymentOption = paymentOptions.PAY_ON_ARRIVAL;
    const { getByText } = render(<RoomDetails {...mockData} />);

    expect(getByText('Thank you, your payment will be taken on arrival')).toBeInTheDocument();
  });

  it('should render correct paymentConfText for pay on arrival', function () {
    mockData.data.selectedPaymentOption = paymentOptions.RESERVE_WITHOUT_CARD;
    const { getByText } = render(<RoomDetails {...mockData} />);

    expect(
      getByText('Thank you for your booking. We are looking forward to your visit!')
    ).toBeInTheDocument();
  });

  it('should render correct paymentConfText for pay on arrival', function () {
    mockData.data.selectedPaymentOption = paymentOptions.RESERVE_WITHOUT_CARD;
    const { getByText } = render(<RoomDetails {...mockData} />);

    expect(
      getByText('Thank you for your booking. We are looking forward to your visit!')
    ).toBeInTheDocument();
  });

  it('should render correct paymentConfText for default paymentOption', function () {
    mockData.data.selectedPaymentOption = 'RoomCard default:' as paymentOptions;
    const { getByText } = render(<RoomDetails {...mockData} />);

    expect(getByText('RoomCard default:')).toBeInTheDocument();
  });

  it('should display room details card expanded is there is only one room reservation and no expand button', function () {
    mockData.data.roomDetails = [
      {
        roomReservationStartDate: `15 Apr 2023 ${mockedActualYear}`,
        roomReservationEndDate: `16 Apr ${mockedActualYear}`,
        leadGuestTitle: 'Mr.',
        leadGuestName: 'Sergio Ponte',
        roomType: 'Twin',
        roomTypeDescription: 'double bed and sofa bed Room',
        rateType: 'Flex',
        rateTypeDescription:
          "You can amend or cancel your booking any time up to 1pm on the day you're due to arrive",
        roomGroup: '1 Adult',
        roomPrice: 100,
        roomTotalPrice: 118.0,
        ratesPerNight: [
          { pricePerNight: 123, startDate: `${mockedActualYear}-04-15`, cityTaxPerNight: 2 },
        ],
        packages: [{ computedPrice: 1, description: 'Meal 1', totalQuantity: 1, unitPrice: 1 }],
        adultMealDescription: ['1 Adult Breakfast'],
        childrenMealDescription: [],
        mealPrice: 13.5,
      },
    ];
    const { queryByTestId, queryByText } = render(<RoomDetails {...mockData} />);
    expect(queryByTestId('RoomCardInfo-room1')).toBeInTheDocument();
    expect(queryByText('Expand all')).toBeNull();
  });

  it('should return null if no data', function () {
    mockData.data.roomDetails = [];
    const { queryByTestId } = render(<RoomDetails {...mockData} />);

    expect(queryByTestId('RoomDetailsSection')).toBeNull();
  });

  it('should render expand all button', function () {
    mockData.data.roomDetails = [
      {
        roomReservationStartDate: `15 Apr 2023 ${mockedActualYear}`,
        roomReservationEndDate: `16 Apr ${mockedActualYear}`,
        leadGuestTitle: 'Mr.',
        leadGuestName: 'Sergio Ponte',
        roomType: 'Twin',
        roomTypeDescription: 'double bed and sofa bed Room',
        rateType: 'Flex',
        rateTypeDescription:
          "You can amend or cancel your booking any time up to 1pm on the day you're due to arrive",
        roomGroup: '1 Adult',
        roomPrice: 100,
        roomTotalPrice: 118.0,
        ratesPerNight: [
          { pricePerNight: 123, startDate: `${mockedActualYear}-04-15`, cityTaxPerNight: 2 },
        ],
        packages: [{ computedPrice: 1, description: 'Meal 1', totalQuantity: 1, unitPrice: 1 }],
        adultMealDescription: ['1 Adult Breakfast'],
        childrenMealDescription: [],
        mealPrice: 13.5,
      },
      {
        roomReservationStartDate: `15 Apr 2023 ${mockedActualYear}`,
        roomReservationEndDate: `16 Apr ${mockedActualYear}`,
        leadGuestTitle: 'Mr.',
        leadGuestName: 'Sergio Ponte',
        roomType: 'Twin',
        roomTypeDescription: 'double bed and sofa bed Room',
        rateType: 'Flex',
        rateTypeDescription:
          "You can amend or cancel your booking any time up to 1pm on the day you're due to arrive",
        roomGroup: '1 Adult',
        roomPrice: 100,
        roomTotalPrice: 118.0,
        ratesPerNight: [
          { pricePerNight: 123, startDate: `${mockedActualYear}-04-15`, cityTaxPerNight: 2 },
        ],
        packages: [{ computedPrice: 1, description: 'Meal 1', totalQuantity: 1, unitPrice: 1 }],
        adultMealDescription: ['1 Adult Breakfast'],
        childrenMealDescription: [],
        mealPrice: 13.5,
      },
      {
        roomReservationStartDate: `15 Apr 2023 ${mockedActualYear}`,
        roomReservationEndDate: `16 Apr ${mockedActualYear}`,
        leadGuestTitle: 'Mr.',
        leadGuestName: 'Sergio Ponte',
        roomType: 'Twin',
        roomTypeDescription: 'double bed and sofa bed Room',
        rateType: 'Flex',
        rateTypeDescription:
          "You can amend or cancel your booking any time up to 1pm on the day you're due to arrive",
        roomGroup: '1 Adult',
        roomPrice: 100,
        roomTotalPrice: 118.0,
        ratesPerNight: [
          { pricePerNight: 123, startDate: `${mockedActualYear}-04-15`, cityTaxPerNight: 2 },
        ],
        packages: [{ computedPrice: 1, description: 'Meal 1', totalQuantity: 1, unitPrice: 1 }],
        adultMealDescription: ['1 Adult Breakfast'],
        childrenMealDescription: [],
        mealPrice: 11.5,
      },
    ];
    const { getByText } = render(<RoomDetails {...mockData} />);

    expect(getByText('Expand all')).toBeInTheDocument();
  });

  it('should expand all rooms info details when button is clicked', function () {
    mockData.data.roomDetails = [
      {
        roomReservationStartDate: `15 Apr 2023 ${mockedActualYear}`,
        roomReservationEndDate: `16 Apr ${mockedActualYear}`,
        leadGuestTitle: 'Mr.',
        leadGuestName: 'Sergio Ponte',
        roomType: 'Twin',
        roomTypeDescription: 'double bed and sofa bed Room',
        rateType: 'Flex',
        rateTypeDescription:
          "You can amend or cancel your booking any time up to 1pm on the day you're due to arrive",
        roomGroup: '1 Adult',
        roomPrice: 100,
        roomTotalPrice: 118.0,
        ratesPerNight: [
          { pricePerNight: 123, startDate: `${mockedActualYear}-04-15`, cityTaxPerNight: 2 },
        ],
        packages: [{ computedPrice: 1, description: 'Meal 1', totalQuantity: 1, unitPrice: 1 }],
        adultMealDescription: ['1 Adult Breakfast'],
        childrenMealDescription: [],
        mealPrice: 13.5,
      },
      {
        roomReservationStartDate: `15 Apr 2023 ${mockedActualYear}`,
        roomReservationEndDate: `16 Apr ${mockedActualYear}`,
        leadGuestTitle: 'Mr.',
        leadGuestName: 'Sergio Ponte',
        roomType: 'Twin',
        roomTypeDescription: 'double bed and sofa bed Room',
        rateType: 'Flex',
        rateTypeDescription:
          "You can amend or cancel your booking any time up to 1pm on the day you're due to arrive",
        roomGroup: '1 Adult',
        roomPrice: 100,
        roomTotalPrice: 115.0,
        ratesPerNight: [
          { pricePerNight: 123, startDate: `${mockedActualYear}-04-15`, cityTaxPerNight: 2 },
        ],
        packages: [{ computedPrice: 1, description: 'Meal 1', totalQuantity: 1, unitPrice: 1 }],
        adultMealDescription: ['1 Adult Breakfast'],
        childrenMealDescription: [],
        mealPrice: 13.5,
      },
    ];
    const { getByText, getByTestId } = render(<RoomDetails {...mockData} />);

    const expandButton = getByText('Expand all');
    act(() => {
      fireEvent.click(expandButton);
    });
    expect(getByTestId('RoomCardInfo-room1')).toBeInTheDocument();
    expect(getByTestId('RoomCardInfo-room2')).toBeInTheDocument();
  });

  describe('Silent Substitution', () => {
    beforeEach(() => {
      jest.clearAllMocks();
    });

    const localStorageMock = getLocalStorageMock();
    Object.defineProperty(window, 'localStorage', {
      value: localStorageMock,
    });

    const SILENT_SUBSTITUTION_STORAGE_KEY = 'SilentSubstitutionRoomLabels';
    localStorageMock.setItem(
      SILENT_SUBSTITUTION_STORAGE_KEY,
      JSON.stringify({
        'MAH-6dd62772-4c14-4628-aa83-b6b18b5b7317': {
          value: [
            {
              roomLabelCode: 'Double room',
              silentSubstitution: true,
            },
            {
              roomLabelCode: 'Accessible room',
              silentSubstitution: true,
            },
          ],
          expire: 123,
        },
      })
    );

    it('should display the labels from localStorage, without room description, in <RoomCardInfo/> when feature flag is true and silentSubstitution from localstorage it`s true', () => {
      mockUseFeatureSwitch.mockReturnValue(true);
      const { getByTestId } = render(<RoomDetails {...mockData} />);

      const headerRoomCardOne = getByTestId('RoomCardHeader-room1');
      const headerRoomCardTwo = getByTestId('RoomCardHeader-room2');
      act(() => {
        fireEvent.click(headerRoomCardOne);
        fireEvent.click(headerRoomCardTwo);
      });

      const roomCardOne = getByTestId('RoomCardInfo-room1-LeftColumn-RoomDetails');
      expect(roomCardOne).toBeInTheDocument();
      expect(roomCardOne.textContent).toEqual('Double room');

      const roomCardTwo = getByTestId('RoomCardInfo-room2-LeftColumn-RoomDetails');
      expect(roomCardTwo).toBeInTheDocument();
      expect(roomCardTwo.textContent).toEqual('Accessible room');
    });

    it('should display the labels from localStorage, with room description, in <RoomCardInfo/> when feature flag is true and silentSubstitution from localstorage it`s false', () => {
      localStorageMock.clear();

      mockUseFeatureSwitch.mockReturnValue(true);
      localStorageMock.setItem(
        SILENT_SUBSTITUTION_STORAGE_KEY,
        JSON.stringify({
          'MAH-6dd62772-4c14-4628-aa83-b6b18b5b7317': {
            value: [
              {
                roomLabelCode: 'Double room',
                silentSubstitution: true,
              },
              {
                roomLabelCode: 'Accessible room',
                silentSubstitution: false,
              },
            ],
            expire: 123,
          },
        })
      );
      const { getByTestId } = render(<RoomDetails {...mockData} />);

      const headerRoomCardOne = getByTestId('RoomCardHeader-room1');
      const headerRoomCardTwo = getByTestId('RoomCardHeader-room2');
      act(() => {
        fireEvent.click(headerRoomCardOne);
        fireEvent.click(headerRoomCardTwo);
      });

      const roomCardOne = getByTestId('RoomCardInfo-room1-LeftColumn-RoomDetails');
      expect(roomCardOne).toBeInTheDocument();
      expect(roomCardOne.textContent).toEqual('Double room');

      const roomCardTwo = getByTestId('RoomCardInfo-room2-LeftColumn-RoomDetails');
      expect(roomCardTwo).toBeInTheDocument();
      expect(roomCardTwo.textContent).toEqual('Twin - double bed and sofa bed Room');
    });

    it('should display the default label, with room description, in <RoomCardInfo/> when feature flag is false', () => {
      mockUseFeatureSwitch.mockReturnValue(false);
      const { getByTestId } = render(<RoomDetails {...mockData} />);

      const headerRoomCardOne = getByTestId('RoomCardHeader-room1');
      const headerRoomCardTwo = getByTestId('RoomCardHeader-room2');
      act(() => {
        fireEvent.click(headerRoomCardOne);
        fireEvent.click(headerRoomCardTwo);
      });
      const roomCardOne = getByTestId('RoomCardInfo-room1-LeftColumn-RoomDetails');
      expect(roomCardOne).toBeInTheDocument();
      expect(roomCardOne.textContent).toEqual('Twin - double bed and sofa bed Room');

      const roomCardTwo = getByTestId('RoomCardInfo-room2-LeftColumn-RoomDetails');
      expect(roomCardTwo).toBeInTheDocument();
      expect(roomCardTwo.textContent).toEqual('Twin - double bed and sofa bed Room');
    });
  });
});
