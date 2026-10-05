import { ArrowDownIcon } from '@chakra-ui/icons';
import { Box, Flex, FlexProps, Icon, useMediaQuery } from '@chakra-ui/react';
import { ChevronUp24, ChevronDown24 } from '@whitbread-eos/atoms';
import { formatDataTestId } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import React from 'react';

import { SORT_BY_VALUES, THEME_COLORS } from '../HotelsPriceTable/constants';
import { SortBy } from '../HotelsPriceTable/types';

type FilterSelection = Record<string, boolean>;

interface Props {
  sortedByDate: string | null;
  handleSortChange: (sortBy: SortBy) => void;
  handleToggleFilterMenu: () => void;
  isMobile: boolean;
  toggleFilterMenu: boolean;
  baseDataTestId: string;
  filterSelection: FilterSelection;
  isRoomTypeFilterEnabled?: boolean;
}

export default function PriceFinderRoomSortToggle({
  sortedByDate,
  handleSortChange,
  isMobile,
  toggleFilterMenu,
  baseDataTestId,
  handleToggleFilterMenu,
  filterSelection,
  isRoomTypeFilterEnabled,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const [isSmallMobile] = useMediaQuery('(max-width: 390px)');
  const numberOfFiltersSelected = Object.values(filterSelection).filter(Boolean).length;

  return (
    <>
      {isMobile && (
        <Flex {...styles.mobileSortWrapper}>
          <Box
            {...styles.mobileSortButton}
            as="button"
            onClick={() => handleSortChange(SORT_BY_VALUES.DISTANCE as SortBy)}
            color={THEME_COLORS.sortTextColor}
          >
            <Box as="span" textDecoration="underline">
              {t('priceFinder.sortByDistance')}
            </Box>
            <Icon
              aria-label="Sorted by distance ascending"
              color={sortedByDate ? THEME_COLORS.disabledColor : THEME_COLORS.sortTextColor}
              as={ArrowDownIcon}
              boxSize={4}
            />
          </Box>
          {isRoomTypeFilterEnabled && (
            <Box
              {...styles.mobileSortButton}
              {...styles.mobileRoomTypeFiltersButton}
              as="button"
              onClick={handleToggleFilterMenu}
              color={THEME_COLORS.sortTextColor}
            >
              <Box as="span" textDecoration="underline">
                {isSmallMobile
                  ? t('priceFinder.filterByRoomTypesMobile')
                  : t('priceFinder.filterByRoomTypes')}
              </Box>
              {numberOfFiltersSelected !== 0 ? `(${numberOfFiltersSelected})` : ''}
              {toggleFilterMenu ? (
                <ChevronUp24 data-testid={formatDataTestId(baseDataTestId, 'chevronUp')} />
              ) : (
                <ChevronDown24 data-testid={formatDataTestId(baseDataTestId, 'chevronDown')} />
              )}
            </Box>
          )}
        </Flex>
      )}

      {!isMobile && isRoomTypeFilterEnabled && (
        <Flex {...styles.filterMenuWrapper}>
          <Box onClick={handleToggleFilterMenu} {...styles.filterToggle} {...styles.filterPointer}>
            {t('priceFinder.filterByRoomTypes')}
          </Box>
          {numberOfFiltersSelected ? `(${numberOfFiltersSelected})` : ''}
          <Box {...styles.filterPointer} onClick={handleToggleFilterMenu}>
            {toggleFilterMenu ? (
              <ChevronUp24 data-testid={formatDataTestId(baseDataTestId, 'chevronUp')} />
            ) : (
              <ChevronDown24 data-testid={formatDataTestId(baseDataTestId, 'chevronDown')} />
            )}
          </Box>
        </Flex>
      )}
    </>
  );
}

const styles = {
  mobileSortWrapper: {
    w: 'full',
    backgroundColor: 'white',
    py: 3,
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
    width: '45%',
    _hover: {
      color: THEME_COLORS.sortTextColor,
    },
  },
  filterMenuWrapper: {
    maxW: '1226px',
    position: 'relative',
    alignItems: 'center',
    mt: '16px',
  } as FlexProps,
  filterToggle: {
    color: THEME_COLORS.sortTextColor,
    textDecoration: 'underline',
    textUnderlineOffset: '3px',
    marginRight: '6px',
  },
  filterPointer: {
    cursor: 'pointer',
  },
  mobileRoomTypeFiltersButton: {
    justifyContent: 'flex-end',
    textAlign: 'right' as const,
  },
};
