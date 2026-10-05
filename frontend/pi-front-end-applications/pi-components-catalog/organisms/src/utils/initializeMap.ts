export default function initializeMap(
  latitude: number,
  longitude: number,
  mapReference: any,
  mapId?: string,
  extraOptions: Partial<google.maps.MapOptions> = {}
) {
  return new google.maps.Map(mapReference.current, {
    center: { lat: latitude, lng: longitude },
    mapId: mapId || 'DEMO_MAP_ID',
    zoom: 11,
    mapTypeControl: true,
    mapTypeControlOptions: {
      position: google.maps.ControlPosition.LEFT_BOTTOM,
    },
    streetViewControl: true,
    zoomControl: true,
    fullscreenControl: true,
    ...extraOptions,
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
  });
}
