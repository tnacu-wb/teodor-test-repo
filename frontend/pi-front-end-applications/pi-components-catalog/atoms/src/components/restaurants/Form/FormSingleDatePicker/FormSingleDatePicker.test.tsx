import '@testing-library/jest-dom';
import { formatDataTestId } from '@whitbread-eos/utils';
import { add } from 'date-fns';
import { useForm } from 'react-hook-form';

import { act, fireEvent, render } from '../../../../utils/test-utils';
import { FORM_FIELD_TYPES } from '../formContants';
import { FieldsType, FormFieldProps } from '../formTypes';
import FormSingleDatePicker from './FormSingleDatePicker.component';

const mockUseMediaQuery = jest.fn();
jest.mock('@chakra-ui/react', () => ({
  ...jest.requireActual('@chakra-ui/react'),
  useMediaQuery: (...args: string[]) => mockUseMediaQuery(...args),
}));

const Component = () => {
  const {
    control,
    formState: { errors },
    getValues,
    setValue,
  } = useForm();
  const baseDataTestId = 'TableBooking';
  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.SINGLE_DATE_PICKER,
    name: 'date',
    label: 'Choose date?',
    testid: formatDataTestId(baseDataTestId, 'SelectedDate'),
    props: {
      isRightIcon: true,
      isDisabled: false,
    },
    styles: { w: '100%', mb: '2xl' },
  };
  const props: FormFieldProps = {
    control,
    formField: formField,
    errors,
    setValue,
    getValues,
  };
  return <FormSingleDatePicker {...props} />;
};
describe('FormSingleDatePicker', () => {
  it('should render the component', () => {
    mockUseMediaQuery.mockReturnValue([true]);
    const { getByTestId } = render(<Component />);
    const component = getByTestId('TableBooking-SelectedDate');
    expect(component).toBeInTheDocument();
  });
  it('should mark the selected date in calendar', async () => {
    mockUseMediaQuery.mockReturnValue([false]);
    const { container, getByRole } = render(<Component />);
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
});
