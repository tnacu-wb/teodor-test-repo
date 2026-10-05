import { Box, BoxProps, FormControl, FormLabel, StyleProps } from '@chakra-ui/react';
import type { ClearHotelFieldsState, SearchSuggestions } from '@whitbread-eos/api';
import {
  getSuggestions,
  HotelsSearchCriteria,
  SearchLocationType,
  SearchManagedPlaceType,
  SR_FORMAT,
} from '@whitbread-eos/api';
import { FormDynamicFieldCompProps } from '@whitbread-eos/atoms';
import { getDistanceUnitBasedOnLocaleSb, useCustomLocale } from '@whitbread-eos/utils';
import debounce from 'lodash/debounce';
import { useTranslation } from 'next-i18next';
import { useEffect, useRef, useState } from 'react';

import HotelDropdownModalContainer from '../HotelDropdownModal';
import SearchBookingsLocationPicker from '../SearchBookingsLocationPicker';

const MIN_LENGTH_SEARCH_TERM = 3;
const INITIAL_DATA: SearchSuggestions = {
  managedPlaces: [],
  places: [],
  properties: [],
};

export default function LocationDropdown({
  formField,
  field,
  errors,
  handleSetValue,
  handleResetField,
  handleSetError,
  handleClearErrors,
}: Readonly<FormDynamicFieldCompProps>) {
  const { t } = useTranslation();
  const { language } = useCustomLocale();
  const locationErrorLabel = t('ccui.manageBooking.hotelLocation.error');

  const [inputValue, setInputValue] = useState<string>(field.value || '');
  const [items, setItems] = useState<SearchSuggestions | null>(null);
  const [isHotelsModalOpen, setIsHotelsModalOpen] = useState(false);
  const [paramsForQuery, setParamsForQuery] = useState<HotelsSearchCriteria>({
    location: '',
    locationFormat: '',
    radius: language === 'en' ? SR_FORMAT.EN_DIST : SR_FORMAT.DIST,
    radiusUnit: getDistanceUnitBasedOnLocaleSb(language || 'en'),
  });

  const suggestions = items ?? INITIAL_DATA;
  const { label, props } = formField;
  const { name, onChange } = field;
  const locationError = !!errors?.hotelLocation;
  const clearHotelFields: ClearHotelFieldsState = props?.clearHotelFields;
  const { clearHotelLocation, setClearHotelLocation, setClearHotelName } = clearHotelFields ?? {};

  const onCloseHotelModal = () => {
    setIsHotelsModalOpen(false);
    resetFields();
  };

  const onSelectHotelFromModal = () => {
    setIsHotelsModalOpen(false);
  };

  useEffect(() => {
    if (clearHotelLocation) {
      setInputValue('');
      handleResetField?.('hotelLocation', { defaultValue: '' });
      handleClearErrors?.(field.name);
      setClearHotelLocation(false);
    }
  }, [clearHotelLocation]);

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
      setItems({ managedPlaces: data?.managedPlaces, places: data?.places, properties: [] });
    } else {
      setItems(INITIAL_DATA);
      handleResetField?.('hotelLocation', { defaultValue: '' });
      setClearHotelName(true);
      handleClearErrors?.(field.name);
    }
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
          onInputFocus={handleLocationInputFocus}
          onInputBlur={handleLocationInputBlur}
        />
      </FormControl>
      <HotelDropdownModalContainer
        isOpen={isHotelsModalOpen}
        onClose={onCloseHotelModal}
        onHotelSelected={onSelectHotelFromModal}
        paramsForQuery={paramsForQuery}
        handleSetValue={handleSetValue}
      />
    </Box>
  );

  function hasNoMatchingLocations(data: SearchSuggestions) {
    return data.places?.length === 0 && data.managedPlaces?.length === 0;
  }

  function handleSelectLocation(location: SearchLocationType | undefined) {
    if (location) {
      onChange((location as SearchLocationType)?.suggestion ?? '');
      updateParamsForQuery(location as SearchManagedPlaceType);
      setInputValue((location as SearchLocationType)?.suggestion ?? '');
      setIsHotelsModalOpen(true);
    }
  }

  function updateParamsForQuery(location: SearchManagedPlaceType) {
    let chosenLocation: string;
    let chosenFormat: string;
    if (location?.geometry) {
      chosenLocation = `${location?.geometry.coordinates[1]},${location?.geometry.coordinates[0]}`;
      chosenFormat = SR_FORMAT.LATLONG;
    } else if (location?.managedPlaceId) {
      chosenLocation = location?.managedPlaceId;
      chosenFormat = SR_FORMAT.MANAGEDPLACEID;
    } else {
      chosenLocation = location?.placeId;
      chosenFormat = SR_FORMAT.PLACEID;
    }
    setParamsForQuery({
      ...paramsForQuery,
      location: chosenLocation,
      locationFormat: chosenFormat,
    });
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

  function handleLocationInputFocus() {
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
  _hover: {},
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
