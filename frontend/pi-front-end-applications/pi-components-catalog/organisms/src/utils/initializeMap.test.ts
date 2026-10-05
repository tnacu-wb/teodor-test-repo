import initializeMap from './initializeMap';

const mockGoogleMap = jest.fn().mockImplementation((mapDiv, opts) => ({ mapDiv, opts }));

const setupGoogleMock = () => {
  (global as any).google = {
    maps: {
      Map: mockGoogleMap,
      ControlPosition: { LEFT_BOTTOM: 'LEFT_BOTTOM' },
    },
  };
};

describe('initializeMap', () => {
  beforeEach(() => {
    setupGoogleMock();
    jest.clearAllMocks();
  });

  it('creates a map centered on the given coordinates with the default mapId and zoom', () => {
    const mapReference = { current: 'div-element' };

    initializeMap(51.5, -0.13, mapReference);

    expect(mockGoogleMap).toHaveBeenCalledWith(
      'div-element',
      expect.objectContaining({
        center: { lat: 51.5, lng: -0.13 },
        mapId: 'DEMO_MAP_ID',
        zoom: 11,
      })
    );
  });

  it('falls back to the default mapId when an empty string is provided', () => {
    const mapReference = { current: 'div-element' };

    initializeMap(51.5, -0.13, mapReference, '');

    expect(mockGoogleMap).toHaveBeenCalledWith(
      'div-element',
      expect.objectContaining({ mapId: 'DEMO_MAP_ID' })
    );
  });

  it('uses a custom mapId when provided', () => {
    const mapReference = { current: 'div-element' };

    initializeMap(51.5, -0.13, mapReference, 'CUSTOM_MAP_ID');

    expect(mockGoogleMap).toHaveBeenCalledWith(
      'div-element',
      expect.objectContaining({ mapId: 'CUSTOM_MAP_ID' })
    );
  });

  it('hides POI labels and geometry via the styles array', () => {
    const mapReference = { current: 'div-element' };

    initializeMap(51.5, -0.13, mapReference);

    const options = mockGoogleMap.mock.calls[0][1];
    expect(options.styles).toEqual(
      expect.arrayContaining([
        expect.objectContaining({ featureType: 'poi', elementType: 'labels' }),
        expect.objectContaining({ featureType: 'poi.hotel', elementType: 'all' }),
      ])
    );
  });

  it('lets extraOptions override the defaults', () => {
    const mapReference = { current: 'div-element' };

    initializeMap(51.5, -0.13, mapReference, undefined, {
      zoom: 14.75,
      streetViewControl: false,
      fullscreenControl: false,
    });

    const options = mockGoogleMap.mock.calls[0][1];
    expect(options.zoom).toBe(14.75);
    expect(options.streetViewControl).toBe(false);
    expect(options.fullscreenControl).toBe(false);
  });

  it('does not let extraOptions override the POI-hiding styles', () => {
    const mapReference = { current: 'div-element' };

    initializeMap(51.5, -0.13, mapReference, undefined, {
      styles: [{ featureType: 'road', stylers: [{ visibility: 'on' }] }],
    } as any);

    const options = mockGoogleMap.mock.calls[0][1];
    expect(options.styles).toEqual(
      expect.arrayContaining([expect.objectContaining({ featureType: 'poi' })])
    );
  });
});
