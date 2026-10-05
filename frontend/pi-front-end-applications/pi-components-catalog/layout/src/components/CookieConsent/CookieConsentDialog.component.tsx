'use client';

import { CountryLanguage } from '@whitbread-eos/api';
import {
  DialogContent,
  DialogTitle,
  Button,
  Dialog,
  DialogHeader,
  DialogFooter,
  DialogDescription,
  Switch,
} from '@whitbread-eos/atoms/ui';
import {
  DYNATRACE_CONSENT_COOKIE_NAME,
  CookiePoliciesLabels,
  renderSanitizedHtml,
  CookiePermission,
  setCookie,
  syncDynatraceConsent,
} from '@whitbread-eos/utils';
import { useState, useEffect } from 'react';

import {
  CookieConsentDialogData,
  useCookieConsent,
  CONSENT_COOKIE,
  ONE_YEAR_IN_MINUTES,
} from './CookieConsentProvider';

export const getCookiePath = (
  countryLanguage: CountryLanguage = { country: 'gb', language: 'en' },
  localeFormat: 'language-region' | 'region'
): string => {
  return localeFormat === 'language-region'
    ? `/${countryLanguage.language}-${countryLanguage.country}`
    : `/${countryLanguage.country}`;
};

export const preventDefault = (e: any) => {
  e?.preventDefault?.();
  e?.stopPropagation?.();
  return false;
};

interface CookieConsentDialogProps {
  isDynatraceRumCookieConsentEnabled?: boolean;
}

export const CookieConsentDialog = ({
  isDynatraceRumCookieConsentEnabled = false,
}: CookieConsentDialogProps) => {
  const cookieDomain = process.env.NEXT_PUBLIC_COOKIES_DOMAIN;
  const [open, setOpen] = useState(true);
  const [isManageCookieModalOpen, setIsManageCookieModalOpen] = useState(false);
  const data: CookieConsentDialogData = useCookieConsent();
  const countryLanguage = (data.countryLanguage as CountryLanguage) || {};
  const cookiePolicies = (data.cookieConsentData as CookiePoliciesLabels)?.cookieConsent
    ?.cookiePolicies;
  const defaultCookiesPermissions: CookiePermission[] = cookiePolicies?.manageView.cookieGroup.map(
    (cookie, index) => {
      return { id: index, name: cookie.cookieName, value: cookie.isAlwaysActive };
    }
  );
  const [iframeLoaded, setIframeLoaded] = useState(false);
  const [cookiePermissions, setCookiePermissions] =
    useState<CookiePermission[]>(defaultCookiesPermissions);

  useEffect(() => {
    const handleMessage = (message: MessageEvent) => {
      if (typeof window !== 'undefined') {
        const baseUrl = window.location.origin;
        if (
          message?.origin === baseUrl &&
          message?.data?.type === 'COOKIES_IFRAME_LOADED' &&
          message?.data?.consentCookies?.consentGiven !== undefined
        ) {
          const {
            consentGiven: consentGivenOnBb,
            permissionExperience,
            permissionMarketing,
            permissionPerformance,
          } = message.data.consentCookies;
          if (consentGivenOnBb) {
            setCookiePolicies(
              [
                { id: 0, name: 'permissionExperience', value: permissionExperience },
                { id: 1, name: 'permissionMarketing', value: permissionMarketing },
                { id: 2, name: 'permissionPerformance', value: permissionPerformance },
              ],
              true
            );
          } else {
            setIframeLoaded(true);
          }
        }
      }
    };

    window.addEventListener('message', handleMessage);

    return () => {
      window.removeEventListener('message', handleMessage);
    };
  }, []);

  const manageCookies = () => {
    setIsManageCookieModalOpen(true);
  };

  const onConfirmCookieSettingsClick = () => {
    setCookiePolicies(cookiePermissions);
    setIsManageCookieModalOpen(false);
    setOpen(false);
  };

  const setAllUserCookies = (cookieValue = false, closeModal = true) => {
    const newCookiePermissions = cookiePermissions?.map((cookiePermission) => {
      if (cookiePermission.name !== 'permissionEssential') {
        return {
          ...cookiePermission,
          value: cookieValue,
        };
      }
      return cookiePermission;
    });
    setCookiePolicies(newCookiePermissions);

    closeModal && setOpen(false);
    closeModal && setIsManageCookieModalOpen(false);
  };

  const setCookiePermissionValue = (cookieName: string, status: boolean) => {
    const newCookiePermissions = cookiePermissions?.map((cookiePermission) => {
      if (cookiePermission.name === cookieName) {
        return {
          ...cookiePermission,
          value: status,
        };
      }
      return cookiePermission;
    });

    setCookiePermissions(newCookiePermissions);
  };

  const setCookiePolicies = (
    cookiePermissionsParam?: CookiePermission[],
    setOnlyForInnBusiness = false
  ) => {
    const permissions = cookiePermissionsParam ?? cookiePermissions;

    permissions?.forEach((permission) => {
      cookiePolicies?.manageView.cookieGroup.forEach((cookie) => {
        if (cookie.cookieName !== 'permissionEssential' && cookie.cookieName === permission.name) {
          setCookie(
            cookie.cookieName,
            permission.value,
            ONE_YEAR_IN_MINUTES,
            getCookiePath(countryLanguage, 'language-region'),
            undefined,
            cookieDomain
          );
          if (!setOnlyForInnBusiness) {
            setCookie(
              cookie.cookieName,
              permission.value,
              ONE_YEAR_IN_MINUTES,
              getCookiePath(countryLanguage, 'region'),
              undefined,
              cookieDomain
            );
          }
          if (permission.name === DYNATRACE_CONSENT_COOKIE_NAME) {
            const dynatraceCookiePaths = [getCookiePath(countryLanguage, 'language-region')];
            if (!setOnlyForInnBusiness) {
              dynatraceCookiePaths.push(getCookiePath(countryLanguage, 'region'));
            }

            syncDynatraceConsent({
              hasConsent: permission.value,
              isEnabled: isDynatraceRumCookieConsentEnabled,
              expiryMinutes: ONE_YEAR_IN_MINUTES,
              paths: dynatraceCookiePaths,
              domain: cookieDomain,
            });
          }
        }
      });
    });

    setCookie(
      CONSENT_COOKIE,
      1,
      ONE_YEAR_IN_MINUTES,
      getCookiePath(countryLanguage, 'language-region'),
      undefined,
      cookieDomain
    );
    if (!setOnlyForInnBusiness) {
      setCookie(
        CONSENT_COOKIE,
        1,
        ONE_YEAR_IN_MINUTES,
        getCookiePath(countryLanguage, 'region'),
        undefined,
        cookieDomain
      );
    }
  };

  if (!data?.cookieConsentData || !countryLanguage || !cookiePolicies) {
    return null;
  }

  if (!iframeLoaded) {
    return (
      <iframe
        src={`/${countryLanguage.country}/${countryLanguage.language}/business-booker/cookies`}
        style={{ display: 'none' }}
        id="cookiesIframe"
        title="BusinessBooker cookies"
      ></iframe>
    );
  }

  if (isManageCookieModalOpen) {
    return (
      <Dialog
        closeOnOverlayClick={false}
        open={isManageCookieModalOpen}
        onOpenChange={setIsManageCookieModalOpen}
      >
        <DialogContent
          onEscapeKeyDown={preventDefault}
          onPointerDownOutside={preventDefault}
          data-testid="ManageCookiesDialog-Container"
          className={dialogStyles}
        >
          <DialogHeader>
            <DialogTitle className="mb-4">{cookiePolicies.manageView.title}</DialogTitle>
            <DialogDescription data-testid="ManageCookiesDialog-Description">
              {renderSanitizedHtml(cookiePolicies.manageView.description)}
            </DialogDescription>
          </DialogHeader>

          <div className="mt-4">
            <div
              className="grid mobile:grid-cols-1 grid-cols-2 gap-6"
              data-testid="ManageCookiesDialog-CookieGroup"
            >
              {cookiePolicies.manageView.cookieGroup.map((cookie, cookieIndex) => {
                const alwaysActiveContainerStyle = `flex ${
                  cookie.isAlwaysActive ? 'justify-end min-w-[150px]' : 'ml-4'
                }`;
                return (
                  <div
                    key={cookieIndex}
                    className="mb-0 p-4 flex justify-between"
                    data-testid={`ManageCookiesDialog-${cookie?.cookieName}`}
                  >
                    <div>
                      <h3
                        className="text-lg font-semibold"
                        data-testid={`ManageCookiesDialog-${cookie?.title}`}
                      >
                        {cookie.title}
                      </h3>
                      {cookie.description && (
                        <p
                          className="text-sm"
                          data-testid={`ManageCookiesDialog-${cookie?.title}-Description`}
                        >
                          {renderSanitizedHtml(cookie.description)}
                        </p>
                      )}
                    </div>
                    <div className={alwaysActiveContainerStyle}>
                      {cookie.isAlwaysActive ? (
                        <span
                          className="text-primaryColor font-medium"
                          data-testid={`ManageCookiesDialog-${cookie?.cookieName}-Text`}
                        >
                          {cookiePolicies.manageView.alwaysActiveText}
                        </span>
                      ) : (
                        <Switch
                          id={`cookie-${cookieIndex}`}
                          data-testid={`ManageCookiesDialog-${cookie?.cookieName}-Switch`}
                          defaultChecked={cookie.isAlwaysActive}
                          onCheckedChange={(checked = false) =>
                            setCookiePermissionValue(cookie.cookieName, checked)
                          }
                        />
                      )}
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          <DialogFooter className={manageCookiesConfirmStyle}>
            <Button
              variant="dialogDefault"
              onClick={onConfirmCookieSettingsClick}
              data-testid={`ManageCookiesDialog-${cookiePolicies.manageView.saveSettingsButtonText}`}
            >
              {cookiePolicies.manageView.saveSettingsButtonText}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    );
  } else {
    return (
      <Dialog open={open} onOpenChange={setOpen}>
        <DialogContent
          className={dialogStyles}
          hasCloseButton={false}
          onEscapeKeyDown={preventDefault}
          onPointerDownOutside={preventDefault}
          data-testid="CookieConsentDialog-Container"
        >
          <DialogHeader>
            <DialogTitle className="mb-0">{cookiePolicies.introView.title}</DialogTitle>
          </DialogHeader>

          <div className="mt-4" data-testid="CookieConsentDialog-Description">
            {renderSanitizedHtml(cookiePolicies.introView.description)}
          </div>

          <DialogFooter>
            <Button
              id="manage-cookies-button"
              variant="dialogOutline"
              onClick={manageCookies}
              data-testid="CookieConsentDialog-ManageButton"
            >
              {cookiePolicies.introView.manageButtonText}
            </Button>
            {cookiePolicies?.introView?.necessaryOnlyButtonText && (
              <Button
                id="necessary-only-cookies-button"
                variant="dialogDefault"
                onClick={() => setAllUserCookies(false)}
                data-testid="CookieConsentDialog-NecessaryOnlyButton"
              >
                {cookiePolicies.introView.necessaryOnlyButtonText}
              </Button>
            )}
            <Button
              id="accept-all-cookies-button"
              variant="dialogDefault"
              onClick={() => setAllUserCookies(true)}
              data-testid="CookieConsentDialog-AcceptAllButton"
            >
              {cookiePolicies.introView.acceptAllButtonText}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    );
  }
};

const dialogStyles = 'max-w-[864px] rounded-[14px] gap-6';
const manageCookiesConfirmStyle = 'w-[288px] mobile:w-full';
