import '@testing-library/jest-dom';
import { HotelBrand } from '@whitbread-eos/api';

import { render } from '../../../utils/test-utils';
import BookingSummaryRateInformation, { Props } from './BookingSummaryRateInformation';

const mockProps: Props = {
  prefixDataTestId: null,
  rate: 'FLEXRATE',
  noNights: 1,
  noRooms: 1,
  t: (key: string) => {
    switch (key) {
      default:
        return 'default';
    }
  },
};

describe('BookingSummaryRateInformation', () => {
  it('should render a <BookingSummaryRateInformation> without data-testid', function () {
    mockProps.rateDescription = 'Pay Now, No changes allowed';
    const expectedHeader = 'Pay Now, No changes allowed';

    const { getByTestId } = render(<BookingSummaryRateInformation {...mockProps} />);

    expect(getByTestId('RateInformation-Wrapper')).toBeInTheDocument();
    expect(getByTestId('RateInformation-Label')).toBeInTheDocument();
    expect(getByTestId('RateInformation-RateDescription')).toBeInTheDocument();
    expect(getByTestId('RateInformation-RateDescription').textContent).toBe(expectedHeader);
  });

  it('should render a <BookingSummaryRateInformation> with data-testid', function () {
    mockProps.prefixDataTestId = 'BookingSummary';
    mockProps.rateDescription = 'Pay Now, No changes allowed';
    const expectedHeader = 'Pay Now, No changes allowed';
    const { getByTestId } = render(<BookingSummaryRateInformation {...mockProps} />);

    expect(getByTestId('BookingSummary-RateInformation-Wrapper')).toBeInTheDocument();
    expect(getByTestId('BookingSummary-RateInformation-Label')).toBeInTheDocument();
    expect(getByTestId('BookingSummary-RateInformation-RateDescription')).toBeInTheDocument();
    expect(getByTestId('BookingSummary-RateInformation-RateDescription').textContent).toBe(
      expectedHeader
    );
  });

  it('should render a <BookingSummaryRateInformation> without brand hub', function () {
    mockProps.brand = HotelBrand.HUB;
    const { getByText } = render(<BookingSummaryRateInformation {...mockProps} />);
    expect(getByText(/hub/i)).toBeInTheDocument();
  });
});
