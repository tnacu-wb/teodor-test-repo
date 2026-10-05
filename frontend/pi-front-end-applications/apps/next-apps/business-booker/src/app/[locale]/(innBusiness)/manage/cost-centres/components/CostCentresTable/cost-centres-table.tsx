'use client';

import { CostCentreData, CustomerAccountDetails } from '@whitbread-eos/api';
import { WorldlineLink } from '@whitbread-eos/atoms/ui';
import { useTranslation, formatIBAssetsUrl } from '@whitbread-eos/utils';
import Image from 'next/image';
import { useState, useMemo } from 'react';

import { DataTableColumn } from '~components/innBusiness/DataTable';
import {
  DataTableClient,
  PaginationType,
} from '~components/innBusiness/DataTable/data-table-client';

import getCostCentresColumns from './cost-centres-columns';
import { CostCentresNoResults } from './cost-centres-no-results';

type Props = {
  baseDataTestId: string;
  initialItems: CostCentreData[];
  pageSize: number;
  icons: Record<string, string>;
  account: CustomerAccountDetails;
  worldlinePostUrl: string;
  worldlineReturnUrl: string;
};

const CostCentresTable: React.FC<Props> = ({
  baseDataTestId,
  initialItems,
  pageSize,
  icons,
  account,
  worldlinePostUrl,
  worldlineReturnUrl,
}) => {
  const { t } = useTranslation('cards');
  const accountHolderIcon = icons['icon.manageEmployees-icon'];
  const hasNextPage = initialItems.length > pageSize;

  const [currentPage, setCurrentPage] = useState(1);
  const handlePageChange = async (page: number) => {
    setCurrentPage(page);
  };

  const visibleItems = useMemo(() => {
    return currentPage > 1
      ? initialItems.slice((currentPage - 1) * pageSize, currentPage * pageSize)
      : initialItems.slice(0, pageSize);
  }, [initialItems, currentPage, pageSize]);

  const renderRowStatus = () => {
    return (
      <div
        className="flex items-center gap-2"
        data-testid={`${baseDataTestId}-row-status-successful`}
      >
        <span
          className={'inline-block w-[12px] h-[12px] rounded-full bg-success'}
          aria-label={t('costCentreMgmt.costCentreStatus.options.active')}
        />
        <span>{t('costCentreMgmt.costCentreStatus.options.active')}</span>
      </div>
    );
  };

  const renderActions = (code: string) => {
    return (
      <div data-testid={`${baseDataTestId}-row-actions`} className={tableActionsRightStyle}>
        <WorldlineLink
          tetheredGuid={account.tetheredGuid}
          className={linkStyle}
          data-testid={`${baseDataTestId}-row-action-edit-${code}-link`}
          worldlinePostUrl={worldlinePostUrl}
          worldlineRequestedPage="CostCentreList.aspx"
          worldlineReturnUrl={worldlineReturnUrl}
          scheme={account.scheme}
        >
          <span>{t('costCentreMgmt.columns.edit')}</span>
          <Image
            alt={t('costCentreMgmt.columns.edit')}
            src={formatIBAssetsUrl(accountHolderIcon)}
            width={12}
            height={12}
            data-testid={`${baseDataTestId}-row-action-edit-${code}-icon`}
          />
        </WorldlineLink>
      </div>
    );
  };

  const columns: DataTableColumn[] = getCostCentresColumns({
    renderRowStatus,
    renderActions,
    t,
  });

  return (
    <DataTableClient
      baseDataTestId={baseDataTestId}
      columns={columns}
      items={visibleItems}
      hasNext={hasNextPage}
      onPageChange={handlePageChange}
      noResultsComponent={<CostCentresNoResults />}
      isLoading={false}
      icons={icons}
      paginationType={PaginationType.PAGES}
      totalPages={Math.ceil(initialItems.length / pageSize)}
    />
  );
};

export default CostCentresTable;

const tableActionsRightStyle =
  'flex gap-1 justify-end items-center mobile:justify-center mobile:mr-2';
const linkStyle = 'p-2 cursor-pointer gap-2 underline text-secondaryColor flex items-center';
