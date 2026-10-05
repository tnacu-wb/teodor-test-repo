import type { BoxProps } from '@chakra-ui/react';
import { Box, Flex, Heading, Text } from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import {
  Channel,
  AccessibilityInfo,
  HIAEMroomType,
  HIAvailabilityRates,
  HIBasketBookConfirmation,
  HIBasketData,
  BookingFlow,
  HIRoom,
  HIRoomClassCode,
  HIRoomRate,
  HIRoomType,
  HIRoomTypeInfoResponse,
  HotelRates,
  HIHotelInventoryRoomType,
  RoomTypeCodeMapped,
  ReservationRoomType,
  RoomReservation,
  BookingChannelCriteria,
  CompanyData,
  HIRateClassification,
  HIGlobalConfigResponse,
  BOOKING_CHANNEL,
  Area,
  BASKET_DETAILS_STATE_INITIAL_VALUE,
  BASKET_DETAILS_STORAGE_KEY,
  RoomClassCodes,
  twinRoomSpecialRequests,
  ROOM_TYPE,
  FT_PI_META_CLASS_RATE_SEARCH,
  FT_PI_BB_CCUI_NONSILENT_SUBSTITUTION_PER_ROOMCLASS,
  RoomClassOrder,
  HotelBrand,
  FT_CCUI_ENABLE_72H_UK_NOTIFICATION,
  FT_CCUI_ENABLE_72H_DE_NOTIFICATION,
  FT_PI_CCUI_BB_PREM_PLUS_ACCESSIBLE,
  RoomUpgradeContent,
  FT_PI_PROMO_CODE_LANDING_PAGE,
  FT_PI_PROMO_CODE_SITE_WIDE,
  FT_CCUI_PROMO_CODE_LANDING_PAGE,
  FT_BB_PROMO_CODE_LANDING_PAGE,
  FT_PI_NO_ROOM_TYPE_SEARCH,
  FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY,
  PromoKind,
  UserChoice,
  ActiveChoiceType,
  SoftBundles,
} from '@whitbread-eos/api';
import { DoubleBed, Info, LoadingSpinner, Notification, Section } from '@whitbread-eos/atoms';
import {
  Basket,
  ChoiceArchitecture,
  BundleChoice,
  ImportantNotification,
  KeyHotelFacts,
  PromoBox,
  RateNotifications,
  getRoomTypeIcon,
  getRoomTypeLabel,
} from '@whitbread-eos/molecules';
import {
  analytics,
  useCustomLocale,
  useLocalStorage,
  useIsExternalSearch,
  useScrolledPast,
  getRoomClassByRoomClassCode,
  getRateClassification,
  getRoomRatesThatMatchRoomClassifications,
  getAllRoomTypesFromRates,
  isArrivalDateWithinSetHours,
  getRoomTypesFromQuery,
  updateSilentSubstLocalStorage,
  useFeatureToggle,
  useForDiscountedRateMicroSite,
  filterRoomsByRoomClass,
  isInnBusinessApp,
  type PromoActionsType,
  getCookie,
  BUNDLE_CHOICE,
  BUNDLE_CHOICE_OPTIONS,
  getSelectedRoomClassCode,
  getBookingFlowId,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';

import { getIsPromoCodeLandingPageEnabled } from '../../amend/helpers';
import RateCards from './RateCards/RateCards.component';

interface Props {
  channel: Channel;
  variant: string;
  isLoading: boolean;
  isError: boolean;
  data: HIAvailabilityRates;
  error: unknown;
  basketData: {
    hotelId: string;
    arrival: string;
    departure: string;
    numberOfUnits: number;
    numberOfNights: number;
    prevReservationId?: string;
  };
  queryClient: QueryClient;
  bookingFlow: BookingFlow | undefined;
  roomTypeInformationResponse: HIRoomTypeInfoResponse;
  globalConfigResponse: HIGlobalConfigResponse;
  isParentAnalytics: boolean;
  brand: string;
  accessibilityInfo: AccessibilityInfo | undefined;
  phoneNumber: string | undefined;
  bookRsvIsLoading: boolean;
  bookRsvIsSuccess: boolean;
  bookRsvData: HIBasketBookConfirmation;
  bookRsvIsError: boolean;
  bookRsvError: unknown;
  handleBooking: (
    reservations: RoomReservation[],
    bookingChannel: BookingChannelCriteria,
    bookingFlowId?: string
  ) => void;
  isHotelOpeningSoon: boolean;
  isLessThanSm: boolean | undefined;
  isLessThanMd: boolean | undefined;
  isLessThanLg: boolean | undefined;
  isCityTaxEnabled?: boolean;
  isDisabledContinueBtn?: boolean;
  mappedRoomLabels: RoomTypeCodeMapped;
  isSilentFeatureFlagEnabled?: boolean;
  thirdParties?: string;
  companyData?: CompanyData;
  isPrePopulateBillingAddressEnabled?: boolean;
  targetRatePlanCode?: string;
  promoActions?: PromoActionsType;
  isSoftBundlesVisible?: boolean;
  isCityTaxBreakdownEnabled?: boolean;
  hasRateSelectorTitleDescription?: boolean;
}

const checkCorporateDiscount = (rates: HIRateClassification[]): boolean => {
  return rates.every((rate) => {
    return rate?.isCorporateDiscountAvailable !== true;
  });
};

export function getRateCodeIndex(
  rateType: string,
  roomRates: HIRoomRate[],
  metaPromoRateMapping: {
    rate: string;
    code: string;
  }[]
) {
  if (!rateType || !roomRates?.length || !metaPromoRateMapping?.length) {
    return -1;
  }

  const matchedMapping = metaPromoRateMapping.find((item) => rateType.startsWith(item.rate));

  if (!matchedMapping?.code) {
    return -1;
  }

  return roomRates.findIndex((rate) => rate?.ratePlanCode.startsWith(matchedMapping.code));
}

export function getPromotionKind(selectedPromoCode: string, promoActions: PromoActionsType) {
  if (selectedPromoCode === promoActions?.promoState?.code) {
    return promoActions?.promoState?.type;
  }
  return null;
}

export default function RateSelector({
  channel,
  variant,
  isLoading,
  isError,
  data,
  error,
  basketData,
  bookingFlow,
  queryClient,
  roomTypeInformationResponse,
  globalConfigResponse,
  isParentAnalytics,
  brand,
  accessibilityInfo,
  phoneNumber,
  bookRsvIsLoading,
  bookRsvIsError,
  bookRsvIsSuccess,
  bookRsvData,
  bookRsvError,
  handleBooking,
  isHotelOpeningSoon,
  isLessThanSm,
  isLessThanMd,
  isLessThanLg,
  isDisabledContinueBtn,
  mappedRoomLabels,
  isSilentFeatureFlagEnabled,
  thirdParties,
  companyData,
  isPrePopulateBillingAddressEnabled,
  targetRatePlanCode,
  isCityTaxEnabled,
  promoActions,
  isCityTaxBreakdownEnabled,
  isSoftBundlesVisible = false,
  hasRateSelectorTitleDescription,
}: Readonly<Props>) {
  const numberOfNights = basketData.numberOfNights;
  let isInnBusiness = false;
  if (typeof window !== 'undefined') {
    isInnBusiness = !!isInnBusinessApp(window?.location?.host ?? '');
  }
  const scrolledPastRates = useScrolledPast('rate-cards-list');
  const [selectedRoomClassAndRate, setSelectedRoomClassAndRate] = useState('');
  const [currentClassRoomTypes, setCurrentClassRoomTypes] = useState<ReservationRoomType[]>([]);
  const [upgradeRoomContent, setUpgradeRoomContent] = useState<RoomUpgradeContent>({
    priceText: '',
    primaryButtonText: '',
    secondaryButtonText: '',
    price: 0,
    description: '',
    heading: '',
    imageUrl: '',
    roomClass: '',
    pmsRoomTypes: [],
    selectedRoomClass: '',
  });
  const { t } = useTranslation(['common']);
  const router = useRouter();
  const { query } = router || { query: {} };
  const { language, country } = useCustomLocale();
  const [, setBasketReference] = useState('');
  const [, setBasketDetailsState] = useLocalStorage(
    BASKET_DETAILS_STORAGE_KEY,
    BASKET_DETAILS_STATE_INITIAL_VALUE
  );
  const { [FT_PI_NO_ROOM_TYPE_SEARCH]: isNoRoomTypeSearchEnabled } = useFeatureToggle();
  const noRoomTypeSearch =
    isNoRoomTypeSearchEnabled &&
    getCookie(BUNDLE_CHOICE) === BUNDLE_CHOICE_OPTIONS.noRoomTypeSearch;

  const [activeChoice, setActiveChoice] = useState<ActiveChoiceType>({
    rate: '',
    class: '',
    softBundle: undefined,
    isRoomOnly: false,
  });

  const [activeUserChoiceRate, setActiveUserChoiceRate] = useState(
    data?.hotelAvailability?.roomRates[0]?.ratePlanCode
  );

  const [userChoice, setUserChoice] = useState<UserChoice[]>([
    {
      roomNumber: 1,
      roomType: { id: 'DB', icon: <DoubleBed />, label: 'Double', code: 'DB' },
      pmsRoomType: 'DBLDBL',
    },
  ]);

  const getSelectedSoftBundles = (): SoftBundles => {
    return isSoftBundlesVisible && !activeChoice.isRoomOnly && activeChoice.softBundle
      ? activeChoice.softBundle
      : {};
  };

  const { isLoadingGlobalConfig, isErrorGlobalConfig, errorGlobalConfig, dataGlobalConfig } =
    globalConfigResponse;

  const roomUpgradeOptions = dataGlobalConfig?.globalConfig?.roomUpgradeOptions ?? {};

  const { priceText, primaryButtonText, secondaryButtonText } = roomUpgradeOptions;

  const orderedRoomClassCodes = dataGlobalConfig?.globalConfig?.roomClassConfig;

  const globalConfigOrderedRoomClassCodes = dataGlobalConfig?.globalConfig?.roomClassConfig;

  const isExternalSearch = useIsExternalSearch(thirdParties ?? '');
  const {
    [FT_PI_META_CLASS_RATE_SEARCH]: isMetaSearchEnabled,
    [FT_PI_BB_CCUI_NONSILENT_SUBSTITUTION_PER_ROOMCLASS]:
      isNonSilentSubstituNotificPerRoomClassEnabled,
    [FT_CCUI_ENABLE_72H_UK_NOTIFICATION]: isUKNotificationEnabled,
    [FT_CCUI_ENABLE_72H_DE_NOTIFICATION]: isDENotificationEnabled,
    [FT_PI_CCUI_BB_PREM_PLUS_ACCESSIBLE]: isPremPlusAccFeatureFlag,
    [FT_PI_PROMO_CODE_SITE_WIDE]: isPromoCodeSiteWideEnabled,
    [FT_PI_PROMO_CODE_LANDING_PAGE]: hasPiPromoCodeLandingPageEnabled,
    [FT_CCUI_PROMO_CODE_LANDING_PAGE]: hasCcuiPromoCodeLandingPageEnabled,
    [FT_BB_PROMO_CODE_LANDING_PAGE]: hasBbPromoCodeLandingPageEnabled,
    [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: isPromotionsInHotelAvailabilityEnabled,
  } = useFeatureToggle();

  const isPromoCodeLandingPageEnabled = getIsPromoCodeLandingPageEnabled(
    channel,
    hasPiPromoCodeLandingPageEnabled,
    hasCcuiPromoCodeLandingPageEnabled,
    hasBbPromoCodeLandingPageEnabled
  );

  const selectedRoomClassCode = getRoomClassCodeFromSelection();
  const selectedRateIndex = getPlanIndexFromSelection();
  const pmsRoomTypeInventories = data?.hotelInventory?.roomTypeInventories || [];

  const roomRates = data?.hotelAvailability?.roomRates || [];
  const allRoomTypes: HIRoomType[] = getAllRoomTypesFromRates(roomRates);

  const resolvedPromoKind = isPromotionsInHotelAvailabilityEnabled
    ? data?.hotelAvailability?.promotionsInformation?.promoKind
    : getPromotionKind(
        roomRates[selectedRateIndex]?.promotionCode as string,
        promoActions as PromoActionsType
      );

  const selectedRate = getSelectedRate(
    noRoomTypeSearch,
    data?.hotelAvailability,
    roomRates,
    activeUserChoiceRate,
    selectedRateIndex,
    activeChoice,
    resolvedPromoKind as PromoKind
  );

  const selectedRateCategory = data?.ratesInformation?.rateClassifications?.filter((rate) => {
    const activeRate = activeChoice.rate ? activeChoice.rate : selectedRate?.ratePlanCode;

    return rate?.rateClassification === activeRate || rate?.ratePlanCode === activeRate;
  })[0];

  const roomClassCodes = getSortedRoomClassCodes();
  const rateClassifications = roomRates?.map((roomRate: HIRoomRate) =>
    getRateClassification(roomRate?.ratePlanCode, data?.ratesInformation?.rateClassifications)
  );

  const arrivalDate = data?.hotelAvailability?.startDate || '';
  const departureDate = data?.hotelAvailability?.endDate || '';
  const adultsNumber = data?.hotelAvailability?.roomRates?.[0]?.roomTypes[0]?.adults;

  const roomsLabelsForSilentSubst = isSilentFeatureFlagEnabled
    ? getRoomTypesFromQuery(
        query,
        mappedRoomLabels,
        t('hoteldetails.bookingsummary.room'),
        dataGlobalConfig?.globalConfig?.maxRoomsLim?.maxRooms
      )
    : [];

  const isItDiscountedRateMicroSite = useForDiscountedRateMicroSite();

  const rateSelected = { ...roomRates[selectedRateIndex] };
  const availableRooms = selectedRate?.roomTypes?.flatMap((roomType) =>
    roomType.rooms.map((room) => room.roomClass)
  );

  useEffect(() => {
    upgradeRoom(selectedRoomClassCode);
  }, [selectedRoomClassCode, selectedRateCategory]);

  const upgradeRoom = (selectedRate: string) => {
    const selectedRoomClassConfig = globalConfigOrderedRoomClassCodes?.find(
      (config: { code: string }) => config.code === selectedRate
    );

    const availabilityUpgrade = selectedRoomClassConfig?.availableUpgrades?.filter(
      (upgrade: string) => availableRooms?.includes(upgrade as HIRoomClassCode)
    );

    const matchedRoomDetails =
      availabilityUpgrade?.length && roomUpgradeOptions?.roomUpgrades
        ? roomUpgradeOptions.roomUpgrades.filter(
            (upgrade) => upgrade.roomClass === availabilityUpgrade[0]
          )
        : [];

    const selectedRoomPrice =
      rateSelected?.roomTypes?.flatMap((roomType) =>
        roomType.rooms.filter((room) => room.roomClass === selectedRate)
      ) ?? [];

    const upgradableRoom =
      rateSelected?.roomTypes?.flatMap((roomType) => {
        const match = roomType.rooms.find(
          (room) => room?.roomClass === matchedRoomDetails[0]?.roomClass
        );
        return match ? [match] : [];
      }) ?? [];

    const upgradePrice =
      availabilityUpgrade?.[0] !== undefined && upgradableRoom[0] && selectedRoomPrice[0]
        ? upgradableRoom[0]?.roomPriceBreakdown.totalNetAmount -
          selectedRoomPrice[0]?.roomPriceBreakdown.totalNetAmount
        : 0;

    const roomUpgradeContent = {
      ...matchedRoomDetails?.[0],
      priceText,
      primaryButtonText,
      secondaryButtonText,
      price: upgradePrice * basketData?.numberOfUnits,
      pmsRoomTypes: upgradableRoom?.map((room: any) => room.pmsRoomType),
      selectedRoomClass: selectedRoomClassAndRate.split('-')[0],
    };
    matchedRoomDetails.length && upgradePrice >= 0
      ? setUpgradeRoomContent(roomUpgradeContent as RoomUpgradeContent)
      : setUpgradeRoomContent({} as RoomUpgradeContent);
  };

  const rateTagsList = isPromoCodeLandingPageEnabled
    ? getRateClassification(
        roomRates[selectedRateIndex]?.ratePlanCode,
        data?.ratesInformation?.rateClassifications
      )?.rateTags
    : [];

  useEffect(() => {
    if (
      data?.hotelAvailability &&
      roomTypeInformationResponse?.dataRoomTypeInformation &&
      bookingFlow
    ) {
      const activeClass = data?.hotelAvailability?.roomRates?.find(
        (rate) => rate.ratePlanCode === activeUserChoiceRate
      );
      setActiveUserChoiceRate(
        activeClass?.ratePlanCode ?? data?.hotelAvailability?.roomRates[0]?.ratePlanCode
      );
      activeClass &&
        setUserChoice(() =>
          activeClass?.roomTypes.map((room: HIRoomType, i: number) => ({
            roomNumber: room?.roomNumber ?? i,
            roomType: {
              id: getRoomTypeLabel(room?.rooms[0]?.roomType ?? 'DB'),
              icon: getRoomTypeIcon(room?.rooms[0]?.roomType ?? 'DB'),
              label: getRoomTypeLabel(room?.rooms[0]?.roomType ?? 'DB'),
              code: room?.rooms[0]?.roomType ?? 'DB',
            },
            pmsRoomType: room?.rooms[0]?.pmsRoomType ?? 'DBLDBL',
          }))
        );
      saveBasketDetailsToLocalStorage();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [
    data,
    roomTypeInformationResponse?.dataRoomTypeInformation,
    bookingFlow,
    selectedRoomClassAndRate,
    activeUserChoiceRate,
    activeChoice,
    isSoftBundlesVisible,
  ]);

  useEffect(() => {
    if (typeof window !== 'undefined' && roomRates?.length > 0 && isParentAnalytics) {
      const hotelRates = roomRates?.map((roomRate: HIRoomRate) => {
        const rateClassification = getRateClassification(
          roomRate?.ratePlanCode,
          data?.ratesInformation?.rateClassifications
        );
        const roomData = roomRate?.roomTypes?.[0]?.rooms?.filter(
          (room) => room?.roomClass === selectedRoomClassCode
        )[0];

        return {
          cellCode: roomRate?.cellCode ?? '',
          currencyCode: roomRate?.roomTypes?.[0]?.rooms?.[0]?.roomPriceBreakdown?.currencyCode,
          description: rateClassification?.rateName ?? '',
          lettingType: roomData?.cotAvailable
            ? roomData?.specialRequests?.join('_').toLowerCase() + '+c'
            : roomData?.specialRequests?.join('_').toLowerCase(),
          price:
            roomData?.roomPriceBreakdown?.totalNetAmount % 1 === 0
              ? roomData?.roomPriceBreakdown?.totalNetAmount.toString() + '.00'
              : roomData?.roomPriceBreakdown?.totalNetAmount.toString(),
          rateCode: roomRate?.ratePlanCode ?? '',
          text: rateClassification?.rateDescription ?? '',
        };
      }) as HotelRates[];

      if (window?.analyticsData?.analyticsDataSearchResult?.searchResultsDisplayed) {
        analytics.update({
          analyticsDataSearchResult: {
            ...window.analyticsData.analyticsDataSearchResult,
            ...(typeof window.analyticsData.analyticsDataSearchResult.searchResultsDisplayed !==
            'number'
              ? {
                  searchResultsDisplayed: [
                    {
                      ...window.analyticsData.analyticsDataSearchResult.searchResultsDisplayed[0],
                      hotelRates: hotelRates ?? [],
                    },
                  ],
                }
              : {}),
          },
        });
      }
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [data, selectedRoomClassAndRate, isParentAnalytics]);

  useEffect(() => {
    if (roomRates?.length) {
      setDefaultSelectedRoomClassRate();
    }
  }, [data, isMetaSearchEnabled]);

  if (isLoading || isLoadingGlobalConfig) {
    return (
      <Box minHeight={400}>
        <LoadingSpinner loadingText={t('searchresults.list.hotel.loading')} />
      </Box>
    );
  }

  if (isError || isErrorGlobalConfig) {
    return <Text>{(error as Error).message || (errorGlobalConfig as Error).message}</Text>;
  }

  if (isHotelOpeningSoon || !data?.hotelAvailability?.available || !roomRates?.length) {
    return null;
  }

  const cot = getCot();

  let bookingFlowId: string | undefined;
  if (variant === 'BB') {
    bookingFlowId = getBookingFlowId(brand);
  } else {
    bookingFlowId = bookingFlow?.bookingFlowItems?.find(
      (bfi) => bfi?.rateCategory === selectedRateCategory?.rateCategory
    )?.bookingId;
  }

  async function saveLabelsForSilentSubstAndRedirect(reservationRoomTypes: ReservationRoomType[]) {
    if (bookRsvIsSuccess && bookRsvData) {
      const basketReference = bookRsvData?.createReservation?.basketReference || null;
      setBasketReference(basketReference ?? '');
      if (basketReference && roomsLabelsForSilentSubst && !!isSilentFeatureFlagEnabled) {
        if (typeof window !== 'undefined') {
          updateSilentSubstLocalStorage(basketReference, reservationRoomTypes);
        }
      }
      router.push(
        `/${country}/${language}${variant === 'PI' && bookingFlowId ? `/${bookingFlowId}` : ''}/${
          variant === 'BB' ? 'business-booker/booking-business/guest-details' : 'ancillaries'
        }?reservationId=${basketReference}`
      );
    }
  }

  const reservationRoomTypes: string[] =
    selectedRate?.roomTypes?.map((room) => {
      return room?.rooms?.filter((room) =>
        isRoomWithMatchingClassCode(room, selectedRoomClassCode)
      )[0]?.pmsRoomType;
    }) || [];

  let twinRoomsWithBothChoiceCount = 0;

  const checkHasTwinRoomChoice = (room: HIRoomType) => {
    const twinRoomSpecialRequestsPerRoomType: string[] =
      room?.rooms
        ?.map((roomType: HIRoom) => {
          return roomType?.specialRequests;
        })
        .flat() || [];

    const uniqueTwinSpecialRequests = twinRoomSpecialRequests.filter((specialRequest: string) =>
      twinRoomSpecialRequestsPerRoomType?.includes(specialRequest)
    );

    if (uniqueTwinSpecialRequests?.length > 1) {
      twinRoomsWithBothChoiceCount++;
    }

    return (
      room?.roomType === ROOM_TYPE.TWIN &&
      uniqueTwinSpecialRequests !== undefined &&
      uniqueTwinSpecialRequests?.length > 1
    );
  };

  const twinRoomChoiceMatches: HIRoomType[] =
    selectedRate?.roomTypes?.filter(checkHasTwinRoomChoice) || [];

  const hasTwinRoomChoiceMatch = !!twinRoomChoiceMatches?.length;

  // all roomTypes in booking
  const roomTypes: HIRoomType[] =
    selectedRate?.roomTypes?.filter((roomTypeItem) => {
      return roomTypeItem?.roomType;
    }) || [];

  const twinOnlyRoomTypes: HIRoomType[] =
    roomTypes?.filter((roomTypeItem) => {
      return roomTypeItem?.roomType === ROOM_TYPE.TWIN;
    }) || [];

  // ensure all twin rooms offer both twin room choice specialRequests (TWDS/T2S)
  const hasAllTwinRoomsOfferingBothChoice: boolean =
    twinOnlyRoomTypes?.length === twinRoomsWithBothChoiceCount;

  const twinPmsRoomTypes: HIRoom[] = twinOnlyRoomTypes?.map((item) => item?.rooms).flat() || [];

  const uniqueTwinPmsRoomTypes = Array.from(
    new Set(twinPmsRoomTypes.map((item) => item?.pmsRoomType))
  );

  const matchedTwinPmsRoomTypesAgainstInventory: HIHotelInventoryRoomType[] | undefined = [];
  let uniqueTwinPmsRoomTypeNotPresentInHotelInventory = false;

  const getInventoryCountPerTwinPmsRoomTypes = (
    uniqueTwinPmsRoomTypes: string[],
    pmsRoomTypeInventories: HIHotelInventoryRoomType[] | undefined
  ) => {
    pmsRoomTypeInventories?.filter((inventoryItem: HIHotelInventoryRoomType) => {
      if (uniqueTwinPmsRoomTypes?.includes(inventoryItem?.code)) {
        matchedTwinPmsRoomTypesAgainstInventory.push(inventoryItem);
      }
    });
    return matchedTwinPmsRoomTypesAgainstInventory;
  };

  const inventoryCountPerMatchedTwinPmsRoomType = getInventoryCountPerTwinPmsRoomTypes(
    uniqueTwinPmsRoomTypes,
    pmsRoomTypeInventories
  );

  // check that there are no twin pmsRoomTypes missing/not listed in the hotelInventory
  if (
    inventoryCountPerMatchedTwinPmsRoomType?.length === 0 ||
    inventoryCountPerMatchedTwinPmsRoomType?.length < uniqueTwinPmsRoomTypes?.length
  ) {
    uniqueTwinPmsRoomTypeNotPresentInHotelInventory = true;
  }

  // Confirm that all the pmsRoomTypes for TWIN rooms have hotelInventory counts greater than or equal to, the no. of ALL rooms searched for
  const hasInventoryCountPerTwinPmsRoomTypePerRoomCount: boolean =
    inventoryCountPerMatchedTwinPmsRoomType?.length > 0 &&
    inventoryCountPerMatchedTwinPmsRoomType?.every(
      (item) => item?.availableCount >= roomTypes?.length
    ) &&
    !uniqueTwinPmsRoomTypeNotPresentInHotelInventory;

  const hasTwinRoomChoice =
    hasInventoryCountPerTwinPmsRoomTypePerRoomCount &&
    hasTwinRoomChoiceMatch &&
    hasAllTwinRoomsOfferingBothChoice;

  const hasAccessibleRoom = hasDisabledRoom(roomTypeInformationResponse, reservationRoomTypes);

  const isDiscountRateNotAvailable =
    isItDiscountedRateMicroSite &&
    checkCorporateDiscount(data?.ratesInformation?.rateClassifications) &&
    brand === 'PID';

  const getBasketRateName = () => {
    return activeChoice.rate
      ? (getRateClassification(activeChoice.rate, data?.ratesInformation?.rateClassifications)
          ?.rateName ?? '')
      : (getRateClassification(
          roomRates[selectedRateIndex]?.ratePlanCode,
          data?.ratesInformation?.rateClassifications
        )?.rateName ?? '');
  };

  const specialRoomLimitMessage = isDiscountRateNotAvailable && targetRatePlanCode && (
    <Box mt="lg" mb="lg" {...notificationBoxStyles}>
      <Notification
        status="info"
        variant="info"
        prefixDataTestId="hdp_specialratelimit"
        svg={<Info />}
        description={t(`promotion.discountrate.${targetRatePlanCode}.specialratelimit`)}
      />
    </Box>
  );

  const renderRateCards = () => {
    return noRoomTypeSearch ? (
      roomTypeInformationResponse.isLoadingRoomTypeInformation ||
      isLoading ||
      !data?.hotelAvailability ||
      !roomTypeInformationResponse?.dataRoomTypeInformation?.roomTypeInformation?.roomTypes ? (
        <LoadingSpinner />
      ) : (
        <ChoiceArchitecture
          accessibilityInfo={accessibilityInfo}
          brand={brand}
          cot={cot}
          hasAccessibleRoom={hasAccessibleRoom}
          isNonSilentSubstituNotificPerRoomClassEnabled={
            isNonSilentSubstituNotificPerRoomClassEnabled
          }
          roomTypeInformationResponse={roomTypeInformationResponse}
          specialRoomLimitMessage={specialRoomLimitMessage as unknown as typeof Box}
          currentClassRoomTypes={currentClassRoomTypes}
          data={{
            hotelAvailability: data as HIAvailabilityRates,
            ratesInformation:
              roomTypeInformationResponse?.dataRoomTypeInformation?.roomTypeInformation?.roomTypes,
            rateClassifications: rateClassifications as HIRateClassification[],
          }}
          activeRate={activeUserChoiceRate}
          setActiveRate={setActiveUserChoiceRate}
          userChoice={userChoice}
          setUserChoice={setUserChoice}
        />
      )
    ) : isSoftBundlesVisible ? (
      <BundleChoice
        data={{
          roomRates: data.hotelAvailability.roomRates,
          roomTypes:
            roomTypeInformationResponse?.dataRoomTypeInformation?.roomTypeInformation?.roomTypes,
          ratesInformation: data.ratesInformation,
        }}
        roomClassCodes={roomClassCodes as HIRoomClassCode[]}
        activeChoice={activeChoice}
        setActiveChoice={setActiveChoice}
        language={language}
        nights={numberOfNights}
        setSelectedRoomClassAndRate={setSelectedRoomClassAndRate}
        selectedRoomClassAndRate={selectedRoomClassAndRate}
        brand={brand}
      />
    ) : (
      <RateCards
        roomClassCodes={roomClassCodes as HIRoomClassCode[]}
        data={data}
        brand={brand}
        channel={channel}
        selectedRoomClassAndRate={selectedRoomClassAndRate}
        setSelectedRoomClassAndRate={setSelectedRoomClassAndRate}
        roomTypeInformationResponse={roomTypeInformationResponse}
        isLessThanSm={isLessThanSm}
        isLessThanMd={isLessThanMd}
        basketData={basketData}
        allRoomTypes={allRoomTypes}
      />
    );
  };

  return (
    <Section dataTestId="hdp_rateSelector">
      <Box maxW="full">
        {!!hasRateSelectorTitleDescription && (
          <>
            <Heading as="h3" data-testid="hdp_rateSelectorTitle" {...headingStyles}>
              {isSoftBundlesVisible
                ? t('hoteldetails.rates.grid.title.roomAndRate')
                : t('hoteldetails.rates.grid.title')}
            </Heading>
            <Text my="md" data-testid="hdp_rateSelectorDescription">
              {t('hoteldetails.rates.grid.description')}
            </Text>
          </>
        )}
        {variant === 'CCUI' && shouldShowNotification(brand) && (
          <Box mt="lg" mb="md">
            <Notification
              status="info"
              variant="info"
              prefixDataTestId="hdp_payOnArrivalOnly"
              svg={<Info />}
              description={t('ccui.payment.notification.payOnArrivalOnly')}
            />
          </Box>
        )}
        {!noRoomTypeSearch && (
          <Box w={{ base: 'full', lg: 'calc(100% - 333px)' }}>
            <RateNotifications
              accessibilityInfo={accessibilityInfo}
              brand={brand}
              cot={cot}
              hasAccessibleRoom={hasAccessibleRoom}
              isNonSilentSubstituNotificPerRoomClassEnabled={
                isNonSilentSubstituNotificPerRoomClassEnabled
              }
              roomTypeInformationResponse={roomTypeInformationResponse}
              specialRoomLimitMessage={specialRoomLimitMessage as unknown as typeof Box}
              currentClassRoomTypes={currentClassRoomTypes}
            />
          </Box>
        )}
        <Flex w="full" alignItems="flex-start" mb="md" flexWrap={{ base: 'wrap', lg: 'initial' }}>
          <Box id="rate-cards-list" mr={{ base: 0, lg: 'lg' }} w="full">
            {renderRateCards()}
            {isLessThanLg && scrolledPastRates && !isSoftBundlesVisible && (
              <Box px="sm" maxW={'17.1875rem'}>
                <PromoBox
                  channel={channel}
                  promoActions={promoActions as PromoActionsType}
                  metaSearchConfigs={
                    dataGlobalConfig?.globalConfig?.promotionsConfig?.metaPromoRateMappingDtos ?? []
                  }
                />
              </Box>
            )}
          </Box>

          <Box
            data-testid="basketWrapper"
            {...(!isLessThanLg ? { ...basketDesktopScrollStyles(isInnBusiness) } : {})}
          >
            <Basket
              upgradeRoomContent={upgradeRoomContent}
              channel={channel}
              variant={variant}
              roomClassIndexFromSelectedRate={selectedRate?.roomTypes?.[0].rooms?.findIndex(
                (room) => isRoomWithMatchingClassCode(room, selectedRoomClassCode)
              )}
              roomClassCode={selectedRoomClassCode}
              isCityTaxExempt={false}
              isLastFewRooms={data?.hotelAvailability?.limitedAvailability}
              softBundles={getSelectedSoftBundles()}
              isRoomOnly={activeChoice.isRoomOnly}
              isHDPBasket={true}
              hasAccessibleRoom={hasAccessibleRoom}
              hasTwinRoomChoice={hasTwinRoomChoice}
              shouldDisplayMobileBasket={isLessThanLg && scrolledPastRates}
              roomClass={getRoomClassByRoomClassCode(selectedRoomClassCode, language)}
              bookingFlowId={bookingFlowId}
              rateName={getBasketRateName()}
              rateTags={rateTagsList}
              userChoice={userChoice}
              isSoftBundlesVisible={isSoftBundlesVisible}
              adultsNumber={adultsNumber}
              {...{
                bookRsvIsLoading,
                bookRsvIsError,
                bookRsvError,
                handleBooking,
                roomTypeInformationResponse,
                selectedRate,
                queryClient,
                isLessThanLg,
                brand,
                ...basketData,
                isDisabledContinueBtn,
                roomsLabelsForSilentSubst,
                isSilentFeatureFlagEnabled,
                saveLabelsForSilentSubstAndRedirect,
                setCurrentClassRoomTypes,
                companyData,
                selectedRateCategory,
                isPrePopulateBillingAddressEnabled,
                isCityTaxEnabled,
                promoActions,
                isCityTaxBreakdownEnabled,
              }}
              metaSearchConfigs={
                dataGlobalConfig?.globalConfig?.promotionsConfig?.metaPromoRateMappingDtos ?? []
              }
            />
          </Box>
        </Flex>
        <ImportantNotification arrival={arrivalDate} departure={departureDate} />
      </Box>
      <KeyHotelFacts channel={channel} />
    </Section>
  );

  function isRoomWithMatchingClassCode(room: HIRoom, roomClassCode: HIRoomClassCode) {
    return room.roomClass === roomClassCode;
  }

  function getPlanIndexFromSelection() {
    return parseInt(selectedRoomClassAndRate.split('-')[1]);
  }

  function getRoomClassCodeFromSelection() {
    return selectedRoomClassAndRate.split('-')[0] as HIRoomClassCode;
  }

  // Helper to get room class set from a roomType
  function getRoomClassForRoom(roomType: HIRoomType): Set<HIRoomClassCode> {
    const roomClasses = roomType?.rooms?.map((room: HIRoom) => room?.roomClass) ?? [];
    return new Set(roomClasses);
  }

  // Helper to get common room classes from all room types.
  function getCommonRoomClasses(roomClassSets: Set<HIRoomClassCode>[]): HIRoomClassCode[] {
    const [firstSet, ...restSets] = roomClassSets;
    return Array.from(firstSet).filter((roomClass) => restSets.every((set) => set.has(roomClass)));
  }

  // original roomClassCode ordering as per Availability
  function getAvailabilityOrderedRoomClassCodes() {
    const allCommonRoomClasses: HIRoomClassCode[][] = [];

    roomRates.forEach((roomRate) => {
      const roomClassSets = roomRate.roomTypes?.map(getRoomClassForRoom) ?? [];
      const commonRoomClasses = getCommonRoomClasses(roomClassSets);
      allCommonRoomClasses.push(commonRoomClasses);
    });

    // Flatten and remove duplicates
    return Array.from(new Set(allCommonRoomClasses.flat()));
  }

  // When the Pay Now flag is turned on, disable 72h notification
  function shouldShowNotification(brand: string): boolean {
    const isGermanHotel = [HotelBrand.PID].includes(brand as HotelBrand);

    return (
      !(
        (!isGermanHotel && isUKNotificationEnabled) ||
        (isGermanHotel && isDENotificationEnabled)
      ) && isArrivalDateWithinSetHours(arrivalDate, 72)
    );
  }

  // sorted roomClassCode ordering, done in FE
  function getSortedRoomClassCodes() {
    const availabilityOrderedRoomClassCodes: HIRoomClassCode[] =
      getAvailabilityOrderedRoomClassCodes();
    const { PREMIER_PLUS_ROOM, STANDARD_ROOM, BIGGER_ROOM } = RoomClassCodes;

    if (orderedRoomClassCodes && variant.toLowerCase() === Area.PI) {
      return orderedRoomClassCodes
        .filter((orderedClass: RoomClassOrder) =>
          availabilityOrderedRoomClassCodes.includes(orderedClass?.code as HIRoomClassCode)
        )
        .map((availableClass: RoomClassOrder) => availableClass?.code);
    }

    if (
      globalConfigOrderedRoomClassCodes &&
      (variant === BOOKING_CHANNEL.BB || variant === BOOKING_CHANNEL.CCUI)
    ) {
      return globalConfigOrderedRoomClassCodes
        .filter((orderedClass: RoomClassOrder) =>
          availabilityOrderedRoomClassCodes.includes(orderedClass?.code as HIRoomClassCode)
        )
        .map((availableClass: RoomClassOrder) => availableClass?.code);
    }

    return availabilityOrderedRoomClassCodes.sort((roomClassCode1, roomClassCode2) => {
      // If there are multiple room classes available, then the order of displaying them is as follows:
      // PI: Premier Plus, Standard
      // Hub: Bigger Room, Standard
      if (brand.toLowerCase() === 'hub') {
        if (roomClassCode1 === BIGGER_ROOM && roomClassCode2 === STANDARD_ROOM) return -1;
        return 1;
      }
      if (roomClassCode1 === PREMIER_PLUS_ROOM && roomClassCode2 === STANDARD_ROOM) return -1;
      return 1;
    });
  }

  function getRoomRateTotalAmount(roomRate: HIRoomRate, roomClassCode: HIRoomClassCode) {
    return roomRate?.roomTypes
      ?.map((roomType: HIRoomType) => {
        return roomType?.rooms.filter((room) => room?.roomClass === roomClassCode)[0]
          ?.roomPriceBreakdown?.totalNetAmount;
      })
      ?.reduce((sum: number, pricePerRoomType: number) => sum + pricePerRoomType, 0);
  }

  function hasRoomClassAvailability(roomRate: HIRoomRate, roomClassCode: string) {
    return roomRate.roomTypes.every((roomType) =>
      roomType?.rooms?.some((room) => room?.roomClass === roomClassCode)
    );
  }

  function getDefaultRateSelection() {
    const ratesWithClassificationInformation = getRoomRatesThatMatchRoomClassifications(
      rateClassifications,
      roomRates
    );

    let defaultSelectedClassCode = roomClassCodes[0];
    // take index of first (lowest) ordered rate in matches as default
    const defaultSelectedRate = ratesWithClassificationInformation?.filter((roomRate) =>
      hasRoomClassAvailability(roomRate, defaultSelectedClassCode)
    )[0];
    let defaultSelectedRateIndex = roomRates?.findIndex(
      (rate) => rate?.ratePlanCode === defaultSelectedRate?.ratePlanCode
    );

    const isPromoCodeApplied = promoActions?.promoState?.isApplied;

    if (isPromoCodeApplied) {
      const latestBasket =
        typeof window !== 'undefined'
          ? JSON.parse(localStorage.getItem(BASKET_DETAILS_STORAGE_KEY) || '{}')
          : {};

      const roomClassCodeLocalStorage = getSelectedRoomClassCode(latestBasket?.roomClass, language);

      if (roomClassCodeLocalStorage) {
        const promoCodeIndex = roomRates?.findIndex((rate) => rate?.promotionCode);
        if (promoCodeIndex !== -1) {
          defaultSelectedRateIndex = promoCodeIndex;
        }
        defaultSelectedClassCode = roomClassCodeLocalStorage;
      }
    }

    return `${defaultSelectedClassCode}-${defaultSelectedRateIndex}`;
  }

  function getLowestRateSelection() {
    const matchedRateClassificationsToRoomRates = getRoomRatesThatMatchRoomClassifications(
      rateClassifications,
      roomRates
    );
    const rateCodesWithRatesClassifications = matchedRateClassificationsToRoomRates?.map(
      (rate) => rate.ratePlanCode
    );
    let prevAmount = 0;
    let defaultSelectedClassCode = '';
    let defaultSelectedRateIndex = 0;
    const { STANDARD_ROOM } = RoomClassCodes;
    for (let i = 0; i < roomClassCodes?.length; i++) {
      for (let j = 0; j < roomRates?.length; j++) {
        if (
          !(
            rateCodesWithRatesClassifications?.includes(roomRates[j].ratePlanCode) &&
            hasRoomClassAvailability(roomRates[j], roomClassCodes[i])
          )
        ) {
          continue;
        }
        const roomTotalAmount = getRoomRateTotalAmount(
          roomRates[j],
          roomClassCodes[i] as HIRoomClassCode
        );
        if (prevAmount === 0 || prevAmount > roomTotalAmount) {
          defaultSelectedRateIndex = j;
          defaultSelectedClassCode = roomClassCodes[i];
          prevAmount = roomTotalAmount;
        }
      }

      // default to standard rooms if it's present in the roomClassCodes array
      if (roomClassCodes.includes(STANDARD_ROOM)) {
        defaultSelectedClassCode = STANDARD_ROOM;
      }
    }
    return `${defaultSelectedClassCode}-${defaultSelectedRateIndex}`;
  }

  function getMetaRateSelection() {
    const [roomClass, rateType] = (query?.SELECT as string)?.split('-') ?? [];
    const roomClassCode = RoomClassCodes[
      roomClass as keyof typeof RoomClassCodes
    ] as HIRoomClassCode;

    const matchedRateClassifications = getRoomRatesThatMatchRoomClassifications(
      rateClassifications,
      roomRates
    );
    let matchedRateCodeIndex = matchedRateClassifications?.findIndex(
      ({ ratePlanCode }) => ratePlanCode === rateType
    );

    const hasPromotionCode = matchedRateClassifications?.some(
      (item) => item?.promotionCode !== null
    );

    if (isPromoCodeSiteWideEnabled && hasPromotionCode) {
      matchedRateCodeIndex = roomRates?.findIndex(({ ratePlanCode }) => ratePlanCode === rateType);
    }

    if (matchedRateCodeIndex === -1 && isPromoCodeSiteWideEnabled && hasPromotionCode) {
      const metaPromoRateMappingDtos =
        dataGlobalConfig?.globalConfig?.promotionsConfig?.metaPromoRateMappingDtos ?? [];
      const matchedIndex = getRateCodeIndex(rateType, roomRates, metaPromoRateMappingDtos);
      if (matchedIndex !== -1) {
        return `${roomClassCode}-${matchedIndex}`;
      }
    }

    return matchedRateCodeIndex !== -1 && roomClassCodes?.includes(roomClassCode)
      ? `${roomClassCode}-${matchedRateCodeIndex}`
      : getLowestRateSelection();
  }

  function setDefaultSelectedRoomClassRate() {
    const metaSearch = router?.query?.SELECT as string;

    if (roomRates?.length) {
      const matchedRateClassifications = getRoomRatesThatMatchRoomClassifications(
        rateClassifications,
        roomRates
      );
      const hasPromotionCode = matchedRateClassifications?.some(
        (item) => item?.promotionCode !== null
      );
      if (
        selectedRoomClassAndRate &&
        !hasPromotionCode &&
        isPromotionsInHotelAvailabilityEnabled &&
        promoActions?.promoState?.isApplied
      ) {
        const [selectedClass, selectedRateIndex] = selectedRoomClassAndRate.split('-');
        const rate = roomRates[Number(selectedRateIndex)];

        if (rate && hasRoomClassAvailability(rate, selectedClass)) {
          return;
        }
      }
      if (variant === 'PI' && isExternalSearch) {
        setSelectedRoomClassAndRate(
          metaSearch && isMetaSearchEnabled ? getMetaRateSelection() : getLowestRateSelection()
        );
      } else {
        setSelectedRoomClassAndRate(getDefaultRateSelection());
      }
    }
  }

  function saveBasketDetailsToLocalStorage() {
    if (roomRates?.length) {
      const basketDetails = {
        ...basketData,
        selectedRate: {
          ...selectedRate,
          rateCategory: selectedRateCategory?.rateCategory,
          roomTypes: filterRoomsByRoomClass(
            selectedRate?.roomTypes,
            selectedRoomClassCode as string,
            isPremPlusAccFeatureFlag as boolean
          ),
        } as unknown as HIRoomRate,
        roomClass: getRoomClassByRoomClassCode(selectedRoomClassCode, language),
        rateName:
          getRateClassification(
            roomRates[selectedRateIndex]?.ratePlanCode,
            data?.ratesInformation?.rateClassifications
          )?.rateName ?? '',
        rateTags: rateTagsList,
        rateDescription:
          getRateClassification(
            roomRates[selectedRateIndex]?.ratePlanCode,
            data?.ratesInformation?.rateClassifications
          )?.rateDescription ?? '',
        roomTypeInformationResponse,
        bookingFlow,
        phoneNumber: phoneNumber ?? '',
        brand,
        silentSubstitutionLabels: roomsLabelsForSilentSubst,
        isCityTaxEnabled: isCityTaxEnabled,
        softBundles: getSelectedSoftBundles(),
        isSoftBundlesVisible: isSoftBundlesVisible,
      } as HIBasketData;

      // Store basket data in local storage (as a safety net in case of page refresh)
      setBasketDetailsState(basketDetails);
    }
  }

  function hasDisabledRoom(
    roomTypeInformationResponse: HIRoomTypeInfoResponse,
    roomClassCodes: string[]
  ) {
    return (
      // use groupId, as roomCategory can differ in content depending on locale
      roomClassCodes
        ?.map((roomCode) => getRoomGroupId(roomTypeInformationResponse, roomCode))
        ?.some((item) => item?.includes('accessible'))
    );
  }

  function getRoomGroupId(roomTypeInformationResponse: HIRoomTypeInfoResponse, roomCode: string) {
    return roomTypeInformationResponse?.dataRoomTypeInformation?.roomTypeInformation?.roomTypes
      ?.find(
        (roomType: HIAEMroomType) =>
          roomType?.roomTypeCode === roomCode || roomType?.roomTypeCode?.includes(roomCode)
      )
      ?.groupId?.toLowerCase();
  }

  function getCot() {
    const cot = {
      requested: false,
      available: false,
    };

    allRoomTypes?.forEach((roomType) => {
      if (roomType?.cotRequested === true) {
        cot.requested = true;
      }
      roomType?.rooms?.some((room) => {
        if (room?.cotAvailable === true) {
          cot.available = true;
        }
      });
    });

    return cot;
  }
}

export function getSelectedRate(
  noRoomTypeSearch: boolean,
  hotelAvailability: any,
  roomRates: HIRoomRate[],
  activeUserChoiceRate: string,
  selectedRateIndex: number,
  activeChoice: any,
  promoKind: PromoKind
) {
  if (noRoomTypeSearch && hotelAvailability) {
    return {
      ...(roomRates?.find((rate) => rate.ratePlanCode === activeUserChoiceRate) ??
        roomRates[selectedRateIndex]),
      promoKind,
    };
  }

  if (activeChoice.rate) {
    const activeRoomRate =
      roomRates?.find((rate) => rate.ratePlanCode === activeChoice.rate) ??
      roomRates[selectedRateIndex];
    return { ...activeRoomRate, promoKind };
  }

  return { ...roomRates[selectedRateIndex], promoKind };
}

const notificationBoxStyles = {
  mb: 'lg',
  w: { base: 'full', lg: 'calc(100% - 333px)' },
} as BoxProps;

const headingStyles = {
  fontSize: { base: 'xl', md: '2xl' },
  fontWeight: 'semibold',
  lineHeight: { base: '3', md: '4' },
  mb: 'lg',
  size: 'md',
};

const basketDesktopScrollStyles = (isInnBusiness = false) => {
  return {
    position: 'sticky',
    top: isInnBusiness ? 100 : 0,
    marginBottom: '2xl',
  } as BoxProps;
};
