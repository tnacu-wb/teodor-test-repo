'use client';

import { Language, IBFormSelectOption } from '@whitbread-eos/api';
import { GLOBALS, formatIBAssetsUrl } from '@whitbread-eos/utils';
import { getCountriesList } from '@whitbread-eos/utils/server';
import * as React from 'react';
import { useState, useEffect } from 'react';

import { FormSelect } from '../FormSelect/index';

interface IBFormCountryType {
  countryName: string;
  flagSrc: string;
  countryCode: string;
}

interface IBFormCountriesProps {
  language: Language;
  placeholder: string;
  id: string;
  disabled?: boolean;
  initialCountry: string;
  onChange?: (value: string) => void;
  arrowIcon: string;
  className: string;
  name?: string;
}

const FormCountries = React.forwardRef<HTMLButtonElement, IBFormCountriesProps>(
  (
    {
      language,
      className,
      placeholder,
      id,
      disabled = false,
      onChange = () => {
        return;
      },
      arrowIcon,
      initialCountry,
      name = '',
      ...rest
    },
    ref
  ) => {
    const [options, setOptions] = useState<Array<IBFormSelectOption>>([]);
    const [selectedOption, setSelectedOption] = useState<IBFormSelectOption>({
      value: '',
      displayValue: '',
    });
    const [isLoading, setIsLoading] = useState(true);
    const countryByLanguage =
      language === GLOBALS.language.EN ? GLOBALS.localeUpper.GB : GLOBALS.localeUpper.DE;
    const [defaultCountry, setDefaultCountry] = useState<string>(
      initialCountry || countryByLanguage
    );

    useEffect(() => {
      if (initialCountry) {
        setDefaultCountry(initialCountry);
      }
    }, [initialCountry]);

    useEffect(() => {
      const fetchListOfCountries = async () => {
        const countriesList = await getCountriesList(language);
        if (!countriesList) {
          return;
        }
        const sortedCountriesList = sortOptions(countriesList);
        const formattedOptions = sortedCountriesList.map((option) => ({
          displayValue: option.countryName,
          value: option.countryCode,
          icon: formatIBAssetsUrl(option.flagSrc),
        }));
        setIsLoading(false);
        setOptions(formattedOptions);
      };
      if (!disabled) {
        fetchListOfCountries();
      }
    }, []);

    useEffect(() => {
      if (options.length !== 0) {
        const defaultOption = options.find(
          (option: IBFormSelectOption) => option.value === defaultCountry
        );

        if (defaultOption) {
          onChange(defaultOption.value);
          setSelectedOption(defaultOption);
        }
      }
    }, [options, defaultCountry]);

    const handleChange = (country: IBFormSelectOption) => {
      onChange(country.value);
      setSelectedOption(country);
    };

    const sortOptions = (options: Array<IBFormCountryType>) => {
      return options.sort((a, b) => {
        if (a.countryCode === defaultCountry) return -1;
        if (b.countryCode === defaultCountry) return 1;
        return a.countryName.localeCompare(b.countryName);
      });
    };

    return (
      <FormSelect
        {...rest}
        ref={ref}
        id={id}
        placeholder={placeholder}
        arrowIcon={arrowIcon}
        className={className}
        options={options}
        onChange={handleChange}
        disabled={disabled}
        name={name}
        value={selectedOption}
        errorIcon={''}
        isLoading={isLoading}
      />
    );
  }
);
FormCountries.displayName = 'FormCountries';

export { FormCountries };
