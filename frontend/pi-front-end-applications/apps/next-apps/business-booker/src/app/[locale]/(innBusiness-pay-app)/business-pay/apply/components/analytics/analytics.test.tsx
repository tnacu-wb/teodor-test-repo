import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import useSatelliteTrack from '~hooks/use-satellite-track';

import { Analytics } from './analytics';

jest.mock('~hooks/use-satellite-track', () => ({
  __esModule: true,
  default: jest.fn(),
}));

const mockSatelliteTrack = jest.fn();
(useSatelliteTrack as jest.Mock).mockImplementation(() => mockSatelliteTrack);

const mockProps = {
  pageName: '',
};

describe('Analytics component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render Analytics component', () => {
    render(<Analytics {...mockProps} />);
  });

  it('should call satelliteTrack only once per unique track value', () => {
    const { rerender } = render(<Analytics {...mockProps} track="PageA" />);
    expect(mockSatelliteTrack).toHaveBeenCalledWith('PageA');

    // Rerender with the same track value - should NOT track again
    rerender(<Analytics {...mockProps} track="PageA" />);
    expect(mockSatelliteTrack).toHaveBeenCalledTimes(1);

    // Rerender with a new track value - should track again
    rerender(<Analytics {...mockProps} track="PageB" />);
    expect(mockSatelliteTrack).toHaveBeenCalledWith('PageB');
    expect(mockSatelliteTrack).toHaveBeenCalledTimes(2);
  });
});
