import { PathParams } from '@whitbread-eos/api';
import {
  getCountryLanguageByLocale,
  getTranslations,
  TranslationProvider,
} from '@whitbread-eos/utils/server';

import AccountDetails from './components/AccountDetails/account-details';

type Props = {
  params?: Promise<PathParams>;
};

export default async function LinkInnbusinessAccount({ params }: Props) {
  const resolvedParams = await params;
  const baseDataTestId = 'LinkInnbusinessAccount';
  const { language } = getCountryLanguageByLocale(resolvedParams?.locale);
  const { translations } = await getTranslations(language, ['spending', 'auth', 'icons']);
  const icons = translations?.['icons'] || {};

  return (
    <TranslationProvider value={translations}>
      <div data-testid={`${baseDataTestId}-container`} className={pageStyle}>
        <AccountDetails
          baseDataTestId={baseDataTestId}
          icons={icons}
          locale={resolvedParams?.locale || ''}
        />
      </div>
    </TranslationProvider>
  );
}

const pageStyle = 'bg-lightGrey5 min-w-[700px] mobile:min-w-full';
