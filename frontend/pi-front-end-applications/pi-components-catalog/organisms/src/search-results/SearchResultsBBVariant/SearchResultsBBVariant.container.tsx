import type { FlexProps } from '@chakra-ui/react';
import { Box, Flex, Text } from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import type {
  GetParamsForQueryFunctionParamsType,
  SRFiltersType,
  SingleHotelAvailability,
  SRMultiSearchParamsType,
  SRParamsForQuery,
  SRPartialTranslationsType,
  FilterByLabels,
} from '@whitbread-eos/api';
import {
  SEARCH_INFORMATION_RESULTS,
  SITE_BB,
  HeaderInformationData,
  RC_PRICE_MODIFIER,
  RC_DISTANCE_MODIFIER,
  FT_BB_SORT_ORDER_DROPDOWN,
  FT_PI_BB_CCUI_BARRIER_FREE_LABEL,
  FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY,
  FT_BB_PRICE_PER_NIGHT,
  getStaticContent,
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
  isDateValid,
  updateSearchResultsAnalytics,
  useCustomLocale,
  useQueryRequest,
  useScreenSize,
  getAuthCookie,
  useFeatureToggle,
  roomDefaultValueIfError,
  getCookie,
  type PromotionsInformation,
  useMobileControlsDisplay,
  getPromoId,
  getNewSearchResultsBB,
  setPromoInfoState,
} from '@whitbread-eos/utils';
import { add, format, isBefore } from 'date-fns';
import { useTranslation } from 'next-i18next';
import { NextRouter, useRouter } from 'next/router';
import { useCallback, useEffect, useState } from 'react';

import { ListView, MapViewBBVariant as SRMapView } from '../../index';
import { isHotelAvailable, mapMultiSearchToQueryParams } from '../../search/utilities';
import { APP_VARIANT, INVALID_LOCATION_ERROR_MESSAGE } from '../constants';
import {
  firstError,
  getCurrentResultsNumber,
  getSearchQueryUrl,
  getNewUrlSortValue,
} from '../utilities';

interface Props {
  multiSearchParams: SRMultiSearchParamsType;
  onNoHotelsWarning?: (value: boolean) => void;
  queryClient: QueryClient;
  variant: string;
  innBusiness?: boolean;
  promotionBannerData?: PromotionsInformation;
}

type NotificationWrapperProps = {
  promotionBannerData: any;
  viewType?: string;
};

export function NotificationWrapper({ promotionBannerData, viewType }: NotificationWrapperProps) {
  if (!promotionBannerData) return null;

  return (
    <PromotionsNotification promotionBannerData={promotionBannerData} viewType={viewType ?? ''} />
  );
}

const HOTEL_AVAILABILITIES_QUERY_KEY = 'hotelAvailabilities';
const idTokenCookie = getAuthCookie();

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
const PIB_SRP_SCROLL_TARGET = 'pib-main-container';

export default function SearchResultsBBVariantContainer({
  multiSearchParams,
  queryClient,
  variant,
  innBusiness,
  promotionBannerData: promotionBannerDataProp,
}: Readonly<Props>) {
  const featureToggles = useFeatureToggle();
  const isIBEnabled = innBusiness;
  const isBarrierFreeLabelEnabled = featureToggles[FT_PI_BB_CCUI_BARRIER_FREE_LABEL];
  const isPricePerNightEnabledOnBb = featureToggles[FT_BB_PRICE_PER_NIGHT];
  const controlsDisplay = useMobileControlsDisplay(APP_VARIANT.BB);

  const { language, country } = useCustomLocale();
  const { t } = useTranslation(['common']);
  const router = useRouter();
  const viewType = router.query?.VIEW;
  const URLSortValue = router.query?.SORT;
  const promoId = getPromoId(router.query?.PROMOID);
  const filters = (router.query?.FILTERS ?? '') as string;
  const baseDataTestId = 'SRP';

  const roomTypes = multiSearchParams.rooms.map((room) => room.type);
  const { isLessThanSm, isLessThanMd, isLessThanLg } = useScreenSize();
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
    [FT_BB_SORT_ORDER_DROPDOWN]: isBbSortOrderDropdownEnabled,
    [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: isPromotionsInHotelAvailabilityEnabled,
  } = useFeatureToggle();

  const [promotionBannerData, setPromotionBannerData] = useState<PromotionsInformation>(
    promotionBannerDataProp as PromotionsInformation
  );

  const [hotelList, setHotelList] = useState<SingleHotelAvailability[]>([]);
  const isValidDate = isDateValid(
    Number(multiSearchParams?.arrivalDay),
    Number(multiSearchParams?.arrivalMonth),
    Number(multiSearchParams?.arrivalYear)
  );

  const today = new Date();
  const dateFromParams = new Date(
    Number(multiSearchParams?.arrivalYear),
    Number(multiSearchParams.arrivalMonth && multiSearchParams?.arrivalMonth - 1),
    Number(multiSearchParams?.arrivalDay)
  );

  const startDate = isIBEnabled
    ? isValidDate
      ? isBefore(dateFromParams, today)
        ? new Date()
        : dateFromParams
      : today
    : isValidDate
      ? new Date(
          Number(multiSearchParams?.arrivalYear),
          Number(multiSearchParams.arrivalMonth && multiSearchParams?.arrivalMonth - 1),
          Number(multiSearchParams?.arrivalDay)
        )
      : new Date();

  const endDate = isIBEnabled
    ? add(startDate, {
        days:
          isNaN(Number(multiSearchParams?.numberOfNights)) ||
          Number(multiSearchParams?.numberOfNights) > 14 ||
          Number(multiSearchParams?.numberOfNights) < 1
            ? 1
            : Number(multiSearchParams?.numberOfNights),
      })
    : add(startDate, { days: Number(multiSearchParams?.numberOfNights) });

  const changeViewType = useCallback(() => {
    const searchRedirectURL = getSearchRedirectURL(router, ['VIEW', 'FILTERS'], country, language);
    const newViewType = viewType === mapView || viewType === undefined ? listView : mapView;

    router.push(`/${searchRedirectURL}&VIEW=${newViewType}&FILTERS=${filters}`, undefined, {
      shallow: true,
    });
  }, [router, viewType, filters]);

  const changeSortValue = useCallback(
    (value: any) => {
      const redirectUrl = getSearchRedirectURL(router, ['SORT', 'FILTERS'], country, language);

      const updatedSortValue = getNewUrlSortValue(value, URLSortValue, {
        isBbSortOrderDropdownEnabled: isBbSortOrderDropdownEnabled,
      });

      router.push(`/${redirectUrl}&SORT=${updatedSortValue}&FILTERS=${filters}`, undefined, {
        shallow: true,
      });
    },
    [router, URLSortValue, filters, viewType]
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

  const query = getStaticContent(isBarrierFreeLabelEnabled);

  const {
    data: headerInformationData,
    isError: headerInformationIsError,
    error: headerInformationError,
    isLoading: headerInformationIsLoading,
  } = useQueryRequest(['GetStaticContent', language, country], query, {
    country,
    language,
    site: SITE_BB,
    businessBooker: true,
  });

  function updateStates(
    results: SingleHotelAvailability[],
    total: number,
    promotionsInformation: PromotionsInformation
  ) {
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
  }

  function handleError(error: any) {
    setResultsMeta((resultsMeta) => ({
      ...resultsMeta,
      SRisError: true,
      SRError: error,
      SRisSuccess: false,
    }));
  }

  const handleSRPResults = ({
    results,
    total,
    promotionsInformation,
  }: {
    results: SingleHotelAvailability[];
    total: number;
    promotionsInformation: PromotionsInformation;
  }) => {
    updateStates(results, total, promotionsInformation);
  };

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
      getNewSearchResultsBB(
        queryClient,
        { ...paramsForQuery, page: 1 },
        promoId as string,
        isPromotionsInHotelAvailabilityEnabled as boolean
      )
        .then(handleSRPResults)
        .catch((error) => {
          handleError(error);
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
      APP_VARIANT.BB,
      idTokenCookie
    ).then(setAvailabilityResult);
  }, [multiSearchParams]);

  const handleSRPPaginationResults = (results: SingleHotelAvailability[], total: number) => {
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
  };

  const fetchNewHotels = () => {
    if (resultsMeta.SRisLoading) return;
    setResultsMeta((resultsMeta) => ({
      ...resultsMeta,
      SRisLoading: true,
      SRisError: false,
      SRError: null,
      SRisSuccess: false,
    }));

    getNewSearchResultsBB(
      queryClient,
      paramsForQuery,
      promoId,
      isPromotionsInHotelAvailabilityEnabled
    )
      .then(({ results, total }) => {
        handleSRPPaginationResults(results, total);

        if (isLessThanSm) {
          window.scrollTo(0, 4000 * currentPage);
        } else {
          window.scrollTo(0, 1500 * currentPage);
        }
      })
      .catch((error) => {
        handleError(error);
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
    isIBEnabled,
  });

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
    partialTranslationsisLoading ||
    headerInformationIsLoading ||
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
        baseDataTestId,
        variant
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
    ? { buttonLabels: {}, filterLabels: {} }
    : {
        buttonLabels: partialTranslations.searchInformation.content.results.menu,
        filterLabels: partialTranslations.searchInformation.content.filter,
      };

  const dynamicFilters: FilterByLabels = partialTranslationsisLoading
    ? { dynamicFilters: [], filters: { info: '', label: '' } }
    : {
        dynamicFilters: partialTranslations.searchInformation?.content?.dynamicFilters,
        filters: {
          info: partialTranslations.searchInformation?.content?.filter?.info,
          label: partialTranslations.searchInformation?.content?.filter?.label,
        },
      };

  const defaultFilters = filters.length > 0 ? filters.split(',') : [];
  const hasHotels = resultsMeta.total > 0;
  const shouldDisplayNoHotelsWarning = !isLoading && !hasHotels && filters.length === 0;
  const containerStylesMobileView = controlsDisplay
    ? containerStyles
    : { paddingTop: 'var(--chakra-space-lg)' };
  const scrollableTarget = isIBEnabled && isLessThanLg ? PIB_SRP_SCROLL_TARGET : undefined;

  if (viewType === mapView) {
    return (
      <Flex position="relative" {...mapViewContainerStyles}>
        <Box {...containerStyles}>
          {hasHotels && (
            <>
              <NotificationWrapper promotionBannerData={promotionBannerData} viewType="any" />
              <Notification
                variant="info"
                status="info"
                description={headerInformationData?.headerInformation?.announcement.text}
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
            channel={APP_VARIANT.BB}
            dynamicFilters={dynamicFilters}
          />
          <Box pos="absolute" {...mapViewFiltersErrorNotificationStyles}>
            {!hasHotels &&
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
              items={items.slice(0, MAX_NR_OF_MAP_ITEMS)}
              multiSearchParams={multiSearchParams}
              locale={language}
              partialTranslations={partialTranslations as SRPartialTranslationsType}
              headerInformation={headerInformationData}
              baseDataTestId={baseDataTestId}
              variant={variant}
              isPricePerNightEnabled={isPricePerNightEnabledOnBb}
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
          {hasHotels && (
            <NotificationWrapper promotionBannerData={promotionBannerData} viewType="warning" />
          )}
          {hasHotels && headerInformationData?.headerInformation?.announcement.text && (
            <Notification
              variant="info"
              status="info"
              description={
                <DescriptionBox
                  html={headerInformationData?.headerInformation?.announcement.text}
                />
              }
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
            isBbSortOrderDropdownEnabled={isBbSortOrderDropdownEnabled}
            channel={APP_VARIANT.BB}
            dynamicFilters={dynamicFilters}
          />
          {!hasHotels &&
            filters.length > 0 &&
            displayNoHotelsFoundWarning(
              partialTranslations.searchInformation.content.results.notifications.noFilteredHotels
            )}
          {isLoading && <LoadingSpinner loadingText={t('searchresults.list.hotel.loading')} />}
        </>

        {!isLoading && (
          <ListView
            fetchNewHotels={fetchNewHotels}
            items={items}
            hasMore={hasMore}
            resultsMeta={resultsMeta}
            partialTranslations={partialTranslations as SRPartialTranslationsType}
            orderedHotels={hotelList}
            roomTypes={roomTypes}
            multiSearchParams={multiSearchParams}
            language={language}
            isHotelAvailable={availabilityResult}
            currentPage={currentPage}
            baseDataTestId={baseDataTestId}
            headerInformation={headerInformationData}
            changeViewType={changeViewType}
            variant="bb"
            featureToggle={{ isPricePerNightEnabledOnBb }}
            promotionBannerData={promotionBannerData}
            scrollableTarget={scrollableTarget}
          />
        )}
      </Box>
    </>
  );

  function handleChangeFilters(filters: SRFiltersType) {
    const searchRedirectURL = getSearchRedirectURL(router, ['FILTERS'], country, language);

    const url =
      filters.length === 0
        ? `/${searchRedirectURL}`
        : `/${searchRedirectURL}&FILTERS=${filters.join()}`;

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
  baseDataTestId: string,
  variant: string
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
            variant={variant}
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

export function getSearchRedirectURL(
  router: NextRouter,
  paramsToIgnore: string[],
  country: string,
  language: string
) {
  const queryParamsUrl = getSearchQueryUrl(router, paramsToIgnore);
  return `${country}/${language}/business-booker/search.html?${queryParamsUrl}`;
}

const priceCookie = getCookie(RC_PRICE_MODIFIER);
const distanceCookie = getCookie(RC_DISTANCE_MODIFIER);

const rcPriceModifier =
  priceCookie !== undefined && priceCookie !== null ? parseFloat(priceCookie) : 1;

const rcDistanceModifier =
  distanceCookie !== undefined && distanceCookie !== null ? parseFloat(distanceCookie) : 1;

const sortOption = {
  rcPriceModifier: rcPriceModifier,
  rcDistanceModifier: rcDistanceModifier,
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
  isIBEnabled,
}: GetParamsForQueryFunctionParamsType): SRParamsForQuery {
  return {
    startDate: startDate && format(startDate, 'yyyy-MM-dd'),
    endDate: endDate && format(endDate, 'yyyy-MM-dd'),
    rooms: isIBEnabled ? roomDefaultValueIfError(multiSearchParams.rooms) : multiSearchParams.rooms,
    place,
    oldWorldChannel: multiSearchParams.bookingChannel,
    channel: APP_VARIANT.BB,
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
    sort: multiSearchParams.sort,
    sortOption,
    filters: filters ?? '',
  };
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

const mapViewFiltersErrorNotificationStyles = {
  ...containerStyles,
  mt: { base: '7xl', sm: '3xl', lg: '2xl' },
  zIndex: 1,
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
