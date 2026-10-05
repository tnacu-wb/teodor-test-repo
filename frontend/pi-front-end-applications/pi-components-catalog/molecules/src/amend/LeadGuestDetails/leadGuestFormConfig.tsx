import { LeadGuestDetailsForm } from '.';
import {
  AmendLeadGuestLabels,
  AmendLeadGuestValidationLabels,
  ReservationLeadGuestType,
} from '@whitbread-eos/api';
import { FORM_FIELD_TYPES, FormProps } from '@whitbread-eos/atoms';
import {
  formatDataTestId,
  formatGuestTitleOptions,
  getTitleDropdownValues,
} from '@whitbread-eos/utils';
import { Dispatch, SetStateAction } from 'react';

import validateForm from './formValidation';

declare module 'yup' {
  interface ArraySchema<T> {
    unique(a: string, message: string): ArraySchema<T>;
  }
}

interface LeadGuestDetailsFormConfigArgsType {
  getFormState: FormProps['getFormState'];
  defaultValues: FormProps['defaultValues'];
  onSubmit: (guestDetails: FormProps['defaultValues']) => void;
  baseTestId: string;
  labels: AmendLeadGuestLabels;
  validationLabels: AmendLeadGuestValidationLabels;
  isEdit?: boolean;
  setGuestDetails?: (value: ReservationLeadGuestType) => void;
  setIsLocationRequired: Dispatch<SetStateAction<boolean>>;
  hotelCountry?: string;
  language?: string;
}

export const leadGuestFormConfig = ({
  getFormState,
  defaultValues,
  onSubmit,
  baseTestId,
  labels,
  validationLabels,
  isEdit,
  setGuestDetails,
  setIsLocationRequired,
  hotelCountry,
  language,
}: LeadGuestDetailsFormConfigArgsType) => {
  const formValidationSchema = validateForm(validationLabels);

  const config = {
    id: 'leadGuestDetailsForm',
    elements: {
      fields: [
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          dropdownOptions: getTitleDropdownValues(
            formatGuestTitleOptions(labels.guestTitle),
            defaultValues.title
          ),
          name: 'leadGuest',
          label: '',
          testid: formatDataTestId(baseTestId, 'Form'),
          Component: LeadGuestDetailsForm,
          props: {
            baseTestId,
            labels,
            isEdit,
            setGuestDetails,
            setIsLocationRequired,
            hotelCountry,
            language,
          },
          styles: { marginBottom: '0px' },
        },
      ],
      fieldsContainerStyles: {
        ...formStyles,
      },
      onSubmitAction: onSubmit,
    },
    defaultValues,
    validationSchema: formValidationSchema,
    getFormState,
  } as FormProps;

  return config;
};

const formStyles = {
  marginBottom: '0px',
};
