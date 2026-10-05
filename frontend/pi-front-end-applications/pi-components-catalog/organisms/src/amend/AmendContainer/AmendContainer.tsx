import {
  AccordionPanelProps,
  AccordionProps,
  Box,
  BoxProps,
  ButtonProps,
  Flex,
  FlexProps,
  Text,
  TextProps,
} from '@chakra-ui/react';
import { keepPreviousData, useQueryClient } from '@tanstack/react-query';
import type {
  AmendConfirmationResponseSuccess,
  AmendContainerData,
  AmendPaymentProps,
  AmendReservation,
  AmendRoomsAndGuestsLabels,
  AmendRoomType,
  AnalyticsData,
  AnalyticsDataAmend,
  AnalyticsDataCartConfirmation,
  AnalyticsRoomDetails,
  ManageBookingResponse,
  MealItemExtension,
  MealKids,
  RoomSelection,
  SelectedMealsPerRoom,
  SummaryOfPaymentsLabels,
  ConfirmAmendError,
  AmendConfirmationErrorLS,
  MutationErrorResponse,
  BCResponse,
  PrivacyPolicy,
  Packages,
  Customer,
} from '@whitbread-eos/api';
import {
  Area,
  BOOKING_CHANNEL,
  BOOKING_SUBCHANNEL,
  CONFIRM_AMEND_STATUS,
  DASHBOARD_MANAGE_BOOKING,
  DATE_TYPE,
  FS_ENABLE_AMEND_PAY_NOW_PI_BB,
  GET_BOOKING_CONFIRMATION_AMEND,
  GET_SUMMARY_OF_PAYMENTS,
  OfferEnum,
  INITIAL_CONFIRM_AMEND_ERROR_KEY,
  INITIAL_CONFIRM_AMEND_ERROR_VALUE,
  AncillaryFilterData,
  BASKET_STATUS,
  FS_SHOW_AMEND_PAYMENT_PAGE,
  AMEND_3CP_VISITED_KEY,
  AMEND_3CP_VISITED_INITIAL_VALUE,
  TEMPORARY_BASKET_KEY,
  FT_PI_AMEND_DELETE_REG_CARD,
  ExtrasItem,
  EARLY_CHECKIN_IDS,
  LATE_CHECKOUT_IDS,
  PROSECCO_IDS,
  WIFI_IDS,
  FT_PI_BB_CCUI_MAXROOMS_AMEND,
  FT_PI_PROMO_CODE_LANDING_PAGE,
  FT_CCUI_PROMO_CODE_LANDING_PAGE,
  FT_BB_PROMO_CODE_LANDING_PAGE,
  FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY,
  Channel,
  FT_MOBILE_PREREGISTERED_REPURPOSE,
  FT_PI_PIB_CCUI_CITY_TAX_AMEND,
} from '@whitbread-eos/api';
import {
  Accordion,
  Alert,
  ChevronLeft24,
  Error,
  Icon,
  IframeEmbed,
  Info,
  LoadingSpinner,
  Notification,
  Success,
} from '@whitbread-eos/atoms';
import {
  DataSecuritySection,
  FreeFoodKidsNotification,
  PageLoader,
  RestaurantMessage,
  RoomsAndGuests,
  RoomSuccessNotification,
  StayDates,
} from '@whitbread-eos/molecules';
import {
  adultsMealsSelector,
  analytics,
  analyticsConfirmation,
  childrenMealsSelector,
  formatCurrency,
  formatDataTestId,
  formatFindBookingToken,
  formatPriceWithDecimal,
  freeBreakfastMaxAllowance,
  getAuthCookie,
  getFindBookingToken,
  cleanupFindBookingToken,
  getMaxValueFromRoomStays,
  getNightsNumber,
  mealsMapperSelector,
  numberOfSelectionsPerRoomSelector,
  roomInformationSelector,
  securityNoticeMoreInfoDataSelector,
  sortMealsByReservationId,
  updateAmendPageAnalytics,
  useDebounce,
  useFeatureSwitch,
  useIPageSubmission,
  usePackages,
  useQueryRequest,
  useSessionStorage,
  filterPackagesByAncillariesCloseOut,
  getAmendSectionTranslations,
  useFeatureToggle,
  renderSanitizedHtml,
  roomPackageSelection,
  extrasPackagesMapperSelector,
  useGetDiscountRateReservationData,
  isInnBusinessApp,
  PromotionsInformation,
  getRestaurantMessageDisplay,
} from '@whitbread-eos/utils';
import { format, formatISO, isThisMonth } from 'date-fns';
import cloneDeep from 'lodash/cloneDeep';
import isEqual from 'lodash/isEqual';
import { useRouter } from 'next/router';
import React, { useCallback, useEffect, useRef, useState } from 'react';

import AmendPayment from '../AmendPayment';
import { BookingSummaryWrapper } from '../BookingSummary';
import RoomsExtrasSelection from '../RoomsExtrasSelection';
import RoomsMealSelection from '../RoomsMealSelection';
import {
  ANALYTICS_TRACKING_AMEND_CONFIRMATION,
  ANALYTICS_TRACKING_AMEND_SEARCH,
  ANALYTICS_TRACKING_AMEND_ERROR,
  SUBCHANNEL,
  PAY_ON_ARRIVAL,
} from '../constants';
import {
  isStayDatesSectionUpdated,
  getBasketStatus,
  getChangedPreCheckedInReservations,
  shouldAmendStayDates,
  showNotificationNoPromotion,
  shouldShowAmendStayDatesError,
  shouldShowAmendStayDatesErrorNoPromo,
  getPromoCondition,
  handleEditRemoveRoomReset,
  getIsPromoCodeLandingPageEnabled,
} from '../helpers';

declare global {
  interface Window {
    analyticsData: AnalyticsData;
    analyticsDataCartConfirmation: AnalyticsDataCartConfirmation;
    // satellite required for adobe analytics on the confirmation Page
    __satelliteLoaded: boolean;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    _satellite: any;
  }
}

interface Props {
  language: string;
  country: string;
  t: (id: string) => string;
  basketReference: string;
  bookingReference: string;
  temporaryBasketReference: string;
  channel: string;
  data: AmendContainerData;
  variant: Area;
  paymentProps?: AmendPaymentProps;
  amendVisited: boolean;
  userDetails?: Customer;
  status?: string | null;
}

type HandleAmendStayDatesMutationParams = {
  invalidateBookingConfirmation: () => void;
  invalidateAmendSummary: () => void;
  editAmendPageAnalytics: (analyticsChange: string, analytics: { nightsChange: number }) => void;
  originalNumberOfNights: number;
  newStartDate: Date;
  newEndDate: Date;
  setShowPromoNotification: (show: boolean) => void;
  setPromoStayData: (promo: PromotionsInformation | null) => void;
  response: any;
  isStayDatesLoading: boolean;
  isPromotionsInHotelAvailabilityEnabled: boolean;
};

export function handleAmendStayDatesMutation({
  invalidateBookingConfirmation,
  invalidateAmendSummary,
  editAmendPageAnalytics,
  originalNumberOfNights,
  newStartDate,
  newEndDate,
  setShowPromoNotification,
  setPromoStayData,
  response,
  isStayDatesLoading,
  isPromotionsInHotelAvailabilityEnabled,
}: HandleAmendStayDatesMutationParams) {
  invalidateBookingConfirmation();
  invalidateAmendSummary();

  if (isPromotionsInHotelAvailabilityEnabled) {
    const promoInfo = response?.changeBookingDates?.promotionsInformation ?? null;

    setPromoStayData(promoInfo);
    setShowPromoNotification(!!promoInfo?.promoBookingInfo?.promotionCode);
  }

  if (!isStayDatesLoading) {
    const newNumberOfNights = getNightsNumber(
      format(newStartDate, DATE_TYPE.YEAR_MONTH_DAY),
      format(newEndDate, DATE_TYPE.YEAR_MONTH_DAY)
    );

    const analyticsChange = `nights changed from ${originalNumberOfNights} to ${newNumberOfNights}`;

    editAmendPageAnalytics(analyticsChange, {
      nightsChange: newNumberOfNights - originalNumberOfNights,
    });
  }
}

export default function AmendContainer({
  language,
  country,
  t,
  basketReference,
  bookingReference,
  temporaryBasketReference,
  channel,
  data,
  variant,
  paymentProps,
  amendVisited,
  userDetails,
  status,
}: Readonly<Props>) {
  const isInnBusiness = isInnBusinessApp(window?.location?.host ?? '');
  const router = useRouter();

  const baseDataTestId = 'amend';
  const queryClient = useQueryClient();

  const {
    bookingConfirmationData,
    headerInformationData,
    stayRulesData,
    employeeStayRulesData,
    RoomOccupancyLimitationsData,
    amendStayDates,
    addNewRoomMutation,
    addNewRoomIsSuccess,
    addNewRoomIsLoading,
    amendEditRoom,
    removeRoom,
    saveReservation,
    confirmAmend,
    brand,
  } = data;
  const {
    amendStayDatesMutation,
    amendStayDatesIsError,
    amendStayDatesIsSuccess,
    amendStayDatesIsLoading,
  } = amendStayDates;

  const [userHasMadeChanges, setUserHasMadeChanges] = useState<boolean>(false);
  const [mealsSectionHasUpdates, setMealsSectionHasUpdates] = useState<boolean>(false);
  const [tempAmendData, setTempAmendData] = useState<any>('');

  const {
    confirmAmendMutation,
    confirmAmendIsLoading,
    confirmAmendIsError,
    confirmAmendIsSuccess,
  } = confirmAmend;

  const {
    bookingSummaryLabels,
    summaryOfPaymentsLabels: _summaryOfPaymentsLabels,
    notificationLabels,
    removeRoomModalLabels,
    stayDatesLabels: _stayDatesLabels,
    leadGuestValidationLabels,
    leadGuestLabels,
    roomAvailabilityLabels,
  } = getAmendSectionTranslations(t);

  const { amendEditRoomMutation, amendEditRoomIsSuccess, amendEditRoomIsLoading } = amendEditRoom;

  const {
    amendSaveReservationMutation,
    amendSaveReservationIsLoading,
    amendSaveReservationIsSuccess,
  } = saveReservation;

  const { removeRoomIsSuccess, removeRoomIsLoading, removeRoomMutation } = removeRoom;

  const isAdultsDecreased = useRef(false);
  const currentRoom = useRef(0);
  const removedRoomNumber = useRef(0);
  const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

  const isWindowDefined = typeof window !== 'undefined';
  const [origin, setOrigin] = useState('');
  const [email, setEmail] = useState<string>('');
  const [hadCityTax, setHadCityTax] = useState<boolean | null>(null);

  const [, setConfirmAmendErrorValue] = useSessionStorage<AmendConfirmationErrorLS>(
    INITIAL_CONFIRM_AMEND_ERROR_KEY,
    INITIAL_CONFIRM_AMEND_ERROR_VALUE
  );

  const [, setAmend3cpVisited] = useSessionStorage<string>(
    AMEND_3CP_VISITED_KEY,
    AMEND_3CP_VISITED_INITIAL_VALUE
  );

  const isAmendPayNowEnabled = useFeatureSwitch({
    featureSwitchKey: FS_ENABLE_AMEND_PAY_NOW_PI_BB,
    fallbackValue: true,
  });

  const isAmendPaymentPageEnabled = useFeatureSwitch({
    featureSwitchKey: FS_SHOW_AMEND_PAYMENT_PAGE,
    fallbackValue: true,
  });

  const amendConfirmationCommonURL = `/amend/booking-confirmation?bookingReference=${bookingReference}`;
  const amendConfirmationURL =
    variant === Area.BB
      ? `/${country}/${language}/business-booker${amendConfirmationCommonURL}`
      : `/${country}/${language}${amendConfirmationCommonURL}`;

  const discountRateReservationData = useGetDiscountRateReservationData(
    headerInformationData?.headerInformation?.content?.global?.offers,
    bookingConfirmationData,
    variant
  );

  useEffect(() => {
    setOrigin(window.location.origin);
  }, [isWindowDefined]);

  const paramsForBookingConfirmation = {
    basketReference: temporaryBasketReference,
    country,
    language,
  };

  const paramsForBookingChannel = {
    channel,
    subchannel: SUBCHANNEL,
    language,
  };

  const paramsForSummaryOfPayments = {
    originalBasketRef: basketReference,
    copyBasketRef: temporaryBasketReference,
    token: formatFindBookingToken(getFindBookingToken().token),
    bookingChannel: paramsForBookingChannel,
    country: country,
  };

  const {
    data: temporaryBookingData,
    isError: temporaryBookingIsError,
    isSuccess: temporaryBookingIsSuccess,
    isFetching: temporaryBookingIsFetching,
    error: temporaryBookingError,
  } = useQueryRequest(
    ['getBookingConfirmationAmend', temporaryBasketReference, country, language],
    GET_BOOKING_CONFIRMATION_AMEND,
    paramsForBookingConfirmation,
    { enabled: !!temporaryBasketReference, placeholderData: keepPreviousData }
  );

  const {
    data: summaryOfPaymentsData,
    isLoading: summaryOfPaymentsIsLoading,
    isError: summaryOfPaymentsIsError,
    error: summaryOfPaymentsError,
    isFetching: summaryOfPaymentsIsFetching,
  } = useQueryRequest(
    ['AmendSummary', paramsForSummaryOfPayments],
    GET_SUMMARY_OF_PAYMENTS,
    paramsForSummaryOfPayments,
    {
      enabled: !!temporaryBasketReference,
    }
  );

  if (!summaryOfPaymentsIsLoading) {
    updateAmendPageAnalytics(
      {
        ...window.analyticsData,
        revenue: summaryOfPaymentsData?.amendSummary?.totalCost,
        extrasRevenueChange: summaryOfPaymentsData?.amendSummary?.charitable,
        totalRevenueChange:
          summaryOfPaymentsData?.amendSummary?.totalCost -
          summaryOfPaymentsData?.amendSummary?.previousTotal,
      } as AnalyticsDataAmend,
      bookingReference
    );
  }
  const cityTaxTotal = temporaryBookingData?.bookingConfirmation?.cityTaxTotal ?? 0;
  const [isClosed, setIsClosed] = useState(false);
  useEffect(() => {
    if (temporaryBookingIsSuccess && hadCityTax === null) {
      setHadCityTax(cityTaxTotal > 0);
    }
  }, [hadCityTax, temporaryBookingIsSuccess, cityTaxTotal]);

  useEffect(() => {
    updateAmendPageAnalytics(
      {
        ...window.analyticsData,
        change: '',
        foodRevenueChange: 0,
        nightsChange: 0,
        roomTypeChange: false,
        roomsChange: 0,
        validation: '',
      } as AnalyticsDataAmend,
      bookingReference
    );
  }, []);

  useEffect(() => {
    return () => {
      clearInterval(intervalRef.current ?? '');
    };
  }, []);

  useEffect(() => {
    const removeTempBasketFromStorage = () => {
      sessionStorage.removeItem(TEMPORARY_BASKET_KEY);
    };
    router.events.on('routeChangeStart', removeTempBasketFromStorage);
    return () => {
      router.events.off('routeChangeStart', removeTempBasketFromStorage);
    };
  }, []);

  const {
    data: manageBookingData,
    isError: manageBookingIsError,
    isLoading: manageBookingIsLoading,
    error: manageBookingError,
  }: {
    data: ManageBookingResponse;
    isError: boolean;
    error: unknown;
    isLoading: boolean;
  } = useQueryRequest(
    [
      'manageBookingDashBoard',
      basketReference,
      (temporaryBookingData as BCResponse)?.bookingConfirmation?.hotelId,
    ],
    DASHBOARD_MANAGE_BOOKING,
    {
      cancelInformationCriteria: {
        userDateTime: formatISO(Date.now()),
        bookingChannel: {
          channel: channel,
          subchannel: BOOKING_SUBCHANNEL.WEB,
          language: language,
        },
        basketReference: basketReference,
        hotelId: (temporaryBookingData as BCResponse)?.bookingConfirmation?.hotelId,
        token: formatFindBookingToken(getFindBookingToken().token),
      },
    },
    {
      enabled: !!temporaryBookingData?.bookingConfirmation?.hotelId && !!basketReference,
      staleTime: 0,
      cacheTime: 0,
    },
    getAuthCookie()
  );

  const [firstReservation] =
    (temporaryBookingData as BCResponse)?.bookingConfirmation?.reservationByIdList || [];
  const {
    isLoading: packagesIsLoading,
    error: packagesError,
    isError: packagesIsError,
    packages: mealPackagesData,
    restaurant: restaurantData,
    privacyPolicy: privacyPolicyPackData,
  } = usePackages({
    adultsNumber: getMaxValueFromRoomStays(
      temporaryBookingData?.bookingConfirmation?.reservationByIdList,
      'adultsNumber'
    ),
    childrenNumber: getMaxValueFromRoomStays(
      temporaryBookingData?.bookingConfirmation?.reservationByIdList,
      'childrenNumber'
    ),
    hotelId: (temporaryBookingData as BCResponse)?.bookingConfirmation?.hotelId,
    basketReferenceId: temporaryBasketReference,
    endDate: firstReservation?.roomStay?.departureDate,
    startDate: firstReservation?.roomStay?.arrivalDate,
    bookingFlowId: (temporaryBookingData as BCResponse)?.bookingConfirmation?.bookingFlowId,
    nightsNumber: getNightsNumber(
      firstReservation?.roomStay?.arrivalDate,
      firstReservation?.roomStay?.departureDate
    ),
    channel: channel,
    options: {
      enabled: !!temporaryBasketReference && !!temporaryBookingData,
      placeholderData: keepPreviousData,
    },
  });
  const hasAncillariesWifiSelected = mealPackagesData?.roomSelection?.some((selection) =>
    selection.packagesSelection?.some((packageSelection) => {
      const id = packageSelection?.id;
      if (!id) return false;

      return WIFI_IDS.includes(id as (typeof WIFI_IDS)[number]);
    })
  );

  const isPageLoaded = !!temporaryBasketReference;

  const isStayDatesLoading = amendStayDatesIsLoading || temporaryBookingIsFetching;

  const isAddNewRoomLoading = addNewRoomIsLoading || temporaryBookingIsFetching;

  const isEditRoomLoading = amendEditRoomIsLoading || temporaryBookingIsFetching;

  const isRemoveRoomLoading = removeRoomIsLoading || temporaryBookingIsFetching;

  const isRoomLoading = isAddNewRoomLoading || isEditRoomLoading || isRemoveRoomLoading;

  const isSaveReservationLoading = amendSaveReservationIsLoading || temporaryBookingIsFetching;

  const isAccordionLoading = isStayDatesLoading || isRoomLoading || isSaveReservationLoading;

  const [isAddOrRemoveMealLoading, setIsAddOrRemoveMealLoading] = useState(false);

  const isCityTaxBusinessHotel =
    (data?.hotelInformation?.hotelInformation as any)?.cityTax?.isCityTaxBusinessHotel ?? false;
  const isCityTaxHotel =
    (data?.hotelInformation?.hotelInformation as any)?.cityTax?.isCityTaxHotel ?? false;

  const isCityTaxEnabled =
    (variant === Area.BB && isCityTaxBusinessHotel) ||
    (variant === Area.PI && isCityTaxHotel) ||
    (variant === Area.CCUI && (isCityTaxHotel || isCityTaxBusinessHotel));

  const shouldRenderRoomsSection =
    !temporaryBookingIsFetching &&
    !addNewRoomIsLoading &&
    !amendEditRoomIsLoading &&
    !isRemoveRoomLoading &&
    !manageBookingIsLoading;

  const shouldRetryPayment = status === CONFIRM_AMEND_STATUS.paymentError;

  const shouldDisplayAddRoomNotification = !temporaryBookingIsFetching && addNewRoomIsSuccess;
  const shouldDisplayEditRoomNotification = !temporaryBookingIsFetching && amendEditRoomIsSuccess;
  const shouldDisplayRemoveRoomNotification = !temporaryBookingIsFetching && removeRoomIsSuccess;
  const shouldDisplaySaveReservationNotification =
    !temporaryBookingIsFetching && amendSaveReservationIsSuccess;

  const [newMeals, setNewMeals] = useState<SelectedMealsPerRoom[] | undefined>();
  const [prevMeals, setPrevMeals] = useState<SelectedMealsPerRoom[]>();
  const [showPromoNotification, setShowPromoNotification] = useState(false);

  const [mealsNotification, setMealsNotification] = useState('');
  const saveRoomsMealsDebounced = useDebounce(saveRoomsMeals, 1500);

  const [iframeContent, setIframeContent] = useState<string>('');
  const { isPaymentComplete, cardType } = useIPageSubmission();

  const [promoStayData, setPromoStayData] = useState<PromotionsInformation | null>(null);

  useEffect(() => {
    if (!isSaveReservationLoading) {
      setIsAddOrRemoveMealLoading(false);
    }
  }, [isSaveReservationLoading]);

  useEffect(() => {
    if (isPaymentComplete) {
      window.__satelliteLoaded && window._satellite.track(ANALYTICS_TRACKING_AMEND_CONFIRMATION);
      router.push(
        `${amendConfirmationURL}&status=${CONFIRM_AMEND_STATUS.payment}&tempBookingReference=${temporaryBasketReference}&basketReference=${basketReference}`
      );
    }
  }, [isPaymentComplete]);

  useEffect(() => {
    if (cardType) {
      analytics.update({ cardType });
      analyticsConfirmation.update({ cardType });
    }
  }, [cardType]);

  const amendPaymentContainer = document.getElementById('amend-payment-container');
  useEffect(() => {
    if (shouldRetryPayment && amendPaymentContainer) {
      window.requestAnimationFrame(() =>
        amendPaymentContainer.scrollIntoView({
          block: 'start',
          behavior: 'smooth',
        })
      );
    }
  }, [amendPaymentContainer, shouldRetryPayment]);

  const {
    [FT_PI_AMEND_DELETE_REG_CARD]: isDeleteRegCardEnabled,
    [FT_PI_BB_CCUI_MAXROOMS_AMEND]: isAmendMaxRoomsEnabled,
    [FT_PI_PROMO_CODE_LANDING_PAGE]: hasPiPromoCodeLandingPageEnabled,
    [FT_CCUI_PROMO_CODE_LANDING_PAGE]: hasCcuiPromoCodeLandingPageEnabled,
    [FT_BB_PROMO_CODE_LANDING_PAGE]: hasBbPromoCodeLandingPageEnabled,
    [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: isPromotionsInHotelAvailabilityEnabled,
    [FT_MOBILE_PREREGISTERED_REPURPOSE]: isMobilePreRegisteredRepurposeEnabled,
    [FT_PI_PIB_CCUI_CITY_TAX_AMEND]: isCityTaxAmendEnabled,
  } = useFeatureToggle();

  const isPromoCodeLandingPageEnabled = getIsPromoCodeLandingPageEnabled(
    channel as Channel,
    hasPiPromoCodeLandingPageEnabled,
    hasCcuiPromoCodeLandingPageEnabled,
    hasBbPromoCodeLandingPageEnabled
  );

  useEffect(() => {
    if (
      isPageLoaded &&
      isDeleteRegCardEnabled &&
      temporaryBookingData?.bookingConfirmation &&
      temporaryBookingData.bookingConfirmation.reservationByIdList.length ===
        bookingConfirmationData.reservationByIdList.length &&
      tempAmendData === ''
    ) {
      const clonedBookingData = cloneDeep(temporaryBookingData);
      const list = clonedBookingData.bookingConfirmation.reservationByIdList.map(
        (room: AmendReservation) => {
          const matchingRoom = bookingConfirmationData.reservationByIdList.find(
            (orgRoom) =>
              orgRoom.reservationGuestList[0].givenName ===
                room.reservationGuestList[0].givenName &&
              orgRoom.reservationGuestList[0].email === room.reservationGuestList[0].email
          );

          return {
            ...room,
            originalReservationId: matchingRoom?.reservationId,
            preCheckInStatus: matchingRoom?.preCheckInStatus,
            deRegCardCompleted: matchingRoom?.deRegCardCompleted,
          };
        }
      );

      setTempAmendData({
        ...clonedBookingData,
        bookingConfirmation: {
          ...clonedBookingData.bookingConfirmation,
          reservationByIdList: list,
        },
      });
    }
  }, [isPageLoaded, temporaryBookingData, tempAmendData, isDeleteRegCardEnabled]);

  const handleOnAddMeal = useCallback(
    (mealId: string) => {
      if (!saveMealsAllAtOnce) {
        setIsAddOrRemoveMealLoading(true);
        setMealsNotification(t('amend.mealAdded'));
        const selectedMeal = mealPackagesData?.meals?.find(
          (item: MealItemExtension) => item.id === mealId
        );
        const analyticsChange = selectedMeal && `1 ${selectedMeal.name} has been added`;
        const foodRevenueChange = window?.analyticsData?.foodRevenueChange ?? 0;

        editAmendPageAnalytics(analyticsChange, {
          foodRevenueChange: selectedMeal && foodRevenueChange + Number(selectedMeal.price),
        });
        amendSaveReservationMutation.reset();
      }
    },
    [mealsNotification, amendSaveReservationMutation]
  );

  const handleOnRemoveMeal = useCallback(
    (mealId: string) => {
      if (!saveMealsAllAtOnce) {
        setIsAddOrRemoveMealLoading(true);
        setMealsNotification(t('amend.mealRemoved'));
        const selectedMeal = mealPackagesData?.meals?.find(
          (item: MealItemExtension) => item.id === mealId
        );

        const analyticsChange = selectedMeal && `1 ${selectedMeal.name} has been removed`;

        const foodRevenueChange = window?.analyticsData?.foodRevenueChange ?? 0;
        editAmendPageAnalytics(analyticsChange, {
          foodRevenueChange: selectedMeal && foodRevenueChange - Number(selectedMeal.price),
        });
        amendSaveReservationMutation.reset();
      }
    },
    [mealsNotification, amendSaveReservationMutation]
  );

  const onRoomsMealSelect = (
    prevRoomsSelection: SelectedMealsPerRoom[],
    roomsSelections: SelectedMealsPerRoom[],
    selectedRoom: number
  ) => {
    setNewMeals(roomsSelections);
    setPrevMeals(prevRoomsSelection?.length === 0 ? preselectedMeals : prevRoomsSelection);
    currentRoom.current = selectedRoom;
    saveRoomsMealsDebounced();
    if (saveMealsAllAtOnce) {
      setMealsNotification(t('ccui.amend.mealsChangesApplied'));
      updateAnalyticsForAllMeals(roomsSelections, preselectedMeals);
    }
  };

  const updateAnalyticsForAllMeals = (
    roomsSelections: SelectedMealsPerRoom[],
    preselectedMeals: SelectedMealsPerRoom[]
  ) => {
    const oldMealIds = preselectedMeals.reduce((prev: string[], cur: SelectedMealsPerRoom) => {
      prev.push(...cur.adults);
      return prev;
    }, []);

    const newMealIds = roomsSelections.reduce((prev: string[], cur: SelectedMealsPerRoom) => {
      prev.push(...cur.adults);
      return prev;
    }, []);

    const addedMeals = newMealIds
      .filter((id) => !oldMealIds.includes(id))
      .map((id) => ({
        isAdded: true,
        id,
      }));
    const removedMeals = oldMealIds
      .filter((id) => !newMealIds.includes(id))
      .map((id) => ({
        isAdded: false,
        id,
      }));

    [...addedMeals, ...removedMeals].forEach((meal) => {
      const foodRevenueChange = window?.analyticsData?.foodRevenueChange ?? 0;

      const selectedMeal = mealPackagesData?.meals?.find(
        (item: MealItemExtension) => item.id === meal.id
      );
      const analyticsChange =
        selectedMeal && `1 ${selectedMeal.name} has been ${meal.isAdded ? 'added' : 'removed'}`;

      selectedMeal &&
        editAmendPageAnalytics(analyticsChange, {
          foodRevenueChange:
            !packagesIsLoading && meal.isAdded
              ? foodRevenueChange + Number(selectedMeal.price)
              : foodRevenueChange - Number(selectedMeal.price),
        });
    });
  };

  // this cannot be undefined - supposevily
  const { meals, mealsKids } = mealPackagesData!;

  let sortedMeals: RoomSelection[] = [];
  let preselectedMeals: SelectedMealsPerRoom[] = [];
  let selectedMeals: SelectedMealsPerRoom[] = [];
  if (
    mealPackagesData?.roomSelection?.length &&
    (temporaryBookingData as BCResponse)?.bookingConfirmation?.reservationByIdList?.length
  ) {
    sortedMeals = sortMealsByReservationId(
      mealPackagesData.roomSelection,
      (temporaryBookingData as BCResponse)?.bookingConfirmation?.reservationByIdList
    );
  }
  if (sortedMeals.length && meals && mealsKids) {
    const previousSelectedMeals: SelectedMealsPerRoom[] = mealsMapperSelector(
      meals,
      mealsKids,
      sortedMeals
    );
    selectedMeals = previousSelectedMeals;
    preselectedMeals = previousSelectedMeals;
  }

  if (
    !isPageLoaded ||
    !temporaryBookingData ||
    summaryOfPaymentsIsLoading ||
    manageBookingIsLoading ||
    packagesIsLoading
  ) {
    return <PageLoader text={t('searchresults.list.hotel.loading')} />;
  }
  if (confirmAmendIsLoading || confirmAmendIsError || (confirmAmendIsSuccess && !iframeContent)) {
    return <PageLoader text={t('booking.loading')} />;
  }

  if (temporaryBookingIsError) {
    return (
      <Text data-testid={formatDataTestId(baseDataTestId, 'error-temporary-booking-confirmation')}>
        {(temporaryBookingError as Error).message}
      </Text>
    );
  }

  if (summaryOfPaymentsIsError) {
    return (
      <Text data-testid={formatDataTestId(baseDataTestId, 'error-summary-of-payments')}>
        {(summaryOfPaymentsError as Error).message}
      </Text>
    );
  }

  if (manageBookingIsError) {
    return (
      <Text data-testid={formatDataTestId(baseDataTestId, 'error-manage-booking')}>
        {(manageBookingError as Error).message}
      </Text>
    );
  }

  if (packagesIsError) {
    return (
      <Text data-testid={formatDataTestId(baseDataTestId, 'error-meal-packages')}>
        {(packagesError as Error).message}
      </Text>
    );
  }

  const temporaryBookingConfirmation = temporaryBookingData.bookingConfirmation;
  const temporaryFirstReservation = temporaryBookingConfirmation.reservationByIdList[0];
  const originalFirstReservation = bookingConfirmationData.reservationByIdList[0];

  const saveMealsAllAtOnce =
    variant === Area.CCUI && temporaryBookingConfirmation?.reservationByIdList.length > 1;

  const privacyPolicyObj = {
    name: privacyPolicyPackData?.name,
    moreInfoLabel: privacyPolicyPackData?.moreInfoLabel,
    linkSrc: privacyPolicyPackData?.linkSrc,
    linkLabel: privacyPolicyPackData?.linkLabel,
    description: privacyPolicyPackData?.description,
    moreInfo: privacyPolicyPackData?.moreInfo,
  };

  const privacyPolicyData: PrivacyPolicy = securityNoticeMoreInfoDataSelector(privacyPolicyObj);
  const hotelCountry = data?.hotelInformation?.hotelInformation?.address?.country;
  const temporaryArrivalDate = temporaryFirstReservation.roomStay.arrivalDate;
  const temporaryDepartureDate = temporaryFirstReservation.roomStay.departureDate;
  const originalArrivalDate = originalFirstReservation.roomStay.arrivalDate;
  const originalDepartureDate = originalFirstReservation.roomStay.departureDate;

  const originalNumberOfNights = getNightsNumber(originalArrivalDate, originalDepartureDate);

  const temporaryNumberOfNights = getNightsNumber(temporaryArrivalDate, temporaryDepartureDate);

  const hasEmployeeOfferReservation = bookingConfirmationData.reservationByIdList.some(
    (room: AmendReservation) => room.roomStay.ratePlanCode === OfferEnum.EMPLOYEE_RATE_CODE
  );

  const getMaxRooms = (
    hasEmployeeOfferReservation: boolean,
    discountRateReservationData: { isDiscountRate: boolean; maxRooms: number }
  ) => {
    if (discountRateReservationData?.isDiscountRate) {
      return discountRateReservationData?.maxRooms;
    }
    // employee offer
    if (
      hasEmployeeOfferReservation &&
      employeeStayRulesData?.globalConfig?.maxRoomsLim?.maxRooms !== undefined
    ) {
      return isAmendMaxRoomsEnabled
        ? employeeStayRulesData?.globalConfig?.maxRoomsLim?.maxRoomsAmend
        : employeeStayRulesData?.globalConfig?.maxRoomsLim?.maxRooms;
    }
    // non-offer
    return isAmendMaxRoomsEnabled
      ? stayRulesData?.globalConfig?.maxRoomsLim?.maxRoomsAmend
      : stayRulesData?.globalConfig?.maxRoomsLim?.maxRooms;
  };

  const stayDatesData = {
    hotelName: temporaryBookingConfirmation.hotelName,
    arrivalDate: new Date(temporaryArrivalDate),
    departureDate: new Date(temporaryDepartureDate),
    maxNights: stayRulesData?.maxNightsLimitation.maxNights,
    maxRooms: getMaxRooms(hasEmployeeOfferReservation, discountRateReservationData),
    maxArrivalDate: stayRulesData?.maxArrivalDateLimitation.maxArrivalDate,
    originalArrivalDate: new Date(originalArrivalDate),
    originalDepartureDate: new Date(originalDepartureDate),
  };

  const roomAndGuestsData = {
    reservations: temporaryBookingConfirmation.reservationByIdList,
    currencyCode: temporaryBookingConfirmation.currencyCode,
  };

  const hotelAvailabilityParams = {
    arrival: format(stayDatesData.arrivalDate, DATE_TYPE.YEAR_MONTH_DAY),
    bookingChannel: {
      channel: channel,
      language: language,
      subchannel: SUBCHANNEL,
    },
    channel: temporaryBookingConfirmation?.channel,
    companyId: temporaryBookingConfirmation?.companyId,
    country: country,
    departure: format(stayDatesData.departureDate, DATE_TYPE.YEAR_MONTH_DAY),
    hotelId: temporaryBookingConfirmation?.hotelId,
    language: language,
    rooms: [],
    rateCode: temporaryBookingConfirmation?.reservationByIdList[0].roomStay.ratePlanCode,
    ratePlanCodes:
      hasEmployeeOfferReservation && variant === Area.PI
        ? [OfferEnum.EMPLOYEE]
        : [temporaryBookingConfirmation?.reservationByIdList[0].roomStay.ratePlanCode],
    originalBasketReference: basketReference,
  };

  const selectedPaymentMethod = paymentProps?.selectedPaymentDetail?.type ?? PAY_ON_ARRIVAL;
  const additionalAmountLabel =
    selectedPaymentMethod === PAY_ON_ARRIVAL ? t('amend.balance.poaNew') : t('amend.balance.pnNew');

  const stayDatesLabels = {
    ..._stayDatesLabels,
    invalidNights: headerInformationData?.headerInformation?.form?.invalidNights ?? '',
  };

  const roomsAndGuestsLabels: AmendRoomsAndGuestsLabels = {
    roomModalLabels: {
      roomDropdownLabels: {
        single: headerInformationData?.headerInformation?.content?.global?.single ?? '',
        double: headerInformationData?.headerInformation?.content?.global?.double ?? '',
        accessible: headerInformationData?.headerInformation?.content?.global?.accessible ?? '',
        twin: headerInformationData?.headerInformation?.content?.global?.twin ?? '',
        family: headerInformationData?.headerInformation?.content?.global?.family ?? '',
      },
      roomDropdownRoomCodes: headerInformationData?.headerInformation?.config?.roomCodes ?? {},
      roomAvailabilityLabels: {
        adult: headerInformationData?.headerInformation?.content?.global?.adult ?? '',
        adults: headerInformationData?.headerInformation?.content?.global?.adults ?? '',
        child: headerInformationData?.headerInformation?.content?.global?.child ?? '',
        children: headerInformationData?.headerInformation?.content?.global?.children ?? '',
        ...roomAvailabilityLabels,
      },
      leadGuestLabels: {
        ...leadGuestLabels,
      },
      leadGuestValidationLabels: {
        ...leadGuestValidationLabels,
      },
      notificationLabels: {
        ...notificationLabels,
      },
    },
    removeRoomModalLabels: {
      ...removeRoomModalLabels,
    },
    roomLabel: headerInformationData?.headerInformation?.content?.global?.roomLabel ?? '',
    edit: t('amend.edit'),
    remove: t('amend.removeRoom'),
  };

  const summaryOfPaymentsLabels: SummaryOfPaymentsLabels = {
    ..._summaryOfPaymentsLabels,
    additionalAmount: additionalAmountLabel,
  };

  const roomSuccessfullyAddedLabel =
    roomsAndGuestsLabels.roomModalLabels.roomAvailabilityLabels.roomSuccessfullyAdded.replace(
      '[number]',
      `${temporaryBookingConfirmation.reservationByIdList?.length}`
    );

  const roomSuccessfullyUpdatedLabel =
    roomsAndGuestsLabels.roomModalLabels.roomAvailabilityLabels.roomSuccessfullyUpdated.replace(
      '[number]',
      `${currentRoom.current + 1}`
    );

  const resetMealsAfterEditRoomLabel =
    roomsAndGuestsLabels.roomModalLabels.notificationLabels.description.replace(
      '[number]',
      `${currentRoom.current + 1}`
    );

  const roomSuccessfullyRemovedLabel =
    roomsAndGuestsLabels.removeRoomModalLabels.roomSuccessfullRemoved.replace(
      '[number]',
      `${removedRoomNumber.current}`
    );

  const roomsLimitDescriptionNotification =
    headerInformationData?.headerInformation?.results?.notifications?.groupBookingMessage;

  const ccuiRoomsLimitDescriptionNotification =
    headerInformationData?.headerInformation?.results?.notifications?.ccuiGroupBookingMessage;
  const roomsLimitDescriptionEmployeeOfferNotification =
    headerInformationData?.headerInformation?.results?.notifications?.emp01groupBookingMessage;

  const alertIcon = <Alert color="var(--chakra-colors-alert)" />;
  const stayDatesSectionHasUpdates = isStayDatesSectionUpdated(
    originalArrivalDate,
    originalDepartureDate,
    temporaryArrivalDate,
    temporaryDepartureDate
  );

  const isPaymentMethodSelected = !!(
    ([Area.PI, Area.BB].includes(variant) && paymentProps?.selectedPaymentDetail?.type) ||
    variant === Area.CCUI
  );

  const adultsMeals: MealItemExtension[] = adultsMealsSelector(
    mealPackagesData?.meals,
    temporaryNumberOfNights
  );

  const showFreeFoodKidsNotification =
    adultsMeals.some((meal: MealItemExtension) => meal.freeBreakfastOption) &&
    freeBreakfastMaxAllowance(adultsMeals) > 0 &&
    temporaryBookingConfirmation?.reservationByIdList.some(
      (room: AmendReservation) => room.roomStay.childrenNumber > 0
    );

  const { balancePaid, payOnArrival, previousTotal, paymentCardDetails, navigationOptions } =
    summaryOfPaymentsData?.amendSummary || {};
  const expirationDate = paymentCardDetails?.expirationDate;
  const isPayNow = balancePaid !== 0;
  const isCardAvailable = expirationDate && !isThisMonth(new Date(expirationDate));

  const shouldDisplayPaymentSection =
    isAmendPayNowEnabled &&
    isPayNow &&
    isCardAvailable &&
    payOnArrival > 0 &&
    balancePaid + payOnArrival > previousTotal;

  const showEciLcoNotification = getRoomsPackages()?.some((room) => {
    return (
      room?.selectedExtrasList?.packagesSelection &&
      room.selectedExtrasList.packagesSelection?.some(
        (extrasItem) =>
          extrasItem.id &&
          (EARLY_CHECKIN_IDS.includes(extrasItem.id as (typeof EARLY_CHECKIN_IDS)[number]) ||
            LATE_CHECKOUT_IDS.includes(extrasItem.id as (typeof LATE_CHECKOUT_IDS)[number]))
      )
    );
  });
  const extrasItemsPrices = {
    eciPrice: mealPackagesData?.extrasItems?.find(
      (item: ExtrasItem) =>
        item?.id && EARLY_CHECKIN_IDS.includes(item.id as (typeof EARLY_CHECKIN_IDS)[number])
    )?.price,

    lcoPrice: mealPackagesData?.extrasItems?.find(
      (item: ExtrasItem) =>
        item?.id && LATE_CHECKOUT_IDS.includes(item.id as (typeof LATE_CHECKOUT_IDS)[number])
    )?.price,

    wifiPrice: mealPackagesData?.extrasItems?.find(
      (item: ExtrasItem) => item?.id && WIFI_IDS.includes(item.id as (typeof WIFI_IDS)[number])
    )?.price,

    bOfProseccoPrice: mealPackagesData?.extrasItems?.find(
      (item: ExtrasItem) =>
        item?.id && PROSECCO_IDS.includes(item.id as (typeof PROSECCO_IDS)[number])
    )?.price,
  };

  const onIframeLoad = (time: string) => {
    analytics.update({
      paymentLoadTime: time,
    });
  };

  const redirectToAmendPaymentCCUI = () => {
    router.push(
      `/${country}/${language}/amend/payment?tempBasketReference=${temporaryBasketReference}&basketReference=${basketReference}&bookingReference=${bookingReference}`
    );
  };
  const showAmendStayDatesNotification = shouldAmendStayDates(
    isPromoCodeLandingPageEnabled,
    isStayDatesLoading,
    amendStayDatesIsSuccess
  );
  const showNotificationNoPromotions = showNotificationNoPromotion(
    showPromoNotification,
    isStayDatesLoading,
    amendStayDatesIsSuccess
  );
  const showAmendStayDatesError = shouldShowAmendStayDatesError(
    promoStayData?.isWithinPromoWindow,
    isStayDatesLoading,
    amendStayDatesIsError
  );
  const showAmendStayDatesErrorNoPromo = shouldShowAmendStayDatesErrorNoPromo(
    isPromoCodeLandingPageEnabled,
    promoStayData?.promoBookingInfo?.promotionCode,
    isStayDatesLoading,
    amendStayDatesIsError
  );

  const isRedirectToAmendPaymentPageEnabled =
    navigationOptions?.amendPaymentPage && isAmendPaymentPageEnabled;

  const promoCondition = getPromoCondition({
    isPromoCodeLandingPageEnabled,
    amendEditRoomIsSuccess,
    removeRoomIsSuccess,
    addNewRoomIsSuccess,
    mealsSectionHasUpdates,
    promoStayData,
  });

  const canConfirmChanges =
    (userHasMadeChanges ||
      stayDatesSectionHasUpdates ||
      mealsSectionHasUpdates ||
      amendVisited ||
      isRedirectToAmendPaymentPageEnabled) &&
    !amendStayDatesIsError &&
    !isAccordionLoading &&
    !summaryOfPaymentsIsFetching &&
    isPaymentMethodSelected &&
    promoCondition;

  if (iframeContent) {
    return (
      <Flex {...amendContainerStyle}>
        <Box {...detailsStyle}>
          <IframeEmbed
            iframeId="paymentFrame"
            iframeContent={iframeContent}
            onIframeLoad={onIframeLoad}
          />
        </Box>
        <BookingSummaryWrapper
          language={language}
          bookingInformation={temporaryBookingConfirmation}
          roomsPackages={getRoomsPackages()}
          originalArrivalDate={originalArrivalDate}
          originalDepartureDate={originalDepartureDate}
          stayDatesLabels={stayDatesLabels}
          roomsAndGuestsLabels={roomsAndGuestsLabels}
          bookingSummaryLabels={bookingSummaryLabels}
          summaryOfPayments={summaryOfPaymentsData?.amendSummary}
          summaryOfPaymentsLabels={summaryOfPaymentsLabels}
          isConfirmButtonEnabled={canConfirmChanges}
          onConfirmChanges={handleConfirmChanges}
          hideConfirmButton={true}
          isRedirectToAmendPaymentEnabled={isRedirectToAmendPaymentPageEnabled}
          handleRedirectToAmendPayment={redirectToAmendPaymentCCUI}
          variant={variant}
          px="0"
          setEmailCallback={setEmail}
          showEciLcoNotification={showEciLcoNotification}
          isCityTaxEnabled={isCityTaxEnabled}
          isCityTaxAmendEnabled={isCityTaxAmendEnabled}
        />
      </Flex>
    );
  }

  return (
    <Flex {...amendContainerStyle}>
      <Box {...detailsStyle}>
        <Text {...titleStyle} data-testid={formatDataTestId(baseDataTestId, 'page-title')}>
          {t('booking.management.bookingReview.amendYourBookingTitle')}
        </Text>
        <Box
          {...notificationAlertEciLcoStyle}
          data-testid={formatDataTestId(baseDataTestId, 'Notification-Alert-EciLco')}
        >
          {showEciLcoNotification && (
            <Notification
              status="warning"
              data-testid="notification-alert-ecilco"
              description={renderSanitizedHtml(t('ancillaries.amend.notification')) as string}
              variant="alert"
              svg={<Alert />}
            />
          )}
        </Box>
        <Box>
          {isCityTaxAmendEnabled && isCityTaxEnabled && hadCityTax == false && cityTaxTotal > 0 && (
            <Notification
              data-testid={`${baseDataTestId}-city-tax-notification`}
              status="warning"
              variant="alert"
              showCloseButton={true}
              isClosed={isClosed}
              onClick={() => setIsClosed(true)}
              title={t('amend.cityTax.title')}
              description={renderSanitizedHtml(t('amend.cityTax.message')) as string}
              svg={<Alert />}
              wrapperStyles={{ ...notificationAlertEciLcoStyle, mb: 'var(--chakra-space-lg)' }}
            />
          )}
        </Box>

        <Accordion
          accordionItems={renderAccordionItems()}
          bgColor="baseWhite"
          accordionOverwriteStyles={accordionOverwriteStyles}
          allowMultiple={false}
          allowToggle={true}
        />
        <Box mt="2xl">
          {shouldDisplayPaymentSection && paymentProps && (
            <AmendPayment
              bookingConfirmation={bookingConfirmationData}
              amendSummary={summaryOfPaymentsData?.amendSummary}
              continueToNextStep={handleConfirmChanges}
              shouldRetryPayment={shouldRetryPayment}
              additionalAmountLabel={additionalAmountLabel}
              temporaryBasketReference={temporaryBasketReference}
              {...paymentProps}
            />
          )}
        </Box>
        <Flex
          data-testid={formatDataTestId(baseDataTestId, 'BackToHomePageButton')}
          {...returnButtonWrapper}
          onClick={handleBackToHomepage}
        >
          <Icon svg={<ChevronLeft24 />} />
          <Text {...returnButtonTextStyle}>
            {variant === Area.CCUI ? t('ccui.amend.backToSearch') : t('amend.anonymousCancelLink')}
          </Text>
        </Flex>
        {[Area.PI, Area.BB].includes(variant) && (
          <DataSecuritySection
            privacyPolicy={privacyPolicyData}
            prefixDataTestId={baseDataTestId}
            containerStyle={containerStyle}
          />
        )}
      </Box>

      <BookingSummaryWrapper
        language={language}
        bookingInformation={temporaryBookingConfirmation}
        roomsPackages={getRoomsPackages()}
        originalArrivalDate={originalArrivalDate}
        originalDepartureDate={originalDepartureDate}
        stayDatesLabels={stayDatesLabels}
        roomsAndGuestsLabels={roomsAndGuestsLabels}
        bookingSummaryLabels={bookingSummaryLabels}
        summaryOfPayments={summaryOfPaymentsData?.amendSummary}
        summaryOfPaymentsLabels={summaryOfPaymentsLabels}
        isConfirmButtonEnabled={canConfirmChanges}
        onConfirmChanges={handleConfirmChanges}
        isRedirectToAmendPaymentEnabled={isRedirectToAmendPaymentPageEnabled}
        handleRedirectToAmendPayment={redirectToAmendPaymentCCUI}
        variant={variant}
        px="0"
        setEmailCallback={setEmail}
        showEciLcoNotification={showEciLcoNotification}
        extrasItemsPrices={extrasItemsPrices}
        isCityTaxEnabled={isCityTaxEnabled}
        isCityTaxAmendEnabled={isCityTaxAmendEnabled}
      />
    </Flex>
  );

  function renderYourMealsContent() {
    if (
      isAddNewRoomLoading ||
      isEditRoomLoading ||
      isSaveReservationLoading ||
      isAddOrRemoveMealLoading
    ) {
      return <LoadingSpinner wrapperStyle={loadingSpinnerStyle} />;
    }

    const ancillaryCloseoutData = data?.hotelInformation?.hotelInformation?.ancillaryCloseout;

    let isAncillaryCloseout = false;

    const ancillaryData: AncillaryFilterData = {
      arrivalDate: firstReservation?.roomStay?.arrivalDate,
      departureDate: firstReservation?.roomStay?.departureDate,
      ancillaryCloseoutData: ancillaryCloseoutData ?? { items: [] },
      adultsMeals: mealPackagesData?.meals ?? [],
      childrenMeals: mealPackagesData?.mealsKids ?? [],
    };
    const { filteredAdultsMeals, filteredChildrenMeals } =
      filterPackagesByAncillariesCloseOut(ancillaryData);
    mealPackagesData!.meals = filteredAdultsMeals;
    mealPackagesData!.mealsKids = filteredChildrenMeals;
    if (filteredAdultsMeals?.length === 0) {
      isAncillaryCloseout = true;
    }

    if (restaurantData?.restaurantNotFound || restaurantData?.noMealsFound || isAncillaryCloseout) {
      const defaultRestaurantTitle = t('upsell.heading.restaurant.closure');
      const defaultRestaurantDescription = t('upsell.message.restaurant.closure');
      const restaurantMessageDisplay = getRestaurantMessageDisplay({
        restaurant: restaurantData,
        ancillaryCloseoutData,
        arrivalDate: firstReservation?.roomStay?.arrivalDate,
        departureDate: firstReservation?.roomStay?.departureDate,
      });

      return (
        <Box my="2xl" mx="lg">
          <RestaurantMessage
            messageDescription={
              restaurantMessageDisplay.displayDescription || defaultRestaurantDescription
            }
            messageTitle={restaurantMessageDisplay.displayTitle || defaultRestaurantTitle}
            prefixDataTestId={baseDataTestId}
          />
        </Box>
      );
    }
    return (
      <>
        {showFreeFoodKidsNotification && (
          <Box mt="2xl" mx="var(--chakra-space-lg)">
            <FreeFoodKidsNotification prefixDataTestId={baseDataTestId} />
          </Box>
        )}
        {!amendSaveReservationIsLoading &&
          shouldDisplaySaveReservationNotification &&
          mealsNotification && (
            <RoomSuccessNotification
              description={mealsNotification}
              dataTestId="RoomsMealSelection-notification"
              styles={{ mt: showFreeFoodKidsNotification ? 'lg' : '2xl', mb: 0 }}
            />
          )}

        <RoomsMealSelection
          rooms={temporaryBookingConfirmation.reservationByIdList}
          mealPackagesData={{ ...mealPackagesData, roomSelection: sortedMeals }}
          restaurantLogo={restaurantData?.logoSrc ?? ''}
          onSaveReservation={onRoomsMealSelect}
          handleOnAddMeal={handleOnAddMeal}
          handleOnRemoveMeal={handleOnRemoveMeal}
          currentRoom={currentRoom.current}
          saveMealsAllAtOnce={saveMealsAllAtOnce}
        />
      </>
    );
  }

  function renderAccordionItems() {
    const accordionItems = [
      {
        title: t('amend.stayDate'),
        content: (
          <>
            <StayDates
              data={stayDatesData}
              labels={stayDatesLabels}
              baseDataTestId={baseDataTestId}
              onAmendStayDates={handleAmendStayDates}
              isLoading={isStayDatesLoading}
              language={language}
              isCancellable={manageBookingData?.manageBooking?.isCancellable}
              variant={variant}
              country={country}
              channel={channel as Channel}
              brand={brand}
              promoStayData={promoStayData}
              setPromoStayData={setPromoStayData}
              showPromoNotification={showPromoNotification}
              setShowPromoNotification={setShowPromoNotification}
              basketReference={basketReference}
              isPromoCodeLandingPageEnabled={isPromoCodeLandingPageEnabled}
            />
            {(showAmendStayDatesNotification || showNotificationNoPromotions) && (
              <>
                <Notification
                  data-testid={`${baseDataTestId}-available-dates-notification`}
                  status="success"
                  variant="success"
                  title=""
                  description={t('amend.roomAvailable')}
                  svg={<Success />}
                  isInnerHTML={false}
                  wrapperStyles={{ ...notificationCommonStyles, mb: 'var(--chakra-space-lg)' }}
                />
                <Notification
                  data-testid={`${baseDataTestId}-update-stay-dates`}
                  status="info"
                  variant="info"
                  title=""
                  description={getStayDatesUpdate({
                    originalArrival: originalArrivalDate,
                    temporaryArrival: temporaryArrivalDate,
                    originalNumberOfNights: originalNumberOfNights,
                    temporaryNumberOfNights: temporaryNumberOfNights,
                    temporaryNewTotal: Number(temporaryBookingConfirmation.newTotal),
                    currency: temporaryBookingConfirmation.currencyCode,
                    language,
                    text: t('amend.amendNotification'),
                  })}
                  svg={<Info />}
                  isInnerHTML
                  wrapperStyles={{ ...notificationCommonStyles, mb: 'var(--chakra-space-lg)' }}
                />
              </>
            )}

            {(showAmendStayDatesError || showAmendStayDatesErrorNoPromo) && (
              <Notification
                data-testid={`${baseDataTestId}-available-dates-notification`}
                status="error"
                variant="error"
                title={t('amend.roomsUnavailable')}
                description={t('amend.roomsUnavailableDescription')}
                svg={<Error />}
                isInnerHTML={false}
                wrapperStyles={{ ...notificationCommonStyles, mb: 'var(--chakra-space-lg)' }}
              />
            )}
          </>
        ),
        onToggleSection: () => amendStayDatesMutation.reset(),
      },
      {
        title: t('amend.roomAndGuests'),
        content: (
          <>
            {isRoomLoading ? (
              <LoadingSpinner wrapperStyle={loadingSpinnerStyle} />
            ) : (
              <>
                {shouldDisplayAddRoomNotification && (
                  <RoomSuccessNotification
                    description={roomSuccessfullyAddedLabel}
                    dataTestId="add-room-success-notification"
                  />
                )}
                {shouldDisplayEditRoomNotification && (
                  <RoomSuccessNotification
                    description={roomSuccessfullyUpdatedLabel}
                    dataTestId="edit-room-success-notification"
                  />
                )}
                {shouldDisplayRemoveRoomNotification && (
                  <RoomSuccessNotification
                    description={roomSuccessfullyRemovedLabel}
                    dataTestId="remove-room-success-notification"
                  />
                )}
                {shouldRenderRoomsSection && (
                  <RoomsAndGuests
                    data={roomAndGuestsData}
                    baseDataTestId={baseDataTestId}
                    roomRules={RoomOccupancyLimitationsData}
                    labels={roomsAndGuestsLabels}
                    language={language}
                    hotelAvailabilityParams={hotelAvailabilityParams}
                    onSaveNewRoom={handleSaveNewRoom}
                    onUpdateRoom={handleUpdateRoom}
                    onRemoveRoom={handleRemoveRoom}
                    maxRooms={getMaxRooms(hasEmployeeOfferReservation, discountRateReservationData)}
                    variant={variant}
                    isCancellable={manageBookingData?.manageBooking?.isCancellable}
                    isAmendable={!!manageBookingData?.manageBooking?.isAmendable}
                    brand={brand}
                    channel={channel as Channel}
                    hotelCountry={hotelCountry}
                    userDetails={userDetails}
                    isPromoCodeLandingPageEnabled={isPromoCodeLandingPageEnabled}
                  />
                )}

                {shouldDisplayEditRoomNotification && isAdultsDecreased.current && (
                  <Notification
                    status="warning"
                    variant="alert"
                    svg={<Alert />}
                    title={t('amend.notification.reset.header')}
                    description={resetMealsAfterEditRoomLabel}
                    data-testid={`edit-room-warning-notification`}
                    wrapperStyles={{ ...notificationCommonStyles, mb: 'var(--chakra-space-xl)' }}
                  />
                )}
                {/*  stayDatesData.maxRooms - uses maxRooms or maxRoomsAmend */}
                {roomAndGuestsData?.reservations.length >= stayDatesData.maxRooms && (
                  <Notification
                    data-testid={`${baseDataTestId}-max-rooms-notification`}
                    status="warning"
                    variant="alert"
                    title={
                      discountRateReservationData?.unAvailableMessage ||
                      headerInformationData?.headerInformation?.results?.notifications
                        ?.groupBookingHeader
                    }
                    description={renderMaxRoomsMessage(
                      hasEmployeeOfferReservation,
                      discountRateReservationData
                    )}
                    svg={alertIcon}
                    isInnerHTML={false}
                    wrapperStyles={{ ...notificationCommonStyles, mb: 'var(--chakra-space-2xl)' }}
                  />
                )}
              </>
            )}
          </>
        ),
        onToggleSection: () => {
          addNewRoomMutation.reset();
          handleEditRemoveRoomReset({
            isPromoCodeLandingPageEnabled,
            amendEditRoomMutation,
            removeRoomMutation,
          });
        },
      },
    ];

    const noSelectedMeals = !mealPackagesData?.roomSelection?.find(
      (room: RoomSelection) => room?.packagesSelection?.length !== 0
    );

    if (!(restaurantData?.restaurantNotFound && noSelectedMeals)) {
      accordionItems.push({
        title: t('amend.yourMeals'),
        content: renderYourMealsContent(),
        onToggleSection: () => {
          amendSaveReservationMutation.reset();
        },
      });
    }

    if (
      hasAncillariesWifiSelected &&
      mealPackagesData &&
      temporaryBookingConfirmation?.reservationByIdList
    ) {
      accordionItems.push({
        title: t('amend.extras.extras'),
        content: renderExtras(
          temporaryBookingConfirmation.reservationByIdList,
          mealPackagesData,
          currentRoom.current
        ),
        onToggleSection: () => {
          amendSaveReservationMutation.reset();
        },
      });
    }

    return accordionItems;
  }

  function renderMaxRoomsMessage(
    hasEmployeeOfferReservation: boolean,
    discountRateReservationData: { isDiscountRate: boolean; roomLimitMessage: string }
  ) {
    if (discountRateReservationData?.isDiscountRate && [Area.PI, Area.CCUI].includes(variant)) {
      return discountRateReservationData?.roomLimitMessage;
    }

    if (variant === Area.CCUI) {
      return ccuiRoomsLimitDescriptionNotification;
    }

    if (hasEmployeeOfferReservation && variant === Area.PI) {
      return roomsLimitDescriptionEmployeeOfferNotification;
    }
    return roomsLimitDescriptionNotification;
  }

  function invalidateBookingConfirmation() {
    queryClient
      .invalidateQueries({
        queryKey: ['getBookingConfirmationAmend', temporaryBasketReference, country, language],
      })
      .catch(() => {
        console.log('invalidate fail');
      });
  }

  function invalidatePackages() {
    queryClient.invalidateQueries({ queryKey: ['GetPackages'] }).catch(() => {
      console.log('invalidate fail');
    });
  }

  function invalidateAmendSummary() {
    queryClient.invalidateQueries({ queryKey: ['AmendSummary'] }).catch(() => {
      console.log('invalidate fail');
    });
  }

  function editAmendPageAnalytics(changeValue: any, newAnalyticsParams?: any) {
    const analytics = window?.analyticsData;

    const paramsForAnalytics = {
      ...analytics,
      ...newAnalyticsParams,
      change: changeValue,
    };

    window.__satelliteLoaded && window._satellite.track(ANALYTICS_TRACKING_AMEND_SEARCH);
    updateAmendPageAnalytics(paramsForAnalytics as AnalyticsDataAmend, bookingReference);
  }

  function handleAmendStayDates(newStartDate: Date, newEndDate: Date) {
    const paramsForQuery = {
      tempBookingRef: temporaryBasketReference,
      channel,
      subchannel: SUBCHANNEL,
      language: language?.toUpperCase(),
      newStartDate: format(newStartDate, DATE_TYPE.YEAR_MONTH_DAY),
      newEndDate: format(newEndDate, DATE_TYPE.YEAR_MONTH_DAY),
      token: getFindBookingToken().token,
      ...(isPromotionsInHotelAvailabilityEnabled && {
        brand,
        country,
        originalBasketReference: basketReference,
      }),
    };

    amendStayDatesMutation
      .mutateAsync(paramsForQuery)
      .then(async (response: any) => {
        handleAmendStayDatesMutation({
          invalidateBookingConfirmation,
          invalidateAmendSummary,
          isPromotionsInHotelAvailabilityEnabled,
          response,
          setPromoStayData,
          setShowPromoNotification,
          isStayDatesLoading,
          newStartDate,
          newEndDate,
          originalNumberOfNights,
          editAmendPageAnalytics,
        });
      })
      .catch((error: MutationErrorResponse) => {
        console.log('Amend failed:', error);
      });
  }

  function handleSaveNewRoom(newRoom: AmendRoomType) {
    const employeeRatePlanCode = OfferEnum.EMPLOYEE_RATE_CODE;
    const params = {
      ...newRoom,
      channel: channel,
      subchannel: SUBCHANNEL,
      language: language,
      tempBookingRef: temporaryBasketReference,
      token: getFindBookingToken().token,
      ratePlanCode:
        // set 'ratePlanCode' prop for hotelAvailability call, in order to pass Employee offer rate as EMPLOYEE
        hasEmployeeOfferReservation && channel === BOOKING_CHANNEL.PI ? employeeRatePlanCode : '',
    };
    addNewRoomMutation.mutateAsync(params).then(() => {
      invalidateBookingConfirmation();
      invalidateAmendSummary();
      invalidatePackages();
      setUserHasMadeChanges(true);

      if (!addNewRoomIsLoading) {
        const analyticsAddRoom = (window?.analyticsData?.roomsChange ?? 0) + 1;

        editAmendPageAnalytics('1 room added', {
          roomsChange: analyticsAddRoom,
        });
      }
    });

    amendEditRoomMutation.reset();
    removeRoomMutation.reset();
  }

  function handleUpdateRoom(
    selectedRoom: AmendRoomType,
    reservationId: string,
    roomNumber: number,
    analyticsRoomDetails?: AnalyticsRoomDetails
  ) {
    const params = {
      ...selectedRoom,
      reservationId: reservationId,
      channel: channel,
      subchannel: SUBCHANNEL,
      language: language,
      tempBookingRef: temporaryBasketReference,
      token: getFindBookingToken().token,
    };

    const { isChanged, roomType, changeAdults } = analyticsRoomDetails as AnalyticsRoomDetails;

    amendEditRoomMutation.mutateAsync(params).then(async () => {
      invalidatePackages();
      invalidateBookingConfirmation();
      invalidateAmendSummary();
      setUserHasMadeChanges(true);
      addNewRoomMutation.reset();
      removeRoomMutation.reset();
      currentRoom.current = roomNumber - 1;

      if (analyticsRoomDetails && !amendEditRoomIsLoading) {
        let analyticsChange;

        if (isChanged && changeAdults) {
          analyticsChange = `Room type changed to ${roomType} and ${changeAdults}`;

          isAdultsDecreased.current = changeAdults?.includes('removed') ?? false;
        } else if (isChanged && !changeAdults) {
          analyticsChange = `Room type changed to ${roomType}`;
        }

        editAmendPageAnalytics(analyticsChange, {
          roomTypeChange: isChanged,
        });
      }
    });
  }

  function handleRemoveRoom(reservationId: string, roomNumber: number) {
    const params = {
      reservationId,
      channel: channel,
      subchannel: SUBCHANNEL,
      language: language,
      tempBookingRef: temporaryBasketReference,
      token: formatFindBookingToken(getFindBookingToken().token),
    };
    removeRoomMutation.mutateAsync(params).then(() => {
      invalidateBookingConfirmation();
      invalidateAmendSummary();
      invalidatePackages();
      setUserHasMadeChanges(true);

      if (!isRemoveRoomLoading) {
        const analyticsRemoveRoom = (window?.analyticsData?.roomsChange ?? 0) - 1;

        editAmendPageAnalytics('1 room removed', {
          roomsChange: analyticsRemoveRoom,
        });
      }
    });

    addNewRoomMutation.reset();
    amendEditRoomMutation.reset();
    removedRoomNumber.current = roomNumber;
    currentRoom.current = 0;
  }

  function handleConfirmChanges() {
    const params = {
      tempBookingRef: temporaryBasketReference,
      originalBookingRef: basketReference,
      channel,
      subchannel: SUBCHANNEL,
      language: language?.toUpperCase(),
      token: getFindBookingToken().token,
      environment: window.location.origin,
      emailAddress: email,
    };

    const preCheckedInAmendedIds = !isDeleteRegCardEnabled
      ? []
      : getChangedPreCheckedInReservations(
          tempAmendData?.bookingConfirmation,
          temporaryBookingData?.bookingConfirmation,
          isMobilePreRegisteredRepurposeEnabled
        );

    const confirmAmendLogicParams = {
      ...params,
      paymentOptionSelected: paymentProps?.selectedPaymentDetail?.type ?? PAY_ON_ARRIVAL,
      preCheckIn: preCheckedInAmendedIds,
    };

    confirmAmendMutation
      .mutateAsync(confirmAmendLogicParams)
      .then(async (response: AmendConfirmationResponseSuccess) => {
        queryClient.invalidateQueries({
          queryKey: ['getBookingConfirmationAmend', basketReference, country, language],
        });

        const paymentRedirect =
          response?.confirmAmendLogic?.payment?.paymentRequiredDetails?.paymentRedirect;
        if (paymentRedirect) {
          setIframeContent(paymentRedirect);
          setAmend3cpVisited(bookingReference);
          return;
        }

        intervalRef.current = setInterval(async () => {
          const { basketStatus, basketError } = await getBasketStatus(
            queryClient,
            temporaryBasketReference
          );

          const isAmendingOrFailed =
            (basketStatus === BASKET_STATUS.AMENDING && basketError) ||
            (basketStatus === BASKET_STATUS.AMEND_FAILED && basketError);

          if (basketStatus === BASKET_STATUS.AMENDED) {
            clearInterval(intervalRef.current ?? '');
            window.__satelliteLoaded &&
              window._satellite.track(ANALYTICS_TRACKING_AMEND_CONFIRMATION);
            await router.push(
              `${amendConfirmationURL}&status=${CONFIRM_AMEND_STATUS.success}&basketReference=${basketReference}&tempBookingReference=${temporaryBasketReference}`
            );
          } else if (isAmendingOrFailed) {
            clearInterval(intervalRef.current ?? '');
            setConfirmAmendErrorValue(basketError.code);
            const paramsForAnalytics = {
              ...window?.analyticsData,
              validation: basketError.code,
            };
            updateAmendPageAnalytics(paramsForAnalytics as AnalyticsDataAmend, bookingReference);
            window.__satelliteLoaded && window._satellite.track(ANALYTICS_TRACKING_AMEND_ERROR);
            await router.push(
              `${amendConfirmationURL}&status=${CONFIRM_AMEND_STATUS.error}&tempBookingReference=${temporaryBasketReference}&basketReference=${basketReference}`
            );
          }
        }, 1 * 1000);
      })
      .catch(async (error: MutationErrorResponse) => {
        let errorMessage: AmendConfirmationErrorLS = '';
        try {
          const message = JSON.parse(error.response.errors[0].message) as ConfirmAmendError;
          errorMessage = message.globalErrTextTemplate;
        } catch (e) {
          console.log(e);
        }

        setConfirmAmendErrorValue(errorMessage);
        const paramsForAnalytics = {
          ...window?.analyticsData,
          validation: errorMessage,
        };
        updateAmendPageAnalytics(paramsForAnalytics as AnalyticsDataAmend, bookingReference);
        window.__satelliteLoaded && window._satellite.track(ANALYTICS_TRACKING_AMEND_ERROR);
        await router.push(
          `${amendConfirmationURL}&status=${CONFIRM_AMEND_STATUS.error}&tempBookingReference=${temporaryBasketReference}&basketReference=${basketReference}`
        );
      });
  }

  function handleBackToHomepage() {
    cleanupFindBookingToken();
    const prefixURL = `${origin}/${country}/${language}/`;
    window.location.href = isInnBusiness
      ? `${origin}/${language}-${country}/homepage`
      : `${prefixURL}${getBackRedirectUrl(variant)}`;
  }

  function getStayDatesUpdate({
    originalArrival,
    temporaryArrival,
    originalNumberOfNights,
    temporaryNumberOfNights,
    temporaryNewTotal,
    currency,
    language,
    text,
  }: {
    originalArrival: string;
    temporaryArrival: string;
    originalNumberOfNights: number;
    temporaryNumberOfNights: number;
    temporaryNewTotal: number;
    currency: string;
    language: string;
    text: string;
  }) {
    const getNightsLabel = (numberOfNights: number) => {
      const label = numberOfNights === 1 ? t('amend.night') : t('amend.nights');
      return `${numberOfNights} ${label}`;
    };

    const mapObject: Record<string, string> = {
      '{arrivalDateOld}': originalArrival,
      '{numNightOld}': getNightsLabel(originalNumberOfNights),
      '{arrivalDateNew}': temporaryArrival,
      '{numNightNew}': getNightsLabel(temporaryNumberOfNights),
      '{newTotal}': formatPriceWithDecimal(
        language,
        formatCurrency(currency),
        temporaryNewTotal,
        true
      ),
    };

    const regex = new RegExp(Object.keys(mapObject).join('|'), 'g');
    return text.replace(
      regex,
      (matched) => `<span style="font-weight: 600">${mapObject[matched]}</span>`
    );
  }

  function saveRoomsMeals() {
    const roomsSelectionsMeal = newMeals && numberOfSelectionsPerRoomSelector(newMeals);
    const params = {
      basketReferenceId: temporaryBasketReference,
      hotelId: temporaryBookingConfirmation.hotelId,
      arrivalDate: temporaryArrivalDate,
      departureDate: temporaryDepartureDate,
      roomsSelections: roomsSelectionsMeal,
      previousRoomsSelections: numberOfSelectionsPerRoomSelector(
        prevMeals as SelectedMealsPerRoom[]
      ),
    };

    amendSaveReservationMutation.mutateAsync(params).then(() => {
      invalidateBookingConfirmation();
      invalidatePackages();
      invalidateAmendSummary();
      const hasMealsUpdated =
        params?.roomsSelections &&
        !isEqual(params.previousRoomsSelections, params?.roomsSelections);

      hasMealsUpdated && setMealsSectionHasUpdates(hasMealsUpdated);
    });
  }

  function getRoomsPackages() {
    const adultsMeals: MealItemExtension[] = adultsMealsSelector(
      mealPackagesData?.meals,
      temporaryNumberOfNights
    );
    const childrenMeals: MealKids[] = childrenMealsSelector(mealPackagesData?.mealsKids);

    return roomInformationSelector(
      temporaryBookingConfirmation?.reservationByIdList,
      selectedMeals,
      adultsMeals,
      childrenMeals,
      roomPackageSelection(extrasPackagesMapperSelector(mealPackagesData?.roomSelectionAmendExtras))
    );
  }
}

function getBackRedirectUrl(variant: Area) {
  switch (variant) {
    case Area.PI:
      return 'home.html';
    case Area.BB:
      return 'business-booker/home.html';
    case Area.CCUI:
      return 'bookings';
    default:
      return 'home.html';
  }
}

function renderExtras(rooms: AmendReservation[], mealPackagesData: Packages, currentRoom: number) {
  return (
    <RoomsExtrasSelection
      rooms={rooms}
      mealPackagesData={mealPackagesData}
      currentRoom={currentRoom}
    />
  );
}

const titleStyle = {
  as: 'h1',
  fontWeight: 'bold',
  color: 'darkGrey1',
  fontSize: {
    mobile: '3xxl',
    sm: '4xl',
  },
  lineHeight: {
    mobile: '5',
    sm: '5',
  },
  mb: {
    mobile: 'xl',
    xs: '3xl',
    md: '3xl',
  },
} as TextProps;

const detailsStyle = {
  mt: {
    mobile: 'lg',
    sm: '2xl',
    lg: '5xl',
  },
  mb: {
    mobile: '3xl',
    lg: '5xl',
  },
  w: 'full',
  px: { mobile: '1rem', sm: '1.125rem', md: '1.5rem' },
} as BoxProps;

const amendContainerStyle = {
  flexDir: {
    mobile: 'column',
    md: 'column',
    lg: 'row',
  },
  justifyContent: 'space-between',
} as FlexProps;

const accordionItemButtonStyle = {
  pr: 'md',
  pl: 'md',
} as BoxProps & ButtonProps;

const accordionItemTextStyle = {
  fontSize: {
    mobile: '2xl',
  },
  lineHeight: '4',
  color: 'darkGrey2',
} as TextProps;

const accordionContainerStyle = {
  borderX: '1px solid var(--chakra-colors-lightGrey4)',
  borderXColor: 'lightGrey3',
  w: {
    base: 'full',
    lg: '50.5rem',
    xl: '54rem',
  },
  borderRadius: 'var(--chakra-radii-xs)',
} as AccordionProps;

const accordionItemPanelStyle = {
  borderTop: '1px solid var(--chakra-colors-lightGrey4)',
  padding: 0,
} as AccordionPanelProps;

const accordionItemStyle = {
  _notLast: {
    borderBottom: 0,
  },
  borderColor: 'lightGrey3',
};

const accordionOverwriteStyles = {
  container: accordionContainerStyle,
  button: accordionItemButtonStyle,
  text: accordionItemTextStyle,
  panel: accordionItemPanelStyle,
  item: accordionItemStyle,
};

const containerStyle = {
  border: '1px solid var(--chakra-colors-lightGrey3)',
  w: {
    lg: '50.5rem',
    xl: '54rem',
  },
};

const returnButtonWrapper = {
  mt: '3xl',
  alignItems: 'center',
  w: 'fit-content',
  justifyContent: 'flex-start',
  cursor: 'pointer',
} as FlexProps;

const returnButtonTextStyle = {
  pl: 'sm',
  fontWeight: 'semibold',
  fontSize: 'md',
  color: 'darkGrey1',
  lineHeight: '3',
} as TextProps;

const loadingSpinnerStyle = {
  margin: {
    sm: 'lg',
  },
} as BoxProps;

const notificationAlertEciLcoStyle = {
  mb: {
    mobile: '3xl',
    lg: '5xl',
  },
  w: {
    base: 'full',
    lg: '50.5rem',
    xl: '54rem',
  },
};
const notificationCommonStyles = {
  width: 'auto',
  m: 'var(--chakra-space-lg)',
  mt: '0',
  mb: 0,
} as BoxProps;
