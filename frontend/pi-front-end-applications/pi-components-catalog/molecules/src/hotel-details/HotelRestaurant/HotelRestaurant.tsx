import type { BoxProps, TextProps } from '@chakra-ui/react';
import { Box, Heading, Image, Text } from '@chakra-ui/react';
import { Info, Notification, Section, Tabs } from '@whitbread-eos/atoms';
import {
  formatAssetsUrl,
  useSemanticTypography,
  useStaticHotelInformation,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useState } from 'react';

import HotelRestaurantMenuContent from '../HotelRestaurantMenuContent';

const HotelRestaurant = () => {
  const { restaurant: data, isLoading, isError, error } = useStaticHotelInformation();
  const [tabIndex, setTabIndex] = useState(0);
  const { t } = useTranslation(['common']);
  const getTypographyProps = useSemanticTypography();
  const headingTypographyProps = getTypographyProps(
    headingLegacyTypography,
    headingSemanticTypography
  );
  const isSemanticHeadingTypography = 'textStyle' in headingTypographyProps;

  if (isLoading) {
    return <Text>{t('searchresults.list.hotel.loading')}</Text>;
  }

  if (isError) {
    return <Text>{(error as Error).message}</Text>;
  }

  if (!data?.menus?.length) {
    return null;
  }

  return (
    <Section dataTestId="hdp_restaurant">
      <Box title={t('restaurant.title')}>
        <Heading
          as="h3"
          data-testid="hotel-restaurant-section"
          {...(isSemanticHeadingTypography ? { size: 'none' } : { size: 'md' })}
          {...headingLayoutStyles}
          {...headingTypographyProps}
        >
          {t('restaurant.title')}
        </Heading>

        <Box {...imageContainerStyles}>
          {data?.logoSrc !== '' && data?.logoSrc !== null && (
            <Image
              src={formatAssetsUrl(data?.logoSrc ?? '')}
              alt={data?.name ?? ''}
              objectFit="contain"
              maxHeight={{ base: '40px', sm: '65px' }}
              data-testid="hotel-restaurant-logo"
            />
          )}
        </Box>

        {!!data?.menus?.[tabIndex]?.disclaimer?.length && (
          <Notification
            title={data?.menus?.[tabIndex]?.disclaimer ?? ''}
            status="info"
            variant="infoGrey"
            svg={<Info />}
            description=""
            prefixDataTestId="hdp_restaurants"
            {...notificationLayoutStyles}
            {...getTypographyProps(notificationLegacyTypography, notificationSemanticTypography)}
          />
        )}

        {data?.menus?.length === 1 ? (
          <HotelRestaurantMenuContent menu={data?.menus?.[0]} />
        ) : (
          <Tabs
            onChange={(tabIndex) => setTabIndex(tabIndex)}
            pt={2.5}
            sx={{ ...tabsStyles() }}
            styles={{
              tab: {
                minW: { mobile: `calc(100% / ${data?.menus?.length})`, sm: '6rem' },
                maxWidth: {
                  xs: '12rem',
                  mobile: '7rem',
                },
              },
              tabList: {
                w: {
                  xl: `calc(100% / 2 - 1.5rem + 1.5rem / 2)`,
                  lg: `calc(100% / 2 - 1.5rem + 1.5rem / 2)`,
                  md: '39.813rem',
                  mobile: 'full',
                },
              },
            }}
            variant="tabsGroup"
            prefixDataTestId="restaurantConfiguration"
            labelStyles={{
              selected: { textStyle: 'body-m-emphasis' },
              unselected: { textStyle: 'body-m-regular' },
            }}
            options={data?.menus?.map((menu, index) => ({
              index,
              label: menu?.name ?? '',
              content: <HotelRestaurantMenuContent menu={menu} isTabPanel />,
            }))}
          />
        )}
      </Box>
    </Section>
  );
};

const tabsStyles = () => ({
  '.chakra-tabs__tablist': {
    w: {
      xl: `calc(100% / 2 - 1.5rem + 1.5rem / 2)`,
      lg: `calc(100% / 2 - 1.5rem + 1.5rem / 2)`,
      md: '39.813rem',
      mobile: 'full',
    },
  },
  '.chakra-tabs__tab': {
    minWidth: '6rem',
    maxWidth: {
      xs: '12rem',
      mobile: '7rem',
    },
  },
});

const imageContainerStyles = {
  mb: 5,
  width: '100%',
  height: 'auto',
} as BoxProps;

const notificationLayoutStyles = {
  maxWidth: { base: 'full', md: '70%' },
  borderColor: '#80CACE',
};

const notificationLegacyTypography = {
  fontWeight: 'bold',
  fontSize: 'sm',
  lineHeight: 2,
};

const notificationSemanticTypography = {
  textStyle: 'body-s-emphasis',
};

const headingLegacyTypography = {
  fontSize: { base: 'xl', md: '2xl' },
  fontWeight: 'semibold',
  lineHeight: { base: '3', md: '4' },
};

const headingSemanticTypography = {
  textStyle: {
    base: 'heading-s',
    md: 'heading-m',
  } as unknown as TextProps['textStyle'],
};

const headingLayoutStyles = {
  mb: '4',
  mt: '3xl',
};

export { HotelRestaurant };
