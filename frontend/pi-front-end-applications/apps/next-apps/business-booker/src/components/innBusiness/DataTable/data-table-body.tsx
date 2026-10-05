'use client';

import { TableBody } from '@whitbread-eos/atoms/ui';

import { DataTableColumn, DataTableRow as DataTableRowType } from './data-table';
import DataTableExpendableRow from './data-table-expendable-row';
import DataTableRow from './data-table-row';

type Props = {
  baseDataTestId: string;
  rows: DataTableRowType[];
  columns: DataTableColumn[];
  isExpendableWith?: string;
  isMobileView?: boolean;
  expandableRowComponent?: typeof DataTableExpendableRow;
};

const DataTableBody: React.FC<Props> = ({
  baseDataTestId,
  rows,
  columns,
  isExpendableWith,
  isMobileView,
  expandableRowComponent,
}) => {
  const ExpandableRow = expandableRowComponent ?? DataTableExpendableRow;

  return (
    <TableBody>
      {rows.map((row: DataTableRowType, rowIndex: number) => {
        const rowData = JSON.stringify(row);

        return isExpendableWith ? (
          <ExpandableRow
            baseDataTestId={baseDataTestId}
            columns={columns as DataTableColumn[]}
            row={row}
            rowData={rowData}
            rowIndex={rowIndex}
            key={`${baseDataTestId}-row-${row.id ?? rowIndex}`}
            expendableKey={isExpendableWith}
            isMobileView={isMobileView}
          />
        ) : (
          <DataTableRow
            baseDataTestId={baseDataTestId}
            columns={columns as DataTableColumn[]}
            rowIndex={rowIndex}
            rowData={rowData}
            row={row}
            key={`${baseDataTestId}-row-${row.id ?? rowIndex}`}
          />
        );
      })}
    </TableBody>
  );
};

export default DataTableBody;
