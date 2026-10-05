import { Box, BoxProps, FormControl, FormLabel, StyleProps } from '@chakra-ui/react';
import type {
  ClearHotelFieldsState,
  SearchPlaceType,
  SearchPropertyType,
  SearchSuggestions,
} from '@whitbread-eos/api';
import { getSuggestions } from '@whitbread-eos/api';
import { FormDynamicFieldCompProps } from '@whitbread-eos/atoms';
import debounce from 'lodash/debounce';
import { useTranslation } from 'next-i18next';
import { useEffect, useRef, useState } from 'react';

import SearchBookingsLocationPicker from '../SearchBookingsLocationPicker';

const MIN_LENGTH_SEARCH_TERM = 3;
const INITIAL_DATA: SearchSuggestions = {
  managedPlaces: [],
  places: [],
  properties: [],
};

interface Props extends Omit<FormDynamicFieldCompProps, 'field'> {
  field: {
    name: string;
    value: { name: string; code: string };
    onChange: (val: { name: string; code: string }) => void;
    onBlur: () => void;
  };
}

export default function HotelDropdown({
  formField,
  field,
  errors,
  handleResetField,
  handleSetError,
  handleClearErrors,
}: Readonly<Props>) {
  const { t } = useTranslation();
  const locationErrorLabel = t('ccui.manageBooking.hotelName.error');
  const [items, setItems] = useState<SearchSuggestions | null>(null);
  const [inputValue, setInputValue] = useState<string>('');

  const suggestions = items ?? INITIAL_DATA;
  const { label, props } = formField;
  const { name, value, onChange } = field;
  const locationError = !!errors?.hotelDetails;
  const clearHotelFields: ClearHotelFieldsState = props?.clearHotelFields;
  const { clearHotelName, setClearHotelLocation, setClearHotelName } = clearHotelFields ?? {};

  useEffect(() => {
    if (clearHotelName) {
      setInputValue('');
      handleResetField?.('hotelDetails', { defaultValue: { name: '', code: '' } });
      handleClearErrors?.(field.name);
      setClearHotelName(false);
    }
  }, [clearHotelName]);

  useEffect(() => {
    if (value?.name) {
      setInputValue(value.name);
    }
  }, [value]);

  const debouncedSearch = useRef(debounce(getLocationSuggestions, 300)).current;

  function handleInputChange(value: string | undefined) {
    setInputValue(value ?? '');
    debouncedSearch(value ?? '');

    // reset the fields when the input firld is cleared manually
    if (value?.trim() === '') {
      resetFields();
    }
  }

  async function getLocationSuggestions(value: string) {
    if (value && value.trim().length >= MIN_LENGTH_SEARCH_TERM) {
      const data: SearchSuggestions = await getSuggestions(value);
      if (!data || hasNoMatchingLocations(data)) {
        handleSetError?.(field.name, { type: 'custom', message: locationErrorLabel });
        setItems(INITIAL_DATA);
        return;
      }

      handleClearErrors?.(field.name);
      setItems({ managedPlaces: [], places: [], properties: data?.properties });
    } else {
      setItems(INITIAL_DATA);
      handleResetField?.('hotelDetails', { defaultValue: { name: '', code: '' } });
      setClearHotelLocation(true);
      handleClearErrors?.(field.name);
    }
  }

  function hasNoMatchingLocations(data: SearchSuggestions) {
    return data.properties?.length === 0;
  }

  return (
    <Box
      {...wrapperStyles}
      {...errorWrapperStyles(locationError)}
      data-testid="LocationDropdown-Container"
    >
      <FormControl isInvalid={locationError}>
        {label && (
          <FormLabel
            data-testid={`LocationDropdown-${name}-label`}
            pos="absolute"
            {...labelStyle(locationError, inputValue)}
            htmlFor={name}
          >
            {label}
          </FormLabel>
        )}

        <SearchBookingsLocationPicker
          inputPlaceholder={label}
          inputvalue={inputValue}
          suggestions={suggestions}
          styles={getLocationPickerStyles(locationError)}
          showError={locationError}
          errorLabel={locationErrorLabel}
          onSelectLocation={handleSelectLocation}
          onInputChange={handleInputChange}
          onClearInput={handleLocationInputClear}
          onInputFocus={handleHotelInputFocus}
          onInputBlur={handleLocationInputBlur}
        />
      </FormControl>
    </Box>
  );

  function handleSelectLocation(location: SearchPropertyType | SearchPlaceType | undefined) {
    if (location) {
      onChange({
        name: (location as SearchPropertyType)?.suggestion,
        code: (location as SearchPropertyType)?.code,
      });
      setInputValue((location as SearchPropertyType)?.suggestion ?? '');
    }
  }

  function handleLocationInputClear() {
    resetFields();
  }

  function handleLocationInputBlur() {
    resetFields();
  }

  function resetFields() {
    setClearHotelLocation(true);
    setClearHotelName(true);
  }

  function handleHotelInputFocus() {
    //reset items when the user is out of input
    setItems(INITIAL_DATA);
  }
}

const wrapperStyles = {
  w: {
    lg: '24.5rem',
    xl: '26.25rem',
  },
  p: 0,
  height: 'var(--chakra-space-4xl)',
  _placeholder: { opacity: 0.7, color: 'var(--chakra-darkGrey1)' },
} as StyleProps;

const inputStyles = {
  border: '0.063rem solid var(--chakra-colors-lightGrey1)',
  px: 'md',
  py: 0,
} as BoxProps;

const errorInputStyles = (locationError: boolean) => {
  return locationError
    ? ({
        borderColor: 'var(--chakra-colors-error)',
        boxShadow: '0 0 0 1px var(--chakra-colors-error)',
        _hover: {
          borderColor: 'var(--chakra-colors-error)',
          boxShadow: '0 0 0 1px var(--chakra-colors-error)',
        },
        _focus: {
          borderWidth: '2px',
          borderColor: 'var(--chakra-colors-error)',
          boxShadow: '0 0 0 1px var(--chakra-colors-error)',
        },
      } as BoxProps)
    : {};
};

const errorWrapperStyles = (locationError: boolean) => {
  return locationError ? ({ marginBottom: 'var(--chakra-space-4xl)' } as BoxProps) : {};
};

const getLocationPickerStyles = (locationError: boolean) => {
  return {
    inputGroupStyles: wrapperStyles,
    inputElementStyles: inputStyles,
    errorInputElementStyles: errorInputStyles(locationError),
  };
};

const labelStyle = (error: boolean, value: string) => {
  return {
    h: '1.25rem',
    w: 'fit-content',
    fontSize: 'sm',
    fontWeight: 'normal',
    align: 'center',
    px: 'xs',
    ml: '0.750rem',
    top: '-0.625rem',
    backgroundColor: 'baseWhite',
    zIndex: '1',
    color: error ? 'error' : 'darkGrey1',
    display: value ? 'block' : 'none',
    _focus: { color: error ? 'error' : 'primary', display: 'block' },
  };
};
