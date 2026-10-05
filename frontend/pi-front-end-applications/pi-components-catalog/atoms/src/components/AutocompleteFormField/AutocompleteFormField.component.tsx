import { type BoxProps, PopoverContentProps, StyleProps } from '@chakra-ui/react';
import React, { useEffect, useState } from 'react';

import AutocompleteLocation from '../AutocompleteLocation';
import FormError from '../Form/FormError';
import { type FormDynamicFieldCompProps } from '../Form/formTypes';

export default function AutoCompleteStaticHotels({
  formField: { props, errorStyles },
  field,
  handleSetValue,
  errors = {},
  handleClearErrors,
}: Readonly<FormDynamicFieldCompProps>) {
  const [inputValue, setInputValue] = useState('');
  const [isError, setIsError] = useState(false);
  useEffect(() => {
    setIsError(!!errors[field.name]);
  }, [errors[field.name]]);
  const handleClearInput = () => setInputValue('');
  const handleLocationInputChange = (value: string | undefined) => {
    setInputValue(value || '');
  };

  const getFilteredHotels = () => {
    if (!inputValue || inputValue.trim().length === 0) {
      return [];
    }
    if (!props?.hotels) {
      return [];
    }
    return props.hotels.filter((hotel: { value?: string; label?: string; component?: string }) => {
      const searchValue = inputValue.toLowerCase();
      const hotelValue = (hotel.value || hotel.label || '').toLowerCase();
      return hotelValue.includes(searchValue);
    });
  };

  if (props?.hotels && props.hotels.length > 0) {
    const filteredItems = getFilteredHotels();

    return (
      <>
        <AutocompleteLocation
          dataTestId="HotelsDropdownPicker"
          items={filteredItems}
          inputPlaceholder={props.placeholder}
          showElements={true}
          openListOnFocus={false}
          disableInternalFilter={true}
          autocompleteStyles={{
            errorMarginBottom,
            ...(isError && {
              errorInputGroupStyles,
              errorInputElementStyles,
            }),
            inputGroupStyles: wrapperStyles,
            inputElementStyles: inputStyles,
            listStyles,
          }}
          onInputChange={handleLocationInputChange}
          onChange={(value: string) => {
            handleSetValue?.(field.name, value);
            setInputValue(value);
            setIsError(false);
            handleClearErrors?.(field.name);
          }}
          inputSelectedValue={inputValue}
          hasClearIcon={true}
          onClearInput={handleClearInput}
          onSelectOption={props?.handleSelectOption}
          hasItemObject={true}
        />
        {isError && <FormError errors={errors} name={field.name} extraStyles={errorStyles} />}
      </>
    );
  }
}
const listStyles = {
  maxHeight: '16rem',
  overflow: 'scroll',
  zIndex: 10,
  mt: '10px',
} as PopoverContentProps;
const wrapperStyles = {
  w: '100%',
  p: 0,
  height: 'var(--chakra-space-4xl)',
  _placeholder: { opacity: 0.7, color: 'var(--chakra-darkGrey1)' },
} as StyleProps;

const inputStyles = {
  border: '0.063rem solid var(--chakra-colors-lightGrey1)',
} as BoxProps;

const errorMarginBottom = {
  mobile: 'sm',
  xs: 'md',
  sm: '0',
};

const errorInputGroupStyles = {
  borderRadius: 'var(--chakra-radii-base)',
  border: '2px solid var(--chakra-colors-error)',
};

const errorInputElementStyles = {
  border: 'none',
  borderRight: 'none',
  _hover: {
    border: 'none',
  },
  _focus: {
    border: 'none',
  },
  _active: {
    border: 'none',
  },
};
