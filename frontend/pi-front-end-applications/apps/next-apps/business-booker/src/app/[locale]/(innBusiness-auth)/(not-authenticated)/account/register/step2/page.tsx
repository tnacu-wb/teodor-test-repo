import { LOCALES, PathParams, SearchParams, EmployeeStatus } from '@whitbread-eos/api';
import { Wizard, WizardHeader } from '@whitbread-eos/layout';
import {
  getPathForLocale,
  getCountryLanguageByLocale,
  getTranslations,
  TranslationProvider,
  formatIBAssetsUrl,
  ID_TOKEN_COOKIE,
  getActivationDetails,
} from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

import Analytics from '../components/Analytics/analytics';
import { PersonalInfoLanding } from './components/PersonalInfoLanding/personal-info-landing';
import { PersonalInfoPassword } from './components/PersonalInfoPassword/personal-info-password';
import {
  RegisterPersonalInformationStep,
  RegisterPersonalInformationState,
} from './components/types';

type Props = {
  params?: Promise<PathParams>;
  searchParams?: Promise<SearchParams>;
};

const PAGE_NAME = 'Confirmed Account';
const LOG_PAGE_NAME = 'account-register-step2' as const;

export default async function Step2({ params, searchParams }: Props) {
  const resolvedParams = await params;
  const resolvedSearchParams = await searchParams;
  const baseDataTestId = 'RegisterStep2';

  const locale = (resolvedParams?.locale?.toLowerCase() as LOCALES) ?? LOCALES.EN;
  const { language, country } = getCountryLanguageByLocale(locale);
  const key = resolvedSearchParams?.key ?? '';
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? null;
  const logContext = { pageName: LOG_PAGE_NAME };

  if (!key || token) {
    redirect(getPathForLocale(locale, 'homepage'));
  }

  const [{ t, translations }, activationDetails] = await Promise.all([
    getTranslations(language, ['common', 'auth', 'users', 'spending', 'icons']),
    getActivationDetails(key, logContext),
  ]);

  const icons = translations?.['icons'] || {};

  if (!activationDetails || activationDetails?.employeeStatus === EmployeeStatus.Active) {
    redirect(getPathForLocale(locale, 'access-restricted'));
  }

  const secureUrl = process?.env?.NEXT_PUBLIC_SECURE2_URL ?? '';

  return (
    <TranslationProvider value={translations}>
      <Wizard<RegisterPersonalInformationState>
        icons={icons}
        header={<WizardHeader logoUrl={formatIBAssetsUrl(t('common.content.header.image'))} />}
        initialState={{
          title: '',
          firstName: '',
          lastName: '',
          phone: {
            prefix: '',
            phoneNumber: '',
          },
          emailAddress: activationDetails?.emailAddress ?? '',
          activationKey: key ?? '',
          countryCode: activationDetails?.address?.country ?? '',
        }}
        initialStepId={RegisterPersonalInformationStep.PERSONAL_INFO_LANDING}
        steps={[
          {
            id: RegisterPersonalInformationStep.PERSONAL_INFO_LANDING,
            component: (
              <PersonalInfoLanding
                baseDataTestId={baseDataTestId}
                language={language}
                accessLevel={activationDetails?.accessLevel}
              />
            ),
          },
          {
            id: RegisterPersonalInformationStep.PERSONAL_INFO_PASSWORD,
            component: (
              <PersonalInfoPassword
                baseDataTestId={baseDataTestId}
                secureUrl={secureUrl}
                locale={locale}
                language={language}
                country={country}
              />
            ),
          },
        ]}
      />
      <Analytics pageName={PAGE_NAME} />
    </TranslationProvider>
  );
}
