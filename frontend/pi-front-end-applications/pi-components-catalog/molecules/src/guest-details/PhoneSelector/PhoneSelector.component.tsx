import { Country, GET_COUNTRIES } from '@whitbread-eos/api';
import { type FormDynamicFieldCompProps, PhoneInput } from '@whitbread-eos/atoms';
import { formatAssetsUrl, useQueryRequest } from '@whitbread-eos/utils';
import { useEffect, useState } from 'react';
import { isValidPhoneNumber, parsePhoneNumber } from 'react-phone-number-input';

export type PhoneValue = {
  countryCode: string;
  dialingCode: string;
  phone: string;
};

export type DefaultValuesType = {
  [key: string]: {
    dialingCode: string;
    countryCode: string;
  };
};

export default function PhoneSelector({
  formField,
  field,
  errors,
  getValues,
  handleTriggerValidation,
}: Readonly<FormDynamicFieldCompProps>) {
  const [countries, setCountries] = useState<Country[]>();

  const currentLang = formField.props?.currentLang || 'en';
  const country = currentLang && currentLang === 'en' ? 'gb' : 'de';
  const defaultValues: DefaultValuesType = {
    en: {
      dialingCode: '+44',
      countryCode: 'GB',
    },
    de: {
      dialingCode: '+49',
      countryCode: 'DE',
    },
  };

  const [phoneNumber, setPhoneNumber] = useState<PhoneValue>({
    countryCode: defaultValues[currentLang]?.countryCode,
    dialingCode: defaultValues[currentLang]?.dialingCode,
    phone: field.value,
  });
  const isBusinessBkng = formField?.props?.isBusinessBkng || false;
  const site = isBusinessBkng ? 'business-booker' : 'leisure';
  const testId = formField.testid ?? 'PhoneSelector';

  // get countries by postcode
  const { data: countriesData, isSuccess: countriesRequestSuccess } = useQueryRequest(
    ['GetCountries', country, currentLang, site],
    GET_COUNTRIES,
    {
      country: country,
      language: currentLang,
      site: 'leisure',
    }
  );

  const findCountry = (
    countries: [],
    countryCode: string
  ): { countryCode?: string; dialingCode?: string } => {
    return (
      countries?.find?.((country: { countryCode: string }) => {
        return country.countryCode === countryCode;
      }) ?? {}
    );
  };

  useEffect(() => {
    const userPhoneNumber = field.value?.includes('+')
      ? field.value
      : defaultValues[currentLang]?.dialingCode + field.value;
    const parsedNumber = parsePhoneNumber(userPhoneNumber);
    const countryCallingCode = `+${parsedNumber?.countryCallingCode}`;

    if (!isValidPhoneNumber(userPhoneNumber)) {
      if (parsedNumber?.nationalNumber) {
        setPhoneNumber({
          countryCode: parsedNumber.country ?? phoneNumber.countryCode,
          dialingCode: countryCallingCode ?? phoneNumber.dialingCode,
          phone: parsedNumber?.nationalNumber,
        });
      }
      return;
    }

    if (parsedNumber?.country) {
      setPhoneNumber({
        countryCode: parsedNumber.country,
        dialingCode: countryCallingCode,
        phone: parsedNumber.nationalNumber,
      });
    }
  }, [field.value]);

  useEffect(() => {
    if (countriesRequestSuccess) {
      setCountries(countriesData?.countries?.countries || []);

      setPhoneNumber((latest) => {
        if (latest.phone) {
          const { countryCode, dialingCode } = findCountry(
            countriesData.countries.countries,
            latest.countryCode ?? getValues('countryCode')
          );
          return {
            ...latest,
            countryCode: countryCode ?? latest.countryCode,
            dialingCode: dialingCode ?? latest.dialingCode,
          };
        }
        return latest;
      });
    }
  }, [countriesRequestSuccess, countriesData]);

  useEffect(() => {
    if (phoneNumber.phone !== field.value && field.value?.includes('userProfile')) {
      const { countryCode, dialingCode } = findCountry(
        countriesData?.countries?.countries,
        getValues('countryCode')
      );
      setPhoneNumber({
        phone: field.value.replace('userProfile', ''),
        countryCode: countryCode ?? phoneNumber.countryCode,
        dialingCode: dialingCode ?? phoneNumber.dialingCode,
      });
    }
  }, [field]);

  useEffect(() => {
    if (formField.props?.clearField) {
      setPhoneNumber({
        countryCode: defaultValues[currentLang]?.countryCode,
        dialingCode: defaultValues[currentLang]?.dialingCode,
        phone: '',
      });
    }
  }, [formField.props?.clearField]);

  const onChange = (phoneValue: PhoneValue) => {
    const value = `${phoneValue.dialingCode}${phoneValue.phone}`;
    setPhoneNumber(phoneValue);
    // Reset the field value, if the phone number is empty
    if (phoneValue?.phone.length === 0) {
      return field.onChange('');
    }
    field.onChange?.(value);
  };

  return (
    <PhoneInput
      value={phoneNumber}
      label={formField.label}
      name={formField.name}
      dataTestId={testId}
      onChange={onChange}
      onBlur={field?.onBlur}
      handleTriggerValidation={handleTriggerValidation}
      dependantOn={formField?.dependantOn}
      countries={countries}
      formatAssetsUrl={formatAssetsUrl}
      placeholder={formField.label}
      currentLang={currentLang}
      error={errors?.[formField.name]?.message}
      showIcon={formField?.props?.showIcon}
      inputProps={{
        className: formField?.props?.className,
        ...(formField?.props?.styles?.inputElementStyles ?? {}),
      }}
      useTooltip={formField?.props?.useTooltip}
      isAltStyle={formField?.props?.isAltStyle}
    />
  );
}
