import { Button } from '@whitbread-eos/atoms';
import { TableRow, TableCell, TableFooter } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl } from '@whitbread-eos/utils/server';
import Image from 'next/image';
import React from 'react';

interface DataTableSimplePaginationProps {
  baseTestId: string;
  columns: any[];
  page: number;
  hasNext: boolean;
  handlePreviousPageClick: () => void;
  handleNextPageClick: () => void;
  icons?: Record<string, string>;
  isLoading: boolean;
}

const DataTableSimplePagination: React.FC<DataTableSimplePaginationProps> = ({
  baseTestId,
  columns,
  page,
  hasNext,
  handlePreviousPageClick,
  handleNextPageClick,
  icons,
  isLoading,
}) => {
  return (
    <TableFooter data-testid={`${baseTestId}-Pagination`} className={'h-[3.75rem]'}>
      <TableRow>
        <TableCell colSpan={columns.length} style={{ textAlign: 'center', borderTop: 'none' }}>
          <div
            className={`flex ${
              hasNext && page === 1 ? 'justify-end' : 'justify-between'
            } items-center`}
          >
            {page && Number(page) > 1 && (
              <Button
                variant={'default'}
                data-testid={`${baseTestId}-Prev`}
                aria-label="Previous"
                type="button"
                onClick={handlePreviousPageClick}
                isDisabled={isLoading}
              >
                <Image
                  className={buttonStyle}
                  src={formatIBAssetsUrl(
                    isLoading
                      ? icons?.['icon.chevron.left'] || ''
                      : icons?.['icon.chevron.left.purple'] || ''
                  )}
                  alt="Previous"
                  width={32}
                  height={32}
                />
              </Button>
            )}
            {hasNext && (
              <Button
                variant={'default'}
                data-testid={`${baseTestId}-Next`}
                aria-label="Next"
                type="button"
                onClick={handleNextPageClick}
                isDisabled={isLoading}
              >
                <Image
                  className={buttonStyle}
                  src={formatIBAssetsUrl(
                    isLoading
                      ? icons?.['icon.chevron.right'] || ''
                      : icons?.['icon.chevron.right.purple'] || ''
                  )}
                  alt="Next"
                  width={32}
                  height={32}
                />
              </Button>
            )}
          </div>
        </TableCell>
      </TableRow>
    </TableFooter>
  );
};

export default DataTableSimplePagination;

const buttonStyle = 'hover:opacity-80';
