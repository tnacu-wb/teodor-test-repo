import { Box, Checkbox, Flex, FlexProps } from '@chakra-ui/react';
import { getStaticContent, HeaderInformationQuery } from '@whitbread-eos/api';
import { useQueryRequest } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import React, { ChangeEvent } from 'react';

import { THEME_COLORS } from '../HotelsPriceTable/constants';

interface FilterType {
  roomType: string;
  name?: string;
}

type FilterSelection = Record<string, boolean>;

interface Props {
  locale: string;
  channel: string;
  isBB: boolean;
  isLoading: boolean;
  filterSelection: FilterSelection;
  setFilterSelection: React.Dispatch<React.SetStateAction<FilterSelection>>;
}

export default function PriceFinderRoomTypeFilter({
  locale,
  channel,
  isBB,
  isLoading,
  filterSelection,
  setFilterSelection,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const query = getStaticContent(true);
  const { data: headerFooterData }: HeaderInformationQuery = useQueryRequest(
    ['GetStaticContent', locale.split('-')[0], locale.split('-')[1]],
    query,
    {
      country: locale.split('-')[1].toLowerCase(),
      language: locale.split('-')[0].toLowerCase(),
      site: channel,
      businessBooker: isBB,
    }
  );

  const { headerInformation } = headerFooterData || {};

  const filterTypes: FilterType[] = [
    { roomType: 'DB', name: headerInformation?.content?.global?.double },
    { roomType: 'TWIN', name: headerInformation?.content?.global?.twin },
    { roomType: 'FAM', name: headerInformation?.content?.global?.family },
    { roomType: 'DIS', name: headerInformation?.content?.global?.accessible },
  ];

  const clearFilters = () => {
    setFilterSelection({
      DB: false,
      TWIN: false,
      FAM: false,
      DIS: false,
    });
  };

  const handleFilterSelection = (event: ChangeEvent<HTMLInputElement>, roomType: string) => {
    setFilterSelection((current: FilterSelection) => {
      const updatedSelection = { ...current };
      if (updatedSelection[roomType] !== undefined) {
        updatedSelection[roomType] = event.target.checked;
      }

      return updatedSelection;
    });
  };

  return (
    <Flex {...styles.filterMenu}>
      <Box {...styles.scroll}>
        {filterTypes.map((filter: FilterType) => (
          <Box
            key={filter.roomType}
            {...styles.filterItem}
            {...(filterSelection[filter.roomType] ? { ...styles.filterItemChecked } : '')}
          >
            <Checkbox
              {...styles.filterItemCheckbox}
              isChecked={!!filterSelection[filter.roomType]}
              onChange={(e) => handleFilterSelection(e, filter.roomType)}
              disabled={isLoading}
            >
              <Box {...styles.filterLabel}>{filter.name}</Box>
            </Checkbox>
          </Box>
        ))}
      </Box>
      <Box onClick={clearFilters} {...styles.filterClear}>
        {t('tableFilter.clearAll')}
      </Box>
    </Flex>
  );
}

const styles = {
  filterMenu: {
    width: '288px',
    border: 'solid 1px #dddddd',
    borderRadius: '5px',
    flexDirection: 'column',
    zIndex: 10,
    backgroundColor: THEME_COLORS.headerBg,
    position: 'absolute',
    marginTop: {
      md: '10px',
    },
    left: {
      base: 'auto',
      md: '15px',
      lg: '0',
    },
    right: {
      base: '16px',
      md: '5px',
    },
    boxShadow: '0px 0px 8px 0px rgba(0,0,0,0.2)',
  } as FlexProps,
  filterItem: {
    padding: '8px 15px',
    borderRadius: '5px',
    _first: {
      borderRadius: '5px 0 0 5px',
    },
  },
  filterItemChecked: {
    backgroundColor: THEME_COLORS.primaryRowBg,
    fontWeight: 500,
    borderRadius: '0',
  },
  filterItemCheckbox: {
    width: 'full',
  },
  filterLabel: {
    fontSize: '14px',
  },
  filterClear: {
    padding: '8px 15px',
    color: THEME_COLORS.sortTextColor,
    textDecoration: 'underline',
    textUnderlineOffset: '3px',
    cursor: 'pointer',
    fontSize: '14px',
  },
  scroll: {
    maxH: '200px',
    overflow: 'auto',
  },
};
