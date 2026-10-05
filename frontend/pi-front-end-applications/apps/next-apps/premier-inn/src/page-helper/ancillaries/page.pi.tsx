import type { BoxProps, FlexProps, GridItemProps, GridProps, TextProps } from '@chakra-ui/react';
import { Box, Flex, Grid, GridItem, Text, useMediaQuery } from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import {
  QueryBookingInformationArgs,
  ReservationById,
  BookingDataReservationDetailsProps,
  BookingSummaryDataProps,
  GET_ANCILLARIES_BOOKING_INFO,
  GET_HOTEL_INFORMATION,
  GuestCountUpdateRateCode,
  QueryHotelInformationArgs,
  PageName,
  MealItemExtension,
  MealKids,
  Menu,
  PrivacyPolicy,
  PackagesCriteria,
  RoomSelection,
  SAVE_RESERVATION_ANCILLARIES,
  SelectedMealsPerRoom,
  UPDATE_ANCILLARIES_RATE_CODE,
  UpsellsSelection,
  AncillaryFilterData,
  FS_SILENT_SUBSTITUTION,
  BILLING_ADDRESS_CAPTURE,
  THIRTY_MINUTES,
  SelectedExtrasPackage,
  FT_PI_BREAKFAST_PROMO_CODE,
  PROMO_CODE_COOKIE,
  Channel,
  FT_PI_FREE_FNB_AND_EXTRAS,
  VALID_EXTRAS_IDS,
  ANCILLARIES_TABS,
  BASKET_DETAILS_STATE_INITIAL_VALUE,
  BASKET_DETAILS_STORAGE_KEY,
  FT_PI_BB_CANCELLATION_POLICY,
  FT_PI_BB_CCUI_SHOW_MEALS_FREE,
  FREE_FOOD_OPTIONS,
  FT_PI_DISPLAY_SOFT_BUNDLES,
  ExtrasItem,
  FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN_ANCILLARIES_GDP_PAYMENT,
  SoftBundle,
} from '@whitbread-eos/api';
import {
  Button,
  Info,
  LoadingSpinner,
  Notification,
  Tabs,
  TabsOptionsItem,
} from '@whitbread-eos/atoms';
import {
  BackButton,
  DataSecuritySection,
  FreeFoodKidsNotification,
  Menus,
  RestaurantMessage,
  SEO,
  CancellationPolicy,
} from '@whitbread-eos/molecules';
import { BookingSummary, MealSelection } from '@whitbread-eos/organisms';
import {
  adultsMealsSelector,
  analytics,
  autocompleteMeals,
  axiosRequest,
  bookingGuestCount,
  calculateTotalCostRoomSelection,
  checkIfExistPreselection,
  childrenMealsSelector,
  decodeIdToken,
  formatAssetsUrl,
  formatDataTestId,
  freeBreakfastMaxAllowance,
  getImportantMessages,
  getAuthCookie,
  useAuthToken,
  useAuth0User,
  getNightsNumber,
  getUniqueRoomProperties,
  hotelInformationSelector,
  isStringValid,
  logicalAndOperator,
  logicalOrOperator,
  mealsMapperSelector,
  menusSelector,
  numberOfSelectionsPerRoomSelector,
  roomInformationSelector,
  securityNoticeMoreInfoDataSelector,
  selectedMealsPerRoomSelector,
  shouldDisplayAutocompleteNotification,
  transformBartIdToOperaId,
  useCustomLocale,
  useMutationRequest,
  usePackages,
  useQueryRequest,
  addReservationNumber,
  analyticsTrackings as trackingTypes,
  filterPackagesByAncillariesCloseOut,
  useFeatureSwitch,
  displayStorageSubstitutionLabels,
  useSilentRoomsMatch,
  setCookie,
  extrasPackagesMapperSelector,
  useFeatureToggle,
  getCookie,
  renderSanitizedHtml,
  roomPackageSelection,
  extrasPackagesAnalyticsMap,
  filterWiFiForPlusRooms,
  useUpdateRateName,
  useLocalStorage,
  getRestaurantMessageDisplay,
  BUNDLE_CHOICE,
  useSoftBundles,
  useSessionStorage,
  EXTRAS_ROOM_TAB_CHANGED,
  useSemanticTypography,
} from '@whitbread-eos/utils';
import { differenceInDays, format } from 'date-fns';
import groupBy from 'lodash/groupBy';
import orderBy from 'lodash/orderBy';
import { useTranslation } from 'next-i18next';
import getConfig from 'next/config';
import dynamic from 'next/dynamic';
import { NextRouter } from 'next/router';
import React, { useCallback, useEffect, useMemo, useState } from 'react';

export interface Props {
  queryClient: QueryClient;
  hiQueryInput: QueryHotelInformationArgs;
  pcksQueryInput: PackagesCriteria;
  biQueryInput: QueryBookingInformationArgs;
  router: NextRouter;
}

export function hasRoomPackagesChanges(
  roomsSelections: RoomSelection[],
  previousRoomSelection: RoomSelection[]
) {
  if (roomsSelections?.length !== previousRoomSelection?.length) {
    return true;
  }
  const groupedRoomsSelections = groupBy(roomsSelections, 'reservationId');
  const groupedPreviousRoomsSelections = groupBy(previousRoomSelection, 'reservationId');

  for (const reservationId in groupedRoomsSelections) {
    const sortedSelection = orderBy(
      groupedRoomsSelections[reservationId]?.[0]?.packagesSelection,
      'id'
    );
    const sortedPreviousSelection = orderBy(
      groupedPreviousRoomsSelections[reservationId]?.[0]?.packagesSelection,
      'id'
    );
    if (JSON.stringify(sortedSelection) !== JSON.stringify(sortedPreviousSelection)) {
      return true;
    }
  }
  return false;
}

const NOT_READY_STATE_USER_MEAL_PREF = 'NOT_READY_STATE_USER_MEAL_PREF';
const ExtrasSection = dynamic(
  async () => {
    const { ExtrasSection } = await import('@whitbread-eos/organisms');
    return { default: ExtrasSection };
  },
  {
    ssr: false,
  }
);

export default function AncillariesPagePi({
  queryClient,
  hiQueryInput,
  pcksQueryInput,
  biQueryInput,
  router,
}: Readonly<Props>) {
  const { t } = useTranslation();
  const { token, isAuth0Enabled } = useAuthToken();
  const { user: auth0User } = useAuth0User(isAuth0Enabled);
  const { language: currentLang, country: currentCountry } = useCustomLocale();
  const baseDataTestId = 'AncillariesPage';

  let { [FT_PI_BREAKFAST_PROMO_CODE]: isBreakfastPromoCodeEnabled } = useFeatureToggle();

  const {
    [FT_PI_FREE_FNB_AND_EXTRAS]: isFreeFnbAndExtrasEnabled,
    [FT_PI_BB_CANCELLATION_POLICY]: isCancellationPolicyEnabled,
    [FT_PI_BB_CCUI_SHOW_MEALS_FREE]: isshowKidsMealsFreeFlag,
    [FT_PI_DISPLAY_SOFT_BUNDLES]: isSoftBundlesEnabled,
    [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN_ANCILLARIES_GDP_PAYMENT]: isCityTaxBreakdownEnabled,
  } = useFeatureToggle();

  if (isFreeFnbAndExtrasEnabled) {
    isBreakfastPromoCodeEnabled = false;
  }

  const getTypographyProps = useSemanticTypography();

  const [sessionSoftBundles] = useSessionStorage<SoftBundle[]>('softBundles', []);
  const hasSessionSoftBundles = !!sessionSoftBundles.length;

  const breakfastPromoCode = t('config.experiments.breakfastPromoCode.code');
  const hasValidCookie = getCookie(PROMO_CODE_COOKIE) === breakfastPromoCode;
  const [isMobileView] = useMediaQuery('(max-width: 765px)');
  const [basketDetailsState] = useLocalStorage(
    BASKET_DETAILS_STORAGE_KEY,
    BASKET_DETAILS_STATE_INITIAL_VALUE
  );
  // for AB test - scrollable tabs in mobile ancillaries
  const SCROLLABLE_TABS_SIZE = 2;

  // get cookie for scrollable tabs - A/B test
  const hasScrollableTabsCookie = getCookie(ANCILLARIES_TABS.configName) === 'variant';
  const isScrollable = hasScrollableTabsCookie && isMobileView;

  const isSilentFeatureFlagEnabled = useFeatureSwitch({
    featureSwitchKey: FS_SILENT_SUBSTITUTION,
  });

  const [selectedMeals, setSelectedMeals] = useState<SelectedMealsPerRoom[]>([]);
  const [mealsTabs, setMealsTabs] = useState<TabsOptionsItem[]>([]);
  const [continueButtonPressed, setContinueButtonPressed] = useState(false);
  const [roomPackagesChanged, setRoomPackagesChanged] = useState(false);
  const [selectedRoom, setSelectedRoom] = useState(0);
  const [startingTab, setStartingTab] = useState(0);
  const [userMealPreferences, setUserMealPreferences] = useState(NOT_READY_STATE_USER_MEAL_PREF);
  const [prefMealId, setPrefMealId] = useState({
    adultMeal: '',
    childMeal: '',
  });

  const [selectedExtrasList, setSelectedExtrasList] = useState<SelectedExtrasPackage[] | undefined>(
    []
  );

  const [bookingInformation, setBookingInformation] = useState({
    children: 0,
    adults: 0,
    nrNights: 0,
  });

  const [listGuests, setListGuests] = useState<GuestCountUpdateRateCode>({
    adultsNumber: [],
    childrenNumber: [],
  });

  const handleSelectedExtrasList = (updatedExtrasItemsList: SelectedExtrasPackage[] | undefined) =>
    setSelectedExtrasList(updatedExtrasItemsList);

  const {
    data: bkngData,
    isError: bkngIsError,
    isLoading: bkngIsLoading,
    error: bkngError,
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

  const { isSoftBundlesVisible } = useSoftBundles(
    isSoftBundlesEnabled,
    getCookie(BUNDLE_CHOICE),
    bkngData?.bookingInformation?.reservationByIdList?.length,
    bkngData?.bookingInformation?.reservationByIdList?.[0]?.roomStay?.childrenNumber
  );

  const isSoftBundlesActive = isSoftBundlesVisible && hasSessionSoftBundles;

  useUpdateRateName(bkngData?.bookingInformation, currentLang, currentCountry);

  const {
    isLoading: pcksIsLoading,
    error: pcksError,
    isError: pcksIsError,
    packages,
    restaurant,
    privacyPolicy,
  } = usePackages({
    adultsNumber: pcksQueryInput.adultsNumber as number,
    childrenNumber: pcksQueryInput.childrenNumber as number,
    hotelId: pcksQueryInput.hotelId,
    basketReferenceId: biQueryInput.basketReference,
    endDate: pcksQueryInput.endDate,
    startDate: pcksQueryInput.startDate,
    bookingFlowId: pcksQueryInput.bookingFlowId,
    nightsNumber: pcksQueryInput.nightsNumber as number,
    channel: Channel.Pi,
  });

  //required for single room (for now) as per soft bundles requirements
  const includedPackages = hasSessionSoftBundles
    ? sessionSoftBundles.map((bundle) => bundle.id)
    : (packages?.roomSelection?.[0]?.packagesSelection?.map((pack) => pack?.id) ?? []);

  const getExtrasLeft = (extras: ExtrasItem[] | undefined) => {
    if (isSoftBundlesActive) {
      return extras?.filter((extra) => !includedPackages?.includes(extra?.id ?? ''));
    }
    return extras;
  };

  const getIncludedMeal = () => {
    return adultsMeals?.find((adultMeal) => includedPackages?.includes(adultMeal.id));
  };

  if (packages?.extrasItems) {
    packages.extrasItems = filterWiFiForPlusRooms(bkngData?.bookingInformation, packages);
  }

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

  const getUserMealPreference = useCallback(async () => {
    const { publicRuntimeConfig = {} } = getConfig() || {};

    // Get email from Auth0 user or legacy token
    const email = isAuth0Enabled ? auth0User?.email : decodeIdToken(getAuthCookie()).email;

    const userMealPreferenceQuery = queryClient.fetchQuery({
      queryKey: ['userDetails', token],
      queryFn: () =>
        axiosRequest({
          method: 'GET',
          url: `${publicRuntimeConfig.NEXT_PUBLIC_REST_API}/customers/hotels/${email}?business=false`,
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }),
    });

    await userMealPreferenceQuery
      .then((userDetails) => {
        if (userDetails?.bookingPreference && !displayRestaurantUnavailableNotification) {
          setUserMealPreferences(userDetails.bookingPreference.foodPreference.toString());
        } else {
          setUserMealPreferences('0'); // for new created accounts ; bookingPreference doesn't exists
        }
      })
      .catch(() => {
        setUserMealPreferences('0'); // in case of api call error let the page go
      });
  }, [token, isAuth0Enabled, auth0User]);

  useEffect(() => {
    /// Don't call the API if we already have the user meal preferences
    if (userMealPreferences !== NOT_READY_STATE_USER_MEAL_PREF) {
      return;
    }

    if (token && isStringValid(token)) {
      getUserMealPreference();
    } else {
      setUserMealPreferences('0');
    }
  }, [token, userMealPreferences]);

  useEffect(() => {
    setContinueButtonPressed(false);
  }, []);

  useEffect(() => {
    analytics.update({
      alcoholAllowed: false,
      dinnerAllowance: false,
      otherChargesAllowed: false,
      validation: '',
      bookingReasonForStay: '',
      wifiAccessAllowed: false,
      wifiOption: '',
    });
  }, []);

  let roomSelection: RoomSelection[] = [];

  if (packages?.roomSelection) {
    roomSelection = packages.roomSelection;
  }

  const isExtrasInventoryAvailable =
    packages?.extrasItems?.some(
      ({ available }) => (available as number) > 0 || available === null
    ) ||
    roomSelection?.some((room) =>
      room.packagesSelection?.some(
        (packageSelected) =>
          packageSelected?.id &&
          VALID_EXTRAS_IDS.includes(packageSelected.id as (typeof VALID_EXTRAS_IDS)[number])
      )
    );

  const priceExtrasTotal =
    selectedExtrasList?.reduce((numberOfExtrasAdded, currentExtras) => {
      numberOfExtrasAdded = numberOfExtrasAdded + currentExtras.price;
      return numberOfExtrasAdded;
    }, 0) ?? 0;

  useEffect(() => {
    if (
      userMealPreferences !== '0' &&
      userMealPreferences !== 'NOT_READY_STATE_USER_MEAL_PREF' &&
      packages?.meals
    ) {
      const prefAdultMeal = transformBartIdToOperaId(userMealPreferences, packages.meals);
      const freeBreakFastCode = isStringValid(prefAdultMeal)
        ? packages.meals.find((meal: MealItemExtension) => meal.id === prefAdultMeal)
            ?.freeBreakfastCode
        : '';
      setPrefMealId({ adultMeal: prefAdultMeal, childMeal: freeBreakFastCode as string });
    }
  }, [userMealPreferences, packages]);

  useEffect(() => {
    if (bkngData) {
      setListGuests(bookingGuestCount(bkngData?.bookingInformation?.reservationByIdList));
    }
  }, [bkngData]);

  const matchedSubstitutions = useSilentRoomsMatch(
    biQueryInput.basketReference,
    bkngData?.bookingInformation?.reservationByIdList ?? []
  );

  useEffect(() => {
    if (
      bkngData?.bookingInformation?.reservationByIdList.length > 0 &&
      selectedExtrasList?.length === 0
    ) {
      let preselectedExtrasPackagesPerRoom: SelectedExtrasPackage[] | undefined = [];
      preselectedExtrasPackagesPerRoom = extrasPackagesMapperSelector(roomSelection);
      setSelectedExtrasList(preselectedExtrasPackagesPerRoom);
    }
  }, [bkngData, packages]);

  useEffect(() => {
    if (
      bkngData?.bookingInformation?.reservationByIdList.length > 0 &&
      selectedMeals.length <= 0 &&
      userMealPreferences !== NOT_READY_STATE_USER_MEAL_PREF
    ) {
      let preselectedMeals: SelectedMealsPerRoom[] = [];

      if (roomSelection && packages?.meals && packages?.mealsKids) {
        if (isSoftBundlesActive) {
          preselectedMeals = mealsMapperSelector(
            packages?.meals,
            packages?.mealsKids,
            roomSelection
          );
          window.__satelliteLoaded && window._satellite.track('bundleSelected');
        } else if (userMealPreferences != '0' && !checkIfExistPreselection(roomSelection)) {
          preselectedMeals = autocompleteMeals(
            packages?.meals,
            packages?.mealsKids,
            listGuests,
            transformBartIdToOperaId(userMealPreferences, packages.meals)
          );
        } else {
          preselectedMeals = mealsMapperSelector(
            packages?.meals,
            packages?.mealsKids,
            roomSelection,
            isAdultHasMealsFree,
            listGuests
          );
        }
      }

      setSelectedMeals(preselectedMeals);
      setSelectedRoom(0);

      if (bkngData?.bookingInformation?.reservationByIdList.length > 1 && mealsTabs.length <= 0) {
        const tabsOptions = bkngData?.bookingInformation?.reservationByIdList.map(
          (room: ReservationById, index: number) => ({
            index,
            label: `${t('account.dashboard.room')} ${index + 1}`,
            description: displayStorageSubstitutionLabels(
              matchedSubstitutions?.[index],
              room?.roomStay?.roomExtraInfo?.roomName ?? '',
              isSilentFeatureFlagEnabled
            ),
          })
        );
        setMealsTabs(tabsOptions);
      }
    }
  }, [bkngData, packages, prefMealId, userMealPreferences]);

  const {
    mutation: svBknMutation,
    isLoading: svBknIsLoading,
    isError: svBknIsError,
    error: svBknError,
    isSuccess: svBknIsSuccess,
  } = useMutationRequest(SAVE_RESERVATION_ANCILLARIES);

  const { mutation: urcMutation, isSuccess: urcIsSuccess } = useMutationRequest(
    UPDATE_ANCILLARIES_RATE_CODE
  );

  useEffect(() => {
    if (urcIsSuccess) {
      queryClient.invalidateQueries({
        queryKey: [
          'GetBookingInformation',
          biQueryInput.language,
          biQueryInput.country,
          biQueryInput.basketReference,
        ],
      });

      queryClient.invalidateQueries({
        queryKey: [
          'getPaymentMethods',
          biQueryInput.language,
          biQueryInput.country,
          biQueryInput.basketReference,
        ],
      });
    }
  }, [urcIsSuccess, biQueryInput, queryClient]);

  useEffect(() => {
    if (
      !svBknIsError &&
      ((roomPackagesChanged && svBknIsSuccess) || !roomPackagesChanged) &&
      continueButtonPressed
    ) {
      router.push(
        `/${currentCountry}/${currentLang}${
          bkngData?.bookingInformation?.bookingFlowId
            ? `/${bkngData?.bookingInformation?.bookingFlowId}`
            : ''
        }/guest-details?reservationId=${biQueryInput.basketReference}`
      );
    }
  }, [
    svBknIsSuccess,
    svBknIsError,
    biQueryInput,
    queryClient,
    roomPackagesChanged,
    continueButtonPressed,
  ]);

  const firstRoom = bkngData?.bookingInformation?.reservationByIdList[0] || {};

  const noNights = getNightsNumber(
    firstRoom.roomStay?.arrivalDate,
    firstRoom.roomStay?.departureDate
  );

  useEffect(() => {
    if (bkngData) {
      setBookingInformation({
        adults:
          bkngData?.bookingInformation?.reservationByIdList[selectedRoom].roomStay.adultsNumber,
        children:
          bkngData?.bookingInformation?.reservationByIdList[selectedRoom].roomStay.childrenNumber,
        nrNights: noNights,
      });
    }
  }, [selectedRoom, bkngData]);

  const ancillaryCloseoutData = hiData?.hotelInformation?.ancillaryCloseout;

  let adultsMeals: MealItemExtension[] = adultsMealsSelector(packages?.meals, noNights);

  const hasPromoMeal =
    isBreakfastPromoCodeEnabled &&
    hasValidCookie &&
    adultsMeals?.some((meal) => meal.id === process.env.NEXT_PUBLIC_BREAKFAST_PROMO_PACKAGE);

  if (hasPromoMeal) {
    adultsMeals = adultsMeals.filter(
      (meal) => meal.id === process.env.NEXT_PUBLIC_BREAKFAST_PROMO_PACKAGE
    );
  }

  let childrenMeals: MealKids[] = useMemo(
    () => (hasPromoMeal ? [] : childrenMealsSelector(packages?.mealsKids)),
    [hasPromoMeal, packages?.mealsKids]
  );

  if (isFreeFnbAndExtrasEnabled) {
    childrenMeals = childrenMealsSelector(packages?.mealsKids);
  }

  const isAdultHasMealsFree = !!(
    isFreeFnbAndExtrasEnabled && adultsMeals?.some((mealItem) => mealItem?.isFree)
  );

  let isAncillaryCloseout = false;
  if (ancillaryCloseoutData?.items?.length && adultsMeals.length && childrenMeals.length) {
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
    isAncillaryCloseout = filteredAdultsMeals?.length === 0;
  }

  const getMealQuantity = useCallback(
    (mealId: string, roomIndex: number) => {
      const currentSelection = numberOfSelectionsPerRoomSelector(selectedMeals);
      if (currentSelection.length > 0) {
        const item = currentSelection[roomIndex]?.packagesSelection.filter(
          (pck) => pck.id === mealId
        );
        return item?.length > 0 ? item[0]?.noOfSelections : 0;
      }
      return 0;
    },
    [selectedMeals]
  );

  const getPackagesSelectionAnalytics = useCallback(() => {
    const ancPackagesSelection: Array<UpsellsSelection[]> = [];

    if (bkngData?.bookingInformation?.reservationByIdList) {
      bkngData?.bookingInformation?.reservationByIdList.forEach(
        (room: ReservationById, roomIndex: number) => {
          const packagesPerRoom = [
            ...adultsMeals.map((meal) => ({
              code: meal?.id ?? '',
              legend: meal?.name ?? '',
              quantity: getMealQuantity(meal?.id ?? '', roomIndex),
              price: meal?.price?.toFixed(2) ?? '',
              currency: meal?.currency,
              freeBreakfastCode: meal?.freeBreakfastCode,
              freeBreakfastOption: meal?.freeBreakfastOption,
              upsellType: meal?.upsellType ?? '',
            })),
            ...childrenMeals.map((meal) => ({
              code: meal?.id ?? '',
              legend: meal?.name ?? '',
              quantity: getMealQuantity(meal?.id ?? '', roomIndex),
              price: '0.00',
            })),
            ...(extrasPackagesAnalyticsMap(roomIndex, noNights, packages) ?? []),
          ];
          return ancPackagesSelection.push([...packagesPerRoom]);
        }
      );
    }
    return ancPackagesSelection;
  }, [
    adultsMeals,
    bkngData?.bookingInformation?.reservationByIdList,
    childrenMeals,
    getMealQuantity,
    selectedExtrasList,
  ]);
  const menus: Menu[] = menusSelector(adultsMeals, childrenMeals);

  const privacyPolicyData: PrivacyPolicy = securityNoticeMoreInfoDataSelector(privacyPolicy ?? {});

  const logoRestaurantUrl = isStringValid(restaurant?.logoSrc)
    ? formatAssetsUrl(restaurant?.logoSrc ?? '')
    : '';

  const showFreeFoodKidsNotification =
    adultsMeals &&
    adultsMeals.some((meal: MealItemExtension) => meal.freeBreakfastOption) &&
    freeBreakfastMaxAllowance(adultsMeals) > 0 &&
    bkngData?.bookingInformation?.reservationByIdList?.some(
      (room: ReservationById) => room?.roomStay?.childrenNumber && room.roomStay.childrenNumber > 0
    );

  const showFreeDinnerKidsNotification =
    isshowKidsMealsFreeFlag &&
    adultsMeals &&
    adultsMeals.some((meal: MealItemExtension) => meal?.upsellType === FREE_FOOD_OPTIONS.DINNER) &&
    freeBreakfastMaxAllowance(adultsMeals) > 0 &&
    bkngData?.bookingInformation?.reservationByIdList?.some(
      (room: ReservationById) => Number(room?.roomStay?.childrenNumber) > 0
    );

  const showFreeFoodKids = logicalAndOperator(
    bookingInformation.children > 0,
    adultsMeals.some((meal: MealItemExtension) => meal.freeBreakfastOption),
    freeBreakfastMaxAllowance(adultsMeals) > 0
  );

  const defaultRestaurantTitle = t('upsell.heading.restaurant.closure');
  const defaultRestaurantDescription = t('upsell.message.restaurant.closure');

  const { showRestaurantMessage, displayTitle, displayDescription } = getRestaurantMessageDisplay({
    restaurant,
    ancillaryCloseoutData,
    arrivalDate: firstRoom.roomStay?.arrivalDate,
    departureDate: firstRoom.roomStay?.departureDate,
  });

  const displayRestaurantTitle = displayTitle || defaultRestaurantTitle;
  const displayRestaurantDescription = displayDescription || defaultRestaurantDescription;

  const updateRateCode = useCallback(() => {
    const roomTypes: Array<string> = bkngData?.bookingInformation?.reservationByIdList.map(
      (room: ReservationById) => room?.roomStay?.roomExtraInfo?.roomType
    );
    const guestCount = bookingGuestCount(bkngData?.bookingInformation?.reservationByIdList);

    urcMutation.mutate({
      basketReferenceId: biQueryInput.basketReference,
      rateCode: bkngData?.bookingInformation?.upgradeToFlex?.flexRateCode,
      hotelId: bkngData?.bookingInformation?.hotelId,
      startDate: firstRoom.roomStay?.arrivalDate,
      endDate: firstRoom.roomStay?.departureDate,
      roomType: roomTypes,
      currency: bkngData?.bookingInformation?.upgradeToFlex?.currency,
      adultsNumber: guestCount.adultsNumber,
      childrenNumber: guestCount.childrenNumber,
    });
  }, [biQueryInput.basketReference]);

  const initialTotalCost =
    bkngData?.bookingInformation?.totalCost -
    calculateTotalCostRoomSelection(adultsMeals, roomSelection, noNights) +
    priceExtrasTotal;

  const bookingSummaryData: BookingSummaryDataProps = {
    hotelInformation:
      hiData?.hotelInformation && hotelInformationSelector(hiData?.hotelInformation),
    totalCost: {
      showVATMessage: true,
      currency: bkngData?.bookingInformation?.currencyCode,
      initialTotalCost: initialTotalCost,
      meals: selectedMealsPerRoomSelector(selectedMeals, adultsMeals, childrenMeals),
    },
    rateInformation: {
      rate: firstRoom.roomStay?.rateExtraInfo?.rateName,
      noNights: noNights,
      noRooms: bkngData?.bookingInformation?.reservationByIdList?.length,
      rateDescription: firstRoom.roomStay?.rateExtraInfo?.rateDescription,
      rateTags: basketDetailsState?.rateTags,
    },
    stayDatesInformation: {
      arrivalDate: logicalOrOperator(firstRoom.roomStay?.arrivalDate, null),
      departureDate: logicalOrOperator(firstRoom.roomStay?.departureDate, null),
      noNights: noNights,
    },
    roomInformation: roomInformationSelector(
      bkngData?.bookingInformation?.reservationByIdList,
      selectedMeals,
      adultsMeals,
      childrenMeals,
      roomPackageSelection(selectedExtrasList)
    ),
    cityTaxTotal: bkngData?.bookingInformation?.cityTaxTotal || '',
    updateToFlex: {
      showUpgradeToFlex:
        basketDetailsState?.rateTags && basketDetailsState?.rateTags?.length > 0
          ? false
          : !!bkngData?.bookingInformation?.upgradeToFlex?.flexRateCode,
      currency: bkngData?.bookingInformation?.upgradeToFlex?.currency,
      amount:
        bkngData?.bookingInformation?.upgradeToFlex?.amount - initialTotalCost + priceExtrasTotal,
      upgradeToFlexCallBack: updateRateCode,
      initialRate: bkngData?.bookingInformation.totalCost,
    },
    showAutocompleteMealsNotification: shouldDisplayAutocompleteNotification(
      listGuests,
      selectedMeals,
      prefMealId
    ),
  };

  useEffect(() => {
    if (
      bkngData?.bookingInformation?.reservationByIdList.length > 0 &&
      bookingSummaryData?.totalCost?.initialTotalCost
    ) {
      const firstRoom = bkngData.bookingInformation.reservationByIdList[0].roomStay || {};
      let totalAdults = 0;
      let totalChildren = 0;

      if (bkngData.bookingInformation.reservationByIdList) {
        bkngData.bookingInformation.reservationByIdList.forEach((room: ReservationById) => {
          if (room?.roomStay) {
            totalAdults += room.roomStay.adultsNumber ?? 0;
            totalChildren += room.roomStay.childrenNumber ?? 0;
          }
        });
      }

      const arrivalDay = format(new Date(firstRoom.arrivalDate), 'EEEE');
      const departureDay = format(new Date(firstRoom.departureDate), 'EEEE');

      analytics.update({
        currencyCode: bkngData.bookingInformation.currencyCode,
        FromToDate: {
          ArrivalDay: arrivalDay,
          DepartureDay: departureDay,
          FromToDay: `${arrivalDay}-${departureDay}`,
        },
        rateCode: firstRoom.ratePlanCode,
        productSelectedRate: firstRoom.rateExtraInfo?.rateName,
        RoomTypes: getUniqueRoomProperties(bkngData?.bookingInformation?.reservationByIdList),
        RoomNames: getUniqueRoomProperties(
          bkngData?.bookingInformation?.reservationByIdList,
          'roomName'
        ),
        productDetails: [
          {
            type: trackingTypes.HOTEL,
            quantity: bkngData.bookingInformation.reservationByIdList.length,
            price: {
              basePrice: (
                bkngData?.bookingInformation?.totalCost -
                calculateTotalCostRoomSelection(adultsMeals, roomSelection, noNights)
              ).toFixed(2),
            },
            productInfo: {
              sku: bkngData.bookingInformation.hotelId,
              totalNumberOfRooms: bkngData.bookingInformation.reservationByIdList.length,
              roomAdults: totalAdults.toString(),
              roomChildren: totalChildren.toString(),
              numberOfGuests: (totalAdults + totalChildren).toString(),
              startDate: format(new Date(firstRoom.arrivalDate), 'yyyy-MM-dd'),
              endDate: format(new Date(firstRoom.departureDate), 'yyyy-MM-dd'),
              numberOfNights: getNightsNumber(
                firstRoom?.arrivalDate,
                firstRoom?.departureDate
              ).toString(),
              daysToCheckIn: differenceInDays(
                new Date(firstRoom.arrivalDate),
                new Date()
              ).toString(),
            },
          },
        ],
        upsells: {
          rooms: getPackagesSelectionAnalytics(),
        },
      });
    }
  }, [
    adultsMeals,
    bkngData,
    bkngData?.bookingInformation?.currencyCode,
    bookingSummaryData?.totalCost?.initialTotalCost,
    getPackagesSelectionAnalytics,
    noNights,
    roomSelection,
    selectedExtrasList,
  ]);

  const reservationDetails: BookingDataReservationDetailsProps = {
    arrivalDate: firstRoom?.roomStay?.arrivalDate || null,
    departureDate: firstRoom?.roomStay?.departureDate || null,
    currency: bkngData?.bookingInformation?.currencyCode,
    noRooms: bkngData?.bookingInformation?.reservationByIdList?.length,
    noNights,
  };

  const listOfImportantMessages: string[] = getImportantMessages(
    hiData?.hotelInformation?.importantInfo?.infoItems,
    firstRoom?.roomStay?.arrivalDate,
    firstRoom?.roomStay?.departureDate
  );

  const noBookingInfoMessages = bkngData?.bookingInformation?.infoMessages?.length;
  const noImportantInfoMessages = listOfImportantMessages?.length;

  const infoMessages: string[] = noImportantInfoMessages > 0 ? listOfImportantMessages : [];

  const continueBooking = useCallback(() => {
    const roomsSelections = addReservationNumber(
      numberOfSelectionsPerRoomSelector(selectedMeals),
      bkngData?.bookingInformation?.reservationByIdList || [],
      selectedExtrasList
    );

    const selectionInfo = {
      basketReferenceId: biQueryInput.basketReference || null,
      hotelId: bkngData?.bookingInformation?.hotelId || null,
      arrivalDate: firstRoom.roomStay?.arrivalDate || null,
      departureDate: firstRoom.roomStay?.departureDate || null,
      roomsSelections,
      previousRoomsSelections: roomSelection || null,
    };

    setContinueButtonPressed(true);
    if (hasRoomPackagesChanges(roomsSelections, roomSelection)) {
      svBknMutation.mutate(selectionInfo);
      setRoomPackagesChanged(true);
    } else {
      setRoomPackagesChanged(false);
    }

    if (typeof window !== 'undefined') {
      if (window?.piConfig?.billingAddressCapture) {
        const { mode } = window.piConfig.billingAddressCapture;
        mode?.length > 0 && setCookie(BILLING_ADDRESS_CAPTURE, mode, THIRTY_MINUTES);
      }
    }
  }, [selectedMeals, roomSelection, selectedExtrasList]);

  const headingTitle = t('upsell.meals.title');

  const cityTaxMessage = bkngData?.bookingInformation?.hasCityTax
    ? t('booking.overview.includeCityTax')
    : '';

  const extrasToDisplay = getExtrasLeft(packages?.extrasItems);
  const displayExtras = !!extrasToDisplay?.length && isExtrasInventoryAvailable;

  const displayRestaurantUnavailableNotification = showRestaurantMessage || isAncillaryCloseout;

  const hideRestaurantSection = !displayExtras && displayRestaurantUnavailableNotification;

  const isContinueButtonReady = useMemo(() => {
    if (hideRestaurantSection) return true;
    return (
      selectedMeals.length > 0 || bkngData?.bookingInformation?.reservationByIdList?.length === 0
    );
  }, [selectedMeals, hideRestaurantSection, bkngData]);

  return (
    <>
      <SEO
        page={PageName.ANCILLARIES}
        hotelId={bkngData?.bookingInformation?.hotelId}
        bookingFlowId={bkngData?.bookingInformation?.bookingFlowId}
        noIndexNoFollow={true}
      />
      <Grid {...mainAncillariesGridStyle} data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}>
        <GridItem {...bookingSummaryMobileContainerStyle}>
          <Flex
            {...bookingSummaryMobileTriggerStyle}
            data-testid={formatDataTestId(baseDataTestId, 'BookingSummary-MobileVariant')}
            className={`bookingInfoMessages-${noBookingInfoMessages} importantInfoMessages-${noImportantInfoMessages}`}
          >
            <BookingSummary
              variant="mobile"
              t={t}
              language={currentLang}
              reservationDetails={reservationDetails}
              bookingSummaryData={bookingSummaryData}
              prefixDataTestId={baseDataTestId}
              infoMessages={infoMessages}
              taxesMessage={cityTaxMessage}
              isExtrasDisplayed={!!packages?.extrasItems}
              isSoftBundlesVisible={isSoftBundlesVisible}
              isCityTaxBreakdownEnabled={isCityTaxBreakdownEnabled}
            />
          </Flex>
        </GridItem>
        <GridItem
          {...pageContentStyle}
          data-testid={formatDataTestId(baseDataTestId, 'PageContent')}
        >
          {renderPageContent()}
        </GridItem>
        <GridItem {...bookingSummaryDesktopStyle}>
          <BookingSummary
            variant="desktop"
            t={t}
            language={currentLang}
            reservationDetails={reservationDetails}
            bookingSummaryData={bookingSummaryData}
            prefixDataTestId={baseDataTestId}
            taxesMessage={cityTaxMessage}
            isExtrasDisplayed={!!packages?.extrasItems}
            isSoftBundlesVisible={isSoftBundlesVisible}
            isCityTaxBreakdownEnabled={isCityTaxBreakdownEnabled}
          />
          <Box
            pt="sm"
            data-testid={formatDataTestId(baseDataTestId, 'BookingSummary-InfoMessages')}
            className={`bookingInfoMessages-${noBookingInfoMessages} importantInfoMessages-${noImportantInfoMessages}`}
          >
            {infoMessages?.map((notification) => {
              if (notification.length) {
                return (
                  <Box mt="md" key={notification}>
                    <Notification
                      maxWidth="full"
                      variant="info"
                      status="info"
                      description={
                        <Box
                          className="formatLinks"
                          {...getTypographyProps({}, infoMessagesSemanticTypography)}
                        >
                          {renderSanitizedHtml(notification)}
                        </Box>
                      }
                      svg={<Info />}
                    />
                  </Box>
                );
              }
            })}
          </Box>
        </GridItem>
      </Grid>
    </>
  );

  function renderMealsSelection() {
    const hasMenus = menus?.length > 0;
    return (
      <>
        <MealSelection
          headingTitle={headingTitle}
          logoRestaurantUrl={logoRestaurantUrl}
          adults={Number(bookingInformation.adults)}
          nights={Number(bookingInformation.nrNights)}
          kids={Number(bookingInformation.children)}
          adultsMeals={adultsMeals}
          childrenMeals={childrenMeals}
          selectedRoom={selectedRoom}
          selectedMeals={selectedMeals}
          setSelectedMeals={setSelectedMeals}
          showFreeFoodKids={showFreeFoodKids}
          prefixDataTestId={baseDataTestId}
          hasPromoMeal={hasPromoMeal}
          hasMenus={hasMenus}
          isSoftBundlesVisible={isSoftBundlesActive}
          softBundleIncludedMeal={getIncludedMeal()}
          isAdultHasMealsFree={isAdultHasMealsFree}
        />
        {!displayRestaurantUnavailableNotification && hasMenus && (
          <Menus availableMenus={menus} prefixDataTestId={baseDataTestId} />
        )}
        {displayExtras && (
          <Box sx={!displayRestaurantUnavailableNotification ? { mt: '3rem' } : { mt: '0' }}>
            <ExtrasSection
              extrasDetailsList={extrasToDisplay}
              handleSelectedExtrasList={handleSelectedExtrasList}
              selectedRoom={selectedRoom}
              selectedExtrasList={selectedExtrasList}
              noNights={noNights}
            />
          </Box>
        )}
      </>
    );
  }

  function renderMultiRoomMealsSelection() {
    return (
      <Box {...renderMultipleRoomsStyle()} data-testid="multipleRoom">
        {renderMealsSelection()}
      </Box>
    );
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
      <>
        {hideRestaurantSection && (
          <RestaurantMessage
            messageDescription={displayRestaurantDescription}
            messageTitle={displayRestaurantTitle}
            prefixDataTestId={baseDataTestId}
          />
        )}

        {!hideRestaurantSection && (
          <>
            <Text
              {...titleLayoutStyle}
              {...getTypographyProps(titleLegacyTypography, titleSemanticTypography)}
              data-testid={formatDataTestId(baseDataTestId, 'MealsHeading')}
            >
              {t('upsell.heading')}
            </Text>

            {showFreeFoodKidsNotification && (
              <FreeFoodKidsNotification
                prefixDataTestId={baseDataTestId}
                isDinnerIncluded={showFreeDinnerKidsNotification && isshowKidsMealsFreeFlag}
              />
            )}

            {displayRestaurantUnavailableNotification && (
              <RestaurantMessage
                messageDescription={displayRestaurantDescription}
                messageTitle={displayRestaurantTitle}
                prefixDataTestId={baseDataTestId}
              />
            )}

            {mealsTabs.length > 1 &&
            bkngData?.bookingInformation?.reservationByIdList.length > 1 ? (
              <>
                {/* for non AB test and mobile only */}
                {isMobileView &&
                  !isScrollable &&
                  bkngData?.bookingInformation?.reservationByIdList.length > 7 && (
                    <h3>
                      {mealsTabs?.[selectedRoom]?.label} - {mealsTabs?.[selectedRoom]?.description}
                    </h3>
                  )}
                <Box {...multiRoomsContainerStyle}>
                  <Tabs
                    options={mealsTabs}
                    variant="greyTabsGroup"
                    index={selectedRoom}
                    onChange={(index: number) => {
                      setSelectedRoom(index);
                      window?.dispatchEvent(
                        new CustomEvent(EXTRAS_ROOM_TAB_CHANGED, { detail: { value: index + 1 } })
                      );
                    }}
                    prefixDataTestId={baseDataTestId}
                    shortMobileLabels={true}
                    singleContent={renderMultiRoomMealsSelection()}
                    setStartingTab={setStartingTab}
                    startingTab={startingTab}
                    tabScrollSize={SCROLLABLE_TABS_SIZE}
                    isScrollable={isScrollable}
                    isMobileView={isMobileView}
                    hasRoomLabels={true}
                  />
                </Box>
              </>
            ) : (
              <Box {...singleRoomContainerStyle}>{renderMealsSelection()}</Box>
            )}
          </>
        )}
        <Box
          display={isCancellationPolicyEnabled ? 'block' : 'none'}
          data-testid={formatDataTestId(baseDataTestId, 'CancellationPolicy')}
        >
          <CancellationPolicy
            rateDescription={basketDetailsState?.rateDescription}
            updateToFlex={bookingSummaryData.updateToFlex}
            rate={bookingSummaryData.rateInformation?.rate}
            hideUpgradeToFlex={isSoftBundlesVisible}
          />
        </Box>
        <Box {...continueButtonSectionStyle}>
          <Button
            onClick={continueBooking}
            isDisabled={continueButtonPressed || !isContinueButtonReady}
            size="full"
            variant="primary"
            data-testid={formatDataTestId(baseDataTestId, 'ContinueButton')}
          >
            <Text
              {...continueTextLayoutStyle}
              {...getTypographyProps(continueTextLegacyTypography, continueTextSemanticTypography)}
            >
              {t('booking.summary.continue')}
            </Text>
          </Button>
        </Box>

        <BackButton prefixDataTestId={baseDataTestId} />
        <DataSecuritySection privacyPolicy={privacyPolicyData} prefixDataTestId={baseDataTestId} />
      </>
    );
  }

  function renderMultipleRoomsStyle() {
    return displayRestaurantUnavailableNotification
      ? noRestaurantMultiRoomsSelectionStyle
      : multiRoomMealsSelectionContainerStyle;
  }
}

const singleRoomContainerStyle = {
  mt: { mobile: 'xl', xs: 'lg', sm: '2xl' },
} as BoxProps;

const multiRoomsContainerStyle = {
  mt: 'xl',
  border: '1px solid var(--chakra-colors-lightGrey2)',
  boxShadow: '0 0 var(--chakra-space-xmd) var(--chakra-colors-lightGrey4)',
  borderRadius: '0 0 3px 3px',
  borderTop: 'none',
} as BoxProps;

const multiRoomMealsSelectionContainerStyle = {
  px: { mobile: 'md', sm: 'lg' },
  pt: { mobile: 'lg', sm: 'xl', lg: '3xl' },
  pb: { mobile: 'md', sm: 'lg' },
} as BoxProps;

const noRestaurantMultiRoomsSelectionStyle = {
  ...multiRoomMealsSelectionContainerStyle,
  pt: { mobile: 'lg' },
} as BoxProps;

const continueButtonSectionStyle = {
  mt: '3xl',
  width: { mobile: 'full', md: '72' },
} as BoxProps;

const continueTextLayoutStyle = {
  color: 'baseWhite',
} as TextProps;

const continueTextLegacyTypography = {
  fontWeight: 'semibold',
  fontSize: 'lg',
  lineHeight: '3',
} as TextProps;

const continueTextSemanticTypography = {
  textStyle: 'label-l',
} as TextProps;

const infoMessagesSemanticTypography = {
  textStyle: 'body-s-regular',
} as TextProps;

const titleLayoutStyle = {
  as: 'h1',
  color: 'darkGrey1',
  mb: {
    mobile: 'xl',
    xs: 'lg',
  },
} as TextProps;

const titleLegacyTypography = {
  fontWeight: 'semibold',
  fontSize: {
    mobile: '3xl',
    sm: '3xxl',
  },
  lineHeight: {
    mobile: '4',
    sm: '5',
  },
} as TextProps;

const titleSemanticTypography = {
  textStyle: 'heading-xl',
} as TextProps;

const mainAncillariesGridStyle = {
  w: 'full',
  maxW: 'var(--chakra-space-breakpoint-xl)',
  px: {
    mobile: '0',
    lg: 'xl',
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
  m: '0!important',
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
    mobile: 'md',
    md: 'lg',
    lg: '0',
  },
  pt: {
    mobile: 'lg',
    md: '2xl',
    lg: '0',
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
    xl: '19.31rem',
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
