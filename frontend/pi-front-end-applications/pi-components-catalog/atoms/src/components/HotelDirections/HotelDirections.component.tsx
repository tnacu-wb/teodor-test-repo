import { Box, Flex, Text } from '@chakra-ui/react';
import { Loader } from '@googlemaps/js-api-loader';
import { renderSanitizedHtml, useSemanticTypography } from '@whitbread-eos/utils';
import React, { useEffect, useRef } from 'react';

import theme from '../../theme';
import { HotelMap } from './components';

interface HotelCoordinates {
  latitude: number;
  longitude: number;
}

interface HotelDirectionData {
  coordinates: HotelCoordinates;
  directions: string;
  brand: string;
}

interface HotelInformationData {
  hotelInformation: HotelDirectionData;
}

interface Props {
  data: HotelInformationData;
  apiKey: string;
  t: (x: string, y?: { [key: string]: string }) => string;
}

export default function HotelDirections({ data, apiKey, t }: Readonly<Props>) {
  const hotelMapRef = useRef(null);
  const getTypographyProps = useSemanticTypography();

  const { coordinates, directions, brand } = data?.hotelInformation ?? '';
  const latitude = coordinates?.latitude;
  const longitude = coordinates?.longitude;

  const fillColor = getFillColorMarker();

  useEffect(() => {
    const loader = new Loader({
      apiKey,
      version: 'weekly',
      libraries: [],
    });

    loader.load().then(() => {
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
          icon: getMarkerIconByBrand(),
        });
      }
    });
  }, [brand, latitude, longitude]);

  return (
    <Box mb="2xl">
      <Flex {...hotelDirectionsWrapperStyle} sx={{ '@media print': { display: 'none' } }}>
        <Flex direction="column" w="full">
          <Text
            {...hotelDirectionsHeadingLayoutStyles}
            {...getTypographyProps(
              hotelDirectionsHeadingLegacyTypography,
              hotelDirectionsHeadingSemanticTypography
            )}
            data-testid="hotelDirections-label"
          >
            {t('booking.confirmation.hotelDirections')}
          </Text>
          <Box {...mapBoxStyles} sx={{ '@media print': { display: 'none' } }}>
            <HotelMap
              latitude={latitude}
              longitude={longitude}
              brand={brand}
              apiKey={apiKey}
              fillColor={fillColor}
            />
          </Box>
          <Box
            {...hotelDirectionsLayoutStyles}
            {...getTypographyProps(
              hotelDirectionsLegacyTypography,
              hotelDirectionsSemanticTypography
            )}
            data-testid="hotelDirections-directions"
          >
            {renderSanitizedHtml(directions)}
          </Box>
        </Flex>
      </Flex>
      <Box display="none" sx={{ '@media print': { display: 'block' } }}>
        <Text fontSize="md" fontWeight="semibold">
          {t('booking.confirmation.hotelDirections')}
        </Text>
        <Box {...mapBoxStyles} display="flex">
          <img
            src={`https://maps.googleapis.com/maps/api/staticmap?center=${latitude},${longitude}&zoom=13&size=814x400&markers=color:0x${fillColor.substring(
              1
            )}%7C${latitude},${longitude}&scale=2&key=${apiKey}`}
            alt={'Google Maps'}
          />
        </Box>
        <Box
          {...hotelDirectionsLayoutStyles}
          {...getTypographyProps(
            hotelDirectionsLegacyTypography,
            hotelDirectionsSemanticTypography
          )}
        >
          {renderSanitizedHtml(directions)}
        </Box>
      </Box>
    </Box>
  );

  function getFillColorMarker(): string {
    let fillColor = theme.colors.btnSecondaryEnabled;
    if (brand?.toLowerCase() === 'hub') {
      fillColor = theme.colors.hubPrimary;
    }

    if (brand?.toLowerCase() === 'zip') {
      fillColor = theme.colors.zipPrimary;
    }
    return fillColor;
  }

  function getMarkerIconByBrand() {
    return {
      ...defaultPinStyles,
      fillColor,
      anchor: new google.maps.Point(15, 30),
    };
  }
}

const MapPinPath =
  'M45.0585 21C45.0585 32.598 22.8232 63.5 22.8232 63.5C22.8232 63.5 0.587891 32.598 0.587891 21C0.587891 9.40202 10.543 0 22.8232 0C35.1034 0 45.0585 9.40202 45.0585 21Z';

const defaultPinStyles = {
  path: MapPinPath,
  fillOpacity: 1,
  strokeWeight: 2,
  strokeColor: theme.colors.baseWhite,
  rotation: 0,
  scale: 0.5,
};

const mapBoxStyles = {
  h: { xl: '25rem', mobile: '9.75rem' },
  borderRadius: '8px',
  mb: 'lg',
};

const hotelDirectionsHeadingLayoutStyles = {
  mb: 'lg',
};

const hotelDirectionsHeadingLegacyTypography = {
  fontWeight: 'semibold',
  fontSize: '2xl',
  lineHeight: '4',
};

const hotelDirectionsHeadingSemanticTypography = {
  textStyle: 'heading-m',
};

const hotelDirectionsLayoutStyles = {
  color: 'darkGrey1',
};

const hotelDirectionsLegacyTypography = {
  fontWeight: 'normal',
  fontSize: 'md',
};

const hotelDirectionsSemanticTypography = {
  textStyle: 'body-m-regular',
};

const hotelDirectionsWrapperStyle = {
  w: 'full',
  border: '1px solid var(--chakra-colors-lightGrey1)',
  borderRadius: '3px',
  p: 'lg',
};
