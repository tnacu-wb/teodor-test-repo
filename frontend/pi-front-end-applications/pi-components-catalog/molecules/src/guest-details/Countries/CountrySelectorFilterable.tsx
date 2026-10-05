import { type TextProps } from '@chakra-ui/react';
import { Country, GET_COUNTRIES, SITE_LEISURE } from '@whitbread-eos/api';
import { type FormDynamicFieldCompProps, CountryInput } from '@whitbread-eos/atoms';
import {
  formatAssetsUrl,
  useQueryRequest,
  useCustomLocale,
  useSemanticTypography,
} from '@whitbread-eos/utils';
import { useEffect, useState, useRef } from 'react';

export type CountryValue = { countryCode: string };

export default function CountrySelectorFilterable({
  formField,
  field,
  selectedId,
  setIsLocationRequired,
}: Readonly<FormDynamicFieldCompProps> & {
  selectedId?: string;
  setIsLocationRequired?: (isRequired: boolean) => void;
}) {
  const { language: currentLang, country: currentCountry } = useCustomLocale();
  const getTypographyProps = useSemanticTypography();
  const [countries, setCountries] = useState<Country[]>([]);
  const [selectedCountry, setSelectedCountry] = useState<Country | undefined>();
  const [countryCodeValue, setCountryCodeValue] = useState<CountryValue>({ countryCode: '' });
  const hasSetDefault = useRef(false);

  const testId = formField.testid ?? 'CountrySelectorFilterable';

  const { data: countriesData, isSuccess: countriesRequestSuccess } = useQueryRequest(
    ['GetCountries', currentCountry, currentLang, SITE_LEISURE],
    GET_COUNTRIES,
    { country: currentCountry, language: currentLang, site: SITE_LEISURE }
  );

  // Reset hasSetDefault if the value is cleared
  useEffect(() => {
    if (!field.value) {
      hasSetDefault.current = false;
    } else {
      setIsLocationRequired?.(field.value === 'DE');
    }
  }, [field.value, setIsLocationRequired]);

  // Set default country by code or legacy code
  // in case user registered with old country code, e.g. A for Austria countryCodeLegacy v AT for countryCode (current)
  function setDefaultCountry(countries: Country[], code?: string) {
    let country = countries.find((c) => c.countryCode === code);
    if (!country && code) {
      country = countries.find((c) => c.countryCodeLegacy === code);
    }
    if (country) {
      setSelectedCountry(country);
      setCountryCodeValue({ countryCode: country.countryCode });
      setIsLocationRequired?.(country.countryCode === 'DE');
      field.onChange?.(country.countryCode);
    }
  }

  // Load countries and set default if needed
  useEffect(() => {
    if (!countriesRequestSuccess) return;
    const loadedCountries = countriesData?.countries?.countries || [];
    setCountries((prev) => {
      // Only update state if the countries have actually changed to avoid unnecessary re-renders
      const prevCodes = prev.map((c) => c.countryCode).join(',');
      const newCodes = loadedCountries.map((c: Country) => c.countryCode).join(',');
      return prevCodes === newCodes ? prev : loadedCountries;
    });

    // Only set default country if user has not already selected one
    if (!field.value && !hasSetDefault.current) {
      hasSetDefault.current = true;
      setDefaultCountry(loadedCountries, selectedId || (currentLang === 'en' ? 'GB' : 'DE'));
    }
  }, [countriesRequestSuccess, countriesData, selectedId, currentLang, field.value]);

  // When field.value changes (from outside, e.g. postcode lookup), update selected country
  useEffect(() => {
    if (!countries.length || !field.value) return;
    const found = countries.find((c) => c.countryCode === field.value);
    setSelectedCountry(found);
    setIsLocationRequired?.(found?.countryCode === 'DE');
    if (found) setCountryCodeValue({ countryCode: found.countryCode });
  }, [field.value, countries, setIsLocationRequired]);

  const onChange = (countryValue: CountryValue) => {
    setCountryCodeValue(countryValue);
    setIsLocationRequired?.(countryValue.countryCode === 'DE');
    field.onChange?.(countryValue.countryCode);
  };

  const countrySelectedValueTypographyStyles = getTypographyProps(
    {},
    countrySelectedValueSemanticTypography
  );

  const countryOptionsTypographyStyles = getTypographyProps({}, countryOptionsSemanticTypography);

  const countrySearchInputTypographyStyles = getTypographyProps(
    {},
    countrySearchInputSemanticTypography
  );

  return (
    <CountryInput
      value={countryCodeValue}
      label={formField.label}
      name={formField.name}
      dataTestId={testId}
      onChange={onChange}
      countries={countries}
      formatAssetsUrl={formatAssetsUrl}
      placeholder={selectedCountry ? selectedCountry.countryName : formField.label}
      currentLang={currentLang}
      inputProps={{
        className: formField?.props?.className,
        ...countrySelectedValueTypographyStyles,
      }}
      selectProps={{
        optionTextStyles: countryOptionsTypographyStyles,
        searchInputTextStyles: countrySearchInputTypographyStyles,
      }}
      useTooltip={formField?.props?.useTooltip}
    />
  );
}

const countrySelectedValueSemanticTypography = {
  textStyle: 'body-m-regular',
} as TextProps;

const countryOptionsSemanticTypography = {
  textStyle: 'body-s-regular',
} as TextProps;

const countrySearchInputSemanticTypography = {
  textStyle: 'body-s-regular',
} as TextProps;
