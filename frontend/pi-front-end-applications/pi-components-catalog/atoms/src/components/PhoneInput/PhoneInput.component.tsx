import type { BoxProps, InputElementProps } from '@chakra-ui/react';
import {
  Box,
  Flex,
  FormControl,
  FormErrorMessage,
  FormLabel,
  Image,
  Input,
  InputGroup,
  InputLeftElement,
  Text,
  useDisclosure,
  useTheme,
  useOutsideClick,
} from '@chakra-ui/react';
import type { Country } from '@whitbread-eos/api';
import { CountryCode, ShortCountry } from '@whitbread-eos/api';
import {
  getInputTypographyOverride,
  getTextStyleTypographyOverride,
  useSemanticTypography,
} from '@whitbread-eos/utils';
import dynamic from 'next/dynamic';
import type { ComponentProps, ForwardedRef } from 'react';
import { forwardRef, memo, useCallback, useEffect, useMemo, useRef } from 'react';

import { Error24, Success24 } from '../../assets/icons';
import ChevronDown from '../../assets/icons/ChevronDown';
import ChevronUp from '../../assets/icons/ChevronUp';
import { formatDataTestId } from '../../utils/formatters';
import Icon from '../Icon';
import CountriesList from './CountriesList';

const Tooltip = dynamic(
  async () => {
    const { default: Tooltip } = await import('../Tooltip/Tooltip.component');
    return { default: Tooltip };
  },
  {
    ssr: false,
  }
);

export type PhoneValue = {
  countryCode: string;
  dialingCode: string;
  phone: string;
};

type Props = Omit<BoxProps, 'onChange'> & {
  countries?: Country[];
  disabled?: boolean;
  error?: string;
  formatAssetsUrl?: (path: string) => string;
  showIcon?: boolean;
  inputProps?: ComponentProps<typeof Input>;
  label?: string;
  placeholder?: string;
  currentLang?: string;
  selectProps?: Omit<ComponentProps<typeof CountriesList>, 'options' | 'onChange'>;
  name: string;
  value?: PhoneValue;
  onChange: (value: Required<Props>['value']) => void;
  onBlur: () => void;
  handleTriggerValidation?: (fieldsName: string | string[]) => void;
  dependantOn?: string | string[];
  dataTestId?: string;
  useTooltip?: boolean;
  isAltStyle?: boolean;
};

/**
 * @TODOs:
 * - add support to open the options list in the directions with the most space
 * - replace the absolute positioning approach with inline elements
 * - add getter function support
 * - find a proper identifier (there are multiple items with the same countryCode or dialingCode)
 */
function PhoneInput(
  {
    countries = [],
    disabled,
    error,
    formatAssetsUrl = (url) => url,
    showIcon,
    inputProps,
    label,
    placeholder,
    currentLang,
    selectProps,
    name,
    value = { countryCode: '', dialingCode: '', phone: '' },
    onChange,
    onBlur,
    handleTriggerValidation,
    dependantOn,
    dataTestId,
    useTooltip = false,
    isAltStyle = false,
    ...rest
  }: Props,
  forwardedRef: ForwardedRef<HTMLInputElement>
) {
  const getTypographyProps = useSemanticTypography();
  const theme = useTheme();
  const ref = useRef<HTMLDivElement>(null) as React.RefObject<HTMLDivElement>;
  const phoneInputRef = useRef<HTMLInputElement | null>(null);
  const prevIsOpenRef = useRef<boolean>(false);
  const { isOpen, onToggle, onClose } = useDisclosure();
  const displayIcon = showIcon && !disabled;
  const displayLabel = label && !disabled;
  const isValid = !error && value.phone;
  const baseDataTestId = dataTestId ?? 'PhoneInput';
  const currentCountrySite = currentLang === CountryCode.DE ? ShortCountry.DE : ShortCountry.GB;
  const inputTypographyOverride = getInputTypographyOverride(inputProps, theme?.textStyles);
  const altErrorTypography = getTypographyProps(
    { fontSize: 'xs' },
    { textStyle: 'body-s-regular' }
  );
  const errorTypography = getTypographyProps({}, { textStyle: 'body-s-regular' });
  const altErrorTypographyProps = {
    ...altErrorTypography,
    ...getTextStyleTypographyOverride(altErrorTypography, theme?.textStyles),
  };
  const errorTypographyProps = {
    ...errorTypography,
    ...getTextStyleTypographyOverride(errorTypography, theme?.textStyles),
  };

  const labelStyleAlt = () => {
    return {
      w: 'fit-content',
      fontSize: 'md',
      align: 'center',
      fontWeight: 'bold',
      zIndex: '1',
      color: '#333333',
    };
  };

  useEffect(() => {
    if (prevIsOpenRef.current && !isOpen) {
      setTimeout(() => {
        phoneInputRef.current?.focus();
      }, 0);
    }
    prevIsOpenRef.current = isOpen;
  }, [isOpen]);

  useOutsideClick({
    ref: ref,
    handler: onClose,
  });

  const sortedCountries = getSortedCountries(countries);
  const country = useMemo(
    () =>
      countries.find(
        (country) =>
          country.countryCode === value.countryCode && country.dialingCode === value.dialingCode
      ),
    [countries, value.countryCode, value.dialingCode]
  );

  const handleCountryChange = useCallback(
    (item: Country) => {
      onChange({ ...value, countryCode: item.countryCode, dialingCode: item.dialingCode });
      onClose();
    },
    [value, onChange, onClose]
  );

  const handleNumberChange = useCallback(
    (event: React.ChangeEvent<HTMLInputElement>) => {
      onChange({ ...value, phone: event.target.value });
    },
    [value, onChange]
  );

  const handleOnBlur = useCallback(() => {
    if (dependantOn && handleTriggerValidation) {
      handleTriggerValidation(dependantOn);
    }
    onBlur();
  }, [onBlur, dependantOn, handleTriggerValidation]);

  const handleKeyDown = useCallback(
    (event: React.KeyboardEvent<HTMLDivElement>) => {
      if (disabled) return;
      if (event.key === 'Enter' || event.key === ' ') {
        event.preventDefault();
        onToggle();
      } else if (event.key === 'Escape' && isOpen) {
        event.preventDefault();
        onClose();
      }
    },
    [disabled, onToggle, isOpen, onClose]
  );

  function getSortedCountries(data: Country[]) {
    if (!data || data?.length === 0) {
      return [];
    }

    const sortedData = [...data].sort(function (a: Country, b: Country) {
      // Keep UK or DE always on top
      if (a.countryCode === currentCountrySite) {
        return -1;
      }

      if (b.countryCode === currentCountrySite) {
        return 0;
      }

      if (a.countryName < b.countryName) {
        return -1;
      }

      if (a.countryName > b.countryName) {
        return 1;
      }

      return 0;
    });

    return sortedData;
  }

  return (
    <FormControl isInvalid={!!error} ref={ref} {...{ ...rest }}>
      {isAltStyle && (
        <FormLabel
          data-testid={formatDataTestId(baseDataTestId, `${name}-label`)}
          {...labelStyleAlt()}
          htmlFor={name}
        >
          {label}
        </FormLabel>
      )}

      <Tooltip
        {...tooltipStyle}
        description={error ?? ''}
        variant="inlineError"
        placement="bottom-start"
        isOpen={!!error && useTooltip}
        svg={<Error24 />}
      >
        <InputGroup>
          <InputLeftElement
            data-testid={formatDataTestId(baseDataTestId, 'countrySelector')}
            {...selectorStyle({ disabled })}
            role="button"
            type="button"
            tabIndex={disabled ? -1 : 0}
            onClick={onToggle}
            onKeyDown={handleKeyDown}
            aria-label={
              country
                ? `Change country code. Currently ${country.countryName} ${country.dialingCode}`
                : 'Select country code'
            }
            aria-expanded={isOpen}
          >
            {country && (
              <Flex data-testid={formatDataTestId(baseDataTestId, 'countryFlag')}>
                <Image
                  borderRadius="full"
                  boxSize="1.5rem"
                  src={formatAssetsUrl(country.flagSrc)}
                  alt={country.countryName}
                  title={country.countryName}
                />
                <Text as="span" ml={1} whiteSpace="nowrap">
                  {country.dialingCode}
                </Text>
              </Flex>
            )}
            <Icon
              mx="2"
              svg={
                isOpen ? (
                  <ChevronUp aria-label="chevron-icon-up" />
                ) : (
                  <ChevronDown aria-label="chevron-icon-down" />
                )
              }
            />
          </InputLeftElement>
          {!isAltStyle && displayLabel && (
            <FormLabel
              data-testid={formatDataTestId(baseDataTestId, `${name}-label`)}
              pos="absolute"
              {...labelStyle(error, value)}
              htmlFor={name}
            >
              {label}
            </FormLabel>
          )}

          <Input
            id={name}
            name={name}
            aria-label={label || placeholder || 'Phone number'}
            disabled={disabled}
            isInvalid={!!error}
            placeholder={!isAltStyle ? (placeholder ?? label) : undefined}
            ref={(el) => {
              phoneInputRef.current = el;
              if (typeof forwardedRef === 'function') {
                forwardedRef(el);
              } else if (forwardedRef) {
                forwardedRef.current = el;
              }
            }}
            type="tel"
            value={value.phone}
            {...(isAltStyle ? inputStyleAlt(error) : inputStyle(error))}
            onChange={handleNumberChange}
            onBlur={handleOnBlur}
            {...inputProps}
            {...inputTypographyOverride}
            data-testid={formatDataTestId(baseDataTestId, 'phoneNumber')}
          />
          {displayIcon && error && (
            <Box {...iconStyle} data-testid={formatDataTestId(baseDataTestId, 'inputIconError')}>
              <Error24 />
            </Box>
          )}
          {displayIcon && isValid && (
            <Box {...iconStyle} data-testid={formatDataTestId(baseDataTestId, 'inputIconSuccess')}>
              <Success24 />
            </Box>
          )}
        </InputGroup>
      </Tooltip>

      {isAltStyle && error && !useTooltip && (
        <FormErrorMessage ml="xs" {...altErrorTypographyProps}>
          {error}
        </FormErrorMessage>
      )}
      {!isAltStyle && error && !useTooltip && (
        <FormErrorMessage ml="md" {...errorTypographyProps}>
          {error}
        </FormErrorMessage>
      )}
      {isOpen ? (
        <CountriesList
          formatAssetsUrl={formatAssetsUrl}
          options={sortedCountries}
          onChange={handleCountryChange}
          onClose={onClose}
          {...selectProps}
        />
      ) : null}
    </FormControl>
  );
}

const inputStyle = (error: string | undefined) => {
  return {
    h: '3.5rem',
    focusBorderColor: 'primary',
    pl: '6.75em',
    borderColor: 'var(--chakra-colors-lightGrey1)',
    _placeholder: {
      color: 'var(--chakra-colors-darkGrey2)',
    },
    _hover: { borderColor: 'none' },
    _focus: { zIndex: '0', borderWidth: '2px', borderColor: error ? 'error' : 'primary' },
    _autofill: {
      boxShadow: '0 0 0 1000px #FFFFFF inset',
    },
  };
};

const inputStyleAlt = (error: string | undefined) => {
  return {
    h: '3rem',
    backgroundColor: 'var(--chakra-colors-gray-100)',
    focusBorderColor: error ? 'error' : 'primary',
    zIndex: 0,
    pl: '6.75em',
    borderWidth: '0px',
    borderColor: 'var(--chakra-colors-lightGrey1)',
    _hover: { borderColor: 'none' },
    _focus: { zIndex: '0', borderWidth: '1px', borderColor: error ? 'error' : 'primary' },

    _placeholder: {
      color: 'darkGrey2',
      _focus: { color: 'success' },
      _hover: { color: 'primary' },
    },
    _autofill: {
      boxShadow: '0 0 0 1000px #FFFFFF inset',
    },
  };
};

const selectorStyle = ({ disabled }: { disabled?: boolean }): InputElementProps => ({
  cursor: 'pointer',
  h: 'full',
  justifyContent: 'flex-end',
  pl: '2',
  width: '6.5em',
  ...(disabled ? { opacity: '0.4', pointerEvents: 'none' } : {}),
});

const iconStyle = {
  ml: 'md',
  alignSelf: 'center',
};

const tooltipStyle = {
  h: '2.25rem',
  display: 'flex',
  alignContent: 'center',
};

const labelStyle = (error: string | undefined, value: PhoneValue | undefined) => {
  return {
    h: '1.25rem',
    w: 'fit-content',
    fontSize: 'sm',
    align: 'center',
    px: 'xs',
    ml: '0.750rem',
    top: '-0.625rem',
    backgroundColor: 'baseWhite',
    zIndex: '1',
    color: error ? 'error' : 'darkGrey1',
    display: value?.phone ? 'block' : 'none',
    _focus: { color: error ? 'error' : 'primary', display: 'block' },
  };
};

export default memo(forwardRef(PhoneInput)) as any;
