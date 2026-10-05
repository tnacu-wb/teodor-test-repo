import { Box } from '@chakra-ui/react';
import { Loader } from '@googlemaps/js-api-loader';
import {
  HeaderInformationData,
  SingleHotelAvailability,
  SRMultiSearchParamsType,
  SRPartialTranslationsType,
} from '@whitbread-eos/api';
import {
  formatCurrency,
  formatDataTestId,
  formatPriceWithDecimal,
  waitForElement,
  analytics,
} from '@whitbread-eos/utils';
import getConfig from 'next/config';
import React, { useEffect, useRef, useState } from 'react';
import ReactDOM from 'react-dom';

import { SRMapHotelCard } from '../../../index';
import initializeMap from '../../../utils/initializeMap';
import { hotelOpensSoon } from '../../utilities';
import { emptyCountryMapCoordinates } from './constants';

interface Props {
  items: SingleHotelAvailability[];
  multiSearchParams: SRMultiSearchParamsType;
  locale: string;
  partialTranslations: SRPartialTranslationsType;
  headerInformation: HeaderInformationData;
  baseDataTestId: string;
  variant?: string;
  isPricePerNightEnabled?: boolean;
}

type HotelStatus = {
  status: string;
};

const AVAILABLE_STATUS = 'available';
const SOLD_OUT_STATUS = 'soldOut';
const OPEN_SOON_STATUS = 'openSoon';

export default function MapViewBBVariant({
  items,
  multiSearchParams,
  locale,
  partialTranslations,
  headerInformation,
  baseDataTestId,
  variant,
  isPricePerNightEnabled,
}: Readonly<Props>) {
  const { publicRuntimeConfig = {} } = getConfig() || {};
  const hotelMapRef = useRef(null);
  const hotelOverlayRef = useRef(null);
  const [displayedItem, setDisplayedItem] = useState(null);

  useEffect(() => {
    const loader = new Loader({
      apiKey: publicRuntimeConfig.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY,
      version: 'weekly',
      libraries: [],
    });

    loader.load().then(() => {
      if (hotelMapRef?.current) {
        if (items.length === 0) {
          initializeEmptyCountryMap(
            emptyCountryMapCoordinates[locale].lat,
            emptyCountryMapCoordinates[locale].long,
            hotelMapRef
          );

          return <Box data-testid={formatDataTestId(baseDataTestId, 'mapView')} />;
        }
        const { centerLat, centerLong } = getCenterByHotels(items);
        const hotelMap = initializeMap(centerLat, centerLong, hotelMapRef);
        analytics.update({
          mapReference: hotelMap,
        });
        const params = {
          hotelMap,
          hotels: items,
          partialTranslations,
          headerInformation,
          multiSearchParams,
          locale,
          setDisplayedItem,
          hotelOverlayRef,
        };
        displayMarkers(params);
      }
    });
  }, [
    multiSearchParams,
    locale,
    items,
    partialTranslations,
    headerInformation,
    publicRuntimeConfig.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY,
  ]);

  return (
    <>
      {hotelOverlayRef.current &&
        displayedItem &&
        ReactDOM.createPortal(
          <SRMapHotelCard
            data={displayedItem}
            locale={locale}
            isPricePerNightEnabled={isPricePerNightEnabled}
            partialTranslations={partialTranslations}
            multiSearchParams={multiSearchParams}
            variant={variant}
          />,
          hotelOverlayRef.current
        )}
      <Box
        data-testid={formatDataTestId(baseDataTestId, 'mapView')}
        ref={hotelMapRef}
        {...mapContainerStyles}
      />
    </>
  );
}

export function getCenterByHotels(hotels: SingleHotelAvailability[]) {
  const latitudes: number[] = [];
  const longitudes: number[] = [];
  hotels.forEach((hotel) => {
    hotel?.hotelInformation?.coordinates &&
      latitudes.push(hotel.hotelInformation.coordinates.latitude);
    hotel?.hotelInformation?.coordinates &&
      longitudes.push(hotel.hotelInformation.coordinates.longitude);
  });
  const centerLat = (Math.max(...latitudes) + Math.min(...latitudes)) / 2;
  const centerLong = (Math.max(...longitudes) + Math.min(...longitudes)) / 2;

  return {
    centerLat,
    centerLong,
  };
}

function getMarkerLabel(
  item: SingleHotelAvailability,
  partialTranslations: SRPartialTranslationsType,
  multiSearchParams: SRMultiSearchParamsType,
  locale: string
) {
  const lowestRate = item?.hotelAvailability?.lowestRoomRate;
  const { status }: HotelStatus = getItemStatus(item, multiSearchParams);
  let content = '';
  if (status === AVAILABLE_STATUS && lowestRate) {
    content = formatPriceWithDecimal(
      locale,
      formatCurrency(lowestRate.currencyCode),
      lowestRate.netTotal
    );
  }

  if (status === SOLD_OUT_STATUS) {
    content = partialTranslations?.searchInformation?.content?.results?.result?.fullyBooked ?? '';
  }

  if (status === OPEN_SOON_STATUS) {
    content = partialTranslations?.searchInformation?.content?.results?.result?.openingSoon ?? '';
  }
  return content;
}

export function getItemStatus(
  item: SingleHotelAvailability,
  multiSearchParams: SRMultiSearchParamsType
): HotelStatus {
  const hotelAvailability = item.hotelAvailability;
  const lowestRate = hotelAvailability?.lowestRoomRate;
  const soldOut = !hotelAvailability?.available;
  const openingDate = item?.hotelInformation?.hotelOpeningDate ?? '';
  const isOpeningSoon = hotelOpensSoon(openingDate, multiSearchParams);

  if (lowestRate && !isOpeningSoon) {
    return {
      status: 'available',
    };
  }

  if (soldOut) {
    return {
      status: 'soldOut',
    };
  }

  if (isOpeningSoon) {
    return {
      status: 'openSoon',
    };
  }
  return { status: '' };
}

function initializeEmptyCountryMap(latitude: number, longitude: number, mapReference: any) {
  return new google.maps.Map(mapReference.current, {
    zoom: 6,
    center: { lat: latitude, lng: longitude },
    disableDefaultUI: true,
  });
}

let marker: google.maps.Marker;

export function createMarkers(
  items: SingleHotelAvailability[],
  partialTranslations: SRPartialTranslationsType,
  multiSearchParams: SRMultiSearchParamsType,
  locale: string,
  hotelMap: google.maps.Map,
  hotelOverlayRef: any,
  setDisplayedItem: any
) {
  return items.map((item) => {
    try {
      const { status } = getItemStatus(item, multiSearchParams);
      const isSoldOut = status === SOLD_OUT_STATUS ? 'soldOut' : '';
      const markerLabel = {
        text: getMarkerLabel(item, partialTranslations, multiSearchParams, locale),
        className: `mapMarker mapMarker-${item.hotelId} ${isSoldOut}`,
      };

      marker = new google.maps.Marker({
        position: {
          lat: item?.hotelInformation?.coordinates?.latitude ?? 0,
          lng: item?.hotelInformation?.coordinates?.longitude ?? 0,
        },
        map: hotelMap,
        label: markerLabel,
        icon: ` `,
      });

      attachHotelCard({
        marker,
        hotelMap,
        markerLabel,
        hotelOverlayRef,
        setDisplayedItem,
        item,
        isSoldOut,
      });

      return marker;
    } catch (e) {
      throw new Error();
    }
  });
}

function getMapBounds(markers: google.maps.Marker[]) {
  const bounds = new google.maps.LatLngBounds();
  markers.forEach((marker) => {
    const markerPosition = marker.getPosition() as google.maps.LatLng;
    bounds.extend(markerPosition);
  });
  return bounds;
}

function displayMarkers(params: {
  hotelMap: google.maps.Map;
  hotels: SingleHotelAvailability[];
  partialTranslations: SRPartialTranslationsType;
  headerInformation: HeaderInformationData;
  multiSearchParams: SRMultiSearchParamsType;
  locale: string;
  hotelOverlayRef: any;
  setDisplayedItem: any;
}) {
  const {
    hotelMap,
    hotels,
    partialTranslations,
    multiSearchParams,
    locale,
    hotelOverlayRef,
    setDisplayedItem,
  } = params;
  const markers = createMarkers(
    hotels,
    partialTranslations,
    multiSearchParams,
    locale,
    hotelMap,
    hotelOverlayRef,
    setDisplayedItem
  );
  analytics.update({
    mapMarkerReferences: markers,
  });
  if (markers.length > 1) {
    const bounds = getMapBounds(markers);
    hotelMap.fitBounds(bounds);
  }
}

let infoWindow: google.maps.InfoWindow;

interface AttachHotelCard {
  marker: google.maps.Marker;
  hotelMap: google.maps.Map;
  markerLabel: {
    className: string;
    text: string;
  };
  hotelOverlayRef: any;
  setDisplayedItem: any;
  item: SingleHotelAvailability;
  isSoldOut: string;
}

// Due to difficult way of testing marker events
/* istanbul ignore next */
export function attachHotelCard({
  marker,
  hotelMap,
  markerLabel,
  hotelOverlayRef,
  setDisplayedItem,
  item,
  isSoldOut,
}: AttachHotelCard) {
  const stringyfiedContent = `<div id="hotel-overlay"></div>`;

  infoWindow = new google.maps.InfoWindow({ content: stringyfiedContent });
  marker.addListener('mouseover', async () => {
    document.querySelectorAll(`.mapMarker`).forEach((hotel) => {
      hotel.classList.remove('activeHover');
    });
    marker.setLabel({
      ...markerLabel,
      className: `mapMarker ${isSoldOut} hoverMarker`,
    });
  });
  marker.addListener('mouseout', async () => {
    document.querySelectorAll(`.mapMarker`).forEach((hotel) => {
      hotel.classList.remove('activeHover');
    });
    marker.setLabel(markerLabel);
  });

  marker.addListener('click', async () => {
    removeActiveClass(`.mapMarker`, 'activeMarker');
    const selectedHotel = document.querySelector(`.mapMarker-${item.hotelId}`);
    selectedHotel?.classList.add('activeMarker');

    marker.setLabel({
      ...markerLabel,
      className: `mapMarker mapMarker-${item.hotelId} activeMarker`,
    });

    marker.addListener('mouseover', async () => {
      removeActiveClass(`.mapMarker`, 'activeMarker');
      selectedHotel?.classList.add('activeHover');
    });
    marker.addListener('mouseout', async () => {
      removeActiveClass(`.mapMarker`, 'activeMarker');

      marker.setLabel({
        ...markerLabel,
        className: `mapMarker activeMarker`,
      });
    });
    infoWindow.open({ map: hotelMap, anchor: marker });
    await waitForElement('#hotel-overlay');
    const el = document.getElementById('hotel-overlay');
    if (el) {
      hotelOverlayRef.current = el;
      setDisplayedItem(item);
      infoWindow.addListener('closeclick', () => {
        removeActiveClass(`.mapMarker`, 'activeMarker');
        infoWindow.close();
        marker.setLabel(markerLabel);
      });
      google.maps.event.addListener(hotelMap, 'click', () => {
        removeActiveClass(`.mapMarker`, 'activeMarker');
        infoWindow.close();
        marker.setLabel(markerLabel);
      });
      google.maps.event.addListener(hotelMap, 'center_changed', () => {
        removeActiveClass(`.mapMarker`, 'activeMarker');
        marker.setLabel(markerLabel);
      });
      google.maps.event.addListener(hotelMap, 'drag', () => {
        removeActiveClass(`.mapMarker`, 'activeMarker');
        marker.setLabel(markerLabel);
        infoWindow.close();
      });
    }
  });
}

export function removeActiveClass(selector: string, className: string) {
  const allHotels = document.querySelectorAll(selector);
  return allHotels.forEach((hotel) => {
    hotel.classList.remove(className);
  });
}

const mapContainerStyles = {
  height: '100%',
};
