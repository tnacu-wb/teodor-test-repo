import {
  COOKIE_MODAL_CLOSED_EVENT,
  GLOBALS,
  ONETRUST_GROUPS_UPDATED_EVENT,
  getCanonicalLocale,
  getDynatraceConsentPaths,
  getOneTrustConfigByLocale,
  isOneTrustCookieConsentActive,
  syncDynatraceConsentFromCookie,
  syncDynatraceConsentFromOneTrust,
} from '@whitbread-eos/utils';
import getConfig from 'next/config';
import { useRouter } from 'next/router';
import Script from 'next/script';
import { useEffect } from 'react';

import AmazonChatIcon from '../AmazonChatIcon';

interface Props {
  isAmazonChatBoxEnabled?: boolean;
  language?: string;
  locale?: string;
  amazonChatIcon?: string;
  isOneTrustCookieConsentEnabled?: boolean;
  isDynatraceRumCookieConsentEnabled?: boolean;
}

export default function ScriptsEmbed({
  isAmazonChatBoxEnabled,
  language,
  locale,
  amazonChatIcon,
  isOneTrustCookieConsentEnabled,
  isDynatraceRumCookieConsentEnabled = false,
}: Readonly<Props>) {
  const { pathname } = useRouter();
  const includeAdobeScript = !pathname.includes('opera-shared-page');
  const { publicRuntimeConfig } = getConfig();
  let amazonChatUrl = publicRuntimeConfig.NEXT_PUBLIC_AMAZON_CHAT_URL_GB;
  let amazonChatId = publicRuntimeConfig.NEXT_PUBLIC_AMAZON_CHAT_ID_GB;
  const dtScript =
    language === GLOBALS.language.EN
      ? process.env.NEXT_PUBLIC_DYNATRACE_GB
      : process.env.NEXT_PUBLIC_DYNATRACE_DE;

  if (language === GLOBALS.language.DE) {
    amazonChatUrl = publicRuntimeConfig.NEXT_PUBLIC_AMAZON_CHAT_URL_DE;
    amazonChatId = publicRuntimeConfig.NEXT_PUBLIC_AMAZON_CHAT_ID_DE;
  }

  const oneTrustLocale = getCanonicalLocale(locale ?? language);
  const oneTrustConfig = getOneTrustConfigByLocale(oneTrustLocale);
  const isOneTrustActive = isOneTrustCookieConsentActive(
    isOneTrustCookieConsentEnabled,
    locale ?? language
  );

  useEffect(() => {
    if (!isAmazonChatBoxEnabled) {
      removeAmazonChatWidget();
    }
  }, [isAmazonChatBoxEnabled]);

  useEffect(() => {
    if (isOneTrustActive) {
      return;
    }

    const syncFromCustomCookie = () => {
      const [localeLanguage, localeCountry] = (locale ?? '').split('-');
      const customCookiePaths =
        localeLanguage && localeCountry
          ? getDynatraceConsentPaths(localeLanguage, localeCountry)
          : ['/'];

      syncDynatraceConsentFromCookie({
        isEnabled: isDynatraceRumCookieConsentEnabled,
        paths: customCookiePaths,
        domain: process.env.NEXT_PUBLIC_COOKIES_DOMAIN,
      });
    };

    syncFromCustomCookie();
    window.addEventListener(COOKIE_MODAL_CLOSED_EVENT, syncFromCustomCookie);

    return () => {
      window.removeEventListener(COOKIE_MODAL_CLOSED_EVENT, syncFromCustomCookie);
    };
  }, [isDynatraceRumCookieConsentEnabled, isOneTrustActive, locale]);

  function removeAmazonChatWidget() {
    document.querySelectorAll('#amazon-connect-chat-widget').forEach((el) => el.remove());
  }

  return (
    <>
      {includeAdobeScript && (
        <Script
          src="//assets.adobedtm.com/launch-EN1f330bc46c5949b29c22bbf3f0573f75.min.js"
          strategy="beforeInteractive"
          data-testid="adobe-script"
        ></Script>
      )}

      {dtScript && (
        <Script
          src={dtScript}
          data-testid="dynatrace-script"
          strategy="beforeInteractive"
          defer={false}
        />
      )}

      {isAmazonChatBoxEnabled && (
        <>
          {amazonChatIcon && <AmazonChatIcon amazonChatIcon={amazonChatIcon} />}
          <Script src={amazonChatUrl} id={amazonChatId} async={true} defer={false}></Script>
        </>
      )}

      {isOneTrustActive && (
        <OneTrustConsentScripts
          scriptUrl={process.env.NEXT_PUBLIC_ONE_TRUST_SCRIPT_URL as string}
          dataLanguage={oneTrustConfig.dataLanguage}
          domainScript={oneTrustConfig.domainScript as string}
          isDynatraceRumCookieConsentEnabled={isDynatraceRumCookieConsentEnabled}
        />
      )}
    </>
  );
}

// Kept together so the Dynatrace sync only runs when the OneTrust script is actually loaded.
function OneTrustConsentScripts({
  scriptUrl,
  dataLanguage,
  domainScript,
  isDynatraceRumCookieConsentEnabled,
}: Readonly<{
  scriptUrl: string;
  dataLanguage: string;
  domainScript: string;
  isDynatraceRumCookieConsentEnabled: boolean;
}>) {
  useEffect(() => {
    const syncConsent = () =>
      syncDynatraceConsentFromOneTrust({ isEnabled: isDynatraceRumCookieConsentEnabled });

    window.addEventListener(ONETRUST_GROUPS_UPDATED_EVENT, syncConsent);
    syncConsent();

    return () => {
      window.removeEventListener(ONETRUST_GROUPS_UPDATED_EVENT, syncConsent);
    };
  }, [isDynatraceRumCookieConsentEnabled]);

  return (
    <>
      <Script
        src={scriptUrl}
        data-document-language="true"
        data-language={dataLanguage}
        type="text/javascript"
        data-domain-script={domainScript}
        strategy="beforeInteractive"
        data-testid="one-trust-cookie-consent"
      />
      {/* OneTrust calls window.OptanonWrapper directly; it must exist even though we sync via OneTrustGroupsUpdated */}
      <Script id="optanon-wrapper" strategy="beforeInteractive">
        {`function OptanonWrapper() {}`}
      </Script>
    </>
  );
}
