import '@testing-library/jest-dom';

import { render, screen, fireEvent, act, userEvent } from '../../utils/test-utils';
import { type FormDynamicFieldCompProps, FormFieldsValuesType } from '../Form/formTypes';
import DatePickerFormField from './DatePickerFormField.component';

jest.mock('../Form/FormError', () => {
  return function FormError() {
    return <div data-testid="FormError">FormError</div>;
  };
});
describe('DatePickerFormField', () => {
  const baseProps: FormDynamicFieldCompProps = {
    formField: {
      type: 'text' as FormFieldsValuesType,
      name: 'datepicker',
      label: 'datepicker',
      props: {
        labels: {
          resetButtonLabel: 'reset',
          doneButtonLabel: 'done',
          todayLabel: 'today',
          tomorrowLabel: 'tomorrow',
          checkoutLabel: 'checkout',
          checkInLabel: 'checkIn',
        },
      },
    },
    field: {
      name: 'datepicker',
      value: '',
      onChange: jest.fn(),
      onBlur: jest.fn(),
    },
    handleSetValue: jest.fn(),
    errors: {},
    handleClearErrors: jest.fn(),
    getValues: jest.fn(),
  };

  it('should render without error', () => {
    const { getByDisplayValue } = render(<DatePickerFormField {...baseProps} />);
    const input = getByDisplayValue(/checkIn | checkout/i);
    expect(input).toBeInTheDocument();
  });
  it('should render error when no date is selected', () => {
    const modifiedProps = {
      ...baseProps,
      errors: { datepicker: { type: 'required', message: 'error' } },
    };
    render(<DatePickerFormField {...modifiedProps} />);
    expect(screen.getByText('FormError')).toBeInTheDocument();
  });

  it('should mark the selected date in calendar', async () => {
    const { container, getByRole } = render(<DatePickerFormField {...baseProps} />);

    const input = getByRole('textbox');
    await act(async () => {
      fireEvent.click(input);
      fireEvent.change(input, { target: { value: new Date() } });
      fireEvent.click(input);
    });

    const selected = container.querySelector('.react-datepicker__day--selected');
    expect(selected).toHaveAttribute('aria-selected', 'true');
  });

  it('should reset the selected date when the user clicks on reset button', async () => {
    const { getByRole, container, getByText } = render(<DatePickerFormField {...baseProps} />);
    const input = getByRole('textbox');

    await act(async () => {
      fireEvent.click(input);
      fireEvent.change(input, { target: { value: new Date() } });
    });
    const selectedDate = container.querySelector('.react-datepicker__day--selected');
    const resetButton = getByText('reset');
    expect(selectedDate).toHaveAttribute('aria-selected', 'true');

    fireEvent.click(resetButton);
    expect(selectedDate).toHaveAttribute('aria-selected', 'true');
  });

  it('should show error when no date is selected and done is clicked', async () => {
    const { getByRole, getByText } = render(<DatePickerFormField {...baseProps} />);

    const input = getByRole('textbox');
    act(() => {
      userEvent.click(input);
    });

    const doneButton = getByText('done');
    act(() => {
      userEvent.click(doneButton);
    });
    expect(screen.getByText('FormError')).toBeInTheDocument();
  });
});
