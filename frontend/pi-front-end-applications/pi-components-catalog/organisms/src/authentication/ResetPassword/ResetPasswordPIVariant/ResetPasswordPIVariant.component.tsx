import { Flex, FlexProps, Link, StyleProps, Text, TextProps } from '@chakra-ui/react';
import { type AuthenticationLabels } from '@whitbread-eos/api';
import { Alert, Form, type FormProps, Notification } from '@whitbread-eos/atoms';
import { formatDataTestId, useRestMutationRequest } from '@whitbread-eos/utils';
import getConfig from 'next/config';
import { Dispatch, SetStateAction, useEffect, useState } from 'react';

import { resetPasswordFormConfig } from './resetPasswordFormConfig';

export interface Props {
  setIsLoginForm?: Dispatch<SetStateAction<boolean>>;
  onClose?: () => void;
  getFormState?: FormProps['getFormState'];
  defaultValues: FormProps['defaultValues'];
  defaultErrors?: FormProps['defaultErrors'];
  labels: AuthenticationLabels;
  isBookingFlow?: boolean;
}

export default function ResetPasswordPIVariant({
  getFormState,
  defaultValues,
  defaultErrors,
  setIsLoginForm,
  onClose,
  labels,
  isBookingFlow,
}: Readonly<Props>) {
  const baseDataTestId = 'ResetPassword';
  const { publicRuntimeConfig = {} } = getConfig() || {};
  const { mutation, data, isError } = useRestMutationRequest(
    `${publicRuntimeConfig.NEXT_PUBLIC_REST_API}/auth/hotels/forgot-password?business=false`,
    'POST'
  );

  const [resetForm, setResetForm] = useState<number>(0);

  const getEmailRedirectUrl = () => {
    const path = '/gb/en/reset-password.html?token=';
    if (process.env.NODE_ENV === 'development') {
      return `${process.env.NEXT_PUBLIC_POC_URL}${path}`;
    }
    return `${window.location.protocol}//${window.location.hostname}${path}`;
  };

  useEffect(() => {
    setResetForm((prev) => prev + 1);
  }, []);

  const onCancel = () => {
    if (setIsLoginForm) {
      setIsLoginForm(true);
    } else {
      onClose?.();
    }
  };

  const onSubmit = (data: { email: string }) => {
    mutation.mutate({
      url: getEmailRedirectUrl(),
      username: data.email,
    });
  };

  return (
    <Flex
      data-testid={formatDataTestId(baseDataTestId, 'Container')}
      direction="column"
      {...containerStyles}
    >
      {data?.success === true ? (
        <Flex {...textContainerStyles}>
          <Text {...resetPassTitleStyles}>{labels?.forgottenPassword?.emailSentHeader}</Text>
          <Text {...resetPassDescStyles}>{labels?.forgottenPassword?.emailSentMessage}</Text>
          <Flex direction="row" justifyContent="center">
            <Link
              {...linkStyles}
              onClick={onCancel}
              data-testid={formatDataTestId(
                baseDataTestId,
                isBookingFlow ? 'BackToYourDetails' : 'BackToLogin'
              )}
            >
              {isBookingFlow
                ? labels.forgottenPassword.backToYourDetails
                : labels.forgottenPassword.backToLogin}
            </Link>
          </Flex>
        </Flex>
      ) : (
        <>
          <Flex {...textContainerStyles}>
            <Text {...resetPassTitleStyles}>{labels.forgottenPassword.leisure.formTitle}</Text>
            <Text {...resetPassDescStyles}>{labels.forgottenPassword.leisure.formLabel}</Text>
          </Flex>

          {isError && (
            <Flex {...errorContainerStyles} data-testid={formatDataTestId(baseDataTestId, 'Error')}>
              <Notification
                description={labels.forgottenPassword.genericError}
                variant="alert"
                status="warning"
                svg={<Alert />}
              />
            </Flex>
          )}

          <Form
            {...resetPasswordFormConfig({
              getFormState,
              defaultValues,
              defaultErrors,
              onSubmit,
              baseDataTestId,
              labels,
              resetForm,
            })}
          />

          <Flex direction="row" justifyContent="center">
            <Link
              {...linkStyles}
              onClick={onCancel}
              data-testid={formatDataTestId(baseDataTestId, 'Cancel')}
            >
              {labels.forgottenPassword.cancel}
            </Link>
          </Flex>
        </>
      )}
    </Flex>
  );
}

const containerStyles = {
  margin: {
    mobile: '1.5rem 1rem',
    sm: '1.5rem 2rem',
    md: '1.5rem 6.875rem',
  },
  width: {
    mobile: 'auto',
    xs: 'auto',
    sm: '25rem',
    md: '26rem',
  },
};

const textContainerStyles = {
  alignItems: 'center',
  direction: 'column',
  padding: 0,
} as FlexProps;

const resetPassTitleStyles = {
  color: 'darkGrey1',
  fontSize: 'md',
  fontWeight: 'semibold',
  marginBottom: 'md',
} as TextProps;

const resetPassDescStyles = {
  align: 'center',
  color: 'darkGrey1',
  fontSize: 'md',
  fontWeight: 'normal',
  marginBottom: '2xl',
} as TextProps;

const linkStyles = {
  color: 'btnSecondaryEnabled',
  textDecoration: 'underline',
  fontSize: 'md',
} as StyleProps;

const errorContainerStyles = {
  marginBottom: '2xl',
};
