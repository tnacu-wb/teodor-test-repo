import {
  FT_DYNATRACE_RUM_COOKIE_CONSENT,
  PI_FAVICON,
  LOCALES,
  FT_IB_USER_PILOT,
  FT_ONE_TRUST_COOKIE_CONSENT,
} from '@whitbread-eos/api';
import {
  formatIBAssetsUrl,
  ID_TOKEN_COOKIE,
  getEmployeeDataforUserPilot,
  getServerUnleashToggles,
  getCountryLanguageByLocale,
  getTranslations,
  getCanonicalLocale,
  getOneTrustConfigByLocale,
  isOneTrustCookieConsentActive,
} from '@whitbread-eos/utils/server';
import type { Metadata } from 'next';
import { cookies, headers } from 'next/headers';
import Script from 'next/script';

import { UserPilot } from '~components/innBusiness/UserPilot';

import DynatraceConsentSync from '../components/innBusiness/DynatraceConsentSync/DynatraceConsentSync';
import FontWrapper from '../components/innBusiness/FontWrapper/font-wrapper';
import './global.css';

type Props = {
  children: React.ReactNode;
};

export async function generateMetadata(): Promise<Metadata> {
  const defaultMetadata = {
    icons: { icon: formatIBAssetsUrl(PI_FAVICON) },
    robots: { index: false, follow: false },
  };
  const headersList = await headers();
  const currentPath = headersList.get('WB-Url') ?? '';
  if (!currentPath) {
    return defaultMetadata;
  }
  try {
    const url = new URL(currentPath);
    const pathSegments = url.pathname.split('/').filter(Boolean);
    const localeValue = pathSegments[0]?.toLowerCase();
    const locale = (
      Object.values(LOCALES).includes(localeValue as LOCALES) ? localeValue : LOCALES.EN
    ) as LOCALES;
    const { language } = getCountryLanguageByLocale(locale);
    const { t } = await getTranslations(language, ['layout']);

    const segments = pathSegments.slice(1);

    let pageTitle = '';
    for (let i = segments.length; i > 0; i--) {
      const key = segments.slice(0, i).join('.');
      const translationKey = `layout.pageTitle.${key}`;
      const translated = t(translationKey);

      if (translated && translated !== translationKey) {
        pageTitle = translated;
        break;
      }
    }
    const finalTitle = pageTitle ? pageTitle : t('layout.pageTitle');
    return { title: { default: finalTitle, template: `%s` }, ...defaultMetadata };
  } catch (error) {
    console.error('Error generating metadata:', error);
    return {
      ...defaultMetadata,
    };
  }
}

export default async function RootLayout({ children }: Readonly<Props>) {
  const PAGE_LABEL = 'IB';
  const cookieStore = await cookies();
  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const userPilotData = await getEmployeeDataforUserPilot(token);
  const flagsFallback = {
    [FT_DYNATRACE_RUM_COOKIE_CONSENT]: false,
    [FT_IB_USER_PILOT]: false,
    [FT_ONE_TRUST_COOKIE_CONSENT]: false,
  };
  const headersList = await headers();
  const currentPath = headersList.get('WB-Url') ?? '';
  const toggles = await getServerUnleashToggles(PAGE_LABEL, flagsFallback, currentPath, {});
  const canonicalLocale = getCanonicalLocale(currentPath);
  const oneTrustConfig = getOneTrustConfigByLocale(canonicalLocale);
  const baseUrl = process?.env?.NEXT_PUBLIC_ASSETS_URL
    ? `{process?.env?.NEXT_PUBLIC_ASSETS_URL}`
    : '';

  const isOneTrustEnabled = isOneTrustCookieConsentActive(
    toggles?.[FT_ONE_TRUST_COOKIE_CONSENT],
    currentPath
  );

  return (
    <html>
      <body>
        <FontWrapper baseUrl={baseUrl} />
        {!!token && toggles?.[FT_IB_USER_PILOT] && <UserPilot userPilotData={userPilotData} />}
        {isOneTrustEnabled && (
          <>
            <Script
              src={process.env.NEXT_PUBLIC_ONE_TRUST_SCRIPT_URL}
              data-document-language="true"
              data-language={oneTrustConfig.dataLanguage}
              data-domain-script={oneTrustConfig.domainScript}
              strategy="beforeInteractive"
              data-testid="one-trust-cookie-consent"
            />
            {/* OneTrust calls window.OptanonWrapper directly; it must exist even though we sync via OneTrustGroupsUpdated */}
            <Script id="optanon-wrapper" strategy="beforeInteractive">
              {`function OptanonWrapper() {}`}
            </Script>
            <DynatraceConsentSync
              isDynatraceRumCookieConsentEnabled={
                toggles?.[FT_DYNATRACE_RUM_COOKIE_CONSENT] ?? false
              }
            />
          </>
        )}
        {children}
      </body>
    </html>
  );
}
