interface SatelliteTrack {
  (eventName: string): void;
}

export default function useSatelliteTrack(): SatelliteTrack {
  return (eventName: string) => {
    if (typeof window !== 'undefined' && window.__satelliteLoaded && window._satellite) {
      window._satellite.track(eventName);
    }
  };
}
