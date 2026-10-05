import { TableHeader, TableRow, TableHead } from '@whitbread-eos/atoms/ui';
import { cn } from '@whitbread-eos/utils/server';

import { DataTableColumn, headerCellStyle, noHoverStyle } from './data-table';

type Props = {
  columns: DataTableColumn[];
  isExpendableWith?: string;
  baseTestId?: string;
  isMobileView?: boolean;
};

const DataTableHeader: React.FC<Props> = ({
  columns,
  isExpendableWith,
  isMobileView,
  baseTestId,
}) => {
  return (
    <TableHeader data-testid={`${baseTestId}-header`}>
      <TableRow data-testid={`${baseTestId}-row`} className={noHoverStyle}>
        {columns.map((column: DataTableColumn) => (
          <TableHead
            data-testid={`${baseTestId}-head-${column.id}`}
            key={column.id}
            className={cn(headerCellStyle, column.headerClassName)}
          >
            {column.label}
          </TableHead>
        ))}
        {isExpendableWith && isMobileView && (
          <TableHead
            data-testid={`${baseTestId}-head-expandable`}
            className={cn(headerCellStyle, 'w-[50px]')}
          />
        )}
      </TableRow>
    </TableHeader>
  );
};

export default DataTableHeader;
