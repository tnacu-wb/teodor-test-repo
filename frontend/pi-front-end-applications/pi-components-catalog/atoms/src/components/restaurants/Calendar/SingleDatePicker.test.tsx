import '@testing-library/jest-dom';
import { add, format } from 'date-fns';
import { enGB } from 'date-fns/locale';
import React from 'react';
import { registerLocale, setDefaultLocale } from 'react-datepicker';

import { act, fireEvent, render, screen, waitFor } from '../../../utils/test-utils';
import SingleDatePicker from './SingleDatePicker.component';

registerLocale('en-GB', enGB);
setDefaultLocale('en-GB');

const defaultProps = {
  defaultStartDate: new Date(),
  isRightIcon: true,
  name: 'selectedDate',
  locale: 'en',
  datepickerStyles: {
    inputGroupStyles: {},
    datepickerInputElementStyles: {},
    iconStyles: {},
    bookingDatepickerSize: {},
  },
  onSelectDate: jest.fn(),
  setValue: jest.fn(),
  displayDateFormat: 'EEEE MMMM do yyyy',
  dateFormat: 'yyyy-MM-dd',
  inputLabel: 'Which date would you like to book?',
  labels: {
    todayLabel: 'Today',
    tomorrowLabel: 'Tomorrow',
  },
  dataTestId: 'datePicker',
};

describe('DatepickerComponent', () => {
  it('should render the component with Today', () => {
    const { getByDisplayValue } = render(<SingleDatePicker {...defaultProps} />);
    expect(getByDisplayValue('Today')).toBeInTheDocument();
  });
  it('should render the component with Tomorrow,isEnquiry true and defaultStartValue is null', () => {
    const { getByDisplayValue } = render(
      <SingleDatePicker {...defaultProps} defaultStartDate={''} isEnquiry={true} />
    );
    expect(getByDisplayValue('Tomorrow')).toBeInTheDocument();
  });
  it('should render the component with Tomorrow,isEnquiry true and defaultStartValue is null with displayDateFormat = ""', () => {
    const { getByDisplayValue } = render(
      <SingleDatePicker
        {...defaultProps}
        defaultStartDate={''}
        displayDateFormat={''}
        isEnquiry={true}
      />
    );
    expect(getByDisplayValue('Tomorrow')).toBeInTheDocument();
  });
  it('should render the component with Tomorrow,isEnquiry true and defaultStartValue is null with dateFormat = ""', () => {
    const { getByDisplayValue } = render(
      <SingleDatePicker {...defaultProps} defaultStartDate={''} dateFormat={''} isEnquiry={true} />
    );
    expect(getByDisplayValue('Tomorrow')).toBeInTheDocument();
  });
  it('should display calendar when the user clicks on the input', async () => {
    const { container, getByRole } = render(<SingleDatePicker {...defaultProps} />);
    const input = getByRole('textbox');
    await act(async () => {
      fireEvent.click(input);
    });
    const calendar = container.querySelector('.react-datepicker');
    expect(calendar).toBeInTheDocument();
  });
  it('should display calendar when the user pres space key down on the input', async () => {
    const { container, getByRole } = render(<SingleDatePicker {...defaultProps} />);
    await act(async () => {
      fireEvent.keyDown(getByRole('textbox'), { key: ' ' });
    });
    const calendar = container.querySelector('.react-datepicker');
    expect(calendar).toBeInTheDocument();
  });
  it('should hide the calendar when the user clicks outside it', async () => {
    const { container, getByRole } = render(<SingleDatePicker {...defaultProps} />);
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
    const { container, getByRole } = render(<SingleDatePicker {...defaultProps} />);
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
  it('should mark the Tomorrow selected date in calendar', async () => {
    const { getByRole, getByDisplayValue } = render(<SingleDatePicker {...defaultProps} />);
    const date = add(new Date(), { days: 1 });
    const input = getByRole('textbox');
    await act(async () => {
      fireEvent.click(input);
      fireEvent.change(input, { target: { value: date } });
      fireEvent.click(input);
    });
    expect(getByDisplayValue('Tomorrow')).toBeInTheDocument();
  });
  it('should mark the date format selected date in calendar', async () => {
    const { getByRole, getByDisplayValue } = render(<SingleDatePicker {...defaultProps} />);
    const date = add(new Date(), { days: 2 });

    const input = getByRole('textbox');
    await act(async () => {
      fireEvent.click(input);
      fireEvent.change(input, { target: { value: date } });
      fireEvent.click(input);
    });
    const inputText = format(new Date(date), defaultProps.displayDateFormat);
    expect(getByDisplayValue(inputText)).toBeInTheDocument();
  });
  it('should display the correct label and the correct color', () => {
    render(<SingleDatePicker {...defaultProps} />);
    const label = screen.getByText('Which date would you like to book?');
    expect(label).toHaveStyle('color: #333333');
  });
});
