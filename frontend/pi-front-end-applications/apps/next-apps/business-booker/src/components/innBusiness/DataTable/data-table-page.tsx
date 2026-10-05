import { ReactNode } from 'react';

import { LoadMore } from '~components/innBusiness/LoadMoreButton';

import {
  DATA_TABLE_PAGE_SIZE,
  DataTableColumn,
  DataTableGetFiltersFn,
  DataTableGetPageFn,
  DataTableRow as DataTableRowType,
  LoadMoreRowsAndToken,
} from './data-table';
import DataTableRow from './data-table-row';

export type DataTablePageProps = {
  pageIndex: number;
  totalPages?: number;
  columns: DataTableColumn[];
  getPage: DataTableGetPageFn;
  getFilters?: DataTableGetFiltersFn;
  noResultsComponent?: ReactNode;
  hasLoadMorePagination?: boolean;
  getExtraRows?: (pageToken: string, clickCount: number) => Promise<LoadMoreRowsAndToken>;
  loadMoreLabel?: string;
  analyticsComponent?: React.ComponentType<any>;
};

export async function DataTablePage({
  pageIndex,
  columns,
  getPage,
  getFilters = () => ({}),
  noResultsComponent,
  hasLoadMorePagination,
  getExtraRows,
  loadMoreLabel,
  analyticsComponent: AnalyticsComponent,
  totalPages = NaN,
}: DataTablePageProps) {
  const baseDataTestId = 'DataTablePage';
  const filters = getFilters();
  const data = await getPage(pageIndex, DATA_TABLE_PAGE_SIZE, filters);
  const isCancelledCardsFilterOn = filters?.cancelledCards || false;

  const rows = Array.isArray(data) ? data : data.rows;

  return (
    <>
      {rows.length ? (
        <>
          {rows.map((row: DataTableRowType, rowIndex: number) => {
            const rowData = JSON.stringify(row);

            return (
              <DataTableRow
                baseDataTestId={baseDataTestId}
                columns={columns as DataTableColumn[]}
                rowIndex={rowIndex}
                rowData={rowData}
                row={row}
                key={`${baseDataTestId}-row-${rowIndex}`}
              />
            );
          })}
          {hasLoadMorePagination && getExtraRows && (
            <LoadMore
              getExtraRows={getExtraRows}
              loadMoreLabel={loadMoreLabel}
              testId={baseDataTestId}
            />
          )}
        </>
      ) : (
        noResultsComponent
      )}
      {AnalyticsComponent && (
        <AnalyticsComponent
          rowsData={rows}
          pageIndex={pageIndex}
          totalPages={totalPages}
          isCancelledCardsFilterOn={isCancelledCardsFilterOn}
        />
      )}
    </>
  );
}

export const cellStyle =
  'mobile:truncate border-t-0 py-0 border-b border-lightGrey3 px-2 first-of-type:pl-4 last-of-type:pr-4';
