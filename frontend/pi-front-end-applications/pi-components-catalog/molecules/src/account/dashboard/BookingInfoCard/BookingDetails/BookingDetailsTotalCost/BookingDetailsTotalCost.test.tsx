import '@testing-library/jest-dom';

import { render } from '../../../../../utils/test-utils';
import type { Props } from './BookingDetailsTotalCost.component';
import BookingDetailsTotalCostComponent from './BookingDetailsTotalCost.component';

const props = {
  currency: 'GBP',
  totalCost: '1',
  newTotal: '1',
  previousTotal: '2',
  balanceOutstanding: '3',
  paymentOption: 'PAY_NOW',
  donationPkg: {
    currency: 'GBP',
    code: '5',
    unitPrice: 5,
  },
  shouldDisplayCityTaxMessage: true,
  paidWithPiba: false,
} as Props;

const mockCustomLocale = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: () => mockCustomLocale(),
}));

describe('BookingDetailsTotalCostComponent', () => {
  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      country: 'gb',
    });
  });

  it('it should render the BookingDetailsTotalCostComponent with paymentOption PAY_NOW', () => {
    const { getByText } = render(<BookingDetailsTotalCostComponent {...props} />);

    expect(getByText('£1.00')).toBeInTheDocument();
    expect(getByText('£2.00')).toBeInTheDocument();
    expect(getByText('£3.00')).toBeInTheDocument();
    expect(getByText('£5.00')).toBeInTheDocument();
    expect(getByText('dashboard.bookings.toPayOnArrival')).toBeInTheDocument();
    expect(getByText('dashboard.bookings.outstandingBalance')).toBeInTheDocument();
    expect(getByText('dashboard.bookings.previousTotal')).toBeInTheDocument();
    expect(getByText('account.dashboard.gosh')).toBeInTheDocument();
    expect(getByText('dashboard.bookings.newTotal')).toBeInTheDocument();
    expect(getByText('dashboard.bookings.balancePaid')).toBeInTheDocument();
    expect(getByText('booking.overview.includeCityTax')).toBeInTheDocument();
  });

  it('it should render the BookingDetailsTotalCostComponent with paymentOption PAY_NOW on de country', () => {
    mockCustomLocale.mockReturnValue({
      country: 'de',
    });

    const { getByText, queryByText } = render(<BookingDetailsTotalCostComponent {...props} />);

    expect(getByText('£1.00')).toBeInTheDocument();
    expect(getByText('£2.00')).toBeInTheDocument();
    expect(getByText('£3.00')).toBeInTheDocument();
    expect(queryByText('£5.00')).toBeFalsy();
    expect(getByText('dashboard.bookings.toPayOnArrival')).toBeInTheDocument();
    expect(getByText('dashboard.bookings.outstandingBalance')).toBeInTheDocument();
    expect(getByText('dashboard.bookings.previousTotal')).toBeInTheDocument();
    expect(queryByText('account.dashboard.gosh')).toBeFalsy();
    expect(getByText('dashboard.bookings.newTotal')).toBeInTheDocument();
    expect(getByText('dashboard.bookings.balancePaid')).toBeInTheDocument();
    expect(queryByText('booking.overview.includeCityTax')).toBeTruthy();
  });

  it('it should render the BookingDetailsTotalCostComponent  with paymentOption PAY_ON_ARRIVAL', () => {
    props.paymentOption = 'PAY_ON_ARRIVAL';
    const { getByText, queryByText } = render(<BookingDetailsTotalCostComponent {...props} />);

    expect(getByText('dashboard.bookings.totalCost')).toBeInTheDocument();
    expect(getByText('account.dashboard.gosh')).toBeInTheDocument();
    expect(getByText('£1.00')).toBeInTheDocument();
    expect(getByText('dashboard.bookings.paymentOnArrival')).toBeInTheDocument();
    expect(queryByText('£2.00')).toBeFalsy();
    expect(queryByText('£3.00')).toBeFalsy();
    expect(queryByText('£5.00')).toBeTruthy();
  });

  it('it should render the BookingDetailsTotalCostComponent  with paymentOption PAY_ON_ARRIVAL on de country', () => {
    props.paymentOption = 'PAY_ON_ARRIVAL';
    mockCustomLocale.mockReturnValue({
      country: 'de',
    });

    const { getByText, queryByText } = render(<BookingDetailsTotalCostComponent {...props} />);

    expect(getByText('dashboard.bookings.totalCost')).toBeInTheDocument();
    expect(queryByText('account.dashboard.gosh')).toBeFalsy();
    expect(getByText('£1.00')).toBeInTheDocument();
    expect(getByText('dashboard.bookings.paymentOnArrival')).toBeInTheDocument();

    expect(queryByText('£2.00')).toBeFalsy();
    expect(queryByText('£3.00')).toBeFalsy();
    expect(queryByText('£5.00')).toBeFalsy();

    expect(queryByText('booking.overview.includeCityTax')).toBeTruthy();
  });

  it('it should render the BookingDetailsTotalCostComponent  with paymentOption RESERVE_WITHOUT_CARD', () => {
    props.paymentOption = 'RESERVE_WITHOUT_CARD';
    const { getByText, queryByText } = render(<BookingDetailsTotalCostComponent {...props} />);

    expect(getByText('dashboard.bookings.totalCost')).toBeInTheDocument();
    expect(getByText('£1.00')).toBeInTheDocument();
    expect(getByText('account.dashboard.gosh')).toBeInTheDocument();
    expect(getByText('dashboard.bookings.paymentOnArrival')).toBeInTheDocument();

    expect(queryByText('£2.00')).toBeFalsy();
    expect(queryByText('£3.00')).toBeFalsy();
    expect(queryByText('£5.00')).toBeTruthy();

    expect(queryByText('booking.overview.includeCityTax')).toBeTruthy();
  });

  it('it should render the BookingDetailsTotalCostComponent  without donation', () => {
    props.donationPkg = undefined;
    props.paymentOption = 'RESERVE_WITHOUT_CARD';

    const { getByText, queryByText } = render(<BookingDetailsTotalCostComponent {...props} />);

    expect(getByText('dashboard.bookings.totalCost')).toBeInTheDocument();
    expect(getByText('£1.00')).toBeInTheDocument();
    expect(queryByText('account.dashboard.gosh')).toBeFalsy();
    expect(getByText('dashboard.bookings.paymentOnArrival')).toBeInTheDocument();
    expect(queryByText('£2.00')).toBeFalsy();
    expect(queryByText('£3.00')).toBeFalsy();
    expect(queryByText('£5.00')).toBeFalsy();
  });

  it('it should render the BookingDetailsTotalCostComponent with default props', () => {
    props.paymentOption = 'default';
    const { queryByText } = render(<BookingDetailsTotalCostComponent {...props} />);

    expect(queryByText('£1.00')).toBeFalsy();
  });

  it('it should render the BookingDetailsTotalCostComponent with shouldDisplayCityTaxMessage false', () => {
    props.shouldDisplayCityTaxMessage = false;
    const { queryByText } = render(<BookingDetailsTotalCostComponent {...props} />);

    expect(queryByText('booking.overview.includeCityTax')).toBeFalsy();
  });

  it('it should render the BookingDetailsTotalCostComponent and display pre auth send text when paidWithPiba true and FUTURE reservation', () => {
    props.paidWithPiba = true;
    props.bookingStatus = 'FUTURE';
    props.paymentOption = 'PAY_ON_ARRIVAL';

    const { getByTestId } = render(<BookingDetailsTotalCostComponent {...props} />);
    const preauthText = getByTestId('preauth-text');
    expect(preauthText.textContent).toEqual('dashboard.bookings.preAuthorisedSpend');
  });

  it('it should render the BookingDetailsTotalCostComponent and display pre auth send text when paidWithPiba true and PAST reservation', () => {
    props.paidWithPiba = true;
    props.bookingStatus = 'PAST';

    const { getByTestId } = render(<BookingDetailsTotalCostComponent {...props} />);
    const preauthText = getByTestId('preauth-text');
    expect(preauthText.textContent).toEqual('dashboard.bookings.preAuthorisedSpend');
  });

  it('it should render the BookingDetailsTotalCostComponent and not display dinner allowance', () => {
    const { queryByTestId } = render(<BookingDetailsTotalCostComponent {...props} />);
    expect(queryByTestId('dinner-allowance-text')).not.toBeInTheDocument();
  });

  it('it should render the BookingDetailsTotalCostComponent and display dinner allowance', () => {
    props.dinnerAllowance = {
      amount: 3333,
      currency: 'GBP',
    };

    const { getByTestId } = render(<BookingDetailsTotalCostComponent {...props} />);
    const text = getByTestId('dinner-allowance-text');
    expect(text.textContent).toEqual('dashboard.bookings.dinnerAllowance - £3333.00');
  });

  it('it should render the BookingDetailsTotalCostComponent with paidWithPiba', () => {
    props.paidWithPiba = true;
    props.bookingStatus = 'FUTURE';
    props.paymentOption = 'PAY_NOW';

    const { getByText } = render(<BookingDetailsTotalCostComponent {...props} />);
    expect(getByText('dashboard.bookings.preAuthorisedSpend')).toBeInTheDocument();
  });

  it('it should render the BookingDetailsTotalCostComponent with paidWithPiba undefined', () => {
    props.paidWithPiba = undefined;

    const { queryByText } = render(<BookingDetailsTotalCostComponent {...props} />);
    expect(queryByText('dashboard.bookings.preAuthorisedSpend')).toBeFalsy();
  });

  it('should render the toPayOnArrival label while is on AmendConfirmationPage', () => {
    const { getByText } = render(<BookingDetailsTotalCostComponent {...props} isAmendPage />);
    expect(getByText('dashboard.bookings.toPayOnArrival')).toBeInTheDocument();
  });

  it('should render the refund label while is on AmendConfirmationPage and the balanceOutstanding is lower than 0', () => {
    props.balanceOutstanding = '-20.0';

    const { getByText } = render(<BookingDetailsTotalCostComponent {...props} isAmendPage />);
    expect(getByText('amend.refundTerms')).toBeInTheDocument();
  });

  it('it should render the BookingDetailsTotalCostComponent and display Balance Paid label', () => {
    const { queryByTestId } = render(<BookingDetailsTotalCostComponent {...props} />);
    expect(queryByTestId('balanceStatus')).toBeInTheDocument();
  });

  it('it should render the BookingDetailsTotalCostComponent and hide Balance Paid label', () => {
    props.newTotal = '5';
    const { queryByTestId } = render(<BookingDetailsTotalCostComponent {...props} />);
    expect(queryByTestId('balanceStatus')).not.toBeInTheDocument();
  });
});
