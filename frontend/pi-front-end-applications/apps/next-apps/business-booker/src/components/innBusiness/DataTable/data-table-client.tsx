'use client';

import {
  Table,
  TableBody,
  TableRow,
  TableCell,
  TableFooter,
  Skeleton,
} from '@whitbread-eos/atoms/ui';
import { ReactNode, useEffect, useState } from 'react';

import DataTableBody from './data-table-body';
import DataTableExpendableRow from './data-table-expendable-row';
import DataTableHeader from './data-table-header';
import DataTablePagesPagination from './data-table-pages-pagination';
import DataTableSimplePagination from './data-table-simple-pagination';
import { DataTableSkeleton } from './data-table-skeleton';

type Props = {
  columns: any[];
  items: any[];
  baseDataTestId: string;
  hasNext: boolean;
  isLoading: boolean;
  noResultsComponent?: ReactNode;
  onPageChange?: (page: number, isPrev: boolean) => void;
  isExpendableWith?: string;
  icons: Record<string, string>;
  expandableRowComponent?: typeof DataTableExpendableRow;
  paginationType?: PaginationType;
  totalPages?: number;
};

export enum PaginationType {
  SIMPLE = 'SIMPLE',
  PAGES = 'PAGES',
}

export function DataTableClient({
  baseDataTestId,
  columns,
  items,
  noResultsComponent,
  isLoading,
  isExpendableWith,
  hasNext,
  onPageChange,
  icons,
  expandableRowComponent,
  paginationType = PaginationType.SIMPLE,
  totalPages = undefined,
}: Props) {
  const [page, setPage] = useState(1);
  const baseTestId = `${baseDataTestId}-DataTableClient`;

  const [isMobileView, setIsMobileView] = useState(false);

  const handlePreviousPageClick = () => {
    if (onPageChange) {
      const newPage = Number(page) - 1;
      setPage(newPage);
      onPageChange(newPage, true);
    }
  };

  const handleNextPageClick = () => {
    if (onPageChange) {
      const newPage = Number(page) + 1;
      setPage(newPage);
      onPageChange(newPage, false);
    }
  };

  const handlePageClick = (pageNumber: number) => {
    if (onPageChange) {
      const isPrev = pageNumber < page;
      setPage(pageNumber);
      onPageChange(pageNumber, isPrev);
    }
  };

  useEffect(() => {
    const handleResize = () => {
      setIsMobileView(window.innerWidth < 1280);
    };
    handleResize();
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  return (
    <Table data-testid={baseTestId} className={tableStyle}>
      <DataTableHeader
        columns={columns}
        isExpendableWith={isExpendableWith}
        baseTestId={baseTestId}
        isMobileView={isMobileView}
      />
      {items?.length > 0 ? (
        <>
          <DataTableBody
            baseDataTestId={baseTestId}
            rows={items}
            columns={columns}
            isExpendableWith={isExpendableWith}
            isMobileView={isMobileView}
            expandableRowComponent={expandableRowComponent}
          />
          {(hasNext || page > 1) && paginationType === PaginationType.SIMPLE && (
            <DataTableSimplePagination
              isLoading={isLoading}
              columns={columns}
              baseTestId={baseTestId}
              page={page}
              hasNext={hasNext}
              handlePreviousPageClick={handlePreviousPageClick}
              handleNextPageClick={handleNextPageClick}
              icons={icons}
            />
          )}
          {paginationType === PaginationType.PAGES && totalPages && (
            <DataTablePagesPagination
              columns={columns}
              baseTestId={baseTestId}
              page={page}
              totalPages={totalPages}
              icons={icons}
              onPageChange={handlePageClick}
              onPrevChange={handlePreviousPageClick}
              onNextChange={handleNextPageClick}
              mobile={isMobileView}
            />
          )}
        </>
      ) : isLoading ? (
        <>
          <TableBody>
            <DataTableSkeleton columnCount={columns.length} />
          </TableBody>
          {(hasNext || page > 1) && (
            <TableFooter>
              <TableRow data-testid="DataTableSkeletonPagination">
                <TableCell>
                  <Skeleton className={skeletonStyle} />
                </TableCell>
              </TableRow>
            </TableFooter>
          )}
        </>
      ) : (
        noResultsComponent
      )}
    </Table>
  );
}

const tableStyle = 'table-fixed w-full';
const skeletonStyle = 'h-[31px] my-2';
