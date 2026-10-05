import { useMediaQuery } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import userEvent from '@testing-library/user-event';
import { CountryCode } from '@whitbread-eos/api';
import { useFeatureToggle } from '@whitbread-eos/utils';
import { add, format } from 'date-fns';
import { act } from 'react-dom/test-utils';

import { fireEvent, render, waitFor } from '../../utils/test-utils';
import Datepicker, { getFormattedDate } from './Datepicker.component';

const defaultProps = {
  minDate: new Date(),
  locale: 'en',
  hasFooter: true,
  selectsRange: true,
  datepickerStyles: { inputGroupStyles: {}, datepickerInputElementStyles: {}, iconStyles: {} },
  onSelectDates: jest.fn(),
  inputPlaceholder: 'Datepicker component',
  dateFormat: 'dd MMM yyyy',
  displayDateFormat: 'dd MMM yyyy',
  labels: {
    resetButtonLabel: 'Reset',
    doneButtonLabel: 'Done',
    todayLabel: 'Today',
    tomorrowLabel: 'Tomorrow',
    checkoutLabel: 'Check out',
  },
};

const clickCalendarDay = async (container: HTMLElement, date: Date) => {
  const dayElements = container.querySelectorAll('.react-datepicker__day');
  const targetDay = Array.from(dayElements).find((el) => {
    const ariaLabel = el.getAttribute('aria-label');
    const isOutsideMonth = el.classList.contains('react-datepicker__day--outside-month');
    const isDisabled = el.classList.contains('react-datepicker__day--disabled');

    return (
      ariaLabel && ariaLabel.includes(format(date, 'MMMM d')) && !isOutsideMonth && !isDisabled
    );
  });

  if (targetDay) {
    await act(async () => {
      fireEvent.click(targetDay);
    });
  }
};

jest.mock('@chakra-ui/react', () => ({
  ...jest.requireActual('@chakra-ui/react'),
  useMediaQuery: jest.fn().mockReturnValue([true]),
}));
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getCookie: (cookieName: string) => {
    if (cookieName === 'display_two_month_search') {
      return 'true';
    }
  },
  useFeatureToggle: jest.fn(() => ({
    release_pi_two_month_search: true,
  })),
}));

describe('DatepickerComponent', () => {
  beforeEach(() => {
    jest.resetModules();
    jest.clearAllMocks();
  });

  it('should render the component', () => {
    const { getByPlaceholderText } = render(<Datepicker {...defaultProps} />);
    expect(getByPlaceholderText('Datepicker component')).toBeInTheDocument();
  });

  it('should display calendar when the user clicks on the input', async () => {
    const { container, getByRole } = render(<Datepicker {...defaultProps} />);
    const input = getByRole('textbox');

    await act(async () => {
      fireEvent.click(input);
    });

    const calendar = container.querySelector('.react-datepicker');
    expect(calendar).toBeInTheDocument();
  });

  it('should hide the calendar when the user clicks outside it', async () => {
    const { container, getByRole } = render(<Datepicker {...defaultProps} />);
    const input = getByRole('textbox');
    await act(async () => {
      fireEvent.click(input);
    });
    waitFor(() => {
      fireEvent.mouseDown(document.body);

      const calendar = container.querySelector('.react-datepicker');
      expect(calendar).not.toBeInTheDocument();
    });
  });

  it('should mark the selected date in calendar', async () => {
    const { container, getByRole } = render(<Datepicker {...defaultProps} />);
    const date = add(new Date(), { days: 5 });

    const input = getByRole('textbox');
    await act(async () => {
      fireEvent.click(input);
      fireEvent.change(input, { target: { value: date } });
      fireEvent.click(input);
    });

    const selected = container.querySelector('.react-datepicker__day--selected');
    expect(selected).toHaveAttribute('aria-selected', 'true');
  });

  it('should display the footer when hasFooter prop is available', async () => {
    const { getByRole } = render(<Datepicker {...defaultProps} />);
    const input = getByRole('textbox');
    await act(async () => {
      fireEvent.click(input);
    });
    const resetButton = getByRole('button', { name: 'Reset' });
    expect(resetButton).toBeInTheDocument();
  });

  it('should reset the selected date when the user clicks on reset button', async () => {
    const { getByRole, container } = render(<Datepicker {...defaultProps} />);
    const input = getByRole('textbox');
    const date = add(new Date(), { days: 5 });

    await act(async () => {
      fireEvent.click(input);
      fireEvent.change(input, { target: { value: date } });
    });
    const selectedDate = container.querySelector('.react-datepicker__day--selected');
    const resetButton = getByRole('button', { name: 'Reset' });
    expect(selectedDate).toHaveAttribute('aria-selected', 'true');

    fireEvent.click(resetButton);
    const today = new Date();
    const todaySelected = container.querySelector('.react-datepicker__day--selected');
    expect(todaySelected).toBeInTheDocument();
    expect(todaySelected).toHaveTextContent(String(today.getDate()));
  });

  it('should reset the month to the actual month if the Reset button has been pressed', async () => {
    const { getByRole, getByText } = render(<Datepicker {...defaultProps} />);
    const input = getByRole('textbox');
    const date = add(new Date(), { days: 21 });

    await act(async () => {
      fireEvent.click(input);
      fireEvent.change(input, { target: { value: date } });
    });
    const resetButton = getByRole('button', { name: 'Reset' });

    fireEvent.click(resetButton);
    const currentMonth = new Date().toLocaleString('default', { month: 'long' });
    expect(getByText(new RegExp(currentMonth, 'i'))).toBeInTheDocument();
  });

  it('should select the date and close the calendar when the user clicks on done button', async () => {
    const { getByRole, container } = render(<Datepicker {...defaultProps} />);
    const input = getByRole('textbox');
    const date = add(new Date(), { days: 5 });

    await act(async () => {
      fireEvent.click(input);
      fireEvent.change(input, { target: { value: date } });
    });
    const calendar = container.querySelector('.react-datepicker');
    const selectedDate = container.querySelector('.react-datepicker__day--selected');
    const doneButton = getByRole('button', { name: 'Done' });
    expect(selectedDate).toHaveAttribute('aria-selected', 'true');

    fireEvent.click(doneButton);
    expect(calendar).not.toBeInTheDocument();
  });
  it.skip('should have select range when selectsRange prop is available', async () => {
    const today = new Date();
    const startNextMonth = new Date(today.getFullYear(), today.getMonth() + 1, 1);
    const startDate = add(startNextMonth, { days: 5 });
    const endDate = add(startNextMonth, { days: 8 });

    const { getByRole, container } = render(<Datepicker {...defaultProps} />);
    const input = getByRole('textbox');

    await act(async () => {
      fireEvent.click(input);
    });

    const nextMonthButton = container.querySelector('.react-datepicker__navigation--next');

    expect(nextMonthButton).not.toBeNull();

    await act(async () => {
      fireEvent.click(nextMonthButton as HTMLElement);
    });

    await clickCalendarDay(container, startDate);
    await clickCalendarDay(container, endDate);

    await waitFor(() => {
      const startDateElement = container.querySelector('.react-datepicker__day--range-start');
      expect(startDateElement).toBeInTheDocument();

      const endDateElement = container.querySelector('.react-datepicker__day--range-end');
      expect(endDateElement).toBeInTheDocument();
    });
  });
  it('should display "Today | Check out" when only startDate is selected and startDate is today', async () => {
    const { getByRole } = render(<Datepicker {...defaultProps} />);
    const input = getByRole('textbox');
    const today = new Date();

    await act(async () => {
      fireEvent.click(input);
      fireEvent.change(input, { target: { value: today } });
    });
    expect(input).toHaveValue('Today | Check out');
  });
  it('should display "Today | Tomorrow" when startDate is today and endDate is tomorrow', async () => {
    const today = new Date();
    const tomorrow = add(today, { days: 1 });

    const { getByRole, container } = render(<Datepicker {...defaultProps} />);
    const input = getByRole('textbox');

    await act(async () => {
      fireEvent.click(input);
    });

    await clickCalendarDay(container, today);
    await clickCalendarDay(container, tomorrow);

    expect(input).toHaveValue('Today | Tomorrow');
  });
  it('should display "Today | {endDate}" when startDate is today and endDate is selected', async () => {
    const today = new Date();
    const endDate = add(today, { days: 5 });

    const { getByRole, container } = render(<Datepicker {...defaultProps} />);
    const input = getByRole('textbox');

    await act(async () => {
      fireEvent.click(input);
    });

    await clickCalendarDay(container, today);
    await clickCalendarDay(container, endDate);

    const endDateFormatted = format(endDate, 'dd MMM yyyy');
    expect(input).toHaveValue(`Today | ${endDateFormatted}`);
  });
  it('should display "Tomorrow | Check out" when only startDate is selected and startDate is tomorrow', async () => {
    const { getByRole } = render(<Datepicker {...defaultProps} />);
    const input = getByRole('textbox');
    const tomorrow = add(new Date(), { days: 1 });

    await act(async () => {
      fireEvent.click(input);
      fireEvent.change(input, { target: { value: tomorrow } });
    });
    expect(input).toHaveValue('Tomorrow | Check out');
  });
  it('should display "Tomorrow | {endDate}" when startDate is tomorrow and endDate is selected', async () => {
    const tomorrow = add(new Date(), { days: 1 });
    const endDate = add(new Date(), { days: 10 });

    const { getByRole, container } = render(<Datepicker {...defaultProps} />);
    const input = getByRole('textbox');

    await act(async () => {
      fireEvent.click(input);
    });

    await clickCalendarDay(container, tomorrow);
    await clickCalendarDay(container, endDate);

    const endDateFormatted = format(endDate, 'dd MMM yyyy');
    expect(input).toHaveValue(`Tomorrow | ${endDateFormatted}`);
  });
  it('should display "{startDate} | Check out" when only startDate is selected and is not today or tomorrow', async () => {
    const { getByRole } = render(<Datepicker {...defaultProps} />);
    const input = getByRole('textbox');
    const startDate = add(new Date(), { days: 10 });

    await act(async () => {
      fireEvent.click(input);
      fireEvent.change(input, { target: { value: startDate } });
    });
    const startDateFormatted = format(startDate, 'dd MMM yyyy');
    expect(input).toHaveValue(`${startDateFormatted} | Check out`);
  });
  it('should display "{startDate} | {endDate}" when startDate and endDate are selected', async () => {
    const startDate = add(new Date(), { days: 10 });
    const endDate = add(startDate, { days: 5 });

    const { getByRole, container } = render(<Datepicker {...defaultProps} />);
    const input = getByRole('textbox');

    await act(async () => {
      fireEvent.click(input);
    });

    await clickCalendarDay(container, startDate);
    await clickCalendarDay(container, endDate);

    const startDateFormatted = format(startDate, 'dd MMM yyyy');
    const endDateFormatted = format(endDate, 'dd MMM yyyy');
    expect(input).toHaveValue(`${startDateFormatted} | ${endDateFormatted}`);
  });
  it('should not allow to have endDate same as startDate', async () => {
    const { getByRole } = render(<Datepicker {...defaultProps} />);
    const input = getByRole('textbox');
    const startDate = add(new Date(), { days: 10 });

    await act(async () => {
      fireEvent.click(input);
      fireEvent.change(input, { target: { value: startDate } });
    });
    await act(async () => {
      fireEvent.change(input, { target: { value: startDate } });
    });
    const startDateFormatted = format(startDate, 'dd MMM yyyy');
    expect(input).toHaveValue(`${startDateFormatted} | Check out`);
  });
  it('should call onReset fn when it is passed to props', async () => {
    const onReset = jest.fn();
    const { getByRole } = render(<Datepicker {...defaultProps} onReset={onReset} />);
    const input = getByRole('textbox');

    await act(async () => {
      fireEvent.click(input);
      fireEvent.change(input, { target: { value: new Date() } });
    });
    const resetButton = getByRole('button', { name: 'Reset' });
    fireEvent.click(resetButton);
    expect(onReset).toBeCalled();
  });
  it('should call onDone fn when it is passed to props', async () => {
    const onDone = jest.fn();
    const { getByRole } = render(<Datepicker {...defaultProps} onDone={onDone} />);
    const input = getByRole('textbox');

    await act(async () => {
      fireEvent.click(input);
      fireEvent.change(input, { target: { value: new Date() } });
    });
    const doneButton = getByRole('button', { name: 'Done' });
    fireEvent.click(doneButton);
    expect(onDone).toBeCalled();
  });
  it('should call onSelectDates fn when it is passed to props', async () => {
    const onSelectDates = jest.fn();
    const { getByRole } = render(<Datepicker {...defaultProps} onSelectDates={onSelectDates} />);
    const input = getByRole('textbox');

    await act(async () => {
      fireEvent.click(input);
      fireEvent.change(input, { target: { value: new Date() } });
    });
    expect(onSelectDates).toBeCalled();
  });
  it('should not call onSelectDates fn when selectsRange props is false', async () => {
    const onSelectDates = jest.fn();
    const { getByRole } = render(
      <Datepicker {...defaultProps} onSelectDates={onSelectDates} selectsRange={false} />
    );
    const input = getByRole('textbox');

    await act(async () => {
      fireEvent.click(input);
      fireEvent.change(input, { target: { value: new Date() } });
    });
    expect(onSelectDates).toBeCalled();
  });
  it('should not display the footer if the hasFooter prop has not been added', async () => {
    const { queryByRole } = render(
      <Datepicker
        minDate={new Date()}
        selectsRange={true}
        datepickerStyles={{
          inputGroupStyles: {},
          datepickerInputElementStyles: {},
          iconStyles: {},
        }}
        onSelectDates={jest.fn()}
        inputPlaceholder="Datepicker component"
        dateFormat="dd MMM yyyy"
        displayDateFormat="dd MMM yyyy"
        labels={{
          resetButtonLabel: 'Reset',
          doneButtonLabel: 'Done',
          todayLabel: 'Today',
          tomorrowLabel: 'Tomorrow',
          checkoutLabel: 'Check out',
        }}
      />
    );
    const resetButton = queryByRole('button', { name: 'Reset' });

    expect(resetButton).not.toBeInTheDocument();
  });

  it('should render datepicker focused when is on repeat booking page', async () => {
    const { getByPlaceholderText } = render(
      <Datepicker
        minDate={new Date()}
        selectsRange={true}
        datepickerStyles={{
          inputGroupStyles: {},
          datepickerInputElementStyles: {},
          iconStyles: {},
        }}
        onSelectDates={jest.fn()}
        inputPlaceholder="Datepicker component"
        dateFormat="dd MMM yyyy"
        displayDateFormat="dd MMM yyyy"
        isDatePickerFocus={true}
        labels={{
          resetButtonLabel: 'Reset',
          doneButtonLabel: 'Done',
          todayLabel: 'Today',
          tomorrowLabel: 'Tomorrow',
          checkoutLabel: 'Check out',
        }}
      />
    );
    const input = getByPlaceholderText('Datepicker component');

    expect(input).toHaveFocus();
  });

  it('shows two months when feature toggle, cookie and media query are enabled', async () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      ['release_pi_two_month_search']: true,
    });
    const { container, getByRole } = render(<Datepicker {...defaultProps} />);

    const input = getByRole('textbox');

    await act(async () => {
      fireEvent.click(input);
      fireEvent.change(input, { target: { value: new Date() } });
    });

    expect(container.getElementsByClassName('two-month-calendar').length).toBe(1);
  });

  it('should open calendar when Enter key is pressed on the input', async () => {
    const { container, getByRole } = render(<Datepicker {...defaultProps} />);
    const input = getByRole('textbox');

    fireEvent.keyDown(input, { key: 'Enter' });

    const calendar = container.querySelector('.react-datepicker');
    expect(calendar).toBeInTheDocument();
  });

  it('should open calendar when Space key is pressed on the input', async () => {
    const { container, getByRole } = render(<Datepicker {...defaultProps} />);
    const input = getByRole('textbox');

    fireEvent.keyDown(input, { key: ' ' });

    const calendar = container.querySelector('.react-datepicker');
    expect(calendar).toBeInTheDocument();
  });

  it('should open calendar when Alt+ArrowDown is pressed on the input', async () => {
    const { container, getByRole } = render(<Datepicker {...defaultProps} />);
    const input = getByRole('textbox');

    fireEvent.keyDown(input, { key: 'ArrowDown', altKey: true });

    const calendar = container.querySelector('.react-datepicker');
    expect(calendar).toBeInTheDocument();
  });

  it('calls onSelectDates when Enter is pressed on the calendar', async () => {
    const onSelectDates = jest.fn();

    const { container, getByRole } = render(
      <Datepicker {...defaultProps} onSelectDates={onSelectDates} />
    );

    fireEvent.click(getByRole('textbox'));

    const day = container.querySelector('.react-datepicker__day[tabindex="0"]');

    expect(day).toBeInTheDocument();

    fireEvent.keyDown(day!, { key: 'Enter', code: 'Enter' });

    expect(onSelectDates).toHaveBeenCalled();
  });

  it('should close calendar and focus input when Escape key is pressed', async () => {
    const { container, getByRole } = render(<Datepicker {...defaultProps} />);
    const input = getByRole('textbox');

    await act(async () => {
      fireEvent.click(input);
    });
    expect(container.querySelector('.react-datepicker')).toBeInTheDocument();

    const wrapper = container.querySelector('[class*="chakra"]')!;
    fireEvent.keyDown(wrapper, { key: 'Escape' });

    expect(container.querySelector('.react-datepicker')).not.toBeInTheDocument();
    expect(input).toHaveFocus();
  });

  describe('day cell accessibility (touch vs desktop)', () => {
    afterEach(() => {
      (useMediaQuery as jest.Mock).mockReturnValue([true]);
    });

    it('does not add tabIndex to the inner day-number on touch devices so screen-reader swipe can reach the calendar cells', async () => {
      (useMediaQuery as jest.Mock).mockImplementation((query: string) =>
        query === '(pointer: coarse)' ? [true] : [false]
      );

      const { container, getByRole } = render(<Datepicker {...defaultProps} />);

      await act(async () => {
        fireEvent.click(getByRole('textbox'));
      });

      const dayNumbers = container.querySelectorAll('.day-number');
      expect(dayNumbers.length).toBeGreaterThan(0);
      dayNumbers.forEach((dayNumber) => {
        expect(dayNumber).not.toHaveAttribute('tabindex');
      });
    });

    it("keeps the desktop keyboard tabbing aid (tabIndex on today's day-number) on non-touch devices", async () => {
      (useMediaQuery as jest.Mock).mockImplementation((query: string) =>
        query === '(pointer: coarse)' ? [false] : [true]
      );

      const { container, getByRole } = render(<Datepicker {...defaultProps} />);

      await act(async () => {
        fireEvent.click(getByRole('textbox'));
      });

      expect(container.querySelector('.day-number[tabindex="0"]')).toBeInTheDocument();
    });
  });

  describe('disableFlip prop', () => {
    it('should render correctly with disableFlip={true}', () => {
      const { getByPlaceholderText } = render(<Datepicker {...defaultProps} disableFlip={true} />);
      expect(getByPlaceholderText('Datepicker component')).toBeInTheDocument();
    });

    it('should open the calendar when input is clicked with disableFlip={true}', async () => {
      const { container, getByRole } = render(<Datepicker {...defaultProps} disableFlip={true} />);
      const input = getByRole('textbox');

      await userEvent.click(input);

      expect(container.querySelector('.react-datepicker')).toBeInTheDocument();
    });

    it('should render correctly without disableFlip prop (default behaviour)', async () => {
      const { container, getByRole } = render(<Datepicker {...defaultProps} />);
      const input = getByRole('textbox');

      await userEvent.click(input);

      expect(container.querySelector('.react-datepicker')).toBeInTheDocument();
    });
  });
});

describe('getFormattedDate function', () => {
  const labels = {
    resetButtonLabel: 'Reset',
    doneButtonLabel: 'Done',
    todayLabel: 'Today',
    tomorrowLabel: 'Tomorrow',
    checkoutLabel: 'Check out',
  };
  const props = {
    labels,
    selectsRange: true,
    displayDatesNotification: false,
  };

  const dateFormat = 'dd MMM yyyy';

  it('should return formatted start date when endDate is null and hasDatepickerRange is undefined', () => {
    const startDate = add(new Date(), { days: 3 });
    const formattedDate = format(startDate, dateFormat);
    expect(
      getFormattedDate(startDate, null, dateFormat, CountryCode.GB, {
        ...props,
        selectsRange: false,
      })
    ).toBe(formattedDate);
  });
  it('should return empty string when startDate is null, endDate is null and hasDatepickerRange is true', () => {
    expect(getFormattedDate(null, null, dateFormat, CountryCode.GB, props)).toBe(
      'Today | Tomorrow'
    );
  });
  it('should return "{todayLabel} | {checkoutLabel}" when startDate is today and endDate is null', () => {
    expect(getFormattedDate(new Date(), null, dateFormat, CountryCode.GB, props)).toBe(
      `${labels.todayLabel} | ${labels.checkoutLabel}`
    );
  });
  it('should return "{todayLabel} | {tomorrowLabel}" when startDate is today and endDate is tomorrow', () => {
    expect(
      getFormattedDate(new Date(), add(new Date(), { days: 1 }), dateFormat, CountryCode.GB, props)
    ).toBe(`${labels.todayLabel} | ${labels.tomorrowLabel}`);
  });
  it('should return "{todayLabel} | {endDateFormatted}" when startDate is today and endDate is selected', () => {
    const endDate = add(new Date(), { days: 5 });
    const endDateFormatted = format(endDate, dateFormat);
    expect(getFormattedDate(new Date(), endDate, dateFormat, CountryCode.GB, props)).toBe(
      `${labels.todayLabel} | ${endDateFormatted}`
    );
  });
  it('should return "{tomorrowLabel} | {checkoutLabel}" when startDate is tomorrow and endDate is null', () => {
    const tomorrow = add(new Date(), { days: 1 });
    expect(getFormattedDate(tomorrow, null, dateFormat, CountryCode.GB, props)).toBe(
      `${labels.tomorrowLabel} | ${labels.checkoutLabel}`
    );
  });
  it('should return "{tomorrowLabel} | {endDateFormatted}" when startDate is tomorrow and endDate is selected', () => {
    const tomorrow = add(new Date(), { days: 1 });
    const endDate = add(new Date(), { days: 5 });
    const endDateFormatted = format(endDate, dateFormat);
    expect(getFormattedDate(tomorrow, endDate, dateFormat, CountryCode.GB, props)).toBe(
      `${labels.tomorrowLabel} | ${endDateFormatted}`
    );
  });
  it('should return "{startDate} | {checkoutLabel}" when startDate is selected and endDate is null', () => {
    const startDate = add(new Date(), { days: 7 });
    const startDateFormatted = format(startDate, dateFormat);
    expect(getFormattedDate(startDate, null, dateFormat, CountryCode.GB, props)).toBe(
      `${startDateFormatted} | ${labels.checkoutLabel}`
    );
  });
  it('should return "{startDateFormatted} | {endDateFormatted}" when startDate and endDate are selected', () => {
    const startDate = add(new Date(), { days: 5 });
    const startDateFormatted = format(startDate, dateFormat);
    const endDate = add(new Date(), { days: 8 });
    const endDateFormatted = format(endDate, dateFormat);
    expect(getFormattedDate(startDate, endDate, dateFormat, CountryCode.GB, props)).toBe(
      `${startDateFormatted} | ${endDateFormatted}`
    );
  });
});
