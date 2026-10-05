import { ChevronLeftIcon, ChevronRightIcon, ArrowDownIcon } from '@chakra-ui/icons';
import { Box, Thead, Tr, Th, IconButton, Icon } from '@chakra-ui/react';
import { useTranslation } from 'next-i18next';
import React from 'react';

import { THEME_COLORS } from '../constants';
import { formatDate, isToday } from '../helpers';
import { TableHeaderProps } from '../types';

export const TableHeader: React.FC<TableHeaderProps> = ({
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
  tableHeaderRef,
  handleSortToggle,
  locale = 'en-GB',
}) => {
  const { t } = useTranslation();

  return (
    <Thead {...styles.thead} data-testid="hotel-price-table-header" ref={tableHeaderRef}>
      <Tr data-testid="hotel-price-table-header-row-dates">
        {!isMobile && (
          <Th {...styles.hotelNameHeader} data-testid="hotel-name-header">
            {handleSortToggle && (
              <Box
                {...styles.desktopSortButton}
                as="button"
                onClick={handleSortToggle}
                color={THEME_COLORS.sortTextColor}
              >
                <>
                  <Box {...styles.sortText} as="span">
                    {t('priceFinder.sortByDistance')}
                  </Box>
                  <Icon
                    aria-label="Sorted by distance ascending"
                    color={sortedByDate ? THEME_COLORS.disabledColor : THEME_COLORS.primaryColor}
                    as={ArrowDownIcon}
                    boxSize={4}
                  />
                </>
              </Box>
            )}
          </Th>
        )}
        <Th
          {...styles.scrollHeader}
          w={isMobile ? '24px' : '40px'}
          data-testid="scroll-left-header"
        >
          {!isScrollLeftDisabled && (
            <IconButton
              {...styles.scrollButton}
              data-testid="scroll-left-button"
              aria-label="View previous 7 days"
              icon={<ChevronLeftIcon boxSize="6" />}
              onClick={handleScrollLeft}
              size="xs"
              variant="ghost"
              isDisabled={isLoading}
            />
          )}
        </Th>
        {currentDates.map((date) => {
          const { weekday, day, month } = formatDate(date, {
            isSmallerThanSm,
            locale,
          });
          const isCurrentSort = sortedByDate === date;
          const isTodayDate = isToday(date);
          return (
            <Th
              {...styles.dateHeader}
              key={date}
              data-testid={`date-header-${date}`}
              onClick={() => handleDateSort(date)}
              onKeyDown={(e) => {
                if (e.key === 'Enter' || e.key === ' ') {
                  e.preventDefault();
                  handleDateSort(date);
                }
              }}
              bg={isCurrentSort ? THEME_COLORS.sortedBg : isTodayDate ? 'blue.50' : 'transparent'}
              _hover={{
                bg: isCurrentSort ? THEME_COLORS.sortedHoverBg : THEME_COLORS.hoverBg,
              }}
              borderBottom={
                isCurrentSort
                  ? '2px solid ' + THEME_COLORS.primaryColor
                  : '1px solid ' + THEME_COLORS.borderColor
              }
              aria-label={`Sort by price for ${weekday} ${day}${
                isCurrentSort ? ' (currently sorted)' : ''
              }`}
              minW={isMobile ? '28px' : '82.55px'}
              w={isMobile ? '28px' : 'full'}
              fontSize={{ base: 'xs', md: 'sm' }}
            >
              <Box
                {...styles.weekdayText}
                color={isCurrentSort ? THEME_COLORS.primaryColor : '#58595B'}
                sx={{
                  'th:hover &': {
                    color: THEME_COLORS.primaryColor,
                  },
                }}
              >
                {weekday}
              </Box>
              <Box
                {...(isMobile ? styles.dayContainerMobile : styles.dayContainerDesktop)}
                color={isCurrentSort ? THEME_COLORS.primaryColor : 'black'}
                sx={{
                  'th:hover &': {
                    color: THEME_COLORS.primaryColor,
                  },
                }}
              >
                {
                  <Box {...styles.dayNumber} {...(month && { ...styles.monthText })}>
                    {month ? `${day} ${month}` : day}
                  </Box>
                }
                <Icon
                  aria-label="Sorted by price ascending"
                  color={isCurrentSort ? THEME_COLORS.primaryColor : THEME_COLORS.borderColor}
                  as={ArrowDownIcon}
                  boxSize={4}
                  sx={{
                    'th:hover &': {
                      color: THEME_COLORS.primaryColor,
                    },
                  }}
                />
              </Box>
              {isTodayDate && <Box {...styles.todayIndicator} />}
            </Th>
          );
        })}
        <Th
          {...styles.scrollHeader}
          w={isMobile ? '24px' : '40px'}
          data-testid="scroll-right-header"
        >
          {!isScrollRightDisabled && (
            <IconButton
              {...styles.scrollButton}
              data-testid="scroll-right-button"
              aria-label="View next 7 days"
              icon={<ChevronRightIcon boxSize="6" />}
              onClick={handleScrollRight}
              size="xs"
              variant="ghost"
              isDisabled={isLoading}
            />
          )}
        </Th>
      </Tr>
    </Thead>
  );
};

// Component styles
const styles = {
  thead: {
    bg: THEME_COLORS.headerBg,
    borderColor: THEME_COLORS.borderColor,
  },
  hotelNameHeader: {
    scope: 'col' as const,
    w: '40%',
    borderColor: THEME_COLORS.borderColor,
    position: 'relative' as const,
    textAlign: 'center' as const,
    verticalAlign: 'middle' as const,
    fontFamily: 'body',
    paddingLeft: '2rem',
  },
  scrollHeader: {
    p: 0,
    borderColor: THEME_COLORS.borderColor,
  },
  dateHeader: {
    scope: 'col' as const,
    textAlign: 'center' as const,
    cursor: 'pointer' as const,
    transition: 'all 0.2s ease',
    tabIndex: 0,
    role: 'button' as const,
    position: 'relative' as const,
    borderColor: THEME_COLORS.borderColor,
    textTransform: 'capitalize' as const,
    fontFamily: 'body',
    padding: '15px 0',
  },
  weekdayText: {
    fontSize: { base: '13px', md: '14px' },
    fontWeight: { base: 400, md: 600 },
    fontFamily: 'body',
    lineHeight: '1',
  },
  dayContainerMobile: {
    display: 'flex',
    flexDirection: 'column' as const,
    alignItems: 'center',
    justifyContent: 'center',
    gap: 0,
    fontWeight: 600,
    textAlign: 'center' as const,
    minHeight: '30px',
    fontFamily: 'body',
  },
  dayContainerDesktop: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: 1,
    fontWeight: 600,
    textAlign: 'center' as const,
    fontFamily: 'body',
    flexDirection: 'row' as const,
  },
  dayNumber: {
    fontSize: {
      base: '13px',
      xs: '16px',
    },
    lineHeight: '1.2',
    fontFamily: 'body',
    height: {
      mobile: '20px',
      sm: '32px',
    },
    alignContent: 'center',
  },
  monthText: {
    fontSize: { base: '13px', xs: '16px' },
    lineHeight: { base: '1.2' },
    fontWeight: 600,
  },
  sortArrow: {
    fontSize: { base: 'xs', md: 'md' },
    fontWeight: 'bold',
    fontFamily: 'system-ui',
  },
  todayIndicator: {
    position: 'absolute' as const,
    top: '2px',
    right: '2px',
    w: '6px',
    h: '6px',
    bg: 'blue.500',
    borderRadius: 'full',
  },
  desktopSortButton: {
    fontWeight: 500,
    fontSize: '13px',
    cursor: 'pointer',
    bg: 'transparent',
    border: 'none',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: 1,
    mx: '0',
    lineHeight: '1.5',
    fontFamily: 'body',
    _hover: {
      textDecoration: 'none',
    },
  },
  sortText: {
    textDecoration: 'underline',
    fontSize: '14px',
    fontWeight: 500,
  },
  sortArrowIcon: {
    color: THEME_COLORS.primaryColor,
    fontSize: 'sm',
    fontFamily: 'system-ui',
  },
  scrollButton: {
    transition: 'all 0.2s ease',
    borderRadius: 'full',
    color: '#58595B',
    textAlign: 'center' as const,
    height: {
      mobile: '22px',
      sm: '24px',
      md: '32px',
    },
    width: {
      mobile: '22px',
      sm: '24px',
      md: '32px',
    },
    _hover: {
      bg: {
        base: 'none',
        sm: THEME_COLORS.primaryColor,
      },
      color: {
        base: 'none',
        sm: 'white',
      },
      borderRadius: 'full',
    },
  },
  disabledScrollButton: {
    opacity: 0.4,
    transition: 'all 0.2s ease',
    borderRadius: 'full',
    _hover: {},
  },
};
