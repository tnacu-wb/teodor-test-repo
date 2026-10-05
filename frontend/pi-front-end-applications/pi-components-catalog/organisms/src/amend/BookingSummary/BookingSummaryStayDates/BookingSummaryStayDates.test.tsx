import '@testing-library/jest-dom';
import { formatDate } from '@whitbread-eos/utils';

import { mockedStayDatesLabels } from '../../../mockData/mockResponse';
import { render, screen } from '../../../utils/test-utils';
import { BookingSummaryStayDates } from './BookingSummaryStayDates';

const initialProps = {
  language: 'en',
  baseDataTestId: 'amend-booking-summary',
  arrivalDate: '2023-07-18',
  departureDate: '2023-07-22',
  originalArrivalDate: '2023-07-18',
  originalDepartureDate: '2023-07-22',
  labels: mockedStayDatesLabels,
};

const dateFormat = 'EEE dd MMM yyyy';

describe('Booking summary stay dates', () => {
  it('should render the component', () => {
    const { getByTestId } = render(<BookingSummaryStayDates {...initialProps} />);
    expect(getByTestId(`${initialProps.baseDataTestId}-stay-dates-wrapper`)).toBeInTheDocument();
  });
  it('should render the dates twice if the interval is not the same', () => {
    const props = {
      ...initialProps,
      arrivalDate: '2023-08-18',
      departureDate: '2023-08-22',
    };
    render(<BookingSummaryStayDates {...props} />);

    const interval = `${formatDate(
      initialProps.arrivalDate,
      dateFormat,
      initialProps.language
    )} - ${formatDate(initialProps.departureDate, dateFormat, initialProps.language)}`;

    const originalInterval = `${formatDate(
      initialProps.originalArrivalDate,
      dateFormat,
      initialProps.language
    )} - ${formatDate(initialProps.originalDepartureDate, dateFormat, initialProps.language)}`;

    expect(screen.getByText(interval)).toBeInTheDocument();
    expect(screen.getByText(originalInterval)).toBeInTheDocument();
    expect(screen.getByText(originalInterval)).toHaveStyle(`text-decoration: line-through`);
  });
  it('should display the number of nights properly', () => {
    let props = {
      ...initialProps,
      departureDate: '2023-07-19',
    };

    const { rerender } = render(<BookingSummaryStayDates {...props} />);
    expect(screen.getByText('1 night')).toBeInTheDocument();

    props = {
      ...props,
      departureDate: '2023-07-20',
    };

    rerender(<BookingSummaryStayDates {...props} />);
    expect(screen.getByText('2 nights')).toBeInTheDocument();
  });
});
