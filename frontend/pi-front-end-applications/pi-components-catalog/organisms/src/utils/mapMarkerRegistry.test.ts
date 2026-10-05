import {
  clearMapMarkerRegistry,
  registerMapMarker,
  removeAllMarkersFromMap,
  setMapMarkerHoverState,
  updateMarkerPriceLabel,
} from './mapMarkerRegistry';

const createMockMarker = (label: any = { text: '£100', className: 'mapMarker' }) => ({
  getLabel: jest.fn(() => label),
  setLabel: jest.fn(),
  setZIndex: jest.fn(),
  setMap: jest.fn(),
});

describe('mapMarkerRegistry', () => {
  afterEach(() => {
    clearMapMarkerRegistry();
    jest.clearAllMocks();
  });

  it('sets the hovered z-index and adds the activeHover class when hovering starts', () => {
    const marker = createMockMarker();
    registerMapMarker('HOTEL1', marker as any);

    setMapMarkerHoverState('HOTEL1', true);

    expect(marker.setZIndex).toHaveBeenCalledWith(1000);
    expect(marker.setLabel).toHaveBeenCalledWith({
      text: '£100',
      className: 'mapMarker activeHover',
    });
  });

  it('restores the base z-index and removes the activeHover class when hovering ends', () => {
    const marker = createMockMarker({ text: '£100', className: 'mapMarker activeHover' });
    registerMapMarker('HOTEL1', marker as any, 3);

    setMapMarkerHoverState('HOTEL1', false);

    expect(marker.setZIndex).toHaveBeenCalledWith(3);
    expect(marker.setLabel).toHaveBeenCalledWith({
      text: '£100',
      className: 'mapMarker',
    });
  });

  it('defaults the base z-index to 0 when none is provided', () => {
    const marker = createMockMarker({ text: '£100', className: 'mapMarker activeHover' });
    registerMapMarker('HOTEL1', marker as any);

    setMapMarkerHoverState('HOTEL1', false);

    expect(marker.setZIndex).toHaveBeenCalledWith(0);
  });

  it('keeps the highlight and does not reset z-index on mouseout while the tile is open', () => {
    const marker = createMockMarker({
      text: '£100',
      className: 'mapMarker activeHover activeMarker',
    });
    registerMapMarker('HOTEL1', marker as any, 2);

    setMapMarkerHoverState('HOTEL1', false);

    expect(marker.setZIndex).not.toHaveBeenCalled();
    expect(marker.setLabel).not.toHaveBeenCalled();
  });

  it('does not add a duplicate activeHover class if already present', () => {
    const marker = createMockMarker({ text: '£100', className: 'mapMarker activeHover' });
    registerMapMarker('HOTEL1', marker as any);

    setMapMarkerHoverState('HOTEL1', true);

    expect(marker.setLabel).toHaveBeenCalledWith({
      text: '£100',
      className: 'mapMarker activeHover',
    });
  });

  it('does nothing when the hotelId is not registered', () => {
    const marker = createMockMarker();
    registerMapMarker('HOTEL1', marker as any);

    setMapMarkerHoverState('UNKNOWN', true);

    expect(marker.setZIndex).not.toHaveBeenCalled();
    expect(marker.setLabel).not.toHaveBeenCalled();
  });

  it('does nothing when the marker has no label', () => {
    const marker = createMockMarker(null);
    registerMapMarker('HOTEL1', marker as any);

    setMapMarkerHoverState('HOTEL1', true);

    expect(marker.setZIndex).toHaveBeenCalledWith(1000);
    expect(marker.setLabel).not.toHaveBeenCalled();
  });

  it('does nothing when the marker label is a plain string', () => {
    const marker = createMockMarker('a plain string label');
    registerMapMarker('HOTEL1', marker as any);

    setMapMarkerHoverState('HOTEL1', true);

    expect(marker.setLabel).not.toHaveBeenCalled();
  });

  it('clears the registry so previously registered markers are no longer found', () => {
    const marker = createMockMarker();
    registerMapMarker('HOTEL1', marker as any);

    clearMapMarkerRegistry();
    setMapMarkerHoverState('HOTEL1', true);

    expect(marker.setZIndex).not.toHaveBeenCalled();
  });

  it('detaches every registered marker from the map and clears the registry', () => {
    const firstMarker = createMockMarker();
    const secondMarker = createMockMarker();
    registerMapMarker('HOTEL1', firstMarker as any);
    registerMapMarker('HOTEL2', secondMarker as any);

    removeAllMarkersFromMap();

    expect(firstMarker.setMap).toHaveBeenCalledWith(null);
    expect(secondMarker.setMap).toHaveBeenCalledWith(null);

    setMapMarkerHoverState('HOTEL1', true);
    expect(firstMarker.setZIndex).not.toHaveBeenCalled();
  });

  it('updates the marker label text in place, preserving the current className', () => {
    const marker = createMockMarker({ text: '£100', className: 'mapMarker activeHover' });
    registerMapMarker('HOTEL1', marker as any, 0, { text: '£100', className: 'mapMarker' });

    updateMarkerPriceLabel('HOTEL1', '£90');

    expect(marker.setLabel).toHaveBeenCalledWith({
      text: '£90',
      className: 'mapMarker activeHover',
    });
  });

  it('mutates the shared markerLabel object so a later reset (e.g. mouseout) shows the new price', () => {
    const marker = createMockMarker({ text: '£100', className: 'mapMarker' });
    const markerLabel = { text: '£100', className: 'mapMarker' };
    registerMapMarker('HOTEL1', marker as any, 0, markerLabel);

    updateMarkerPriceLabel('HOTEL1', '£90');

    expect(markerLabel.text).toBe('£90');
  });

  it('does nothing when updating the price label for an unregistered hotelId', () => {
    const marker = createMockMarker();
    registerMapMarker('HOTEL1', marker as any);

    updateMarkerPriceLabel('UNKNOWN', '£90');

    expect(marker.setLabel).not.toHaveBeenCalled();
  });

  it('overwrites a previously registered marker for the same hotelId', () => {
    const firstMarker = createMockMarker();
    const secondMarker = createMockMarker();
    registerMapMarker('HOTEL1', firstMarker as any);
    registerMapMarker('HOTEL1', secondMarker as any);

    setMapMarkerHoverState('HOTEL1', true);

    expect(firstMarker.setZIndex).not.toHaveBeenCalled();
    expect(secondMarker.setZIndex).toHaveBeenCalledWith(1000);
  });
});
