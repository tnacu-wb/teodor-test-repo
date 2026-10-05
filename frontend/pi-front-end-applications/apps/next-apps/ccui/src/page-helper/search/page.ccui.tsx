import { Box, Flex, FlexProps } from '@chakra-ui/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import {
  CCUISearchContainer as Search,
  getSearchParams,
  SearchResultsCCUIVariant,
} from '@whitbread-eos/organisms';
import { getDefaultRooms, type PromotionsInformation } from '@whitbread-eos/utils';
import { NextRouter } from 'next/router';
import { useState } from 'react';

interface Props {
  router: NextRouter;
  queryClient: QueryClient;
  promotionBannerData?: PromotionsInformation;
}

export default function SearchPageCCUI({
  router,
  queryClient,
  promotionBannerData,
}: Readonly<Props>) {
  const multiSearchParams = getSearchParams(router.query);
  const [isSearchError, setIsSearchError] = useState<boolean>(true);
  const defaultRooms = getDefaultRooms(router.query);
  const { reservationId } = router.query;
  const {
    location,
    arrivalDay: ARRdd,
    arrivalMonth: ARRmm,
    arrivalYear: ARRyyyy,
    numberOfNights: NIGHTS,
    rooms: ROOMS,
    corpId: CORPID,
    compId: COMPID,
  } = multiSearchParams;
  const PROMOID = router.query?.PROMOID ?? '';
  return (
    <QueryClientProvider client={queryClient}>
      <Flex {...containerStyles}>
        {/* eslint-disable-next-line @typescript-eslint/ban-ts-comment */}
        {/* @ts-ignore */}
        <ErrorBoundary noContentBoundary={true}>
          <Box {...searchContainerStyles}>
            <Search
              queryClient={queryClient}
              isSummaryActive
              defaultLocation={location}
              defaultRooms={defaultRooms}
              ARRdd={Number(ARRdd)}
              ARRmm={Number(ARRmm)}
              ARRyyyy={Number(ARRyyyy)}
              NIGHTS={Number(NIGHTS)}
              ROOMS={Number(ROOMS)}
              CORPID={CORPID}
              PROMOID={PROMOID as string}
              COMPID={COMPID}
              setIsSearchError={setIsSearchError}
              prevReservationId={reservationId ? String(reservationId) : undefined}
            />
          </Box>
        </ErrorBoundary>
        {/* eslint-disable-next-line @typescript-eslint/ban-ts-comment */}
        {/* @ts-ignore */}
        <ErrorBoundary>
          <SearchResultsCCUIVariant
            multiSearchParams={multiSearchParams}
            queryClient={queryClient}
            isSearchError={isSearchError}
            promotionBannerData={promotionBannerData}
          />
        </ErrorBoundary>
      </Flex>
    </QueryClientProvider>
  );
}
const containerStyles = {
  height: '100%',
  display: 'flex',
  flexDirection: 'column',
  mt: 'xl',
} as FlexProps;

const searchContainerStyles = {
  maxWidth: {
    lg: 'var(--chakra-space-breakpoint-lg)',
    xl: 'var(--chakra-space-breakpoint-xl)',
  },
  px: {
    lg: '1.75rem',
    xl: '4.125rem',
  },
  mx: 'auto',
  width: '100%',
};
