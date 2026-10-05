import {
  Box,
  Button,
  Divider,
  Flex,
  FocusLock,
  Heading,
  Slide,
  SlideDirection,
  Text,
  useOutsideClick,
  ButtonProps,
} from '@chakra-ui/react';
import type { SRFiltersType, SrpFilter } from '@whitbread-eos/api';
import { useTranslation } from 'next-i18next';
import { ChangeEvent, useEffect, useRef, useState, ReactElement } from 'react';

import { FilterPanelClearButton, FilterPanelCloseButton } from '../../utils/FilterPanelControls';
import { createCheckboxList } from '../../utils/createCheckboxList';
import { onPanelEscapeKeyDown } from '../../utils/filterPanelKeyboard';
import { useFilterPanelTypography } from '../../utils/useFilterPanelTypography';

const FILTERS_HEADING_ID = 'SRP-Filters-heading';

interface Props {
  defaultFilters: SRFiltersType;
  isDisabled?: boolean;
  onChangeFilters: (filters: SRFiltersType) => void;
  labels: {
    filterByButton: string;
    filters: SrpFilter;
  };
  leftIcon?: ReactElement;
  buttonProps?: ButtonProps;
}

const CLEAR_FILTERS_ENABLER = 2;

export default function Filters({
  defaultFilters,
  isDisabled = false,
  onChangeFilters,
  labels,
  leftIcon,
  buttonProps,
}: Readonly<Props>) {
  const { t } = useTranslation();
  const { headingTypography, labelTypography, mergedButtonProps } =
    useFilterPanelTypography(buttonProps);
  const [isFilterOpen, setIsFilterOpen] = useState(false);
  const [selectedFilters, setSelectedFilters] = useState<SRFiltersType>(defaultFilters);
  const ref = useRef<HTMLDivElement>(null) as React.MutableRefObject<HTMLDivElement>;
  const filterButtonRef = useRef<HTMLButtonElement>(null);
  useOutsideClick({
    ref: ref,
    handler: () => setIsFilterOpen(false),
  });
  const { filterByButton, filters } = labels;
  const { label, code, info } = filters;

  const PARKING_FILTERS = [
    { name: label?.freeParking, code: code?.freeParking?.[0] },
    { name: label?.chargeableOnsiteParking, code: code?.chargeableOnsiteParking },
    { name: label?.chargeableOffsiteParking, code: code?.chargeableOffsiteParking },
  ];
  const FACILITIES_FILTERS = [
    { name: label?.airCon, code: code?.airCon },
    { name: label?.lift, code: code?.lift },
    { name: label?.meet, code: code?.meet },
    { name: label?.restaurant, code: code?.restaurant },
  ];

  useEffect(() => {
    document.body.style.overflow = isFilterOpen ? 'hidden' : 'auto';
  }, [isFilterOpen]);

  return (
    <>
      <Slide
        direction={'left' as SlideDirection}
        in={isFilterOpen}
        unmountOnExit
        style={{
          zIndex: 999,
          background: 'rgba(0,0,0,0.5)',
        }}
      >
        <FocusLock restoreFocus finalFocusRef={filterButtonRef}>
          <Box
            {...panelStyles}
            data-testid="SRP-Filters-content"
            ref={ref}
            role="dialog"
            aria-modal="true"
            aria-labelledby={FILTERS_HEADING_ID}
            tabIndex={-1}
            onKeyDown={onPanelEscapeKeyDown(() => setIsFilterOpen(false))}
          >
            <Flex justifyContent="space-between" alignItems="center" mt="2.5rem">
              <Heading
                as="h2"
                id={FILTERS_HEADING_ID}
                size="none"
                {...headingTypography}
                color="var(--chakra-colors-darkGrey1)"
              >
                {label?.header}
              </Heading>
              <FilterPanelCloseButton
                testId="SRP-Filters-Close-button"
                label={t('searchResults.filters.close', { defaultValue: 'Close' })}
                onClose={() => setIsFilterOpen(false)}
              />
            </Flex>
            {selectedFilters.length >= CLEAR_FILTERS_ENABLER && (
              <FilterPanelClearButton
                {...clearFiltersStyles}
                testId="SRP-Clear-filters"
                label={
                  label?.reset ?? t('tableFilter.clearAll', { defaultValue: 'Clear all filters' })
                }
                onClear={handleClearFilters}
              />
            )}
            <Divider {...dividerStyles} />
            <Box role="group" aria-labelledby="SRP-Filters-parking-heading">
              <Heading
                as="h3"
                id="SRP-Filters-parking-heading"
                mb="sm"
                size="none"
                {...headingTypography}
                color="var(--chakra-colors-darkGrey1)"
              >
                {label?.parking}
              </Heading>
              {createCheckboxList(
                'SRP',
                selectedFilters,
                handleFilterChange,
                PARKING_FILTERS,
                info?.lift ?? '',
                false,
                labelTypography
              )}
            </Box>
            <Divider {...dividerStyles} />
            <Box role="group" aria-labelledby="SRP-Filters-facilities-heading">
              <Heading
                as="h3"
                id="SRP-Filters-facilities-heading"
                mb="sm"
                size="none"
                {...headingTypography}
                color="var(--chakra-colors-darkGrey1)"
              >
                {label?.facilities}
              </Heading>
              {createCheckboxList(
                'SRP',
                selectedFilters,
                handleFilterChange,
                FACILITIES_FILTERS,
                info?.lift ?? '',
                false,
                labelTypography
              )}
            </Box>
          </Box>
        </FocusLock>
      </Slide>
      <Button
        ref={filterButtonRef}
        data-testid="SRP-controls-filter-by-button"
        size="sm"
        variant="genericSecondary"
        mr={{ base: 'md', lg: 'lg' }}
        isDisabled={isDisabled}
        onClick={() => setIsFilterOpen(true)}
        aria-haspopup="dialog"
        aria-expanded={isFilterOpen}
        aria-controls={isFilterOpen ? 'SRP-Filters-content' : undefined}
        {...filterButtonWidth}
        leftIcon={leftIcon}
        {...mergedButtonProps}
      >
        <Text as="span" {...labelTypography}>
          {filterByButton}
        </Text>
      </Button>
    </>
  );

  function handleFilterChange(e: ChangeEvent<HTMLInputElement>) {
    const newState = e.target.checked
      ? [...selectedFilters, e.target.value]
      : selectedFilters.filter((elem) => elem !== e.target.value);
    setSelectedFilters(newState);
    onChangeFilters(newState);
  }

  function handleClearFilters() {
    setSelectedFilters([]);
    onChangeFilters([]);
  }
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
  height: '100dvh',
  width: 'full',
  px: 'lg',
  maxWidth: {
    base: '19rem',
    sm: '21.875rem',
    md: '23.25rem',
    lg: '26.25rem',
    xl: '30.375rem',
  },
  overflow: 'hidden',
  zIndex: '9999',
};

const dividerStyles = {
  borderColor: 'lightGrey2',
  my: 'lg',
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
