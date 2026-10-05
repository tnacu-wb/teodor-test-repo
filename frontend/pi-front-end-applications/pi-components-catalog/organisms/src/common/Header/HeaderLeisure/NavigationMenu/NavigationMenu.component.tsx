import { Box, Flex, Heading, List, ListItem, Text } from '@chakra-ui/react';
import {
  type NavItems,
  type SubNavCategory,
  FT_PI_SIGNUP_IN_HEADER,
  FT_PI_AUTH0_LOGIN,
  FT_PIB_LOGIN_REDIRECT,
  GET_STATIC_CONTENT,
  HeaderInformationQuery,
  SITE_LEISURE,
} from '@whitbread-eos/api';
import { Popover, Button } from '@whitbread-eos/atoms';
import { ManageBookingContainer } from '@whitbread-eos/molecules';
import {
  decodeIdToken,
  getAuthCookie,
  getLoggedInUserInfo,
  useAuthToken,
  useAuth0User,
  useRestQueryRequest,
  useUserData,
  useCustomLocale,
  useFeatureToggle,
  analytics,
  useAuth0Navigation,
  useQueryRequest,
} from '@whitbread-eos/utils';
import getConfig from 'next/config';
import { useSearchParams } from 'next/navigation';
import { useEffect, useLayoutEffect, useState } from 'react';

import { AuthContentManagerPIVariant } from '../../../../index';
import {
  getPibLoginRedirectUrl,
  isBusinessPage,
  useLogoutWithRedirect,
} from '../../helpers/helpers';
import NavItem from '../NavItem';
import AuthSubNav from './AuthSubNav';
import {
  boxStyles,
  businessListStyles,
  headingStyles,
  linkStyles,
  listItemStyles,
  listStyles,
} from './NavigationMenu.style';

interface Props {
  labels: NavItems;
}

export default function NavigationMenu({ labels }: Readonly<Props>) {
  const { publicRuntimeConfig = {} } = getConfig() || {};
  const [isManageBookingModalOpen, setIsManageBookingModalOpen] = useState(false);
  const [isLoginModalOpen, setIsLoginModalOpen] = useState(false);
  const { isLoggedIn } = useUserData();
  const { token, isAuth0Enabled: isAuth0TokenEnabled } = useAuthToken();
  const { user: auth0User } = useAuth0User(isAuth0TokenEnabled);
  const {
    [FT_PI_SIGNUP_IN_HEADER]: isSignupInHeaderEnabled,
    [FT_PI_AUTH0_LOGIN]: isAuth0Enabled,
    [FT_PIB_LOGIN_REDIRECT]: isPibLoginRedirectEnabled,
  } = useFeatureToggle();

  const searchParams = useSearchParams();
  const manageBooking = searchParams?.get('manage-booking') === 'true';

  useLayoutEffect(() => {
    if (manageBooking) {
      setIsManageBookingModalOpen(true);
    }
  }, [manageBooking]);

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

  const { language, country } = useCustomLocale();
  const { navigateToLogin, navigateToLogout, navigateToSignup } = useAuth0Navigation();

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

  const logout = useLogoutWithRedirect(country, language, !!isAuth0Enabled, navigateToLogout);

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
      if (isLoginModalOpen) {
        // Legacy Secure2 login modal
        analytics.remove(['loginClicked']);
      } else {
        analytics.update({
          loginClicked: true,
        });
      }
      setIsLoginModalOpen(!isLoginModalOpen);
    }
  };

  const toggleLoginModal = () => {
    setIsLoginModalOpen(!isLoginModalOpen);
  };

  const LoginNavigation = () => {
    if (isSignupInHeaderEnabled && labels?.signUpButton) {
      return (
        <Flex gap="0.5rem">
          <Button variant="primary" onClick={handleLoginClick} {...buttonStyles}>
            {labels.logIn}
          </Button>
          <Button
            variant="primary"
            onClick={() => {
              if (isAuth0Enabled) {
                analytics.track('auth_sign_up_click', { authStep: 'sign_up_click' });
                navigateToSignup();
              } else {
                window.location.href = `/${country}/${language}/account/register.html?intcmp=navbar_register_button`;
              }
            }}
            {...buttonStyles}
            data-testid="Global-SignUp-Desktop"
          >
            {labels.signUpButton}
          </Button>
        </Flex>
      );
    } else {
      return (
        <NavItem
          data-testid="Global-Login-Desktop"
          onClick={handleLoginClick}
          title={labels.logIn}
        />
      );
    }
  };

  return (
    <Flex justifyContent="flex-end" data-testid="menuWrapperId" alignItems="center">
      <Box {...boxStyles} data-testid="listId">
        <Popover triggerItem={<NavItem title={labels.discoverPI} />}>
          {labels.subNav && <Flex>{renderSubNavItems(labels.subNav, true)}</Flex>}
        </Popover>
      </Box>
      <Box {...boxStyles} data-testid="listId">
        <Popover triggerItem={<NavItem title={labels.business} />}>
          <Flex {...businessListStyles}>
            {labels?.businessSubNav && renderSubNavItems([labels.businessSubNav], false)}
          </Flex>
        </Popover>
      </Box>
      <Box {...boxStyles} data-testid="listId">
        <NavItem
          onClick={toggleManageModal}
          title={labels.findBooking}
          data-testid="ManageBookingButton"
        />
        <ManageBookingContainer onClose={toggleManageModal} isOpen={isManageBookingModalOpen} />
      </Box>
      <Box {...boxStyles} mr="0" data-testid="listId">
        {!isLoggedIn ? (
          <LoginNavigation />
        ) : (
          !!data?.contactDetail && (
            <Popover
              triggerItem={
                <NavItem
                  title={`${data?.contactDetail?.firstName} ${data?.contactDetail?.lastName}`}
                  date-testid="Global-UserName-Desktop"
                />
              }
            >
              <AuthSubNav labels={labels} onLogout={logout} />
            </Popover>
          )
        )}
        <AuthContentManagerPIVariant
          isLoginModalOpen={isLoginModalOpen}
          toggleLoginModal={toggleLoginModal}
        />
      </Box>
    </Flex>
  );

  function renderSubNavItems(list: SubNavCategory[], showHeading?: boolean) {
    return list.map((item) => {
      return (
        <List key={item.title} {...listStyles}>
          {showHeading && item.title && <Heading {...headingStyles}>{item.title}</Heading>}
          {item.navOptions.map((option) => {
            return (
              <ListItem {...listItemStyles} key={option.title} data-testid="listItem">
                <a href={option.url ?? '/'}>
                  <Text {...linkStyles}>{option.title}</Text>
                </a>
              </ListItem>
            );
          })}
        </List>
      );
    });
  }
}

const buttonStyles = {
  width: '7.5rem',
  height: '2.75rem',
  borderRadius: '5px',
  _hover: {
    boxShadow: 'none',
    backgroundColor: '#005a6a',
  },
};
