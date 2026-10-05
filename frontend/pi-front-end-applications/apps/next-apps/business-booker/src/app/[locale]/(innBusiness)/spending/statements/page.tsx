import {
  PathParams,
  CustomerAccountDetails,
  SearchParams,
  RegistrationRole,
  LOCALES,
} from '@whitbread-eos/api';
import {
  TranslationProvider,
  getTranslations,
  getCountryLanguageByLocale,
  getPathForLocale,
  ID_TOKEN_COOKIE,
  getDetailsFromToken,
  getAccountList,
} from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';
import { Suspense } from 'react';

import { TextWithInfoTooltip } from '~components/innBusiness/TextWithInfoTooltip';

import { AccountSelector } from '../../homepage/components';
import Analytics from './components/Analytics/analytics';
import { PaymentsTable } from './components/PaymentsTable/payments-table';
import PaymentsTableSkeleton from './components/PaymentsTable/payments-table-skeleton';
import { StatementsTable } from './components/StatementsTable/statements-table';
import StatementsTotal, {
  StatementsTotalSkeleton,
} from './components/StatementsTotal/statements-total';
import SuspendedNotificationsWrapper, {
  SuspendedNotificationsWrapperSkeleton,
  getAccountSuspensionStatus,
} from './components/SuspendedNotificationWrapper/suspended-notifications-wrapper';

const PAGE_NAME = 'Spending and Reporting';
const LOG_PAGE_NAME = 'spending-statements';

type Props = {
  params?: Promise<PathParams>;
  searchParams?: Promise<SearchParams>;
};

export default async function StatementsInvoices({ params, searchParams }: Props) {
  const resolvedParams = await params;
  const resolvedSearchParams = await searchParams;
  const locale = resolvedParams?.locale || LOCALES.EN;
  const baseDataTestId = 'StatementsInvoices';
  const { language } = getCountryLanguageByLocale(locale);
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const { employeeId, companyId } = getDetailsFromToken(token);

  const [{ t, translations }, accounts] = await Promise.all([
    getTranslations(language, ['spending', 'icons', 'layout']),
    getAccountList(token, {}, { pageName: LOG_PAGE_NAME, userId: employeeId, companyId }),
  ]);
  const icons = translations?.['icons'] ?? {};

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

  const renderSectionTitle = (title: string, info: string, baseDataTestId: string) => (
    <h2 className="flex">
      <TextWithInfoTooltip
        baseDataTestId={baseDataTestId}
        mainClassName={sectionTitle}
        mainText={title}
        infoText={info}
        locale={locale}
        icons={icons}
        iconSize={26}
      />
    </h2>
  );

  const isAccountSuspended = await getAccountSuspensionStatus(account);

  return (
    <TranslationProvider value={translations}>
      <div data-testid={`${baseDataTestId}-container`} className={pageStyle}>
        <h1 data-testid={`${baseDataTestId}-Title`} className={h1Style}>
          {t('spending.statementsInvoicesPayments.heading.title')}
        </h1>
        <Suspense fallback={<SuspendedNotificationsWrapperSkeleton />}>
          <SuspendedNotificationsWrapper
            account={account}
            locale={locale}
            isAccountSuspended={isAccountSuspended}
          />
        </Suspense>
        <div className={suspendedContainerStyle}>
          {isAccountSuspended && (
            <div
              data-testid={`${baseDataTestId}-SuspendedOverlay`}
              className={suspendedOverlayStyle}
            />
          )}
          <div
            className={`${contentWrapperStyle} ${isAccountSuspended ? suspendedContentStyle : ''}`}
            aria-disabled={isAccountSuspended}
          >
            <AccountSelector
              className="mobile:hidden"
              locale={locale}
              parentDataTestId={`${baseDataTestId}-accountSelector`}
              hideDropdown={true}
            />
            <div className="flex flex-col gap-6">
              <div className="flex flex-col gap-4">
                {renderSectionTitle(
                  t('spending.statementsInvoicesPayments.payments.title'),
                  t('spending.statementsInvoicesPayments.payments.info.title'),
                  `${baseDataTestId}-PaymentsTitle`
                )}
              </div>
              <Suspense fallback={<PaymentsTableSkeleton t={t} />}>
                <PaymentsTable token={token} account={account} locale={locale} />
              </Suspense>
            </div>
            <hr />
            <div className="flex flex-col gap-10">
              <div className="flex flex-col gap-4">
                {renderSectionTitle(
                  t('spending.statementsInvoicesPayments.statements.title'),
                  t('spending.statementsInvoicesPayments.statements.info.title'),
                  `${baseDataTestId}-StatementsTitle`
                )}
                <Suspense fallback={<StatementsTotalSkeleton t={t} />}>
                  <StatementsTotal
                    locale={locale}
                    icons={icons}
                    baseDataTestId={`${baseDataTestId}-StatementsTotal`}
                    account={account}
                  />
                </Suspense>
              </div>
              <StatementsTable locale={locale} token={token} account={account} icons={icons} />
            </div>
          </div>
        </div>
      </div>
      <Analytics pageName={PAGE_NAME} />
    </TranslationProvider>
  );
}

const pageStyle =
  'px-12 pt-12 min-w-[700px] mobile:min-w-full mobile:px-4 mobile:py-6 flex flex-col gap-12 w-full [@media(min-width:120rem)]:max-w-[81.75rem] [@media(min-width:120rem)]:mx-auto';
const h1Style = 'text-[2.5rem] font-black leading-[2.75rem] text-secondaryColor mobile:text-4xl';
const sectionTitle = 'font-bold text-xl';
const suspendedContainerStyle = 'relative';
const suspendedOverlayStyle = 'absolute inset-0 rounded-md bg-white/60 backdrop-blur-[2px] z-10';
const contentWrapperStyle = 'flex flex-col gap-12';
const suspendedContentStyle = 'pointer-events-none select-none opacity-50 blur-[1px]';
