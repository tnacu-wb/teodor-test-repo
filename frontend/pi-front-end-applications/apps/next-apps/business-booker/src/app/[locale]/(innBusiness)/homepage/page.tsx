import {
  PathParams,
  LOCALES,
  SearchParams,
  CustomerAccountDetails,
  BUSINESS_BOOKER_USER_ROLES,
  FT_IB_PAY_PIBA_EURO,
  CountryCode,
} from '@whitbread-eos/api';
import { Skeleton } from '@whitbread-eos/atoms/ui';
import {
  getCountryLanguageByLocale,
  getTranslations,
  TranslationProvider,
  ID_TOKEN_COOKIE,
  getServerUnleashToggles,
  getWorldlineReturnUrl,
  getDetailsFromToken,
  getAccountRegistrationRoleDetails,
  getAccountList,
  getPayApplications,
} from '@whitbread-eos/utils/server';
import { cookies, headers } from 'next/headers';
import { Suspense } from 'react';

import { InnBusinessPayApplySkeleton } from '../manage/cards/components/InnBusinessPay/inn-business-pay-apply-skeleton';
import {
  Applications,
  ApplicationsSkeleton,
  AccountSelector,
  News,
  NewsSkeleton,
  UpcomingBookings,
  UpcomingBookingsSkeleton,
  Welcome,
  WelcomeSkeleton,
  SpendingSummary,
  SpendingSummarySkeleton,
  Notifications,
  BannerPromo,
  AccountSelectorSkeleton,
} from './components';
import Analytics from './components/Analytics/analytics';
import { getValidApplications } from './components/Applications/applications';

const PAGE_LABEL = 'IB | HMP | Homepage';
const PAGE_NAME = 'Homepage';
const LOG_PAGE_NAME = 'homepage';

type Props = {
  params?: Promise<PathParams>;
  searchParams?: Promise<SearchParams>;
};

export default async function Homepage({ params, searchParams }: Props) {
  const headerList = await headers();
  const baseDataTestId = 'HomePage';
  const resolvedParams = await params;
  const locale = (resolvedParams?.locale?.toLowerCase() as LOCALES) ?? '';
  const wlReturnUrl = getWorldlineReturnUrl(locale, 'homepage');
  const { language, country } = getCountryLanguageByLocale(locale);
  const resolvedSearchParams = await searchParams;
  const accountSearchParam = resolvedSearchParams?.account ?? '';

  const cookieStore = await cookies();
  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  if (!token || token.trim() === '') {
    return (
      <div data-testid={`${baseDataTestId}-container`} className={pageStyle}>
        <Skeleton className="h-12 w-full mb-4" />
      </div>
    );
  }

  const { accessLevel, employeeId, companyId, isBusinessPayManager, isBusinessPayUser } =
    getDetailsFromToken(token);
  const currentPath = headerList.get('WB-Url') ?? '';
  const flagsFallback = {
    [FT_IB_PAY_PIBA_EURO]: false,
  };

  const [{ t, translations }, accounts, response, toggles] = await Promise.all([
    getTranslations(language, ['homepage', 'notifications', 'icons', 'cards', 'layout']),
    getAccountList(token, {}, { pageName: LOG_PAGE_NAME, userId: employeeId, companyId }),
    getPayApplications(token),
    getServerUnleashToggles(PAGE_LABEL, flagsFallback, currentPath, {}),
  ]);

  const isBusinessPayRole = isBusinessPayManager || isBusinessPayUser;
  let account =
    accounts?.find((acc: CustomerAccountDetails) => acc.tetheredGuid === accountSearchParam) ??
    null;
  if (!account && accounts?.length) {
    account = accounts?.[0];
  }
  const { isOnlyCardHolder, isOnlyCostCenter, isCardHolderAndCostCenterUser } =
    getAccountRegistrationRoleDetails(account);
  const isCostCenterUser = isOnlyCostCenter || isCardHolderAndCostCenterUser;

  const isDeLanguage = language === CountryCode.DE;

  const isBannerVisible = t('homepage.home.promoBanner.isVisible');
  const bannerTitle = t('homepage.home.promoBanner.title');
  const bannerDescription = t('homepage.home.promoBanner.description');
  const bannerLink = t('homepage.home.promoBanner.link');
  const bannerImage = t('homepage.home.promoBanner.image');

  const showApply = toggles?.[FT_IB_PAY_PIBA_EURO] || !isDeLanguage;

  const isExcludedRole = [
    BUSINESS_BOOKER_USER_ROLES.SELF,
    BUSINESS_BOOKER_USER_ROLES.STAYER,
    BUSINESS_BOOKER_USER_ROLES.BOOKER,
    BUSINESS_BOOKER_USER_ROLES.BUSINESS_PAY_USER,
  ].includes(accessLevel);

  const showSpendingSummaryBanner = showApply || account;
  const shouldRenderSpendingSummary = !account || (!isOnlyCardHolder && !isCostCenterUser);

  const applications = getValidApplications(response?.data?.getPayApplications?.applications ?? []);

  const shouldRenderInnBusinessPayHeading =
    !isExcludedRole &&
    !isDeLanguage &&
    (account || shouldRenderSpendingSummary || applications?.length > 0);

  return (
    <TranslationProvider value={translations}>
      <div data-testid={`${baseDataTestId}-container`} className={pageStyle}>
        <Suspense fallback={<Skeleton className="h-12 w-full mb-4" />}>
          <Notifications account={account} locale={locale} token={token} />
        </Suspense>
        {isBannerVisible && (
          <Suspense fallback={<Skeleton className={'w-full min-h-[3.56rem] mt-px mb-[3rem]'} />}>
            <BannerPromo
              language={language}
              dataTestId={baseDataTestId}
              title={bannerTitle}
              description={bannerDescription}
              link={bannerLink}
              bannerImage={bannerImage}
            />
          </Suspense>
        )}
        <Suspense fallback={<WelcomeSkeleton />}>
          <Welcome locale={locale} parentDataTestId={baseDataTestId} t={t} />
        </Suspense>
        {shouldRenderInnBusinessPayHeading && (
          <h2 className={h2Style} data-testid={`${baseDataTestId}-PageTitle`}>
            {t('homepage.home.innbusinessPay.heading')}
          </h2>
        )}
        <>
          {account && (
            <Suspense fallback={<AccountSelectorSkeleton />}>
              <AccountSelector
                locale={locale}
                parentDataTestId={baseDataTestId}
                isHomepage={true}
                accountID={account?.tetheredGuid}
              />
            </Suspense>
          )}
          {shouldRenderSpendingSummary && (
            <Suspense
              key={`homepage-${account?.tetheredGuid ?? 'no-account'}`}
              fallback={
                account ? <SpendingSummarySkeleton t={t} /> : <InnBusinessPayApplySkeleton />
              }
            >
              <SpendingSummary
                token={token}
                locale={locale}
                account={account}
                bannerHiddenForRoles={[
                  BUSINESS_BOOKER_USER_ROLES.BOOKER,
                  BUSINESS_BOOKER_USER_ROLES.STAYER,
                  BUSINESS_BOOKER_USER_ROLES.SELF,
                  BUSINESS_BOOKER_USER_ROLES.BUSINESS_PAY_USER,
                ]}
                wlReturnUrl={wlReturnUrl}
                showBanner={showSpendingSummaryBanner}
                isIBPayOn={true}
              />
            </Suspense>
          )}
        </>
        <Suspense fallback={<ApplicationsSkeleton t={t} />}>
          <Applications locale={locale} showApply={showApply} applications={applications} />
        </Suspense>
        {!isBusinessPayRole && (
          <Suspense
            fallback={<UpcomingBookingsSkeleton t={t} country={country} language={language} />}
          >
            <UpcomingBookings locale={locale} />
          </Suspense>
        )}
        <Suspense fallback={<NewsSkeleton t={t} />}>
          <News locale={locale} />
        </Suspense>
        <Analytics pageName={PAGE_NAME} />
      </div>
    </TranslationProvider>
  );
}

const pageStyle =
  'px-12 pt-12 min-w-[700px] mobile:min-w-full mobile:px-4 mobile:py-6 w-full [@media(min-width:120rem)]:max-w-[81.75rem] [@media(min-width:120rem)]:mx-auto';
const h2Style = 'text-xl leading-[1.5rem] font-bold mb-4';
