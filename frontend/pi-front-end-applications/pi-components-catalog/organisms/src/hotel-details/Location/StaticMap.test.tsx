import '@testing-library/jest-dom';
import { fireEvent, render, screen } from '@testing-library/react';
import { theme } from '@whitbread-eos/atoms';

import StaticMap from './StaticMap';

const MOCK_API_KEY = 'dummy-key';

describe('StaticMap', () => {
  const defaultProps = {
    latitude: 51.496015,
    longitude: -0.447979,
    apiKey: MOCK_API_KEY,
  };

  const loadImage = () => {
    const img = screen.getByAltText('Hotel location map') as HTMLImageElement;
    fireEvent.load(img);
    return img;
  };

  it('should show loading spinner initially', () => {
    render(<StaticMap {...defaultProps} />);

    expect(screen.getByTestId('loading-spinner')).toBeInTheDocument();
  });

  it('should render static map image after load', () => {
    render(<StaticMap {...defaultProps} />);

    const img = screen.getByAltText('Hotel location map');
    fireEvent.load(img);

    expect(img).toBeInTheDocument();
    expect(screen.queryByTestId('loading-spinner')).not.toBeInTheDocument();
  });

  it('should construct correct static map URL', () => {
    render(<StaticMap {...defaultProps} />);

    const img = loadImage();

    expect(img.src).toContain('maps.googleapis.com/maps/api/staticmap');
    expect(img.src).toContain('center=51.496015,-0.447979');
    expect(img.src).toContain('zoom=13');
    expect(img.src).toContain('size=800x400');
    expect(img.src).toContain('scale=2');
    expect(img.src).toContain('maptype=roadmap');
    expect(img.src).toContain(`key=${MOCK_API_KEY}`);
  });

  it('should use default (PI) marker color when no brand', () => {
    render(<StaticMap {...defaultProps} />);

    const img = loadImage();

    const expectedColor = theme.colors.btnSecondaryEnabled.replace('#', '0x');
    expect(img.src).toContain(`color:${expectedColor}`);
  });

  it('should use HUB marker color', () => {
    render(<StaticMap {...defaultProps} brand="hub" />);

    const img = loadImage();

    const expectedColor = theme.colors.hubPrimary.replace('#', '0x');
    expect(img.src).toContain(`color:${expectedColor}`);
  });

  it('should use ZIP marker color', () => {
    render(<StaticMap {...defaultProps} brand="zip" />);

    const img = loadImage();

    const expectedColor = theme.colors.zipPrimary.replace('#', '0x');
    expect(img.src).toContain(`color:${expectedColor}`);
  });

  it('should be case insensitive for brand', () => {
    render(<StaticMap {...defaultProps} brand="HUB" />);

    const img = loadImage();

    const expectedColor = theme.colors.hubPrimary.replace('#', '0x');
    expect(img.src).toContain(`color:${expectedColor}`);
  });

  it('should return null if latitude is missing', () => {
    const { container } = render(
      <StaticMap longitude={-0.447979} apiKey={MOCK_API_KEY} brand="ZIP" />
    );

    expect(container.firstChild).toBeNull();
  });

  it('should return null if longitude is missing', () => {
    const { container } = render(
      <StaticMap latitude={51.496015} apiKey={MOCK_API_KEY} brand="HUB" />
    );

    expect(container.firstChild).toBeNull();
  });

  it('should hide spinner on image error', () => {
    render(<StaticMap {...defaultProps} />);

    const img = screen.getByAltText('Hotel location map');
    fireEvent.error(img);

    expect(screen.queryByTestId('loading-spinner')).not.toBeInTheDocument();
  });
});
