//libraries
import { VStack, Box, BoxProps } from '@chakra-ui/react';
import { useQueryClient } from '@tanstack/react-query';
import { GET_LOCATIONS_DETAILS, SITE_LEISURE, PI_FAVICON } from '@whitbread-eos/api';
import { Container, ErrorBoundary } from '@whitbread-eos/atoms';
import {
  useAppDataDispatch,
  analytics,
  formatAssetsUrl,
  useQueryRequestRestaurants,
} from '@whitbread-eos/utils';
import { getCommonParams } from '@whitbread-eos/utils/restaurants';
import dynamic from 'next/dynamic';
import Head from 'next/head';
import { useEffect } from 'react';

import useSetOrientation from '~hooks/use-orientation';
import useSetScreenSize from '~hooks/use-screensize';

const Header = dynamic(
  async () => {
    const { Header } = await import('@whitbread-eos/organisms');
    return { default: Header };
  },
  {
    ssr: false,
  }
);

const Footer = dynamic(
  () => import('@whitbread-eos/organisms').then((mod) => ({ default: mod.FooterWrapper })),
  { ssr: false }
);

interface Locations {
  path: string;
  title: string;
  id: number;
  bookingHeroImage: string;
  bookingHeroBackgroundImage: string;
}
export interface selectedLocationDataType {
  locations: Locations[];
}

function RestaurantLayout({ children }: Readonly<{ children: React.ReactNode | any }>) {
  const { restaurantBrandName, restaurantBrandNameForAemApi, subLocationName, locationName } =
    children?.props?.children?.props ?? {};

  const queryClient = useQueryClient();

  useSetScreenSize();
  useSetOrientation();
  const dispatch = useAppDataDispatch();

  useEffect(() => {
    dispatch({ type: 'changeRestaurantName', payload: restaurantBrandName });
  }, [dispatch, restaurantBrandName]);

  const commonParams = getCommonParams(restaurantBrandNameForAemApi, locationName, subLocationName);

  const { data: locationData } = useQueryRequestRestaurants(
    ['getLocationDetails', ...Object.values(commonParams)],
    GET_LOCATIONS_DETAILS,
    commonParams
  );

  const { locations } = (locationData as selectedLocationDataType) || {};
  const {
    title = '',
    id = '',
    bookingHeroImage = '',
    bookingHeroBackgroundImage = '',
  } = locations ? locations[0] : {};

  useEffect(() => {
    const currentData = window?.analyticsData ?? {};
    const restaurantsData = window?.analyticsData?.restaurants;
    if (locationData && id) {
      analytics.update({
        ...currentData,
        restaurants: { ...restaurantsData, restaurantID: id },
      });
    }
  }, [id, locationData]);

  return (
    <VStack minH="100vh" gap={0} className="restaurant-page">
      <Head>
        <title>Book a table at {title}</title>
        <link rel="icon" type="image/x-icon" href={formatAssetsUrl(PI_FAVICON)} />{' '}
      </Head>
      <ErrorBoundary isHeaderBoundary={true}>
        <Header variant="default" queryClient={queryClient} />
      </ErrorBoundary>
      <Box bgImage={`url('${formatAssetsUrl(bookingHeroBackgroundImage)}')`} {...mainBox}>
        <Box backgroundImage={`url('${formatAssetsUrl(bookingHeroImage)}')`} {...boxWrapper} />
        <Container>{children}</Container>
      </Box>
      <ErrorBoundary isFooterBoundary={true}>
        <Box w="full">
          <Container>
            <Footer isPremierInn={true} site={SITE_LEISURE} />
          </Container>
        </Box>
      </ErrorBoundary>
    </VStack>
  );
}

const mainBox = {
  w: 'full',
  flex: '1',
  as: 'main',
  backgroundRepeat: 'repeat',
} as BoxProps;

const boxWrapper = {
  height: '420px',
  backgroundRepeat: 'no-repeat',
  backgroundSize: 'cover',
  mb: '-300px',
};

export default RestaurantLayout;
