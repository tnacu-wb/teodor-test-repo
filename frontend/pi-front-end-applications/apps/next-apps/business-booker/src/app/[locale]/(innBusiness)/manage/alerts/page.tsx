import { PathParams, LOCALES, FT_IB_BOOKING_ALERTS } from '@whitbread-eos/api';
import {
  getCountryLanguageByLocale,
  getTranslations,
  TranslationProvider,
  getCompanyDetails,
  getInnBusinessHeaderLabels,
  ID_TOKEN_COOKIE,
  getServerUnleashToggles,
  getPathForLocale,
  getDetailsFromToken,
} from '@whitbread-eos/utils/server';
import { cookies, headers } from 'next/headers';
import { redirect } from 'next/navigation';

import { AlertsContent } from './components/AlertsContent';

const PAGE_LABEL = 'IB | PAYAPP | Pay Application Apply';

const flagsFallback = {
  [FT_IB_BOOKING_ALERTS]: false,
};

export const getBookingAlertsFF = async () => {
  const headersList = await headers();
  const currentPath = headersList.get('WB-Url') ?? '';
  const toggles = await getServerUnleashToggles(PAGE_LABEL, flagsFallback, currentPath, {});
  return toggles?.[FT_IB_BOOKING_ALERTS];
};

type Props = {
  params?: Promise<PathParams>;
};

export default async function AlertsPage({ params }: Props) {
  const resolvedParams = await params;
  const locale = (resolvedParams?.locale?.toLowerCase() as LOCALES) ?? '';
  const { language } = getCountryLanguageByLocale(resolvedParams?.locale);
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const { companyId, employeeId, isTravelManager } = getDetailsFromToken(token);

  if (!isTravelManager) {
    redirect(getPathForLocale(resolvedParams?.locale, 'homepage'));
  }

  const logContext = {
    pageName: 'manage-alerts',
    userId: employeeId,
    companyId: companyId,
  };

  const [{ translations }, labels, companyDetails, isBookingAlertsActive] = await Promise.all([
    getTranslations(language, ['users', 'company', 'global', 'icons']),
    getInnBusinessHeaderLabels(language),
    getCompanyDetails(companyId, token, true, logContext),
    getBookingAlertsFF(),
  ]);
  const icons = translations?.['icons'] ?? {};

  const globalLabels = labels?.content?.global ?? {};
  const locationIcon = labels?.content?.form?.whereIcon ?? '';
  const bookingAlerts = companyDetails?.requestedCompany?.bookingAlerts || {};

  const headersList = await headers();
  const currentPath = headersList.get('WB-Url') ?? '';
  if (currentPath.indexOf('manage/alerts') > -1 && !isBookingAlertsActive) {
    redirect(getPathForLocale(locale, 'homepage'));
    return null;
  }

  return (
    <TranslationProvider value={translations}>
      <AlertsContent
        icons={icons}
        globalLabels={globalLabels}
        locationIcon={locationIcon}
        companyId={companyId}
        bookingAlerts={bookingAlerts}
        language={language}
      />
    </TranslationProvider>
  );
}
