'use client';

import { TableRow, TableCell } from '@whitbread-eos/atoms/ui';
import { cn, useTranslation, formatIBAssetsUrl } from '@whitbread-eos/utils';
import Image from 'next/image';
import { useState, FC } from 'react';

import { DataTableColumn, DataTableRow as DataTableRowType } from './data-table';
import { cellStyle } from './data-table-page';

interface DataTableExpendableRowProps {
  baseDataTestId: string;
  columns: DataTableColumn[];
  row: DataTableRowType;
  rowData: string;
  rowIndex: number;
  expendableKey: string;
  isMobileView?: boolean;
}

const DataTableExpendableRow: FC<DataTableExpendableRowProps> = ({
  baseDataTestId,
  columns,
  row,
  rowData,
  rowIndex,
  expendableKey,
  isMobileView,
}) => {
  const { t } = useTranslation('icons');
  const [isExpanded, setIsExpanded] = useState(false);
  const expandIcon = formatIBAssetsUrl(t('icon.expand-icon'));
  const collapseIcon = formatIBAssetsUrl(t('icon.collapse-icon'));

  return (
    <>
      <TableRow
        data-rowdata={rowData}
        data-testid={`${baseDataTestId}-row-${rowIndex}`}
        key={rowIndex}
        onClick={() => (isMobileView ? setIsExpanded((prev) => !prev) : undefined)}
        className={isMobileView ? 'cursor-pointer' : ''}
      >
        {columns.map((column: DataTableColumn) => {
          const cell = row[column.id];
          return (
            <TableCell
              key={column.id}
              data-testid={`${baseDataTestId}-row-${column.id}-${rowIndex}`}
              className={cn(cellStyle, column.className)}
            >
              {column.render ? column.render(cell, row) : cell}
            </TableCell>
          );
        })}
        {isMobileView && (
          <TableCell
            data-testid={`${baseDataTestId}-row-expandable-${rowIndex}`}
            className={cn(cellStyle, 'w-[50px]')}
          >
            <Image
              data-testid="Expand-Collapse-Icon"
              src={isExpanded ? collapseIcon : expandIcon}
              alt={isExpanded ? 'collapse' : 'expand'}
              width={24}
              height={24}
            />
          </TableCell>
        )}
      </TableRow>
      {isExpanded && isMobileView && (
        <TableRow
          data-testid={`${baseDataTestId}-row-expanded`}
          className="bg-lightGrey4 !hover:bg-lightGrey4"
        >
          <TableCell colSpan={columns.length + 1}>{row[expendableKey]}</TableCell>
        </TableRow>
      )}
    </>
  );
};

export default DataTableExpendableRow;
