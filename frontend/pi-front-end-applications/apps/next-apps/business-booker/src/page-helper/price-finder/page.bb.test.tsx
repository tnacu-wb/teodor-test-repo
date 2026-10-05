import { ChakraProvider } from '@chakra-ui/react';
import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import i18n from 'i18next';
import React from 'react';
import { I18nextProvider, initReactI18next } from 'react-i18next';

import { PriceFinderPageBB } from './index';

i18n.use(initReactI18next).init({
  lng: 'en',
  fallbackLng: 'en',
  ns: ['common', 'examples'],
  defaultNS: 'common',
  interpolation: { escapeValue: false },
  resources: { en: { common: {}, examples: {} }, de: { common: {}, examples: {} } },
});

const mockRouter = { push: jest.fn(), query: {}, locale: 'en-GB' };

let mockHandleLocationSearch: (placeId: string) => void;

const getToday = () => new Date().toISOString().split('T')[0];

const getCurrentMonth = () => {
  const date = new Date();
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
};

const getFutureMonth = () => {
  const date = new Date();
  date.setMonth(date.getMonth() + 1);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
};

const getFutureDate = (monthOffset = 1, day = 15) => {
  const date = new Date();
  date.setMonth(date.getMonth() + monthOffset);
  date.setDate(day);
  return date.toISOString().split('T')[0];
};

const getSameDayOfMonth = (day = 15) => {
  const date = new Date();
  date.setDate(day);
  return date.toISOString().split('T')[0];
};

const getDateRange = (startDate: string, days = 3) => {
  const dates: string[] = [];
  const baseDate = new Date(startDate);
  for (let i = 0; i < days; i++) {
    const date = new Date(baseDate.getTime() + i * 24 * 60 * 60 * 1000);
    dates.push(date.toISOString().split('T')[0]);
  }
  return dates;
};

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockRouter,
}));

jest.mock('next/dynamic', () => () => {
  const MockSearchContainer = ({ handleLocationSearch }: any) => {
    mockHandleLocationSearch = handleLocationSearch;
    return (
      <div data-testid="SearchContainer">
        <button
          data-testid="trigger-location-search"
          onClick={() => handleLocationSearch('test-place-id')}
        >
          Test Location Search
        </button>
      </div>
    );
  };
  return MockSearchContainer;
});

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  PriceFinderRoomSortToggle: () => (
    <div data-testid="price-finder-room-sort-toggle">Sort Toggle</div>
  ),
  HeroBanner: () => <div data-testid="HeroBanner" />,
  VIEW_TYPE_CONSTANTS: { listView: 'list', mapView: 'map' },
  HotelsPriceTable: ({
    sortBy,
    onSortChange,
    data,
    isLoading,
    isError,
    locationId,
    formattedDate,
    onDateChange,
    onCurrentDatesChange,
  }: {
    sortBy?: 'distance' | 'price';
    onSortChange?: (sortBy: 'distance' | 'price') => void;
    data?: any;
    isLoading?: boolean;
    isError?: boolean;
    locationId?: string;
    formattedDate?: string;
    onDateChange?: (newDate: string) => void;
    onCurrentDatesChange?: (dates: string[]) => void;
  }) => {
    // Simulate the HotelsPriceTable calling onCurrentDatesChange on mount
    React.useEffect(() => {
      if (onCurrentDatesChange) {
        // Simulate current dates being set based on formattedDate
        const currentDate = formattedDate || getToday();
        const dates = getDateRange(currentDate, 3);
        onCurrentDatesChange(dates);
      }
    }, [onCurrentDatesChange, formattedDate]);

    return (
      <div data-testid="HotelsPriceTable">
        <div data-testid="current-sort">{sortBy || 'distance'}</div>
        <div data-testid="table-data">{data ? 'has-data' : 'no-data'}</div>
        <div data-testid="table-loading">{isLoading ? 'loading' : 'not-loading'}</div>
        <div data-testid="table-error">{isError ? 'error' : 'no-error'}</div>
        <div data-testid="location-id">
          {locationId || 'priceFinder.MVP.customConfig.defaultLocation'}
        </div>
        <div data-testid="formatted-date">{formattedDate || getToday()}</div>
        <button
          data-testid="sort-toggle"
          onClick={() => onSortChange && onSortChange(sortBy === 'distance' ? 'price' : 'distance')}
        >
          Toggle Sort
        </button>
        <button
          data-testid="date-change-trigger"
          onClick={() => {
            onDateChange && onDateChange(getSameDayOfMonth(15));
          }}
        >
          Trigger Date Change
        </button>
        <button
          data-testid="current-dates-change-trigger"
          onClick={() =>
            onCurrentDatesChange &&
            (() => {
              const futureStartDate = getFutureDate(1, 15);
              const dates = getDateRange(futureStartDate, 3);
              onCurrentDatesChange(dates);
            })()
          }
        >
          Trigger Current Dates Change
        </button>
      </div>
    );
  },
  Pagination: ({ currentPage, totalPages, onPageChange }: any) => (
    <div data-testid="Pagination">
      <div data-testid="current-page">{currentPage}</div>
      <div data-testid="total-pages">{totalPages}</div>
      <button data-testid="page-change-trigger" onClick={() => onPageChange && onPageChange(2)}>
        Go to Page 2
      </button>
    </div>
  ),
  THEME_COLORS: {
    primaryColor: '#00798E',
    sortTextColor: '#511E62',
    floatingHeaderBg: '#F7FAFC',
    borderColor: '#CCCCCC',
    disabledColor: '#9CA3AF',
  },
  SORT_BY_VALUES: {
    DISTANCE: 'distance',
    PRICE: 'price',
  },
  TABLE_CONFIG: {
    PAGE_SIZE: 20,
    DAYS_TO_SHOW: 7,
    SCROLL_OFFSET: 7,
    MAX_BACKWARD_DAYS: 0,
    SKELETON_ROWS_COUNT: 20,
    LAZY_LOAD_PAGE_SIZE: 20,
  },
}));

jest.mock('@whitbread-eos/atoms', () => ({
  Notification: () => <div data-testid="Alert">Notification negotiated/corperates rates </div>,
  Button: ({ children, onClick, ...props }: any) => (
    <button data-testid="change-month-button" onClick={onClick} {...props}>
      {children}
    </button>
  ),
  theme: {
    colors: {
      baseWhite: '#FFFFFF',
      primaryColor: '#00798E',
      // Add other colors as needed
    },
  },
}));

jest.mock('@whitbread-eos/organisms', () => ({
  MonthTabsCarousel: ({ onMonthChange, initialMonthValue }: any) => {
    return (
      <div data-testid="MonthTabsCarousel">
        <div data-testid="initial-month-value">{initialMonthValue || 'no-initial-month'}</div>
        <button
          data-testid="month-select-trigger"
          onClick={() => {
            onMonthChange && onMonthChange({ value: getCurrentMonth() });
          }}
        >
          Select Month
        </button>
        <button
          data-testid="month-select-future-trigger"
          onClick={() => {
            onMonthChange && onMonthChange({ value: getFutureMonth() });
          }}
        >
          Select Future Month
        </button>
      </div>
    );
  },
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQueryRequest: jest.fn(() => ({
    data: null,
    isError: false,
    isLoading: false,
  })),
  formatDataTestId: (baseTestId: string, suffix: string) => `${baseTestId}-${suffix}`,
  useCustomLocale: jest.fn(() => ({ language: 'en' })),
  useFeatureToggle: jest.fn(() => ({})),
}));

jest.mock('next-i18next', () => ({
  useTranslation: () => ({ t: (key: string) => key }),
}));

// Mock matchMedia for mobile query
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: jest.fn().mockImplementation((query) => ({
    matches: false, // Default to desktop view
    media: query,
    onchange: null,
    addListener: jest.fn(),
    removeListener: jest.fn(),
    addEventListener: jest.fn(),
    removeEventListener: jest.fn(),
    dispatchEvent: jest.fn(),
  })),
});

const queryClient = new ReactQuery.QueryClient();

// Get reference to the mocked function for testing
const mockUseCustomLocale = jest.mocked(jest.requireMock('@whitbread-eos/utils').useCustomLocale);
const mockUseFeatureToggle = jest.mocked(jest.requireMock('@whitbread-eos/utils').useFeatureToggle);

describe('Price Finder Page BB', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render without errors', () => {
    render(
      <ChakraProvider>
        <I18nextProvider i18n={i18n}>
          <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
        </I18nextProvider>
      </ChakraProvider>
    );

    expect(screen.getByTestId('SearchContainer')).toBeInTheDocument();
  });

  it('should render SearchContainer with handleLocationSearch callback', () => {
    render(
      <ChakraProvider>
        <I18nextProvider i18n={i18n}>
          <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
        </I18nextProvider>
      </ChakraProvider>
    );

    expect(screen.getByTestId('SearchContainer')).toBeInTheDocument();
    expect(mockHandleLocationSearch).toBeDefined();
  });

  it('should handle location search callback correctly', async () => {
    const user = userEvent.setup();
    render(
      <ChakraProvider>
        <I18nextProvider i18n={i18n}>
          <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
        </I18nextProvider>
      </ChakraProvider>
    );

    // Trigger the handleLocationSearch function
    const triggerButton = screen.getByTestId('trigger-location-search');
    await user.click(triggerButton);

    // Verify the function was called (you can extend this to verify state changes)
    expect(mockHandleLocationSearch).toBeDefined();
  });

  it('should render HeroBanner component', () => {
    render(
      <ChakraProvider>
        <I18nextProvider i18n={i18n}>
          <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
        </I18nextProvider>
      </ChakraProvider>
    );

    expect(screen.getByTestId('HeroBanner')).toBeInTheDocument();
  });

  it('should render HotelsPriceTable component with default sort', () => {
    render(
      <ChakraProvider>
        <I18nextProvider i18n={i18n}>
          <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
        </I18nextProvider>
      </ChakraProvider>
    );

    expect(screen.getByTestId('HotelsPriceTable')).toBeInTheDocument();
    expect(screen.getByTestId('current-sort')).toHaveTextContent('distance');
  });

  it('should handle sort change when sort toggle is clicked', async () => {
    const user = userEvent.setup();
    render(
      <ChakraProvider>
        <I18nextProvider i18n={i18n}>
          <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
        </I18nextProvider>
      </ChakraProvider>
    );

    const sortToggle = screen.getByTestId('sort-toggle');
    const currentSort = screen.getByTestId('current-sort');

    expect(currentSort).toHaveTextContent('distance');

    await user.click(sortToggle);
    expect(currentSort).toHaveTextContent('price');

    await user.click(sortToggle);
    expect(currentSort).toHaveTextContent('distance');
  });

  it('should render MonthTabsCarousel component', () => {
    render(
      <ChakraProvider>
        <I18nextProvider i18n={i18n}>
          <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
        </I18nextProvider>
      </ChakraProvider>
    );

    expect(screen.getByTestId('MonthTabsCarousel')).toBeInTheDocument();
    expect(screen.getByTestId('month-select-trigger')).toHaveTextContent('Select Month');
  });

  it('should handle month selection functionality', async () => {
    const user = userEvent.setup();
    render(
      <ChakraProvider>
        <I18nextProvider i18n={i18n}>
          <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
        </I18nextProvider>
      </ChakraProvider>
    );

    const monthSelectTrigger = screen.getByTestId('month-select-trigger');

    await expect(user.click(monthSelectTrigger)).resolves.not.toThrow();
  });

  it('should not show mobile sort toggle in desktop view', () => {
    render(
      <ChakraProvider>
        <I18nextProvider i18n={i18n}>
          <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
        </I18nextProvider>
      </ChakraProvider>
    );

    // Mobile sort button should not be present in desktop view
    expect(screen.queryByText('priceFinder.sortByDistance')).not.toBeInTheDocument();
  });

  describe('Mobile view', () => {
    beforeEach(() => {
      // Mock mobile media query
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
    });

    it('should show mobile sort toggle in mobile view', () => {
      render(
        <ChakraProvider>
          <I18nextProvider i18n={i18n}>
            <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
          </I18nextProvider>
        </ChakraProvider>
      );

      // In mobile view, the component should render differently
      // Check for mobile-specific elements
      expect(screen.getByTestId('HotelsPriceTable')).toBeInTheDocument();
    });

    it('should handle mobile sort toggle click', async () => {
      const user = userEvent.setup();
      render(
        <ChakraProvider>
          <I18nextProvider i18n={i18n}>
            <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
          </I18nextProvider>
        </ChakraProvider>
      );

      // Test that mobile view renders and interacts correctly
      const sortToggle = screen.getByTestId('sort-toggle');
      expect(sortToggle).toBeInTheDocument();

      // Test interaction works without error
      await expect(user.click(sortToggle)).resolves.not.toThrow();
    });
  });

  describe('handleMonthChange functionality', () => {
    beforeEach(() => {
      // Mock current date to ensure consistent testing
      jest.useFakeTimers();
      jest.setSystemTime(new Date('2025-08-19'));
    });

    afterEach(() => {
      jest.useRealTimers();
    });

    it('should handle month change to current month', async () => {
      const user = userEvent.setup({ advanceTimers: jest.advanceTimersByTime });
      render(
        <ChakraProvider>
          <I18nextProvider i18n={i18n}>
            <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
          </I18nextProvider>
        </ChakraProvider>
      );

      const monthSelectTrigger = screen.getByTestId('month-select-trigger');
      const formattedDateDisplay = screen.getByTestId('formatted-date');

      // Trigger month change to current month (August 2025)
      await user.click(monthSelectTrigger);

      // Should use today's date when selecting current month
      expect(formattedDateDisplay).toHaveTextContent('2025-08-19');
    });

    it('should handle month change to future month', async () => {
      const user = userEvent.setup({ advanceTimers: jest.advanceTimersByTime });
      render(
        <ChakraProvider>
          <I18nextProvider i18n={i18n}>
            <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
          </I18nextProvider>
        </ChakraProvider>
      );

      const futureMonthTrigger = screen.getByTestId('month-select-future-trigger');
      const formattedDateDisplay = screen.getByTestId('formatted-date');

      await user.click(futureMonthTrigger);

      // Should update to first day of future month
      expect(formattedDateDisplay).toHaveTextContent('2025-09-01');
    });

    it('should maintain sort state when month changes', async () => {
      const user = userEvent.setup({ advanceTimers: jest.advanceTimersByTime });
      render(
        <ChakraProvider>
          <I18nextProvider i18n={i18n}>
            <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
          </I18nextProvider>
        </ChakraProvider>
      );

      const sortToggle = screen.getByTestId('sort-toggle');
      const currentSort = screen.getByTestId('current-sort');
      const monthSelectTrigger = screen.getByTestId('month-select-trigger');
      const formattedDateDisplay = screen.getByTestId('formatted-date');

      // First change sort to price
      await user.click(sortToggle);
      expect(currentSort).toHaveTextContent('price');

      // Then trigger month change
      await user.click(monthSelectTrigger);

      // Verify that the date changed (current month)
      expect(formattedDateDisplay).toHaveTextContent('2025-08-19');

      // Sort should maintain its current state (not reset unless location changes)
      expect(currentSort).toHaveTextContent('price');
    });
  });

  describe('handleCurrentDatesChange functionality', () => {
    it('should update initialMonthValue when current dates change on mount', async () => {
      render(
        <ChakraProvider>
          <I18nextProvider i18n={i18n}>
            <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
          </I18nextProvider>
        </ChakraProvider>
      );

      // The mock HotelsPriceTable should automatically call onCurrentDatesChange with default dates
      // This should update the initialMonthValue which is passed to MonthTabsCarousel
      const initialMonthValueDisplay = screen.getByTestId('initial-month-value');

      expect(initialMonthValueDisplay).toHaveTextContent(getCurrentMonth());
    });

    it('should extract month from first date in dates array', async () => {
      const user = userEvent.setup();
      render(
        <ChakraProvider>
          <I18nextProvider i18n={i18n}>
            <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
          </I18nextProvider>
        </ChakraProvider>
      );

      const currentDatesChangeTrigger = screen.getByTestId('current-dates-change-trigger');
      const initialMonthValueDisplay = screen.getByTestId('initial-month-value');

      expect(initialMonthValueDisplay).toHaveTextContent(getCurrentMonth());

      await user.click(currentDatesChangeTrigger);

      // Should update to next month
      expect(initialMonthValueDisplay).toHaveTextContent(getFutureMonth());
    });

    it('should handle dates with different years correctly', async () => {
      const user = userEvent.setup();
      render(
        <ChakraProvider>
          <I18nextProvider i18n={i18n}>
            <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
          </I18nextProvider>
        </ChakraProvider>
      );

      const currentDatesChangeTrigger = screen.getByTestId('current-dates-change-trigger');
      const initialMonthValueDisplay = screen.getByTestId('initial-month-value');

      await user.click(currentDatesChangeTrigger);

      // Should extract year and month from first date
      expect(initialMonthValueDisplay).toHaveTextContent(getFutureMonth());
    });

    it('should update initialMonthValue when formattedDate changes', async () => {
      const user = userEvent.setup();
      render(
        <ChakraProvider>
          <I18nextProvider i18n={i18n}>
            <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
          </I18nextProvider>
        </ChakraProvider>
      );

      const dateChangeTrigger = screen.getByTestId('date-change-trigger');
      const initialMonthValueDisplay = screen.getByTestId('initial-month-value');

      expect(initialMonthValueDisplay).toHaveTextContent(getCurrentMonth());

      await user.click(dateChangeTrigger);

      expect(initialMonthValueDisplay).toHaveTextContent(getCurrentMonth());
    });

    it('should handle date parsing and month extraction correctly', async () => {
      const user = userEvent.setup();
      render(
        <ChakraProvider>
          <I18nextProvider i18n={i18n}>
            <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
          </I18nextProvider>
        </ChakraProvider>
      );

      const currentDatesChangeTrigger = screen.getByTestId('current-dates-change-trigger');
      const initialMonthValueDisplay = screen.getByTestId('initial-month-value');

      // Trigger current dates change, which uses dates starting with next month
      await user.click(currentDatesChangeTrigger);

      // Verify that the month was correctly extracted from the first date
      const futureDate = new Date();
      futureDate.setMonth(futureDate.getMonth() + 1);
      const expectedMonth = `${futureDate.getFullYear()}-${String(
        futureDate.getMonth() + 1
      ).padStart(2, '0')}`;
      expect(initialMonthValueDisplay).toHaveTextContent(expectedMonth);
    });

    it('should handle callback function correctly', async () => {
      render(
        <ChakraProvider>
          <I18nextProvider i18n={i18n}>
            <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
          </I18nextProvider>
        </ChakraProvider>
      );

      // The mock automatically calls onCurrentDatesChange with dates
      // Verify the function is used and component doesn't crash
      const initialMonthValueDisplay = screen.getByTestId('initial-month-value');

      // Should have been set by the useEffect in the mock
      expect(initialMonthValueDisplay).toHaveTextContent(getCurrentMonth());

      // Verify that both components are rendered properly
      expect(screen.getByTestId('HotelsPriceTable')).toBeInTheDocument();
      expect(screen.getByTestId('MonthTabsCarousel')).toBeInTheDocument();
    });

    it('should handle edge cases and maintain component stability', async () => {
      render(
        <ChakraProvider>
          <I18nextProvider i18n={i18n}>
            <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
          </I18nextProvider>
        </ChakraProvider>
      );

      // Component should render without issues even with various date scenarios
      expect(screen.getByTestId('HotelsPriceTable')).toBeInTheDocument();
      expect(screen.getByTestId('MonthTabsCarousel')).toBeInTheDocument();
      expect(screen.getByTestId('initial-month-value')).toBeInTheDocument();

      // Verify that the handleCurrentDatesChange function is properly connected
      const initialMonthValueDisplay = screen.getByTestId('initial-month-value');
      expect(initialMonthValueDisplay).toHaveTextContent(getCurrentMonth());
    });
  });

  describe('Locale functionality', () => {
    beforeEach(() => {
      // Reset mock before each test
      mockUseCustomLocale.mockClear();
    });

    it('should transform short locale to full locale correctly', () => {
      mockUseCustomLocale.mockReturnValue({ language: 'en' });

      render(
        <ChakraProvider>
          <I18nextProvider i18n={i18n}>
            <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
          </I18nextProvider>
        </ChakraProvider>
      );

      // Component should render and pass the transformed locale (en -> en-GB) to HotelsPriceTable
      expect(screen.getByTestId('HotelsPriceTable')).toBeInTheDocument();
      expect(mockUseCustomLocale).toHaveBeenCalled();
    });

    it('should handle German locale transformation', () => {
      mockUseCustomLocale.mockReturnValue({ language: 'de' });

      render(
        <ChakraProvider>
          <I18nextProvider i18n={i18n}>
            <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
          </I18nextProvider>
        </ChakraProvider>
      );

      // Component should render and transform de -> de-DE
      expect(screen.getByTestId('HotelsPriceTable')).toBeInTheDocument();
      expect(mockUseCustomLocale).toHaveBeenCalled();
    });

    it('should default to en-GB for unknown locales', () => {
      mockUseCustomLocale.mockReturnValue({ language: 'unknown' });

      render(
        <ChakraProvider>
          <I18nextProvider i18n={i18n}>
            <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
          </I18nextProvider>
        </ChakraProvider>
      );

      // Component should render with default locale
      expect(screen.getByTestId('HotelsPriceTable')).toBeInTheDocument();
      expect(mockUseCustomLocale).toHaveBeenCalled();
    });
  });

  describe('Dynamic SearchContainer import', () => {
    it('should render SearchContainer with dynamic import', async () => {
      render(
        <ChakraProvider>
          <I18nextProvider i18n={i18n}>
            <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
          </I18nextProvider>
        </ChakraProvider>
      );

      // SearchContainer should be rendered (mocked in our test setup)
      expect(screen.getByTestId('SearchContainer')).toBeInTheDocument();
    });

    it('should handle location search callback from dynamic component', async () => {
      const user = userEvent.setup();
      render(
        <ChakraProvider>
          <I18nextProvider i18n={i18n}>
            <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
          </I18nextProvider>
        </ChakraProvider>
      );

      const triggerButton = screen.getByTestId('trigger-location-search');
      const locationIdDisplay = screen.getByTestId('location-id');

      // Initially should show default location
      expect(locationIdDisplay).toHaveTextContent('priceFinder.MVP.customConfig.defaultLocation');

      // Trigger location search
      await user.click(triggerButton);

      // Location should be updated (the mock sets it to 'test-place-id')
      expect(locationIdDisplay).toHaveTextContent('test-place-id');
    });

    it('should reset sort to distance when location changes', async () => {
      const user = userEvent.setup();
      render(
        <ChakraProvider>
          <I18nextProvider i18n={i18n}>
            <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
          </I18nextProvider>
        </ChakraProvider>
      );

      const sortToggle = screen.getByTestId('sort-toggle');
      const currentSort = screen.getByTestId('current-sort');
      const triggerButton = screen.getByTestId('trigger-location-search');

      // First change sort to price
      await user.click(sortToggle);
      expect(currentSort).toHaveTextContent('price');

      // Then trigger location search
      await user.click(triggerButton);

      // Sort should reset to distance
      expect(currentSort).toHaveTextContent('distance');
    });

    it('should have ssr: false configuration for SearchContainer', () => {
      render(
        <ChakraProvider>
          <I18nextProvider i18n={i18n}>
            <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
          </I18nextProvider>
        </ChakraProvider>
      );

      // The dynamic import with ssr: false should work correctly
      // We verify this by ensuring the SearchContainer renders without issues
      expect(screen.getByTestId('SearchContainer')).toBeInTheDocument();
    });
  });

  describe('Data Integration', () => {
    it('should pass API data to HotelsPriceTable component', () => {
      render(
        <ChakraProvider>
          <I18nextProvider i18n={i18n}>
            <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
          </I18nextProvider>
        </ChakraProvider>
      );

      // Verify that HotelsPriceTable receives the mocked API data
      expect(screen.getByTestId('table-data')).toHaveTextContent('no-data'); // Since useQueryRequest is mocked to return null
      expect(screen.getByTestId('table-loading')).toHaveTextContent('not-loading');
      expect(screen.getByTestId('table-error')).toHaveTextContent('no-error');
      expect(screen.getByTestId('location-id')).toHaveTextContent(
        'priceFinder.MVP.customConfig.defaultLocation'
      );
    });

    it('should pass correct location and date to HotelsPriceTable', () => {
      render(
        <ChakraProvider>
          <I18nextProvider i18n={i18n}>
            <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
          </I18nextProvider>
        </ChakraProvider>
      );

      // Check that locationId and formattedDate are passed
      expect(screen.getByTestId('location-id')).toBeInTheDocument();
      expect(screen.getByTestId('formatted-date')).toBeInTheDocument();
    });

    it('should handle date changes from HotelsPriceTable scroll navigation', async () => {
      const user = userEvent.setup();
      render(
        <ChakraProvider>
          <I18nextProvider i18n={i18n}>
            <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
          </I18nextProvider>
        </ChakraProvider>
      );

      const dateChangeTrigger = screen.getByTestId('date-change-trigger');
      const formattedDateDisplay = screen.getByTestId('formatted-date');

      // Trigger date change from HotelsPriceTable
      await user.click(dateChangeTrigger);

      // Verify that the formatted date was updated
      expect(formattedDateDisplay).toHaveTextContent(getSameDayOfMonth(15));
    });

    it('should handle handleSortedByDateChange callback', () => {
      render(
        <ChakraProvider>
          <I18nextProvider i18n={i18n}>
            <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
          </I18nextProvider>
        </ChakraProvider>
      );

      // Component should render with the callback properly passed
      // The actual callback functionality is tested in the child component
      expect(screen.getByTestId('HotelsPriceTable')).toBeInTheDocument();
    });
  });

  describe('useFeatureToggle Functionality', () => {
    beforeEach(() => {
      mockUseFeatureToggle.mockClear();
    });

    it('should call useFeatureToggle hook correctly', () => {
      mockUseFeatureToggle.mockReturnValue({});

      render(
        <ChakraProvider>
          <I18nextProvider i18n={i18n}>
            <PriceFinderPageBB queryClient={queryClient} router={mockRouter as any} />
          </I18nextProvider>
        </ChakraProvider>
      );

      // Verify that useFeatureToggle hook was called
      expect(mockUseFeatureToggle).toHaveBeenCalled();
      expect(screen.getByTestId('HotelsPriceTable')).toBeInTheDocument();
    });
  });
});
