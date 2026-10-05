import { Image, type TextProps } from '@chakra-ui/react';
import { Country, GET_COUNTRIES } from '@whitbread-eos/api';
import { Dropdown, DropdownCustomContent, DropdownStyles } from '@whitbread-eos/atoms';
import {
  formatAssetsUrl,
  getSortedCountriesByCurrentLang,
  useCustomLocale,
  useQueryRequest,
  useSemanticTypography,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { memo, useEffect, useState } from 'react';

interface Props {
  onChange: (val: string) => void;
  hasError: boolean;
  selectedId?: string;
  showIcon?: boolean;
  styles: DropdownStyles;
  isDisabled: boolean;
}

function CountrySelector({
  onChange,
  hasError,
  selectedId,
  showIcon = true,
  styles,
  isDisabled,
}: Readonly<Props>) {
  const { t } = useTranslation();
  const { language: currentLang, country: currentCountry } = useCustomLocale();
  const getTypographyProps = useSemanticTypography();

  const emptyCountry: Country = {
    countryCode: '',
    countryCodeLegacy: '',
    countryName: '',
    passportRequired: false,
    dialingCode: '',
    flagSrc: '',
  };

  const [countries, setCountries] = useState<Country[]>();
  const [selectedCountry, setSelectedCountry] = useState<Country>(emptyCountry);

  const { data: countriesData, isSuccess: countriesRequestSuccess } = useQueryRequest(
    ['GetCountries', currentCountry, currentLang, 'leisure'],
    GET_COUNTRIES,
    {
      country: currentCountry,
      language: currentLang,
      site: 'leisure',
    }
  );

  const [isDropdownOpen, setIsDropdownOpen] = useState(false);

  const labelTypographyStyles = getTypographyProps(
    countryLabelLegacyTypography,
    countryLabelSemanticTypography
  );

  const optionsTypographyStyles = getTypographyProps({}, countryOptionsSemanticTypography);

  useEffect(() => {
    if (countriesRequestSuccess && !countries?.length) {
      const sortedCountries = getSortedCountriesByCurrentLang(
        countriesData?.countries?.countries,
        currentLang
      );
      setCountries(sortedCountries || []);
      if (selectedId) {
        setDefaultCountry(sortedCountries, selectedId);
      } else {
        setDefaultCountry(sortedCountries, currentLang === 'en' ? 'GB' : 'DE');
      }
    }
  }, [countriesRequestSuccess, countriesData, selectedId]);

  return (
    <Dropdown
      dropdownStyles={{
        menuListStyles,
        ...styles,
        labelTextStyles: labelTypographyStyles,
        menuItemTextStyles: optionsTypographyStyles,
      }}
      dataTestId="Country"
      label={t('booking.country')}
      placeholder={
        countries?.find((country: Country) => country.countryCode === selectedCountry.countryCode)
          ?.countryName
      }
      isOpenMenu={isDropdownOpen}
      showStatusIcon={showIcon}
      icon={
        <Image
          alt={selectedCountry?.countryName}
          borderRadius="full"
          boxSize="1.5rem"
          src={formatAssetsUrl(selectedCountry?.flagSrc)}
        />
      }
      onChange={(country: any) => {
        setSelectedCountry(country);
        setIsDropdownOpen(false);
        onChange(country.id);
      }}
      hasError={hasError}
      options={countries?.map(({ countryCode, countryName, flagSrc }: Country) => ({
        label: countryName,
        id: countryCode,
        icon: (
          <Image
            alt={countryName}
            borderRadius="full"
            boxSize="1.5rem"
            src={formatAssetsUrl(flagSrc)}
          />
        ),
      }))}
      selectedId={selectedId}
      disabled={isDisabled}
    >
      {DropdownCustomContent}
    </Dropdown>
  );

  function setDefaultCountry(countries: any, countryCode: Country['countryCode']) {
    const country = countries?.find((country: Country) => country.countryCode === countryCode);
    if (!country && selectedId) {
      setDefaultCountryByLegacyCode(countries, selectedId);
    } else {
      setSelectedCountry(country || emptyCountry);
      onChange(country ? country.countryCode : {});
    }
  }

  function setDefaultCountryByLegacyCode(
    countries: any,
    countryCode: Country['countryCodeLegacy']
  ) {
    const country = countries?.find(
      (country: Country) => country.countryCodeLegacy === countryCode
    );
    setSelectedCountry(country || emptyCountry);
    onChange(country ? country.countryCode : {});
  }
}

const menuListStyles = {
  maxH: 'var(--chakra-space-52)',
};

const countryLabelLegacyTypography = {
  fontSize: 'var(--chakra-fontSizes-sm)',
  lineHeight: 'var(--chakra-lineHeights-1)',
} as TextProps;

const countryLabelSemanticTypography = {
  textStyle: 'body-m-regular',
} as TextProps;

const countryOptionsSemanticTypography = {
  textStyle: 'body-s-regular',
} as TextProps;

export default memo(CountrySelector);
