import '@testing-library/jest-dom';

import { fireEvent, render } from '../../utils/test-utils';
import RoomCardHeader from './RoomCardHeader.component';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: () => ({
    locale: 'en',
  }),
}));

const mockData = {
  roomReservationStartDate: '15 Apr 2022',
  roomReservationEndDate: '16 Apr 2022',
  roomTotalPrice: 118,
  currency: 'GBP',
  currentLang: 'en',
  roomNumber: 1,
  showRoomCardInfo: false,

  cardsNumberExpanded: [
    { roomNumber: 1, expanded: false },
    { roomNumber: 2, expanded: false },
  ],
  handleExpandCollapse: jest.fn(),
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
        return 'Room[roomNumber]';
      default:
        return 'RoomCard default';
    }
  },
};

describe('RoomCardHeader', () => {
  it('should render RoomCardHeader corectly', function () {
    const { getByText, getByTestId } = render(<RoomCardHeader {...mockData} />);

    expect(getByTestId('RoomCardHeader-room1')).toBeInTheDocument();
    expect(getByText('15 Apr - 16 Apr')).toBeInTheDocument();
    expect(getByTestId('svg-container')).toBeInTheDocument();
  });

  it('should toggle card info on header click', function () {
    const onHeaderClick = jest.fn();
    mockData.handleExpandCollapse = onHeaderClick;
    const { queryByTestId, getByTestId } = render(<RoomCardHeader {...mockData} />);

    expect(queryByTestId('RoomCardInfo-room1')).toBe(null);
    fireEvent.click(getByTestId('RoomCardHeader-room1'));
    expect(onHeaderClick).toHaveBeenCalledTimes(1);
  });

  it('should formatDates if lang is de', function () {
    mockData.currentLang = 'de';

    const { getByText } = render(<RoomCardHeader {...mockData} />);

    expect(getByText('15 Apr. - 16 Apr.')).toBeInTheDocument();
  });
});
