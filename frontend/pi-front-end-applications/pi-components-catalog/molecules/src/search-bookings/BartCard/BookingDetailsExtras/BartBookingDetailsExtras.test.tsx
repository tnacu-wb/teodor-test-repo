import '@testing-library/jest-dom';

import { render } from '../../../utils/test-utils';
import type { Props } from './BartBookingDetailsExtras.component';
import BartBookingDetailsExtra from './BartBookingDetailsExtras.component';

const props = {
  packages: [
    {
      cost: { amount: 1, currencyCode: 'GBP' },
      days: 1,
      id: '17',
      name: 'Meal Deal',
      qty: 1,
    },
  ],
  language: 'en',
  t: (value: string) => value,
} as Props;

describe('BartBookingDetailsExtrasComponent', () => {
  it('it should render the BartBookingDetailsExtrasComponent with default props', () => {
    const { getByText } = render(<BartBookingDetailsExtra {...props} />);

    expect(getByText('ccui.manageBooking.meals')).toBeInTheDocument();
  });

  it('it should render the BartBookingDetailsExtrasComponent and render first and last name', () => {
    const { getByText } = render(<BartBookingDetailsExtra {...props} />);

    expect(getByText('Meal Deal')).toBeInTheDocument();
  });

  it('it should render the BartBookingDetailsExtrasComponent and render price £1', () => {
    const { getByText } = render(<BartBookingDetailsExtra {...props} />);

    expect(getByText('£1')).toBeInTheDocument();
  });

  it('it should render the BartBookingDetailsExtrasComponent and render price 1€', () => {
    props.packages[0].cost.currencyCode = 'EUR';
    props.language = 'de';
    const { getByText } = render(<BartBookingDetailsExtra {...props} />);

    expect(getByText('1€')).toBeInTheDocument();
  });

  it('it should render the BartBookingDetailsExtrasComponent and render price in de', () => {
    props.packages[0].cost.currencyCode = 'EUR';
    props.language = 'en';
    const { getByText } = render(<BartBookingDetailsExtra {...props} />);

    expect(getByText('€1')).toBeInTheDocument();
  });
});
