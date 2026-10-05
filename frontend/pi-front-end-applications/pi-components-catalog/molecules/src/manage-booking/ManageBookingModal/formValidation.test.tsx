import '@testing-library/jest-dom';
import { Form } from '@whitbread-eos/atoms';

import { render, userEvent, waitFor } from '../../utils/test-utils';
import { manageBookingFormConfig } from './manageBookingFormConfig';

const getFormState = jest.fn();
const onSubmit = jest.fn();
const resetForm = 0;
const labels = {
  headerInformation: {
    form: {
      findBookingTitle: 'test',
      findBookingDescription: 'test',
      bookingReferenceLabel: 'bookingReferenceLabel',
      bookingSurnameLabel: 'bookingSurnameLabel',
      invalidReference: 'invalidReference',
      invalidSurname: 'invalidSurname',
      arrivalDateLabel: 'arrival date',
    },
    content: {
      global: {
        today: 'Today',
        tomorrow: 'Tomorrow',
      },
    },
  },
};

let baseTestId = 'ManageBooking';

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const Component = () => {
  const defaultValues = {
    bookingReference: '',
    bookingSurname: '',
    arrivalDate: '',
  };

  return (
    <Form
      data-testid={'form'}
      {...manageBookingFormConfig({
        isSubmitDisabled: false,
        getFormState,
        defaultValues,
        onSubmit,
        baseTestId,
        resetForm,
        labels,
      })}
    />
  );
};

describe('Render Form', () => {
  afterAll(() => {
    jest.clearAllMocks();
  });
  beforeEach(() => {
    jest.resetAllMocks();
  });

  it('should render Form component', () => {
    const { queryByTestId } = render(<Component />);
    expect(queryByTestId('ManageBooking-BookingReference')).toBeTruthy();
    expect(queryByTestId('ManageBooking-BookingSurname')).toBeTruthy();
  });

  it('should render Form component with test id undefined', () => {
    baseTestId = undefined;
    const { queryByTestId } = render(<Component />);
    expect(queryByTestId('ArrivalDate')).toBeTruthy();
  });

  it('should have aria-invalid attribute if given input is incorrect', async () => {
    const { getByRole, getByTestId } = render(<Component />);
    const input = getByTestId('input-bookingReference');
    const searchBtn = getByRole('button', { name: 'Search' });
    await userEvent.type(input, '1');
    userEvent.click(searchBtn);
    await waitFor(() => {
      expect(input).toBeInvalid();
    });
  });
  it('should not have aria-invalid attribute if given input has correct format', async () => {
    const { getByRole, getByTestId } = render(<Component />);
    const input = getByTestId('input-bookingReference');
    const searchBtn = getByRole('button', { name: 'Search' });
    await userEvent.type(input, '1234567890');
    userEvent.click(searchBtn);
    await waitFor(() => {
      expect(input).not.toBeInvalid();
    });
  });

  it('should have aria-invalid attribute if given input is shorter than minimum allowed', async () => {
    const { getByRole, getByTestId } = render(<Component />);
    const input = getByTestId('input-bookingSurname');
    const searchBtn = getByRole('button', { name: 'Search' });
    await userEvent.type(input, 'a');
    userEvent.click(searchBtn);
    await waitFor(() => {
      expect(input).toBeInvalid();
    });
  });

  it('should have aria-invalid attribute if given input has forbidden characters', async () => {
    const { getByRole, getByTestId } = render(<Component />);
    const input = getByTestId('input-bookingSurname');
    const searchBtn = getByRole('button', { name: 'Search' });
    await userEvent.type(input, 'abc:;~');
    userEvent.click(searchBtn);
    await waitFor(() => {
      expect(input).toBeInvalid();
    });
  });
  it('should not have aria-invalid attribute if given surname has correct format', async () => {
    const { getByRole, getByTestId } = render(<Component />);
    const input = getByTestId('input-bookingSurname');
    const searchBtn = getByRole('button', { name: 'Search' });
    await userEvent.type(input, 'john');
    userEvent.click(searchBtn);
    await waitFor(() => {
      expect(input).not.toBeInvalid();
    });
  });
});
