import { Box, Flex, FlexProps, Text, Center } from '@chakra-ui/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import {
  Channel,
  FS_ENABLE_META_ARRIVAL_DAY_KEYWORD_PI,
  FS_SILENT_SUBSTITUTION,
  HeaderInformationQuery,
  HIHotelAvailabilityResponse,
  HIVisualDisplayContext,
  PageName,
  SearchRoomType,
  SITE_LEISURE,
  getStaticContent,
  FT_PI_BB_CCUI_BARRIER_FREE_LABEL,
  CountryCode,
  PromoKind,
  FT_PI_NO_ROOM_TYPE_SEARCH,
  FT_PI_DISPLAY_SOFT_BUNDLES,
  FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN,
  ROOM_CODES,
  FT_PI_ROOM_DETAILS_DRAWER,
  FT_PI_SHOW_PROMOTION_BOX,
  HIAEMroomTypesInfo,
  HIAvailabilityRates,
  FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE,
  FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY,
  PROMO_CODE_COOKIE,
} from '@whitbread-eos/api';
import { CityTax } from '@whitbread-eos/api/dist/types/graphql';
import { PromotionsNotification, LoadingSpinner } from '@whitbread-eos/atoms';
import {
  AnnouncementNotification,
  HotelBadges,
  HotelBreadcrumb,
  HotelContactInformation,
  HotelDescriptionInformation,
  HotelFacilitiesList,
  HotelFaq,
  HotelGallery,
  HotelHeadline,
  HotelLocation,
  HotelParkingInformation,
  HotelRestaurant,
  HotelRooms,
  HotelTitle,
  HubZipNotice,
  OpeningSoonNotification,
  SEARCH_REFERRER_INITIAL_VALUE,
  SEO as Seo,
  SoldOutNotification,
  TripAdvisorReview,
} from '@whitbread-eos/molecules';
import {
  formatDataTestId,
  getMappedRooms,
  getRatePlanCode,
  getTodayTomorrowDate,
  isDateValid,
  MAX_ROOMS_SEARCH_LIMIT,
  isFaqValidForRender,
  isHotelOpeningSoon as isHotelOpeningSoonFunction,
  swapKeysAndValues,
  updateHotelDisplayPageAnalytics,
  useCustomLocale,
  useDiscountRateInfoHotelAvailability,
  useFeatureSwitch,
  useFeatureToggle,
  useForDiscountedRateMicroSite,
  useHotelAvailability,
  useLocalStorage,
  useQueryRequest,
  useStaticHotelInformation,
  type PromotionsInformation,
  type PromoActionsType,
  getActivePromotionCode,
  BUNDLE_CHOICE,
  BUNDLE_CHOICE_OPTIONS,
  getCookie,
  useSoftBundles,
  extractPromoBoxData,
} from '@whitbread-eos/utils';
import { getHotelLabel, labelsConstants } from '@whitbread-eos/utils';
import { add, endOfDay, format, isAfter, isSameDay, startOfDay } from 'date-fns';
import { useTranslation } from 'next-i18next';
import dynamic from 'next/dynamic';
import DefaultErrorPage from 'next/error';
import { NextRouter } from 'next/router';
import { useEffect, useMemo, useState, useRef } from 'react';

interface QueryParams {
  [key: string]: string;
}

interface Props {
  queryClient: QueryClient;
  router: NextRouter;
  visualDisplayContext: HIVisualDisplayContext;
  promotionBannerData?: PromotionsInformation;
  hotelAvailability?: HIAvailabilityRates;
  roomTypeInformation?: HIAEMroomTypesInfo;
}

const Location = dynamic(
  async () => {
    const { Location } = await import('@whitbread-eos/organisms');
    return { default: Location };
  },
  {
    loading: () => <Text> </Text>,
    ssr: false,
  }
);

const RateSelector = dynamic(
  async () => {
    const { RateSelector } = await import('@whitbread-eos/organisms');
    return { default: RateSelector };
  },
  {
    loading: () => <Text> </Text>,
    ssr: false,
  }
);

const SearchContainer = dynamic(
  async () => {
    const { PISearchContainer } = await import('@whitbread-eos/organisms');
    return { default: PISearchContainer };
  },
  {
    ssr: false,
  }
);

export default function HotelDetailsPagePI({
  queryClient,
  router,
  visualDisplayContext,
  promotionBannerData,
  hotelAvailability,
  roomTypeInformation,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const [isParentAnalytics, setIsParentAnalytics] = useState(false);
  const { isLessThanSm, isLessThanMd, isLessThanLg } = visualDisplayContext;
  const { searchLocation, NIGHTS, ROOMS, PROMOID } = router.query;
  let { ARRdd, ARRmm, ARRyyyy } = router.query;
  const {
    [FT_PI_NO_ROOM_TYPE_SEARCH]: isNoRoomTypeSearchEnabled,
    [FT_PI_DISPLAY_SOFT_BUNDLES]: isSoftBundlesEnabled,
  } = useFeatureToggle();
  const promotionBoxCodeCookie = getCookie('appliedPromoBoxCode');
  const noRoomTypeSearch =
    isNoRoomTypeSearchEnabled &&
    getCookie(BUNDLE_CHOICE) === BUNDLE_CHOICE_OPTIONS.noRoomTypeSearch;
  // use helm (server side) feature switch to ensure toggle ready before page load
  const isMetaArrivalDayKeywordFlagEnabled = useFeatureSwitch({
    featureSwitchKey: FS_ENABLE_META_ARRIVAL_DAY_KEYWORD_PI,
    fallbackValue: false,
  });

  const cookieBasedPromo = getCookie(PROMO_CODE_COOKIE);

  // for meta query url - with today or tomorrow set for ARRdd, as only date
  const updatedArrivalDate = getTodayTomorrowDate(ARRdd, ARRmm, ARRyyyy);

  if (isMetaArrivalDayKeywordFlagEnabled) {
    ARRdd = updatedArrivalDate.ARRdd;
    ARRmm = updatedArrivalDate.ARRmm;
    ARRyyyy = updatedArrivalDate.ARRyyyy;
  }

  const { AVAILABLE_HOTEL, UNAVAILABLE_HOTEL, UNCHECKED_HOTEL } = labelsConstants;
  const [, setSearchReferrer] = useLocalStorage('SearchReferrer', SEARCH_REFERRER_INITIAL_VALUE);
  const baseDataTestId = 'HotelDetailsPIPage';
  const { language, country } = useCustomLocale();
  const isSilentFeatureFlagEnabled = useFeatureSwitch({
    featureSwitchKey: FS_SILENT_SUBSTITUTION,
  });
  const isDiscountedRateEnabled = useForDiscountedRateMicroSite();

  const defaultRooms: SearchRoomType[] = [];
  for (let idx = 0; idx < Number(ROOMS) && idx < MAX_ROOMS_SEARCH_LIMIT; idx++) {
    const room: SearchRoomType = {
      adults: Number(router.query[`ADULT${idx + 1}`]),
      children: Number(router.query[`CHILD${idx + 1}`]),
      shouldBeAccessible: router.query[`INTTYP${idx + 1}`] === ROOM_CODES.accessible,
      shouldIncludeCot: !!Number(router.query[`COT${idx + 1}`]),
      roomType: router.query[`INTTYP${idx + 1}`] as string,
    };
    defaultRooms.push(room);
  }

  const {
    [FT_PI_BB_CCUI_BARRIER_FREE_LABEL]: isBarrierFreeLabelEnabledFT,
    [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN]: isCityTaxBreakdownEnabled,
    [FT_PI_ROOM_DETAILS_DRAWER]: isRoomDetailsDrawerEnabled,
    [FT_PI_SHOW_PROMOTION_BOX]: isShowPromoBoxPIEnabled,
    [FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE]: isPromoBoxAppliedCodeCookieEnabled,
    [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: isPromotionsInHotelAvailabilityEnabled,
  } = useFeatureToggle();

  const { arrival, departure, numberOfUnits, rooms, numberOfNights, empOfferCodes, corpId } =
    useMemo(() => getMultiSearchParams(noRoomTypeSearch), [router?.query]);

  const isHotelDetailsOnHDP = !!numberOfNights && !!numberOfUnits;

  const softBundles = useSoftBundles(
    isSoftBundlesEnabled,
    getCookie(BUNDLE_CHOICE),
    defaultRooms.length,
    defaultRooms[0]?.children
  );

  const isSoftBundlesVisible =
    softBundles.isSoftBundlesVisible && (empOfferCodes ?? []).length === 0;
  const { softBundle } = softBundles;

  const isBarrierFreeLabelEnabled =
    language === CountryCode.DE ? isBarrierFreeLabelEnabledFT : false;

  const [promoState, setPromoState] = useState({
    code: getActivePromotionCode(promotionBannerData as PromotionsInformation, PROMOID as string),
    type: promotionBannerData?.promoKind as PromoKind,
    error: '',
    success: '',
    isApplied: false,
    isOpen: false,
    shouldShowRemoveButton: false,
    isPromoBox: false,
    rateName: '',
    roomClass: '',
  });
  const appliedPromoCode = useRef('');

  useEffect(() => {
    const cleanupReferrer = () => {
      setSearchReferrer(SEARCH_REFERRER_INITIAL_VALUE);
    };
    return () => cleanupReferrer();
  }, [setSearchReferrer]);

  useEffect(() => {
    window.localStorage.setItem('HDPPath', location.pathname + location.search);
    localStorage.removeItem('3cpVisited');
  }, []);

  // NOTE: get all data here that you need to pass to the molecules via props
  const {
    hotelId,
    name: hotelName,
    brand: hotelBrand,
    title: hotelTitle,
    breadcrumb: breadcrumbs,
    tripAdvisorReviews,
    headline,
    faq,
    hotelOpeningDate,
    hotelFacilities,
    isLoading,
    isError,
    error,
    coordinates,
    hotelDescription,
    address,
    links,
    galleryImages,
    cityTax,
    announcement,
    contactDetails,
    countryCodeISO,
    seo,
  } = useStaticHotelInformation();

  const { isCityTaxHotel, effectiveFrom, bookingDateFrom } = cityTax as CityTax;

  const getCityTaxEffectiveness = () => {
    if (effectiveFrom && bookingDateFrom && ARRyyyy && ARRmm && ARRdd) {
      const arrivalDate = startOfDay(new Date(+ARRyyyy, +ARRmm - 1, +ARRdd));
      const bookingDate = startOfDay(new Date(bookingDateFrom as string));
      const effectiveDate = startOfDay(new Date(effectiveFrom as string));

      return (
        (isSameDay(new Date(), effectiveDate) || isAfter(new Date(), effectiveDate)) &&
        (isSameDay(arrivalDate, bookingDate) || isAfter(arrivalDate, bookingDate))
      );
    }
    return true; // default to true if dates are not defined - business logic
  };

  const isCityTaxEnabled = cityTax && (isCityTaxHotel ?? false) && getCityTaxEffectiveness();

  let hotelAvailabilityResponse: HIHotelAvailabilityResponse | any;
  let dataHotelAvailability: HIHotelAvailabilityResponse | any;

  const discountRateAndCorpIdDisabled = !isDiscountedRateEnabled && !corpId;
  const arrivaldate = discountRateAndCorpIdDisabled ? arrival : undefined;
  const discountRateAndCorpIdEnabled = !!isDiscountedRateEnabled && !!corpId;
  const shouldPassPromoStateInfo = !corpId;
  const shouldSkipPromoBoxStateInfo = Boolean(
    corpId || cookieBasedPromo || !isPromotionsInHotelAvailabilityEnabled
  );

  const {
    isLoading: isLoadingHotelAvailability,
    isFetching: isFetchingHotelAvailability,
    isError: isErrorHotelAvailability,
    data: dataHotelAvailabilityPI,
    error: errorHotelAvailability,
  } = useHotelAvailability(
    hotelId,
    hotelBrand,
    arrivaldate,
    departure,
    rooms,
    'PI',
    empOfferCodes,
    '',
    shouldPassPromoStateInfo ? promoState?.code : '',
    shouldPassPromoStateInfo ? promoState?.type : undefined,
    softBundle === BUNDLE_CHOICE_OPTIONS.roomOnly ? BUNDLE_CHOICE_OPTIONS.rate : softBundle,
    shouldPassPromoStateInfo ? promoState?.rateName : undefined,
    shouldPassPromoStateInfo ? promoState?.roomClass : undefined,
    shouldSkipPromoBoxStateInfo ? undefined : promoState?.isPromoBox
  );

  const query = getStaticContent(isBarrierFreeLabelEnabled);
  const { data: dataHeaderInformation }: HeaderInformationQuery = useQueryRequest(
    ['GetStaticContent', language, country],
    query,
    {
      country,
      language,
      site: SITE_LEISURE,
      businessBooker: false,
    }
  );

  const discountRateInfo = useDiscountRateInfoHotelAvailability({
    hotelId,
    hotelBrand,
    arrival: arrival as string,
    departure: departure as string,
    rooms,
    offers: dataHeaderInformation?.headerInformation?.content?.global?.offers,
  });

  if (discountRateAndCorpIdEnabled) {
    dataHotelAvailability = discountRateInfo?.dataHotelAvailability;
    hotelAvailabilityResponse = discountRateInfo?.hotelAvailabilityResponse;
  } else {
    dataHotelAvailability = dataHotelAvailabilityPI;
    hotelAvailabilityResponse = {
      isLoadingHotelAvailability,
      isErrorHotelAvailability,
      dataHotelAvailability,
      errorHotelAvailability,
    };
  }

  const srcHubLogo = dataHeaderInformation?.headerInformation?.content?.global?.brand?.hubHdpLogo;
  const thirdParties =
    dataHeaderInformation?.headerInformation?.content?.global?.thirdParties ?? '';

  const roomCodes = dataHeaderInformation?.headerInformation?.config?.roomCodes || {};
  const {
    double = '',
    accessible = '',
    family = '',
    single = '',
    twin = '',
  } = dataHeaderInformation?.headerInformation?.content?.global || {};
  const globalTranslationForRooms = { double, accessible, family, single, twin };

  const mappedRoomLabels =
    (isSilentFeatureFlagEnabled &&
      swapKeysAndValues(getMappedRooms(roomCodes, globalTranslationForRooms))) ||
    {};

  const isHotelOpeningSoon = useMemo(
    () => isHotelOpeningSoonFunction(hotelOpeningDate ?? '', arrival),
    [hotelOpeningDate, arrival]
  );

  const isHotelAvailable =
    dataHotelAvailability?.hotelAvailability?.available &&
    dataHotelAvailability?.hotelAvailability?.roomRates.length > 0;

  if (isHotelAvailable && typeof window !== 'undefined') {
    window.localStorage.setItem(
      'hotelAvailability',
      JSON.stringify(dataHotelAvailability.hotelAvailability)
    );
  }

  const roomRates =
    hotelAvailabilityResponse?.dataHotelAvailability?.hotelAvailability?.roomRates ?? [];
  const latestPromotionBannerData = extractPromoBoxData(
    isPromotionsInHotelAvailabilityEnabled,
    dataHotelAvailability?.hotelAvailability,
    promotionBannerData
  );

  const promoActions = {
    setPromoState,
    promoState,
    shouldShowRemoveButton: promoState?.isApplied,
    promotionBannerData: latestPromotionBannerData,
    searchQuery: {
      arrival,
      departure,
      country,
      language,
      hotelBrand,
      roomRates,
    },
    appliedPromoCode,
    isFetching: isFetchingHotelAvailability || isLoadingHotelAvailability,
  } as PromoActionsType;

  const isDisplayRates = !isHotelOpeningSoon && isHotelAvailable;

  const isPromoBoxVisible = Boolean(isShowPromoBoxPIEnabled && promotionBannerData?.promoBox);
  // HDP Analytics
  useEffect(() => {
    let hotelAvailability;

    if (!ROOMS) {
      hotelAvailability = UNCHECKED_HOTEL;
    } else if (!isHotelOpeningSoon && isHotelAvailable) {
      hotelAvailability = AVAILABLE_HOTEL;
    } else {
      hotelAvailability = UNAVAILABLE_HOTEL;
    }

    if (hotelName) {
      updateHotelDisplayPageAnalytics({
        multiSearchParams: router.query as QueryParams,
        hotelName,
        hotelId,
        hotelLabel: getHotelLabel(dataHotelAvailability, labelsConstants),
        get hotelAvailability() {
          return hotelAvailability;
        },
        hotelFacilityIcons: hotelFacilities.map((facility) => facility.icon ?? ''),
        isMetaArrivalDayKeywordFlagEnabled,
        dataHotelAvailability: dataHotelAvailabilityPI,
        promoActions,
        isPromoBoxVisible,
      });

      setIsParentAnalytics(true);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [router.query, ROOMS, hotelName, hotelBrand, hotelId, dataHotelAvailability]);

  if (isLoading) {
    return <Text data-testid="HotelDetailsPIPage-Wrapper-loading"> </Text>;
  }

  if (isError) {
    return <Text>{(error as Error).message}</Text>;
  }

  if (!hotelId) {
    return <DefaultErrorPage statusCode={404} />;
  }

  if (discountRateAndCorpIdEnabled && discountRateInfo.isDiscountRateLoading) {
    return <Text data-testid="HotelDetailsPIPage-Wrapper-loading"> </Text>;
  }

  const targetRatePlanCode = getRatePlanCode(
    dataHeaderInformation?.headerInformation?.content?.global?.offers as any,
    corpId as string
  );
  const isRoomRatesLoading =
    promoState?.isPromoBox && (isLoadingHotelAvailability || isFetchingHotelAvailability);

  const hasRateSelectorTitleDescription =
    !!t('hoteldetails.rates.grid.title.roomAndRate', { defaultValue: '' }) &&
    !!t('hoteldetails.rates.grid.title', { defaultValue: '' }) &&
    !!t('hoteldetails.rates.grid.description', { defaultValue: '' });

  return (
    <QueryClientProvider client={queryClient}>
      <Box data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}>
        <Seo
          page={PageName.HDP}
          displayMeta
          {...{
            hotelId,
            tripAdvisorReviews,
            breadcrumbs,
          }}
          hotelAvailability={hotelAvailability}
          roomTypeInformation={roomTypeInformation}
          hotel={{
            coordinates,
            hotelDescription,
            address,
            links,
            hotelId,
            name: hotelName,
            tripAdvisorReviews,
            galleryImages,
            contactDetails,
            hotelFacilities,
            brand: hotelBrand,
            countryCodeISO,
            seo,
          }}
        />
        <SearchContainer
          queryClient={queryClient}
          searchLocation={searchLocation?.toString()}
          defaultLocation={hotelName}
          defaultRooms={defaultRooms}
          ARRdd={Number(ARRdd)}
          ARRmm={Number(ARRmm)}
          ARRyyyy={Number(ARRyyyy)}
          NIGHTS={Number(NIGHTS)}
          ROOMS={Number(ROOMS)}
          CORPID={corpId?.toString()}
          variant="pi"
          PROMOID={PROMOID as string}
        />
        <AnnouncementNotification
          announcement={announcement}
          arrivalDate={arrival}
          departureDate={departure}
        />
        {(roomRates?.length ?? 0) > 0 &&
          !promoState?.isApplied &&
          !(isPromoBoxAppliedCodeCookieEnabled && promotionBoxCodeCookie) && (
            <PromotionsNotification
              promotionBannerData={promotionBannerData}
              viewType="any"
              page={PageName.HDP}
              roomRates={roomRates}
            />
          )}
        <OpeningSoonNotification
          hotelAvailabilityResponse={hotelAvailabilityResponse}
          isHotelOpeningSoon={isHotelOpeningSoon}
          hotelName={hotelName}
          hotelOpeningDate={hotelOpeningDate}
          language={language}
        />
        {ARRyyyy && ARRmm && ARRdd && (
          <SoldOutNotification
            hotelAvailabilityResponse={hotelAvailabilityResponse}
            isHotelOpeningSoon={isHotelOpeningSoon}
          />
        )}
        <HubZipNotice srcHubLogo={srcHubLogo} />
        <HotelBreadcrumb />
        <Flex my="xl" direction={{ base: 'column-reverse', lg: 'initial' }}>
          <Box flex={3.5} data-testid="hdp_hotelInformationTopSection" mr="md">
            <Box mb="sm">
              <HotelTitle name={hotelTitle} />
            </Box>
            <Box mb="sm">
              <HotelLocation />
            </Box>
            <TripAdvisorReview isTopSection={true} />
            <Box my="md">
              <HotelBadges
                hubBadge={
                  dataHeaderInformation?.headerInformation?.content?.global?.brand?.hubBadge ?? ''
                }
              />
              <HotelHeadline headline={headline} />
            </Box>
            {isLessThanSm && (
              <Box borderBottom="1px solid var(--chakra-colors-lightGrey4)" mt="md" />
            )}
            <HotelFacilitiesList
              isLessThanSm={isLessThanSm}
              isLessThanMd={isLessThanMd}
              isSoftBundlesVisible={isSoftBundlesVisible}
            />
          </Box>
          <Box flex={4}>
            <HotelGallery
              thumbnailSectionHeight="18.75rem"
              eagerLoad={true}
              isLessThanLg={isLessThanLg}
            />
          </Box>
        </Flex>
        {!!hasRateSelectorTitleDescription && (
          <Box
            data-testid="hdp_hotelInformationRatesSpacer"
            borderBottom="1px solid var(--chakra-colors-lightGrey4)"
            mb="3xl"
          />
        )}
        {isHotelDetailsOnHDP && (
          <>
            {isRoomRatesLoading && (
              <Center
                position="fixed"
                top={0}
                left={0}
                right={0}
                bottom={0}
                bg="blackAlpha.300"
                zIndex={1000}
              >
                <LoadingSpinner />
              </Center>
            )}
            <RateSelector
              variant="PI"
              channel={Channel.Pi}
              {...{
                queryClient,
                hotelAvailabilityResponse,
                isParentAnalytics,
                arrival,
                departure,
                numberOfNights,
                numberOfUnits,
                isHotelOpeningSoon,
                isLessThanSm,
                isLessThanMd,
                isLessThanLg,
                thirdParties,
                mappedRoomLabels,
                isSilentFeatureFlagEnabled,
                targetRatePlanCode: targetRatePlanCode ?? '',
                globalTranslationForRooms,
                isCityTaxEnabled,
                isCityTaxBreakdownEnabled,
                hasRateSelectorTitleDescription,
              }}
              promoActions={promoActions}
              isSoftBundlesVisible={isSoftBundlesVisible}
            />
          </>
        )}
        <Location />
        <HotelParkingInformation />
        {!(isRoomDetailsDrawerEnabled && isHotelDetailsOnHDP) && (
          <HotelRooms
            isPremierInn={true}
            isLessThanSm={isLessThanSm}
            isLessThanMd={isLessThanMd}
            isLessThanLg={isLessThanLg}
            isDisplayRates={isDisplayRates}
          />
        )}
        <HotelRestaurant />
        <span id="tripadvisor-reivew-section-bottom"></span>
        <TripAdvisorReview isTopSection={false} />
        <Flex {...hdpDescriptionFaq}>
          <Box flex={'1'} borderBottom={'none'}>
            <HotelDescriptionInformation />
          </Box>
          {isFaqValidForRender(faq, true) && (
            <Box flex={'1'} data-testid="hdp-faq-pi">
              <HotelFaq />
            </Box>
          )}
        </Flex>
        <HotelContactInformation />
      </Box>
    </QueryClientProvider>
  );

  function getMultiSearchParams(isNoRoomTypeSearchEnabled = false) {
    if (!router?.query) return {};
    const { NIGHTS, ROOMS, CELLCODES, CORPID, PROMOID } = router.query;
    const cellCodes = CELLCODES as string;
    const empOfferCodes = (cellCodes?.split(',') as string[] | []) || [];

    // Set arrival and departure as undefined if the page is accessed from an external source without params
    // In this case both server-side request and page request will not be consumed
    if (
      !ARRdd &&
      !ARRmm &&
      !ARRyyyy &&
      !isDateValid(Number(ARRdd), Number(ARRmm), Number(ARRyyyy))
    ) {
      return {
        arrival: undefined,
        departure: undefined,
        rooms: [],
      };
    }

    //Set default search parameters if one the required params is missing
    if (
      !ARRdd ||
      !ARRmm ||
      !ARRyyyy ||
      !NIGHTS ||
      !ROOMS ||
      !isDateValid(Number(ARRdd), Number(ARRmm), Number(ARRyyyy))
    ) {
      return {
        arrival: format(endOfDay(new Date()), 'yyyy-MM-dd'),
        departure: format(endOfDay(add(new Date(), { days: Number(NIGHTS) || 1 })), 'yyyy-MM-dd'),
        numberOfNights: Number(NIGHTS) || 1,
        rooms: [
          {
            adultsNumber: 1,
            childrenNumber: 0,
            roomType: 'DB',
            cotRequired: false,
          },
        ],
        corpId: CORPID,
        promoId: PROMOID,
      };
    }

    const arrival = new Date(+ARRyyyy, +ARRmm - 1, +ARRdd);
    const departure = add(arrival, { days: +NIGHTS });
    const roomsCount = Math.min(+ROOMS || 0, MAX_ROOMS_SEARCH_LIMIT);
    const rooms = [];
    for (let idx = 0; idx < roomsCount; idx++) {
      rooms.push({
        adultsNumber: +router.query[`ADULT${idx + 1}`]!,
        childrenNumber: +router.query[`CHILD${idx + 1}`]!,
        roomType: isNoRoomTypeSearchEnabled ? '' : router.query[`INTTYP${idx + 1}`],
        cotRequired: router.query[`COT${idx + 1}`] === '1',
      });
    }

    return {
      arrival: format(arrival, 'yyyy-MM-dd'),
      departure: format(departure, 'yyyy-MM-dd'),
      numberOfUnits: roomsCount,
      numberOfNights: +NIGHTS,
      rooms,
      empOfferCodes,
      corpId: CORPID,
      promoId: PROMOID,
    };
  }
}

const hdpDescriptionFaq = {
  borderBottom: '1px solid var(--chakra-colors-lightGrey1)',
  marginTop: '2px',
  marginBottom: '2px',
  gap: '20px',
  direction: { base: 'column', lg: 'initial' },
} as FlexProps;
