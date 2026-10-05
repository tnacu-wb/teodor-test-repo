import { Box, Center, Flex, Text } from '@chakra-ui/react';
import { Logo, theme } from '@whitbread-eos/atoms';
import { useStaticHotelInformation } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

export const HubZipNotice = ({ srcHubLogo }: Readonly<{ srcHubLogo?: string }>) => {
  const { t } = useTranslation(['common']);
  const { brand, isLoading, isError, error } = useStaticHotelInformation();

  if (isLoading) {
    return <Text>{t('searchresults.list.hotel.loading')}</Text>;
  }

  if (isError) {
    return <Text>{(error as Error).message}</Text>;
  }

  if (!brand || !['HUB', 'ZIP'].includes(brand)) {
    return null;
  }

  const notice = {
    title:
      brand === 'HUB' ? t('hoteldetails.hub.alertText') : t('hoteldetails.priceFighter.alertText'),
    subtitle: t('hoteldetails.hub.tagline1'),
    description: t('hoteldetails.hub.tagline2'),
  };

  const titleStyles = {
    bg: brand === 'HUB' ? theme.colors.hubPrimary : theme.colors.zipPrimary,
    py: '.3125rem',
    mb: 'lg',
    color: brand === 'HUB' ? 'darkGrey1' : 'white',
    fontWeight: brand === 'HUB' ? 'bold' : '',
    lineHeight: 3,
    mx: {
      mobile: '-1.5rem',
      lg: '0',
    },
    fontSize: 'md',
  };

  const subtitleStyles = {
    fontSize: 'md',
    fontWeight: '600',
    mb: 'sm',
    color: 'darkGrey1',
    lineHeight: 3,
  };

  const descriptionStyles = {
    fontSize: 'md',
    lineHeight: 3,
    color: 'darkGrey1',
  };

  return (
    <Box mt="md" data-testid="hub-zip-notice">
      <Center {...titleStyles} data-testid="hdp_hubZipNoticeTitle">
        {notice.title}
      </Center>
      <Flex
        direction={{ base: 'column', sm: 'inherit' }}
        alignItems={{ base: 'baseline', sm: 'center' }}
        mb={{ base: 12, sm: 0 }}
      >
        <Box mb={{ base: 6, sm: 0 }}>
          <Logo variant={brand === 'HUB' ? 'hub' : 'zip'} transform="scale(1)" src={srcHubLogo} />
        </Box>
        <Flex direction="column" pl={{ sm: '3.125rem' }} data-testid="hdp_hubZipNoticeInformation">
          <Text {...subtitleStyles}>{notice.subtitle}</Text>
          <Text {...descriptionStyles}>{notice.description}</Text>
        </Flex>
      </Flex>
    </Box>
  );
};
