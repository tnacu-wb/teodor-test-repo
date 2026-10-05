import { Box, Text } from '@chakra-ui/react';
import { useSemanticTypography, useStaticHotelInformation } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

export default function HotelLocationInformation() {
  const { address, satNavDirections, whatThreeWords, isLoading, isError, error } =
    useStaticHotelInformation();

  const getTypographyProps = useSemanticTypography();

  const countryCode: string =
    address?.country !== undefined &&
    (address.country === 'Germany' || address.country === 'Deutschland')
      ? 'DE'
      : 'GB';
  const { t } = useTranslation(['common']);

  if (isLoading) {
    return <Text>{t('searchresults.list.hotel.loading')}</Text>;
  }

  if (isError) {
    return <Text>{(error as Error).message}</Text>;
  }

  return (
    <>
      <Box {...boxLayoutStyles} {...getTypographyProps(boxLegacyTypography, boxSemanticTypography)}>
        {countryCode === 'DE' ? (
          <Text data-testid="hdp_hotelLocationAddress">{`${address?.addressLine1}, ${address?.postalCode} ${address?.addressLine2}`}</Text>
        ) : (
          <>
            <Text data-testid="hdp_hotelLocationAddress">{`${address?.addressLine1}, ${address?.addressLine2}, ${address?.addressLine3}`}</Text>
            <Text data-testid="hdp_hotelLocationPostcode">{address?.postalCode}</Text>
          </>
        )}
      </Box>
      <Box mb={2.5}>
        {satNavDirections &&
          renderHotelLocationInformation(
            'hoteldetails.satnav.title',
            satNavDirections,
            'hdp_satNavDirections'
          )}
        {whatThreeWords &&
          renderHotelLocationInformation(
            'hoteldetails.whatthreewords.label',
            whatThreeWords,
            'hdp_whatThreeWords'
          )}
      </Box>
    </>
  );

  function renderHotelLocationInformation(title: string, description: string, dataTestId: string) {
    return (
      <Box>
        <Box
          as="span"
          display="inline"
          data-testid={`${dataTestId}Title`}
          {...getTypographyProps(titleLegacyTypography, titleSemanticTypography)}
        >
          {t(title)}
        </Box>
        <Box
          as="span"
          display="inline"
          ml={1.5}
          data-testid={`${dataTestId}Description`}
          {...getTypographyProps(descriptionLegacyTypography, descriptionSemanticTypography)}
        >
          {description}
        </Box>
      </Box>
    );
  }
}

const boxLayoutStyles = {
  mb: 1.5,
};

const boxLegacyTypography = {
  fontWeight: 'semibold',
  fontSize: 'md',
  lineHeight: 'base',
};

const boxSemanticTypography = {
  textStyle: 'body-m-emphasis',
};

const titleLegacyTypography = {
  fontSize: 'md',
};

const titleSemanticTypography = {
  textStyle: 'body-m-regular',
};

const descriptionLegacyTypography = {
  fontWeight: 'bold',
};

const descriptionSemanticTypography = {
  textStyle: 'body-m-emphasis',
};
