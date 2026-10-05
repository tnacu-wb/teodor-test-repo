import type { BoxProps, FlexProps, GridItemProps, GridProps, TextProps } from '@chakra-ui/react';
import { Box, Flex, GridItem, Text, useMediaQuery } from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import {
  QueryBookingInformationArgs,
  ReservationById,
  BookingDataReservationDetailsProps,
  BookingSummaryDataProps,
  BUSINESS_BOOKER_USER_ROLES,
  CREATE_RESERVATION_GUEST,
  GET_ANCILLARIES_BOOKING_INFO,
  GET_HOTEL_INFORMATION,
  GuestCountUpdateRateCode,
  QueryHotelInformationArgs,
  PageName,
  MealItem,
  MealKids,
  Menu,
  PrivacyPolicy,
  PackagesCriteria,
  RoomSelection,
  SAVE_RESERVATION_ANCILLARIES,
  SelectedMealsPerRoom,
  Suggestion,
  UpsellsSelection,
  AncillaryFilterData,
  FS_SILENT_SUBSTITUTION,
  Area,
  SelectedExtrasPackage,
  ExtrasItem,
  COMPANY_DATA_ULTIMATE_WIFI,
  FT_BB_ACCOMPANYING_GUEST_DETAILS,
  StayingGuest,
  ANCILLARIES_TABS,
  PurposeOfStay,
  BASKET_DETAILS_STORAGE_KEY,
  BASKET_DETAILS_STATE_INITIAL_VALUE,
  FT_PI_BB_CANCELLATION_POLICY,
  Customer,
  CompanyDetailsResponse,
  FT_BB_MARKETING_EMAIL_OPTIN,
  GET_CONTACT_PREFERENCES,
  BRANDCODES,
  FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN_ANCILLARIES_GDP_PAYMENT,
  EARLY_CHECKIN_IDS,
  LATE_CHECKOUT_IDS,
  WIFI_IDS,
  FT_BB_FREE_FNB_AND_EXTRAS,
} from '@whitbread-eos/api';
import {
  Button,
  FormProps,
  Info,
  LoadingSpinner,
  Notification,
  Success,
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
  MarketingEmail,
} from '@whitbread-eos/molecules';
import { BookingSummary, GuestDetailsBBContainer, MealSelection } from '@whitbread-eos/organisms';
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
  getImportantMessages,
  freeBreakfastMaxAllowance,
  getAuthCookie,
  getLoggedInUserInfo,
  getNightsNumber,
  getUniqueRoomProperties,
  getCityTaxMessages,
  hotelInformationSelector,
  isStringValid,
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
  useCompanyDetails,
  useQueryRequest,
  useUserData,
  useUserDetails,
  addReservationNumber,
  analyticsTrackings as trackingTypes,
  filterPackagesByAncillariesCloseOut,
  useFeatureSwitch,
  updateAncillariesAnalytics,
  displayStorageSubstitutionLabels,
  useSilentRoomsMatch,
  extrasPackagesMapperSelector,
  renderSanitizedHtml,
  extrasPackagesAnalyticsMap,
  roomPackageSelection,
  filterWiFiForPlusRooms,
  useFeatureToggle,
  isGuestDetailsPageAllowed,
  getRestaurantMessageDisplay,
  isDataCollectionMsgAndFormVisible,
  getCookie,
  useLocalStorage,
  useSemanticTypography,
} from '@whitbread-eos/utils';
import { differenceInDays, format } from 'date-fns';
import { useTranslation } from 'next-i18next';
import getConfig from 'next/config';
import dynamic from 'next/dynamic';
import { NextRouter } from 'next/router';
import React, { SetStateAction, useCallback, useEffect, useState } from 'react';

import {
  PAYMENT_NAVIGATION_SOURCE_GUEST_DETAILS,
  PAYMENT_NAVIGATION_SOURCE_QUERY_PARAM,
} from '../../utils/bb-all-pages-constants';

export interface Props {
  queryClient: QueryClient;
  hiQueryInput: QueryHotelInformationArgs;
  pcksQueryInput: PackagesCriteria;
  biQueryInput: QueryBookingInformationArgs;
  router: NextRouter;
  userDetails?: Customer;
  companyDetails?: CompanyDetailsResponse;
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

const Grid = dynamic(
  async () => {
    const { Grid } = await import('@chakra-ui/react');
    return { default: Grid };
  },
  {
    ssr: false,
  }
);

export default function GuestDetailsPageBB({
  queryClient,
  hiQueryInput,
  pcksQueryInput,
  biQueryInput,
  router,
  userDetails,
  companyDetails,
}: Props) {
  const { t, i18n } = useTranslation();
  const idTokenCookie = getAuthCookie();

  const { accessLevel, cdhEmployeeId, cdhCompanyId, sessionId } =
    getLoggedInUserInfo(idTokenCookie);

  // get cookie for scrollable tabs - AB test
  const hasScrollableTabsCookie = getCookie(ANCILLARIES_TABS.configName) === 'variant';
  const [isMobileView] = useMediaQuery('(max-width: 765px)');
  const isScrollable = hasScrollableTabsCookie && isMobileView;

  const [selectedExtrasList, setSelectedExtrasList] = useState<SelectedExtrasPackage[] | undefined>(
    []
  );
  const [acceptFutureMailing, setAcceptFutureMailing] = useState(false);
  // for AB test - scrollable tabs in mobile ancillaries
  const SCROLLABLE_TABS_SIZE = 2;

  const { isLoggedIn } = useUserData();
  const userData = useUserDetails(true, isLoggedIn, userDetails);
  const companyData = useCompanyDetails(
    cdhCompanyId,
    sessionId,
    cdhEmployeeId,
    isLoggedIn,
    companyDetails
  );

  const { language: currentLang, country: currentCountry } = useCustomLocale();
  const baseDataTestId = 'GuestDetailsPageBB';
  const [selectedMeals, setSelectedMeals] = useState<SelectedMealsPerRoom[]>([]);
  const [mealsTabs, setMealsTabs] = useState<TabsOptionsItem[]>([]);
  const [continueButtonPressed, setContinueButtonPressed] = useState(false);

  const [selectedRoom, setSelectedRoom] = useState(0);
  const [startingTab, setStartingTab] = useState(0);

  const [userMealPreferences, setUserMealPreferences] = useState(NOT_READY_STATE_USER_MEAL_PREF);
  const [prefMealId, setPrefMealId] = useState({
    adultMeal: '',
    childMeal: '',
  });

  const isSilentFeatureFlagEnabled = useFeatureSwitch({
    featureSwitchKey: FS_SILENT_SUBSTITUTION,
  });

  const {
    [FT_BB_ACCOMPANYING_GUEST_DETAILS]: isAccompanyingGuestDetailsEnabled,
    [FT_PI_BB_CANCELLATION_POLICY]: isCancellationPolicyEnabled,
    [FT_BB_MARKETING_EMAIL_OPTIN]: isMarketingEmailOptInEnabled,
    [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN_ANCILLARIES_GDP_PAYMENT]: isCityTaxBreakdownEnabled,
    [FT_BB_FREE_FNB_AND_EXTRAS]: isFreeFnbAndExtrasEnabled,
  } = useFeatureToggle();

  const getTypographyProps = useSemanticTypography();

  const infoMessagesNotificationTypographyProps = getTypographyProps(
    infoMessagesNotificationLegacyTypography,
    infoMessagesNotificationSemanticTypography
  );
  const infoMessagesNotificationDescriptionTextStyle =
    'textStyle' in infoMessagesNotificationTypographyProps
      ? (infoMessagesNotificationTypographyProps.textStyle as string)
      : undefined;

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

  const [basketDetailsState] = useLocalStorage(
    BASKET_DETAILS_STORAGE_KEY,
    BASKET_DETAILS_STATE_INITIAL_VALUE
  );
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
  const { email } = decodeIdToken(idTokenCookie);
  const variables = {
    request: {
      contactType: 'email' as const,
      contactValue: email,
      brandCodes: BRANDCODES[0],
      business: true,
      contactChannelId: null,
    },
  };
  const {
    data: contactPreferencesData,
    isLoading: contactPreferencesLoading,
    isError: contactPreferencesIsError,
  } = useQueryRequest(
    ['ContactPreferences', idTokenCookie],
    GET_CONTACT_PREFERENCES,
    variables,
    undefined,
    idTokenCookie,
    true
  );

  const isWifiExtra = (id?: string) => !!id && WIFI_IDS.includes(id as (typeof WIFI_IDS)[number]);

  const {
    isLoading: pcksIsLoading,
    error: pcksError,
    isError: pcksIsError,
    packages,
    restaurant,
    privacyPolicy,
    hotelHasCityTaxForLeisure,
    hotelHasCityTaxForBusiness,
  } = usePackages({
    adultsNumber: pcksQueryInput.adultsNumber as number,
    childrenNumber: pcksQueryInput.childrenNumber as number,
    hotelId: pcksQueryInput.hotelId,
    basketReferenceId: biQueryInput.basketReference,
    endDate: pcksQueryInput.endDate,
    startDate: pcksQueryInput.startDate,
    bookingFlowId: pcksQueryInput.bookingFlowId,
    nightsNumber: pcksQueryInput.nightsNumber as number,
    upsellItemsAllowed: (companyData as any)?.requestedCompany?.bookingAllowances
      ?.upsellItemsAllowed,
    channel: pcksQueryInput.channel,
  });

  if (packages?.extrasItems) {
    if (
      companyData?.requestedCompany?.bookingAllowances?.extrasCodes &&
      !companyData.requestedCompany.bookingAllowances.extrasCodes.includes(
        COMPANY_DATA_ULTIMATE_WIFI
      )
    ) {
      packages.extrasItems = packages.extrasItems.filter(({ id }: ExtrasItem) => !isWifiExtra(id));
    }
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
    const { publicRuntimeConfig = {} } = getConfig() ?? {};
    const { email } = decodeIdToken(idTokenCookie);
    const userMealPreferenceQuery = userDetails
      ? Promise.resolve(userDetails)
      : queryClient.fetchQuery({
          queryKey: ['userDetails', idTokenCookie],
          queryFn: () =>
            axiosRequest({
              method: 'GET',
              url: `${publicRuntimeConfig.NEXT_PUBLIC_REST_API}/customers/hotels/${email}?business=true`,
              headers: {
                Authorization: `Bearer ${idTokenCookie}`,
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
  }, [idTokenCookie]);

  const priceExtrasTotal =
    selectedExtrasList?.reduce((numberOfExtrasAdded, currentExtras) => {
      numberOfExtrasAdded = numberOfExtrasAdded + currentExtras.price;
      return numberOfExtrasAdded;
    }, 0) ?? 0;

  useEffect(() => {
    if (bkngData && packages) {
      updateAncillariesAnalytics(bkngData.bookingInformation, packages, Area.BB);
    }
  }, [bkngData, packages]);

  useEffect(() => {
    if (
      idTokenCookie &&
      isStringValid(idTokenCookie) &&
      userMealPreferences === NOT_READY_STATE_USER_MEAL_PREF
    ) {
      getUserMealPreference();
    } else {
      setUserMealPreferences('0');
    }
  }, [idTokenCookie]);

  useEffect(() => {
    setContinueButtonPressed(false);
  }, []);

  useEffect(() => {
    let preselectedExtrasPackagesPerRoom: SelectedExtrasPackage[] | undefined = [];
    if (bkngData?.bookingInformation?.reservationByIdList.length > 0) {
      preselectedExtrasPackagesPerRoom = extrasPackagesMapperSelector(roomSelection);
    }
    setSelectedExtrasList(preselectedExtrasPackagesPerRoom);
  }, [bkngData, packages]);

  useEffect(() => {
    analytics.update({
      alcoholAllowed: false,
      dinnerAllowance: false,
      otherChargesAllowed: false,
      validation: '',
      bookingReasonForStay: '',
      wifiAccessAllowed: false,
      wifiOption: '',
      siteType: '6.5',
      contentComponentOrder: 'employees,upsell,contactpreference,wifi,submitdetails,contactdetails',
      loginFormComponentsOrder: 'bookingflowmessages,loginregisterauth0,',
      bookingPanelComponentsOrder:
        'submitdetails,bookingoverview,bookingdonation,bookingdetails, infotext_FlexPID,infotext_FlexPI,infotext_FlexHub,infotext_Advance,infotext_standard,infotext, infotext_semiflex,barthotelinfo,submitdetails_0',
      carParkingAllowed: false,
      userID: cdhEmployeeId,
      marketingOptInChoice: contactPreferencesData?.getContactPreferences?.permissions[0]?.optIn,
    });
  }, []);

  let roomSelection: RoomSelection[] = [];

  const isEarlyCheckinOrLateCheckout = (id?: string) =>
    !!id &&
    (EARLY_CHECKIN_IDS.includes(id as (typeof EARLY_CHECKIN_IDS)[number]) ||
      LATE_CHECKOUT_IDS.includes(id as (typeof LATE_CHECKOUT_IDS)[number]));

  if (packages?.roomSelection) {
    roomSelection = packages.roomSelection;
  }
  const isExtrasInventoryAvailable =
    packages?.extrasItems?.some(
      ({ available }) => (available as number) > 0 || available === null
    ) ||
    roomSelection?.some((room) =>
      room.packagesSelection?.some((packageSelected) =>
        isEarlyCheckinOrLateCheckout(packageSelected?.id)
      )
    );

  useEffect(() => {
    if (
      userMealPreferences !== '0' &&
      userMealPreferences !== 'NOT_READY_STATE_USER_MEAL_PREF' &&
      packages?.meals
    ) {
      const prefAdultMeal = transformBartIdToOperaId(userMealPreferences, packages.meals);
      const freeBreakFastCode = isStringValid(prefAdultMeal)
        ? packages.meals.find((meal: MealItem) => meal.id === prefAdultMeal)?.freeBreakfastCode
        : '';
      setPrefMealId({ adultMeal: prefAdultMeal, childMeal: String(freeBreakFastCode) });
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

  const getMappedMeals = (isAdultHasMealsFree: boolean) =>
    mealsMapperSelector(
      packages?.meals,
      packages?.mealsKids,
      roomSelection,
      isAdultHasMealsFree,
      listGuests
    );

  const {
    mutation: svBknMutation,
    isLoading: svBknIsLoading,
    isError: svBknIsError,
    error: svBknError,
    isSuccess: svBknIsSuccess,
  } = useMutationRequest(SAVE_RESERVATION_ANCILLARIES);

  const {
    mutation: svGstMutation,
    isLoading: svGstIsLoading,
    isError: svGstIsError,
    error: svGstError,
    isSuccess: svGstIsSuccess,
  } = useMutationRequest(CREATE_RESERVATION_GUEST);

  useEffect(() => {
    if (!svBknIsError && svBknIsSuccess && svGstIsSuccess) {
      queryClient.invalidateQueries({
        queryKey: [
          'GetBookingInformation',
          biQueryInput.language,
          biQueryInput.country,
          biQueryInput.basketReference,
        ],
      });

      const paymentSearchParams = new URLSearchParams({
        reservationId: biQueryInput.basketReference,
        [PAYMENT_NAVIGATION_SOURCE_QUERY_PARAM]: PAYMENT_NAVIGATION_SOURCE_GUEST_DETAILS,
      });

      router.push(
        `/${currentCountry}/${currentLang}/business-booker/booking-business/payment?${paymentSearchParams.toString()}`
      );
    }
  }, [svBknIsSuccess, svBknIsError, biQueryInput, queryClient, svGstIsSuccess]);

  const firstRoom = bkngData?.bookingInformation?.reservationByIdList[0] ?? {};

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

  let adultsMeals: MealItem[] = adultsMealsSelector(packages?.meals, noNights);

  let childrenMeals: MealKids[] = childrenMealsSelector(packages?.mealsKids);

  let closedOutMeals: MealItem[] = adultsMealsSelector(packages?.meals, noNights);

  let isAncillaryCloseout = false;

  const isAdultHasMealsFree = !!(
    isFreeFnbAndExtrasEnabled &&
    adultsMeals?.length &&
    adultsMeals?.some((mealItem) => mealItem?.isFree)
  );

  if (ancillaryCloseoutData?.items?.length && adultsMeals.length && childrenMeals.length) {
    const ancillaryData: AncillaryFilterData = {
      arrivalDate: firstRoom.roomStay?.arrivalDate,
      departureDate: firstRoom.roomStay?.departureDate,
      ancillaryCloseoutData,
      adultsMeals,
      childrenMeals,
      closedOutMeals,
    };
    const { filteredAdultsMeals, filteredChildrenMeals, filteredClosedOutMeals } =
      filterPackagesByAncillariesCloseOut(ancillaryData);
    adultsMeals = filteredAdultsMeals;
    childrenMeals = filteredChildrenMeals;
    closedOutMeals = filteredClosedOutMeals;
    isAncillaryCloseout = filteredAdultsMeals?.length === 0;
  }

  useEffect(() => {
    const hasMealPackages = !!packages?.meals && !!packages?.mealsKids;

    const hasRoomSelection = !!roomSelection?.length;

    const canMapMeals = hasRoomSelection && hasMealPackages && isAdultHasMealsFree;

    if (
      bkngData?.bookingInformation?.reservationByIdList.length > 0 &&
      selectedMeals.length <= 0 &&
      userMealPreferences !== NOT_READY_STATE_USER_MEAL_PREF
    ) {
      let preselectedMeals: SelectedMealsPerRoom[] = [];

      const userMealPreferencesSelection =
        userMealPreferences != '0' && !checkIfExistPreselection(roomSelection);

      if (canMapMeals) {
        preselectedMeals = getMappedMeals(isAdultHasMealsFree);
      } else if (roomSelection && hasMealPackages) {
        const preferredMealId = transformBartIdToOperaId(
          userMealPreferences,
          packages.meals as MealItem[]
        );
        const shouldAutocompleteMeals =
          userMealPreferencesSelection &&
          adultsMeals.some((meal: MealItem) => meal?.id === preferredMealId);

        preselectedMeals = shouldAutocompleteMeals
          ? autocompleteMeals(adultsMeals, childrenMeals, listGuests, preferredMealId)
          : mealsMapperSelector(adultsMeals, childrenMeals, roomSelection);
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
  }, [bkngData, packages, prefMealId, userMealPreferences, isAdultHasMealsFree]);

  const getMealQuantity = useCallback(
    (mealId: string, roomIndex: number) => {
      const currentSelection = numberOfSelectionsPerRoomSelector(selectedMeals);
      if (currentSelection.length > 0) {
        const item = currentSelection[roomIndex].packagesSelection.filter(
          (pck) => pck.id === mealId
        );
        return item.length > 0 ? item[0]?.noOfSelections : 0;
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
            ...adultsMeals.map((meal: MealItem) => ({
              code: meal?.id ?? '',
              legend: meal?.name ?? '',
              quantity: getMealQuantity(meal?.id ?? '', roomIndex),
              price: meal?.price?.toFixed(2) ?? '',
              currency: meal?.currency,
              freeBreakfastCode: meal?.freeBreakfastCode,
              freeBreakfastOption: meal?.freeBreakfastOption,
            })),
            ...childrenMeals.map((meal: MealItem) => ({
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
  ]);
  const menus: Menu[] = menusSelector(adultsMeals, childrenMeals);

  const privacyPolicyData: PrivacyPolicy = securityNoticeMoreInfoDataSelector(privacyPolicy ?? {});

  const logoRestaurantUrl = isStringValid(restaurant?.logoSrc)
    ? formatAssetsUrl(restaurant?.logoSrc ?? '')
    : '';

  const showFreeFoodKidsNotification =
    adultsMeals &&
    adultsMeals.some((meal: MealItem) => meal.freeBreakfastOption) &&
    freeBreakfastMaxAllowance(adultsMeals) > 0 &&
    bkngData?.bookingInformation?.reservationByIdList?.some(
      (room: ReservationById) => room?.roomStay?.childrenNumber && room.roomStay.childrenNumber > 0
    );

  const showFreeFoodKids =
    bookingInformation.children > 0 &&
    adultsMeals.some((meal: MealItem) => meal.freeBreakfastOption) &&
    freeBreakfastMaxAllowance(adultsMeals) > 0;

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

  const showDataCollectionMsgAndForm = isDataCollectionMsgAndFormVisible(accessLevel);
  const showSelfBookerDetailsFor2AdultsInOneRoom =
    isGuestDetailsPageAllowed(accessLevel) && isAccompanyingGuestDetailsEnabled;

  const showGeneralDataCollectionMsgAndForm =
    accessLevel === BUSINESS_BOOKER_USER_ROLES.SELF &&
    bkngData?.bookingInformation?.reservationByIdList[0]?.roomStay?.adultsNumber === 2
      ? showSelfBookerDetailsFor2AdultsInOneRoom
      : showDataCollectionMsgAndForm;

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
      rateDescription: basketDetailsState?.rateDescription || '',
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
    showAutocompleteMealsNotification: shouldDisplayAutocompleteNotification(
      listGuests,
      selectedMeals,
      prefMealId
    ),
    cityTaxTotal: bkngData?.bookingInformation?.cityTaxTotal || '',
  };

  useEffect(() => {
    if (
      bkngData?.bookingInformation?.reservationByIdList.length > 0 &&
      bookingSummaryData?.totalCost?.initialTotalCost
    ) {
      const firstRoom = bkngData.bookingInformation.reservationByIdList[0].roomStay ?? {};
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
              sku: bkngData?.bookingInformation?.hotelId,
              totalNumberOfRooms: bkngData?.bookingInformation?.reservationByIdList?.length,
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
  ]);

  const reservationDetails: BookingDataReservationDetailsProps = {
    arrivalDate: firstRoom?.roomStay?.arrivalDate ?? null,
    departureDate: firstRoom?.roomStay?.departureDate ?? null,
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
  const noImportantInfoMessages = listOfImportantMessages.length;

  const infoMessages: string[] = noImportantInfoMessages > 0 ? listOfImportantMessages : [];

  const formatGuestTitles = (title: string | undefined) => {
    if (!title) return '';
    const translationKey = `details.userForm.titles.${title.toLowerCase()}`;
    return i18n?.exists(translationKey) ? t(translationKey) : title;
  };

  const onSubmit = async (guestDetails: any) => {
    const selectionInfo = {
      basketReferenceId: logicalOrOperator(biQueryInput.basketReference, null),
      hotelId: logicalOrOperator(bkngData?.bookingInformation?.hotelId, null),
      arrivalDate: logicalOrOperator(firstRoom.roomStay?.arrivalDate, null),
      departureDate: logicalOrOperator(firstRoom.roomStay?.departureDate, null),
      roomsSelections: addReservationNumber(
        numberOfSelectionsPerRoomSelector(selectedMeals),
        bkngData?.bookingInformation?.reservationByIdList ?? [],
        selectedExtrasList
      ),
      previousRoomsSelections: logicalOrOperator(roomSelection, null),
    };

    const bookingInfo = {
      biQueryInput,
      bkngData,
    };

    setContinueButtonPressed(true);
    const dataToSend = formatDataToSend(
      cdhCompanyId,
      formatGuestTitles((userData as any)?.contactDetail.title),
      {
        ...userData,
        acceptFutureMailing:
          acceptFutureMailing ||
          !!contactPreferencesData?.getContactPreferences?.permissions[0]?.optIn,
      },
      bookingInfo,
      accessLevel,
      accessLevel === BUSINESS_BOOKER_USER_ROLES.SELF ? guestList : guestDetails,
      isAccompanyingGuestDetailsEnabled
    );
    svBknMutation.mutate(selectionInfo, {
      onSuccess: () => {
        svGstMutation.mutate(dataToSend);
      },
    });
  };

  const headingTitle = t('upsell.meals.title');
  // set currentReasonForStay for BB, as business by default
  // as there are no lesisure/business radio options to select in BB GDP
  const currentReasonForStay = PurposeOfStay.BUSINESS;
  const cityTaxMessages = getCityTaxMessages(
    hotelHasCityTaxForLeisure,
    hotelHasCityTaxForBusiness,
    currentReasonForStay,
    t,
    bkngData?.bookingInformation?.currencyCode,
    currentLang,
    bkngData?.bookingInformation?.totalCost
  );

  const displayExtras = packages?.extrasItems && isExtrasInventoryAvailable;

  const displayRestaurantUnavailableNotification = showRestaurantMessage || isAncillaryCloseout;

  const hideRestaurantSection = !displayExtras && displayRestaurantUnavailableNotification;

  const validationLabels = {
    titleError: t('config.errorMessages.yourDetails.title.required'),
    firstNameRequiredError: t('config.errorMessages.yourDetails.firstName.required'),
    firstNameMinError: t('config.errorMessages.yourDetails.firstName.min'),
    lastNameRequiredError: t('config.errorMessages.yourDetails.lastName.required'),
    firstNameInvalidError: t('config.errorMessages.yourDetails.firstName.invalid'),
    lastNameInvalidError: t('config.errorMessages.yourDetails.lastName.invalid'),
    emailInvalidError: t('config.errorMessages.yourDetails.email.max'),
    checkUniqueError: t('config.errorMessages.guestDetails.firstName.duplicate'),
  };

  const guestDetailsLabels = {
    title: t('booking.contactDetails.title'),
    firstName: t('booking.contactDetails.name'),
    lastName: t('booking.contactDetails.surname'),
    email: t('booking.contactDetails.email'),
  };

  const [guestList, setGuestList] = useState<FormProps['defaultValues']>({
    bbGuestDetails: [
      ...Array.from(
        { length: bkngData?.bookingInformation?.reservationByIdList.length },
        (_, i: number) => i + 1
      ).map(() => ({
        title: '',
        firstName: '',
        lastName: '',
        emailAddress: '',
        id: '',
      })),
    ],
    bbAccompanyingGuestDetails: [
      ...Array.from(
        { length: bkngData?.bookingInformation?.reservationByIdList.length },
        (_, i: number) => i + 1
      ).map(() => ({
        title: '',
        firstName: '',
        lastName: '',
        emailAddress: '',
        id: '',
      })),
    ],
  });

  const getSelfBookerDetails = () => {
    const contactDetails = userData?.contactDetail;
    return contactDetails
      ? `${contactDetails.title} ${contactDetails.firstName} ${contactDetails.lastName}`
      : '';
  };
  const selfBookerDetails = getSelfBookerDetails();

  useEffect(() => {
    if (accessLevel === BUSINESS_BOOKER_USER_ROLES.SELF) {
      setGuestList({
        bbGuestDetails: [
          {
            title:
              accessLevel === BUSINESS_BOOKER_USER_ROLES.SELF
                ? (userData as any)?.contactDetail.title
                : '',

            firstName:
              accessLevel === BUSINESS_BOOKER_USER_ROLES.SELF
                ? (userData as any)?.contactDetail.firstName
                : '',
            lastName:
              accessLevel === BUSINESS_BOOKER_USER_ROLES.SELF
                ? (userData as any)?.contactDetail.lastName
                : '',
            emailAddress:
              accessLevel === BUSINESS_BOOKER_USER_ROLES.SELF
                ? (userData as any)?.contactDetail.email
                : '',
            id: (userData as any)?.business.employeeId,
          },
        ],
        bbAccompanyingGuestDetails: [],
      });
    }
  }, [userData, accessLevel]);

  function setGuestUser(user: Suggestion, index: number, typeOfGuestDetails: string) {
    const tempList: any = guestList;
    typeOfGuestDetails.length !== 0 && typeOfGuestDetails === 'bbGuestDetails'
      ? (tempList.bbGuestDetails[index] = user)
      : (tempList.bbAccompanyingGuestDetails[index] = user);
    setGuestList(tempList);
  }

  const getGuestDetailsFormState: FormProps['getFormState'] = useCallback(
    (state: any) => {
      setGuestList(state as SetStateAction<FormProps['defaultValues']>);
    },
    [setGuestList]
  );

  const handleMarketingOptin = useCallback((optin: boolean) => {
    setAcceptFutureMailing(optin);
    analytics.update({
      optInCustomer: optin,
    });
  }, []);

  if (typeof window !== 'undefined') {
    const HDPPath = localStorage.getItem('HDPPath');
    const hasVisitedIframe = localStorage.getItem('3cpVisited');
    if (HDPPath && hasVisitedIframe) {
      router.push(HDPPath);
      return <></>;
    }
  }

  return (
    <>
      <SEO
        page={PageName.GUEST_DETAILS}
        hotelId={bkngData?.bookingInformation?.hotelId}
        bookingFlowId={bkngData?.bookingInformation?.bookingFlowId}
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
              taxesMessage={cityTaxMessages?.summaryText ?? ''}
              isExtrasDisplayed={!!packages?.extrasItems}
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
            taxesMessage={cityTaxMessages?.summaryText ?? ''}
            isExtrasDisplayed={!!packages?.extrasItems}
            isCityTaxBreakdownEnabled={isCityTaxBreakdownEnabled}
          />
          {/* eslint-disable-next-line @typescript-eslint/ban-ts-comment */}
          {/*  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore */}
          <Button
            isDisabled={continueButtonPressed}
            form="guestDetailsBBForm"
            type="submit"
            {...(accessLevel === BUSINESS_BOOKER_USER_ROLES.SELF && {
              onClick: onSubmit,
            })}
            size="full"
            mt="lg"
            variant="primary"
            data-testid={formatDataTestId(baseDataTestId, 'BookingSummary-ContinueButton')}
          >
            <Text
              {...continueTextLayoutStyles}
              {...getTypographyProps(continueTextLegacyTypography, continueTextSemanticTypography)}
            >
              {t('booking.summary.continue')}
            </Text>
          </Button>
          <Box
            pt="sm"
            data-testid={formatDataTestId(baseDataTestId, 'BookingSummary-InfoMessages')}
            className={`bookingInfoMessages-${noBookingInfoMessages} importantInfoMessages-${noImportantInfoMessages}`}
          >
            {infoMessages?.map((notification: string) => {
              if (notification.length) {
                return (
                  <Box mt="md" key={notification}>
                    <Notification
                      maxWidth="full"
                      variant="info"
                      status="info"
                      description={
                        <Box className="formatLinks">{renderSanitizedHtml(notification)}</Box>
                      }
                      svg={<Info />}
                      descriptionTextStyle={infoMessagesNotificationDescriptionTextStyle}
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

  function getBookingAllowancesNotification() {
    const bookingAllowancesNotificationTypographyProps = getTypographyProps(
      bookingAllowancesNotificationLegacyTypography,
      bookingAllowancesNotificationSemanticTypography
    );
    const bookingAllowancesNotificationDescriptionTextStyle =
      'textStyle' in bookingAllowancesNotificationTypographyProps
        ? (bookingAllowancesNotificationTypographyProps.textStyle as string)
        : undefined;

    let notification = (
      <Box pt="xl" pb="xl">
        <Notification
          maxWidth="full"
          variant="success"
          status="success"
          description={<Box>{t('upsell.meal.available.notification')}</Box>}
          svg={<Success />}
          descriptionTextStyle={bookingAllowancesNotificationDescriptionTextStyle}
        />
      </Box>
    );
    if (!adultsMeals?.length) {
      notification = (
        <Box pt="md">
          <Notification
            maxWidth="full"
            variant="info"
            status="info"
            description={<Box>{t('upsell.meal.not.available.notification')}</Box>}
            svg={<Info />}
            descriptionTextStyle={bookingAllowancesNotificationDescriptionTextStyle}
          />
        </Box>
      );
    }
    if (companyData) {
      return notification;
    }
    return null;
  }

  function renderMealsSelection(multiRoomSelection = false) {
    const hasMenus = menus?.length > 0;

    if (companyData) {
      return (
        <>
          <MealSelection
            headingTitle={headingTitle}
            logoRestaurantUrl={logoRestaurantUrl}
            adults={bookingInformation.adults}
            nights={bookingInformation.nrNights}
            kids={bookingInformation.children}
            adultsMeals={adultsMeals}
            childrenMeals={childrenMeals}
            selectedRoom={selectedRoom}
            selectedMeals={selectedMeals}
            setSelectedMeals={setSelectedMeals}
            showFreeFoodKids={showFreeFoodKids}
            prefixDataTestId={baseDataTestId}
            hasMenus={hasMenus}
            notification={!multiRoomSelection && getBookingAllowancesNotification()}
            isAdultHasMealsFree={isAdultHasMealsFree}
          />
          {hasMenus && <Menus availableMenus={menus} prefixDataTestId={baseDataTestId} />}
          {displayExtras && (
            <Box sx={!displayRestaurantUnavailableNotification ? { mt: '3rem' } : { mt: '0' }}>
              <ExtrasSection
                extrasDetailsList={packages?.extrasItems}
                selectedRoom={selectedRoom}
                selectedExtrasList={selectedExtrasList}
                handleSelectedExtrasList={handleSelectedExtrasList}
                noNights={noNights}
              />
            </Box>
          )}
        </>
      );
    }
    return null;
  }

  function renderMultipleRoomsStyle() {
    return displayRestaurantUnavailableNotification
      ? noRestaurantMultiRoomsSelectionStyle
      : multiRoomMealsSelectionContainerStyle;
  }

  function renderMultiRoomMealsSelection() {
    return (
      !!companyData && (
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
              onChange={setSelectedRoom}
              prefixDataTestId={baseDataTestId}
              shortMobileLabels={true}
              singleContent={
                <Box {...renderMultipleRoomsStyle()} data-testid="multipleRoom">
                  {renderMealsSelection(!!'multiRoomSelection')}
                </Box>
              }
              setStartingTab={setStartingTab}
              startingTab={startingTab}
              tabScrollSize={SCROLLABLE_TABS_SIZE}
              isScrollable={isScrollable}
              isMobileView={isMobileView}
              hasRoomLabels={true}
              labelStyles={roomTabsLabelStyles}
            />
          </Box>
        </>
      )
    );
  }
  function renderGuestDetailsOrAccompanyingGDSection() {
    return (
      <GuestDetailsBBContainer
        numberOfRooms={bkngData?.bookingInformation?.reservationByIdList.length}
        reservationByIdList={bkngData?.bookingInformation?.reservationByIdList}
        validationLabels={validationLabels}
        labels={guestDetailsLabels}
        onSubmit={onSubmit}
        guestList={guestList}
        getFormState={getGuestDetailsFormState}
        queryClient={queryClient}
        setGuestUser={setGuestUser}
        isDynamicSearchVisible={true}
        isAccompanyingGuestDetailsEnabled={isAccompanyingGuestDetailsEnabled}
        accessLevel={accessLevel}
        selfBookerDetails={selfBookerDetails}
        userDetails={userDetails}
      />
    );
  }

  function renderPageContent() {
    if (
      pcksIsLoading ||
      bkngIsLoading ||
      hiIsLoading ||
      svBknIsLoading ||
      svBknIsSuccess ||
      svGstIsLoading ||
      contactPreferencesLoading
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
    if (svGstIsError) {
      return (
        <Box>
          <Box>Error on getting save booking information....</Box>
          <Box bg="orange" color="black">
            {(svGstError as Error).message}
          </Box>
        </Box>
      );
    }

    const privacyPolicyLegacyTypography = {};
    const privacyPolicySemanticTypography = { textStyle: 'body-s-regular' };
    const privacyPolicyTypographyProps = getTypographyProps(
      privacyPolicyLegacyTypography,
      privacyPolicySemanticTypography
    );
    const privacyPolicyDescriptionTextStyle =
      'textStyle' in privacyPolicyTypographyProps
        ? (privacyPolicyTypographyProps.textStyle as string)
        : undefined;

    return (
      <>
        {hideRestaurantSection && (
          <Flex direction="column">
            <RestaurantMessage
              messageDescription={displayRestaurantDescription}
              messageTitle={displayRestaurantTitle}
              prefixDataTestId={baseDataTestId}
            />

            {showGeneralDataCollectionMsgAndForm && (
              <>
                <Box mt="md">
                  <Notification
                    maxWidth="full"
                    variant="info"
                    status="info"
                    description={t('booking.header.privacyPolicy.message.bb')}
                    svg={<Info />}
                    descriptionTextStyle={privacyPolicyDescriptionTextStyle}
                  />
                </Box>
                {renderGuestDetailsOrAccompanyingGDSection()}
              </>
            )}
          </Flex>
        )}
        {!hideRestaurantSection && (
          <>
            <Text
              {...titleLayoutStyles}
              {...getTypographyProps(titleLegacyTypography, titleSemanticTypography)}
              data-testid={formatDataTestId(baseDataTestId, 'MealsHeading')}
            >
              {t('upsell.heading')}
            </Text>

            {showFreeFoodKidsNotification && !!companyData && (
              <Box mt="xl">
                <FreeFoodKidsNotification prefixDataTestId={baseDataTestId} />
              </Box>
            )}
            {displayRestaurantUnavailableNotification && (
              <Box mt="xl">
                <RestaurantMessage
                  messageDescription={displayRestaurantDescription}
                  messageTitle={displayRestaurantTitle}
                  prefixDataTestId={baseDataTestId}
                />
              </Box>
            )}
            <Box mt="xl">
              {showGeneralDataCollectionMsgAndForm && (
                <Notification
                  maxWidth="full"
                  variant="info"
                  status="info"
                  description={t('booking.header.privacyPolicy.message.bb')}
                  svg={<Info />}
                  descriptionTextStyle={privacyPolicyDescriptionTextStyle}
                />
              )}
            </Box>

            {showGeneralDataCollectionMsgAndForm && renderGuestDetailsOrAccompanyingGDSection()}
            {mealsTabs.length > 1 &&
            bkngData?.bookingInformation?.reservationByIdList.length > 1 ? (
              <Box>
                {getBookingAllowancesNotification()}
                {renderMultiRoomMealsSelection()}
              </Box>
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
            rate={bookingSummaryData.rateInformation?.rate}
          />
        </Box>
        {isMarketingEmailOptInEnabled &&
          !contactPreferencesIsError &&
          !contactPreferencesData?.getContactPreferences?.permissions[0]?.optIn && (
            <Box
              {...marketingEmailStyles}
              data-testid={formatDataTestId(baseDataTestId, 'MarketingEmail')}
            >
              <MarketingEmail
                handleMarketingOptin={handleMarketingOptin}
                testIdPrefix={baseDataTestId}
              />
            </Box>
          )}

        <Box {...continueButtonSectionStyle}>
          {/* @ts-expect-error -- conditional spread of onClick prop is not assignable to Button's strict prop types */}
          <Button
            isDisabled={continueButtonPressed}
            form="guestDetailsBBForm"
            {...(accessLevel === BUSINESS_BOOKER_USER_ROLES.SELF && {
              onClick: onSubmit,
            })}
            type="submit"
            size="full"
            variant="primary"
            data-testid={formatDataTestId(baseDataTestId, 'ContinueButton')}
          >
            <Text
              {...continueTextLayoutStyles}
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
}

export const formatGuests = (
  data: any,
  guestTitle: string,
  userData: any,
  isAccompanyingGuestDetailsEnabled: boolean
) => {
  const stayingGuests: any = [];
  if (data.bbGuestDetails?.length) {
    data.bbGuestDetails.map(async (item: any, index: number) => {
      let guest: StayingGuest = {
        sameAsBooker:
          item.title == userData?.contactDetail?.title &&
          item.firstName == userData?.contactDetail?.firstName &&
          item.lastName == userData?.contactDetail?.lastName &&
          item.emailAddress === userData?.contactDetail?.email,
        stayingGuestDetails: {
          title: item.title,
          firstName: item.firstName,
          lastName: item.lastName,
          employeeAccountId: item.id,
          emailAddress: item.emailAddress,
        },
      };
      if (isAccompanyingGuestDetailsEnabled && data.bbAccompanyingGuestDetails?.length) {
        guest = {
          ...guest,
          accompanyingGuestDetails: {
            title: data.bbAccompanyingGuestDetails[index]?.title,
            firstName: data.bbAccompanyingGuestDetails[index]?.firstName,
            lastName: data.bbAccompanyingGuestDetails[index]?.lastName,
            emailAddress: data.bbAccompanyingGuestDetails[index]?.emailAddress,
            employeeAccountId: data.bbAccompanyingGuestDetails[index]?.employeeAccountId,
          },
        };
      }
      stayingGuests.push(guest);
    });
  }
  return stayingGuests;
};

export const formatDataToSend = (
  cdhCompanyId: string,
  guestTitle: string,
  userData: any,
  bookingInfo: any,
  accessLevel: string,
  guestDetails: any,
  isAccompanyingGuestDetailsEnabled: boolean
) => {
  let stayingGuests: StayingGuest[] = [];
  const { biQueryInput, bkngData } = bookingInfo;
  const contactDetail = userData?.contactDetail;

  const objToSend = {
    companyId: cdhCompanyId,
    acceptFutureMailing: userData?.acceptFutureMailing,
    title: logicalOrOperator(guestTitle),
    firstName: logicalOrOperator(contactDetail.firstName, ''),
    lastName: logicalOrOperator(contactDetail.lastName, ''),
    emailAddress: logicalOrOperator(contactDetail.email, ''),
    mobile: contactDetail.mobile ?? '',
    landline: contactDetail.telephone ?? '',
    addressType: 'BUSINESS',
    postalCode: contactDetail.address.postCode ?? '',
    addressLine1: contactDetail.address.line1 ?? '',
    addressLine2: contactDetail.address.line2 ?? '',
    addressLine3: contactDetail.address.line3 ?? '',
    addressLine4: contactDetail.address.line4 ?? '',
    countryCode: contactDetail.address.countryCodeISO ?? '',
    cityName: contactDetail.address.line4 ?? '',
    basketReference: biQueryInput.basketReference ?? null,
    hotelId: bkngData?.bookingInformation?.hotelId ?? null,
    reasonForStay: 'BUS',
    sendEmailConfirmation: false,
    sendEmailInvoice: false,
  };

  if (accessLevel === BUSINESS_BOOKER_USER_ROLES.SELF) {
    let stayingGuest: StayingGuest = {
      sameAsBooker: true,
      stayingGuestDetails: {
        title: guestTitle,
        firstName: contactDetail.firstName,
        lastName: contactDetail.lastName,
        emailAddress: contactDetail.email,
        employeeAccountId: userData?.business.employeeId,
      },
    };
    if (isAccompanyingGuestDetailsEnabled && guestDetails?.bbAccompanyingGuestDetails?.length) {
      stayingGuest = {
        ...stayingGuest,
        accompanyingGuestDetails: {
          title: guestDetails.bbAccompanyingGuestDetails[0]?.title,
          firstName: guestDetails.bbAccompanyingGuestDetails[0]?.firstName,
          lastName: guestDetails.bbAccompanyingGuestDetails[0]?.lastName,
          emailAddress: guestDetails.bbAccompanyingGuestDetails[0]?.emailAddress,
          employeeAccountId: guestDetails.bbAccompanyingGuestDetails[0]?.id,
        },
      };
    }
    stayingGuests.push(stayingGuest);
  } else {
    stayingGuests = formatGuests(
      guestDetails,
      guestTitle,
      userData,
      isAccompanyingGuestDetailsEnabled
    );
  }

  return { ...objToSend, stayingGuests };
};

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

const continueTextLayoutStyles = {
  color: 'baseWhite',
} as TextProps;

const continueTextLegacyTypography = {
  fontWeight: 'semibold',
  fontSize: 'lg',
  lineHeight: '3',
} as TextProps;

const continueTextSemanticTypography = {
  textStyle: 'label-xl',
} as TextProps;

const titleLayoutStyles = {
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

const roomTabsLabelStyles = {
  selected: { textStyle: 'title-s-emphasis' },
  unselected: { textStyle: 'title-s-emphasis' },
};

const bookingAllowancesNotificationLegacyTypography = {} as TextProps;

const bookingAllowancesNotificationSemanticTypography = {
  textStyle: 'body-s-regular',
} as TextProps;

const infoMessagesNotificationLegacyTypography = {} as TextProps;

const infoMessagesNotificationSemanticTypography = {
  textStyle: 'body-s-regular',
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

const marketingEmailStyles = {
  marginTop: '3xl',
};
