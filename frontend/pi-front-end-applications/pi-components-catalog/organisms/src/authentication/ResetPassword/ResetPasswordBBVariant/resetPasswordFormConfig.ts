import { type AuthenticationLabels } from '@whitbread-eos/api';
import {
  FORM_BUTTON_TYPES,
  FORM_FIELD_TYPES,
  FORM_VALIDATIONS,
  type FormProps,
} from '@whitbread-eos/atoms';
import { formatDataTestId } from '@whitbread-eos/utils';
import * as yup from 'yup';

interface ResetPasswordFormConfigArgsType {
  getFormState: FormProps['getFormState'];
  defaultValues: FormProps['defaultValues'];
  defaultErrors?: FormProps['defaultErrors'];
  onSubmit: (data: { email: string }) => void;
  baseDataTestId: string;
  labels: AuthenticationLabels;
}

export const resetPasswordFormConfig = ({
  getFormState,
  defaultValues,
  defaultErrors,
  onSubmit,
  baseDataTestId,
  labels,
}: ResetPasswordFormConfigArgsType) => {
  const resetPasswordValidationSchema = yup.object().shape({
    email: yup
      .string()
      .required(labels?.login?.business?.bookingLoginRequiredText || labels?.login?.invalidEmail)
      .matches(
        FORM_VALIDATIONS.EMAIL.MATCHES,
        labels?.login?.business?.bookingsInvalidEmailMsg || labels?.login?.invalidEmail
      )
      .max(
        FORM_VALIDATIONS.EMAIL.MAX,
        labels?.login?.business?.bookingEmailMaxLengthMsg || labels?.login?.invalidEmail
      ),
  });

  const config = {
    elements: {
      fieldsContainerStyles: {
        marginBottom: '1rem',
      },
      fields: [
        {
          type: FORM_FIELD_TYPES.INPUT_EMAIL,
          name: 'email',
          label: labels.forgottenPassword.business.emailPlaceholder,
          props: {
            useTooltip: true,
          },
          styles: { marginBottom: 'lg' },
          testid: formatDataTestId(baseDataTestId, 'Email'),
        },
      ],
      buttons: [
        {
          type: FORM_BUTTON_TYPES.SUBMIT,
          label: labels.forgottenPassword.business.submitButton,
          action: onSubmit,
          props: {
            variant: 'primary',
            size: 'full',
          },
          styles: { marginBottom: '0' },
          testid: formatDataTestId(baseDataTestId, 'Submit'),
        },
      ],
    },
    defaultValues,
    validationSchema: resetPasswordValidationSchema,
    getFormState,
    defaultErrors,
  } as FormProps;

  return config;
};
