import { Box, Flex, FlexProps, Heading, Text } from '@chakra-ui/react';
import { Query, QueryClient, QueryClientProvider } from '@tanstack/react-query';
import {
  CommonIconsQuery,
  DlpAnalytics,
  DlpAnalyticsRequest,
  DlpInformation,
  DlpItem,
  Faq,
  FilterByLabels,
  FT_PI_NEW_DLP_FILTERS,
  GET_STATIC_CONTENT,
  getInnBusinessCommonIcons,
  HeaderInformationQuery,
  HIVisualDisplayContext,
  HotelInformationOptional,
  LanguageEnum,
  PageName,
  ResponsiveValue,
  SearchInformation,
  SelectedFilter,
  SITE_LEISURE,
  ToDoItem,
} from '@whitbread-eos/api';
import { Button, Dismiss, Icon, Notification, Bell } from '@whitbread-eos/atoms';
import {
  DestinationCarousel,
  DestinationFilters,
  HeroSection,
  SEO as Seo,
  TravelGuides,
  WhyUs,
} from '@whitbread-eos/molecules';
import {
  DLPHotelCard,
  MapViewDLPVariant,
  PISearchContainer as SearchContainer,
} from '@whitbread-eos/organisms';
import {
  analytics,
  formatDataTestId,
  updateDestinationPageAnalytics,
  useCustomLocale,
  useFeatureToggle,
  useQueryRequest,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import dynamic from 'next/dynamic';
import DefaultErrorPage from 'next/error';
import { useSearchParams } from 'next/navigation';
import { NextRouter } from 'next/router';
import { useEffect, useRef, useState } from 'react';

import { INITIAL_NUMBER_OF_RENDERED_CARDS } from './data.pi';

const DestinationFaq = dynamic(
  async () => {
    const { DestinationFaq } = await import('@whitbread-eos/molecules');
    return { default: DestinationFaq };
  },
  {
    ssr: true,
  }
);

const SwitchToggle = dynamic(
  async () => {
    const { SwitchToggle } = await import('@whitbread-eos/atoms');
    return { default: SwitchToggle };
  },
  {
    ssr: true,
  }
);

interface Props {
  queryClient: QueryClient;
  router: NextRouter;
  visualDisplayContext: HIVisualDisplayContext;
  queries: Query[];
  dlpQueryKey: unknown[];
  hotelsInformationQueryKey: unknown[];
  searchInformationQueryKey: unknown[];
}

export default function DestinationLandingPagePI({
  queryClient,
  router,
  queries,
  dlpQueryKey,
  hotelsInformationQueryKey,
  searchInformationQueryKey,
}: Readonly<Props>) {
  const { [FT_PI_NEW_DLP_FILTERS]: isDlpFiltersEnabled } = useFeatureToggle();
  const baseDataTestId = 'DestinationLandingPIPage';
  const slug = router?.query?.slug;
  const { country, language } = useCustomLocale();
  const dlpInformationQuery = queries.find(
    (query) => query.queryKey.toString() === dlpQueryKey.toString()
  )?.state.data as { dlpInformation: DlpInformation };
  const getHotelsInformationQuery = queries.find(
    (query) => query.queryKey.toString() === hotelsInformationQueryKey.toString()
  )?.state.data as { getHotelsInformation: HotelInformationOptional[] };
  const getSearchInformationQuery = queries.find(
    (query) => query.queryKey.toString() === searchInformationQueryKey.toString()
  )?.state.data as { searchInformation: SearchInformation };

  const dlpInformation = dlpInformationQuery?.dlpInformation;
  const hotelsInformation = getHotelsInformationQuery?.getHotelsInformation ?? [];
  const searchInformation = getSearchInformationQuery?.searchInformation;

  const heroSectionData = {
    title: dlpInformation?.title || '',
    description: dlpInformation?.description || '',
    picture: dlpInformation?.picture || '',
    breadcrumbs: dlpInformation?.breadcrumbs || [],
  };

  const hotelsList = dlpInformation?.hotels
    .sort((a, b) => a.order - b.order)
    .map((hotel) => hotel.code);
  const { t } = useTranslation();
  const [hotels, setHotels] = useState<HotelInformationOptional[]>(
    hotelsInformation.slice(0, INITIAL_NUMBER_OF_RENDERED_CARDS)
  );
  const filteredHotels = useRef<HotelInformationOptional[]>([]);
  const showGridMapToggle = hotelsList?.length > 0;

  const filterByLabels: FilterByLabels = {
    filters: {
      label: searchInformation?.content?.filter?.label,
      info: searchInformation?.content?.filter?.info,
    },
    dynamicFilters: searchInformation?.content?.dynamicFilters,
  };

  const searchParams = useSearchParams();
  const filters = (
    searchParams.get(language === LanguageEnum.ENGLISH ? 'facility' : 'einrichtung') ?? ''
  )
    .split(',')
    .filter((filter) => filter);

  const [mapSelected, setMapSelected] = useState(showGridMapToggle && router.query.VIEW == '1');
  const [selectedFilters, setSelectedFilters] = useState<SelectedFilter[]>(
    getSelectedFilters(filterByLabels, filters)
  );

  const visibleHotelsNumber = hotels?.length;
  const dlpItems = dlpInformation.dlps?.dlpItems;
  const isFiltersApplied = selectedFilters.length > 0;

  useEffect(() => {
    const queryFilters = getSelectedFilters(
      filterByLabels,
      (searchParams.get(language === LanguageEnum.ENGLISH ? 'facility' : 'einrichtung') ?? '')
        .split(',')
        .filter((filter) => filter)
    );
    if (JSON.stringify(queryFilters) !== JSON.stringify(selectedFilters)) {
      setSelectedFilters(queryFilters);
    }

    const queryMapSelected = showGridMapToggle && router.query.VIEW == '1';
    if (mapSelected !== queryMapSelected) {
      setMapSelected(queryMapSelected);
    }
  }, [router.query]);

  useEffect(() => {
    updateDestinationPageAnalytics({
      locationName: slug?.[slug.length - 1]?.replace('.html', ''),
      hotelDisplayedCount: visibleHotelsNumber,
      destinations: dlpItems,
      hotels: hotels,
      filterType: 'recommended',
      thingsToDo: dlpInformation?.thingsToDo,
      mapReference: mapSelected,
      premierPlusLabel: t('hoteldetails.rates.premierplus'),
      promos: dlpInformation?.promos,
    });
  }, []);

  useEffect(() => {
    const groupedFilters = groupSelectedFilters(selectedFilters);
    filteredHotels.current = filterHotels(hotelsInformation, groupedFilters);
    setHotels(filteredHotels.current.slice(0, INITIAL_NUMBER_OF_RENDERED_CARDS));
    setFilterQueryParams(selectedFilters, router, country, language);
  }, [selectedFilters]);

  useEffect(() => {
    setMapSelectedQueryParams(mapSelected, router, country);
  }, [mapSelected]);

  const latitude = dlpInformation?.coordinates?.latitude;
  const longitude = dlpInformation?.coordinates?.longitude;
  const hideHotelDistance = dlpInformation?.coordinates?.hideHotelDistance ?? false;

  const { data: dataHeaderInformation }: HeaderInformationQuery = useQueryRequest(
    ['GetStaticContent', language, country],
    GET_STATIC_CONTENT,
    {
      country,
      language,
      site: SITE_LEISURE,
      businessBooker: false,
    }
  );

  const { data: dataCommonIcons }: CommonIconsQuery = useQueryRequest(
    ['GetCommonItems', language, country],
    getInnBusinessCommonIcons(),
    {
      country,
      language,
    }
  );

  const changeViewType = (mapSelected: boolean) => {
    setMapSelected(mapSelected);
    analytics.update({
      dlp: {
        ...window.analyticsData.dlp,
        mapReference: mapSelected,
      } as DlpAnalytics,
    });
    if (!mapSelected) {
      analytics.update({
        dlp: {
          ...window.analyticsData.dlp,
          hotelBrandLabel: undefined,
          hotelDistanceFromSearch: undefined,
        } as DlpAnalytics,
      });
    }
  };

  if (!slug) {
    return <DefaultErrorPage statusCode={404} />;
  }

  const { favicon } = dataHeaderInformation.headerInformation.content;
  const icons = JSON.parse(dataCommonIcons.getPageData.commonIconsEndpoint);

  const showMoreHotels = () => {
    const newList =
      selectedFilters.length >= 1
        ? filteredHotels.current.slice(0, hotels.length + INITIAL_NUMBER_OF_RENDERED_CARDS)
        : hotelsInformation.slice(0, hotels.length + INITIAL_NUMBER_OF_RENDERED_CARDS);
    setHotels(newList);
    updateDestinationPageAnalytics({
      ...window.analyticsData.dlp,
      hotelDisplayedCount: hotels.length + newList.length,
      hotels: [...hotels, ...newList],
      destinations: dlpItems,
    } as DlpAnalyticsRequest);
  };

  const showLessHotels = () => {
    const initialHotelsList = hotels.slice(0, INITIAL_NUMBER_OF_RENDERED_CARDS);
    setHotels(initialHotelsList);
    document?.getElementById('hotelsList')?.scrollIntoView({
      behavior: 'smooth',
    });
    updateDestinationPageAnalytics({
      ...window.analyticsData.dlp,
      hotelDisplayedCount: initialHotelsList.length,
      hotels: initialHotelsList,
      destinations: dlpItems,
    } as DlpAnalyticsRequest);
  };

  const isShowMore =
    selectedFilters.length >= 1
      ? visibleHotelsNumber < filteredHotels.current.length
      : hotelsInformation?.length >= INITIAL_NUMBER_OF_RENDERED_CARDS &&
        visibleHotelsNumber < hotelsInformation.length;
  const isShowLess =
    hotelsInformation?.length > INITIAL_NUMBER_OF_RENDERED_CARDS &&
    visibleHotelsNumber === hotelsInformation.length;

  const handleClearFilters = () => {
    setSelectedFilters([]);
    analytics.update({
      dlp: {
        ...window.analyticsData.dlp,
        filtersCleared: true,
        searchFilter: '',
      } as DlpAnalytics,
    });
  };

  return (
    <QueryClientProvider client={queryClient}>
      <Box data-testid={formatDataTestId(baseDataTestId, 'Wrapper')} pb="3xl">
        <Seo
          page={PageName.DLP}
          dlpData={{
            ...dlpInformation.seo,
            cardImageUrl: dlpInformation.picture,
            faq: dlpInformation.faq
              ? { faqItems: dlpInformation.faq.flatMap((item) => item.faqItems) }
              : [],
            faviconUrl: favicon?.faviconUrl ?? '',
            icons: favicon?.icons ?? '',
            msIcons: favicon?.msIcons ?? '',
          }}
          breadcrumbs={dlpInformation.breadcrumbs}
          hotelsList={hotels}
          displayMeta
          noIndexNoFollow={filters.length > 0}
          destinationCoordinates={dlpInformation.coordinates}
          countryCodeISO={hotelsInformation[0]?.countryCodeISO}
        />
        <SearchContainer
          queryClient={queryClient}
          isSummaryActive={false}
          variant="pi"
          marginBottom={{ mobile: '8xl', xs: '8.5rem', sm: 'md' } as ResponsiveValue}
        />
        <HeroSection data={heroSectionData} />
        <Heading as="h6" {...hotelsCounterStyles}>
          {t('dlp.filters.hotelsFound').replace(
            '{amount}',
            selectedFilters?.length > 0
              ? `${filteredHotels.current?.length}`
              : `${hotelsInformation.length}`
          )}
        </Heading>
        {showGridMapToggle && (
          <Flex mt="0.781rem" gap="xmd">
            {isDlpFiltersEnabled && (
              <DestinationFilters
                selectedFilters={selectedFilters}
                setSelectedFilters={setSelectedFilters}
                labels={filterByLabels}
              />
            )}
            <Flex ml="auto">
              {isFiltersApplied && filteredHotels.current.length === 0 ? null : (
                <SwitchToggle
                  defaultSelected={mapSelected}
                  baseDataTestId={baseDataTestId + 'MapGrid'}
                  first={t('dlp.view.map')}
                  second={t('dlp.view.grid')}
                  onToggle={changeViewType}
                />
              )}
            </Flex>
          </Flex>
        )}
        {isDlpFiltersEnabled && isFiltersApplied && (
          <Flex gap="sm" overflow="auto" position="relative" alignItems="center">
            {selectedFilters.map((filter) => (
              <Flex
                {...filterPodStyles}
                key={filter.name}
                data-testid={formatDataTestId(baseDataTestId, `Pod-${filter.name}`)}
              >
                <Text fontFamily="body">{filter.name}</Text>
                <Icon
                  svg={
                    <Dismiss
                      data-testid={formatDataTestId(baseDataTestId, 'Dismiss-Filter-Button')}
                    />
                  }
                  onClick={() => {
                    const newList = selectedFilters.filter(
                      (selectedFilter) => selectedFilter !== filter
                    );
                    analytics.update({
                      dlp: {
                        ...window.analyticsData.dlp,
                        searchFilter: newList.map((filter) => filter.codes).join(','),
                      } as DlpAnalytics,
                    });
                    setSelectedFilters(newList);
                  }}
                  style={{ cursor: 'pointer' }}
                />
              </Flex>
            ))}
          </Flex>
        )}
        {mapSelected && (
          <Box {...mapStyles}>
            {isFiltersApplied && hotels.length === 0 && displayNoHotelsFoundWarning(t)}
            <MapViewDLPVariant
              baseDataTestId={baseDataTestId}
              items={isFiltersApplied ? filteredHotels.current : hotelsInformation}
              latitude={latitude ?? 0}
              longitude={longitude ?? 0}
              icons={icons}
              hideHotelDistance={hideHotelDistance}
            />
          </Box>
        )}
        {!mapSelected && (
          <Box pb="3em">
            {isFiltersApplied && hotels.length === 0 ? (
              renderNoFilteredHotels(t, handleClearFilters)
            ) : (
              <Flex
                id="hotelsList"
                {...hotelsWrapperStyles}
                data-testid={formatDataTestId(baseDataTestId, 'HotelsList')}
              >
                {!hotels?.length
                  ? t('dlp.description.noHotels')
                  : hotels.map((item) => (
                      <DLPHotelCard
                        key={item.name}
                        data={item}
                        hideHotelDistance={hideHotelDistance}
                      />
                    ))}
                <Box {...fillerStyles}></Box>
                <Box {...fillerStyles}></Box>
                <Box {...fillerStyles}></Box>
              </Flex>
            )}
            <Box mt={isShowMore || isShowLess ? 'md' : '0'}>
              {isShowMore && (
                <Button
                  variant="tertiary"
                  onClick={showMoreHotels}
                  data-testid={formatDataTestId(baseDataTestId, 'showMoreBtn')}
                >
                  {t('dlp.hotelCard.showMore')}
                </Button>
              )}
              {isShowLess && (
                <Button
                  variant="tertiary"
                  onClick={showLessHotels}
                  data-testid={formatDataTestId(baseDataTestId, 'showLessBtn')}
                >
                  {t('content.showLess')}
                </Button>
              )}
            </Box>
          </Box>
        )}
        {dlpInformation && (
          <>
            {dlpInformation.promos && dlpInformation.promos.length > 0 && (
              <TravelGuides baseDataTestId="TravelGuides" data={dlpInformation.promos} />
            )}
            {dlpInformation.why && <WhyUs data={dlpInformation.why} />}
            {dlpInformation.thingsToDo && (
              <DestinationCarousel
                baseDataTestId="ThingsToDoList"
                title={dlpInformation?.thingsToDo?.title as string}
                items={dlpInformation?.thingsToDo?.items as ToDoItem[]}
              />
            )}
            {dlpInformation.faq && dlpInformation.faq.length > 0 && (
              <DestinationFaq data={dlpInformation.faq as Faq[]} />
            )}
            {dlpInformation.dlps && (
              <DestinationCarousel
                baseDataTestId="DestinationsList"
                title={dlpInformation.dlps.title as string}
                items={dlpInformation.dlps.dlpItems as DlpItem[]}
              />
            )}
          </>
        )}
      </Box>
    </QueryClientProvider>
  );
}

export const getSelectedFilters = (filterByLabels: FilterByLabels, filters: string[]) =>
  (filterByLabels.dynamicFilters ?? [])
    .map((dynamicFilter) => ({
      groupItems: (dynamicFilter.groupItems ?? []).map((groupItem) => ({
        ...groupItem,
        operator: dynamicFilter.groupOperator,
      })),
    }))
    .flatMap((dynamicFilter) => dynamicFilter.groupItems ?? [])
    .filter((filter) => filters.includes(filter.queryParam ?? ''))
    .map((filter) => ({
      codes: (filter.codes ?? '').split(','),
      name: filter.label,
      operator: filter.operator,
      queryParam: filter.queryParam,
    })) as SelectedFilter[];

export const setMapSelectedQueryParams = (
  mapSelected: boolean,
  router: NextRouter,
  country: string
) => {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const { VIEW, slug, ...query } = router.query;
  if ((!VIEW && !mapSelected) || VIEW == (mapSelected ? '1' : '2')) {
    return;
  }
  router.push(
    {
      pathname: `/${country}${router.asPath.split('?')[0]}`,
      query: {
        ...query,
        VIEW: mapSelected ? '1' : '2',
      },
    },
    undefined,
    { shallow: true }
  );
};

export function setFilterQueryParams(
  selectedFilters: SelectedFilter[],
  router: NextRouter,
  country: string,
  language: string
) {
  const facilityQueryParams = selectedFilters
    .map((filter) => filter.queryParam)
    .filter((queryParam) => queryParam)
    .sort()
    .join(',');
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const { facility, einrichtung, slug, ...query } = router.query;
  if ((facility ?? einrichtung ?? '') === facilityQueryParams) {
    return;
  }
  if (facilityQueryParams.length) {
    router.push(
      {
        pathname: `/${country}${router.asPath.split('?')[0]}`,
        query: {
          ...query,
          [language === LanguageEnum.ENGLISH ? 'facility' : 'einrichtung']: facilityQueryParams,
        },
      },
      undefined,
      { shallow: true }
    );
  } else {
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    const { facility, einrichtung, slug, ...query } = router.query;
    router.push(
      {
        pathname: `/${country}${router.asPath.split('?')[0]}`,
        query,
      },
      undefined,
      { shallow: true }
    );
  }
}

export function groupSelectedFilters(selectedFilters: SelectedFilter[]) {
  return selectedFilters.reduce(
    (acc, curr) => {
      acc[curr.operator] = acc[curr.operator] ?? [];
      const isFilterWithMultipleCodes = curr.codes.length > 1;
      const selectedFilterCodes = isFilterWithMultipleCodes
        ? (curr.codes as string[])
        : curr.codes[0];

      acc[curr.operator].push(selectedFilterCodes as string);

      return acc;
    },
    {} as Record<string, string[]>
  );
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function renderNoFilteredHotels(t: any, handleClearFilters: any) {
  return (
    <Flex {...noResultsContainerStyles}>
      <Heading as="h1" {...noResultsHeadingStyles} data-testid="DLP-Filters-NoResults-Title">
        {t('dlp.filters.noResults.title')}
      </Heading>
      <Text {...noResultsTextStyles} data-testid="DLP-Filters-NoResults-Subtitle">
        {t('dlp.filters.noResults.subtitle')}
      </Text>
      <Button
        onClick={handleClearFilters}
        variant="tertiary"
        size="sm"
        data-testid="DLP-Filters-NoResults-ClearAll-Button"
      >
        {t('dlp.filters.noResults.clearAll')}
      </Button>
    </Flex>
  );
}

export function filterHotels(
  hotelsInformation: HotelInformationOptional[],
  groupedFilters: Record<string, string[]>
) {
  return hotelsInformation?.filter((hotel) => {
    const hotelFacilitiesCodes = hotel?.hotelFacilities?.map((facility) => facility.code);
    const orFilters =
      groupedFilters.OR?.length === 0 ||
      groupedFilters.OR?.some((filterCode) => {
        if (Array.isArray(filterCode)) {
          return filterCode.some((code) => hotelFacilitiesCodes?.includes(code));
        }
        return hotelFacilitiesCodes?.includes(filterCode);
      });

    const andFilters = groupedFilters.AND?.every((filterCode) => {
      if (Array.isArray(filterCode)) {
        return filterCode.some((code) => hotelFacilitiesCodes?.includes(code));
      }
      return hotelFacilitiesCodes?.includes(filterCode);
    });

    return (
      (groupedFilters?.OR?.length ? orFilters : true) &&
      (groupedFilters?.AND?.length ? andFilters : true)
    );
  });
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const displayNoHotelsFoundWarning = (t: any) => {
  return (
    <Box position="absolute" {...mapViewFiltersNotificationWrapperStyles}>
      <Notification
        variant="info"
        status="info"
        title={t('dlp.filters.noResults.title')}
        description={t('dlp.filters.noResults.mapSubtitle')}
        svg={<Bell />}
        wrapperStyles={filtersNotificationStyles}
      />
    </Box>
  );
};

const hotelsWrapperStyles = {
  mx: 'auto',
  mt: { mobile: 'lg', xl: 'xmd' },
  flexWrap: 'wrap',
  justifyContent: 'center',
  gap: 'lg',
} as FlexProps;

const fillerStyles = {
  w: {
    mobile: 'full',
    xs: '22.375rem',
    sm: '16.25rem',
    md: '14.313rem',
    lg: '18.375rem',
    xl: '19.312rem',
  },
};

const mapStyles = {
  mt: { mobile: 'lg', xl: 'xmd' },
  mb: { mobile: '2.5rem', xs: '1.5rem', sm: '3.5rem' },
  h: {
    mobile: '32.5rem',
    xs: '32.5rem',
    sm: '50rem',
    md: '50rem',
    lg: '50rem',
    xl: '50rem',
  },
};

const filterPodStyles = {
  p: 'var(--chakra-space-xs) var(--chakra-space-xmd)',
  borderRadius: '2.75rem',
  backgroundColor: 'rgba(229, 242, 246, 1)',
  alignItems: 'center',
  h: '1.75rem',
  minW: 'max-content',
  gap: 'sm',
  justifyContent: 'space-between',
  mt: 'xmd',
};

const hotelsCounterStyles = {
  fontSize: 'md',
  mt: 'sm',
  fontFamily: 'body',
};

const noResultsContainerStyles = {
  flexDirection: 'column',
  alignItems: 'center',
  justifyContent: 'center',
  gap: 'lg',
  py: '4xl',
} as FlexProps;

const noResultsHeadingStyles = {
  fontWeight: 'bold',
  fontFamily: 'body',
  fontSize: '3xl',
};

const noResultsTextStyles = { fontSize: 'lg', fontWeight: 'medium' };

const filtersNotificationStyles = {
  marginTop: 'lg',
  marginLeft: 'lg',
  zIndex: '1',
  backgroundColor: 'baseWhite',
  borderColor: 'lightGrey3',
};

const mapViewFiltersNotificationWrapperStyles = {
  maxWidth: {
    mobile: '65%',
    xs: '70%',
    sm: '80%',
    md: '100%',
  },
};
