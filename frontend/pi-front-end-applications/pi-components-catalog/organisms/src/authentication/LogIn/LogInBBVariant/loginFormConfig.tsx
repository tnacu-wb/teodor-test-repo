import { Link, StyleProps } from '@chakra-ui/react';
import { type AuthenticationLabels } from '@whitbread-eos/api';
import {
  FORM_BUTTON_TYPES,
  FORM_FIELD_TYPES,
  FORM_VALIDATIONS,
  type FormProps,
} from '@whitbread-eos/atoms';
import { formatDataTestId } from '@whitbread-eos/utils';
import * as yup from 'yup';

interface LoginFormConfigArgsType {
  getFormState: FormProps['getFormState'];
  defaultValues: FormProps['defaultValues'];
  defaultErrors?: FormProps['defaultErrors'];
  onSubmit: (data: { email: string; password: string }) => void;
  handleResetPass: () => void;
  baseDataTestId: string;
  extraStyles?: StyleProps;
  buttonsContainerStyles?: StyleProps;
  resetForm?: number;
  fieldProps: {
    showIcon?: boolean;
    useTooltip?: boolean;
    useCustomTooltip?: boolean;
  };
  labels: AuthenticationLabels;
}

export const loginFormConfig = ({
  getFormState,
  defaultValues,
  defaultErrors,
  onSubmit,
  handleResetPass,
  baseDataTestId,
  extraStyles,
  buttonsContainerStyles,
  resetForm,
  fieldProps,
  labels,
}: LoginFormConfigArgsType) => {
  const loginFormValidationSchema = yup.object().shape({
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
    password: yup
      .string()
      .required(
        labels?.login?.business?.bookingLoginRequiredText || labels?.login?.badCredentialsError
      ),
  });

  const config = {
    elements: {
      buttonsContainerStyles,
      fields: [
        {
          type: FORM_FIELD_TYPES.INPUT_EMAIL,
          name: 'email',
          label: labels.login.business.emailPlaceholder,
          props: {
            ...fieldProps,
          },
          styles: { marginBottom: 'lg' },
          testid: formatDataTestId(baseDataTestId, 'Email'),
        },
        {
          type: FORM_FIELD_TYPES.INPUT_PASSWORD,
          name: 'password',
          label: labels.login.passwordPlaceholder,
          props: {
            ...fieldProps,
          } as StyleProps,
          testid: formatDataTestId(baseDataTestId, 'Password'),
        },
        {
          type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
          name: 'forgottenPassLink',
          content: (
            <Link
              {...extraStyles}
              textDecoration="underline"
              display="block"
              mb="md"
              data-testid={formatDataTestId(baseDataTestId, 'ResetPassLink')}
              onClick={handleResetPass}
            >
              {' '}
              {labels.login.forgotPassword}
            </Link>
          ),
        },
      ],
      buttons: [
        {
          type: FORM_BUTTON_TYPES.SUBMIT,
          label: labels.login.business.loginButton,
          action: onSubmit,
          props: {
            variant: 'primary',
            size: 'full',
          },
          testid: formatDataTestId(baseDataTestId, 'ButtonLogin'),
        },
      ],
    },
    defaultValues,
    validationSchema: loginFormValidationSchema,
    getFormState,
    defaultErrors,
    resetForm,
  } as FormProps;

  return config;
};
