import {
  PathParams,
  LOCALES,
  SearchParams,
  FT_IB_PAY_PIBA_EURO,
  CountryCode,
  FT_IB_STATEMENTS_INVOICES,
  FT_IB_TRANSACTIONS,
  FT_IB_YOUR_SPENDING,
} from '@whitbread-eos/api';
import {
  getCountryLanguageByLocale,
  getTranslations,
  TranslationProvider,
  getServerUnleashToggles,
  ID_TOKEN_COOKIE,
  getDetailsFromToken,
  getAccountList,
} from '@whitbread-eos/utils/server';
import { headers, cookies } from 'next/headers';
import { Suspense } from 'react';

import { Company } from './components/Company';
import CompanySpendingSkeleton from './components/Company/company-skeleton';
import { InnBusinessPay } from './components/InnBusinessPay';
import { InnBusinessPaySkeleton } from './components/InnBusinessPay/inn-business-pay';
import { SPENDING_TABS, SpendingTabs } from './components/Tabs';
import { YourSpending } from './components/YourSpending';
import YourSpendingSkeleton from './components/YourSpending/your-spending-skeleton';

type Props = {
  params?: Promise<PathParams>;
  searchParams?: Promise<SearchParams>;
};

const LOG_PAGE_NAME = 'spending';

const shouldShowSpendingTabs = (
  isTravelManager: boolean,
  isDeLanguage: boolean,
  isTethered: boolean,
  isPibaEuroEnabled: boolean,
  isYourSpendingTabEnabled: boolean,
  isGuest: boolean
): boolean => {
  if (isYourSpendingTabEnabled) {
    if (isGuest) {
      return false;
    }
    return true;
  } else {
    if (!isTravelManager) {
      return false;
    }

    if (!isDeLanguage || isTethered || isPibaEuroEnabled) {
      return true;
    }

    return false;
  }
};

export default async function Spending({ params, searchParams }: Props) {
  const resolvedParams = await params;
  const resolvedSearchParams = await searchParams;
  const headerList = await headers();
  const baseDataTestId = 'Spending';
  const { language, country } = getCountryLanguageByLocale(resolvedParams?.locale);
  const locale = resolvedParams?.locale ?? LOCALES.EN;
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const { isTravelManager, isBusinessPayManager, employeeId, companyId, isGuest } =
    getDetailsFromToken(token);
  const currentPath = headerList.get('WB-Url') ?? '';
  const flagsFallback = {
    [FT_IB_PAY_PIBA_EURO]: false,
    [FT_IB_STATEMENTS_INVOICES]: false,
    [FT_IB_TRANSACTIONS]: false,
    [FT_IB_YOUR_SPENDING]: false,
  };

  const [{ t, translations }, accounts, toggles] = await Promise.all([
    getTranslations(language, [
      'spending',
      'homepage',
      'cards',
      'notifications',
      'icons',
      'layout',
    ]),
    getAccountList(token, {}, { pageName: LOG_PAGE_NAME, userId: employeeId, companyId }),
    getServerUnleashToggles(currentPath, flagsFallback),
  ]);

  const icons = translations?.['icons'] || {};

  const isDeLanguage = language === CountryCode.DE;
  const isTethered = accounts && accounts.length > 0;
  const isPibaEuroEnabled = toggles[FT_IB_PAY_PIBA_EURO];
  const isYourSpendingTabEnabled = toggles[FT_IB_YOUR_SPENDING];
  let tab = isTravelManager ? SPENDING_TABS.COMPANY : SPENDING_TABS.INN_BUSINESS_PAY;
  if (resolvedSearchParams?.tab === SPENDING_TABS.INN_BUSINESS_PAY) {
    tab = SPENDING_TABS.INN_BUSINESS_PAY;
  }
  if (resolvedSearchParams?.tab === SPENDING_TABS.YOUR_SPENDING) {
    tab = SPENDING_TABS.YOUR_SPENDING;
  }
  const shouldSelectCompanyTab =
    isDeLanguage && isTravelManager && !isTethered && !isPibaEuroEnabled;
  if (shouldSelectCompanyTab) {
    tab = SPENDING_TABS.COMPANY;
  }

  const showTabs = shouldShowSpendingTabs(
    isTravelManager,
    isDeLanguage,
    isTethered,
    isPibaEuroEnabled,
    isYourSpendingTabEnabled,
    isGuest
  );
  const showYourSpendingTab = isYourSpendingTabEnabled && !isGuest;
  return (
    <TranslationProvider value={translations}>
      <div data-testid={`${baseDataTestId}-container`} className={pageStyle}>
        <h1 data-testid={`${baseDataTestId}-Title`} className={h1Style}>
          {t('spending.spending.reporting.heading')}
        </h1>

        {showTabs && (
          <SpendingTabs
            selectedTab={tab}
            locale={locale}
            isTravelManager={isTravelManager}
            showYourSpendingTab={showYourSpendingTab}
          />
        )}

        {isTravelManager && tab === SPENDING_TABS.COMPANY && (
          <Suspense fallback={<CompanySpendingSkeleton />}>
            <Company locale={locale} token={token} icons={icons} />
          </Suspense>
        )}
        {((!isTravelManager && !isYourSpendingTabEnabled) ||
          tab === SPENDING_TABS.INN_BUSINESS_PAY) && (
          <Suspense fallback={<InnBusinessPaySkeleton t={t} />}>
            <InnBusinessPay
              locale={locale}
              searchParamAccount={resolvedSearchParams?.account as string}
              token={token}
              language={language}
              country={country}
              isBusinessPayManager={isBusinessPayManager}
              isTravelManager={isTravelManager}
              toggles={toggles}
            />
          </Suspense>
        )}
        {tab === SPENDING_TABS.YOUR_SPENDING && showYourSpendingTab && (
          <Suspense fallback={<YourSpendingSkeleton />}>
            <YourSpending locale={locale} token={token} icons={icons} />
          </Suspense>
        )}
      </div>
    </TranslationProvider>
  );
}

const h1Style =
  'text-[2.5rem] font-black leading-[2.75rem] text-secondaryColor mobile:text-4xl mb-8';
const pageStyle =
  'px-12 pt-12 min-w-[700px] mobile:min-w-full mobile:px-4 mobile:py-6 w-full [@media(min-width:120rem)]:max-w-[81.75rem] [@media(min-width:120rem)]:mx-auto';
