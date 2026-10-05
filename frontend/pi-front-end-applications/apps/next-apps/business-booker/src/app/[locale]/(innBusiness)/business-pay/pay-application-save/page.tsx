import {
  PathParams,
  LOCALES,
  Scheme,
  SearchParams,
  ApplicationParticipant,
} from '@whitbread-eos/api';
import {
  getCountryLanguageByLocale,
  TranslationProvider,
  getTranslations,
  ID_TOKEN_COOKIE,
  getPayApplicationDetails,
  getDetailsFromToken,
} from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

import { SavedApplication } from '../components';

const LOG_PAGE_NAME = 'pay-application-save';

type Props = {
  params?: Promise<PathParams>;
  searchParams?: Promise<SearchParams>;
};

export default async function PayApplicationSavePage({ params, searchParams }: Props) {
  const resolvedParams = await params;
  const resolvedSearchParams = await searchParams;
  const baseDataTestId = 'PayApplicationSavePage';
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';

  if (!token) {
    redirect(`/${resolvedParams?.locale ?? LOCALES.EN}/login`);
  }
  const { companyId, employeeId, email, isTravelManager } = getDetailsFromToken(token);
  const logContext = { pageName: LOG_PAGE_NAME, userId: employeeId, companyId };
  const { language } = getCountryLanguageByLocale(resolvedParams?.locale);
  const scheme = (resolvedParams?.locale === LOCALES.EN ? 'GB' : 'DE') as Scheme;
  const applicationGUID = Array.isArray(resolvedSearchParams?.applicationGuid)
    ? resolvedSearchParams.applicationGuid[0]
    : (resolvedSearchParams?.applicationGuid ?? '');
  const applicationId = Array.isArray(resolvedSearchParams?.applicationId)
    ? resolvedSearchParams.applicationId[0]
    : (resolvedSearchParams?.applicationId ?? '');

  const [{ translations }, payApplicationDetails] = await Promise.all([
    getTranslations(language, ['common', 'payApplication', 'company', 'users', 'profile', 'icons']),
    getPayApplicationDetails(token, applicationGUID, applicationId, scheme, logContext),
  ]);
  const icons = translations?.['icons'] ?? {};

  const {
    accountName: companyName,
    applicationNumber,
    contactDetails,
    participants,
  } = payApplicationDetails;

  const userHasAccess = participants.some(
    (participant: ApplicationParticipant) =>
      participant.email?.toLowerCase() === email?.toLowerCase()
  );

  if (!userHasAccess) {
    redirect(`/${resolvedParams?.locale ?? LOCALES.EN}/pay-application-access-restricted`);
  }

  return (
    <TranslationProvider value={translations}>
      <div data-testid={`${baseDataTestId}-container`} className={pageStyle}>
        <SavedApplication
          companyName={companyName}
          applicationReference={applicationNumber}
          startedBy={contactDetails?.email}
          companyId={companyId}
          icons={icons}
          locale={resolvedParams?.locale}
          participants={participants}
          applicationId={applicationId}
          applicationGuid={applicationGUID}
          token={token}
          showShareAppWithColleague={isTravelManager}
        />
      </div>
    </TranslationProvider>
  );
}

const pageStyle = 'p-12 min-w-[700px] mobile:min-w-full mobile:px-4 mobile:py-6';
