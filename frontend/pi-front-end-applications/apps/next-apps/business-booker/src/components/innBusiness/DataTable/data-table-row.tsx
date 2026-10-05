import { TableRow, TableCell } from '@whitbread-eos/atoms/ui';
import { cn } from '@whitbread-eos/utils/server';

import { DataTableColumn, DataTableRow as DataTableRowType } from './data-table';
import { cellStyle } from './data-table-page';

type Props = {
  baseDataTestId: string;
  rowIndex: number;
  columns: DataTableColumn[];
  rowData: string;
  row: DataTableRowType;
};

const DataTableRow: React.FC<Props> = ({ rowIndex, rowData, row, columns, baseDataTestId }) => {
  return (
    <TableRow
      data-rowdata={rowData}
      data-testid={`${baseDataTestId}-row-${rowIndex}`}
      key={rowIndex}
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
    </TableRow>
  );
};

export default DataTableRow;
