import { ChevronLeftIcon, ChevronRightIcon } from '@chakra-ui/icons';
import { Box, Button, Flex, useMediaQuery } from '@chakra-ui/react';
import React from 'react';

const PAGINATION_COLORS = {
  primaryColor: '#00798E',
  borderColor: '#CCCCCC',
  disabledColor: '#9CA3AF',
  chevronColor: '#58595B',
} as const;

const PAGINATION_CONFIG = {
  // Visibility and layout settings
  MAX_PAGES_WITHOUT_ELLIPSIS: 6,
  MIN_PAGES_FOR_DISPLAY: 1,
  PAGES_THRESHOLD_FOR_SIMPLE_DISPLAY: 6,

  EARLY_PAGE_THRESHOLD: 4,
  LATE_PAGE_THRESHOLD: 3,

  // Number of pages to show in different sections
  PAGES_IN_START_GROUP: 5, // 1, 2, 3, 4, 5
  PAGES_IN_END_GROUP: 5, // last-4, last-3, last-2, last-1, last
  PAGES_AROUND_CURRENT: 1, // current-1, current, current+1
  PAGES_FROM_END_FOR_CONTEXT: 4,
} as const;

const MOBILE_PAGINATION_CONFIG = {
  // Mobile view configuration (≤375px)
  MAX_PAGES_WITHOUT_ELLIPSIS: 6,
  PAGES_THRESHOLD_FOR_SIMPLE_DISPLAY: 6, // Show all pages if 6 or fewer
  EARLY_PAGE_THRESHOLD: 3,
  LATE_PAGE_THRESHOLD: 2,
  // Edge case rules from design
  MAX_PAGES_LIMIT: 6, // Never show more than 6 pages total
} as const;

export interface PaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
  isDisabled?: boolean;
}

export const Pagination: React.FC<PaginationProps> = ({
  currentPage,
  totalPages,
  onPageChange,
  isDisabled = false,
}) => {
  const [isExtraSmall] = useMediaQuery('(max-width: 375px)');

  if (totalPages <= PAGINATION_CONFIG.MIN_PAGES_FOR_DISPLAY) return null;

  const getVisiblePages = () => {
    const pages: (number | string)[] = [];

    // For extra small screens (375px or smaller) with specific edge case rules
    if (isExtraSmall) {
      // Rule: 6 pages or fewer - show all pages without ellipsis
      if (totalPages <= MOBILE_PAGINATION_CONFIG.PAGES_THRESHOLD_FOR_SIMPLE_DISPLAY) {
        for (let i = 1; i <= totalPages; i++) {
          pages.push(i);
        }
      }
      // Rule: More than 6 pages - apply ellipsis with smart truncation
      else {
        if (currentPage <= MOBILE_PAGINATION_CONFIG.EARLY_PAGE_THRESHOLD) {
          // Early pages pattern: show up to current page + 1, then ellipsis
          for (let i = 1; i <= Math.min(currentPage + 1, totalPages - 1); i++) {
            pages.push(i);
          }
          if (currentPage + 1 < totalPages) {
            pages.push('...');
          }
          pages.push(totalPages);
        } else if (currentPage > totalPages - MOBILE_PAGINATION_CONFIG.LATE_PAGE_THRESHOLD) {
          // Near end pattern: 1, ..., last-2, last-1, last
          pages.push(1);
          pages.push('...');
          pages.push(totalPages - 2, totalPages - 1, totalPages);
        } else {
          // Middle page pattern: 1, ..., current-1, current, current+1, ..., last
          pages.push(1);
          pages.push('...');
          pages.push(currentPage - 1, currentPage, currentPage + 1);
          pages.push('...');
          pages.push(totalPages);
        }
      }
    } else if (totalPages <= PAGINATION_CONFIG.PAGES_THRESHOLD_FOR_SIMPLE_DISPLAY) {
      // Show all pages if threshold or fewer
      for (let i = 1; i <= totalPages; i++) {
        pages.push(i);
      }
    } else {
      // Always show first page
      pages.push(1);

      if (currentPage <= PAGINATION_CONFIG.EARLY_PAGE_THRESHOLD) {
        // Show pages 1, 2, 3, 4, 5, ..., last
        pages.push(2, 3, 4, PAGINATION_CONFIG.PAGES_IN_START_GROUP);
        if (totalPages > PAGINATION_CONFIG.MAX_PAGES_WITHOUT_ELLIPSIS) {
          pages.push('...');
        }
        pages.push(totalPages);
      } else if (currentPage >= totalPages - PAGINATION_CONFIG.LATE_PAGE_THRESHOLD) {
        // Show pages 1, ..., last-3, last-2, last-1, last
        if (totalPages > PAGINATION_CONFIG.PAGES_IN_END_GROUP) {
          pages.push('...');
        }
        pages.push(
          totalPages - PAGINATION_CONFIG.PAGES_FROM_END_FOR_CONTEXT,
          totalPages - 3,
          totalPages - 2,
          totalPages - 1,
          totalPages
        );
      } else {
        // Show pages 1, ..., current-1, current, current+1, ..., last
        pages.push('...');
        pages.push(
          currentPage - PAGINATION_CONFIG.PAGES_AROUND_CURRENT,
          currentPage,
          currentPage + PAGINATION_CONFIG.PAGES_AROUND_CURRENT
        );
        pages.push('...');
        pages.push(totalPages);
      }
    }

    return pages;
  };

  const visiblePages = getVisiblePages();

  return (
    <Flex {...styles.container} data-testid="pagination">
      {/* Previous Button */}
      <Button
        {...styles.navButton}
        isDisabled={currentPage === 1 || isDisabled}
        onClick={() => !isDisabled && onPageChange(currentPage - 1)}
        data-testid="pagination-prev"
        aria-label="Go to previous page"
      >
        <ChevronLeftIcon {...styles.icon} />
      </Button>

      {/* Page Numbers */}
      {visiblePages.map((page, index) => {
        if (page === '...') {
          return (
            <Box key={`ellipsis-${index}`} {...styles.ellipsis} data-testid="pagination-ellipsis">
              ...
            </Box>
          );
        }

        const pageNumber = page as number;
        const isActive = pageNumber === currentPage;

        return (
          <Button
            key={pageNumber}
            {...styles.pageButton}
            {...(isActive ? styles.activePageButton : {})}
            isDisabled={isDisabled}
            onClick={() => !isDisabled && onPageChange(pageNumber)}
            data-testid={`pagination-page-${pageNumber}`}
            aria-label={`Go to page ${pageNumber}`}
            aria-current={isActive ? 'page' : undefined}
          >
            {pageNumber}
          </Button>
        );
      })}

      {/* Next Button */}
      <Button
        {...styles.navButton}
        isDisabled={currentPage === totalPages || isDisabled}
        onClick={() => !isDisabled && onPageChange(currentPage + 1)}
        data-testid="pagination-next"
        aria-label="Go to next page"
      >
        <ChevronRightIcon {...styles.icon} />
      </Button>
    </Flex>
  );
};

const styles = {
  container: {
    justify: 'center',
    align: 'center',
    gap: 1,
    py: 4,
    px: 2,
    minW: '320px',
    transition: 'all 0.2s ease-in-out',
  },
  navButton: {
    size: 'sm',
    variant: 'outline',
    borderColor: PAGINATION_COLORS.borderColor,
    color: PAGINATION_COLORS.chevronColor,
    bg: 'white',
    w: '40px',
    h: '40px',
    minW: 'unset',
    fontSize: 'sm',
    p: 0,
    transition: 'all 0.15s ease-in-out',
    _hover: {
      bg: PAGINATION_COLORS.primaryColor,
      color: 'white',
      borderColor: PAGINATION_COLORS.primaryColor,
    },
    _disabled: {
      opacity: 0.4,
      cursor: 'not-allowed',
      _hover: {
        bg: 'white',
        color: PAGINATION_COLORS.disabledColor,
        borderColor: PAGINATION_COLORS.borderColor,
      },
    },
  },
  pageButton: {
    size: 'sm',
    variant: 'outline',
    borderColor: PAGINATION_COLORS.borderColor,
    color: 'black',
    bg: 'white',
    w: {
      base: '30px',
      sm: '40px',
    },
    h: {
      base: '38px',
      sm: '40px',
    },
    minW: 'unset',
    fontSize: 'sm',
    fontWeight: 'normal',
    p: 0,
    transition: 'all 0.15s ease-in-out',
    _hover: {
      bg: PAGINATION_COLORS.primaryColor,
      color: 'white',
      borderColor: PAGINATION_COLORS.primaryColor,
    },
  },
  activePageButton: {
    bg: PAGINATION_COLORS.primaryColor,
    color: 'white',
    borderColor: PAGINATION_COLORS.primaryColor,
    fontWeight: 'bold',
    _hover: {
      bg: PAGINATION_COLORS.primaryColor,
      color: 'white',
      borderColor: PAGINATION_COLORS.primaryColor,
    },
  },
  ellipsis: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    w: {
      base: '15px',
      sm: '40px',
    },
    h: {
      base: '38px',
      sm: '40px',
    },
    border: {
      base: 'none',
      sm: '1px solid #CCCCCC',
    },
    bg: 'white',
    fontWeight: 'bold',
    fontSize: 'sm',
    cursor: 'default',
    userSelect: 'none' as const,
    borderRadius: 'md',
  },
  icon: {
    width: '1.8em',
    height: '1.8em',
  },
};
