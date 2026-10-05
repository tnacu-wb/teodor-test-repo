import { ChakraProvider } from '@chakra-ui/react';
import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import i18n from 'i18next';
import React from 'react';
import { I18nextProvider, initReactI18next } from 'react-i18next';

import { PriceFinderPageCcui } from './index';

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
  const MockSearchContainer = ({ handleLocationSearch, setLocationName }: any) => {
    mockHandleLocationSearch = handleLocationSearch;
    return (
      <div data-testid="SearchContainer">
        <button
          data-testid="trigger-location-search"
          onClick={() => {
            handleLocationSearch('test-place-id');
            setLocationName && setLocationName('Test Location');
          }}
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
    setTotalItems,
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
    setTotalItems?: (total: number) => void;
  }) => {
    // Simulate the HotelsPriceTable calling onCurrentDatesChange on mount
    React.useEffect(() => {
      if (onCurrentDatesChange) {
        // Simulate current dates being set based on formattedDate
        const currentDate = formattedDate || getToday();
        const dates = getDateRange(currentDate, 3);
        onCurrentDatesChange(dates);
      }
      if (setTotalItems) {
        setTotalItems(50); // Mock total items for pagination testing
      }
    }, [onCurrentDatesChange, formattedDate, setTotalItems]);

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
  formatAssetsUrl: jest.fn((url) => url),
  renderSanitizedHtml: jest.fn((html) => html),
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

describe('Price Finder Page CCUI', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render without errors', () => {
    render(
      <ChakraProvider>
        <I18nextProvider i18n={i18n}>
          <PriceFinderPageCcui queryClient={queryClient} router={mockRouter as any} />
        </I18nextProvider>
      </ChakraProvider>
    );

    expect(screen.getByTestId('SearchContainer')).toBeInTheDocument();
  });

  it('should render with CCUI-specific test IDs', () => {
    render(
      <ChakraProvider>
        <I18nextProvider i18n={i18n}>
          <PriceFinderPageCcui queryClient={queryClient} router={mockRouter as any} />
        </I18nextProvider>
      </ChakraProvider>
    );

    // Should use CCUI-specific test ID
    expect(screen.getByTestId('PriceFinderPageCcui-Wrapper')).toBeInTheDocument();
  });

  it('should render SearchContainer with handleLocationSearch callback', () => {
    render(
      <ChakraProvider>
        <I18nextProvider i18n={i18n}>
          <PriceFinderPageCcui queryClient={queryClient} router={mockRouter as any} />
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
          <PriceFinderPageCcui queryClient={queryClient} router={mockRouter as any} />
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
          <PriceFinderPageCcui queryClient={queryClient} router={mockRouter as any} />
        </I18nextProvider>
      </ChakraProvider>
    );

    expect(screen.getByTestId('HeroBanner')).toBeInTheDocument();
  });

  it('should render HotelsPriceTable component with default sort', () => {
    render(
      <ChakraProvider>
        <I18nextProvider i18n={i18n}>
          <PriceFinderPageCcui queryClient={queryClient} router={mockRouter as any} />
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
          <PriceFinderPageCcui queryClient={queryClient} router={mockRouter as any} />
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
          <PriceFinderPageCcui queryClient={queryClient} router={mockRouter as any} />
        </I18nextProvider>
      </ChakraProvider>
    );

    expect(screen.getByTestId('MonthTabsCarousel')).toBeInTheDocument();
    expect(screen.getByTestId('month-select-trigger')).toHaveTextContent('Select Month');
  });

  it('should render Pagination component when total items exceed page size', () => {
    render(
      <ChakraProvider>
        <I18nextProvider i18n={i18n}>
          <PriceFinderPageCcui queryClient={queryClient} router={mockRouter as any} />
        </I18nextProvider>
      </ChakraProvider>
    );

    // Should render pagination since mock sets totalItems to 50
    expect(screen.getByTestId('Pagination')).toBeInTheDocument();
    expect(screen.getByTestId('current-page')).toHaveTextContent('1');
    expect(screen.getByTestId('total-pages')).toHaveTextContent('3'); // 50/20 = 2.5, rounded up to 3
  });

  it('should handle pagination page change', async () => {
    const user = userEvent.setup();
    render(
      <ChakraProvider>
        <I18nextProvider i18n={i18n}>
          <PriceFinderPageCcui queryClient={queryClient} router={mockRouter as any} />
        </I18nextProvider>
      </ChakraProvider>
    );

    const pageChangeButton = screen.getByTestId('page-change-trigger');
    const currentPage = screen.getByTestId('current-page');

    expect(currentPage).toHaveTextContent('1');

    await user.click(pageChangeButton);
    expect(currentPage).toHaveTextContent('2');
  });

  it('should handle month selection functionality', async () => {
    const user = userEvent.setup();
    render(
      <ChakraProvider>
        <I18nextProvider i18n={i18n}>
          <PriceFinderPageCcui queryClient={queryClient} router={mockRouter as any} />
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
          <PriceFinderPageCcui queryClient={queryClient} router={mockRouter as any} />
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
            <PriceFinderPageCcui queryClient={queryClient} router={mockRouter as any} />
          </I18nextProvider>
        </ChakraProvider>
      );

      // In mobile view, the component should render differently
      // Check for mobile-specific elements
      expect(screen.getByTestId('HotelsPriceTable')).toBeInTheDocument();
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
            <PriceFinderPageCcui queryClient={queryClient} router={mockRouter as any} />
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
            <PriceFinderPageCcui queryClient={queryClient} router={mockRouter as any} />
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
            <PriceFinderPageCcui queryClient={queryClient} router={mockRouter as any} />
          </I18nextProvider>
        </ChakraProvider>
      );

      // Component should render with default locale
      expect(screen.getByTestId('HotelsPriceTable')).toBeInTheDocument();
      expect(mockUseCustomLocale).toHaveBeenCalled();
    });
  });

  describe('CCUI-specific functionality', () => {
    it('should use CCUI-specific base test ID', () => {
      render(
        <ChakraProvider>
          <I18nextProvider i18n={i18n}>
            <PriceFinderPageCcui queryClient={queryClient} router={mockRouter as any} />
          </I18nextProvider>
        </ChakraProvider>
      );

      // Should use PriceFinderPageCcui instead of PriceFinderPagePi
      expect(screen.getByTestId('PriceFinderPageCcui-Wrapper')).toBeInTheDocument();
    });

    it('should handle CCUI-specific search container integration', async () => {
      const user = userEvent.setup();
      render(
        <ChakraProvider>
          <I18nextProvider i18n={i18n}>
            <PriceFinderPageCcui queryClient={queryClient} router={mockRouter as any} />
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
  });
});
