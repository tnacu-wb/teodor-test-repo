import { PathParams } from '@whitbread-eos/api';
import {
  getCountryLanguageByLocale,
  getTranslations,
  TranslationProvider,
  ID_TOKEN_COOKIE,
  getPathForLocale,
  getCompanyDetails,
  getDetailsFromToken,
} from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

import { BookingAllowances } from './components/BookingAllowances';

type Props = {
  params?: Promise<PathParams>;
};

const LOG_PAGE_NAME = 'manage-allowances';

export default async function BookingAllowancesPage({ params }: Props) {
  const resolvedParams = await params;
  const cookieStore = await cookies();
  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const { companyId, employeeId, isTravelManager } = getDetailsFromToken(token);

  if (!isTravelManager) {
    redirect(getPathForLocale(resolvedParams?.locale, 'homepage'));
  }

  const logContext = {
    pageName: LOG_PAGE_NAME,
    userId: employeeId,
    companyId: companyId,
  };

  const baseDataTestId = 'BookingAllowancesPage';

  const { language } = getCountryLanguageByLocale(resolvedParams?.locale);

  const [{ t, translations }, companyDetails] = await Promise.all([
    getTranslations(language, ['company', 'users', 'profile', 'icons']),
    getCompanyDetails(companyId, token, true, logContext),
  ]);
  const icons = translations?.['icons'] ?? {};

  return (
    <TranslationProvider value={translations}>
      <div data-testid={`${baseDataTestId}-container`} className={`${pageStyle}`}>
        <h1 data-testid={`${baseDataTestId}-container-title`} className={h1Style}>
          {t('company.coMngt.allowances.title')}
        </h1>
        <div data-testid={`${baseDataTestId}-container-subtitle`} className={subtitleStyle}>
          {t('company.coMngt.allowances.subtitle')}
        </div>
        <BookingAllowances
          companyId={companyId}
          icons={icons}
          bookingAllowances={companyDetails?.requestedCompany?.bookingAllowances}
        />
      </div>
    </TranslationProvider>
  );
}

const pageStyle =
  'px-12 pt-12 mobile:pt-6 min-w-[700px] mobile:min-w-full mobile:px-4 w-full [@media(min-width:120rem)]:max-w-[81.75rem] [@media(min-width:120rem)]:mx-auto';
const h1Style =
  'text-[2.5rem] font-black leading-[2.75rem] text-secondaryColor max-w-[620px] min-w-0 break-words mobile:text-[1.75rem] mobile:leading-[2.25rem]';
const subtitleStyle = 'mt-4 text-darkGrey1 font-normal max-w-[620px]';
