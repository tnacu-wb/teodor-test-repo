import '@testing-library/jest-dom';
import { add } from 'date-fns';
import React from 'react';
import { act } from 'react-dom/test-utils';

import { fireEvent, render, waitFor } from '../../utils/test-utils';
import SingleDatePicker from './SingleDatePicker.component';

const defaultProps = {
  minDate: new Date(),
  name: 'Datepicker',
  locale: 'en',
  datepickerStyles: {
    inputGroupStyles: {},
    datepickerInputElementStyles: {},
    iconStyles: {},
    bookingDatepickerSize: {},
  },
  onSelectDate: jest.fn(),
  inputPlaceholder: 'Datepicker component',
  dateFormat: 'dd MMM yyyy',
  displayDateFormat: 'dd MMM yyyy',
  inputLabel: 'Single Date Picker',
  labels: {
    todayLabel: 'Today',
    tomorrowLabel: 'Tomorrow',
  },
};

describe('DatepickerComponent', () => {
  it('should render the component', () => {
    const { getByPlaceholderText } = render(<SingleDatePicker {...defaultProps} />);
    expect(getByPlaceholderText('Datepicker component')).toBeInTheDocument();
  });
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

it('should display calendar with custom header when the user clicks on the input', async () => {
  const { container, getByRole } = render(<SingleDatePicker {...defaultProps} customHeader />);
  const input = getByRole('textbox');
  await act(async () => {
    fireEvent.click(input);
  });
  const calendar = container.querySelector('.react-datepicker');
  expect(calendar).toBeInTheDocument();
});
