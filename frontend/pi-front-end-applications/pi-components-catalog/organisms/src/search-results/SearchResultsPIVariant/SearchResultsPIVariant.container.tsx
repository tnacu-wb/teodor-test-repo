import { Box, Flex, Text } from '@chakra-ui/react';
import type { FlexProps } from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import {
  GET_STATIC_CONTENT,
  SEARCH_INFORMATION_RESULTS,
  SITE_LEISURE,
  HeaderInformationData,
  SRPartialTranslationsType,
  RC_PRICE_MODIFIER,
  RC_DISTANCE_MODIFIER,
  RC_HUB_MODIFIER,
  FT_PI_SORT_ORDER_DROPDOWN,
  FT_PI_PRICE_PER_NIGHT,
  FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY,
  FT_PI_SRP_SPLIT_MAP_VIEW,
} from '@whitbread-eos/api';
import type {
  SingleHotelAvailability,
  SRMultiSearchParamsType,
  SRParamsForQuery,
  SRFiltersType,
  GetParamsForQueryFunctionParamsType,
  FilterByLabels,
} from '@whitbread-eos/api';
import {
  Alert,
  Info,
  Notification,
  MapCompress,
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
  getCookie,
  useFeatureToggle,
  type PromotionsInformation,
  useMobileControlsDisplay,
  analytics,
  getPromoId,
  getNewSearchResultsPI,
  setPromoInfoState,
} from '@whitbread-eos/utils';
import { add, format } from 'date-fns';
import { useTranslation } from 'next-i18next';
import { NextRouter, useRouter } from 'next/router';
import { useCallback, useEffect, useState } from 'react';

import { ListView, MapViewPIVariant as SRMapView } from '../../index';
import { isHotelAvailable, mapMultiSearchToQueryParams } from '../../search/utilities';
import { APP_VARIANT, INVALID_LOCATION_ERROR_MESSAGE } from '../constants';
import {
  firstError,
  getCurrentResultsNumber,
  getSearchQueryUrl,
  getNewUrlSortValue,
} from '../utilities';
import SplitView, { useSplitViewLayout } from './SplitView';

interface Props {
  multiSearchParams: SRMultiSearchParamsType;
  onNoHotelsWarning?: (value: boolean) => void;
  queryClient: QueryClient;
  promotionBannerData?: PromotionsInformation;
}

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

const MAX_NR_OF_MAP_ITEMS = 40;
const HOTEL_AVAILABILITIES_QUERY_KEY = 'hotelAvailabilitiesV2';

const getMobileContainerStyles = (controlsDisplay: boolean) =>
  controlsDisplay ? containerStyles : { paddingTop: 'var(--chakra-space-lg)' };

export default function SearchResultsPIVariantContainer({
  multiSearchParams,
  queryClient,
  promotionBannerData: promotionBannerDataProp,
}: Readonly<Props>) {
  const { language, country } = useCustomLocale();
  const controlsDisplay = useMobileControlsDisplay(APP_VARIANT.PI);
  const { t } = useTranslation(['common']);
  const router = useRouter();
  const viewType = router.query?.VIEW;
  const URLSortValue = router.query?.SORT;
  const filters = (router.query?.FILTERS ?? '') as string;
  const promoId = getPromoId(router.query?.PROMOID);
  const baseDataTestId = 'SRP';
  const roomTypes = multiSearchParams.rooms.map((room) => room.type);
  const { isLessThanSm, isLessThanMd } = useScreenSize();
  const [currentPage, setCurrentPage] = useState(1);
  const [items, setItems] = useState<SingleHotelAvailability[]>([]);
  const [availabilityResult, setAvailabilityResult] = useState<any>(null);
  const [resultsMeta, setResultsMeta] = useState({
    total: 0,
    currentResults: 0,
    SRisLoading: true,
    SRisError: false,
    SRError: null,
    SRisSuccess: false,
  });
  const {
    [FT_PI_SORT_ORDER_DROPDOWN]: isPiSortOrderDropdownEnabled,
    [FT_PI_PRICE_PER_NIGHT]: isPricePerNightEnabledOnPi,
    [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: isPromotionsInHotelAvailabilityEnabled,
    [FT_PI_SRP_SPLIT_MAP_VIEW]: isSplitMapViewEnabled,
  } = useFeatureToggle();
  const { isSplitViewSupported, isCompactSplitView } = useSplitViewLayout();
  const isSplitViewActive = isSplitMapViewEnabled && isSplitViewSupported;

  useEffect(() => {
    if (!isSplitViewActive) return;

    const previousOverflowY = document.body.style.overflowY;
    document.body.style.overflowY = 'hidden';
    document.body.classList.add('srp-split-view-active');

    return () => {
      document.body.style.overflowY = previousOverflowY;
      document.body.classList.remove('srp-split-view-active');
    };
  }, [isSplitViewActive]);

  const [promotionBannerData, setPromotionBannerData] = useState<PromotionsInformation>(
    promotionBannerDataProp as PromotionsInformation
  );

  const [hotelList, setHotelList] = useState<SingleHotelAvailability[]>([]);
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
    : null;
  const endDate =
    (startDate && add(startDate, { days: Number(multiSearchParams.numberOfNights) })) || null;

  const cellCodes = multiSearchParams?.cellCodes;

  const changeViewType = useCallback(() => {
    const searchRedirectURL = getSearchRedirectURL(router, ['VIEW', 'FILTERS'], country, language);
    const newViewType = viewType === mapView || viewType === undefined ? listView : mapView;

    router.push(`${searchRedirectURL}&VIEW=${newViewType}&FILTERS=${filters}`, undefined, {
      shallow: true,
    });

    if (newViewType === mapView) {
      analytics.track('analyticsData_updated_switch_to_map_view', undefined, true);
    } else if (newViewType === listView) {
      analytics.track('analyticsData_updated_switch_to_list_view', undefined, true);
    }
  }, [router, viewType, filters]);

  const changeSortValue = useCallback(
    (value: any) => {
      const searchRedirectURL = getSearchRedirectURL(
        router,
        ['SORT', 'FILTERS'],
        country,
        language
      );

      const newSortValue = getNewUrlSortValue(value, URLSortValue, {
        isPiSortOrderDropdownEnabled: isPiSortOrderDropdownEnabled,
      });

      analytics.track('analyticsData_updated_sorting_applied', undefined, true);

      router.push(`${searchRedirectURL}&SORT=${newSortValue}&FILTERS=${filters}`, undefined, {
        shallow: true,
      });
    },
    [router, viewType, URLSortValue, filters]
  );

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
  } = useQueryRequest(['GetStaticContent', language, country], GET_STATIC_CONTENT, {
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
      queryClient.cancelQueries({ queryKey: [HOTEL_AVAILABILITIES_QUERY_KEY] });
      getNewSearchResultsPI(
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

          updateHotelList(results, { clearPreviousData: true });
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
      APP_VARIANT.PI
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

    getNewSearchResultsPI(
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
        updateHotelList(results);
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
          SRisLoading: false,
          SRError: error,
          SRisSuccess: false,
        }));
      });
  };

  const updateHotelList = (
    hotels: SingleHotelAvailability[],
    { clearPreviousData } = { clearPreviousData: false }
  ) => {
    setHotelList((currentHotels: SingleHotelAvailability[]) => [
      ...(!clearPreviousData ? currentHotels : []),
      ...hotels,
    ]);

    const totalHotels = [...(!clearPreviousData ? hotelList : []), ...hotels];

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
  const headerAnnouncement = headerInformationData?.headerInformation?.announcement.text;
  const paramsForQuery = getParamsForQuery({
    startDate,
    endDate,
    multiSearchParams,
    place,
    partialTranslations: partialTranslations as SRPartialTranslationsType,
    currentPage,
    country,
    language,
    filters,
    cellCodes,
  });

  if (headerInformationIsError || partialTranslationsIsError) {
    return (
      <Box>
        <Box>Error on loading translations...</Box>
        {partialTranslationsError && <Box>{(partialTranslationsError as Error).message}</Box>}
        {headerInformationError && <Box>{(headerInformationError as Error).message}</Box>}
      </Box>
    );
  }

  const isLoading =
    headerInformationIsLoading ||
    partialTranslationsisLoading ||
    (resultsMeta.SRisLoading && !hotelList.length);

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
    resultsMeta.currentResults > 0 && resultsMeta.total > Number(paramsForQuery.initialPageSize);

  if (viewType === undefined || (viewType !== listView && viewType !== mapView)) {
    router.push({
      query: { ...router.query, VIEW: listView },
    });
  }

  const controlsLabels = partialTranslationsisLoading
    ? { filterLabels: {}, buttonLabels: {} }
    : {
        filterLabels: partialTranslations.searchInformation.content.filter,
        buttonLabels: partialTranslations.searchInformation.content.results.menu,
      };

  const dynamicFilters: FilterByLabels = partialTranslationsisLoading
    ? { dynamicFilters: [], filters: { label: '', info: '' } }
    : {
        filters: {
          label: partialTranslations.searchInformation?.content?.filter?.label,
          info: partialTranslations.searchInformation?.content?.filter?.info,
        },
        dynamicFilters: partialTranslations.searchInformation?.content?.dynamicFilters,
      };

  const defaultFilters = filters.length > 0 ? filters.split(',') : [];
  const hasHotels = resultsMeta.total > 0;
  const shouldDisplayNoHotelsWarning = !hasHotels && !isLoading && filters.length === 0;

  const containerStylesMobileView = getMobileContainerStyles(controlsDisplay ?? false);

  const showSplitViewTotalResults = hasHotels && !partialTranslationsisLoading;
  const showSplitViewPricePerNightToggle =
    showSplitViewTotalResults &&
    isPricePerNightEnabledOnPi &&
    (multiSearchParams.numberOfNights ?? 0) > 1;

  if (isSplitViewActive) {
    return (
      <SplitView
        isCompactSplitView={isCompactSplitView}
        controlsLabels={controlsLabels}
        multiSearchParams={multiSearchParams}
        changeViewType={changeViewType}
        changeSortValue={changeSortValue}
        handleChangeFilters={handleChangeFilters}
        defaultFilters={defaultFilters}
        isPiSortOrderDropdownEnabled={isPiSortOrderDropdownEnabled}
        dynamicFilters={dynamicFilters}
        channel={APP_VARIANT.PI}
        showSplitViewTotalResults={showSplitViewTotalResults}
        showSplitViewPricePerNightToggle={showSplitViewPricePerNightToggle}
        resultsMeta={resultsMeta}
        partialTranslations={partialTranslations as SRPartialTranslationsType}
        baseDataTestId={baseDataTestId}
        t={t}
        shouldDisplayNoHotelsWarning={shouldDisplayNoHotelsWarning}
        headerInformationData={headerInformationData}
        promotionBannerData={promotionBannerData}
        hasHotels={hasHotels}
        headerAnnouncement={headerAnnouncement}
        isLoading={isLoading}
        filters={filters}
        currentPage={currentPage}
        availabilityResult={availabilityResult}
        language={language}
        roomTypes={roomTypes}
        hotelList={hotelList}
        fetchNewHotels={fetchNewHotels}
        items={items}
        isPricePerNightEnabledOnPi={isPricePerNightEnabledOnPi}
        hasMore={hasMore}
      />
    );
  }

  if (viewType === mapView) {
    return (
      <Flex position="relative" {...mapViewContainerStyles}>
        <Box {...containerStyles}>
          {hasHotels && (
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
        <Box position="relative" height="100%" {...containerStylesMobileView}>
          <SRControls
            viewType={viewType}
            onChangeViewType={changeViewType}
            onChangeSortValue={changeSortValue}
            onChangeFilters={handleChangeFilters}
            labels={controlsLabels}
            sortValue={multiSearchParams.sort}
            defaultFilters={defaultFilters}
            dynamicFilters={dynamicFilters}
            channel={APP_VARIANT.PI}
          />
          <Box pos="absolute" {...mapViewFiltersErrorNotificationStyles}>
            {!isLoading &&
              !hasHotels &&
              filters.length > 0 &&
              displayNoHotelsFoundWarning(
                partialTranslations.searchInformation.content.results.notifications.noFilteredHotels
              )}
          </Box>
          {isLoading && <LoadingSpinner loadingText={t('searchresults.list.hotel.loading')} />}
          {isLessThanMd && !controlsDisplay && (
            <Box
              data-testid="mobile-list-view-button"
              {...mobileViewTypeButtonStyles}
              top="40px"
              onClick={changeViewType}
            >
              <MapCompress />
            </Box>
          )}
          {!isLoading && (
            <SRMapView
              isPricePerNightEnabled={isPricePerNightEnabledOnPi}
              variant="pi"
              baseDataTestId={baseDataTestId}
              headerInformation={headerInformationData}
              partialTranslations={partialTranslations as SRPartialTranslationsType}
              locale={language}
              multiSearchParams={multiSearchParams}
              items={items.slice(0, MAX_NR_OF_MAP_ITEMS)}
            />
          )}
        </Box>
      </Flex>
    );
  }

  return (
    <>
      {shouldDisplayNoHotelsWarning && (
        <Box {...containerStyles}>
          {displayNoHotelsFoundWarning(
            headerInformationData?.headerInformation?.results.notifications.noResults
          )}
        </Box>
      )}
      <Box {...containerStyles} display={shouldDisplayNoHotelsWarning ? 'none' : 'initial'}>
        <>
          <PromotionsNotification promotionBannerData={promotionBannerData} viewType="warning" />
          {hasHotels && headerAnnouncement && (
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
            onChangeViewType={changeViewType}
            onChangeSortValue={changeSortValue}
            onChangeFilters={handleChangeFilters}
            defaultFilters={defaultFilters}
            isPiSortOrderDropdownEnabled={isPiSortOrderDropdownEnabled}
            dynamicFilters={dynamicFilters}
            channel={APP_VARIANT.PI}
          />
          {!isLoading &&
            !hasHotels &&
            filters.length > 0 &&
            displayNoHotelsFoundWarning(
              partialTranslations.searchInformation.content.results.notifications.noFilteredHotels
            )}
          {isLoading && <LoadingSpinner loadingText={t('searchresults.list.hotel.loading')} />}
        </>

        {!isLoading && (
          <ListView
            featureToggle={{ isPricePerNightEnabledOnPi }}
            variant="pi"
            changeViewType={changeViewType}
            headerInformation={headerInformationData}
            baseDataTestId={baseDataTestId}
            currentPage={currentPage}
            isHotelAvailable={availabilityResult}
            language={language}
            multiSearchParams={multiSearchParams}
            roomTypes={roomTypes}
            orderedHotels={hotelList}
            partialTranslations={partialTranslations as SRPartialTranslationsType}
            resultsMeta={resultsMeta}
            hasMore={hasMore}
            items={items}
            fetchNewHotels={fetchNewHotels}
            promotionBannerData={promotionBannerData}
          />
        )}
      </Box>
    </>
  );

  function handleChangeFilters(filters: SRFiltersType) {
    const searchRedirectURL = getSearchRedirectURL(router, ['FILTERS'], country, language);

    analytics.track('analyticsData_updated_filter_applied', undefined, true);

    const url =
      filters.length === 0 ? searchRedirectURL : `${searchRedirectURL}&FILTERS=${filters.join()}`;

    return router.push(url, undefined, {
      shallow: true,
    });
  }
}

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

export const displayNoHotelsFoundWarning = (description: string) => {
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

export function getSearchRedirectURL(
  router: NextRouter,
  paramsToIgnore: string[],
  country: string,
  language: string
) {
  const queryParamsUrl = getSearchQueryUrl(router, paramsToIgnore);
  return `${country}/${language}/search.html?${queryParamsUrl}`;
}

const priceCookie = getCookie(RC_PRICE_MODIFIER);
const distanceCookie = getCookie(RC_DISTANCE_MODIFIER);
const hubModifierCookie = getCookie(RC_HUB_MODIFIER);

const rcPriceModifier =
  priceCookie !== undefined && priceCookie !== null ? parseFloat(priceCookie) : 1;

const rcDistanceModifier =
  distanceCookie !== undefined && distanceCookie !== null ? parseFloat(distanceCookie) : 1;

const rcHubModifier =
  hubModifierCookie !== undefined && hubModifierCookie !== null
    ? parseFloat(hubModifierCookie)
    : undefined;

const sortOption = {
  rcPriceModifier: rcPriceModifier,
  rcDistanceModifier: rcDistanceModifier,
  rcHubModifier: rcHubModifier,
};

function getParamsForQuery({
  startDate,
  endDate,
  multiSearchParams,
  place,
  partialTranslations,
  currentPage,
  country,
  language,
  filters,
  cellCodes,
}: GetParamsForQueryFunctionParamsType): SRParamsForQuery {
  return {
    startDate: startDate && format(startDate, 'yyyy-MM-dd'),
    endDate: endDate && format(endDate, 'yyyy-MM-dd'),
    rooms: multiSearchParams.rooms,
    place,
    oldWorldChannel: multiSearchParams.bookingChannel,
    channel: APP_VARIANT.PI,
    subChannel: multiSearchParams.bookingChannel,
    page: currentPage,
    initialPageSize: Number(
      partialTranslations?.searchInformation?.config?.api?.initialPageSize ?? 40
    ),
    lazyLoadPageSize: Number(
      partialTranslations?.searchInformation?.config?.api?.lazyLoadPageSize ?? 40
    ),
    country,
    language,
    sort: multiSearchParams.sort,
    sortOption,
    filters: filters ?? '',
    ratePlanCodes: cellCodes ?? [],
  };
}

export const notificationWrapperStyles = {
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

const mapViewFiltersErrorNotificationStyles = {
  ...containerStyles,
  mt: { mobile: '0px', base: '7xl', sm: '3xl', lg: '2xl' },
  zIndex: 1,
  pr: {
    mobile: '3rem',
    sm: '3rem',
  },
  px: {
    mobile: '1.1875rem',
    sm: 'var(--chakra-space-lg)',
    md: '2.375rem',
    lg: '1.75rem',
    xl: '4.8125rem',
  },
};

const mobileViewTypeButtonStyles = {
  pos: 'absolute',
  top: 'var(--chakra-space-lg)',
  right: 'var(--chakra-space-sm)',
  zIndex: '10',
  background: 'white',
  padding: 'var(--chakra-space-sm)',
  cursor: 'pointer',
} as const;
