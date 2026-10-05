import type {
  BoxProps,
  FlexProps,
  GridProps,
  StackProps,
  TextProps,
  LinkProps,
} from '@chakra-ui/react';
import {
  Box,
  Circle,
  Container,
  Flex,
  Grid,
  Link,
  Stack,
  Text,
  List,
  ListItem,
  Heading,
  ListItemProps as ListItemPropChakra,
} from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import type { HotelBrandType, LANG, Claims } from '@whitbread-eos/api';
import {
  Area,
  GET_STATIC_CONTENT,
  HeaderLinks,
  UserRoles,
  SITE_LEISURE,
  FS_DISPLAY_GUEST_ACCOUNT,
  FT_CCUI_UNIQUE_PROMO_CODE,
  Channel,
  MemoModalVariants,
} from '@whitbread-eos/api';
import {
  Icon,
  LanguageSelectorSwitcher,
  Logo,
  Popover,
  LanguageOptions,
} from '@whitbread-eos/atoms';
import {
  useCustomLocale,
  formatAssetsUrl,
  useQueryRequest,
  useAgentMemo,
  useFeatureSwitch,
  useHotelBrands,
  useFeatureToggle,
  isPromoAdmin,
  formatDataTestId,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import NextLink from 'next/link';
import { useRouter } from 'next/router';
import { useEffect, useState, useCallback } from 'react';

import { ChangeLog } from '../../index';
import { getListOfLanguagesForSwitcher } from '../helpers/helpers';

export interface Props {
  user?: Claims;
  roles?: string[];
  queryClient: QueryClient;
}

interface NavOption {
  title: string;
  url: string;
}

export default function HeaderAgent({ roles, user, queryClient }: Readonly<Props>) {
  const { query, pathname, asPath } = useRouter() || {
    query: { agent_country: '', reservationId: '' },
    pathname: '',
    asPath: '',
  };

  const isGuestAccountEnabled = useFeatureSwitch({
    featureSwitchKey: FS_DISPLAY_GUEST_ACCOUNT,
  });

  // Agent memos work with reservationId (booking flow) or tempBasketReference/tempBookingReference (amend flow)
  const isAgentMemoEnabled =
    !!query.reservationId || !!query.tempBasketReference || !!query.tempBookingReference;
  const { language, country } = useCustomLocale();
  const { t } = useTranslation(['common']);
  const {
    openAgentMemo,
    isAgentMemoOpen,
    setAgentMemoReservationId,
    agentMemoCount,
    closeAgentMemo,
  } = useAgentMemo();
  const agentCountry = query['agent_country'] as LANG;
  const currentLang = agentCountry ?? (language as LANG);
  const isWindowDefined = typeof window !== 'undefined';

  // basketReference is for basket operations - use reservationId or tempBasketReference/tempBookingReference
  const basketReference = String(
    query?.reservationId || query?.tempBasketReference || query?.tempBookingReference || ''
  );
  const bookingReference = query?.bookingReference ? String(query.bookingReference) : '';

  const [origin, setOrigin] = useState('');

  useEffect(() => {
    setOrigin(window.location.origin);
  }, [isWindowDefined]);

  useEffect(() => {
    // Use basket reference from either reservationId (booking flow) or tempBasketReference/tempBookingReference (amend flow)
    // Note: We use tempBasketReference/tempBookingReference not bookingReference because API expects basket format
    const referenceId =
      query.reservationId || query.tempBasketReference || query.tempBookingReference;
    if (referenceId) {
      setAgentMemoReservationId(referenceId as string);
    } else {
      closeAgentMemo();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [query.reservationId, query.tempBasketReference, query.tempBookingReference]);

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const isManager = roles?.includes(UserRoles.MANAGER); // NOSONAR

  const { data } = useQueryRequest(['GetStaticContent', language, country], GET_STATIC_CONTENT, {
    language,
    country,
    site: SITE_LEISURE,
    businessBooker: false,
  });

  const { [FT_CCUI_UNIQUE_PROMO_CODE]: isUniquePromoCodeEnabled } = useFeatureToggle();

  const isPromoAdminUser = isPromoAdmin(user);
  const isPromoCodeEnabled = isUniquePromoCodeEnabled && isPromoAdminUser;

  const agentDisabledLinks = isGuestAccountEnabled
    ? []
    : [data?.headerInformation?.content?.menu?.guestAccount];
  const [showChangeLogModal, setShowChangeLogModal] = useState(false);
  const regex = /\/amend/;
  const isAmendPage = regex.test(pathname);
  const isChangeLogEnabled = isAmendPage && bookingReference;
  const enableModal = showChangeLogModal && bookingReference;

  const handleChangeLogClicked = useCallback(() => {
    setShowChangeLogModal(true);
  }, []);

  const countries = data?.headerInformation?.content?.countries ?? [];
  const languagesList = getListOfLanguagesForSwitcher(countries);

  const { brand } = useHotelBrands({ basketReference, channel: Channel.Ccui, queryClient });

  const routeManager = (route: string) => {
    // TO DO: Add missing routes
    switch (route) {
      case 'discoverPI':
        return '/';
      case 'findBooking':
        return `/${country}/${currentLang}/bookings`;
      case 'guestAccount':
        return `/${country}/${currentLang}/search-account`;
      case 'changeLogs':
        return '/';
      case 'promoCode':
        return `/${country}/${currentLang}/unique-promotions/list`;
      default:
        return '/';
    }
  };

  const navigationLinks = Object.keys(HeaderLinks).map((item: string) => {
    const menuResponse = data?.headerInformation?.content?.menu;

    if (item === 'promoCode' && !isPromoCodeEnabled) {
      return null;
    }

    if (
      menuResponse &&
      Object.keys(menuResponse).length > 0 &&
      Object.keys(menuResponse).includes(item)
    ) {
      return { key: item, text: menuResponse[item], url: routeManager(item) };
    }
  });

  const router = useRouter() || {};
  useEffect(() => {
    if (agentCountry) {
      delete query['agent_country'];

      let copyAsPath: string;
      if (asPath.indexOf(`&agent_country=${agentCountry}`) !== -1) {
        copyAsPath = asPath.replace(`&agent_country=${agentCountry}`, '');
      } else if (asPath.indexOf(`agent_country=${agentCountry}&`) !== -1) {
        copyAsPath = asPath.replace(`agent_country=${agentCountry}&`, '');
      } else {
        copyAsPath = asPath.replace(`?agent_country=${agentCountry}`, '');
      }

      router.push({ pathname, query }, copyAsPath, {
        locale: agentCountry as any,
      });
    }
  }, [agentCountry, router]);

  const navStyle = language === 'en' ? stackStyle : stackStyleDE;
  const textStyle = language === 'en' ? linkStyle : linkStyleDE;
  const tickIcon = (
    <Icon
      src={formatAssetsUrl(data?.headerInformation?.content?.menu?.tick as string)}
      color="var(--chakra-colors-primary)"
    />
  );
  const containerWrapperStyles = () => {
    return {
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      maxW: 'var(--chakra-space-breakpoint-xl)',
      h: 'full',
      pl:
        language === 'en'
          ? { mobile: 'md', xs: '5', sm: 'md', md: 'lg', lg: '4.125rem' }
          : { mobile: 'md', xs: '5', sm: 'md', md: 'lg', lg: '1.75rem', xl: '4.125rem' },
      pr:
        language === 'en'
          ? { mobile: 'md', md: '1.688rem', lg: '4.125rem' }
          : { mobile: 'md', md: '1.688rem', lg: '1.75rem', xl: '4.125rem' },
    };
  };

  const handleSessionItem = (key?: string) => {
    if (pathname !== '/bookings' && key === 'findBooking') {
      if (typeof window !== 'undefined') {
        window.localStorage.setItem('SearchBookingFormBookingReference', JSON.stringify(''));
        window.sessionStorage.removeItem('ccuiPrevSearchCriteria');
      }
    }
  };

  function applyDisabledStyles(isDisabled: boolean) {
    return isDisabled ? textStyleDisabled : {};
  }

  const subNav = data?.headerInformation?.content?.subNav;

  return (
    <Box {...headerWrapperStyles} data-testid="common-header-wrapper">
      <Container {...containerWrapperStyles()}>
        <Grid {...contentStyles} sx={{ '@media print': { py: 'sm' } }}>
          <Flex {...containerLogoStyle} data-testid="logo-container">
            <Logo
              src={formatAssetsUrl(data?.headerInformation?.content.header.image)}
              href={`${origin}/${country}/${language}`}
              isHeaderLogo={true}
              variant={
                currentLang === 'de' && brand !== 'hub' && brand !== 'zip'
                  ? 'pi-simple'
                  : (brand as HotelBrandType)
              }
              transform="scale(1)"
            />
          </Flex>
          <Grid
            templateColumns="1fr auto"
            display={{ mobile: 'none', lg: 'grid' }}
            sx={{ '@media print': { display: 'none' } }}
          >
            <Stack as="nav" {...navStyle}>
              <Box {...languageSelectorWrapperStyles}>
                <LanguageSelectorSwitcher
                  currentLanguage={currentLang as any}
                  tickIcon={tickIcon}
                  languagesList={languagesList as LanguageOptions[]}
                  area={Area.CCUI}
                />
              </Box>
              {navigationLinks
                .filter((elem) => elem)
                .map((link) => {
                  let isLinkDisabled = false;
                  let isPopoverLinkEnabled = false;
                  // uncomment next line once the rules are available
                  // if (!isManager) {
                  isLinkDisabled = agentDisabledLinks.includes(link?.text);
                  const disabledLinkStyle = applyDisabledStyles(isLinkDisabled);
                  isPopoverLinkEnabled = link?.key === 'discoverPI';
                  // }
                  return (
                    <Box key={link?.text}>
                      {!isPopoverLinkEnabled ? (
                        <NextLink href={link?.url ?? ''} passHref legacyBehavior>
                          <Link
                            onClick={() => handleSessionItem(link?.key)}
                            {...{ ...noBoxShadowStyle, ...(disabledLinkStyle as LinkProps) }}
                          >
                            <Text
                              {...{ ...textStyle, ...(disabledLinkStyle as TextProps) }}
                              data-testid={formatDataTestId(
                                'navigation-link',
                                link?.key?.replace(/ /g, '_')
                              )}
                            >
                              {link?.text}
                            </Text>
                          </Link>
                        </NextLink>
                      ) : (
                        <Popover
                          triggerItem={
                            <Box {...textStyle}>
                              <Link
                                {...{ ...noBoxShadowStyle, ...(disabledLinkStyle as LinkProps) }}
                              >
                                <Text
                                  {...textStyle}
                                  data-testid={formatDataTestId(
                                    'popover-enabler-text',
                                    link?.key?.replace(/ /g, '_')
                                  )}
                                  {...(disabledLinkStyle as TextProps)}
                                >
                                  {link?.text}
                                </Text>
                              </Link>
                            </Box>
                          }
                        >
                          <Flex
                            data-testid="HeaderAgent-entire-popover-DiscoverPI"
                            justifyContent="flex-end"
                          >
                            {renderListItems(
                              subNav[1]?.navOptions ?? [],
                              subNav[1].title,
                              'discoverPIfirstColumn'
                            )}
                            {renderListItems(
                              subNav[2]?.navOptions ?? [],
                              subNav[2].title,
                              'discoverPISecondColumn'
                            )}
                            {renderListItems(
                              subNav[3]?.navOptions ?? [],
                              subNav[3].title,
                              'discoverPIThirdColumn'
                            )}
                          </Flex>
                        </Popover>
                      )}
                    </Box>
                  );
                })}

              <Link
                key="changeLog"
                onClick={handleChangeLogClicked}
                {...{
                  ...noBoxShadowStyle,
                  ...(applyDisabledStyles(!isChangeLogEnabled) as LinkProps),
                }}
              >
                <Text
                  {...{ ...textStyle, ...(applyDisabledStyles(!isChangeLogEnabled) as TextProps) }}
                  data-testid="navigation-link-changeLogs"
                >
                  {data?.headerInformation?.content?.menu?.changeLogs}
                </Text>
              </Link>

              <Link
                key="agentMemo"
                href="#"
                {...{
                  ...noBoxShadowStyle,
                  ...(applyDisabledStyles(!isAgentMemoEnabled) as LinkProps),
                }}
                onClick={(e) => {
                  e.preventDefault();
                  if (!isAgentMemoEnabled) return;
                  // Use tempBasketReference/tempBookingReference for API calls, not bookingReference
                  const referenceId =
                    query.reservationId || query.tempBasketReference || query.tempBookingReference;
                  openAgentMemo(MemoModalVariants.PAGE);
                  setAgentMemoReservationId(referenceId as string);
                }}
              >
                <Box {...agentLinkContainerStyles}>
                  <Text
                    {...{
                      ...linkStyle,
                      ...(applyDisabledStyles(!isAgentMemoEnabled) as TextProps),
                      ...(isAgentMemoOpen && { ...agentMemoOpenStyle }),
                    }}
                    data-testid="navigation-link-agentMemo"
                  >
                    {data?.headerInformation?.content?.menu?.agentMemo}
                  </Text>
                  {!!agentMemoCount && isAgentMemoEnabled && (
                    <Circle {...agentLinkMemoCountCircleStyles}>
                      <Text {...agentLinkMemoCountTextStyles}>{agentMemoCount}</Text>
                    </Circle>
                  )}
                </Box>
              </Link>
              <Link key="log-out" href="/auth/logout" {...noBoxShadowStyle}>
                <Text {...linkStyle} data-testid="navigation-link-logout">
                  {data?.headerInformation?.content?.authentication?.logoutButton}
                </Text>
              </Link>
            </Stack>
          </Grid>
          <Grid display={{ mobile: 'grid', lg: 'none' }} justifyContent={'end'}>
            Mobile navigation
          </Grid>
        </Grid>
      </Container>
      {enableModal && (
        <ChangeLog
          showChangeLogModal={showChangeLogModal}
          setShowChangeLogModal={setShowChangeLogModal}
          bookingReference={bookingReference}
        />
      )}
    </Box>
  );

  function renderListItems(list: NavOption[], heading?: string, dataTestId?: string) {
    return (
      <List data-testid={dataTestId} {...listStyles}>
        {heading && <Heading {...headingStyles}>{t(heading)}</Heading>}

        {list.map((item: NavOption, index) => {
          return (
            <ListItem
              {...listItemStyles}
              key={index}
              data-testid={`${dataTestId}_listItemPopover-${index}`}
            >
              <Link href={item.url ?? '/'} target="_blank">
                <Text {...linkPopoverStyles}>{t(item.title)}</Text>
              </Link>
            </ListItem>
          );
        })}
      </List>
    );
  }
}

const agentLinkContainerStyles = {
  display: 'flex',
  alignItems: 'center',
  gap: 'sm',
};

const agentLinkMemoCountTextStyles = {
  fontSize: 'lg',
  fontStyle: 'normal',
  fontWeight: 'semibold',
  lineHeight: 'lg',
};

const agentLinkMemoCountCircleStyles = {
  size: '32px',
  bg: 'darkGrey3',
  color: 'baseWhite',
};

const languageSelectorWrapperStyles = {
  mr: { xl: '4rem' },
};

const headerWrapperStyles = {
  w: 'full',
  bg: 'white',
  boxShadow: '0 0.125rem 0.75rem var(--chakra-colors-lightGrey2)',
  h: { mobile: 'var(--chakra-space-4xl)', lg: 'var(--chakra-space-6xl)' },
  zIndex: 1010,
} as BoxProps;

const headingStyles = {
  _focusVisible: {
    outline: 'none',
  },
  fontSize: 'md',
  lineHeight: 3,
  mb: 'sm',
  fontWeight: 'semibold',
};

const contentStyles = {
  w: 'full',
  templateColumns: 'auto 1fr',
  alignItems: 'center',
} as GridProps;

const containerLogoStyle = {
  justifyContent: 'center',
  alignItems: 'center',
} as FlexProps;

const linkStyle = {
  fontSize: 'lg',
  lineHeight: 3,
  fontWeight: 'medium',
  textAlign: 'center',
  color: 'darkGrey1',
} as TextProps;

const linkPopoverStyles = {
  cursor: 'pointer',
  _hover: {
    textDecoration: 'underline',
  },
  _focusVisible: {
    outline: 'none',
  },
};

const linkStyleDE = {
  fontSize: 'lg',
  lineHeight: 3,
  fontWeight: 'medium',
  textAlign: 'center',
  maxWidth: '160px',
  color: 'darkGrey1',
} as TextProps;

const textStyleDisabled = {
  color: 'lightGrey3',
  pointerEvents: 'none',
  textDecoration: 'none',
} as LinkProps | TextProps;

const stackStyle = {
  direction: 'row',
  spacing: { lg: 'xl', xl: '3xl' },
  justifyContent: 'flex-end',
  alignItems: 'center',
  gap: 'var(--chakra-space-xl)',
} as StackProps;

const stackStyleDE = {
  flexDirection: 'row',
  justifyContent: 'flex-end',
  alignItems: 'center',
  gap: 7,
} as StackProps;

const noBoxShadowStyle = {
  _focus: {
    boxShadow: 'none',
  },
};

const agentMemoOpenStyle = {
  fontWeight: '700',
};

const listItemStyles = {
  w: '48',
  mb: '1',
  _focusVisible: {
    outline: 'none',
  },

  _hover: {
    a: {
      textDecoration: 'underline',
    },
  },
} as ListItemPropChakra;

const listStyles = {
  w: '48',
  mr: '5',
  color: 'darkGrey1',
};
