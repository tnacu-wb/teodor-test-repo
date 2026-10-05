import '@testing-library/jest-dom';

import { fireEvent, render } from '../../utils/test-utils';
import RoomCard from './RoomCard.component';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: () => ({
    locale: 'en',
  }),
}));

const mockData = {
  room: {
    roomReservationStartDate: '15 Apr 2022',
    roomReservationEndDate: '16 Apr 2022',
    leadGuestTitle: 'Mr.',
    leadGuestName: 'Sergio Ponte',
    roomType: 'Twin',
    roomTypeDescription: 'double bed and sofa bed Room',
    rateType: 'Flex',
    rateTypeDescription:
      "You can amend or cancel your booking any time up to 1pm on the day you're due to arrive",
    roomGroup: '1 Adult, 1 Child',
    ratesPerNight: [{ pricePerNight: 123, startDate: '2022-09-09', cityTaxPerNight: 2 }],
    roomTotalPrice: 118,
    adultMealDescription: ['1 Adult Premier Inn Breakfast'],
    childrenMealDescription: ['1 Child Free breakfast for kids'],
    mealPrice: 18,
  },

  currency: 'GBP',
  roomNumber: 1,
  currentLang: 'en',
  taxesMessage: 'Price includes taxes and fees',
  t: (key: string) => {
    switch (key) {
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
      default:
        return 'RoomCard default';
    }
  },
  isCardExpanded: true,
  cardsExpanded: [
    { roomNumber: 1, expanded: true },
    { roomNumber: 2, expanded: true },
  ],
  setCardsExpanded: jest.fn(),
  handleExpandCollapse: jest.fn(),
};

describe('RoomCard', () => {
  it('should render RoomCard corectly', function () {
    const { getByText, getByTestId } = render(<RoomCard {...mockData} />);

    expect(getByTestId('RoomCardHeader-room1')).toBeInTheDocument();
    expect(getByText('15 Apr - 16 Apr')).toBeInTheDocument();
  });

  it('should display full room info on header click', function () {
    const { getByTestId } = render(<RoomCard {...mockData} />);

    fireEvent.click(getByTestId('RoomCardHeader-room1'));
    expect(getByTestId('RoomCardInfo-room1')).toBeInTheDocument();
  });
});
