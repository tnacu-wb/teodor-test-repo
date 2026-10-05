import { Link, StyleProps, Text, TextProps } from '@chakra-ui/react';
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
  getTypographyProps: (legacyTypography: TextProps, semanticTypography: TextProps) => TextProps;
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
    hideForgottenPassword?: boolean;
  };
  labels: AuthenticationLabels;
}

export const loginFormConfig = ({
  getTypographyProps,
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

  const { hideForgottenPassword = false } = fieldProps;

  const inputFieldStyles = {
    inputElementStyles: {
      ...getTypographyProps({}, inputFieldSemanticTypography),
      ...placeholderStyles,
    },
  };

  const forgottenPasswordLinkTypographyStyles = getTypographyProps(
    {},
    forgottenPasswordLinkSemanticTypography
  );

  const loginButtonTypographyStyles = getTypographyProps({}, loginButtonSemanticTypography);

  const config = {
    elements: {
      buttonsContainerStyles,
      fields: [
        {
          type: FORM_FIELD_TYPES.INPUT_EMAIL,
          name: 'email',
          label: labels.login.leisure.emailPlaceholder,
          props: { ...fieldProps, styles: inputFieldStyles },
          styles: { marginBottom: 'lg' },
          testid: formatDataTestId(baseDataTestId, 'Email'),
        },
        {
          type: FORM_FIELD_TYPES.INPUT_PASSWORD,
          name: 'password',
          label: labels.login.passwordPlaceholder,
          props: { ...fieldProps, styles: inputFieldStyles } as StyleProps,
          testid: formatDataTestId(baseDataTestId, 'Password'),
        },
        {
          type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
          name: 'forgottenPassLink',
          hidden: hideForgottenPassword, // hiding forgotten password link from guest details page until work for DNRQ-69071 is done
          content: (
            <Link
              {...extraStyles}
              textDecoration="underline"
              display="block"
              mb="md"
              data-testid={formatDataTestId(baseDataTestId, 'ResetPassLink')}
              onClick={handleResetPass}
            >
              <Text as="span" {...forgottenPasswordLinkTypographyStyles}>
                {labels.login.forgotPassword}
              </Text>
            </Link>
          ),
        },
      ],
      buttons: [
        {
          type: FORM_BUTTON_TYPES.SUBMIT,
          label: labels.login.leisure.loginButton,
          action: onSubmit,
          props: {
            variant: 'login',
            size: 'full',
            ...loginButtonTypographyStyles,
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

const placeholderStyles = {
  _placeholder: {
    fontStyle: 'normal',
    color: 'var(--chakra-colors-chakra-subtle-text)',
  },
};

const inputFieldSemanticTypography = {
  textStyle: 'body-m-regular',
} as TextProps;

const forgottenPasswordLinkSemanticTypography = {
  textStyle: 'link-m-regular',
} as TextProps;

const loginButtonSemanticTypography = {
  textStyle: 'label-xl',
} as TextProps;
