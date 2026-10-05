import { Box, Flex, FlexProps, Text } from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import {
  SEARCH_INFORMATION_RESULTS,
  SITE_LEISURE,
  SRPartialTranslationsType,
  HeaderInformationData,
  SR_FORMAT,
  getStaticContent,
  FT_PI_BB_CCUI_BARRIER_FREE_LABEL,
  FT_CCUI_PRICE_PER_NIGHT,
  CountryCode,
  FT_SRP_DYNAMIC_FILTERS,
  FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY,
} from '@whitbread-eos/api';
import type {
  SRFiltersType,
  SingleHotelAvailability,
  SRMultiSearchParamsType,
  FilterByLabels,
} from '@whitbread-eos/api';
import {
  Alert,
  Info,
  Notification,
  LoadingSpinner,
  DescriptionBox,
  PromotionsNotification,
} from '@whitbread-eos/atoms';
import { SRControls, VIEW_TYPE_CONSTANTS } from '@whitbread-eos/molecules';
import {
  getPlace,
  useCustomLocale,
  useQueryRequest,
  useScreenSize,
  updateSearchResultsAnalytics,
  isDateValid,
  getPromoId,
  useFeatureToggle,
  PromotionsInformation,
  getNewSearchResultsCCUI,
  setPromoInfoState,
} from '@whitbread-eos/utils';
import { add, differenceInDays, format } from 'date-fns';
import { useTranslation } from 'next-i18next';
import { NextRouter, useRouter } from 'next/router';
import { useCallback, useEffect, useMemo, useState } from 'react';

import { ListView, MapViewCCUIVariant as SRMapView } from '../../index';
import { isHotelAvailable, mapMultiSearchToQueryParams } from '../../search/utilities';
import { APP_VARIANT, INVALID_LOCATION_ERROR_MESSAGE } from '../constants';
import {
  firstError,
  getOpeningSoonHotels,
  getCurrentResultsNumber,
  getSearchRedirectURL,
  getNewUrlSortValue,
} from '../utilities';

interface Props {
  multiSearchParams: SRMultiSearchParamsType;
  onNoHotelsWarning?: (value: boolean) => void;
  queryClient: QueryClient;
  isSearchError: boolean;
  promotionBannerData?: PromotionsInformation;
}

const MAX_NR_OF_MAP_ITEMS = 40;
const MAPPED_ROOM_TYPES = {
  DB: 'DB',
  SB: 'SB',
  FAM: 'FAM',
  TWIN: 'TWIN',
  DIS: 'DIS',
};
const { listView, mapView } = VIEW_TYPE_CONSTANTS;
export const viewTypeLabelsConstants = {
  LIST_VIEW: 'list view',
  MAP_VIEW: 'map view',
};

export default function SearchResultsCCUIVariantContainer({
  multiSearchParams,
  queryClient,
  isSearchError,
  promotionBannerData: promotionBannerDataProp,
}: Readonly<Props>) {
  const { language, country } = useCustomLocale();
  const { t } = useTranslation(['common']);
  const router = useRouter();
  const filters = (router.query?.FILTERS ?? '') as string;
  const URLSortValue = router.query?.SORT;
  const viewType = router.query?.VIEW;
  const promoId = getPromoId(router.query?.PROMOID);
  const baseDataTestId = 'SRP';
  const roomTypes = multiSearchParams.rooms.map((room) => room.type);
  const { isLessThanSm } = useScreenSize();
  const {
    [FT_PI_BB_CCUI_BARRIER_FREE_LABEL]: isBarrierFreeLabelEnabledFT,
    [FT_CCUI_PRICE_PER_NIGHT]: isPricePerNightEnabledOnCcui,
    [FT_SRP_DYNAMIC_FILTERS]: isSrpDynamicFiltersEnabled,
    [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: isPromotionsInHotelAvailabilityEnabled,
  } = useFeatureToggle();

  const [promotionBannerData, setPromotionBannerData] = useState<PromotionsInformation>(
    promotionBannerDataProp as PromotionsInformation
  );

  const [currentPage, setCurrentPage] = useState(1);
  const [availabilityResult, setAvailabilityResult] = useState<any>(null);

  const [items, setItems] = useState<SingleHotelAvailability[]>([]);
  const [resultsMeta, setResultsMeta] = useState({
    total: 0,
    currentResults: 0,
    SRisLoading: true,
    SRisError: false,
    SRError: null,
    SRisSuccess: false,
  });

  const [hotelsWithAvailableRooms, setHotelsWithAvailableRooms] = useState<
    SingleHotelAvailability[]
  >([]);
  const [openingSoonHotels, setOpeningSoonHotels] = useState<SingleHotelAvailability[]>([]);
  const [soldOutHotels, setSoldOutHotels] = useState<SingleHotelAvailability[]>([]);
  const [mlosHotels, setMlosHotels] = useState<SingleHotelAvailability[]>([]);

  const isValidDate = isDateValid(
    Number(multiSearchParams.arrivalDay),
    Number(multiSearchParams.arrivalMonth),
    Number(multiSearchParams.arrivalYear)
  );

  const startDate = isValidDate
    ? new Date(
        Number(multiSearchParams.arrivalYear),
        Number(multiSearchParams.arrivalMonth && multiSearchParams.arrivalMonth - 1),
        Number(multiSearchParams.arrivalDay)
      )
    : new Date();

  const endDate = add(startDate, { days: Number(multiSearchParams.numberOfNights) });
  const isBarrierFreeLabelEnabled =
    language === CountryCode.DE ? isBarrierFreeLabelEnabledFT : false;

  const changeViewType = useCallback(() => {
    if (viewType === mapView || viewType === undefined) {
      router.push({ query: { ...router.query, VIEW: listView, FILTERS: filters } });
      return;
    }
    router.push({
      query: { ...router.query, VIEW: mapView },
    });
  }, [viewType, filters, router]);

  const changeSortValue = useCallback(
    (value: any) => {
      const searchRedirectURL = getSearchRedirectURL(
        router,
        ['SORT', 'FILTERS'],
        country,
        language
      );

      const legacySortMap = { DISTANCE: '1', PRICE: '2' };
      const newSortValue =
        legacySortMap[value?.id as keyof typeof legacySortMap] ??
        getNewUrlSortValue(value, URLSortValue);

      router.push(`${searchRedirectURL}&SORT=${newSortValue}&FILTERS=${filters}`, undefined, {
        shallow: true,
      });
    },
    [router, URLSortValue, filters, country, language]
  );

  const query = getStaticContent(isBarrierFreeLabelEnabled);

  const {
    data: partialTranslations,
    isError: partialTranslationsIsError,
    error: partialTranslationsError,
    isLoading: partialTranslationsisLoading,
  } = useQueryRequest(['searchInformation', country, language], SEARCH_INFORMATION_RESULTS, {
    country,
    language,
  });

  const {
    data: headerInformationData,
    isError: headerInformationIsError,
    error: headerInformationError,
    isLoading: headerInformationIsLoading,
  } = useQueryRequest(['GetStaticContent', language, country], query, {
    country,
    language,
    site: SITE_LEISURE,
    businessBooker: false,
  });

  useEffect(() => {
    if (!partialTranslationsisLoading && !partialTranslationsIsError) {
      setCurrentPage(() => 1);
      setResultsMeta((resultsMeta) => ({
        ...resultsMeta,
        SRisLoading: true,
        SRisError: false,
        SRError: null,
        SRisSuccess: false,
      }));

      getNewSearchResultsCCUI(
        queryClient,
        { ...paramsForQuery, page: 1 },
        promoId,
        isPromotionsInHotelAvailabilityEnabled
      )
        .then(({ results, total, promotionsInformation }) => {
          setPromoInfoState(
            promotionsInformation as PromotionsInformation,
            setPromotionBannerData,
            isPromotionsInHotelAvailabilityEnabled
          );
          setResultsMeta((resultsMeta) => ({
            ...resultsMeta,
            total,
            currentResults: results.length,
            SRisLoading: false,
            SRError: null,
            SRisError: false,
            SRisSuccess: true,
          }));

          setHotelsInOrder(results, { clearPreviousData: true });
          setItems(() => [...results]);
          setCurrentPage(() => 2);
        })
        .catch((error) => {
          setResultsMeta((resultsMeta) => ({
            ...resultsMeta,
            SRisError: true,
            SRError: error,
            SRisSuccess: false,
          }));
        });
    }
  }, [multiSearchParams]);

  useEffect(() => {
    if (!multiSearchParams.code) {
      const stayDetailsState = window.localStorage.getItem('StayDetailsState');
      const parsedStay = stayDetailsState && JSON.parse(stayDetailsState);
      multiSearchParams.code = parsedStay?.data?.info?.suggestion?.hotelId;
    }

    isHotelAvailable(
      mapMultiSearchToQueryParams(multiSearchParams),
      country,
      language,
      MAPPED_ROOM_TYPES,
      APP_VARIANT.CCUI
    ).then(setAvailabilityResult);
  }, [multiSearchParams]);

  const fetchNewHotels = () => {
    if (resultsMeta.SRisLoading) return;
    setResultsMeta((resultsMeta) => ({
      ...resultsMeta,
      SRisLoading: true,
      SRisError: false,
      SRError: null,
      SRisSuccess: false,
    }));

    getNewSearchResultsCCUI(
      queryClient,
      paramsForQuery,
      promoId,
      isPromotionsInHotelAvailabilityEnabled
    )
      .then(({ results, total }) => {
        setResultsMeta((resultsMeta) => ({
          ...resultsMeta,
          total,
          currentResults: getCurrentResultsNumber(
            results,
            items,
            total,
            paramsForQuery.lazyLoadPageSize
          ),
          SRisLoading: false,
          SRisSuccess: true,
        }));
        setHotelsInOrder(results);
        setItems((items) => [...items, ...results]);
        setCurrentPage((page) => page + 1);

        if (isLessThanSm) {
          window.scrollTo(0, 4000 * currentPage);
        } else {
          window.scrollTo(0, 1500 * currentPage);
        }
      })
      .catch((error) => {
        setResultsMeta((resultsMeta) => ({
          ...resultsMeta,
          SRisError: true,
          SRError: error,
          SRisSuccess: false,
        }));
      });
  };

  const setHotelsInOrder = (
    hotels: SingleHotelAvailability[],
    { clearPreviousData } = { clearPreviousData: false }
  ) => {
    const newHotelsWithAvailableRooms = getHotelsWithAvailableRooms(hotels);
    const newOpeningSoonHotels = getOpeningSoonHotels(hotels, multiSearchParams);
    const newMlosHotels = searchByHotel && currentPage === 1 ? getMlosHotels(hotels) : mlosHotels;
    const newSoldOutHotels = getSoldOutHotels(hotels, multiSearchParams, newMlosHotels);
    if (searchByHotel && currentPage === 1) {
      setMlosHotels((currentHotels: SingleHotelAvailability[]) => [
        ...(!clearPreviousData ? currentHotels : []),
        ...newMlosHotels,
      ]);
    }
    setHotelsWithAvailableRooms((currentHotels: SingleHotelAvailability[]) => [
      ...(!clearPreviousData ? currentHotels : []),
      ...newHotelsWithAvailableRooms,
    ]);
    setOpeningSoonHotels((currentHotels: SingleHotelAvailability[]) => [
      ...(!clearPreviousData ? currentHotels : []),
      ...newOpeningSoonHotels,
    ]);
    setSoldOutHotels((currentHotels: SingleHotelAvailability[]) => [
      ...(!clearPreviousData ? currentHotels : []),
      ...newSoldOutHotels,
    ]);

    const totalHotels = [
      ...(!clearPreviousData ? mlosHotels : []),
      ...mlosHotels,
      ...(!clearPreviousData ? hotelsWithAvailableRooms : []),
      ...newHotelsWithAvailableRooms,
      ...(!clearPreviousData ? openingSoonHotels : []),
      ...newOpeningSoonHotels,
      ...(!clearPreviousData ? soldOutHotels : []),
      ...newSoldOutHotels,
    ];

    const isMapView = viewType === mapView;
    const { LIST_VIEW, MAP_VIEW } = viewTypeLabelsConstants;

    updateSearchResultsAnalytics({
      addedResults: !clearPreviousData ? hotels.length : undefined,
      isNewSearch: clearPreviousData,
      multiSearchParams,
      startDate,
      endDate,
      viewType: isMapView ? MAP_VIEW : LIST_VIEW,
      searchResults: isMapView ? 0 : totalHotels.length,
      searchResultsDisplayed: totalHotels,
      promotionBannerData: promotionBannerData,
    });
  };

  const place = getPlace(multiSearchParams, language);
  const searchByHotel = place.locationFormat === SR_FORMAT.LATLONG;
  const headerAnnouncement = headerInformationData?.headerInformation?.announcement.text;
  const defaultHotelsOrder = useMemo(
    () => [...mlosHotels, ...hotelsWithAvailableRooms, ...openingSoonHotels, ...soldOutHotels],
    [mlosHotels, hotelsWithAvailableRooms, openingSoonHotels, soldOutHotels]
  );

  const paramsForQuery = {
    startDate: format(startDate, 'yyyy-MM-dd'),
    endDate: format(endDate, 'yyyy-MM-dd'),
    rooms: multiSearchParams.rooms,
    place,
    oldWorldChannel: multiSearchParams.bookingChannel,
    channel: APP_VARIANT.CCUI,
    subChannel: multiSearchParams.bookingChannel,
    page: currentPage,
    initialPageSize: Number(
      partialTranslations?.searchInformation?.config?.api?.initialPageSize ?? 40
    ),
    lazyLoadPageSize: Number(
      partialTranslations?.searchInformation?.config?.api?.lazyLoadPageSize ?? 10
    ),
    country,
    language,
    filters: filters ?? '',
    sort: multiSearchParams.sort,
    companyId: multiSearchParams.corpId,
  };

  if (headerInformationIsError || partialTranslationsIsError) {
    return (
      <Box>
        <Box>Error on loading translations...</Box>
        <Box>{(partialTranslationsError as Error).message}</Box>
        <Box>{(headerInformationError as Error).message}</Box>
      </Box>
    );
  }

  const isLoading =
    headerInformationIsLoading ||
    partialTranslationsisLoading ||
    (resultsMeta.SRisLoading && !items.length);

  if (!resultsMeta.SRisLoading && resultsMeta.SRisError && resultsMeta.SRError) {
    const errorMessage = firstError(resultsMeta.SRError).message;

    if (errorMessage === INVALID_LOCATION_ERROR_MESSAGE) {
      return displayInvalidLocationWarning(
        headerInformationData,
        partialTranslations,
        viewType,
        language,
        multiSearchParams,
        baseDataTestId
      );
    }
  }

  const hasMore =
    resultsMeta.total > Number(paramsForQuery.initialPageSize) && resultsMeta.currentResults > 0;

  if (viewType === undefined || (viewType !== mapView && viewType !== listView)) {
    router.push({
      query: { ...router.query, VIEW: listView },
    });
  }

  const controlsLabels = partialTranslationsisLoading
    ? { buttonLabels: {}, filterLabels: {} }
    : {
        buttonLabels: partialTranslations.searchInformation.content.results.menu,
        filterLabels: partialTranslations.searchInformation.content.filter,
      };

  const dynamicFilters: FilterByLabels = partialTranslationsisLoading
    ? { filters: { label: '', info: '' }, dynamicFilters: [] }
    : {
        dynamicFilters: partialTranslations.searchInformation?.content?.dynamicFilters,
        filters: {
          info: partialTranslations.searchInformation?.content?.filter?.info,
          label: partialTranslations.searchInformation?.content?.filter?.label,
        },
      };

  const defaultFilters = filters.length > 0 ? filters.split(',') : [];

  if (viewType === mapView) {
    const resultedItems = isSearchError ? items.slice(0, MAX_NR_OF_MAP_ITEMS) : [];

    return (
      <Flex position="relative" {...mapViewContainerStyles}>
        <Box {...containerStyles}>
          {resultsMeta.total > 0 && (
            <>
              <PromotionsNotification promotionBannerData={promotionBannerData} viewType="any" />
              <Notification
                variant="info"
                status="info"
                description={headerAnnouncement}
                svg={<Info />}
                isInnerHTML
                wrapperStyles={notificationWrapperStyles}
              />
            </>
          )}
        </Box>
        <Box position="relative" height="100%" paddingTop="var(--chakra-space-lg)">
          <SRControls
            viewType={viewType}
            labels={controlsLabels}
            sortValue={multiSearchParams.sort}
            onChangeViewType={changeViewType}
            onChangeSortValue={changeSortValue}
            onChangeFilters={(filters: SRFiltersType) => {
              handleChangeFilters(filters, router, country, language);
            }}
            visibility={{ isFilterDisabled: !isSrpDynamicFiltersEnabled }}
            defaultFilters={defaultFilters}
            dynamicFilters={dynamicFilters}
          />
          {isLoading && <LoadingSpinner loadingText={t('searchresults.list.hotel.loading')} />}
          <Box pos="absolute" {...mapViewFiltersNoResultsNotificationStyles}>
            {!isLoading &&
              filters.length > 0 &&
              resultsMeta.total === 0 &&
              displayNoHotelsFoundWarning(
                partialTranslations.searchInformation.content.results.notifications.noFilteredHotels
              )}
          </Box>
          <SRMapView
            items={resultedItems}
            multiSearchParams={multiSearchParams}
            locale={language}
            partialTranslations={partialTranslations as SRPartialTranslationsType}
            headerInformation={headerInformationData}
            baseDataTestId={baseDataTestId}
            isPricePerNightEnabled={isPricePerNightEnabledOnCcui}
          />
        </Box>
      </Flex>
    );
  }

  return (
    <Box {...containerStyles}>
      {resultsMeta.total === 0 &&
        filters.length > 0 &&
        resultsMeta.SRisSuccess &&
        displayNoHotelsFoundWarning(
          partialTranslations.searchInformation.content.results.notifications.noFilteredHotels
        )}
      {resultsMeta.total === 0 &&
        filters.length === 0 &&
        resultsMeta.SRisSuccess &&
        displayNoHotelsFoundWarning(
          headerInformationData?.headerInformation?.results?.notifications?.noResults
        )}
      {resultsMeta.total >= 0 && isSearchError && (
        <>
          <PromotionsNotification promotionBannerData={promotionBannerData} viewType="warning" />
          {headerAnnouncement && (
            <Notification
              variant="info"
              status="info"
              description={<DescriptionBox html={headerAnnouncement} />}
              svg={<Info />}
              isInnerHTML
              wrapperStyles={notificationWrapperStyles}
            />
          )}
          <SRControls
            viewType={viewType ?? listView}
            labels={controlsLabels}
            sortValue={multiSearchParams.sort}
            defaultFilters={defaultFilters}
            onChangeFilters={(filters: SRFiltersType) => {
              handleChangeFilters(filters, router, country, language);
            }}
            onChangeViewType={changeViewType}
            onChangeSortValue={changeSortValue}
            visibility={{
              isFilterDisabled: !isSrpDynamicFiltersEnabled,
              isSortDisabled: !isSrpDynamicFiltersEnabled,
            }}
            dynamicFilters={dynamicFilters}
          />
          {isLoading && <LoadingSpinner loadingText={t('searchresults.list.hotel.loading')} />}
        </>
      )}
      {isSearchError && (
        <ListView
          fetchNewHotels={fetchNewHotels}
          items={items}
          hasMore={hasMore}
          resultsMeta={resultsMeta}
          partialTranslations={partialTranslations as SRPartialTranslationsType}
          orderedHotels={defaultHotelsOrder}
          isHotelAvailable={availabilityResult}
          roomTypes={roomTypes}
          multiSearchParams={multiSearchParams}
          language={language}
          currentPage={currentPage}
          baseDataTestId={baseDataTestId}
          headerInformation={headerInformationData}
          changeViewType={changeViewType}
          variant="ccui"
          featureToggle={{ isPricePerNightEnabledOnCcui }}
          promotionBannerData={promotionBannerData}
        />
      )}
    </Box>
  );
}

export const getHotelsWithAvailableRooms = (hotels: SingleHotelAvailability[]) =>
  hotels?.filter((hotel: SingleHotelAvailability) => {
    if (!hotel?.hotelInformation?.hotelOpeningDate) {
      return hotel?.hotelAvailability?.available;
    } else {
      try {
        return (
          hotel?.hotelAvailability?.available &&
          differenceInDays(new Date(hotel.hotelInformation.hotelOpeningDate), new Date()) <= 0
        );
      } catch (e) {
        return false;
      }
    }
  });

export const getSoldOutHotels = (
  hotels: SingleHotelAvailability[],
  multiSearchParams: SRMultiSearchParamsType,
  mlosHotels: SingleHotelAvailability[]
) =>
  hotels?.filter((hotel: SingleHotelAvailability) => {
    if (mlosHotels.find((mlosHotel) => mlosHotel.hotelId === hotel.hotelId)) {
      return false;
    }
    if (!hotel?.hotelInformation?.hotelOpeningDate) {
      return !hotel?.hotelAvailability?.available;
    }
    try {
      return (
        !hotel?.hotelAvailability?.available &&
        differenceInDays(
          new Date(hotel?.hotelInformation?.hotelOpeningDate),
          new Date(
            `${multiSearchParams?.arrivalMonth}/${multiSearchParams?.arrivalDay}/${multiSearchParams?.arrivalYear}`
          )
        ) <= 0
      );
    } catch (e) {
      return false;
    }
  });

export const getMlosHotels = (hotels: SingleHotelAvailability[]) =>
  hotels?.filter((hotel: SingleHotelAvailability) => hotel?.hotelAvailability?.hasMlosRestriction);

const displayInvalidLocationWarning = (
  headerInformationData: HeaderInformationData,
  partialTranslations: any,
  viewType: string | string[] | undefined,
  language: string,
  multiSearchParams: SRMultiSearchParamsType,
  baseDataTestId: string
) => {
  const description = (
    <>
      <Text>
        {partialTranslations.searchInformation?.content?.results?.notifications?.errorTitle}
      </Text>
      <Text>
        {
          partialTranslations.searchInformation?.content?.results?.notifications
            ?.availabilitiesErrorMessage
        }
      </Text>
    </>
  );

  return (
    <>
      <Box {...containerStyles}>
        <Notification
          status="warning"
          variant="alert"
          description={description}
          svg={<Alert color="var(--chakra-colors-alert)" />}
          wrapperStyles={notificationWrapperStyles}
        />
      </Box>

      {viewType === mapView && (
        <Box mt="md" height="100%">
          <SRMapView
            items={[]}
            multiSearchParams={multiSearchParams}
            locale={language}
            partialTranslations={partialTranslations}
            headerInformation={headerInformationData}
            baseDataTestId={baseDataTestId}
          />
        </Box>
      )}
    </>
  );
};

const displayNoHotelsFoundWarning = (description: string) => {
  return (
    <Notification
      variant="alert"
      status="warning"
      description={description}
      svg={<Alert />}
      wrapperStyles={notificationWrapperStyles}
    />
  );
};

function handleChangeFilters(
  filters: SRFiltersType,
  router: NextRouter,
  country: string,
  language: string
) {
  const searchRedirectURL = getSearchRedirectURL(router, ['FILTERS'], country, language);

  const url =
    filters.length === 0 ? searchRedirectURL : `${searchRedirectURL}&FILTERS=${filters.join()}`;

  return router.push(url, undefined, {
    shallow: true,
  });
}

const notificationWrapperStyles = {
  marginTop: { base: 'md', lg: 'lg' },
  zIndex: '1',
};

const containerStyles = {
  maxWidth: {
    mobile: '100%',
    lg: 'var(--chakra-space-breakpoint-lg)',
    xl: 'var(--chakra-space-breakpoint-xl)',
  },
  px: {
    mobile: '1rem',
    sm: '1.25rem',
    md: '1.5rem',
    lg: '1.75rem',
    xl: '4.125rem',
  },
  width: '100%',
  mx: 'auto',
};

const mapViewContainerStyles = {
  height: '100%',
  flexDirection: 'column',
} as FlexProps;

const mapViewFiltersNoResultsNotificationStyles = {
  ...containerStyles,
  zIndex: 1,
  mt: { base: '7xl', sm: '3xl', lg: '2xl' },
  px: {
    xl: '4.8125rem',
    lg: '1.75rem',
    md: '2.375rem',
    sm: 'var(--chakra-space-lg)',
    mobile: '1.1875rem',
  },
};
