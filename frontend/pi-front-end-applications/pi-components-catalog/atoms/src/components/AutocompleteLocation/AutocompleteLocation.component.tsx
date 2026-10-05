import { Box } from '@chakra-ui/react';
import { MutableRefObject, RefObject, useState } from 'react';

import { Dismiss, Location } from '../../assets/icons';
import Autocomplete, { autoCompleteItemType } from '../Autocomplete';
import Icon from '../Icon';
import { AutocompleteStyleProps } from './';

export interface Props {
  inputPlaceholder: string;
  inputSelectedValue?: string;
  items: autoCompleteItemType[];
  hasClearIcon?: boolean;
  openListOnFocus?: boolean;
  disableInternalFilter?: boolean;
  hasListDivider?: boolean;
  isRequired?: boolean;
  onChange?: (params: string) => void;
  onSelectOption?: (params: any) => void;
  onInputChange?: (params: string | undefined) => void;
  onClearInput?: () => void;
  onBlurInput?: (param: string) => void;
  onFocusInput?: () => void;
  autocompleteStyles: AutocompleteStyleProps;
  dataTestId?: string;
  showElements?: boolean;
  hasItemObject?: boolean;
  isPriceFinder?: boolean;
  ariaInvalid?: boolean;
  ariaDescribedBy?: string;
}

export interface IconProps {
  isOpen?: boolean;
  inputValue?: string;
  selectedItem?: any;
  highlightedIndex?: number;
  inputPlaceholder: string;

  autocompleteRef: MutableRefObject<{
    open?: () => void;
    close?: () => void;
    toggle?: () => void;
    resetItems?: () => void;
    removeItem?: (value?: string) => void;
  }>;

  autocompleteInputRef: RefObject<HTMLInputElement>;
}

export default function AutocompleteLocation({
  items,
  inputSelectedValue,
  hasClearIcon,
  hasListDivider,
  openListOnFocus,
  disableInternalFilter,
  isRequired,
  onChange,
  onSelectOption,
  onInputChange,
  onClearInput,
  onBlurInput,
  onFocusInput,
  autocompleteStyles,
  inputPlaceholder,
  dataTestId,
  showElements = false,
  hasItemObject,
  isPriceFinder,
  ariaInvalid,
  ariaDescribedBy,
}: Readonly<Props>) {
  const [inputHasFocus, setInputHasFocus] = useState(false);

  const { inputStyles, wrapperStyles, listStyles } =
    getAutocompleteLocationStyles(autocompleteStyles);

  const autoCompleteProps = {
    items,
    onChange,
    onSelectOption,
    onInputChange,
    onBlurInput,
    onFocusInput,
    setInputHasFocus,
    inputPlaceholder,
    openListOnFocus,
    disableInternalFilter,
    isRequired,
    hasListDivider,
    icons: {
      left: showElements ? () => <Location color="var(--chakra-colors-darkGrey1)" /> : null,
      right: (props: IconProps) => (inputHasFocus ? <ClearInputIcon {...props} /> : null),
    },
    inputStyles,
    wrapperStyles,
    listStyles,
    autocompleteStyles,
    inputSelectedValue,
    dataTestId,
    showElements,
    hasItemObject,
    isPriceFinder,
    ariaInvalid,
    ariaDescribedBy,
  };

  return <Autocomplete {...autoCompleteProps} />;

  function ClearInputIcon({ autocompleteRef, autocompleteInputRef }: Readonly<IconProps>) {
    return (
      <Box
        bg={hasClearIcon && inputHasFocus ? 'lightGrey2' : 'inherit'}
        borderRadius="50%"
        key="clear-input-icon"
        p="xs"
        cursor="pointer"
        aria-label="clear-icon"
        data-testid={`${dataTestId}-clearLocationButton`}
        onMouseDown={(event) => {
          event.preventDefault();
          autocompleteRef?.current?.resetItems?.();
          autocompleteRef?.current?.removeItem?.(autocompleteInputRef?.current?.value);
          autocompleteInputRef.current?.focus();
          onClearInput && onClearInput();
        }}
      >
        <Box as="span" display={`${hasClearIcon && inputHasFocus ? 'initial' : 'none'}`}>
          <Icon
            style={{
              transform: 'scale(0.7)',
            }}
            svg={<Dismiss />}
          />
        </Box>
      </Box>
    );
  }
}

const getAutocompleteLocationStyles = (autocompleteStyles: AutocompleteStyleProps) => {
  const inputStyles = {
    variant: 'unstyled',
    textOverflow: 'ellipsis',
    py: 'md',
    border: '1px solid transparent',
    cursor: 'pointer',
    _hover: {
      border: '1px solid var(--chakra-colors-darkGrey1)',
      borderRadius: '4px',
    },
    _focus: {
      border: '2px solid var(--chakra-colors-primary)',
      cursor: 'auto',
      borderRadius: '4px',
    },
    _placeholder: {
      color: 'var(--chakra-colors-darkGrey2)',
      fontStyle: 'normal',
    },
    borderRadius: '4px',
    ...autocompleteStyles?.inputElementStyles,
    ...autocompleteStyles?.errorInputElementStyles,
  };
  const wrapperStyles = {
    w: '50%',
    ...autocompleteStyles?.inputGroupStyles,
    ...autocompleteStyles?.errorInputGroupStyles,
    ...autocompleteStyles?.locationErrorGroupStyles,
  };
  const listStyles = {
    ...autocompleteStyles.listStyles,
  };
  return { inputStyles, wrapperStyles, listStyles };
};
