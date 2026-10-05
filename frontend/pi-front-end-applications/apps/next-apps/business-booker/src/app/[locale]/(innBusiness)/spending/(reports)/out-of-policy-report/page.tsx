import { PathParams, LOCALES } from '@whitbread-eos/api';
import { Skeleton } from '@whitbread-eos/atoms/ui';
import {
  getTranslations,
  getCountryLanguageByLocale,
  ID_TOKEN_COOKIE,
  getInnBusinessHeaderLabels,
  getPathForLocale,
  getDetailsFromToken,
} from '@whitbread-eos/utils/server';
import { cookies, headers } from 'next/headers';
import { redirect } from 'next/navigation';

import { getOutOfPolicyReportFF } from './../../components/Company/components/CardList/card-list';
import { ReportDates, ReportDatesSkeleton } from './components/ReportDates';

type Props = {
  params?: Promise<PathParams>;
};

export default async function OutOfPolicyReport({ params }: Props) {
  const resolvedParams = await params;
  const baseDataTestId = 'OutOfPolicyReportPage';
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const locale = (resolvedParams?.locale?.toLowerCase() as LOCALES) ?? '';
  const { language } = getCountryLanguageByLocale(resolvedParams?.locale);

  const { companyId } = getDetailsFromToken(token);

  const [{ t, translations }, labels, isOutOfPolicyReportActive] = await Promise.all([
    getTranslations(language, ['spending', 'icons']),
    getInnBusinessHeaderLabels(language),
    getOutOfPolicyReportFF(),
  ]);

  const icons = translations?.['icons'] || {};

  const headersList = await headers();
  const currentPath = headersList.get('WB-Url') ?? '';

  if (currentPath.indexOf('spending/out-of-policy-report') > -1 && !isOutOfPolicyReportActive) {
    redirect(getPathForLocale(locale, 'homepage'));
    return null;
  }

  return (
    <div className={containerStyle}>
      <div data-testid={`${baseDataTestId}-title`} className={textStyle}>
        <h1 className={h1Style}>{t('spending.out.of.policy.report.heading')}</h1>
        <p>{t('spending.out.of.policy.report.description')}</p>
      </div>
      <ReportDates
        calendarLabels={labels?.content?.form}
        icons={icons}
        baseDataTestId={baseDataTestId}
        token={token}
        companyId={companyId}
      />
    </div>
  );
}

export function OutOfPolicyReportSkeleton() {
  return (
    <div className={containerStyle}>
      <div className={textStyle}>
        <Skeleton className="h-12 w-1/4 mb-6" />
        <Skeleton className="h-6 w-1/3" />
      </div>
      <ReportDatesSkeleton />
    </div>
  );
}

const containerStyle = 'py-12 px-12 w-full mobile:py-6 mobile:px-4';
const h1Style =
  'text-[2.5rem] font-black leading-[2.75rem] text-secondaryColor mobile:text-4xl pb-4';
const textStyle = 'pb-12';
