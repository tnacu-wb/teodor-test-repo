import '@testing-library/jest-dom';
import { render, screen, fireEvent, act, waitFor } from '@testing-library/react';
import { FormProvider, useForm } from 'react-hook-form';

import { HotelAlertsSelector } from './HotelAlertsSelector';

const mockState = {
  selectedHotels: [],
};

const mockOnRemoveHotel = jest.fn();
const mockOnHotelSelect = jest.fn();

const mockGetHotelInformationIB = jest.fn();

jest.mock('./HotelSearchInput', () => ({
  HotelSearchInput: jest.fn(({ onHotelSelect, baseDataTestId }) => {
    mockOnHotelSelect.mockImplementation(onHotelSelect);

    return (
      <div data-testid={`${baseDataTestId}-search-input-mock`}>
        <button
          data-testid="mock-add-hotel-button"
          onClick={() => {
            const hotel = { id: 'NEW1', code: 'NEW1', suggestion: 'New Hotel 1', brand: 'PI' };
            mockOnHotelSelect(hotel);
          }}
        >
          Add Hotel 1
        </button>
        <button
          data-testid="mock-add-duplicate-button"
          onClick={() => {
            const hotel = { id: 'HOTEL1', code: 'HOTEL1', suggestion: 'Hotel One', brand: 'PI' };
            mockOnHotelSelect(hotel);
          }}
        >
          Add Duplicate Hotel
        </button>
        <button
          data-testid="mock-add-many-hotels-button"
          onClick={() => {
            for (let i = 1; i <= 5; i++) {
              const hotel = {
                id: `BULK${i}`,
                code: `BULK${i}`,
                suggestion: `Bulk Hotel ${i}`,
                brand: 'PI',
              };
              mockOnHotelSelect(hotel);
            }
          }}
        >
          Add Many Hotels
        </button>
      </div>
    );
  }),
  HotelSearchResult: jest.fn(),
}));

jest.mock('./SelectedHotelsList', () => ({
  SelectedHotelsList: jest.fn(({ selectedHotels, onRemoveHotel, baseDataTestId }) => {
    mockState.selectedHotels = selectedHotels;
    mockOnRemoveHotel.mockImplementation(onRemoveHotel);

    return (
      <div data-testid={`${baseDataTestId}-selected-list-mock`}>
        <span>{selectedHotels.length} selected</span>
        {selectedHotels.map((hotel: any) => (
          <button
            key={hotel.id}
            data-testid={`mock-remove-${hotel.id}`}
            onClick={() => {
              mockOnRemoveHotel(hotel.id);
            }}
          >
            Remove {hotel.suggestion}
          </button>
        ))}
      </div>
    );
  }),
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  useTranslation: () => ({
    t: (key: string, fallback: string) => fallback || key,
  }),
  formatIBAssetsUrl: (url: string) => `formatted/${url || ''}`,
  getHotelInformationIB: (...args: any[]) => mockGetHotelInformationIB(...args),
}));

jest.mock('@whitbread-eos/api', () => ({
  HotelBrand: {
    HUB: 'HUB',
    ZIP: 'ZIP',
    PI: 'PI',
  },
}));

const defaultProps = {
  globalLabels: {
    brand: {
      piLogo: 'pi.svg',
      hubLogo: 'hub.svg',
      zipLogo: 'zip.svg',
    },
  },
  locationIcon: 'loc.svg',
  language: 'en',
};

const renderWithFormProvider = (
  ui: React.ReactElement,
  defaultValues: any = { selectedHotels: [] }
) => {
  const Wrapper: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const methods = useForm({ defaultValues });
    return <FormProvider {...methods}>{children}</FormProvider>;
  };
  return render(ui, { wrapper: Wrapper });
};

describe('HotelAlertsSelector', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockState.selectedHotels = [];
    mockOnRemoveHotel.mockClear();
    mockOnHotelSelect.mockClear();
    mockGetHotelInformationIB.mockClear();
  });

  it('renders the search input and the selected list mocks', () => {
    renderWithFormProvider(<HotelAlertsSelector {...defaultProps} />);
    expect(screen.getByTestId('HotelAlertsSelector-search-input-mock')).toBeInTheDocument();
    expect(screen.getByTestId('HotelAlertsSelector-selected-list-mock')).toBeInTheDocument();
  });

  it('adds a hotel when selected from HotelSearchInput mock', async () => {
    renderWithFormProvider(<HotelAlertsSelector {...defaultProps} />);

    expect(mockState.selectedHotels).toHaveLength(0);

    const addButton = screen.getByTestId('mock-add-hotel-button');
    await act(async () => {
      fireEvent.click(addButton);
    });

    expect(screen.getByTestId('HotelAlertsSelector-selected-list-mock')).toHaveTextContent(
      '1 selected'
    );

    expect(mockState.selectedHotels).toHaveLength(1);
    expect(mockState.selectedHotels[0]).toEqual({
      id: 'NEW1',
      code: 'NEW1',
      suggestion: 'New Hotel 1',
      brand: 'PI',
    });
  });

  it('removes a hotel when removeHotel is called', async () => {
    const initialHotels = [{ id: 'HOTEL1', code: 'HOTEL1', suggestion: 'Hotel One', brand: 'PI' }];
    renderWithFormProvider(<HotelAlertsSelector {...defaultProps} />, {
      selectedHotels: initialHotels,
    });

    expect(screen.getByTestId('HotelAlertsSelector-selected-list-mock')).toHaveTextContent(
      '1 selected'
    );

    const removeButton = screen.getByTestId('mock-remove-HOTEL1');
    await act(async () => {
      fireEvent.click(removeButton);
    });

    expect(screen.getByTestId('HotelAlertsSelector-selected-list-mock')).toHaveTextContent(
      '0 selected'
    );
    expect(mockState.selectedHotels).toHaveLength(0);
  });

  it('does not add duplicate hotels', async () => {
    renderWithFormProvider(<HotelAlertsSelector {...defaultProps} />);

    const addButton = screen.getByTestId('mock-add-hotel-button');
    await act(async () => {
      fireEvent.click(addButton);
    });
    expect(screen.getByTestId('HotelAlertsSelector-selected-list-mock')).toHaveTextContent(
      '1 selected'
    );
    expect(mockState.selectedHotels).toHaveLength(1);

    const addDuplicateButton = screen.getByTestId('mock-add-duplicate-button');
    await act(async () => {
      fireEvent.click(addDuplicateButton);
    });

    expect(screen.getByTestId('HotelAlertsSelector-selected-list-mock')).toHaveTextContent(
      '2 selected'
    );
    expect(mockState.selectedHotels).toHaveLength(2);

    await act(async () => {
      fireEvent.click(addButton);
    });
    expect(screen.getByTestId('HotelAlertsSelector-selected-list-mock')).toHaveTextContent(
      '2 selected'
    );
    expect(mockState.selectedHotels).toHaveLength(2);

    await act(async () => {
      fireEvent.click(addDuplicateButton);
    });
    expect(screen.getByTestId('HotelAlertsSelector-selected-list-mock')).toHaveTextContent(
      '2 selected'
    );
    expect(mockState.selectedHotels).toHaveLength(2);
  });

  it('enforces MAX_SELECTED_HOTELS limit (100 hotels)', async () => {
    const initialHotels = Array.from({ length: 100 }, (_, i) => ({
      id: `INITIAL${i}`,
      code: `INITIAL${i}`,
      suggestion: `Initial Hotel ${i}`,
      brand: 'PI',
    }));

    renderWithFormProvider(<HotelAlertsSelector {...defaultProps} />, {
      selectedHotels: initialHotels,
    });

    expect(screen.getByTestId('HotelAlertsSelector-selected-list-mock')).toHaveTextContent(
      '100 selected'
    );

    const addButton = screen.getByTestId('mock-add-hotel-button');
    await act(async () => {
      fireEvent.click(addButton);
    });

    expect(screen.getByTestId('HotelAlertsSelector-selected-list-mock')).toHaveTextContent(
      '100 selected'
    );
    expect(mockState.selectedHotels).toHaveLength(100);
  });

  it('loads initial hotels successfully with getHotelInformationIB', async () => {
    const mockHotelData = {
      hotelId: 'HOTEL1',
      name: 'Test Hotel',
      brand: 'HUB',
    };

    mockGetHotelInformationIB.mockResolvedValue(mockHotelData);

    await act(async () => {
      renderWithFormProvider(
        <HotelAlertsSelector {...defaultProps} initialHotelIds={['HOTEL1']} />
      );
    });

    await waitFor(() => {
      expect(mockGetHotelInformationIB).toHaveBeenCalledWith('HOTEL1', 'en');
    });

    await waitFor(() => {
      expect(mockState.selectedHotels).toHaveLength(1);
      expect(mockState.selectedHotels[0]).toEqual({
        id: 'HOTEL1',
        code: 'HOTEL1',
        suggestion: 'Test Hotel',
        brand: 'HUB',
      });
    });
  });

  it('handles individual hotel fetch errors and falls back to hotel ID', async () => {
    mockGetHotelInformationIB.mockRejectedValue(new Error('Hotel not found'));

    await act(async () => {
      renderWithFormProvider(
        <HotelAlertsSelector {...defaultProps} initialHotelIds={['UNKNOWN_HOTEL']} />
      );
    });

    await waitFor(() => {
      expect(mockGetHotelInformationIB).toHaveBeenCalledWith('UNKNOWN_HOTEL', 'en');
    });

    await waitFor(() => {
      expect(mockState.selectedHotels).toHaveLength(1);
      expect(mockState.selectedHotels[0]).toEqual({
        id: 'UNKNOWN_HOTEL',
        code: 'UNKNOWN_HOTEL',
        suggestion: 'UNKNOWN_HOTEL (Hotel)',
        brand: 'PINN',
      });
    });
  });

  it('handles partial hotel data from getHotelInformationIB', async () => {
    const partialHotelData = {
      hotelId: null,
      name: null,
      brand: null,
    };

    mockGetHotelInformationIB.mockResolvedValue(partialHotelData);

    await act(async () => {
      renderWithFormProvider(
        <HotelAlertsSelector {...defaultProps} initialHotelIds={['PARTIAL_HOTEL']} />
      );
    });

    await waitFor(() => {
      expect(mockState.selectedHotels).toHaveLength(1);
      expect(mockState.selectedHotels[0]).toEqual({
        id: 'PARTIAL_HOTEL',
        code: 'PARTIAL_HOTEL',
        suggestion: 'PARTIAL_HOTEL (Hotel)',
        brand: 'PINN',
      });
    });
  });

  it('handles multiple initial hotels with mixed success/failure', async () => {
    mockGetHotelInformationIB
      .mockResolvedValueOnce({ hotelId: 'HOTEL1', name: 'Success Hotel', brand: 'PI' })
      .mockRejectedValueOnce(new Error('Failed to fetch'))
      .mockResolvedValueOnce({ hotelId: 'HOTEL3', name: 'Another Success', brand: 'ZIP' });

    await act(async () => {
      renderWithFormProvider(
        <HotelAlertsSelector {...defaultProps} initialHotelIds={['HOTEL1', 'HOTEL2', 'HOTEL3']} />
      );
    });

    await waitFor(() => {
      expect(mockState.selectedHotels).toHaveLength(3);
    });

    expect(mockState.selectedHotels).toEqual([
      { id: 'HOTEL1', code: 'HOTEL1', suggestion: 'Success Hotel', brand: 'PI' },
      { id: 'HOTEL2', code: 'HOTEL2', suggestion: 'HOTEL2 (Hotel)', brand: 'PINN' },
      { id: 'HOTEL3', code: 'HOTEL3', suggestion: 'Another Success', brand: 'ZIP' },
    ]);
  });

  it('handles empty initialHotelIds array', async () => {
    await act(async () => {
      renderWithFormProvider(<HotelAlertsSelector {...defaultProps} initialHotelIds={[]} />);
    });

    expect(mockGetHotelInformationIB).not.toHaveBeenCalled();
    expect(mockState.selectedHotels).toHaveLength(0);
  });

  it('handles undefined initialHotelIds (default parameter)', async () => {
    await act(async () => {
      renderWithFormProvider(<HotelAlertsSelector {...defaultProps} />);
    });

    expect(mockGetHotelInformationIB).not.toHaveBeenCalled();
    expect(mockState.selectedHotels).toHaveLength(0);
  });

  it('passes the correct getHotelIcon function', () => {
    const { rerender } = renderWithFormProvider(<HotelAlertsSelector {...defaultProps} />);

    const HotelSearchInputMock = jest.requireMock('./HotelSearchInput');
    const SelectedHotelsListMock = jest.requireMock('./SelectedHotelsList');

    const searchInputProps = HotelSearchInputMock.HotelSearchInput.mock.calls[0][0];
    const selectedListProps = SelectedHotelsListMock.SelectedHotelsList.mock.calls[0][0];

    expect(searchInputProps.getHotelIcon('PI')).toBe('formatted/pi.svg');
    expect(searchInputProps.getHotelIcon('HUB')).toBe('formatted/hub.svg');
    expect(searchInputProps.getHotelIcon('ZIP')).toBe('formatted/zip.svg');
    expect(searchInputProps.getHotelIcon('UNKNOWN')).toBe('formatted/pi.svg');

    expect(selectedListProps.getHotelIcon('PI')).toBe('formatted/pi.svg');
    expect(selectedListProps.getHotelIcon('HUB')).toBe('formatted/hub.svg');

    act(() => {
      rerender(<HotelAlertsSelector {...defaultProps} globalLabels={{}} />);
    });

    const lastSearchInputCallIndex = HotelSearchInputMock.HotelSearchInput.mock.calls.length - 1;
    const newSearchInputProps =
      HotelSearchInputMock.HotelSearchInput.mock.calls[lastSearchInputCallIndex][0];
    expect(newSearchInputProps.getHotelIcon('PI')).toBe('formatted/');
  });

  it('handles missing brand logos gracefully in getHotelIcon', () => {
    renderWithFormProvider(<HotelAlertsSelector {...defaultProps} globalLabels={{ brand: {} }} />);
    const HotelSearchInputMock = jest.requireMock('./HotelSearchInput');
    const searchInputProps = HotelSearchInputMock.HotelSearchInput.mock.calls[0][0];
    expect(searchInputProps.getHotelIcon('PI')).toBe('formatted/');
    expect(searchInputProps.getHotelIcon('HUB')).toBe('formatted/');
  });

  it('renders container with correct data-testid and className', () => {
    renderWithFormProvider(<HotelAlertsSelector {...defaultProps} />);
    const container = screen.getByTestId('HotelAlertsSelector-container');
    expect(container).toBeInTheDocument();
    expect(container).toHaveClass('flex', 'flex-col', 'gap-6');
  });
});
