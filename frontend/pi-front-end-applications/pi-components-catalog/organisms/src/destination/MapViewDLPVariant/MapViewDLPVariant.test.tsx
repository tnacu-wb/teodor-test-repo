import { Circle, InfoWindow } from '@googlemaps/jest-mocks';
import '@testing-library/jest-dom';
import React from 'react';

import { mockControlPositions } from '../../mockData/mockResponse';
import { render, screen, waitFor } from '../../utils/test-utils';
import MapViewDLPVariant from './MapViewDLPVariant.component';

const mock = {
  items: [
    {
      brand: 'PI',
      coordinates: {
        latitude: 52.03957,
        longitude: -0.75216,
      },
    },
    {
      brand: 'HUB',
      coordinates: {
        latitude: 52.043225,
        longitude: -0.749329,
      },
    },
    {
      brand: 'ZIP',
      coordinates: {
        latitude: 53.043225,
        longitude: -0.849329,
      },
    },
  ],
  icons: {
    'icon.pin-city': '/content/dam/global/icons/common/pin-city.svg',
    'icon.pin-default': '/content/dam/global/icons/common/pin-purple.svg',
    'icon.pin-zip': '/content/dam/global/icons/common/pin-zip.svg',
    'icon.pin-hub': '/content/dam/global/icons/common/pin-hub.svg',
  },
  latitude: 52.042622,
  longitude: -0.758008,
};

const mockGoogleMapsLoader = jest.fn().mockImplementation(() => Promise.resolve({}));
jest.mock('@googlemaps/js-api-loader', () => ({
  ...jest.requireActual('@googlemaps/js-api-loader'),
  Loader: jest.fn().mockImplementation(() => ({
    load: mockGoogleMapsLoader,
  })),
}));

const mockGoogleMap = jest
  .fn()
  .mockImplementation((mapDiv, opts) => ({ mapDiv, opts, fitBounds: () => jest.fn() }));
const mockGooglePoint = jest.fn().mockImplementation((a, b) => ({ a, b }));
const mockAdvancedMarkerElement = jest.fn().mockImplementation((opts) => ({
  opts,
  getPosition: () => jest.fn(),
  addListener: () => jest.fn(),
}));

const setupGoogleMock = () => {
  const google = {
    maps: {
      Map: mockGoogleMap,
      Point: mockGooglePoint,
      ControlPosition: mockControlPositions,
      Circle: Circle,
      InfoWindow: InfoWindow,
      LatLngBounds: jest.fn().mockImplementation((a, b) => ({
        a,
        b,
        extend: (c: any) => ({ c }),
      })),
      event: jest.fn(),
      importLibrary: jest.fn().mockImplementation((libraryName) => {
        if (libraryName === 'marker') {
          return Promise.resolve({
            AdvancedMarkerElement: mockAdvancedMarkerElement,
          });
        }
        return Promise.resolve({});
      }),
    },
  };

  global.window.google = google as any;
};

setupGoogleMock();
const mapOptions = {
  center: {
    lat: 52.042622,
    lng: -0.758008,
  },
  zoom: 11,
  mapTypeControl: true,
  mapTypeControlOptions: {
    position: google.maps.ControlPosition.LEFT_BOTTOM,
  },
  streetViewControl: true,
  zoomControl: true,
  fullscreenControl: true,
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

describe('MapView component', () => {
  beforeAll(() => {
    setupGoogleMock();
  });
  afterEach(() => {
    jest.clearAllMocks();
  });
  it('should render the MapView component', () => {
    const { getByTestId } = render(
      <MapViewDLPVariant
        items={mock.items}
        icons={mock.icons}
        baseDataTestId="DLP"
        latitude={mock.latitude}
        longitude={mock.longitude}
      />
    );
    expect(getByTestId('DLP-mapView')).toBeInTheDocument();
  });
  it('should load Google Maps script', () => {
    render(
      <MapViewDLPVariant
        items={mock.items}
        icons={mock.icons}
        baseDataTestId="DLP"
        latitude={mock.latitude}
        longitude={mock.longitude}
      />
    );
    expect(mockGoogleMapsLoader).toHaveBeenCalled();
  });
  it('should instantiate Google Maps with initial parameters', async () => {
    render(
      <MapViewDLPVariant
        items={mock.items}
        icons={mock.icons}
        baseDataTestId="DLP"
        latitude={mock.latitude}
        longitude={mock.longitude}
      />
    );
    const mapElement = screen.getByTestId('DLP-mapView');
    await waitFor(() => {
      expect(mockGoogleMap).toHaveBeenCalledWith(mapElement, mapOptions);
    });
  });
  it('should create 3 hotel markers and one city marker', async () => {
    render(
      <MapViewDLPVariant
        items={mock.items}
        icons={mock.icons}
        baseDataTestId="DLP"
        latitude={mock.latitude}
        longitude={mock.longitude}
      />
    );

    await waitFor(() => {
      expect(mockAdvancedMarkerElement).toBeCalledTimes(4);
    });
  });
});
