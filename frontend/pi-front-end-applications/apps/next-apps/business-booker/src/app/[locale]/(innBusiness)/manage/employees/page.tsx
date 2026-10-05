import { CountryCode, FT_IB_PAY_PIBA_EURO, PathParams, SearchParams } from '@whitbread-eos/api';
import { Tabs, TabsList, StaticTabsTrigger, SearchParamLink } from '@whitbread-eos/atoms/ui';
import {
  getAccessLevel,
  getCountryLanguageByLocale,
  getPathForLocale,
  getTranslations,
  TranslationProvider,
  getAvailableTabs,
  MANAGE_TABS,
  ID_TOKEN_COOKIE,
  getServerUnleashToggles,
  getSearchParams,
} from '@whitbread-eos/utils/server';
import { cookies, headers } from 'next/headers';
import { redirect } from 'next/navigation';
import { Suspense } from 'react';

import { Analytics } from '~components/innBusiness/ManageEmployeesAnalytics';

import { InnBusinessPayApplySkeleton } from '../cards/components/InnBusinessPay/inn-business-pay-apply-skeleton';
import { InnBusiness, InnBusinessSkeleton } from './components/InnBusiness';
import {
  InnBusinessPay,
  InnBusinessPayApply,
  InnBusinessPaySkeleton,
} from './components/InnBusinessPay';

type Props = {
  params?: Promise<PathParams>;
  searchParams?: Promise<SearchParams>;
};

export default async function Employees({ params, searchParams }: Props) {
  const resolvedParams = await params;
  const resolvedSearchParams = await searchParams;
  const baseDataTestId = 'ManageEmployeesPage';
  const headerList = await headers();
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const { language } = getCountryLanguageByLocale(resolvedParams?.locale);

  const isDeLanguage = language === CountryCode.DE;

  const currentPath = headerList.get('WB-Url') ?? '';
  const flagsFallback = {
    [FT_IB_PAY_PIBA_EURO]: false,
  };

  const [
    { t, translations },
    { isTravelManager, isTethered, hasAccountHolder, hasCardHolder, isBusinessPayManager },
    toggle,
    urlSearchParams,
  ] = await Promise.all([
    getTranslations(language, ['users', 'cards', 'notifications', 'layout']),
    getAccessLevel(),
    getServerUnleashToggles(currentPath, flagsFallback),
    getSearchParams(),
  ]);

  const availableTabs: string[] = getAvailableTabs(
    isTravelManager,
    isTethered,
    hasAccountHolder,
    hasCardHolder,
    false,
    isDeLanguage,
    toggle[FT_IB_PAY_PIBA_EURO],
    false,
    isBusinessPayManager
  );

  const pageName =
    resolvedSearchParams?.tab === MANAGE_TABS.INN_BUSINESS_PAY
      ? 'Manage Employees: Select Account'
      : null;

  if (!availableTabs.length) {
    redirect(getPathForLocale(resolvedParams?.locale, 'homepage'));
  }

  let tab = '';
  if (availableTabs.length === 1) {
    tab = availableTabs[0];
  } else {
    tab = MANAGE_TABS.INN_BUSINESS;
    if (resolvedSearchParams?.tab === MANAGE_TABS.INN_BUSINESS_PAY) {
      tab = MANAGE_TABS.INN_BUSINESS_PAY;
    }
  }

  return (
    <TranslationProvider value={translations}>
      <div data-testid={`${baseDataTestId}-container`} className={pageStyle}>
        <h1 className={h1Style}>{t('users.userMgmt.manageEmployees.title')}</h1>

        {availableTabs.length > 1 && (
          <Tabs className={tabsStyle}>
            <TabsList>
              <SearchParamLink
                name="tab"
                value={MANAGE_TABS.INN_BUSINESS}
                clear
                searchParams={urlSearchParams}
              >
                <StaticTabsTrigger
                  data-testid={`${baseDataTestId}-InnBusinessTab`}
                  active={tab === MANAGE_TABS.INN_BUSINESS}
                >
                  {t('users.userMgmt.manageEmployees.toggle.innBusiness.label')}
                </StaticTabsTrigger>
              </SearchParamLink>

              <SearchParamLink
                name="tab"
                value={MANAGE_TABS.INN_BUSINESS_PAY}
                clear
                searchParams={urlSearchParams}
              >
                <StaticTabsTrigger
                  data-testid={`${baseDataTestId}-innbusinessPayTab`}
                  active={tab === MANAGE_TABS.INN_BUSINESS_PAY}
                >
                  {t('users.userMgmt.manageEmployees.toggle.innBusinessPay.label')}
                </StaticTabsTrigger>
              </SearchParamLink>
            </TabsList>
          </Tabs>
        )}

        {tab === MANAGE_TABS.INN_BUSINESS && (
          <div className={tabsContentStyle}>
            <Suspense fallback={<InnBusinessSkeleton />}>
              <InnBusiness locale={resolvedParams?.locale} />
            </Suspense>
          </div>
        )}

        {tab === MANAGE_TABS.INN_BUSINESS_PAY && (
          <div className={tabsContentStyle}>
            {isTethered ? (
              <Suspense fallback={<InnBusinessPaySkeleton />}>
                <InnBusinessPay locale={resolvedParams?.locale} />
              </Suspense>
            ) : (
              <Suspense fallback={<InnBusinessPayApplySkeleton />}>
                <InnBusinessPayApply locale={resolvedParams?.locale} token={token} />
              </Suspense>
            )}
            <Analytics pageName={pageName} />
          </div>
        )}

        <Analytics availableTabs={availableTabs} />
      </div>
    </TranslationProvider>
  );
}

const pageStyle =
  'px-12 pt-12 min-w-[700px] mobile:min-w-full mobile:px-4 mobile:py-6 w-full [@media(min-width:120rem)]:max-w-[81.75rem] [@media(min-width:120rem)]:mx-auto';
const h1Style = 'text-[2.5rem] font-black leading-[2.75rem] text-secondaryColor mobile:text-4xl';
const tabsStyle = 'mt-12 mobile:hidden';
const tabsContentStyle = 'mt-12';
