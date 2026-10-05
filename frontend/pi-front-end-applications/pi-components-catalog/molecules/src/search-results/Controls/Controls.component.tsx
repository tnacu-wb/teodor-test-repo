import type { FlexboxProps, FlexProps, IconProps } from '@chakra-ui/react';
import { Flex, SimpleGrid, Box, Icon, Text } from '@chakra-ui/react';
import {
  FilterByLabels,
  FT_SRP_DYNAMIC_FILTERS,
  SelectedFilter,
  SRFiltersType,
  SrpFilter,
  SrpMenu,
} from '@whitbread-eos/api';
import {
  Button,
  ButtonProps,
  Location as MapPinIcon,
  Filter as HorizontalFilter,
  Dismiss,
  Icon as IconComponent,
} from '@whitbread-eos/atoms';
import {
  useScreenSize,
  useMobileControlsDisplay,
  useFeatureToggle,
  useSemanticTypography,
} from '@whitbread-eos/utils';
import { List as ListIcon } from 'lucide-react';
import { useTranslation } from 'next-i18next';
import { useState } from 'react';
import type { ReactNode } from 'react';

import { DestinationFilters } from '../../destination';
import Filters from '../Filters';
import SortBy from '../SortBy';

interface Props {
  viewType: string | string[];
  onChangeViewType: () => void;
  onChangeSortValue: (value: any) => void;
  onChangeFilters: (params: SRFiltersType) => void;
  labels: {
    buttonLabels: SrpMenu;
    filterLabels: SrpFilter;
  };
  sortValue: string;
  defaultFilters: SRFiltersType;
  visibility?: {
    isFilterDisabled?: boolean;
    isSortDisabled?: boolean;
  };
  isPiSortOrderDropdownEnabled?: boolean;
  isBbSortOrderDropdownEnabled?: boolean;
  channel?: string;
  dynamicFilters?: FilterByLabels;
  hideViewToggle?: boolean;
  rightContent?: ReactNode;
  isSplitView?: boolean;
}

export const VIEW_TYPE_CONSTANTS = {
  mapView: '1',
  listView: '2',
};

interface ControlsDisplaySectionProps {
  viewType: string | string[];
  onChangeViewType: () => void;
  onChangeSortValue: (value: any) => void;
  onChangeFilters: (params: SRFiltersType) => void;
  buttonLabels: SrpMenu;
  filterByLabels: {
    filterByButton: string;
    filters: SrpFilter;
  };
  sortByLabels: {
    sortByRecommended: string;
    sortByDistance: string;
    sortByPrice: string;
  };
  sortValue: string;
  defaultFilters: SRFiltersType;
  visibility?: {
    isFilterDisabled?: boolean;
    isSortDisabled?: boolean;
  };
  isPiSortOrderDropdownEnabled?: boolean;
  isBbSortOrderDropdownEnabled?: boolean;
  hideViewToggle?: boolean;
  showDynamicFilters: boolean;
  dynamicFilters?: FilterByLabels;
  selectedFilters: SelectedFilter[];
  setSelectedFilters: (selectedFilters: SelectedFilter[]) => void;
  clearAllLabel: string;
}

export function ControlsDisplaySection({
  viewType,
  onChangeViewType,
  onChangeSortValue,
  onChangeFilters,
  buttonLabels,
  filterByLabels,
  sortByLabels,
  defaultFilters,
  sortValue,
  visibility,
  isPiSortOrderDropdownEnabled,
  isBbSortOrderDropdownEnabled,
  hideViewToggle,
  showDynamicFilters,
  dynamicFilters,
  selectedFilters,
  setSelectedFilters,
  clearAllLabel,
}: Readonly<ControlsDisplaySectionProps>) {
  const getTypographyProps = useSemanticTypography();
  const genericSecondaryLegacyTypography = {};
  const genericSecondarySemanticTypography = { textStyle: 'body-m-regular' };
  const genericSecondaryTypography = getTypographyProps(
    genericSecondaryLegacyTypography,
    genericSecondarySemanticTypography
  );
  const computedSharedButtonProps = {
    ...sharedButtonPropsLayout,
    ...getTypographyProps(sharedButtonLegacyTypography, sharedButtonSemanticTypography),
  } as Partial<ButtonProps>;
  const isSortVisible = viewType === VIEW_TYPE_CONSTANTS.listView;

  return (
    <SimpleGrid {...getControlsGridStyles(isSortVisible, !hideViewToggle)}>
      <Box sx={fullWidthChild}>
        {showDynamicFilters && dynamicFilters ? (
          <Box mr={{ base: 'md', lg: 'lg' }}>
            <DestinationFilters
              selectedFilters={selectedFilters}
              setSelectedFilters={(selectedFilters) => {
                setSelectedFilters(selectedFilters);
                const filters = selectedFilters.reduce((prev, curr) => {
                  return [...prev, ...(curr.codes ?? [])];
                }, [] as string[]);
                onChangeFilters(filters);
              }}
              labels={dynamicFilters}
              showClearFilters={false}
              leftIcon={<Icon as={HorizontalFilter} {...filterIconStyles} viewBox="0 0 16 16" />}
              buttonProps={computedSharedButtonProps}
            />
          </Box>
        ) : (
          <Filters
            onChangeFilters={onChangeFilters}
            defaultFilters={defaultFilters}
            isDisabled={!!visibility?.isFilterDisabled}
            labels={filterByLabels}
            leftIcon={<Icon as={HorizontalFilter} {...filterIconStyles} viewBox="0 0 16 16" />}
            buttonProps={computedSharedButtonProps}
          />
        )}
      </Box>
      {isSortVisible && (
        <Box sx={fullWidthChild}>
          <SortBy
            labels={sortByLabels}
            selectedOption={sortValue}
            isDisabled={!!visibility?.isSortDisabled}
            onChange={onChangeSortValue}
            isPiSortOrderDropdownEnabled={isPiSortOrderDropdownEnabled}
            isBbSortOrderDropdownEnabled={isBbSortOrderDropdownEnabled}
            buttonProps={computedSharedButtonProps}
          />
        </Box>
      )}
      {!hideViewToggle && (
        <Box sx={fullWidthChild}>
          <Button
            data-testid="controls-map-or-list-button"
            {...mapListButtonStyles}
            {...genericSecondaryTypography}
            onClick={onChangeViewType}
            leftIcon={
              viewType === VIEW_TYPE_CONSTANTS.listView ? (
                <Icon as={MapPinIcon} {...mapIconStyles} />
              ) : (
                <Icon as={ListIcon} {...listIconStyles} />
              )
            }
          >
            <Text as="span" {...genericSecondaryTypography}>
              {viewType === VIEW_TYPE_CONSTANTS.listView ? buttonLabels?.map : buttonLabels?.list}
            </Text>
          </Button>
        </Box>
      )}
      {renderSelectedDynamicFilters(
        !!showDynamicFilters,
        selectedFilters,
        viewType,
        setSelectedFilters,
        onChangeFilters,
        clearAllLabel
      )}
    </SimpleGrid>
  );
}

export default function Controls({
  viewType,
  onChangeViewType,
  onChangeSortValue,
  onChangeFilters,
  labels,
  dynamicFilters,
  sortValue,
  defaultFilters,
  visibility,
  isPiSortOrderDropdownEnabled = false,
  isBbSortOrderDropdownEnabled = false,
  channel,
  hideViewToggle = false,
  rightContent,
  isSplitView = false,
}: Readonly<Props>) {
  const { t } = useTranslation();
  const { isLessThanMd } = useScreenSize();
  const { buttonLabels, filterLabels } = labels;
  const controlsDisplay = useMobileControlsDisplay(channel);
  const { [FT_SRP_DYNAMIC_FILTERS]: isSrpDynamicFiltersEnabled } = useFeatureToggle();
  const getTypographyProps = useSemanticTypography();
  const genericSecondaryLegacyTypography = {};
  const genericSecondarySemanticTypography = { textStyle: 'body-m-regular' };
  const genericSecondaryTypography = getTypographyProps(
    genericSecondaryLegacyTypography,
    genericSecondarySemanticTypography
  );

  const showDynamicFilters =
    isSrpDynamicFiltersEnabled && dynamicFilters && !visibility?.isFilterDisabled;

  const sortByLabels = {
    sortByRecommended: buttonLabels?.recommended ?? '',
    sortByDistance: buttonLabels?.distance ?? '',
    sortByPrice: buttonLabels?.price ?? '',
  };
  const filterByLabels = {
    filterByButton: buttonLabels?.filtersLong ?? '',
    filters: filterLabels,
  };

  const [selectedFilters, setSelectedFilters] = useState<SelectedFilter[]>(
    getSelectedFilters(dynamicFilters, defaultFilters)
  );

  const mapViewStyles: FlexProps = showDynamicFilters
    ? { ...mapViewWrapperStyles, position: 'relative', ml: horizontalOffset, mr: 'auto' }
    : { ...mapViewWrapperStyles, position: 'absolute', zIndex: 2, left: horizontalOffset };

  if (controlsDisplay) {
    return (
      <ControlsDisplaySection
        viewType={viewType}
        onChangeViewType={onChangeViewType}
        onChangeSortValue={onChangeSortValue}
        onChangeFilters={onChangeFilters}
        filterByLabels={{ ...filterByLabels, filterByButton: buttonLabels?.filter ?? '' }}
        buttonLabels={buttonLabels}
        sortByLabels={sortByLabels}
        defaultFilters={defaultFilters}
        sortValue={sortValue}
        visibility={visibility}
        isPiSortOrderDropdownEnabled={true}
        isBbSortOrderDropdownEnabled={true}
        hideViewToggle={hideViewToggle}
        showDynamicFilters={!!showDynamicFilters}
        dynamicFilters={dynamicFilters}
        selectedFilters={selectedFilters}
        setSelectedFilters={setSelectedFilters}
        clearAllLabel={t('tableFilter.clearAll')}
      />
    );
  }

  return (
    <>
      <Flex
        {...wrapperStyles}
        {...(isSplitView ? splitViewWrapperStyles : {})}
        {...(viewType === VIEW_TYPE_CONSTANTS.mapView ? mapViewStyles : {})}
        mb={showDynamicFilters && selectedFilters.length > 0 ? '0' : 'var(--chakra-space-md)'}
        sx={isSplitView ? { '& > *': { margin: 0 } } : undefined}
      >
        <Flex
          mb={{ mobile: 'sm', md: '0' }}
          gap={isSplitView ? 'xlg' : undefined}
          sx={isSplitView ? { '& > *': { margin: 0 } } : undefined}
        >
          {showDynamicFilters ? (
            <Box mr={isSplitView ? 0 : { base: 'md', lg: 'lg' }}>
              <DestinationFilters
                selectedFilters={selectedFilters}
                setSelectedFilters={(selectedFilters) => {
                  setSelectedFilters(selectedFilters);
                  const filters = selectedFilters.reduce((prev, curr) => {
                    return [...prev, ...(curr.codes ?? [])];
                  }, [] as string[]);
                  onChangeFilters(filters);
                }}
                labels={dynamicFilters}
                showClearFilters={false}
              />
            </Box>
          ) : (
            <Filters
              onChangeFilters={onChangeFilters}
              defaultFilters={defaultFilters}
              isDisabled={visibility?.isFilterDisabled}
              labels={filterByLabels}
              buttonProps={genericSecondaryTypography as ButtonProps}
            />
          )}

          {viewType === VIEW_TYPE_CONSTANTS.listView && (
            <SortBy
              labels={sortByLabels}
              selectedOption={sortValue}
              isDisabled={!!visibility?.isSortDisabled}
              onChange={onChangeSortValue}
              isPiSortOrderDropdownEnabled={isPiSortOrderDropdownEnabled}
              isBbSortOrderDropdownEnabled={isBbSortOrderDropdownEnabled}
              buttonProps={genericSecondaryTypography as ButtonProps}
              isSplitView={isSplitView}
            />
          )}
        </Flex>
        {!isLessThanMd && !hideViewToggle && (
          <Button
            data-testid="controls-map-or-list-button"
            size="sm"
            variant="genericSecondary"
            onClick={onChangeViewType}
            {...buttonWidth}
            {...genericSecondaryTypography}
          >
            <Text as="span" {...genericSecondaryTypography}>
              {viewType === VIEW_TYPE_CONSTANTS.listView
                ? buttonLabels.mapLong
                : buttonLabels.listLong}
            </Text>
          </Button>
        )}
        {!isLessThanMd && hideViewToggle && rightContent}
      </Flex>
      {renderSelectedDynamicFilters(
        !!showDynamicFilters,
        selectedFilters,
        viewType,
        setSelectedFilters,
        onChangeFilters,
        t('tableFilter.clearAll')
      )}
    </>
  );
}

const renderSelectedDynamicFilters = (
  showDynamicFilters: boolean,
  selectedFilters: SelectedFilter[],
  viewType: string | string[],
  setSelectedFilters: (selectedFilters: SelectedFilter[]) => void,
  onChangeFilters: (params: SRFiltersType) => void,
  clearAllLabel: string
) => {
  return (
    <>
      {showDynamicFilters && selectedFilters.length > 0 && (
        <Flex
          {...wrapperStyles}
          {...(viewType === VIEW_TYPE_CONSTANTS.mapView ? mapViewHorizontalOffsetStyles : {})}
          overflow="auto"
          position="relative"
          mt="0"
          gridColumn="1 / -1"
        >
          <Flex mr="0.5rem" flexShrink={0}>
            <Text
              {...clearFiltersStyles}
              onClick={() => {
                setSelectedFilters([]);
                onChangeFilters([]);
              }}
              data-testid={'srp-dynamic-filters-Clear-Filters-Button'}
            >
              {clearAllLabel}
            </Text>
          </Flex>
          {selectedFilters.map((filter) => (
            <Flex
              {...filterPodStyles}
              key={filter.name}
              data-testid={`srp-dynamic-filters-Pod-${filter.name}`}
            >
              <Text fontFamily="body">{filter.name}</Text>
              <IconComponent
                svg={
                  <Dismiss
                    data-testid={`srp-dynamic-filters-Pod-${filter.name}-Dismiss-Filter-Button`}
                  />
                }
                onClick={() => {
                  const newList = selectedFilters.filter(
                    (selectedFilter) => selectedFilter !== filter
                  );
                  setSelectedFilters(newList);
                  onChangeFilters(
                    newList.reduce((prev, curr) => [...prev, ...(curr.codes ?? [])], [] as string[])
                  );
                }}
                style={{ cursor: 'pointer' }}
              />
            </Flex>
          ))}
        </Flex>
      )}
    </>
  );
};

export const getSelectedFilters = (filterByLabels?: FilterByLabels, filters?: string[]) =>
  (filterByLabels?.dynamicFilters ?? [])
    .map((dynamicFilter) => ({
      groupItems: (dynamicFilter.groupItems ?? []).map((groupItem) => ({
        ...groupItem,
        operator: dynamicFilter.groupOperator,
      })),
    }))
    .flatMap((dynamicFilter) => dynamicFilter.groupItems ?? [])
    .filter((filter) => {
      const filterCodes = (filter.codes ?? '').split(',');
      return filters?.some((selectedFilter) => filterCodes.includes(selectedFilter));
    })
    .map((filter) => ({
      codes: (filter.codes ?? '').split(','),
      name: filter.label,
      operator: filter.operator,
      queryParam: filter.queryParam,
    })) as SelectedFilter[];

const filterPodStyles = {
  p: 'var(--chakra-space-xs) var(--chakra-space-xmd)',
  borderRadius: '2.75rem',
  backgroundColor: 'rgba(229, 242, 246, 1)',
  alignItems: 'center',
  h: '1.75rem',
  minW: 'max-content',
  gap: 'sm',
  justifyContent: 'space-between',
  mt: 'xmd',
  mr: '0.5rem',
};

const clearFiltersStyles = {
  fontSize: 'md',
  fontWeight: 'normal',
  lineHeight: '3',
  textDecoration: 'underline',
  color: 'btnSecondaryEnabled',
  marginTop: 'md',
  _hover: {
    cursor: 'pointer',
  },
};

const splitViewWrapperStyles = {
  maxW: 'full',
  m: '0',
  py: 'xlg',
  gap: 'xlg',
  alignItems: 'center',
} as FlexboxProps;

const wrapperStyles = {
  m: 'var(--chakra-space-lg) auto var(--chakra-space-md)',
  maxW: {
    mobile: 'full',
    md: '45rem',
    lg: '50.5rem',
    xl: '54rem',
  },
  w: 'full',
  flexWrap: {
    mobile: 'wrap',
    md: 'nowrap',
  },
} as FlexboxProps;

const horizontalOffset = {
  mobile: '1.1875rem',
  sm: 'var(--chakra-space-lg)',
  md: '2.375rem',
  lg: '1.75rem',
  xl: '4.8125rem',
};

const mapViewWrapperStyles = {
  maxW: {
    mobile: 'full',
    md: '21rem',
    lg: '25rem',
    xl: '25rem',
  },
  marginTop: {
    mobile: 'var(--chakra-space-sm)',
    sm: 'var(--chakra-space-xmd)',
    md: 'var(--chakra-space-md)',
  },
} as FlexboxProps;

const mapViewHorizontalOffsetStyles = {
  ml: horizontalOffset,
  mr: 'auto',
  maxW: '100%',
};

const buttonWidth = {
  maxW: '11.5rem',
  w: 'full',
  mr: {
    mobile: 'md',
    md: '0',
  },
};

const sharedButtonPropsLayout = {
  w: '100%',
  height: '40px',
  borderRadius: '0.75rem',
  display: 'flex !important',
  alignItems: 'center',
  justifyContent: 'center',
  gap: '0',
  px: '1rem',
  '& .chakra-icon': {
    marginInlineEnd: '0 !important',
    marginInlineStart: '0 !important',
  },
};

const sharedButtonLegacyTypography = {
  fontSize: 'var(--chakra-fontSizes-md)',
};

const sharedButtonSemanticTypography = {
  textStyle: 'body-m-regular',
};

const fullWidthChild = {
  h: '100%',
  '& > *': {
    width: '100% !important',
    height: '100%',
  },
};

const getControlsGridStyles = (isSortVisible: boolean, isViewToggleVisible = true) => ({
  display: 'grid',
  gridTemplateColumns: getControlsGridTemplateColumns(isSortVisible, isViewToggleVisible),
  gap: 'sm',
  w: 'full',
  alignItems: 'stretch',
  mt: 'var(--chakra-space-lg)',
  mb: 'var(--chakra-space-lg)',
  mx: 'auto',
});

const getControlsGridTemplateColumns = (isSortVisible: boolean, isViewToggleVisible: boolean) => {
  if (isSortVisible && isViewToggleVisible)
    return 'minmax(0,0.8fr) minmax(0,1.6fr) minmax(0,0.8fr)';
  if (isSortVisible) return 'minmax(0,1fr) minmax(0,1fr)';
  if (isViewToggleVisible) return 'minmax(0,1fr) minmax(0,1fr)';
  return 'minmax(0,1fr)';
};

const mapListButtonStyles: any = {
  ...sharedButtonPropsLayout,
  h: '40px',
  variant: 'genericSecondary',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  gap: '0',
};

const filterIconStyles = {
  boxSize: '1.25rem',
  position: 'relative',
  top: '2px',
  left: '1px',
  overflow: 'visible',
} as IconProps;

const listIconStyles = {
  boxSize: '1.125rem',
  position: 'relative',
  top: '1px',
} as IconProps;

const mapIconStyles: IconProps = {
  width: '0.8rem',
  overflow: 'visible',
};
