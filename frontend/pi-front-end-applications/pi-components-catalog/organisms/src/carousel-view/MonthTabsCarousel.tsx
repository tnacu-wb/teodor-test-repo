import { Flex, Box, useMediaQuery } from '@chakra-ui/react';
import { AnalyticsData } from '@whitbread-eos/api';
import { MonthTab } from '@whitbread-eos/atoms';
import { ScrollButton } from '@whitbread-eos/molecules';
import { LowestRate } from '@whitbread-eos/molecules/dist/price-finder/HotelsPriceTable/types';
import { useHorizontalScroll } from '@whitbread-eos/utils';
import { MONTHS } from '@whitbread-eos/utils';
import React, { useState, useEffect, useMemo } from 'react';

import { generateMonthData, type MonthData } from '../utils/common/date-utils';

declare global {
  interface Window {
    analyticsData: AnalyticsData;
    // satellite required for adobe analytics on the confirmation Page
    __satelliteLoaded: boolean;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    _satellite: any;
  }
}

interface MonthTabsCarouselProps {
  maxMonths?: number;
  initialMonthValue?: string;
  onMonthChange?: (monthData: MonthData) => void;
  language?: string;
  locale?: 'en-GB' | 'de-DE';
  lowestPrice?: LowestRate;
  setLowestPrice?: (price: LowestRate) => void;
  isLoading?: boolean;
  isLowestPriceInMonthTabEnabled?: boolean;
  badgeColor?: string;
  fullMonthParams?: string;
}

const MonthTabsCarousel: React.FC<MonthTabsCarouselProps> = ({
  maxMonths = 12,
  initialMonthValue,
  onMonthChange,
  language,
  locale = 'en-GB',
  setLowestPrice,
  lowestPrice,
  isLoading,
  isLowestPriceInMonthTabEnabled,
  badgeColor,
  fullMonthParams,
}) => {
  const months = useMemo(() => generateMonthData(maxMonths, locale), [maxMonths, locale]);
  const [isMobile] = useMediaQuery('(max-width: 391px)');

  const {
    scrollRef,
    scrollToLeft,
    scrollToRight,
    canScrollLeft,
    canScrollRight,
    hasOverflow,
    checkScrollButtons,
  } = useHorizontalScroll<HTMLDivElement>(146);

  const handleScrollLeft = () => {
    scrollToLeft();
  };

  const handleScrollRight = () => {
    scrollToRight();
  };

  const [activeMonthValue, setActiveMonthValue] = useState<string>(() => {
    if (initialMonthValue && months.some((m) => m.value === initialMonthValue)) {
      return initialMonthValue;
    }
    return months[0]?.value || '';
  });

  const getNext13Months = () => {
    const now = new Date();
    const currentMonth = now.getMonth(); // 0–11
    const currentYear = now.getFullYear();
    const result = [];

    for (let i = 0; i < 13; i++) {
      const monthIndex = (currentMonth + i) % 12;
      const yearOffset = Math.floor((currentMonth + i) / 12);
      const year = currentYear + yearOffset;

      result.push(`${MONTHS[monthIndex]} ${year}`);
    }

    return result;
  };
  const [sm] = useMediaQuery('(max-width: 575px)');
  const [md] = useMediaQuery('(min-width: 576px) and (max-width: 767px)');
  const [lg] = useMediaQuery('(min-width: 768px)');
  useEffect(() => {
    const monthScrollToIdx = getNext13Months().indexOf(fullMonthParams as string);
    if (!scrollRef.current || monthScrollToIdx < 0) return;

    const breakpoints = {
      sm: { distance: 133, offset: 46, label: 'sm' },
      md: { distance: 161, offset: 38, label: 'md' },
      lg: { distance: 161, offset: 68, label: 'lg' },
    };

    const scrollSettings =
      (sm && breakpoints.sm) || (md && breakpoints.md) || (lg && breakpoints.lg) || breakpoints.lg;

    const { distance, offset } = scrollSettings;

    //reset scroll
    if (scrollRef.current) {
      scrollRef.current.scrollTo({ left: -10000, behavior: 'auto' });
    }

    scrollRef.current.scrollTo({
      left: distance * monthScrollToIdx - offset,
      behavior: 'auto',
    });
  }, [sm, md, lg]);

  useEffect(() => {
    const el = scrollRef.current;
    if (!el) return;

    const handleScroll = () => {
      checkScrollButtons();
    };

    el.addEventListener('scroll', handleScroll);
    checkScrollButtons();

    return () => el.removeEventListener('scroll', handleScroll);
  }, [scrollRef, checkScrollButtons]);

  // Update active month when initialMonthValue changes (from external calendar navigation)
  useEffect(() => {
    if (initialMonthValue && months.some((m) => m.value === initialMonthValue)) {
      setActiveMonthValue(initialMonthValue);
    }
  }, [initialMonthValue, months]);

  const handleTabClick = (monthData: MonthData) => {
    setActiveMonthValue(monthData.value);
    if (setLowestPrice && monthData?.value !== activeMonthValue) {
      setLowestPrice({
        price: 0,
        currency: '',
      });
      window.__satelliteLoaded && window._satellite.track('priceFinderHotelSelected');
    }
    if (onMonthChange) onMonthChange(monthData);
  };

  return (
    <Box position="relative">
      {/* Main carousel container */}
      <Box {...styles.container}>
        <Box
          ref={scrollRef as React.LegacyRef<HTMLDivElement>}
          {...styles.scrollContainer}
          onScroll={checkScrollButtons}
        >
          <Flex {...styles.innerTrack}>
            {months.map((month) => (
              <MonthTab
                language={language}
                key={month.value}
                label={month.label}
                price={lowestPrice?.price}
                isActive={month.value === activeMonthValue}
                onClick={() => handleTabClick(month)}
                data-month-value={month.value}
                currencySymbol={lowestPrice?.currency}
                isLoading={isLoading}
                isLowestPriceInMonthTabEnabled={isLowestPriceInMonthTabEnabled}
                badgeColor={badgeColor}
              />
            ))}
          </Flex>
        </Box>
      </Box>

      {/* Left scroll button with gradient - overlapping */}
      <Box {...styles.leftSection}>
        <Box {...styles.leftGradient} opacity={hasOverflow && canScrollLeft ? 0.8 : 0} />
        {!isMobile && (
          <Box
            opacity={hasOverflow && canScrollLeft ? 0.8 : 0}
            pointerEvents={hasOverflow && canScrollLeft ? 'auto' : 'none'}
            {...styles.scrollButtonContainer}
          >
            <ScrollButton direction="left" onClick={handleScrollLeft} {...styles.scrollButton} />
          </Box>
        )}
      </Box>

      {/* Right scroll button with gradient - overlapping */}
      <Box {...styles.rightSection}>
        <Box {...styles.rightGradient} opacity={hasOverflow && canScrollRight ? 0.8 : 0} />
        {!isMobile && (
          <Box
            opacity={hasOverflow && canScrollRight ? 0.8 : 0}
            pointerEvents={hasOverflow && canScrollRight ? 'auto' : 'none'}
            {...styles.scrollButtonContainer}
          >
            <ScrollButton direction="right" onClick={handleScrollRight} {...styles.scrollButton} />
          </Box>
        )}
      </Box>
    </Box>
  );
};

export default MonthTabsCarousel;

const styles = {
  outerContainer: {
    alignItems: 'center',
    w: 'full',
    gap: 0,
    padding: 0,
  },
  leftSection: {
    position: 'absolute' as const,
    top: 0,
    left: 0,
    bottom: 0,
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'flex-start',
    w: { base: '50px', md: '80px' },
    zIndex: 5,
  },
  rightSection: {
    position: 'absolute' as const,
    top: 0,
    right: 0,
    bottom: 0,
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'flex-end',
    w: { base: '50px', md: '80px' },
    zIndex: 5,
  },
  container: {
    position: 'relative' as const,
    flex: 1,
    overflow: 'hidden',
    margin: '0 20px',
  },
  scrollContainer: {
    overflowX: 'auto' as const,
    w: 'full',
    sx: {
      '&::-webkit-scrollbar': { display: 'none' },
      '-ms-overflow-style': 'none',
      scrollbarWidth: 'none',
    },
  },
  innerTrack: {
    justify: 'flex-start',
    align: 'flex-end',
  },
  leftGradient: {
    position: 'absolute' as const,
    right: 0,
    top: 0,
    bottom: 0,
    width: { base: '50px', md: '65px' },
    background:
      'linear-gradient(to right, rgba(255,255,255,1) 60%, rgba(255,255,255,0.95) 30%, rgba(255,255,255,0.7) 80%, rgba(255,255,255,0) 100%)',
    zIndex: 1,
    pointerEvents: 'none' as const,
    marginBottom: '1px',
  },
  rightGradient: {
    position: 'absolute' as const,
    top: 0,
    bottom: 0,
    width: { base: '50px', md: '65px' },
    background:
      'linear-gradient(to left, rgba(255,255,255,1) 60%, rgba(255,255,255,0.95) 30%, rgba(255,255,255,0.7) 80%, rgba(255,255,255,0) 100%)',
    zIndex: 1,
    pointerEvents: 'none' as const,
    marginBottom: '1px',
  },
  scrollButtonContainer: {
    zIndex: 3,
  },
  scrollButton: {
    borderRadius: 'full',
    boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
    bg: 'white',
    border: '1px solid #E2E8F0',
    minW: { base: '32px', md: '40px' },
    h: { base: '32px', md: '40px' },
    zIndex: 2,
    _hover: {
      boxShadow: '0 4px 12px rgba(0, 0, 0, 0.2)',
      transform: 'translateY(-1px)',
    },
  },
};
