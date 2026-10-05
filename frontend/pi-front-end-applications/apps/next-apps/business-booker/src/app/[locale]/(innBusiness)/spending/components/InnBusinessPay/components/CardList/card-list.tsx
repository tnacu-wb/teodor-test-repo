'use client';

import { CustomerAccountDetails } from '@whitbread-eos/api';
import { WorldlineLink } from '@whitbread-eos/atoms/ui';
import {
  useTranslation,
  getPathForLocale,
  getAccountRegistrationRoleDetails,
} from '@whitbread-eos/utils/server';

import CardLink, { CardLinkProps } from '~components/innBusiness/CardLink/card-link';

export interface CardListProps {
  account: CustomerAccountDetails | null;
  wlReturnUrl?: string;
  wlPostUrl?: string;
  locale?: string;
  isStatementsInvoicesOn?: boolean;
  isTransactionsOn?: boolean;
}

const CardList = ({
  account,
  wlReturnUrl,
  wlPostUrl,
  locale,
  isStatementsInvoicesOn,
  isTransactionsOn,
}: CardListProps) => {
  const { t } = useTranslation('spending');
  const { isOnlyCardHolder, isOnlyCostCenter } = getAccountRegistrationRoleDetails(account);

  const isPIBAEuro = account?.scheme === 'DE';
  const shouldUseExternalTransactionsLink = isOnlyCardHolder || !isTransactionsOn;

  const cardData: CardLinkProps[] = [
    {
      title: t('spending.summary.reports'),
      subtitle: t('spending.summary.reports.description'),
      alt: `${t('spending.summary.reports')} icon`,
      icon: t('spending.summary.reports.icon'),
      href: 'ReportsList.aspx',
      baseDataTestId: 'AboutReportsCard',
      isExternalHref: true,
    },
    {
      title: t('spending.summary.create.reports'),
      subtitle: t('spending.summary.create.reports.description'),
      alt: `${t('spending.summary.create.reports')} icon`,
      icon: t('spending.summary.create.reports.icon'),
      href: isPIBAEuro ? 'ReportsList.aspx' : 'ReportDefine.aspx',
      baseDataTestId: 'CreateReportCard',
      isExternalHref: true,
    },
    {
      title: t('spending.summary.scheduled.reports'),
      subtitle: t('spending.summary.scheduled.reports.description'),
      alt: `${t('spending.summary.scheduled.reports')} icon`,
      icon: t('spending.summary.scheduled.reports.icon'),
      href: 'DownloadScheduledReports.aspx',
      baseDataTestId: 'ScheduledReportsCard',
      isExternalHref: true,
    },
    ...(isOnlyCardHolder || isOnlyCostCenter || isPIBAEuro
      ? []
      : [
          {
            title: t('spending.summary.linked.accounts'),
            subtitle: t('spending.summary.linked.accounts.description'),
            alt: `${t('spending.summary.linked.accounts')} icon`,
            icon: t('spending.summary.linked.accounts.icon'),
            href: 'LinkedAccounts.aspx',
            baseDataTestId: 'LinkedAccountsCard',
            isExternalHref: true,
          },
        ]),
    ...(isOnlyCardHolder
      ? []
      : [
          {
            title: t('spending.summary.statement.invoice'),
            subtitle: t('spending.summary.statement.invoice.description'),
            alt: `${t('spending.summary.statement.invoice')} icon`,
            icon: t('spending.summary.statement.invoice.icon'),
            href: isStatementsInvoicesOn
              ? getPathForLocale(
                  locale,
                  `spending/statements${
                    account?.tetheredGuid ? `?account=${account.tetheredGuid}` : ''
                  }`
                )
              : 'Statements.aspx',
            baseDataTestId: 'StatementsAndInvoicesCard',
            isExternalHref: !isStatementsInvoicesOn,
          },
        ]),
    {
      title: t('spending.summary.transactions'),
      subtitle: t('spending.summary.transactions.description'),
      alt: `${t('spending.summary.transactions')} icon`,
      icon: t('spending.summary.transactions.icon'),
      href: shouldUseExternalTransactionsLink
        ? 'Transactions.aspx'
        : getPathForLocale(
            locale,
            `spending/transactions${
              account?.tetheredGuid ? `?account=${account.tetheredGuid}` : ''
            }`
          ),
      baseDataTestId: 'TransactionsCard',
      isExternalHref: shouldUseExternalTransactionsLink,
    },
  ];

  const renderCard = (
    card: CardLinkProps,
    onClick?: (e: React.MouseEvent<HTMLElement>) => void
  ) => (
    <CardLink
      key={card.baseDataTestId}
      title={card.title}
      subtitle={card.subtitle}
      alt={card.alt}
      icon={card.icon}
      href={card.href}
      baseDataTestId={card.baseDataTestId}
      isExternalHref={card.isExternalHref}
      onClick={onClick}
    />
  );

  return (
    <div className={`${cardGridStyle} ${isOnlyCardHolder ? 'grid-cols-2' : 'grid-cols-3'}`}>
      {cardData.map((card) =>
        card.isExternalHref ? (
          <WorldlineLink
            key={card.baseDataTestId}
            tetheredGuid={account?.tetheredGuid ?? ''}
            worldlinePostUrl={wlPostUrl ?? ''}
            worldlineRequestedPage={card.href}
            worldlineReturnUrl={wlReturnUrl}
            scheme={account?.scheme}
            renderButton={(onWorldlineLinkClick: (e: React.MouseEvent<HTMLElement>) => void) =>
              renderCard(card, onWorldlineLinkClick)
            }
          ></WorldlineLink>
        ) : (
          renderCard(card)
        )
      )}
    </div>
  );
};

const cardGridStyle = 'mt-[3rem] grid mobile:grid-cols-1 gap-8';

export default CardList;
