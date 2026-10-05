import {
  Box,
  BoxProps,
  Flex,
  FlexProps,
  Modal,
  ModalBody,
  ModalContent,
  ModalContentProps,
  ModalOverlay,
  Slide,
  Text,
  TextProps,
} from '@chakra-ui/react';
import {
  Area,
  type NavItems,
  FT_PI_AUTH0_LOGIN,
  FT_PIB_LOGIN_REDIRECT,
  GET_STATIC_CONTENT,
  HeaderInformationQuery,
  SITE_LEISURE,
} from '@whitbread-eos/api';
import { BurgerMenu, ChevronRight, Icon, LanguageOptions } from '@whitbread-eos/atoms';
import { ManageBookingContainer } from '@whitbread-eos/molecules';
import {
  analytics,
  decodeIdToken,
  getAuthCookie,
  getLoggedInUserInfo,
  useAuthToken,
  useAuth0User,
  useRestQueryRequest,
  useUserData,
  useFeatureToggle,
  useAuth0Navigation,
  useCustomLocale,
  useQueryRequest,
} from '@whitbread-eos/utils';
import getConfig from 'next/config';
import { useEffect, useState } from 'react';

import { AuthContentManagerPIVariant } from '../../../../index';
import {
  getPibLoginRedirectUrl,
  getSecureTwoOrigin,
  isBusinessPage,
  useLogoutWithRedirect,
} from '../../helpers/helpers';
import AuthSideNav from './AuthSideNav';
import BusinessSideNav from './BusinessSideNav';
import DiscoverSideNav from './DiscoverSideNav';
import HeaderSideNav from './HeaderSideNav';
import LanguageSelectorSideNav from './LanguageSideNav';
import { renderLanguageItem } from './LanguageSideNav/LanguageSideNav.component';
import NavigationItem from './NavigationItem';
import { containerItemStyle } from './NavigationMenuMobile.style';

interface Props {
  currentLanguage: string;
  languagesList: LanguageOptions[];
  tickIcon: React.ReactElement;
  labels: NavItems;
  area: Area;
}

export default function NavigationMenuMobile({
  currentLanguage,
  languagesList,
  tickIcon,
  labels,
  area,
}: Readonly<Props>) {
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [isManageBookingModalOpen, setIsManageBookingModalOpen] = useState(false);
  const [isLoginModalOpen, setIsLoginModalOpen] = useState(false);
  const [selectedSideNav, setSelectedSideNav] = useState('default');
  const { publicRuntimeConfig = {} } = getConfig() || {};
  const { isLoggedIn } = useUserData();
  const { token, isAuth0Enabled: isAuth0TokenEnabled } = useAuthToken();
  const { user: auth0User } = useAuth0User(isAuth0TokenEnabled);
  const {
    [FT_PI_AUTH0_LOGIN]: isAuth0Enabled,
    [FT_PIB_LOGIN_REDIRECT]: isPibLoginRedirectEnabled,
  } = useFeatureToggle();
  const { language, country } = useCustomLocale();
  const { navigateToLogin, navigateToLogout } = useAuth0Navigation();

  const { data: headerInformationData }: HeaderInformationQuery = useQueryRequest(
    ['GetStaticContent', language, country],
    GET_STATIC_CONTENT,
    {
      country,
      language,
      site: SITE_LEISURE,
      businessBooker: false,
    }
  );

  // Get email from Auth0 user or legacy token
  const email =
    isAuth0TokenEnabled && auth0User?.email
      ? auth0User.email
      : decodeIdToken(getAuthCookie()).email;

  const { data } = useRestQueryRequest(
    ['userDetails', token],
    'GET',
    `${publicRuntimeConfig.NEXT_PUBLIC_REST_API}/customers/hotels/${email}?business=false`,
    { Authorization: `Bearer ${token}` },
    { enabled: isLoggedIn && !!email }
  );

  const logout = useLogoutWithRedirect(country, language, !!isAuth0Enabled, navigateToLogout);

  useEffect(() => {
    const handleMessages = (message: MessageEvent) => {
      if (message.origin === getSecureTwoOrigin()) {
        const action = typeof message?.data === 'string' ? message.data : message.data?.action;
        if (action === 'userLoggedOut') {
          toggleSideNav('default');
        }
      }
    };
    window.addEventListener('message', handleMessages);
    return () => {
      window.removeEventListener('message', handleMessages);
    };
  }, []);

  const { isBusiness } = getLoggedInUserInfo(token);

  useEffect(() => {
    const businessDomain =
      headerInformationData?.headerInformation?.content?.authentication?.login?.business
        ?.businessDomain;
    if (isLoggedIn && isBusiness && businessDomain) {
      window.location.href = getPibLoginRedirectUrl(businessDomain, language, country);
    }
  }, [isLoggedIn, isBusiness, headerInformationData, language, country]);

  const toggleManageModal = () => {
    setIsManageBookingModalOpen(!isManageBookingModalOpen);
  };

  const toggleLoginModal = () => {
    setIsLoginModalOpen(!isLoginModalOpen);
  };

  const handleLoginClick = () => {
    if (
      isPibLoginRedirectEnabled &&
      isBusinessPage(window.location.pathname) &&
      headerInformationData?.headerInformation?.content?.authentication?.login?.business
        ?.businessDomain
    ) {
      window.location.href = getPibLoginRedirectUrl(
        headerInformationData.headerInformation.content.authentication.login.business
          .businessDomain,
        language,
        country
      );
    } else if (isAuth0Enabled) {
      analytics.track('auth_sign_in_click', { authStep: 'sign_in_click' });
      navigateToLogin();
    } else {
      toggleLoginModal();
    }
  };

  const toggleSideNav = (sideNav: string) => {
    setSelectedSideNav(sideNav);
  };

  return (
    <Flex flexDir="column" data-testid="mobile-navigation-menu">
      <Flex
        {...burgerMenuContainerStyle}
        sx={{
          '@media print': {
            marginLeft: 'auto',
          },
        }}
        onClick={() => {
          setIsModalVisible(true);
        }}
      >
        <Icon
          _hover={{
            cursor: 'pointer',
          }}
          svg={<BurgerMenu data-testid="burgerMenu" />}
        />
        <Text margin={0} {...burgerMenuTextStyle}>
          {labels?.mobileMenuButton}
        </Text>
      </Flex>
      <Modal
        isOpen={isModalVisible}
        onClose={() => {
          setIsModalVisible(false);
        }}
        size="full"
      >
        <ModalOverlay zIndex="var(--chakra-zIndices-banner)" data-testid="modalOverlay" />
        <Slide
          direction="left"
          in={isModalVisible}
          style={{ zIndex: 'var(--chakra-zIndices-overlay)' } as any}
        >
          <ModalContent {...modalContentStyle} data-testid="modalSideBar">
            {renderSideNav(selectedSideNav)}
          </ModalContent>
        </Slide>
      </Modal>
    </Flex>
  );

  function renderSideNav(selectedSideNav: string) {
    switch (selectedSideNav) {
      case 'default': {
        return renderDefaultSideBar();
      }

      case 'languageSelector': {
        const languageSelectorLabels = {
          language: labels.language,
        };
        return (
          <LanguageSelectorSideNav
            currentLanguage={currentLanguage}
            languagesList={languagesList}
            onClickHeaderTitle={() => toggleSideNav('default')}
            tickIcon={tickIcon}
            area={area}
            labels={languageSelectorLabels}
          />
        );
      }

      case 'auth': {
        return (
          <AuthSideNav
            onClickHeaderTitle={() => toggleSideNav('default')}
            header={`${data?.contactDetail?.firstName} ${data?.contactDetail?.lastName}`}
            logout={logout}
            labels={labels}
          />
        );
      }

      case 'business': {
        return (
          <BusinessSideNav labels={labels} onClickHeaderTitle={() => toggleSideNav('default')} />
        );
      }

      case 'discover': {
        return (
          <DiscoverSideNav labels={labels} onClickHeaderTitle={() => toggleSideNav('default')} />
        );
      }
    }
  }

  function renderDefaultSideBar() {
    return (
      <>
        <HeaderSideNav />
        <ModalBody p="0" data-testid="defaultSideNav">
          <Flex flexDir="column">
            <Box
              data-testid={'Global-TriggerLanguageSideNav'}
              onClick={() => toggleSideNav('languageSelector')}
            >
              {renderLanguageItem({
                languagesList,
                locale: currentLanguage,
                icon: (
                  <Box pt="0.37rem" pl="sm">
                    <Icon svg={<ChevronRight />} />
                  </Box>
                ),
                withStyleHover: true,
              })}
            </Box>
            <Flex bgColor="lightGrey5" flexDir="column">
              {!isLoggedIn ? (
                <Box
                  onClick={handleLoginClick}
                  {...customContainerItemStyle}
                  data-testid="Global-Login-Mobile"
                >
                  {labels.logIn}
                </Box>
              ) : (
                !!data?.contactDetail && (
                  <Box
                    {...customContainerItemStyle}
                    onClick={() => toggleSideNav('auth')}
                    data-testid="Global-SideNav-UserName-NavItem-Mobile"
                  >
                    <NavigationItem
                      title={`${data.contactDetail?.firstName} ${data.contactDetail?.lastName}`}
                      icon={
                        <Box pt="0.37rem" pl="sm">
                          <Icon svg={<ChevronRight />} />
                        </Box>
                      }
                    />
                  </Box>
                )
              )}
              <AuthContentManagerPIVariant
                isLoginModalOpen={isLoginModalOpen}
                toggleLoginModal={toggleLoginModal}
              />
              <Box
                {...customContainerItemStyle}
                onClick={toggleManageModal}
                data-testid={'Global-TriggerManageModal'}
              >
                {labels.findBooking}
                <ManageBookingContainer
                  onClose={toggleManageModal}
                  isOpen={isManageBookingModalOpen}
                />
              </Box>
            </Flex>
            <Box
              {...customContainerItemStyle}
              data-testid={'Global-TriggerBusinessSideNav'}
              onClick={() => toggleSideNav('business')}
            >
              <NavigationItem
                title={labels.business}
                icon={
                  <Box pt="0.37rem" pl="sm">
                    <Icon svg={<ChevronRight />} />
                  </Box>
                }
              />
            </Box>
            <Box
              {...customContainerItemStyle}
              data-testid={'Global-TriggerDiscoverSideNav'}
              onClick={() => toggleSideNav('discover')}
            >
              <NavigationItem
                title={labels.discoverPI}
                icon={
                  <Box pt="0.37rem" pl="sm">
                    <Icon svg={<ChevronRight />} />
                  </Box>
                }
              />
            </Box>
          </Flex>
        </ModalBody>
      </>
    );
  }
}

const customContainerItemStyle = {
  ...containerItemStyle,
  _hover: {
    color: 'primary',
  },
} as BoxProps;

const modalContentStyle = {
  borderRadius: 0,
  position: 'absolute',
  zIndex: 'var(--chakra-zIndices-overlay)',
  left: 0,
  display: { lg: 'none' },
  w: { mobile: '72', xs: '21.44rem', sm: '21.875rem', md: '23.25rem' },
} as ModalContentProps;

const burgerMenuContainerStyle = {
  pt: '0.5rem',
  flexDir: 'column',
  justifyContent: 'center',
  alignItems: 'center',
} as FlexProps;

const burgerMenuTextStyle = {
  pt: '0.375rem',
  fontSize: 'sm',
  lineHeight: '2',
  fontWeight: 'normal',
} as TextProps;
