import { Box } from '@chakra-ui/react';
import type { SearchBrandType, SearchLocationType, SearchSuggestions } from '@whitbread-eos/api';
import type { AutocompleteStyleProps } from '@whitbread-eos/atoms';
import {
  AutocompleteLocation,
  Error24,
  Icon,
  LogoHubSimple,
  LogoZipSimple,
  PremierInnLogo,
} from '@whitbread-eos/atoms';
import dynamic from 'next/dynamic';
import { CSSProperties } from 'react';

const Tooltip = dynamic(
  async () => {
    const { Tooltip } = await import('@whitbread-eos/atoms');
    return { default: Tooltip };
  },
  {
    ssr: false,
  }
);

const MAX_ITEMS_PER_SECTION = 5;

interface Props {
  inputPlaceholder: string;
  inputvalue: string;
  suggestions: SearchSuggestions;
  styles: AutocompleteStyleProps;
  showError: boolean;
  errorLabel: string;
  onInputChange: (value: string | undefined) => void;
  onSelectLocation: (params: SearchLocationType) => void;
  onClearInput?: () => void;
  onInputFocus?: () => void;
  onInputBlur?: () => void;
}

const SearchBookingsLocationPicker = ({
  inputPlaceholder,
  inputvalue,
  suggestions,
  styles,
  showError,
  errorLabel,
  onInputChange,
  onSelectLocation,
  onClearInput,
  onInputFocus,
  onInputBlur,
}: Props) => {
  const hasClearIcon = !!inputvalue;

  return (
    <Tooltip
      {...tooltipStyle}
      description={errorLabel}
      variant="inlineError"
      placement="bottom-start"
      isDisabled={!showError}
      isOpen={showError}
      svg={<Error24 />}
    >
      <Box {...styles.locationPickerStyles}>
        <AutocompleteLocation
          dataTestId="SearchBookingsLocationPicker"
          items={mapItemsForAutocomplete(suggestions)}
          inputPlaceholder={inputPlaceholder}
          inputSelectedValue={inputvalue}
          onSelectOption={handleLocationChange}
          onInputChange={onInputChange}
          onClearInput={onClearInput}
          onBlurInput={onInputBlur}
          onFocusInput={onInputFocus}
          hasClearIcon={hasClearIcon}
          openListOnFocus={false}
          disableInternalFilter={true}
          autocompleteStyles={{
            ...styles,
          }}
        />
      </Box>
    </Tooltip>
  );

  function handleLocationChange(location: string) {
    const selectedLocation = getSelectedItem(location);
    onSelectLocation?.(selectedLocation);
  }

  function getSelectedItem(item: string) {
    const items = [...suggestions.managedPlaces, ...suggestions.places, ...suggestions.properties];
    const suggestion = items.find(
      (element) => element?.suggestion?.replace(/\//g, ' ') === item?.replace(/\//g, ' ')
    );

    return suggestion ?? items[0];
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
};

export default SearchBookingsLocationPicker;

const tooltipStyle = {
  h: '2.25rem',
  display: 'flex',
  alignContent: 'center',
};
