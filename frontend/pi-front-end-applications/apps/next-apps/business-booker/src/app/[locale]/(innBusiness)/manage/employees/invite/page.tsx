import { PathParams } from '@whitbread-eos/api';
import {
  getCountryLanguageByLocale,
  getTranslations,
  TranslationProvider,
  getDetailsFromToken,
  getPathForLocale,
  getCompanyDetails,
  ID_TOKEN_COOKIE,
} from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

import { Analytics } from '~components/innBusiness/ManageEmployeesAnalytics';

import { InviteEmployees as InviteEmployeesClient } from '../components/InviteEmployees';

type Props = {
  params?: Promise<PathParams>;
};

const LOG_PAGE_NAME = 'manage-employees-invite';

export default async function InviteEmployees({ params }: Props) {
  const resolvedParams = await params;
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const { companyId, employeeId, isTravelManager, isBusinessPayManager } =
    getDetailsFromToken(token);
  if (!isTravelManager && !isBusinessPayManager) {
    redirect(getPathForLocale(resolvedParams?.locale, 'homepage'));
  }

  const logContext = {
    pageName: LOG_PAGE_NAME,
    userId: employeeId,
    companyId: companyId,
  };

  const { language } = getCountryLanguageByLocale(resolvedParams?.locale);
  const [{ translations }, companyDetails] = await Promise.all([
    getTranslations(language, ['users', 'icons']),
    getCompanyDetails(companyId, token, false, logContext),
  ]);
  const icons = translations?.['icons'] ?? {};
  const paymentCards = companyDetails?.requestedCompany?.paymentDetails?.paymentCards;

  return (
    <TranslationProvider value={translations}>
      <InviteEmployeesClient
        icons={icons}
        paymentCards={paymentCards}
        companyId={companyId}
        language={language}
        isBusinessPayManager={isBusinessPayManager}
      />
      <Analytics pageName={PAGE_NAME} />
    </TranslationProvider>
  );
}

const PAGE_NAME = 'Manage Employees: Add An Employee';
