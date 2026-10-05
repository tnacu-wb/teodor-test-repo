import { Box, BoxProps } from '@chakra-ui/react';
import type {
  AnalyticsData,
  SearchBrandType,
  SearchLocationType,
  SearchSuggestions,
} from '@whitbread-eos/api';
import type { AutocompleteStyleProps } from '@whitbread-eos/atoms';
import {
  AutocompleteLocation,
  Icon,
  InfoMessage,
  LogoHubSimple,
  LogoZipSimple,
  PremierInnLogo,
} from '@whitbread-eos/atoms';
import { analytics } from '@whitbread-eos/utils';
import { CSSProperties, useEffect, useState } from 'react';

declare global {
  interface Window {
    analyticsData: AnalyticsData;
  }
}

const MAX_ITEMS_PER_SECTION = 5;

interface Props {
  isLoading?: boolean;
  isError?: boolean;
  isLocationRequired?: boolean;
  hasListDivider?: boolean;
  showErrorMessage?: boolean;
  inputPlaceholder: string;
  defaultInputValue?: string;
  errorMessage?: string;
  suggestions: SearchSuggestions;
  styles: AutocompleteStyleProps;
  onSelectLocation?: (params: SearchLocationType) => void;
  onInputChange?: (value: string | undefined) => void;
  onInputClear?: () => void;
  onInputFocus?: (value: string | undefined) => void;
  showElements?: boolean;
  isPriceFinder?: boolean;
}

export default function LocationPicker({
  isLocationRequired,
  hasListDivider,
  showErrorMessage,
  inputPlaceholder,
  defaultInputValue,
  suggestions,
  errorMessage,
  styles,
  onSelectLocation,
  onInputChange,
  onInputClear,
  onInputFocus,
  showElements = true,
  isPriceFinder,
}: Readonly<Props>) {
  const [inputSelectedValue, setInputSelectedValue] = useState<string | undefined>(
    defaultInputValue ?? ''
  );
  const [searchTerm, setSearchTerm] = useState<string | undefined>('');
  const [selectionInitialized, setSelectionInitialized] = useState(false);
  const hasClearIcon = !!(inputSelectedValue ?? searchTerm?.length);
  useEffect(() => {
    if (searchTerm?.length) {
      onInputChange?.(searchTerm);
    }
  }, [searchTerm]);

  useEffect(() => {
    setInputSelectedValue(defaultInputValue);
  }, [defaultInputValue]);

  useEffect(() => {
    if (
      (suggestions.managedPlaces.length > 0 ||
        suggestions.places.length > 0 ||
        suggestions.properties.length > 0) &&
      !selectionInitialized
    ) {
      handleLocationChange(defaultInputValue ?? searchTerm ?? '');
      setSelectionInitialized(true);
    }
  }, [suggestions, selectionInitialized, defaultInputValue]);

  return (
    <Box
      position={'relative'}
      zIndex={2}
      {...styles.locationPickerStyles}
      marginBottom={styles.errorMarginBottom}
    >
      <AutocompleteLocation
        items={mapItemsForAutocomplete(suggestions)}
        inputPlaceholder={inputPlaceholder}
        inputSelectedValue={inputSelectedValue}
        onChange={handleLocationChange}
        onInputChange={handleInputChange}
        onClearInput={handleClearInput}
        onBlurInput={handleBlurInput}
        onFocusInput={handleFocusInput}
        hasClearIcon={hasClearIcon}
        hasListDivider={hasListDivider}
        isRequired={isLocationRequired}
        openListOnFocus={false}
        disableInternalFilter={true}
        autocompleteStyles={{
          ...styles,
        }}
        dataTestId="locationPicker"
        showElements={showElements}
        isPriceFinder={isPriceFinder}
        ariaInvalid={showErrorMessage}
        ariaDescribedBy={showErrorMessage ? 'location-picker-error' : undefined}
      />
      {showErrorMessage && errorMessage && (
        <InfoMessage
          infoMessage={errorMessage}
          otherStyles={isPriceFinder ? alertStylesPriceFinder : alertStyles}
          messageId="location-picker-error"
        />
      )}
    </Box>
  );

  function handleInputChange(value: string | undefined) {
    setSearchTerm(value);
    setInputSelectedValue(value);
  }

  function handleLocationChange(location: string) {
    setInputSelectedValue(location);
    const selectedLocation = getSelectedItem(location);
    onSelectLocation?.(selectedLocation);

    if ('placeId' in selectedLocation && selectedLocation.placeId) {
      analytics.update({
        selectedSearch: {
          suggestion: selectedLocation.suggestion,
          placeId: selectedLocation.placeId,
        },
      });

      if (typeof window !== 'undefined') {
        window?.dispatchEvent(new CustomEvent('userSelectedSearchLocation'));
      }
    }

    if ('brand' in selectedLocation && selectedLocation.brand) {
      analytics.update({
        selectedSearch: {
          brand: selectedLocation.brand,
          code: selectedLocation.code,
          suggestion: selectedLocation.suggestion,
          geometry: selectedLocation.geometry,
        },
      });

      if (typeof window !== 'undefined') {
        window?.dispatchEvent(new CustomEvent('userSelectedSearchLocation'));
      }
    }
  }

  function getSelectedItem(item: string) {
    const items = [...suggestions.managedPlaces, ...suggestions.places, ...suggestions.properties];
    const suggestion = items.find(
      (element) => element?.suggestion?.replace(/\//g, ' ') === item?.replace(/\//g, ' ')
    );

    return suggestion ?? items[0];
  }

  function handleClearInput() {
    setSearchTerm('');
    setInputSelectedValue('');

    onInputClear?.();
  }

  function handleBlurInput(value: string) {
    handleLocationChange(value);
  }

  function handleFocusInput() {
    onInputFocus?.(searchTerm);
  }

  function getLogoByBrand(brand: SearchBrandType, code: string) {
    const logoStyle = {
      position: 'relative',
      transform: 'scale(0.45)',
    } as CSSProperties;

    const iconStyle = {
      width: 'var(--chakra-space-lg)',
      height: 'var(--chakra-space-lg)',
      marginRight: 'var(--chakra-space-md)',
    };

    if (brand === 'PI' || brand === 'PID') {
      return (
        <Box mr="md" key={code} style={{ position: 'relative', top: '-0.125rem' }}>
          <PremierInnLogo />
        </Box>
      );
    }
    if (brand === 'HUB') {
      return (
        <Icon
          key={code}
          style={iconStyle}
          svg={<LogoHubSimple style={{ ...logoStyle, top: '-1.25rem', left: '-1.125rem' }} />}
        />
      );
    }
    if (brand === 'ZIP') {
      return (
        <Icon
          key={code}
          style={iconStyle}
          svg={<LogoZipSimple style={{ ...logoStyle, top: '-0.375rem', left: '-1.5rem' }} />}
        />
      );
    }
  }

  function mapItemsForAutocomplete(items: SearchSuggestions) {
    const mappedManagedPlaces = items?.managedPlaces?.map((managedPlace) => {
      return {
        value: managedPlace.suggestion,
      };
    });
    const mappedPlaces = items?.places?.map((place) => {
      return {
        value: place.suggestion,
      };
    });
    const mappedProperties = items?.properties?.map((property) => {
      return {
        value: property.suggestion,
        group: 'Hotels',
        component: getLogoByBrand(property.brand as SearchBrandType, property.code),
      };
    });
    const places = [...mappedManagedPlaces, ...mappedPlaces].slice(0, MAX_ITEMS_PER_SECTION);
    const hotels = [...mappedProperties].slice(0, MAX_ITEMS_PER_SECTION);

    return [...places, ...hotels];
  }
}

const alertStyles = {
  w: {
    base: 'full',
    sm: '25.313rem',
    lg: 'full',
  },
  h: '40px',
  zIndex: 1,
  mb: 0,
};

const alertStylesPriceFinder = {
  position: 'absolute',
  w: {
    base: 'full',
    md: 'auto',
  },
  h: '40px',
  zIndex: 1,
} as BoxProps;
