import '@testing-library/jest-dom';

import { render } from '../../../utils/test-utils';
import BookingSummaryStayDatesInformation, { Props } from './BookingSummaryStayDatesInformation';

const mockProps: Props = {
  prefixDataTestId: null,
  arrivalDate: '2022-06-10',
  departureDate: '2022-06-11',
  noNights: 2,
  t: (key: string) => {
    switch (key) {
      case 'booking.summary.night':
        return 'booking.summary.night';
      case 'booking.summary.nights':
        return 'booking.summary.nights';
      default:
        return 'default';
    }
  },
};

describe('BookingSummaryStayDatesInformation', () => {
  it('should render a <BookingSummaryStayDatesInformation> without data-testid', function () {
    const { getByTestId } = render(<BookingSummaryStayDatesInformation {...mockProps} />);

    expect(getByTestId('StayDatesInformation-Wrapper')).toBeInTheDocument();
    expect(getByTestId('StayDatesInformation-ArrivalDate')).toBeInTheDocument();
    expect(getByTestId('StayDatesInformation-DepartureDate')).toBeInTheDocument();
    expect(getByTestId('StayDatesInformation-NightsNumber')).toBeInTheDocument();
  });

  it('should render a <BookingSummaryStayDatesInformation> without data-testid', function () {
    mockProps.prefixDataTestId = 'BookingSummary';
    const { getByTestId } = render(<BookingSummaryStayDatesInformation {...mockProps} />);

    expect(getByTestId('BookingSummary-StayDatesInformation-Wrapper')).toBeInTheDocument();
    expect(getByTestId('BookingSummary-StayDatesInformation-ArrivalDate')).toBeInTheDocument();
    expect(getByTestId('BookingSummary-StayDatesInformation-DepartureDate')).toBeInTheDocument();
    expect(getByTestId('BookingSummary-StayDatesInformation-NightsNumber')).toBeInTheDocument();
  });

  it('should render a <BookingSummaryStayDatesInformation> correctly with given props', function () {
    const { getByTestId } = render(<BookingSummaryStayDatesInformation {...mockProps} />);

    const expectArrivalDate = 'Fri 10 Jun 2022';
    expect(getByTestId('BookingSummary-StayDatesInformation-ArrivalDate').textContent).toEqual(
      expectArrivalDate
    );

    const expectDepartureDate = 'Sat 11 Jun 2022';
    expect(getByTestId('BookingSummary-StayDatesInformation-DepartureDate').textContent).toEqual(
      expectDepartureDate
    );

    const expectNoNights = '2 booking.summary.nights';
    expect(getByTestId('BookingSummary-StayDatesInformation-NightsNumber').textContent).toEqual(
      expectNoNights
    );
  });

  it('should render a <BookingSummaryStayDatesInformation> correctly with given props in de ', function () {
    const { getByText } = render(
      <BookingSummaryStayDatesInformation {...mockProps} language="de" />
    );
    expect(getByText('Fr. 10 Juni 2022')).toBeInTheDocument();

    expect(getByText('Sa. 11 Juni 2022')).toBeInTheDocument();
  });

  it('should render a <BookingSummaryStayDatesInformation> correctly with singular label for number of nights', function () {
    const { getByTestId } = render(
      <BookingSummaryStayDatesInformation {...mockProps} noNights={1} />
    );

    const expectNoNights = '1 booking.summary.night';
    expect(getByTestId('BookingSummary-StayDatesInformation-NightsNumber').textContent).toEqual(
      expectNoNights
    );
  });
});
