import '@testing-library/jest-dom';

import {
  mockedSummaryOfPaymentsData,
  mockedSummaryOfPaymentsLabels,
} from '../../../mockData/mockResponse';
import { render } from '../../../utils/test-utils';
import { BookingSummaryCost } from './BookingSummaryCost';

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => ({
    t: () => jest.fn(),
  }),
}));

const initialProps = {
  language: 'en',
  baseDataTestId: 'amend-booking-summary',
  currency: 'GBP',
  summaryOfPayments: mockedSummaryOfPaymentsData,
  summaryOfPaymentsLabels: mockedSummaryOfPaymentsLabels,
};

describe('BookingSummaryCost component', () => {
  it('should render the component', () => {
    const { getByTestId } = render(<BookingSummaryCost {...initialProps} />);
    const bookingSummary = getByTestId('amend-booking-summary-of-payments');
    expect(bookingSummary).toBeInTheDocument();
  });

  it('should display non-refundable label', () => {
    initialProps.summaryOfPayments.nonRefundable = 10.5;
    initialProps.summaryOfPayments.payOnArrival = 0;
    initialProps.summaryOfPayments.balanceAuthorised = 100.5;
    const { getByText } = render(<BookingSummaryCost {...initialProps} />);
    expect(getByText(initialProps.summaryOfPaymentsLabels.nonRefundable)).toBeVisible();
  });
});
