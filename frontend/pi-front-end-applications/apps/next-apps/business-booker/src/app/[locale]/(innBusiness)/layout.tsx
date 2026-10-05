import {
  FT_DYNATRACE_RUM_COOKIE_CONSENT,
  PathParams,
  LOCALES,
  FT_IB_PAY_PIBA_EURO,
  FT_IB_OUT_OF_POLICY_REPORT,
  FT_IB_BOOKING_ALERTS,
  FT_IB_COST_CENTRE_MANAGEMENT,
  FT_IB_REDIRECT_AFTER_LOGIN,
  FT_IB_PIBA_MEMORABLE_WORD_IFRAME,
  FT_IB_YOUR_SPENDING,
  FT_ONE_TRUST_COOKIE_CONSENT,
} from '@whitbread-eos/api';
import {
  getServerUnleashToggles,
  getCountryLanguageByLocale,
  getInnBusinessServerSideProps,
  ID_TOKEN_COOKIE,
  getPathForLocale,
  getAccessLevel,
} from '@whitbread-eos/utils/server';
import { cookies, headers } from 'next/headers';
import { redirect } from 'next/navigation';

import { AuthGuard } from '~components/innBusiness/AuthGuard/auth-guard';
import { ImagePreloader } from '~components/innBusiness/ImagePreloader';
import { default as Layout } from '~components/innBusiness/InnBusinessLayout';
import { NotificationBanner } from '~components/innBusiness/NotificationBanner';
import getPageUrlBeforeRedirectToLogin from '~utils/getPageUrlBeforeRedirectToLogin';

const PAGE_LABEL = 'IB | HMP | Homepage';

type Props = {
  children: React.ReactNode;
  params?: Promise<PathParams>;
};

export default async function InnBusinessLayout({ children, params }: Props) {
  const headersList = await headers();
  const resolvedParams = await params;
  const locale = (resolvedParams?.locale?.toLowerCase() as LOCALES) ?? '';
  if (!locale || !Object.values(LOCALES).includes(locale)) {
    redirect(`/${LOCALES.EN}/homepage`);
  }

  const currentPath = headersList.get('WB-Url') ?? '';
  const flagsFallback = {
    [FT_DYNATRACE_RUM_COOKIE_CONSENT]: false,
    [FT_IB_PAY_PIBA_EURO]: false,
    [FT_IB_OUT_OF_POLICY_REPORT]: false,
    [FT_IB_BOOKING_ALERTS]: false,
    [FT_IB_COST_CENTRE_MANAGEMENT]: false,
    [FT_IB_REDIRECT_AFTER_LOGIN]: false,
    [FT_IB_PIBA_MEMORABLE_WORD_IFRAME]: false,
    [FT_IB_YOUR_SPENDING]: false,
    [FT_ONE_TRUST_COOKIE_CONSENT]: false,
  };
  const toggles = await getServerUnleashToggles(PAGE_LABEL, flagsFallback, currentPath, {});
  const { language } = getCountryLanguageByLocale(locale);
  const cookieStore = await cookies();
  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const isRedirectAfterLoginEnabled = toggles?.[FT_IB_REDIRECT_AFTER_LOGIN] ?? false;

  if (!token) {
    getPageUrlBeforeRedirectToLogin(currentPath, locale, isRedirectAfterLoginEnabled);
  }

  const [serverSideProps, { isTethered }] = await Promise.all([
    getInnBusinessServerSideProps(token, language, true),
    getAccessLevel(),
  ]);

  if (!serverSideProps) {
    redirect(getPathForLocale(locale, 'error'));
  }

  const { labels, icons } = serverSideProps;
  const secureUrl = process?.env?.NEXT_PUBLIC_SECURE2_URL ?? '';
  const consentCookie = cookieStore.get('consent_cookie')?.value ?? false;

  return (
    <>
      <ImagePreloader
        menuLabels={labels.layout.menu}
        collapseIcons={labels.layout.sidebar}
        languages={labels.content.countries}
      />

      <Layout
        showFooter={true}
        serverSideProps={{ ...serverSideProps, isTethered }}
        featureToggle={toggles}
        secureUrl={secureUrl}
        token={token}
        serverConsentCookie={!!consentCookie}
      >
        <AuthGuard secureUrl={secureUrl} locale={locale} />
        <NotificationBanner contactBanner={labels.content?.contactBanner} icons={icons} />
        {children}
      </Layout>
    </>
  );
}
