import { PathParams } from '@whitbread-eos/api';
import {
  getAccessLevel,
  getPathForLocale,
  getCountryLanguageByLocale,
  getTranslations,
  TranslationProvider,
} from '@whitbread-eos/utils/server';
import { redirect } from 'next/navigation';

type Props = {
  children: React.ReactNode;
  params?: Promise<PathParams>;
};

export default async function ReportLayout({ children, params }: Readonly<Props>) {
  const resolvedParams = await params;
  const { isTravelManager } = await getAccessLevel();
  if (!isTravelManager) {
    redirect(getPathForLocale(resolvedParams?.locale, 'homepage'));
  }

  const { language } = getCountryLanguageByLocale(resolvedParams?.locale);
  const { translations } = await getTranslations(language, ['spending', 'common']);

  return <TranslationProvider value={translations}>{children}</TranslationProvider>;
}
