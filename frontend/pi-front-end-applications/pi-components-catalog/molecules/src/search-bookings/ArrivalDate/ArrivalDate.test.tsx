import '@testing-library/jest-dom';
import { FieldsType, FORM_FIELD_TYPES } from '@whitbread-eos/atoms';
import { useForm } from 'react-hook-form';

import { act, fireEvent, render, waitFor } from '../../utils/test-utils';
import ArrivalDate from './ArrivalDate.component';

const mockLocale = jest.fn();

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const fieldType: FieldsType = {
  type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
  name: 'arrivalDate',
  label: 'arrivalDate',
  testid: 'SearchBookingsPage-ArrivalDate',
};

const Component = () => {
  const { control } = useForm();

  const props: any = {
    control,
    formField: fieldType,
  };

  return <ArrivalDate {...props} />;
};

describe('Arrival Date', () => {
  beforeEach(() => {
    jest.mock('@whitbread-eos/utils', () => ({
      ...jest.requireActual('@whitbread-eos/utils'),
      useCustomLocale: () => ({
        language: mockLocale.mockReturnValue('en'),
      }),
    }));
  });

  it('should render the component ', () => {
    const { getByTestId } = render(<Component />);
    expect(getByTestId('SearchBookingsPage-ArrivalDate-Container')).toBeInTheDocument();
  });

  it('should display calendar when the user clicks on the input', async () => {
    const { container, getByTestId } = render(<Component />);
    const input = getByTestId('SingleDatePicker');

    await act(async () => {
      fireEvent.click(input);
    });
    const calendar = container.querySelector('.react-datepicker');
    expect(calendar).toBeInTheDocument();
  });

  it('should hide the calendar when the user clicks outside it', async () => {
    const { container, getByTestId } = render(<Component />);
    const input = getByTestId('SingleDatePicker');
    await act(async () => {
      fireEvent.click(input);
    });
    waitFor(() => {
      fireEvent.mouseDown(document.body);

      const calendar = container.querySelector('.react-datepicker');
      expect(calendar).not.toBeInTheDocument();
    });
  });

  it('should render the component with datatest undefined', () => {
    fieldType.testid = undefined;
    const { getByTestId } = render(<Component />);
    expect(getByTestId('ArrivalDate-Container')).toBeInTheDocument();
  });

  it('should render the component with locale de', () => {
    mockLocale.mockReturnValue('de');
    const { getByTestId } = render(<Component />);
    expect(getByTestId('ArrivalDate-Container')).toBeInTheDocument();
  });
});
