import { Box } from '@chakra-ui/react';
import { Loader } from '@googlemaps/js-api-loader';
import { useEffect, useRef } from 'react';

import theme from '../../../theme';

interface Props {
  latitude: number;
  longitude: number;
  brand: string;
  apiKey: string;
  fillColor: string;
}

export default function HotelMap({
  latitude,
  longitude,
  brand,
  apiKey,
  fillColor,
}: Readonly<Props>) {
  const hotelMapRef = useRef(null);

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

  return <Box data-testid="hotelDirections-map" h="full" ref={hotelMapRef} />;

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
