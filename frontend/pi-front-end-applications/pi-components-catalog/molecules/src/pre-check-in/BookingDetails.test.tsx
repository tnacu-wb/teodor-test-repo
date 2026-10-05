import '@testing-library/jest-dom';
import React from 'react';
import { useForm, FieldError } from 'react-hook-form';

import { render, fireEvent } from '../utils/test-utils';
import BookingDetails from './BookingDetails';

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const ComponentWithDynamicField = () => {
  const {
    control,
    formState: { errors },
  } = useForm();

  const formField = {
    type: 'DYNAMIC_FIELD',
    name: 'fieldName',
    label: 'Field Name',
    props: {
      bookingReferenceError: true,
    },
  };

  const props = {
    control,
    formField,
    errors,
    testid: 'PreCheckInBDPage',
    getValues: (name) => name || 'value',
  };

  return <BookingDetails {...props} />;
};

const ComponentWithCustomError = ({
  errorField = '',
  errorMessage = '',
}: {
  errorField?: string;
  errorMessage?: string;
}) => {
  const {
    control,
    formState: { errors },
  } = useForm();

  if (errorField && errorMessage) {
    errors[errorField] = {
      message: errorMessage,
      type: 'validation',
    } as FieldError;
  }

  const formField = {
    type: 'DYNAMIC_FIELD',
    name: errorField,
    label: errorField ? `${errorField.charAt(0).toUpperCase()}${errorField.slice(1)}` : '',
    props: {
      bookingReferenceError: false,
    },
  };

  const props = {
    control,
    formField,
    errors,
    testid: 'PreCheckInBDPage',
  };

  return <BookingDetails {...props} />;
};

describe('BookingDetails Component', () => {
  it('should render the component with dynamic fields', () => {
    const { getByTestId } = render(<ComponentWithDynamicField />);
    const fields = ['input-bookingNumber', 'input-surname', 'ArrivalDate-SingleDatePicker'];
    fields.forEach((field) => expect(getByTestId(field)).toBeTruthy());
  });

  it('should select a date in the date picker', () => {
    const { getByTestId, getByText } = render(<ComponentWithDynamicField />);
    const datePicker = getByTestId('ArrivalDate-SingleDatePicker');

    fireEvent.click(datePicker);
    const targetDate = getByText('15');
    fireEvent.click(targetDate);
    expect(datePicker).toBeInTheDocument();
  });

  it('should display the error message when arrivalDate has an error', () => {
    const { getByText } = render(
      <ComponentWithCustomError errorField="arrivalDate" errorMessage="Invalid arrival date" />
    );
    expect(getByText('Invalid arrival date')).toBeInTheDocument();
  });

  it('should display the error message when booking number has an error', () => {
    const { getByText } = render(
      <ComponentWithCustomError errorField="bookingNumber" errorMessage="Invalid booking number" />
    );
    expect(getByText('Invalid booking number')).toBeInTheDocument();
  });

  it('should display the error message when surname has an error', () => {
    const { getByText } = render(
      <ComponentWithCustomError errorField="surname" errorMessage="Invalid surname" />
    );
    expect(getByText('Invalid surname')).toBeInTheDocument();
  });

  it('should display the error message when booking number is not found', () => {
    const { getByTestId } = render(<ComponentWithDynamicField />);
    expect(getByTestId('BookingDetails-AlertDescription')).toBeInTheDocument();
  });
});
