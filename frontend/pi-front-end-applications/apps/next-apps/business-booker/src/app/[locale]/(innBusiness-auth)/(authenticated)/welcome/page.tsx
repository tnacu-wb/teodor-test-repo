import { FT_IB_REDIRECT_AFTER_LOGIN, LOCALES, PathParams, SearchParams } from '@whitbread-eos/api';
import { Wizard, WizardHeader } from '@whitbread-eos/layout';
import {
  getCountryLanguageByLocale,
  getTranslations,
  TranslationProvider,
  formatIBAssetsUrl,
  getServerUnleashToggles,
  ID_TOKEN_COOKIE,
} from '@whitbread-eos/utils/server';
import { cookies, headers } from 'next/headers';

import getPageUrlBeforeRedirectToLogin from '~utils/getPageUrlBeforeRedirectToLogin';

import Analytics from './components/Analytics/analytics';
import Confirmation from './components/Confirmation/confirmation';
import { ConfirmationStep } from './components/types';

type Props = {
  params?: Promise<PathParams>;
  searchParams?: Promise<SearchParams>;
};

const PAGE_LABEL = 'Auth';
const PAGE_NAME = 'Confirmed Account';

export default async function AuthConfirmation({ params }: Props) {
  const resolvedParams = await params;
  const headerList = await headers();
  const locale = (resolvedParams?.locale?.toLowerCase() as LOCALES) ?? '';
  const { language } = getCountryLanguageByLocale(locale);
  const flagsFallback = {
    [FT_IB_REDIRECT_AFTER_LOGIN]: false,
  };
  const currentPath = headerList.get('WB-Url') ?? '';
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';

  const [toggles, { t, translations }] = await Promise.all([
    getServerUnleashToggles(PAGE_LABEL, flagsFallback, currentPath, {}),
    getTranslations(language, ['common', 'auth', 'icons']),
  ]);

  const isRedirectAfterLoginEnabled = toggles?.[FT_IB_REDIRECT_AFTER_LOGIN] ?? false;
  if (!token) {
    getPageUrlBeforeRedirectToLogin(currentPath, locale, isRedirectAfterLoginEnabled);
  }

  const icons = translations?.['icons'] || {};

  return (
    <TranslationProvider value={translations}>
      <Wizard
        icons={icons}
        header={<WizardHeader logoUrl={formatIBAssetsUrl(t('common.content.header.image'))} />}
        initialState={{}}
        initialStepId={ConfirmationStep.CONFIRMATION}
        steps={[
          {
            id: ConfirmationStep.CONFIRMATION,
            component: <Confirmation locale={locale} token={token} />,
          },
        ]}
      />
      <Analytics pageName={PAGE_NAME} />
    </TranslationProvider>
  );
}
