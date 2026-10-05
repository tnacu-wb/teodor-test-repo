import '@testing-library/jest-dom';
import { BC_RESERVATION_STATUS } from '@whitbread-eos/api';

import { render } from '../../../../../utils/test-utils';
import type { Props } from './BookingDetailsRoomInformation.component';
import BookingDetailsRoomInformationComponent from './BookingDetailsRoomInformation.component';

const props = {
  roomNumber: 1,
  noAdults: 1,
  currencyCode: 'GBP',
  language: 'en',
  noNights: 1,
  leadGuestName: 'name',
  roomType: 'double',
  noChildren: 1,
  cot: false,
  roomPrice: 1,
  bookingStatus: BC_RESERVATION_STATUS.COMPLETED,
  childrenMealDescription: [],
  adultMealDescription: [{ price: 1, noSelections: 1, id: '1', title: 'mealName' }],
} as Props;

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

describe('BookingDetailsRoomInformationComponent', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('it should render the BookingDetailsRoomInformationComponent with default props', () => {
    const { getByText } = render(<BookingDetailsRoomInformationComponent {...props} />);

    expect(getByText('booking.summary.room 1')).toBeInTheDocument();
  });

  it('it should render the BookingDetailsRoomInformationComponent and render number of adult 1 text', () => {
    const { getByText } = render(<BookingDetailsRoomInformationComponent {...props} />);

    expect(getByText('- 1 account.dashboard.adult')).toBeInTheDocument();
  });

  it('it should render the BookingDetailsRoomInformationComponent and render number of adults 2 text', () => {
    props.noAdults = 2;
    const { getByText } = render(<BookingDetailsRoomInformationComponent {...props} />);

    expect(getByText('- 2 account.dashboard.adults')).toBeInTheDocument();
  });

  it('it should render the BookingDetailsRoomInformationComponent and render number of kids 1 text', () => {
    const { getByText } = render(<BookingDetailsRoomInformationComponent {...props} />);

    expect(getByText(', 1 account.dashboard.child')).toBeInTheDocument();
  });

  it('it should render the BookingDetailsRoomInformationComponent and render number of kids 2 text', () => {
    props.noChildren = 2;
    const { getByText } = render(<BookingDetailsRoomInformationComponent {...props} />);

    expect(getByText(', 2 account.dashboard.children')).toBeInTheDocument();
  });
});
