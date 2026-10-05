import {
  PathParams,
  LOCALES,
  SearchParams,
  CompanyType,
  FT_PIB_BUSINESS_PAY_USER,
} from '@whitbread-eos/api';
import { Wizard, WizardHeader } from '@whitbread-eos/layout';
import {
  getCountryLanguageByLocale,
  TranslationProvider,
  getTranslations,
  formatIBAssetsUrl,
} from '@whitbread-eos/utils/server';
import { Metadata } from 'next';
import { notFound } from 'next/navigation';

import Register from './components/Register/register';
import RegisterValidation from './components/RegisterValidation/register-validation';

const PAGE_LABEL = 'PIB | Register';

type Props = {
  params?: Promise<PathParams>;
  searchParams?: Promise<SearchParams>;
};

export enum RegisterSteps {
  REGISTER = 'REGISTER',
  CONFIRMATION = 'CONFIRMATION',
}

export type RegisterValidationState = {
  existingCompany: boolean | undefined;
  existingEmployee: boolean | undefined;
  email: string | undefined;
  company: string | undefined;
};

export async function generateMetadata({
  searchParams,
}: {
  searchParams: Promise<SearchParams>;
}): Promise<Metadata> {
  const resolvedSearchParams = await searchParams;
  const shouldNotIndex = resolvedSearchParams?.type === CompanyType.BUSINESS_PAY;
  return {
    robots: {
      index: !shouldNotIndex,
      follow: !shouldNotIndex,
    },
  };
}

export default async function RegisterPage({ params, searchParams }: Props) {
  const resolvedParams = await params;
  const resolvedSearchParams = await searchParams;
  const baseDataTestId = 'RegisterPage';
  const locale = (resolvedParams?.locale?.toLowerCase() as LOCALES) ?? LOCALES.EN;
  const { language } = getCountryLanguageByLocale(locale);
  const { t, translations } = await getTranslations(language, [
    'common',
    'icons',
    'users',
    'company',
    'profile',
    'auth',
  ]);
  const icons = translations?.['icons'] ?? {};
  const companyType = resolvedSearchParams?.type === 'BP' ? CompanyType.BUSINESS_PAY : '';

  if (companyType === CompanyType.BUSINESS_PAY) {
    const flagsFallback = {
      [FT_PIB_BUSINESS_PAY_USER]: false,
    };

    const { getServerUnleashToggles } = await import('@whitbread-eos/utils/server');
    const toggles = await getServerUnleashToggles(PAGE_LABEL, flagsFallback);

    if (!toggles?.[FT_PIB_BUSINESS_PAY_USER]) {
      notFound();
    }
  }

  return (
    <TranslationProvider value={translations}>
      <Wizard<RegisterValidationState>
        icons={icons}
        header={
          <WizardHeader
            logoUrl={formatIBAssetsUrl(t('common.content.header.image'))}
            logoRedirectUrl="account/login"
          />
        }
        initialState={{
          existingCompany: undefined,
          existingEmployee: undefined,
          email: undefined,
          company: undefined,
        }}
        initialStepId={RegisterSteps.REGISTER}
        steps={[
          {
            id: RegisterSteps.REGISTER,
            component: (
              <Register
                icons={icons}
                language={language}
                baseDataTestId={baseDataTestId}
                locale={resolvedParams?.locale as LOCALES}
                companyType={companyType}
              />
            ),
          },
          {
            id: RegisterSteps.CONFIRMATION,
            component: <RegisterValidation locale={resolvedParams?.locale as LOCALES} />,
          },
        ]}
      />
    </TranslationProvider>
  );
}
