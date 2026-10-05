import { Box, Flex, Text, TextProps } from '@chakra-ui/react';
import { Loader } from '@googlemaps/js-api-loader';
import type { AnalyticsDataSearchResult, Coordinates } from '@whitbread-eos/api';
import { FT_PI_BB_SHOW_GOOGLE_STATIC_MAP } from '@whitbread-eos/api';
import { LoadingSpinner, Section, theme } from '@whitbread-eos/atoms';
import {
  DirectionsInformation,
  HotelLocationInformation,
  TransportInformation,
} from '@whitbread-eos/molecules';
import {
  analytics,
  useElementVisited,
  useFeatureToggle,
  useSemanticTypography,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import getConfig from 'next/config';
import { useEffect, useRef, useState } from 'react';

import StaticMap from './StaticMap';

export interface Props {
  isLoading: boolean;
  isError: boolean;
  error: unknown;
  data: {
    coordinates: Coordinates;
    brand: string;
  };
}

export function getMarkerIconByHotelType(brand: string) {
  const PIHotelMarker = {
    ...defaultPinStyles,
    fillColor: theme.colors.btnSecondaryEnabled,
    anchor: new google.maps.Point(22, 72),
  };

  const HUBHotelMarker = {
    ...defaultPinStyles,
    fillColor: theme.colors.hubPrimary,
    anchor: new google.maps.Point(22, 72),
  };

  const ZIPHotelMarker = {
    ...defaultPinStyles,
    fillColor: theme.colors.zipPrimary,
    anchor: new google.maps.Point(22, 72),
  };

  if (brand?.toLowerCase() === 'hub') {
    return HUBHotelMarker;
  }

  if (brand?.toLowerCase() === 'zip') {
    return ZIPHotelMarker;
  }

  return PIHotelMarker;
}

export function LocationInfoSection() {
  return (
    <>
      <HotelLocationInformation />
      <DirectionsInformation />
      <TransportInformation />
    </>
  );
}

export function LocationHeading() {
  const { t } = useTranslation(['common']);
  const getTypographyProps = useSemanticTypography();
  const headingTypographyProps = getTypographyProps(
    headingLegacyTypography,
    headingSemanticTypography
  );

  return (
    <Text
      as="h3"
      data-testid="hotel-location-title"
      {...headingLayoutStyles}
      {...headingTypographyProps}
    >
      {t('hoteldetails.location')}
    </Text>
  );
}

export default function LocationComponent({ isLoading, isError, error, data }: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const hotelMapRef = useRef(null);
  const [loadMap, setLoadMap] = useState(false);
  const { [FT_PI_BB_SHOW_GOOGLE_STATIC_MAP]: isStaticMapEnabled } = useFeatureToggle();
  const { publicRuntimeConfig = {} } = getConfig() || {};

  const latitude = data?.coordinates?.latitude;
  const longitude = data?.coordinates?.longitude;
  const brand = data?.brand;
  const hasElementVisited = useElementVisited('hotel-location-section');
  useEffect(() => {
    if (!hasElementVisited) {
      return;
    }
    if (isStaticMapEnabled && !loadMap) return;
    analytics.update({
      analyticsDataSearchResult: {
        ...(window?.analyticsData?.analyticsDataSearchResult as AnalyticsDataSearchResult),
        mapLoaded: true,
      },
    });

    const loader = new Loader({
      apiKey: publicRuntimeConfig.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY,
      version: 'weekly',
      libraries: [],
    });

    loader.load().then(() => {
      window.onunload = () => {
        sessionStorage.removeItem('scroller');
      };
      window.onscroll = () => {
        sessionStorage.scroller = document.documentElement.scrollTop;
      };

      if (hotelMapRef?.current && latitude && longitude) {
        const google = window.google;

        // Display Google Map
        const hotelMap = new google.maps.Map(hotelMapRef.current, {
          center: { lat: latitude, lng: longitude },
          zoom: 13,
        });

        // Display map marker for hotel location
        // prettier-ignore
        new google.maps.Marker({ // NOSONAR
          position: { lat: latitude, lng: longitude },
          map: hotelMap,
          icon: getMarkerIconByHotelType(brand),
        });

        const panorama = hotelMap.getStreetView();
        google.maps.event.addListener(panorama, 'closeclick', () => {
          panorama.setVisible(false);
          window.scrollTo(0, sessionStorage.scroller);
        });
      }
    });
  }, [
    brand,
    latitude,
    longitude,
    publicRuntimeConfig?.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY,
    hasElementVisited,
    loadMap,
    isStaticMapEnabled,
  ]);

  if (isLoading) {
    return <LoadingSpinner loadingText={t('searchresults.list.hotel.loading')} />;
  }

  if (isError) {
    return <Text>{(error as Error).message}</Text>;
  }

  if (!data) {
    return null;
  }

  const handleLoadMap = () => {
    if (!loadMap) {
      setLoadMap(true);
    }
  };

  return (
    <Section dataTestId="hdp_location">
      <LocationHeading />
      <Flex
        direction={{ base: 'column', md: 'row' }}
        id="hotel-location-section"
        data-testid="hotel-location-section"
      >
        {isStaticMapEnabled && hasElementVisited ? (
          <Box
            {...mapBoxStyles}
            position="relative"
            cursor="pointer"
            onClick={handleLoadMap}
            onTouchStart={handleLoadMap}
          >
            {loadMap ? (
              <Box data-testid="hotel-google-map" ref={hotelMapRef} {...mapContainerStyles}>
                <LoadingSpinner loadingText="Loading..." />
              </Box>
            ) : (
              <StaticMap
                latitude={latitude}
                longitude={longitude}
                brand={brand}
                apiKey={publicRuntimeConfig.NEXT_PUBLIC_GOOGLE_STATIC_MAPS_API_KEY}
              />
            )}
          </Box>
        ) : (
          hasElementVisited && (
            <Box {...mapBoxStyles}>
              <Box data-testid="hotel-google-map" h="full" ref={hotelMapRef} />
            </Box>
          )
        )}
        <Box
          ml={{ base: 0, md: 4 }}
          w={{ base: 'full', md: '40%', xl: '30%' }}
          data-testid="hotel-location-information-section"
        >
          <Flex direction="column" data-testid="hotel-location-information-column">
            <LocationInfoSection />
          </Flex>
        </Box>
      </Flex>
    </Section>
  );
}

export const MapPinPath =
  'M45.0585 21C45.0585 32.598 22.8232 63.5 22.8232 63.5C22.8232 63.5 0.587891 32.598 0.587891 21C0.587891 9.40202 10.543 0 22.8232 0C35.1034 0 45.0585 9.40202 45.0585 21Z';

const defaultPinStyles = {
  path: MapPinPath,
  fillOpacity: 1,
  strokeWeight: 2,
  strokeColor: theme.colors.baseWhite,
  rotation: 0,
  scale: 0.5,
};

export const mapBoxStyles = {
  h: { base: 80, sm: '21.188rem', md: '26.5rem', xl: '28rem' },
  w: { base: 'full', md: '66%', xl: '68%' },
  mb: 4,
};

const headingLayoutStyles = {
  mb: '4',
  mt: '3xl',
};

const headingLegacyTypography = {
  fontWeight: 'semibold',
  fontSize: { base: 'xl', sm: '2xl' },
  lineHeight: { base: '3', sm: '4' },
};

const headingSemanticTypography = {
  textStyle: {
    base: 'heading-s',
    md: 'heading-m',
  } as unknown as TextProps['textStyle'],
};

const mapContainerStyles = {
  w: '100%',
  h: '100%',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
};
