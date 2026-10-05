import { PathParams } from '@whitbread-eos/api';
import {
  getCountryLanguageByLocale,
  getTranslations,
  TranslationProvider,
  ID_TOKEN_COOKIE,
  getCompanyDetails,
  getPathForLocale,
  parseRegistrationQuestions,
  getDetailsFromToken,
} from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

import { AddEditEmployee } from '../components/AddEditEmployee';

type Props = {
  params?: Promise<PathParams>;
  searchParams?: Promise<{
    backUrl?: string;
  }>;
};

const LOG_PAGE_NAME = 'manage-employee-add';

export default async function AddEmployee({ params, searchParams }: Props) {
  const resolvedParams = await params;
  const resolvedSearchParams = await searchParams;
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const { companyId, employeeId, isTravelManager, isBusinessPayManager } =
    getDetailsFromToken(token);

  const { backUrl = undefined } = resolvedSearchParams ?? {};

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
    getTranslations(language, ['users', 'profile', 'icons']),
    getCompanyDetails(companyId, token, false, logContext),
  ]);

  const icons = translations?.['icons'] ?? {};
  const companyAddress = companyDetails?.requestedCompany?.companyDetails?.companyAddress;
  const paymentCards = companyDetails?.requestedCompany?.paymentDetails?.paymentCards;
  const companyType = companyDetails?.requestedCompany?.companyType;

  const registrationQuestions = parseRegistrationQuestions(
    companyDetails?.requestedCompany?.companyManagementDetails
  );

  return (
    <TranslationProvider value={translations}>
      <AddEditEmployee
        icons={icons}
        language={language}
        companyAddress={companyAddress}
        paymentCards={paymentCards}
        companyId={companyId}
        registrationQuestions={registrationQuestions}
        token={token}
        backUrl={backUrl}
        companyType={companyType}
      />
    </TranslationProvider>
  );
}
