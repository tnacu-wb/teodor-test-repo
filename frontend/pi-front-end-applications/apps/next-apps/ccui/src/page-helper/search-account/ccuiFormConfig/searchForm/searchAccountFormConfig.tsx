import { StyleProps } from '@chakra-ui/react';
import type { SearchAccountForm } from '@whitbread-eos/api';
import { FORM_BUTTON_TYPES, FORM_FIELD_TYPES, FormProps } from '@whitbread-eos/atoms';
import { PhoneSelector } from '@whitbread-eos/molecules';
import { formatDataTestId } from '@whitbread-eos/utils';

import validateSearchAccountForm from './validateSearchAccountForm';

interface SearchAccountFormConfigArgsType {
  getFormState: FormProps['getFormState'];
  defaultValues: FormProps['defaultValues'];
  onSubmit: (data: SearchAccountForm) => void;
  onAbort: () => void;
  onReset: () => void;
  baseDataTestId: string;
  t: (id: string) => string;
  resetForm?: number;
  language?: string;
  clearPhoneFields?: boolean;
  submitBtnDisabled: boolean;
}

export const searchAccountFormConfig = ({
  getFormState,
  defaultValues,
  onSubmit,
  onAbort,
  onReset,
  baseDataTestId,
  t,
  resetForm,
  language,
  clearPhoneFields,
  submitBtnDisabled,
}: SearchAccountFormConfigArgsType) => {
  const { formValidationSchema } = validateSearchAccountForm(t);

  const config = {
    id: 'searchAccountForm',
    elements: {
      fieldsContainerStyles,
      buttonsContainerStyles,
      fields: [
        {
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          id: 'firstName',
          name: 'firstName',
          label: t('ccui.guestAccounts.firstName'),
          testid: formatDataTestId(baseDataTestId, 'firstName'),
          styles: { ...inputStyle },
        },
        {
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          id: 'lastName',
          name: 'lastName',
          label: t('ccui.guestAccounts.surname'),
          testid: formatDataTestId(baseDataTestId, 'lastName'),
          styles: { ...inputStyle },
        },
        {
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          id: 'companyName',
          name: 'companyName',
          label: t('ccui.guestAccounts.companyName'),
          testid: formatDataTestId(baseDataTestId, 'companyName'),
          styles: { ...inputStyle },
        },
        {
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          id: 'email',
          name: 'email',
          label: t('ccui.guestAccounts.emailAddress'),
          testid: formatDataTestId(baseDataTestId, 'email'),
          styles: { ...inputStyle },
        },
        {
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          id: 'address',
          name: 'address',
          label: t('ccui.guestAccounts.address'),
          testid: formatDataTestId(baseDataTestId, 'address'),
          styles: { ...inputStyle },
        },
        {
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          id: 'postalCode',
          name: 'postalCode',
          label: t('ccui.guestAccounts.postCode'),
          testid: formatDataTestId(baseDataTestId, 'postalCode'),
          styles: { ...inputStyle },
        },
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          name: 'mobileNumber',
          id: 'mobileNumber',
          label: t('ccui.guestAccounts.mobile'),
          Component: PhoneSelector,
          styles: {
            ...inputStyle,
          },
          props: {
            clearField: clearPhoneFields,
            showIcon: false,
            currentLang: language,
            className: 'sessioncamhidetext',
          },
          testid: formatDataTestId(baseDataTestId, 'mobileNumber'),
        },
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          id: 'landlineNumber',
          name: 'landlineNumber',
          label: t('ccui.guestAccounts.landline'),
          Component: PhoneSelector,
          styles: {
            ...inputStyle,
          },
          props: {
            clearField: clearPhoneFields,
            showIcon: false,
            currentLang: language,
            className: 'sessioncamhidetext',
          },
          testid: formatDataTestId(baseDataTestId, 'landlineNumber'),
        },
      ],
      buttons: [
        {
          type: FORM_BUTTON_TYPES.SUBMIT,
          label: t('ccui.guestAccounts.cta.searchGuest'),
          action: onSubmit,
          props: {
            variant: 'primary',
            size: 'full',
            backgroundColor: 'var(--chakra-colors-btnSecondaryEnabled)',
            disabled: submitBtnDisabled,
          },
          testid: 'searchButton',
        },
        {
          type: FORM_BUTTON_TYPES.BUTTON,
          label: t('ccui.guestAccounts.cta.abortSearch'),
          action: onAbort,
          props: {
            variant: 'tertiary',
            size: 'full',
          },
          testid: 'abortButton',
        },
        {
          type: FORM_BUTTON_TYPES.RESET,
          label: t('ccui.guestAccounts.cta.clearSearch'),
          action: onReset,
          styles: {
            gridColumnStart: 1,
          },
          props: {
            w: 'auto',
            variant: 'tertiary',
            border: 'none',
            p: 0,
            textAlign: 'left',
            display: 'block',
            _hover: {
              boxShadow: 'none',
              background: 'none',
              textDecoration: 'underline',
            },
            _focus: {
              boxShadow: 'none',
            },
          },
          testid: formatDataTestId(baseDataTestId, 'Reset'),
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

const fieldsContainerStyles = {
  display: 'grid',
  gridTemplateColumns: '1fr 1fr 1fr',
  justifyItems: 'stretch',
  columnGap: 'lg',
  rowGap: 'sm',
  height: 'auto',
  mb: 'md',
} as StyleProps;

const buttonsContainerStyles = {
  display: 'grid',
  gridTemplateColumns: '1fr 1fr 1fr',
  justifyItems: 'stretch',
  columnGap: 'lg',
  rowGap: 'sm',
  height: 'auto',
  mb: 'md',
} as StyleProps;

const inputStyle = {
  w: {
    lg: '24.5rem',
    xl: '26.25rem',
  },
  p: 0,
} as StyleProps;
