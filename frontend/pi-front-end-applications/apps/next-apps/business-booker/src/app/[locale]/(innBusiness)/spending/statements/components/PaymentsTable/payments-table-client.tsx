'use client';

import { CustomerAccountDetails, LOCALES } from '@whitbread-eos/api';
import { useTranslation } from '@whitbread-eos/utils';
import { getAccountPayments } from '@whitbread-eos/utils/server';
import { useState, useMemo } from 'react';

import { DataTableColumn, DataTableRow } from '~components/innBusiness/DataTable';
import { DataTableClient } from '~components/innBusiness/DataTable/data-table-client';

import { formatAmount } from '../../utils/format-amount';
import getPaymentsColumns from './payments-columns';
import { PaymentsNoResults } from './payments-no-results';

type Props = {
  baseDataTestId: string;
  token: string;
  account: CustomerAccountDetails;
  initialItems: any[];
  pageSize: number;
  locale: LOCALES;
  icons: Record<string, string>;
};

const PaymentsTableClient: React.FC<Props> = ({
  locale,
  baseDataTestId,
  initialItems,
  token,
  account,
  pageSize,
  icons,
}) => {
  const { t } = useTranslation('spending');

  const [allLoadedItems, setAllLoadedItems] = useState<DataTableRow[]>(initialItems);
  const [currentPage, setCurrentPage] = useState(1);
  const [isLoading, setIsLoading] = useState(false);
  const [hasNextPage, setHasNextPage] = useState(initialItems.length > pageSize);
  const [maxPage, setMaxPage] = useState(1);

  const fetchAccountPayments = async (page: number): Promise<DataTableRow[]> => {
    const response = await getAccountPayments(
      token,
      account.accountNumber,
      page,
      pageSize + 1,
      account.tetheredGuid
    );
    return response?.payments ?? [];
  };

  const handlePageChange = async (page: number, isPrev: boolean) => {
    let fetched: DataTableRow[] = [];

    if (!isPrev && page >= maxPage + 1) {
      setIsLoading(true);
      fetched = await fetchAccountPayments(page);
      setMaxPage((prev) => Math.max(prev, page));
    }

    const totalItems = [...allLoadedItems, ...fetched];
    setAllLoadedItems((prev) => [...prev, ...fetched]);
    const hasMore = isPrev ? true : totalItems.length > pageSize * page;
    setHasNextPage(hasMore);
    setCurrentPage(page);
    setIsLoading(false);
  };

  const visibleItems = useMemo(() => {
    return currentPage > 1
      ? allLoadedItems.slice((currentPage - 1) * pageSize, currentPage * pageSize)
      : allLoadedItems.slice(0, pageSize);
  }, [allLoadedItems, currentPage, pageSize]);

  const renderRowStatus = (row: DataTableRow, field: string) => {
    const isFailed = row?.[field];

    return (
      <div
        className="flex items-center gap-2"
        data-testid={`${baseDataTestId}-row-status-${isFailed ? 'failed' : 'successful'}`}
      >
        <span
          className={`inline-block w-[12px] h-[12px] rounded-full ${
            isFailed ? 'bg-error' : 'bg-success'
          }`}
          aria-label={
            isFailed
              ? t('statementsInvoicesPayments.payments.table.row.status.failed')
              : t('statementsInvoicesPayments.payments.table.row.status.successful')
          }
        />
        <span>
          {isFailed
            ? t('statementsInvoicesPayments.payments.table.row.status.failed')
            : t('statementsInvoicesPayments.payments.table.row.status.successful')}
        </span>
      </div>
    );
  };

  const renderRowAmount = (row: DataTableRow, field: string) => {
    const { value, currencyCode } = row?.[field] || {};
    return (
      <span>
        {typeof value !== 'undefined' && !isNaN(Number(value))
          ? formatAmount(value, currencyCode, locale)
          : '-'}
      </span>
    );
  };

  const columns: DataTableColumn[] = getPaymentsColumns({
    renderRowAmount,
    renderRowStatus,
    t,
  });

  return (
    <DataTableClient
      baseDataTestId={baseDataTestId}
      columns={columns}
      items={visibleItems}
      isExpendableWith="paymentDescription"
      hasNext={hasNextPage}
      onPageChange={handlePageChange}
      noResultsComponent={<PaymentsNoResults />}
      isLoading={isLoading}
      icons={icons}
    />
  );
};

export default PaymentsTableClient;
