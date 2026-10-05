import { Box, Table, Tbody, Tr, Td, useMediaQuery } from '@chakra-ui/react';
import { AnalyticsData, CountryCode } from '@whitbread-eos/api';
import { useCustomLocale, formatPrice, formatCurrency } from '@whitbread-eos/utils';
import { AnimatePresence } from 'framer-motion';
import { useTranslation } from 'next-i18next';
import dynamic from 'next/dynamic';
import React, { useMemo, useRef, useState, useEffect } from 'react';

import { FloatingHeader } from './FloatingHeader';
import { SkeletonTableRow } from './SkeletonTableRow';
import { TableHeader } from './TableHeader';
import {
  TABLE_CONFIG,
  THEME_COLORS,
  DEFAULT_LOCATION_ID,
  DEFAULT_LOCATION_ID_DE,
  SORT_BY_VALUES,
} from './constants';
import { formatDate, getPriceStyle, buildHotelQueryParams } from './helpers';
import { useDateNavigation, useHotelData, useFloatingHeader, useLowestRates } from './hooks';
import { GetLowestRatesByLocationIdResponse, SupportedLocales, SortBy, LowestRate } from './types';

const Tooltip = dynamic(
  async () => {
    const { Tooltip } = await import('@whitbread-eos/atoms');
    return { default: Tooltip };
  },
  {
    ssr: false,
  }
);

declare global {
  interface Window {
    analyticsData: AnalyticsData;
    // satellite required for adobe analytics on the confirmation Page
    __satelliteLoaded: boolean;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    _satellite: any;
  }
}

export interface HotelsPriceTableProps {
  sortBy?: SortBy;
  onSortChange?: (sortBy: SortBy) => void;
  data?: GetLowestRatesByLocationIdResponse | null;
  isLoading?: boolean;
  isError?: boolean;
  locationId?: string;
  formattedDate?: string;
  onDateChange?: (newDate: string) => void;
  onCurrentDatesChange?: (dates: string[]) => void;
  onSortedByDateChange?: (sortedByDate: string | null) => void;
  locale?: SupportedLocales;
  setLowestPrice?: (price: LowestRate) => void;
  setIsLoading?: (loading: boolean) => void;
  currentPage?: number;
  setTotalItems?: (total: number) => void;
  onPriceSortChange?: () => void;
  sortResetKey?: number;
  locationName?: string;
  filterSelection?: Record<string, boolean>;
  priceFinderConfig?: {
    highlightedPriceRangeStep?: number;
    highlightedPriceRangeMin?: number;
    highlightedPricePrimaryColour?: string;
    highlightedPriceSecondaryColour?: string;
    path?: string;
  };
  isBB?: boolean;
  priceFinderViewLocationId?: string;
  placeId?: string;
}

export default function HotelsPriceTable({
  sortBy: externalSortBy,
  onSortChange,
  data: externalData,
  isLoading: externalIsLoading,
  isError: externalIsError,
  locationId,
  formattedDate,
  onDateChange,
  onCurrentDatesChange,
  onSortedByDateChange,
  locale = 'en-GB' as SupportedLocales,
  setLowestPrice,
  setIsLoading,
  currentPage = 1,
  setTotalItems,
  onPriceSortChange,
  sortResetKey,
  locationName,
  filterSelection,
  priceFinderConfig,
  isBB,
  priceFinderViewLocationId,
  placeId,
}: HotelsPriceTableProps = {}) {
  const [isMobile] = useMediaQuery('(max-width: 767px)');
  const [isSmallerThanSm] = useMediaQuery('(max-width: 575px)');
  const [isLargeScreen] = useMediaQuery('(min-width: 1024px)');
  const tableHeaderRef = useRef<HTMLTableSectionElement>(
    null
  ) as React.MutableRefObject<HTMLTableSectionElement>;
  const [internalSortBy, setInternalSortBy] = useState<SortBy>(SORT_BY_VALUES.DISTANCE as SortBy);
  const [priceSortDate, setPriceSortDate] = useState<string | null>(null);
  const [hoveredHotelIdx, setHoveredHotelIdx] = useState<number | null>(null);
  const sortBy = externalSortBy ?? internalSortBy;
  const { t } = useTranslation();
  const { language, country } = useCustomLocale();
  const roomTypeCodes = ['SB', 'DB', 'TWIN', 'FAM', 'DIS'];

  // Track which arrival date the priceSortDate is associated with
  const [priceSortArrival, setPriceSortArrival] = useState<string | null>(null);

  // Dynamically get roomCategory translation using shortCode. Text to render shortCode depends on the backend code response
  const getRoomCategoryText = (shortCode?: string): string => {
    if (!shortCode) return '';
    return t(`priceFinder.roomType.${shortCode}`) || shortCode;
  };

  const {
    currentDates,
    currentStartDate,
    arrival,
    handleScrollLeft,
    handleScrollRight,
    isScrollLeftDisabled,
    isScrollRightDisabled,
  } = useDateNavigation(onDateChange, formattedDate);

  // Reset priceSortDate when parent explicitly sets sortBy to DISTANCE
  useEffect(() => {
    if (sortBy === SORT_BY_VALUES.DISTANCE) {
      setPriceSortDate(null);
      setPriceSortArrival(null);
    }
  }, [sortBy]);

  // Reset priceSortDate when sortResetKey changes (parent triggered reset)
  useEffect(() => {
    if (typeof sortResetKey !== 'undefined') {
      setPriceSortDate(null);
      setPriceSortArrival(null);
    }
  }, [sortResetKey]);

  // Reset priceSortDate when location or date changes
  useEffect(() => {
    setPriceSortDate(null);
    setPriceSortArrival(null);
  }, [locationId, formattedDate, arrival]);

  // Notify parent about current dates change for month tab sync
  useEffect(() => {
    if (onCurrentDatesChange && currentDates.length > 0) {
      onCurrentDatesChange(currentDates);
    }
  }, [currentDates, onCurrentDatesChange]);

  // Notify parent about sorted by date change for mobile button color
  useEffect(() => {
    if (onSortedByDateChange) {
      onSortedByDateChange(priceSortDate);
    }
  }, [priceSortDate, onSortedByDateChange]);

  // Calculate colSpan based on mobile state (scroll columns are always present now)
  const getColSpan = (dateCount: number) => {
    if (isMobile) {
      // Mobile: hotel name (0) + left scroll (1) + dates + right scroll (1)
      return dateCount + 2;
    } else {
      // Desktop: hotel name (1) + left scroll (1) + dates + right scroll (1)
      return dateCount + 3;
    }
  };

  const selectedFilters = filterSelection
    ? Object.keys(filterSelection).filter((key) => filterSelection[key])
    : [];

  const criteria = useMemo(() => {
    // Only use priceSortDate if it's associated with the current arrival date
    // This prevents using old priceSortDate when arrival date changes
    const currentArrival = formattedDate || arrival;
    const isPriceSortDateValid = priceSortDate && priceSortArrival === currentArrival;

    // Determine actual sort type:
    // - If priceSortDate exists AND is for the current arrival date, use PRICE
    // - Otherwise, use parent's sortBy value
    const actualSortBy = isPriceSortDateValid
      ? 'PRICE'
      : (sortBy.toUpperCase() as 'DISTANCE' | 'PRICE');

    const defaultLocationId =
      country === CountryCode.DE ? DEFAULT_LOCATION_ID_DE : DEFAULT_LOCATION_ID;

    const altLocationId = placeId ?? priceFinderViewLocationId ?? defaultLocationId;

    const baseCriteria = {
      locationId: locationId || altLocationId,
      arrival: formattedDate || arrival, // use formattedDate if provided, otherwise use current arrival date
      daysRange: TABLE_CONFIG.DAYS_TO_SHOW,
      showMinimumNights: false,
      page: currentPage,
      initialPageSize: TABLE_CONFIG.PAGE_SIZE,
      lazyLoadPageSize: TABLE_CONFIG.LAZY_LOAD_PAGE_SIZE,
      country,
      language,
      sortBy: actualSortBy,
      filterByRoomType: selectedFilters.length ? selectedFilters : roomTypeCodes,
    };

    // Only include sortDate when sortBy is PRICE and priceSortDate is valid for current arrival
    if (actualSortBy === 'PRICE' && isPriceSortDateValid) {
      return { ...baseCriteria, sortDate: priceSortDate };
    }

    return baseCriteria;
  }, [
    locationId,
    formattedDate,
    arrival,
    sortBy,
    priceSortDate,
    priceSortArrival,
    currentPage,
    country,
    language,
    selectedFilters,
  ]);

  // Use external data when provided, otherwise use standardized internal query
  const shouldUseInternalQuery =
    !externalData && externalIsLoading === undefined && externalIsError === undefined;

  // Use the new standardized hook for internal queries
  const {
    data: internalData,
    hotelData: internalHotelData,
    globalLowestPrice: internalGlobalLowestPrice,
    lowestMonthlyRate: internalLowestMonthlyRate,
    isLoading: internalIsLoading,
    isError: internalIsError,
  } = useLowestRates(criteria, currentDates, { enabled: shouldUseInternalQuery }, locationName);

  // Determine which data source to use
  const data = externalData !== undefined ? externalData : internalData;
  const isLoading = externalIsLoading !== undefined ? externalIsLoading : internalIsLoading;
  const isError = externalIsError !== undefined ? externalIsError : internalIsError;

  // Use transformed data from standardized hook when available, otherwise use useHotelData
  const shouldUseInternalTransformedData = shouldUseInternalQuery && internalHotelData.length > 0;

  const { hotelData: externalHotelData, globalLowestPrice: externalGlobalLowestPrice } =
    useHotelData({
      currentDates,
      currentStartDate,
      data: shouldUseInternalTransformedData ? null : data,
      isLoading: shouldUseInternalTransformedData ? false : isLoading,
      isError: shouldUseInternalTransformedData ? false : isError,
    });

  // Use appropriate data source
  const hotelData = shouldUseInternalTransformedData ? internalHotelData : externalHotelData;
  const globalLowestPrice = shouldUseInternalTransformedData
    ? internalGlobalLowestPrice
    : externalGlobalLowestPrice;

  const { showFloatingHeader } = useFloatingHeader(tableHeaderRef);

  // Use lowestMonthlyRate from the hook for MonthTabsCarousel component
  const lowestMonthlyRate = shouldUseInternalQuery
    ? internalLowestMonthlyRate
    : data?.getLowestRatesByLocationId?.lowestMonthlyRate || null;

  // Track previous values to prevent unnecessary updates
  const prevLowestMonthlyRate = useRef<LowestRate | null>(null);
  const prevIsLoading = useRef<boolean | undefined>(undefined);
  const prevTotal = useRef<number | undefined>(undefined);
  const prevLocationId = useRef<string | undefined>(undefined);
  const prevArrival = useRef<string | undefined>(undefined);

  // Reset prevLowestMonthlyRate when location or arrival date changes
  useEffect(() => {
    const currentLocationId = locationId || DEFAULT_LOCATION_ID;
    const currentArrival = formattedDate || arrival;

    if (prevLocationId.current !== currentLocationId || prevArrival.current !== currentArrival) {
      prevLowestMonthlyRate.current = null;
      prevLocationId.current = currentLocationId;
      prevArrival.current = currentArrival;
    }
  }, [locationId, formattedDate, arrival]);

  useEffect(() => {
    // Only update if lowestMonthlyRate has actually changed
    if (setLowestPrice && lowestMonthlyRate !== null) {
      const hasChanged =
        prevLowestMonthlyRate.current === null ||
        prevLowestMonthlyRate.current.price !== lowestMonthlyRate.price ||
        prevLowestMonthlyRate.current.currency !== lowestMonthlyRate.currency;

      if (hasChanged) {
        prevLowestMonthlyRate.current = lowestMonthlyRate;
        setLowestPrice(lowestMonthlyRate);
      }
    }

    // Only update if isLoading has actually changed
    if (setIsLoading && isLoading !== prevIsLoading.current) {
      prevIsLoading.current = isLoading;
      setIsLoading(isLoading);
    }

    // Only update if total has actually changed
    const currentTotal = data?.getLowestRatesByLocationId?.total;
    if (setTotalItems && currentTotal !== undefined && currentTotal !== prevTotal.current) {
      prevTotal.current = currentTotal;
      setTotalItems(currentTotal);
    }
  }, [
    data,
    isLoading,
    lowestMonthlyRate,
    setLowestPrice,
    setIsLoading,
    setTotalItems,
    selectedFilters,
  ]);

  const handleSortToggle = () => {
    // Always sort by distance when clicking the sort button
    // Reset price sort date to switch back to distance sorting
    setPriceSortDate(null);
    setPriceSortArrival(null);
    if (onSortChange) {
      onSortChange(SORT_BY_VALUES.DISTANCE as SortBy);
      window.__satelliteLoaded && window._satellite.track('priceFinderHotelSelected');
    } else {
      setInternalSortBy(SORT_BY_VALUES.DISTANCE as SortBy);
    }
  };

  const handleDateSortForPrice = (date: string) => {
    // Set the date for price sorting - this will trigger a new query with sortBy=PRICE
    // Also record which arrival date this price sort is for
    const currentArrival = formattedDate || arrival;
    setPriceSortDate(date);
    setPriceSortArrival(currentArrival);
    // Reset pagination to page 1 when price sorting changes
    if (onPriceSortChange) {
      onPriceSortChange();
      window.__satelliteLoaded && window._satellite.track('priceFinderHotelSelected');
    }
  };

  const handleScrollLeftWithReset = () => {
    // Reset price sorting when scrolling to different dates
    setPriceSortDate(null);
    setPriceSortArrival(null);
    if (onSortChange) {
      onSortChange(SORT_BY_VALUES.DISTANCE as SortBy);
    } else {
      setInternalSortBy(SORT_BY_VALUES.DISTANCE as SortBy);
    }
    handleScrollLeft();
  };

  const handleScrollRightWithReset = () => {
    // Reset price sorting when scrolling to different dates
    setPriceSortDate(null);
    setPriceSortArrival(null);
    if (onSortChange) {
      onSortChange(SORT_BY_VALUES.DISTANCE as SortBy);
    } else {
      setInternalSortBy(SORT_BY_VALUES.DISTANCE as SortBy);
    }
    handleScrollRight();
  };

  const handleHotelRedirect = (
    hotelLink: string | undefined,
    price: number | null,
    selectedDate: string,
    roomTypeCode?: string
  ) => {
    if (hotelLink && price && roomTypeCode) {
      const queryParams = buildHotelQueryParams(selectedDate, roomTypeCode);
      const bbHdpPath = isBB ? '/business-booker' : '';
      const finalUrl = `/${country}/${language}${bbHdpPath}/hotels${hotelLink}.html?${queryParams}`;
      window.open(finalUrl, '_blank');
      window.__satelliteLoaded && window._satellite.track('priceFinderHotelSelected');
    }
  };

  const getColumnWidth = () => {
    if (isSmallerThanSm) return '22px';
    if (isMobile) return '40px';
    if (isLargeScreen) return '82.55px';
    return '54px';
  };

  const renderPriceCells = (
    hotel: {
      hotelName: string;
      hotelLink?: string;
      hotelId: string;
      prices: Array<{
        date: string;
        amount: number | null;
        currency: string;
        roomCategory?: string;
      }>;
    },
    hIdx: number,
    isMobileView: boolean
  ) => {
    return hotel.prices.map((price, idx) => {
      const dateForThisPrice = currentDates[idx];
      const isCurrentSort = priceSortDate === dateForThisPrice;
      const formattedPrice = price.amount
        ? formatPrice(formatCurrency(price.currency), price.amount, language)
        : '\u2501';
      const priceStyle = getPriceStyle(
        price.amount,
        globalLowestPrice,
        parseInt(t('priceFinder.MVP.customConfig.highlightedPriceRange') || '5'),
        priceFinderConfig
      );
      const cellBg = isCurrentSort ? THEME_COLORS.sortedCellBg : 'transparent';

      const cellStyles = isMobileView ? styles.mobilePriceCell : styles.desktopPriceCell;
      const focusStyles = isMobileView
        ? {}
        : {
            _focus: {
              outline: '2px solid',
              outlineColor: THEME_COLORS.primaryColor,
              outlineOffset: '2px',
            },
          };

      // Get the mapped roomCategory for tooltip
      const mappedRoomCategory = getRoomCategoryText(price.roomCategory);

      const isTransparent = priceStyle.textBg === 'transparent';
      const hasLinkAndPrice = !!(hotel.hotelLink && price.amount);

      const priceBox = (
        <Box
          tabIndex={0}
          bg={priceStyle.textBg}
          color={priceStyle.color}
          fontWeight={priceStyle.fontWeight || 'normal'}
          {...styles.priceBoxDimensions(isMobile, isMobileView, isTransparent)}
          sx={styles.priceBoxHover(hasLinkAndPrice)}
          {...styles.priceBox}
        >
          {formattedPrice}
        </Box>
      );

      return (
        <Td
          key={`${hotel.hotelId}-${idx}`}
          data-testid={`price-cell-${hotel.hotelId}-${idx}`}
          bg={cellBg}
          aria-label={
            price.amount
              ? `${hotel.hotelName} price on ${formatDate(dateForThisPrice, { locale }).weekday} ${
                  formatDate(dateForThisPrice, { locale }).day
                }: ${formattedPrice}`
              : `${hotel.hotelName} not available on ${
                  formatDate(dateForThisPrice, { locale }).weekday
                } ${formatDate(dateForThisPrice, { locale }).day}`
          }
          cursor={hotel.hotelLink && price.amount ? 'pointer' : 'default'}
          {...focusStyles}
          onClick={() =>
            handleHotelRedirect(hotel.hotelLink, price.amount, dateForThisPrice, price.roomCategory)
          }
          {...cellStyles}
        >
          {price.amount && mappedRoomCategory ? (
            <Tooltip
              description={mappedRoomCategory}
              hasArrow={false}
              variant="standard"
              alertElementStyles={{ padding: '0.5rem 0.25rem 0.5rem 0' }}
              placement="top"
              sx={styles.tooltip}
            >
              {priceBox}
            </Tooltip>
          ) : (
            priceBox
          )}
        </Td>
      );
    });
  };

  return (
    <>
      {/* Floating header that appears when main header is out of view */}
      <AnimatePresence>
        {showFloatingHeader && (
          <FloatingHeader
            key="floating-header"
            currentDates={currentDates}
            sortedByDate={priceSortDate}
            handleDateSort={handleDateSortForPrice}
            handleScrollLeft={handleScrollLeftWithReset}
            handleScrollRight={handleScrollRightWithReset}
            isScrollLeftDisabled={isScrollLeftDisabled}
            isScrollRightDisabled={isScrollRightDisabled}
            isLoading={isLoading}
            isMobile={isMobile}
            isSmallerThanSm={isSmallerThanSm}
            handleSortToggle={handleSortToggle}
            locale={locale}
            getColumnWidth={getColumnWidth}
          />
        )}
      </AnimatePresence>
      <Table {...styles.table} data-testid="hotel-price-table" style={styles.tableLayout(isMobile)}>
        <colgroup>
          {!isMobile && <col style={styles.hotelNameCol} />}
          <col style={styles.colSpacer(isMobile)} />
          {currentDates.map((_, idx) => (
            <col key={idx} data-testid={`col-${idx}`} style={styles.colDate(getColumnWidth())} />
          ))}
          <col style={styles.colSpacer(isMobile)} />
        </colgroup>
        <TableHeader
          currentDates={currentDates}
          sortedByDate={priceSortDate}
          handleDateSort={handleDateSortForPrice}
          handleScrollLeft={handleScrollLeftWithReset}
          handleScrollRight={handleScrollRightWithReset}
          isScrollLeftDisabled={isScrollLeftDisabled}
          isScrollRightDisabled={isScrollRightDisabled}
          isLoading={isLoading}
          isMobile={isMobile}
          isSmallerThanSm={isSmallerThanSm}
          tableHeaderRef={tableHeaderRef}
          handleSortToggle={handleSortToggle}
          locale={locale}
        />
        <Tbody data-testid="hotel-price-table-body" sx={styles.tbody(isLoading, isMobile)}>
          {isLoading ? (
            <>
              {Array.from({ length: TABLE_CONFIG.SKELETON_ROWS_COUNT }, (_, idx) => (
                <SkeletonTableRow
                  key={`skeleton-${idx}`}
                  isMobile={isMobile}
                  currentDates={currentDates}
                  index={idx}
                />
              ))}
            </>
          ) : isError ? (
            <Tr data-testid="error-row" bg={THEME_COLORS.primaryRowBg}>
              <Td {...styles.errorCell} colSpan={getColSpan(TABLE_CONFIG.DAYS_TO_SHOW)}>
                <Box {...styles.errorContainer}>
                  <Box {...styles.errorTitle} data-testid="error-message-title">
                    {t('priceFinder.noResults.title')}
                  </Box>
                  <Box {...styles.errorDescription} data-testid="error-message-description">
                    {t('priceFinder.noResults.description')}
                  </Box>
                </Box>
              </Td>
            </Tr>
          ) : hotelData.length === 0 ? (
            <Tr data-testid="no-data-row" bg={THEME_COLORS.primaryRowBg}>
              <Td {...styles.errorCell} colSpan={getColSpan(TABLE_CONFIG.DAYS_TO_SHOW)}>
                <Box {...styles.errorContainer}>
                  <Box {...styles.errorTitle} data-testid="no-data-message">
                    {t('priceFinder.noResults.title')}
                  </Box>
                  <Box {...styles.errorDescription} data-testid="no-data-description">
                    {t('priceFinder.noResults.description')}
                  </Box>
                </Box>
              </Td>
            </Tr>
          ) : (
            hotelData.map((hotel, hIdx) =>
              isMobile ? (
                <React.Fragment key={hotel.hotelId}>
                  <Tr
                    data-testid={`hotel-row-name-${hIdx}`}
                    bg={
                      hoveredHotelIdx === hIdx
                        ? styles.mobileHotelNamePricesRow._hover.bg
                        : hIdx % 2 === 0
                          ? THEME_COLORS.primaryRowBg
                          : THEME_COLORS.alternateRowBg
                    }
                    {...styles.mobileHotelNameRow}
                    onMouseEnter={() => setHoveredHotelIdx(hIdx)}
                    onMouseLeave={() => setHoveredHotelIdx(null)}
                  >
                    <Td colSpan={getColSpan(currentDates.length)} {...styles.mobileHotelNameCell}>
                      {hotel.hotelName}
                    </Td>
                  </Tr>
                  <Tr
                    data-testid={`hotel-row-prices-${hIdx}`}
                    bg={
                      hoveredHotelIdx === hIdx
                        ? styles.mobileHotelNamePricesRow._hover.bg
                        : hIdx % 2 === 0
                          ? THEME_COLORS.primaryRowBg
                          : THEME_COLORS.alternateRowBg
                    }
                    {...styles.mobilePricesRow}
                    onMouseEnter={() => setHoveredHotelIdx(hIdx)}
                    onMouseLeave={() => setHoveredHotelIdx(null)}
                  >
                    <Td data-testid={`hotel-spacer-left-${hIdx}`} {...styles.spacerCell} />
                    {renderPriceCells(hotel, hIdx, true)}
                    <Td data-testid={`hotel-spacer-right-${hIdx}`} {...styles.spacerCell} />
                  </Tr>
                </React.Fragment>
              ) : (
                <Tr
                  key={hotel.hotelId}
                  data-testid={`hotel-row-${hIdx}`}
                  bg={hIdx % 2 === 0 ? THEME_COLORS.primaryRowBg : THEME_COLORS.alternateRowBg}
                  {...styles.hotelNamePricesRow}
                >
                  <Td data-testid={`hotel-name-${hIdx}`} {...styles.desktopHotelNameCell}>
                    {hotel.hotelName}
                  </Td>
                  <Td data-testid={`hotel-spacer-left-${hIdx}`} />
                  {renderPriceCells(hotel, hIdx, false)}
                  <Td data-testid={`hotel-spacer-right-${hIdx}`} />
                </Tr>
              )
            )
          )}
        </Tbody>
      </Table>
    </>
  );
}

// Component styles
const styles = {
  table: {
    variant: 'simple' as const,
    size: 'sm' as const,
    role: 'table' as const,
  },
  hotelNameCol: {
    width: '40%',
  },
  errorCell: {
    textAlign: 'center' as const,
    py: 12,
  },
  errorContainer: {
    display: 'flex',
    flexDirection: 'column' as const,
    alignItems: 'center',
    gap: 4,
  },
  errorTitle: {
    fontSize: 'xl',
    fontWeight: 'semibold',
  },
  errorDescription: {
    fontSize: 'md',
  },
  mobileHotelNameRow: {
    transition: 'background-color 0.2s ease',
    sx: {
      borderBottom: 'none',
    },
  },
  mobileHotelNameCell: {
    fontWeight: { base: 600, md: 400 },
    fontSize: { base: '14px', md: '16px' },
    textAlign: 'left' as const,
    px: { base: '1.5rem', sm: '3.5rem' },
    py: 1,
    sx: {
      overflow: 'hidden',
      whiteSpace: 'nowrap',
      textOverflow: 'ellipsis',
      borderBottom: 'none',
    },
  },
  mobilePricesRow: {
    transition: 'background-color 0.2s ease',
    sx: {
      borderTop: 'none',
    },
  },
  hotelNamePricesRow: {
    transition: 'background-color 0.2s ease',
    _hover: {
      bg: '#EBF4F6',
    },
  },
  mobileHotelNamePricesRow: {
    _hover: {
      bg: '#EBF4F6',
    },
  },
  spacerCell: {
    sx: { borderTop: 'none' },
  },
  mobilePriceCell: {
    textAlign: 'center' as const,
    transition: 'background-color 0.2s ease',
    minW: '44px',
    w: '70px',
    fontSize: {
      base: 'xs',
      md: 'sm',
    },
    sx: {
      borderTop: 'none',
    },
    p: { base: '2px', md: '2' },
  },
  priceBox: {
    borderRadius: '4px',
    display: 'inline-block',
    textDecoration: 'none',
    transition: 'all 0.1s linear',
    whiteSpace: 'nowrap' as const,
  },
  desktopRow: {
    transition: 'background-color 0.2s ease',
  },
  desktopHotelNameCell: {
    fontWeight: { base: 600, md: 400 },
    scope: 'row' as const,
    fontSize: { base: '14px', md: '16px' },
    w: '50%',
    sx: {
      overflow: 'hidden',
      whiteSpace: 'nowrap',
      textOverflow: 'ellipsis',
    },
    paddingLeft: '2rem',
  },
  desktopPriceCell: {
    textAlign: 'center' as const,
    transition: 'background-color 0.2s ease',
    minW: 'calc(50% / 7)',
    w: 'calc(50% / 7)',
    fontSize: {
      base: 'xs',
      md: 'sm',
    },
  },
  // Dynamic style helpers
  tableLayout: (isMobile: boolean) => ({
    tableLayout: 'fixed' as const,
    minWidth: isMobile ? '280px' : 'auto',
  }),
  colSpacer: (isMobile: boolean) => ({
    width: isMobile ? '12px' : '40px',
  }),
  colDate: (width: string) => ({
    width,
  }),
  tbody: (isLoading: boolean, isMobile: boolean) =>
    isLoading
      ? {
          height: isMobile ? '1540px' : '980px',
        }
      : {},
  priceBoxHover: (hasLinkAndPrice: boolean) => ({
    '&:hover, &:focus': {
      textDecoration: hasLinkAndPrice ? 'underline' : 'none',
      color: hasLinkAndPrice ? '#00798d' : 'inherit',
      fontWeight: hasLinkAndPrice ? '700' : 'normal',
      transform: hasLinkAndPrice ? 'scale(1.1)' : 'none',
    },
  }),
  priceBoxDimensions: (isMobile: boolean, isMobileView: boolean, isTransparent: boolean) => ({
    px: !isTransparent ? { base: isMobileView ? 1 : '2px', xs: 2 } : 0,
    py: !isTransparent ? { base: isMobileView ? 0.5 : '1px', xs: 1 } : 0,
    my: isMobile && isMobileView && !isTransparent ? '2px' : 0,
  }),
  tooltip: {
    bg: 'var(--chakra-colors-white)',
    color: 'var(--chakra-colors-darkGrey1)',
    border: '1px solid #00798d',
    _before: { display: 'none' },
  },
};
