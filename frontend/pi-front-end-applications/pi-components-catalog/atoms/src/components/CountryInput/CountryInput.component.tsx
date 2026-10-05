import type { BoxProps, InputElementProps } from '@chakra-ui/react';
import {
  Flex,
  FormControl,
  FormLabel,
  Image,
  Input,
  InputGroup,
  InputLeftElement,
  useTheme,
  useDisclosure,
  useOutsideClick,
} from '@chakra-ui/react';
import type { Country } from '@whitbread-eos/api';
import {
  getInputTypographyOverride,
  getSortedCountriesByCurrentLang,
  GLOBALS,
} from '@whitbread-eos/utils';
import dynamic from 'next/dynamic';
import type { ComponentProps, ForwardedRef } from 'react';
import { forwardRef, memo, useCallback, useMemo, useRef } from 'react';

import ChevronDown from '../../assets/icons/ChevronDown';
import ChevronUp from '../../assets/icons/ChevronUp';
import { formatDataTestId } from '../../utils/formatters';
import Icon from '../Icon';
import CountriesList from '../PhoneInput/CountriesList';

const Tooltip = dynamic(
  async () => {
    const { default: Tooltip } = await import('../Tooltip/Tooltip.component');
    return { default: Tooltip };
  },
  {
    ssr: false,
  }
);

type Props = Omit<BoxProps, 'onChange'> & {
  countries?: Country[];
  disabled?: boolean;
  error?: string;
  formatAssetsUrl?: (path: string) => string;
  inputProps?: ComponentProps<typeof Input>;
  label?: string;
  placeholder?: string;
  currentLang?: string;
  selectProps?: Omit<ComponentProps<typeof CountriesList>, 'options' | 'onChange'>;
  name: string;
  value?: Country;
  onChange: (country: Country) => void;
  dataTestId?: string;
  useTooltip?: boolean;
};

function CountryInput(
  {
    countries = [],
    disabled,
    error,
    formatAssetsUrl = (url) => url,
    inputProps,
    label,
    placeholder,
    currentLang,
    selectProps,
    name,
    value,
    onChange,
    dataTestId,
    useTooltip = false,
    ...rest
  }: Props,
  forwardedRef: ForwardedRef<HTMLInputElement>
) {
  const theme = useTheme();
  const ref = useRef<HTMLElement>(null) as React.RefObject<HTMLElement>;
  const { isOpen, onToggle, onClose } = useDisclosure();
  const displayLabel = label && !disabled;
  const baseDataTestId = dataTestId;
  const inputTypographyOverride = getInputTypographyOverride(inputProps, theme?.textStyles);

  useOutsideClick({
    ref: ref,
    handler: () => onClose(),
  });

  const language = currentLang || GLOBALS.language.EN;

  const country = useMemo(
    () => countries.find((c) => c.countryCode === value?.countryCode),
    [countries, value]
  );

  const sortedCountries = getSortedCountriesByCurrentLang(countries, language);
  const handleCountryChange = useCallback(
    (item: Country) => {
      onChange(item);
      onClose();
    },
    [onChange, onClose]
  );

  return (
    <FormControl {...rootStyle} isInvalid={!!error} ref={ref} {...{ ...rest }}>
      <Tooltip
        {...tooltipStyle}
        description={error ?? ''}
        variant="inlineError"
        placement="bottom-start"
        isOpen={useTooltip}
      >
        <>
          <InputGroup tabIndex={0} sx={{ ...groupInputStyles }}>
            <InputLeftElement
              data-testid={formatDataTestId(baseDataTestId, 'countrySelector')}
              {...selectorStyle()}
              role="button"
              type="button"
              onClick={onToggle}
            >
              {country && (
                <Flex data-testid={formatDataTestId(baseDataTestId, 'countryFlag')} pl={'xmd'}>
                  <Image
                    borderRadius="full"
                    boxSize="1.5rem"
                    src={formatAssetsUrl(country.flagSrc)}
                    title={country.countryName}
                  />
                </Flex>
              )}
            </InputLeftElement>
            {displayLabel && (
              <FormLabel
                data-testid={formatDataTestId(baseDataTestId, `${name}-label`)}
                pos="absolute"
                {...labelStyle()}
                htmlFor={name}
              >
                {label}
              </FormLabel>
            )}
            <Input
              disabled={disabled}
              placeholder={placeholder ?? label}
              ref={forwardedRef}
              name={name}
              value={country?.countryName ?? ''}
              readOnly
              {...inputProps}
              {...inputTypographyOverride}
              {...countryInputStyles}
              data-testid={formatDataTestId(baseDataTestId, 'countryName')}
            />
            <Icon
              onClick={onToggle}
              mx="2"
              svg={
                isOpen ? (
                  <ChevronUp aria-label="chevron-icon-up" />
                ) : (
                  <ChevronDown aria-label="chevron-icon-down" />
                )
              }
              {...countryInputIconStyles}
            />
          </InputGroup>
        </>
      </Tooltip>
      {isOpen ? (
        <CountriesList
          formatAssetsUrl={formatAssetsUrl}
          options={sortedCountries}
          onChange={handleCountryChange}
          showDialingCode={false}
          {...selectProps}
        />
      ) : null}
    </FormControl>
  );
}

const countryInputStyles: InputElementProps = {
  h: '3.5rem',
  borderRadius: 'md',
  borderColor: 'lightGrey1',
  _focus: {
    borderColor: 'primary',
    boxShadow: '0 0 0 1px var(--chakra-colors-primary)',
  },
  paddingLeft: '3.2rem',
};

const countryInputIconStyles: InputElementProps = {
  cursor: 'pointer',
  position: 'absolute',
  right: '0.5rem',
  top: '50%',
  transform: 'translateY(-50%)',
  color: 'darkGrey1',
  _hover: { color: 'primary' },
  _focus: { color: 'primary' },
};

const selectorStyle = (): InputElementProps => ({
  cursor: 'pointer',
  h: 'full',
  justifyContent: 'flex-start',
  pl: '2',
  width: '100%',
});

const groupInputStyles = {
  borderRadius: 'md',
  '&:focus-within': {
    borderColor: 'var(--chakra-colors-primary)',
    borderWidth: '1px',
    boxShadow: '0 0 0 1px var(--chakra-colors-primary)',
  },
};

const tooltipStyle = {
  h: '2.25rem',
  display: 'flex',
  alignContent: 'center',
};

const labelStyle = () => {
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
    color: 'darkGrey1',
    display: 'block',
    _focus: { color: 'primary', display: 'block' },
  };
};

const rootStyle = {
  maxW: '26.25rem',
};

export default memo(forwardRef(CountryInput)) as any;
