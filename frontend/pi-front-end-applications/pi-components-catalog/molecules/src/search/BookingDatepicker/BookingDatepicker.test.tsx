import '@testing-library/jest-dom';
import { fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { add, format } from 'date-fns';
import { act } from 'react-dom/test-utils';

import { render } from '../../utils/test-utils';
import BookingDatepickerComponent from './BookingDatepicker.component';

const defaultProps = {
  maxArrivalDate: 364,
  maxNumberOfNights: 9,
  displayDateFormat: 'dd MMM',
  partialTranslations: {
    content: {
      global: {
        done: 'Done',
        today: 'Today',
        tomorrow: 'Tomorrow',
      },
    },
    datePicker: { reset: 'Reset', checkOut: 'Check out' },
    form: {
      checkout: 'Checkout',
    },
  },
  datepickerStyles: { inputGroupStyles: {}, datepickerInputElementStyles: {}, iconStyles: {} },
  onSelectDates: jest.fn(),
  handleSetNumberOfNights: jest.fn(),
  locale: 'en',
  isLessThanSm: undefined,
  displayDatesNotification: false,
};

const clickCalendarDay = async (container: HTMLElement, date: Date) => {
  const dayElements = container.querySelectorAll('.react-datepicker__day');
  const targetDay = Array.from(dayElements).find((el) => {
    const ariaLabel = el.getAttribute('aria-label');
    return ariaLabel && ariaLabel.includes(format(date, 'MMMM d'));
  });

  if (targetDay) {
    await act(async () => {
      fireEvent.click(targetDay!);
    });
  }
};

describe('BookingDatepicker', () => {
  beforeEach(() => {
    jest.useFakeTimers();
    jest.setSystemTime(new Date(2026, 3, 15));
  });

  afterEach(() => {
    jest.useRealTimers();
  });

  it('should render the BookingDatepicker component', () => {
    const { getByRole } = render(<BookingDatepickerComponent {...defaultProps} />);
    expect(getByRole('textbox')).toBeInTheDocument();
  });

  it('should select dates', async () => {
    const { getByRole, container } = render(<BookingDatepickerComponent {...defaultProps} />);
    const input = getByRole('textbox');

    const startDate = add(new Date(), { days: 3 });
    const endDate = add(startDate, { days: 4 });

    // Open calendar
    await act(async () => {
      fireEvent.click(input);
    });

    await clickCalendarDay(container, startDate);

    await clickCalendarDay(container, endDate);

    const startDateFormatted = format(startDate, 'dd MMM');
    const endDateFormatted = format(endDate, 'dd MMM');
    expect(input).toHaveValue(`${startDateFormatted} | ${endDateFormatted}`);
  });

  it('should let to select up to 1 day if the user selects the latest 364th available arrivalDate from today', async () => {
    const { getByRole, container } = render(<BookingDatepickerComponent {...defaultProps} />);
    const input = getByRole('textbox');

    const startDate = add(new Date(), { days: 364 });

    // Open calendar
    await act(async () => {
      fireEvent.click(input);
    });

    const nextButton = container.querySelector('.react-datepicker__navigation--next');
    for (let i = 0; i < 12; i++) {
      await act(async () => {
        fireEvent.click(nextButton!);
      });
    }

    // Click the date
    await clickCalendarDay(container, startDate);

    const startDateFormatted = format(startDate, 'dd MMM');
    expect(input).toHaveValue(`${startDateFormatted} | Check out`);
  });
  it('should display the alert message as default if the display notification value is true and no value is selected', () => {
    const { getByRole } = render(
      <BookingDatepickerComponent {...defaultProps} displayDatesNotification={true} />
    );

    expect(getByRole('status')).toBeInTheDocument();
  });
  it('should not display the alert message if display notification value is false', () => {
    const { queryByRole } = render(
      <BookingDatepickerComponent {...defaultProps} displayDatesNotification={false} />
    );

    expect(queryByRole('alert')).not.toBeInTheDocument();
  });

  describe('disableFlip prop', () => {
    it('should render correctly with disableFlip={true}', () => {
      const { getByRole } = render(
        <BookingDatepickerComponent {...defaultProps} disableFlip={true} />
      );
      expect(getByRole('textbox')).toBeInTheDocument();
    });

    it('should open the calendar when input is clicked with disableFlip={true}', async () => {
      const { container, getByRole } = render(
        <BookingDatepickerComponent {...defaultProps} disableFlip={true} />
      );
      const input = getByRole('textbox');

      await userEvent.click(input);

      expect(container.querySelector('.react-datepicker')).toBeInTheDocument();
    });

    it('should render correctly without disableFlip prop', () => {
      const { getByRole } = render(<BookingDatepickerComponent {...defaultProps} />);
      expect(getByRole('textbox')).toBeInTheDocument();
    });
  });
});
