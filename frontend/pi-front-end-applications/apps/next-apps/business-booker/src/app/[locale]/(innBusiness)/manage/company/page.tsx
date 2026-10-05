import { LOCALES, PathParams } from '@whitbread-eos/api';
import {
  getCountryLanguageByLocale,
  getTranslations,
  TranslationProvider,
  ID_TOKEN_COOKIE,
  getPathForLocale,
  getCompanyDetails,
  getDetailsFromToken,
} from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

import { Analytics } from './components/Analytics/Analytics';
import { CompanyInformation } from './components/CompanyInformation';

type Props = {
  params?: Promise<PathParams>;
};

const LOG_PAGE_NAME = 'manage-company';

export default async function CompanyDetails({ params }: Props) {
  const resolvedParams = await params;
  const cookieStore = await cookies();
  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const { companyId, employeeId, isTravelManager, isBusinessPayManager } =
    getDetailsFromToken(token);
  const hasCompanyDetailsAccess = isTravelManager || isBusinessPayManager;

  if (!hasCompanyDetailsAccess) {
    redirect(getPathForLocale(resolvedParams?.locale, 'homepage'));
  }

  const logContext = {
    pageName: 'manage-company',
    userId: employeeId,
    companyId: companyId,
  };

  const baseDataTestId = 'CompanyDetailsPage';

  const { language } = getCountryLanguageByLocale(resolvedParams?.locale);

  const [{ t, translations }, companyDetails] = await Promise.all([
    getTranslations(language, ['company', 'users', 'profile', 'icons']),
    getCompanyDetails(companyId, token, true, {
      ...logContext,
      pageName: LOG_PAGE_NAME,
    }),
  ]);

  const icons = translations?.['icons'] ?? {};

  const companyAddress = companyDetails?.requestedCompany?.companyDetails?.companyAddress;
  const {
    companyName,
    alternateCompanyName,
    companySector,
    averageMonthlyBooking,
    numberOfEmployee,
  } = companyDetails?.requestedCompany?.companyDetails ?? {};
  const mainEmployee = companyDetails?.requestedCompany?.companyDetails?.mainEmployee;
  const {
    title,
    firstName,
    lastName,
    emailAddress,
    phoneNumber,
    mobileNumber,
    position,
    id,
    address,
  } = mainEmployee || {};

  return (
    <TranslationProvider value={translations}>
      <div data-testid={`${baseDataTestId}-container`} className={pageStyle}>
        <h1 className={h1Style}>{t('company.coMngt.title')}</h1>
        <div
          data-testid={`${baseDataTestId}-display-company-info-container`}
          className={containerStyle}
        >
          <CompanyInformation
            locale={resolvedParams?.locale as LOCALES}
            companyAddress={companyAddress}
            companyId={companyId}
            icons={icons}
            mainContact={{
              title,
              firstName,
              lastName,
              emailAddress,
              phoneNumber,
              mobileNumber,
              position,
              id,
              address,
            }}
            additionalDetails={{
              companySector,
              averageMonthlyBooking,
              numberOfEmployee,
            }}
            companyName={companyName}
            alternateCompanyName={alternateCompanyName}
          />
        </div>

        <Analytics pageName="Company Management: Company Details" companyDetails={companyDetails} />
      </div>
    </TranslationProvider>
  );
}

const pageStyle =
  'px-12 pt-12 min-w-[700px] mobile:min-w-full mobile:px-4 w-full [@media(min-width:120rem)]:max-w-[81.75rem] [@media(min-width:120rem)]:mx-auto';
const h1Style = 'text-[2.5rem] font-black leading-[2.75rem] text-secondaryColor mobile:text-4xl';

const containerStyle = 'pt-12 mobile:pb-4';
