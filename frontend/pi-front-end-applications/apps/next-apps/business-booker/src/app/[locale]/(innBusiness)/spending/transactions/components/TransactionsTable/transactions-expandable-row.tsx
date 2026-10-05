'use client';

import { TableRow, TableCell } from '@whitbread-eos/atoms/ui';
import {
  cn,
  useTranslation,
  formatIBAssetsUrl,
  getLocaleByPathname,
} from '@whitbread-eos/utils/server';
import Image from 'next/image';
import { usePathname } from 'next/navigation';
import { FC, useState } from 'react';

import type {
  DataTableColumn,
  DataTableRow as DataTableRowType,
} from '~components/innBusiness/DataTable/data-table';
import { cellStyle } from '~components/innBusiness/DataTable/data-table-page';

import {
  formatAmount,
  getCurrencyCodeBasedOnCurrencySymbol,
} from '../../../statements/utils/format-amount';

type Itemisation = {
  type: 'itemisations';
  items: Array<{
    description: string;
    guestName?: string;
    amount: number;
    currencyCode: string;
    currencySymbol?: string;
  }>;
};

export interface TransactionsExpandableRowProps {
  baseDataTestId: string;
  columns: DataTableColumn[];
  row: DataTableRowType;
  rowData: string;
  rowIndex: number;
  expendableKey: string;
  isMobileView?: boolean;
}

const TransactionsExpandableRow: FC<TransactionsExpandableRowProps> = ({
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
  const pathname = usePathname();
  const locale = getLocaleByPathname(pathname);
  const formatCurrencyAndAmount = (item: Itemisation['items'][number]) => {
    return formatAmount(
      item.amount,
      getCurrencyCodeBasedOnCurrencySymbol(item.currencySymbol || ''),
      locale
    );
  };

  const renderExpandableContent = () => {
    try {
      const expandableData = JSON.parse(row[expendableKey]) as Itemisation;

      if (expandableData?.type === 'itemisations') {
        if (isMobileView) {
          return (
            <TableCell colSpan={columns.length + 1} className="p-0">
              <div className="py-2 px-4 break-words">
                {expandableData.items.map((item, itemIndex) => (
                  <div key={itemIndex} className="mb-2 last:mb-0">
                    <div
                      style={{
                        display: 'grid',
                        gridTemplateColumns: '2fr 1fr 1fr',
                        gap: '1rem',
                        alignItems: 'center',
                      }}
                    >
                      <div style={{ gridColumn: '1 / 3' }}>{item.description}</div>
                      <div className="text-right">{formatCurrencyAndAmount(item)}</div>
                    </div>
                    {item.guestName && <div className="text-gray-600 mt-1">({item.guestName})</div>}
                  </div>
                ))}
              </div>
            </TableCell>
          );
        }

        return (
          <>
            {columns.map((column: DataTableColumn, colIndex: number) => {
              if (column.id === 'location') {
                return (
                  <TableCell key={colIndex} className={cn(cellStyle, column.className)}>
                    <div className="py-2 space-y-1 break-words">
                      {expandableData.items.map((item, itemIndex) => (
                        <div key={itemIndex}>
                          <div>{item.description}</div>
                          {item.guestName && (
                            <div className="text-gray-600">({item.guestName})</div>
                          )}
                        </div>
                      ))}
                    </div>
                  </TableCell>
                );
              }

              if (column.id === 'grossValue') {
                return (
                  <TableCell key={colIndex} className={cn(cellStyle, column.className)}>
                    <div className="py-2 text-right space-y-1">
                      {expandableData.items.map((item, itemIndex) => (
                        <div key={itemIndex}>{formatCurrencyAndAmount(item)}</div>
                      ))}
                    </div>
                  </TableCell>
                );
              }

              return (
                <TableCell key={colIndex} className={cn(cellStyle, column.className)}></TableCell>
              );
            })}
            <TableCell className={cn(cellStyle, 'w-[50px]')}></TableCell>
          </>
        );
      }
      // eslint-disable-next-line no-empty
    } catch {}

    return (
      <TableCell colSpan={columns.length + 1}>
        <div className="whitespace-pre-line break-words p-4">{row[expendableKey]}</div>
      </TableCell>
    );
  };

  return (
    <>
      <TableRow
        data-rowdata={rowData}
        data-testid={`${baseDataTestId}-row-${rowIndex}`}
        key={rowIndex}
        onClick={() => setIsExpanded((prev) => !prev)}
        className="cursor-pointer hover:bg-gray-50"
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
      </TableRow>
      {isExpanded && (
        <TableRow
          data-testid={`${baseDataTestId}-row-expanded`}
          className="bg-lightGrey4 hover:bg-lightGrey4"
        >
          {renderExpandableContent()}
        </TableRow>
      )}
    </>
  );
};

export default TransactionsExpandableRow;
