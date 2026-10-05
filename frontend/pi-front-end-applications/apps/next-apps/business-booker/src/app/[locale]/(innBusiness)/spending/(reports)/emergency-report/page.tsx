import { PathParams } from '@whitbread-eos/api';
import {
  getTranslations,
  getCountryLanguageByLocale,
  ID_TOKEN_COOKIE,
} from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';

import { EmergencyReport } from './components/EmergencyReport';

type Props = {
  params?: Promise<PathParams>;
};

export default async function EmergencyReportPage({ params }: Props) {
  const resolvedParams = await params;
  const baseDataTestId = 'EmergencyReportPage';
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const { language } = getCountryLanguageByLocale(resolvedParams?.locale);

  const { t, translations } = await getTranslations(language, ['spending', 'icons']);
  const icons = translations?.['icons'] || {};

  return (
    <div className={containerStyle}>
      <div data-testid={`${baseDataTestId}-title`}>
        <h1 className={h1Style}>{t('spending.emergency.report.heading')}</h1>
        <EmergencyReport
          icons={icons}
          baseDataTestId={baseDataTestId}
          language={language}
          token={token}
        />
      </div>
    </div>
  );
}

const containerStyle = 'py-12 px-12 w-full mobile:py-6 mobile:px-4';
const h1Style =
  'text-[2.5rem] font-black leading-[2.75rem] text-secondaryColor mobile:text-4xl pb-4';
