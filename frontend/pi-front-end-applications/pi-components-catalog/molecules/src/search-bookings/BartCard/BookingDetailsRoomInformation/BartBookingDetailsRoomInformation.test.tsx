import '@testing-library/jest-dom';

import { render } from '../../../utils/test-utils';
import type { Props } from './BartBookingDetailsRoomInformation.component';
import BartBookingDetailsRoomInformationComponent from './BartBookingDetailsRoomInformation.component';

const props = {
  roomNumber: 1,
  firstName: 'first',
  lastName: 'last',
  roomName: 'double',
  noAdults: 1,
  noKids: 1,
  price: 1,
  currency: 'GBP',
  language: 'en',
  t: (value: string) => value,
} as Props;

describe('BartBookingDetailsRoomInformationComponent', () => {
  it('it should render the BartBookingDetailsRoomInformationComponent with default props', () => {
    const { getByText } = render(<BartBookingDetailsRoomInformationComponent {...props} />);

    expect(getByText('booking.summary.room 1')).toBeInTheDocument();
  });

  it('it should render the BartBookingDetailsRoomInformationComponent and render first and last name', () => {
    const { getByText } = render(<BartBookingDetailsRoomInformationComponent {...props} />);

    expect(getByText('first last')).toBeInTheDocument();
  });

  it('it should render the BartBookingDetailsRoomInformationComponent and render number of adult 1 text', () => {
    const { getByText } = render(<BartBookingDetailsRoomInformationComponent {...props} />);

    expect(getByText('- 1 account.dashboard.adult')).toBeInTheDocument();
  });

  it('it should render the BartBookingDetailsRoomInformationComponent and render number of adults 2 text', () => {
    props.noAdults = 2;
    const { getByText } = render(<BartBookingDetailsRoomInformationComponent {...props} />);

    expect(getByText('- 2 account.dashboard.adults')).toBeInTheDocument();
  });

  it('it should render the BartBookingDetailsRoomInformationComponent and render number of kids 1 text', () => {
    const { getByText } = render(<BartBookingDetailsRoomInformationComponent {...props} />);

    expect(getByText('- 1 booking.mealChoose.child')).toBeInTheDocument();
  });

  it('it should render the BartBookingDetailsRoomInformationComponent and render number of kids 2 text', () => {
    props.noKids = 2;
    const { getByText } = render(<BartBookingDetailsRoomInformationComponent {...props} />);

    expect(getByText('- 2 account.dashboard.children')).toBeInTheDocument();
  });

  it('it should render the BartBookingDetailsRoomInformationComponent and render price £1', () => {
    const { getByText } = render(<BartBookingDetailsRoomInformationComponent {...props} />);

    expect(getByText('£1')).toBeInTheDocument();
  });

  it('it should render the BartBookingDetailsRoomInformationComponent and render price 1€', () => {
    props.currency = 'EUR';
    props.language = 'de';
    const { getByText } = render(<BartBookingDetailsRoomInformationComponent {...props} />);

    expect(getByText('1€')).toBeInTheDocument();
  });

  it('it should render the BartBookingDetailsRoomInformationComponent and render price in de', () => {
    props.currency = 'EUR';
    props.language = 'en';
    const { getByText } = render(<BartBookingDetailsRoomInformationComponent {...props} />);

    expect(getByText('€1')).toBeInTheDocument();
  });
});
