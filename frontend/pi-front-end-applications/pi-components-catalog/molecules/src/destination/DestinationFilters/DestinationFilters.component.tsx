import {
  Box,
  Button,
  ButtonProps,
  Divider,
  Flex,
  FocusLock,
  Heading,
  Slide,
  SlideDirection,
  Text,
  useOutsideClick,
} from '@chakra-ui/react';
import {
  type SrpLabel,
  type DynamicFilters,
  type SrpInfo,
  type SelectedFilter,
  type DlpAnalytics,
} from '@whitbread-eos/api';
import { analytics, formatDataTestId, useScreenSize } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { ChangeEvent, ReactElement, useEffect, useRef, useState } from 'react';

import { FilterPanelClearButton, FilterPanelCloseButton } from '../../utils/FilterPanelControls';
import { createCheckboxList } from '../../utils/createCheckboxList';
import { onPanelEscapeKeyDown } from '../../utils/filterPanelKeyboard';
import { useFilterPanelTypography } from '../../utils/useFilterPanelTypography';

const DLP_FILTERS_HEADING_ID = 'DLP-Filters-heading';

interface Props {
  selectedFilters: SelectedFilter[];
  setSelectedFilters: (state: SelectedFilter[]) => void;
  labels: {
    filters: { label?: SrpLabel; info?: SrpInfo };
    dynamicFilters?: DynamicFilters[];
  };
  isDisabled?: boolean;
  showClearFilters?: boolean;
  leftIcon?: ReactElement;
  buttonProps?: ButtonProps;
}

const CLEAR_FILTERS_ENABLER = 2;

export default function DestinationFilters({
  selectedFilters,
  setSelectedFilters,
  isDisabled = false,
  labels,
  showClearFilters = true,
  leftIcon,
  buttonProps,
}: Readonly<Props>) {
  const [isFilterOpen, setIsFilterOpen] = useState(false);
  const { t } = useTranslation();
  const { headingTypography, labelTypography, mergedButtonProps } =
    useFilterPanelTypography(buttonProps);
  const ref = useRef<HTMLDivElement>(null) as React.MutableRefObject<HTMLDivElement>;
  const filterButtonRef = useRef<HTMLButtonElement>(null);
  useOutsideClick({
    ref: ref,
    handler: () => setIsFilterOpen(false),
  });
  const { filters, dynamicFilters } = labels;
  const { label, info } = filters;
  const baseDataTestId = 'DLP-Filters';
  const { isLessThanSm } = useScreenSize();

  useEffect(() => {
    document.body.style.overflow = isFilterOpen ? 'hidden' : 'auto';
  }, [isFilterOpen]);

  const handleFilterChange = (e: ChangeEvent<HTMLInputElement>, operator: string) => {
    const newState = e.target.checked
      ? [
          ...selectedFilters,
          {
            codes: e.target.value?.split(',').flat(),
            name: e.target.ariaLabel,
            operator: operator,
            queryParam: e.target.parentElement?.getAttribute('data-query-param') ?? '',
          },
        ]
      : selectedFilters.filter((selectedFilter) => {
          const selectedFilterCodes = selectedFilter.codes;

          if (selectedFilterCodes.length > 1) {
            return selectedFilterCodes.join(',') !== e.target.value;
          }
          return selectedFilterCodes[0] !== e.target.value;
        });
    setSelectedFilters(newState as SelectedFilter[]);

    analytics.update({
      dlp: {
        ...(window.analyticsData?.dlp ?? {}),
        searchFilter: newState.map((filter) => filter.codes).join(','),
        filterSectionOpened: true,
        filtersCleared: false,
      } as DlpAnalytics,
    });
  };

  const handleClearFilters = () => {
    setSelectedFilters([]);
    analytics.update({
      dlp: {
        ...(window.analyticsData?.dlp ?? {}),
        filtersCleared: true,
        searchFilter: '',
      } as DlpAnalytics,
    });
  };

  return (
    <Box>
      <Slide
        direction={'left' as SlideDirection}
        in={isFilterOpen}
        unmountOnExit
        style={{
          zIndex: 999,
          background: 'rgba(0,0,0,0.5)',
        }}
      >
        <FocusLock restoreFocus finalFocusRef={filterButtonRef} initialFocusRef={ref}>
          <Box
            {...panelStyles}
            data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}
            id={formatDataTestId(baseDataTestId, 'Wrapper')}
            ref={ref}
            role="dialog"
            aria-modal="true"
            aria-labelledby={DLP_FILTERS_HEADING_ID}
            tabIndex={-1}
            onKeyDown={onPanelEscapeKeyDown(() => setIsFilterOpen(false))}
          >
            <Box pt="2.5rem" position="sticky">
              <Flex justifyContent="space-between" alignItems="center">
                <Heading
                  as="h2"
                  size="none"
                  id={DLP_FILTERS_HEADING_ID}
                  {...headingTypography}
                  color="var(--chakra-colors-darkGrey1)"
                >
                  {label?.header}
                </Heading>
                <FilterPanelCloseButton
                  testId={formatDataTestId(baseDataTestId, 'Close-Button')}
                  label={t('searchResults.filters.close', { defaultValue: 'Close' })}
                  onClose={() => setIsFilterOpen(false)}
                />
              </Flex>
              {selectedFilters.length >= CLEAR_FILTERS_ENABLER && (
                <FilterPanelClearButton
                  {...clearFiltersStyles}
                  testId={formatDataTestId(baseDataTestId, 'Clear-Filters-Button')}
                  label={
                    label?.reset ?? t('tableFilter.clearAll', { defaultValue: 'Clear all filters' })
                  }
                  onClear={handleClearFilters}
                />
              )}
              <Divider {...dividerStyles} />
            </Box>
            <Box overflow="auto" height="82vh">
              {dynamicFilters?.map((dynamicFilterSection: DynamicFilters, i: number) => {
                const groupHeadingId = `DLP-Filters-group-${i}-heading`;
                return (
                  <Box
                    key={dynamicFilterSection.groupTitle}
                    pt="md"
                    role="group"
                    aria-labelledby={groupHeadingId}
                  >
                    <Heading
                      as="h3"
                      id={groupHeadingId}
                      mb="sm"
                      size="none"
                      {...headingTypography}
                      color="var(--chakra-colors-darkGrey1)"
                    >
                      {dynamicFilterSection.groupTitle}
                    </Heading>
                    {createCheckboxList(
                      baseDataTestId,
                      selectedFilters,
                      (e) => handleFilterChange(e, dynamicFilterSection.groupOperator ?? ''),
                      dynamicFilterSection?.groupItems ?? [],
                      info?.lift ?? '',
                      true,
                      labelTypography
                    )}
                    {dynamicFilters.length - 1 !== i && <Divider {...dividerStyles} />}
                  </Box>
                );
              })}
            </Box>
          </Box>
        </FocusLock>
      </Slide>
      <Flex alignItems="center">
        <Button
          ref={filterButtonRef}
          data-testid={formatDataTestId(baseDataTestId, 'Open-Button')}
          size="sm"
          variant="genericSecondary"
          isDisabled={isDisabled}
          onClick={() => setIsFilterOpen(true)}
          aria-haspopup="dialog"
          aria-expanded={isFilterOpen}
          aria-controls={isFilterOpen ? formatDataTestId(baseDataTestId, 'Wrapper') : undefined}
          leftIcon={leftIcon}
          {...filterButtonWidth}
          {...mergedButtonProps}
          overflow="hidden"
        >
          {t('dlp.filters.filtersAmount').replace(
            '({amount})',
            !isLessThanSm && selectedFilters?.length > 0 ? `(${selectedFilters?.length})` : ''
          )}
        </Button>
        {showClearFilters && selectedFilters.length > 0 && (
          <Text
            {...clearFiltersStyles}
            display={{ mobile: 'none', sm: 'block' }}
            mt="0"
            mb="0"
            ml="0.938rem"
            onClick={handleClearFilters}
            data-testid={formatDataTestId(baseDataTestId, 'Clear-All-Button')}
          >
            {t('tableFilter.clearAll')}
          </Text>
        )}
      </Flex>
    </Box>
  );
}

const filterButtonWidth = {
  w: {
    mobile: '8.75rem',
    xs: '10.25rem',
    sm: '7.75rem',
    lg: '11.5rem',
  },
};

const panelStyles = {
  background: 'baseWhite',
  height: '100vh',
  width: 'full',
  px: 'lg',
  maxWidth: {
    base: '19.063rem',
    xs: '19.438rem',
    sm: '21.875rem',
    md: '23.375rem',
    lg: '26.063rem',
    xl: '30.375rem',
  },
  zIndex: '9999',
};

const dividerStyles = {
  borderColor: 'lightGrey2',
  mt: 'lg',
};

const clearFiltersStyles = {
  fontSize: 'md',
  fontWeight: 'normal',
  lineHeight: '3',
  textDecoration: 'underline',
  color: 'btnSecondaryEnabled',
  marginTop: 'md',
  marginBottom: 'lg',
  _hover: {
    cursor: 'pointer',
  },
};
