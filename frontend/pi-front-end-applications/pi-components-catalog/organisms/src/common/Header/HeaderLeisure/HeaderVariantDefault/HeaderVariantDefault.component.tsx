import type { BoxProps } from '@chakra-ui/react';
import { Box, Container, Flex, Grid } from '@chakra-ui/react';
import type { HotelBrandType, NavItems, SubNavCategory } from '@whitbread-eos/api';
import { Area } from '@whitbread-eos/api';
import { Icon, LanguageOptions, LanguageSelectorSwitcher, Logo } from '@whitbread-eos/atoms';
import { formatAssetsUrl, useCustomLocale } from '@whitbread-eos/utils';
import React, { useEffect, useState } from 'react';

import { getListOfLanguagesForSwitcher } from '../../helpers/helpers';
import {
  containerLogoStyle,
  containerWrapperStyles,
  contentStyles,
  headerWrapperStyles,
} from '../HeaderLeisure.style';
import NavigationMenu from '../NavigationMenu';
import NavigationMenuMobile from '../NavigationMenuMobile';

interface Props {
  headerInfoData: any;
  hotelBrand?: HotelBrandType;
  useNextImage?: boolean;
}

export default function HeaderVariantDefault({
  headerInfoData,
  hotelBrand,
  useNextImage,
}: Readonly<Props>) {
  const { content, config } = headerInfoData;
  const { language: currentLanguage, country: currentCountry } = useCustomLocale();
  const isWindowDefined = typeof window !== 'undefined';
  const [origin, setOrigin] = useState('');

  const [tickIcon, setTickIcon] = useState<React.ReactElement>(<></>);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const countries = content?.countries;

  useEffect(() => {
    setOrigin(window.location.origin);
  }, [isWindowDefined]);

  const [languagesList, setLanguagesList] = useState<LanguageOptions[]>([]);
  const [navigationLabels, setNavigationLabels] = useState<NavItems>({
    business: '',
    businessSubNav: {
      title: '',
      navOptions: [
        {
          title: '',
          url: '',
        },
      ],
    },
    logIn: '',
    logOut: '',
    findBooking: '',
    discoverPI: '',
    subNav: [
      {
        title: '',
        navOptions: [
          {
            title: '',
            url: '',
          },
        ],
      },
    ],
    accountLinks: [],
    mobileMenuButton: '',
    language: '',
    signUpButton: '',
  });

  useEffect(() => {
    if (countries?.length) {
      setLanguagesList(getListOfLanguagesForSwitcher(countries) as LanguageOptions[]);

      setTickIcon(
        <Icon
          src={formatAssetsUrl(content?.menu?.tick as string)}
          color="var(--chakra-colors-primary)"
        />
      );
      setIsLoading(false);

      setNavigationLabels({
        discoverPI: content?.menu?.discoverPI,
        business: content?.menu?.business,
        businessSubNav: getBusinessOptions(content?.subNav),
        findBooking: content?.menu?.findBooking,
        logIn: content?.menu?.logIn,
        logOut: content?.authentication?.logoutButton,
        subNav: filterBusinessOptions(content?.subNav),
        accountLinks: config?.authentication?.accountLinks ?? [],
        mobileMenuButton: content?.menu?.mobileMenuButton,
        language: content?.menu?.language,
        signUpButton: content?.authentication?.signUpButton,
      });
    }
  }, [content, config]);

  return (
    <Box {...customHeaderWrapperStyles} data-testid="common-header-wrapper">
      <Container {...containerWrapperStyles}>
        <Grid {...contentStyles} sx={{ '@media print': { py: 'sm' } }}>
          <Flex {...containerLogoStyle} data-testid="logo-container">
            {!isLoading && (
              <Logo
                href={`${origin}/${currentCountry}/${currentLanguage}/home.html`}
                src={formatAssetsUrl(content?.header?.image)}
                variant={hotelBrand}
                alt={content?.global?.brand?.[hotelBrand as keyof typeof content.global.brand]}
                useNextImage={useNextImage}
              />
            )}
          </Flex>
          <Grid
            templateColumns="1fr auto"
            alignItems={'center'}
            display={{ mobile: 'none', lg: 'grid' }}
            sx={{ '@media print': { display: 'none' } }}
          >
            {!isLoading && (
              <LanguageSelectorSwitcher
                currentLanguage={currentLanguage}
                languagesList={languagesList}
                prefixDataTestId="pi"
                tickIcon={tickIcon}
                area={Area.PI}
              />
            )}
            <NavigationMenu labels={navigationLabels} />
          </Grid>
          <Grid display={{ mobile: 'grid', lg: 'none' }} justifyContent={'end'}>
            {!isLoading && (
              <NavigationMenuMobile
                currentLanguage={currentLanguage}
                languagesList={languagesList}
                tickIcon={tickIcon}
                area={Area.PI}
                labels={navigationLabels}
              />
            )}
          </Grid>
        </Grid>
      </Container>
    </Box>
  );

  function getBusinessOptions(list: SubNavCategory[]) {
    if (!list) {
      return {
        title: '',
        navOptions: [
          {
            title: '',
            url: '',
          },
        ],
      };
    }

    return (
      list.find((item: SubNavCategory) => item.title.includes('Business')) ?? {
        title: '',
        navOptions: [
          {
            title: '',
            url: '',
          },
        ],
      }
    );
  }

  function filterBusinessOptions(list: SubNavCategory[]) {
    if (!list) {
      return [];
    }

    return list.filter(
      // Filter empty title value and Business column
      (item: SubNavCategory) => !item.title.includes('Business') && item.title.trim().length > 0
    );
  }
}

const customHeaderWrapperStyles = {
  ...headerWrapperStyles,
  h: { mobile: 'var(--chakra-space-4xl)', lg: 'var(--chakra-space-6xl)' },
} as BoxProps;
