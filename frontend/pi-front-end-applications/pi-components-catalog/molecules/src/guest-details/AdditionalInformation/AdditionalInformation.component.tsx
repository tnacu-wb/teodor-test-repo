import { Box, Flex, Text, TextProps } from '@chakra-ui/react';
import { GET_COUNTRIES, SITE_LEISURE } from '@whitbread-eos/api';
import { Button, Path, Rectangle, Icon } from '@whitbread-eos/atoms';
import {
  formatDataTestId,
  getSortedCountriesByCurrentLang,
  useCustomLocale,
  useQueryRequest,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useEffect, useState } from 'react';
import { Controller } from 'react-hook-form';

import { gridStyles, additionalDetailsFieldsBooker, Nationality } from './common';
import FieldController from './field';

export const AdditionalInformation = ({
  control,
  formField,
  errors,
  getValues,
  handleSetValue,
}: any) => {
  const { language, country } = useCustomLocale();
  const [nationalities, setNationalities] = useState<Nationality[]>([]);
  const { t } = useTranslation();
  const [showCheckInInfo, setShowCheckInInfo] = useState(false);

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

  const nationality = getValues()?.nationality?.value;

  const toggleCheckInInfo = () => {
    setShowCheckInInfo((prev) => !prev);
  };

  return (
    <>
      <Box
        mb="lg"
        data-testid={formatDataTestId(formField.testid, 'guestDetailsMyselfHeadingOptional')}
      >
        <Text {...heading3}>{t('precheckin.guestdetails')}</Text>
        <Text mt="md">{t('precheckin.completedetails.statement')}</Text>
      </Box>

      <Box>
        <Button
          mb="md"
          variant="tertiary"
          size="full"
          onClick={toggleCheckInInfo}
          data-testId={formatDataTestId(formField.testid, 'CheckinButton')}
        >
          {showCheckInInfo ? (
            <>
              <Icon svg={<Rectangle color={'var(--chakra-colors-darkGrey2)'} />} {...iconStyles} />
              {` ${t('booking.guestDetails.hideCheckIn')}`}
            </>
          ) : (
            <>
              <Icon svg={<Path color={'var(--chakra-colors-darkGrey2)'} />} {...iconStyles} />
              {` ${t('booking.guestDetails.addCheckIn')}`}
            </>
          )}
        </Button>
      </Box>
      {showCheckInInfo && (
        <Flex direction="column">
          {additionalDetailsFieldsBooker.map(
            ({ label, name, styles, testId, type }) =>
              (name !== 'passport' || (nationality && nationality !== 'DE')) && (
                <Box
                  {...gridStyles}
                  {...styles}
                  key={name}
                  mb="4"
                  data-testid={formatDataTestId(formField.testid, testId)}
                >
                  <Controller
                    name={name}
                    control={control}
                    render={({ field }) => (
                      <FieldController
                        field={field}
                        type={type}
                        label={label}
                        formField={formField}
                        nationalities={nationalities}
                        errMsg={errors?.[name]?.message}
                        handleSetValue={handleSetValue}
                        fieldToClear="passport"
                      />
                    )}
                  />
                </Box>
              )
          )}
        </Flex>
      )}
    </>
  );
};

export default AdditionalInformation;

const heading3 = {
  fontSize: 'lg',
  fontWeight: '700',
  lineHeight: '3',
  color: 'darkGrey1',
} as TextProps;

const iconStyles = {
  mr: '0.75rem',
  display: {
    base: 'none',
    xs: 'block',
  },
};
