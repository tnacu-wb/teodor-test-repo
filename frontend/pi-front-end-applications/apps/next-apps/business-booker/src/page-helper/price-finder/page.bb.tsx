import { Box, FlexProps, type GridItemProps } from '@chakra-ui/react';
import { useMediaQuery } from '@chakra-ui/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import {
  PI_FAVICON,
  FT_PI_PRICE_FINDER_SEO_NOINDEX,
  FT_PI_PIB_CCUI_SHOW_LOWEST_PRICE_MONTH_TAB,
  FT_PI_PIB_CCUI_SHOW_FILTER_ROOM_TYPES,
  AnalyticsData,
  SITE_BB,
  PageName,
} from '@whitbread-eos/api';
import { Info, Notification } from '@whitbread-eos/atoms';
import {
  HeroBanner,
  HotelsPriceTable,
  SORT_BY_VALUES,
  Pagination,
  THEME_COLORS,
  TABLE_CONFIG,
  PriceFinderRoomTypeFilter,
  PriceFinderRoomSortToggle,
} from '@whitbread-eos/molecules';
import type { SupportedLocales, SortBy } from '@whitbread-eos/molecules/src/price-finder';
import type { HotelsPriceTableProps } from '@whitbread-eos/molecules/src/price-finder/HotelsPriceTable/HotelsPriceTable.component';
import { LowestRate } from '@whitbread-eos/molecules/src/price-finder/HotelsPriceTable/types';
import { MonthTabsCarousel, type MonthData } from '@whitbread-eos/organisms';
import { getValidOrTodayDate } from '@whitbread-eos/utils';
import {
  useCustomLocale,
  useFeatureToggle,
  formatAssetsUrl,
  renderSanitizedHtml,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import dynamic from 'next/dynamic';
import Head from 'next/head';
import type { NextRouter } from 'next/router';
import { useState, useCallback, useEffect } from 'react';

declare global {
  interface Window {
    analyticsData: AnalyticsData;
    // satellite required for adobe analytics on the confirmation Page
    __satelliteLoaded: boolean;
    _satellite: any;
  }
}

// Helper function to transform short locale to full locale
const transformLocale = (shortLocale: string): SupportedLocales => {
  switch (shortLocale) {
    case 'en':
      return 'en-GB';
    case 'de':
      return 'de-DE';
    default:
      return 'en-GB'; // default fallback
  }
};

interface Props {
  queryClient: QueryClient;
  router: NextRouter;
}

const SearchContainer = dynamic(
  async () => {
    const { PISearchContainer } = await import('@whitbread-eos/organisms');
    return { default: PISearchContainer };
  },
  {
    ssr: false,
  }
);

export function BBPageContent({ queryClient, router }: Readonly<Props>) {
  const { PLACEID, ARRdd, ARRmm, ARRyyyy } = router.query;
  const { t } = useTranslation();
  const baseDataTestId = 'PriceFinderPageBB';
  const defaultLocation = t('priceFinder.MVP.customConfig.defaultLocation');
  const today = new Date().toISOString().split('T')[0];
  const validDate = getValidOrTodayDate(ARRdd, ARRmm, ARRyyyy, today);
  const [formattedDate, setFormattedDate] = useState(validDate);
  const [locationId, setLocationId] = useState<string | null>(
    (PLACEID as string) || defaultLocation
  );
  const [sortBy, setSortBy] = useState<SortBy>(SORT_BY_VALUES.DISTANCE as SortBy);
  const [isMobile] = useMediaQuery('(max-width: 767px)');
  const { language: shortLocale } = useCustomLocale();
  const locale = transformLocale(shortLocale);
  const [initialMonthValue, setInitialMonthValue] = useState<string | undefined>(undefined);
  const [sortedByDate, setSortedByDate] = useState<string | null>(null);
  const [lowestPrice, setLowestPrice] = useState<LowestRate | undefined>(undefined);
  const [isLoading, setIsLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalItems, setTotalItems] = useState(0);
  const [isPaginating, setIsPaginating] = useState(false);
  const [sortResetKey, setSortResetKey] = useState(0);
  const [locationName, setLocationName] = useState<string | null>(null);
  const [filterSelection, setFilterSelection] = useState<Record<string, boolean>>({
    DB: false,
    TWIN: false,
    FAM: false,
    DIS: false,
  });
  const [toggleFilterMenu, setToggleFilterMenu] = useState(false);

  const fullMonthParams = new Date(formattedDate).toLocaleString('default', {
    month: 'long',
    year: 'numeric',
  });

  const monthsCovered = (startDate = new Date()) => {
    const start = new Date(startDate);
    const end = new Date(start);
    end.setDate(end.getDate() + 364);

    const months = new Set();
    const current = new Date(start);
    current.setDate(1);

    while (current <= end) {
      months.add(current.getMonth() + '-' + current.getFullYear());
      current.setMonth(current.getMonth() + 1);
    }

    return months.size;
  };

  const {
    [FT_PI_PRICE_FINDER_SEO_NOINDEX]: isPriceFinderNoIndexEnabled,
    [FT_PI_PIB_CCUI_SHOW_LOWEST_PRICE_MONTH_TAB]: isLowestPriceInMonthTabEnabled,
    [FT_PI_PIB_CCUI_SHOW_FILTER_ROOM_TYPES]: isRoomTypeFilterEnabled,
  } = useFeatureToggle();

  const handleLocationSearch = (placeId: string) => {
    setLocationId(placeId);
    // Reset sorting to distance when location changes
    setSortBy(SORT_BY_VALUES.DISTANCE as SortBy);
    // Reset to page 1 when location changes
    setCurrentPage(1);
  };

  const handleMonthChange = (month: MonthData) => {
    const [year, monthStr] = month.value.split('-');
    const selectedYear = parseInt(year, 10);
    const selectedMonthNum = parseInt(monthStr, 10);

    const currentDate = new Date();
    const currentYear = currentDate.getFullYear();
    const currentMonth = currentDate.getMonth() + 1;

    let newFormattedDate: string;

    // Check if the selected month is the current month and year
    if (selectedMonthNum === currentMonth && selectedYear === currentYear) {
      newFormattedDate = currentDate.toISOString().split('T')[0];
    } else {
      newFormattedDate = `${selectedYear}-${monthStr}-01`;
    }

    // Update the date which will trigger a new API call
    handleDateChange(newFormattedDate);
  };

  const handleSortChange = (newSortBy: SortBy) => {
    setSortBy(newSortBy);
    setCurrentPage(1);
    setSortedByDate(null);
    setSortResetKey((prev) => prev + 1);
  };

  const handlePageChange = (page: number) => {
    if (page !== currentPage && !isPaginating) {
      setIsPaginating(true);
      setCurrentPage(page);
      window.__satelliteLoaded && window._satellite.track('priceFinderHotelSelected');
    }
  };

  const handlePriceSortChange = () => {
    // Reset to page 1 when price sorting changes
    setCurrentPage(1);
  };

  const handleDateChange = useCallback((newDate: string) => {
    setFormattedDate(newDate);
    // Reset to page 1 when date changes
    setCurrentPage(1);
  }, []);

  const handleToggleFilterMenu = () => {
    setToggleFilterMenu(!toggleFilterMenu);
  };

  const handleCurrentDatesChange = useCallback((dates: string[]) => {
    if (dates.length > 0) {
      const firstDate = new Date(dates[0]);
      const monthValue = `${firstDate.getFullYear()}-${String(firstDate.getMonth() + 1).padStart(
        2,
        '0'
      )}`;
      setInitialMonthValue(monthValue);
    }
  }, []);

  const handleSortedByDateChange = useCallback((dateValue: string | null) => {
    setSortedByDate(dateValue);
  }, []);

  useEffect(() => {
    const currentDate = new Date();
    const currentMonthValue = `${currentDate.getFullYear()}-${String(
      currentDate.getMonth() + 1
    ).padStart(2, '0')}`;
    setInitialMonthValue(formattedDate.slice(0, 7) || currentMonthValue);
  }, []);

  return (
    <>
      {isPriceFinderNoIndexEnabled ? (
        <Head>
          <title>{t('priceFinder.MVP.customHeroTitle')}</title>
          <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no" />
          <meta name="robots" content="noindex,nofollow" />
          <meta name="googlebot" content="noindex,nofollow" />
          <link rel="icon" type="image/x-icon" href={formatAssetsUrl(PI_FAVICON)} />
        </Head>
      ) : null}
      <HeroBanner
        title={t('priceFinder.MVP.customHeroTitle') || 'Our lowest fare finder'}
        image={
          t('priceFinder.MVP.customHeroBanner') ||
          '/content/dam/pi/websites/desktop/homepage/hero-content/events/summer/summer-beach4.jpg'
        }
      />
      <Box {...styles.searchSection}>
        <SearchContainer
          queryClient={queryClient}
          handleLocationSearch={handleLocationSearch}
          setLocationName={setLocationName}
          pageName={PageName.PRICE_FINDER}
        />
      </Box>

      <Box {...styles.container} {...styles.notificationWrapper}>
        <Notification
          maxWidth="full"
          variant="info"
          status="info"
          description={t('priceFinder.notification.negotiatingCorporateRates')}
          svg={<Info />}
        />
      </Box>

      <Box {...styles.container} {...styles.filterMenuWrapper}>
        <PriceFinderRoomSortToggle
          isMobile={isMobile}
          handleSortChange={handleSortChange}
          sortedByDate={sortedByDate}
          toggleFilterMenu={toggleFilterMenu}
          baseDataTestId={baseDataTestId}
          handleToggleFilterMenu={handleToggleFilterMenu}
          filterSelection={filterSelection}
          isRoomTypeFilterEnabled={isRoomTypeFilterEnabled}
        />

        {toggleFilterMenu && isRoomTypeFilterEnabled && (
          <PriceFinderRoomTypeFilter
            locale={locale}
            channel={SITE_BB}
            isBB={false}
            filterSelection={filterSelection}
            setFilterSelection={setFilterSelection}
            isLoading={isLoading}
          />
        )}
      </Box>

      <Box {...styles.pageGrid} data-testid={`${baseDataTestId}-Wrapper`}>
        <Box {...styles.calendarGridItem}>
          {/* Month Tabs positioned directly above table */}
          <Box {...styles.monthTabsContainer}>
            <MonthTabsCarousel
              maxMonths={monthsCovered(new Date())}
              onMonthChange={handleMonthChange}
              locale={locale}
              language={shortLocale}
              initialMonthValue={initialMonthValue}
              lowestPrice={lowestPrice}
              isLoading={isLoading}
              setLowestPrice={setLowestPrice}
              isLowestPriceInMonthTabEnabled={isLowestPriceInMonthTabEnabled}
              fullMonthParams={fullMonthParams}
            />
            {/* Table with no top margin/padding to connect with tabs */}
          </Box>
          <Box
            {...styles.card}
            textAlign="center"
            transition="opacity 0.2s ease-in-out"
            opacity={isPaginating && isLoading ? 0.6 : 1}
            minH="200px"
          >
            <HotelsPriceTable
              {...({
                sortBy,
                onSortChange: handleSortChange,
                locationId,
                formattedDate,
                onDateChange: handleDateChange,
                onCurrentDatesChange: handleCurrentDatesChange,
                onSortedByDateChange: handleSortedByDateChange,
                locale,
                setLowestPrice,
                locationName: locationName,
                setIsLoading: (loading: boolean) => {
                  setIsLoading(loading);
                  if (!loading && isPaginating) {
                    // Add small delay to ensure smooth transition
                    setTimeout(() => setIsPaginating(false), 100);
                  }
                },
                currentPage,
                setTotalItems,
                sortResetKey: sortResetKey,
                onPriceSortChange: handlePriceSortChange,
                filterSelection,
                isBB: true,
              } as HotelsPriceTableProps & {
                setTotalItems: (total: number) => void;
                onPriceSortChange: () => void;
              })}
            />
          </Box>
          {/* Pagination */}
          {totalItems > TABLE_CONFIG.PAGE_SIZE && (
            <Box
              mt={4}
              minH="60px"
              minW="300px"
              display="flex"
              alignItems="center"
              justifyContent="center"
              opacity={isLoading && !isPaginating ? 0 : 1}
              transition="opacity 0.2s ease-in-out"
              visibility={isLoading && !isPaginating ? 'hidden' : 'visible'}
            >
              <Pagination
                currentPage={currentPage}
                totalPages={Math.ceil(totalItems / TABLE_CONFIG.PAGE_SIZE)}
                onPageChange={handlePageChange}
                isDisabled={isPaginating || isLoading}
              />
            </Box>
          )}

          {/* Terms and Conditions Section */}
          <Box {...styles.termsSection}>
            <Box {...styles.termsText}>{renderSanitizedHtml(t('priceFinder.MVP.customTerms'))}</Box>
          </Box>
        </Box>
      </Box>
    </>
  );
}

export default function PriceFinderPageBB({ queryClient, router }: Readonly<Props>) {
  return (
    <QueryClientProvider client={queryClient}>
      <BBPageContent queryClient={queryClient} router={router} />
    </QueryClientProvider>
  );
}

// add all styles/sub-styles below
const styles = {
  pageGrid: {
    templateColumns: 'repeat(12, 1fr)',
    gap: 6,
    mx: 'auto',
    mt: { base: 6 },
    px: { base: 0, sm: 4 },
  },
  container: {
    width: '100%',
    px: {
      mobile: 4,
    },
    maxW: {
      xl: '1260px',
    },
    position: 'relative',
    margin: '0 auto',
  } as const,
  searchSection: {
    w: '100%',
    backgroundColor: '#F8F8F8',
    py: { base: 6, md: 10 },
    px: 4,
  },
  searchWrapper: {
    w: '100%',
    backgroundColor: '#F8F8F8',
    py: { base: 4, md: 6 },
    px: 4,
  },
  searchInner: {
    maxW: '1200px',
    mx: 'auto',
    w: '100%',
  },
  gradientHeadingGridItem: {
    colSpan: 12,
    textAlign: 'center' as GridItemProps['textAlign'],
  },
  calendarGridItem: {
    colSpan: 12,
    colStart: 1,
    m: 0,
    mb: 20,
    p: 0,
    maxW: {
      base: '100%',
      xl: '1228px',
    },
    mx: 'auto',
  },
  card: {
    w: 'full',
    p: 0,
    bg: 'white',
    borderTopLeftRadius: { base: 0, sm: '5px' },
    borderTopRightRadius: { base: 0, sm: '5px' },
    border: `1px solid ${THEME_COLORS.borderColor}`,
    overflow: 'hidden',
    boxShadow: 'none',
    overflowX: 'auto' as const,
    position: 'relative' as const,
    zIndex: 3,
    marginTop: '-1px',
  },
  mobileSortWrapper: {
    w: 'full',
    backgroundColor: 'white',
    paddingTop: 3,
    paddingBottom: 0,
    justifyContent: 'space-between',
  },
  mobileSortButton: {
    fontWeight: 500,
    fontSize: '13px',
    cursor: 'pointer',
    bg: 'transparent',
    border: 'none',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'flex-start',
    gap: 1,
    px: 4,
    _hover: {
      color: THEME_COLORS.sortTextColor,
    },
  },
  gradientHeading: {
    color: 'transparent',
    backgroundClip: 'text',
    backgroundImage: 'linear-gradient(to right, #531f59 50%, #fbba00)',
  },
  monthTabsContainer: {
    maxW: '1440',
    w: '100%',
    mx: 'auto',
    position: 'relative' as const,
    zIndex: 4,
  },
  calendarBox: {
    borderWidth: '1px',
    borderRadius: 'lg',
    bg: 'gray.50',
    p: 10,
    w: '100%',
    maxW: '1200px',
    mx: 'auto',
  },
  termsSection: {
    mt: 10,
    mx: { base: 4, sm: 'auto' },
    w: { base: 'calc(100% - 32px)', sm: '100%' },
    maxW: '1200px',
  },
  termsText: {
    fontSize: { base: 'xs', md: 'sm' },
    color: 'gray.600',
    lineHeight: '1.4',
    textAlign: 'left' as const,
  },
  filterMenuWrapper: {
    marginTop: '16px',
  } as FlexProps,
  filterToggle: {
    color: THEME_COLORS.sortTextColor,
    textDecoration: 'underline',
    textUnderlineOffset: '3px',
    cursor: 'pointer',
    background: 'blue',
  },
  notificationWrapper: {
    paddingTop: 4,
  },
};
