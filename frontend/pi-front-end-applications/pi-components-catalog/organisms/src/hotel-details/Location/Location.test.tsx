import { initialize } from '@googlemaps/jest-mocks';
import '@testing-library/jest-dom';
import type { Coordinates } from '@whitbread-eos/api';
import { Channel } from '@whitbread-eos/api';
import { analytics, useElementVisited, useFeatureToggle } from '@whitbread-eos/utils';

import { render, waitFor } from '../../utils/test-utils';
import LocationComponent, { Props } from './Location.component';
import LocationContainer from './Location.container';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useStaticHotelInformation: () => ({
    brand: mockData.brand,
    coordinates: mockData.coordinates,
    directions: 'test',
    address: mockAddress,
    satNavDirections: mockSatNavDirections,
    whatThreeWords: mockWhatThreeWords,
    transportInformation: ['test'],
  }),
  useElementVisited: jest.fn().mockImplementation(() => true),
  useFeatureToggle: jest.fn().mockImplementation(() => ({
    release_pi_bb_show_google_static_map: false,
  })),
  analytics: { update: jest.fn() },
}));

const mockAddress = {
  addressLine1: '27-29 Red Lion Street',
  addressLine2: 'Holborn',
  addressLine3: 'London',
  addressLine4: 'London',
  country: 'England',
  postalCode: 'WC1R 4PS',
};
const mockSatNavDirections = 'WC1R 4PS';
const mockWhatThreeWords = '///truth.drums.bowls';
const mockGetStreetView = jest.fn();

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
  getStreetView: mockGetStreetView,
}));
const mockGoogleMarker = jest.fn().mockImplementation((opts) => ({ opts }));
const mockGooglePoint = jest.fn().mockImplementation((a, b) => ({ a, b }));
const setupGoogleMock = () => {
  const google = {
    maps: {
      Map: mockGoogleMap,
      Marker: mockGoogleMarker,
      Point: mockGooglePoint,
      event: {
        addListener: jest.fn(),
      },
    },
  };

  window.google = google as any;
};

const customMapMarkerStyles = {
  path: 'M45.0585 21C45.0585 32.598 22.8232 63.5 22.8232 63.5C22.8232 63.5 0.587891 32.598 0.587891 21C0.587891 9.40202 10.543 0 22.8232 0C35.1034 0 45.0585 9.40202 45.0585 21Z',
  fillOpacity: 1,
  strokeWeight: 2,
  strokeColor: '#FFFFFF',
  rotation: 0,
  scale: 0.5,
};

const mockData = {
  coordinates: {
    latitude: -34.397,
    longitude: 150.644,
  },
  brand: 'PI',
};

const locationProps: Props = {
  isLoading: false,
  isError: false,
  error: null,
  data: mockData,
};

describe('Location', () => {
  beforeEach(() => {
    initialize();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('renders LocationContainer with default props', () => {
    const { getByTestId } = render(<LocationContainer />);
    expect(getByTestId('hdp_location-Section')).toBeInTheDocument();
  });

  it('should render LocationContainer', () => {
    const { getByTestId } = render(<LocationContainer />);
    expect(getByTestId('hdp_location-Section')).toBeInTheDocument();
  });

  it('should render LocationContainer if channel is CCUI', () => {
    const { getByTestId } = render(<LocationContainer channel={Channel.Ccui} />);
    expect(getByTestId('hdp_location-Section')).toBeInTheDocument();
  });

  it('should render a loading message if isLoading prop is true', () => {
    const { getByText } = render(<LocationComponent {...locationProps} isLoading />);
    expect(getByText('searchresults.list.hotel.loading')).toBeInTheDocument();
  });

  it('should render an error message if isError prop is true', () => {
    const { getByText } = render(
      <LocationComponent {...locationProps} error={{ message: 'Error' }} isError />
    );
    expect(getByText('Error')).toBeInTheDocument();
  });

  it('should render nothing if no coordinates are passed', () => {
    const { queryByTestId } = render(
      <LocationComponent
        {...locationProps}
        data={{ brand: 'PI', coordinates: {} as Coordinates }}
      />
    );
    expect(queryByTestId('hdp_location')).toBeNull();
  });

  it('should load Google Maps script', () => {
    render(<LocationComponent {...locationProps} />);
    expect(mockGoogleMapsLoader).toHaveBeenCalled();
  });

  it('should create Google Map based on given parameters', async () => {
    setupGoogleMock();

    const { getByTestId } = render(<LocationComponent {...locationProps} />);
    const mapDiv = getByTestId('hotel-google-map');
    const mapOptions = {
      center: {
        lat: mockData.coordinates.latitude,
        lng: mockData.coordinates.longitude,
      },
      zoom: 13,
    };

    await waitFor(() => {
      expect(mockGoogleMap).toHaveBeenCalledWith(mapDiv, mapOptions);
    });
  });

  it('should create Google Marker for PI hotel based on given parameters', async () => {
    setupGoogleMock();

    const { getByTestId } = render(<LocationComponent {...locationProps} />);
    const mapDiv = getByTestId('hotel-google-map');
    const mapOptions = {
      center: {
        lat: mockData.coordinates.latitude,
        lng: mockData.coordinates.longitude,
      },
      zoom: 13,
    };

    const hotelMap = new google.maps.Map(mapDiv, mapOptions);
    const PIHotelMarker = {
      ...customMapMarkerStyles,
      fillColor: '#511E62',
      anchor: new google.maps.Point(22, 72),
    };
    const markerOptions = {
      position: {
        lat: mockData.coordinates.latitude,
        lng: mockData.coordinates.longitude,
      },
      map: hotelMap,
      icon: PIHotelMarker,
    };

    await waitFor(() => {
      expect(mockGoogleMarker).toHaveBeenCalledWith(markerOptions);
    });
  });

  it('should create Google Marker for HUB hotel based on given parameters', async () => {
    setupGoogleMock();

    locationProps.data.brand = 'HUB';
    const { getByTestId } = render(<LocationComponent {...locationProps} />);
    const mapDiv = getByTestId('hotel-google-map');
    const mapOptions = {
      center: {
        lat: mockData.coordinates.latitude,
        lng: mockData.coordinates.longitude,
      },
      zoom: 13,
    };

    const hotelMap = new google.maps.Map(mapDiv, mapOptions);
    const HUBHotelMarker = {
      ...customMapMarkerStyles,
      fillColor: '#BDD500',
      anchor: new google.maps.Point(22, 72),
    };
    const markerOptions = {
      position: {
        lat: mockData.coordinates.latitude,
        lng: mockData.coordinates.longitude,
      },
      map: hotelMap,
      icon: HUBHotelMarker,
    };

    await waitFor(() => {
      expect(mockGoogleMarker).toHaveBeenCalledWith(markerOptions);
    });
  });

  it('should create Google Marker for ZIP hotel based on given parameters', async () => {
    setupGoogleMock();

    locationProps.data.brand = 'ZIP';
    const { getByTestId } = render(<LocationComponent {...locationProps} />);
    const mapDiv = getByTestId('hotel-google-map');
    const mapOptions = {
      center: {
        lat: mockData.coordinates.latitude,
        lng: mockData.coordinates.longitude,
      },
      zoom: 13,
    };

    const hotelMap = new google.maps.Map(mapDiv, mapOptions);
    const ZIPHotelMarker = {
      ...customMapMarkerStyles,
      fillColor: '#FC0F42',
      anchor: new google.maps.Point(22, 72),
    };
    const markerOptions = {
      position: {
        lat: mockData.coordinates.latitude,
        lng: mockData.coordinates.longitude,
      },
      map: hotelMap,
      icon: ZIPHotelMarker,
    };

    await waitFor(() => {
      expect(mockGoogleMarker).toHaveBeenCalledWith(markerOptions);
    });
  });

  it('should render the component with no data', () => {
    const { queryByTestId } = render(
      <LocationComponent isLoading={false} error={null} isError={false} />
    );
    expect(queryByTestId('hdp_location')).toBeNull();
  });

  it('should not load Google Maps script if not scrolled to map', () => {
    (useElementVisited as jest.Mock).mockImplementation(() => {
      return false;
    });
    render(<LocationComponent {...locationProps} />);
    expect(mockGoogleMapsLoader).not.toHaveBeenCalled();
  });

  it('should load Google Maps script and update mapLoaded analytics if scrolled to map', () => {
    (useElementVisited as jest.Mock).mockImplementation(() => {
      return true;
    });
    render(<LocationComponent {...locationProps} />);
    expect(analytics.update).toHaveBeenCalledWith({
      analyticsDataSearchResult: { mapLoaded: true },
    });
    expect(mockGoogleMapsLoader).toHaveBeenCalled();
  });
});

describe('Location - Static Map Flow', () => {
  beforeEach(() => {
    initialize();
    (useFeatureToggle as jest.Mock).mockReturnValue({
      release_pi_bb_show_google_static_map: true,
    });
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should render static map initially and NOT load Google map', () => {
    const { queryByTestId, getByTestId } = render(<LocationComponent {...locationProps} />);

    expect(getByTestId('hotel-location-section')).toBeInTheDocument();
    expect(queryByTestId('hotel-google-map')).not.toBeInTheDocument();
    expect(mockGoogleMapsLoader).not.toHaveBeenCalled();
  });

  it('should load Google map on click', async () => {
    setupGoogleMock();

    const { getByTestId } = render(<LocationComponent {...locationProps} />);

    const section = getByTestId('hotel-location-section');
    const clickableDiv = section.querySelector('[style*="cursor: pointer"]') || section.firstChild;

    clickableDiv?.dispatchEvent(new Event('touchstart', { bubbles: true }));

    await waitFor(() => {
      expect(mockGoogleMapsLoader).toHaveBeenCalled();
    });
  });

  it('should load Google map on touch start', async () => {
    setupGoogleMock();

    const { getByTestId } = render(<LocationComponent {...locationProps} />);

    const section = getByTestId('hotel-location-section');

    const clickableDiv = section.querySelector('[style*="cursor: pointer"]') || section.firstChild;

    clickableDiv?.dispatchEvent(new Event('touchstart', { bubbles: true }));

    await waitFor(() => {
      expect(mockGoogleMapsLoader).toHaveBeenCalled();
    });
  });
});
