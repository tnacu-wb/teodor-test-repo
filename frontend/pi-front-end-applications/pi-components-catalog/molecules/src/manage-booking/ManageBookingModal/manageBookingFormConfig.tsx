import { HeaderInformationData } from '@whitbread-eos/api';
import { FORM_BUTTON_TYPES, FORM_FIELD_TYPES, FormProps } from '@whitbread-eos/atoms';
import { formatDataTestId } from '@whitbread-eos/utils';

import ArrivalDate from './ArrivalDateInput/ArrivalDateInput.component';
import validateForm from './formValidation';

declare module 'yup' {
  interface ArraySchema<T> {
    unique(a: string, message: string): ArraySchema<T>;
  }
}

interface ManageBookingModalConfigArgsType {
  getFormState: FormProps['getFormState'];
  defaultValues: FormProps['defaultValues'];
  onSubmit: (value: any) => void;
  baseTestId: string;
  resetForm: number;
  labels: HeaderInformationData;
  isSubmitDisabled: boolean;
}

export const manageBookingFormConfig = ({
  getFormState,
  defaultValues,
  onSubmit,
  baseTestId,
  resetForm,
  labels,
  isSubmitDisabled,
}: ManageBookingModalConfigArgsType) => {
  const { formValidationSchema } = validateForm(labels);
  const config = {
    id: 'manageBookingForm',
    elements: {
      fields: [
        {
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          name: 'bookingReference',
          label: labels?.headerInformation?.form?.bookingReferenceLabel,
          testid: formatDataTestId(baseTestId, 'BookingReference'),
          styles: { ...inputStyle, marginBottom: 'lg' },
          props: { useTooltip: true, styles: placeholderStyles },
        },
        {
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          name: 'bookingSurname',
          label: labels?.headerInformation?.form?.bookingSurnameLabel,
          testid: formatDataTestId(baseTestId, 'BookingSurname'),
          styles: { ...inputStyle, marginBottom: 'lg' },
          props: { useTooltip: true, styles: placeholderStyles },
        },
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          name: 'arrivalDate',
          label: labels?.headerInformation?.form?.arrivalDateLabel,
          Component: ArrivalDate,
          props: {
            todayLabel: labels?.headerInformation?.content?.global?.today,
            tomorrowLabel: labels?.headerInformation?.content?.global?.tomorrow,
          },
          testid: formatDataTestId(baseTestId, 'ArrivalDate'),
          styles: inputStyle,
        },
      ],

      buttons: [
        {
          type: FORM_BUTTON_TYPES.SUBMIT,
          label: 'Search',
          action: onSubmit,
          props: {
            variant: 'primary',
            size: 'full',
            disabled: isSubmitDisabled,
          },
          styles: { ...searchButton },
          testid: formatDataTestId(baseTestId, 'Search'),
        },
      ],
    },
    defaultValues,
    validationSchema: formValidationSchema,
    getFormState,
    resetForm,
  } as FormProps;

  return config;
};

const placeholderStyles = {
  inputElementStyles: {
    _placeholder: {
      fontStyle: 'normal',
      color: 'var(--chakra-colors-chakra-subtle-text)',
    },
  },
};

const searchButton = {
  mb: 0,
};

const inputStyle = {
  width: { mobile: '18rem', xs: '21.5rem', lg: '24.5rem', xl: '26.25rem' },
};
