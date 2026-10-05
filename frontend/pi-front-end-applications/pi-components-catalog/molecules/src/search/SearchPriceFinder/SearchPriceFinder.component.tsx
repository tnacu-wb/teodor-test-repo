import { Flex } from '@chakra-ui/react';
import type {
  ScreenSize,
  SearchAEMTranslationsType,
  SearchPartialTranslationsType,
  SearchPlaceType,
  SearchPropertyType,
  SearchSuggestions,
  ResponsiveValue,
  AnalyticsData,
} from '@whitbread-eos/api';
import { Button, Icon, SearchIcon } from '@whitbread-eos/atoms';
import { useSearchParams } from 'next/navigation';
import { NextRouter } from 'next/router';
import { useState } from 'react';

import LocationPicker from '../LocationPicker';

declare global {
  interface Window {
    analyticsData: AnalyticsData;
    // satellite required for adobe analytics on the confirmation Page
    __satelliteLoaded: boolean;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    _satellite: any;
  }
}
interface Props {
  router: NextRouter;
  defaultLocation?: string;
  searchLocation?: string;
  partialTranslations: SearchPartialTranslationsType;
  AEMTranslations: SearchAEMTranslationsType;
  locale?: string;
  screenSize: ScreenSize;
  suggestions: SearchSuggestions;
  onLocationInputChange?: (value: string | undefined) => void;
  onLocationInputFocus?: (value: string | undefined) => void;
  onLocationInputClear?: () => void;
  onIsSearchActive?: (value: boolean) => void;
  searchPriceFinderStyles: any;
  errorField?: string;
  errorMessage?: string;
  isSearchActive: boolean;
  channel?: string;
  searchDisabled?: boolean;
  marginBottom?: ResponsiveValue;
  handleLocationSearch?: (placeId: string) => void;
  setLocationName?: (locationName: string) => void;
}

const FIELDS = {
  location: 'location',
};

export default function SearchPriceFinder({
  router,
  defaultLocation,
  partialTranslations,
  AEMTranslations,
  screenSize,
  suggestions,
  onIsSearchActive,
  onLocationInputChange,
  onLocationInputClear,
  onLocationInputFocus,
  searchPriceFinderStyles,
  errorField,
  errorMessage,
  searchDisabled = false,
  handleLocationSearch,
  setLocationName,
}: Readonly<Props>) {
  const { isLessThanLg, isLessThanMd, isLessThanSm } = screenSize;
  const [location, setLocation] = useState<SearchPropertyType | SearchPlaceType | undefined>(
    undefined
  );
  const { PLACEID, searchTerm } = router.query;
  const searchParams = useSearchParams();

  const {
    searchWrapper,
    locationPickerStyles,
    inputGroupStyles,
    inputElementStyles,
    errorInputGroupStyles,
    errorInputElementStyles,
    errorMarginBottom,
    buttonStyles,
  } = searchPriceFinderStyles;

  const autocompleteStyles = {
    locationPickerStyles,
    inputGroupStyles,
    inputElementStyles,
    errorMarginBottom,
    ...(errorField === FIELDS.location && {
      errorInputGroupStyles,
      errorInputElementStyles,
    }),
  };

  const handleOnClick = () => {
    if (handleLocationSearch && setLocationName && location && 'placeId' in location) {
      handleLocationSearch(location.placeId);
      setLocationName(location?.suggestion);
      if (PLACEID && searchTerm) {
        updateQueryParams(
          { PLACEID: location.placeId, searchTerm: location?.suggestion },
          searchParams,
          router
        );
      }
      window.__satelliteLoaded && window._satellite.track('priceFinderHotelSelected');
    }
  };

  return (
    <Flex direction="column" alignItems="center">
      <Flex
        align="center"
        bgColor="baseWhite"
        {...searchWrapper}
        data-testid="search-component"
        onFocus={() => {
          if (onIsSearchActive) {
            onIsSearchActive(true);
          }
        }}
      >
        <LocationPicker
          {...locationPickerStyles}
          inputPlaceholder={
            AEMTranslations.priceFinderLocationPlaceholder || getLocationPlaceholder()
          }
          defaultInputValue={defaultLocation || searchTerm}
          isLocationRequired={true}
          hasListDivider={false}
          styles={autocompleteStyles}
          onSelectLocation={handleSelectLocation}
          suggestions={suggestions}
          onInputChange={onLocationInputChange}
          onInputClear={onLocationInputClear}
          onInputFocus={onLocationInputFocus}
          isPriceFinder={true}
          {...(errorField === FIELDS.location && {
            showErrorMessage: true,
            errorMessage: errorMessage,
          })}
        />
        <Button
          zIndex={10}
          size="md"
          variant="primary"
          name="search-button"
          data-testid="search-component-button"
          isDisabled={searchDisabled}
          {...buttonStyles}
          mt={{
            base: '0',
            sm: '0',
          }}
          onClick={handleOnClick}
        >
          {getButtonContent()}
        </Button>
      </Flex>
    </Flex>
  );

  function handleSelectLocation(location: SearchPropertyType | SearchPlaceType | undefined) {
    setLocation(location);
  }

  function getLocationPlaceholder() {
    return isLessThanLg && !isLessThanSm
      ? AEMTranslations.locationPlaceholder
      : partialTranslations?.form?.where;
  }

  function getButtonContent() {
    return isLessThanMd ? (
      <Icon svg={<SearchIcon color="var(--chakra-colors-baseWhite)" />} />
    ) : (
      AEMTranslations.submitButtonLabel
    );
  }
}

export const updateQueryParams = (newParams: any, searchParams: any, router: any) => {
  const params = new URLSearchParams(searchParams?.toString?.() ?? '');

  Object.entries(newParams).forEach(([key, value]) => {
    if (value === null || value === undefined || value === '') {
      params.delete(key);
    } else {
      params.set(key, String(value));
    }
  });

  router.push(`${window.location.pathname}?${params.toString()}`);
};
