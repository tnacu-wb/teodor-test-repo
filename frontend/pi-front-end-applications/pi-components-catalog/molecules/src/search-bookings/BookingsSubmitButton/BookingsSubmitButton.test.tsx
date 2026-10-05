import '@testing-library/jest-dom';
import { FieldsType, FORM_FIELD_TYPES } from '@whitbread-eos/atoms';
import { useForm } from 'react-hook-form';

import { render, userEvent } from '../../utils/test-utils';
import BookingsSubmitButton from './BookingsSubmitButton.component';

const mockOnSubmit = jest.fn();
const filledFormValues = {
  bookingReference: 'AEAR423983',
  bookerLastName: 'John',
  arrivalDate: '22-03-2023',
  guestLastName: 'Doe',
  bookerPostcode: 'E1 6AN',
  hotelDetails: { name: 'Manchester Old Trafford', code: 'MAN' },
  bookerEmail: 'john@doe.com',
  bookerPhone: '123456',
  cancellationDate: '',
  companyName: 'John Doe Inc',
  thirdPartyBookingReferenceNumber: '',
};

const Component = () => {
  const { control } = useForm({
    defaultValues: filledFormValues,
  });

  const fieldType: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    label: 'Search for booking',
    name: 'bookingsSubmitButton',
    testid: 'Bookings-Submit',
    Component: BookingsSubmitButton,
    props: {
      action: mockOnSubmit,
      type: 'submit',
    },
  };

  const props: any = {
    control,
    formField: fieldType,
    getValues: () => filledFormValues,
    errors: {},
  };
  return <BookingsSubmitButton {...props} />;
};

const ComponentWithEmptyFields = () => {
  const { control } = useForm({
    defaultValues: {
      bookingReference: '',
      bookerLastName: '',
      arrivalDate: '',
      guestLastName: '',
      bookerPostcode: '',
      hotelDetails: { name: '', code: '' },
      bookerEmail: '',
      bookerPhone: '',
      cancellationDate: '',
      companyName: '',
    },
  });

  const fieldType: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    label: 'Search for booking',
    name: 'bookingsSubmitButton',
    testid: 'Bookings-Submit',
    Component: BookingsSubmitButton,
    props: {
      action: mockOnSubmit,
      type: 'submit',
    },
  };

  const props: any = {
    control,
    formField: fieldType,
    getValues: jest.fn(),
    errors: {},
  };
  return <BookingsSubmitButton {...props} />;
};

const EnhancedSearchComponent = () => {
  const { control } = useForm({
    defaultValues: filledFormValues,
  });

  const fieldType: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    label: 'Search for booking',
    name: 'bookingsSubmitButton',
    testid: 'Bookings-Submit',
    Component: BookingsSubmitButton,
    props: {
      action: mockOnSubmit,
      type: 'submit',
      enhancedSearch: true,
    },
  };

  const props: any = {
    control,
    formField: fieldType,
    getValues: () => filledFormValues,
    errors: {},
  };
  return <BookingsSubmitButton {...props} />;
};

const EnhancedSearchComponentWithEmptyFields = () => {
  const { control } = useForm({
    defaultValues: {
      bookingReference: '',
      bookerLastName: '',
      arrivalDate: '',
      guestLastName: '',
      bookerPostcode: '',
      hotelDetails: { name: '', code: '' },
      bookerEmail: '',
      bookerPhone: '',
      cancellationDate: '',
      companyName: '',
    },
  });

  const fieldType: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    label: 'Search for booking',
    name: 'bookingsSubmitButton',
    testid: 'Bookings-Submit',
    Component: BookingsSubmitButton,
    props: {
      action: mockOnSubmit,
      type: 'submit',
      enhancedSearch: true,
    },
  };

  const props: any = {
    control,
    formField: fieldType,
    getValues: jest.fn(),
    errors: {},
  };
  return <BookingsSubmitButton {...props} />;
};

describe('BookingsSubmitButton', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render the component', () => {
    const { getByTestId } = render(<Component />);
    expect(getByTestId('Bookings-Submit-Button')).toBeInTheDocument();
  });

  it('should trigger the submit when clicked', async () => {
    const { getByTestId } = render(<Component />);
    const button = getByTestId('Bookings-Submit-Button');

    userEvent.click(button);
    await expect(mockOnSubmit).toHaveBeenCalledTimes(1);
  });

  it('should trigger the submit when clicked(Enhanced Search)', async () => {
    const { getByTestId } = render(<EnhancedSearchComponent />);
    const button = getByTestId('Bookings-Submit-Button');

    userEvent.click(button);
    await expect(mockOnSubmit).toHaveBeenCalledTimes(1);
  });

  it('should be disabled if the fields are not completed', () => {
    const { getByTestId } = render(<ComponentWithEmptyFields />);
    const button = getByTestId('Bookings-Submit-Button');

    expect(button).toBeDisabled();
  });

  it('should be disabled if the fields are not completed(Enhanced Search)', () => {
    const { getByTestId } = render(<EnhancedSearchComponentWithEmptyFields />);
    const button = getByTestId('Bookings-Submit-Button');

    expect(button).toBeDisabled();
  });
});
