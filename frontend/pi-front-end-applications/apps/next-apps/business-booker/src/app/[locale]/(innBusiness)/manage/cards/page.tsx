import { PathParams, SearchParams, CountryCode, FT_IB_PAY_PIBA_EURO } from '@whitbread-eos/api';
import { Tabs, TabsList, StaticTabsTrigger, SearchParamLink } from '@whitbread-eos/atoms/ui';
import {
  getPathForLocale,
  getAvailableTabs,
  ID_TOKEN_COOKIE,
  getTranslations,
  getCountryLanguageByLocale,
  getAccessLevel,
  MANAGE_TABS,
  TranslationProvider,
  getServerUnleashToggles,
  getDetailsFromToken,
  getSearchParams,
} from '@whitbread-eos/utils/server';
import { cookies, headers } from 'next/headers';
import { redirect } from 'next/navigation';
import { Suspense } from 'react';

import { Analytics } from './components/Analytics/Analytics';
import { CentrallyStored, CentrallyStoredSkeleton } from './components/CentrallyStored';
import {
  InnBusinessPay,
  InnBusinessPayApply,
  InnBusinessPaySkeleton,
  InnBusinessPayApplySkeleton,
} from './components/InnBusinessPay';

const PAGE_NAME = 'Card Management';

type Props = {
  params?: Promise<PathParams>;
  searchParams?: Promise<SearchParams>;
};

export default async function Cards({ params, searchParams }: Props) {
  const resolvedParams = await params;
  const resolvedSearchParams = await searchParams;
  const baseDataTestId = 'ManageCardsPage';
  const headerList = await headers();
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const { companyId } = getDetailsFromToken(token);
  const { language } = getCountryLanguageByLocale(resolvedParams?.locale);

  const isDeLanguage = language === CountryCode.DE;

  const currentPath = headerList.get('WB-Url') ?? '';
  const flagsFallback = {
    [FT_IB_PAY_PIBA_EURO]: false,
  };

  const [
    {
      isTethered,
      isTravelManager,
      hasCardHolder,
      hasAccountHolder,
      hasCostCenterHolder,
      isBusinessPayManager,
    },
    { t, translations },
    toggle,
    urlSearchParams,
  ] = await Promise.all([
    getAccessLevel(resolvedSearchParams?.account),
    getTranslations(language, ['cards', 'notifications', 'layout']),
    getServerUnleashToggles(currentPath, flagsFallback),
    getSearchParams(),
  ]);

  const availableTabs = getAvailableTabs(
    isTravelManager,
    isTethered,
    hasAccountHolder,
    hasCardHolder,
    true,
    isDeLanguage,
    toggle[FT_IB_PAY_PIBA_EURO],
    hasCostCenterHolder,
    isBusinessPayManager
  );

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
        <h1 className={h1Style}>{t('cards.cardMgmt.cardManagement.title')}</h1>

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
                  data-testid={`${baseDataTestId}-centrallyStoredTab`}
                  active={tab === MANAGE_TABS.INN_BUSINESS}
                >
                  {t('cards.cardMgmt.tabs.centrallyStored')}
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
                  {t('cards.cardMgmt.tabs.innBusinessPay')}
                </StaticTabsTrigger>
              </SearchParamLink>
            </TabsList>
          </Tabs>
        )}

        <div className={tabsContentStyle}>
          {tab === MANAGE_TABS.INN_BUSINESS && (
            <Suspense fallback={<CentrallyStoredSkeleton />}>
              <CentrallyStored
                locale={resolvedParams?.locale}
                token={token}
                companyId={companyId}
              />
            </Suspense>
          )}

          {tab === MANAGE_TABS.INN_BUSINESS_PAY &&
            (isTethered ? (
              <Suspense fallback={<InnBusinessPaySkeleton />}>
                <InnBusinessPay
                  locale={resolvedParams?.locale}
                  token={token}
                  searchParams={resolvedSearchParams}
                />
              </Suspense>
            ) : (
              <Suspense fallback={<InnBusinessPayApplySkeleton />}>
                <InnBusinessPayApply locale={resolvedParams?.locale} token={token} />
              </Suspense>
            ))}
        </div>
      </div>
      <Analytics
        pageName={PAGE_NAME}
        tab={tab === MANAGE_TABS.INN_BUSINESS ? 'Centrally Stored' : 'Inn Business Pay'}
      />
    </TranslationProvider>
  );
}

const pageStyle =
  'p-12 min-w-[700px] mobile:min-w-full mobile:px-4 mobile:py-6 w-full [@media(min-width:120rem)]:max-w-[81.75rem] [@media(min-width:120rem)]:mx-auto';
const h1Style = 'text-[2.5rem] font-black leading-[2.75rem] text-secondaryColor mobile:text-4xl';
const tabsStyle = 'mt-12 mobile:hidden';
const tabsContentStyle = 'mt-12';
