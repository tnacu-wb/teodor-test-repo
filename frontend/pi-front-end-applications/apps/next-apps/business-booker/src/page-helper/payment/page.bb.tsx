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
  BCReservationListItem,
  BOOKING_CHANNEL,
  BookingDataReservationDetailsProps,
  BookingSummaryDataProps,
  BusinessAllowance,
  GET_BOOKING_INFORMATION,
  GET_HOTEL_INFORMATION,
  GET_PAYMENT_INFO_MESSAGES_QUERY,
  GET_PAYMENT_METHODS_QUERY_BB,
  GET_TERMS_AND_CONDITIONS_QUERY,
  QueryHotelInformationArgs,
  INITIATE_PAYMENT_MUTATION,
  INITIATE_PAYPAL_PAYMENT_MUTATION,
  MessagesPaymentType,
  PageName,
  PaymentMethod,
  paymentOptions as PaymentType,
  MealItem,
  MealKids,
  PrivacyPolicy,
  PackagesCriteria,
  SelectedMealsPerRoom,
  UserType,
  paymentSteps,
  Area,
  Questions,
  ReferencesQuestions,
  CompanyReferencesQuestions,
  PAYMENT_FAILED_KEY,
  PAYMENT_FAILED_INITIAL_VALUE,
  PAYMENT_FAILED_VALUE,
  PAYMENT_FAILURE_CODE_KEY,
  PAYMENT_FAILURE_CODE_INITIAL_VALUE,
  PAYMENT_FAILURE_DESCRIPTION_KEY,
  PAYMENT_FAILURE_DESCRIPTION_INITIAL_VALUE,
  PiCardType,
  APGP_PAYMENT,
  WALLET_APPLE,
  PaymentAnalytics,
  PAYMENT_ANALYTICS_KEY,
  PAYPAL_PAYMENT,
  PaymentMethods,
  PaymentOption,
  PackageDetails,
  BusinessCardType,
  FT_PI_BB_CCUI_COMPANY_NAME_SPECIAL_CHARACTERS,
  FT_PI_BB_CCUI_DISABLE_PAYMENTS,
  FT_BB_ENABLE_PAYMENT_REDESIGN,
  FT_PI_BB_NON_GUARANTEED_REMINDER,
  BASKET_DETAILS_STORAGE_KEY,
  BASKET_DETAILS_STATE_INITIAL_VALUE,
  Customer,
  CompanyDetailsResponse,
  FT_PI_PIB_SWAP_PAYMENT_OPTIONS,
  FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN_ANCILLARIES_GDP_PAYMENT,
  FT_PI_PIB_PAYMENT_BACK_TO_OPTIONS_LINK,
  WIFI_IDS,
} from '@whitbread-eos/api';
import {
  Alert,
  Button,
  IframeEmbed,
  Info,
  LoadingSpinner,
  Notification,
  PaypalWBButton,
  PaypalWBProps,
} from '@whitbread-eos/atoms';
import {
  BusinessAllowances,
  DataSecuritySection,
  PaymentAuthorization,
  PaymentDetails,
  NoPaymentMethodsNotification,
  BackToPage,
  SEO,
  EmployeeQuestions,
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
  getImportantMessages,
  formatUrlTermsConditions,
  getAuthCookie,
  getBillingAddress,
  getBookingSummaryData,
  getCityTaxMessages,
  getIsBillingAddressDisplayed,
  getMaxValueFromRoomStays,
  getNightsNumber,
  getPaymentError,
  logicalOrOperator,
  mealsMapperSelector,
  securityNoticeMoreInfoDataSelector,
  useCustomLocale,
  useIPageSubmission,
  useMutationRequest,
  useQueryRequest,
  usePackages,
  getIsBBCardDetailsDisplayed,
  getPriceValueWithDecimal,
  useUserData,
  useUserDetails,
  useCompanyDetails,
  getLoggedInUserInfo,
  setBusinessAllowancesSections,
  createReservationDetails,
  getTotalCost,
  updateAncillariesAnalytics,
  useSessionStorage,
  getPaypalDeviceData,
  getPaypalOptionsParams,
  usePaymentPaypal,
  useFeatureToggle,
  renderSanitizedHtml,
  useSemanticTypography,
  getCentrallyStoredCardBillingAddress,
  formatBillingAddress,
  applyDefaultPaymentRestrictions,
  isSecureBookingPage,
  type secureBookingType,
  useLocalStorage,
  formatAnalyticsFunnelStep,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import getConfig from 'next/config';
import { useRouter } from 'next/router';
import Script from 'next/script';
import { useEffect, useState, useMemo, SetStateAction } from 'react';
import { v4 as uuidv4 } from 'uuid';

import {
  PAYMENT_NAVIGATION_SOURCE_GUEST_DETAILS,
  PAYMENT_NAVIGATION_SOURCE_QUERY_PARAM,
} from '../../utils/bb-all-pages-constants';

interface Props {
  hiQueryInput: QueryHotelInformationArgs;
  pcksQueryInput: PackagesCriteria;
  basketReference: string | null;
  paypalPaymentData?: PaymentMethod | null;
  userDetails?: Customer;
  companyDetails?: CompanyDetailsResponse;
}
interface GetPaymentParamsProps {
  billingAddress?: AddressGuestInput;
  paymentType?: string;
  paypalNonce?: string;
  paypalDeviceData?: any;
}

export function BBPageContent({
  pcksQueryInput,
  hiQueryInput,
  basketReference,
  paypalPaymentData,
  userDetails,
  companyDetails,
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
    [FT_BB_ENABLE_PAYMENT_REDESIGN]: isPaymentRedesignEnabled,
    [FT_PI_BB_CCUI_COMPANY_NAME_SPECIAL_CHARACTERS]: isCompanyNameAdvanceEnabled,
    [FT_PI_BB_CCUI_DISABLE_PAYMENTS]: disablePaymentOptions,
    [FT_PI_BB_NON_GUARANTEED_REMINDER]: isSecureBookingFeatureEnabled,
    [FT_PI_PIB_SWAP_PAYMENT_OPTIONS]: isSwapPaymentOptionsEnabled,
    [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN_ANCILLARIES_GDP_PAYMENT]: isCityTaxBreakdownEnabled,
    [FT_PI_PIB_PAYMENT_BACK_TO_OPTIONS_LINK]: isBackToPaymentOptionsLinkEnabled,
  } = useFeatureToggle();

  if (typeof window !== 'undefined') {
    analytics.update({
      hasPaymentFailure: undefined,
    });
  }

  const idTokenCookie = getAuthCookie();

  /** [1/2] when transitioning to this page from the previous page, booking information is not yet complete... */
  const {
    isLoading: isLoadingBookingInformation,
    data: bkngData,
    refetch: refetchBookingInfo,
  } = useQueryRequest(
    ['GetBookingInformation', language, country, basketReference],
    GET_BOOKING_INFORMATION,
    {
      basketReference,
      language,
      country,
      bookingChannelCriteria: {
        channel: 'BB',
        subchannel: 'WEB',
        language: language === 'en' ? 'EN' : 'DE',
      },
    },
    { enabled: false }
  );

  // [2/2] ...this will trigger the GetBookingInformation query
  useEffect(() => {
    refetchBookingInfo();
  }, []);

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
    hotelHasCityTaxForLeisure,
    hotelHasCityTaxForBusiness,
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
  const hasWifiSelected = !packages?.extrasItems?.some(
    (item: PackageDetails) =>
      item?.packageCode && WIFI_IDS.includes(item.packageCode as (typeof WIFI_IDS)[number])
  );

  const { isLoading: isLoadingHotelInformation, data: hiData } = useQueryRequest(
    ['GetHotelInformation', hiQueryInput.hotelId, hiQueryInput.country, hiQueryInput.language],
    GET_HOTEL_INFORMATION,
    {
      ...hiQueryInput,
    }
  );

  const userType = UserType.Business;

  const paymentMethodsQueryInput = {
    language,
    country,
    basketReference,
    userType: idTokenCookie ? userType : undefined,
    clientChannel: Area.BB.toUpperCase(),
  };

  const {
    isLoading: isLoadingPaymentOptions,
    data: paymentMthData,
  }: {
    isLoading: boolean;
    data: PaymentMethods;
  } = useQueryRequest(
    ['getPaymentMethods', language, country, basketReference],
    GET_PAYMENT_METHODS_QUERY_BB,
    {
      ...paymentMethodsQueryInput,
    },
    undefined,
    idTokenCookie
  );

  const { isLoading: isLoadingTermsAndConditions, data: termsAndConditionsData } = useQueryRequest(
    [
      'GetTermsAndConditions',
      hiQueryInput.hotelId,
      country,
      language,
      bookingInformation?.ratePlanCode,
      BOOKING_CHANNEL.BB,
    ],
    GET_TERMS_AND_CONDITIONS_QUERY,
    {
      language,
      country,
      hotelId: hiQueryInput.hotelId,
      rateCode: bookingInformation?.ratePlanCode,
      bookingChannel: BOOKING_CHANNEL.BB,
    }
  );

  const { isLoading: isLoadingPaymentInfoMessage, data: paymentInfoMessageData } = useQueryRequest(
    [
      'GetPaymentInfoMessages',
      hiQueryInput.hotelId,
      country,
      language,
      bookingInformation?.ratePlanCode,
      BOOKING_CHANNEL.BB,
    ],
    GET_PAYMENT_INFO_MESSAGES_QUERY,
    {
      hotelId: hiQueryInput.hotelId,
      language,
      country,
      rateCode: bookingInformation?.ratePlanCode,
      bookingChannel: BOOKING_CHANNEL.BB,
    },
    { enabled: !!bookingInformation?.ratePlanCode }
  );

  const {
    mutation: initiatePaymentMutation,
    isSuccess,
    data: initiatePaymentMutationData,
    isError: isErrorInitiatePaymentMutation, // If isError is changed, also change inside getPaymentError
    error: initiatePaymentMutationError, // If error is changed, also change inside getPaymentError
  } = useMutationRequest(INITIATE_PAYMENT_MUTATION, true, idTokenCookie);

  const {
    mutation: initiatePaypalPaymentMutation,
    isSuccess: isPaypalSuccess,
    isLoading: isPaypalLoading,
    data: initiatePaypalPaymentMutationData,
    isError: isErrorInitiatePaypalPaymentMutation,
    error: initiatePaypalPaymentMutationError,
  } = useMutationRequest(INITIATE_PAYPAL_PAYMENT_MUTATION, true);

  const { isPaymentComplete, cardType } = useIPageSubmission();
  const [paymentStepState, setPaymentStepState] = useState(paymentSteps.PAYMENT_DETAILS);
  const isPaymentDetailsStep = paymentStepState === paymentSteps.PAYMENT_DETAILS;
  const [selectedPaymentDetail, setSelectedPaymentDetail] = useState<PaymentOption>({
    type: 'default',
    order: 1,
    enabled: false,
  });

  const [confAnalytics, setConfAnalytics] = useSessionStorage<PaymentAnalytics>(
    PAYMENT_ANALYTICS_KEY,
    {}
  );

  useEffect(() => {
    if (bkngData && packages) {
      updateAncillariesAnalytics(bkngData.bookingInformation, packages, Area.BB);
    }
  }, [bkngData, packages]);

  useEffect(() => {
    if (paymentStepState === paymentSteps.CARD_DETAILS && basketReference) {
      window.localStorage.setItem('3cpVisited', basketReference.toString());
      setConfAnalytics({
        ...confAnalytics,
        paymentCardSelected: '',
        paymentTakenNow: (selectedPaymentDetail?.type === 'PAY_NOW').toString(),
      });
      analytics.update({
        paymentCardSelected: '',
        paymentTakenNow: (selectedPaymentDetail?.type === 'PAY_NOW').toString(),
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
        paymentTakenNow: 'false',
      });
      analytics.update({
        paymentCardSelected: selectedPaymentDetail.type,
        paymentTakenNow: 'false',
      });
    }
  }, [paymentStepState, selectedPaymentDetail]);

  useEffect(() => {
    const cleanupPaymentFailedValue = () => {
      setPaymentFailedValue(PAYMENT_FAILED_INITIAL_VALUE);
      setPaymentFailureCode(PAYMENT_FAILURE_CODE_INITIAL_VALUE);
      setPaymentFailureDescription(PAYMENT_FAILURE_DESCRIPTION_INITIAL_VALUE);
    };
    window.addEventListener('beforeunload', cleanupPaymentFailedValue);
    return () => window.removeEventListener('beforeunload', cleanupPaymentFailedValue);
  }, [setPaymentFailedValue]);

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

  const [selectedPaymentType, setSelectedPaymentType] = useState({
    name: '',
    type: '',
    subType: '',
    order: 0,
    paymentOptions: [{ type: '', order: 0, enabled: true }],
    enabled: false,
    cnpPreSelected: false,
    cnpOptionAvailable: false,
    reasons: [],
    bookingAllowances: {
      allowAlcohol: false,
      allowCarParking: false,
      allowAdditionalCosts: false,
      maxDinnerBudgets: {
        ukWide: {
          amount: 0,
          currency: '',
        },
      },
    },
  } as PaymentMethod);

  const [paymentAuth, setPaymentAuth] = useState(false);
  const [paymentPassword, setPaymentPassword] = useState('');
  const [dinnerAllowance, setDinnerAllowance] = useState(false);
  const [businessAllowances, setBusinessAllowances] = useState({
    totalDinnerBudgetPersonNight: '0.0',
    isAlcoholDinner: false,
    carParking: false,
    additionalCharges: false,
    wifi: hasWifiSelected,
  } as BusinessAllowance);
  const [displayedBusinessSections, setDisplayedBusinessSections] = useState({
    referenceDetails: false,
    paymentAuth: false,
    businessAllowances: false,
    businessAllowancesSections: {
      amountDisabled: true,
      amount: 0,
      allowDinner: true,
      allowAlcohol: false,
      allowCarParking: false,
      allowWiFi: false,
    },
  });

  const { allowDinner, allowCarParking, allowWiFi } =
    displayedBusinessSections.businessAllowancesSections;

  const [hasError, setHasError] = useState<boolean>(false);
  const [validateQuestions, setValidateQuestions] = useState<boolean>(false);
  const [purchaseOrder, setPurchaseOrder] = useState<ReferencesQuestions>({
    answer: '',
    question: CompanyReferencesQuestions.PURCHASE_ORDER_NUMBER,
    questionId: 8,
    mandatory: true,
    managementHeader: '',
  });
  const [customerRef, setCustomerRef] = useState<ReferencesQuestions>({
    answer: '',
    question: CompanyReferencesQuestions.CUSTOMER_REFERENCE,
    questionId: 9,
    mandatory: true,
    managementHeader: '',
  });

  const [hasEmployeeQuestionsErrors, setHasEmployeeQuestionsErrors] = useState<boolean>(false);
  const [hasBusinessAllowanceError, setHasBusinessAllowanceError] = useState<boolean>(false);
  const [userDefinedQuestions, setUserDefinedQuestions] = useState<Array<Questions>>([
    {
      answer: '',
      question: '',
      questionId: 0,
      mandatory: false,
      managementHeader: '',
    },
  ]);

  useEffect(() => {
    hasEmployeeQuestionsErrors || hasBusinessAllowanceError
      ? setHasError(true)
      : setHasError(false);
  }, [
    userDefinedQuestions,
    hasEmployeeQuestionsErrors,
    purchaseOrder,
    customerRef,
    hasBusinessAllowanceError,
    dinnerAllowance,
  ]);

  const updateIframeHeight =
    selectedPaymentType?.type === PiCardType.NEW_CARD ||
    selectedPaymentType?.type === PiCardType.NEW_PIBA;
  const trimAnswerValue = (value: unknown) => (typeof value === 'string' ? value.trim() : '');

  const isPibaCard = (selectedPaymentType: any) => {
    if (
      (selectedPaymentType?.type === 'SAVED_CARD' && selectedPaymentType?.name === 'PIBA') ||
      selectedPaymentType?.type === 'NEW_PIBA'
    ) {
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

  const hotelCounty = hiData?.hotelInformation?.county;

  const { isLoggedIn } = useUserData();
  const userData = useUserDetails(true, isLoggedIn, userDetails);
  const { cdhCompanyId, sessionId, cdhEmployeeId } = getLoggedInUserInfo(idTokenCookie);
  const companyData = useCompanyDetails(
    cdhCompanyId,
    sessionId,
    cdhEmployeeId,
    isLoggedIn,
    companyDetails
  );

  const { requestedCompany } = companyData || {};

  useEffect(() => {
    const displayedSections = getIsBBCardDetailsDisplayed({
      selectedPaymentType,
      hotelCounty,
      accessLevel: (userData as any)?.business?.accessLevel,
    });

    setDisplayedBusinessSections(displayedSections);
    if (
      displayedSections?.paymentAuth === false &&
      displayedSections?.businessAllowances === true
    ) {
      setPaymentAuth(true);
    } else {
      setPaymentAuth(false);
    }
    if (displayedSections?.businessAllowances === true) {
      const inputRegex = new RegExp(/^\d{0,3}$/g);
      const amount = displayedSections?.businessAllowancesSections?.amount?.toString?.();
      if (inputRegex.test(amount)) {
        setBusinessAllowances({
          ...businessAllowances,
          totalDinnerBudgetPersonNight: amount,
        });
      }
    } else {
      setBusinessAllowances({
        ...businessAllowances,
        totalDinnerBudgetPersonNight: '0.0',
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

    setBusinessAllowancesSections({
      displayedSections,
      selectedPaymentType,
      hotelCounty,
      hiData,
    });
  }, [selectedPaymentType, hotelCounty]);

  useEffect(() => {
    if (paymentAuth && displayedBusinessSections.businessAllowances) {
      setConfAnalytics({
        ...confAnalytics,
        alcoholAllowed:
          displayedBusinessSections.businessAllowancesSections.allowDinner &&
          displayedBusinessSections.businessAllowancesSections.allowAlcohol &&
          dinnerAllowance
            ? businessAllowances.isAlcoholDinner
            : false,
        carParkingAllowed: displayedBusinessSections.businessAllowancesSections.allowCarParking
          ? businessAllowances.carParking
          : false,
        dinnerAllowance: displayedBusinessSections.businessAllowancesSections.allowDinner
          ? dinnerAllowance
          : false,
        otherChargesAllowed: false,
        pageName: 'booking payment',
      });
      analytics.update({
        alcoholAllowed:
          displayedBusinessSections.businessAllowancesSections.allowDinner &&
          displayedBusinessSections.businessAllowancesSections.allowAlcohol &&
          dinnerAllowance
            ? businessAllowances.isAlcoholDinner
            : false,
        carParkingAllowed: displayedBusinessSections.businessAllowancesSections.allowCarParking
          ? businessAllowances.carParking
          : false,
        dinnerAllowance: displayedBusinessSections.businessAllowancesSections.allowDinner
          ? dinnerAllowance
          : false,
        otherChargesAllowed: false,
        pageName: 'booking payment',
      });
    } else {
      setConfAnalytics({
        ...confAnalytics,
        alcoholAllowed: false,
        carParkingAllowed: false,
        dinnerAllowance: false,
        otherChargesAllowed: false,
        pageName: 'booking payment',
      });
      analytics.update({
        alcoholAllowed: false,
        carParkingAllowed: false,
        dinnerAllowance: false,
        otherChargesAllowed: false,
        pageName: 'booking payment',
      });
    }
  }, [displayedBusinessSections, paymentAuth, dinnerAllowance, businessAllowances]);

  const [notifyPaymentMethodChange, setNotifyPaymentMethodChange] = useState<boolean>(false);

  const rooms = bkngData?.bookingInformation.reservationByIdList.map(
    (room: BCReservationListItem) => {
      return {
        adultsNumber: room?.roomStay?.adultsNumber,
        rate: room?.roomStay?.ratePlanCode,
        type: room?.roomStay?.roomExtraInfo?.roomType,
      };
    }
  );

  const togglePaymentAuth = () => setPaymentAuth(!paymentAuth);

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
    hotelHasCityTaxForLeisure, //!'hasCityTaxForLeisure',
    hotelHasCityTaxForBusiness, // !!'hasCityTaxForBusiness',
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
    termsAndConditionsData,
    onclickBillingFormHandler,
    onSubmitBtnText:
      selectedPaymentDetail?.type === 'RESERVE_WITHOUT_CARD'
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
      reservationDetails?.currency
    ),
    rateDescription: basketDetailsState?.rateDescription || '',
    rateTags: basketDetailsState?.rateTags,
    cityTaxTotal: bkngData?.bookingInformation?.cityTaxTotal || '',
  });

  const billing = bkngData?.bookingInformation?.reservationByIdList[0]?.billing;
  const [billingAddress, setBillingAddress] = useState<AddressGuestInput | undefined>(
    billing?.address || undefined
  );

  const currentLang = language;

  const hotelBrand = hiData?.hotelInformation?.brand;

  const isBillingAddressDisplayed = getIsBillingAddressDisplayed({
    selectedPaymentType,
    hiData,
    selectedPaymentDetail,
    isBb: true,
  });

  const toggleDinnerAllowance = () => {
    setDinnerAllowance(!dinnerAllowance);
  };

  function confirmBookingLabel() {
    const selectedPaymentType = selectedPaymentDetail?.type;

    return (paymentStepState === paymentSteps.CARD_DETAILS &&
      (selectedPaymentType === PaymentType.PAY_ON_ARRIVAL ||
        selectedPaymentType === PaymentType.PAY_NOW)) ||
      (paymentStepState === paymentSteps.PAYMENT_DETAILS &&
        selectedPaymentType === PaymentType.RESERVE_WITHOUT_CARD)
      ? t('ccui.payment.confirmBooking.button')
      : t('terms.continueText.paymentDetails');
  }

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

  const infoMessages =
    [...(listOfImportantMessagesHotel?.length > 0 ? [listOfImportantMessagesHotel] : [])]?.flat() ||
    [];
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
    if (!paymentAuth) {
      return false;
    }
    if (
      (allowance.key === 'dinner' || allowance.key === 'alcohol') &&
      !displayedBusinessSections.businessAllowancesSections.allowDinner
    ) {
      return false;
    }
    if (allowance.key === 'alcohol') {
      if (!displayedBusinessSections.businessAllowancesSections.allowAlcohol) {
        return false;
      }
      const dinnerAllowanceSelected = availableAllowances.find(
        (item: any) => item.key === 'dinner'
      )?.isSelected;
      if (!dinnerAllowanceSelected) {
        return false;
      }
    }
    if (
      allowance.key === 'carParking' &&
      !displayedBusinessSections.businessAllowancesSections.allowCarParking
    ) {
      return false;
    }
    return !!allowance.isSelected;
  };

  const updateSelectedPaymentOption = () => {
    if (selectedPaymentType?.name === PaymentType.RESERVE_WITHOUT_CARD) {
      return PaymentType.RESERVE_WITHOUT_CARD;
    }

    return selectedPaymentType.type === APGP_PAYMENT ? WALLET_APPLE : selectedPaymentType.name;
  };

  // for initiatePaymentMutation
  const getPaymentParams = ({
    billingAddress,
    paymentType,
    paypalNonce,
    paypalDeviceData,
  }: GetPaymentParamsProps) => {
    const allRooms = bkngData?.bookingInformation?.reservationByIdList || [];
    const isStayingInRoom = (room: { reservationGuestList: { email: string }[] }, email: string) =>
      room.reservationGuestList?.some((guest) => guest.email === email);
    const bookerIsNotGuest = !allRooms.some((room: { reservationGuestList: { email: string }[] }) =>
      isStayingInRoom(room, userData?.contactDetail?.email)
    );

    const { hotelId } = bookingInformation;
    const billingObj = {
      ...billing,
      landline: undefined, //for now createPaymentMutation does not accept landline in billing
      // current behaviour is to use mobile number (alternative telephone), to populate telephone field
      // fix - provide fallback and use landline
      // in the update employee form - landline is the compulsory field, alternative (mobileNumber) telephone is optional
      telephone: billing?.landline || billing?.telephone,
      address: getBillingAddress({
        countryRouter: country,
        isBillingAddressDisplayed,
        billingAddress,
        billing,
      }),
      bookerIsNotGuest,
    };

    let card:
      | {
          token: any;
          cardType: any;
          logoUrl?: string;
          cardholderName?: string;
          cardName?: string;
          cardNumber?: string;
          cnpRequired?: boolean;
          expiryMonth?: string;
          expiryYear?: string;
          type?: string;
        }
      | undefined;
    if (selectedPaymentType?.card) {
      const {
        logoSrc: logoUrl,
        cardHolderName: cardholderName,
        ...cardDetails
      } = selectedPaymentType.card;
      card = { ...cardDetails, logoUrl, cardholderName };
      delete (card as any)['cardNumber'];
    }

    const bookingRequest = {
      businessSite: {
        identifier: hotelId,
        name: bookingSummaryData.hotelInformation?.hotelName,
        type: 'HOTEL',
        location: hotelId,
      },
      channel: 'BB',
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

    function getPibaCardPresent(selectedPaymentType: any) {
      if (displayedBusinessSections?.paymentAuth) {
        return !paymentAuth;
      }
      if (typeof selectedPaymentType?.card?.cnpRequired !== 'undefined') {
        return !selectedPaymentType?.card?.cnpRequired;
      }
      // though the pibaCardPresent flag name might be misleading, the flag should
      // be set to true for the NEW_CARD payment type, to avoid CNP authorisation notes being created
      // pibaCardPresent should be true for apgp payment type, as per APGP-115
      return (
        selectedPaymentType.type === PiCardType.NEW_CARD ||
        selectedPaymentType.type === PiCardType.APGP
      );
    }

    const businessItemsRequest =
      displayedBusinessSections?.paymentAuth || displayedBusinessSections?.businessAllowances
        ? {
            businessItems: {
              purchaseOrderNumber: '',
              customReferenceNumber: '',
              businessAllowances: mapBusinessAllowances(businessAllowances),
            },
          }
        : {};

    const purchaseOrderAnswer = trimAnswerValue(purchaseOrder?.answer);
    const purchaseOrderQuestionAndAnswer = purchaseOrderAnswer
      ? {
          question: purchaseOrder?.question,
          answer: purchaseOrderAnswer,
          questionHeader: purchaseOrder?.managementHeader,
        }
      : {};

    const customerReferenceAnswer = trimAnswerValue(customerRef?.answer);
    const customerReferenceQuestionAndAnswer = customerReferenceAnswer
      ? {
          question: customerRef?.question,
          answer: customerReferenceAnswer,
          questionHeader: customerRef?.managementHeader,
        }
      : {};

    const userDefinedQuestionAndAnswers = userDefinedQuestions
      .map((filteredItem: Questions) => {
        const answer = trimAnswerValue(filteredItem?.answer);
        return {
          question: filteredItem?.question,
          answer,
          questionHeader: filteredItem?.managementHeader,
        };
      })
      .filter((item) => item.answer);

    const companyQuestionAndAnswerDetails = {
      purchaseOrderQuestionAndAnswer: purchaseOrderQuestionAndAnswer,
      customerReferenceQuestionAndAnswer: customerReferenceQuestionAndAnswer,
      userDefinedQuestionAndAnswers: userDefinedQuestionAndAnswers,
    };

    const cardBillingAddress = !isBillingAddressDisplayed
      ? getSavedCardBillingAddress(
          card?.cardType ?? '',
          requestedCompany?.companyDetails?.companyName,
          userData?.paymentPreference?.paymentCard?.billingAddress,
          card?.token ?? '',
          requestedCompany?.paymentDetails?.paymentCards ?? []
        )
      : null;
    const formattedBillingObj = { ...billingObj, cardBillingAddress };

    const paymentRequest = {
      billing: formattedBillingObj,
      card,
      environment: window.location.origin,
      subType: PAYPAL_PAYMENT === paymentType ? 'MIT' : 'ECOMM',
      type: updateSelectedPaymentOption(),
      pibaCardPresent: getPibaCardPresent(selectedPaymentType),
      ...businessItemsRequest,
      ...(PAYPAL_PAYMENT === paymentType && {
        paypalNonce,
        paypalDeviceData,
      }),
    };

    const createPaymentCriteria = {
      booking: bookingRequest,
      payment: paymentRequest,
      hotelId: hotelId,
      requestId: uuidv4(),
      companyQuestionAndAnswerDetails: companyQuestionAndAnswerDetails,
    };
    // if url has querystring secure-booking then set isSecureBooking to true
    if (isSecureBookingPage(router?.query as secureBookingType, isSecureBookingFeatureEnabled)) {
      return { ...createPaymentCriteria, isSecureBooking: true };
    }
    return { ...createPaymentCriteria };
  };

  const continueToNextStep = (billingAddress?: AddressGuestInput) => {
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
        `/${country}/${language}/business-booker/booking-business/guest-details?reservationId=${basketReference}`
      );
    }
  };

  const handleBackClick = () => {
    if (paymentStepState === paymentSteps.CARD_DETAILS) {
      setPaymentStepState(paymentSteps.PAYMENT_DETAILS);
      // put user back to top of the page when going back to payment options
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
    let queryString = `reservationId=${basketReference}`;

    if (isSecureBookingPage(router?.query as secureBookingType, isSecureBookingFeatureEnabled)) {
      queryString = `reservationId=${basketReference}&secure-booking=true`;
    }

    router
      .push(`/${country}/${language}/business-booker/booking-business/confirmation?${queryString}`)
      // eslint-disable-next-line no-console
      .catch((error) => console.log(error));
  };

  const paymentStatus = initiatePaymentMutationData?.initiatePayment?.status;

  useEffect(() => {
    setConfAnalytics({
      ...confAnalytics,
      basketReference,
    });
  }, [paymentStatus]);

  useEffect(() => {
    if (isSuccess) {
      if (paymentStatus === 'PAYMENT_REQUIRED') {
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
      } else if (paymentStatus === 'NOT_REQUIRED') {
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
    if (initiatePaymentMutationData?.initiatePayment?.status === 'NOT_REQUIRED') {
      goToConfirmationPage();
    }
  }, [initiatePaymentMutationData]);

  const hasPaymentMethods = useMemo(() => {
    return !!paymentMthData?.paymentMethods?.find(
      (paymentMethod: PaymentMethod) => paymentMethod.enabled
    );
  }, [paymentMthData]);

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
        paymentTakenNow: (selectedPaymentDetail?.type === 'PAY_NOW').toString(),
      });
      analytics.update({
        paypal: true,
        paymentTakenNow: (selectedPaymentDetail?.type === 'PAY_NOW').toString(),
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
    isLoadingPaymentOptions,
    isLoadingPaymentPcks,
    isLoadingPaymentInfoMessage,
    isLoadingTermsAndConditions,
    isPaypalLoading,
    isPaypalSuccess
  );

  if (isLoading) {
    return (
      <Flex {...loadingStyle}>
        <LoadingSpinner loadingText={t('booking.loading')} />
      </Flex>
    );
  }

  function onclickBillingFormHandler() {
    setValidateQuestions(true);
    if (!isBillingAddressDisplayed) {
      !hasError && continueToNextStep();
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
        userType={UserType.Business}
        variant={Area.BB}
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

            {/* hide dont go back notification if feature enabled */}
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

            {!hasPaymentMethods && <NoPaymentMethodsNotification />}
            {hasPaymentMethods && (
              <>
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
                  <EmployeeQuestions
                    companyManagementDetails={requestedCompany?.companyManagementDetails}
                    setPurchaseOrder={setPurchaseOrder}
                    purchaseOrder={purchaseOrder}
                    setCustomerRef={setCustomerRef}
                    customerRef={customerRef}
                    setUserDefinedQuestions={setUserDefinedQuestions}
                    userDefinedQuestions={userDefinedQuestions}
                    setValidateQuestions={setValidateQuestions}
                    validateQuestions={validateQuestions}
                    setHasEmployeeQuestionsErrors={setHasEmployeeQuestionsErrors}
                  />

                  {!disablePaymentOptions && (
                    <>
                      {displayedBusinessSections?.paymentAuth && (
                        <PaymentAuthorization
                          togglePaymentAuth={togglePaymentAuth}
                          paymentAuth={paymentAuth}
                          password={paymentPassword}
                          onChangePassword={setPaymentPassword}
                        />
                      )}

                      {paymentAuth &&
                        displayedBusinessSections?.businessAllowances &&
                        (allowDinner || allowCarParking || allowWiFi) && (
                          <BusinessAllowances
                            reservationDetails={reservationDetails}
                            toggleDinnerAllowance={toggleDinnerAllowance}
                            dinnerAllowance={dinnerAllowance}
                            businessAllowances={businessAllowances}
                            setBusinessAllowances={setBusinessAllowances}
                            businessAllowancesSections={
                              displayedBusinessSections?.businessAllowancesSections
                            }
                            hasBusinessAllowanceErrorState={[
                              hasBusinessAllowanceError,
                              setHasBusinessAllowanceError,
                            ]}
                          />
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
                    />
                  )}

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

                  <TotalCostPayment
                    hotelName={bookingSummaryData.hotelInformation?.hotelName as string}
                    hotelId={bookingInformation.hotelId}
                    rateCode={bookingInformation.ratePlanCode}
                    ratePlan={getTotalCost(
                      bookingInformation?.ratePlanCode,
                      bookingInformation?.totalCost,
                      reservationDetails?.currency
                    )}
                    isBillingAddressDisplayed={isBillingAddressDisplayed}
                    continueToNextStep={continueToNextStep}
                    selectedPaymentDetail={selectedPaymentDetail}
                    errorMessagePayment={errorMessagePayment}
                    bookingChannel={BOOKING_CHANNEL.BB}
                    setValidateQuestions={setValidateQuestions}
                    hasError={hasError}
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
                    {/* Back to payment page 1 - payment details - bottom link */}
                    {isBackToPaymentOptionsLinkEnabled && (
                      <BackToPage
                        goBack={handleBackClick}
                        linkText={t('booking.backToPaymentMethods.text')}
                      />
                    )}
                  </>
                )}
                <Box {...securityContainerStyle}>
                  <DataSecuritySection
                    privacyPolicy={privacyPolicyData}
                    prefixDataTestId={'PaymentPage'}
                    containerStyle={{ mt: 'xl' }}
                  />
                </Box>
              </>
            )}
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
          {paymentStepState === paymentSteps.PAYMENT_DETAILS && hasPaymentMethods && (
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

              {PAYPAL_PAYMENT === selectedPaymentType?.name ? (
                <Box {...buttonStyle}>
                  <PaypalWBButton {...paypalOptionsData} />
                </Box>
              ) : (
                <Button
                  {...buttonStyle}
                  type="submit"
                  form="billingAddressForm"
                  size="md"
                  data-testid="submitButton"
                  variant="primary"
                  onClick={onclickBillingFormHandler}
                >
                  {confirmBookingLabel()}
                </Button>
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

export function getSavedCardBillingAddress(
  selectedCardType: string,
  companyName: string,
  userDataBillingAddress: any,
  cardToken: string,
  paymentCards: Array<any>
) {
  if (selectedCardType === BusinessCardType.BUSINESS_PERSONAL_STORED_CARD) {
    const personalBillingAddress = userDataBillingAddress;
    return personalBillingAddress
      ? formatBillingAddress(personalBillingAddress, companyName ?? '')
      : null;
  }
  if (selectedCardType === BusinessCardType.BUSINESS_CENTRALLY_STORED_CARD) {
    const centrallyStoredCardBillingAddress = getCentrallyStoredCardBillingAddress(
      cardToken,
      paymentCards
    );
    return centrallyStoredCardBillingAddress
      ? formatBillingAddress(centrallyStoredCardBillingAddress, companyName ?? '')
      : null;
  }
  return null;
}

export default function PaymentPageBb(props: Readonly<Props>) {
  const { basketReference } = props;
  const { language, country } = useCustomLocale();

  const { injectPaypalProvider, paypalPaymentData } = usePaymentPaypal(
    basketReference ?? '',
    Area.BB.toUpperCase()
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
          <BBPageContent paypalPaymentData={paypalPaymentData} {...props} />
        </PayPalScriptProvider>
      ) : (
        <BBPageContent {...props} />
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

const buttonStyle = {
  mt: 'lg',
  w: { mobile: 'full', lg: '18rem', xl: '19.3125rem' },
};

const notificationDescriptionSemanticTypography = {
  textStyle: 'body-s-regular',
} as const;
