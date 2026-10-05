import type { FlexProps, TextProps } from '@chakra-ui/react';
import { Box, Flex, Text } from '@chakra-ui/react';
import {
  renderSanitizedHtml,
  useCustomLocale,
  useSemanticTypography,
  useStaticHotelInformation,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

export const HotelContactInformation = () => {
  const { t } = useTranslation(['common']);
  const { language } = useCustomLocale();
  const { contactDetails, brand, isLoading, isError, error } = useStaticHotelInformation();
  const getTypographyProps = useSemanticTypography();

  const data = contactDetails?.phone;
  const email = contactDetails?.email;

  if (isLoading) {
    return <Text>{t('searchresults.list.hotel.loading')}</Text>;
  }

  if (isError) {
    return <Text>{(error as Error).message}</Text>;
  }

  if (!data) {
    return null;
  }

  const flexStyles = {
    direction: language === 'de' ? 'column' : { base: 'column', lg: 'row' },
    mt: language === 'de' ? '4' : { base: '2', sm: '1' },
  } as FlexProps;

  const boxStyles = {
    maxW: language === 'de' ? 'full' : { base: 'full', lg: '1053px' },
    ml: language === 'de' ? '0' : { base: '0' },
    mt: language === 'de' ? '0' : { base: '0', lg: '1' },
  };

  return (
    <Box maxW="full" data-testid="hotel-details-contact" {...containerStyles}>
      <Text
        as="h3"
        {...getTypographyProps(headingContactLegacyTypography, headingContactSemanticTypography)}
      >
        {t('contactModule.contactLabel')}
      </Text>
      <Text
        mt={4}
        {...getTypographyProps({}, contactBodySemanticTypography)}
      >{`${t('contactModule.phoneLabel')} ${data}`}</Text>
      {brand === 'PID' && email && (
        <Text
          mt={1}
          {...getTypographyProps({}, contactBodySemanticTypography)}
        >{`${t('booking.leadGuest.Email')}: ${email}`}</Text>
      )}
      <Flex {...flexStyles}>
        <Box
          {...boxStyles}
          className="formatLinks"
          {...getTypographyProps({}, contactBodySemanticTypography)}
        >
          {renderSanitizedHtml(t('contactModule.Email'))}
        </Box>
      </Flex>
    </Box>
  );
};

const contactBodySemanticTypography = {
  textStyle: 'body-m-regular',
};

const headingContactLegacyTypography = {
  fontWeight: 'semibold',
  fontSize: { base: 'xl', sm: '2xl' },
  lineHeight: { base: '3', sm: '4' },
};

const headingContactSemanticTypography = {
  textStyle: {
    base: 'heading-s',
    md: 'heading-m',
  } as unknown as TextProps['textStyle'],
};

const containerStyles = {
  mt: { base: 'lg', sm: '3xl' },
  mb: { base: 'lg', sm: '2xl' },
};
