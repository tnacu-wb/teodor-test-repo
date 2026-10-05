import { PathParams } from '@whitbread-eos/api';
import {
  getTranslations,
  getCountryLanguageByLocale,
  getInnBusinessHeaderLabels,
  ID_TOKEN_COOKIE,
} from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';

import { ReportDates } from './components/ReportDates';

type Props = {
  params?: Promise<PathParams>;
};

export default async function ManagementInfoReport({ params }: Props) {
  const resolvedParams = await params;
  const baseDataTestId = 'ManagementInformationReportPage';
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const { language } = getCountryLanguageByLocale(resolvedParams?.locale);

  const [{ t, translations }, labels] = await Promise.all([
    getTranslations(language, ['spending', 'icons']),
    getInnBusinessHeaderLabels(language),
  ]);

  const icons = translations?.['icons'] || {};

  return (
    <div className={containerStyle}>
      <div data-testid={`${baseDataTestId}-title`}>
        <h1 className={h1Style}>{t('spending.management.info.report.heading')}</h1>
      </div>
      <ReportDates
        calendarLabels={labels?.content?.form}
        icons={icons}
        language={language}
        baseDataTestId={baseDataTestId}
        token={token}
      />
    </div>
  );
}

const containerStyle = 'py-12 px-12 w-full mobile:py-6 mobile:px-4';
const h1Style =
  'text-[2.5rem] font-black leading-[2.75rem] text-secondaryColor mobile:text-4xl pb-12';
