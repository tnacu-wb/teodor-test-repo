import {
  Box,
  BoxProps,
  Flex,
  FlexProps,
  Grid,
  GridItem,
  GridItemProps,
  GridProps,
  Text,
} from '@chakra-ui/react';
import { PayPalScriptProvider } from '@paypal/react-paypal-js';
import {
  AddressGuestInput,
  Area,
  BCReservationListItem,
  BOOKING_CHANNEL,
  BookingDataReservationDetailsProps,
  BookingSummaryDataProps,
  BusinessAllowance,
  DonationPackage,
  GET_BOOKING_INFORMATION,
  GET_DONATIONS_QUERY,
  GET_HOTEL_INFORMATION,
  GET_PAYMENT_INFO_MESSAGES_QUERY,
  GET_TERMS_AND_CONDITIONS_QUERY,
  QueryHotelInformationArgs,
  INITIATE_PAYMENT_MUTATION,
  INITIATE_PAYPAL_PAYMENT_MUTATION,
  MessagesPaymentType,
  PAYMENT_FAILED_INITIAL_VALUE,
  PAYMENT_FAILED_KEY,
  PAYMENT_FAILED_VALUE,
  PAYMENT_FAILURE_CODE_KEY,
  PAYMENT_FAILURE_CODE_INITIAL_VALUE,
  PAYMENT_FAILURE_DESCRIPTION_KEY,
  PAYMENT_FAILURE_DESCRIPTION_INITIAL_VALUE,
  PAYPAL_PAYMENT,
  APGP_PAYMENT,
  MealItem,
  MealKids,
  PrivacyPolicy,
  PackagesCriteria,
  PageName,
  PaymentMethod,
  paymentOptions as PaymentType,
  SelectedMealsPerRoom,
  UserType,
  paymentSteps,
  GET_PAYMENT_STATUS,
  BASKET_STATUS,
  PAYMENT_ANALYTICS_KEY,
  PaymentAnalytics,
  PaymentOption,
  PiCardType,
  paymentOptions,
  WALLET_APPLE,
  WIFI_IDS,
  FT_PI_BB_CCUI_COMPANY_NAME_SPECIAL_CHARACTERS,
  FT_PI_BB_CCUI_DISABLE_PAYMENTS,
  FT_PI_ENABLE_PAYMENT_REDESIGN,
  FT_PI_BB_NON_GUARANTEED_REMINDER,
  BASKET_DETAILS_STORAGE_KEY,
  BASKET_DETAILS_STATE_INITIAL_VALUE,
  FT_PI_PIB_SWAP_PAYMENT_OPTIONS,
  FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN_ANCILLARIES_GDP_PAYMENT,
  FT_PI_PIB_PAYMENT_BACK_TO_OPTIONS_LINK,
  FT_PI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE,
  HotelBrand,
} from '@whitbread-eos/api';
import {
  DonationsInfoBox,
  IframeEmbed,
  Info,
  Alert,
  LoadingSpinner,
  Notification,
  PaypalWBButton,
  PaypalWBProps,
} from '@whitbread-eos/atoms';
import {
  BackToPage,
  BusinessAllowances,
  DataSecuritySection,
  INITIAL_GUEST_DETAILS_FORM_DATA,
  PaymentAuthorization,
  PaymentDetails,
  ReferenceDetails,
  SEO,
  Donations,
} from '@whitbread-eos/molecules';
import {
  BillingAddress,
  BookingSummary,
  PaymentTypeContainer,
  TotalCostPayment,
} from '@whitbread-eos/organisms';
import {
  adultsMealsSelector,
  analytics,
  analyticsConfirmation,
  childrenMealsSelector,
  formatUrlTermsConditions,
  getBillingAddress,
  getBookingSummaryData,
  getCityTaxMessages,
  getImportantMessages,
  getIsBillingAddressDisplayed,
  getIsDonationInfoBoxDisplayed,
  getIsDonationsDisplayed,
  getMaxValueFromRoomStays,
  getNightsNumber,
  getPaymentError,
  getPaypalDeviceData,
  getPaypalOptionsParams,
  getPriceValueWithDecimal,
  logicalAndOperator,
  logicalOrOperator,
  mealsMapperSelector,
  securityNoticeMoreInfoDataSelector,
  useCustomLocale,
  useIPageSubmission,
  useLocalStorage,
  useMutationRequest,
  usePackages,
  useQueryRequest,
  createReservationDetails,
  getTotalCost,
  updateAncillariesAnalytics,
  useSessionStorage,
  usePaymentPaypal,
  useFeatureToggle,
  renderSanitizedHtml,
  useSemanticTypography,
  useUpdateRateName,
  applyDefaultPaymentRestrictions,
  isSecureBookingPage,
  type secureBookingType,
  addBasketIdToCookie,
  updateDashboardAnalytics,
  formatAnalyticsFunnelStep,
  getDefaultDataFromBooking,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import getConfig from 'next/config';
import { useRouter } from 'next/router';
import Script from 'next/script';
import { SetStateAction, useEffect, useMemo, useState } from 'react';
import { v4 as uuidv4 } from 'uuid';

import {
  PAYMENT_NAVIGATION_SOURCE_GUEST_DETAILS,
  PAYMENT_NAVIGATION_SOURCE_QUERY_PARAM,
} from '../../utils/pi-all-pages-constants';

interface Props {
  hiQueryInput: QueryHotelInformationArgs;
  pcksQueryInput: PackagesCriteria;
  basketReference: string | null;
  paypalPaymentData?: PaymentMethod | null;
  paymentStatus?: { basket: { status: BASKET_STATUS } };
}

interface GetPaymentParamsProps {
  billingAddress?: AddressGuestInput;
  paymentType?: string;
  paypalNonce?: string;
  paypalDeviceData?: any;
}

export function PIPageContent({
  pcksQueryInput,
  hiQueryInput,
  basketReference,
  paypalPaymentData,
  paymentStatus: initialPaymentStatus,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const router = useRouter();
  const { language, country } = useCustomLocale();
  const getTypographyProps = useSemanticTypography();
  const { publicRuntimeConfig = {} } = getConfig() || {};

  const [paymentFailedValue, setPaymentFailedValue] = useSessionStorage<string>(
    PAYMENT_FAILED_KEY,
    PAYMENT_FAILED_INITIAL_VALUE
  );

  const [paymentFailureCode, setPaymentFailureCode] = useSessionStorage<string>(
    PAYMENT_FAILURE_CODE_KEY,
    PAYMENT_FAILURE_CODE_INITIAL_VALUE
  );
  const [paymentFailureDescription, setPaymentFailureDescription] = useSessionStorage<string>(
    PAYMENT_FAILURE_DESCRIPTION_KEY,
    PAYMENT_FAILURE_DESCRIPTION_INITIAL_VALUE
  );
  const [basketDetailsState] = useLocalStorage(
    BASKET_DETAILS_STORAGE_KEY,
    BASKET_DETAILS_STATE_INITIAL_VALUE
  );
  const {
    [FT_PI_ENABLE_PAYMENT_REDESIGN]: isPaymentRedesignEnabled,
    [FT_PI_BB_CCUI_COMPANY_NAME_SPECIAL_CHARACTERS]: isCompanyNameAdvanceEnabled,
    [FT_PI_BB_CCUI_DISABLE_PAYMENTS]: disablePaymentOptions,
    [FT_PI_BB_NON_GUARANTEED_REMINDER]: isSecureBookingFeatureEnabled,
    [FT_PI_PIB_SWAP_PAYMENT_OPTIONS]: isSwapPaymentOptionsEnabled,
    [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN_ANCILLARIES_GDP_PAYMENT]: isCityTaxBreakdownEnabled,
    [FT_PI_PIB_PAYMENT_BACK_TO_OPTIONS_LINK]: isBackToPaymentOptionsLinkEnabled,
    [FT_PI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE]: isRemovePIIDataFromLocalStorageEnabled,
  } = useFeatureToggle();

  const { data: paymentStatus } = useQueryRequest(
    ['GetPaymentStatus', basketReference],
    GET_PAYMENT_STATUS,
    {
      basketReference,
    }
  );

  if (typeof window !== 'undefined') {
    analytics.update({
      hasPaymentFailure: undefined,
    });
  }

  const isDonationHidden =
    paymentStatus?.basket?.status === BASKET_STATUS.PAY_PENDING ||
    initialPaymentStatus?.basket?.status === BASKET_STATUS.PAY_PENDING;

  const { isLoading: isLoadingBookingInformation, data: bkngData } = useQueryRequest(
    ['GetBookingInformation', language, country, basketReference],
    GET_BOOKING_INFORMATION,
    {
      basketReference,
      language,
      country,
      bookingChannelCriteria: {
        channel: 'PI',
        subchannel: 'WEB',
        language: language === 'en' ? 'EN' : 'DE',
      },
    }
  );

  const firstRoom = logicalOrOperator(bkngData?.bookingInformation?.reservationByIdList[0], {});
  const noNights = getNightsNumber(
    firstRoom.roomStay?.arrivalDate,
    firstRoom.roomStay?.departureDate
  );

  const bookingInformation = {
    hotelId: bkngData?.bookingInformation.hotelId,
    adults: getMaxValueFromRoomStays(
      bkngData?.bookingInformation?.reservationByIdList,
      'adultsNumber'
    ),
    children: getMaxValueFromRoomStays(
      bkngData?.bookingInformation?.reservationByIdList,
      'childrenNumber'
    ),
    nrNights: noNights,
    ratePlanCode: firstRoom?.roomStay?.ratePlanCode,
    totalCost: bkngData?.bookingInformation?.totalCost,
  };

  const {
    isLoading: isLoadingPaymentPcks,
    packages,
    privacyPolicy,
    hotelHasCityTaxForBusiness,
    hotelHasCityTaxForLeisure,
  } = usePackages({
    adultsNumber: pcksQueryInput.adultsNumber,
    childrenNumber: pcksQueryInput.childrenNumber,
    hotelId: pcksQueryInput.hotelId,
    basketReferenceId: basketReference as string,
    endDate: pcksQueryInput.endDate,
    startDate: pcksQueryInput.startDate,
    bookingFlowId: pcksQueryInput.bookingFlowId,
    nightsNumber: pcksQueryInput.nightsNumber,
    channel: pcksQueryInput.channel,
  });

  const hasAncillariesWifiSelected = packages?.roomSelection?.some((selection) =>
    selection.packagesSelection?.some((packageSelection) => {
      const id = packageSelection?.id;
      if (!id) return false;

      return WIFI_IDS.includes(id as (typeof WIFI_IDS)[number]);
    })
  );

  const { isLoading: isLoadingHotelInformation, data: hiData } = useQueryRequest(
    ['GetHotelInformation', hiQueryInput.hotelId, hiQueryInput.country, hiQueryInput.language],
    GET_HOTEL_INFORMATION,
    {
      ...hiQueryInput,
    }
  );

  const {
    isLoading: isLoadingDonations,
    data: donationsData,
    refetch: refetchDonations,
  } = useQueryRequest(
    [
      'GetDonations',
      hiQueryInput.hotelId,
      country,
      language,
      bookingInformation?.ratePlanCode,
      BOOKING_CHANNEL.PI,
    ],
    GET_DONATIONS_QUERY,
    {
      country,
      language,
      hotelId: hiQueryInput.hotelId,
      rateCode: bookingInformation?.ratePlanCode,
      bookingChannel: BOOKING_CHANNEL.PI,
    },
    { enabled: false }
  );

  const { isLoading: isLoadingTermsAndConditions, data: termsAndConditionsData } = useQueryRequest(
    [
      'GetTermsAndConditions',
      hiQueryInput.hotelId,
      country,
      language,
      bookingInformation?.ratePlanCode,
      BOOKING_CHANNEL.PI,
    ],
    GET_TERMS_AND_CONDITIONS_QUERY,
    {
      country,
      language,
      hotelId: hiQueryInput.hotelId,
      rateCode: bookingInformation?.ratePlanCode,
      bookingChannel: BOOKING_CHANNEL.PI,
    }
  );
  const { isLoading: isLoadingPaymentInfoMessage, data: paymentInfoMessageData } = useQueryRequest(
    [
      'GetPaymentInfoMessages',
      hiQueryInput.hotelId,
      country,
      language,
      bookingInformation?.ratePlanCode,
      BOOKING_CHANNEL.PI,
    ],
    GET_PAYMENT_INFO_MESSAGES_QUERY,
    {
      hotelId: hiQueryInput.hotelId,
      language,
      country,
      rateCode: bookingInformation?.ratePlanCode,
      bookingChannel: BOOKING_CHANNEL.PI,
    },
    { enabled: !!bookingInformation?.ratePlanCode }
  );

  useUpdateRateName(bkngData?.bookingInformation, language, country);

  const {
    mutation: initiatePaymentMutation,
    isSuccess,
    data: initiatePaymentMutationData,
    isError: isErrorInitiatePaymentMutation,
    error: initiatePaymentMutationError,
  } = useMutationRequest(INITIATE_PAYMENT_MUTATION, true);

  const {
    mutation: initiatePaypalPaymentMutation,
    isSuccess: isPaypalSuccess,
    isLoading: isPaypalLoading,
    data: initiatePaypalPaymentMutationData,
    isError: isErrorInitiatePaypalPaymentMutation,
    error: initiatePaypalPaymentMutationError,
  } = useMutationRequest(INITIATE_PAYPAL_PAYMENT_MUTATION, true);

  // Add basket ID to cookie on page load for authorization on confirmation/registration pages
  useEffect(() => {
    if (basketReference) {
      addBasketIdToCookie(basketReference);
    }
  }, [basketReference]);

  useEffect(() => {
    if (bkngData && packages) {
      updateAncillariesAnalytics(bkngData.bookingInformation, packages);
    }
  }, [bkngData, packages]);

  useEffect(() => {
    if (bkngData?.bookingInformation && language === 'en') refetchDonations();
  }, [bkngData?.bookingInformation, language, refetchDonations]);

  useEffect(() => {
    const cleanupPaymentFailedValue = () => {
      setPaymentFailedValue(PAYMENT_FAILED_INITIAL_VALUE);
      setPaymentFailureCode(PAYMENT_FAILURE_CODE_INITIAL_VALUE);
      setPaymentFailureDescription(PAYMENT_FAILURE_DESCRIPTION_INITIAL_VALUE);
    };
    window.addEventListener('beforeunload', cleanupPaymentFailedValue);
    return () => window.removeEventListener('beforeunload', cleanupPaymentFailedValue);
  }, [setPaymentFailedValue]);

  const { isPaymentComplete, cardType } = useIPageSubmission();
  const [paymentStepState, setPaymentStepState] = useState(paymentSteps.PAYMENT_DETAILS);
  const isPaymentDetailsStep = paymentStepState === paymentSteps.PAYMENT_DETAILS;

  const [selectedPaymentDetail, setSelectedPaymentDetail] = useState<PaymentOption>({
    type: 'default',
    order: 1,
    enabled: false,
  });
  const [selectedPaymentType, setSelectedPaymentType] = useState({
    name: '',
    type: '',
    subType: '',
    order: 0,
    logoSrc: '',
    paymentOptions: [{ type: '', order: 0, enabled: true }],
    enabled: false,
    cnpPreSelected: false,
    cnpOptionAvailable: false,
    reasons: [],
  } as PaymentMethod);

  const paymentCardSelected =
    selectedPaymentType?.type === 'SAVED_CARD'
      ? selectedPaymentType?.card?.type
      : selectedPaymentType.type;

  const [confAnalytics, setConfAnalytics] = useSessionStorage<PaymentAnalytics>(
    PAYMENT_ANALYTICS_KEY,
    {}
  );

  useEffect(() => {
    if (paymentStepState === paymentSteps.CARD_DETAILS && basketReference) {
      window.localStorage.setItem('3cpVisited', basketReference.toString());
      setConfAnalytics({
        ...confAnalytics,
        paymentCardSelected,
        paymentTakenNow: (selectedPaymentDetail?.type === paymentOptions.PAY_NOW).toString(),
      });
      analytics.update({
        paymentCardSelected,
        paymentTakenNow: (selectedPaymentDetail?.type === paymentOptions.PAY_NOW).toString(),
      });
    }
    if (
      paymentStepState === paymentSteps.PAYMENT_DETAILS &&
      basketReference &&
      selectedPaymentDetail?.type === PaymentType.RESERVE_WITHOUT_CARD
    ) {
      setConfAnalytics({
        ...confAnalytics,
        paymentCardSelected: selectedPaymentDetail.type,
        paymentTakenNow: '',
      });
      analytics.update({
        paymentCardSelected: selectedPaymentDetail.type,
        paymentTakenNow: 'false',
      });
    }
  }, [paymentStepState, selectedPaymentDetail]);

  const [selectedDonation, setSelectedDonation] = useState<DonationPackage>();

  const [referenceDetails, setReferenceDetails] = useState({
    reference: '',
    purchaseOrderNumber: '',
  });
  const [hasError, setHasError] = useState<boolean>(false);
  const [hasBusinessAllowanceError, setHasBusinessAllowanceError] = useState<boolean>(false);
  const [hasReferenceErrors, setHasReferenceErrors] = useState(false);
  const [paymentAuth, setPaymentAuth] = useState(false);
  const [paymentPassword, setPaymentPassword] = useState('');
  const [dinnerAllowance, setDinnerAllowance] = useState(false);
  const [businessAllowances, setBusinessAllowances] = useState({
    totalDinnerBudgetPersonNight: '£',
    isAlcoholDinner: false,
    carParking: false,
    wifi: false,
    // decision is yet to be made regarding where this field should be saved, until then it will be hidden
    // additionalCharges: false,
  } as BusinessAllowance);

  const [notifyPaymentMethodChange, setNotifyPaymentMethodChange] = useState<boolean>(false);

  useEffect(() => {
    setHasError(hasBusinessAllowanceError);
  }, [hasBusinessAllowanceError, dinnerAllowance]);

  const rooms = bkngData?.bookingInformation.reservationByIdList.map(
    (room: BCReservationListItem) => {
      return {
        adultsNumber: room?.roomStay?.adultsNumber,
        rate: room?.roomStay?.ratePlanCode,
        type: room?.roomStay?.roomExtraInfo?.roomType,
      };
    }
  );

  const togglePaymentAuth = () => {
    setPaymentAuth(!paymentAuth);
    if (paymentAuth) {
      if (!businessAllowances.totalDinnerBudgetPersonNight.startsWith('£')) {
        setBusinessAllowances({
          ...businessAllowances,
          totalDinnerBudgetPersonNight: '£',
        });
      }
    }
  };

  const privacyPolicyData: PrivacyPolicy = securityNoticeMoreInfoDataSelector(privacyPolicy ?? {});

  const meals = packages?.meals;
  const mealsKids = packages?.mealsKids;
  const roomSelection = packages?.roomSelection;

  const [selectedMeals, setSelectedMeals] = useState<SelectedMealsPerRoom[]>([]);
  const adultsMeals: MealItem[] = adultsMealsSelector(packages?.meals, noNights);
  const childrenMeals: MealKids[] = childrenMealsSelector(packages?.mealsKids);
  const currentReasonForStay =
    bkngData?.bookingInformation?.reservationByIdList?.[0]?.additionalGuestInfo?.purposeOfStay;
  const cityTaxMessages = getCityTaxMessages(
    hotelHasCityTaxForLeisure,
    hotelHasCityTaxForBusiness,
    currentReasonForStay,
    t,
    bkngData?.bookingInformation?.currencyCode,
    language,
    bkngData?.bookingInformation?.totalCost
  );

  useEffect(() => {
    if (roomSelection && meals && mealsKids) {
      setSelectedMeals(mealsMapperSelector(meals, mealsKids, roomSelection));
    }
  }, [roomSelection, meals, mealsKids]);

  const numberOfPackages = packages?.roomSelection?.[0]?.packagesSelection?.length ?? 0;
  const savedDonation = donationsData?.donations?.donationPackages
    ?.map((donation: DonationPackage) => {
      return (packages?.roomSelection?.[0]?.packagesSelection ?? []).find(
        (pack) => donation.code === pack.id
      );
    })
    ?.filter(Boolean)[0];

  useEffect(() => {
    if (savedDonation && savedDonation?.id !== '') {
      const updateDonation = donationsData?.donations?.donationPackages?.find(
        (pckg: DonationPackage) => pckg?.code === savedDonation?.id
      );
      setSelectedDonation(updateDonation);
    }
  }, [packages?.roomSelection?.[0] ?? {}, donationsData?.donations?.donationPackages]);

  function getSelectedDonation() {
    const updateDonation = donationsData?.donations?.donationPackages?.find(
      (pckg: DonationPackage) => pckg?.code === savedDonation?.id
    ) || { unitPrice: 0, code: '' };

    if (!selectedDonation) {
      return { unitPrice: 0, code: '' };
    } else if (savedDonation) {
      return {
        unitPrice: updateDonation?.unitPrice,
        code: updateDonation?.code,
      };
    }
    return {
      unitPrice: selectedDonation?.unitPrice,
      code: selectedDonation?.code,
    };
  }

  const reservationDetails: BookingDataReservationDetailsProps = createReservationDetails(
    firstRoom.roomStay?.arrivalDate,
    firstRoom.roomStay?.departureDate,
    bkngData?.bookingInformation?.currencyCode,
    bkngData?.bookingInformation?.reservationByIdList,
    noNights
  );

  const bookingSummaryData: BookingSummaryDataProps = getBookingSummaryData({
    hiData,
    bkngData,
    selectedDonation,
    termsAndConditionsData,
    onclickBillingFormHandler,
    onSubmitBtnText:
      selectedPaymentDetail?.type === PiCardType.RESERVE_WITHOUT_CARD
        ? t('ccui.payment.confirmBooking.button')
        : t('terms.continueText.paymentDetails'),
    firstRoom,
    noNights,
    selectedMeals,
    adultsMeals,
    childrenMeals,
    roomSelection,
    paymentStepState,
    updatedTotalCost: getTotalCost(
      bookingInformation?.ratePlanCode,
      bookingInformation?.totalCost,
      reservationDetails?.currency,
      savedDonation,
      donationsData?.donations?.donationPackages,
      numberOfPackages > 1 ? getSelectedDonation() : selectedDonation,
      Area.PI
    ),
    rateDescription: basketDetailsState?.rateDescription || '',
    rateTags: basketDetailsState?.rateTags,
    cityTaxTotal: bkngData?.bookingInformation?.cityTaxTotal || '',
  });

  const [formData] = useLocalStorage('formDetails', INITIAL_GUEST_DETAILS_FORM_DATA);
  const billing =
    !isRemovePIIDataFromLocalStorageEnabled && formData?.billing?.differentBillingAddress
      ? formData.billing
      : bkngData?.bookingInformation?.reservationByIdList[0]?.billing;

  const [billingAddress, setBillingAddress] = useState<AddressGuestInput | undefined>(
    billing?.address || undefined
  );

  const currentLang = language;

  const hotelBrand = hiData?.hotelInformation?.brand;
  const isGermanHotel = [HotelBrand.PID].includes(hotelBrand);

  const isBillingAddressDisplayed = getIsBillingAddressDisplayed({
    selectedPaymentType,
    hiData,
    selectedPaymentDetail,
    formData,
  });

  const isDonationsDisplayed = getIsDonationsDisplayed({ currentLang, hiData });
  const isDonationInfoBoxDisplayed = getIsDonationInfoBoxDisplayed({
    currentLang,
    paymentStepState,
    hiData,
    paymentSteps,
    donationsData,
  });

  const areBusinessAllowancesDisplayed =
    (selectedPaymentType?.type === 'SAVED_CARD' && selectedPaymentType?.name === 'PIBA') ||
    selectedPaymentType?.type === 'NEW_PIBA';

  const updateIframeHeight =
    selectedPaymentType?.type === PiCardType.NEW_CARD ||
    selectedPaymentType?.type === PiCardType.NEW_PIBA;

  const isPibaCard = (selectedPaymentType: any) => {
    if (areBusinessAllowancesDisplayed) {
      const selectedCardSubType = selectedPaymentType?.subType;
      const pibaCardSubTypes: Record<string, string> = {
        PIBAGB: 'gbp',
        PIBADE: 'eur',
      };

      return Object.keys(pibaCardSubTypes).includes(selectedCardSubType)
        ? pibaCardSubTypes[selectedCardSubType]
        : false;
    }
  };
  useEffect(() => {
    if (disablePaymentOptions) {
      const paymentOutageData = {
        paymentCardSelected: PaymentType.RESERVE_WITHOUT_CARD,
        paymentOutage: disablePaymentOptions,
        cardType: PaymentType.RESERVE_WITHOUT_CARD,
      };
      analytics.update({ ...paymentOutageData });
      setConfAnalytics({ ...paymentOutageData });
    }
  }, [disablePaymentOptions]);
  useEffect(() => {
    if (areBusinessAllowancesDisplayed && paymentAuth) {
      setConfAnalytics({
        ...confAnalytics,
        alcoholAllowed: dinnerAllowance && businessAllowances.isAlcoholDinner,
        carParkingAllowed: businessAllowances.carParking,
        wifiAccessAllowed: businessAllowances.wifi,
        dinnerAllowance: dinnerAllowance,
        otherChargesAllowed: false,
        pageName: 'booking payment',
      });
      analytics.update({
        alcoholAllowed: dinnerAllowance && businessAllowances.isAlcoholDinner,
        carParkingAllowed: businessAllowances.carParking,
        wifiAccessAllowed: businessAllowances.wifi,
        dinnerAllowance: dinnerAllowance,
        otherChargesAllowed: false,
        pageName: 'booking payment',
      });
    } else {
      setConfAnalytics({
        ...confAnalytics,
        alcoholAllowed: false,
        carParkingAllowed: false,
        wifiAccessAllowed: false,
        dinnerAllowance: false,
        otherChargesAllowed: false,
        pageName: 'booking payment',
      });
      analytics.update({
        alcoholAllowed: false,
        carParkingAllowed: false,
        wifiAccessAllowed: false,
        dinnerAllowance: false,
        otherChargesAllowed: false,
        pageName: 'booking payment',
      });
    }
    const pibaCardType = isPibaCard(selectedPaymentType);
    if (pibaCardType) {
      setConfAnalytics({
        ...confAnalytics,
        piba: pibaCardType,
      });
      analytics.update({
        piba: pibaCardType,
      });
    } else if (window.analyticsData) {
      analytics.remove(['piba']);
      setConfAnalytics({
        ...confAnalytics,
        piba: '',
      });
    }
  }, [
    areBusinessAllowancesDisplayed,
    paymentAuth,
    dinnerAllowance,
    businessAllowances,
    selectedPaymentType,
  ]);

  const toggleDinnerAllowance = () => {
    setDinnerAllowance(!dinnerAllowance);
  };

  const { paymentInfoMessages } = paymentInfoMessageData || {};
  const listOfImportantMessagesHotel: string[] = getImportantMessages(
    hiData?.hotelInformation?.importantInfo?.infoItems,
    firstRoom?.roomStay?.arrivalDate,
    firstRoom?.roomStay?.departureDate
  );

  const orderedListOfMessagesPaymentType: { index: number; messagesNotif: string }[] = [];
  const listOfImportantMessagesPaymentType: string[] = paymentInfoMessages?.length // array empty/undefiend => false
    ? paymentInfoMessages
        .filter((objMsg: MessagesPaymentType) => objMsg.paymentType === selectedPaymentDetail?.type)
        .map((objMsg: MessagesPaymentType) => objMsg.messages)
        .flat()
    : [];

  listOfImportantMessagesPaymentType?.forEach((item: string, indexEach: number) => {
    orderedListOfMessagesPaymentType.push({ index: indexEach, messagesNotif: item });
  });

  const infoMessages = [
    ...(listOfImportantMessagesHotel?.length > 0 ? [...listOfImportantMessagesHotel] : []),
  ];
  const orderedInfoMessages: { infoMsg: string; indexOrder: number }[] = [];

  infoMessages.forEach((infoMsg: string, indexOrder: number) => {
    orderedInfoMessages.push({ indexOrder, infoMsg });
  });

  const errorData = {
    isErrorInitiatePaymentMutation,
    initiatePaymentMutationError,
    isErrorInitiatePaypalPaymentMutation,
    initiatePaypalPaymentMutationError,
    currentReasonForStay,
    hotelBrand,
    t,
  };

  const displayPaymentFailedError = paymentFailedValue === PAYMENT_FAILED_VALUE;
  useEffect(() => {
    if (displayPaymentFailedError) {
      analytics.update({
        paymentDecline: true,
        declineReasonCode: paymentFailureCode,
      });
    } else {
      analytics.update({
        paymentDecline: false,
        declineReasonCode: '',
      });
    }
  }, [displayPaymentFailedError, paymentFailureCode]);
  let paymentFailedErrorMessage;
  if (
    t(`errors.payment.${paymentFailureDescription}`) !==
    `errors.payment.${paymentFailureDescription}`
  ) {
    paymentFailedErrorMessage = t(`errors.payment.${paymentFailureDescription}`);
  } else {
    paymentFailedErrorMessage = t('errors.confirmation.generic');
  }

  const errorMessagePayment = displayPaymentFailedError
    ? paymentFailedErrorMessage
    : getPaymentError(errorData);

  const resetMutations = () => {
    if (isErrorInitiatePaymentMutation) initiatePaymentMutation.reset();
    if (isErrorInitiatePaypalPaymentMutation) initiatePaypalPaymentMutation.reset();
  };

  const getSelectedStatus = (allowance: any, availableAllowances: any) => {
    if (!paymentAuth || !areBusinessAllowancesDisplayed) {
      return false;
    }
    if (allowance.key === 'alcohol') {
      const dinnerAllowanceSelected = availableAllowances.find(
        (item: any) => item.key === 'dinner'
      )?.isSelected;
      if (!dinnerAllowanceSelected) {
        return false;
      }
    }
    return !!allowance.isSelected;
  };

  const updateSelectedPaymentOption = () => {
    if (selectedPaymentType?.name === PaymentType.RESERVE_WITHOUT_CARD) {
      return PaymentType.RESERVE_WITHOUT_CARD;
    }

    return selectedPaymentType.type === APGP_PAYMENT ? WALLET_APPLE : selectedPaymentType.name;
  };

  const getPaymentParams = ({
    billingAddress,
    paymentType,
    paypalNonce,
    paypalDeviceData,
  }: GetPaymentParamsProps) => {
    let bookingForSomeoneElse = false;
    if (isRemovePIIDataFromLocalStorageEnabled) {
      const defaultDataFromBooking = getDefaultDataFromBooking(
        bkngData?.bookingInformation?.reservationByIdList ?? [],
        {},
        basketReference as string,
        currentLang,
        hotelBrand,
        isGermanHotel,
        true
      );
      bookingForSomeoneElse = defaultDataFromBooking?.bookingForSomeoneElse;
    } else {
      bookingForSomeoneElse = formData?.bookingForSomeoneElse;
    }
    const { hotelId } = bookingInformation;
    const billingObj = {
      ...billing,
      landline: undefined, //for now createPaymentMutation does not accept landline in billing
      address: getBillingAddress({
        countryRouter: country,
        isBillingAddressDisplayed,
        billingAddress,
        billing,
      }),
      differentBillingAddress: isRemovePIIDataFromLocalStorageEnabled
        ? false
        : formData?.billing?.differentBillingAddress,
      bookerIsNotGuest: bookingForSomeoneElse,
    };

    let card;
    if (selectedPaymentType?.card) {
      const {
        logoSrc: logoUrl,
        cardHolderName: cardholderName,
        ...cardDetails
      } = selectedPaymentType.card;
      card = Object.keys(cardDetails).reduce<{ [key: string]: string | boolean | undefined }>(
        (prev, key) => {
          if (!['cardNumber', 'cardName'].includes(key)) {
            prev[key] = cardDetails[key as keyof typeof cardDetails] as string;
          }
          return prev;
        },
        { logoUrl, cardholderName }
      );
    }

    const bookingRequest = {
      businessSite: {
        identifier: hotelId,
        name: bookingSummaryData.hotelInformation?.hotelName,
        type: 'HOTEL',
        location: hotelId,
      },
      channel: 'PI',
      journey: 'BOOKING',
      language,
      rooms,
      arrivalDate: bookingSummaryData?.stayDatesInformation?.arrivalDate,
      departureDate: bookingSummaryData?.stayDatesInformation?.departureDate,
      type:
        selectedPaymentType?.name === PaymentType.RESERVE_WITHOUT_CARD
          ? PaymentType.RESERVE_WITHOUT_CARD
          : selectedPaymentDetail?.type,
    };

    function mapBusinessAllowances(businessAllowances: BusinessAllowance) {
      const availableAllowances = [
        {
          key: 'dinner',
          isSelected: dinnerAllowance,
        },
        {
          key: 'alcohol',
          isSelected: businessAllowances.isAlcoholDinner,
        },
        {
          key: 'carParking',
          isSelected: businessAllowances.carParking,
        },
        {
          key: 'ultimateWifi',
          isSelected: businessAllowances.wifi,
        },
        // decision is yet to be made regarding where this field should be saved, until then it will be hidden
        // {
        //   key: 'otherCharges',
        //   isSelected: businessAllowances.additionalCharges,
        // },
      ];

      return availableAllowances.map((allowance) => ({
        budget:
          allowance.key === 'dinner'
            ? getPriceValueWithDecimal(businessAllowances.totalDinnerBudgetPersonNight?.toString())
            : 0.0, // Only dinner allowance has a budget
        allowance: allowance.key,
        isAuthorised: getSelectedStatus(allowance, availableAllowances),
      }));
    }
    const businessItemsRequest = areBusinessAllowancesDisplayed
      ? {
          businessItems: {
            purchaseOrderNumber: referenceDetails.purchaseOrderNumber,
            customReferenceNumber: referenceDetails.reference,
            businessAllowances: mapBusinessAllowances(businessAllowances),
          },
        }
      : {};

    const paymentRequest = {
      billing: billingObj,
      card,
      environment: window.location.origin,
      subType: PAYPAL_PAYMENT === paymentType ? 'MIT' : 'ECOMM',
      type: updateSelectedPaymentOption(),
      pibaCardPresent: !areBusinessAllowancesDisplayed || !paymentAuth,
      ...businessItemsRequest,
      ...(PAYPAL_PAYMENT === paymentType && {
        paypalNonce,
        paypalDeviceData,
      }),
    };

    const createPaymentCriteria = {
      booking: bookingRequest,
      payment: paymentRequest,
      charityPackageCode: selectedDonation?.code,
      hotelId: hotelId,
      requestId: uuidv4(),
    };
    // if url has querystring secure-booking then set isSecureBooking to true
    if (isSecureBookingPage(router?.query as secureBookingType, isSecureBookingFeatureEnabled)) {
      return { ...createPaymentCriteria, isSecureBooking: true };
    }
    return { ...createPaymentCriteria };
  };

  const [isPaymentAuthError, setIsPaymentAuthError] = useState<boolean>(false);

  const continueToNextStep = (billingAddress?: AddressGuestInput) => {
    if (hasReferenceErrors && selectedPaymentType.name === 'PIBA') {
      return false;
    }

    if (areBusinessAllowancesDisplayed && paymentAuth && paymentPassword.length === 0) {
      setIsPaymentAuthError(true);
      return false;
    }

    resetMutations();
    initiatePaymentMutation.mutate({
      basketReference: basketReference,
      createPaymentCriteria: getPaymentParams({ billingAddress }),
    });
  };

  const handleRedirection = () => {
    if (
      isSecureBookingPage(router?.query as secureBookingType, isSecureBookingFeatureEnabled) ||
      router.query[PAYMENT_NAVIGATION_SOURCE_QUERY_PARAM] ===
        PAYMENT_NAVIGATION_SOURCE_GUEST_DETAILS
    ) {
      router.back();
    } else {
      router.push(
        `${country}/${language}${
          bkngData?.bookingInformation?.bookingFlowId
            ? `/${bkngData?.bookingInformation?.bookingFlowId}`
            : ''
        }/guest-details?reservationId=${basketReference}`
      );
    }
  };

  const handleBackClick = () => {
    if (paymentStepState === paymentSteps.CARD_DETAILS) {
      setPaymentStepState(paymentSteps.PAYMENT_DETAILS);
      window.scrollTo(0, 0);
    } else {
      handleRedirection();
      setPaymentFailedValue(PAYMENT_FAILED_INITIAL_VALUE);
      setPaymentFailureCode(PAYMENT_FAILURE_CODE_INITIAL_VALUE);
      setPaymentFailureDescription(PAYMENT_FAILURE_DESCRIPTION_INITIAL_VALUE);
    }
  };

  const onIframeLoad = (time: string) => {
    analytics.track('paymentPage2');
    setConfAnalytics({
      ...confAnalytics,
      paymentLoadTime: time,
    });
    analytics.update({
      paymentLoadTime: time,
    });
  };

  const goToConfirmationPage = () => {
    if (hasReferenceErrors && selectedPaymentType.name === 'PIBA') {
      return false;
    }

    let queryString = `reservationId=${basketReference}`;
    if (isSecureBookingPage(router?.query as secureBookingType, isSecureBookingFeatureEnabled)) {
      queryString = `reservationId=${basketReference}&secure-booking=true`;
    }

    router
      .push(
        `${country}/${language}${
          bkngData?.bookingInformation?.bookingFlowId
            ? `/${bkngData?.bookingInformation?.bookingFlowId}`
            : ''
        }/confirmation?${queryString}`
      )
      // eslint-disable-next-line no-console
      .catch((error) => console.log(error));
  };
  const initiatePaymentStatus = initiatePaymentMutationData?.initiatePayment?.status;

  useEffect(() => {
    setConfAnalytics({
      ...confAnalytics,
      basketReference,
    });
  }, [paymentStatus, initiatePaymentStatus]);

  useEffect(() => {
    if (isSuccess) {
      if (initiatePaymentStatus === BASKET_STATUS.PAYMENT_REQUIRED) {
        setPaymentStepState(paymentSteps.CARD_DETAILS);
        const { sessionId, template } =
          initiatePaymentMutationData?.initiatePayment?.paymentRequiredDetails || {};
        setConfAnalytics({
          ...confAnalytics,
          paymentSessionID: sessionId,
          paymentTemplateID: template,
          cardNotPresent: false,
        });
        analytics.update({
          paymentSessionID: sessionId,
          paymentTemplateID: template,
          cardNotPresent: false,
          funnel_step: formatAnalyticsFunnelStep('PI', 'PaymentDetails2', language),
        });
      } else if (initiatePaymentStatus === BASKET_STATUS.NOT_REQUIRED) {
        setConfAnalytics({
          ...confAnalytics,
          cardNotPresent: true,
        });
        analytics.update({
          cardNotPresent: true,
        });
      }
    }
  }, [isSuccess, initiatePaymentMutationData]);

  useEffect(() => {
    if (initiatePaymentStatus === BASKET_STATUS.NOT_REQUIRED) {
      goToConfirmationPage();
    }
  }, [initiatePaymentMutationData]);

  /** this effect checks for payment status from the provider (now: 3CP). Upon isPaymentComplete, redirects to confirmation page by constructing the url */
  useEffect(() => {
    if (isPaymentComplete) {
      setPaymentFailedValue(PAYMENT_FAILED_INITIAL_VALUE);
      setPaymentFailureCode(PAYMENT_FAILURE_CODE_INITIAL_VALUE);
      setPaymentFailureDescription(PAYMENT_FAILURE_DESCRIPTION_INITIAL_VALUE);
      goToConfirmationPage();
    }
  }, [isPaymentComplete]);

  useEffect(() => {
    if (cardType) {
      setConfAnalytics({
        ...confAnalytics,
        cardType,
      });
      analytics.update({ cardType });
      analyticsConfirmation.update({ cardType });
    }
  }, [cardType]);

  useEffect(() => {
    if (isSecureBookingPage(router?.query as secureBookingType, isSecureBookingFeatureEnabled)) {
      updateDashboardAnalytics({ secureBookingAction: true });
    }
  }, [router?.query]);

  useEffect(() => {
    if (
      isPaypalSuccess &&
      initiatePaypalPaymentMutationData?.initiatePaypalPayment?.status === 'NOT_REQUIRED'
    ) {
      setPaymentFailedValue(PAYMENT_FAILED_INITIAL_VALUE);
      setPaymentFailureCode(PAYMENT_FAILURE_CODE_INITIAL_VALUE);
      setPaymentFailureDescription(PAYMENT_FAILURE_DESCRIPTION_INITIAL_VALUE);
      setConfAnalytics({
        ...confAnalytics,
        paypal: true,
        paymentTakenNow: (selectedPaymentDetail?.type === paymentOptions.PAY_NOW).toString(),
      });
      analytics.update({
        paypal: true,
        paymentTakenNow: (selectedPaymentDetail?.type === paymentOptions.PAY_NOW).toString(),
      });
      goToConfirmationPage();
    }
  }, [isPaypalSuccess, initiatePaypalPaymentMutationData]);

  const handlePaypalPayment = async (nonce: string) => {
    setPaymentFailedValue(PAYMENT_FAILED_INITIAL_VALUE);
    setPaymentFailureCode(PAYMENT_FAILURE_CODE_INITIAL_VALUE);
    setPaymentFailureDescription(PAYMENT_FAILURE_DESCRIPTION_INITIAL_VALUE);
    resetMutations();
    const deviceData = await getPaypalDeviceData(paypalPaymentData?.clientToken as string);
    const createPaymentCriteria = getPaymentParams({
      billingAddress,
      paymentType: PAYPAL_PAYMENT,
      paypalNonce: nonce,
      paypalDeviceData: deviceData,
    });
    initiatePaypalPaymentMutation.mutate({
      basketReference: basketReference,
      createPaymentCriteria,
    });
  };

  const handlePaymentTypeSection = (value: SetStateAction<PaymentMethod>) => {
    const paymentOptions = value as PaymentMethod;
    //in case of secure booking, apply payment options restrictions
    applyDefaultPaymentRestrictions(
      paymentOptions,
      PaymentType,
      router?.query,
      isSecureBookingFeatureEnabled
    );
    const selectedPaymentOption = paymentOptions?.paymentOptions?.filter(
      (option) => option?.enabled && option.type !== selectedPaymentDetail?.type
    );
    setNotifyPaymentMethodChange(
      !!selectedPaymentOption?.length &&
        (PAYPAL_PAYMENT === paymentOptions.type || APGP_PAYMENT === paymentOptions.type)
    );
    setSelectedPaymentType(value);
  };

  const paypalOptionsData: PaypalWBProps = useMemo(() => {
    const { createBillingAgreement, onApprove, onError } = getPaypalOptionsParams({
      currencyCode: bkngData?.bookingInformation?.currencyCode,
      approveCallBack: handlePaypalPayment,
      errorCallBack: setPaymentFailedValue,
    });

    return {
      style: {
        layout: 'vertical',
        shape: 'rect',
        label: 'pay',
      },
      disabled: false,
      currency: `${bkngData?.bookingInformation?.currencyCode}`,
      onApprove,
      onError,
      createBillingAgreement,
    };
  }, [
    bkngData?.bookingInformation?.currencyCode,
    paypalPaymentData?.clientToken,
    handlePaypalPayment,
  ]);

  const isLoading = logicalOrOperator(
    isLoadingHotelInformation,
    isLoadingBookingInformation,
    isLoadingPaymentPcks,
    isLoadingPaymentPcks,
    isLoadingPaymentInfoMessage,
    isLoadingTermsAndConditions,
    isPaypalLoading,
    isPaypalSuccess,
    logicalAndOperator(country === 'gb', isLoadingDonations)
  );

  if (isLoading) {
    return (
      <Flex {...loadingStyle}>
        <LoadingSpinner loadingText={t('booking.loading')} />
      </Flex>
    );
  }

  function onclickBillingFormHandler() {
    if (!isBillingAddressDisplayed) {
      continueToNextStep();
    }
  }

  const renderPaymentDetails = () => {
    return (
      <PaymentDetails
        selectedPaymentType={selectedPaymentType}
        selectedPaymentDetail={selectedPaymentDetail}
        setSelectedPaymentDetail={setSelectedPaymentDetail}
        errorMessagePayment={errorMessagePayment}
        t={t}
        hotelBrand={hotelBrand}
        disablePaymentOptions={disablePaymentOptions}
        isPaymentRedesignEnabled={isPaymentRedesignEnabled}
      />
    );
  };

  const renderPaymentContainer = () => {
    return (
      <PaymentTypeContainer
        selectedPaymentDetail={selectedPaymentDetail}
        selectedPaymentType={selectedPaymentType}
        onPaymentTypeClick={handlePaymentTypeSection}
        userType={UserType.Leisure}
        variant={Area.PI}
        isPaymentRedesignEnabled={isPaymentRedesignEnabled}
      />
    );
  };

  const renderPaymentMethods = () => {
    return isSwapPaymentOptionsEnabled ? (
      <>
        {selectedPaymentDetail?.type !== PiCardType.RESERVE_WITHOUT_CARD &&
          renderPaymentContainer()}
        {renderPaymentDetails()}
      </>
    ) : (
      <>
        {renderPaymentDetails()}
        {selectedPaymentDetail?.type !== PiCardType.RESERVE_WITHOUT_CARD &&
          renderPaymentContainer()}
      </>
    );
  };

  return (
    <>
      <Script
        src={publicRuntimeConfig.NEXT_PUBLIC_APPLEPAY_SCRIPT_URL}
        strategy="lazyOnload"
      ></Script>
      <SEO
        page={PageName.PAYMENT}
        hotelId={bkngData.bookingInformation.hotelId}
        bookingFlowId={bkngData.bookingInformation.bookingFlowId}
        noIndexNoFollow={true}
      />
      <Grid data-testid="paymentPageSection" {...mainPaymentGridStyle}>
        <GridItem {...bookingSummaryMobileContainerStyle}>
          <Flex {...bookingSummaryMobileTriggerStyle}>
            <BookingSummary
              variant="mobile"
              t={t}
              language={currentLang}
              bookingSummaryData={bookingSummaryData}
              reservationDetails={reservationDetails}
              infoMessages={infoMessages}
              taxesMessage={cityTaxMessages?.summaryText}
              isExtrasDisplayed={!!packages?.extrasItems}
              isCityTaxBreakdownEnabled={isCityTaxBreakdownEnabled}
            />
          </Flex>
        </GridItem>

        {isPaymentComplete ? (
          <LoadingSpinner loadingText={t('booking.loading')} {...partialLoadingStyle} />
        ) : (
          <GridItem data-testid="paymentPageSection_content" {...pageContentStyle}>
            {/* Back to payment page 1 - payment details - top link */}
            {isBackToPaymentOptionsLinkEnabled &&
              paymentStepState === paymentSteps.CARD_DETAILS && (
                <BackToPage
                  goBack={handleBackClick}
                  linkText={t('booking.backToPaymentMethods.text')}
                />
              )}
            {!isBackToPaymentOptionsLinkEnabled && (
              <Notification
                maxWidth="full"
                variant="info"
                status="info"
                description={
                  <Box
                    className="formatLinks"
                    {...getTypographyProps({}, notificationDescriptionSemanticTypography)}
                  >
                    {renderSanitizedHtml(t('booking.header.notification'))}
                  </Box>
                }
                svg={<Info />}
                wrapperStyles={{ mb: 'xl' }}
              />
            )}

            <Box
              hidden={!isPaymentDetailsStep}
              aria-hidden={!isPaymentDetailsStep}
              pointerEvents={isPaymentDetailsStep ? 'auto' : 'none'}
            >
              {renderPaymentMethods()}

              {notifyPaymentMethodChange && (
                <Box mb="5xl" data-testid="PaypalPaymentType-InfoMessages">
                  <Box mt="sm" key="payment_custom_message">
                    <Notification
                      variant="alert"
                      status="warning"
                      description={
                        <>
                          <Text fontWeight="bold">
                            {t('booking.payment.paynow.notification.title')}
                          </Text>
                          <Text>{t('booking.payment.paynow.notification.message')}</Text>
                        </>
                      }
                      svg={<Alert />}
                      wrapperStyles={paypalPaymentTypeInfoMsgStyle}
                    />
                  </Box>
                </Box>
              )}
              {areBusinessAllowancesDisplayed && (
                <>
                  <ReferenceDetails
                    referenceDetails={referenceDetails}
                    updateReference={setReferenceDetails}
                    setReferenceDetailsErrors={(value: boolean) => setHasReferenceErrors(value)}
                  />

                  {!disablePaymentOptions && (
                    <>
                      <PaymentAuthorization
                        togglePaymentAuth={togglePaymentAuth}
                        paymentAuth={paymentAuth}
                        password={paymentPassword}
                        onChangePassword={setPaymentPassword}
                        isPaymentAuthError={isPaymentAuthError}
                      />

                      {paymentAuth && (
                        <BusinessAllowances
                          reservationDetails={reservationDetails}
                          toggleDinnerAllowance={toggleDinnerAllowance}
                          dinnerAllowance={dinnerAllowance}
                          paymentHasError={isErrorInitiatePaymentMutation}
                          businessAllowances={businessAllowances}
                          setBusinessAllowances={setBusinessAllowances}
                          hasBusinessAllowanceErrorState={[
                            hasBusinessAllowanceError,
                            setHasBusinessAllowanceError,
                          ]}
                          hasAncillariesWifiSelected={hasAncillariesWifiSelected}
                        />
                      )}
                    </>
                  )}
                </>
              )}
              {isBillingAddressDisplayed && (
                <BillingAddress
                  currentBillingAddress={billing?.address}
                  continueToNextStep={continueToNextStep}
                  setBillingAddress={setBillingAddress}
                  t={t}
                  currentLang={currentLang}
                  hasError={hasError}
                  isCompanyNameAdvanceEnabled={isCompanyNameAdvanceEnabled}
                  horizontalRadioButtons={isPaymentRedesignEnabled}
                  isCountryAllowTypingEnabled={true}
                />
              )}
              {!isDonationHidden && isDonationsDisplayed && (
                <Donations
                  onDonationChange={setSelectedDonation}
                  selectedDonation={selectedDonation}
                  bookingInformation={bookingInformation}
                  bookingChannel={BOOKING_CHANNEL.PI}
                />
              )}
              <TotalCostPayment
                hotelName={bookingSummaryData.hotelInformation?.hotelName as string}
                hotelId={bookingInformation.hotelId}
                rateCode={bookingInformation.ratePlanCode}
                ratePlan={getTotalCost(
                  bookingInformation?.ratePlanCode,
                  bookingInformation?.totalCost,
                  reservationDetails?.currency,
                  savedDonation,
                  donationsData?.donations?.donationPackages,
                  numberOfPackages > 1 ? getSelectedDonation() : selectedDonation,
                  Area.PI
                )}
                isBillingAddressDisplayed={isBillingAddressDisplayed}
                continueToNextStep={continueToNextStep}
                selectedPaymentDetail={selectedPaymentDetail}
                errorMessagePayment={errorMessagePayment}
                bookingChannel={BOOKING_CHANNEL.PI}
                hasError={false}
                selectedPaymentType={selectedPaymentType}
                paypalOptions={paypalOptionsData}
              />
              <BackToPage
                goBack={handleBackClick}
                linkText={t('booking.submitBox.backToYourDetails')}
              />
            </Box>

            {paymentStepState === paymentSteps.CARD_DETAILS && (
              <>
                <IframeEmbed
                  iframeId="paymentFrame"
                  iframeContent={
                    initiatePaymentMutationData?.initiatePayment?.paymentRequiredDetails
                      ?.paymentRedirect
                  }
                  onIframeLoad={onIframeLoad}
                  providerUrl={
                    initiatePaymentMutationData?.initiatePayment?.paymentRequiredDetails
                      ?.providerUrl
                  }
                  updateIframeHeight={updateIframeHeight}
                />
                {isBackToPaymentOptionsLinkEnabled && (
                  <BackToPage
                    goBack={handleBackClick}
                    linkText={t('booking.backToPaymentMethods.text')}
                  />
                )}
              </>
            )}

            {isDonationInfoBoxDisplayed && (
              <DonationsInfoBox informationBox={donationsData?.donations?.informationBox} />
            )}

            <Box {...securityContainerStyle}>
              <DataSecuritySection
                privacyPolicy={privacyPolicyData}
                prefixDataTestId={'PaymentPage'}
                containerStyle={{ mt: 'xl' }}
              />
            </Box>
          </GridItem>
        )}
        <GridItem {...bookingSummaryDesktopStyle}>
          <BookingSummary
            variant="desktop"
            t={t}
            language={currentLang}
            bookingSummaryData={bookingSummaryData}
            reservationDetails={reservationDetails}
            taxesMessage={cityTaxMessages?.summaryText}
            isExtrasDisplayed={!!packages?.extrasItems}
            isCityTaxBreakdownEnabled={isCityTaxBreakdownEnabled}
          />

          {paymentStepState === paymentSteps.PAYMENT_DETAILS && (
            <>
              <Box
                sx={termsAndConditionsStyle}
                data-testid="termsAndConditionsPayment"
                className="formatLinks"
              >
                {renderSanitizedHtml(
                  formatUrlTermsConditions(termsAndConditionsData?.termsAndConditions?.text)
                )}
              </Box>

              {PAYPAL_PAYMENT === selectedPaymentType?.name && (
                <PaypalWBButton {...paypalOptionsData} />
              )}
            </>
          )}

          <Box pt="sm" data-testid="BookingSummary-InfoMessages">
            {orderedInfoMessages?.map((item) => {
              if (item.infoMsg) {
                return (
                  <Box mt="md" key={item.indexOrder}>
                    <Notification
                      maxWidth="full"
                      variant="info"
                      status="info"
                      description={
                        <Box className="formatLinks">{renderSanitizedHtml(item.infoMsg)}</Box>
                      }
                      svg={<Info />}
                    />
                  </Box>
                );
              }
            })}
          </Box>
          {!!listOfImportantMessagesPaymentType?.length && (
            <Box mb="5xl" data-testid="PaymentType-InfoMessages">
              {orderedListOfMessagesPaymentType.map((item) => (
                <Box mt="md" key={item.index}>
                  <Notification
                    variant="info"
                    status="info"
                    description={<Box>{renderSanitizedHtml(item.messagesNotif)}</Box>}
                    svg={<Info />}
                    wrapperStyles={paymentTypeInfoMsgStyle}
                  />
                </Box>
              ))}
            </Box>
          )}
          {displayPaymentFailedError && paymentStepState === paymentSteps.PAYMENT_DETAILS && (
            <Box pt="lg">
              <Notification
                prefixDataTestId="Payment-Failed-Error"
                variant="error"
                status="error"
                description={<Box>{renderSanitizedHtml(paymentFailedErrorMessage)}</Box>}
                svg={<Info color="var(--chakra-colors-error)" />}
              />
            </Box>
          )}
        </GridItem>
      </Grid>
    </>
  );
}

export default function PaymentPagePi(props: Readonly<Props>) {
  const { basketReference } = props;
  const { language, country } = useCustomLocale();

  const { injectPaypalProvider, paypalPaymentData } = usePaymentPaypal(
    basketReference ?? '',
    Area.PI.toUpperCase()
  );

  return (
    <>
      {injectPaypalProvider ? (
        <PayPalScriptProvider
          options={{
            clientId: paypalPaymentData?.clientId as string,
            dataUserIdToken: paypalPaymentData?.clientToken as string,
            intent: 'tokenize',
            vault: true,
            locale: `${language}_${country.toString().toUpperCase()}`,
            components: 'buttons',
          }}
        >
          <PIPageContent paypalPaymentData={paypalPaymentData} {...props} />
        </PayPalScriptProvider>
      ) : (
        <PIPageContent {...props} />
      )}
    </>
  );
}

const mainPaymentGridStyle = {
  w: 'full',
  maxW: 'var(--chakra-space-breakpoint-xl)',
  px: {
    mobile: '0px',
    lg: '7',
    xl: '5xl',
  },
  pb: {
    mobile: 'md',
    sm: '5',
    md: 'lg',
    lg: '7',
    xl: '5xl',
  },
  pt: {
    mobile: '0px',
    lg: '5xl',
  },
  m: '0px',
  templateColumns: {
    mobile: '1fr',
    lg: '1fr auto',
  },
  columnGap: {
    mobile: '0px',
    lg: '32',
    xl: '8.5rem',
  },
} as GridProps;

const pageContentStyle = {
  minW: 0,
  px: {
    mobile: 'md',
    sm: '5',
    md: 'lg',
    lg: '0px',
  },
  pt: {
    mobile: 'lg',
    sm: 'xl',
    md: '2xl',
    lg: '0px',
  },
} as GridItemProps;

const bookingSummaryMobileContainerStyle = {
  display: {
    mobile: 'block',
    lg: 'none',
  },
  backgroundColor: 'lightGrey5',
} as GridItemProps;

const bookingSummaryMobileTriggerStyle = {
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
    lg: '72',
    xl: '19.3125rem',
  },
} as GridItemProps;

const paymentTypeInfoMsgStyle = {
  w: {
    mobile: 'full',
    xs: 'full',
    md: '45rem',
    lg: '50.5rem',
    xl: 'full',
  },
} as BoxProps;

const paypalPaymentTypeInfoMsgStyle = {
  w: {
    mobile: 'full',
    xs: 'full',
    sm: '26.3rem',
    md: '27.563rem',
    lg: '24.5rem',
    xl: '26.25rem',
  },
  mt: '-2xl',
} as BoxProps;

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

const partialLoadingStyle = {
  height: '100%',
  width: '100%',
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

const securityContainerStyle = {
  w: { mobile: 'full', md: '45rem', lg: '50.5rem', xl: '54rem' },
};

const termsAndConditionsStyle = {
  mt: 'lg',
  p: {
    fontSize: 'md',
    fontWeight: 'normal',
    lineHeight: '3',
  },
  a: {
    textDecoration: 'underline',
    color: 'zipSecondary',
  },
};

const notificationDescriptionSemanticTypography = {
  textStyle: 'body-s-regular',
} as const;
