import { Flex, Text, TextProps } from '@chakra-ui/react';
import { SRHotelFilters, SelectedFilter } from '@whitbread-eos/api';
import { GroupItems } from '@whitbread-eos/api/dist/types/graphql';
import { Checkbox, Icon, Info } from '@whitbread-eos/atoms';
import { formatDataTestId } from '@whitbread-eos/utils';
import dynamic from 'next/dynamic';
import { ChangeEvent } from 'react';

const Tooltip = dynamic(
  async () => {
    const { Tooltip } = await import('@whitbread-eos/atoms');
    return { default: Tooltip };
  },
  {
    ssr: false,
  }
);

export function createCheckboxList(
  baseDataTestId: string,
  selectedFilters: string[] | SelectedFilter[],
  handleFilterChange: (e: ChangeEvent<HTMLInputElement>) => void,
  items: SRHotelFilters[] | GroupItems[],
  liftInfoMessage: string,
  isDlp = false,
  labelTypography: Partial<TextProps> = {}
) {
  function isSRHotelFilter(item: SRHotelFilters | GroupItems): item is SRHotelFilters {
    return 'code' in item;
  }
  const LIFT_ACCESS_CODE = isDlp ? 'LFT,HUL' : 'LFT';

  return items.map((item: SRHotelFilters | GroupItems) => {
    const itemCode = isSRHotelFilter(item) ? item.code : item.codes;
    const itemName = isSRHotelFilter(item) ? item.name : item.label;
    const selectedDlpFiltersCodes = (selectedFilters as SelectedFilter[]).map(
      (filter) => filter.codes
    );

    return (
      <Checkbox
        my="xs"
        key={itemCode}
        value={itemCode}
        aria-label={itemName}
        isChecked={
          isDlp
            ? selectedDlpFiltersCodes.some((filterCodes) => {
                if (filterCodes.length > 1) {
                  return filterCodes.some((filter: string) => {
                    return itemCode?.includes(filter);
                  });
                }
                return filterCodes[0] === itemCode;
              })
            : selectedFilters.some((filterCode) => filterCode === itemCode)
        }
        data-testid={formatDataTestId(baseDataTestId, `Filters-checkbox-${itemCode}`)}
        data-query-param={(item as GroupItems).queryParam ?? ''}
        onChange={handleFilterChange}
      >
        {itemCode === LIFT_ACCESS_CODE ? (
          <Flex direction="row" display="inline-flex">
            <Text {...labelTypography}>{itemName}</Text>
            <Tooltip
              description={liftInfoMessage}
              variant="facilities"
              placement="bottom-start"
              position="relative"
              alertElementStyles={alertStyles}
              data-testid={formatDataTestId(baseDataTestId, 'Filters-Lift-access-info-tooltip')}
              {...tooltipStyles}
            >
              <Text
                as="div"
                role="img"
                tabIndex={0}
                aria-label={liftInfoMessage}
                data-testid={formatDataTestId(baseDataTestId, 'Filters-Lift-access-info-icon')}
              >
                <Icon ml="sm" mt="xs" svg={<Info />} />
              </Text>
            </Tooltip>
          </Flex>
        ) : (
          <Text {...labelTypography}>{itemName}</Text>
        )}
      </Checkbox>
    );
  });
}

const alertStyles = {
  py: 'sm',
  padding: 0,
  w: '11.625rem',
  h: '6.125rem',
};

const tooltipStyles = {
  marginLeft: '-1rem',
  fontSize: 'sm',
  lineHeight: '2',
  paddingLeft: 0,
  fontWeight: 'normal',
  top: 'xs',
};
