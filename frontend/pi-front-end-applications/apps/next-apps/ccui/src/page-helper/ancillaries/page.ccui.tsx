import {
  Box,
  BoxProps,
  Divider,
  Flex,
  FlexProps,
  Grid,
  GridItem,
  GridItemProps,
  GridProps,
  Text,
  TextProps,
} from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import {
  QueryBookingInformationArgs,
  ReservationById,
  BookingDataReservationDetailsProps,
  BookingSummaryDataProps,
  GET_ANCILLARIES_BOOKING_INFO,
  GET_HOTEL_INFORMATION,
  QueryHotelInformationArgs,
  MealItemExtension,
  MealKids,
  Menu,
  PackagesCriteria,
  SAVE_RESERVATION_ANCILLARIES,
  SelectedMealsPerRoom,
  UPDATE_ANCILLARIES_RATE_CODE,
  RoomSelection,
  GET_PAYMENT_STATUS,
  BASKET_STATUS,
  UpsellsSelection,
  AncillaryFilterData,
  FS_SILENT_SUBSTITUTION,
  Channel,
  SelectedExtrasPackage,
  BASKET_DETAILS_STATE_INITIAL_VALUE,
  BASKET_DETAILS_STORAGE_KEY,
  FT_PI_BB_CCUI_SHOW_MEALS_FREE,
  FREE_FOOD_OPTIONS,
  FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN_ANCILLARIES_GDP_PAYMENT,
  Claims,
  EARLY_CHECKIN_IDS,
  LATE_CHECKOUT_IDS,
  type GuestCountUpdateRateCode,
  FT_CCUI_FREE_FNB_AND_EXTRAS,
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
  FreeFoodKidsNotification,
  Menus,
  RestaurantMessage,
} from '@whitbread-eos/molecules';
import { AgentMemo, BookingSummary, MealSelection } from '@whitbread-eos/organisms';
import {
  analytics,
  adultsMealsSelector,
  bookingGuestCount,
  calculateTotalCostRoomSelection,
  childrenMealsSelector,
  formatAssetsUrl,
  formatDataTestId,
  freeBreakfastMaxAllowance,
  getImportantMessages,
  getNightsNumber,
  hotelInformationSelector,
  isStringValid,
  mealsMapperSelector,
  menusSelector,
  numberOfSelectionsPerRoomSelector,
  roomInformationSelector,
  selectedMealsPerRoomSelector,
  useCustomLocale,
  useMutationRequest,
  usePackages,
  useQueryRequest,
  addReservationNumber,
  filterPackagesByAncillariesCloseOut,
  useFeatureSwitch,
  logicalAndOperator,
  logicalOrOperator,
  analyticsTrackings as trackingTypes,
  getUniqueRoomProperties,
  displayStorageSubstitutionLabels,
  useSilentRoomsMatch,
  renderSanitizedHtml,
  extrasPackagesMapperSelector,
  roomPackageSelection,
  filterWiFiForPlusRooms,
  extrasPackagesAnalyticsMap,
  useLocalStorage,
  useFeatureToggle,
  useSemanticTypography,
  getRestaurantMessageDisplay,
} from '@whitbread-eos/utils';
import { differenceInDays, format } from 'date-fns';
import groupBy from 'lodash/groupBy';
import orderBy from 'lodash/orderBy';
import { useTranslation } from 'next-i18next';
import dynamic from 'next/dynamic';
import { NextRouter } from 'next/router';
import React, { useCallback, useEffect, useRef, useState } from 'react';

export interface Props {
  queryClient: QueryClient;
  hiQueryInput: QueryHotelInformationArgs;
  pcksQueryInput: PackagesCriteria;
  biQueryInput: QueryBookingInformationArgs;
  router: NextRouter;
  user: Claims | undefined;
  setAnalyticsUser: any;
}

const ExtrasSection = dynamic(
  async () => {
    const { ExtrasSection } = await import('@whitbread-eos/organisms');
    return { default: ExtrasSection };
  },
  {
    ssr: false,
  }
);

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
      groupedRoomsSelections[reservationId][0]?.packagesSelection,
      'reservationId'
    );
    const sortedPreviousSelection = orderBy(
      groupedPreviousRoomsSelections[reservationId][0]?.packagesSelection,
      'reservationId'
    );
    if (JSON.stringify(sortedSelection) !== JSON.stringify(sortedPreviousSelection)) {
      return true;
    }
  }
  return false;
}

export default function AncillariesPageCcui({
  queryClient,
  hiQueryInput,
  pcksQueryInput,
  biQueryInput,
  router,
  user,
  setAnalyticsUser,
}: Readonly<Props>) {
  const { t } = useTranslation();
  const { language: currentLang, country: currentCountry } = useCustomLocale();
  const baseDataTestId = 'AncillariesPage';
  const isSilentFeatureFlagEnabled = useFeatureSwitch({
    featureSwitchKey: FS_SILENT_SUBSTITUTION,
  });

  const {
    [FT_PI_BB_CCUI_SHOW_MEALS_FREE]: isshowKidsMealsFreeFlag,
    [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN_ANCILLARIES_GDP_PAYMENT]: isCityTaxBreakdownEnabled,
    [FT_CCUI_FREE_FNB_AND_EXTRAS]: isFreeFnbAndExtrasEnabled,
  } = useFeatureToggle();

  const getTypographyProps = useSemanticTypography();

  const [basketDetailsState] = useLocalStorage(
    BASKET_DETAILS_STORAGE_KEY,
    BASKET_DETAILS_STATE_INITIAL_VALUE
  );
  const [selectedMeals, setSelectedMeals] = useState<SelectedMealsPerRoom[]>([]);
  const [mealsTabs, setMealsTabs] = useState<TabsOptionsItem[]>([]);
  const [continueButtonPressed, setContinueButtonPressed] = useState(false);
  const [roomPackagesChanged, setRoomPackagesChanged] = useState(false);
  const [adultHasMealsFree, setAdultHasMealsFree] = useState(false);
  const setAdultHasMealsFreeRef = useRef(false);
  const [bookingInformation, setBookingInformation] = useState<{
    occupancy: { reservationId: string; adults: number; children: number }[];
    nrNights: number;
    totalAdults: number;
    totalChildren: number;
  }>({
    occupancy: [],
    nrNights: 0,
    totalChildren: 0,
    totalAdults: 0,
  });
  const [selectedRoom, setSelectedRoom] = useState(0);
  const [selectedExtrasList, setSelectedExtrasList] = useState<SelectedExtrasPackage[] | undefined>(
    []
  );

  const handleSelectedExtrasList = (updatedExtrasItemsList: SelectedExtrasPackage[] | undefined) =>
    setSelectedExtrasList(updatedExtrasItemsList);

  //<editor-fold desc="Gathering data" defaultstate="collapsed">
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

  const {
    isLoading: pcksIsLoading,
    error: pcksError,
    isError: pcksIsError,
    packages,
    restaurant,
  } = usePackages({
    adultsNumber: pcksQueryInput.adultsNumber as number,
    childrenNumber: pcksQueryInput.childrenNumber as number,
    hotelId: pcksQueryInput.hotelId,
    basketReferenceId: biQueryInput.basketReference,
    endDate: pcksQueryInput.endDate,
    startDate: pcksQueryInput.startDate,
    bookingFlowId: pcksQueryInput.bookingFlowId,
    nightsNumber: pcksQueryInput.nightsNumber as number,
    channel: Channel.Ccui,
  });

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

  const { data: paymentStatus } = useQueryRequest('GetPaymentStatus', GET_PAYMENT_STATUS, {
    basketReference: biQueryInput.basketReference,
  });

  //</editor-fold>

  const {
    mutation: svBknMutation,
    isError: svBknIsError,
    error: svBknError,
    isSuccess: svBknIsSuccess,
  } = useMutationRequest(SAVE_RESERVATION_ANCILLARIES);

  let previousRoomSelection: RoomSelection[] = [];

  if (packages?.roomSelection) {
    previousRoomSelection = packages.roomSelection;
  }

  const matchedSubstitutions = useSilentRoomsMatch(
    biQueryInput.basketReference,
    bkngData?.bookingInformation?.reservationByIdList ?? []
  );

  const isExtrasInventoryAvailable =
    packages?.extrasItems?.some(
      ({ available }) => (available as number) > 0 || available === null
    ) ||
    previousRoomSelection?.some((room) =>
      room.packagesSelection?.some((packageSelected) => {
        const id = packageSelected?.id;
        if (!id) return false;

        return (
          EARLY_CHECKIN_IDS.includes(id as (typeof EARLY_CHECKIN_IDS)[number]) ||
          LATE_CHECKOUT_IDS.includes(id as (typeof LATE_CHECKOUT_IDS)[number])
        );
      })
    );

  const priceExtrasTotal =
    selectedExtrasList?.reduce((numberOfExtrasAdded, currentExtras) => {
      numberOfExtrasAdded += currentExtras.price;
      return numberOfExtrasAdded;
    }, 0) ?? 0;

  useEffect(() => {
    const listGuests: GuestCountUpdateRateCode = {
      adultsNumber:
        bkngData?.bookingInformation?.reservationByIdList?.map(
          (room: ReservationById) => room?.roomStay?.adultsNumber ?? 0
        ) ?? [],
      childrenNumber:
        bkngData?.bookingInformation?.reservationByIdList?.map(
          (room: ReservationById) => room?.roomStay?.childrenNumber ?? 0
        ) ?? [],
    };

    const hasMeals = !!packages?.meals && !!packages?.mealsKids;

    let preselectedMeals: SelectedMealsPerRoom[] = [];

    if (
      bkngData?.bookingInformation?.reservationByIdList.length > 0 &&
      adultHasMealsFree === true
    ) {
      if (previousRoomSelection && hasMeals) {
        preselectedMeals = mealsMapperSelector(
          packages?.meals,
          packages?.mealsKids,
          previousRoomSelection,
          adultHasMealsFree,
          listGuests
        );
      }
    } else if (
      previousRoomSelection &&
      hasMeals &&
      adultHasMealsFree === false &&
      selectedMeals.length <= 0
    ) {
      preselectedMeals = mealsMapperSelector(
        packages?.meals,
        packages?.mealsKids,
        previousRoomSelection
      );
    }

    if (
      bkngData?.bookingInformation?.reservationByIdList.length > 0 &&
      (selectedMeals.length <= 0 || adultHasMealsFree)
    ) {
      setSelectedMeals(preselectedMeals);

      const occupancy: {
        reservationId: string;
        adults: number;
        children: number;
      }[] = [];

      let totalAdults = 0;
      let totalChildren = 0;

      bkngData?.bookingInformation?.reservationByIdList.forEach((room: ReservationById) => {
        if (room?.roomStay) {
          totalAdults += room.roomStay.adultsNumber ?? 0;
          totalChildren += room.roomStay.childrenNumber ?? 0;

          occupancy.push({
            reservationId: room.reservationId as string,
            adults: room.roomStay.adultsNumber ?? 0,
            children: room.roomStay.childrenNumber ?? 0,
          });
        }
      });

      setBookingInformation({
        occupancy: occupancy,
        nrNights: noNights,
        totalChildren: totalChildren,
        totalAdults: totalAdults,
      });

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
  }, [bkngData, packages, adultHasMealsFree]);

  useEffect(() => {
    if (
      bkngData?.bookingInformation?.reservationByIdList.length > 0 &&
      selectedExtrasList?.length === 0
    ) {
      let preselectedExtrasPackagesPerRoom: SelectedExtrasPackage[] | undefined = [];
      preselectedExtrasPackagesPerRoom = extrasPackagesMapperSelector(previousRoomSelection);
      setSelectedExtrasList(preselectedExtrasPackagesPerRoom);
    }
  }, [bkngData, packages]);

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
          'getPaymentMethodsCCUI',
          biQueryInput.language,
          biQueryInput.country,
          biQueryInput.basketReference,
        ],
      });
    }
  }, [urcIsSuccess, biQueryInput, queryClient]);

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
      roomsSelections: roomsSelections,
      previousRoomsSelections: previousRoomSelection || null,
    };
    setContinueButtonPressed(true);

    if (hasRoomPackagesChanges(roomsSelections, previousRoomSelection)) {
      svBknMutation.mutate(selectionInfo);
      setRoomPackagesChanged(true);
    } else {
      setRoomPackagesChanged(false);
    }
  }, [selectedMeals, previousRoomSelection, selectedExtrasList]);

  useEffect(() => {
    if (
      !svBknIsError &&
      ((roomPackagesChanged && svBknIsSuccess) || !roomPackagesChanged) &&
      continueButtonPressed
    ) {
      queryClient.invalidateQueries({
        queryKey: [
          'GetBookingInformation',
          biQueryInput.language,
          biQueryInput.country,
          biQueryInput.basketReference,
        ],
      });
      router.push(
        `${currentCountry}/${currentLang}/guest-details?reservationId=${biQueryInput.basketReference}`
      );
    }
  }, [
    svBknIsError,
    svBknIsSuccess,
    continueButtonPressed,
    roomPackagesChanged,
    biQueryInput,
    queryClient,
  ]);

  useEffect(() => {
    setAnalyticsUser(user, currentLang);
  }, [user, currentLang]);

  const firstRoom = bkngData?.bookingInformation?.reservationByIdList[0] || {};

  const noNights = getNightsNumber(
    firstRoom.roomStay?.arrivalDate,
    firstRoom.roomStay?.departureDate
  );

  let adultsMeals: MealItemExtension[] = adultsMealsSelector(
    packages?.meals,
    noNights,
    bookingInformation.totalAdults
  );

  const isAdultHasMealsFree = !!(
    isFreeFnbAndExtrasEnabled &&
    adultsMeals?.length &&
    adultsMeals?.some((mealItem) => mealItem?.isFree)
  );

  useEffect(() => {
    if (isAdultHasMealsFree && !setAdultHasMealsFreeRef.current) {
      setAdultHasMealsFree(true);
      setAdultHasMealsFreeRef.current = true;
    }
  }, [isAdultHasMealsFree, setAdultHasMealsFreeRef.current]);

  let childrenMeals: MealKids[] = childrenMealsSelector(packages?.mealsKids);

  const ancillaryCloseoutData = hiData?.hotelInformation?.ancillaryCloseout;

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

  const menus: Menu[] = menusSelector(adultsMeals, childrenMeals);

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
    calculateTotalCostRoomSelection(adultsMeals, previousRoomSelection, noNights) +
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
      arrivalDate: firstRoom.roomStay?.arrivalDate || null,
      departureDate: firstRoom.roomStay?.departureDate || null,
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
      initialRate: bkngData?.bookingInformation?.totalCost,
    },
  };

  const getMealQuantity = useCallback(
    (mealId: string, roomIndex: number) => {
      const currentSelection = numberOfSelectionsPerRoomSelector(selectedMeals);
      if (currentSelection.length > 0) {
        const item = currentSelection[roomIndex]?.packagesSelection.filter(
          (pck) => pck.id === mealId
        );
        return item && item.length > 0 ? item[0]?.noOfSelections : 0;
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
              code: meal.id ?? '',
              legend: meal.name ?? '',
              quantity: getMealQuantity(meal.id ?? '', roomIndex),
              price: Number(meal.price).toFixed(2),
              currency: meal.currency,
              freeBreakfastCode: meal.freeBreakfastCode,
              freeBreakfastOption: meal.freeBreakfastOption,
              upsellType: meal?.upsellType ?? '',
            })),
            ...childrenMeals.map((meal) => ({
              code: meal.id ?? '',
              legend: meal.name ?? '',
              quantity: getMealQuantity(meal.id ?? '', roomIndex),
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

  useEffect(() => {
    if (
      bkngData?.bookingInformation?.reservationByIdList.length > 0 &&
      bookingSummaryData?.totalCost?.initialTotalCost
    ) {
      const firstRoom = bkngData.bookingInformation.reservationByIdList[0].roomStay || {};
      let totalAdults = 0;
      let totalChildren = 0;

      bkngData?.bookingInformation?.reservationByIdList.forEach((room: ReservationById) => {
        if (room?.roomStay) {
          if (room.roomStay.adultsNumber) {
            totalAdults += room.roomStay.adultsNumber;
          }
          if (room.roomStay.childrenNumber) {
            totalChildren += room.roomStay.childrenNumber;
          }
        }
      });

      const arrivalDay = format(new Date(firstRoom.arrivalDate), 'EEEE');
      const departureDay = format(new Date(firstRoom.departureDate), 'EEEE');

      analytics.update({
        bookingPanelComponentsOrder: '',
        cellCode: '',
        contentComponentOrder: '',
        FromToDate: {
          ArrivalDay: arrivalDay,
          DepartureDay: departureDay,
          FromToDay: `${arrivalDay}-${departureDay}`,
        },
        lettingTypes: '',
        productDetails: [
          {
            type: trackingTypes.HOTEL,
            quantity: bkngData.bookingInformation.reservationByIdList.length,
            price: {
              basePrice: (
                bkngData?.bookingInformation?.totalCost -
                calculateTotalCostRoomSelection(adultsMeals, previousRoomSelection, noNights)
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
        productSelectedRate: firstRoom.rateExtraInfo?.rateName,
        rateCode: firstRoom.ratePlanCode,
        RoomTypes: getUniqueRoomProperties(bkngData?.bookingInformation?.reservationByIdList),
        RoomNames: getUniqueRoomProperties(
          bkngData?.bookingInformation?.reservationByIdList,
          'roomName'
        ),
        sessionId: '',
        upsells: {
          rooms: getPackagesSelectionAnalytics(),
        },
        validation: '',
        wifiAccessAllowed: false,
        wifiOption: '',
      });
    }
  }, [
    adultsMeals,
    bkngData,
    bookingSummaryData?.totalCost?.initialTotalCost,
    getPackagesSelectionAnalytics,
    noNights,
    previousRoomSelection,
    selectedExtrasList,
  ]);

  if (paymentStatus?.basket?.status === BASKET_STATUS.COMPLETED) {
    router.push(`/${currentLang}/${currentCountry}`);
    return <></>;
  }

  const logoRestaurantUrl = isStringValid(restaurant?.logoSrc)
    ? formatAssetsUrl(restaurant?.logoSrc ?? '')
    : '';

  const showFreeDinnerKidsNotification =
    isshowKidsMealsFreeFlag &&
    adultsMeals &&
    adultsMeals.some((meal: MealItemExtension) => meal?.upsellType === FREE_FOOD_OPTIONS.DINNER) &&
    freeBreakfastMaxAllowance(adultsMeals) > 0 &&
    bkngData?.bookingInformation?.reservationByIdList?.some(
      (room: ReservationById) => Number(room?.roomStay?.childrenNumber) > 0
    );

  const showFreeFoodKids = logicalAndOperator(
    bookingInformation.occupancy[selectedRoom]?.children > 0,
    adultsMeals.some((meal: MealItemExtension) => meal.freeBreakfastOption),
    freeBreakfastMaxAllowance(adultsMeals) > 0
  );

  const showFreeFoodKidsForEntire = logicalAndOperator(
    adultsMeals.some((meal: MealItemExtension) => meal.freeBreakfastOption),
    freeBreakfastMaxAllowance(adultsMeals) > 0,
    bookingInformation.occupancy.some((room) => room?.children > 0)
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

  const reservationDetails: BookingDataReservationDetailsProps = {
    arrivalDate: logicalOrOperator(firstRoom?.roomStay?.arrivalDate, null),
    departureDate: logicalOrOperator(firstRoom?.roomStay?.departureDate, null),
    currency: bkngData?.bookingInformation?.currencyCode,
    noRooms: bkngData?.bookingInformation?.reservationByIdList?.length,
    noNights,
  };

  const listOfImportantMessages: string[] = getImportantMessages(
    hiData?.hotelInformation?.importantInfo?.infoItems,
    firstRoom?.roomStay?.arrivalDate,
    firstRoom?.roomStay?.departureDate
  );

  const noImportantInfoMessages = listOfImportantMessages.length;

  const infoMessages: string[] = noImportantInfoMessages > 0 ? listOfImportantMessages : [];

  const entireSelectionTitle = t('upsell.meals.title.allRooms');
  const individualSelectionTitle = t('upsell.meals.title.addMeals');

  const cityTaxMessage = bkngData?.bookingInformation?.hasCityTax
    ? t('booking.overview.includeCityTax')
    : '';

  const displayExtras = packages?.extrasItems && isExtrasInventoryAvailable;

  const displayRestaurantUnavailableNotification = showRestaurantMessage || isAncillaryCloseout;

  const hideRestaurantSection = !displayExtras && displayRestaurantUnavailableNotification;

  return (
    <Grid {...mainAncillariesGridStyle} data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}>
      <GridItem {...bookingSummaryMobileContainerStyle}>
        <Flex
          {...bookingSummaryMobileTriggerStyle}
          data-testid={formatDataTestId(baseDataTestId, 'BookingSummary-MobileVariant')}
          className={`importantInfoMessages-${noImportantInfoMessages}`}
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
            isCityTaxBreakdownEnabled={isCityTaxBreakdownEnabled}
          />
        </Flex>
      </GridItem>
      <GridItem {...pageContentStyle} data-testid={formatDataTestId(baseDataTestId, 'PageContent')}>
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
          isCityTaxBreakdownEnabled={isCityTaxBreakdownEnabled}
        />
        <Button
          onClick={continueBooking}
          isDisabled={continueButtonPressed}
          size="full"
          mt="lg"
          variant="primary"
          data-testid={formatDataTestId(baseDataTestId, 'BookingSummary-ContinueButton')}
        >
          <Text {...continueTextStyle}>{t('booking.summary.continue')}</Text>
        </Button>
        <Box
          pt="sm"
          data-testid={formatDataTestId(baseDataTestId, 'BookingSummary-InfoMessages')}
          className={`importantInfoMessages-${noImportantInfoMessages}`}
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
                  />
                </Box>
              );
            }
          })}
        </Box>
      </GridItem>
    </Grid>
  );

  function renderMealsSelection() {
    const hasMenus = menus?.length > 0;
    return (
      <>
        <MealSelection
          headingTitle={individualSelectionTitle}
          logoRestaurantUrl={logoRestaurantUrl}
          adults={Number(bookingInformation.occupancy?.[selectedRoom]?.adults)}
          nights={Number(bookingInformation.nrNights)}
          kids={Number(bookingInformation.occupancy?.[selectedRoom]?.children)}
          adultsMeals={adultsMeals}
          childrenMeals={childrenMeals}
          selectedRoom={selectedRoom}
          selectedMeals={selectedMeals}
          setSelectedMeals={setSelectedMeals}
          showFreeFoodKids={showFreeFoodKids}
          prefixDataTestId={formatDataTestId(baseDataTestId, 'IndividualSelection')}
          hasMenus={hasMenus}
          isAdultHasMealsFree={isAdultHasMealsFree}
        />
        {hasMenus && <Menus availableMenus={menus} prefixDataTestId={baseDataTestId} />}
        {displayExtras && (
          <Box sx={!displayRestaurantUnavailableNotification ? { mt: '3rem' } : { mt: '0' }}>
            <ExtrasSection
              extrasDetailsList={packages?.extrasItems}
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
    //<editor-fold desc="Is loading and error" defaultstate="collapsed">
    if (pcksIsLoading || bkngIsLoading || hiIsLoading) {
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

    //</editor-fold>
    const adultsPerRoom: number[] = [];
    const kidsPerRoom: number[] = [];
    bookingInformation.occupancy.forEach((room) => {
      adultsPerRoom.push(room.adults);
      kidsPerRoom.push(room.children);
    });

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

            {showFreeFoodKidsForEntire && (
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

            <Box mt="xl">
              <MealSelection
                logoRestaurantUrl={logoRestaurantUrl}
                adultsPerRoom={adultsPerRoom}
                kidsPerRoom={kidsPerRoom}
                adults={bookingInformation.totalAdults}
                kids={bookingInformation.totalChildren}
                nights={Number(bookingInformation.nrNights)}
                adultsMeals={adultsMeals}
                childrenMeals={childrenMeals}
                selectedRoom={selectedRoom}
                selectedMeals={selectedMeals}
                setSelectedMeals={setSelectedMeals}
                showFreeFoodKids={showFreeFoodKidsForEntire}
                prefixDataTestId={formatDataTestId(baseDataTestId, 'EntireStay')}
                headingTitle={entireSelectionTitle}
                isAdultHasMealsFree={isAdultHasMealsFree}
                isForEntireStay
              />
            </Box>

            {displayExtras && (
              <Box sx={{ mt: '3rem' }}>
                <ExtrasSection
                  extrasDetailsList={packages?.extrasItems}
                  handleSelectedExtrasList={handleSelectedExtrasList}
                  selectedRoom={selectedRoom}
                  selectedExtrasList={selectedExtrasList}
                  allRooms={true}
                  noNights={noNights}
                />
              </Box>
            )}

            <Divider {...dividerStyle} />

            <Text
              {...individualMealsTitle}
              data-testid={formatDataTestId(baseDataTestId, 'Individual-Room-Title')}
            >
              {t('upsell.meals.title.individualRoom')}
            </Text>

            {mealsTabs.length > 1 &&
            bkngData?.bookingInformation?.reservationByIdList.length > 1 ? (
              <Box {...multiRoomsContainerStyle}>
                <Tabs
                  options={mealsTabs}
                  variant="greyTabsGroup"
                  index={selectedRoom}
                  onChange={setSelectedRoom}
                  prefixDataTestId={baseDataTestId}
                  shortMobileLabels={true}
                  singleContent={renderMultiRoomMealsSelection()}
                />
              </Box>
            ) : (
              <Box {...singleRoomContainerStyle}>{renderMealsSelection()}</Box>
            )}
          </>
        )}

        <Box {...continueButtonSectionStyle}>
          <Button
            onClick={continueBooking}
            isDisabled={continueButtonPressed}
            size="full"
            variant="primary"
            data-testid={formatDataTestId(baseDataTestId, 'ContinueButton')}
          >
            <Text {...continueTextStyle}>{t('booking.summary.continue')}</Text>
          </Button>
          <BackButton prefixDataTestId={baseDataTestId} />
        </Box>
        <AgentMemo />
      </>
    );
  }

  function renderMultipleRoomsStyle() {
    return displayRestaurantUnavailableNotification
      ? noRestaurantMultiRoomsSelectionStyle
      : multiRoomMealsSelectionContainerStyle;
  }
}

//<editor-fold desc="Styles" defaultstate="collapsed">
const dividerStyle = {
  my: '3xl',
  borderColor: 'lightGrey1',
};

const individualMealsTitle = {
  fontWeight: 'semibold',
  fontSize: { mobile: 'xl', sm: '2xl' },
  lineHeight: { mobile: '3', sm: '4' },
  color: 'var(--chakra-colors-darkGrey1)',
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
  mt: { mobile: 'xl', xs: '3xl', sm: '5xl', md: '3xl' },
  width: { mobile: 'full', md: '72' },
} as BoxProps;

const continueTextStyle = {
  fontWeight: 'semibold',
  fontSize: 'lg',
  lineHeight: '3',
  color: 'baseWhite',
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
  templateColumns: {
    mobile: '1fr',
    lg: '1fr auto',
  },
  columnGap: {
    mobile: '0',
    lg: '32',
    xl: '8.5rem',
  },
  m: '0!important',
} as GridProps;

const pageContentStyle = {
  px: {
    mobile: 'md',
    sm: 'lg',
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
//</editor-fold>
