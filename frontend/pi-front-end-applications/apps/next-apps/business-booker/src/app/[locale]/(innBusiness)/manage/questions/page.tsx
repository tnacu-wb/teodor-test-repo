import { PathParams } from '@whitbread-eos/api';
import {
  getPathForLocale,
  getCountryLanguageByLocale,
  getTranslations,
  TranslationProvider,
  ID_TOKEN_COOKIE,
  getCompanyDetails,
  getDetailsFromToken,
} from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

import { EmployeeQuestionsContent } from './components/employee-questions-content';

type Props = {
  params?: Promise<PathParams>;
};

const LOG_PAGE_NAME = 'manage-questions';

export default async function EmployeeQuestions({ params }: Props) {
  const resolvedParams = await params;
  const cookieStore = await cookies();
  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const { companyId, employeeId, isTravelManager } = getDetailsFromToken(token);

  if (!isTravelManager) {
    redirect(getPathForLocale(resolvedParams?.locale, 'homepage'));
  }

  const logContext = {
    pageName: LOG_PAGE_NAME,
    userId: employeeId,
    companyId: companyId,
  };

  const baseDataTestId = 'EmployeeQuestions';
  const { language } = getCountryLanguageByLocale(resolvedParams?.locale);

  const [{ t, translations }, companyDetails] = await Promise.all([
    getTranslations(language, ['company', 'users', 'profile', 'icons']),
    getCompanyDetails(companyId, token, true, logContext),
  ]);
  const icons = translations?.['icons'] ?? {};

  const { purchaseOrderManagement, customerReferenceManagement, userDefinedManagement } =
    companyDetails?.requestedCompany?.companyManagementDetails ?? {};

  return (
    <TranslationProvider value={translations}>
      <div data-testid={`${baseDataTestId}-container`} className={pageStyle}>
        <h1 className={h1Style}>{t('company.coMngt.questions.title')}</h1>
        <p className={subtitleStyle}>{t('company.coMngt.questions.subtitle')}</p>

        <EmployeeQuestionsContent
          purchaseOrderManagement={purchaseOrderManagement}
          customerReferenceManagement={customerReferenceManagement}
          userDefinedManagement={userDefinedManagement ?? []}
          companyId={companyId}
          icons={icons}
        />
      </div>
    </TranslationProvider>
  );
}

const pageStyle =
  'p-12 min-w-[700px] mobile:min-w-full mobile:px-4 mobile:py-6 w-full [@media(min-width:120rem)]:max-w-[81.75rem] [@media(min-width:120rem)]:mx-auto';
const h1Style = 'text-[2.5rem] font-black leading-[2.75rem] text-secondaryColor mobile:text-4xl';
const subtitleStyle = 'mt-4';
