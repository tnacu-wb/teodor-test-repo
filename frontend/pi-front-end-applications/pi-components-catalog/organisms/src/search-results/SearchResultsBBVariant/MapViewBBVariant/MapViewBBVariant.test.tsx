import { Box } from '@chakra-ui/react';
import { Circle, InfoWindow } from '@googlemaps/jest-mocks';
import '@testing-library/jest-dom';
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
import MapViewBBVariant, {
  createMarkers,
  getCenterByHotels,
  getItemStatus,
} from './MapViewBBVariant.component';

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
const mockGoogleMarker = jest.fn().mockImplementation((opts) => ({
  opts,
  getPosition: () => jest.fn(),
  addListener: () => jest.fn(),
}));

const setupGoogleMock = () => {
  const google = {
    maps: {
      Map: mockGoogleMap,
      Marker: mockGoogleMarker,
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
      <MapViewBBVariant
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
  it('should load Google Maps script', () => {
    render(
      <MapViewBBVariant
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
        <MapViewBBVariant
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
        <MapViewBBVariant
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
      <MapViewBBVariant
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
      <MapViewBBVariant
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
      <MapViewBBVariant
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
      mockedItems[0]
    );
    expect(markers).toHaveLength(1);
  });
  it('should return 1 marker with Sold out label', async () => {
    const { getByTestId } = render(
      <MapViewBBVariant
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
      mockedSoldOutItem
    );
    expect(markers).toHaveLength(1);
    expect(markers[0].opts.label.text).toBe(
      mockedPartialTranslations.searchInformation.content.results.result.fullyBooked
    );
  });
  it('should return 1 marker with Open soon label', async () => {
    const { getByTestId } = render(
      <MapViewBBVariant
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
      mockedOpenSoonItem
    );
    expect(markers).toHaveLength(1);
    expect(markers[0].opts.label.text).toBe(
      mockedPartialTranslations.searchInformation.content.results.result.openingSoon
    );
  });
  it('should return 2 markers', () => {
    const { getByTestId } = render(
      <MapViewBBVariant
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
      mockedItems[0]
    );
    expect(markers).toHaveLength(2);
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
