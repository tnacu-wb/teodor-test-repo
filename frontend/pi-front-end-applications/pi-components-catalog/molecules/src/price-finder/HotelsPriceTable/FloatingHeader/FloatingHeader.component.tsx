import { Box, Table } from '@chakra-ui/react';
import { motion } from 'framer-motion';
import React from 'react';

import { TableHeader } from '../TableHeader';
import { THEME_COLORS } from '../constants';
import { FloatingHeaderProps } from '../types';

export const FloatingHeader: React.FC<FloatingHeaderProps> = ({
  currentDates,
  sortedByDate,
  handleDateSort,
  handleScrollLeft,
  handleScrollRight,
  isScrollLeftDisabled,
  isScrollRightDisabled = false,
  isLoading = false,
  isMobile,
  isSmallerThanSm,
  handleSortToggle,
  getColumnWidth,
  locale = 'en-GB',
}) => (
  <Box
    as={motion.div}
    initial={{ opacity: 0, y: -20 }}
    animate={{ opacity: 1, y: 0 }}
    exit={{ opacity: 0, y: -20 }}
    style={styles.motionContainer}
    data-testid="hotel-price-table-floating-header"
  >
    <Box {...styles.outerContainer}>
      <Box {...styles.innerContainer}>
        <Table
          {...styles.table}
          style={{ tableLayout: 'fixed', minWidth: isMobile ? '280px' : 'auto' }}
        >
          <colgroup>
            {!isMobile && <col style={styles.hotelNameCol} />}
            <col style={{ width: isMobile ? '20px' : '40px' }} />
            {currentDates.map((_, idx) => (
              <col
                key={idx}
                data-testid={`col-${idx}`}
                style={{
                  width: getColumnWidth(),
                }}
              />
            ))}
            <col style={{ width: isMobile ? '20px' : '40px' }} />
          </colgroup>
          <TableHeader
            currentDates={currentDates}
            sortedByDate={sortedByDate}
            handleDateSort={handleDateSort}
            handleScrollLeft={handleScrollLeft}
            handleScrollRight={handleScrollRight}
            isScrollLeftDisabled={isScrollLeftDisabled}
            isScrollRightDisabled={isScrollRightDisabled}
            isLoading={isLoading}
            isMobile={isMobile}
            isSmallerThanSm={isSmallerThanSm}
            handleSortToggle={handleSortToggle}
            locale={locale}
          />
        </Table>
      </Box>
    </Box>
  </Box>
);

// Component styles
const styles = {
  motionContainer: {
    position: 'fixed' as const,
    top: 0,
    left: 0,
    width: '100%',
    zIndex: 100,
    background: THEME_COLORS.floatingHeaderBg,
    boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
    padding: 0,
    backdropFilter: 'blur(8px)',
  },
  outerContainer: {
    maxW: {
      base: '100%',
      xs: '100%',
      sm: 'calc(100% - 30px)',
      md: 'calc(100% - 40px)',
      lg: 'calc(100% - 100px)',
      xl: '1217px',
    },
    mx: 'auto',
    px: 0,
  },
  innerContainer: {
    w: '100%',
    mx: 'auto',
    px: { base: 0, md: 4, xl: 0 },
  },
  table: {
    variant: 'simple' as const,
    size: 'sm' as const,
    role: 'presentation' as const,
  },
  hotelNameCol: {
    width: '40%',
  },
};
