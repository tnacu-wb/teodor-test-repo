import { FlexProps, TextProps } from '@chakra-ui/react';
import { GET_COOKIE_CONSENT_INFO } from '@whitbread-eos/api';
import { CookieModalVariantProps, ModalVariants } from '@whitbread-eos/atoms';
import {
  DYNATRACE_CONSENT_COOKIE_NAME,
  getDynatraceConsentPaths,
  setCookie,
  syncDynatraceConsent,
  useCustomLocale,
  useQueryRequest,
  CookieGroup,
  CookiePermission,
  CookiePoliciesLabels,
} from '@whitbread-eos/utils';
import { useEffect, useState } from 'react';

import { CONSENT_COOKIE } from './CookiePolicies.constants';
import CookiePolicies from './CookiePolicies/CookiePolicies.component';
import ManageCookies from './ManageCookies/ManageCookies.component';

export interface CookiePoliciesModalContainerProps {
  isOpen: boolean;
  onClose: () => void;
  brand: string;
  isDynatraceRumCookieConsentEnabled?: boolean;
}

export default function CookiePoliciesModalContainer({
  onClose,
  isOpen,
  brand,
  isDynatraceRumCookieConsentEnabled = false,
}: Readonly<CookiePoliciesModalContainerProps>) {
  const { language, country } = useCustomLocale();
  const [isManageCookieModalOpen, setIsManageCookieModalOpen] = useState(false);
  const cookieDomain = process.env.NEXT_PUBLIC_COOKIES_DOMAIN;

  const { data } = useQueryRequest(
    ['getCookieConsentInfoQuery', country, language, brand],
    GET_COOKIE_CONSENT_INFO,
    {
      country,
      language,
      brand,
    },
    {
      select: (data: any) => {
        if (data?.cookieConsent?.cookiePolicies?.manageView?.cookieGroup?.map) {
          data.cookieConsent.cookiePolicies.manageView.cookieGroup.forEach((group: CookieGroup) => {
            if (group.cookieName === 'permissionEssential') {
              group.isAlwaysActive = true;
            }
          });
        }
        return data;
      },
    }
  );
  const cookiePolicies = (data as CookiePoliciesLabels)?.cookieConsent?.cookiePolicies;
  const manageCookies = () => {
    setIsManageCookieModalOpen(true);
  };

  const defaultCookiesPermissions: CookiePermission[] = cookiePolicies?.manageView.cookieGroup.map(
    (cookie, index) => {
      return { id: index, name: cookie.cookieName, value: cookie.isAlwaysActive };
    }
  );
  const [cookiePermissions, setCookiePermissions] = useState<CookiePermission[]>([]);

  useEffect(() => {
    if (data) {
      setCookiePermissions(defaultCookiesPermissions);
    }
  }, [data]);

  const COOKIE_OPT_IN_EXPIRY_DAYS =
    60 * 24 * (cookiePolicies?.config?.cookieOptInExpiryDays ?? 365);
  const COOKIE_OPT_OUT_EXPIRY_DAYS =
    60 * 24 * (cookiePolicies?.config?.cookieOptOutExpiryDays ?? 365);

  const setCookiePolicies = (cookiePermissionsParam?: CookiePermission[]) => {
    const permissions = cookiePermissionsParam ?? cookiePermissions;
    let performanceCookie;

    permissions?.forEach((permission) => {
      cookiePolicies?.manageView.cookieGroup.forEach((cookie) => {
        if (cookie.cookieName !== 'permissionEssential' && cookie.cookieName === permission.name) {
          setCookie(
            cookie.cookieName,
            permission.value,
            !permission.value ? COOKIE_OPT_OUT_EXPIRY_DAYS : COOKIE_OPT_IN_EXPIRY_DAYS,
            `/${country}`,
            undefined,
            cookieDomain
          );
          if (permission.name === 'permissionPerformance') {
            performanceCookie = permission.value;
          }
          setCookie(
            cookie.cookieName,
            permission.value,
            !permission.value ? COOKIE_OPT_OUT_EXPIRY_DAYS : COOKIE_OPT_IN_EXPIRY_DAYS,
            `/${language}-${country}`,
            undefined,
            cookieDomain
          );
          if (permission.name === DYNATRACE_CONSENT_COOKIE_NAME) {
            syncDynatraceConsent({
              hasConsent: permission.value,
              isEnabled: isDynatraceRumCookieConsentEnabled,
              expiryMinutes: permission.value
                ? COOKIE_OPT_IN_EXPIRY_DAYS
                : COOKIE_OPT_OUT_EXPIRY_DAYS,
              paths: getDynatraceConsentPaths(language, country),
              domain: cookieDomain,
            });
          }
        }
      });
    });

    setCookie(
      CONSENT_COOKIE,
      1,
      performanceCookie ? COOKIE_OPT_IN_EXPIRY_DAYS : COOKIE_OPT_OUT_EXPIRY_DAYS,
      `/${country}`,
      undefined,
      cookieDomain
    );
    setCookie(
      CONSENT_COOKIE,
      1,
      performanceCookie ? COOKIE_OPT_IN_EXPIRY_DAYS : COOKIE_OPT_OUT_EXPIRY_DAYS,
      `/${language}-${country}`,
      undefined,
      cookieDomain
    );
    onClose();
  };

  return (
    <>
      {data && (
        <ModalVariants
          isOpen={isOpen}
          onClose={onClose}
          dataTestId={'CookiePolicyContainer'}
          variant="cookie"
          variantProps={
            {
              title: !isManageCookieModalOpen
                ? cookiePolicies?.introView?.title
                : cookiePolicies?.manageView.title,
              closeOnOverlayClick: false,
              overflowVisible: false,
              isCentered: true,
              isCookieConsentModal: !isManageCookieModalOpen,
            } as CookieModalVariantProps
          }
        >
          {!isManageCookieModalOpen ? (
            <CookiePolicies
              labels={data}
              cookiePermissions={cookiePermissions}
              setCookiePolicies={setCookiePolicies}
              manageCookies={manageCookies}
              modalStyles={{ wrapperStyles, buttonsStyle, textStyles }}
            />
          ) : (
            <ManageCookies
              labels={data}
              cookiePermissions={cookiePermissions}
              setCookiePermissions={setCookiePermissions}
              setCookiePolicies={setCookiePolicies}
              modalStyles={{ wrapperStyles, textStyles, buttonsStyle }}
            />
          )}
        </ModalVariants>
      )}
    </>
  );
}

const wrapperStyles = {
  alignItems: 'center',
  justifyContent: 'center',
  direction: 'column',

  padding: 'lg',
} as FlexProps;

const buttonsStyle = {
  display: 'flex',
  height: 'auto',
  alignContent: 'baseline',
  flexDirection: { mobile: 'column', xs: 'column', md: 'row', sm: 'row' },
  width: '100%',
} as FlexProps;

const textStyles = {
  title: {
    color: 'darkGrey1',
    fontWeight: 'semibold',
    fontSize: 'xl',
    lineHeight: '3',
  } as TextProps,
  description: {
    color: 'darkGrey1',
    textAlign: 'left',
    fontWeight: 'normal',
    fontSize: 'md',
    lineHeight: '3',
  } as TextProps,
};
