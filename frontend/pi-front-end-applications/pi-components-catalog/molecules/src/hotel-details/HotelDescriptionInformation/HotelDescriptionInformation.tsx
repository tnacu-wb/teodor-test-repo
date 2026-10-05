import { Box, Text, TextProps } from '@chakra-ui/react';
import { Section } from '@whitbread-eos/atoms';
import {
  renderSanitizedHtml,
  useSemanticTypography,
  useStaticHotelInformation,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

export const HotelDescriptionInformation = () => {
  const { t } = useTranslation(['common']);
  const { hotelDescription, isLoading, isError, error } = useStaticHotelInformation();
  const getTypographyProps = useSemanticTypography();

  if (isLoading) {
    return <Text>{t('searchresults.list.hotel.loading')}</Text>;
  }

  if (isError) {
    return <Text>{(error as Error).message}</Text>;
  }

  if (!hotelDescription) {
    return null;
  }

  return (
    <Section dataTestId="hdp_description">
      <Box maxW="full" data-testid="hotel-details-description" {...containerStyles}>
        <Text
          as="h3"
          {...getTypographyProps(
            headingDescriptionLegacyTypography,
            headingDescriptionSemanticTypography
          )}
        >
          {t('hoteldetails.description')}
        </Text>
        <Box
          mt={{ base: '2', sm: '1' }}
          className="formatLinks"
          {...getTypographyProps({}, formatLinksDescriptionSemanticTypography)}
        >
          {renderSanitizedHtml(hotelDescription)}
        </Box>
      </Box>
    </Section>
  );
};

const formatLinksDescriptionSemanticTypography = {
  textStyle: 'body-m-regular',
};

const headingDescriptionLegacyTypography = {
  fontWeight: 'semibold',
  fontSize: { base: 'xl', sm: '2xl' },
  lineHeight: { base: '3', sm: '4' },
};

const headingDescriptionSemanticTypography = {
  textStyle: {
    base: 'heading-s',
    md: 'heading-m',
  } as unknown as TextProps['textStyle'],
};

const containerStyles = {
  mt: { base: 'lg', sm: '3xl' },
  mb: { base: 'lg', sm: '2xl' },
};
