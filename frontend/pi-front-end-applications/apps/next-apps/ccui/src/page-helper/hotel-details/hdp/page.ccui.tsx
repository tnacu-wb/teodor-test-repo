import { Box, Flex, Text, FlexProps, Center } from '@chakra-ui/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import {
  BOOKING_CHANNEL,
  HeaderInformationQuery,
  HIHotelAvailabilityResponse,
  HIVisualDisplayContext,
  SEARCH_COMPANY_BY_ID,
  SearchRoomType,
  SITE_LEISURE,
  FS_SILENT_SUBSTITUTION,
  Channel,
  FT_CCUI_PRE_POPULATE_BILLING_ADDRESS,
  FT_PI_BB_CCUI_BARRIER_FREE_LABEL,
  getStaticContent,
  CountryCode,
  PageName,
  PromoKind,
  FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN,
  Claims,
  FT_CCUI_SHOW_PROMOTION_BOX,
  FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE,
  FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY,
} from '@whitbread-eos/api';
import { CityTax } from '@whitbread-eos/api/dist/types/graphql';
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
  SoldOutNotification,
  HotelFaq,
  HotelBreadcrumb,
  TripAdvisorReview,
  SRHotelNotification,
} from '@whitbread-eos/molecules';
import { CCUISearchContainer as SearchContainer, Location } from '@whitbread-eos/organisms';
import {
  formatDataTestId,
  getMappedRooms,
  isHotelOpeningSoon as isHotelOpeningSoonFunction,
  swapKeysAndValues,
  updateHotelDisplayPageAnalytics,
  useCustomLocale,
  useHotelAvailabilityCCUI,
  useHotelRatesInformationCCUI,
  useLocalStorage,
  useQueryRequest,
  useStaticHotelInformation,
  useFeatureSwitch,
  isDateValid,
  isFaqValidForRender,
  useFeatureToggle,
  PromotionsInformation,
  getActivePromotionCode,
  type PromoActionsType,
  getCookie,
  extractPromoBoxData,
  MAX_ROOMS_SEARCH_LIMIT,
} from '@whitbread-eos/utils';
import { add, endOfDay, format, isAfter, isSameDay, startOfDay } from 'date-fns';
import { useTranslation } from 'next-i18next';
import dynamic from 'next/dynamic';
import DefaultErrorPage from 'next/error';
import { NextRouter } from 'next/router';
import { useEffect, useMemo, useState, useRef } from 'react';

const labelsConstants = {
  AVAILABLE_HOTEL: 'available',
  UNAVAILABLE_HOTEL: 'unavailable',
};

interface QueryParams {
  [key: string]: string;
}

interface Props {
  queryClient: QueryClient;
  router: NextRouter;
  user?: Claims;
  setAnalyticsUser?: any;
  visualDisplayContext: HIVisualDisplayContext;
  promotionBannerData?: PromotionsInformation;
}

const RateSelector = dynamic(
  async () => {
    const { RateSelector } = await import('@whitbread-eos/organisms');
    return { default: RateSelector };
  },
  {
    ssr: false,
  }
);

export default function HotelDetailsPageCCUI({
  queryClient,
  router,
  user,
  setAnalyticsUser,
  visualDisplayContext,
  promotionBannerData,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const searchLocation = router.query.searchLocation;
  //setting default values in case there are no query params in the URL
  const today = new Date();
  const ARRdd = router.query.ARRdd || today.getDate();
  const ARRmm = router.query.ARRmm || today.getMonth() + 1;
  const ARRyyyy = router.query.ARRyyyy || today.getFullYear();
  const NIGHTS = router.query.NIGHTS || 1;
  const ROOMS = router.query.ROOMS;

  const [isParentAnalytics, setIsParentAnalytics] = useState(false);
  const { isLessThanSm, isLessThanMd, isLessThanLg } = visualDisplayContext;
  const { AVAILABLE_HOTEL, UNAVAILABLE_HOTEL } = labelsConstants;
  const [, setSearchReferrer] = useLocalStorage('SearchReferrer', SEARCH_REFERRER_INITIAL_VALUE);
  const baseDataTestId = 'HotelDetailsCCUIPage';
  const { reservationId, PROMOID } = router.query;
  const prevReservationId = reservationId ?? '';
  const isSilentFeatureFlagEnabled = useFeatureSwitch({
    featureSwitchKey: FS_SILENT_SUBSTITUTION,
  });

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

  const { language, country } = useCustomLocale();

  const {
    [FT_CCUI_PRE_POPULATE_BILLING_ADDRESS]: isPrePopulateBillingAddressEnabled,
    [FT_PI_BB_CCUI_BARRIER_FREE_LABEL]: isBarrierFreeLabelEnabledFT,
    [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN]: isCityTaxBreakdownEnabled,
    [FT_CCUI_SHOW_PROMOTION_BOX]: isShowPromoBoxCCUIEnabled,
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

  useEffect(() => {
    setAnalyticsUser(user, language);
  }, [user, language, setAnalyticsUser]);

  // NOTE: get all data here that you need to pass to the molecules via props
  const {
    hotelId,
    name: hotelName,
    brand: hotelBrand,
    faq,
    // eslint-disable-next-line react-hooks/exhaustive-deps
    title: hotelTitle,
    headline,
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

  const isCityTaxEnabled = cityTax && (isCityTaxHotel ?? false) && getCityTaxEffectiveness();

  const { arrival, departure, numberOfUnits, rooms, numberOfNights, corpId, compId } = useMemo(
    getMultiSearchParams,
    [router?.query]
  );

  const { isLoading: isLoadingCompanyData, data: companyData } = useQueryRequest(
    ['searchCompanyById', corpId],
    SEARCH_COMPANY_BY_ID,
    {
      corpId,
    },
    { enabled: !!corpId, retry: false },
    undefined,
    true
  );

  const queryEnabled = arrival !== undefined && departure !== undefined;
  const shouldPassPromoStateInfo = !corpId;
  const shouldSkipPromoBoxStateInfo = Boolean(corpId || !isPromotionsInHotelAvailabilityEnabled);

  const {
    isLoading: isLoadingHotelAvailabilities,
    isFetching: isFetchingHotelAvailability,
    isError: isErrorHotelAvailabilities,
    data: dataHotelAvailability,
    error: errorHotelAvailabilities,
  } = useHotelAvailabilityCCUI(
    hotelId,
    hotelBrand,
    arrival,
    departure,
    rooms,
    BOOKING_CHANNEL.CCUI,
    !isLoadingCompanyData && queryEnabled,
    companyData?.companyProfile?.companyId,
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
  } = useHotelRatesInformationCCUI(
    hotelId,
    hotelBrand,
    dataHotelAvailability,
    BOOKING_CHANNEL.CCUI,
    !isLoadingHotelAvailabilities
  );

  const hotelAvailabilityResponse: HIHotelAvailabilityResponse = {
    isLoadingHotelAvailability: isLoadingHotelInformations || isLoadingHotelAvailabilities,
    isErrorHotelAvailability: isErrorHotelInformations || isErrorHotelAvailabilities,
    dataHotelAvailability: dataHotelInformations &&
      dataHotelAvailability && { ...dataHotelInformations, ...(dataHotelAvailability as any) },
    errorHotelAvailability: errorHotelInformations || errorHotelAvailabilities,
  };

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
    isFetching: isLoadingHotelInformations || isFetchingHotelAvailability,
  } as PromoActionsType;

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

  const displayNotification = () => {
    if (dataHotelAvailability?.hotelAvailability?.mlos) {
      return (
        <Box mt="20px">
          <SRHotelNotification description={t('search.mlos.ccui.notification')} wide={true} />
        </Box>
      );
    } else if (router.query.ARRyyyy && router.query.ARRmm && router.query.ARRdd) {
      return (
        <SoldOutNotification
          hotelAvailabilityResponse={hotelAvailabilityResponse}
          isHotelOpeningSoon={isHotelOpeningSoon}
        />
      );
    }
  };
  const isPromoBoxVisible = Boolean(isShowPromoBoxCCUIEnabled && promotionBannerData?.promoBox);

  // HDP Analytics
  useEffect(() => {
    if (hotelName) {
      updateHotelDisplayPageAnalytics({
        multiSearchParams: router.query as QueryParams,
        hotelName,
        hotelId,
        hotelLabel: [hotelBrand],
        hotelAvailability: (dataHotelAvailability as any)?.hotelAvailability?.available
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
    // eslint-disable-next-line react-hooks/exhaustive-deps
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
        <SearchContainer
          queryClient={queryClient}
          searchLocation={searchLocation?.toString()}
          defaultLocation={hotelName}
          defaultRooms={defaultRooms}
          ARRdd={Number(ARRdd)}
          ARRmm={Number(ARRmm)}
          ARRyyyy={Number(ARRyyyy)}
          ROOMS={Number(ROOMS)}
          NIGHTS={Number(NIGHTS)}
          prevReservationId={prevReservationId as string}
          CORPID={corpId}
          COMPID={compId}
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
        {displayNotification()}
        <HubZipNotice srcHubLogo={srcHubLogo} />
        <HotelBreadcrumb openInNewTab />
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
              variant="CCUI"
              channel={Channel.Ccui}
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
                prevReservationId: prevReservationId as string,
                mappedRoomLabels,
                isSilentFeatureFlagEnabled,
                companyData,
                isPrePopulateBillingAddressEnabled,
                globalTranslationForRooms,
                isCityTaxEnabled,
                isCityTaxBreakdownEnabled,
                hasRateSelectorTitleDescription,
              }}
              promoActions={promoActions}
            />
          </>
        )}
        <Location channel={Channel.Ccui} />
        <HotelParkingInformation />
        <HotelRooms
          isPremierInn={false}
          isLessThanSm={isLessThanSm}
          isLessThanMd={isLessThanMd}
          isLessThanLg={isLessThanLg}
          arrival={arrival}
          departure={departure}
        />
        <HotelRestaurant />
        <TripAdvisorReview isTopSection={false} />
        <Flex {...hdpDescriptionFaq}>
          <Box flex={'1'} borderBottom={'none'}>
            <HotelDescriptionInformation />
          </Box>
          {isFaqValidForRender(faq, true) && (
            <Box flex={'1'} data-testid="hdp-faq-ccui">
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
    const { ARRdd, ARRmm, ARRyyyy, NIGHTS, ROOMS, CORPID, COMPID } = router.query;

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
    if (!ARRdd || !ARRmm || !ARRyyyy || !NIGHTS || !ROOMS)
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
    const corpId = CORPID && (CORPID as string);

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
      corpId,
      compId: COMPID && (COMPID as string),
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
