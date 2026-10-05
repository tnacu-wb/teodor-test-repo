import {
  Box,
  BoxProps,
  Flex,
  Grid,
  GridItem,
  GridItemProps,
  GridProps,
  Heading,
  HeadingProps,
} from '@chakra-ui/react';
import {
  AddressGuestInput,
  AddressGuestInputWithCountry,
  BASKET_STATUS,
  BOOKERS_REFERENCE_MAX_LENGTH,
  BookersReferencesDetailsType,
  BookingDataReservationDetailsProps,
  BookingSummaryDataProps,
  BookingType,
  BusinessAllowanceCCUItype,
  CardHolderType,
  CardName,
  CardStatus,
  GET_PAYMENT_STATUS,
  CCUI_INITIATE_PAYMENT_PROCESS,
  CcuiCardType,
  CcuiConfirmBookingData,
  CcuiConfirmBookingBillingAddress,
  CountryEnum,
  Currency,
  EckohParameters,
  EmailConfirmation,
  GET_BOOKING_INFORMATION,
  GET_ECKOH_STATUS,
  GET_HOTEL_INFORMATION,
  QueryHotelInformationArgs,
  INITIATE_ECKOH_IFRAME, // InitiateIframeMutationInterface,
  LanguageEnum,
  PaymentMethod,
  paymentOptions,
  MealItem,
  MealKids,
  PackagesCriteria,
  PibaType,
  SelectedMealsPerRoom,
  ShortCountry,
  TOTAL_DINNER_BUDGET_REGEX,
  UPDATE_DISCOUNT,
  UserRoleBasedAccess,
  UserRoles,
  EckohPayMethod,
  TypeOfCaller as TypeOfCallerEnum,
  Area,
  PaymentAnalytics,
  PAYMENT_ANALYTICS_KEY,
  AncillaryFilterData,
  UPDATE_EMAIL,
  PaymentOption,
  CompanyProfile,
  FT_PI_BB_CCUI_COMPANY_NAME_SPECIAL_CHARACTERS,
  FT_PI_BB_CCUI_DISABLE_PAYMENTS,
  PAYMENT_FAILED_INFO_KEY,
  PAYMENT_FAILURE_INFO_INITIAL_VALUE,
  PaymentErrorInfo,
  BASKET_DETAILS_STORAGE_KEY,
  BASKET_DETAILS_STATE_INITIAL_VALUE,
  FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN_ANCILLARIES_GDP_PAYMENT,
  type Claims,
  WIFI_IDS,
  FT_CCUI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE,
  HotelBrand,
  FT_CCUI_GDP_BILLING_ADDRESS,
} from '@whitbread-eos/api';
import { Info, LoadingSpinner, Notification } from '@whitbread-eos/atoms';
import {
  AccountToCompanyContainer,
  BackToDetails,
  BillingAddress,
  BookersReferenceDetails,
  BusinessAllowancesCCUI,
  CardHolderName,
  CardPresentSection,
  DiscountSection,
  LaunchEckoh,
  PaymentDetails,
  PaymentTypeContainer,
  TotalCost,
  TypeOfCaller,
  INITIAL_GUEST_DETAILS_FORM_DATA,
} from '@whitbread-eos/molecules';
import { AgentMemo, BookingSummary } from '@whitbread-eos/organisms';
import {
  adultsMealsSelector,
  analytics,
  calculateTotalCostRoomSelection,
  childrenMealsSelector,
  getCityTaxMessages,
  getImportantMessages,
  getNightsNumber,
  hotelInformationSelector,
  mealsMapperSelector,
  renderSanitizedHtml,
  replaceWithEmptyString,
  roomInformationSelector,
  selectedMealsPerRoomSelector,
  setAnalyticsUser,
  useCustomLocale,
  useMutationRequest,
  useQueryRequest,
  usePackages,
  createReservationDetails,
  logicalOrOperator,
  updateAncillariesAnalytics,
  useSessionStorage,
  filterPackagesByAncillariesCloseOut,
  getA2CBusinessAllowances,
  useLocalStorage,
  GLOBALS,
  extrasPackagesMapperSelector,
  roomPackageSelection,
  getIsBillingAddressDisplayed,
  useFeatureToggle,
  checkIsBookingForSomeoneElse,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import getConfig from 'next/config';
import { useRouter } from 'next/router';
import { useEffect, useMemo, useState } from 'react';

export interface PaymentCcuiProps {
  hiQueryInput?: QueryHotelInformationArgs;
  pcksQueryInput?: PackagesCriteria;
  basketReference: string | null;
  user: Claims;
  accessToken: string;
  resourceIdByRoles?: string[];
  hiddenFeatures?: {
    DISCOUNT_SECTION: boolean;
    PAYMENT_METHOD_TYPES: Array<{ name?: string; type: string }>;
  };
  paymentStatus?: { basket: { status: BASKET_STATUS } };
  featureToggles?: DynamicObject;
}

interface DynamicObject {
  [key: string]: boolean;
}

export function PaymentPageCcui({
  pcksQueryInput,
  hiQueryInput,
  basketReference,
  user,
  resourceIdByRoles,
  hiddenFeatures,
  paymentStatus: initialPaymentStatus,
}: Readonly<PaymentCcuiProps>) {
  const publicRuntimeConfig = getConfig()?.publicRuntimeConfig;
  const { t } = useTranslation(['common']);
  const router = useRouter();
  const { language, country } = useCustomLocale();
  // Locale
  const currentLang = language;
  const [companyProfile] = useLocalStorage<CompanyProfile | undefined>('CompanyProfile', undefined);
  const { data: bkngData, refetch: refetchBooking } = useQueryRequest(
    ['GetBookingInformation', language, country, basketReference],
    GET_BOOKING_INFORMATION,
    {
      language,
      country,
      basketReference,
      bookingChannelCriteria: {
        channel: 'CCUI',
        subchannel: 'WEB',
        language: language === 'en' ? 'EN' : 'DE',
      },
    }
  );

  const { data: hiData } = useQueryRequest(
    ['GetHotelInformation', hiQueryInput?.hotelId, hiQueryInput?.country, hiQueryInput?.language],
    GET_HOTEL_INFORMATION,
    {
      ...hiQueryInput,
    }
  );

  const { packages, hotelHasCityTaxForLeisure, hotelHasCityTaxForBusiness } = usePackages({
    adultsNumber: pcksQueryInput?.adultsNumber as number,
    childrenNumber: pcksQueryInput?.childrenNumber as number,
    hotelId: pcksQueryInput?.hotelId as string,
    basketReferenceId: basketReference as string,
    endDate: pcksQueryInput?.endDate as string,
    startDate: pcksQueryInput?.startDate as string,
    bookingFlowId: pcksQueryInput?.bookingFlowId as string,
    nightsNumber: pcksQueryInput?.nightsNumber as number,
    channel: pcksQueryInput?.channel,
  });
  const hasAncillariesWifiSelected = packages?.roomSelection?.some((selection) =>
    selection.packagesSelection?.some(
      (packageSelection) =>
        packageSelection?.id && WIFI_IDS.includes(packageSelection.id as (typeof WIFI_IDS)[number])
    )
  );

  const {
    [FT_PI_BB_CCUI_COMPANY_NAME_SPECIAL_CHARACTERS]: isCompanyNameAdvanceEnabled,
    [FT_PI_BB_CCUI_DISABLE_PAYMENTS]: disablePaymentOptionsforRWC,
    [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN_ANCILLARIES_GDP_PAYMENT]: isCityTaxBreakdownEnabled,
    [FT_CCUI_GDP_BILLING_ADDRESS]: isBillingAddressEnabled,
    [FT_CCUI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE]: isRemovePiiDataFromLocalStorageEnabled,
  } = useFeatureToggle();

  // Discount section value and error
  const [discount, setDiscount] = useState<string>('');
  const [isDisabledDiscount, setIsDisabledDiscount] = useState<boolean>(false);
  const [isDiscountApplied, setIsDiscountApplied] = useState<boolean>(false);

  const [cardType, setCardType] = useState<string>('');
  const [disabledCardType, setDisabledCardType] = useState<string>('');

  const [disabledPaymentTypesOptions, setDisabledPaymentTypesOptions] = useState<string[]>([]);

  const [discountValidationError, setDiscountValidationError] = useState<string>('');
  // eslint-disable-next-line @typescript-eslint/no-unused-vars,no-unused-vars
  const [, setHasBusinessAllowancesError] = useState<boolean>(false);

  // Card holder name section
  const [cardHolderNames, setCardHolderNames] = useState<CardHolderType>({
    firstName: '',
    lastName: '',
  });
  const [dinnerAllowance, setDinnerAllowance] = useState(false);
  const [businessAllowances, setBusinessAllowances] = useState({
    totalDinnerBudgetPersonNight: '',
    isAlcoholDinner: false,
    carParking: false,
    ultimateWifi: false,
    mealDeal: false,
    premierInnBreakfast: false,
    continentalBreakfast: false,
  } as BusinessAllowanceCCUItype);

  const toggleDinnerAllowance = () => {
    setDinnerAllowance(!dinnerAllowance);
    if (dinnerAllowance) {
      setBusinessAllowances({
        ...businessAllowances,
        isAlcoholDinner: false,
        totalDinnerBudgetPersonNight: '',
      });
    }
  };
  //Bookers references details section
  const [bookerReferencesDetails, setBookerReferencesDetails] =
    useState<BookersReferencesDetailsType>({
      purchaseOrderNumber: '',
      companyReference: '',
    });
  // eslint-disable-next-line @typescript-eslint/no-unused-vars,no-unused-vars
  const [hasCardHolderNameError, setHasCardHolderNameError] = useState<boolean>(false);
  // eslint-disable-next-line @typescript-eslint/no-unused-vars,no-unused-vars
  const [, setHasBookerReferencesDetailsError] = useState<boolean>(false);

  const [disabledEmailOption, setDisabledEmailOption] = useState<string>('');

  const [acCharges, setACCharges] = useState<string[]>([]);
  const [acCompanyReference, setACCompanyReference] = useState<string>('');
  const [companyNumber, setCompanyNumber] = useState<string>('');
  const [companyId, setCompanyId] = useState<string>('');
  const [companyDetails, setCompanyDetails] = useState<CompanyProfile | null>(null);
  const [eckohCheckPassed, setEckohCheckPassed] = useState<boolean>(false);

  const [isEnabledEckohQuery, setIsEnabledEckohQuery] = useState<boolean>(true);
  const [isEnabledPaymentStatus, setIsEnabledPaymentStatus] = useState<boolean>(false);
  const [typeOfCaller, setTypeOfCaller] = useState<string>('');
  const [isTotalCostVisible, setIsTotalCostVisible] = useState<boolean>(false);
  const [hasChecks, setHasChecks] = useState<boolean>(false);
  const [sendEmail, setSendEmail] = useState<boolean>(false);
  const [emailAddress, setEmailAddress] = useState<string | undefined>('');
  const [emailError, setEmailError] = useState<string>('');
  const [hasErrors] = useState<boolean>(false);
  const [companyReferenceError, setCompanyReferenceError] = useState<boolean>(false);

  const [confirmationStarted, setConfirmationStarted] = useState<boolean>(false);

  const { mutation: mutationInitiatePaymentProcess, error: dataErrorInitiatePaymentProcess } =
    useMutationRequest(CCUI_INITIATE_PAYMENT_PROCESS, true);
  const { mutation: initiateIframeMutation } = useMutationRequest(INITIATE_ECKOH_IFRAME);

  const [, setPaymentFailure] = useSessionStorage<PaymentErrorInfo>(
    PAYMENT_FAILED_INFO_KEY,
    PAYMENT_FAILURE_INFO_INITIAL_VALUE
  );
  const [basketDetailsState] = useLocalStorage(
    BASKET_DETAILS_STORAGE_KEY,
    BASKET_DETAILS_STATE_INITIAL_VALUE
  );
  useEffect(() => {
    if (dataErrorInitiatePaymentProcess) {
      const firstError = (dataErrorInitiatePaymentProcess as any).response?.errors[0];

      let errorInfo = firstError?.errorInfo;
      if (!errorInfo) {
        const parsedErrorMessage = JSON.parse(firstError?.message ?? '{}');
        const { errCode, debugMessage, globalErrTextTemplate } = parsedErrorMessage;
        errorInfo = { errCode, debugMessage, globalErrTextTemplate };
      }
      setPaymentFailure(errorInfo);
      analytics.update({
        paymentDecline: true,
        declineReasonCode: errorInfo?.debugMessage ?? '',
      });
      router.push(`/${country}/${language}/payment-errors?reservationId=${basketReference}`);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [dataErrorInitiatePaymentProcess]);

  // Eckoh status
  const { data: eckohData } = useQueryRequest(
    'GetEckohStatus',
    GET_ECKOH_STATUS,
    {
      basketReference,
    },
    {
      enabled: isEnabledEckohQuery,
      gcTime: 0,
      refetchInterval: 3000,
      retry: 3,
      refetchIntervalInBackground: true,
    }
  );

  // Payment status
  const { data: paymentStatus } = useQueryRequest(
    'GetPaymentStatus',
    GET_PAYMENT_STATUS,
    {
      basketReference,
    },
    {
      enabled: isEnabledPaymentStatus,
      gcTime: 0,
      refetchInterval: 3000,
      retry: 3,
      refetchIntervalInBackground: true,
    }
  );

  const {
    mutation: updateDiscountMutation,
    isError: isDiscountError,
    error: dataErrorUpdateDiscount,
  } = useMutationRequest(UPDATE_DISCOUNT, true);

  const { mutation: updateEmailMutation } = useMutationRequest(UPDATE_EMAIL, true);

  useEffect(() => {
    setConfAnalytics({
      ...confAnalytics,
      basketReference,
    });
  }, [paymentStatus]);

  useEffect(() => {
    if (
      initialPaymentStatus?.basket.status === BASKET_STATUS.COMPLETED ||
      paymentStatus?.basket.status === BASKET_STATUS.COMPLETED
    ) {
      setIsEnabledPaymentStatus(false);
      router.push(`/confirmation?reservationId=${basketReference}`);
    } else if (paymentStatus?.basket.status === BASKET_STATUS.FAILED && confirmationStarted) {
      router.push(`/${country}/${language}/payment-errors?reservationId=${basketReference}`);
      setIsEnabledPaymentStatus(false);
    }
  }, [paymentStatus, confirmationStarted, initialPaymentStatus]);

  useEffect(() => {
    if (disablePaymentOptionsforRWC) {
      const paymentOutageData = {
        paymentCardSelected: CcuiCardType.NON_GUARANTEED,
        paymentOutage: disablePaymentOptionsforRWC,
        cardType: CcuiCardType.NON_GUARANTEED,
      };
      analytics.update({ ...paymentOutageData });
      setConfAnalytics({ ...paymentOutageData });
    }
  }, [disablePaymentOptionsforRWC]);

  const userRole = user['https://ccui.opera.whitbread.digital/role']?.[0];
  const isManager = userRole === UserRoles.MANAGER;
  const isDiscountSectionActive = useMemo(
    () => resourceIdByRoles?.some?.((role) => role === UserRoleBasedAccess.CC_ROLE05.ROLE),
    [resourceIdByRoles]
  );

  const [selectedPaymentDetail, setSelectedPaymentDetail] = useState<PaymentOption>({
    type: 'default',
    order: 1,
    enabled: true,
  });
  const [selectedPaymentType, setSelectedPaymentType] = useState({
    name: '',
    type: '',
    subType: '',
    order: 0,
    paymentOptions: [{ type: '', order: 0, enabled: true }],
    enabled: true,
    cnpPreSelected: true,
    cnpOptionAvailable: true,
    reasons: [],
  } as PaymentMethod);
  const isA2CPayment = selectedPaymentType.type === CcuiCardType.ACCOUNT_COMPANY;

  const { bookingInformation } = bkngData;
  const firstRoom = logicalOrOperator(bookingInformation?.reservationByIdList?.[0], {});
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

  const isGermanHotel = [HotelBrand.PID].includes(hiData?.hotelInformation?.brand);
  const isBillingAddressDisplayedFlag =
    isBillingAddressEnabled && currentLang === GLOBALS.language.DE && isGermanHotel;

  const [formData] = useLocalStorage('formDetails', INITIAL_GUEST_DETAILS_FORM_DATA);
  const billing =
    formData?.billing?.differentBillingAddress && !isRemovePiiDataFromLocalStorageEnabled
      ? formData.billing
      : firstRoom?.billing;

  const isBillingAddressDisplayed = getIsBillingAddressDisplayed({
    selectedPaymentType,
    hiData,
    selectedPaymentDetail,
    formData: !isRemovePiiDataFromLocalStorageEnabled
      ? formData
      : { isBillingAddressDisplayed: isBillingAddressDisplayedFlag },
  });

  const arrivalDate = logicalOrOperator(firstRoom?.roomStay?.arrivalDate, null);
  const departureDate = logicalOrOperator(firstRoom?.roomStay?.departureDate, null);

  const noNights = getNightsNumber(
    firstRoom?.roomStay?.arrivalDate,
    firstRoom?.roomStay?.departureDate
  );

  const listOfImportantMessagesHotel: string[] = getImportantMessages(
    hiData?.hotelInformation?.importantInfo?.infoItems,
    firstRoom?.roomStay?.arrivalDate,
    firstRoom?.roomStay?.departureDate
  );

  const infoMessages = [
    ...(listOfImportantMessagesHotel?.length > 0 ? [...listOfImportantMessagesHotel] : []),
  ];

  const orderedInfoMessages: { infoMsg: string; indexOrder: number }[] = [];

  listOfImportantMessagesHotel.forEach((infoMsg: string, indexOrder: number) => {
    orderedInfoMessages.push({ indexOrder, infoMsg });
  });

  const meals = packages?.meals;
  const mealsKids = packages?.mealsKids;
  const roomSelection = packages?.roomSelection;

  const [selectedMeals, setSelectedMeals] = useState<SelectedMealsPerRoom[]>([]);
  let adultsMeals: MealItem[] = adultsMealsSelector(
    packages?.meals,
    noNights,
    bookingInformation.totalAdults
  );

  let childrenMeals: MealKids[] = childrenMealsSelector(packages?.mealsKids);

  const ancillaryCloseoutData = hiData?.hotelInformation?.ancillaryCloseout;

  // filter meals by ancillaryCloseoutData from hotelInformation data
  if (ancillaryCloseoutData?.items?.length && adultsMeals?.length && childrenMeals?.length) {
    const ancillaryData: AncillaryFilterData = {
      arrivalDate: firstRoom.roomStay?.arrivalDate,
      departureDate: firstRoom.roomStay?.departureDate,
      ancillaryCloseoutData,
      adultsMeals,
      childrenMeals,
    };
    const { filteredAdultsMeals, filteredChildrenMeals } =
      filterPackagesByAncillariesCloseOut(ancillaryData);
    adultsMeals = filteredAdultsMeals;
    childrenMeals = filteredChildrenMeals;
  }

  const availableMealsIds = adultsMeals?.map((meal) => meal.id);
  // temp solution to implement restrictions only for single room bookings
  const mealPackagesSelected =
    roomSelection?.length === 1 ? roomSelection[0]?.packagesSelection?.length !== 0 : false;

  useEffect(() => {
    if (roomSelection && meals && mealsKids) {
      setSelectedMeals(mealsMapperSelector(meals, mealsKids, roomSelection));
    }
  }, [roomSelection, meals, mealsKids]);

  const bookingSummaryData: BookingSummaryDataProps = {
    hotelInformation: hotelInformationSelector(hiData?.hotelInformation),
    totalCost: {
      discount: +bookingInformation.discount,
      showVATMessage: true,
      currency: bookingInformation.currencyCode,
      initialTotalCost:
        bkngData?.bookingInformation?.totalCost -
        calculateTotalCostRoomSelection(adultsMeals, roomSelection ?? [], noNights),
      newTotalCost: bkngData?.bookingInformation.totalCost,
      previousTotalCost: bkngData?.bookingInformation.totalCostWoDiscount,
      meals: selectedMealsPerRoomSelector(selectedMeals, adultsMeals, childrenMeals),
    },
    rateInformation: {
      rate: firstRoom.roomStay?.rateExtraInfo.rateName,
      noNights: noNights,
      noRooms: bookingInformation.reservationByIdList?.length,
      rateDescription: basketDetailsState?.rateDescription || '',
      rateTags: basketDetailsState?.rateTags,
    },
    stayDatesInformation: {
      arrivalDate,
      departureDate,
      noNights: noNights,
    },
    roomInformation: roomInformationSelector(
      bkngData?.bookingInformation?.reservationByIdList,
      selectedMeals,
      adultsMeals,
      childrenMeals,
      roomPackageSelection(extrasPackagesMapperSelector(roomSelection))
    ),
    cityTaxTotal: bkngData?.bookingInformation?.cityTaxTotal || '',
  };

  const { hotelInformation } = bookingSummaryData;

  const [billingAddress, setBillingAddress] = useState<AddressGuestInputWithCountry>({
    addressLine1: '',
    addressLine2: undefined,
    addressLine3: undefined,
    addressLine4: undefined,
    cityName: undefined,
    postalCode: '',
    countryCode: '',
    companyName: undefined,
    billingAddressSelection: '',
  });
  const [eckohParameters, setEckohParameters] = useState<EckohParameters>();

  const [confAnalytics, setConfAnalytics] = useSessionStorage<PaymentAnalytics>(
    PAYMENT_ANALYTICS_KEY,
    {}
  );

  useEffect(() => {
    if (billingAddress?.billingAddressSelection === 'CurrentAddress') {
      setBillingAddress(bkngData?.bookingInformation?.reservationByIdList?.[0]?.billing?.address);
    }
  }, [billingAddress]);

  useEffect(() => {
    setDisabledEmailOption(
      selectedPaymentDetail?.type === 'default' ||
        selectedPaymentDetail?.type === paymentOptions.PAY_NOW
        ? EmailConfirmation.NO_SEND_EMAIL
        : ''
    );
    let disablePaymentOptions: string[] = [];

    if (!isManager && !disablePaymentOptionsforRWC) {
      disablePaymentOptions = [CcuiCardType.NON_GUARANTEED];
    }
    if (selectedPaymentDetail?.type === paymentOptions.PAY_NOW) {
      disablePaymentOptions = [...disablePaymentOptions, CcuiCardType.ACCOUNT_COMPANY];
    }

    setDisabledPaymentTypesOptions(disablePaymentOptions);

    if (
      selectedPaymentDetail?.type === paymentOptions.PAY_NOW ||
      selectedPaymentDetail?.type === paymentOptions.PAY_ON_ARRIVAL
    ) {
      setIsTotalCostVisible(false);
      setEckohCheckPassed(false);
      setCardHolderNames({ firstName: '', lastName: '' });
      setBusinessAllowances({
        totalDinnerBudgetPersonNight: '',
        isAlcoholDinner: false,
        carParking: false,
        ultimateWifi: false,
        mealDeal: false,
        premierInnBreakfast: false,
        continentalBreakfast: false,
      });
      setBookerReferencesDetails({ purchaseOrderNumber: '', companyReference: '' });
    }

    typeOfCaller === '' ? setHasChecks(false) : setHasChecks(true);

    switch (selectedPaymentType.type) {
      case CcuiCardType.NEW_CARD:
        setTypeOfCaller('');
        setHasChecks(true);
        setCardType(CardStatus.CARD_PRESENT);
        if (
          selectedPaymentType?.paymentOptions?.[0]?.enabled ||
          selectedPaymentType?.paymentOptions?.[1]?.enabled
        ) {
          setDisabledCardType(CardStatus.CARD_NOT_PRESENT);
        }
        setIsTotalCostVisible(false);
        setEckohCheckPassed(false);
        setCardHolderNames({ firstName: '', lastName: '' });
        setBusinessAllowances({
          totalDinnerBudgetPersonNight: '',
          isAlcoholDinner: false,
          carParking: false,
          ultimateWifi: false,
          mealDeal: false,
          premierInnBreakfast: false,
          continentalBreakfast: false,
        });
        setBookerReferencesDetails({ purchaseOrderNumber: '', companyReference: '' });

        break;
      case CcuiCardType.NEW_PIBA:
        setTypeOfCaller('');
        setHasChecks(true);
        setIsTotalCostVisible(false);
        setDisabledCardType('');
        setCardType('');
        setEckohCheckPassed(false);
        setCardHolderNames({ firstName: '', lastName: '' });
        setBusinessAllowances({
          totalDinnerBudgetPersonNight: '',
          isAlcoholDinner: false,
          carParking: false,
          ultimateWifi: false,
          mealDeal: false,
          premierInnBreakfast: false,
          continentalBreakfast: false,
        });
        setBookerReferencesDetails({ purchaseOrderNumber: '', companyReference: '' });
        break;
      case CcuiCardType.ACCOUNT_COMPANY:
        setTypeOfCaller('');
        setHasChecks(true);
        setIsTotalCostVisible(false);
        setEckohParameters(undefined);
        setIsEnabledEckohQuery(false);
        setCardHolderNames({ firstName: '', lastName: '' });
        setBusinessAllowances({
          totalDinnerBudgetPersonNight: '',
          isAlcoholDinner: false,
          carParking: false,
          ultimateWifi: false,
          mealDeal: false,
          premierInnBreakfast: false,
          continentalBreakfast: false,
        });
        setBookerReferencesDetails({ purchaseOrderNumber: '', companyReference: '' });

        break;
      case CcuiCardType.NON_GUARANTEED:
        setIsTotalCostVisible(false);
        setEckohParameters(undefined);
        setIsEnabledEckohQuery(false);
        setCardHolderNames({ firstName: '', lastName: '' });
        setBusinessAllowances({
          totalDinnerBudgetPersonNight: '',
          isAlcoholDinner: false,
          carParking: false,
          ultimateWifi: false,
          mealDeal: false,
          premierInnBreakfast: false,
          continentalBreakfast: false,
        });
        setBookerReferencesDetails({ purchaseOrderNumber: '', companyReference: '' });
        setTypeOfCaller(TypeOfCallerEnum.ANY_CUSTOMER);

        break;
    }
  }, [selectedPaymentType, selectedPaymentDetail, userRole, hasChecks]);

  useEffect(() => {
    if (cardType === CardStatus.CARD_PRESENT || cardType === CardStatus.CARD_NOT_PRESENT) {
      setIsTotalCostVisible(false);
      setEckohCheckPassed(false);
      setCardHolderNames({ firstName: '', lastName: '' });
      setBusinessAllowances({
        totalDinnerBudgetPersonNight: '',
        isAlcoholDinner: false,
        carParking: false,
        ultimateWifi: false,
        mealDeal: false,
        premierInnBreakfast: false,
        continentalBreakfast: false,
      });
      setBookerReferencesDetails({ purchaseOrderNumber: '', companyReference: '' });
    }
  }, [cardType]);

  useEffect(() => {
    setAnalyticsUser(user, language);
  }, [user, language]);

  const reservationDetails: BookingDataReservationDetailsProps = createReservationDetails(
    arrivalDate,
    departureDate,
    bkngData?.bookingInformation?.currencyCode,
    bkngData?.bookingInformation?.reservationByIdList,
    noNights
  );

  const currency = reservationDetails.currency === 'GBP' ? Currency.GBP : Currency.EUR;

  const isDisabledDiscountChildren = bkngData?.bookingInformation?.reservationByIdList?.some(
    (item: any) => item.roomStay.childrenNumber !== 0
  );

  useEffect(() => {
    setBillingAddress(bkngData?.bookingInformation?.reservationByIdList?.[0]?.billing?.address);
    setDiscount(bookingInformation?.discount === 0 ? '' : bookingInformation?.discount);
  }, [bkngData?.bookingInformation?.reservationByIdList]);

  useEffect(() => {
    setConfAnalytics({
      ...confAnalytics,
      paymentTakenNow: (selectedPaymentDetail?.type === paymentOptions.PAY_NOW).toString(),
    });
    analytics.update({
      paymentTakenNow: (selectedPaymentDetail?.type === paymentOptions.PAY_NOW).toString(),
    });
  }, [selectedPaymentDetail]);

  useEffect(() => {
    if (bkngData && packages) {
      updateAncillariesAnalytics(bkngData.bookingInformation, packages, Area.CCUI);
      setConfAnalytics({
        ...confAnalytics,
        cardNotPresent: cardType === CardStatus.CARD_NOT_PRESENT,
        cardType,
      });
      analytics.update({ cardNotPresent: cardType === CardStatus.CARD_NOT_PRESENT, cardType });
    }
  }, [bkngData, cardType, packages]);

  useEffect(() => {
    const isCurrentBookingFormData = formData?.basketReferenceId === basketReference;
    const email =
      (!isRemovePiiDataFromLocalStorageEnabled && isCurrentBookingFormData && formData?.email) ||
      firstRoom?.billing?.email;
    if (email) {
      setEmailAddress(email);
    }
  }, [
    firstRoom?.billing?.email,
    formData?.email,
    formData?.basketReferenceId,
    basketReference,
    isRemovePiiDataFromLocalStorageEnabled,
  ]);

  const isCompWithoutEckoh =
    selectedPaymentType.type === CcuiCardType.ACCOUNT_COMPANY ||
    selectedPaymentType.type === CcuiCardType.NON_GUARANTEED;

  const isNewPIBAShowTotalCost =
    billingAddress?.addressLine1 !== '' &&
    cardHolderNames.firstName !== '' &&
    cardHolderNames.lastName !== '' &&
    !hasCardHolderNameError &&
    bookerReferencesDetails.companyReference.length <= BOOKERS_REFERENCE_MAX_LENGTH &&
    bookerReferencesDetails.purchaseOrderNumber.length <= BOOKERS_REFERENCE_MAX_LENGTH &&
    TOTAL_DINNER_BUDGET_REGEX.test(String(businessAllowances.totalDinnerBudgetPersonNight)) &&
    selectedPaymentType.type === CcuiCardType.NEW_PIBA;

  const isPayNowShowTotalCost =
    cardHolderNames.firstName !== '' &&
    cardHolderNames.lastName !== '' &&
    eckohCheckPassed &&
    selectedPaymentDetail?.type === paymentOptions.PAY_NOW;

  const isTotalCostVisibleNCCandPOA = eckohCheckPassed; // due to DNRQ-47135

  const isEckohRequired =
    selectedPaymentType.type !== CcuiCardType.ACCOUNT_COMPANY &&
    selectedPaymentType.type !== CcuiCardType.NON_GUARANTEED;

  const isCardHolderNameCompleted = isEckohRequired
    ? cardHolderNames.firstName !== '' && cardHolderNames.lastName !== '' && eckohCheckPassed
    : true;

  const showTotalCostSection = logicalOrOperator(
    isTotalCostVisible,
    isTotalCostVisibleNCCandPOA,
    typeOfCaller !== '',
    isNewPIBAShowTotalCost,
    isPayNowShowTotalCost
  );

  const isLoading = logicalOrOperator(
    initialPaymentStatus?.basket.status === BASKET_STATUS.COMPLETED,
    initialPaymentStatus?.basket.status === BASKET_STATUS.PROCESSING,
    confirmationStarted && initialPaymentStatus?.basket.status === BASKET_STATUS.PAY_PENDING,
    paymentStatus?.basket.status === BASKET_STATUS.COMPLETED,
    paymentStatus?.basket.status === BASKET_STATUS.PROCESSING,
    confirmationStarted && paymentStatus?.basket.status === BASKET_STATUS.PAY_PENDING,
    dataErrorInitiatePaymentProcess,
    hasErrors,
    confirmationStarted
  );

  const onIframeLoad = (time: string) => {
    analytics.track('paymentPage2');
    setConfAnalytics({
      ...confAnalytics,
      paymentLoadTime: time,
      paymentSessionID: '',
      paymentTemplateID: '',
    });
    analytics.update({
      paymentLoadTime: time,
      paymentSessionID: '',
      paymentTemplateID: '',
    });
  };

  if (isLoading) {
    return (
      <Flex {...loadingStyle}>
        <LoadingSpinner loadingText={t('booking.loading')} />
      </Flex>
    );
  }

  const isDiscountSectionVisible = () => !hiddenFeatures?.DISCOUNT_SECTION;
  const BookersReferencesAndBusinessAllowancesCheck =
    selectedPaymentType.type !== CcuiCardType.NEW_CARD &&
    selectedPaymentDetail?.type !== paymentOptions.PAY_NOW &&
    cardType !== CardStatus.CARD_PRESENT &&
    cardType !== '';

  const hasCompanyReferenceErrors =
    selectedPaymentType.type === CcuiCardType.ACCOUNT_COMPANY && companyReferenceError;
  return (
    <Flex data-testid="paymentPageSection" flexDirection={{ md: 'column', lg: 'row' }}>
      <Box data-testid="paymentPageSection_wrapper">
        <Box pb={4} data-testid="paymentPageSection_wrapperTitle">
          <Heading as="h3" {...headerStyles} data-testid="paymentPageSection_title">
            {t('ccui.payment.title')}
          </Heading>
        </Box>
        <Box pb={4} data-testid="paymentPageSection_wrapperTitleDescription">
          <Heading as="h6" {...descriptionStyles} data-testid="paymentPageSection_titleDescription">
            {t('ccui.payment.description')}
          </Heading>
        </Box>
        {isDiscountSectionVisible() && isDiscountSectionActive && (
          <DiscountSection
            setValue={setDiscount}
            discountValue={discount}
            currency={currency}
            language={currentLang}
            onDiscountUpdate={onDiscountUpdate}
            isDiscountServerError={isDiscountError}
            discountServerError={dataErrorUpdateDiscount as Error}
            isDisabled={isDisabledDiscount || isDisabledDiscountChildren}
            discountValidationError={discountValidationError}
            setDiscountValidationError={setDiscountValidationError}
            validateDiscountValue={validateDiscountValue}
          />
        )}
        <PaymentDetails
          isCCUI={true}
          hideHeader={true}
          selectedPaymentType={selectedPaymentType}
          selectedPaymentDetail={selectedPaymentDetail}
          setSelectedPaymentDetail={setSelectedPaymentDetail}
          t={t}
          disablePaymentOptions={disablePaymentOptionsforRWC}
        />
        <PaymentTypeContainer
          selectedPaymentDetail={selectedPaymentDetail}
          selectedPaymentType={selectedPaymentType}
          onPaymentTypeClick={setSelectedPaymentType}
          disabledOptions={disabledPaymentTypesOptions}
          hiddenPaymentMethodTypes={hiddenFeatures?.PAYMENT_METHOD_TYPES}
        />
        {selectedPaymentType.type === CcuiCardType.NON_GUARANTEED && (
          <TypeOfCaller value={typeOfCaller} setValue={setTypeOfCaller} />
        )}
        {selectedPaymentDetail?.type !== paymentOptions.PAY_NOW &&
          selectedPaymentType.type === CcuiCardType.ACCOUNT_COMPANY && (
            <AccountToCompanyContainer
              hotelId={hiQueryInput?.hotelId ?? ''}
              selectedPaymentDetail={selectedPaymentDetail?.type as string}
              setIsTotalCostVisible={setIsTotalCostVisible}
              setACCharges={setACCharges}
              setCompanyReferenceError={setCompanyReferenceError}
              setCompanyNumber={setCompanyNumber}
              setCompanyId={setCompanyId}
              setCompanyDetails={setCompanyDetails}
              setACCompanyReference={setACCompanyReference}
            />
          )}
        {isEckohRequired && (
          <>
            <CardPresentSection
              value={cardType}
              setValue={onCardChange}
              disabledOption={disabledCardType}
              cardType={selectedPaymentType.type}
            />
            <LaunchEckoh
              onSuccess={() => setEckohCheckPassed(true)}
              onFail={() => setEckohCheckPassed(false)}
              setIsEnabledEckohQuery={setIsEnabledEckohQuery}
              initiateIframe={initiateIframe}
              eckohParameters={eckohParameters}
              eckohStatus={eckohData?.eckohRecordingStatus?.status}
              disabledEckoh={!cardType || selectedPaymentDetail?.type === 'default'}
              onIframeLoad={onIframeLoad}
            />

            {eckohCheckPassed && (
              <>
                {BookersReferencesAndBusinessAllowancesCheck && (
                  <>
                    <BusinessAllowancesCCUI
                      mealPackagesSelected={mealPackagesSelected}
                      availableMealsIds={availableMealsIds}
                      toggleDinnerAllowance={toggleDinnerAllowance}
                      dinnerAllowance={dinnerAllowance}
                      businessAllowances={businessAllowances}
                      setBusinessAllowances={setBusinessAllowances}
                      currency={currency}
                      language={currentLang}
                      setHasError={setHasBusinessAllowancesError}
                      hasAncillariesWifiSelected={hasAncillariesWifiSelected}
                    />
                    <BookersReferenceDetails
                      bookerReferencesDetails={bookerReferencesDetails}
                      setBookerReferencesDetails={setBookerReferencesDetails}
                      setHasError={setHasBookerReferencesDetailsError}
                    />
                  </>
                )}

                <CardHolderName
                  cardHolderNames={cardHolderNames}
                  setCardHolderNames={setCardHolderNames}
                  setHasError={setHasCardHolderNameError}
                />
                {isBillingAddressDisplayed && (
                  <BillingAddress
                    currentBillingAddress={billing?.address}
                    continueToNextStep={continueToNextStep}
                    t={t}
                    currentLang={currentLang}
                    setBillingAddress={setBillingAddress}
                    companyProfile={companyProfile}
                    isCompanyNameAdvanceEnabled={isCompanyNameAdvanceEnabled}
                    isCountryAllowTypingEnabled={true}
                  />
                )}
              </>
            )}
          </>
        )}
        <TotalCost
          discount={
            !discount || discountValidationError
              ? undefined
              : parseFloat(bookingInformation.discount)
          }
          totalCost={bookingInformation.totalCost || 0}
          hotelName={hotelInformation?.hotelName as string}
          hotelId={bookingInformation.hotelId}
          hotelBrand={
            hotelInformation?.hotelBrand ? hotelInformation?.hotelBrand.toLowerCase() : 'pi'
          }
          disabledOption={disabledEmailOption}
          language={language}
          country={country}
          isCompWithoutEckoh={isCompWithoutEckoh}
          onConfirmClick={continueWithoutEckoh}
          continueWithPayment={continueWithPayment}
          isSectionVisible={showTotalCostSection}
          hasAllChecks={hasChecks && isCardHolderNameCompleted && !hasCompanyReferenceErrors}
          setSendEmail={setSendEmail}
          sendEmail={sendEmail}
          emailSection={{
            setEmailAddress,
            emailAddress,
            emailError,
            setEmailError,
          }}
          currencyCode={reservationDetails.currency}
          isDiscountApplied={isDiscountApplied}
          previousTotalCost={bookingInformation.totalCostWoDiscount}
        />
        <BackToDetails goBack={() => router.back()} prefixDataTestId="paymentPageSection" />
      </Box>
      <Box right="0">
        <Grid {...mainPaymentsGridStyle}>
          <GridItem {...bookingSummaryMobileContainerStyle}>
            <Flex {...bookingSummaryMobileTriggerStyle}>
              <BookingSummary
                variant="mobile"
                t={t}
                language={language}
                reservationDetails={reservationDetails}
                bookingSummaryData={bookingSummaryData}
                isDiscountApplied={isDiscountApplied}
                infoMessages={infoMessages}
                taxesMessage={cityTaxMessages?.summaryText}
                isExtrasDisplayed={!!packages?.extrasItems}
                isCityTaxBreakdownEnabled={isCityTaxBreakdownEnabled}
              />
            </Flex>
          </GridItem>
          <GridItem {...bookingSummaryDesktopStyle}>
            <BookingSummary
              variant="desktop"
              t={t}
              language={language}
              reservationDetails={reservationDetails}
              bookingSummaryData={bookingSummaryData}
              isDiscountApplied={isDiscountApplied}
              taxesMessage={cityTaxMessages?.summaryText}
              isExtrasDisplayed={!!packages?.extrasItems}
              isCityTaxBreakdownEnabled={isCityTaxBreakdownEnabled}
            />
            {orderedInfoMessages?.length > 0 && (
              <Box pt="sm" data-testid="BookingSummary-InfoMessages">
                {orderedInfoMessages.map((item) => {
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
            )}
          </GridItem>
        </Grid>
      </Box>
      <AgentMemo />
    </Flex>
  );

  function continueWithPayment() {
    if (currentLang === GLOBALS.language.DE) {
      continueToNextStep(billing.address);
    }
  }

  function onCardChange(type: string) {
    setCardType(type);
  }

  function continueToNextStep(billingAddressNew?: AddressGuestInput) {
    if (billingAddressNew) {
      if (!hasErrors) {
        onConfirmBooking(billingAddressNew);
      } else {
        router.push(`/${country}/${language}/payment-errors?reservationId=${basketReference}`);
      }
    } else if (!hasErrors) {
      onConfirmBooking(billingAddress);
    } else {
      router.push(`/${country}/${language}/payment-errors?reservationId=${basketReference}`);
    }
  }

  function continueWithoutEckoh() {
    onConfirmBooking(billing.address);
  }

  function onDiscountUpdate() {
    const currentBookingDiscount = Number(bookingInformation?.discount ?? 0);
    const discountAmount = !discount || Number(discount) <= 0 ? 0 : Number(discount);

    if (discountAmount === 0 && currentBookingDiscount === 0) {
      return;
    }

    const isValid = validateDiscountValue(discount);
    if (isValid) {
      setIsDisabledDiscount(true);
      updateDiscountMutation
        .mutateAsync({ basketReference, discountAmount: +discount })
        .then(() => {
          refetchBooking();
          setIsDiscountApplied(+discount !== 0);
        })
        .finally(() => {
          setIsDisabledDiscount(false);
        });
    }
  }

  async function onEmailUpdate() {
    const updateEmailCriteria = {
      email: emailAddress,
    };
    try {
      await updateEmailMutation.mutateAsync({ basketReference, updateEmailCriteria });
      return true;
    } catch {
      setEmailError(t('ccui.payment.emailAddress.error'));
      return false;
    }
  }

  /**
   * Validate the input value without the currency
   */
  function validateDiscountValue(value: string): boolean {
    if (!/^\d*(\.\d{0,2})?$/.test(value)) {
      setDiscountValidationError(t('ccui.payment.discount.invalidMessage'));
      return false;
    } else if (parseFloat(value) > bookingInformation.totalCostWoDiscount) {
      setDiscountValidationError(t('ccui.payment.discount.errorMessage'));
      return false;
    } else {
      setDiscountValidationError('');
      return true;
    }
  }

  function getBillingData(bA: AddressGuestInput) {
    const billingCountry = bA?.countryCode || bA?.country;

    let paymentProcessCountryFromGD = '';
    if (billingCountry) {
      if (billingCountry === ShortCountry.UK) {
        paymentProcessCountryFromGD = ShortCountry.GB;
      } else {
        paymentProcessCountryFromGD = String(billingCountry);
      }
    }
    const billingAddressType = bA?.billingAddressSelection || bA?.addressType;
    const billingData: CcuiConfirmBookingBillingAddress = {
      address: {
        // bookingSummaryData
        addressLine1: bA?.addressLine1 ?? '',
        addressLine2: bA?.addressLine2 ?? '',
        addressLine3: bA?.addressLine3 ?? '',
        addressLine4: bA?.addressLine4 ?? '',
        cityName: bA?.cityName ?? '',
        postalCode: bA?.postalCode ?? '',
        country: paymentProcessCountryFromGD,
        companyName: bA?.companyName ?? '',
        addressType: billingAddressType ?? '',
      },
      firstName: logicalOrOperator(
        bookingInformation?.reservationByIdList[0]?.billing?.firstName,
        ''
      ),
      lastName: logicalOrOperator(
        bookingInformation?.reservationByIdList[0]?.billing?.lastName,
        ''
      ),
      bookerIsNotGuest: !isRemovePiiDataFromLocalStorageEnabled
        ? formData?.bookingForSomeoneElse
        : checkIsBookingForSomeoneElse(bookingInformation?.reservationByIdList ?? []),
      email: logicalOrOperator(billing.email, ''),
      title: logicalOrOperator(billing.title, ''),
      telephone: logicalOrOperator(billing.telephone, ''),
      differentBillingAddress:
        billing.differentBillingAddress ||
        billingAddress?.billingAddressSelection === 'DifferentAddress' ||
        isA2CPayment,
    };

    if (isA2CPayment && companyDetails?.address) {
      billingData.address = {
        ...companyDetails.address,
        companyName: companyDetails?.name ?? '',
        addressType: 'BUSINESS',
      };
    }
    return billingData;
  }

  function getPaymentRequest(bA: AddressGuestInput) {
    // Generate random INT number
    const arrayRandom = new Uint32Array(1);
    const randomInt: number = window.crypto.getRandomValues(arrayRandom)[0];
    const paymentProcessCountry =
      bookingInformation?.reservationByIdList[0]?.billing?.address?.country === ShortCountry.UK
        ? ShortCountry.GB
        : bookingInformation?.reservationByIdList[0]?.billing?.address?.country;
    return {
      requestId: randomInt,
      payment: {
        type: selectedPaymentType.name,
        subType: 'MOTO',
        billing: getBillingData(bA),
        card: {
          cardHolderLastName: cardHolderNames.lastName ?? '',
          cardHolderFirstName: cardHolderNames.firstName ?? '',
          cardHolderAddress: {
            addressLine1:
              bookingInformation?.reservationByIdList[0]?.billing?.address?.addressLine1 ?? '',
            addressLine2:
              bookingInformation?.reservationByIdList[0]?.billing?.address?.addressLine2 ?? '',
            addressLine3:
              bookingInformation?.reservationByIdList[0]?.billing?.address?.addressLine3 ?? '',
            addressLine4:
              bookingInformation?.reservationByIdList[0]?.billing?.address?.addressLine4 ?? '',
            cityName: bookingInformation?.reservationByIdList[0]?.billing?.address?.cityName ?? '',
            postalCode:
              bookingInformation?.reservationByIdList[0]?.billing?.address.postalCode ?? '',
            country: paymentProcessCountry ?? '',
            addressType:
              bookingInformation?.reservationByIdList[0]?.billing?.address.addressType ?? '',
          },
        },
      },
      booking: {
        type: selectedPaymentDetail?.type as BookingType,
        journey: 'BOOKING',
        channel: 'CCC',
        language: currentLang,
        businessSite: {
          identifier: bookingInformation.hotelId,
          type: 'HOTEL',
          name: bookingSummaryData.hotelInformation?.hotelName ?? '',
          location: String(bookingSummaryData.hotelInformation?.hotelAddress[0]),
        },
      },
    };
  }

  async function onConfirmBooking(bA: AddressGuestInput) {
    // Check card present
    const hasNoCardPresentOption =
      selectedPaymentType.type !== CcuiCardType.ACCOUNT_COMPANY &&
      selectedPaymentType.type !== CcuiCardType.NON_GUARANTEED;

    const cardPresentStatus = hasNoCardPresentOption && cardType === CardStatus.CARD_PRESENT;
    const selectedPaymentTypeName =
      selectedPaymentType.name === CcuiCardType.PIBA_EU
        ? EckohPayMethod.PIBA_DE
        : EckohPayMethod.PIBA_GB;
    const subPaymentType =
      selectedPaymentType.type === CcuiCardType.NEW_PIBA ? selectedPaymentTypeName : null;

    //ACCOUNT TO COMPANY ITEMS
    const isA2CPayment = selectedPaymentType.type === CcuiCardType.ACCOUNT_COMPANY;
    const accountCompanyItems = isA2CPayment
      ? {
          companyNumber,
          companyId,
          charges: acCharges.join(','),
          businessItems: {
            businessAllowances: getA2CBusinessAllowances(acCharges),
            customReferenceNumber: acCompanyReference,
            purchaseOrderNumber: '',
          },
        }
      : {
          companyNumber: '',
          companyId: '',
          charges: '',
          businessItems: null,
        };

    // BOOKING INFORMATION DATA
    const bookingDetails: CcuiConfirmBookingData = {
      paymentOption: selectedPaymentType.type as CcuiCardType,
      subPaymentType: subPaymentType,
      ccuiExtraItems: {
        sendMail: sendEmail ?? false,
        cardPresent: cardPresentStatus,
        accountCompanyItems,
        nonguaranteedItems: {
          typeOfCaller: typeOfCaller ?? null,
        },
        addressCompanyName: bA ? bA.companyName : null,
        businessItems: {
          businessAllowances: [
            {
              allowance: 'carParking',
              budget: 0,
              isAuthorised: businessAllowances.carParking,
            },
            {
              allowance: 'ultimateWifi',
              budget: 0,
              isAuthorised: businessAllowances.ultimateWifi,
            },
            {
              allowance: 'mealDeal',
              budget: 0,
              isAuthorised: businessAllowances.mealDeal,
            },
            {
              allowance: 'premierInnBreakfast',
              budget: 0,
              isAuthorised: businessAllowances.premierInnBreakfast,
            },
            {
              allowance: 'continentalBreakfast',
              budget: 0,
              isAuthorised: businessAllowances.continentalBreakfast,
            },
            {
              allowance: 'alcohol',
              budget: 0,
              isAuthorised: businessAllowances.isAlcoholDinner,
            },
            {
              allowance: 'dinner',
              budget: +businessAllowances.totalDinnerBudgetPersonNight,
              isAuthorised: +businessAllowances.totalDinnerBudgetPersonNight > 0,
            },
          ],
          customReferenceNumber: bookerReferencesDetails.companyReference,
          purchaseOrderNumber: bookerReferencesDetails.purchaseOrderNumber,
        },
      },
      paymentRequest: getPaymentRequest(bA),
    };

    let businessItems = null;
    const isPibaCnpPayment =
      cardType === CardStatus.CARD_NOT_PRESENT &&
      selectedPaymentType.type === CcuiCardType.NEW_PIBA;

    if (isPibaCnpPayment) {
      businessItems = bookingDetails.ccuiExtraItems?.businessItems;
    } else if (isA2CPayment) {
      businessItems = bookingDetails.ccuiExtraItems?.accountCompanyItems?.businessItems;
    }

    if (emailAddress && emailAddress !== firstRoom?.billing?.email && !emailError) {
      const isEmailUpdated = await onEmailUpdate();

      if (isEmailUpdated) {
        handleInitiatePaymentProcess(bookingDetails, businessItems);
      }
    } else {
      handleInitiatePaymentProcess(bookingDetails, businessItems);
    }
  }

  function handleInitiatePaymentProcess(
    bookingDetails: CcuiConfirmBookingData,
    businessItems: any
  ) {
    mutationInitiatePaymentProcess
      .mutateAsync({
        basketReference,
        paymentOption: bookingDetails.paymentOption,
        subPaymentType: bookingDetails.subPaymentType,
        sendMail: bookingDetails.ccuiExtraItems?.sendMail,
        cardPresent: bookingDetails.ccuiExtraItems?.cardPresent,
        companyNumber: bookingDetails.ccuiExtraItems?.accountCompanyItems?.companyNumber,
        companyId: bookingDetails.ccuiExtraItems?.accountCompanyItems?.companyId,
        charges: bookingDetails.ccuiExtraItems?.accountCompanyItems?.charges,
        typeOfCaller: bookingDetails.ccuiExtraItems?.nonguaranteedItems?.typeOfCaller,
        addressCompanyName: bookingDetails.ccuiExtraItems?.addressCompanyName,
        requestId: bookingDetails.paymentRequest.requestId.toString(),
        type: bookingDetails.paymentRequest.payment.type,
        subType: bookingDetails.paymentRequest.payment.subType,
        firstName: bookingDetails.paymentRequest.payment.billing.firstName,
        lastName: bookingDetails.paymentRequest.payment.billing.lastName,
        title: bookingDetails.paymentRequest.payment.billing.title,
        email: emailAddress ?? bookingDetails.paymentRequest.payment.billing.email,
        telephone: bookingDetails.paymentRequest.payment.billing.telephone,
        bookerIsNotGuest: bookingDetails.paymentRequest.payment.billing.bookerIsNotGuest,
        differentBillingAddress:
          bookingDetails.paymentRequest.payment.billing.differentBillingAddress,
        addressLine1: bookingDetails.paymentRequest.payment.billing.address.addressLine1,
        addressLine2: bookingDetails.paymentRequest.payment.billing.address.addressLine2,
        addressLine3: bookingDetails.paymentRequest.payment.billing.address.addressLine3,
        addressLine4: bookingDetails.paymentRequest.payment.billing.address.addressLine4,
        cityName: bookingDetails.paymentRequest.payment.billing.address.cityName,
        postalCode: bookingDetails.paymentRequest.payment.billing.address.postalCode,
        country: bookingDetails.paymentRequest.payment.billing.address.country,
        companyName: bookingDetails.paymentRequest.payment.billing.address.companyName,
        addressType: bookingDetails.paymentRequest.payment.billing.address.addressType,
        card: {
          cardHolderLastName: bookingDetails.paymentRequest.payment.card.cardHolderLastName,
          cardHolderFirstName: bookingDetails.paymentRequest.payment.card.cardHolderFirstName,
          cardHolderAddress: {
            addressLine1: bookingDetails.paymentRequest.payment.card.cardHolderAddress.addressLine1,
            addressLine2: bookingDetails.paymentRequest.payment.card.cardHolderAddress.addressLine2,
            addressLine3: bookingDetails.paymentRequest.payment.card.cardHolderAddress.addressLine3,
            addressLine4: bookingDetails.paymentRequest.payment.card.cardHolderAddress.addressLine4,
            cityName: bookingDetails.paymentRequest.payment.card.cardHolderAddress.cityName,
            postalCode: bookingDetails.paymentRequest.payment.card.cardHolderAddress.postalCode,
            country: bookingDetails.paymentRequest.payment.card.cardHolderAddress.country,
            addressType: bookingDetails.paymentRequest.payment.card.cardHolderAddress.addressType,
          },
        },
        bookingType: bookingDetails.paymentRequest.booking.type,
        journey: bookingDetails.paymentRequest.booking.journey,
        channel: bookingDetails.paymentRequest.booking.channel,
        language: bookingDetails.paymentRequest.booking.language,
        identifier: bookingDetails.paymentRequest.booking.businessSite.identifier,
        bookingBusinessSiteType: bookingDetails.paymentRequest.booking.businessSite.type,
        name: bookingDetails.paymentRequest.booking.businessSite.name,
        location: bookingDetails.paymentRequest.booking.businessSite.location,
        businessItems,
      })
      .then(() => {
        setIsEnabledPaymentStatus(true);
      });
    setEckohParameters(undefined);
    setConfirmationStarted(true);
  }

  function initiateIframe() {
    const requestId = Math.random().toString(36).slice(2);

    let isPiba = false;
    let pibaType = '';

    if (selectedPaymentType.type === CcuiCardType.NEW_PIBA) {
      isPiba = true;
      if (selectedPaymentType.name === CardName.PIBA_EU) {
        pibaType = PibaType.PIBA_EU;
      } else {
        pibaType = PibaType.PIBA_UK;
      }
    }

    const paymentTypeAPI = isPiba ? pibaType : selectedPaymentType.name;
    const selectedPaymentMethod = getPaymentMethodType(selectedPaymentType);

    initiateIframeMutation
      .mutateAsync({
        basketReference,
        agentEmail: user.email,
        agentName: user.name,
        country,
        identifier: bookingInformation.hotelId,
        location: bookingSummaryData.hotelInformation?.hotelAddress[0],
        hotelName: replaceWithEmptyString(bookingSummaryData.hotelInformation?.hotelName),
        reservationType: 'HOTEL',
        channel: 'PI',
        journey: 'BOOKING',
        language,
        paymentType: selectedPaymentDetail?.type,
        requestId,
        paymentMethod: paymentTypeAPI,
      })
      .then((data: any) => {
        const paymentId = data.initiateEckohPayment?.paymentId;
        setEckohParameters(
          new EckohParameters(
            replaceWithEmptyString(bookingSummaryData.hotelInformation?.hotelName) || '',
            publicRuntimeConfig?.ECKOH_CLIENT_ID || '',
            user.email,
            language as LanguageEnum,
            paymentId ?? '',
            selectedPaymentDetail?.type as string,
            selectedPaymentMethod as CcuiCardType,
            cardType as CardStatus,
            bookingSummaryData.hotelInformation?.hotelCountry as CountryEnum,
            publicRuntimeConfig?.ECKOH_PROD === 'true'
              ? `mWB-${hiQueryInput?.hotelId}`
              : `mWB-DEFAULT`,
            publicRuntimeConfig?.ECKOH_ENV
          )
        );
        setIsEnabledEckohQuery(true);
        setConfAnalytics({
          ...confAnalytics,
          echoID: paymentId ?? '',
        });
        analytics.update({
          echoID: paymentId ?? '',
        });
      });
  }
  function getPaymentMethodType(selectedPaymentType: PaymentMethod) {
    if (selectedPaymentType.subType != null) {
      return selectedPaymentType.subType;
    } else return selectedPaymentType.type;
  }
}

const headerStyles = {
  color: 'darkGrey1',
  fontSize: '3xxl',
  lineHeight: '5',
  marginTop: '5xl',
  fontStyle: 'normal',
  fontWeight: 'semibold',
} as HeadingProps;

const bookingSummaryDesktopStyle = {
  display: {
    mobile: 'none',
    lg: 'block',
  },
  w: {
    lg: '72',
    xl: '19.31rem',
  },
} as GridItemProps;

const mainPaymentsGridStyle = {
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
  m: '0',
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

const bookingSummaryMobileContainerStyle = {
  display: {
    mobile: 'block',
    lg: 'none',
  },
  backgroundColor: 'lightGrey5',
};

const bookingSummaryMobileTriggerStyle = {
  justifyContent: 'center',
  fontWeight: 'bold',
};

const descriptionStyles = {
  fontWeight: 'normal',
  fontStyle: 'normal',
  fontSize: 'md',
  lineHeight: 3,
  color: 'darkGrey1',
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
