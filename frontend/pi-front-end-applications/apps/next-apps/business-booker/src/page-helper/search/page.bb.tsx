import type { FlexProps } from '@chakra-ui/react';
import { Box, Flex } from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import {
  BUSINESS_BOOKER_USER_ROLES,
  InnBusinessServerSideProps,
  PageName,
  FT_BB_SORT_ORDER_DROPDOWN,
} from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import { SEO as Seo } from '@whitbread-eos/molecules';
import {
  BBSearchContainer as Search,
  getSearchParams,
  SearchResultsBBVariant,
} from '@whitbread-eos/organisms';
import {
  useCustomLocale,
  getDefaultRooms,
  useFeatureToggle,
  type PromotionsInformation,
} from '@whitbread-eos/utils';
import { NextRouter } from 'next/router';
import { useEffect, useState } from 'react';

interface Props {
  router: NextRouter;
  queryClient: QueryClient;
  variant: string;
  accessLevel: string;
  innBusiness?: InnBusinessServerSideProps;
  fallbackSearchPlace?: { PLACEID?: string; 'searchModel.searchTerm'?: string };
  promotionBannerData?: PromotionsInformation;
}

export default function SearchPageBB({
  router,
  queryClient,
  variant,
  accessLevel,
  innBusiness,
  fallbackSearchPlace,
  promotionBannerData,
}: Readonly<Props>) {
  const { country, language } = useCustomLocale();
  const [showNoHotelsWarning, setShowNoHotelsWarning] = useState(false);
  const [stayerRole, setStayerRole] = useState(false);
  const [loggedIn, setLoggedIn] = useState(false);

  const { [FT_BB_SORT_ORDER_DROPDOWN]: isBbSortOrderDropdownEnabled } = useFeatureToggle();

  const multiSearchParams = getSearchParams(router.query, false, {
    isBbSortOrderDropdownEnabled,
  });
  const { placeId, coordinates } = multiSearchParams;

  useEffect(() => {
    setStayerRole(([BUSINESS_BOOKER_USER_ROLES.STAYER] as string[]).includes(accessLevel));
    setLoggedIn(!!accessLevel);
  }, [accessLevel]);

  useEffect(() => {
    if (stayerRole && loggedIn) {
      router?.push(`/${country}/${language}/business-booker`);
    }
  }, [stayerRole, accessLevel]);

  const defaultRooms = getDefaultRooms(router.query);
  const {
    location,
    arrivalDay: ARRdd,
    arrivalMonth: ARRmm,
    arrivalYear: ARRyyyy,
    numberOfNights: NIGHTS,
    rooms: ROOMS,
  } = multiSearchParams;

  if (!placeId && !coordinates && fallbackSearchPlace?.PLACEID) {
    multiSearchParams.placeId = fallbackSearchPlace.PLACEID;
  }

  const noHotelsWarningHandle = (value: boolean) => {
    setShowNoHotelsWarning(value);
  };

  const promoId = router?.query?.PROMOID ?? '';

  return (
    <Flex {...containerStyles}>
      {!stayerRole && loggedIn && !innBusiness && (
        <>
          <Seo page={PageName.SRP} />
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
                showNoHotelsWarning={showNoHotelsWarning}
                variant={variant}
                PROMOID={promoId as string}
              />
            </Box>
          </ErrorBoundary>
        </>
      )}
      <ErrorBoundary>
        {!stayerRole && loggedIn && (
          <SearchResultsBBVariant
            queryClient={queryClient}
            multiSearchParams={multiSearchParams}
            onNoHotelsWarning={noHotelsWarningHandle}
            variant={variant}
            innBusiness={!!innBusiness}
            promotionBannerData={promotionBannerData}
          />
        )}
      </ErrorBoundary>
    </Flex>
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
