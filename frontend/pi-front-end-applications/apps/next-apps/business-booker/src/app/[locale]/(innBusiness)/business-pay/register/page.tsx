import { LOCALES, PathParams } from '@whitbread-eos/api';
import {
  getCountryLanguageByLocale,
  TranslationProvider,
  getTranslations,
  ID_TOKEN_COOKIE,
} from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';

import { RegistrationCodeForm } from './components/RegistrationCodeForm';

type Props = {
  params?: Promise<PathParams>;
};

export default async function RegisterIbPage({ params }: Props) {
  const resolvedParams = await params;
  const baseDataTestId = 'RegisterIbPage';
  const locale = (resolvedParams?.locale as LOCALES) ?? LOCALES.EN;
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const { language } = getCountryLanguageByLocale(locale);

  const { translations } = await getTranslations(language, ['auth', 'icons']);

  return (
    <TranslationProvider value={translations}>
      <div data-testid={`${baseDataTestId}-container`} className={pageStyle}>
        <RegistrationCodeForm
          locale={locale}
          baseDataTestId={baseDataTestId}
          icons={translations?.['icons'] || {}}
          token={token}
        />
      </div>
    </TranslationProvider>
  );
}

const pageStyle = 'bg-lightGrey5';
