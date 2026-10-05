import { Flex, Text, TextProps } from '@chakra-ui/react';
import { BOOKING_SUBCHANNEL, ResetPasswordLabels } from '@whitbread-eos/api';
import { Alert, Button, Form, type FormProps, Info, Notification } from '@whitbread-eos/atoms';
import { formatDataTestId, useRestMutationRequest } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import getConfig from 'next/config';

import { newPasswordFormConfig } from './newPasswordFormConfig';

export interface Props {
  defaultValues: FormProps['defaultValues'];
  toggleLoginModal: () => void;
  token: string;
  isBusinessBooker: boolean;
  labels: ResetPasswordLabels;
}

export default function NewPassword({
  defaultValues,
  toggleLoginModal,
  token,
  isBusinessBooker,
  labels,
}: Readonly<Props>) {
  const baseDataTestId = 'NewPassword';
  const { t } = useTranslation();
  const { publicRuntimeConfig = {} } = getConfig() || {};

  const { mutation, data, isError } = useRestMutationRequest(
    `${publicRuntimeConfig.NEXT_PUBLIC_REST_API}/auth/hotels/forgot-password?business=${isBusinessBooker}`,
    'PUT',
    {
      'password-token': token,
      bookingchannel: BOOKING_SUBCHANNEL.WEB,
    }
  );

  const onSubmit = (data: { email: string; password: string }) => {
    mutation.mutate({
      customerId: data.email,
      newPassword: data.password,
    });
  };

  return (
    <>
      {data?.passwordChanged === true ? (
        <Flex
          direction="column"
          data-testid={formatDataTestId(baseDataTestId, 'Container-Successful')}
        >
          <Notification
            description={labels?.resetPassword?.successMessage}
            variant="info"
            status="info"
            svg={<Info />}
            prefixDataTestId={formatDataTestId(baseDataTestId, 'Successful')}
            maxW="full"
          />
          <Button size="md" variant="secondary" mt="lg" onClick={toggleLoginModal}>
            {labels?.resetPassword?.logInButton}
          </Button>
        </Flex>
      ) : (
        <Flex direction="column" data-testid={formatDataTestId(baseDataTestId, 'Container')}>
          <Text {...titleStyle}>{labels?.resetPassword?.resetPasswordTitle}</Text>
          <Flex {...newPasswordStyles}>
            <Form
              {...newPasswordFormConfig({
                defaultValues,
                onSubmit,
                baseDataTestId,
                t,
                labels,
                isBusinessBooker,
              })}
            />
          </Flex>
          {isError && (
            <Flex {...errorContainerStyles}>
              <Notification
                description={t('booking.login.changePassword.error')}
                variant="alert"
                status="warning"
                svg={<Alert />}
                maxW="full"
              />
            </Flex>
          )}
        </Flex>
      )}
    </>
  );
}

const newPasswordStyles = {
  marginTop: '2xl',
  width: {
    mobile: 'auto',
    xs: 'auto',
    sm: '25rem',
    md: '26rem',
  },
};

const titleStyle = {
  color: 'darkGrey3',
  fontSize: '3xxl',
  fontWeight: 'semibold',
  lineHeight: '4',
} as TextProps;

const errorContainerStyles = {
  mt: '2xl',
};
