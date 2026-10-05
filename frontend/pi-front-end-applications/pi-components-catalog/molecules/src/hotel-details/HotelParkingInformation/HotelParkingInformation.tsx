import { Box, Text, TextProps } from '@chakra-ui/react';
import { Section } from '@whitbread-eos/atoms';
import {
  getPIBrandText,
  renderSanitizedHtml,
  useSemanticTypography,
  useStaticHotelInformation,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

export default function HotelParkingInformation() {
  const { parkingDescription, isLoading, isError, error, brand, title } =
    useStaticHotelInformation();
  const { t } = useTranslation(['common']);
  const getTypographyProps = useSemanticTypography();

  if (isLoading) {
    return <Text>{t('searchresults.list.hotel.loading')}</Text>;
  }

  if (isError) {
    return <Text>{(error as Error).message}</Text>;
  }

  if (!parkingDescription) {
    return null;
  }

  return (
    <Section dataTestId="hdp_parking">
      <Box maxW="full" data-testid="hotel-details-parking" {...containerStyles}>
        <Text
          as="h3"
          {...getTypographyProps(headingParkingLegacyTypography, headingParkingSemanticTypography)}
        >
          {t('hoteldetails.mapparking.title')}
          {getPIBrandText(t, brand)}
          {title}
        </Text>
        <Box
          mt="1"
          className="formatLinks"
          {...getTypographyProps({}, formatLinksParkingSemanticTypography)}
        >
          {renderSanitizedHtml(parkingDescription)}
        </Box>
      </Box>
    </Section>
  );
}

const formatLinksParkingSemanticTypography = {
  textStyle: 'body-m-regular',
};

const headingParkingLegacyTypography = {
  fontWeight: 'semibold',
  fontSize: '2xl',
  lineHeight: '4',
};

const headingParkingSemanticTypography = {
  textStyle: {
    base: 'heading-s',
    md: 'heading-m',
  } as unknown as TextProps['textStyle'],
};

const containerStyles = {
  my: { base: '1.5rem', md: '3rem' },
};
