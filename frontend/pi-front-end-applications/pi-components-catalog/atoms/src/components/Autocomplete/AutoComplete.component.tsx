import {
  Box,
  BoxProps,
  Input,
  InputGroup,
  InputLeftElement,
  InputProps,
  InputRightElement,
  PopoverContentProps,
  Text,
} from '@chakra-ui/react';
import { logicalAndOperator } from '@whitbread-eos/utils';
import { useCombobox, useTagGroup } from 'downshift';
import {
  JSXElementConstructor,
  ReactNode,
  useEffect,
  useImperativeHandle,
  useRef,
  useState,
} from 'react';

import { AutocompleteStyleProps } from '../AutocompleteLocation';

// Type definitions to maintain compatibility with @choc-ui API
export interface AutoCompleteChildProps {
  isOpen?: boolean;
  inputValue?: string;
  selectedItem?: any;
  highlightedIndex?: number;
}

export interface AutoCompleteRefMethods {
  open?: () => void;
  close?: () => void;
  toggle?: () => void;
  resetItems?: () => void;
  removeItem?: (value?: string) => void;
}

const NOT_GROUPED_KEY = 'notGrouped';

export type autoCompleteItemType = {
  value: string;
  group?: string;
  component?: ReactNode;
};

type groupedItemsType = {
  [key: string]: autoCompleteItemType[];
};

export interface Props {
  items: autoCompleteItemType[];
  onChange?: (params: string, item?: any) => void;
  onSelectOption?: (params: any) => void;
  onInputChange?: (params: string | undefined) => void;
  onBlurInput?: (param: string) => void;
  onFocusInput?: () => void;
  setInputHasFocus?: (param: boolean) => void;
  wrapperStyles?: BoxProps;
  inputStyles?: InputProps;
  inputPlaceholder: string;
  multiSelectable?: boolean;
  openListOnFocus?: boolean;
  disableInternalFilter?: boolean;
  hasListDivider?: boolean;
  autocompleteStyles?: AutocompleteStyleProps;
  isRequired?: boolean;
  icons?: {
    left?: JSXElementConstructor<any> | null;
    right?: JSXElementConstructor<any>;
  };
  inputSelectedValue?: string;
  dataTestId?: string;
  showElements?: boolean;
  listStyles?: PopoverContentProps;
  hasItemObject?: boolean;
  isPriceFinder?: boolean;
  ariaInvalid?: boolean;
  ariaDescribedBy?: string;
}

export default function Autocomplete({
  items,
  inputSelectedValue,
  inputPlaceholder,
  multiSelectable = false,
  openListOnFocus = true,
  disableInternalFilter = false,
  isRequired = false,
  hasListDivider = true,
  wrapperStyles,
  icons = {},
  inputStyles,
  dataTestId,
  onChange,
  onSelectOption,
  onInputChange,
  onBlurInput,
  onFocusInput,
  setInputHasFocus,
  ariaInvalid,
  ariaDescribedBy,
  showElements = true,
  listStyles,
  hasItemObject = false,
  isPriceFinder,
}: Readonly<Props>) {
  const inputRef = useRef<HTMLInputElement>(null);
  const autocompleteListRef = useRef<HTMLDivElement>(null);
  const comboboxRef = useRef<any>(null);
  const justSelectedRef = useRef(false);
  const itemWasSelectedRef = useRef(false);

  // State for input value
  const [internalInputValue, setInternalInputValue] = useState('');
  const currentInputValue =
    inputSelectedValue !== undefined ? inputSelectedValue : internalInputValue;

  const groupedItems = items.reduce<groupedItemsType>((acc, cur) => {
    const { group = NOT_GROUPED_KEY } = cur;
    acc[group] = acc[group] || [];
    acc[group].push(cur);

    return acc;
  }, {} as groupedItemsType);

  // Flatten items for downshift
  const flatItems = items;

  // Filter items if internal filtering is enabled
  const filteredItems = disableInternalFilter
    ? flatItems
    : flatItems.filter((item) =>
        item.value.toLowerCase().includes(currentInputValue.toLowerCase())
      );

  // Single select combobox
  const {
    isOpen: comboboxIsOpen,
    getMenuProps,
    getInputProps,
    highlightedIndex: comboboxHighlightedIndex,
    getItemProps: comboboxGetItemProps,
    selectedItem: comboboxSelectedItem,
    openMenu,
    closeMenu,
    toggleMenu,
    reset,
    selectItem,
  } = useCombobox({
    items: filteredItems,
    inputValue: currentInputValue,
    onInputValueChange: ({ inputValue, type }) => {
      if (!multiSelectable) {
        if (type === useCombobox.stateChangeTypes.InputChange) {
          itemWasSelectedRef.current = false;
        }
        setInternalInputValue(inputValue || '');
        onInputChange?.(inputValue);
      }
    },
    onSelectedItemChange: ({ selectedItem }) => {
      if (!multiSelectable && selectedItem) {
        justSelectedRef.current = true;
        itemWasSelectedRef.current = true;
        // choc-ui wraps the item in a specific format for onSelectOption when hasItemObject is true
        const paramsItem = hasItemObject
          ? {
              label: selectedItem.value,
              originalValue: selectedItem,
              value: selectedItem.value,
            }
          : selectedItem.value;
        onSelectOption?.(paramsItem);
        // Call onChange with both value and item object (choc-ui compatible format)
        // choc-ui passes (value, {label, value, originalValue})
        const itemObject = {
          label: selectedItem.value,
          value: selectedItem.value,
          originalValue: selectedItem.value,
        };
        onChange?.(selectedItem.value, itemObject as any);
      }
    },
    itemToString: (item) => (item ? item.value : ''),
    ...(openListOnFocus ? {} : { defaultIsOpen: false }),
  });

  // Multi-select with tags
  const [selectedTags, setSelectedTags] = useState<autoCompleteItemType[]>([]);

  const { items: tagItems, addItem: addTagItem } = useTagGroup<autoCompleteItemType>({
    items: selectedTags,
    onItemsChange: ({ items: newItems }) => {
      if (multiSelectable && newItems) {
        setSelectedTags(newItems);
        onChange?.(newItems.map((item) => item.value).join(',') || '');
      }
    },
  });

  const removeTagItem = (item: autoCompleteItemType) => {
    setSelectedTags((prev) => prev.filter((t) => t.value !== item.value));
  };

  // Tag group props for rendering multi-select tags
  const tagGroupHook = useTagGroup<autoCompleteItemType>({
    items: tagItems,
  });

  const {
    isOpen: multiComboboxIsOpen,
    getMenuProps: multiGetMenuProps,
    getInputProps: multiGetInputProps,
    highlightedIndex: multiHighlightedIndex,
    getItemProps: multiGetItemProps,
    reset: multiReset,
    openMenu: multiOpenMenu,
    closeMenu: multiCloseMenu,
    toggleMenu: multiToggleMenu,
  } = useCombobox({
    items: filteredItems,
    inputValue: currentInputValue,
    selectedItem: null,
    onInputValueChange: ({ inputValue }) => {
      if (multiSelectable) {
        setInternalInputValue(inputValue || '');
        onInputChange?.(inputValue);
      }
    },
    onSelectedItemChange: ({ selectedItem }) => {
      if (multiSelectable && selectedItem) {
        addTagItem(selectedItem);
        const paramsItem = hasItemObject ? selectedItem : selectedItem.value;
        onSelectOption?.(paramsItem);
        setInternalInputValue('');
      }
    },
    itemToString: (item) => (item ? item.value : ''),
    stateReducer: (state, actionAndChanges) => {
      const { changes, type } = actionAndChanges;
      switch (type) {
        case useCombobox.stateChangeTypes.InputKeyDownEnter:
        case useCombobox.stateChangeTypes.ItemClick:
          return {
            ...changes,
            isOpen: true, // Keep menu open
            highlightedIndex: state.highlightedIndex,
            inputValue: '', // Clear input after selection
          };
        case useCombobox.stateChangeTypes.InputBlur:
          return {
            ...changes,
            inputValue: '', // Don't set selected item value on blur
          };
        default:
          return changes;
      }
    },
  });

  // Expose ref methods for backward compatibility
  useImperativeHandle(comboboxRef, () => ({
    open: multiSelectable ? multiOpenMenu : openMenu,
    close: multiSelectable ? multiCloseMenu : closeMenu,
    toggle: multiSelectable ? multiToggleMenu : toggleMenu,
    resetItems: () => {
      if (multiSelectable) {
        multiReset();
        tagItems.forEach(() => removeTagItem(tagItems[0]));
      } else {
        reset();
      }
      setInternalInputValue('');
      itemWasSelectedRef.current = false;
    },
    removeItem: (value?: string) => {
      if (multiSelectable) {
        const item = tagItems.find((item) => item.value === value);
        if (item) {
          removeTagItem(item);
        }
      } else {
        selectItem(null);
      }
    },
  }));

  // Open menu when items are available and input has content
  useEffect(() => {
    if (justSelectedRef.current) {
      justSelectedRef.current = false;
      return;
    }

    const isInputFocused =
      typeof document !== 'undefined' && document.activeElement === inputRef.current;

    if (filteredItems.length > 0 && currentInputValue.length > 0 && isInputFocused) {
      if (multiSelectable) {
        multiOpenMenu();
      } else {
        openMenu();
      }
    } else if (filteredItems.length === 0) {
      if (multiSelectable) {
        multiCloseMenu();
      } else {
        closeMenu();
      }
    }
  }, [filteredItems.length, items.length]);

  // Use the appropriate hooks based on mode
  const isOpen = multiSelectable ? multiComboboxIsOpen : comboboxIsOpen;
  const highlightedIndex = multiSelectable ? multiHighlightedIndex : comboboxHighlightedIndex;
  const selectedItem = multiSelectable ? null : comboboxSelectedItem;

  // Autocomplete state for icons
  const autocompleteState: AutoCompleteChildProps = {
    isOpen,
    inputValue: currentInputValue,
    selectedItem,
    highlightedIndex,
  };

  // Always call getMenuProps to satisfy downshift requirements
  const menuProps = multiSelectable
    ? multiGetMenuProps({}, { suppressRefError: true })
    : getMenuProps({}, { suppressRefError: true });

  // Override role to match choc-ui behavior (tests expect role="dialog")
  const finalMenuProps = {
    ...menuProps,
    role: 'dialog' as const,
  };

  // Call both getInputProps and getMenuProps to satisfy downshift requirements
  if (!multiSelectable) {
    multiGetInputProps({}, { suppressRefError: true });
    multiGetMenuProps({}, { suppressRefError: true });
  } else {
    // When in multi-select mode, call the single-select getter to prevent warnings
    getInputProps({}, { suppressRefError: true });
    getMenuProps({}, { suppressRefError: true });
  }

  return (
    <Box as="span">
      <Box style={{ height: '100%' }}>
        <InputGroup data-testid={`${dataTestId}-inputGroup`} {...wrapperStyles}>
          {multiSelectable
            ? renderMultipleInput(inputPlaceholder)
            : renderSimpleInput(autocompleteState, inputPlaceholder, dataTestId)}
        </InputGroup>
        <Box
          as="ul"
          {...autocompleteListStyles}
          {...listStyles}
          w={isPriceFinder ? autocompleteListWidthPriceFinder : autocompleteListWidth}
          ref={autocompleteListRef}
          data-testid={`${dataTestId}-autocompleteList`}
          {...finalMenuProps}
          style={{
            display: filteredItems.length > 0 && isOpen ? 'block' : 'none',
            zIndex: 999,
          }}
        >
          {isPriceFinder &&
            Object.entries(groupedItems).map(([key, values]) => {
              if (key === NOT_GROUPED_KEY) {
                return values.map((v, id) => {
                  const index = filteredItems.findIndex((item) => item.value === v.value);
                  return renderItem(id, v, index);
                });
              }
              return null;
            })}
          {!isPriceFinder &&
            (() => {
              const renderedElements: React.ReactNode[] = [];
              Object.entries(groupedItems).forEach(([key, values], groupId) => {
                // Add group title if it's a named group and showElements is true
                if (key !== NOT_GROUPED_KEY && showElements) {
                  renderedElements.push(
                    <Box
                      key={`group-title-${groupId}`}
                      data-testid={`${dataTestId}-hotelsLabel`}
                      as="div"
                      {...autocompleteGroupTitleStyles}
                      sx={{ listStyle: 'none' }}
                    >
                      {key}
                    </Box>
                  );
                }
                // Add all items
                values.forEach((v, idx) => {
                  const index = filteredItems.findIndex((item) => item.value === v.value);
                  const itemKey = parseInt(`${groupId}${idx}`, 10);
                  renderedElements.push(renderItem(itemKey, v, index));
                });
                // Add divider only if showElements is true
                if (
                  key !== NOT_GROUPED_KEY &&
                  hasListDivider &&
                  showElements &&
                  groupId < Object.entries(groupedItems).length - 1
                ) {
                  renderedElements.push(
                    <Box
                      key={`divider-${groupId}`}
                      as="hr"
                      borderColor="lightGrey3"
                      my={0}
                      sx={{ listStyle: 'none' }}
                    />
                  );
                }
              });
              return renderedElements;
            })()}
        </Box>
      </Box>
    </Box>
  );

  function renderItem(key: number, item: autoCompleteItemType, itemIndex: number) {
    const getItemPropsFunc = multiSelectable ? multiGetItemProps : comboboxGetItemProps;
    const isHighlighted = highlightedIndex === itemIndex;

    return (
      <Box
        as="li"
        key={key}
        {...autocompleteListItemStyles}
        {...(isHighlighted ? { bg: 'var(--chakra-colors-lightGrey5) !important' } : {})}
        {...getItemPropsFunc({ item, index: itemIndex })}
        sx={{ listStyle: 'none' }}
      >
        {item.component}
        <Text as="span" {...autocompleteItemValueStyles}>
          {item.value}
        </Text>
      </Box>
    );
  }

  function renderMultipleInput(inputPlaceholder: string) {
    const {
      getTagGroupProps: localGetTagGroupProps,
      getTagProps: localGetTagProps,
      getTagRemoveProps: localGetTagRemoveProps,
    } = tagGroupHook;

    const multiInputProps = multiGetInputProps({
      ref: inputRef,
      onFocus: () => {
        onFocusInput?.();
        setInputHasFocus?.(true);
      },
      onBlur: () => {
        setInputHasFocus?.(false);
      },
    });

    return (
      <>
        <Box {...localGetTagGroupProps()} display="flex" flexWrap="wrap" gap="xs">
          {tagItems.map((tag, index) => (
            <Box
              as="span"
              key={`${tag.value}-${index}`}
              bg="gray.100"
              borderRadius="md"
              px="2"
              py="1"
              display="inline-flex"
              alignItems="center"
              {...localGetTagProps({ index })}
            >
              {tag.value}
              <Box
                as="button"
                ml="1"
                cursor="pointer"
                fontSize="sm"
                {...localGetTagRemoveProps({ index })}
                onClick={(e: React.MouseEvent) => {
                  e.stopPropagation();
                  removeTagItem(tag);
                }}
              >
                ×
              </Box>
            </Box>
          ))}
        </Box>
        <Input
          {...inputStyles}
          {...multiInputProps}
          placeholder={inputPlaceholder}
          data-testid={`${dataTestId}-locationPlaceholder`}
          isRequired={isRequired}
          isInvalid={ariaInvalid}
          value={currentInputValue}
          onChange={(event: any) => {
            multiInputProps.onChange?.(event);
            onInputChange?.(event?.target?.value);
          }}
          aria-describedby={ariaDescribedBy || undefined}
        />
      </>
    );
  }

  function renderSimpleInput(
    autocompleteState: AutoCompleteChildProps,
    inputPlaceholder: string,
    dataTestId: string | undefined
  ) {
    const createIcon = (
      icon: JSXElementConstructor<any> | undefined | null,
      showLeftIcon?: boolean
    ) => {
      if (!icon || logicalAndOperator(icon === null, !showLeftIcon)) {
        return null;
      }
      const PrimaryIcon = icon;

      return (
        <PrimaryIcon
          {...autocompleteState}
          inputPlaceholder={inputPlaceholder}
          autocompleteRef={comboboxRef}
          autocompleteInputRef={inputRef}
        />
      );
    };
    const leftIcon = createIcon(icons?.left, showElements);
    const rightIcon = createIcon(icons?.right);

    const comboboxInputProps = getInputProps({
      ref: inputRef,
      onFocus: () => {
        onFocusInput?.();
        setInputHasFocus?.(true);
        if (openListOnFocus) {
          openMenu();
        }
      },
      onBlur: (event: any) => {
        setInputHasFocus?.(false);
        if (itemWasSelectedRef.current) {
          return;
        }
        const list = Object.entries(groupedItems);
        if (
          inputRef?.current?.value &&
          inputRef?.current?.value?.length >= 3 &&
          list?.length &&
          event.relatedTarget?.parentNode?.parentNode !== inputRef?.current?.parentNode?.parentNode
        ) {
          const [, values] = list[0];
          if (values?.length) {
            const item = values[0];
            onBlurInput?.(item.value);
          }
        }
      },
    });

    return (
      <>
        <Input
          {...renderStyles()}
          {...comboboxInputProps}
          placeholder={inputPlaceholder}
          data-testid={`${dataTestId}-locationPlaceholder`}
          isRequired={isRequired}
          isInvalid={ariaInvalid}
          value={currentInputValue}
          onChange={(event: any) => {
            comboboxInputProps.onChange?.(event);
            onInputChange?.(event?.target?.value);
          }}
          aria-describedby={ariaDescribedBy || undefined}
        />
        {leftIcon !== null && (
          <InputLeftElement top="50%" transform="auto" translateY="-50%">
            {leftIcon}
          </InputLeftElement>
        )}
        {rightIcon && (
          <InputRightElement
            top="50%"
            transform="auto"
            translateY="-50%"
            translateX={isPriceFinder ? '-150%' : '0'}
            data-testid={`${dataTestId}-inputRight`}
          >
            {rightIcon}
          </InputRightElement>
        )}
      </>
    );
  }

  function renderStyles() {
    return {
      ...(icons?.left ? { pl: '2xl' } : {}),
      ...inputStyles,
      ...(isPriceFinder ? { paddingRight: '65px' } : {}),
    };
  }
}

const autocompleteListWidth = {
  mobile: '18rem',
  xs: '21.4375rem',
  sm: '23.375rem',
  lg: '25.25rem',
  xl: '26.25rem',
};

const autocompleteListWidthPriceFinder = {
  mobile: 'full',
  md: '25.25rem',
  xl: '26.25rem',
};

const autocompleteListStyles = {
  position: 'absolute' as const,
  bg: 'var(--chakra-colors-white)',
  border: '1px solid var(--chakra-colors-lightGrey3)',
  borderRadius: 'var(--chakra-space-radiusSmall)',
  boxShadow: '0 2px var(--chakra-space-xmd) var(--chakra-colors-lightGrey2)',
  marginTop: 0,
  maxHeight: '28.75rem',
  overflow: 'hidden',
  py: 'sm',
  ml: '0 !important',
};

const autocompleteGroupTitleStyles: BoxProps = {
  fontSize: 'sm',
  lineHeight: '2',
  fontWeight: 'normal',
  margin: 'var(--chakra-space-xmd) 0 var(--chakra-space-sm) var(--chakra-space-md)',
  color: 'darkGrey1',
  textTransform: 'capitalize',
};

const autocompleteListItemStyles = {
  display: 'flex',
  alignItems: 'center',
  h: 'var(--chakra-space-2xl)',
  fontSize: 'sm',
  lineHeight: '1',
  fontWeight: 'normal',
  color: 'darkGrey1',
  mx: 0,
  p: 'var(--chakra-space-xmd) var(--chakra-space-md)',
  borderRadius: 0,
  bg: 'var(--chakra-colors-baseWhite) !important',
  cursor: 'pointer',

  _hover: {
    bg: 'var(--chakra-colors-lightGrey5) !important',
  },
};

const autocompleteItemValueStyles: BoxProps = {
  display: 'block',
  textOverflow: 'ellipsis',
  overflow: 'hidden',
  whiteSpace: 'nowrap',
};
