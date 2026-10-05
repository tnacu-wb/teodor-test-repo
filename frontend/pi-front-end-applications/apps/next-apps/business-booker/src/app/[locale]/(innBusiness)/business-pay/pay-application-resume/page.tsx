import {
  PathParams,
  LOCALES,
  Scheme,
  SearchParams,
  ApplicationParticipant,
  PayApplicationStatus,
  requestErrors,
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

import { ResumeApplication } from '../components';

const LOG_PAGE_NAME = 'pay-application-resume';

type Props = {
  params?: Promise<PathParams>;
  searchParams?: Promise<SearchParams>;
};

export default async function PayApplicationResumePage(props: Props) {
  const baseDataTestId = 'PayApplicationResumePage';
  const [params, searchParams, cookiesData] = await Promise.all([
    props.params,
    props.searchParams,
    cookies(),
  ]);
  const token = cookiesData.get(ID_TOKEN_COOKIE)?.value ?? '';
  const { companyId, employeeId, email, isTravelManager, isBusinessPayManager } =
    getDetailsFromToken(token);
  const logContext = { pageName: LOG_PAGE_NAME, userId: employeeId, companyId };
  const { language } = getCountryLanguageByLocale(params?.locale);
  const scheme = (params?.locale === LOCALES.EN ? 'GB' : 'DE') as Scheme;
  const applicationGUID = Array.isArray(searchParams?.applicationGuid)
    ? searchParams.applicationGuid[0]
    : (searchParams?.applicationGuid ?? '');
  const applicationId = Array.isArray(searchParams?.applicationId)
    ? searchParams.applicationId[0]
    : (searchParams?.applicationId ?? '');

  if (!applicationGUID || !applicationId) {
    redirect(`/${params?.locale ?? LOCALES.EN}/homepage`);
  }

  const [
    { translations },
    { accountName: companyName, applicationNumber, contactDetails, participants, status, errCode },
  ] = await Promise.all([
    getTranslations(language, ['common', 'payApplication', 'company', 'users', 'profile', 'icons']),
    getPayApplicationDetails(token, applicationGUID, applicationId, scheme, logContext),
  ]);

  if (
    status === PayApplicationStatus.Cancelled ||
    (errCode && errCode !== requestErrors.payAppAccessRestricted)
  ) {
    redirect(`/${params?.locale ?? LOCALES.EN}/error`);
  }

  const icons = translations?.['icons'] ?? {};

  const userHasAccess = participants?.some(
    (participant: ApplicationParticipant) =>
      participant.email?.toLowerCase() === email?.toLowerCase()
  );

  if (!userHasAccess || errCode === requestErrors.payAppAccessRestricted) {
    redirect(`/${params?.locale ?? LOCALES.EN}/pay-application-access-restricted`);
  }

  return (
    <TranslationProvider value={translations}>
      <div data-testid={`${baseDataTestId}-container`} className={pageStyle}>
        <ResumeApplication
          companyName={companyName}
          applicationReference={applicationNumber}
          startedBy={contactDetails?.email}
          companyId={companyId}
          icons={icons}
          locale={params?.locale}
          participants={participants}
          applicationId={applicationId}
          applicationGuid={applicationGUID}
          token={token}
          showShareAppWithColleague={isTravelManager || isBusinessPayManager}
        />
      </div>
    </TranslationProvider>
  );
}

const pageStyle = 'p-12 min-w-[700px] mobile:min-w-full mobile:px-4 mobile:py-6';
