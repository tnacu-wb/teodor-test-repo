import { Loader } from '@googlemaps/js-api-loader';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { useElementVisited, useScreenSize } from '@whitbread-eos/utils';
import { analytics } from '@whitbread-eos/utils';

import LocationWithDynamicMap from './LocationWithDynamicMap.component';

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => ({
    t: (key: string) => {
      const map: Record<string, string> = {
        'hoteldetails.viewmap': 'View map',
        'searchresults.filter.hideMap': 'Close map',
      };
      return map[key] || key;
    },
  }),
  withTranslation: () => (Component: any) => {
    Component.defaultProps = {
      ...Component.defaultProps,
      t: (key: string) => key,
    };
    return Component;
  },
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useElementVisited: jest.fn(),
  useScreenSize: jest.fn(),
  analytics: {
    update: jest.fn(),
  },
}));

jest.mock('@googlemaps/js-api-loader', () => ({
  Loader: jest.fn(),
}));

jest.mock('next/config', () => () => ({
  publicRuntimeConfig: {
    NEXT_PUBLIC_GOOGLE_MAPS_API_KEY: 'test-key',
  },
}));

jest.mock('./Location.component', () => ({
  LocationHeading: () => <div>Location Heading</div>,
  LocationInfoSection: () => <div>Info Section</div>,
  getMarkerIconByHotelType: jest.fn(),
  mapBoxStyles: {},
}));

beforeAll(() => {
  global.google = {
    maps: {
      Map: jest.fn(),
      Marker: jest.fn(),
    },
  };
});

const defaultProps = {
  isLoading: false,
  isError: false,
  error: null,
  data: {
    coordinates: { latitude: 12.34, longitude: 56.78 },
    brand: 'PI',
  },
};

describe('LocationWithDynamicMap', () => {
  beforeEach(() => {
    jest.clearAllMocks();

    (useElementVisited as jest.Mock).mockReturnValue(true);
    (useScreenSize as jest.Mock).mockReturnValue({ isLessThanMd: false });

    (Loader as jest.Mock).mockImplementation(() => ({
      load: jest.fn().mockResolvedValue(undefined),
    }));
  });

  it('renders loading state', () => {
    render(<LocationWithDynamicMap {...defaultProps} isLoading />);

    expect(screen.getByText('searchresults.list.hotel.loading')).toBeInTheDocument();
  });

  it('renders error state', () => {
    render(
      <LocationWithDynamicMap {...defaultProps} isError error={new Error('Something went wrong')} />
    );

    expect(screen.getByText('Something went wrong')).toBeInTheDocument();
  });

  it('renders nothing when no data', () => {
    const { container } = render(<LocationWithDynamicMap {...defaultProps} data={null as any} />);

    expect(container.firstChild).toBeNull();
  });

  it('renders heading and info section', () => {
    render(<LocationWithDynamicMap {...defaultProps} />);

    expect(screen.getByText('Location Heading')).toBeInTheDocument();
    expect(screen.getByText('Info Section')).toBeInTheDocument();
  });

  it('toggles map on click', () => {
    render(<LocationWithDynamicMap {...defaultProps} />);

    const toggle = screen.getByText('View map');
    fireEvent.click(toggle);

    expect(screen.getByText('Close map')).toBeInTheDocument();
  });

  it('shows spinner when map is loading', () => {
    render(<LocationWithDynamicMap {...defaultProps} />);

    fireEvent.click(screen.getByText('View map'));

    expect(screen.getByText('booking.loading')).toBeInTheDocument();
  });

  it('renders mobile map below toggle', () => {
    (useScreenSize as jest.Mock).mockReturnValue({ isLessThanMd: true });

    render(<LocationWithDynamicMap {...defaultProps} />);

    fireEvent.click(screen.getByText('View map'));

    expect(screen.getByText('Info Section')).toBeInTheDocument();
  });

  it('calls analytics when map loads', async () => {
    render(<LocationWithDynamicMap {...defaultProps} />);

    fireEvent.click(screen.getByText('View map'));

    await waitFor(() => {
      expect(analytics.update).toHaveBeenCalled();
    });
  });

  it('initializes google map when loaded', async () => {
    render(<LocationWithDynamicMap {...defaultProps} />);

    fireEvent.click(screen.getByText('View map'));

    await waitFor(() => {
      expect(global.google.maps.Map).toHaveBeenCalled();
      expect(global.google.maps.Marker).toHaveBeenCalled();
    });
  });
});
