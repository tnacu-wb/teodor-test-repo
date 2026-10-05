import '@testing-library/jest-dom';
import { paymentOptions } from '@whitbread-eos/api';

import { render } from '../../../utils/test-utils';
import type { Props } from './BartBookingDetailsTotalCost.component';
import BartBookingDetailsTotalCostComponent from './BartBookingDetailsTotalCost.component';

const props = {
  rateDescription: 'description',
  currency: 'GBP',
  language: 'en',
  donationPkg: { amount: 0, currencyCode: 'EUR' },
  totalCost: { amount: 2, currencyCode: 'EUR' },
  balanceOutstanding: { amount: 3, currencyCode: 'EUR' },
  previousTotal: { amount: 4, currencyCode: 'EUR' },
  shouldDisplayCityTaxMessage: true,
  paymentOption: '',
  baseDataTestId: '',
} as Props;

describe('BartBookingDetailsTotalCostComponent', () => {
  beforeEach(() => {
    jest.resetAllMocks();
  });

  it('it should render the BartBookingDetailsTotalCostComponent', () => {
    const { queryByText } = render(<BartBookingDetailsTotalCostComponent {...props} />);

    expect(queryByText('dashboard.bookings.totalCost')).toBeFalsy();
  });

  it('it should render the BartBookingDetailsTotalCostComponent for Pay now option and EUR currency code', () => {
    props.paymentOption = paymentOptions.PAY_NOW;
    const { getByText, getAllByText, queryByText } = render(
      <BartBookingDetailsTotalCostComponent {...props} />
    );

    expect(getAllByText('dashboard.bookings.balancePaid').length).toBe(2);
    expect(getByText('dashboard.bookings.outstandingBalance')).toBeInTheDocument();
    expect(getByText('dashboard.bookings.toPayOnArrival')).toBeInTheDocument();
    expect(getByText('dashboard.bookings.totalCost')).toBeInTheDocument();
    expect(getByText('dashboard.bookings.totalCost')).toBeInTheDocument();
    expect(queryByText('booking.overview.includeCityTax')).toBeTruthy();
    expect(getByText('€2.00')).toBeInTheDocument();
    expect(getByText('€3.00')).toBeInTheDocument();
    expect(getByText('€4.00')).toBeInTheDocument();
  });

  it('it should render the BartBookingDetailsTotalCostComponent for Pay now option with GDP currency code', () => {
    props.balanceOutstanding.currencyCode = 'GBP';
    props.totalCost.currencyCode = 'GBP';
    props.previousTotal.currencyCode = 'GBP';
    const { getByText } = render(<BartBookingDetailsTotalCostComponent {...props} />);

    expect(getByText('£2.00')).toBeInTheDocument();
    expect(getByText('£3.00')).toBeInTheDocument();
    expect(getByText('£4.00')).toBeInTheDocument();
  });

  it('it should render the BartBookingDetailsTotalCostComponent for Pay now option with donation package', () => {
    props.donationPkg = { amount: 1, currencyCode: 'GBP' };
    const { getByText } = render(<BartBookingDetailsTotalCostComponent {...props} />);

    expect(getByText('account.dashboard.gosh')).toBeInTheDocument();
    expect(getByText('£1.00')).toBeInTheDocument();
  });

  it('it should render the BartBookingDetailsTotalCostComponent and render rate description', () => {
    const { getByText } = render(<BartBookingDetailsTotalCostComponent {...props} />);

    expect(getByText('description')).toBeInTheDocument();
  });

  it('it should render the BartBookingDetailsTotalCostComponent for reserved without card option and EUR currency code', () => {
    props.paymentOption = paymentOptions.RESERVE_WITHOUT_CARD;
    const { getByText, getByTestId, queryByText } = render(
      <BartBookingDetailsTotalCostComponent {...props} />
    );

    expect(getByTestId('pay-on-arrival-total-cost-label')).toBeInTheDocument();
    expect(getByText('£1.00')).toBeInTheDocument();

    expect(queryByText('booking.overview.includeCityTax')).toBeTruthy();
  });

  it('it should render the BartBookingDetailsTotalCostComponent for Pay on arrival option and EUR currency code', () => {
    props.paymentOption = paymentOptions.PAY_ON_ARRIVAL;
    const { getByText, getByTestId, queryByText } = render(
      <BartBookingDetailsTotalCostComponent {...props} />
    );

    expect(getByTestId('pay-on-arrival-total-cost-label')).toBeInTheDocument();
    expect(getByText('£1.00')).toBeInTheDocument();

    expect(queryByText('booking.overview.includeCityTax')).toBeTruthy();
  });
});
