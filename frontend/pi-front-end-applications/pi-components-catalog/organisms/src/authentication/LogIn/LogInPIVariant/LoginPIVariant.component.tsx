import { Flex, FlexProps, Link, StyleProps, Text, TextProps } from '@chakra-ui/react';
import { type AuthenticationLabels } from '@whitbread-eos/api';
import type { HeaderInformationData } from '@whitbread-eos/api';
import {
  Alert,
  Form,
  type FormProps,
  Notification,
  Icon,
  ExternalLink,
} from '@whitbread-eos/atoms';
import {
  formatDataTestId,
  getSecureTwoURL,
  useCustomLocale,
  analytics,
  useSemanticTypography,
} from '@whitbread-eos/utils';
import { Dispatch, SetStateAction, useEffect, useState } from 'react';

import { getPibLoginRedirectUrl } from '../../../common/Header/helpers/helpers';
import { loginFormConfig } from './loginFormConfig';

export interface Props {
  setIsLoginForm: Dispatch<SetStateAction<boolean>>;
  defaultValues: FormProps['defaultValues'];
  getFormState?: FormProps['getFormState'];
  defaultErrors?: FormProps['defaultErrors'];
  toggleLoginModal: () => void;
  labels: AuthenticationLabels;
  headerInfoData?: HeaderInformationData['headerInformation'];
}

export default function LoginPIVariant({
  setIsLoginForm,
  getFormState,
  defaultValues,
  defaultErrors,
  toggleLoginModal,
  labels,
  headerInfoData,
}: Readonly<Props>) {
  const [resetForm, setResetForm] = useState(0);
  const [isError, setIsError] = useState(false);
  const { country, language } = useCustomLocale();
  const getTypographyProps = useSemanticTypography();

  const baseDataTestId = 'Login';
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
      });
      authIframe.contentWindow.postMessage(message, getSecureTwoURL());
    }
  };

  useEffect(() => {
    const handleMessages = (message: any) => {
      if (message?.origin === getSecureTwoURL()) {
        const data = typeof message?.data === 'string' ? JSON.parse(message.data) : message.data;
        const action = data.action;
        if (action === 'loginError') {
          analytics.update({
            logInUnsuccessful: true,
          });
          analytics.remove(['logInSuccessful']);
          setIsError(true);
        } else if (action === 'userLoggedIn') {
          analytics.update({
            logInSuccessful: true,
          });
          analytics.remove(['logInUnsuccessful']);
          setResetForm((prev) => prev + 1);
          setIsError(false);
          toggleLoginModal();
        }
      }
    };
    window.addEventListener('message', handleMessages);
    setResetForm((prev) => prev + 1);
    return () => {
      window.removeEventListener('message', handleMessages);
    };
  }, []);

  return (
    <Flex
      data-testid={formatDataTestId(baseDataTestId, 'Container')}
      direction="column"
      {...containerStyle}
    >
      <Text {...headerStyle} data-testid={formatDataTestId(baseDataTestId, 'Title')}>
        {labels.login.leisure.formLabel}
      </Text>
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
          getTypographyProps,
          getFormState,
          defaultValues,
          defaultErrors,
          onSubmit,
          handleResetPass: onLinkClick,
          baseDataTestId,
          extraStyles: linkStyles,
          resetForm,
          fieldProps: {
            useTooltip: true,
          },
          labels,
        })}
      />
      <Flex direction="row" justifyContent="center" mb="0rem" mt="0rem" {...linkStyles}>
        <Text mb="1.313rem">{labels.login.signupMessage}</Text>
        <Link
          data-testid={formatDataTestId(baseDataTestId, 'SignUpLink')}
          display="block"
          textDecoration="underline"
          pl="xs"
          href={`${getSecureTwoURL()}/${country}/${language}/account/register.html`}
        >
          {' '}
          {labels.login.signupLink}
        </Link>
      </Flex>
      {(headerInfoData?.content?.authentication?.login?.business?.travelForBusiness ||
        headerInfoData?.content?.authentication?.login?.business?.businessDomain) && (
        <>
          <Flex direction="row" justifyContent="center" {...linkStyles} {...businessLoginStyles}>
            {headerInfoData?.content?.authentication?.login?.business?.travelForBusiness && (
              <Text mb="0px">
                {headerInfoData.content.authentication.login.business.travelForBusiness}
              </Text>
            )}
          </Flex>
          {headerInfoData?.content?.authentication?.login?.business?.businessDomain && (
            <Flex direction="row" justifyContent="center" mb="1.125rem" mt="0.5rem" {...linkStyles}>
              <Link
                data-testid={formatDataTestId(baseDataTestId, 'BusinessLink')}
                display="block"
                textDecoration="underline"
                pl="xs"
                href={getPibLoginRedirectUrl(
                  headerInfoData.content.authentication.login.business.businessDomain,
                  language,
                  country
                )}
                target="_blank"
                isExternal={true}
              >
                {headerInfoData.content.authentication.login.business.businessLogin}
                <Icon ml="4px" display="inline-block" alignItems="center" svg={<ExternalLink />} />
              </Link>
            </Flex>
          )}
        </>
      )}
    </Flex>
  );
}

const businessLoginStyles = {
  borderTop: '1px solid var(--neutral-300-light-grey-4, #E0E0E0)',
  paddingTop: '2.125rem',
} as StyleProps;

const containerStyle = {
  margin: {
    mobile: '1.5rem 1rem',
    sm: '1.5rem 2rem',
    md: '1.5rem 6.875rem',
  },
  width: {
    mobile: 'auto',
    xs: 'auto',
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
  mb: '2xl',
} as TextProps;

const linkStyles = {
  fontStyle: 'normal',
  fontWeight: 'normal',
  fontSize: 'md',
  lineHeight: 'var(--chakra-lineHeights-3)',
  color: 'btnSecondaryEnabled',
} as StyleProps;

const errorContainerStyles = {
  marginBottom: '2xl',
};
