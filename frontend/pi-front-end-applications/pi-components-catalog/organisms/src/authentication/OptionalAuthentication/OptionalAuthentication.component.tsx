import { Box, ButtonProps, Flex, StyleProps, Text, TextProps, Link } from '@chakra-ui/react';
import { type AuthenticationLabels, FS_DISPLAY_REGISTER_GDP } from '@whitbread-eos/api';
import {
  Alert,
  Button,
  ChevronDown,
  ChevronUp,
  Form,
  type FormProps,
  Notification,
  ModalVariants,
} from '@whitbread-eos/atoms';
import {
  formatDataTestId,
  getSecureTwoURL,
  useCustomLocale,
  useFeatureSwitch,
  useSemanticTypography,
  useUserData,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { type SetStateAction, useCallback, useEffect, useState } from 'react';
import type { FieldErrors } from 'react-hook-form';

import { loginFormConfig } from '../LogIn/LogInPIVariant/loginFormConfig';
import { ResetPasswordPIVariant } from '../ResetPassword';

interface OptionalAuthenticationProps {
  labels: AuthenticationLabels;
  showIcon: boolean;
  isRegisterSelected: boolean;
  setRegisterSectionSelected: (param: boolean) => void;
}

export default function OptionalAuthentication({
  labels,
  showIcon,
  setRegisterSectionSelected,
  isRegisterSelected,
}: Readonly<OptionalAuthenticationProps>) {
  const { t } = useTranslation();
  const getTypographyProps = useSemanticTypography();
  const [isSignInSelected, setIsSignInSelected] = useState(false);
  const [isResetPasswordOpen, setIsResetPasswordOpen] = useState(false);
  const [resetPasswordDefaultValues, setResetPasswordDefaultValues] = useState({
    email: '',
  });
  const [resetPasswordDefaultErrors, setResetPasswordDefaultErrors] = useState({});
  const [resetForm, setResetForm] = useState(0);
  const { isLoggedIn } = useUserData();
  const [isError, setIsError] = useState(false);
  const { country } = useCustomLocale();

  const isRegisterGDPEnabled = useFeatureSwitch({
    featureSwitchKey: FS_DISPLAY_REGISTER_GDP,
    fallbackValue: true,
    country: country,
  });

  const onSubmit = (data: { email: string; password: string }) => {
    const authIframe = document.getElementById('authIframe') as HTMLIFrameElement | null;

    if (authIframe?.contentWindow) {
      const message = JSON.stringify({
        action: 'login',
        username: data.email,
        password: data.password,
      });
      authIframe.contentWindow.postMessage(message, getSecureTwoURL());
    }
  };

  const [loginDefaultValues, setLoginDefaultValues] = useState({
    email: '',
    password: '',
  });
  const [loginDefaultErrors, setLoginDefaultErrors] = useState({});
  const getLoginFormState: FormProps['getFormState'] = useCallback(
    (state1: any, errors1: any) => {
      setLoginDefaultValues(state1 as SetStateAction<{ email: string; password: string }>);
      setLoginDefaultErrors(errors1 as FieldErrors<{ email: string; password: string }>);
    },
    [setLoginDefaultValues, setLoginDefaultErrors]
  );

  const baseDataTestId = 'GuestDetails-OptionalAuth';

  const setSectionSelected = (section: 'signin' | 'register', selected: boolean) => {
    setIsSignInSelected(false);
    setRegisterSectionSelected(false);
    if (selected) {
      if (section === 'signin') {
        setIsSignInSelected(true);
        setRegisterSectionSelected(false);
      } else {
        setRegisterSectionSelected(true);
        setIsSignInSelected(false);
      }
    }
  };

  const getResetPasswordFormState: FormProps['getFormState'] = useCallback(
    (state: any, errors: any) => {
      setResetPasswordDefaultValues(state as SetStateAction<{ email: string }>);
      setResetPasswordDefaultErrors(errors as FieldErrors<{ email: ''; password: string }>);
    },
    [setResetPasswordDefaultValues, setResetPasswordDefaultErrors]
  );

  const handleResetPass = () => {
    setIsResetPasswordOpen(true);
  };

  const handleCloseModal = () => {
    setIsResetPasswordOpen(false);
  };

  useEffect(() => {
    const handleMessages = (message: any) => {
      if (message?.origin === getSecureTwoURL()) {
        const data = typeof message?.data === 'string' ? JSON.parse(message.data) : message.data;
        const action = data.action;
        if (action === 'loginError') {
          setIsError(true);
        } else if (action === 'userLoggedIn') {
          setResetForm((prev) => prev + 1);
          setIsError(false);
        }
      }
    };
    window.addEventListener('message', handleMessages);
    return () => {
      window.removeEventListener('message', handleMessages);
    };
  }, []);

  if (!isLoggedIn) {
    return (
      <>
        <Flex
          flexDirection="column"
          {...containerStyles}
          data-testid={formatDataTestId(baseDataTestId, 'Container')}
        >
          <Text
            {...headerStyle}
            {...getTypographyProps(headerLegacyTypography, headerSemanticTypography)}
            data-testid={formatDataTestId(baseDataTestId, 'Header')}
          >
            {t('booking.login.labelHaveAccount')}
            &nbsp;
            {t('booking.login.labelOptional')}
          </Text>
          <Text
            {...contentTextStyle}
            {...getTypographyProps(contentLegacyTypography, contentSemanticTypography)}
            data-testid={formatDataTestId(baseDataTestId, 'Description')}
          >
            {t('booking.login.notRegisteredYetLabel')}
          </Text>

          <Flex
            flexDirection={{
              mobile: 'column',
              md: 'row',
            }}
          >
            <Box flex="1">
              <Button
                variant="generic"
                size="full"
                data-testid={formatDataTestId(baseDataTestId, 'SignIn-Option')}
                mr="lg"
                mb="lg"
                onClick={() => setSectionSelected('signin', !isSignInSelected)}
                {...(isSignInSelected && optionSelectedStyle)}
              >
                <Text
                  {...(isSignInSelected && selectedTextColor)}
                  {...getTypographyProps({}, authOptionSemanticTypography)}
                >
                  {t('booking.login.labelHaveAccount')}
                </Text>
                <Box ml="xmd">{!isSignInSelected ? <ChevronDown /> : <ChevronUp />}</Box>
              </Button>
              {isSignInSelected && (
                <Flex
                  data-testid={formatDataTestId(baseDataTestId, 'Login-Container')}
                  direction="column"
                  {...loginContainerStyles}
                >
                  {isError && (
                    <Flex {...errorContainerStyles}>
                      <Notification
                        title={labels?.login?.badCredentialsError}
                        description={
                          <Link
                            textDecoration="underline"
                            display="block"
                            data-testid={formatDataTestId(baseDataTestId, 'ResetPassLink')}
                            onClick={handleResetPass}
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
                      getFormState: getLoginFormState,
                      defaultValues: loginDefaultValues,
                      defaultErrors: loginDefaultErrors,
                      onSubmit,
                      handleResetPass,
                      baseDataTestId,
                      extraStyles: linkStyles,
                      buttonsContainerStyles,
                      resetForm,
                      fieldProps: {
                        showIcon: showIcon,
                        hideForgottenPassword: false,
                      },
                      labels,
                    })}
                  />
                </Flex>
              )}
            </Box>

            <Box flex="1" ml={{ md: 'lg' }}>
              {isRegisterGDPEnabled && (
                <Button
                  variant="generic"
                  size="full"
                  data-testid={formatDataTestId(baseDataTestId, 'Register-Option')}
                  mb="lg"
                  onClick={() => setSectionSelected('register', !isRegisterSelected)}
                  {...(isRegisterSelected && optionSelectedStyle)}
                >
                  <Text
                    {...(isRegisterSelected && selectedTextColor)}
                    {...getTypographyProps({}, authOptionSemanticTypography)}
                  >
                    {t('booking.login.labelRegister')}
                  </Text>
                  <Box ml="xmd">{!isRegisterSelected ? <ChevronDown /> : <ChevronUp />}</Box>
                </Button>
              )}
            </Box>
          </Flex>
        </Flex>
        <ModalVariants
          onClose={handleCloseModal}
          isOpen={isResetPasswordOpen}
          variant="default"
          variantProps={{ title: '', delimiter: true, sizeSm: 'full' }}
          updatedWidth={{ md: 'auto', sm: 'full' }}
          dataTestId={'Header-Auth'}
        >
          <ResetPasswordPIVariant
            onClose={handleCloseModal}
            defaultValues={resetPasswordDefaultValues}
            defaultErrors={resetPasswordDefaultErrors}
            getFormState={getResetPasswordFormState}
            labels={labels}
            isBookingFlow={true}
          />
        </ModalVariants>
      </>
    );
  }
  return null;
}

const headerStyle = {
  color: 'darkGrey1',
  mb: 'md',
} as TextProps;

const headerLegacyTypography = {
  fontWeight: 'semibold',
  fontSize: '2xl',
  lineHeight: '4',
} as TextProps;

const headerSemanticTypography = {
  textStyle: 'heading-m',
} as TextProps;

const contentTextStyle = {
  color: 'darkGrey1',
  mb: 'lg',
} as TextProps;

const contentLegacyTypography = {
  fontWeight: 'normal',
  fontSize: 'md',
  lineHeight: '3',
} as TextProps;

const contentSemanticTypography = {
  textStyle: 'body-m-regular',
} as TextProps;

const containerStyles = {
  mb: {
    mobile: 'md',
    lg: '0',
  },
  maxWidth: {
    md: '49rem',
    xl: '55rem',
  },
  px: {
    mobile: '0',
    sm: 'md',
    md: '0',
    lg: '0',
  },
  mt: {
    mobile: '2xl',
    sm: '2xl',
    lg: '0',
  },
};

const optionSelectedStyle = {
  borderColor: 'var(--chakra-colors-primary)',
};

const selectedTextColor = {
  color: 'var(--chakra-colors-primary)',
};

const authOptionSemanticTypography = {
  textStyle: 'label-xl',
} as TextProps;

const loginContainerStyles = {
  maxWidth: {
    md: '20.75rem',
    lg: '23.75rem',
    xl: '25.344rem',
  },
};

const buttonsContainerStyles = {
  maxWidth: {
    lg: '19.313rem',
    xl: '19.313rem',
  },
  width: '100%',
} as ButtonProps;

const errorContainerStyles = {
  marginBottom: '2xl',
};

const linkStyles = {
  fontStyle: 'normal',
  fontWeight: 'normal',
  fontSize: 'md',
  lineHeight: 'var(--chakra-lineHeights-3)',

  color: 'btnSecondaryEnabled',
} as StyleProps;
