import { PathParams } from '@whitbread-eos/api';
import {
  getCountryLanguageByLocale,
  ID_TOKEN_COOKIE,
  getContactPreferences,
  TranslationProvider,
  getTranslations,
  getWorldlineReturnUrl,
  getDetailsFromToken,
} from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';
import { Suspense } from 'react';

import { ContactPreferencesForm } from '../components/ContactPreferences/contact-preferences-page';
import { DataPolicySection } from '../components/ContactPreferences/data-policy-section';
import { InnBusinessPaySection } from '../components/ContactPreferences/inn-business-pay-section';
import Loading from '../loading';

type Props = {
  params?: Promise<PathParams>;
};

const LOG_PAGE_NAME = 'profile-contact-preferences' as const;

export default async function ContactPreferencesPage({ params }: Props) {
  const resolvedParams = await params;
  const { language } = getCountryLanguageByLocale(resolvedParams?.locale);
  const cookieStore = await cookies();
  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const { companyId, employeeId, email } = getDetailsFromToken(token);
  const logContext = { pageName: LOG_PAGE_NAME, userId: employeeId, companyId };
  const [{ translations }, fetchContactPreferences] = await Promise.all([
    getTranslations(language, ['profile', 'users', 'icons', 'layout']),
    getContactPreferences(email, token, logContext),
  ]);
  const icons = translations?.['icons'] ?? {};
  const initialPreferences = fetchContactPreferences?.permissions?.[0];
  const wlReturnUrl = getWorldlineReturnUrl(resolvedParams?.locale, 'profile/contact-preferences');

  return (
    <TranslationProvider value={translations}>
      <Suspense fallback={<Loading />}>
        <div className="px-12 pt-12 min-w-[700px] mobile:min-w-full mobile:px-4 w-full [@media(min-width:120rem)]:max-w-[81.75rem] [@media(min-width:120rem)]:mx-auto">
          <ContactPreferencesForm
            icons={icons}
            email={email}
            initialPreferences={initialPreferences}
            token={token}
          />
          <InnBusinessPaySection
            locale={resolvedParams?.locale}
            companyId={companyId}
            token={token}
            icons={icons}
            worldlinePostUrl={process.env.NEXT_PUBLIC_WORLDLINE_POST_URL}
            worldlineReturnUrl={wlReturnUrl}
          />
          <DataPolicySection icons={icons} />
        </div>
      </Suspense>
    </TranslationProvider>
  );
}
