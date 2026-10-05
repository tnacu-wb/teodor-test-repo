import type { BoxProps, StyleProps } from '@chakra-ui/react';
import { Box, Text, Container, Flex } from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import type { RegisterPersonalDetails, UpdatePreferencesRequest } from '@whitbread-eos/api';
import {
  BOOKING_CHANNEL,
  BOOKING_FLOW_PAGE,
  BRANDCODES,
  FT_PI_RECAPTCHA_REGISTER,
  FT_PI_BB_CCUI_COMPANY_NAME_SPECIAL_CHARACTERS,
  FT_PI_BB_CCUI_COUNTRY_SELECTOR_FILTERABLE,
  FT_PI_BASKET_IDS_COOKIE_VALIDATION,
  GET_REGISTER_BOOKING_CONFIRMATION,
  LanguageEnum,
  MARKETING_CHANNEL,
  MARKETING_JOURNEY,
  MARKETING_OPT_IN,
  ONE_MINUTE,
  PageName,
  REGISTER_USER_ACCOUNT,
  SearchRoomType,
  ROOM_CODES,
} from '@whitbread-eos/api';
import { Error, FormProps, LoadingSpinner, Notification } from '@whitbread-eos/atoms';
import { SEO as Seo } from '@whitbread-eos/molecules';
import {
  decodeFromBase64,
  formatDataTestId,
  getBasketIdsJsonFromCookie,
  getCookie,
  REGISTER_BASKET_REF_COOKIE,
  useCustomLocale,
  useFeatureToggle,
  useMutationRequest,
  isNonEmptyString,
  useQueryRequest,
  GLOBALS,
  analytics,
  setCookie,
  validateBasketIdInCookie,
  MAX_ROOMS_SEARCH_LIMIT,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import getConfig from 'next/config';
import dynamic from 'next/dynamic';
import { NextRouter } from 'next/router';
import { SetStateAction, useCallback, useEffect, useState } from 'react';
import { useGoogleReCaptcha } from 'react-google-recaptcha-v3';

import { registerDetailsFormConfig } from './piFormConfig/registerDetailsFormConfig';

interface Props {
  queryClient: QueryClient;
  router: NextRouter;
}

const SearchContainer = dynamic(
  async () => (await import('@whitbread-eos/organisms')).PISearchContainer,
  {
    loading: () => <Text> </Text>,
    ssr: false,
  }
);

const Form = dynamic(
  async () => {
    const { Form } = await import('@whitbread-eos/atoms');
    return { default: Form };
  },
  {
    ssr: false,
  }
);

export default function RegisterPagePi({ queryClient, router }: Readonly<Props>) {
  const { t } = useTranslation();
  const [resetForm, setResetForm] = useState<number>(0);
  const [clearPhoneFields, setClearPhoneFields] = useState<boolean>(false);
  const [isLocationRequired, setIsLocationRequired] = useState<boolean>(false);
  const [captchaToken, setCaptchaToken] = useState<string>();

  const { language: currentLang, country: currentCountry } = useCustomLocale();
  const { searchLocation, ARRdd, ARRmm, ARRyyyy, NIGHTS, ROOMS } = router.query;
  const { executeRecaptcha } = useGoogleReCaptcha();

  const { publicRuntimeConfig = {} } = getConfig() || {};
  const {
    [FT_PI_RECAPTCHA_REGISTER]: isRecaptchaEnabled,
    [FT_PI_BB_CCUI_COMPANY_NAME_SPECIAL_CHARACTERS]: isCompanyNameAdvanceEnabled,
    [FT_PI_BB_CCUI_COUNTRY_SELECTOR_FILTERABLE]: isCountrySelectorFilterableEnabled,
    [FT_PI_BASKET_IDS_COOKIE_VALIDATION]: isBasketIdsCookieValidationEnabled,
  } = useFeatureToggle();

  const {
    mutation: registerMutation,
    isLoading: registerIsLoading,
    isError: registerIsError,
    data: registerData,
    error: registerError,
    isSuccess: registerIsSuccess,
  } = useMutationRequest(REGISTER_USER_ACCOUNT, true);

  useEffect(() => {
    if (!registerIsError && registerIsSuccess && registerData) {
      window.__satelliteLoaded && window._satellite.track('successfulRegistrationComplete');

      setCookie(MARKETING_OPT_IN, window?.analyticsData?.marketingOptIn, ONE_MINUTE);

      window.location.href = `/${currentCountry}/${currentLang}/${
        publicRuntimeConfig[`NEXT_PUBLIC_REGISTRATION_REDIRECT_${currentCountry.toUpperCase()}`]
      }`;
      // Commented as part of Hotfix - OPERA06022024
      // Adding delay between registration and auto login
      // setTimeout(() => {
      //   //set marketing preferences once registration is successful

      //   const authIframe = document.getElementById('authIframe');
      //   // eslint-disable-next-line
      //   // @ts-ignore
      //   if (authIframe?.contentWindow) {
      //     const message = JSON.stringify({
      //       action: 'login',
      //       username: userDetails?.email,
      //       password: userDetails?.password,
      //     });
      //     // eslint-disable-next-line
      //     // @ts-ignore
      //     authIframe.contentWindow.postMessage(message, getSecureTwoURL());
      //   }

      //   const handleMessages = (message: { origin: string; data: object }) => {
      //     if (message?.origin === getSecureTwoURL()) {
      //       const data =
      //         typeof message?.data === 'string' ? JSON.parse(message.data) : message.data;
      //       const { action } = data;
      //       if (action !== 'userLoggedIn') {
      //         setIsError(true);
      //         setResetForm((prev) => prev + 1);
      //       } else {
      //         setResetForm((prev) => prev + 1);
      //         router.push(`/${currentCountry}/${currentLang}/account/dashboard.html`);
      //       }
      //     }
      //   };
      //   window.addEventListener('message', handleMessages);
      //   return () => window.removeEventListener('message', handleMessages);
      // }, Number(publicRuntimeConfig?.NEXT_PUBLIC_REGISTER_LOGIN_DELAY) * 1000 || 0);
    }

    if (registerIsError || registerError) {
      window.__satelliteLoaded && window._satellite.track('unsuccessfulRegistration');

      analytics.update({
        validation: 'existing email address',
      });

      setResetForm((prev) => prev + 1);
      setClearPhoneFields(true);
    }
  }, [registerData, registerIsError, registerIsSuccess, router]);

  useEffect(() => {
    clearPhoneFields && setClearPhoneFields(false);
  }, [clearPhoneFields]);

  const defaultRooms: SearchRoomType[] = [];
  for (let idx = 0; idx < Number(ROOMS) && idx < MAX_ROOMS_SEARCH_LIMIT; idx++) {
    const room: SearchRoomType = {
      adults: Number(router.query[`ADULT${idx + 1}`]),
      children: Number(router.query[`CHILD${idx + 1}`]),
      shouldBeAccessible: router.query[`INTTYP${idx + 1}`] === ROOM_CODES.accessible,
      shouldIncludeCot: !!Number(router.query[`COT${idx + 1}`]),
      roomType: router.query[`INTTYP${idx + 1}`] as string,
    };
    defaultRooms.push(room);
  }

  const basketReference = getBasketReference();

  // Only validate basket access if feature toggle is enabled
  // If toggle is disabled, allow access for backward compatibility
  const hasBasketAccess = isBasketIdsCookieValidationEnabled
    ? Boolean(basketReference && validateBasketIdInCookie(basketReference))
    : Boolean(basketReference);

  // Get the array of encoded basket IDs from cookie as JSON string to pass as authorization header
  const basketIdsJsonString = getBasketIdsJsonFromCookie();

  const { data: bkngData } = useQueryRequest(
    [
      'GetRegisterPageBookingConfirmation',
      currentLang,
      currentCountry,
      basketReference,
      BOOKING_CHANNEL.PI,
      BOOKING_FLOW_PAGE.REGISTER_PAGE,
    ],
    GET_REGISTER_BOOKING_CONFIRMATION,
    {
      basketReference,
      language: currentLang,
      country: currentCountry,
      bookingChannel: BOOKING_CHANNEL.PI,
      flow: BOOKING_FLOW_PAGE.REGISTER_PAGE,
    },
    {
      enabled: hasBasketAccess,
    },
    undefined,
    true,
    { basketIds: basketIdsJsonString }
  );

  const onSubmit = (data: RegisterPersonalDetails) => {
    analytics.update({
      marketingOptIn: data?.acceptFutureMailing,
    });

    if (isRecaptchaEnabled) {
      handleReCaptchaVerify();
    }
    registerAccount(data, captchaToken);
    setMarketingPreferencesForAnalytics(data.acceptFutureMailing);
  };

  const setMarketingPreferencesForAnalytics = (accepts: boolean) => {
    let marketingOptInChoice = accepts;
    if (currentLang === LanguageEnum.GERMAN) {
      marketingOptInChoice = !accepts;
    }

    analytics.update({ marketingOptInChoice });
  };

  const handleReCaptchaVerify = useCallback(async () => {
    if (!executeRecaptcha) {
      return;
    }

    const token = await executeRecaptcha('onSubmit');
    setCaptchaToken(token);
  }, [executeRecaptcha]);

  const registerAccount = useCallback(
    (data: RegisterPersonalDetails, captchaToken?: string) => {
      const updatePreferencesRequest: UpdatePreferencesRequest = {
        brandCodes: BRANDCODES,
        optIn: data.acceptFutureMailing,
        doubleOptIn: String(data.countryCode).toUpperCase() === 'DE',
        customer: {
          title: data.title,
          firstName: data.firstName,
          lastName: data.lastName,
          countryOfResidence: currentLang === 'en' ? 'GB' : 'DE',
          language: currentLang,
        },
        sourceDetails: {
          channel: MARKETING_CHANNEL,
          journey: MARKETING_JOURNEY,
          locale: currentLang === 'en' ? 'UK' : 'DE',
        },
      };

      registerMutation.mutate({
        country: currentCountry,
        language: currentLang,
        companyName: data.companyName,
        addressLine1: data.addressLine1,
        addressLine2: data.addressLine2,
        addressLine3: data.addressLine3,
        addressLine4: data.addressLine4,
        addressType:
          currentLang === GLOBALS.language.DE ? GLOBALS.addressType.HOME : data.addressSelection,
        cityName: data.cityName,
        countryCode: data.countryCode, // stop converting countryCode 'DE' to 'D' - DNRQ-77229
        postalCode: data.postalCode,
        title: data.title,
        firstName: data.firstName,
        lastName: data.lastName,
        emailAddress: data.email,
        mobile: data.phone,
        password: data.password,
        captcha: captchaToken,
        acceptFutureMailing: data.acceptFutureMailing,
        basketReference: getBasketReference(),
        updatePreferencesRequest,
      });
    },
    [registerMutation, currentCountry, currentLang]
  );

  const [defaultValues, setDefaultValues] = useState<FormProps['defaultValues']>({
    title: '',
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    companyName: '',
    addressLine1: '',
    addressLine2: '',
    addressLine3: '',
    addressLine4: '',
    postalCode: '',
    manualAddressToggle: currentLang === 'de' ? 'manualAddress' : '',
    cityName: '',
    postcodeAddress: '',
    addressSelection: '',
    password: '',
    countryCode: currentLang === 'en' ? 'GB' : 'DE',
    captcha: '',
    acceptFutureMailing: true,
  });

  useEffect(() => {
    if ((defaultValues.countryCode as string) === 'DE') {
      setIsLocationRequired(true);
    }
  }, [defaultValues.countryCode]);

  const getFormState: FormProps['getFormState'] = useCallback(
    (state: any) => {
      setDefaultValues(state as SetStateAction<FormProps['defaultValues']>);
    },
    [setDefaultValues]
  );

  const baseDataTestId = 'RegisterPIPage';

  return (
    <Box data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}>
      <SearchContainer
        queryClient={queryClient}
        searchLocation={searchLocation?.toString()}
        defaultRooms={defaultRooms}
        ARRdd={Number(ARRdd)}
        ARRmm={Number(ARRmm)}
        ARRyyyy={Number(ARRyyyy)}
        NIGHTS={Number(NIGHTS)}
        ROOMS={Number(ROOMS)}
        variant="pi"
        isSummaryActive={false}
      />
      <Container {...containerStyle}>{renderPageContent()}</Container>
    </Box>
  );

  function renderErrorNotification() {
    return (
      <Box {...notificationStyles} data-testid="registration-error-notification">
        <Notification
          description={t('bart.accountRegister.ErrorCode.7007.globalMessage')}
          status="error"
          variant="error"
          svg={<Error />}
          prefixDataTestId="registration-error-notification"
        />
      </Box>
    );
  }

  function renderPageContent() {
    if (registerIsLoading || registerIsSuccess) {
      return (
        <Flex {...loadingStyle}>
          <LoadingSpinner loadingText={t('booking.loading')} />
        </Flex>
      );
    }

    return (
      <>
        <Seo page={PageName.REGISTER} />
        <Flex flexDirection="column" alignItems="center">
          <> {(registerIsError || registerError) && renderErrorNotification()}</>
          <Form
            {...registerDetailsFormConfig({
              isCompanyNameAdvanceEnabled,
              isCountrySelectorFilterableEnabled,
              registerIsError,
              getFormState,
              defaultValues,
              onSubmit,
              baseDataTestId,
              resetForm,
              clearPhoneFields,
              currentLang,
              t,
              isLocationRequired,
              setIsLocationRequired,
              bkngData,
            })}
          />
        </Flex>
      </>
    );
  }

  function getBasketReference() {
    const reservationId = router.query.reservationId as string;
    if (isNonEmptyString(reservationId)) {
      const basketReferenceCookie = getCookie(REGISTER_BASKET_REF_COOKIE);
      const decodedBasketRef = decodeFromBase64(basketReferenceCookie);
      if (decodedBasketRef === reservationId) {
        return reservationId;
      }
    }
  }
}

const containerStyle = {
  maxW: '100vw',
  paddingInlineStart: {
    mobile: '0px',
  },
  paddingInlineEnd: {
    mobile: '0px',
  },
  overflow: 'auto',
} as StyleProps;

const loadingStyle = {
  height: '100%',
  width: '100%',
  left: 0,
  top: 0,
  position: 'fixed',
  zIndex: 3,
  backgroundColor: 'baseWhite',
  textAlign: 'center',
  lineHeight: 3,
  justifyContent: 'center',
  color: 'btnSecondaryEnabled',
  fontSize: 'xl',
  flex: 'none',
  flexGrow: 0,
  order: 1,
  alignSelf: 'stretch',
} as BoxProps;

const notificationStyles = {
  maxW: { base: 'full', md: '100%' },
  width: '80%',
  lineHeight: '2',
  mt: 'xl',
};
