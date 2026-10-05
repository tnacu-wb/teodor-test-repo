import { FORM_FIELD_TYPES, FormProps, DropdownOption } from '@whitbread-eos/atoms';
import { PreCheckInForm, PreCheckInFormBookingDetails } from '@whitbread-eos/organisms';
import { formatDataTestId } from '@whitbread-eos/utils';
import React, { Dispatch, SetStateAction } from 'react';

import { SUBMIT_TYPE } from '../common';
import validateForm from './formValidation';
import validateFormBookingDetails from './formValidationBookingDetails';

interface PreCheckInBookingDetailsFormConfigType {
  getFormState: FormProps['getFormState'];
  defaultValues: FormProps['defaultValues'];
  onSubmit: (data: any) => void;
  baseDataTestId: string;
  t: (id: string) => string;
  currentLang: string | undefined;
  bookingReferenceError: boolean;
}

interface PreCheckInFormConfigType {
  getFormState: FormProps['getFormState'];
  defaultValues: FormProps['defaultValues'];
  onSubmit: (data: any, event: React.MouseEvent<HTMLButtonElement>) => void;
  baseDataTestId: string;
  t: (id: string) => string;
  currentLang: string | undefined;
  dependents: DropdownOption[];
  setSubmitType: Dispatch<SetStateAction<SUBMIT_TYPE>>;
  submitType: SUBMIT_TYPE;
  setIsLocationRequired: Dispatch<SetStateAction<boolean>>;
  isMobilePreRegisteredRepurposeEnabled: boolean;
}

export const preCheckinBookingDetailsFormConfig = ({
  getFormState,
  defaultValues,
  onSubmit,
  baseDataTestId,
  t,
  currentLang,
  bookingReferenceError,
}: PreCheckInBookingDetailsFormConfigType) => {
  const { formValidationSchemaBookingDetails } = validateFormBookingDetails({ t, currentLang });

  const config = {
    id: 'preCheckInBookingDetailsForm',
    testid: formatDataTestId(baseDataTestId, 'preCheckInBookingDetailsForm'),
    elements: {
      fields: [
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          name: 'pre-check-in-booking-details',
          label: '',
          testid: formatDataTestId(baseDataTestId, 'Form'),
          Component: PreCheckInFormBookingDetails,
          props: { bookingReferenceError },
        },
      ],
      onSubmitAction: onSubmit,
    },
    defaultValues,
    validationSchema: formValidationSchemaBookingDetails,
    getFormState,
  } as FormProps;

  return config;
};

export const preCheckinFormConfig = ({
  getFormState,
  defaultValues,
  onSubmit,
  baseDataTestId,
  t,
  currentLang,
  dependents,
  submitType,
  setSubmitType,
  setIsLocationRequired,
  isMobilePreRegisteredRepurposeEnabled,
}: PreCheckInFormConfigType) => {
  const { formValidationSchema, saveFormValidationSchema } = validateForm({ t, currentLang });

  const config = {
    id: 'preCheckInRegistrationForm',
    testid: formatDataTestId(baseDataTestId, 'preCheckInRegistrationForm'),
    elements: {
      fields: [
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          name: 'pre-check-in',
          label: '',
          testid: formatDataTestId(baseDataTestId, 'Form'),
          Component: PreCheckInForm,
          props: {
            dependents,
            setSubmitType,
            setIsLocationRequired,
            isMobilePreRegisteredRepurposeEnabled,
          },
        },
      ],
      onSubmitAction: onSubmit,
    },
    defaultValues,
    validationSchema:
      submitType === SUBMIT_TYPE.SAVE ? saveFormValidationSchema : formValidationSchema,
    getFormState,
  } as NonNullable<unknown> as FormProps;

  return config;
};
