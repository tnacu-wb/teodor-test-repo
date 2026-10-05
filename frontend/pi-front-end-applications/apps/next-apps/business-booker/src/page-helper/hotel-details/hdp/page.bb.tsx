import { Box, Flex, Text, FlexProps, Center } from '@chakra-ui/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import {
  BOOKING_CHANNEL,
  HeaderInformationQuery,
  HIHotelAvailabilityResponse,
  HIVisualDisplayContext,
  PageName,
  SearchRoomType,
  SITE_BB,
  FS_SILENT_SUBSTITUTION,
  Channel,
  FT_PI_BB_CCUI_BARRIER_FREE_LABEL,
  getStaticContent,
  CountryCode,
  PromoKind,
  FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN,
  FT_BB_SHOW_PROMOTION_BOX,
  FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE,
  FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY,
} from '@whitbread-eos/api';
import { CityTax } from '@whitbread-eos/api/src/types/graphql';
import { PromotionsNotification, LoadingSpinner } from '@whitbread-eos/atoms';
import {
  AnnouncementNotification,
  HotelBadges,
  HotelContactInformation,
  HotelDescriptionInformation,
  HotelFacilitiesList,
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
  HotelFaq,
  TripAdvisorReview,
} from '@whitbread-eos/molecules';
import {
  BBSearchContainer as SearchContainer,
  Location,
  RateSelector,
} from '@whitbread-eos/organisms';
import {
  formatDataTestId,
  getAuthCookie,
  isDateValid,
  getMappedRooms,
  isHotelOpeningSoon as isHotelOpeningSoonFunction,
  swapKeysAndValues,
  updateHotelDisplayPageAnalytics,
  useCustomLocale,
  useHotelAvailabilityBB,
  useHotelRatesInformationBB,
  useLocalStorage,
  useQueryRequest,
  useStaticHotelInformation,
  useFeatureSwitch,
  isFaqValidForRender,
  useFeatureToggle,
  getActivePromotionCode,
  type PromotionsInformation,
  type PromoActionsType,
  getCookie,
  extractPromoBoxData,
  MAX_ROOMS_SEARCH_LIMIT,
} from '@whitbread-eos/utils';
import { add, endOfDay, format, isAfter, isSameDay, startOfDay } from 'date-fns';
import { useTranslation } from 'next-i18next';
import DefaultErrorPage from 'next/error';
import { NextRouter } from 'next/router';
import { useEffect, useMemo, useState, useRef } from 'react';

interface QueryParams {
  [key: string]: string;
}

const labelsConstants = {
  AVAILABLE_HOTEL: 'available',
  UNAVAILABLE_HOTEL: 'unavailable',
};

interface Props {
  queryClient: QueryClient;
  router: NextRouter;
  visualDisplayContext: HIVisualDisplayContext;
  showSearch?: boolean;
  promotionBannerData?: PromotionsInformation;
}

export default function HotelDetailsPageBB({
  queryClient,
  router,
  visualDisplayContext,
  showSearch = true,
  promotionBannerData,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const [isParentAnalytics, setIsParentAnalytics] = useState(false);
  const { isLessThanSm, isLessThanMd, isLessThanLg } = visualDisplayContext;
  const { searchLocation, ARRdd, ARRmm, ARRyyyy, NIGHTS, ROOMS, PROMOID } = router.query;
  const { AVAILABLE_HOTEL, UNAVAILABLE_HOTEL } = labelsConstants;
  const [, setSearchReferrer] = useLocalStorage('SearchReferrer', SEARCH_REFERRER_INITIAL_VALUE);
  const baseDataTestId = 'HotelDetailsBBPage';
  const { language, country } = useCustomLocale();
  const isSilentFeatureFlagEnabled = useFeatureSwitch({
    featureSwitchKey: FS_SILENT_SUBSTITUTION,
  });

  const {
    [FT_PI_BB_CCUI_BARRIER_FREE_LABEL]: isBarrierFreeLabelEnabledFT,
    [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN]: isCityTaxBreakdownEnabled,
    [FT_BB_SHOW_PROMOTION_BOX]: isShowPromoBoxBBEnabled,
    [FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE]: isPromoBoxAppliedCodeCookieEnabled,
    [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: isPromotionsInHotelAvailabilityEnabled,
  } = useFeatureToggle();
  const isBarrierFreeLabelEnabled =
    language === CountryCode.DE ? isBarrierFreeLabelEnabledFT : false;
  const promotionBoxCodeCookie = getCookie('appliedPromoBoxCode');
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

  const defaultRooms: SearchRoomType[] = [];
  for (let idx = 0; idx < Number(ROOMS) && idx < MAX_ROOMS_SEARCH_LIMIT; idx++) {
    const room: SearchRoomType = {
      adults: Number(router.query[`ADULT${idx + 1}`]),
      children: Number(router.query[`CHILD${idx + 1}`]),
      shouldIncludeCot: !!Number(router.query[`COT${idx + 1}`]),
      roomType: router.query[`INTTYP${idx + 1}`] as string,
    };
    defaultRooms.push(room);
  }

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
    headline,
    faq,
    hotelOpeningDate,
    hotelFacilities,
    cityTax,
    announcement,
    isLoading,
    isError,
    error,
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

  const isCityTaxEnabled = cityTax && isCityTaxHotel && getCityTaxEffectiveness();

  const { arrival, departure, numberOfUnits, rooms, numberOfNights, cellCodes, corpId } = useMemo(
    getMultiSearchParams,
    [router?.query]
  );

  const idTokenCookie = getAuthCookie();
  const shouldPassPromoStateInfo = !corpId;
  const shouldSkipPromoBoxStateInfo = Boolean(corpId || !isPromotionsInHotelAvailabilityEnabled);

  const {
    isLoading: isLoadingHotelAvailabilities,
    isFetching: isFetchingHotelAvailability,
    isError: isErrorHotelAvailabilities,
    data: dataHotelAvailability,
    error: errorHotelAvailabilities,
  } = useHotelAvailabilityBB(
    hotelId,
    hotelBrand,
    arrival,
    departure,
    rooms,
    BOOKING_CHANNEL.BB,
    idTokenCookie,
    shouldPassPromoStateInfo ? promoState?.code : '',
    shouldPassPromoStateInfo ? promoState?.type : undefined,
    shouldPassPromoStateInfo ? promoState?.rateName : undefined,
    shouldPassPromoStateInfo ? promoState?.roomClass : undefined,
    shouldSkipPromoBoxStateInfo ? undefined : promoState?.isPromoBox
  );

  const {
    isLoading: isLoadingHotelInformations,
    isError: isErrorHotelInformations,
    data: dataHotelInformations,
    error: errorHotelInformations,
  } = useHotelRatesInformationBB(
    hotelId,
    hotelBrand,
    dataHotelAvailability,
    BOOKING_CHANNEL.BB,
    idTokenCookie
  );

  const hotelAvailabilityResponse: HIHotelAvailabilityResponse = {
    isLoadingHotelAvailability: isLoadingHotelInformations || isLoadingHotelAvailabilities,
    isErrorHotelAvailability: isErrorHotelInformations || isErrorHotelAvailabilities,
    dataHotelAvailability: dataHotelInformations &&
      dataHotelAvailability && { ...dataHotelInformations, ...(dataHotelAvailability as any) },
    errorHotelAvailability: errorHotelInformations || errorHotelAvailabilities,
  };

  const roomRates = hotelAvailabilityResponse?.dataHotelAvailability?.hotelAvailability?.roomRates;
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
    isFetching: isFetchingHotelAvailability || isLoadingHotelAvailabilities,
  } as PromoActionsType;

  const query = getStaticContent(isBarrierFreeLabelEnabled);
  const { data: dataHeaderInformation }: HeaderInformationQuery = useQueryRequest(
    ['GetStaticContent', language, country],
    query,
    {
      country,
      language,
      site: SITE_BB,
      businessBooker: true,
    }
  );
  const srcHubLogo = dataHeaderInformation?.headerInformation?.content?.global?.brand?.hubHdpLogo;

  const roomCodes = dataHeaderInformation?.headerInformation?.config?.roomCodes || {};
  const {
    double = '',
    accessible = '',
    family = '',
    single = '',
    twin = '',
  } = dataHeaderInformation?.headerInformation?.content?.global || {};
  const globalTranslationForRooms = { double, accessible, family, single, twin };

  const mappedRoomLabels = isSilentFeatureFlagEnabled
    ? swapKeysAndValues(getMappedRooms(roomCodes, globalTranslationForRooms)) || {}
    : {};

  const isHotelOpeningSoon = useMemo(
    () => isHotelOpeningSoonFunction(hotelOpeningDate ?? '', arrival),
    [hotelOpeningDate, arrival]
  );

  const isPromoBoxVisible = Boolean(isShowPromoBoxBBEnabled && promotionBannerData?.promoBox);

  // HDP Analytics
  useEffect(() => {
    if (hotelName) {
      updateHotelDisplayPageAnalytics({
        multiSearchParams: router.query as QueryParams,
        hotelName,
        hotelId,
        hotelLabel: [hotelBrand],
        hotelAvailability: dataHotelAvailability?.hotelAvailability?.available
          ? AVAILABLE_HOTEL
          : UNAVAILABLE_HOTEL,
        hotelFacilityIcons: hotelFacilities.map((facility) => facility.icon ?? ''),
        dataHotelAvailability: dataHotelInformations &&
          dataHotelAvailability && { ...dataHotelInformations, ...(dataHotelAvailability as any) },
        promoActions,
        isPromoBoxVisible,
      });

      setIsParentAnalytics(true);
    }
  }, [
    router.query,
    ROOMS,
    hotelName,
    hotelBrand,
    hotelId,
    dataHotelAvailability,
    dataHotelInformations,
  ]);

  if (isLoading) {
    return <Text>{t('searchresults.list.hotel.loading')}</Text>;
  }

  if (isError) {
    return <Text>{(error as Error).message}</Text>;
  }

  if (!hotelId) {
    return <DefaultErrorPage statusCode={404} />;
  }
  const isRoomRatesLoading =
    promoState?.isPromoBox && (isLoadingHotelAvailabilities || isFetchingHotelAvailability);

  const hasRateSelectorTitleDescription =
    !!t('hoteldetails.rates.grid.title.roomAndRate', { defaultValue: '' }) &&
    !!t('hoteldetails.rates.grid.title', { defaultValue: '' }) &&
    !!t('hoteldetails.rates.grid.description', { defaultValue: '' });

  return (
    <QueryClientProvider client={queryClient}>
      <Box data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}>
        <Seo page={PageName.HDP} hotelId={hotelId} />
        {showSearch && (
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
            variant="bb"
            PROMOID={PROMOID as string}
          />
        )}
        <AnnouncementNotification
          announcement={announcement}
          channel={BOOKING_CHANNEL.BB}
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
              roomRates={roomRates ?? []}
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
            <HotelFacilitiesList isLessThanSm={isLessThanSm} />
          </Box>
          <Box flex={4}>
            <HotelGallery thumbnailSectionHeight="18.75rem" isLessThanLg={isLessThanLg} />
          </Box>
        </Flex>
        {!!hasRateSelectorTitleDescription && (
          <Box
            data-testid="hdp_hotelInformationRatesSpacer"
            borderBottom="1px solid var(--chakra-colors-lightGrey4)"
            mb="3xl"
          />
        )}
        {!!numberOfNights && !!numberOfUnits && (
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
              channel={Channel.Bb}
              variant="BB"
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
                cellCodes,
                mappedRoomLabels,
                isSilentFeatureFlagEnabled,
                globalTranslationForRooms,
                isCityTaxEnabled,
                isCityTaxBreakdownEnabled,
                hasRateSelectorTitleDescription,
              }}
              promoActions={promoActions}
            />
          </>
        )}
        <Location />
        <HotelParkingInformation />
        <HotelRooms
          isPremierInn={true}
          isLessThanSm={isLessThanSm}
          isLessThanMd={isLessThanMd}
          isLessThanLg={isLessThanLg}
        />
        <HotelRestaurant />
        <TripAdvisorReview isTopSection={false} />
        <Flex {...hdpDescriptionFaq}>
          <Box flex={'1'} borderBottom={'none'}>
            <HotelDescriptionInformation />
          </Box>
          {isFaqValidForRender(faq, true) && (
            <Box flex={'1'} data-testid="hdp-faq-bb">
              <HotelFaq />
            </Box>
          )}
        </Flex>
        <HotelContactInformation />
      </Box>
    </QueryClientProvider>
  );

  function getMultiSearchParams() {
    if (!router?.query) return {};
    const { ARRdd, ARRmm, ARRyyyy, NIGHTS, ROOMS, CELLCODES = '', CORPID = '' } = router.query;
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
    )
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
      };

    const arrival = new Date(+ARRyyyy, +ARRmm - 1, +ARRdd);
    const departure = add(arrival, { days: +NIGHTS });
    const roomsCount = Math.min(+ROOMS || 0, MAX_ROOMS_SEARCH_LIMIT);
    const rooms = [];
    for (let idx = 0; idx < roomsCount; idx++) {
      rooms.push({
        adultsNumber: +router.query[`ADULT${idx + 1}`]!,
        childrenNumber: +router.query[`CHILD${idx + 1}`]!,
        roomType: router.query[`INTTYP${idx + 1}`],
        cotRequired: router.query[`COT${idx + 1}`] === '1',
      });
    }
    return {
      arrival: format(arrival, 'yyyy-MM-dd'),
      departure: format(departure, 'yyyy-MM-dd'),
      numberOfUnits: roomsCount,
      numberOfNights: +NIGHTS,
      rooms,
      cellCodes: String(CELLCODES),
      corpId: String(CORPID),
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
