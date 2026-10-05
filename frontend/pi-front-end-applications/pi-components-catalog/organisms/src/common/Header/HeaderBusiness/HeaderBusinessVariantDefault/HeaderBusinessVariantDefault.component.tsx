import { Box, Container, Flex, Grid } from '@chakra-ui/react';
import {
  type BusinessNavItems,
  Language,
  type SubNavCategory,
  type SubNavCategoryItem,
  BB_MENU_IDS,
  BBHeaderInfo,
  businessAccountsLinks,
} from '@whitbread-eos/api';
import { Icon, LanguageOptions, Logo } from '@whitbread-eos/atoms';
import {
  formatAssetsUrl,
  getAuthCookie,
  getLoggedInUserInfo,
  getSecureTwoURL,
  useCompanyDetails,
  isStringValid,
  useCustomLocale,
  useUserData,
  useUserDetails,
  formatForMobile,
  superRoleFilter,
  langFilter,
} from '@whitbread-eos/utils';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';

import { getListOfLanguagesForSwitcher } from '../../helpers/helpers';
import {
  containerLogoStyle,
  containerWrapperStyles,
  contentStyles,
  headerWrapperStyles,
} from '../HeaderBusiness.style';
import BBNavigationMenu from '../NavigationMenu';
import BBNavigationMenuMobile from '../NavigationMenuMobile';

interface Props {
  BBHeaderInfo: BBHeaderInfo;
}

export default function HeaderBusinessVariantDefault({ BBHeaderInfo }: Readonly<Props>) {
  const { content, config }: BBHeaderInfo = BBHeaderInfo;
  const { language, country } = useCustomLocale();
  const [origin, setOrigin] = useState('');
  const [languagesList, setLanguagesList] = useState<LanguageOptions[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const router = useRouter();
  const isWindowDefined = typeof window !== 'undefined';
  const [mappedNavigationLabels, setMappedNavigationLabels] = useState<BusinessNavItems[]>([]);
  const { isLoggedIn } = useUserData();
  const business = useUserDetails(true, isLoggedIn) || {};
  const idTokenCookie = getAuthCookie();
  const { accessLevel, cdhCompanyId, sessionId, cdhEmployeeId } =
    getLoggedInUserInfo(idTokenCookie);
  const { requestedCompany } =
    useCompanyDetails(cdhCompanyId, sessionId, cdhEmployeeId, isLoggedIn) || {};

  const logoutLabel = content?.authentication?.logoutButton;
  const isLogoutButton = (title: string) => title === logoutLabel;
  const homeBB = `${origin}/${country}/${language}/business-booker/home.html`;

  const tickIconSrc = content?.menu?.tick;
  const tickIcon = <Icon src={formatAssetsUrl(tickIconSrc)} color="var(--chakra-colors-primary)" />;
  const isLanguageListLoaded = !isLoading && !!languagesList;
  const countries = content?.countries;

  useEffect(() => {
    setOrigin(window.location.origin);
  }, [isWindowDefined]);

  useEffect(() => {
    const handleMessages = (message: any) => {
      if (message?.origin === getSecureTwoURL()) {
        const action = typeof message?.data === 'string' ? message.data : message.data?.action;
        if (action === 'userLoggedOut') {
          router.push(homeBB);
        }
      }
    };
    window.addEventListener('message', handleMessages);
    return () => {
      window.removeEventListener('message', handleMessages);
    };
  }, [homeBB, router]);

  useEffect(() => {
    if (!content || !config || !isStringValid(idTokenCookie) || !requestedCompany || !business) {
      return;
    }
    const [businessAccountOptions] = content.subNav.filter(
      (item: SubNavCategory) => item.title === BB_MENU_IDS.BUSINESS_ACCOUNT
    );
    const businessAccountMenu =
      language !== 'de'
        ? {
            navOptions: businessAccountOptions?.navOptions
              .map((navOption: SubNavCategoryItem) => {
                if (!navOption.title.includes(BB_MENU_IDS.ACCOUNT_DASHBOARD)) {
                  return navOption;
                } else if (
                  navOption.title.includes(BB_MENU_IDS.ACCOUNT_DASHBOARD) &&
                  business?.tethered
                ) {
                  return { title: navOption.title, url: navOption.url, superRole: true };
                }
              })
              .filter((navOption: SubNavCategoryItem | undefined) => navOption),
            title: businessAccountOptions?.title,
          }
        : null;

    const mappedCompanyMenu = {
      navOptions: [
        ...config.authentication.accountLinks,
        ...config.authentication.business.businessAccountLinks.map(
          (item: businessAccountsLinks) => ({
            ...item,
            superRole: true,
          })
        ),
        { title: logoutLabel },
      ],
      title: BB_MENU_IDS.COMPANY_DETAILS,
    };
    if (!mappedNavigationLabels.length) {
      const navItems = [
        {
          navTitle: content?.menu?.business,
          subNav: content?.subNav?.filter(
            (item) => item.title === BB_MENU_IDS.BUSINESS_CUSTOMERS
          ) as SubNavCategory[],
          id: BB_MENU_IDS.ABOUT,
        },
        {
          navTitle: businessAccountMenu?.title,
          subNav: superRoleFilter(accessLevel, [businessAccountMenu as SubNavCategory]),
          id: BB_MENU_IDS.BUSINESS,
        },
        {
          navTitle: requestedCompany?.companyDetails?.companyName || '',
          subNav: superRoleFilter(accessLevel, [mappedCompanyMenu]),
          id: BB_MENU_IDS.COMPANY,
        },
      ];

      setMappedNavigationLabels(langFilter(language, navItems));
    }
  }, [
    accessLevel,
    content,
    config,
    idTokenCookie,
    requestedCompany,
    language,
    logoutLabel,
    isLoggedIn,
    mappedNavigationLabels,
    business,
  ]);

  useEffect(() => {
    if (countries?.length > 0) {
      setLanguagesList(getListOfLanguagesForSwitcher(countries) as LanguageOptions[]);
      setIsLoading(false);
    }
  }, [countries]);

  const genericLabels = {
    language: content?.menu?.language,
    mobileMenuButton: content?.menu?.mobileMenuButton,
  };

  return (
    <Box {...headerWrapperStyles} data-testid="HeaderBusiness">
      <Container {...containerWrapperStyles(language as Language)}>
        <Grid {...contentStyles} sx={{ '@media print': { py: 'sm' } }}>
          <Flex {...containerLogoStyle} data-testid="logo-container">
            <Logo href={homeBB} src={formatAssetsUrl(content?.header?.image)} variant={'pi-icon'} />
          </Flex>
          <Grid
            templateColumns="1fr auto"
            display={{ mobile: 'none', lg: 'grid' }}
            sx={{ '@media print': { display: 'none' } }}
          >
            {isLanguageListLoaded && (
              <BBNavigationMenu
                navigationLabels={mappedNavigationLabels}
                languageMenuLabels={languagesList}
                language={language as Language}
                tickIcon={tickIcon}
                dataTestId="HeaderBusiness"
                isLogoutButton={isLogoutButton}
              />
            )}
          </Grid>
          <Grid display={{ mobile: 'grid', lg: 'none' }} justifyContent={'end'}>
            {isLanguageListLoaded && (
              <BBNavigationMenuMobile
                navigationLabels={formatForMobile(mappedNavigationLabels)}
                languageMenuLabels={languagesList}
                language={language as Language}
                tickIcon={tickIcon}
                isLogoutButton={isLogoutButton}
                genericLabels={genericLabels}
              />
            )}
          </Grid>
        </Grid>
      </Container>
    </Box>
  );
}
