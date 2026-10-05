import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { getLocationResults } from '@whitbread-eos/utils/server';

import { HotelSearchInput, HotelSearchResult } from './HotelSearchInput';

jest.mock('@whitbread-eos/utils/server', () => ({
  useTranslation: () => ({ t: (defaultValue: string) => defaultValue }),
  getLocationResults: jest.fn(),
  formatIBAssetsUrl: (url: string) => url,
  cn: (...inputs: string[]) => inputs.join(' '),
}));

jest.mock('@whitbread-eos/atoms/ui', () => ({
  Input: ({
    className,
    'data-testid': dataTestId,
    onChange,
    ...props
  }: {
    className: string;
    'data-testid': string;
    onChange: (value: string) => void;
    [key: string]: any;
  }) => (
    <input
      className={className}
      data-testid={dataTestId}
      onChange={(e) => onChange && onChange(e.target.value)}
      {...props}
    />
  ),
}));

jest.mock('next/image', () => ({
  __esModule: true,
  default: ({
    src,
    alt,
    className,
    ...props
  }: {
    src: string;
    alt: string;
    className: string;
    [key: string]: any;
  }) => <img src={src} alt={alt} className={className} {...props} />,
}));

const mockHotels: HotelSearchResult[] = [
  {
    suggestion: 'Premier Inn London Waterloo',
    brand: 'PI',
    code: 'LON123',
    id: 'LON123',
  },
  {
    suggestion: 'Premier Inn Manchester City Centre',
    brand: 'PI',
    code: 'MAN456',
    id: 'MAN456',
  },
];

describe('HotelSearchInput', () => {
  const defaultProps = {
    globalLabels: {},
    locationIcon: '/icons/location.svg',
    onHotelSelect: jest.fn(),
    getHotelIcon: jest.fn().mockReturnValue('/icons/hotel.svg'),
    baseDataTestId: 'test-hotel-search',
  };

  beforeEach(() => {
    jest.clearAllMocks();
    (getLocationResults as jest.Mock).mockResolvedValue({
      properties: mockHotels.map((hotel) => ({
        suggestion: hotel.suggestion,
        brand: hotel.brand,
        code: hotel.code,
      })),
    });
  });

  it('should display search results after typing a search term', async () => {
    render(<HotelSearchInput {...defaultProps} />);

    const searchInput = screen.getByTestId('test-hotel-search-search-input');

    fireEvent.change(searchInput, { target: { value: 'London' } });

    await waitFor(() => {
      expect(getLocationResults).toHaveBeenCalledWith('London', true);
    });

    await waitFor(() => {
      const resultsContainer = screen.getByTestId('test-hotel-search-search-results');
      expect(resultsContainer).toBeTruthy();
    });

    const firstResult = screen.getByTestId('test-hotel-search-search-result-LON123');
    expect(firstResult).toBeTruthy();
    expect(firstResult.textContent).toContain('Premier Inn London Waterloo');
  });

  it('should call onHotelSelect when selecting a hotel from the dropdown', async () => {
    render(<HotelSearchInput {...defaultProps} />);

    const searchInput = screen.getByTestId('test-hotel-search-search-input');

    fireEvent.change(searchInput, { target: { value: 'London' } });

    await waitFor(() => {
      expect(getLocationResults).toHaveBeenCalled();
    });

    await waitFor(() => {
      const firstResult = screen.getByTestId('test-hotel-search-search-result-LON123');
      expect(firstResult).toBeTruthy();
    });

    const firstResult = screen.getByTestId('test-hotel-search-search-result-LON123');
    fireEvent.click(firstResult);

    expect(defaultProps.onHotelSelect).toHaveBeenCalledWith(mockHotels[0]);

    expect(searchInput).toHaveProperty('value', '');
  });
});
