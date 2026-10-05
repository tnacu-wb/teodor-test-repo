import { Box } from '@chakra-ui/react';
import { Circle, InfoWindow } from '@googlemaps/jest-mocks';
import '@testing-library/jest-dom';
import { formatCurrency, formatPriceWithDecimal } from '@whitbread-eos/utils';
import { add, format } from 'date-fns';

import { mockControlPositions } from '../../../mockData/mockResponse';
import { render, screen, waitFor } from '../../../utils/test-utils';
import {
  mockedItem,
  mockedItems,
  mockedMultiSearchParams,
  mockedPartialTranslations,
  mockedHeaderInformation,
} from '../../mockResponse';
import MapViewPIVariant, {
  calculateZoomLevel,
  centerMapOnSearchPlace,
  createMarkers,
  getCenterByHotels,
  getItemStatus,
} from './MapViewPIVariant.component';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  waitForElement: (selector: string) => {
    const el = globalThis.document.querySelector(selector);
    return el ? Promise.resolve(el) : new Promise(() => {});
  },
}));

const mockGoogleMapsLoader = jest.fn().mockImplementation(() => Promise.resolve({}));
jest.mock('@googlemaps/js-api-loader', () => ({
  ...jest.requireActual('@googlemaps/js-api-loader'),
  Loader: jest.fn().mockImplementation(() => ({
    load: mockGoogleMapsLoader,
  })),
}));

const mockGoogleMap = jest.fn().mockImplementation((mapDiv, opts) => ({
  mapDiv,
  opts,
  fitBounds: jest.fn(),
  setZoom: jest.fn(),
  getDiv: () => ({ offsetWidth: 400 }),
  getCenter: () => ({ lat: () => 51.5 }),
}));
const mockGooglePoint = jest.fn().mockImplementation((a, b) => ({ a, b }));
const markerListeners = new Map<any, Record<string, () => void>>();

const mockGoogleMarker = jest.fn().mockImplementation((opts) => {
  const listeners: Record<string, () => void> = {};
  const instance = {
    opts,
    getPosition: () => jest.fn(),
    getLabel: () => instance.opts.label,
    addListener: (event: string, callback: () => void) => {
      listeners[event] = callback;
    },
    setLabel: jest.fn((label) => {
      instance.opts = { ...instance.opts, label };
    }),
    setZIndex: jest.fn(),
    setMap: jest.fn(),
  };
  markerListeners.set(instance, listeners);
  return instance;
});

const fireMarkerEvent = (marker: any, event: string) => markerListeners.get(marker)?.[event]?.();

const mockGetDetails = jest.fn();
const mockPlacesService = jest.fn().mockImplementation(() => ({ getDetails: mockGetDetails }));

let overlayMouseTargetEl: HTMLDivElement;
let overlayLayerEl: HTMLDivElement;

class MockOverlayView {
  setMap() {
    (this as any).onAdd?.();
    (this as any).draw?.();
  }

  getPanes() {
    return { overlayMouseTarget: overlayMouseTargetEl, overlayLayer: overlayLayerEl };
  }

  getProjection() {
    return { fromLatLngToDivPixel: () => ({ x: 0, y: 0 }) };
  }
}

const setupGoogleMock = () => {
  overlayMouseTargetEl = document.createElement('div');
  overlayLayerEl = document.createElement('div');

  const google = {
    maps: {
      Map: mockGoogleMap,
      Marker: mockGoogleMarker,
      Point: mockGooglePoint,
      ControlPosition: mockControlPositions,
      Circle: Circle,
      InfoWindow: InfoWindow,
      OverlayView: MockOverlayView,
      RenderingType: { VECTOR: 'VECTOR' },
      LatLngBounds: jest.fn().mockImplementation((a, b) => ({
        a,
        b,
        extend: (c: any) => ({ c }),
      })),
      places: {
        PlacesService: mockPlacesService,
        PlacesServiceStatus: { OK: 'OK' },
      },
      importLibrary: jest.fn().mockResolvedValue(undefined),
      event: { addListener: jest.fn() },
    },
  };

  global.window.google = google as any;
};

setupGoogleMock();
const mapOptions = {
  center: {
    lat: 51.61348,
    lng: -0.0466,
  },
  zoom: 14.75,
  mapTypeControlOptions: {
    position: google.maps.ControlPosition.LEFT_BOTTOM,
  },
  zoomControl: true,
  gestureHandling: 'greedy',
  renderingType: 'VECTOR',
  clickableIcons: false,
  minZoom: 7.5,
  mapTypeControl: false,
  scaleControl: false,
  streetViewControl: false,
  rotateControl: false,
  fullscreenControl: false,
  cameraControl: false,
  mapId: 'DEMO_MAP_ID',
  styles: [
    {
      featureType: 'poi',
      elementType: 'labels',
      stylers: [{ visibility: 'off' }],
    },
    {
      featureType: 'poi',
      elementType: 'geometry',
      stylers: [{ visibility: 'off' }],
    },
    {
      featureType: 'poi.business',
      stylers: [{ visibility: 'off' }],
    },
    {
      featureType: 'poi.hotel',
      elementType: 'all',
      stylers: [{ visibility: 'off' }],
    },
  ],
};

const mockedItemWithoutPrice = [
  {
    ...mockedItem[0],
    hotelAvailability: {
      lowestRoomRate: null,
      available: true,
    },
  },
];

const mockedSoldOutItem = mockedItem.map((item) => ({
  ...item,
  hotelAvailability: { ...item.hotelAvailability, lowestRoomRate: null, available: false },
}));
const mockedOpenSoonItem = mockedItem.map((item) => ({
  ...item,
  hotelInformation: {
    ...item.hotelInformation,
    hotelOpeningDate: format(add(new Date(), { days: 5 }), 'yyyy-MM-dd'),
  },
}));
const mockedNoStatusItem = mockedItem.map((item) => ({
  ...item,
  hotelAvailability: { ...item.hotelAvailability, lowestRoomRate: null },
}));

const mockedMultiSearchParamsArrivalToday = {
  ...mockedMultiSearchParams,
  arrivalDay: new Date().getDate(),
  arrivalMonth: new Date().getMonth() + 1,
  arrivalYear: new Date().getFullYear(),
};

const hotelOverlayRef = '<div id="hotel-overlay></div>';

describe('MapView component', () => {
  beforeAll(() => {
    setupGoogleMock();
  });
  afterEach(() => {
    jest.clearAllMocks();
  });
  it('should render the MapView component', () => {
    const { getByTestId } = render(
      <MapViewPIVariant
        items={mockedItem}
        multiSearchParams={mockedMultiSearchParams}
        locale="en"
        partialTranslations={mockedPartialTranslations}
        headerInformation={mockedHeaderInformation}
        baseDataTestId="SRP"
      />
    );
    expect(getByTestId('SRP-mapView')).toBeInTheDocument();
  });
  it('exposes the map instance and its markers on window.analyticsData for tracking', async () => {
    render(
      <MapViewPIVariant
        items={mockedItem}
        multiSearchParams={mockedMultiSearchParams}
        locale="en"
        partialTranslations={mockedPartialTranslations}
        headerInformation={mockedHeaderInformation}
        baseDataTestId="SRP"
      />
    );

    await waitFor(() => {
      expect((window as any).analyticsData?.mapReference).toBeDefined();
      expect((window as any).analyticsData?.mapMarkerReferences).toHaveLength(1);
    });
  });
  it('should load Google Maps script', () => {
    render(
      <MapViewPIVariant
        items={mockedItem}
        multiSearchParams={mockedMultiSearchParams}
        locale="en"
        partialTranslations={mockedPartialTranslations}
        headerInformation={mockedHeaderInformation}
        baseDataTestId="SRP"
      />
    );
    expect(mockGoogleMapsLoader).toHaveBeenCalled();
  });
  it('should render Google Maps country map when there are no items', async () => {
    render(
      <>
        <Box id="hotel-overlay"></Box>
        <MapViewPIVariant
          items={[]}
          multiSearchParams={mockedMultiSearchParams}
          locale="en"
          partialTranslations={mockedPartialTranslations}
          headerInformation={mockedHeaderInformation}
          baseDataTestId="SRP"
        />
      </>
    );
    const mapElement = screen.getByTestId('SRP-mapView');
    const mapCountryOptions = {
      center: {
        lat: 55.378051,
        lng: -3.435973,
      },
      disableDefaultUI: true,
      zoom: 6,
    };
    await waitFor(() => {
      expect(mockGoogleMap).toHaveBeenCalledWith(mapElement, mapCountryOptions);
    });
  });

  it('should instantiate Google Maps with initial parameters', async () => {
    render(
      <>
        <Box id="hotel-overlay"></Box>
        <MapViewPIVariant
          items={mockedItem}
          multiSearchParams={mockedMultiSearchParams}
          locale="en"
          partialTranslations={mockedPartialTranslations}
          headerInformation={mockedHeaderInformation}
          baseDataTestId="SRP"
        />
      </>
    );
    const mapElement = screen.getByTestId('SRP-mapView');
    await waitFor(() => {
      expect(mockGoogleMap).toHaveBeenCalledWith(mapElement, mapOptions);
    });
  });
  it('should create a Marker', async () => {
    render(
      <MapViewPIVariant
        items={mockedItem}
        multiSearchParams={mockedMultiSearchParams}
        locale="en"
        partialTranslations={mockedPartialTranslations}
        headerInformation={mockedHeaderInformation}
        baseDataTestId="SRP"
      />
    );
    await waitFor(() => {
      expect(mockGoogleMarker).toBeCalledTimes(1);
    });
  });
  it('should create 2 Markers', async () => {
    render(
      <MapViewPIVariant
        items={mockedItems}
        multiSearchParams={mockedMultiSearchParams}
        locale="en"
        partialTranslations={mockedPartialTranslations}
        headerInformation={mockedHeaderInformation}
        baseDataTestId="SRP"
      />
    );

    await waitFor(() => {
      expect(mockGoogleMarker).toBeCalledTimes(2);
    });
  });

  it('reuses the existing map instance and redraws markers instead of rebuilding the map when more hotels load', async () => {
    const { rerender } = render(
      <MapViewPIVariant
        items={mockedItem}
        multiSearchParams={mockedMultiSearchParams}
        locale="en"
        partialTranslations={mockedPartialTranslations}
        headerInformation={mockedHeaderInformation}
        baseDataTestId="SRP"
      />
    );

    await waitFor(() => {
      expect(mockGoogleMap).toBeCalledTimes(1);
      expect(mockGoogleMarker).toBeCalledTimes(1);
    });

    rerender(
      <MapViewPIVariant
        items={mockedItems}
        multiSearchParams={mockedMultiSearchParams}
        locale="en"
        partialTranslations={mockedPartialTranslations}
        headerInformation={mockedHeaderInformation}
        baseDataTestId="SRP"
      />
    );

    await waitFor(() => {
      expect(mockGoogleMarker).toBeCalledTimes(1 + mockedItems.length);
    });

    expect(mockGoogleMap).toBeCalledTimes(1);

    const hotelMapInstance = mockGoogleMap.mock.results[0].value;
    expect(hotelMapInstance.fitBounds).toHaveBeenCalled();
  });

  it('zooms out to fit every hotel when more load in split view, instead of the initial distance-based zoom', async () => {
    const { rerender } = render(
      <MapViewPIVariant
        items={mockedItem}
        multiSearchParams={mockedMultiSearchParams}
        locale="en"
        partialTranslations={mockedPartialTranslations}
        headerInformation={mockedHeaderInformation}
        baseDataTestId="SRP"
        isSplitViewActive
      />
    );

    await waitFor(() => {
      expect(mockGoogleMap).toBeCalledTimes(1);
    });

    const hotelMapInstance = mockGoogleMap.mock.results[0].value;
    hotelMapInstance.fitBounds.mockClear();
    hotelMapInstance.setZoom.mockClear();

    rerender(
      <MapViewPIVariant
        items={mockedItems}
        multiSearchParams={mockedMultiSearchParams}
        locale="en"
        partialTranslations={mockedPartialTranslations}
        headerInformation={mockedHeaderInformation}
        baseDataTestId="SRP"
        isSplitViewActive
      />
    );

    await waitFor(() => {
      expect(hotelMapInstance.fitBounds).toHaveBeenCalled();
    });
    expect(hotelMapInstance.setZoom).not.toHaveBeenCalled();
    expect(mockGoogleMap).toBeCalledTimes(1);
  });

  it('does not rebuild or redraw the map when unrelated props get a new reference but the hotel list is unchanged', async () => {
    const { rerender } = render(
      <MapViewPIVariant
        items={mockedItem}
        multiSearchParams={mockedMultiSearchParams}
        locale="en"
        partialTranslations={mockedPartialTranslations}
        headerInformation={mockedHeaderInformation}
        baseDataTestId="SRP"
      />
    );

    await waitFor(() => {
      expect(mockGoogleMap).toBeCalledTimes(1);
      expect(mockGoogleMarker).toBeCalledTimes(1);
    });

    rerender(
      <MapViewPIVariant
        items={[...mockedItem]}
        multiSearchParams={mockedMultiSearchParams}
        locale="en"
        partialTranslations={{ ...mockedPartialTranslations }}
        headerInformation={{ ...mockedHeaderInformation }}
        baseDataTestId="SRP"
      />
    );

    expect(mockGoogleMap).toBeCalledTimes(1);
    expect(mockGoogleMarker).toBeCalledTimes(1);
  });

  it('updates marker label text in place when only pricePerNight changes, without recreating markers or touching the map bounds', async () => {
    const { rerender } = render(
      <MapViewPIVariant
        items={mockedItem}
        multiSearchParams={mockedMultiSearchParams}
        locale="en"
        partialTranslations={mockedPartialTranslations}
        headerInformation={mockedHeaderInformation}
        baseDataTestId="SRP"
        isSplitViewActive
        pricePerNight={false}
      />
    );

    await waitFor(() => {
      expect(mockGoogleMap).toBeCalledTimes(1);
      expect(mockGoogleMarker).toBeCalledTimes(1);
    });

    const netTotal = mockedItem[0].hotelAvailability.lowestRoomRate.netTotal;
    const currency = formatCurrency(mockedItem[0].hotelAvailability.lowestRoomRate.currencyCode);
    const expectedTotalPrice = formatPriceWithDecimal('en', currency, netTotal);
    const expectedPricePerNight = formatPriceWithDecimal(
      'en',
      currency,
      netTotal / mockedMultiSearchParams.numberOfNights
    );
    expect(mockGoogleMarker.mock.calls[0][0].label.text).toBe(expectedTotalPrice);

    const markerInstance = mockGoogleMarker.mock.results[0].value;
    const hotelMapInstance = mockGoogleMap.mock.results[0].value;
    hotelMapInstance.fitBounds.mockClear();
    hotelMapInstance.setZoom.mockClear();

    rerender(
      <MapViewPIVariant
        items={mockedItem}
        multiSearchParams={mockedMultiSearchParams}
        locale="en"
        partialTranslations={mockedPartialTranslations}
        headerInformation={mockedHeaderInformation}
        baseDataTestId="SRP"
        isSplitViewActive
        pricePerNight
      />
    );

    await waitFor(() => {
      expect(markerInstance.getLabel().text).toBe(expectedPricePerNight);
    });
    // The marker is updated in place, not recreated — preserving any hover/active state.
    expect(mockGoogleMarker).toBeCalledTimes(1);
    expect(mockGoogleMap).toBeCalledTimes(1);
    expect(hotelMapInstance.fitBounds).not.toHaveBeenCalled();
    expect(hotelMapInstance.setZoom).not.toHaveBeenCalled();
  });

  it('detaches markers from the map (not just the registry) on unmount', async () => {
    const { unmount } = render(
      <MapViewPIVariant
        items={mockedItem}
        multiSearchParams={mockedMultiSearchParams}
        locale="en"
        partialTranslations={mockedPartialTranslations}
        headerInformation={mockedHeaderInformation}
        baseDataTestId="SRP"
      />
    );

    await waitFor(() => {
      expect(mockGoogleMarker).toBeCalledTimes(1);
    });

    const markerInstance = mockGoogleMarker.mock.results[0].value;

    unmount();

    expect(markerInstance.setMap).toHaveBeenCalledWith(null);
  });
});

describe('createMarkers function', () => {
  beforeAll(() => {
    setupGoogleMock();
  });
  afterEach(() => {
    jest.clearAllMocks();
  });
  it('should return 1 marker', () => {
    const { getByTestId } = render(
      <MapViewPIVariant
        items={mockedItem}
        multiSearchParams={mockedMultiSearchParams}
        locale="en"
        partialTranslations={mockedPartialTranslations}
        headerInformation={mockedHeaderInformation}
        baseDataTestId="SRP"
      />
    );
    const mapElement = getByTestId('SRP-mapView');
    const hotelMap = new google.maps.Map(mapElement, mapOptions);

    const markers = createMarkers(
      mockedItem,
      mockedPartialTranslations,
      mockedMultiSearchParams,
      'en',
      hotelMap,
      hotelOverlayRef,
      mockedItems[0],
      true
    );
    expect(markers).toHaveLength(1);
    expect(markers[0].opts.label.className).toContain('splitViewMarker');
    expect(markers[0].opts.label.className).toContain('standardMarker');
    expect(markers[0].opts.cursor).toBe('pointer');
  });
  it('should return 1 marker with Sold out label', async () => {
    const { getByTestId } = render(
      <MapViewPIVariant
        items={mockedSoldOutItem}
        multiSearchParams={mockedMultiSearchParamsArrivalToday}
        locale="en"
        partialTranslations={mockedPartialTranslations}
        headerInformation={mockedHeaderInformation}
        baseDataTestId="SRP"
      />
    );

    const mapElement = getByTestId('SRP-mapView');
    const hotelMap = new google.maps.Map(mapElement, mapOptions);

    const markers = createMarkers(
      mockedSoldOutItem,
      mockedPartialTranslations,
      mockedMultiSearchParamsArrivalToday,
      'en',
      hotelMap,
      hotelOverlayRef,
      mockedSoldOutItem,
      true
    );
    expect(markers).toHaveLength(1);
    expect(markers[0].opts.label.text).toBe('-');
    expect(markers[0].opts.label.className).toContain('soldOut');
    expect(markers[0].opts.zIndex).toBe(0);
  });
  it('should return 1 marker with Open soon label', async () => {
    const { getByTestId } = render(
      <MapViewPIVariant
        items={mockedOpenSoonItem}
        multiSearchParams={mockedMultiSearchParamsArrivalToday}
        locale="en"
        partialTranslations={mockedPartialTranslations}
        headerInformation={mockedHeaderInformation}
        baseDataTestId="SRP"
      />
    );

    const mapElement = getByTestId('SRP-mapView');
    const hotelMap = new google.maps.Map(mapElement, mapOptions);

    const markers = createMarkers(
      mockedOpenSoonItem,
      mockedPartialTranslations,
      mockedMultiSearchParamsArrivalToday,
      'en',
      hotelMap,
      hotelOverlayRef,
      mockedOpenSoonItem,
      true
    );
    expect(markers).toHaveLength(1);
    expect(markers[0].opts.label.text).toBe(
      mockedPartialTranslations.searchInformation.content.results.result.openingSoon
    );
  });
  it('shows the total price by default and the per-night price when pricePerNight is true', () => {
    const { getByTestId } = render(
      <MapViewPIVariant
        items={mockedItem}
        multiSearchParams={mockedMultiSearchParams}
        locale="en"
        partialTranslations={mockedPartialTranslations}
        headerInformation={mockedHeaderInformation}
        baseDataTestId="SRP"
      />
    );
    const mapElement = getByTestId('SRP-mapView');
    const hotelMap = new google.maps.Map(mapElement, mapOptions);

    const netTotal = mockedItem[0].hotelAvailability.lowestRoomRate.netTotal;
    const currency = formatCurrency(mockedItem[0].hotelAvailability.lowestRoomRate.currencyCode);
    const expectedTotalPrice = formatPriceWithDecimal('en', currency, netTotal);
    const expectedPricePerNight = formatPriceWithDecimal(
      'en',
      currency,
      netTotal / mockedMultiSearchParams.numberOfNights
    );

    const totalPriceMarkers = createMarkers(
      mockedItem,
      mockedPartialTranslations,
      mockedMultiSearchParams,
      'en',
      hotelMap,
      hotelOverlayRef,
      mockedItem,
      true,
      false
    );
    expect(totalPriceMarkers[0].opts.label.text).toBe(expectedTotalPrice);

    const perNightMarkers = createMarkers(
      mockedItem,
      mockedPartialTranslations,
      mockedMultiSearchParams,
      'en',
      hotelMap,
      hotelOverlayRef,
      mockedItem,
      true,
      true
    );
    expect(perNightMarkers[0].opts.label.text).toBe(expectedPricePerNight);
    expect(perNightMarkers[0].opts.label.text).not.toBe(expectedTotalPrice);
  });

  it('should return 2 markers', () => {
    const { getByTestId } = render(
      <MapViewPIVariant
        items={mockedItems}
        multiSearchParams={mockedMultiSearchParams}
        locale="en"
        partialTranslations={mockedPartialTranslations}
        headerInformation={mockedHeaderInformation}
        baseDataTestId="SRP"
      />
    );
    const mapElement = getByTestId('SRP-mapView');
    const hotelMap = new google.maps.Map(mapElement, mapOptions);

    const markers = createMarkers(
      mockedItems,
      mockedPartialTranslations,
      mockedMultiSearchParams,
      'en',
      hotelMap,
      hotelOverlayRef,
      mockedItems[0],
      true
    );
    expect(markers).toHaveLength(2);
  });

  it('should return an empty marker if the lowestRoomRate comes null', () => {
    const { getByTestId } = render(
      <MapViewPIVariant
        items={mockedItemWithoutPrice}
        multiSearchParams={mockedMultiSearchParamsArrivalToday}
        locale="en"
        partialTranslations={mockedPartialTranslations}
        headerInformation={mockedHeaderInformation}
        baseDataTestId="SRP"
      />
    );

    const mapElement = getByTestId('SRP-mapView');
    const hotelMap = new google.maps.Map(mapElement, mapOptions);

    const markers = createMarkers(
      mockedItemWithoutPrice,
      mockedPartialTranslations,
      mockedMultiSearchParamsArrivalToday,
      'en',
      hotelMap,
      hotelOverlayRef,
      mockedItemWithoutPrice,
      true
    );
    expect(markers).toHaveLength(1);
    expect(markers[0].opts.label.text).toBe(' ');
  });

  it('applies the hub and zip brand classes for HUB and ZIP hotels', () => {
    const hubItem = [
      { ...mockedItem[0], hotelInformation: { ...mockedItem[0].hotelInformation, brand: 'HUB' } },
    ];
    const zipItem = [
      { ...mockedItem[0], hotelInformation: { ...mockedItem[0].hotelInformation, brand: 'ZIP' } },
    ];
    const { getByTestId } = render(
      <MapViewPIVariant
        items={mockedItem}
        multiSearchParams={mockedMultiSearchParams}
        locale="en"
        partialTranslations={mockedPartialTranslations}
        headerInformation={mockedHeaderInformation}
        baseDataTestId="SRP"
      />
    );
    const mapElement = getByTestId('SRP-mapView');
    const hotelMap = new google.maps.Map(mapElement, mapOptions);

    const hubMarkers = createMarkers(
      hubItem,
      mockedPartialTranslations,
      mockedMultiSearchParams,
      'en',
      hotelMap,
      hotelOverlayRef,
      hubItem[0],
      true
    );
    const zipMarkers = createMarkers(
      zipItem,
      mockedPartialTranslations,
      mockedMultiSearchParams,
      'en',
      hotelMap,
      hotelOverlayRef,
      zipItem[0],
      true
    );

    expect(hubMarkers[0].opts.label.className).toContain('hubMarker');
    expect(zipMarkers[0].opts.label.className).toContain('zipMarker');
  });

  it('does not add the splitViewMarker class when split view is disabled', () => {
    const { getByTestId } = render(
      <MapViewPIVariant
        items={mockedItem}
        multiSearchParams={mockedMultiSearchParams}
        locale="en"
        partialTranslations={mockedPartialTranslations}
        headerInformation={mockedHeaderInformation}
        baseDataTestId="SRP"
      />
    );
    const mapElement = getByTestId('SRP-mapView');
    const hotelMap = new google.maps.Map(mapElement, mapOptions);

    const markers = createMarkers(
      mockedItem,
      mockedPartialTranslations,
      mockedMultiSearchParams,
      'en',
      hotelMap,
      hotelOverlayRef,
      mockedItems[0],
      false
    );

    expect(markers[0].opts.label.className).not.toContain('splitViewMarker');
  });

  it('brings the hovered marker to the front and returns it to the default stack on mouseout', () => {
    const { getByTestId } = render(
      <MapViewPIVariant
        items={mockedItem}
        multiSearchParams={mockedMultiSearchParams}
        locale="en"
        partialTranslations={mockedPartialTranslations}
        headerInformation={mockedHeaderInformation}
        baseDataTestId="SRP"
      />
    );
    const mapElement = getByTestId('SRP-mapView');
    const hotelMap = new google.maps.Map(mapElement, mapOptions);

    const markers = createMarkers(
      mockedItem,
      mockedPartialTranslations,
      mockedMultiSearchParams,
      'en',
      hotelMap,
      hotelOverlayRef,
      mockedItems[0],
      true
    );
    const marker = markers[0];

    fireMarkerEvent(marker, 'mouseover');
    expect(marker.setZIndex).toHaveBeenCalledWith(google.maps.Marker.MAX_ZINDEX + 1);

    fireMarkerEvent(marker, 'mouseout');
    expect(marker.setZIndex).toHaveBeenCalledWith(1);
  });

  it('tracks an analytics event when a marker is clicked', () => {
    const trackSpy = jest.fn();
    window._satellite = { track: trackSpy } as any;

    const { getByTestId } = render(
      <MapViewPIVariant
        items={mockedItem}
        multiSearchParams={mockedMultiSearchParams}
        locale="en"
        partialTranslations={mockedPartialTranslations}
        headerInformation={mockedHeaderInformation}
        baseDataTestId="SRP"
      />
    );
    const mapElement = getByTestId('SRP-mapView');
    const hotelMap = new google.maps.Map(mapElement, mapOptions);

    const markers = createMarkers(
      mockedItem,
      mockedPartialTranslations,
      mockedMultiSearchParams,
      'en',
      hotelMap,
      hotelOverlayRef,
      mockedItems[0],
      true
    );

    fireMarkerEvent(markers[0], 'click');

    expect(trackSpy).toHaveBeenCalledWith('analyticsData_updated_map_marker_click', {
      hotelId: mockedItem[0].hotelId,
    });

    delete (window as any)._satellite;
  });

  it('fires a "Map List View | Clicked Tile" window event each time a marker is clicked in split view', () => {
    const dispatchSpy = jest.spyOn(window, 'dispatchEvent');

    const { getByTestId } = render(
      <MapViewPIVariant
        items={mockedItem}
        multiSearchParams={mockedMultiSearchParams}
        locale="en"
        partialTranslations={mockedPartialTranslations}
        headerInformation={mockedHeaderInformation}
        baseDataTestId="SRP"
      />
    );
    const mapElement = getByTestId('SRP-mapView');
    const hotelMap = new google.maps.Map(mapElement, mapOptions);

    const markers = createMarkers(
      mockedItem,
      mockedPartialTranslations,
      mockedMultiSearchParams,
      'en',
      hotelMap,
      hotelOverlayRef,
      mockedItems[0],
      true
    );

    fireMarkerEvent(markers[0], 'click');
    fireMarkerEvent(markers[0], 'click');

    const tileClickedEvents = dispatchSpy.mock.calls
      .map(([event]) => event as Event)
      .filter((event) => event.type === 'Map List View | Clicked Tile');
    expect(tileClickedEvents).toHaveLength(2);

    dispatchSpy.mockRestore();
  });

  it('does not fire the split-view click events when split view is disabled', () => {
    const dispatchSpy = jest.spyOn(window, 'dispatchEvent');

    const { getByTestId } = render(
      <MapViewPIVariant
        items={mockedItem}
        multiSearchParams={mockedMultiSearchParams}
        locale="en"
        partialTranslations={mockedPartialTranslations}
        headerInformation={mockedHeaderInformation}
        baseDataTestId="SRP"
      />
    );
    const mapElement = getByTestId('SRP-mapView');
    const hotelMap = new google.maps.Map(mapElement, mapOptions);

    const markers = createMarkers(
      mockedItem,
      mockedPartialTranslations,
      mockedMultiSearchParams,
      'en',
      hotelMap,
      hotelOverlayRef,
      mockedItems[0],
      false
    );

    fireMarkerEvent(markers[0], 'click');

    const customEventTypes = dispatchSpy.mock.calls.map(([event]) => (event as Event).type);
    expect(customEventTypes).not.toContain('Map List View | Clicked Tile');
    expect(customEventTypes).not.toContain('map_bubble_clicked');

    dispatchSpy.mockRestore();
  });

  it('fires a map_bubble_clicked window event with the hotelCode and displayed price once the tile is added', async () => {
    const dispatchSpy = jest.spyOn(window, 'dispatchEvent');

    const { getByTestId } = render(
      <>
        <Box id="hotel-overlay" />
        <MapViewPIVariant
          items={mockedItem}
          multiSearchParams={mockedMultiSearchParams}
          locale="en"
          partialTranslations={mockedPartialTranslations}
          headerInformation={mockedHeaderInformation}
          baseDataTestId="SRP"
        />
      </>
    );
    const mapElement = getByTestId('SRP-mapView');
    const hotelMap = new google.maps.Map(mapElement, mapOptions);
    const realHotelOverlayRef = { current: null };
    const setDisplayedItem = jest.fn();

    const markers = createMarkers(
      mockedItem,
      mockedPartialTranslations,
      mockedMultiSearchParams,
      'en',
      hotelMap,
      realHotelOverlayRef,
      setDisplayedItem,
      true
    );

    const netTotal = mockedItem[0].hotelAvailability.lowestRoomRate.netTotal;
    const currency = formatCurrency(mockedItem[0].hotelAvailability.lowestRoomRate.currencyCode);
    const expectedDisplayPrice = formatPriceWithDecimal('en', currency, netTotal);

    fireMarkerEvent(markers[0], 'click');

    await waitFor(() => {
      const bubbleClickedEvent = dispatchSpy.mock.calls
        .map(([event]) => event as CustomEvent)
        .find((event) => event.type === 'map_bubble_clicked');
      expect(bubbleClickedEvent).toBeDefined();
      expect(bubbleClickedEvent?.detail).toEqual({
        hotelCode: mockedItem[0].hotelId,
        displayPrice: expectedDisplayPrice,
      });
    });

    dispatchSpy.mockRestore();
  });

  it('assigns a base z-index of 0 to sold-out markers and a list-position-based z-index otherwise', () => {
    const { getByTestId } = render(
      <MapViewPIVariant
        items={mockedItems}
        multiSearchParams={mockedMultiSearchParams}
        locale="en"
        partialTranslations={mockedPartialTranslations}
        headerInformation={mockedHeaderInformation}
        baseDataTestId="SRP"
      />
    );
    const mapElement = getByTestId('SRP-mapView');
    const hotelMap = new google.maps.Map(mapElement, mapOptions);

    const items = [mockedSoldOutItem[0], mockedItems[0]];
    const markers = createMarkers(
      items,
      mockedPartialTranslations,
      mockedMultiSearchParamsArrivalToday,
      'en',
      hotelMap,
      hotelOverlayRef,
      items[0],
      true
    );

    expect(markers[0].opts.zIndex).toBe(0);
    expect(markers[1].opts.zIndex).toBe(1);
  });
});

describe('getCenterByHotels function', () => {
  it('should return hotel coordinates if there is only a hotel', () => {
    const center = getCenterByHotels(mockedItem);
    const lat = mockedItem[0].hotelInformation.coordinates.latitude;
    const lng = mockedItem[0].hotelInformation.coordinates.longitude;
    expect(center.centerLong).toBe(lng);
    expect(center.centerLat).toBe(lat);
  });
  it('should return center', () => {
    const center = getCenterByHotels(mockedItems);
    const lat =
      (mockedItems[0].hotelInformation.coordinates.latitude +
        mockedItems[1].hotelInformation.coordinates.latitude) /
      2;
    const lng =
      (mockedItems[0].hotelInformation.coordinates.longitude +
        mockedItems[1].hotelInformation.coordinates.longitude) /
      2;
    expect(center.centerLong).toBe(lng);
    expect(center.centerLat).toBe(lat);
  });
});

describe('getItemStatus function', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });
  it('should return status: available', () => {
    const mockedStatus = {
      status: 'available',
    };
    const status = getItemStatus(mockedItems[0], mockedMultiSearchParams);
    expect(status).toStrictEqual(mockedStatus);
  });
  it('should return status: soldOut', () => {
    const mockedStatus = {
      status: 'soldOut',
    };
    const status = getItemStatus(mockedSoldOutItem[0], mockedMultiSearchParams);
    expect(status).toStrictEqual(mockedStatus);
  });
  it('should return status: openSoon', () => {
    const mockedStatus = {
      status: 'openSoon',
    };
    const status = getItemStatus(mockedOpenSoonItem[0], mockedMultiSearchParamsArrivalToday);
    expect(status).toStrictEqual(mockedStatus);
  });
  it('should return status: empty', () => {
    const mockedStatus = {
      status: '',
    };
    const status = getItemStatus(mockedNoStatusItem[0], mockedMultiSearchParamsArrivalToday);
    expect(status).toStrictEqual(mockedStatus);
  });
});

describe('calculateZoomLevel function', () => {
  it('should zoom out further for a single very close hotel than for a single further one', () => {
    const closeZoom = calculateZoomLevel([0.5], 400, 51.5);
    const farZoom = calculateZoomLevel([5], 400, 51.5);
    expect(closeZoom).toBeGreaterThan(farZoom);
  });

  it('should zoom out further as the 4th-closest hotel gets further away, for 10+ results', () => {
    const closerFourth = calculateZoomLevel([1, 2, 3, 4, 5, 6, 7, 8, 9, 10], 400, 51.5);
    const furtherFourth = calculateZoomLevel([1, 2, 3, 8, 9, 10, 11, 12, 13, 14], 400, 51.5);
    expect(furtherFourth).toBeLessThan(closerFourth);
  });

  it('should zoom in further on a wider map for the same distances', () => {
    const narrowMap = calculateZoomLevel([1, 2, 3], 400, 51.5);
    const wideMap = calculateZoomLevel([1, 2, 3], 800, 51.5);
    expect(wideMap).toBeGreaterThan(narrowMap);
  });
});

describe('centerMapOnSearchPlace function', () => {
  const fakeHotelMap = {
    setCenter: jest.fn(),
    setZoom: jest.fn(),
    getDiv: () => ({ offsetWidth: 400 }),
    getCenter: () => ({ lat: () => 51.5 }),
  } as unknown as google.maps.Map;

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should center the map on the searched place when a placeId is provided', async () => {
    const location = { lat: () => 51.5, lng: () => -0.13 } as google.maps.LatLng;
    mockGetDetails.mockImplementation((_request, callback) => {
      callback({ geometry: { location } }, google.maps.places.PlacesServiceStatus.OK);
    });

    await centerMapOnSearchPlace(fakeHotelMap, mockedMultiSearchParams, mockedItems);

    expect(mockPlacesService).toHaveBeenCalledWith(fakeHotelMap);
    expect(mockGetDetails).toHaveBeenCalledWith(
      { placeId: mockedMultiSearchParams.placeId, fields: ['geometry'] },
      expect.any(Function)
    );
    expect(fakeHotelMap.setCenter).toHaveBeenCalledWith(location);
    expect(fakeHotelMap.setZoom).toHaveBeenCalled();
  });

  it('should not move the map when the PlacesService lookup fails', async () => {
    mockGetDetails.mockImplementation((_request, callback) => {
      callback(null, 'ERROR');
    });

    await centerMapOnSearchPlace(fakeHotelMap, mockedMultiSearchParams, mockedItems);

    expect(fakeHotelMap.setCenter).not.toHaveBeenCalled();
  });

  it('should fall back to the coordinates when the places library fails to load', async () => {
    (google.maps.importLibrary as jest.Mock).mockRejectedValueOnce(new Error('failed to load'));

    await centerMapOnSearchPlace(
      fakeHotelMap,
      { ...mockedMultiSearchParams, coordinates: '51.5,-0.13' },
      mockedItems
    );

    expect(mockPlacesService).not.toHaveBeenCalled();
    expect(fakeHotelMap.setCenter).toHaveBeenCalledWith({ lat: 51.5, lng: -0.13 });
  });

  it('should fall back to the coordinates when PlacesService is unavailable', async () => {
    const originalPlacesService = google.maps.places.PlacesService;
    // @ts-expect-error simulating the Places library not exposing PlacesService
    delete google.maps.places.PlacesService;

    await centerMapOnSearchPlace(
      fakeHotelMap,
      { ...mockedMultiSearchParams, coordinates: '51.5,-0.13' },
      mockedItems
    );

    expect(fakeHotelMap.setCenter).toHaveBeenCalledWith({ lat: 51.5, lng: -0.13 });

    google.maps.places.PlacesService = originalPlacesService;
  });

  it('should center on parsed coordinates when no placeId is provided', async () => {
    await centerMapOnSearchPlace(
      fakeHotelMap,
      { ...mockedMultiSearchParams, placeId: '', coordinates: '51.5,-0.13' },
      mockedItems
    );

    expect(mockPlacesService).not.toHaveBeenCalled();
    expect(fakeHotelMap.setCenter).toHaveBeenCalledWith({ lat: 51.5, lng: -0.13 });
  });

  it('should do nothing when neither placeId nor coordinates are provided', async () => {
    await centerMapOnSearchPlace(
      fakeHotelMap,
      { ...mockedMultiSearchParams, placeId: '', coordinates: undefined },
      mockedItems
    );

    expect(mockPlacesService).not.toHaveBeenCalled();
    expect(fakeHotelMap.setCenter).not.toHaveBeenCalled();
  });
});
