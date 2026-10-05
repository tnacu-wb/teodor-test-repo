import { StyleProps } from '@chakra-ui/react';
import { FS_ENABLE_SEARCH_ACCOUNT_SAVE_CHANGES } from '@whitbread-eos/api';
import { FormProps, FORM_FIELD_TYPES, FORM_BUTTON_TYPES } from '@whitbread-eos/atoms';
import { formatDataTestId, useFeatureSwitch as isFeatureFlagEnabled } from '@whitbread-eos/utils';

import PhoneSelector from '../../../guest-details/PhoneSelector';
import validateUpdateAccountForm from './validateUpdateAccountForm';

interface UpdateAccountFormConfigArgsType {
  getFormState: FormProps['getFormState'];
  defaultValues: FormProps['defaultValues'];
  onSubmit: () => void;
  onGuestVerify: () => void;
  onUnlock: () => void;
  onResetPassword: () => void;
  onReuseDetails: () => void;
  baseDataTestId: string;
  t: (id: string) => string;
  language?: string;
  fieldsetDisabled: boolean;
  reservationId?: string;
}

export const updateAccountFormConfig = ({
  getFormState,
  defaultValues,
  onSubmit,
  onGuestVerify,
  onUnlock,
  onResetPassword,
  onReuseDetails,
  baseDataTestId,
  t,
  language,
  fieldsetDisabled,
  reservationId,
}: UpdateAccountFormConfigArgsType) => {
  const isSaveAccountEnabled = isFeatureFlagEnabled({
    featureSwitchKey: FS_ENABLE_SEARCH_ACCOUNT_SAVE_CHANGES,
  });
  const { formValidationSchema } = validateUpdateAccountForm(t, defaultValues);

  const fields = [
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
        ...inputDynamicStyle,
      },
      props: {
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
        ...inputDynamicStyle,
      },
      props: {
        showIcon: false,
        currentLang: language,
        className: 'sessioncamhidetext',
      },
      testid: formatDataTestId(baseDataTestId, 'landlineNumber'),
    },
  ];

  const commonButtons = [
    {
      type: FORM_BUTTON_TYPES.SUBMIT,
      label: t('ccui.guestAccounts.cta.saveChanges'),
      action: !isSaveAccountEnabled ? null : onSubmit,
      props: {
        variant: 'primary',
        size: 'full',
        isDisabled: !isSaveAccountEnabled,
      },
      testid: formatDataTestId(baseDataTestId, 'Save-Changes-Btn'),
    },
    {
      type: FORM_BUTTON_TYPES.BUTTON,
      label: t('ccui.guestAccounts.cta.unlockAccount'),
      action: onUnlock,
      props: {
        variant: 'primary',
        size: 'full',
        isDisabled: true,
      },
      testid: formatDataTestId(baseDataTestId, 'Unlock-Account-Btn'),
    },
    {
      type: FORM_BUTTON_TYPES.BUTTON,
      label: t('ccui.guestAccounts.cta.resetPassword'),
      action: onResetPassword,
      props: {
        variant: 'primary',
        size: 'full',
        isDisabled: true,
      },
      testid: formatDataTestId(baseDataTestId, 'Reset-Password-Btn'),
    },
  ];

  const buttons = fieldsetDisabled
    ? [
        {
          type: FORM_BUTTON_TYPES.BUTTON,
          label: t('ccui.guestAccounts.cta.guestVerified'),
          action: onGuestVerify,
          props: {
            variant: 'primary',
            size: 'full',
          },
          testid: formatDataTestId(baseDataTestId, 'Guest-Verified-Btn'),
        },
      ]
    : reservationId // taken originally from url, used for GDP->SearchMyPIAcc flow
      ? [
          ...commonButtons,
          {
            type: FORM_BUTTON_TYPES.BUTTON,
            label: t('ccui.manageBooking.reuseDetails'),
            action: onReuseDetails,
            props: {
              variant: 'tertiary',
              size: 'full',
            },
            testid: formatDataTestId(baseDataTestId, 'Reuse-Details-Btn'),
          },
        ]
      : commonButtons;

  const config = {
    id: 'updateAccountForm',
    elements: {
      fieldsContainerStyles,
      buttonsContainerStyles,
      fields,
      buttons,
    },
    defaultValues,
    validationSchema: formValidationSchema,
    getFormState,
    fieldsetDisabled,
  } as FormProps;

  return config;
};

const fieldsContainerStyles = {
  display: 'grid',
  gridTemplateColumns: '1fr 1fr 1fr',
  gridTemplateRow: 'auto',
  justifyItems: 'stretch',
  columnGap: 'lg',
  rowGap: 'sm',
  height: 'auto',
  mb: 'md',
} as StyleProps;

const buttonsContainerStyles = {
  display: 'grid',
  gridTemplateColumns: '1fr 1fr 1fr',
  gridTemplateRow: 'auto',
  justifyItems: 'stretch',
  columnGap: 'lg',
  rowGap: 'sm',
  height: 'auto',
  mb: 'md',
} as StyleProps;

const inputStyle = {
  sx: {
    '& input': {
      backgroundColor: 'var(--chakra-colors-white)',
    },
  },
  w: {
    lg: '24.5rem',
    xl: '26.25rem',
  },
  p: 0,
} as StyleProps;

const inputDynamicStyle = {
  sx: {
    '& input': {
      backgroundColor: 'var(--chakra-colors-white)',
      borderColor: 'var(--chakra-colors-lightGrey1)',
      '&::placeholder': {
        color: 'var(--chakra-colors-darkGrey2) ',
      },
    },
    '& label': {
      color: 'var(--chakra-colors-darkGrey2) ',
    },
  },
} as StyleProps;
