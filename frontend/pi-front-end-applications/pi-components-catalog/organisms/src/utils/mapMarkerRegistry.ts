interface MarkerLabel {
  text: string;
  className: string;
}

interface RegisteredMarker {
  marker: google.maps.Marker;
  baseZIndex: number;
  markerLabel?: MarkerLabel;
}

const markerRegistry = new Map<string, RegisteredMarker>();

const HOVERED_MARKER_Z_INDEX = 1000;
const ACTIVE_HOVER_CLASS = 'activeHover';
const ACTIVE_MARKER_CLASS = 'activeMarker';

export function registerMapMarker(
  hotelId: string,
  marker: google.maps.Marker,
  baseZIndex = 0,
  markerLabel?: MarkerLabel
) {
  markerRegistry.set(hotelId, { marker, baseZIndex, markerLabel });
}

export function updateMarkerPriceLabel(hotelId: string, text: string) {
  const registered = markerRegistry.get(hotelId);
  if (!registered) return;
  const { marker, markerLabel } = registered;
  if (markerLabel) markerLabel.text = text;

  const currentLabel = marker.getLabel();
  if (!currentLabel || typeof currentLabel === 'string') return;
  marker.setLabel({ ...currentLabel, text });
}

export function clearMapMarkerRegistry() {
  markerRegistry.clear();
}

export function removeAllMarkersFromMap() {
  markerRegistry.forEach(({ marker }) => marker.setMap(null));
  markerRegistry.clear();
}

function getMarkerClassNames(marker: google.maps.Marker): Set<string> {
  const label = marker.getLabel();
  if (!label || typeof label === 'string') return new Set();
  return new Set((label.className ?? '').split(' ').filter(Boolean));
}

function toggleMarkerLabelClass(marker: google.maps.Marker, className: string, isActive: boolean) {
  const label = marker.getLabel();
  if (!label || typeof label === 'string') return;

  const classNames = getMarkerClassNames(marker);
  isActive ? classNames.add(className) : classNames.delete(className);

  marker.setLabel({ ...label, className: Array.from(classNames).join(' ') });
}

export function setMapMarkerHoverState(hotelId: string, isHovering: boolean) {
  const registered = markerRegistry.get(hotelId);
  if (!registered) return;
  const { marker, baseZIndex } = registered;

  if (!isHovering && getMarkerClassNames(marker).has(ACTIVE_MARKER_CLASS)) return;

  const hoveredZIndex =
    typeof google !== 'undefined' && google.maps?.Marker?.MAX_ZINDEX != null
      ? google.maps.Marker.MAX_ZINDEX + 1
      : HOVERED_MARKER_Z_INDEX;

  marker.setZIndex(isHovering ? hoveredZIndex : baseZIndex);
  toggleMarkerLabelClass(marker, ACTIVE_HOVER_CLASS, isHovering);
}
