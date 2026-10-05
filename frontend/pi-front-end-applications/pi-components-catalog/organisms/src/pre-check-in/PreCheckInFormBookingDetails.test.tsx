import '@testing-library/jest-dom';
import { FieldsType, FORM_FIELD_TYPES } from '@whitbread-eos/atoms';
import React from 'react';
import { useForm } from 'react-hook-form';

import { render } from '../utils/test-utils';
import PreCheckInFormBookingDetails from './PreCheckInFormBookingDetails';

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

// Correct type annotation for `useForm`
const ComponentWithDynamicField = () => {
  const {
    control,
    formState: { errors },
    getValues, // This is the correct `getValues` from `useForm`
  } = useForm();

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'fieldNameBookingDetails',
    label: 'Field Name Booking Details',
    testid: 'preCheckInBookingDetailsForm',
    props: {
      bookingReferenceError: false,
      isMobilePreRegisteredRepurposeEnabled: false,
    },
  };

  const props = {
    control,
    formField,
    errors,
    getValues, // Use the `getValues` from `useForm` without redefining
  };

  return <PreCheckInFormBookingDetails {...props} />;
};

describe('PreRegister Booking details PI ', () => {
  it('should render Pre register page skeleton', () => {
    const { getByTestId } = render(<ComponentWithDynamicField />);
    expect(getByTestId('submit-reg-booking-details-form')).toBeInTheDocument();
  });
});
