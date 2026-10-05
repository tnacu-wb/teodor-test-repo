import { Flex, FlexProps, Link, StyleProps, Text, TextProps } from '@chakra-ui/react';
import { type AuthenticationLabels } from '@whitbread-eos/api';
import {
  Alert,
  Error,
  Form,
  type FormProps,
  Info,
  Notification,
  Success24,
} from '@whitbread-eos/atoms';
import { LoginFormFooter } from '@whitbread-eos/molecules';
import { formatDataTestId, getSecureTwoURL, useCustomLocale } from '@whitbread-eos/utils';
import { Dispatch, SetStateAction, useEffect, useState } from 'react';

import { loginFormConfig } from './loginFormConfig';

export interface Props {
  setIsLoginForm: Dispatch<SetStateAction<boolean>>;
  defaultValues: FormProps['defaultValues'];
  getFormState?: FormProps['getFormState'];
  defaultErrors?: FormProps['defaultErrors'];
  toggleLoginModal: () => void;
  showRegisterNotification?: boolean;
  hasRegisteredSuccessfully?: boolean;
  labels: AuthenticationLabels;
}

export default function LoginBBVariant({
  setIsLoginForm,
  getFormState,
  defaultValues,
  defaultErrors,
  toggleLoginModal,
  showRegisterNotification,
  hasRegisteredSuccessfully,
  labels,
}: Readonly<Props>) {
  const { country, language } = useCustomLocale();
  const [resetForm, setResetForm] = useState(0);
  const [isError, setIsError] = useState(false);
  const baseDataTestId = 'Login';
  const urlOrigin = typeof window !== 'undefined' ? window.location.origin : '';

  useEffect(() => {
    const handleMessages = (message: { origin: string; data: object }) => {
      if (message?.origin === getSecureTwoURL()) {
        const data = typeof message?.data === 'string' ? JSON.parse(message.data) : message.data;
        const action = data.action;
        if (action === 'loginError') {
          setIsError(true);
        } else if (action === 'userLoggedIn') {
          setResetForm((prev) => prev + 1);
          setIsError(false);
          toggleLoginModal();
        }
      }
    };

    window.addEventListener('message', handleMessages);

    return () => {
      window.removeEventListener('message', handleMessages);
    };
  }, [toggleLoginModal]);

  const onLinkClick = () => {
    setIsLoginForm(false);
  };

  const onSubmit = (data: { email: string; password: string }) => {
    const authIframe = document.getElementById('authIframe') as HTMLIFrameElement;
    if (authIframe?.contentWindow) {
      const message = JSON.stringify({
        action: 'login',
        username: data.email,
        password: data.password,
        isBusiness: true,
      });

      authIframe.contentWindow.postMessage(message, getSecureTwoURL());
    }
  };

  const bbNotificationStyles = {
    mb: isError ? 'xlg' : '3xl',
  };

  return (
    <>
      <Flex
        data-testid={formatDataTestId(baseDataTestId, 'Container')}
        direction="column"
        {...containerStyle}
      >
        <Text {...headerStyle} data-testid={formatDataTestId(baseDataTestId, 'Title')}>
          {labels.login.business.formLabel}
        </Text>
        {!showRegisterNotification && (
          <Flex
            {...bbNotificationStyles}
            data-testid={formatDataTestId(baseDataTestId, 'BBNotification')}
          >
            <Notification
              description={labels.login.business.loginInfoNotification}
              prefixDataTestId="BBNotification"
              variant="info"
              status="info"
              svg={<Info />}
            />
          </Flex>
        )}
        {showRegisterNotification && hasRegisteredSuccessfully && (
          <Flex mb="lg" data-testid={formatDataTestId(baseDataTestId, 'BBSuccessNotification')}>
            <Notification
              title={labels.login.business.companyActivateSuccessTitle}
              description={labels.login.business.companyActivateSuccessBody}
              prefixDataTestId="BBSuccessNotification"
              variant="success"
              status="success"
              svg={<Success24 />}
            />
          </Flex>
        )}
        {showRegisterNotification && !hasRegisteredSuccessfully && (
          <Flex mb="lg" data-testid={formatDataTestId(baseDataTestId, 'BBErrorNotification')}>
            <Notification
              title={labels.login.business.companyActivateFailTitle}
              description={
                <>
                  <Text
                    data-testid={formatDataTestId(baseDataTestId, 'BBErrorNotificationDescription')}
                  >
                    {labels?.login?.business?.companyActivateFailBody?.split('<span>', 2)[0]}
                  </Text>
                  <Link
                    textDecoration="underline"
                    display="block"
                    data-testid={formatDataTestId(baseDataTestId, 'BBSupportLink')}
                  >
                    {
                      labels?.login?.business?.companyActivateFailBody
                        ?.split('<span>', 2)[1]
                        ?.split('</span>')[0]
                    }
                  </Link>
                </>
              }
              prefixDataTestId="BBErrorNotification"
              variant="error"
              status="error"
              svg={<Error />}
            />
          </Flex>
        )}
        {isError && (
          <Flex {...errorContainerStyles}>
            <Notification
              title={labels?.login?.badCredentialsError}
              description={
                <Link
                  textDecoration="underline"
                  display="block"
                  data-testid={formatDataTestId(baseDataTestId, 'ResetPassLink')}
                  onClick={onLinkClick}
                >
                  {' '}
                  {labels.login.forgotPassword}
                </Link>
              }
              variant="alert"
              status="warning"
              svg={<Alert />}
            />
          </Flex>
        )}
        <Form
          {...loginFormConfig({
            getFormState,
            defaultValues,
            defaultErrors,
            onSubmit,
            handleResetPass: onLinkClick,
            baseDataTestId,
            extraStyles: linkStyles,
            resetForm,
            fieldProps: {
              useCustomTooltip: true,
            },
            labels,
          })}
        />

        <Flex direction="row" justifyContent="center">
          <Text>{labels.login.signupMessage}</Text>
          <Link
            data-testid={formatDataTestId(baseDataTestId, 'SignUpLink')}
            href={`${urlOrigin}/${country}/${language}/business-booker/account/register.html`}
            {...signUpLinkStyles}
          >
            {' '}
            {labels.login.signupLink}
          </Link>
        </Flex>
      </Flex>

      <LoginFormFooter labels={labels} />
    </>
  );
}

const containerStyle = {
  margin: {
    mobile: '1.5rem 1rem',
    sm: '1.5rem 4rem',
    md: '1.5rem 6.875rem',
  },
  width: {
    mobile: 'auto',
    xs: 'auto',
    sm: '25rem',
    md: '26rem',
  },
} as FlexProps;

const headerStyle = {
  fontStyle: 'normal',
  fontWeight: 'semibold',
  fontSize: 'lg',
  lineHeight: 'var(--chakra-lineHeights-3)',
  textAlign: 'center',
  color: 'darkGrey1',
  mb: 'xlg',
} as TextProps;

const linkStyles = {
  fontStyle: 'normal',
  fontWeight: 'normal',
  fontSize: 'md',
  lineHeight: 'var(--chakra-lineHeights-3)',
  color: 'btnSecondaryEnabled',
} as StyleProps;

const signUpLinkStyles = {
  display: 'block',
  textDecoration: 'underline',
  pl: 'xs',
  color: 'btnSecondaryEnabled',
} as StyleProps;

const errorContainerStyles = {
  marginBottom: 'xl',
};
