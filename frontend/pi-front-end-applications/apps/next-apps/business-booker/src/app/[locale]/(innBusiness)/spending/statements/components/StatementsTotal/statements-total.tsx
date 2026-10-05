import {
  CurrentBalanceItem,
  Currency,
  CustomerAccountDetails,
  LOCALES,
  Scheme,
} from '@whitbread-eos/api';
import { Skeleton } from '@whitbread-eos/atoms/ui';
import {
  getAccountInfo,
  getCountryLanguageByLocale,
  getWorldlineReturnUrl,
  getTranslations,
  getSpendingSummaryV2,
  ID_TOKEN_COOKIE,
} from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';
import React from 'react';

import { TextWithInfoTooltip } from '~components/innBusiness/TextWithInfoTooltip';
import { WordlineButton } from '~components/innBusiness/WordlineButton';

import { formatAmount } from '../../utils/format-amount';

interface StatementsTotalProps {
  locale: LOCALES;
  icons: Record<string, string>;
  baseDataTestId: string;
  account: CustomerAccountDetails;
}

export default async function StatementsTotal({
  locale,
  icons,
  baseDataTestId,
  account,
}: StatementsTotalProps) {
  const { language } = getCountryLanguageByLocale(locale);
  const wlReturnUrl = getWorldlineReturnUrl(locale, 'homepage');

  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';

  const [{ t }, accountInfo, spendingSummary] = await Promise.all([
    getTranslations(language, ['icons', 'spending']),
    getAccountInfo(account?.scheme ?? 'GB', account?.tetheredGuid ?? ''),
    account
      ? getSpendingSummaryV2(token, account.scheme as Scheme, account.tetheredGuid)
      : Promise.resolve(null),
  ]);

  const currentBalance: CurrentBalanceItem | null =
    (spendingSummary?.data?.getAccountBalanceSummaryV2 as CurrentBalanceItem) ?? null;

  const isPIBAEuro = account?.scheme === 'DE';

  const wlPostUrl = isPIBAEuro
    ? process?.env?.NEXT_PUBLIC_WORLDLINE_POST_URL_DE
    : process?.env?.NEXT_PUBLIC_WORLDLINE_POST_URL;

  const makePaymentPage = isPIBAEuro ? 'InterimPayment.aspx' : 'CardPayment.aspx';

  const renderSpendingDetail = (
    title: string,
    info: string,
    value: string,
    baseDataTestId: string
  ) => (
    <div className={spendingDetail}>
      <span className="flex">
        <TextWithInfoTooltip
          baseDataTestId={baseDataTestId}
          mainClassName="font-semibold text-base"
          mainText={title}
          infoText={info}
          locale={locale}
          icons={icons}
          iconSize={24}
        />
      </span>
      <span className="font-semibold text-4xl">{value}</span>
    </div>
  );

  return (
    <>
      <div className={statementsSummaryContainer} data-testid={baseDataTestId}>
        {renderSpendingDetail(
          t('spending.statementsInvoicesPayments.statements.latestStatementValue.label'),
          t('spending.statementsInvoicesPayments.statements.latestStatementValue.info.title'),
          formatAmount(
            accountInfo?.statementValue?.value ?? 0,
            accountInfo?.statementValue?.currencyCode ?? Currency.GBP_CODE,
            locale
          ),
          `${baseDataTestId}-Statements-LastValue`
        )}
        {renderSpendingDetail(
          t('spending.statementsInvoicesPayments.statements.interimPayments.label'),
          t('spending.statementsInvoicesPayments.statements.interimPayments.info.title'),
          formatAmount(
            currentBalance?.interimPayments?.amount ?? 0,
            currentBalance?.interimPayments?.currencyCode === Currency.EUR_NAME
              ? Currency.EUR_CODE
              : Currency.GBP_CODE,
            locale
          ),
          `${baseDataTestId}-Statements-TotalValue`
        )}
        {renderSpendingDetail(
          t('spending.statementsInvoicesPayments.statements.outstandingBalance.label'),
          t('spending.statementsInvoicesPayments.statements.outstandingBalance.info.title'),
          formatAmount(
            currentBalance?.outstanding?.amount ?? 0,
            currentBalance?.outstanding?.currencyCode === Currency.EUR_NAME
              ? Currency.EUR_CODE
              : Currency.GBP_CODE,
            locale
          ),
          `${baseDataTestId}-Statements-Balance`
        )}
      </div>
      <WordlineButton
        baseDataTestId={`${baseDataTestId}-Make-a-payment`}
        tetheredGuid={account?.tetheredGuid ?? ''}
        scheme={account?.scheme}
        icon={t('icons.icon.manageEmployees-icon')}
        text={t('spending.statementsInvoicesPayments.statements.button.makePayment')}
        page={makePaymentPage}
        returnUrl={wlReturnUrl}
        variant={'outline'}
        postUrl={wlPostUrl ?? ''}
        analyticsLogEvent="makePayment"
      />
    </>
  );
}

export type StatementsTotalSkeletonProps = {
  t: (key: string) => string;
};

// eslint-disable-next-line no-empty-pattern
export const StatementsTotalSkeleton = ({}: StatementsTotalSkeletonProps) => {
  return (
    <>
      <div className={statementsSummaryContainer}>
        <div className={spendingDetail}>
          <span className="flex font-semibold text-base">Latest statement value</span>
          <Skeleton className="w-[19.854rem] h-[2.5rem]" />
        </div>
        <div className={spendingDetail}>
          <span className="flex font-semibold text-base">Interim payments</span>
          <Skeleton className="w-[19.854rem] h-[2.5rem]" />
        </div>
        <div className={spendingDetail}>
          <span className="flex font-semibold text-base">Outstanding balance</span>
          <Skeleton className="w-[19.854rem] h-[2.5rem]" />
        </div>
      </div>
      <Skeleton className="w-[18.75rem] h-[3.5rem]" />
    </>
  );
};

const statementsSummaryContainer = 'grid grid-cols-3 mobile:grid-cols-1 gap-4';
const spendingDetail = 'w-[19.854rem] flex flex-col gap-2';
