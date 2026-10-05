import useSatelliteTrack from './use-satellite-track';

describe('useSatelliteTrack', () => {
  beforeEach(() => {
    window._satellite = { track: jest.fn() };
    window.__satelliteLoaded = true;
  });

  afterEach(() => {
    window._satellite = undefined;
    window.__satelliteLoaded = false;
    jest.clearAllMocks();
  });

  it('calls window._satellite.track when satellite is loaded', () => {
    const satelliteTrack = useSatelliteTrack();
    satelliteTrack('Test Event');
    expect(window._satellite.track).toHaveBeenCalledWith('Test Event');
  });

  it('does not call track if satellite is not loaded', () => {
    window.__satelliteLoaded = false;
    const satelliteTrack = useSatelliteTrack();
    satelliteTrack('Test Event');
    expect(window._satellite.track).not.toHaveBeenCalled();
  });

  it('does not throw if _satellite is missing', () => {
    window._satellite = undefined;
    const satelliteTrack = useSatelliteTrack();
    expect(() => satelliteTrack('Test Event')).not.toThrow();
  });
});
