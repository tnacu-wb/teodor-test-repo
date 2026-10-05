import type { BoxProps, FlexProps, GridItemProps, GridProps, StyleProps } from '@chakra-ui/react';
import { Box, Container, Flex, Grid, GridItem, Text } from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import {
  AnonymousNewsletterPreferences,
  Area,
  BILLING_ADDRESS_CAPTURE,
  BILLING_ADDRESS_CAPTURE_VARIANT,
  BIResponse,
  BRANDCODES,
  CREATE_RESERVATION_GUEST,
  FT_PI_ACCOMPANYING_GUEST_DETAILS,
  FT_PI_ADDITIONAL_INFORMATION,
  FT_PI_BB_CCUI_COMPANY_NAME_SPECIAL_CHARACTERS,
  FT_PI_GDP_BILLING_ADDRESS,
  FT_PI_GDP_PAGE_DESIGN_CHANGE,
  GDP_ACCOMPANYING_GUEST_DETAILS,
  GDP_DIGI_REG_ADDITIONAL_INFO,
  GET_ANCILLARIES_BOOKING_INFO,
  GET_ANONYMOUS_NEWSLETTER_PREFERENCES,
  GET_HOTEL_INFORMATION,
  HashType,
  HotelBrand,
  Language,
  MARKETING_CHANNEL,
  MARKETING_JOURNEY,
  PackagesCriteria,
  PageName,
  PrivacyPolicy,
  PurposeOfStay,
  QueryBookingInformationArgs,
  QueryHotelInformationArgs,
  REGISTER_USER_ACCOUNT,
  REGISTER_USER_MARKETING,
  RegisterPersonalDetails,
  UPDATE_REASON_FOR_STAY,
  FT_PI_CCUI_CONSOLIDATE_MOBILE_LANDLINE,
  FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN_ANCILLARIES_GDP_PAYMENT,
  FT_PI_DISPLAY_SOFT_BUNDLES,
  FT_PI_AUTH0_LOGIN,
  FT_PI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE,
} from '@whitbread-eos/api';
import { FormProps, LoadingSpinner } from '@whitbread-eos/atoms';
import {
  DataSecuritySection,
  INITIAL_GUEST_DETAILS_FORM_DATA,
  SEO,
} from '@whitbread-eos/molecules';
import {
  Auth0SignIn,
  BookingSummaryContainer,
  OptionalAuthentication,
} from '@whitbread-eos/organisms';
import {
  analytics,
  decodeIdToken,
  formatAccompanyingGuest,
  formatDataTestId,
  formatDate,
  getAuthCookie,
  getCityTaxMessages,
  getCookie,
  GLOBALS,
  graphQLRequest,
  hashString,
  isEmailValid,
  isStringValid,
  securityNoticeMoreInfoDataSelector,
  updateAncillariesAnalytics,
  useCookieForABTesting,
  useAuthToken,
  useCustomLocale,
  useFeatureToggle,
  useLocalStorage,
  useMutationRequest,
  usePackages,
  useQueryRequest,
  useSemanticTypography,
  useUpdateRateName,
  useUserData,
  useSoftBundles,
  BUNDLE_CHOICE,
  getDefaultDataFromBooking,
} from '@whitbread-eos/utils';
import { clearGuestFormData } from '@whitbread-eos/utils/server';
import debounce from 'lodash/debounce';
import { useTranslation } from 'next-i18next';
import dynamic from 'next/dynamic';
import { NextRouter } from 'next/router';
import React, { SetStateAction, useCallback, useEffect, useRef, useState } from 'react';

import {
  PAYMENT_NAVIGATION_SOURCE_GUEST_DETAILS,
  PAYMENT_NAVIGATION_SOURCE_QUERY_PARAM,
} from '../../utils/pi-all-pages-constants';
import { guestDetailsFormConfig } from './piFormConfig/guestDetailsFormConfig';

interface CheckInfoToggle {
  [key: string]: boolean;
}

interface MarketingPreferences {
  optIn: boolean;
  suppressMarketingCheckbox: boolean;
}

interface Props {
  queryClient: QueryClient;
  router: NextRouter;
  hiQueryInput: QueryHotelInformationArgs;
  pcksQueryInput: PackagesCriteria;
  biQueryInput: QueryBookingInformationArgs;
  cachedGuestDetailsFormData?: Record<string, unknown>;
  userEmail?: string | null;
}

type RegisterMarketingFormInfo = {
  email?: string;
  title?: string;
  firstName?: string;
  lastName?: string;
  countryCode?: string;
  acceptFutureMailing?: boolean;
};

export const buildRegisterMarketingPreferencesPayload = (
  formInfo: RegisterMarketingFormInfo | null,
  currentLang: string
) => {
  return {
    customerId: formInfo?.email,
    title: formInfo?.title,
    firstName: formInfo?.firstName,
    lastName: formInfo?.lastName,
    countryOfResidence: formInfo?.countryCode,
    locale: formInfo?.countryCode === 'GB' ? 'UK' : 'DE', // Bart marketing ms accepts only UK as country code.
    optIn: formInfo?.acceptFutureMailing,
    doubleOptIn: formInfo?.countryCode === 'DE',
    language: currentLang,
    channel: MARKETING_CHANNEL,
    journey: MARKETING_JOURNEY,
    brandCodes: BRANDCODES,
  };
};

export const getDefaultValuesFromFormState = ({
  isInitialized,
  isRemovePIIDataFromLocalStorageEnabled,
  bookingAcceptFutureMailing,
  state,
}: {
  isInitialized: boolean;
  isRemovePIIDataFromLocalStorageEnabled: boolean;
  bookingAcceptFutureMailing?: boolean;
  state: SetStateAction<FormProps['defaultValues']>;
}): SetStateAction<FormProps['defaultValues']> | undefined => {
  if (!isInitialized) return undefined;

  if (
    isRemovePIIDataFromLocalStorageEnabled &&
    typeof bookingAcceptFutureMailing === 'boolean' &&
    typeof state === 'object' &&
    state
  ) {
    return {
      ...(state as FormProps['defaultValues']),
      acceptFutureMailing: bookingAcceptFutureMailing,
    };
  }

  return state;
};

const Form = dynamic(
  async () => {
    const { Form } = await import('@whitbread-eos/atoms');
    return { default: Form };
  },
  {
    ssr: false,
  }
);
export default function GuestDetailsPagePi({
  queryClient,
  router,
  pcksQueryInput,
  hiQueryInput,
  biQueryInput,
  cachedGuestDetailsFormData,
  userEmail,
}: Props) {
  const { t } = useTranslation();
  const getTypographyProps = useSemanticTypography();
  const { language: currentLang, country: currentCountry } = useCustomLocale();
  const [updatingTotalCost, setUpdatingTotalCost] = useState(false);
  const [isLocationRequired, setIsLocationRequired] = useState(false);
  const { isLoggedIn } = useUserData();
  const idTokenCookie = getAuthCookie();
  const email = userEmail || decodeIdToken(idTokenCookie)?.email;
  const { token: authToken, isLoading: isAuthTokenLoading } = useAuthToken();
  const [billingAddressCapture, setBillingAddressCapture] = useState(false);
  const [isDifferentBillingAddress, setIsDifferentBillingAddress] = useState(false);
  const [bookerEmail, setBookerEmail] = useState(email);
  const [showCheckInInfo, setShowCheckInInfo] = useState<CheckInfoToggle>({});
  const [countryOfResidence, setCountryOfResidence] = useState(currentCountry.toLowerCase());
  const [isInitialized, setIsInitialized] = useState(false);
  const [marketingPreferences, setMarketingPreferences] = useState<MarketingPreferences>({
    optIn: true,
    suppressMarketingCheckbox: false,
  });

  const [isRegisterSelected, setIsRegisterSelected] = useState(false);
  const setRegisterSectionSelected = (selected: boolean) => {
    setIsRegisterSelected(false);
    if (selected) {
      setIsRegisterSelected(true);
    }
  };
  const submittedFormDataRef = useRef<any>(null);
  const {
    isLoading: pcksIsLoading,
    error: pcksError,
    isError: pcksIsError,
    packages,
    privacyPolicy,
    hotelHasCityTaxForBusiness,
    hotelHasCityTaxForLeisure,
  } = usePackages({
    adultsNumber: pcksQueryInput.adultsNumber as number,
    childrenNumber: pcksQueryInput.childrenNumber as number,
    hotelId: pcksQueryInput.hotelId,
    basketReferenceId: biQueryInput.basketReference,
    endDate: pcksQueryInput.endDate,
    startDate: pcksQueryInput.startDate,
    bookingFlowId: pcksQueryInput.bookingFlowId,
    nightsNumber: pcksQueryInput.nightsNumber as number,
    channel: pcksQueryInput.channel,
  });

  const {
    data: bkngData,
    isError: bkngIsError,
    isLoading: bkngIsLoading,
    error: bkngError,
    isSuccess: bkngSuccess,
  } = useQueryRequest(
    [
      'GetBookingInformation',
      biQueryInput.language,
      biQueryInput.country,
      biQueryInput.basketReference,
    ],
    GET_ANCILLARIES_BOOKING_INFO,
    biQueryInput
  );

  useUpdateRateName(bkngData?.bookingInformation, currentLang, currentCountry);
  useEffect(() => {
    if (bkngSuccess) {
      setUpdatingTotalCost(false);
    }
  }, [bkngData]);

  const {
    data: hiData,
    isError: hiIsError,
    isLoading: hiIsLoading,
    error: hiError,
  } = useQueryRequest(
    ['GetHotelInformation', hiQueryInput.hotelId, hiQueryInput.country, hiQueryInput.language],
    GET_HOTEL_INFORMATION,
    {
      ...hiQueryInput,
    }
  );

  const isGermanHotel = [HotelBrand.PID].includes(hiData?.hotelInformation?.brand);

  const {
    mutation: svBknMutation,
    isLoading: svBknIsLoading,
    isError: svBknIsError,
    data: svBknData,
    error: svBknError,
    isSuccess: svBknIsSuccess,
  } = useMutationRequest(
    CREATE_RESERVATION_GUEST,
    false,
    isAuthTokenLoading ? undefined : authToken
  );

  const {
    mutation: registerMutation,
    isError: registerIsError,
    data: registerData,
    isSuccess: registerIsSuccess,
  } = useMutationRequest(REGISTER_USER_ACCOUNT, true);

  const { mutation: marketingPreferencesMutation, data: marketingPreferencesData } =
    useMutationRequest(REGISTER_USER_MARKETING, true);

  const { mutation: urfsMutation } = useMutationRequest(UPDATE_REASON_FOR_STAY);

  useEffect(() => {
    if (bkngData && packages) {
      updateAncillariesAnalytics((bkngData as BIResponse).bookingInformation, packages);
    }
  }, [bkngData, packages]);

  useEffect(() => {
    if (
      !svBknIsError &&
      svBknIsSuccess &&
      svBknData?.createReservationGuest &&
      isStringValid(svBknData.createReservationGuest.basketReference)
    ) {
      const paymentSearchParams = new URLSearchParams({
        reservationId: svBknData.createReservationGuest.basketReference,
        [PAYMENT_NAVIGATION_SOURCE_QUERY_PARAM]: PAYMENT_NAVIGATION_SOURCE_GUEST_DETAILS,
      });

      router.push(
        `/${currentCountry}/${currentLang}${
          bkngData?.bookingInformation?.bookingFlowId
            ? `/${bkngData?.bookingInformation?.bookingFlowId}`
            : ''
        }/payment?${paymentSearchParams.toString()}`
      );
    }
  }, [svBknData, svBknIsError, svBknIsSuccess, router]);

  useEffect(() => {
    if (!registerIsError && registerIsSuccess && registerData) {
      registerMarketingPreferences();
    }
  }, [registerData, registerIsError, registerIsSuccess]);

  const getLeadGuestFormat = (data: any, item: any) => {
    const {
      title,
      firstName,
      lastName,
      countryCode,
      addressLine1,
      addressLine2,
      addressLine3,
      addressLine4,
      postcodeAddress,
    } = data;
    const {
      title: itemTitle,
      firstName: itemFirstName,
      lastName: itemLastName,
      countryCode: itemCountryCode,
      addressLine1: itemAddressLine1,
      addressLine2: itemAddressLine2,
      addressLine3: itemAddressLine3,
      addressLine4: itemAddressLine4,
      postcodeAddress: itemPostcodeAddress,
      stayInThisRoom,
    } = item;

    const addressCountryCode = stayInThisRoom ? countryCode : itemCountryCode;
    const isGermany = addressCountryCode === GLOBALS.localeUpper.DE;
    setCountryOfResidence(addressCountryCode?.toLowerCase() ?? '');

    let guest: any = {
      sameAsBooker: !!stayInThisRoom,
      stayingGuestDetails: {
        title: stayInThisRoom ? title : itemTitle,
        firstName: stayInThisRoom ? firstName : itemFirstName,
        lastName: stayInThisRoom ? lastName : itemLastName,
        address: {
          countryCode: stayInThisRoom ? countryCode : itemCountryCode,
          addressLine1: stayInThisRoom ? addressLine1 : itemAddressLine1,
          addressLine2: stayInThisRoom ? addressLine2 : itemAddressLine2,
          addressLine3: stayInThisRoom ? addressLine3 : itemAddressLine3,
          ...(!isGermany && { addressLine4: stayInThisRoom ? addressLine4 : itemAddressLine4 }),
          ...(isGermany && { cityName: stayInThisRoom ? addressLine4 : itemAddressLine4 }),
          postalCode: stayInThisRoom ? postcodeAddress : itemPostcodeAddress,
          ...(currentLang === GLOBALS.language.DE &&
            isGermanHotel && {
              addressType: GLOBALS.addressType.HOME,
            }),
        },
      },
    };

    if (isAdditionalInformationEnabled) {
      const { dateOfBirth, passport, nationality, consent } = item;
      const { email } = stayInThisRoom ? data : item;
      if (consent !== false) {
        // If consent is true or undefined, then addditional details will be added
        guest.stayingGuestDetails.emailAddress = email;

        if (dateOfBirth || passport || nationality)
          guest.stayingGuestDetails.additionalDetails = formatAdditionalInformation(
            dateOfBirth,
            passport,
            nationality
          );
      }
    }

    if (shouldUseAccompanyingGuest && item) {
      guest = formatAccompanyingGuest(guest, item);
    }

    return guest;
  };

  const [isBookingForSomeoneElse, setIsBookingForSomeoneElse] = useState<boolean>(false);

  const {
    [FT_PI_GDP_BILLING_ADDRESS]: isBillingAddressEnabled,
    [FT_PI_ADDITIONAL_INFORMATION]: isAdditionalInformationEnabledFlag,
    [FT_PI_GDP_PAGE_DESIGN_CHANGE]: isPageDesignChangeEnabled,
    [FT_PI_BB_CCUI_COMPANY_NAME_SPECIAL_CHARACTERS]: isCompanyNameAdvanceEnabled,
    [FT_PI_ACCOMPANYING_GUEST_DETAILS]: isAccompanyingGuestDetailsEnabledFlag,
    [FT_PI_CCUI_CONSOLIDATE_MOBILE_LANDLINE]: isConsolidateMobileLandlineEnabled,
    [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN_ANCILLARIES_GDP_PAYMENT]: isCityTaxBreakdownEnabled,
    [FT_PI_DISPLAY_SOFT_BUNDLES]: isSoftBundlesEnabled,
    [FT_PI_AUTH0_LOGIN]: isAuth0Enabled,
    [FT_PI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE]: isRemovePIIDataFromLocalStorageEnabled,
  } = useFeatureToggle();

  const { isSoftBundlesVisible } = useSoftBundles(
    isSoftBundlesEnabled,
    getCookie(BUNDLE_CHOICE),
    bkngData?.bookingInformation?.reservationByIdList?.length,
    bkngData?.bookingInformation?.reservationByIdList[0]?.children
  );

  // A/B testing for accompanying guests
  const hasAccompanyingGuestCookie = useCookieForABTesting(
    GDP_ACCOMPANYING_GUEST_DETAILS.cookieName,
    GDP_ACCOMPANYING_GUEST_DETAILS.mode
  );

  // A/B testing for additional info (Digital Reg Feature)
  const hasAdditionalInfoCookie = useCookieForABTesting(
    GDP_DIGI_REG_ADDITIONAL_INFO.cookieName,
    GDP_DIGI_REG_ADDITIONAL_INFO.mode
  );

  const isAdditionalInformationEnabled =
    hasAdditionalInfoCookie && isGermanHotel && isAdditionalInformationEnabledFlag;
  const shouldUseAccompanyingGuest =
    hasAccompanyingGuestCookie && !isGermanHotel && isAccompanyingGuestDetailsEnabledFlag;

  const isBillingAddressDisplayed =
    isBillingAddressEnabled && currentLang === GLOBALS.language.DE && isGermanHotel;
  const shouldCheckMarketingPreferences = true;

  useEffect(() => {
    const selectedDesign = getCookie(BILLING_ADDRESS_CAPTURE);
    setBillingAddressCapture(selectedDesign && BILLING_ADDRESS_CAPTURE_VARIANT === selectedDesign);
  }, []);

  const debouncedGetAnonymousNewsletterPreferences = useRef(
    debounce(getAnonymousNewsletterPreferences, 300)
  ).current;

  useEffect(() => {
    if (!shouldCheckMarketingPreferences || !debouncedGetAnonymousNewsletterPreferences) {
      return;
    }

    if (!bookerEmail || !isEmailValid(bookerEmail)) {
      setMarketingPreferences((prev) => ({ ...prev, suppressMarketingCheckbox: false }));
      analytics.update({ optInCustomer: undefined, marketingOptInChoice: true });
      return;
    }

    debouncedGetAnonymousNewsletterPreferences(
      bookerEmail,
      queryClient,
      setMarketingPreferences,
      true,
      countryOfResidence,
      currentLang as Language
    );
  }, [
    bookerEmail,
    isLoggedIn,
    true,
    currentLang,
    shouldCheckMarketingPreferences,
    debouncedGetAnonymousNewsletterPreferences,
    countryOfResidence,
  ]);

  const isSomeoneElseAndSingleRoomRedesign = isPageDesignChangeEnabled && isBookingForSomeoneElse;

  const mapAddressData = (data: any) => {
    const leadGuest =
      data.bookingForSomeoneElse || isSomeoneElseAndSingleRoomRedesign ? data.leadGuest[0] : data;
    const isGermany = leadGuest.countryCode === GLOBALS.localeUpper.DE;
    return {
      countryCode: leadGuest?.countryCode,
      addressLine1: leadGuest?.addressLine1,
      addressLine2: leadGuest?.addressLine2,
      addressLine3: leadGuest?.addressLine3,
      ...(!isGermany && { addressLine4: leadGuest?.addressLine4 }),
      ...(isGermany && { cityName: leadGuest?.addressLine4 }),
      postalCode: leadGuest?.postcodeAddress,
    };
  };

  const formatStayingGuestBE = (data: any) => {
    const stayingGuests: any = [];
    if (data.leadGuest && data.leadGuest.length > 1) {
      data.leadGuest.map((item: any) => {
        const guest = getLeadGuestFormat(data, item);
        stayingGuests.push(guest);
      });
    } else {
      let guest: any = {
        sameAsBooker:
          data.bookingForSomeoneElse || isSomeoneElseAndSingleRoomRedesign ? false : true,
        stayingGuestDetails: {
          title:
            data.bookingForSomeoneElse || isSomeoneElseAndSingleRoomRedesign
              ? data.leadGuest[0].title
              : data.title,
          firstName:
            data.bookingForSomeoneElse || isSomeoneElseAndSingleRoomRedesign
              ? data.leadGuest[0].firstName
              : data.firstName,
          lastName:
            data.bookingForSomeoneElse || isSomeoneElseAndSingleRoomRedesign
              ? data.leadGuest[0].lastName
              : data.lastName,
          address: mapAddressData(data),
        },
      };

      if (isAdditionalInformationEnabled) {
        const bookingForSomeoneElse =
          data.bookingForSomeoneElse || isSomeoneElseAndSingleRoomRedesign;

        const { dateOfBirth, passport, nationality, email, consent } = bookingForSomeoneElse
          ? data.leadGuest[0]
          : data;

        if (consent !== false) {
          // If consent is true or undefined, then addditional details will be added
          guest.stayingGuestDetails.emailAddress = email;

          if (dateOfBirth || passport || nationality)
            guest.stayingGuestDetails.additionalDetails = formatAdditionalInformation(
              dateOfBirth,
              passport,
              nationality
            );
        }
      }

      if (shouldUseAccompanyingGuest && data?.leadGuest?.[0]) {
        guest = formatAccompanyingGuest(guest, data.leadGuest[0]);
      }

      stayingGuests.push(guest);
    }

    return stayingGuests;
  };

  const formatAdditionalInformation = (
    dateOfBirth: Date,
    passport: string,
    nationality: { value: string }
  ) => {
    const additionalDetails: any = {};
    if (dateOfBirth) additionalDetails.dob = formatDate(dateOfBirth.toString(), 'yyyy-MM-dd');
    if (passport) additionalDetails.passportNumber = passport;
    if (nationality?.value) additionalDetails.nationality = nationality.value;
    return additionalDetails;
  };
  const continueBooking = useCallback(
    (data: any) => {
      const stayingGuests = formatStayingGuestBE(data);
      const isGermany = data.countryCode === GLOBALS.localeUpper.DE;
      let companyName = '';
      if (data?.addressSelection === 'BUSINESS') {
        companyName =
          isGermanHotel && data.billingAddressCheckbox
            ? formData.billing.address.companyName
            : data.companyName;
      }
      const bookingInfo = {
        hotelId: bkngData?.bookingInformation?.hotelId,
        reasonForStay: data.reasonForStay,
        companyName,
        addressLine1: data.addressLine1,
        addressLine2: data.addressLine2,
        addressLine3: data.addressLine3,
        ...(!isGermany && { addressLine4: data.addressLine4 }),
        ...(isGermany && { cityName: data.cityName }),
        // make HOME default address for De hotel and multiroom design feature switch
        addressType: isGermanHotel && isPageDesignChangeEnabled ? 'HOME' : data.addressSelection,
        countryCode: data.countryCode,
        postalCode: data.postalCode,
        title: data.title,
        firstName: data.firstName,
        lastName: data.lastName,
        emailAddress: data.email,
        mobile: data.phone ?? '',
        landline: data.landline ?? '',
        acceptFutureMailing: marketingPreferences.suppressMarketingCheckbox
          ? marketingPreferences.optIn
          : data.acceptFutureMailing,
        language: currentLang,
        basketReference: biQueryInput.basketReference,
        stayingGuests: stayingGuests,
        ...(isAuth0Enabled && { updateProfileConsent: Boolean(data.updateProfileConsent) }),
      };

      svBknMutation.mutate(bookingInfo);
    },
    [biQueryInput.basketReference, marketingPreferences, currentLang, bkngData, svBknMutation]
  );

  const registerAccount = useCallback(
    (data: RegisterPersonalDetails) => {
      registerMutation.mutate({
        companyName: data.addressSelection === 'BUSINESS' ? data.companyName : '',
        addressLine1: data.addressLine1,
        addressLine2: data.addressLine2,
        addressLine3: data.addressLine3,
        addressLine4: data.addressLine4,
        addressType: data.addressSelection,
        cityName: data.cityName,
        countryCode: data.countryCode, // stop converting countryCode 'DE' to 'D' - DNRQ-77229, no longer needed after BART migration
        postalCode: data.postalCode,
        title: data.title,
        firstName: data.firstName,
        lastName: data.lastName,
        emailAddress: data.email,
        mobile: data.phone,
        password: data.password,
        acceptFutureMailing: data.acceptFutureMailing,
      });
    },
    [registerData, registerMutation]
  );

  const registerMarketingPreferences = useCallback(() => {
    const formInfo = isRemovePIIDataFromLocalStorageEnabled
      ? submittedFormDataRef.current
      : formData;

    marketingPreferencesMutation.mutate(
      buildRegisterMarketingPreferencesPayload(formInfo, currentLang)
    );
  }, [
    marketingPreferencesData,
    marketingPreferencesMutation,
    isRemovePIIDataFromLocalStorageEnabled,
  ]);

  const setReasonForStay = useCallback(
    (reasonForStay: string) => {
      if (!hotelHasCityTaxForBusiness && !hotelHasCityTaxForLeisure) {
        return;
      }
      setUpdatingTotalCost(true);
      urfsMutation.mutate(
        {
          hotelId: bkngData?.bookingInformation?.hotelId,
          reasonForStay: reasonForStay,
          basketReference: biQueryInput.basketReference,
          country: currentCountry,
          language: currentLang,
          arrivalDate: bkngData?.bookingInformation?.reservationByIdList[0]?.roomStay?.arrivalDate,
        },
        {
          onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['GetBookingInformation'] });
            setUpdatingTotalCost(false);
          },
          onError: () => {
            console.log('Error setting reason for stay');
          },
        }
      );
    },
    [biQueryInput.basketReference, bkngData, urfsMutation]
  );

  const onSubmit = async (data: any) => {
    if (isRemovePIIDataFromLocalStorageEnabled) {
      submittedFormDataRef.current = data;
      if (cachedGuestDetailsFormData) {
        await clearGuestFormData(biQueryInput.basketReference);
      }
    } else {
      saveFormDataInLocalStorage(data);
    }
    analytics.update({
      userHashedEA: await hashString(data.email, HashType.SHA256),
    });
    isRegisterSelected && registerAccount(data);
    continueBooking(data);
  };

  const onChange = (values: {
    billingAddressCheckbox: boolean;
    email: string;
    countryCode: string;
  }) => {
    setAnalyticsData(values);
    setCountryOfResidence(values.countryCode.toLowerCase());
    if (values?.billingAddressCheckbox !== isDifferentBillingAddress) {
      setIsDifferentBillingAddress(values?.billingAddressCheckbox);
    }
    if (values?.email !== bookerEmail) {
      setBookerEmail(values.email);
    }
  };

  const goBack = useCallback(() => {
    const bookingFlowId = bkngData?.bookingInformation?.bookingFlowId;
    router.push(
      `/${currentCountry}/${currentLang}${
        bookingFlowId ? `/${bookingFlowId}` : ''
      }/ancillaries?reservationId=${biQueryInput.basketReference}`
    );
  }, [router, currentCountry, currentLang, bkngData, biQueryInput.basketReference]);

  const setAnalyticsData = (data: any) => {
    if (isGermanHotel && data?.leadGuest?.length >= 2) {
      const hasClickedCheckinButton = Object.keys(showCheckInInfo).some(
        (key) => showCheckInInfo[key]
      );

      const propertyHasValue = (property: string, checkLeadGuestsOnly = false) => {
        if (data[property] && !checkLeadGuestsOnly) {
          return true;
        }

        return Boolean(data?.leadGuest?.some((guest: any) => guest[property]));
      };

      const guestAnalytics = {
        guestDetails: {
          addCheckinInfo: hasClickedCheckinButton,
          guestConsent: propertyHasValue('consent', true),
          addressLine1: propertyHasValue('addressLine1', true),
          addressLine2: propertyHasValue('addressLine2', true),
          addressLine3: propertyHasValue('addressLine3', true),
          postalCode: propertyHasValue('postcodeAddress', true),
          location: propertyHasValue('cityName', true) || propertyHasValue('addressLine4', true),
          countrySelection: propertyHasValue('countryCode', true),
          email: propertyHasValue('email', true),
          birthDate: propertyHasValue('dateOfBirth'),
          nationality: propertyHasValue('nationality'),
          passportNum: propertyHasValue('passport'),
        },
      };

      analytics.update(guestAnalytics);
    }
  };

  const setCurrentLanguage = (currentLang: string) => {
    return currentLang === 'en' ? 'GB' : 'DE';
  };

  const [currentReasonForStay, setCurrentReasonForStay] = useState(
    HotelBrand.HUB === hiData?.hotelInformation?.brand ? PurposeOfStay.LEISURE : ''
  );

  const cityTaxMessages = getCityTaxMessages(
    hotelHasCityTaxForLeisure, //!'hasCityTaxForLeisure',
    hotelHasCityTaxForBusiness, // !!'hasCityTaxForBusiness',
    currentReasonForStay,
    t,
    bkngData?.bookingInformation?.currencyCode,
    currentLang,
    bkngData?.bookingInformation?.totalCost
  );

  const updateReasonForStay = (reasonForStay: string) => {
    setCurrentReasonForStay(reasonForStay);
    setReasonForStay(reasonForStay);
  };

  const baseDataTestId = 'GuestDetails';
  const [formData, setFormDetails] = useLocalStorage(
    'formDetails',
    INITIAL_GUEST_DETAILS_FORM_DATA
  );

  INITIAL_GUEST_DETAILS_FORM_DATA.addressSelection = isGermanHotel ? 'HOME' : '';

  useEffect(() => {
    if (isPageDesignChangeEnabled && isRemovePIIDataFromLocalStorageEnabled === false) {
      setFormDetails(INITIAL_GUEST_DETAILS_FORM_DATA);
    }
  }, [
    isPageDesignChangeEnabled,
    isRemovePIIDataFromLocalStorageEnabled,
    INITIAL_GUEST_DETAILS_FORM_DATA,
  ]);

  const defaultDataFromBooking = getDefaultDataFromBooking(
    bkngData?.bookingInformation?.reservationByIdList ?? [],
    {},
    biQueryInput.basketReference,
    currentLang,
    hiData?.hotelInformation?.brand,
    isGermanHotel,
    true
  );
  const bookingAcceptFutureMailing =
    bkngData?.bookingInformation?.reservationByIdList?.[0]?.additionalGuestInfo
      ?.acceptFutureMailing;
  const [defaultValues, setDefaultValues] = useState<FormProps['defaultValues']>(
    isRemovePIIDataFromLocalStorageEnabled
      ? {
          ...defaultDataFromBooking,
          acceptFutureMailing: bookingAcceptFutureMailing ?? true,
          updateProfileConsent:
            (defaultDataFromBooking as { updateProfileConsent?: boolean })?.updateProfileConsent ??
            false,
          manualAddressToggle:
            currentLang === 'de' ||
            (defaultDataFromBooking?.addressSelection && defaultDataFromBooking?.addressLine1)
              ? 'manualAddress'
              : '',
          addressSelection:
            isGermanHotel && isPageDesignChangeEnabled
              ? 'HOME'
              : defaultDataFromBooking?.addressSelection,
          whoBookerIsTabs:
            defaultDataFromBooking?.bookingForSomeoneElse && currentLang === 'de'
              ? 'SOMEONEELSE'
              : 'MYSELF',
          ...(cachedGuestDetailsFormData ?? {}),
        }
      : {
          reasonForStay:
            HotelBrand.HUB === hiData?.hotelInformation?.brand ? PurposeOfStay.LEISURE : '',
          title: '',
          firstName: '',
          lastName: '',
          email: '',
          phone: '',
          landline: '',
          companyName: '',
          addressLine1: '',
          addressLine2: '',
          addressLine3: '',
          addressLine4: '',
          postalCode: '',
          // sets manualAddress link to toggle home/business radios (or home address fields - de hotel, multiRoomRedesignEnabled) to open or not
          manualAddressToggle:
            currentLang === 'de' && !isPageDesignChangeEnabled ? 'manualAddress' : '',
          cityName: '',
          postcodeAddress: '',
          // For De hotel - set Home address as default
          addressSelection: isGermanHotel && isPageDesignChangeEnabled ? 'HOME' : '',
          countryCode: setCurrentLanguage(currentLang),
          acceptFutureMailing: true,
          updateProfileConsent: false,
          whoBookerIsTabs: 'MYSELF',
          billingAddressCheckbox: false, // initially should be undefined
          billing_countryCode: setCurrentLanguage(currentLang),
          billing_companyName: '',
          billing_addressLine1: '',
          billing_addressLine2: '',
          billing_addressLine3: '',
          billing_addressLine4: '',
          billing_postalCode: '',
          billing_addressSelection: '',
        }
  );

  useEffect(() => {
    if (typeof isRemovePIIDataFromLocalStorageEnabled !== 'boolean') {
      return;
    }

    if (isRemovePIIDataFromLocalStorageEnabled) {
      const defaultDataFromBooking = getDefaultDataFromBooking(
        bkngData?.bookingInformation?.reservationByIdList ?? [],
        {},
        biQueryInput.basketReference,
        currentLang,
        hiData?.hotelInformation?.brand,
        isGermanHotel,
        true
      );
      const updatedValues = {
        ...defaultDataFromBooking,
        acceptFutureMailing: bookingAcceptFutureMailing ?? true,
        updateProfileConsent:
          (defaultDataFromBooking as { updateProfileConsent?: boolean })?.updateProfileConsent ??
          false,
        manualAddressToggle:
          currentLang === 'de' ||
          (defaultDataFromBooking?.addressSelection && defaultDataFromBooking?.addressLine1)
            ? 'manualAddress'
            : '',
        addressSelection:
          isGermanHotel && isPageDesignChangeEnabled
            ? 'HOME'
            : defaultDataFromBooking?.addressSelection,
        whoBookerIsTabs:
          defaultDataFromBooking?.bookingForSomeoneElse && currentLang === 'de'
            ? 'SOMEONEELSE'
            : 'MYSELF',
        ...(cachedGuestDetailsFormData ?? {}),
      };
      setDefaultValues(updatedValues);
      const countryCode = updatedValues.countryCode?.toLowerCase() ?? '';
      countryCode && setCountryOfResidence(countryCode);
    }

    setIsInitialized(true);
  }, [
    isRemovePIIDataFromLocalStorageEnabled,
    isPageDesignChangeEnabled,
    biQueryInput.basketReference,
    bkngData?.bookingInformation?.reservationByIdList,
    currentLang,
    hiData?.hotelInformation?.brand,
    isGermanHotel,
    bookingAcceptFutureMailing,
    cachedGuestDetailsFormData,
  ]);

  useEffect(() => {
    if (isRemovePIIDataFromLocalStorageEnabled && currentLang === 'de') {
      defaultValues?.whoBookerIsTabs === 'MYSELF'
        ? setIsBookingForSomeoneElse(false)
        : setIsBookingForSomeoneElse(true);
    }
  }, [defaultValues?.whoBookerIsTabs, currentLang, isRemovePIIDataFromLocalStorageEnabled]);

  const getFormState: FormProps['getFormState'] = useCallback(
    (state: any) => {
      const resolvedState = getDefaultValuesFromFormState({
        isInitialized,
        isRemovePIIDataFromLocalStorageEnabled,
        bookingAcceptFutureMailing,
        state: state as SetStateAction<FormProps['defaultValues']>,
      });

      if (typeof resolvedState === 'undefined') {
        return;
      }

      setDefaultValues(resolvedState);
    },
    [
      isInitialized,
      setDefaultValues,
      isRemovePIIDataFromLocalStorageEnabled,
      bookingAcceptFutureMailing,
    ]
  );

  useEffect(() => {
    if (formData?.basketReferenceId !== biQueryInput?.basketReference) {
      if ((defaultValues?.countryCode as string) === 'DE') {
        setIsLocationRequired(true);
      }
    } else if ((formData?.countryCode as string) === 'DE') {
      setIsLocationRequired(true);
    }
  }, [defaultValues.countryCode]);

  const privacyPolicyData: PrivacyPolicy = securityNoticeMoreInfoDataSelector(privacyPolicy ?? {});

  if (typeof window !== 'undefined') {
    const HDPPath = localStorage.getItem('HDPPath');
    const hasVisitedIframe = localStorage.getItem('3cpVisited');
    if (HDPPath && hasVisitedIframe) {
      router.push(HDPPath);
      return <></>;
    }
  }

  return (
    <Container {...containerStyle}>
      <SEO
        page={PageName.GUEST_DETAILS}
        hotelId={bkngData?.bookingInformation?.hotelId}
        bookingFlowId={bkngData?.bookingInformation?.bookingFlowId}
        noIndexNoFollow={true}
      />

      <Grid
        {...mainGuestDetailsGridStyle}
        data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}
      >
        <GridItem {...bookingSummaryMobileContainerStyle}>
          <Flex {...bookingSummaryMobileTriggerStyle}>
            <BookingSummaryContainer
              queryClient={queryClient}
              packages={packages}
              bkngData={bkngData}
              hiData={hiData}
              biQueryInput={biQueryInput}
              basketReferenceId={biQueryInput.basketReference}
              variant="mobile"
              t={t}
              language={currentLang}
              taxesMessage={cityTaxMessages.summaryText}
              area={Area.PI}
              isExtrasDisplayed={!!packages?.extrasItems}
              submitButtonDisabled={updatingTotalCost}
              isCityTaxBreakdownEnabled={isCityTaxBreakdownEnabled}
              isSoftBundlesVisible={isSoftBundlesVisible}
            />
          </Flex>
        </GridItem>
        <GridItem {...mainContentStyle}>
          {isAuth0Enabled ? (
            <Auth0SignIn />
          ) : (
            <OptionalAuthentication
              queryClient={queryClient}
              showIcon={false}
              isRegisterSelected={isRegisterSelected}
              setRegisterSectionSelected={setRegisterSectionSelected}
            />
          )}
          <GridItem
            {...pageContentStyle}
            data-testid={formatDataTestId(baseDataTestId, 'PageContent')}
          >
            {renderPageContent()}
          </GridItem>
        </GridItem>
        <GridItem {...bookingSummaryDesktopStyle}>
          <BookingSummaryContainer
            queryClient={queryClient}
            packages={packages}
            bkngData={bkngData}
            hiData={hiData}
            biQueryInput={biQueryInput}
            basketReferenceId={biQueryInput.basketReference}
            variant="desktop"
            t={t}
            language={currentLang}
            taxesMessage={cityTaxMessages.summaryText}
            area={Area.PI}
            isExtrasDisplayed={!!packages?.extrasItems}
            submitButtonDisabled={updatingTotalCost}
            isCityTaxBreakdownEnabled={isCityTaxBreakdownEnabled}
            isSoftBundlesVisible={isSoftBundlesVisible}
          />
        </GridItem>
        <GridItem {...dataSecurityContainer}>
          <DataSecuritySection
            privacyPolicy={privacyPolicyData}
            prefixDataTestId={baseDataTestId}
          />
        </GridItem>
      </Grid>
    </Container>
  );

  function getFirstName() {
    if (isLoggedIn) {
      const userData = getCookie('userData');
      try {
        const { firstName } = userData ? JSON.parse(userData) : undefined;
        return firstName;
      } catch (e) {
        console.error(e);
      }
    }
  }

  function saveFormDataInLocalStorage(formData: any) {
    const multiroom = bkngData?.bookingInformation?.reservationByIdList?.length > 1;
    const bookerIsNotGuest = multiroom
      ? !formData.leadGuest?.some((guest: { stayInThisRoom: boolean }) => !!guest.stayInThisRoom)
      : formData.bookingForSomeoneElse || isSomeoneElseAndSingleRoomRedesign;
    setCountryOfResidence(formData?.countryCode.toLowerCase() ?? '');
    setFormDetails((formDetails: any) => {
      if (formData) {
        formDetails.addressLine1 = formData.addressLine1;
        formDetails.addressLine2 = formData.addressLine2;
        formDetails.addressLine3 = formData.addressLine3;
        formDetails.addressLine4 = formData.addressLine4;
        formDetails.postcodeAddress = formData.postcodeAddress;
        formDetails.addressSelection = formData.addressSelection;
        formDetails.cityName = formData.cityName;
        formDetails.companyName = formData.companyName;
        formDetails.countryCode = formData.countryCode;
        formDetails.email = formData.email;
        formDetails.firstName = formData.firstName;
        formDetails.landline = formData.landline;
        formDetails.lastName = formData.lastName;
        formDetails.manualAddressToggle = formData.manualAddressToggle;
        formDetails.phone = formData.phone;
        formDetails.postalCode = formData.postalCode;
        formDetails.reasonForStay = formData.reasonForStay;
        formDetails.title = formData.title;
        formDetails.updateProfileConsent = formData.updateProfileConsent;
        formDetails.bookingForSomeoneElse = bookerIsNotGuest;
        formDetails.leadGuest = formData.leadGuest;
        formDetails.basketReferenceId = biQueryInput.basketReference;
        formDetails.updated = true;
        formDetails.isBillingAddressDisplayed = isBillingAddressDisplayed;
        formDetails.billing = {
          address: {
            countryCode: formData.billing_countryCode,
            companyName: formData.billing_companyName,
            addressLine1: formData.billing_addressLine1,
            addressLine2: formData.billing_addressLine2,
            addressLine3: formData.billing_addressLine3,
            addressLine4: formData.billing_addressLine4,
            cityName: formData.billing_cityName,
            postalCode: formData.billing_postalCode,
            addressType: formData.billingAddressCheckbox
              ? formData.billing_addressSelection
              : formData.addressSelection,
          },
          title: formData.title,
          firstName: formData.firstName,
          lastName: formData.lastName,
          email: formData.email,
          telephone: formData.phone,
          differentBillingAddress: formData.billingAddressCheckbox,
        };
        if (isAdditionalInformationEnabled) {
          const { dateOfBirth, nationality, passport } = formData;
          if (dateOfBirth)
            formDetails.dateOfBirth = formatDate(dateOfBirth.toString(), 'yyyy-MM-dd');
          else formDetails.dateOfBirth = dateOfBirth;
          formDetails.nationality = nationality;
          formDetails.passport = passport;
        }
        return formDetails;
      }
    });
  }

  function renderPageContent() {
    if (pcksIsLoading || bkngIsLoading || hiIsLoading || svBknIsLoading || svBknIsSuccess) {
      return (
        <Flex {...loadingStyle}>
          <LoadingSpinner loadingText={t('booking.loading')} />
        </Flex>
      );
    }

    if (pcksIsError) {
      return (
        <Box>
          <Box>Error on getting packages....</Box>
          <Box bg="red" color="white">
            {(pcksError as Error).message}
          </Box>
        </Box>
      );
    }

    if (bkngIsError) {
      return (
        <Box>
          <Box>Error on getting booking information....</Box>
          <Box bg="blue" color="white">
            {(bkngError as Error).message}
          </Box>
        </Box>
      );
    }

    if (hiIsError) {
      return (
        <Box>
          <Box>Error on getting hotel information....</Box>
          <Box bg="pink" color="black">
            {(hiError as Error).message}
          </Box>
        </Box>
      );
    }

    if (svBknIsError) {
      return (
        <Box>
          <Box>Error on getting save booking information....</Box>
          <Box bg="orange" color="black">
            {(svBknError as Error).message}
          </Box>
        </Box>
      );
    }

    return (
      <Flex flexDirection="column">
        <Box>
          {isLoggedIn && (
            <Text fontSize="2xl" fontWeight="semibold">
              {t('booking.login.greeting')} {getFirstName()}
            </Text>
          )}
          <Form
            key={
              isRemovePIIDataFromLocalStorageEnabled
                ? `guest-details-form-${isInitialized ? 'ready' : 'waiting'}`
                : undefined
            }
            onChange={onChange}
            {...guestDetailsFormConfig({
              getTypographyProps,
              getFormState,
              defaultValues,
              onSubmit,
              baseDataTestId,
              currentLang,
              basketReferenceId: biQueryInput.basketReference,
              t,
              brand: hiData?.hotelInformation?.brand,
              bkndData: {
                hiData: hiData,
                rooms: bkngData?.bookingInformation?.reservationByIdList,
              },
              goBack,
              cityTaxMessages,
              updateReasonForStay,
              isLocationRequired,
              setIsLocationRequired,
              hotelBrand: hiData?.hotelInformation?.brand,
              isRegisterSelected: isRegisterSelected,
              isSingleRoomRedesignEnabled:
                isPageDesignChangeEnabled &&
                (billingAddressCapture || currentLang === GLOBALS.language.DE),
              isMultiRoomRedesignEnabled:
                isPageDesignChangeEnabled &&
                (billingAddressCapture || currentLang === GLOBALS.language.DE),
              isBookingForSomeoneElse,
              setIsBookingForSomeoneElse,
              isGermanHotel,
              isBillingAddressEnabled: isBillingAddressDisplayed,
              isAdditionalInformationEnabled,
              showCheckInInfo,
              setShowCheckInInfo,
              isCompanyNameAdvanceEnabled,
              shouldAskForAccompanyingGuest: shouldUseAccompanyingGuest,
              isDifferentBillingAddress,
              suppressMarketingCheckbox: marketingPreferences.suppressMarketingCheckbox,
              isDEOptInEnabled: countryOfResidence === GLOBALS.language.DE,
              submitButtonDisabled: updatingTotalCost || isAuthTokenLoading,
              isConsolidateMobileLandlineEnabled,
              isRemovePIIDataFromLocalStorageEnabled,
              isAuth0Enabled,
              isUserSignedIn: isLoggedIn || !!userEmail,
              privacyPolicyLinkPath: privacyPolicyData?.linkSrc,
              isCountryAllowTypingEnabled: true,
              setCountryOfResidence,
            })}
          />
        </Box>
      </Flex>
    );
  }
}

export async function getAnonymousNewsletterPreferences(
  email: string,
  queryClient: any,
  setMarketingPreferences: any,
  isDEOptInEnabled?: boolean,
  countryOfResidence?: string,
  language?: Language
) {
  let isSubscribed = false;
  let optInCustomer: boolean | undefined = undefined;
  try {
    const queryKey = isDEOptInEnabled
      ? ['getAnonymousNewsletterPreferences', BRANDCODES[0], email, countryOfResidence, language]
      : ['getAnonymousNewsletterPreferences', BRANDCODES[0], email];

    const data: { anonymousNewsletterPreferences: AnonymousNewsletterPreferences } =
      await queryClient.fetchQuery({
        queryKey: queryKey,
        queryFn: () =>
          graphQLRequest(
            GET_ANONYMOUS_NEWSLETTER_PREFERENCES,
            isDEOptInEnabled
              ? { brandCode: BRANDCODES[0], email: email, countryOfResidence, language }
              : {
                  brandCode: BRANDCODES[0],
                  email: email,
                }
          ),
        gcTime: 0,
        staleTime: 0,
      });

    optInCustomer = data?.anonymousNewsletterPreferences?.optIn ?? undefined;
    isSubscribed = isDEOptInEnabled
      ? data?.anonymousNewsletterPreferences?.suppressMarketingCheckbox
      : !!optInCustomer;
  } catch {
    isSubscribed = false;
  } finally {
    setMarketingPreferences(
      (prevState: MarketingPreferences) =>
        ({
          ...prevState,
          optIn: optInCustomer,
          suppressMarketingCheckbox: isSubscribed,
        }) as MarketingPreferences
    );
    analytics.update({ optInCustomer: optInCustomer });
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
const mainGuestDetailsGridStyle = {
  w: 'full',
  maxW: 'var(--chakra-space-breakpoint-xl)',
  px: {
    mobile: '0',
    lg: 'lg',
    xl: '5xl',
  },
  pb: {
    mobile: 'md',
    md: 'lg',
    lg: 'xl',
    xl: '5xl',
  },
  pt: {
    mobile: '0',
    lg: '5xl',
  },
  mx: 'auto',
  my: '0',
  templateColumns: {
    mobile: '1fr',
    lg: '1fr auto',
  },
  columnGap: {
    mobile: '0',
    lg: '32',
    xl: '8.5rem',
  },
} as GridProps;

const pageContentStyle = {
  px: {
    mobile: '0',
    sm: 'md',
    md: '0',
    lg: '0',
  },
  pt: {
    mobile: 'lg',
    md: 'lg',
    lg: 0,
  },
} as GridItemProps;

const bookingSummaryMobileContainerStyle = {
  //TODO: just as a mock until booking summary will be done
  display: {
    mobile: 'block',
    lg: 'none',
  },
  backgroundColor: 'lightGrey5',
} as GridItemProps;

const bookingSummaryMobileTriggerStyle = {
  //TODO: just as a mock until booking summary will be done
  justifyContent: 'center',
  fontWeight: 'bold',
  flexDirection: 'column',
} as FlexProps;

const bookingSummaryDesktopStyle = {
  display: {
    mobile: 'none',
    lg: 'block',
  },
  w: {
    lg: '288px', // TODO: just as a mock until booking summary will be done
    xl: '309px', // TODO: just as a mock until booking summary will be done
  },
} as GridItemProps;

const mainContentStyle = {
  px: {
    mobile: 'md',
    sm: '0px',
    md: 'lg',
  },
  maxW: '100vw',
};

const dataSecurityContainer = {
  mb: {
    mobile: 'md',
    lg: 'lg',
  },
  maxWidth: {
    md: '49rem',
    xl: '55rem',
  },
  px: {
    mobile: 'md',
    sm: 'md',
    md: 'lg',
    lg: '0',
  },
};
const loadingStyle = {
  height: '100%',
  width: '100%',
  left: 0,
  top: 0,
  position: 'fixed',
  zIndex: 1,
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
