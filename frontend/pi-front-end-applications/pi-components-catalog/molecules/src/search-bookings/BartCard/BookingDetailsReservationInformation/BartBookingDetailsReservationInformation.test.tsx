import '@testing-library/jest-dom';

import { render } from '../../../utils/test-utils';
import type { Props } from './BartBookingDetailsReservationInformation.component';
import BartBookingDetailsReservationInformation from './BartBookingDetailsReservationInformation.component';

const props = {
  bookingType: 'Leisure',
  sourcePms: 'OPERA',
  rateType: 'Flex',
  t: (value: string) => value,
} as Props;

describe('BartBookingDetailsReservationInformation', () => {
  it('it should render the BartBookingDetailsReservationInformation with default props', () => {
    const { getByText } = render(<BartBookingDetailsReservationInformation {...props} />);

    expect(getByText(': Leisure')).toBeInTheDocument();
  });

  it('it should render the BartBookingDetailsReservationInformation and render Business as booking type', () => {
    props.bookingType = 'Business';
    const { getByText } = render(<BartBookingDetailsReservationInformation {...props} />);

    expect(getByText(': Business')).toBeInTheDocument();
  });

  it('it should render the BartBookingDetailsReservationInformation and render Bart as source', () => {
    props.sourcePms = 'Bart';
    const { getByText } = render(<BartBookingDetailsReservationInformation {...props} />);

    expect(getByText('Bart')).toBeInTheDocument();
  });

  it('it should render the BartBookingDetailsReservationInformation and isBart true', () => {
    props.isBart = true;
    const { getByText } = render(<BartBookingDetailsReservationInformation {...props} />);

    expect(getByText('Bart')).toBeInTheDocument();
  });

  it('it should render the BartBookingDetailsReservationInformation and render Standard as rate type', () => {
    props.rateType = 'Standard';
    const { getByText } = render(<BartBookingDetailsReservationInformation {...props} />);

    expect(getByText(': Standard')).toBeInTheDocument();
  });
});
