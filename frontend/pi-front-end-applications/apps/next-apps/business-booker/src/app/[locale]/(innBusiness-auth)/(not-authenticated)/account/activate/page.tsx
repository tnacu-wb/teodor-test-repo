import { LOCALES, PathParams, SearchParams } from '@whitbread-eos/api';
import { Wizard, WizardHeader } from '@whitbread-eos/layout';
import {
  getCountryLanguageByLocale,
  getTranslations,
  getInnBusinessHeaderLabels,
  ID_TOKEN_COOKIE,
  getPathForLocale,
  getIBActivationDetails,
  TranslationProvider,
  formatIBAssetsUrl,
  getCompanyRegistrationQuestionsAndAnswers,
} from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

import { EmployeeActivation } from './components/EmployeeActivation/employee-activation';

type Props = {
  params?: Promise<PathParams>;
  searchParams?: Promise<SearchParams>;
};

export enum EmployeeActivationSteps {
  EMPLOYEE_ACTIVATION = 'EMPLOYEE_ACTIVATION',
}

export type EmployeeActivationState = {
  title: string;
  firstName: string;
  lastName: string;
  emailAddress: string;
  phoneNumber: string;
  mobileNumber: string;
  companyId: string;
  companyName: string;
  employeeId: string | string[];
  address: {
    postCode: string;
    country: string;
    addressLine5: string;
    addressLine4: string;
    addressLine3: string;
    addressLine2: string;
    addressLine1: string;
  };
  activationKey: string | string[];
  accessLevel: string;
};

const LOG_PAGE_NAME = 'account-activate' as const;

export default async function EmployeeActivationPage({ params, searchParams }: Props) {
  const resolvedParams = await params;
  const resolvedSearchParams = await searchParams;
  const baseDataTestId = 'EmployeeActivationPage';
  const locale = (resolvedParams?.locale?.toLowerCase() as LOCALES) ?? LOCALES.EN;
  const { language, country } = getCountryLanguageByLocale(locale);
  const key = resolvedSearchParams?.key ?? '';
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? null;
  const logContext = { pageName: LOG_PAGE_NAME };

  if (!key || token) {
    redirect(getPathForLocale(locale, 'homepage'));
  }

  const [{ translations }, headerLabels, activationDetails] = await Promise.all([
    getTranslations(language, ['auth', 'users', 'spending', 'profile', 'icons']),
    getInnBusinessHeaderLabels(language),
    getIBActivationDetails(key, logContext),
  ]);

  const icons = translations?.['icons'] || {};

  if (!activationDetails) {
    redirect(getPathForLocale(locale, 'access-restricted'));
  }

  const companyRegistrationQuestions = await getCompanyRegistrationQuestionsAndAnswers(
    activationDetails?.companyId ?? '',
    {
      'employee-id': activationDetails?.id ?? '',
    },
    logContext
  );

  const secureUrl = process?.env?.NEXT_PUBLIC_SECURE2_URL ?? '';

  return (
    <TranslationProvider value={translations}>
      <Wizard<EmployeeActivationState>
        icons={icons}
        header={
          <WizardHeader
            logoUrl={formatIBAssetsUrl(headerLabels?.content.header.image)}
            logoRedirectUrl="account/login"
          />
        }
        initialState={{
          title: activationDetails?.title ?? '',
          firstName: activationDetails?.firstName ?? '',
          lastName: activationDetails?.lastName ?? '',
          emailAddress: activationDetails?.emailAddress ?? '',
          phoneNumber: activationDetails?.phoneNumber ?? '',
          mobileNumber: activationDetails?.mobileNumber ?? '',
          companyId: activationDetails?.companyId ?? '',
          companyName: activationDetails?.companyName ?? '',
          employeeId: activationDetails?.id,
          address: activationDetails?.address,
          activationKey: key,
          accessLevel: activationDetails?.accessLevel,
        }}
        initialStepId={EmployeeActivationSteps.EMPLOYEE_ACTIVATION}
        steps={[
          {
            id: EmployeeActivationSteps.EMPLOYEE_ACTIVATION,
            component: (
              <EmployeeActivation
                baseDataTestId={baseDataTestId}
                language={language}
                country={country}
                companyRegistrationQuestions={companyRegistrationQuestions}
                secureUrl={secureUrl}
                locale={locale}
              />
            ),
          },
        ]}
      />
    </TranslationProvider>
  );
}
