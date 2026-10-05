import { PathParams } from '@whitbread-eos/api';
import {
  getCountryLanguageByLocale,
  getTranslations,
  TranslationProvider,
  getRegistrationQuestionsWithAnswers,
  ID_TOKEN_COOKIE,
  getPathForLocale,
  getEmployeeDetails,
  getCompanyDetails,
  getDetailsFromToken,
} from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

import { AddEditEmployee } from '../components/AddEditEmployee';

type Props = {
  params?: Promise<{ id: string } & PathParams>;
};

const LOG_PAGE_NAME = 'manage-employee-edit';

export default async function EditEmployee({ params }: Props) {
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
  const [{ translations }, registrationQuestions, companyDetails, employeeDetails] =
    await Promise.all([
      getTranslations(language, ['users', 'profile', 'icons']),
      getRegistrationQuestionsWithAnswers(resolvedParams?.id ?? ''),
      getCompanyDetails(companyId, token, false, logContext),
      getEmployeeDetails(companyId, resolvedParams?.id ?? '', token, logContext),
    ]);
  const icons = translations?.['icons'] ?? {};

  const paymentCards = companyDetails?.requestedCompany?.paymentDetails?.paymentCards;
  const mainEmployee = companyDetails?.requestedCompany?.companyDetails?.mainEmployee;
  const companyType = companyDetails?.requestedCompany?.companyType;

  const {
    employeeStatus,
    title,
    firstName,
    lastName,
    emailAddress,
    phoneNumber,
    mobileNumber,
    address,
    centralCardId,
    id,
    accessLevel,
  } = employeeDetails;

  const isMainContact = mainEmployee?.id === id;

  return (
    <TranslationProvider value={translations}>
      <AddEditEmployee
        registrationQuestions={registrationQuestions}
        id={resolvedParams?.id}
        icons={icons}
        language={language}
        paymentCards={paymentCards}
        employeeStatus={employeeStatus}
        userDetails={{
          title,
          firstName,
          lastName,
          emailAddress,
          phoneNumber,
          mobileNumber,
          address,
          centralCardId,
          employeeId: id,
          accessLevel,
        }}
        isMainContact={isMainContact}
        token={token}
        companyId={companyId}
        companyType={companyType}
      />
    </TranslationProvider>
  );
}
