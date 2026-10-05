import { CloseIcon, ChevronDownIcon } from '@chakra-ui/icons';
import {
  Box,
  BoxProps,
  FormControl,
  FormLabel,
  List,
  ListItem,
  Spinner,
  StyleProps,
  Text,
  ListProps,
  IconButton,
  FormHelperText,
} from '@chakra-ui/react';
import type { SearchSuggestions } from '@whitbread-eos/api';
import { getSuggestions } from '@whitbread-eos/api';
import { usePromoTranslation } from '@whitbread-eos/utils';
import debounce from 'lodash/debounce';
import { useEffect, useRef, useState } from 'react';
import type {
  ControllerRenderProps,
  FieldErrors,
  UseFormClearErrors,
  UseFormSetValue,
} from 'react-hook-form';

import type { FormField } from './types';

interface HotelIdAutocompleteProps {
  formField: FormField;
  field: ControllerRenderProps<any>;
  errors: FieldErrors<any>;
  handleSetValue?: UseFormSetValue<any>;
  handleSetError: (name: string, error: { type: string; message: string }) => void;
  handleClearErrors?: UseFormClearErrors<any>;
}
export interface HotelProperty {
  code: string;
  brand: string;
  suggestion: string;
  geometry?: {
    type: string;
    coordinates: [number, number];
  };
}

const MIN_LENGTH_SEARCH_TERM = 3;
const SEARCH_DEBOUNCE_DELAY = 600;

export default function HotelIdAutocomplete({
  formField,
  field,
  errors,
  handleSetValue,
  handleSetError,
  handleClearErrors,
}: Readonly<HotelIdAutocompleteProps>) {
  const t = usePromoTranslation();

  const hotelIdErrorLabel = t.hotelIdRequiredError;

  const { label } = formField;
  const { name, value } = field;

  const [inputValue, setInputValue] = useState<string>(value || '');
  const [suggestions, setSuggestions] = useState<HotelProperty[]>([]);
  const [isOpen, setIsOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [selectedHotel, setSelectedHotel] = useState<HotelProperty | null>(null);
  const [placeholder, setPlaceholder] = useState(t.hotelIdLabel);

  const hotelIdError = !!errors?.[name];
  const containerRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);
  const hasSelectedHotel = useRef(false);

  const debouncedSearch = useRef(debounce(searchHotels, SEARCH_DEBOUNCE_DELAY)).current;

  useEffect(() => {
    if (typeof value === 'string' && value.trim() !== '') {
      hasSelectedHotel.current = true;
      setSelectedHotel({ code: value, brand: '', suggestion: '' });
      setInputValue(value);
    }
  }, [value]);

  useEffect(() => {
    setPlaceholder(t.hotelIdLabel);
  }, [t.hotelIdLabel]);

  useEffect(() => {
    return () => {
      debouncedSearch.cancel();
    };
  }, [debouncedSearch]);

  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (containerRef.current && !containerRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  async function searchHotels(searchTerm: string) {
    if (!searchTerm || searchTerm.trim().length < MIN_LENGTH_SEARCH_TERM) {
      setSuggestions([]);
      setIsLoading(false);
      return;
    }

    setIsLoading(true);
    try {
      const data: SearchSuggestions = await getSuggestions(searchTerm);
      const properties = (data?.properties ?? []) as unknown as HotelProperty[];

      if (properties.length === 0) {
        handleSetError?.(name, { type: 'custom', message: hotelIdErrorLabel });
      } else {
        handleClearErrors?.(name);
      }

      setSuggestions(properties);
      setIsOpen(true);
    } catch {
      handleSetError?.(name, { type: 'custom', message: hotelIdErrorLabel });
      setSuggestions([]);
    } finally {
      setIsLoading(false);
    }
  }

  function handleInputBlur() {
    setPlaceholder(t.hotelIdLabel);
    if (!hasSelectedHotel.current) {
      setInputValue('');
      handleSetValue?.(name, '');
      handleSetError?.(name, {
        type: 'custom',
        message: hotelIdErrorLabel,
      });
    }
    setSuggestions([]);
    setIsOpen(false);
  }

  function handleInputChange(newValue: string) {
    setSelectedHotel(null);
    hasSelectedHotel.current = false;

    setInputValue(newValue);
    handleSetValue?.(name, '');

    if (newValue.trim() === '') {
      setSuggestions([]);
      setIsOpen(false);
      handleSetError?.(name, {
        type: 'custom',
        message: hotelIdErrorLabel,
      });

      return;
    }

    debouncedSearch(newValue);
  }

  function handleSelectHotel(hotel: HotelProperty) {
    hasSelectedHotel.current = true;
    setSelectedHotel(hotel);

    setInputValue(hotel.code);
    handleSetValue?.(name, hotel.code);
    handleClearErrors?.(name);

    setSuggestions([]);
    setIsOpen(false);
  }

  function handleClearSelection() {
    hasSelectedHotel.current = false;
    setSelectedHotel(null);
    setInputValue('');
    setSuggestions([]);
    setIsOpen(false);

    handleSetValue?.(name, '', {
      shouldDirty: true,
      shouldTouch: true,
      shouldValidate: true,
    });

    handleSetError?.(name, {
      type: 'custom',
      message: hotelIdErrorLabel,
    });
  }

  function handleInputFocus() {
    setPlaceholder(t.hotelIdPlaceholder);
    if (suggestions.length > 0) {
      setIsOpen(true);
    }
  }

  return (
    <Box
      ref={containerRef}
      pos="relative"
      {...wrapperStyles}
      data-testid={`HotelIdAutocomplete-${name}-Container`}
    >
      <FormControl isInvalid={hotelIdError}>
        {label && (
          <FormLabel
            data-testid={`HotelIdAutocomplete-${name}-label`}
            pos="absolute"
            {...labelStyle(hotelIdError, inputValue)}
            htmlFor={name}
          >
            {label}
          </FormLabel>
        )}

        <Box
          as="input"
          ref={inputRef}
          id={name}
          name={name}
          value={inputValue}
          placeholder={placeholder}
          aria-label={label as string}
          onChange={(e: React.ChangeEvent<HTMLInputElement>) => handleInputChange(e.target.value)}
          onFocus={handleInputFocus}
          onBlur={handleInputBlur}
          autoComplete="off"
          data-testid={`HotelIdAutocomplete-${name}-input`}
          {...inputStyles}
          {...errorInputStyles(hotelIdError)}
        />
        {!isLoading && !inputValue && (
          <ChevronDownIcon
            position="absolute"
            right="0.75rem"
            top="1.8rem"
            transform="translateY(-50%)"
            color="gray.500"
            pointerEvents="none"
            boxSize={6}
          />
        )}
        {isLoading ? (
          <Spinner
            size="sm"
            pos="absolute"
            right="0.75rem"
            top="1.2rem"
            data-testid={`HotelIdAutocomplete-${name}-spinner`}
          />
        ) : (
          selectedHotel && (
            <IconButton
              aria-label="Clear hotel"
              icon={<CloseIcon boxSize={2.5} />}
              size="xs"
              variant="ghost"
              pos="absolute"
              right="0.1rem"
              top="0.45rem"
              data-testid={`HotelIdAutocomplete-${name}-clear`}
              onMouseDown={(e) => {
                e.preventDefault();
                handleClearSelection();
                inputRef.current?.focus();
              }}
            />
          )
        )}
        <FormHelperText ml="4" mt="2" data-testid={`HotelIdAutocomplete-${name}-helper`}>
          {t.hotelIdHint}
        </FormHelperText>

        {hotelIdError && (
          <Text
            color="error"
            fontSize="xs"
            mt="2"
            ml="4"
            data-testid={`HotelIdAutocomplete-${name}-Error`}
          >
            {errors?.[name]?.message as string}
          </Text>
        )}
      </FormControl>

      {isOpen && suggestions.length > 0 && (
        <List {...dropdownStyles} data-testid={`HotelIdAutocomplete-${name}-Suggestions`}>
          {suggestions.map((hotel) => (
            <ListItem
              key={hotel.code}
              px="md"
              py="sm"
              cursor="pointer"
              _hover={{ backgroundColor: 'var(--chakra-colors-lightGrey4)' }}
              onMouseDown={(e) => {
                e.preventDefault();
                handleSelectHotel(hotel);
              }}
              data-testid={`HotelIdAutocomplete-Option-${hotel.code}`}
            >
              <Text fontWeight="500" fontSize="sm">
                {hotel.code}
              </Text>
              <Text fontSize="xs" color="darkGrey1">
                {hotel.suggestion} · {hotel.brand}
              </Text>
            </ListItem>
          ))}
        </List>
      )}
    </Box>
  );
}

const wrapperStyles = {
  w: {
    lg: '24.5rem',
    xl: '26.25rem',
  },
  p: 0,
} as StyleProps;

const inputStyles = {
  w: '100%',
  height: 'var(--chakra-space-4xl)',
  border: '0.063rem solid var(--chakra-colors-lightGrey1)',
  borderRadius: '0.25rem',
  px: 'md',
  fontSize: 'sm',
  outline: 'none',
  color: 'baseBlack',

  _placeholder: {
    color: 'darkGrey2',
  },
} as BoxProps;

const errorInputStyles = (hotelIdError: boolean) => {
  return hotelIdError
    ? ({
        borderColor: 'var(--chakra-colors-error)',
        boxShadow: '0 0 0 1px var(--chakra-colors-error)',
      } as BoxProps)
    : {};
};

const dropdownStyles = {
  pos: 'absolute',
  top: '3.5rem',
  left: 0,
  right: 0,
  mt: '0.25rem',
  maxH: '16rem',
  overflowY: 'auto',
  bg: 'baseWhite',
  border: '0.063rem solid var(--chakra-colors-lightGrey1)',
  borderRadius: 'md',
  boxShadow: 'md',
  zIndex: 10,
} satisfies ListProps;

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
