import { ResetPasswordLabels } from '@whitbread-eos/api';
import type { FormProps } from '@whitbread-eos/atoms';
import { FORM_BUTTON_TYPES, FORM_FIELD_TYPES, FORM_VALIDATIONS } from '@whitbread-eos/atoms';
import { formatDataTestId } from '@whitbread-eos/utils';
import * as yup from 'yup';

import NewPasswordDescription from './NewPasswordDescription.component';

interface NewPasswordFormConfigArgsType {
  defaultValues: FormProps['defaultValues'];
  onSubmit: (data: { email: string; password: string }) => void;
  baseDataTestId: string;
  t: (id: string) => string;
  labels: ResetPasswordLabels;
  isBusinessBooker: boolean;
}

export const newPasswordFormConfig = ({
  defaultValues,
  onSubmit,
  baseDataTestId,
  labels,
  isBusinessBooker,
  t,
}: NewPasswordFormConfigArgsType) => {
  const newPasswordValidationSchema = yup.object().shape({
    email: yup
      .string()
      .required(
        isBusinessBooker
          ? (labels?.login?.business?.bookingLoginRequiredText ?? t('booking.login.required.text'))
          : t('booking.login.required.text')
      )
      .matches(
        FORM_VALIDATIONS.EMAIL.MATCHES,
        isBusinessBooker
          ? (labels?.login?.business?.bookingsInvalidEmailMsg ?? labels.login.invalidEmail)
          : labels.login.invalidEmail
      )
      .max(
        FORM_VALIDATIONS.EMAIL.MAX,
        isBusinessBooker
          ? (labels?.login?.business?.bookingEmailMaxLengthMsg ??
              t('booking.login.email.emailMaxLength'))
          : t('booking.login.email.emailMaxLength')
      ),
    password: yup
      .string()
      .required(
        isBusinessBooker
          ? (labels?.resetPassword?.passwordRequired ?? t('booking.login.required.text'))
          : t('booking.login.required.text')
      )
      .min(FORM_VALIDATIONS.PASSWORD.MIN, labels?.resetPassword?.passwordMin)
      .matches(FORM_VALIDATIONS.PASSWORD.MATCHES, labels?.resetPassword?.invalidPassword),
    confirm_password: yup
      .string()
      .required(labels?.resetPassword?.passwordRequired)
      .min(FORM_VALIDATIONS.PASSWORD.MIN, labels?.resetPassword?.passwordMin)
      .matches(FORM_VALIDATIONS.PASSWORD.MATCHES, labels?.resetPassword?.invalidPassword)
      .oneOf([yup.ref('password')], labels?.resetPassword?.invalidPasswordConfirmation),
  });

  return {
    elements: {
      fieldsContainerStyles: {
        marginBottom: '1rem',
      },
      fields: [
        {
          type: FORM_FIELD_TYPES.INPUT_EMAIL,
          name: 'email',
          label: labels?.resetPassword?.email,
          testid: formatDataTestId(baseDataTestId, 'Email'),
          props: {
            showIcon: true,
          },
        },
        {
          type: FORM_FIELD_TYPES.INPUT_PASSWORD,
          name: 'password',
          label: labels?.resetPassword?.password,
          testid: formatDataTestId(baseDataTestId, 'Password'),
          props: {
            showIcon: true,
          },
        },
        {
          type: FORM_FIELD_TYPES.INPUT_PASSWORD,
          name: 'confirm_password',
          label: labels?.resetPassword?.passwordConfirmation,
          styles: {
            marginBottom: 0,
          },
          testid: formatDataTestId(baseDataTestId, 'ConfirmPassword'),
          props: {
            showIcon: true,
          },
        },
        {
          type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
          name: 'help-text',
          content: <NewPasswordDescription labels={labels}></NewPasswordDescription>,
          testid: formatDataTestId(baseDataTestId, 'HelpText'),
        },
      ],
      buttons: [
        {
          type: FORM_BUTTON_TYPES.SUBMIT,
          label: labels?.resetPassword?.submitButton,
          action: onSubmit,
          styles: {
            marginBottom: '0',
          },
          props: {
            variant: 'primary',
            size: 'full',
          },
          testid: formatDataTestId(baseDataTestId, 'Submit'),
        },
      ],
    },
    defaultValues,
    validationSchema: newPasswordValidationSchema,
  } as FormProps;
};
