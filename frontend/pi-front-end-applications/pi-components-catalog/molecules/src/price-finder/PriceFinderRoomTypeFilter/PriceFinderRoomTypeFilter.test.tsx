import { render, screen, fireEvent } from '@testing-library/react';
import { getStaticContent } from '@whitbread-eos/api';
import { useQueryRequest } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

import PriceFinderRoomTypeFilter from './PriceFinderRoomTypeFilter';

jest.mock('next-i18next', () => ({
  useTranslation: jest.fn(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  useQueryRequest: jest.fn(),
}));

jest.mock('@whitbread-eos/api', () => ({
  getStaticContent: jest.fn(),
}));

const mockUseTranslation = useTranslation as jest.Mock;
const mockUseQueryRequest = useQueryRequest as jest.Mock;
const mockGetStaticContent = getStaticContent as jest.Mock;

const defaultProps = {
  locale: 'en-GB',
  channel: 'test-channel',
  isBB: false,
  isLoading: false,
  filterSelection: {
    DB: false,
    TWIN: false,
    FAM: false,
    DIS: false,
  },
  setFilterSelection: jest.fn(),
};

const mockHeaderData = {
  headerInformation: {
    content: {
      global: {
        double: 'Double Room',
        twin: 'Twin Room',
        family: 'Family Room',
        accessible: 'Accessible Room',
      },
    },
  },
};

describe('PriceFinderRoomTypeFilter', () => {
  beforeEach(() => {
    mockUseTranslation.mockReturnValue({
      t: (key: string) => {
        const translations: Record<string, string> = {
          'tableFilter.clearAll': 'Clear all filters',
        };
        return translations[key] || key;
      },
    });

    mockUseQueryRequest.mockReturnValue({
      data: mockHeaderData,
    });

    mockGetStaticContent.mockReturnValue({});
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should render all filter options with correct labels', () => {
    render(<PriceFinderRoomTypeFilter {...defaultProps} />);

    expect(screen.getByText('Double Room')).toBeInTheDocument();
    expect(screen.getByText('Twin Room')).toBeInTheDocument();
    expect(screen.getByText('Family Room')).toBeInTheDocument();
    expect(screen.getByText('Accessible Room')).toBeInTheDocument();
  });

  it('should render clear all filters button', () => {
    render(<PriceFinderRoomTypeFilter {...defaultProps} />);

    expect(screen.getByText('Clear all filters')).toBeInTheDocument();
  });

  it('should handle checkbox selection correctly', () => {
    const setFilterSelection = jest.fn();
    const props = { ...defaultProps, setFilterSelection };

    render(<PriceFinderRoomTypeFilter {...props} />);

    const doubleRoomCheckbox = screen.getByLabelText('Double Room');
    fireEvent.click(doubleRoomCheckbox);

    expect(setFilterSelection).toHaveBeenCalledTimes(1);

    const callback = setFilterSelection.mock.calls[0][0];
    const currentState = { DB: false, TWIN: false, FAM: false, DIS: false };
    const result = callback(currentState);

    expect(result).toEqual({ DB: false, TWIN: false, FAM: false, DIS: false });
  });

  it('should call clearFilters when clear all is clicked', () => {
    const setFilterSelection = jest.fn();
    const props = { ...defaultProps, setFilterSelection };

    render(<PriceFinderRoomTypeFilter {...props} />);

    const clearButton = screen.getByText('Clear all filters');
    fireEvent.click(clearButton);

    expect(setFilterSelection).toHaveBeenCalledWith({
      DB: false,
      TWIN: false,
      FAM: false,
      DIS: false,
    });
  });

  it('should disable checkboxes when isLoading is true', () => {
    const props = { ...defaultProps, isLoading: true };

    render(<PriceFinderRoomTypeFilter {...props} />);

    const checkboxes = screen.getAllByRole('checkbox');
    checkboxes.forEach((checkbox) => {
      expect(checkbox).toBeDisabled();
    });
  });

  it('should enable checkboxes when isLoading is false', () => {
    const props = { ...defaultProps, isLoading: false };

    render(<PriceFinderRoomTypeFilter {...props} />);

    const checkboxes = screen.getAllByRole('checkbox');
    checkboxes.forEach((checkbox) => {
      expect(checkbox).toBeEnabled();
    });
  });

  it('should show checked state based on filterSelection prop', () => {
    const props = {
      ...defaultProps,
      filterSelection: {
        DB: true,
        TWIN: false,
        FAM: true,
        DIS: false,
      },
    };

    render(<PriceFinderRoomTypeFilter {...props} />);

    const doubleCheckbox = screen.getByLabelText('Double Room');
    const twinCheckbox = screen.getByLabelText('Twin Room');
    const familyCheckbox = screen.getByLabelText('Family Room');
    const accessibleCheckbox = screen.getByLabelText('Accessible Room');

    expect(doubleCheckbox).toBeChecked();
    expect(twinCheckbox).not.toBeChecked();
    expect(familyCheckbox).toBeChecked();
    expect(accessibleCheckbox).not.toBeChecked();
  });

  it('should handle missing header data gracefully', () => {
    mockUseQueryRequest.mockReturnValue({
      data: { headerInformation: null },
    });

    render(<PriceFinderRoomTypeFilter {...defaultProps} />);

    expect(screen.getByText('Clear all filters')).toBeInTheDocument();
  });

  it('should call useQueryRequest with correct parameters', () => {
    render(<PriceFinderRoomTypeFilter {...defaultProps} />);

    expect(mockGetStaticContent).toHaveBeenCalledWith(true);
    expect(mockUseQueryRequest).toHaveBeenCalledWith(
      ['GetStaticContent', 'en', 'GB'],
      {},
      {
        country: 'gb',
        language: 'en',
        site: 'test-channel',
        businessBooker: false,
      }
    );
  });

  it('should apply correct styles to checked items', () => {
    const props = {
      ...defaultProps,
      filterSelection: {
        DB: true,
        TWIN: false,
        FAM: false,
        DIS: false,
      },
    };

    const { getAllByRole } = render(<PriceFinderRoomTypeFilter {...props} />);
    const filterItems = getAllByRole('checkbox');
    expect(filterItems.length).toBeGreaterThan(0);
  });

  describe('filterTypes array', () => {
    it('should maps all room types to filter options', () => {
      render(<PriceFinderRoomTypeFilter {...defaultProps} />);

      const expectedRoomTypes = [
        { roomType: 'DB', name: 'Double Room' },
        { roomType: 'TWIN', name: 'Twin Room' },
        { roomType: 'FAM', name: 'Family Room' },
        { roomType: 'DIS', name: 'Accessible Room' },
      ];

      expectedRoomTypes.forEach(({ name }) => {
        expect(screen.getByText(name)).toBeInTheDocument();
      });
    });
  });
});
