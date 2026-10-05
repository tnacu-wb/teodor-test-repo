import {
  Box,
  BoxProps,
  Input,
  InputGroup,
  InputLeftElement,
  Popover,
  PopoverBody,
  PopoverBodyProps,
  PopoverContent,
  PopoverContentProps,
  PopoverFooter,
  PopoverFooterProps,
  PopoverHeader,
  PopoverHeaderProps,
  PopoverTrigger,
  Text,
  TextProps,
} from '@chakra-ui/react';
import {
  Button,
  Checkbox,
  Dismiss,
  Icon,
  Info,
  Notification,
  SearchIcon,
} from '@whitbread-eos/atoms';
import { useTranslation } from 'next-i18next';
import React, { ChangeEvent, ReactNode, useMemo, useState } from 'react';

interface FilterSelection {
  [key: string]: boolean;
}

interface Props {
  popoverTrigger: ((isPopoverOpen: boolean, columnKey: string) => ReactNode) | (() => ReactNode);
  filterKey: string;
  filterOptions: string[];
  selectedFilters: string[];
  onApplyFilters: (selectedFilters: string[], filterKey: string) => void;
  externalStyles?: {
    contentStyles?: PopoverContentProps;
    headerStyles?: PopoverHeaderProps;
    bodyStyles?: PopoverBodyProps;
    footerStyles?: PopoverFooterProps;
  };
}

function TableFilter({
  popoverTrigger,
  filterKey,
  filterOptions,
  selectedFilters,
  onApplyFilters,
  externalStyles,
}: Readonly<Props>) {
  const [selection, setSelection] = useState<FilterSelection>({});
  const [searchTerm, setSearchTerm] = useState<string>('');
  const sortedFilterOptions = useMemo(() => {
    return filterOptions.sort();
  }, [filterOptions]);

  const { t } = useTranslation();

  const numberSelected = Object.keys(selection).filter((key) => selection[key]).length;
  const multiSelectionLabel = numberSelected
    ? t('tableFilter.clearAll')
    : t('tableFilter.selectAll');
  const matchingFilters =
    searchTerm.length < 3
      ? sortedFilterOptions
      : sortedFilterOptions.filter((option) =>
          option.toLowerCase().includes(searchTerm.toLowerCase())
        );

  const handleMultiSelection = () => {
    setSelection((current: FilterSelection) => {
      const updatedSelection = { ...current };
      const newValue = !numberSelected;

      Object.keys(updatedSelection).forEach((key) => {
        updatedSelection[key] = newValue;
      });

      return updatedSelection;
    });
  };

  const handleCheckboxValueChanged = (event: ChangeEvent<HTMLInputElement>, option: string) => {
    setSelection((current: FilterSelection) => {
      const updatedSelection = { ...current };
      if (updatedSelection[option] !== undefined) {
        updatedSelection[option] = event.target.checked;
      }

      return updatedSelection;
    });
  };

  const handleApplyFilters = () => {
    const selectedFilters = Object.keys(selection).filter((key) => selection[key]);
    onApplyFilters(selectedFilters, filterKey);
  };

  const clearAllSelection = () => {
    setSelection((current: FilterSelection) => {
      const updatedSelection = Object.keys(current).reduce((obj, current) => {
        return { ...obj, [current]: false };
      }, {});

      return updatedSelection;
    });
  };

  const setFilterSelection = () => {
    const updatedSelection = filterOptions.reduce((obj, current) => {
      return { ...obj, [current]: selectedFilters.includes(current) };
    }, {});

    setSelection(updatedSelection);
  };

  return (
    <Popover
      onOpen={setFilterSelection}
      onClose={() => {
        clearAllSelection();
        setSearchTerm('');
      }}
    >
      {({ isOpen, onClose }) => (
        <>
          <PopoverTrigger>{popoverTrigger(isOpen, filterKey)}</PopoverTrigger>
          <PopoverContent {...{ ...contentStyles, ...externalStyles?.contentStyles }}>
            <PopoverHeader {...{ ...headerStyles, ...externalStyles?.headerStyles }}>
              <Box {...verticalFlexStyles} gap={6}>
                <Box {...headerTopSectionStyles}>
                  <Box {...verticalFlexStyles} gap={2} alignItems="flex-start">
                    <Text {...headerTextStyles}>
                      {`${numberSelected} ${t('tableFilter.selectionCount')}`}{' '}
                    </Text>
                    <Text
                      onClick={handleMultiSelection}
                      {...{ ...headerTextStyles, ...headerTextLinkStyles }}
                    >
                      {multiSelectionLabel}
                    </Text>
                  </Box>
                  <Box
                    as="button"
                    data-testid="TableFilter-PopoverCloseIcon"
                    {...closeIconStyles}
                    onClick={onClose}
                  >
                    <Icon svg={<Dismiss />} />
                  </Box>
                </Box>
                <InputGroup>
                  <InputLeftElement {...inputLeftElementStyles} pointerEvents="none">
                    <Icon svg={<SearchIcon />} />
                  </InputLeftElement>
                  <Input
                    name="searchFilters"
                    placeholder={t('tableFilter.search.placeholder')}
                    value={searchTerm}
                    onChange={(event: ChangeEvent<HTMLInputElement>) =>
                      setSearchTerm(event.target.value)
                    }
                    {...inputElementStyles}
                  />
                </InputGroup>
              </Box>
            </PopoverHeader>
            <PopoverBody {...{ ...bodyStyles, ...externalStyles?.bodyStyles }}>
              {matchingFilters.length > 0 && (
                <Box {...verticalFlexStyles} gap={4} paddingBottom={4}>
                  {matchingFilters.map((filter, index) => {
                    return (
                      <Checkbox
                        key={`${filter}-${index}`}
                        isChecked={selection[filter] ?? false}
                        onChange={(e) => {
                          handleCheckboxValueChanged(e, filter);
                        }}
                        checkboxWrapperStyles={{ margin: '0' }}
                        {...checkBoxStyles}
                      >
                        <Text {...checkBoxLabelStyles}>{filter} </Text>
                      </Checkbox>
                    );
                  })}
                </Box>
              )}

              {!!filterOptions.length && !matchingFilters.length && (
                <Notification
                  maxWidth="full"
                  variant="info"
                  status="info"
                  description={t('tableFilter.search.noResults')}
                  svg={<Info />}
                  wrapperStyles={notificationStyles}
                />
              )}
            </PopoverBody>
            <PopoverFooter {...{ ...footerStyles, ...externalStyles?.footerStyles }}>
              <Button
                onClick={() => {
                  handleApplyFilters();
                  onClose();
                }}
                size="full"
                variant="tertiary"
                {...footerButtonStyles}
              >
                {t('tableFilter.apply')}
              </Button>
            </PopoverFooter>
          </PopoverContent>
        </>
      )}
    </Popover>
  );
}

const contentStyles: PopoverContentProps = {
  display: 'flex',
  inset: 'var(--chakra-space-4) auto auto 0px',
  boxShadow: '0px 2px 12px 0px var(--chakra-colors-lightGrey2)',
  padding: '0',
  borderRadius: 'var(--chakra-radii-base)',

  _focus: {
    boxShadow: '0px 2px 12px 0px var(--chakra-colors-lightGrey2)',
  },
  _focusVisible: {
    outline: 'none',
  },
};

const headerStyles: PopoverHeaderProps = {
  border: 'none',
  padding: 'var(--chakra-space-6) ',
};

const headerTopSectionStyles: BoxProps = {
  width: 'full',
  display: 'flex',
  alignItems: 'flex-start',
  justifyContent: 'space-between',
};

const bodyStyles: PopoverBodyProps = {
  padding: '0',
  margin: '0 var(--chakra-space-6) ',
  flex: '1',
  overflow: 'auto',
};

const footerStyles: PopoverFooterProps = {
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'space-between',
  borderTop: '1px solid var(--chakra-colors-lightGrey2)',
  padding: 'var(--chakra-space-6) 0',
};

const headerTextStyles: TextProps = {
  color: 'darkGrey1',
  fontSize: 'md',
  lineHeight: '3',
  fontWeight: 'normal',
};

const headerTextLinkStyles: TextProps = {
  textDecoration: 'underline',
  cursor: 'pointer',
  marginTop: '0',
};

const closeIconStyles: BoxProps = {
  paddingY: 'var(--chakra-space-1-5)',
};

const inputLeftElementStyles = {
  top: 'var(--chakra-space-4)',
  left: 'var(--chakra-space-4)',
  height: 'var(--chakra-sizes-6)',
  width: 'var(--chakra-sizes-6)',
};

const inputElementStyles = {
  fontSize: 'md',
  lineHeight: '3',
  color: 'darkGrey2',
  height: 'var(--chakra-sizes-14)',
  paddingLeft: 'var(--chakra-space-3xl)',
  paddingTop: 'var(--chakra-space-2)',
  paddingBottom: 'var(--chakra-space-2)',
  borderRadius: 'var(--chakra-radii-base)',
  border: '1px solid var(--chakra-colors-lightGrey1)',
  _placeholder: {
    color: 'darkGrey2',
  },
  _disabled: {
    opacity: '0.4',
    cursor: 'not-allowed',
  },
};

const checkBoxStyles = {
  display: 'flex',
  alignItems: 'center',
};

const checkBoxLabelStyles = {
  color: 'darkGrey1',
  fontSize: 'md',
  fontWeight: '400',
  lineHeight: '3',
  fontFamily: 'header',
};

const footerButtonStyles = {
  margin: '0 var(--chakra-space-6)',
};

const verticalFlexStyles: BoxProps = {
  display: 'flex',
  flexDirection: 'column',
};

const notificationStyles: BoxProps = {
  width: 'auto',
  margin: 0,
};

export default TableFilter;
