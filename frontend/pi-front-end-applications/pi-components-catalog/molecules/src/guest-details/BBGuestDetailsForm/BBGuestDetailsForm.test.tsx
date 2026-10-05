import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { FORM_FIELD_TYPES } from '@whitbread-eos/atoms';
import { formatDataTestId } from '@whitbread-eos/utils';
import React from 'react';
import { useForm } from 'react-hook-form';

import { render } from '../../utils/test-utils';
import BBGuestDetailsForm from './BBGuestDetailsForm';

const baseDataTestId = 'GuestDetailsBB';

const mockHandleSetValue = jest.fn();
const mockHandleResetField = jest.fn();
const mockHandleGetValues = jest.fn();

let numberOfRooms: number | undefined = 1;
const ComponentWithSingleRoom = () => {
  const {
    formState: { errors },
  } = useForm();

  const fieldType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'bbGuestDetails',
    dropdownOptions: [{ id: 'Mr', label: 'Mr' }],
    label: '',
    testid: baseDataTestId,
    props: {
      defaultValues: {
        bbGestDetails: [
          {
            emailAddress: '',
            firstName: '',
            id: '',
            lastName: '',
            title: '',
            composedName: '',
          },
        ],
      },
      guestList: {
        bbGuestDetails: [
          {
            composedName: '',
            emailAddress: '',
            firstName: '',
            id: '',
            lastName: '',
            title: '',
          },
        ],
      },
      isDynamicSearchVisible: true,
      numberOfRooms: numberOfRooms,
      labels: {},
      queryClient: jest.fn(),
      setGuestUser: jest.fn(),
    },
  };

  const props: any = {
    reset: jest.fn(),
    control: {
      _formValues: {
        bbGuestDetails: [
          {
            composedName: '',
            emailAddress: '',
            firstName: '',
            id: '',
            lastName: '',
            title: '',
          },
        ],
      },
    },
    formField: fieldType,
    errors,
    handleSetValue: mockHandleSetValue,
    handleResetField: mockHandleResetField,
    getValues: mockHandleGetValues,
  };

  return (
    <QueryClientProvider client={new QueryClient()}>
      <BBGuestDetailsForm {...props} />
    </QueryClientProvider>
  );
};

describe('Guest Details BB', () => {
  it('should render the  Guest Details BB with single room', () => {
    const { getByTestId } = render(<ComponentWithSingleRoom />);
    expect(getByTestId(formatDataTestId(baseDataTestId, 'Container'))).toBeInTheDocument();
  });

  it('should render the  Guest Details BB with number of rooms undefined', () => {
    numberOfRooms = undefined;
    const { getByTestId } = render(<ComponentWithSingleRoom />);
    expect(getByTestId(formatDataTestId(baseDataTestId, 'Container'))).toBeInTheDocument();
  });
});
