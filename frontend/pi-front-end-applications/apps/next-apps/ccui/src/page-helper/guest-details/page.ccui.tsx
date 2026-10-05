import type { BoxProps, FlexProps, GridItemProps, GridProps, StyleProps } from '@chakra-ui/react';
import { Box, Container, Flex, Grid, GridItem } from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import type {
  QueryBookingInformationArgs,
  QueryHotelInformationArgs,
  PackagesCriteria,
} from '@whitbread-eos/api';
import {
  CREATE_RESERVATION_GUEST_CCUI,
  GET_PAYMENT_STATUS,
  GET_ANCILLARIES_BOOKING_INFO,
  GET_HOTEL_INFORMATION,
  UPDATE_REASON_FOR_STAY,
  HotelBrand,
  PurposeOfStay,
  Area,
  BASKET_STATUS,
  CompanyProfile,
  FT_CCUI_GDP_BILLING_ADDRESS,
  FT_CCUI_GDP_MULTI_BOOKING,
  FT_CCUI_GDP_SINGLE_BOOKING,
  FT_CCUI_ADDITIONAL_INFORMATION,
  FT_CCUI_ACCOMPANYING_GUEST_DETAILS,
  FT_PI_BB_CCUI_COMPANY_NAME_SPECIAL_CHARACTERS,
  FT_PI_CCUI_CONSOLIDATE_MOBILE_LANDLINE,
  FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN_ANCILLARIES_GDP_PAYMENT,
  FT_CCUI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE,
} from '@whitbread-eos/api';
import { FormProps, LoadingSpinner } from '@whitbread-eos/atoms';
import { INITIAL_GUEST_DETAILS_FORM_DATA } from '@whitbread-eos/molecules';
import { AgentMemo, BookingSummaryContainer } from '@whitbread-eos/organisms';
import {
  getCityTaxMessages,
  isStringValid,
  useCustomLocale,
  useLocalStorage,
  useMutationRequest,
  useQueryRequest,
  usePackages,
  formatDataTestId,
  getDefaultDataFromBooking,
  updateAncillariesAnalytics,
  useFeatureToggle,
  GLOBALS,
  formatGuests,
  mapBookingInformationForReuseBooking,
} from '@whitbread-eos/utils';
import { clearGuestFormData } from '@whitbread-eos/utils/server';
import { useTranslation } from 'next-i18next';
import dynamic from 'next/dynamic';
import { NextRouter } from 'next/router';
import { SetStateAction, useCallback, useEffect, useState } from 'react';

import { guestDetailsFormConfig } from './ccuiFormConfig/guestDetailsFormConfig';

const Form = dynamic(
  async () => {
    const { Form } = await import('@whitbread-eos/atoms');
    return { default: Form };
  },
  {
    ssr: false,
  }
);

interface CheckInfoToggle {
  [key: string]: boolean;
}

interface Props {
  queryClient: QueryClient;
  router: NextRouter;
  hiQueryInput: QueryHotelInformationArgs;
  pcksQueryInput: PackagesCriteria;
  biQueryInput: QueryBookingInformationArgs;
  cachedGuestDetailsFormData?: FormProps['defaultValues'];
}

export default function GuestDetailsPageCcui({
  queryClient,
  router,
  pcksQueryInput,
  hiQueryInput,
  biQueryInput,
  cachedGuestDetailsFormData,
}: Props) {
  const { t } = useTranslation();
  const { language: currentLang, country: currentCountry } = useCustomLocale();
  const { bookingFlowId } = router.query;
  const [updatingTotalCost, setUpdatingTotalCost] = useState(false);
  const [isLocationRequired, setIsLocationRequired] = useState(false);
  const [isDefaultValuesReady, setIsDefaultValuesReady] = useState(false);
  const [companyProfile] = useLocalStorage<CompanyProfile | undefined>('CompanyProfile', undefined);
  const [isDifferentBillingAddress, setIsDifferentBillingAddress] = useState(false);
  const [showCheckInInfo, setShowCheckInInfo] = useState<CheckInfoToggle>({});

  const [reUseReservation, setReUseReservation] = useLocalStorage<{ id: string } | null>(
    'reUseReservation',
    null
  );

  const {
    isLoading: pcksIsLoading,
    error: pcksError,
    isError: pcksIsError,
    packages,
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
    options: { refetchOnMount: 'always', cacheTime: 0 },
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

  const { data: paymentStatus } = useQueryRequest('GetPaymentStatus', GET_PAYMENT_STATUS, {
    basketReference: biQueryInput.basketReference,
  });

  const {
    mutation: svBknMutation,
    isLoading: svBknIsLoading,
    isError: svBknIsError,
    data: svBknData,
    error: svBknError,
    isSuccess: svBknIsSuccess,
  } = useMutationRequest(CREATE_RESERVATION_GUEST_CCUI);

  const { mutation: urfsMutation } = useMutationRequest(UPDATE_REASON_FOR_STAY);

  useEffect(() => {
    if (bkngData && packages) {
      updateAncillariesAnalytics(bkngData.bookingInformation, packages, Area.CCUI);
    }
  }, [bkngData, packages]);

  useEffect(() => {
    if (
      !svBknIsError &&
      svBknIsSuccess &&
      svBknData?.createReservationGuest &&
      isStringValid(svBknData.createReservationGuest.basketReference)
    ) {
      setReUseReservation(null);
      router.push(
        `/${currentCountry}/${currentLang}${
          bookingFlowId ? `/${bookingFlowId}` : ''
        }/payment?reservationId=${svBknData.createReservationGuest.basketReference}`
      );
    }
  }, [svBknData, svBknIsError, svBknIsSuccess, router]);

  const [isBookingForSomeoneElse, setIsBookingForSomeoneElse] = useState<boolean>(false);

  // GDP Feature switches
  const {
    [FT_CCUI_GDP_SINGLE_BOOKING]: isSingleRoomRedesignEnabled,
    [FT_CCUI_GDP_MULTI_BOOKING]: isMultiRoomRedesignEnabled,
    [FT_CCUI_GDP_BILLING_ADDRESS]: isBillingAddressEnabled,
    [FT_CCUI_ADDITIONAL_INFORMATION]: isAdditionalInformationEnabledFlag,
    [FT_CCUI_ACCOMPANYING_GUEST_DETAILS]: isAccompanyingGuestDetailsEnabledFlag,
    [FT_PI_BB_CCUI_COMPANY_NAME_SPECIAL_CHARACTERS]: isCompanyNameAdvanceEnabled,
    [FT_PI_CCUI_CONSOLIDATE_MOBILE_LANDLINE]: isConsolidateMobileLandlineEnabled,
    [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN_ANCILLARIES_GDP_PAYMENT]: isCityTaxBreakdownEnabled,
    [FT_CCUI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE]: isRemovePIIDataFromLocalStorageEnabled,
  } = useFeatureToggle();

  const {
    data: reUseBookingData,
    isLoading: reUseBookingIsLoading,
    isSuccess: reUseBookingIsSuccess,
  } = useQueryRequest(
    [
      'GetBookingInformation',
      biQueryInput.language,
      biQueryInput.country,
      reUseReservation?.id ?? '',
    ],
    GET_ANCILLARIES_BOOKING_INFO,
    { ...biQueryInput, basketReference: reUseReservation?.id ?? '' },
    { enabled: !!reUseReservation?.id && isRemovePIIDataFromLocalStorageEnabled }
  );

  const isBillingAddressDisplayed =
    isBillingAddressEnabled && currentLang === GLOBALS.language.DE && isGermanHotel;

  const isAdditionalInformationEnabled = isAdditionalInformationEnabledFlag && isGermanHotel;
  const shouldUseAccompanyingGuest = !isGermanHotel && isAccompanyingGuestDetailsEnabledFlag;

  const isSomeoneElseAndSingleRoomRedesign = isSingleRoomRedesignEnabled && isBookingForSomeoneElse;

  const multiroom = bkngData?.bookingInformation?.reservationByIdList?.length > 1;

  const continueBooking = useCallback(
    (data: any) => {
      const stayingGuests = formatGuests(
        data,
        isSomeoneElseAndSingleRoomRedesign,
        shouldUseAccompanyingGuest
      );
      let companyName = '';
      if (data?.addressSelection === 'BUSINESS') {
        companyName = data.billingAddressCheckbox
          ? formDetails.billing.address.companyName
          : data.companyName;
      }
      svBknMutation.mutate({
        hotelId: bkngData?.bookingInformation?.hotelId,
        reasonForStay: data.reasonForStay,
        companyName,
        addressLine1: data.addressLine1,
        addressLine2: data.addressLine2,
        addressLine3: data.addressLine3,
        addressLine4: data.addressLine4,
        // make HOME default address for De hotel and multiroom design feature switch
        addressType: isGermanHotel && isMultiRoomRedesignEnabled ? 'HOME' : data.addressSelection,
        cityName: data.cityName,
        countryCode: data.countryCode,
        postalCode: data.postalCode,
        title: data.title,
        firstName: data.firstName,
        lastName: data.lastName,
        emailAddress: data.email,
        mobile: data.phone ?? '',
        landline: data.landline ?? '',
        acceptFutureMailing: false,
        basketReference: biQueryInput.basketReference,
        stayingGuests: stayingGuests,
      });
    },
    [biQueryInput.basketReference, bkngData, svBknMutation]
  );

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

  const goBack = useCallback(() => {
    router.back();
  }, [currentCountry, currentLang, bookingFlowId, biQueryInput.basketReference]);

  const onSubmit = async (data: any) => {
    if (isRemovePIIDataFromLocalStorageEnabled) {
      if (cachedGuestDetailsFormData) {
        await clearGuestFormData(biQueryInput.basketReference);
      }
    } else {
      saveFormDataInLocalStorage(data);
    }
    continueBooking(data);
  };

  const onChange = (values: { billingAddressCheckbox: boolean }) => {
    if (values?.billingAddressCheckbox !== isDifferentBillingAddress) {
      setIsDifferentBillingAddress(values?.billingAddressCheckbox);
    }
  };

  const [formDetails, setFormDetails] = useLocalStorage(
    'formDetails',
    INITIAL_GUEST_DETAILS_FORM_DATA
  );

  // Set default for addressSelection to be 'HOME' for GDP redesign for De hotels
  // this is to make the HOME address set as default and have the booker address fields opened by default
  // as there is no manual toggle
  INITIAL_GUEST_DETAILS_FORM_DATA.addressSelection = isGermanHotel ? 'HOME' : '';

  useEffect(() => {
    if (isMultiRoomRedesignEnabled && !isRemovePIIDataFromLocalStorageEnabled) {
      setFormDetails(INITIAL_GUEST_DETAILS_FORM_DATA);
    }
  }, [
    INITIAL_GUEST_DETAILS_FORM_DATA,
    isMultiRoomRedesignEnabled,
    isRemovePIIDataFromLocalStorageEnabled,
  ]);

  const [defaultValues, setDefaultValues] = useState<FormProps['defaultValues']>(
    getDefaultDataFromBooking(
      bkngData?.bookingInformation?.reservationByIdList ?? [],
      isRemovePIIDataFromLocalStorageEnabled ? {} : formDetails,
      biQueryInput.basketReference,
      currentLang,
      hiData?.hotelInformation?.brand,
      isGermanHotel,
      isMultiRoomRedesignEnabled
    )
  );

  useEffect(() => {
    if (
      formDetails?.updated &&
      formDetails?.basketReferenceId === biQueryInput.basketReference &&
      !isRemovePIIDataFromLocalStorageEnabled
    ) {
      setDefaultValues(
        getDefaultDataFromBooking(
          bkngData?.bookingInformation?.reservationByIdList ?? [],
          formDetails,
          biQueryInput.basketReference,
          currentLang,
          hiData?.hotelInformation?.brand,
          isGermanHotel,
          isMultiRoomRedesignEnabled
        )
      );
    }
  }, [
    formDetails,
    biQueryInput.basketReference,
    bkngData?.bookingInformation?.reservationByIdList,
    currentLang,
    hiData?.hotelInformation?.brand,
    isGermanHotel,
    isMultiRoomRedesignEnabled,
    isRemovePIIDataFromLocalStorageEnabled,
  ]);

  useEffect(() => {
    if (
      isRemovePIIDataFromLocalStorageEnabled &&
      bkngData?.bookingInformation &&
      !reUseReservation?.id &&
      !cachedGuestDetailsFormData
    ) {
      const nextDefaultValues = getDefaultDataFromBooking(
        bkngData?.bookingInformation?.reservationByIdList ?? [],
        {},
        biQueryInput.basketReference,
        currentLang,
        hiData?.hotelInformation?.brand,
        isGermanHotel,
        isMultiRoomRedesignEnabled
      );

      setDefaultValues({
        ...nextDefaultValues,
        manualAddressToggle:
          currentLang === 'de' ||
          (nextDefaultValues?.addressSelection && nextDefaultValues?.addressLine1)
            ? 'manualAddress'
            : '',
      });
    }
  }, [
    isRemovePIIDataFromLocalStorageEnabled,
    biQueryInput.basketReference,
    bkngData?.bookingInformation,
    reUseReservation?.id,
    cachedGuestDetailsFormData,
    currentLang,
    hiData?.hotelInformation?.brand,
    isGermanHotel,
    isMultiRoomRedesignEnabled,
  ]);

  useEffect(() => {
    if (
      isRemovePIIDataFromLocalStorageEnabled &&
      reUseBookingIsSuccess &&
      reUseBookingData &&
      reUseReservation?.id
    ) {
      setDefaultValues(
        mapBookingInformationForReuseBooking(
          biQueryInput.basketReference,
          reUseBookingData?.bookingInformation?.reservationByIdList ?? [],
          currentLang,
          isMultiRoomRedesignEnabled
        )
      );
    }
  }, [
    reUseReservation,
    isRemovePIIDataFromLocalStorageEnabled,
    biQueryInput,
    reUseBookingIsSuccess,
    reUseBookingData,
    currentLang,
    isMultiRoomRedesignEnabled,
  ]);

  useEffect(() => {
    if (typeof isRemovePIIDataFromLocalStorageEnabled !== 'boolean') {
      return;
    }

    if (isRemovePIIDataFromLocalStorageEnabled && cachedGuestDetailsFormData) {
      setDefaultValues((currentValues) => ({
        ...(currentValues ?? {}),
        ...cachedGuestDetailsFormData,
      }));
    }

    setIsDefaultValuesReady(true);
  }, [isRemovePIIDataFromLocalStorageEnabled, cachedGuestDetailsFormData]);

  useEffect(() => {
    setIsDifferentBillingAddress(true);
  }, []);

  const [currentReasonForStay, setCurrentReasonForStay] = useState(
    HotelBrand.HUB === hiData?.hotelInformation?.brand ? PurposeOfStay.LEISURE : ''
  );

  useEffect(() => {
    if ((defaultValues.countryCode as string) === GLOBALS.language.DE) {
      setIsLocationRequired(true);
    }
  }, [defaultValues.countryCode]);

  useEffect(() => {
    if ((defaultValues.billing_countryCode as string) === GLOBALS.localeUpper.DE) {
      setIsLocationRequired(true);
    }
  }, [defaultValues.billing_countryCode]);

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

  const getFormState: FormProps['getFormState'] = useCallback(
    (state: any) => {
      if (isRemovePIIDataFromLocalStorageEnabled && !isDefaultValuesReady) {
        return;
      }
      setDefaultValues(state as SetStateAction<FormProps['defaultValues']>);
    },
    [isDefaultValuesReady, isRemovePIIDataFromLocalStorageEnabled]
  );
  const baseDataTestId = 'GuestDetails';

  function saveFormDataInLocalStorage(formData: any) {
    const bookerIsNotGuest = multiroom
      ? !formData.leadGuest?.some((guest: { stayInThisRoom: boolean }) => !!guest.stayInThisRoom)
      : formData.bookingForSomeoneElse || isSomeoneElseAndSingleRoomRedesign;

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
            postalCode: formData.billing_postalCode,
            cityName: formData.billing_cityName,
            billingAddressSelection: formData.billing_addressSelection,
          },
          differentBillingAddress: formData.billingAddressCheckbox,
        };
        return formDetails;
      }
    });
  }

  if (paymentStatus?.basket?.status === BASKET_STATUS.COMPLETED) {
    router.push(`/${currentLang}/${currentCountry}`);
    return <></>;
  }

  return (
    <Container {...containerStyle}>
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
              area={Area.CCUI}
              isExtrasDisplayed={!!packages?.extrasItems}
              submitButtonDisabled={updatingTotalCost}
              isCityTaxBreakdownEnabled={isCityTaxBreakdownEnabled}
            />
          </Flex>
        </GridItem>

        <GridItem {...mainContentStyle}>
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
            area={Area.CCUI}
            isExtrasDisplayed={!!packages?.extrasItems}
            submitButtonDisabled={updatingTotalCost}
            isCityTaxBreakdownEnabled={isCityTaxBreakdownEnabled}
          />
        </GridItem>
      </Grid>
      <AgentMemo />
    </Container>
  );

  function renderPageContent() {
    if (
      pcksIsLoading ||
      bkngIsLoading ||
      hiIsLoading ||
      svBknIsLoading ||
      svBknIsSuccess ||
      reUseBookingIsLoading
    ) {
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
          <Form
            key={
              isRemovePIIDataFromLocalStorageEnabled
                ? `guest-details-form-${isDefaultValuesReady ? 'ready' : 'waiting'}`
                : undefined
            }
            onChange={onChange}
            {...guestDetailsFormConfig({
              getFormState,
              defaultValues,
              onSubmit,
              baseDataTestId,
              currentLang,
              currentCountry,
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
              isSingleRoomRedesignEnabled,
              isMultiRoomRedesignEnabled,
              isGermanHotel,
              isBillingAddressEnabled: isBillingAddressDisplayed,
              isBookingForSomeoneElse,
              setIsBookingForSomeoneElse,
              companyProfile,
              isAdditionalInformationEnabled,
              showCheckInInfo,
              setShowCheckInInfo,
              shouldAskForAccompanyingGuest: shouldUseAccompanyingGuest,
              isDifferentBillingAddress,
              isCompanyNameAdvanceEnabled,
              submitButtonDisabled: updatingTotalCost,
              isConsolidateMobileLandlineEnabled,
              isCountryAllowTypingEnabled: true,
              isRemovePIIDataFromLocalStorageEnabled: isRemovePIIDataFromLocalStorageEnabled,
            })}
          />
        </Box>
      </Flex>
    );
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
  minW: 'var(--chakra-space-breakpoint-lg)',
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

const mainContentStyle = {
  px: {
    mobile: 'md',
    sm: '0px',
    md: 'lg',
  },
  maxW: '100vw',
};

const pageContentStyle = {
  px: {
    mobile: 'md',
    sm: 'lg',
    lg: '0',
  },
  pt: {
    mobile: 'lg',
    md: 'lg',
    lg: 0,
  },
} as GridItemProps;

const bookingSummaryMobileContainerStyle = {
  // just as a mock until booking summary will be done
  display: {
    mobile: 'block',
    lg: 'none',
  },
  backgroundColor: 'lightGrey5',
} as GridItemProps;

const bookingSummaryMobileTriggerStyle = {
  // just as a mock until booking summary will be done
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
    lg: '288px', //  just as a mock until booking summary will be done
    xl: '309px', //  just as a mock until booking summary will be done
  },
} as GridItemProps;

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
