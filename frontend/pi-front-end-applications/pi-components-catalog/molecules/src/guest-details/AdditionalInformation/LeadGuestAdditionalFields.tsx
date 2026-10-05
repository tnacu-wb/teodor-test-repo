import { Box } from '@chakra-ui/react';
import { GET_COUNTRIES, SITE_LEISURE } from '@whitbread-eos/api';
import {
  formatDataTestId,
  getSortedCountriesByCurrentLang,
  useCustomLocale,
  useQueryRequest,
} from '@whitbread-eos/utils';
import { useEffect, useState } from 'react';
import { Controller } from 'react-hook-form';

import { Nationality } from './common';
import FieldController from './field';

export const LeadGuestAdditionalFields = ({
  control,
  formField,
  errMsg,
  styles,
  name,
  label,
  type,
  testId,
  isDisabled,
  handleSetValue,
  fieldToClear,
}: any) => {
  const { language, country } = useCustomLocale();
  const [nationalities, setNationalities] = useState<Nationality[]>([]);

  const { data: countriesData, isSuccess: countriesRequestSuccess } = useQueryRequest(
    ['GetCountries', country, language, SITE_LEISURE],
    GET_COUNTRIES,
    {
      country,
      language,
      site: SITE_LEISURE,
    }
  );

  useEffect(() => {
    if (countriesRequestSuccess && !nationalities?.length) {
      const sortedCountries = getSortedCountriesByCurrentLang(
        countriesData?.countries?.countries,
        language
      );
      setNationalities(
        sortedCountries?.map(({ nationality, countryCode, flagSrc }) => ({
          value: countryCode,
          label: nationality ?? '',
          image: flagSrc,
        })) || []
      );
    }
  }, [countriesRequestSuccess, countriesData]);

  return (
    <Box {...styles} key={name} data-testid={formatDataTestId(formField.testid, testId)}>
      <Controller
        name={name}
        control={control}
        render={({ field }) => (
          <FieldController
            field={field}
            type={type}
            label={label}
            name={name}
            isDisabled={isDisabled}
            formField={formField}
            nationalities={nationalities}
            errMsg={errMsg}
            handleSetValue={handleSetValue}
            fieldToClear={fieldToClear}
          />
        )}
      />
    </Box>
  );
};

export default LeadGuestAdditionalFields;
