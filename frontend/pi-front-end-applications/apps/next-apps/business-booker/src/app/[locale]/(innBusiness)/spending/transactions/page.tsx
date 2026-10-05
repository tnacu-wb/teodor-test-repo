import {
  PathParams,
  CustomerAccountDetails,
  SearchParams,
  RegistrationRole,
  LOCALES,
  FT_IB_TRANSACTIONS,
} from '@whitbread-eos/api';
import {
  TranslationProvider,
  getTranslations,
  getCountryLanguageByLocale,
  getPathForLocale,
  ID_TOKEN_COOKIE,
  getServerUnleashToggles,
  getAccountList,
  getDetailsFromToken,
} from '@whitbread-eos/utils/server';
import { cookies, headers } from 'next/headers';
import { redirect } from 'next/navigation';

import { AccountSelector } from '../../homepage/components';
import Analytics from './components/Analytics/analytics';
import { TransactionsDownloadButton } from './components/TransactionsDownloadButton/transactions-download-button';
import { TransactionsTable } from './components/TransactionsTable';

type Props = {
  params?: Promise<PathParams>;
  searchParams?: Promise<SearchParams>;
};

const PAGE_NAME = 'Transactions';
const LOG_PAGE_NAME = 'spending-transactions';

export default async function Transactions({ params, searchParams }: Props) {
  const resolvedParams = await params;
  const resolvedSearchParams = await searchParams;
  const locale = resolvedParams?.locale || LOCALES.EN;
  const baseDataTestId = 'Transactions';
  const { language } = getCountryLanguageByLocale(locale);
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const { employeeId, companyId } = getDetailsFromToken(token);
  const headerList = await headers();
  const currentPath = headerList.get('WB-Url') ?? '';
  const pageSize = 15;
  const pageIndexParam = Number(resolvedSearchParams?.pageIndex);
  const pageIndex = Number.isFinite(pageIndexParam) && pageIndexParam > 0 ? pageIndexParam : 1;

  const flagsFallback = {
    [FT_IB_TRANSACTIONS]: false,
  };

  const [{ t, translations }, accounts, toggles] = await Promise.all([
    getTranslations(language, ['icons', 'layout', 'spending']),
    getAccountList(token, {}, { pageName: LOG_PAGE_NAME, userId: employeeId, companyId }),
    getServerUnleashToggles(currentPath, flagsFallback),
  ]);

  if (!toggles[FT_IB_TRANSACTIONS]) {
    redirect(getPathForLocale(locale, 'homepage'));
  }

  const icons = translations?.['icons'] || {};

  let account: CustomerAccountDetails =
    accounts?.find(
      (acc: CustomerAccountDetails) => acc.tetheredGuid === resolvedSearchParams?.account
    ) ?? null;

  if (!account && accounts?.length) {
    account = accounts?.[0];
  }

  const isOnlyCardHolder =
    account?.registrationRoles?.length === 1 &&
    account?.registrationRoles?.[0] === RegistrationRole.CardHolder;

  if (!account || isOnlyCardHolder) {
    redirect(getPathForLocale(locale, 'homepage'));
  }

  const formatDateToIsoString = (date: Date) => date.toISOString().split('T')[0];

  const currentDate = new Date();
  const threeYearsAgoDate = new Date(currentDate.getTime());
  threeYearsAgoDate.setFullYear(threeYearsAgoDate.getFullYear() - 3);

  const transactionsSearchCriteria = {
    dateSearch: {
      dateFrom: formatDateToIsoString(threeYearsAgoDate),
      dateTo: formatDateToIsoString(currentDate),
      transactionTypes: 'Both',
    },
  };

  return (
    <TranslationProvider value={translations}>
      <div data-testid={`${baseDataTestId}-container`} className={pageStyle}>
        <h1 data-testid={`${baseDataTestId}-Title`} className={h1Style}>
          {t('spending.spending.transactions.title')}
        </h1>
        <AccountSelector locale={locale} parentDataTestId={`${baseDataTestId}-accountSelector`} />
        <TransactionsTable
          icons={icons}
          locale={locale}
          token={token}
          pageSize={pageSize}
          pageIndex={pageIndex}
          requestParameters={{
            scheme: account?.scheme || '',
            schemeCustomerId: account?.schemeCustomerId || 0,
            tetheredUserGuid: account?.tetheredGuid || '',
            searchCriteria: transactionsSearchCriteria,
          }}
          actions={
            <TransactionsDownloadButton
              icons={icons}
              token={token}
              schemeCustomerId={account?.schemeCustomerId}
              tetheredUserGuid={account?.tetheredGuid}
              scheme={account?.scheme}
              testId={`${baseDataTestId}-Download`}
              className="border border-secondaryColor"
            />
          }
        />
      </div>
      <Analytics pageName={PAGE_NAME} />
    </TranslationProvider>
  );
}

const pageStyle =
  'px-12 pt-12 min-w-[700px] mobile:min-w-full mobile:px-4 mobile:py-6 flex flex-col gap-12 w-full [@media(min-width:120rem)]:max-w-[81.75rem] [@media(min-width:120rem)]:mx-auto';
const h1Style = 'text-[2.5rem] font-black leading-[2.75rem] text-secondaryColor mobile:text-4xl';
