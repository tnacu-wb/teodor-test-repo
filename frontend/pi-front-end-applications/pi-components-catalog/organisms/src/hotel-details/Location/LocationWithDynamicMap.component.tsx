import { Box, Flex, Text } from '@chakra-ui/react';
import { Loader } from '@googlemaps/js-api-loader';
import { AnalyticsDataSearchResult } from '@whitbread-eos/api';
import { LoadingSpinner, Section } from '@whitbread-eos/atoms';
import { analytics, useElementVisited, useScreenSize } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import getConfig from 'next/config';
import { useEffect, useRef, useState } from 'react';

import {
  getMarkerIconByHotelType,
  mapBoxStyles,
  Props,
  LocationInfoSection,
  LocationHeading,
} from './Location.component';

export default function LocationWithDynamicMap({
  isLoading,
  isError,
  error,
  data,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const hotelMapRef = useRef<HTMLDivElement | null>(null);
  const markerRef = useRef<google.maps.Marker | null>(null);

  const [showMap, setShowMap] = useState(false);
  const [isMapLoading, setIsMapLoading] = useState(false);

  const { isLessThanMd } = useScreenSize();
  const isMobile = isLessThanMd;

  const { publicRuntimeConfig = {} } = getConfig() || {};

  const latitude = data?.coordinates?.latitude;
  const longitude = data?.coordinates?.longitude;
  const brand = data?.brand;

  const hasElementVisited = useElementVisited('hotel-location-section');

  useEffect(() => {
    if (!hasElementVisited) return;
    if (!showMap) return;

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
      if (hotelMapRef?.current && latitude && longitude) {
        const google = window.google;

        const hotelMap = new google.maps.Map(hotelMapRef.current, {
          center: { lat: latitude, lng: longitude },
          zoom: 13,
        });

        markerRef.current = new google.maps.Marker({
          position: { lat: latitude, lng: longitude },
          map: hotelMap,
          icon: getMarkerIconByHotelType(brand),
        });
      }

      setIsMapLoading(false);
    });
  }, [
    brand,
    latitude,
    longitude,
    publicRuntimeConfig?.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY,
    hasElementVisited,
    showMap,
  ]);

  if (isLoading) {
    return <LoadingSpinner loadingText={t('searchresults.list.hotel.loading')} />;
  }

  if (isError) {
    return <Text>{(error as Error).message}</Text>;
  }

  if (!data) return null;

  const viewMapTitle = t('searchresults.filter.hideMap');
  const hideMapTitle = t('hoteldetails.viewmap');

  return (
    <Section dataTestId="hdp_location">
      <LocationHeading />
      <Flex direction={{ base: 'column', md: 'row' }} id="hotel-location-section">
        <Box {...infoSectionStyles}>
          <Flex direction="column">
            <Text
              {...linkTextStyles}
              onClick={() => {
                if (!showMap) setIsMapLoading(true);
                setShowMap((prev) => !prev);
              }}
            >
              {showMap ? viewMapTitle : hideMapTitle}
            </Text>

            {isMobile && hasElementVisited && showMap && (
              <Box {...mapBoxStyles} position="relative" mt={2}>
                <Box ref={hotelMapRef} h="full" />

                {isMapLoading && (
                  <Flex
                    position="absolute"
                    inset="0"
                    align="center"
                    justify="center"
                    bg="whiteAlpha.700"
                  >
                    <LoadingSpinner loadingText={t('booking.loading')} />
                  </Flex>
                )}
              </Box>
            )}

            <LocationInfoSection />
          </Flex>
        </Box>

        {!isMobile && hasElementVisited && showMap && (
          <Box {...mapBoxStyles} position="relative">
            <Box ref={hotelMapRef} h="full" />

            {isMapLoading && (
              <Flex
                position="absolute"
                inset="0"
                align="center"
                justify="center"
                bg="whiteAlpha.700"
              >
                <LoadingSpinner loadingText={t('booking.loading')} />
              </Flex>
            )}
          </Box>
        )}
      </Flex>
    </Section>
  );
}

const linkTextStyles = {
  cursor: 'pointer',
  fontWeight: 'bold',
  color: 'blue.500',
  textDecoration: 'underline',
  mb: '0.5rem',
  display: 'inline-block',
  _hover: {
    color: 'blue.600',
  },
};

const infoSectionStyles = {
  mr: { base: 0, md: 4 },
  w: { base: 'full', md: '40%', xl: '30%' },
};
