import { LOCALES } from '@whitbread-eos/api';
import { getAccountTransactions } from '@whitbread-eos/utils/server';
import { Suspense, ReactNode } from 'react';

import { DataTableRow } from '~components/innBusiness/DataTable';
import { DataTablePagination } from '~components/innBusiness/DataTable/data-table-pagination';

import Analytics from '../Analytics/analytics';
import TransactionsTableClient from './transactions-table-client';

type Transaction = {
  transactionDate: string;
  pan: string;
  cardName: string;
  location: string;
  purchaseOrderReference: string;
  customerOwnRef: string;
  grossAmount: {
    amount: number;
    currencyCode: string;
    currencySymbol: string;
  };
  lineItems: Array<{
    grossAmount: {
      amount: number;
      currencyCode: string;
      currencySymbol: string;
    };
    description: string;
    guestName: string;
  }>;
};

type TransactionsRequestParameters = {
  scheme: string;
  schemeCustomerId: number;
  tetheredUserGuid: string;
  searchCriteria: {
    dateSearch: {
      dateFrom: string;
      dateTo: string;
      transactionTypes: string;
    };
  };
};

type Props = {
  icons: Record<string, string>;
  locale: LOCALES;
  token: string;
  pageSize?: number;
  pageIndex?: number;
  requestParameters: TransactionsRequestParameters;
  actions?: ReactNode;
};

const DEFAULT_PAGE_SIZE = 15;

const buildTableRows = (
  transactions: Transaction[],
  page: number,
  pageSize: number
): DataTableRow[] => {
  return transactions.map((transaction, index) => {
    const pan = transaction?.pan ?? '';
    const maskedPan = pan.includes('*') ? `**** ${pan.slice(-4)}` : pan;
    const lineItems = transaction?.lineItems ?? [];
    const globalIndex = (page - 1) * pageSize + index;

    return {
      id: `transaction-${globalIndex}`,
      date: transaction?.transactionDate,
      cardHolder: transaction?.cardName ?? '',
      cardNo: maskedPan,
      location: transaction?.location ?? '',
      purchaseOrder: transaction?.purchaseOrderReference ?? '',
      customerRef: transaction?.customerOwnRef ?? '',
      grossValue:
        transaction?.grossAmount ?? ({ amount: 0, currencyCode: '', currencySymbol: '' } as const),
      itemisations: JSON.stringify({
        type: 'itemisations',
        items: lineItems.map((item) => ({
          description: item?.description ?? '',
          guestName: item?.guestName,
          amount: item?.grossAmount?.amount ?? 0,
          currencyCode: item?.grossAmount?.currencyCode ?? '',
          currencySymbol: item?.grossAmount?.currencySymbol ?? '',
        })),
      }),
    };
  });
};

export default async function TransactionsTable({
  icons,
  locale,
  token,
  pageSize = DEFAULT_PAGE_SIZE,
  pageIndex,
  requestParameters,
  actions,
}: Props) {
  const currentPage = Number.isFinite(pageIndex) && pageIndex && pageIndex > 0 ? pageIndex : 1;

  const response = await getAccountTransactions(token, {
    ...requestParameters,
    pagingRequest: {
      page: currentPage,
      maximumDisplayRows: pageSize,
    },
  });

  const viewAccountTransactions = response?.data?.viewAccountTransactions;
  const transactions = (viewAccountTransactions?.response?.transactions ?? []) as Transaction[];
  const totalRecords =
    viewAccountTransactions?.pagingResult?.totalRecordCount ?? transactions.length;
  const totalPages = Math.max(1, Math.ceil(totalRecords / pageSize));

  const items = buildTableRows(transactions, currentPage, pageSize);
  const hasResults = items.length > 0;

  return (
    <div data-testid="TransactionsTable-container" className="overflow-x-auto">
      <TransactionsTableClient items={items} icons={icons} locale={locale} actions={actions} />

      {hasResults && totalPages > 1 && (
        <div className="mt-6 flex justify-center">
          <Suspense fallback={null}>
            <DataTablePagination pageIndex={currentPage} totalPages={totalPages} />
          </Suspense>
        </div>
      )}
      <Analytics transactionsCount={items.length} />
    </div>
  );
}
