import HotelDirections from '.';
import { initialize } from '@googlemaps/jest-mocks';
import '@testing-library/jest-dom';
import React from 'react';

import { render, waitFor } from '../../utils/test-utils';

const mockGoogleMapsLoader = jest.fn().mockImplementation(() => Promise.resolve({}));
jest.mock('@googlemaps/js-api-loader', () => ({
  ...jest.requireActual('@googlemaps/js-api-loader'),
  Loader: jest.fn().mockImplementation(() => ({
    load: mockGoogleMapsLoader,
  })),
}));

const mockGoogleMap = jest.fn().mockImplementation((mapDiv, opts) => ({ mapDiv, opts }));
const mockGoogleMarker = jest.fn().mockImplementation((opts) => ({ opts }));
const mockGooglePoint = jest.fn().mockImplementation((a, b) => ({ a, b }));
const setupGoogleMock = () => {
  const google = {
    maps: {
      Map: mockGoogleMap,
      Marker: mockGoogleMarker,
      Point: mockGooglePoint,
    },
  };

  window.google = google as any;
};

const mockData = {
  data: {
    hotelInformation: {
      brand: 'PI',
      coordinates: {
        latitude: 53.46524,
        longitude: -2.28816,
      },
      directions:
        'Exit M60 at Junction 7, follow A56 Chester Road and signs for Manchester United Football Ground. After approx 1 mile you will pass a Ford garage on your left, go through 2 sets of traffic lights keeping in the left hand lane and following signs for Salford Quays. At the third set, take the second left onto Trafford Wharf Road, go through the lights and the hotel is on your right. Tram is located nearby, bus number 250 to centre.',
    },
  },
  apiKey: 'apiKey',
  getFillColorMarker: () => '#511E62',
  t: (key: string) => {
    switch (key) {
      case 'account.dashboard.directions':
        return 'Hotel Directions';
      default:
        return 'default';
    }
  },
  currentLang: 'en',
};

const customMapMarkerStyles = {
  path: 'M45.0585 21C45.0585 32.598 22.8232 63.5 22.8232 63.5C22.8232 63.5 0.587891 32.598 0.587891 21C0.587891 9.40202 10.543 0 22.8232 0C35.1034 0 45.0585 9.40202 45.0585 21Z',
  fillColor: '#511E62',
  fillOpacity: 1,
  strokeWeight: 2,
  strokeColor: '#FFFFFF',
  rotation: 0,
  scale: 0.5,
};

describe('HotelDirections', () => {
  beforeEach(() => {
    initialize();
  });

  it('should render hotel directions', () => {
    const { getByTestId, getAllByTestId } = render(<HotelDirections {...mockData} />);
    expect(getByTestId('hotelDirections-label')).toBeInTheDocument();
    expect(getAllByTestId('hotelDirections-map')[0]).toBeInTheDocument();
    expect(getByTestId('hotelDirections-directions')).toBeInTheDocument();
  });

  it('should load Google Maps script', () => {
    render(<HotelDirections {...mockData} />);
    expect(mockGoogleMapsLoader).toHaveBeenCalled();
  });

  it('should create Google Map based on given parameters', async () => {
    setupGoogleMock();

    const { getAllByTestId } = render(<HotelDirections {...mockData} />);
    const mapDiv = getAllByTestId('hotelDirections-map')[0];
    const mapOptions = {
      center: {
        lat: mockData.data.hotelInformation.coordinates.latitude,
        lng: mockData.data.hotelInformation.coordinates.longitude,
      },
      zoom: 13,
    };

    await waitFor(() => {
      expect(mockGoogleMap).toHaveBeenCalledWith(mapDiv, mapOptions);
    });
  });

  it('should create Google Marker for PI hotel based on given parameters', async () => {
    setupGoogleMock();

    const { getAllByTestId } = render(<HotelDirections {...mockData} />);
    const mapDiv = getAllByTestId('hotelDirections-map')[0];
    const mapOptions = {
      center: {
        lat: mockData.data.hotelInformation.coordinates.latitude,
        lng: mockData.data.hotelInformation.coordinates.longitude,
      },
      zoom: 13,
    };

    const hotelMap = new google.maps.Map(mapDiv, mapOptions);
    const PIHotelMarker = {
      ...customMapMarkerStyles,
      anchor: new google.maps.Point(15, 30),
    };
    const markerOptions = {
      position: {
        lat: mockData.data.hotelInformation.coordinates.latitude,
        lng: mockData.data.hotelInformation.coordinates.longitude,
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

    mockData.data.hotelInformation.brand = 'HUB';
    const { getAllByTestId } = render(<HotelDirections {...mockData} />);
    const mapDiv = getAllByTestId('hotelDirections-map')[0];
    const mapOptions = {
      center: {
        lat: mockData.data.hotelInformation.coordinates.latitude,
        lng: mockData.data.hotelInformation.coordinates.longitude,
      },
      zoom: 13,
    };

    const hotelMap = new google.maps.Map(mapDiv, mapOptions);
    const HUBHotelMarker = {
      ...customMapMarkerStyles,
      fillColor: '#BDD500',
      anchor: new google.maps.Point(15, 30),
    };
    const markerOptions = {
      position: {
        lat: mockData.data.hotelInformation.coordinates.latitude,
        lng: mockData.data.hotelInformation.coordinates.longitude,
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

    mockData.data.hotelInformation.brand = 'ZIP';
    const { getAllByTestId } = render(<HotelDirections {...mockData} />);
    const mapDiv = getAllByTestId('hotelDirections-map')[0];
    const mapOptions = {
      center: {
        lat: mockData.data.hotelInformation.coordinates.latitude,
        lng: mockData.data.hotelInformation.coordinates.longitude,
      },
      zoom: 13,
    };

    const hotelMap = new google.maps.Map(mapDiv, mapOptions);
    const ZIPHotelMarker = {
      ...customMapMarkerStyles,
      fillColor: '#FC0F42',
      anchor: new google.maps.Point(15, 30),
    };
    const markerOptions = {
      position: {
        lat: mockData.data.hotelInformation.coordinates.latitude,
        lng: mockData.data.hotelInformation.coordinates.longitude,
      },
      map: hotelMap,
      icon: ZIPHotelMarker,
    };

    await waitFor(() => {
      expect(mockGoogleMarker).toHaveBeenCalledWith(markerOptions);
    });
  });
});
