import { Box, Tr, Td } from '@chakra-ui/react';
import React from 'react';

import { THEME_COLORS } from '../constants';
import { SkeletonTableRowProps } from '../types';

const SkeletonBox = ({
  width = '100%',
  height = '20px',
  borderRadius = '4px',
}: {
  width?: string;
  height?: string;
  borderRadius?: string;
}) => <Box width={width} height={height} borderRadius={borderRadius} {...styles.skeletonBox} />;

export const SkeletonTableRow: React.FC<SkeletonTableRowProps> = ({
  isMobile,
  currentDates,
  index = 0,
}) => {
  const hotelNameWidth = '75%'; // Fixed width for hotel names
  const priceWidth = '75%'; // Fixed width for price columns

  return (
    <>
      {isMobile ? (
        <React.Fragment>
          {/* Mobile: Hotel name row */}
          <Tr
            bg={index % 2 === 0 ? THEME_COLORS.primaryRowBg : THEME_COLORS.alternateRowBg}
            {...styles.mobileNameRow}
          >
            <Td colSpan={currentDates.length + 2} {...styles.mobileNameCell}>
              <SkeletonBox width={hotelNameWidth} height="20px" />
            </Td>
          </Tr>
          {/* Mobile: Prices row - individual skeletons for each date */}
          <Tr
            bg={index % 2 === 0 ? THEME_COLORS.primaryRowBg : THEME_COLORS.alternateRowBg}
            {...styles.mobilePricesRow}
          >
            <Td data-testid={`skeleton-spacer-left-${index}`} {...styles.spacerCell} />
            {currentDates.map((_, idx) => (
              <Td key={idx} data-testid={`Td-${idx}`} {...styles.mobilePriceCell}>
                <SkeletonBox width={priceWidth} height="16px" borderRadius="4px" />
              </Td>
            ))}
            <Td data-testid={`skeleton-spacer-right-${index}`} {...styles.spacerCell} />
          </Tr>
        </React.Fragment>
      ) : (
        <Tr
          bg={index % 2 === 0 ? THEME_COLORS.primaryRowBg : THEME_COLORS.alternateRowBg}
          {...styles.desktopRow}
        >
          <Td data-testid={`skeleton-hotel-name-${index}`} {...styles.desktopNameCell}>
            <SkeletonBox width={hotelNameWidth} height="20px" />
          </Td>
          <Td data-testid={`skeleton-spacer-left-${index}`} />
          {currentDates.map((_, idx) => (
            <Td key={idx} data-testid={`Td-${idx}`} {...styles.desktopPriceCell}>
              <SkeletonBox width={priceWidth} height="16px" borderRadius="4px" />
            </Td>
          ))}
          <Td data-testid={`skeleton-spacer-right-${index}`} />
        </Tr>
      )}
    </>
  );
};

// Component styles
const styles = {
  skeletonBox: {
    bg: '#EEEEEE',
    position: 'relative' as const,
    overflow: 'hidden',
    _before: {
      content: '""',
      position: 'absolute',
      top: 0,
      left: '-100%',
      width: '100%',
      height: '100%',
      background: 'linear-gradient(90deg, transparent, rgba(255,255,255,0.6), transparent)',
      animation: 'shimmer 1.8s infinite ease-in-out',
    },
    sx: {
      '@keyframes shimmer': {
        '0%': { left: '-100%' },
        '50%': { left: '100%' },
        '100%': { left: '100%' },
      },
    },
  },
  mobileNameRow: {
    sx: {
      borderBottom: 'none',
    },
  },
  mobileNameCell: {
    fontWeight: 'semibold',
    fontSize: { base: 'sm', md: 'md' },
    textAlign: 'left' as const,
    px: 2,
    py: 1,
    sx: {
      overflow: 'hidden',
      whiteSpace: 'nowrap',
      textOverflow: 'ellipsis',
      borderTopLeftRadius: '8px',
      borderTopRightRadius: '8px',
      borderBottom: 'none',
    },
  },
  mobilePricesRow: {
    sx: {
      borderTop: 'none',
      borderBottomLeftRadius: '8px',
      borderBottomRightRadius: '8px',
    },
  },
  mobilePriceCell: {
    textAlign: 'center' as const,
    sx: { borderTop: 'none' },
    minW: '34px',
    w: '44px',
    fontSize: { base: 'xs', md: 'sm' },
    p: { base: '2px', md: '2' },
  },
  spacerCell: {
    sx: { borderTop: 'none' },
  },
  desktopRow: {
    transition: 'background-color 0.2s ease',
  },
  desktopNameCell: {
    fontWeight: 'semibold',
    scope: 'row' as const,
    fontSize: { base: 'xs', md: 'sm' },
    w: '50%',
    sx: {
      overflow: 'hidden',
      whiteSpace: 'nowrap',
      textOverflow: 'ellipsis',
    },
  },
  desktopPriceCell: {
    textAlign: 'center' as const,
    minW: 'calc(50% / 7)',
    w: 'calc(50% / 7)',
    fontSize: { base: 'xs', md: 'sm' },
  },
};
