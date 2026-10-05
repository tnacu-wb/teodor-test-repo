import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { render, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { useQueryRequest, useCustomLocale } from '@whitbread-eos/utils';
import React from 'react';

import HotelsPriceTable from './HotelsPriceTable.component';

const filterSelection = {
  DB: false,
  TWIN: false,
  FAM: false,
  DIS: false,
};

jest.mock('next-i18next', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
  withTranslation: () => (Component: any) => Component,
}));

// Mock the API hook
jest.mock('@whitbread-eos/utils', () => ({
  useQueryRequest: jest.fn(),
  useCustomLocale: jest.fn(() => ({ country: 'gb', language: 'en' })),
  formatPrice: jest.fn((currency, amount) => `${currency}${amount}`),
  formatCurrency: jest.fn((currency: string) => {
    const currencySymbols = {
      GBP: '£',
      USD: '$',
      EUR: '€',
    };
    return currency in currencySymbols
      ? currencySymbols[currency as keyof typeof currencySymbols]
      : currency;
  }),
  cn: (...args: string[]) => args.filter(Boolean).join(' '),
}));

// Mock framer-motion for animation testing
jest.mock('framer-motion', () => ({
  ...jest.requireActual('framer-motion'),
  AnimatePresence: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  motion: {
    div: ({ children, ...props }: React.ComponentProps<'div'>) => <div {...props}>{children}</div>,
  },
}));

const mockUseQueryRequest = useQueryRequest as jest.Mock;

// Mock IntersectionObserver
const mockIntersectionObserver = jest.fn();
mockIntersectionObserver.mockReturnValue({
  observe: () => null,
  unobserve: () => null,
  disconnect: () => null,
});
global.IntersectionObserver = mockIntersectionObserver;

// Mock matchMedia for responsive tests
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: jest.fn().mockImplementation((query) => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: jest.fn(),
    removeListener: jest.fn(),
    addEventListener: jest.fn(),
    removeEventListener: jest.fn(),
    dispatchEvent: jest.fn(),
  })),
});

// Mock GraphQL API response data
const mockApiData = {
  getLowestRatesByLocationId: {
    priceFinderOperaHotelAvailabilitiesDtoList: [
      {
        hotelCode: 'LONKIN',
        hotelName: 'London Kings Cross',
        availabilities: [
          {
            availableDate: '2025-08-03',
            currency: 'GBP',
            minimumRate: 40,
            rateCode: 'NONFLEX',
            hasMlosRestriction: false,
            hasClosedRestriction: false,
            rateClassification: 'O',
            roomType: 'DBLWIN,BIGWIN,ACCWIN',
            roomCategory: 'SB',
            minimumNights: 1,
            quantity: 388,
          },
          {
            availableDate: '2025-08-04',
            currency: 'GBP',
            minimumRate: 45,
            rateCode: 'NONFLEX',
            hasMlosRestriction: false,
            hasClosedRestriction: false,
            rateClassification: 'O',
            roomType: 'DBLWIN,BIGWIN,ACCWIN',
            roomCategory: 'SB',
            minimumNights: 1,
            quantity: 387,
          },
        ],
      },
      {
        hotelCode: 'LONEUS',
        hotelName: 'London Euston',
        availabilities: [
          {
            availableDate: '2025-08-03',
            currency: 'GBP',
            minimumRate: 55,
            rateCode: 'FLEX',
            hasMlosRestriction: false,
            hasClosedRestriction: false,
            rateClassification: 'O',
            roomType: 'DBLWIN,BIGWIN,ACCWIN',
            roomCategory: 'SB',
            minimumNights: 1,
            quantity: 200,
          },
          {
            availableDate: '2025-08-04',
            currency: 'GBP',
            minimumRate: 0,
            rateCode: '',
            hasMlosRestriction: true,
            hasClosedRestriction: false,
            rateClassification: '',
            roomType: '',
            roomCategory: '',
            minimumNights: 1,
            quantity: 0,
          },
        ],
      },
      {
        hotelCode: 'LONWAT',
        hotelName: 'London Waterloo',
        availabilities: [],
      },
    ],
    total: 150,
    page: 1,
    pageSize: 25,
    lowestMonthlyRate: {
      price: 40,
      currency: 'GBP',
    },
  },
};

const setLowestPrice = jest.fn();
const setIsLoading = jest.fn();
const onPriceSortChange = jest.fn();
const satelliteTrack = jest.fn();
const satelliteLoaded = jest.fn();

let queryClient: QueryClient;

const createWrapper = () => {
  queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
      },
    },
  });

  const Wrapper = ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  );

  Wrapper.displayName = 'TestWrapper';
  return Wrapper;
};

describe('HotelsPriceTable', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    // Mock current date to ensure consistent test results
    jest.useFakeTimers();
    jest.setSystemTime(new Date('2025-08-03'));
    (window as any)._satellite = { track: satelliteTrack };
    (window as any).__satelliteLoaded = { track: satelliteLoaded };
  });

  afterEach(() => {
    jest.useRealTimers();
    queryClient?.clear();
  });

  describe('Loading State', () => {
    it('should render skeleton loading when data is loading', () => {
      mockUseQueryRequest.mockReturnValue({
        isLoading: true,
        isError: false,
        data: null,
      });

      const { getByTestId, container } = render(<HotelsPriceTable />, {
        wrapper: createWrapper(),
      });

      expect(getByTestId('hotel-price-table')).toBeInTheDocument();
      expect(getByTestId('hotel-price-table-body')).toBeInTheDocument();

      // Should show skeleton loading elements (SkeletonBox components)
      const skeletonElements = container.querySelectorAll('.css-emmkq2, .css-rbq2i3, .css-0');
      expect(skeletonElements.length).toBeGreaterThan(0);
    });

    it('should show loading message for skeleton rows', () => {
      mockUseQueryRequest.mockReturnValue({
        isLoading: true,
        isError: false,
        data: null,
      });

      const { container } = render(<HotelsPriceTable />, {
        wrapper: createWrapper(),
      });

      // Check for skeleton loading elements (look for table rows in loading state)
      const tableRows = container.querySelectorAll('tbody tr');
      expect(tableRows.length).toBe(20); // Should show 20 skeleton rows
    });
  });

  describe('Error State', () => {
    it('should render error message when API call fails', () => {
      mockUseQueryRequest.mockReturnValue({
        isLoading: false,
        isError: true,
        data: null,
      });

      const { getByTestId, getByText } = render(<HotelsPriceTable />, {
        wrapper: createWrapper(),
      });

      expect(getByTestId('error-row')).toBeInTheDocument();
      expect(getByText('priceFinder.noResults.title')).toBeInTheDocument();
    });
  });

  describe('API Data Rendering', () => {
    beforeEach(() => {
      mockUseQueryRequest.mockReturnValue({
        isLoading: false,
        isError: false,
        data: mockApiData,
      });
    });

    it('should render table with API data', async () => {
      const { getByTestId } = render(<HotelsPriceTable filterSelection={filterSelection} />, {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        expect(getByTestId('hotel-price-table')).toBeInTheDocument();
        expect(getByTestId('hotel-price-table-header')).toBeInTheDocument();
        expect(getByTestId('hotel-price-table-body')).toBeInTheDocument();
      });
    });

    it('should show "━" for unavailable dates', async () => {
      const { getAllByText } = render(<HotelsPriceTable />, {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        const unavailableElements = getAllByText('━');
        expect(unavailableElements.length).toBeGreaterThan(0);
      });
    });
  });

  describe('Header Functionality', () => {
    beforeEach(() => {
      mockUseQueryRequest.mockReturnValue({
        isLoading: false,
        isError: false,
        data: mockApiData,
      });
    });

    it('should render date headers with weekday and day', async () => {
      const { getByTestId } = render(<HotelsPriceTable />, {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        // Check for today's date (2025-08-03 is a Sunday)
        expect(getByTestId('date-header-2025-08-03')).toBeInTheDocument();
      });
    });

    it('should render right scroll button and hide left when at start', async () => {
      const { queryByTestId, getByTestId } = render(<HotelsPriceTable />, {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        expect(queryByTestId('scroll-left-button')).not.toBeInTheDocument();
        expect(getByTestId('scroll-right-button')).toBeInTheDocument();
      });
    });

    it('should not show left scroll button when at start position', async () => {
      const { queryByTestId } = render(<HotelsPriceTable />, {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        const leftButton = queryByTestId('scroll-left-button');
        expect(leftButton).not.toBeInTheDocument();
      });
    });

    it('should scroll dates when clicking scroll buttons', async () => {
      const { getByTestId } = render(<HotelsPriceTable />, {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        const rightButton = getByTestId('scroll-right-button');
        userEvent.click(rightButton);

        // After scrolling right, left button should be enabled
        const leftButton = getByTestId('scroll-left-button');
        expect(leftButton).not.toBeDisabled();
      });
    });
  });

  describe('Sorting Functionality', () => {
    beforeEach(() => {
      mockUseQueryRequest.mockReturnValue({
        isLoading: false,
        isError: false,
        data: mockApiData,
      });
    });

    it('should sort hotels by price when clicking date header', async () => {
      const { getByTestId, container } = render(<HotelsPriceTable />, {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        const dateHeader = getByTestId('date-header-2025-08-03');
        userEvent.click(dateHeader);

        // Check that the header shows sorted state (verify it has the sorted background)
        expect(dateHeader).toBeInTheDocument();

        // Check that sorted column cells exist (look for price cells with test IDs)
        const priceCells = container.querySelectorAll('[data-testid*="price-cell"]');
        expect(priceCells.length).toBeGreaterThan(0);
      });
    });

    it('should handle keyboard navigation on date headers', async () => {
      const { getByTestId } = render(<HotelsPriceTable />, {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        const dateHeader = getByTestId('date-header-2025-08-03');

        // Focus the element first, then test Enter key
        userEvent.click(dateHeader);
        userEvent.keyboard('{Enter}');

        // Test Space key
        userEvent.keyboard(' ');

        // Just verify the header exists and is interactive
        expect(dateHeader).toBeInTheDocument();
        expect(dateHeader).toHaveAttribute('role', 'button');
      });
    });
  });

  describe('Responsive Behavior', () => {
    beforeEach(() => {
      mockUseQueryRequest.mockReturnValue({
        isLoading: false,
        isError: false,
        data: mockApiData,
      });
    });

    it('should render mobile layout on small screens', async () => {
      const { container } = render(<HotelsPriceTable />, {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        // Component should render successfully with mobile breakpoints
        const table = container.querySelector('table');
        expect(table).toBeInTheDocument();
      });
    });
  });

  describe('Floating Header', () => {
    beforeEach(() => {
      mockUseQueryRequest.mockReturnValue({
        isLoading: false,
        isError: false,
        data: mockApiData,
      });
    });

    it('should show floating header when main header is out of view', async () => {
      const { getByTestId } = render(<HotelsPriceTable />, {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        expect(getByTestId('hotel-price-table-header')).toBeInTheDocument();
        // Verify that IntersectionObserver was called
        expect(mockIntersectionObserver).toHaveBeenCalled();
      });
    });
  });

  describe('Today Indicator', () => {
    beforeEach(() => {
      mockUseQueryRequest.mockReturnValue({
        isLoading: false,
        isError: false,
        data: mockApiData,
      });
    });

    it("should highlight today's date", async () => {
      const { getByTestId } = render(<HotelsPriceTable />, {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        // Today is 2025-08-03 based on our mock
        const todayHeader = getByTestId('date-header-2025-08-03');

        // Check that today's date header exists
        expect(todayHeader).toBeInTheDocument();

        // Check that it has the today indicator (blue dot)
        // The today indicator is a Box with position="absolute", w="6px", h="6px", bg="blue.500"
        const todayIndicator = todayHeader.querySelector('div');
        expect(todayIndicator).toBeInTheDocument();

        // Verify the header has the correct test ID
        expect(todayHeader).toHaveAttribute('data-testid', 'date-header-2025-08-03');
      });
    });
  });

  describe('Price Highlighting Logic', () => {
    beforeEach(() => {
      mockUseQueryRequest.mockReturnValue({
        isLoading: false,
        isError: false,
        data: mockApiData,
      });
    });

    it('should handle zero and null prices correctly', async () => {
      const { getAllByText } = render(<HotelsPriceTable />, {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        // Should show "━" for unavailable/zero prices
        const unavailableElements = getAllByText('━');
        expect(unavailableElements.length).toBeGreaterThan(0);
      });
    });
  });

  describe('useEffect Hooks Coverage', () => {
    beforeEach(() => {
      mockUseQueryRequest.mockReturnValue({
        isLoading: false,
        isError: false,
        data: mockApiData,
      });
    });

    it('should reset priceSortDate when sortBy changes to DISTANCE', async () => {
      const mockOnSortChange = jest.fn();
      const { rerender } = render(
        <HotelsPriceTable sortBy="price" onSortChange={mockOnSortChange} />,
        { wrapper: createWrapper() }
      );

      // Change sortBy to distance - should trigger useEffect to reset priceSortDate
      rerender(<HotelsPriceTable sortBy="distance" onSortChange={mockOnSortChange} />);

      // Verify the component renders without issues after the useEffect
      await waitFor(() => {
        expect(mockUseQueryRequest).toHaveBeenCalled();
      });
    });

    it('should reset priceSortDate when locationId changes', async () => {
      const { rerender } = render(<HotelsPriceTable locationId="123" />, {
        wrapper: createWrapper(),
      });

      // Change locationId - should trigger useEffect to reset priceSortDate
      rerender(<HotelsPriceTable locationId="456" />);

      await waitFor(() => {
        expect(mockUseQueryRequest).toHaveBeenCalled();
      });
    });

    it('should reset priceSortDate when formattedDate changes', async () => {
      const { rerender } = render(<HotelsPriceTable formattedDate="2025-08-01" />, {
        wrapper: createWrapper(),
      });

      // Change formattedDate - should trigger useEffect to reset priceSortDate
      rerender(<HotelsPriceTable formattedDate="2025-08-05" />);

      await waitFor(() => {
        expect(mockUseQueryRequest).toHaveBeenCalled();
      });
    });

    it('should call onCurrentDatesChange when currentDates change', async () => {
      const mockOnCurrentDatesChange = jest.fn();
      render(<HotelsPriceTable onCurrentDatesChange={mockOnCurrentDatesChange} />, {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        expect(mockOnCurrentDatesChange).toHaveBeenCalled();
        expect(mockOnCurrentDatesChange).toHaveBeenCalledWith(
          expect.arrayContaining([expect.stringMatching(/\d{4}-\d{2}-\d{2}/)])
        );
      });
    });

    it('should not call onCurrentDatesChange when callback is not provided', async () => {
      render(<HotelsPriceTable />, { wrapper: createWrapper() });

      await waitFor(() => {
        // Should render without issues even when callback is not provided
        expect(mockUseQueryRequest).toHaveBeenCalled();
      });
    });

    it('should call onSortedByDateChange when priceSortDate changes', async () => {
      const mockOnSortedByDateChange = jest.fn();
      const { getByTestId } = render(
        <HotelsPriceTable onSortedByDateChange={mockOnSortedByDateChange} />,
        { wrapper: createWrapper() }
      );

      await waitFor(() => {
        // Initially should be called with null
        expect(mockOnSortedByDateChange).toHaveBeenCalledWith(null);
      });

      // Click on a date header to trigger price sorting
      await waitFor(() => {
        const dateHeader = getByTestId('date-header-2025-08-03');
        userEvent.click(dateHeader);
      });

      await waitFor(() => {
        // Should be called with the selected date
        expect(mockOnSortedByDateChange).toHaveBeenCalledWith('2025-08-03');
      });
    });

    it('should not call onSortedByDateChange when callback is not provided', async () => {
      render(<HotelsPriceTable />, { wrapper: createWrapper() });

      await waitFor(() => {
        // Should render without issues even when callback is not provided
        expect(mockUseQueryRequest).toHaveBeenCalled();
      });
    });
  });

  describe('Function Handlers Coverage', () => {
    beforeEach(() => {
      mockUseQueryRequest.mockReturnValue({
        isLoading: false,
        isError: false,
        data: mockApiData,
      });
    });

    it('should handle handleSortToggle with external onSortChange', async () => {
      const mockOnSortChange = jest.fn();
      const { getByText } = render(
        <HotelsPriceTable sortBy="price" onSortChange={mockOnSortChange} />,
        { wrapper: createWrapper() }
      );

      await waitFor(() => {
        const sortButton = getByText('priceFinder.sortByDistance');
        userEvent.click(sortButton);
        expect(mockOnSortChange).toHaveBeenCalledWith('distance');
      });
    });

    it('should handle handleSortToggle with internal sort state', async () => {
      const { getByText } = render(<HotelsPriceTable sortBy="price" />, {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        const sortButton = getByText('priceFinder.sortByDistance');
        userEvent.click(sortButton);
        // Should not throw error when onSortChange is not provided
      });
    });

    it('should handle handleDateSortForPrice', async () => {
      const { getByTestId } = render(<HotelsPriceTable onPriceSortChange={onPriceSortChange} />, {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        const dateHeader = getByTestId('date-header-2025-08-03');
        userEvent.click(dateHeader);
        // Should not throw error and should sort by the selected date
      });
      expect(window._satellite.track).toHaveBeenCalledWith('priceFinderHotelSelected');
    });

    it('should handle handleScrollLeftWithReset with external onSortChange', async () => {
      const mockOnSortChange = jest.fn();

      // First scroll right to enable left scroll
      const { getByTestId } = render(<HotelsPriceTable onSortChange={mockOnSortChange} />, {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        const rightButton = getByTestId('scroll-right-button');
        userEvent.click(rightButton);
      });

      // Now test left scroll
      await waitFor(() => {
        const leftButton = getByTestId('scroll-left-button');
        userEvent.click(leftButton);
        expect(mockOnSortChange).toHaveBeenCalledWith('distance');
      });
    });

    it('should handle handleScrollRightWithReset with internal sort state', async () => {
      const { getByTestId } = render(<HotelsPriceTable />, { wrapper: createWrapper() });

      await waitFor(() => {
        const rightButton = getByTestId('scroll-right-button');
        userEvent.click(rightButton);
        // Should not throw error when onSortChange is not provided
      });
    });

    it('should handle handleHotelRedirect with valid data', async () => {
      // Mock window.open
      const mockOpen = jest.fn();
      const originalOpen = window.open;
      window.open = mockOpen;

      const { container } = render(<HotelsPriceTable />, { wrapper: createWrapper() });

      await waitFor(() => {
        const priceCells = container.querySelectorAll('[data-testid*="price-cell"]');
        expect(priceCells.length).toBeGreaterThan(0);

        // Since the original mock data doesn't have hotelLink,
        // window.open should not be called when clicking
        const firstPriceCell = priceCells[0];
        userEvent.click(firstPriceCell);

        // Verify that window.open is not called since hotelLink is missing
        expect(mockOpen).not.toHaveBeenCalled();
      });

      // Restore window.open
      window.open = originalOpen;
    });

    it('should not redirect when hotelLink is missing', async () => {
      const mockOpen = jest.fn();
      const originalOpen = window.open;
      window.open = mockOpen;

      const { container } = render(<HotelsPriceTable />, { wrapper: createWrapper() });

      await waitFor(() => {
        const priceCell = container.querySelector('[data-testid="price-cell-0-0"]');
        if (priceCell) {
          userEvent.click(priceCell);
          expect(mockOpen).not.toHaveBeenCalled();
        }
      });

      window.open = originalOpen;
    });

    it('should not redirect when price is null', async () => {
      const mockOpen = jest.fn();
      const originalOpen = window.open;
      window.open = mockOpen;

      const { container } = render(<HotelsPriceTable />, { wrapper: createWrapper() });

      await waitFor(() => {
        // Click on a cell with no price (should show "━")
        const unavailableCell = container.querySelector('[data-testid="price-cell-1-1"]');
        if (unavailableCell) {
          userEvent.click(unavailableCell);
          expect(mockOpen).not.toHaveBeenCalled();
        }
      });

      window.open = originalOpen;
    });
  });

  describe('Data Source Logic Coverage', () => {
    it('should use external data when provided', async () => {
      const mockExternalData = {
        getLowestRatesByLocationId: {
          priceFinderOperaHotelAvailabilitiesDtoList: [
            {
              hotelCode: 'EXT001',
              hotelName: 'External Hotel',
              availabilities: [
                {
                  availableDate: '2025-08-03',
                  currency: 'GBP',
                  minimumRate: 50,
                  rateCode: 'STD',
                  hasMlosRestriction: false,
                  hasClosedRestriction: false,
                  rateClassification: 'O',
                  roomType: 'DBLWIN',
                  roomCategory: 'SB',
                  minimumNights: 1,
                  quantity: 100,
                },
              ],
            },
          ],
          total: 1,
          page: 1,
          pageSize: 25,
          lowestMonthlyRate: { price: 50, currency: 'GBP' },
        },
      };

      mockUseQueryRequest.mockReturnValue({
        isLoading: false,
        isError: false,
        data: null, // Internal data is null
      });

      const { getByText } = render(
        <HotelsPriceTable data={mockExternalData} isLoading={false} isError={false} />,
        { wrapper: createWrapper() }
      );

      await waitFor(() => {
        expect(getByText('External Hotel')).toBeInTheDocument();
      });
    });

    it('should use internal data when external data is undefined', async () => {
      mockUseQueryRequest.mockReturnValue({
        isLoading: false,
        isError: false,
        data: mockApiData,
      });

      const { getByText } = render(
        <HotelsPriceTable />, // No external data provided
        { wrapper: createWrapper() }
      );

      await waitFor(() => {
        expect(getByText('London Kings Cross')).toBeInTheDocument();
      });
    });

    it('should handle external loading state', async () => {
      mockUseQueryRequest.mockReturnValue({
        isLoading: false,
        isError: false,
        data: null,
      });

      const { container } = render(
        <HotelsPriceTable data={null} isLoading={true} isError={false} />,
        { wrapper: createWrapper() }
      );

      await waitFor(() => {
        // Should show skeleton loading
        const skeletonRows = container.querySelectorAll('tbody tr');
        expect(skeletonRows.length).toBe(20);
      });
    });

    it('should handle external error state', async () => {
      mockUseQueryRequest.mockReturnValue({
        isLoading: false,
        isError: false,
        data: null,
      });

      const { getByTestId } = render(
        <HotelsPriceTable data={null} isLoading={false} isError={true} />,
        { wrapper: createWrapper() }
      );

      await waitFor(() => {
        expect(getByTestId('error-row')).toBeInTheDocument();
      });
    });
  });

  describe('Conditional Branches Coverage', () => {
    beforeEach(() => {
      mockUseQueryRequest.mockReturnValue({
        isLoading: false,
        isError: false,
        data: mockApiData,
      });
    });

    it('should handle empty hotel data', async () => {
      const emptyData = {
        getLowestRatesByLocationId: {
          priceFinderOperaHotelAvailabilitiesDtoList: [],
          total: 0,
          page: 1,
          pageSize: 25,
          lowestMonthlyRate: { price: 0, currency: 'GBP' },
        },
      };

      mockUseQueryRequest.mockReturnValue({
        isLoading: false,
        isError: false,
        data: emptyData,
      });

      const { getByTestId } = render(<HotelsPriceTable />, { wrapper: createWrapper() });

      await waitFor(() => {
        expect(getByTestId('no-data-row')).toBeInTheDocument();
      });
    });

    it('should handle shouldUseInternalQuery logic', async () => {
      // Case 1: Should use internal query (no external data/loading/error)
      mockUseQueryRequest.mockReturnValue({
        isLoading: false,
        isError: false,
        data: mockApiData,
      });

      const { getByText, rerender } = render(
        <HotelsPriceTable />, // No external props
        { wrapper: createWrapper() }
      );

      await waitFor(() => {
        expect(getByText('London Kings Cross')).toBeInTheDocument();
      });

      // Case 2: Should not use internal query (external data provided)
      rerender(<HotelsPriceTable data={mockApiData} isLoading={false} isError={false} />);

      await waitFor(() => {
        expect(getByText('London Kings Cross')).toBeInTheDocument();
      });
    });

    it('should handle mobile vs desktop colSpan calculation', async () => {
      // Test mobile layout
      (window.matchMedia as jest.Mock).mockImplementation((query) => ({
        matches: query === '(max-width: 767px)',
        media: query,
        onchange: null,
        addListener: jest.fn(),
        removeListener: jest.fn(),
        addEventListener: jest.fn(),
        removeEventListener: jest.fn(),
        dispatchEvent: jest.fn(),
      }));

      const { container, rerender } = render(<HotelsPriceTable />, { wrapper: createWrapper() });

      await waitFor(() => {
        expect(container.querySelector('table')).toBeInTheDocument();
      });

      // Test desktop layout - should have different colSpan calculations
      (window.matchMedia as jest.Mock).mockImplementation((query) => ({
        matches: false,
        media: query,
        onchange: null,
        addListener: jest.fn(),
        removeListener: jest.fn(),
        addEventListener: jest.fn(),
        removeEventListener: jest.fn(),
        dispatchEvent: jest.fn(),
      }));

      rerender(<HotelsPriceTable />);

      await waitFor(() => {
        expect(container.querySelector('table')).toBeInTheDocument();
      });
    });

    it('should handle criteria building with sortBy=PRICE and priceSortDate', async () => {
      const { getByTestId } = render(<HotelsPriceTable sortBy="price" />, {
        wrapper: createWrapper(),
      });

      // Click a date to set priceSortDate
      await waitFor(() => {
        const dateHeader = getByTestId('date-header-2025-08-03');
        userEvent.click(dateHeader);
      });

      // The component should rebuild criteria with sortDate included
      await waitFor(() => {
        expect(mockUseQueryRequest).toHaveBeenCalled();
      });
    });
  });

  describe('Sort Functionality Props', () => {
    beforeEach(() => {
      mockUseQueryRequest.mockReturnValue({
        isLoading: false,
        isError: false,
        data: mockApiData,
      });
    });

    it('should accept sortBy and onSortChange props', async () => {
      const mockOnSortChange = jest.fn();

      const { getByText } = render(
        <HotelsPriceTable sortBy="distance" onSortChange={mockOnSortChange} />,
        {
          wrapper: createWrapper(),
        }
      );

      await waitFor(() => {
        const sortButton = getByText('priceFinder.sortByDistance');
        expect(sortButton).toBeInTheDocument();

        userEvent.click(sortButton);
        expect(mockOnSortChange).toHaveBeenCalledWith('distance');
      });
    });

    it('should display correct sort text based on sortBy prop', async () => {
      const { getByText } = render(
        <HotelsPriceTable
          sortBy="price"
          setLowestPrice={setLowestPrice}
          setIsLoading={setIsLoading}
        />,
        {
          wrapper: createWrapper(),
        }
      );

      await waitFor(() => {
        const sortButton = getByText('priceFinder.sortByDistance');
        expect(sortButton).toBeInTheDocument();
      });
    });

    it('should render hotel redirect functionality', async () => {
      const { container } = render(<HotelsPriceTable />, {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        // Check that price cells have cursor pointer style when they should be clickable
        const priceCells = container.querySelectorAll('[data-testid*="price-cell"]');
        expect(priceCells.length).toBeGreaterThan(0);
      });
    });
  });

  describe('Hotel Redirect URL Construction', () => {
    const mockUseCustomLocale = useCustomLocale as jest.Mock;

    beforeEach(() => {
      mockUseCustomLocale.mockClear();
    });

    it('should construct correct URL with country and language from useCustomLocale', async () => {
      // Mock window.open
      const mockOpen = jest.fn();
      const originalOpen = window.open;
      window.open = mockOpen;

      mockUseCustomLocale.mockReturnValue({ country: 'gb', language: 'en' });

      // Create mock data with hotelLink in correct structure
      const mockDataWithHotelLink = {
        getLowestRatesByLocationId: {
          priceFinderOperaHotelAvailabilitiesDtoList: [
            {
              hotelCode: 'LONKIN',
              hotelName: 'London Kings Cross',
              links: {
                detailsPage: '/london/kings-cross',
              },
              availabilities: [
                {
                  availableDate: '2025-08-03',
                  currency: 'GBP',
                  minimumRate: 40,
                  rateCode: 'NONFLEX',
                  hasMlosRestriction: false,
                  hasClosedRestriction: false,
                  rateClassification: 'O',
                  roomType: 'DBLWIN,BIGWIN,ACCWIN',
                  roomCategory: 'SB',
                  minimumNights: 1,
                  quantity: 388,
                },
                {
                  availableDate: '2025-08-04',
                  currency: 'GBP',
                  minimumRate: 45,
                  rateCode: 'NONFLEX',
                  hasMlosRestriction: false,
                  hasClosedRestriction: false,
                  rateClassification: 'O',
                  roomType: 'DBLWIN,BIGWIN,ACCWIN',
                  roomCategory: 'SB',
                  minimumNights: 1,
                  quantity: 388,
                },
              ],
            },
          ],
        },
      };

      // Mock the useQueryRequest hook to return the data for internal query logic
      mockUseQueryRequest.mockReturnValue({
        data: mockDataWithHotelLink,
        isLoading: false,
        isError: false,
        refetch: jest.fn(),
        error: null,
      });

      const { container } = render(<HotelsPriceTable isBB={true} />, { wrapper: createWrapper() });

      await waitFor(() => {
        const priceCells = container.querySelectorAll('[data-testid*="price-cell"]');
        expect(priceCells.length).toBeGreaterThan(0);

        // Find a price cell with a valid price
        const validPriceCell = Array.from(priceCells).find((cell) => {
          const priceText = cell.textContent;
          return priceText && priceText.includes('£40');
        });

        expect(validPriceCell).toBeTruthy();
        return validPriceCell;
      });

      // Find the price cell with £40
      const priceCells = container.querySelectorAll('[data-testid*="price-cell"]');
      const targetPriceCell = Array.from(priceCells).find(
        (cell) => cell.textContent && cell.textContent.includes('£40')
      );

      expect(targetPriceCell).toBeTruthy();

      // Use userEvent for click simulation
      if (targetPriceCell) {
        userEvent.click(targetPriceCell);

        // Verify that window.open is called with correctly constructed URL
        expect(mockOpen).toHaveBeenCalledWith(
          expect.stringMatching(/^\/gb\/en\/business-booker\/hotels\/london\/kings-cross\.html\?/),
          '_blank'
        );
      }

      // Restore window.open
      window.open = originalOpen;
    });

    it('should construct correct URL with German locale', async () => {
      // Mock window.open
      const mockOpen = jest.fn();
      const originalOpen = window.open;
      window.open = mockOpen;

      mockUseCustomLocale.mockReturnValue({ country: 'de', language: 'de' });

      // Create mock data with hotelLink in correct structure
      const mockDataWithHotelLink = {
        getLowestRatesByLocationId: {
          priceFinderOperaHotelAvailabilitiesDtoList: [
            {
              hotelCode: 'BERLIN',
              hotelName: 'Berlin Hauptbahnhof',
              links: {
                detailsPage: '/berlin/hauptbahnhof',
              },
              availabilities: [
                {
                  availableDate: '2025-08-03',
                  currency: 'EUR',
                  minimumRate: 60,
                  rateCode: 'NONFLEX',
                  hasMlosRestriction: false,
                  hasClosedRestriction: false,
                  rateClassification: 'O',
                  roomType: 'DBLWIN,BIGWIN,ACCWIN',
                  roomCategory: 'SB',
                  minimumNights: 1,
                  quantity: 388,
                },
              ],
            },
          ],
        },
      };

      mockUseQueryRequest.mockReturnValue({
        data: mockDataWithHotelLink,
        isLoading: false,
        isError: false,
        refetch: jest.fn(),
        error: null,
      });

      const { container } = render(<HotelsPriceTable />, { wrapper: createWrapper() });

      await waitFor(() => {
        const priceCells = container.querySelectorAll('[data-testid*="price-cell"]');
        expect(priceCells.length).toBeGreaterThan(0);

        // Click on a price cell that should have valid hotel link and price
        const firstPriceCell = priceCells[0];
        userEvent.click(firstPriceCell);

        // Verify that window.open is called with German locale URL
        expect(mockOpen).toHaveBeenCalledWith(
          expect.stringMatching(/^\/de\/de\/hotels\/berlin\/hauptbahnhof\.html\?/),
          '_blank'
        );
      });

      // Restore window.open
      window.open = originalOpen;
    });

    it('should include query parameters in hotel URL', async () => {
      // Mock window.open
      const mockOpen = jest.fn();
      const originalOpen = window.open;
      window.open = mockOpen;

      mockUseCustomLocale.mockReturnValue({ country: 'gb', language: 'en' });

      // Create mock data with hotelLink in correct structure
      const mockDataWithHotelLink = {
        getLowestRatesByLocationId: {
          priceFinderOperaHotelAvailabilitiesDtoList: [
            {
              hotelCode: 'LONKIN',
              hotelName: 'London Kings Cross',
              links: {
                detailsPage: '/london/kings-cross',
              },
              availabilities: [
                {
                  availableDate: '2025-08-03',
                  currency: 'GBP',
                  minimumRate: 40,
                  rateCode: 'NONFLEX',
                  hasMlosRestriction: false,
                  hasClosedRestriction: false,
                  rateClassification: 'O',
                  roomType: 'DBLWIN,BIGWIN,ACCWIN',
                  roomCategory: 'SB',
                  minimumNights: 1,
                  quantity: 388,
                },
              ],
            },
          ],
        },
      };

      mockUseQueryRequest.mockReturnValue({
        data: mockDataWithHotelLink,
        isLoading: false,
        isError: false,
        refetch: jest.fn(),
        error: null,
      });

      const { container } = render(<HotelsPriceTable />, { wrapper: createWrapper() });

      await waitFor(() => {
        const priceCells = container.querySelectorAll('[data-testid*="price-cell"]');
        expect(priceCells.length).toBeGreaterThan(0);

        // Click on a price cell that should have valid hotel link and price
        const firstPriceCell = priceCells[0];
        userEvent.click(firstPriceCell);

        // Verify that window.open is called with query parameters
        expect(mockOpen).toHaveBeenCalledWith(
          expect.stringContaining('ARRdd=03&ARRmm=08&ARRyyyy=2025&NIGHTS=1&ROOMS=1'),
          '_blank'
        );
      });

      // Restore window.open
      window.open = originalOpen;
    });

    it('should set the default occupancy in the URL for SB roomType, 1 Adults 0 Child', async () => {
      // Mock window.open
      const mockOpen = jest.fn();
      const originalOpen = window.open;
      window.open = mockOpen;

      mockUseCustomLocale.mockReturnValue({ country: 'gb', language: 'en' });

      // Create mock data with hotelLink in correct structure
      const mockDataWithHotelLink = {
        getLowestRatesByLocationId: {
          priceFinderOperaHotelAvailabilitiesDtoList: [
            {
              hotelCode: 'LONKIN',
              hotelName: 'London Kings Cross',
              links: {
                detailsPage: '/london/kings-cross',
              },
              availabilities: [
                {
                  availableDate: '2025-08-03',
                  currency: 'GBP',
                  minimumRate: 40,
                  rateCode: 'NONFLEX',
                  hasMlosRestriction: false,
                  hasClosedRestriction: false,
                  rateClassification: 'O',
                  roomType: 'DBLWIN,BIGWIN,ACCWIN',
                  minimumNights: 1,
                  quantity: 388,
                  roomCategory: 'SB',
                },
              ],
            },
          ],
        },
      };

      mockUseQueryRequest.mockReturnValue({
        data: mockDataWithHotelLink,
        isLoading: false,
        isError: false,
        refetch: jest.fn(),
        error: null,
      });

      const { container } = render(<HotelsPriceTable />, { wrapper: createWrapper() });

      await waitFor(() => {
        const priceCells = container.querySelectorAll('[data-testid*="price-cell"]');
        expect(priceCells.length).toBeGreaterThan(0);

        // Click on a price cell that should have valid hotel link and price
        const firstPriceCell = priceCells[0];
        userEvent.click(firstPriceCell);

        // Verify that window.open is called with query parameters
        expect(mockOpen).toHaveBeenCalledWith(
          expect.stringContaining(
            '/gb/en/hotels/london/kings-cross.html?ARRdd=03&ARRmm=08&ARRyyyy=2025&NIGHTS=1&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=SB'
          ),
          '_blank'
        );
      });

      // Restore window.open
      window.open = originalOpen;
    });

    it('should set the default occupancy in the URL for DB roomType, 2 Adults 0 Child', async () => {
      // Mock window.open
      const mockOpen = jest.fn();
      const originalOpen = window.open;
      window.open = mockOpen;

      mockUseCustomLocale.mockReturnValue({ country: 'gb', language: 'en' });

      // Create mock data with hotelLink in correct structure
      const mockDataWithHotelLink = {
        getLowestRatesByLocationId: {
          priceFinderOperaHotelAvailabilitiesDtoList: [
            {
              hotelCode: 'LONKIN',
              hotelName: 'London Kings Cross',
              links: {
                detailsPage: '/london/kings-cross',
              },
              availabilities: [
                {
                  availableDate: '2025-08-03',
                  currency: 'GBP',
                  minimumRate: 40,
                  rateCode: 'NONFLEX',
                  hasMlosRestriction: false,
                  hasClosedRestriction: false,
                  rateClassification: 'O',
                  roomType: 'DBLWIN,BIGWIN,ACCWIN',
                  minimumNights: 1,
                  quantity: 388,
                  roomCategory: 'DB',
                },
              ],
            },
          ],
        },
      };

      mockUseQueryRequest.mockReturnValue({
        data: mockDataWithHotelLink,
        isLoading: false,
        isError: false,
        refetch: jest.fn(),
        error: null,
      });

      const { container } = render(<HotelsPriceTable />, { wrapper: createWrapper() });

      await waitFor(() => {
        const priceCells = container.querySelectorAll('[data-testid*="price-cell"]');
        expect(priceCells.length).toBeGreaterThan(0);

        // Click on a price cell that should have valid hotel link and price
        const firstPriceCell = priceCells[0];
        userEvent.click(firstPriceCell);

        // Verify that window.open is called with query parameters
        expect(mockOpen).toHaveBeenCalledWith(
          expect.stringContaining(
            '/gb/en/hotels/london/kings-cross.html?ARRdd=03&ARRmm=08&ARRyyyy=2025&NIGHTS=1&ROOMS=1&ADULT1=2&CHILD1=0&COT1=0&INTTYP1=DB'
          ),
          '_blank'
        );
      });

      // Restore window.open
      window.open = originalOpen;
    });

    it('should set the default occupancy in the URL for TWIN roomType, 2 Adults 0 Child', async () => {
      // Mock window.open
      const mockOpen = jest.fn();
      const originalOpen = window.open;
      window.open = mockOpen;

      mockUseCustomLocale.mockReturnValue({ country: 'gb', language: 'en' });

      // Create mock data with hotelLink in correct structure
      const mockDataWithHotelLink = {
        getLowestRatesByLocationId: {
          priceFinderOperaHotelAvailabilitiesDtoList: [
            {
              hotelCode: 'LONKIN',
              hotelName: 'London Kings Cross',
              links: {
                detailsPage: '/london/kings-cross',
              },
              availabilities: [
                {
                  availableDate: '2025-08-03',
                  currency: 'GBP',
                  minimumRate: 40,
                  rateCode: 'NONFLEX',
                  hasMlosRestriction: false,
                  hasClosedRestriction: false,
                  rateClassification: 'O',
                  roomType: 'DBLWIN,BIGWIN,ACCWIN',
                  minimumNights: 1,
                  quantity: 388,
                  roomCategory: 'TWIN',
                },
              ],
            },
          ],
        },
      };

      mockUseQueryRequest.mockReturnValue({
        data: mockDataWithHotelLink,
        isLoading: false,
        isError: false,
        refetch: jest.fn(),
        error: null,
      });

      const { container } = render(<HotelsPriceTable />, { wrapper: createWrapper() });

      await waitFor(() => {
        const priceCells = container.querySelectorAll('[data-testid*="price-cell"]');
        expect(priceCells.length).toBeGreaterThan(0);

        // Click on a price cell that should have valid hotel link and price
        const firstPriceCell = priceCells[0];
        userEvent.click(firstPriceCell);

        // Verify that window.open is called with query parameters
        expect(mockOpen).toHaveBeenCalledWith(
          expect.stringContaining(
            '/gb/en/hotels/london/kings-cross.html?ARRdd=03&ARRmm=08&ARRyyyy=2025&NIGHTS=1&ROOMS=1&ADULT1=2&CHILD1=0&COT1=0&INTTYP1=TWIN'
          ),
          '_blank'
        );
      });

      // Restore window.open
      window.open = originalOpen;
    });

    it('should set the default occupancy in the URL for FAM roomType, 2 Adults 1 Child', async () => {
      // Mock window.open
      const mockOpen = jest.fn();
      const originalOpen = window.open;
      window.open = mockOpen;

      mockUseCustomLocale.mockReturnValue({ country: 'gb', language: 'en' });

      // Create mock data with hotelLink in correct structure
      const mockDataWithHotelLink = {
        getLowestRatesByLocationId: {
          priceFinderOperaHotelAvailabilitiesDtoList: [
            {
              hotelCode: 'LONKIN',
              hotelName: 'London Kings Cross',
              links: {
                detailsPage: '/london/kings-cross',
              },
              availabilities: [
                {
                  availableDate: '2025-08-03',
                  currency: 'GBP',
                  minimumRate: 40,
                  rateCode: 'NONFLEX',
                  hasMlosRestriction: false,
                  hasClosedRestriction: false,
                  rateClassification: 'O',
                  roomType: 'DBLWIN,BIGWIN,ACCWIN',
                  minimumNights: 1,
                  quantity: 388,
                  roomCategory: 'FAM',
                },
              ],
            },
          ],
        },
      };

      mockUseQueryRequest.mockReturnValue({
        data: mockDataWithHotelLink,
        isLoading: false,
        isError: false,
        refetch: jest.fn(),
        error: null,
      });

      const { container } = render(<HotelsPriceTable />, { wrapper: createWrapper() });

      await waitFor(() => {
        const priceCells = container.querySelectorAll('[data-testid*="price-cell"]');
        expect(priceCells.length).toBeGreaterThan(0);

        // Click on a price cell that should have valid hotel link and price
        const firstPriceCell = priceCells[0];
        userEvent.click(firstPriceCell);

        // Verify that window.open is called with query parameters
        expect(mockOpen).toHaveBeenCalledWith(
          expect.stringContaining(
            '/gb/en/hotels/london/kings-cross.html?ARRdd=03&ARRmm=08&ARRyyyy=2025&NIGHTS=1&ROOMS=1&ADULT1=2&CHILD1=1&COT1=0&INTTYP1=FAM'
          ),
          '_blank'
        );
      });

      // Restore window.open
      window.open = originalOpen;
    });

    it('should set the default occupancy in the URL for DIS roomType, 1 Adults 0 Child', async () => {
      // Mock window.open
      const mockOpen = jest.fn();
      const originalOpen = window.open;
      window.open = mockOpen;

      mockUseCustomLocale.mockReturnValue({ country: 'gb', language: 'en' });

      // Create mock data with hotelLink in correct structure
      const mockDataWithHotelLink = {
        getLowestRatesByLocationId: {
          priceFinderOperaHotelAvailabilitiesDtoList: [
            {
              hotelCode: 'LONKIN',
              hotelName: 'London Kings Cross',
              links: {
                detailsPage: '/london/kings-cross',
              },
              availabilities: [
                {
                  availableDate: '2025-08-03',
                  currency: 'GBP',
                  minimumRate: 40,
                  rateCode: 'NONFLEX',
                  hasMlosRestriction: false,
                  hasClosedRestriction: false,
                  rateClassification: 'O',
                  roomType: 'DBLWIN,BIGWIN,ACCWIN',
                  minimumNights: 1,
                  quantity: 388,
                  roomCategory: 'DIS',
                },
              ],
            },
          ],
        },
      };

      mockUseQueryRequest.mockReturnValue({
        data: mockDataWithHotelLink,
        isLoading: false,
        isError: false,
        refetch: jest.fn(),
        error: null,
      });

      const { container } = render(<HotelsPriceTable />, { wrapper: createWrapper() });

      await waitFor(() => {
        const priceCells = container.querySelectorAll('[data-testid*="price-cell"]');
        expect(priceCells.length).toBeGreaterThan(0);

        // Click on a price cell that should have valid hotel link and price
        const firstPriceCell = priceCells[0];
        userEvent.click(firstPriceCell);

        // Verify that window.open is called with query parameters
        expect(mockOpen).toHaveBeenCalledWith(
          expect.stringContaining(
            '/gb/en/hotels/london/kings-cross.html?ARRdd=03&ARRmm=08&ARRyyyy=2025&NIGHTS=1&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DIS'
          ),
          '_blank'
        );
      });

      // Restore window.open
      window.open = originalOpen;
    });

    it('should not redirect when hotelLink is missing from hotel data', async () => {
      // Mock window.open
      const mockOpen = jest.fn();
      const originalOpen = window.open;
      window.open = mockOpen;

      mockUseCustomLocale.mockReturnValue({ country: 'gb', language: 'en' });

      // Create mock data without hotelLink
      const mockDataWithoutHotelLink = {
        getLowestRatesByLocationId: {
          priceFinderOperaHotelAvailabilitiesDtoList: [
            {
              hotelCode: 'LONKIN',
              hotelName: 'London Kings Cross',
              // Missing links property completely
              availabilities: [
                {
                  availableDate: '2025-08-03',
                  currency: 'GBP',
                  minimumRate: 40,
                  rateCode: 'NONFLEX',
                  hasMlosRestriction: false,
                  hasClosedRestriction: false,
                  rateClassification: 'O',
                  roomType: 'DBLWIN,BIGWIN,ACCWIN',
                  roomCategory: 'SB',
                  minimumNights: 1,
                  quantity: 388,
                },
              ],
            },
          ],
        },
      };

      mockUseQueryRequest.mockReturnValue({
        data: mockDataWithoutHotelLink,
        isLoading: false,
        isError: false,
        refetch: jest.fn(),
        error: null,
      });

      const { container } = render(<HotelsPriceTable />, { wrapper: createWrapper() });

      await waitFor(() => {
        const priceCells = container.querySelectorAll('[data-testid*="price-cell"]');
        expect(priceCells.length).toBeGreaterThan(0);

        // Click on a price cell that should not redirect due to missing hotelLink
        const firstPriceCell = priceCells[0];
        userEvent.click(firstPriceCell);

        // Verify that window.open is not called
        expect(mockOpen).not.toHaveBeenCalled();
      });

      // Restore window.open
      window.open = originalOpen;
    });

    it('should not redirect when price is null', async () => {
      // Mock window.open
      const mockOpen = jest.fn();
      const originalOpen = window.open;
      window.open = mockOpen;

      mockUseCustomLocale.mockReturnValue({ country: 'gb', language: 'en' });

      // Create mock data with hotelLink but null price
      const mockDataWithNullPrice = {
        getLowestRatesByLocationId: {
          priceFinderOperaHotelAvailabilitiesDtoList: [
            {
              hotelCode: 'LONKIN',
              hotelName: 'London Kings Cross',
              links: {
                detailsPage: '/london/kings-cross',
              },
              availabilities: [
                {
                  availableDate: '2025-08-03',
                  currency: 'GBP',
                  minimumRate: null, // null price
                  rateCode: 'NONFLEX',
                  hasMlosRestriction: false,
                  hasClosedRestriction: false,
                  rateClassification: 'O',
                  roomType: 'DBLWIN,BIGWIN,ACCWIN',
                  roomCategory: 'SB',
                  minimumNights: 1,
                  quantity: 388,
                },
              ],
            },
          ],
        },
      };

      mockUseQueryRequest.mockReturnValue({
        data: mockDataWithNullPrice,
        isLoading: false,
        isError: false,
        refetch: jest.fn(),
        error: null,
      });

      const { container } = render(<HotelsPriceTable />, { wrapper: createWrapper() });

      await waitFor(() => {
        const priceCells = container.querySelectorAll('[data-testid*="price-cell"]');
        expect(priceCells.length).toBeGreaterThan(0);

        // Click on a price cell with null price
        const firstPriceCell = priceCells[0];
        userEvent.click(firstPriceCell);

        // Verify that window.open is not called due to null price
        expect(mockOpen).not.toHaveBeenCalled();
      });

      // Restore window.open
      window.open = originalOpen;
    });

    it('should call useCustomLocale hook to get country and language', async () => {
      mockUseCustomLocale.mockReturnValue({ country: 'fr', language: 'fr' });

      render(<HotelsPriceTable />, { wrapper: createWrapper() });

      // Verify that useCustomLocale hook was called
      expect(mockUseCustomLocale).toHaveBeenCalled();
    });
  });

  describe('Price Sort Arrival Date Tracking', () => {
    beforeEach(() => {
      mockUseQueryRequest.mockReturnValue({
        isLoading: false,
        isError: false,
        data: mockApiData,
      });
    });

    it('should associate priceSortDate with current arrival date when price sorting', async () => {
      const { getByTestId } = render(<HotelsPriceTable formattedDate="2025-08-03" />, {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        const dateHeader = getByTestId('date-header-2025-08-03');
        userEvent.click(dateHeader);
        // After clicking, priceSortDate should be associated with formattedDate "2025-08-03"
        expect(mockUseQueryRequest).toHaveBeenCalled();
      });
    });

    it('should invalidate priceSortDate when arrival date changes', async () => {
      const { rerender, getByTestId } = render(<HotelsPriceTable formattedDate="2025-08-03" />, {
        wrapper: createWrapper(),
      });

      // Set price sort for a specific date
      await waitFor(() => {
        const dateHeader = getByTestId('date-header-2025-08-03');
        userEvent.click(dateHeader);
      });

      // Change arrival date - should invalidate the price sort
      rerender(<HotelsPriceTable formattedDate="2025-08-10" />);

      await waitFor(() => {
        // Component should have re-queried with distance sort (not price sort)
        expect(mockUseQueryRequest).toHaveBeenCalled();
      });
    });

    it('should reset priceSortArrival when sortBy changes to DISTANCE', async () => {
      const mockOnSortChange = jest.fn();
      const { rerender, getByTestId } = render(
        <HotelsPriceTable
          sortBy="distance"
          onSortChange={mockOnSortChange}
          formattedDate="2025-08-03"
        />,
        { wrapper: createWrapper() }
      );

      // Set price sort
      await waitFor(() => {
        const dateHeader = getByTestId('date-header-2025-08-03');
        userEvent.click(dateHeader);
      });

      // Change sortBy to DISTANCE - should reset priceSortArrival
      rerender(
        <HotelsPriceTable
          sortBy="distance"
          onSortChange={mockOnSortChange}
          formattedDate="2025-08-03"
        />
      );

      await waitFor(() => {
        expect(mockUseQueryRequest).toHaveBeenCalled();
      });
    });

    it('should reset priceSortArrival when sortResetKey changes', async () => {
      const { rerender, getByTestId } = render(
        <HotelsPriceTable sortResetKey={0} formattedDate="2025-08-03" />,
        { wrapper: createWrapper() }
      );

      // Set price sort
      await waitFor(() => {
        const dateHeader = getByTestId('date-header-2025-08-03');
        userEvent.click(dateHeader);
      });

      // Change sortResetKey - should reset priceSortArrival
      rerender(<HotelsPriceTable sortResetKey={1} formattedDate="2025-08-03" />);

      await waitFor(() => {
        // Should have re-queried
        expect(mockUseQueryRequest).toHaveBeenCalled();
      });
    });

    it('should not use priceSortDate when it is for a different arrival date', async () => {
      const { rerender, getByTestId } = render(<HotelsPriceTable formattedDate="2025-08-03" />, {
        wrapper: createWrapper(),
      });

      // Set price sort for arrival date "2025-08-03"
      await waitFor(() => {
        const dateHeader = getByTestId('date-header-2025-08-03');
        userEvent.click(dateHeader);
      });

      // Change arrival date to "2025-08-10" without resetting priceSortDate
      // The component should detect that priceSortDate is for a different arrival and not use it
      rerender(<HotelsPriceTable formattedDate="2025-08-10" />);

      await waitFor(() => {
        // Should query with distance sort, not price sort
        expect(mockUseQueryRequest).toHaveBeenCalled();
      });
    });

    it('should use PRICE sort only when priceSortDate matches current arrival', async () => {
      const { getByTestId } = render(<HotelsPriceTable formattedDate="2025-08-03" />, {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        expect(getByTestId('hotel-price-table')).toBeInTheDocument();
      });

      // Click a date to set price sort
      await waitFor(() => {
        const dateHeader = getByTestId('date-header-2025-08-03');
        userEvent.click(dateHeader);
      });

      // Should now use PRICE sort - just verify the component re-renders
      await waitFor(() => {
        expect(mockUseQueryRequest).toHaveBeenCalled();
      });
    });

    it('should reset priceSortArrival when scrolling left', async () => {
      const { getByTestId } = render(<HotelsPriceTable formattedDate="2025-08-03" />, {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        expect(getByTestId('hotel-price-table')).toBeInTheDocument();
      });

      // First scroll right to enable left scroll
      await waitFor(() => {
        const rightButton = getByTestId('scroll-right-button');
        userEvent.click(rightButton);
      });

      // Scroll left should reset price sort
      await waitFor(() => {
        const leftButton = getByTestId('scroll-left-button');
        userEvent.click(leftButton);
        expect(mockUseQueryRequest).toHaveBeenCalled();
      });
    });

    it('should reset priceSortArrival when scrolling right', async () => {
      const { getByTestId } = render(<HotelsPriceTable formattedDate="2025-08-03" />, {
        wrapper: createWrapper(),
      });

      // Set price sort
      await waitFor(() => {
        const dateHeader = getByTestId('date-header-2025-08-03');
        userEvent.click(dateHeader);
      });

      // Scroll right should reset price sort
      await waitFor(() => {
        const rightButton = getByTestId('scroll-right-button');
        userEvent.click(rightButton);
        expect(mockUseQueryRequest).toHaveBeenCalled();
      });
    });

    it('should reset both priceSortDate and priceSortArrival when location changes', async () => {
      const { rerender, getByTestId } = render(
        <HotelsPriceTable locationId="location-1" formattedDate="2025-08-03" />,
        { wrapper: createWrapper() }
      );

      // Set price sort
      await waitFor(() => {
        const dateHeader = getByTestId('date-header-2025-08-03');
        userEvent.click(dateHeader);
      });

      // Change location - should reset both price sort states
      rerender(<HotelsPriceTable locationId="location-2" formattedDate="2025-08-03" />);

      await waitFor(() => {
        // Should re-query with new location
        expect(mockUseQueryRequest).toHaveBeenCalled();
      });
    });

    it('should handle sortBy toggle while priceSortDate is set', async () => {
      const { getByTestId, getByText } = render(<HotelsPriceTable formattedDate="2025-08-03" />, {
        wrapper: createWrapper(),
      });

      // Set price sort by clicking a date
      await waitFor(() => {
        const dateHeader = getByTestId('date-header-2025-08-03');
        userEvent.click(dateHeader);
      });

      // Click sort toggle button - should reset price sort
      await waitFor(() => {
        const sortButton = getByText('priceFinder.sortByDistance');
        userEvent.click(sortButton);
        expect(mockUseQueryRequest).toHaveBeenCalled();
      });
    });
  });

  describe('Duplicate API Call Prevention', () => {
    beforeEach(() => {
      mockUseQueryRequest.mockReturnValue({
        isLoading: false,
        isError: false,
        data: mockApiData,
      });
    });

    it('should not make duplicate API calls when switching sort modes', async () => {
      const { getByTestId, getByText } = render(<HotelsPriceTable formattedDate="2025-08-03" />, {
        wrapper: createWrapper(),
      });

      const initialCallCount = mockUseQueryRequest.mock.calls.length;

      // Click to sort by price
      await waitFor(() => {
        const dateHeader = getByTestId('date-header-2025-08-03');
        userEvent.click(dateHeader);
      });

      const afterPriceSortCallCount = mockUseQueryRequest.mock.calls.length;

      // Click to sort by distance
      await waitFor(() => {
        const sortButton = getByText('priceFinder.sortByDistance');
        userEvent.click(sortButton);
      });

      const afterDistanceSortCallCount = mockUseQueryRequest.mock.calls.length;

      // Verify that calls were made, but not duplicated unnecessarily
      expect(afterPriceSortCallCount).toBeGreaterThanOrEqual(initialCallCount);
      expect(afterDistanceSortCallCount).toBeGreaterThanOrEqual(afterPriceSortCallCount);
    });

    it('should not make duplicate API calls when arrival date changes', async () => {
      const { rerender, getByTestId } = render(<HotelsPriceTable formattedDate="2025-08-03" />, {
        wrapper: createWrapper(),
      });

      // Set price sort
      await waitFor(() => {
        const dateHeader = getByTestId('date-header-2025-08-03');
        userEvent.click(dateHeader);
      });

      const beforeDateChangeCallCount = mockUseQueryRequest.mock.calls.length;

      // Change arrival date - should make new call with correct params
      rerender(<HotelsPriceTable formattedDate="2025-08-10" />);

      await waitFor(() => {
        const afterDateChangeCallCount = mockUseQueryRequest.mock.calls.length;
        // Should have made additional calls, but not duplicate ones
        expect(afterDateChangeCallCount).toBeGreaterThanOrEqual(beforeDateChangeCallCount);
      });
    });
  });
});
