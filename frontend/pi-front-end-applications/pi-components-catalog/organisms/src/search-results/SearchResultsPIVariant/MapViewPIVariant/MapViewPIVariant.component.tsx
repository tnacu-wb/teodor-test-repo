import { Box } from '@chakra-ui/react';
import { Loader } from '@googlemaps/js-api-loader';
import {
  HeaderInformationData,
  SingleHotelAvailability,
  SRMultiSearchParamsType,
  SRPartialTranslationsType,
} from '@whitbread-eos/api';
import {
  analytics,
  formatAssetsUrl,
  formatCurrency,
  formatDataTestId,
  formatPriceWithDecimal,
  waitForElement,
} from '@whitbread-eos/utils';
import getConfig from 'next/config';
import React, { useEffect, useRef, useState } from 'react';
import ReactDOM from 'react-dom';

import { SRMapHotelCard } from '../../../index';
import initializeMap from '../../../utils/initializeMap';
import {
  registerMapMarker,
  removeAllMarkersFromMap,
  updateMarkerPriceLabel,
} from '../../../utils/mapMarkerRegistry';
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
  isSplitViewActive?: boolean;
  pricePerNight?: boolean | null;
}

type HotelStatus = {
  status: string;
};

const AVAILABLE_STATUS = 'available';
const SOLD_OUT_STATUS = 'soldOut';
const OPEN_SOON_STATUS = 'openSoon';
const HUB_BRAND = 'HUB';
const ZIP_BRAND = 'ZIP';

export function getMarkerBrandClass(item: SingleHotelAvailability) {
  const brand = item?.hotelInformation?.brand?.toUpperCase();
  if (brand === HUB_BRAND) return 'hubMarker';
  if (brand === ZIP_BRAND) return 'zipMarker';
  return 'standardMarker';
}

export default function MapViewPIVariant({
  items,
  multiSearchParams,
  locale,
  partialTranslations,
  headerInformation,
  baseDataTestId,
  variant,
  isPricePerNightEnabled,
  isSplitViewActive: isSplitMapViewEnabled,
  pricePerNight,
}: Readonly<Props>) {
  const { publicRuntimeConfig = {} } = getConfig() || {};
  const hotelMapRef = useRef(null);
  const hotelOverlayRef = useRef(null);
  const [displayedItem, setDisplayedItem] = useState(null);
  const hotelMapInstanceRef = useRef<google.maps.Map | null>(null);
  const previousItemIdsRef = useRef<string[]>([]);
  const previousSearchContextRef = useRef<{
    multiSearchParams: SRMultiSearchParamsType;
    locale: string;
    isSplitMapViewEnabled?: boolean;
    apiKey?: string;
    pricePerNight?: boolean | null;
  } | null>(null);

  useEffect(() => {
    const apiKey = publicRuntimeConfig.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY;
    const currentItemIds = items.map((item) => item.hotelId);
    const previousItemIds = previousItemIdsRef.current;
    const previousSearchContext = previousSearchContextRef.current;

    const sameSearchContext =
      hotelMapInstanceRef.current !== null &&
      !!previousSearchContext &&
      previousSearchContext.multiSearchParams === multiSearchParams &&
      previousSearchContext.locale === locale &&
      previousSearchContext.isSplitMapViewEnabled === isSplitMapViewEnabled &&
      previousSearchContext.apiKey === apiKey;

    const itemIdsUnchanged =
      currentItemIds.length === previousItemIds.length &&
      currentItemIds.every((id, index) => id === previousItemIds[index]);

    const pricePerNightChanged =
      !!previousSearchContext && previousSearchContext.pricePerNight !== pricePerNight;

    const isIncrementalLoad =
      sameSearchContext &&
      currentItemIds.length > previousItemIds.length &&
      previousItemIds.every((id, index) => id === currentItemIds[index]);

    previousItemIdsRef.current = currentItemIds;
    previousSearchContextRef.current = {
      multiSearchParams,
      locale,
      isSplitMapViewEnabled,
      apiKey,
      pricePerNight,
    };

    if (sameSearchContext && itemIdsUnchanged) {
      if (!pricePerNightChanged) {
        return;
      }

      items.forEach((item) => {
        const text = getMarkerLabel(
          item,
          partialTranslations,
          multiSearchParams,
          locale,
          isSplitMapViewEnabled,
          pricePerNight
        );
        updateMarkerPriceLabel(item.hotelId, text);
      });
      return;
    }

    if (isIncrementalLoad && hotelMapInstanceRef.current) {
      displayMarkers({
        hotelMap: hotelMapInstanceRef.current,
        hotels: items,
        partialTranslations,
        headerInformation,
        multiSearchParams,
        locale,
        setDisplayedItem,
        hotelOverlayRef,
        isSplitMapViewEnabled,
        pricePerNight,
        fitAllMarkers: true,
      });
      return;
    }

    const loader = new Loader({
      apiKey,
      version: 'weekly',
      libraries: [],
    });

    loader.load().then(() => {
      if (hotelMapRef?.current) {
        if (items.length === 0) {
          hotelMapInstanceRef.current = null;
          initializeEmptyCountryMap(
            emptyCountryMapCoordinates[locale].lat,
            emptyCountryMapCoordinates[locale].long,
            hotelMapRef
          );

          return <Box data-testid={formatDataTestId(baseDataTestId, 'mapView')} />;
        }
        const { centerLat, centerLong } = getCenterByHotels(items);
        const hotelMap = initializeMap(
          centerLat,
          centerLong,
          hotelMapRef,
          publicRuntimeConfig.NEXT_PUBLIC_GOOGLE_MAP_ID_SRP,
          {
            zoom: 14.75,
            gestureHandling: 'greedy',
            renderingType: google.maps.RenderingType.VECTOR,
            clickableIcons: false,
            minZoom: 7.5,
            zoomControl: true,
            cameraControl: false,
            mapTypeControl: false,
            scaleControl: false,
            streetViewControl: false,
            rotateControl: false,
            fullscreenControl: false,
          }
        );
        hotelMapInstanceRef.current = hotelMap;
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
          isSplitMapViewEnabled,
          pricePerNight,
        };
        displayMarkers(params);
        if (isSplitMapViewEnabled) {
          centerMapOnSearchPlace(hotelMap, multiSearchParams, items);
        }
      }
    });
  }, [
    multiSearchParams,
    locale,
    items,
    partialTranslations,
    headerInformation,
    publicRuntimeConfig.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY,
    publicRuntimeConfig.NEXT_PUBLIC_GOOGLE_MAP_ID_SRP,
    isSplitMapViewEnabled,
    pricePerNight,
  ]);

  useEffect(() => {
    return () => {
      removeAllMarkersFromMap();
    };
  }, []);

  return (
    <>
      {hotelOverlayRef.current &&
        displayedItem &&
        ReactDOM.createPortal(
          <SRMapHotelCard
            isPricePerNightEnabled={isPricePerNightEnabled}
            data={displayedItem}
            locale={locale}
            partialTranslations={partialTranslations}
            multiSearchParams={multiSearchParams}
            variant={variant}
            isSplitView={isSplitMapViewEnabled}
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
  locale: string,
  isSplitMapViewEnabled?: boolean,
  pricePerNight?: boolean | null
) {
  const lowestRate = item?.hotelAvailability?.lowestRoomRate;
  const { status }: HotelStatus = getItemStatus(item, multiSearchParams);
  let content = ' ';

  if (status === AVAILABLE_STATUS && lowestRate) {
    const price = pricePerNight
      ? lowestRate.netTotal / (multiSearchParams?.numberOfNights || 1)
      : lowestRate.netTotal;
    content = formatPriceWithDecimal(locale, formatCurrency(lowestRate.currencyCode), price);
  }

  if (status === SOLD_OUT_STATUS) {
    content = isSplitMapViewEnabled
      ? '-'
      : (partialTranslations?.searchInformation?.content?.results?.result?.fullyBooked ?? '');
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
  setDisplayedItem: any,
  isSplitMapViewEnabled?: boolean,
  pricePerNight?: boolean | null
) {
  removeAllMarkersFromMap();
  const splitViewClass = isSplitMapViewEnabled ? 'splitViewMarker' : '';
  return items.map((item, index) => {
    try {
      const { status } = getItemStatus(item, multiSearchParams);
      const isSoldOutStatus = status === SOLD_OUT_STATUS;
      const isSoldOut = isSoldOutStatus ? 'soldOut' : '';
      const brandClass = getMarkerBrandClass(item);
      // Sold-out markers always sit behind available ones; otherwise markers higher up
      // the list view (lower index) render in front of markers further down the list.
      const zIndex = isSoldOutStatus ? 0 : items.length - index;
      const markerLabel = {
        text: getMarkerLabel(
          item,
          partialTranslations,
          multiSearchParams,
          locale,
          isSplitMapViewEnabled,
          pricePerNight
        ),
        className: `mapMarker mapMarker-${item.hotelId} ${brandClass} ${splitViewClass} ${isSoldOut}`,
      };

      marker = new google.maps.Marker({
        position: {
          lat: item?.hotelInformation?.coordinates?.latitude ?? 0,
          lng: item?.hotelInformation?.coordinates?.longitude ?? 0,
        },
        map: hotelMap,
        label: markerLabel,
        icon: ` `,
        cursor: 'pointer',
        zIndex,
      });

      registerMapMarker(item.hotelId, marker, zIndex, markerLabel);

      attachHotelCard({
        marker,
        hotelMap,
        markerLabel,
        hotelOverlayRef,
        setDisplayedItem,
        item,
        isSoldOut,
        brandClass,
        splitViewClass,
        baseZIndex: zIndex,
        isSplitMapViewEnabled,
      });

      return marker;
    } catch (e) {
      throw new Error();
    }
  });
}

const EARTH_CIRCUMFERENCE_METERS = 40075017;
const TILE_SIZE_PIXELS = 256;

export function calculateZoomLevel(
  sortedDistances: number[],
  mapWidthPx: number,
  centerLatDegrees: number
) {
  let radius: number;

  if (sortedDistances.length < 10) {
    radius =
      sortedDistances.length < 3
        ? Math.max(...sortedDistances) * 6400
        : Math.max(...sortedDistances) * 3200;
  } else if (
    sortedDistances.findIndex((distance) => distance > 5) > 5 ||
    sortedDistances[sortedDistances.length - 1] < 5
  ) {
    radius = sortedDistances[Math.floor(sortedDistances.length / 2)] * 3200;
  } else {
    radius = sortedDistances.length > 4 ? sortedDistances[3] * 3200 : sortedDistances[0] * 3200;
  }

  const metersPerPixel = (radius * 2) / mapWidthPx;

  return Math.log2(
    EARTH_CIRCUMFERENCE_METERS /
      (TILE_SIZE_PIXELS * metersPerPixel * Math.cos((centerLatDegrees * Math.PI) / 180))
  );
}

function setMapZoomForHotels(hotelMap: google.maps.Map, hotels: SingleHotelAvailability[]) {
  if (hotels.length === 0) return;

  const sortedDistances = hotels
    .map((hotel) => hotel.hotelAvailability?.distance)
    .filter(
      (distance): distance is number =>
        distance !== undefined && Number.isFinite(distance) && distance > 0
    )
    .sort((a, b) => a - b);

  if (sortedDistances.length === 0) return;

  const mapWidthPx = hotelMap.getDiv().offsetWidth;
  if (mapWidthPx <= 0) return;

  const centerLatDegrees = hotelMap.getCenter()?.lat() ?? 0;

  hotelMap.setZoom(calculateZoomLevel(sortedDistances, mapWidthPx, centerLatDegrees));
}

function parseLatLong(coordinates?: string) {
  const [lat, lng] = (coordinates ?? '').split(',').map(Number);
  return Number.isFinite(lat) && Number.isFinite(lng) ? { lat, lng } : null;
}

const LOCATION_MARKER_ASSET_PATH = '/content/dam/pi/websites/target/map/';

function getLocationMarkerIcon(searchTerm?: string) {
  const term = (searchTerm ?? '').toLowerCase();
  if (term.includes('airport') || term.includes('terminal')) return 'location_airport.svg';
  if (term.includes('stadium')) return 'location_stadium.svg';
  return 'location_city.svg';
}

let currentLocationMarker: google.maps.OverlayView | null = null;

function addLocationMarker(
  hotelMap: google.maps.Map,
  position: google.maps.LatLng,
  searchTerm?: string
) {
  currentLocationMarker?.setMap(null);

  // Defined lazily (rather than as a module-level class) since `google.maps.OverlayView`
  // only exists once the Google Maps script has finished loading.
  class LocationMarkerOverlay extends google.maps.OverlayView {
    private markerDiv: HTMLDivElement;

    constructor(private markerPosition: google.maps.LatLng) {
      super();
      this.markerDiv = document.createElement('div');
      this.markerDiv.className = 'locationMarker';
      const icon = document.createElement('img');
      icon.src = formatAssetsUrl(
        `${LOCATION_MARKER_ASSET_PATH}${getLocationMarkerIcon(searchTerm)}`
      );
      this.markerDiv.appendChild(icon);
    }

    onAdd() {
      // Appended to `overlayLayer` (not `overlayMouseTarget`) because Google Maps stacks
      // panes in a fixed order regardless of any individual element's zIndex: overlayLayer
      // renders below markerLayer, where hotel markers live. This guarantees hotel markers —
      // especially the hovered one, which is bumped to MAX_ZINDEX — are never covered by
      // the search location pin.
      this.getPanes()?.overlayLayer.appendChild(this.markerDiv);
    }

    draw() {
      const point = this.getProjection()?.fromLatLngToDivPixel(this.markerPosition);
      if (point) {
        this.markerDiv.style.left = `${point.x}px`;
        this.markerDiv.style.top = `${point.y}px`;
      }
    }

    onRemove() {
      this.markerDiv.remove();
    }
  }

  const locationMarker = new LocationMarkerOverlay(position);
  locationMarker.setMap(hotelMap);
  currentLocationMarker = locationMarker;
}

export async function centerMapOnSearchPlace(
  hotelMap: google.maps.Map,
  multiSearchParams: SRMultiSearchParamsType,
  hotels: SingleHotelAvailability[]
) {
  const recenterMap = (location: google.maps.LatLng | google.maps.LatLngLiteral) => {
    hotelMap.setCenter(location);
    setMapZoomForHotels(hotelMap, hotels);
  };

  const centerOnCoordinates = () => {
    const coordinates = parseLatLong(multiSearchParams?.coordinates);
    if (coordinates) {
      recenterMap(coordinates);
    }
  };

  if (multiSearchParams?.placeId) {
    try {
      await google.maps.importLibrary('places');
    } catch {
      centerOnCoordinates();
      return;
    }

    if (!google.maps.places?.PlacesService) {
      centerOnCoordinates();
      return;
    }

    new google.maps.places.PlacesService(hotelMap).getDetails(
      { placeId: multiSearchParams.placeId, fields: ['geometry'] },
      (place, status) => {
        if (status === google.maps.places.PlacesServiceStatus.OK && place?.geometry?.location) {
          recenterMap(place.geometry.location);
          addLocationMarker(hotelMap, place.geometry.location, multiSearchParams?.location);
        }
      }
    );
    return;
  }

  centerOnCoordinates();
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
  isSplitMapViewEnabled?: boolean;
  pricePerNight?: boolean | null;
  fitAllMarkers?: boolean;
}) {
  const {
    hotelMap,
    hotels,
    partialTranslations,
    multiSearchParams,
    locale,
    hotelOverlayRef,
    setDisplayedItem,
    isSplitMapViewEnabled,
    pricePerNight,
    fitAllMarkers,
  } = params;
  const markers = createMarkers(
    hotels,
    partialTranslations,
    multiSearchParams,
    locale,
    hotelMap,
    hotelOverlayRef,
    setDisplayedItem,
    isSplitMapViewEnabled,
    pricePerNight
  );
  analytics.update({
    mapMarkerReferences: markers,
  });

  if ((fitAllMarkers || !isSplitMapViewEnabled) && markers.length > 1) {
    const bounds = new google.maps.LatLngBounds();
    markers.forEach((marker) => {
      const markerPosition = marker.getPosition() as google.maps.LatLng;
      bounds.extend(markerPosition);
    });
    hotelMap.fitBounds(bounds);
    return;
  }

  setMapZoomForHotels(hotelMap, hotels);
}

let infoWindow: google.maps.InfoWindow;

interface AttachHotelCard {
  marker: google.maps.Marker;
  hotelMap: google.maps.Map;
  markerLabel: { className: string; text: string };
  hotelOverlayRef: any;
  setDisplayedItem: any;
  item: SingleHotelAvailability;
  isSoldOut: string;
  brandClass: string;
  splitViewClass: string;
  baseZIndex: number;
  isSplitMapViewEnabled?: boolean;
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
  brandClass,
  splitViewClass,
  baseZIndex,
  isSplitMapViewEnabled,
}: AttachHotelCard) {
  const stringyfiedContent = `<div id="hotel-overlay"></div>`;

  infoWindow = new google.maps.InfoWindow({ content: stringyfiedContent });
  marker.addListener('mouseover', async () => {
    document.querySelectorAll(`.mapMarker`).forEach((hotel) => {
      hotel.classList.remove('activeHover');
    });
    marker.setZIndex(google.maps.Marker.MAX_ZINDEX + 1);
    marker.setLabel({
      ...markerLabel,
      className: `mapMarker ${brandClass} ${splitViewClass} ${isSoldOut} hoverMarker`,
    });
  });
  marker.addListener('mouseout', async () => {
    document.querySelectorAll(`.mapMarker`).forEach((hotel) => {
      hotel.classList.remove('activeHover');
    });
    marker.setZIndex(baseZIndex);
    marker.setLabel(markerLabel);
  });

  marker.addListener('click', async () => {
    analytics.track('analyticsData_updated_map_marker_click', { hotelId: item.hotelId });
    if (isSplitMapViewEnabled) {
      window.dispatchEvent(new CustomEvent('Map List View | Clicked Tile'));
    }
    removeActiveClass(`.mapMarker`, 'activeMarker');
    const selectedHotel = document.querySelector(`.mapMarker-${item.hotelId}`);
    selectedHotel?.classList.add('activeMarker');

    marker.setLabel({
      ...markerLabel,
      className: `mapMarker mapMarker-${item.hotelId} ${brandClass} ${splitViewClass} activeMarker`,
    });

    marker.addListener('mouseover', async () => {
      removeActiveClass(`.mapMarker`, 'activeMarker');
      marker.setZIndex(google.maps.Marker.MAX_ZINDEX + 1);
      selectedHotel?.classList.add('activeHover');
    });
    marker.addListener('mouseout', async () => {
      removeActiveClass(`.mapMarker`, 'activeMarker');
      marker.setZIndex(baseZIndex);

      marker.setLabel({
        ...markerLabel,
        className: `mapMarker ${brandClass} ${splitViewClass} activeMarker`,
      });
    });
    infoWindow.open({ map: hotelMap, anchor: marker });
    await waitForElement('#hotel-overlay');
    const el = document.getElementById('hotel-overlay');
    if (el) {
      hotelOverlayRef.current = el;
      setDisplayedItem(item);
      if (isSplitMapViewEnabled) {
        window.dispatchEvent(
          new CustomEvent('map_bubble_clicked', {
            detail: { hotelCode: item.hotelId, displayPrice: markerLabel.text },
          })
        );
      }
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
