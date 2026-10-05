import { Box } from '@chakra-ui/react';
import { Loader } from '@googlemaps/js-api-loader';
import { DlpAnalytics, HotelBrand, HotelInformationOptional } from '@whitbread-eos/api';
import { formatAssetsUrl, formatDataTestId, waitForElement, analytics } from '@whitbread-eos/utils';
import getConfig from 'next/config';
import React, { useEffect, useRef, useState } from 'react';
import ReactDOM from 'react-dom';

import initializeMap from '../../utils/initializeMap';
import DLPMapHotelCard from '../MapHotelCard';

interface Props {
  items: HotelInformationOptional[];
  baseDataTestId: string;
  latitude: number;
  longitude: number;
  icons: Record<string, string>;
  hideHotelDistance: boolean;
}

export default function MapViewDLPVariant({
  items,
  icons,
  baseDataTestId,
  latitude,
  longitude,
  hideHotelDistance,
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
        const hotelMap = initializeMap(
          latitude,
          longitude,
          hotelMapRef,
          publicRuntimeConfig.NEXT_PUBLIC_GOOGLE_MAP_ID_DLP
        );
        displayMarkers(
          hotelMap,
          items,
          icons,
          latitude,
          longitude,
          hotelOverlayRef,
          setDisplayedItem
        );
      }
    });
  }, [
    items,
    baseDataTestId,
    latitude,
    longitude,
    icons,
    publicRuntimeConfig.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY,
  ]);

  return (
    <>
      {hotelOverlayRef.current &&
        displayedItem &&
        ReactDOM.createPortal(
          <DLPMapHotelCard data={displayedItem} hideHotelDistance={hideHotelDistance} />,
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

export async function createMarkers(
  items: HotelInformationOptional[],
  hotelMap: google.maps.Map,
  icons: Record<string, string>,
  hotelOverlayRef: any,
  setDisplayedItem: any
) {
  const { AdvancedMarkerElement } = (await google.maps.importLibrary(
    'marker'
  )) as google.maps.MarkerLibrary;

  const markers = items.map((item) => {
    const pin = document.createElement('img');
    const selected = document.createElement('img');
    let iconKey: keyof typeof icons;

    switch (item.brand) {
      case HotelBrand.ZIP:
        iconKey = 'icon.pin-zip';
        break;

      case HotelBrand.HUB:
        iconKey = 'icon.pin-hub';
        break;

      default:
        iconKey = 'icon.pin-default';
        break;
    }

    const src = formatAssetsUrl(icons[iconKey]);
    pin.src = src;
    pin.dataset.originalSrc = src;
    selected.src = formatAssetsUrl(icons['icon.pin-selected']);
    selected.dataset.originalSrc = src;

    const marker = new AdvancedMarkerElement({
      position: {
        lat: item.coordinates?.latitude ?? 0,
        lng: item.coordinates?.longitude ?? 0,
      },
      map: hotelMap,
      content: pin,
    });

    (marker as any).selectedContent = selected;
    (marker as any).defaultContent = pin;
    (marker as any).hotelBrandLabel = item.brand;
    (marker as any).hotelDistanceFromSearch = (item.distanceFromReference ?? 0).toFixed(2);

    attachHotelCard({
      marker,
      hotelMap,
      hotelOverlayRef,
      setDisplayedItem,
      item,
      icons,
    });

    return marker;
  });

  markers.forEach((marker) =>
    marker.addListener('click', () => {
      markers.forEach((marker) => (marker.content = (marker as any).defaultContent));
      marker.content = (marker as any).selectedContent;
      analytics.update({
        dlp: {
          ...window.analyticsData.dlp,
          hotelBrandLabel: (marker as any).hotelBrandLabel,
          hotelDistanceFromSearch: (marker as any).hotelDistanceFromSearch,
        } as DlpAnalytics,
      });
    })
  );

  return markers;
}

export async function createRegionMarker(
  latitude: number,
  longitude: number,
  hotelMap: google.maps.Map,
  icons: Record<string, string>
) {
  const { AdvancedMarkerElement } = (await google.maps.importLibrary(
    'marker'
  )) as google.maps.MarkerLibrary;

  const pin = document.createElement('img');
  pin.src = formatAssetsUrl(icons['icon.pin-city']);

  return new AdvancedMarkerElement({
    position: {
      lat: latitude ?? 0,
      lng: longitude ?? 0,
    },
    map: hotelMap,
    content: pin,
  });
}

function getMapBounds(markers: google.maps.marker.AdvancedMarkerElement[]) {
  const bounds = new google.maps.LatLngBounds();
  markers.forEach((marker) => {
    const markerPosition = marker.position as google.maps.LatLng;
    bounds.extend(markerPosition);
  });
  return bounds;
}

async function displayMarkers(
  hotelMap: google.maps.Map,
  hotels: HotelInformationOptional[],
  icons: Record<string, string>,
  latitude: number,
  longitude: number,
  hotelOverlayRef: any,
  setDisplayedItem: any
) {
  const markers = [
    ...(await createMarkers(hotels, hotelMap, icons, hotelOverlayRef, setDisplayedItem)),
    await createRegionMarker(latitude, longitude, hotelMap, icons),
  ];
  const bounds = getMapBounds(markers);
  hotelMap.fitBounds(bounds);
}

let infoWindow: google.maps.InfoWindow;

interface AttachHotelCard {
  marker: google.maps.marker.AdvancedMarkerElement;
  hotelMap: google.maps.Map;
  hotelOverlayRef: any;
  setDisplayedItem: any;
  item: HotelInformationOptional;
  icons: Record<string, string>;
}

// Due to difficult way of testing marker events
/* istanbul ignore next */
export function attachHotelCard({
  marker,
  hotelMap,
  hotelOverlayRef,
  setDisplayedItem,
  item,
  icons,
}: AttachHotelCard) {
  const stringyfiedContent = `<div id="hotel-overlay"></div>`;

  infoWindow = new google.maps.InfoWindow({ content: stringyfiedContent });

  marker.addListener('click', async () => {
    infoWindow.open({ map: hotelMap, anchor: marker });
    await waitForElement('#hotel-overlay');
    const activePin = marker.querySelector<HTMLImageElement>('img');

    if (activePin) {
      activePin.src = formatAssetsUrl(icons['icon.pin-selected']);
    }
    const el = document.getElementById('hotel-overlay');

    const revertAndClose = () => {
      infoWindow.close();
      if (activePin?.dataset.originalSrc) {
        activePin.src = activePin.dataset.originalSrc;
      }
    };

    if (el) {
      hotelOverlayRef.current = el;
      setDisplayedItem(item);

      infoWindow.addListener('closeclick', revertAndClose);
      google.maps.event.addListener(hotelMap, 'click', revertAndClose);
      google.maps.event.addListener(hotelMap, 'drag', revertAndClose);
    }
  });
}

const mapContainerStyles = {
  height: '100%',
};
