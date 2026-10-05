import type { FlexProps } from '@chakra-ui/react';
import { Box, Flex } from '@chakra-ui/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import {
  getSuggestions,
  PageName,
  FS_ENABLE_META_ARRIVAL_DAY_KEYWORD_PI,
  FT_PI_SORT_ORDER_DROPDOWN,
} from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import { SEO as Seo } from '@whitbread-eos/molecules';
import {
  getFallbackSearchPlace,
  getSearchParams,
  PISearchContainer as Search,
  SearchResultsPIVariant,
} from '@whitbread-eos/organisms';
import {
  useCustomLocale,
  getDefaultRooms,
  useFeatureSwitch,
  useFeatureToggle,
  type PromotionsInformation,
} from '@whitbread-eos/utils';
import { NextRouter } from 'next/router';
import { useEffect, useState } from 'react';

interface Props {
  router: NextRouter;
  queryClient: QueryClient;
  fallbackSearchPlace?: { PLACEID?: string; 'searchModel.searchTerm'?: string };
  promotionBannerData?: PromotionsInformation;
}

export default function SearchPagePI({
  router,
  queryClient,
  fallbackSearchPlace,
  promotionBannerData,
}: Readonly<Props>) {
  const [showNoHotelsWarning, setShowNoHotelsWarning] = useState(false);

  // use helm (server side) feature switch to ensure toggle ready before page load
  const isMetaArrivalDayKeywordFlagEnabled = useFeatureSwitch({
    featureSwitchKey: FS_ENABLE_META_ARRIVAL_DAY_KEYWORD_PI,
    fallbackValue: false,
  });
  const { [FT_PI_SORT_ORDER_DROPDOWN]: isPiSortOrderDropdownEnabled } = useFeatureToggle();

  const multiSearchParams = getSearchParams(router.query, isMetaArrivalDayKeywordFlagEnabled, {
    isPiSortOrderDropdownEnabled,
  });
  const { placeId, coordinates, location, corpId } = multiSearchParams;
  const { language, country } = useCustomLocale();

  useEffect(() => {
    if (!placeId && !coordinates) {
      if (fallbackSearchPlace) {
        const newQuery = { ...router.query, ...fallbackSearchPlace };
        router.push(
          {
            pathname: `${country}/${language}${router.pathname}.html`,
            query: newQuery,
            search: undefined,
          },
          undefined,
          { shallow: true }
        );
      } else {
        const fetchSuggestions = async () => {
          try {
            const response = await getSuggestions(location);
            const newQuery = { ...router.query, ...getFallbackSearchPlace(location, response) };
            router.push(
              {
                pathname: `${country}/${language}${router.pathname}.html`,
                query: newQuery,
                search: undefined,
              },
              undefined,
              { shallow: true }
            );
          } catch (error) {
            console.log(error);
          }
        };
        fetchSuggestions();
      }
    }
  }, [placeId, coordinates, location, fallbackSearchPlace]);

  const defaultRooms = getDefaultRooms(router.query);

  if (!placeId && !coordinates && fallbackSearchPlace?.PLACEID) {
    multiSearchParams.placeId = fallbackSearchPlace.PLACEID;
  }

  const {
    arrivalDay: ARRdd,
    arrivalMonth: ARRmm,
    arrivalYear: ARRyyyy,
    numberOfNights: NIGHTS,
    rooms: ROOMS,
  } = multiSearchParams;

  const noHotelsWarningHandle = (value: boolean) => {
    setShowNoHotelsWarning(value);
  };

  const isMetaSearch = 'WEB' !== (router.query?.BOOKINGCHANNEL as string);

  const PROMOID = router.query?.PROMOID ?? '';

  return (
    <QueryClientProvider client={queryClient}>
      <Flex {...containerStyles}>
        <Seo page={PageName.SRP} displayMeta noIndexNoFollow={!!multiSearchParams.filters} />
        {/* eslint-disable-next-line @typescript-eslint/ban-ts-comment */}
        {/* @ts-ignore */}
        <ErrorBoundary noContentBoundary={true}>
          <Box {...searchContainerStyles}>
            <Search
              queryClient={queryClient}
              isSummaryActive={!isMetaSearch}
              defaultLocation={location}
              defaultRooms={defaultRooms}
              ARRdd={Number(ARRdd)}
              ARRmm={Number(ARRmm)}
              ARRyyyy={Number(ARRyyyy)}
              NIGHTS={Number(NIGHTS)}
              ROOMS={Number(ROOMS)}
              CORPID={corpId}
              PROMOID={PROMOID as string}
              showNoHotelsWarning={showNoHotelsWarning}
            />
          </Box>
        </ErrorBoundary>
        {/* eslint-disable-next-line @typescript-eslint/ban-ts-comment */}
        {/* @ts-ignore */}
        <ErrorBoundary>
          <SearchResultsPIVariant
            queryClient={queryClient}
            multiSearchParams={multiSearchParams}
            onNoHotelsWarning={noHotelsWarningHandle}
            promotionBannerData={promotionBannerData}
          />
        </ErrorBoundary>
      </Flex>
    </QueryClientProvider>
  );
}

const searchContainerStyles = {
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
  paddingTop: 'var(--chakra-space-sm)',
  mx: 'auto',
  width: '100%',
};
const containerStyles = {
  height: '100%',
  display: 'flex',
  flexDirection: 'column',
} as FlexProps;
