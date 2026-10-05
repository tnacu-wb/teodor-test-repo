import {
  Table,
  TableHeader,
  TableRow,
  TableHead,
  TableBody,
  TableCell,
} from '@whitbread-eos/atoms/ui';
import { getSearchParams, cn } from '@whitbread-eos/utils/server';
import { ReactNode, Suspense } from 'react';

import { DataTablePage } from './data-table-page';
import { DataTablePagination } from './data-table-pagination';
import { DataTableSkeleton } from './data-table-skeleton';

export type DataTableCell = any;

export type DataTableRow = Record<string, DataTableCell>;
export type DataTableRowLoadMore = { rows: DataTableRow[]; pageToken: string };

export type DataTableColumn = {
  id: string;
  label: string;
  headerClassName?: string;
  className?: string;
  render?: (cell: DataTableCell, row: DataTableRow) => React.ReactNode;
};

export type DataTableFilters = Record<string, any>;

export type DataTableGetPageFn = (
  pageIndex: number,
  pageSize: number,
  filters?: DataTableFilters
) => Promise<DataTableRow[] | DataTableRowLoadMore>;

export type DataTableGetTotalFn = (filters?: DataTableFilters) => Promise<number>;
export type LoadMoreRowsAndToken = {
  result: React.ReactNode[] | React.ReactNode;
  pageToken: string;
};

export type DataTableGetFiltersFn = () => DataTableFilters;

export const DATA_TABLE_PAGE_SIZE = 15;

export type DataTableProps = {
  pageIndex: string | string[] | undefined | number;
  columns: DataTableColumn[];
  getPage: DataTableGetPageFn;
  getTotal: DataTableGetTotalFn;
  getFilters?: DataTableGetFiltersFn;
  noResultsComponent?: ReactNode;
  hasLoadMorePagination?: boolean;
  getExtraRows?: (pageToken: string, clickCount: number) => Promise<LoadMoreRowsAndToken>;
  loadMoreLabel?: string;
  analyticsComponent?: React.ComponentType<any>;
};

export async function DataTable({
  pageIndex,
  columns,
  getPage,
  getTotal,
  getFilters = () => ({}),
  noResultsComponent,
  hasLoadMorePagination = false,
  getExtraRows,
  loadMoreLabel,
  analyticsComponent,
}: DataTableProps) {
  const [total, searchParams] = await Promise.all([getTotal(getFilters()), getSearchParams()]);
  const totalPages = Math.ceil(total / DATA_TABLE_PAGE_SIZE);

  pageIndex = Number(pageIndex) || 1;
  if (pageIndex < 1) {
    pageIndex = 1;
  }
  if (pageIndex > totalPages) {
    pageIndex = totalPages;
  }

  return (
    <Table data-testid="InnBusiness-DataTable" className={tableStyle}>
      <TableHeader data-testid="InnBusiness-DataTable-header">
        <TableRow data-testid="InnBusiness-DataTable-row" className={noHoverStyle}>
          {columns.map((column: DataTableColumn) => (
            <TableHead
              data-testid={`InnBusiness-DataTable-head-${column.id}`}
              key={column.id}
              className={cn(headerCellStyle, column.headerClassName)}
            >
              {column.label}
            </TableHead>
          ))}
        </TableRow>
      </TableHeader>

      <TableBody data-testid="InnBusiness-DataTable-body">
        <Suspense
          key={searchParams.toString()}
          fallback={<DataTableSkeleton columnCount={columns.length} />}
        >
          <DataTablePage
            analyticsComponent={analyticsComponent}
            pageIndex={pageIndex}
            totalPages={totalPages}
            columns={columns}
            getPage={getPage}
            getFilters={getFilters}
            noResultsComponent={noResultsComponent}
            hasLoadMorePagination={hasLoadMorePagination}
            getExtraRows={getExtraRows}
            loadMoreLabel={loadMoreLabel}
          />
        </Suspense>

        {totalPages > 1 && (
          <TableRow className={noHoverStyle}>
            <TableCell colSpan={columns.length} className={paginationCellStyle}>
              <DataTablePagination
                pageIndex={pageIndex}
                totalPages={totalPages}
                className={paginationDesktopStyle}
              />
              <DataTablePagination
                pageIndex={pageIndex}
                totalPages={totalPages}
                mobile
                className={paginationMobileStyle}
              />
            </TableCell>
          </TableRow>
        )}
      </TableBody>
    </Table>
  );
}

export const noHoverStyle = 'hover:bg-transparent';
const paginationCellStyle = 'h-[60px]';
export const tableStyle = 'mobile:table-fixed tablet:table-fixed';
const paginationDesktopStyle = 'mobile:hidden tablet:hidden';
const paginationMobileStyle = 'hidden mobile:flex tablet:flex';
export const headerCellStyle =
  'mobile:overflow-hidden mobile:text-ellipsis mobile:whitespace-nowrap tablet:truncate px-2 first-of-type:pl-4 last-of-type:pr-4';
