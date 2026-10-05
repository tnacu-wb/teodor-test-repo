import {
  Table,
  TableHeader,
  TableRow,
  TableCell,
  TableHead,
  TableBody,
  TableFooter,
  Skeleton,
} from '@whitbread-eos/atoms/ui';
import { cn } from '@whitbread-eos/utils/server';

import { headerCellStyle, noHoverStyle, tableStyle } from './data-table';
import { DataTableSkeleton as SkeletonRows } from './data-table-skeleton';

type Props = {
  baseTestId: string;
  columns: DataTableSkeletonColumn[];
};

export type DataTableSkeletonColumn = {
  label: string;
  headerClassName?: string;
};

const DataTableClientSkeleton: React.FC<Props> = ({ baseTestId, columns }) => {
  return (
    <Table data-testid={baseTestId} className={tableStyle}>
      <TableHeader data-testid={`${baseTestId}-header`}>
        <TableRow data-testid={`${baseTestId}-row`} className={noHoverStyle}>
          {columns.map((column: DataTableSkeletonColumn, index: number) => (
            <TableHead
              data-testid={`${baseTestId}-head-${index}`}
              key={`column-${column.label}-${index}`}
              className={cn(headerCellStyle, column.headerClassName)}
            >
              {column.label}
            </TableHead>
          ))}
        </TableRow>
      </TableHeader>
      <TableBody data-testid={`${baseTestId}-body`}>
        <SkeletonRows columnCount={columns.length} />
      </TableBody>
      <TableFooter>
        <TableRow>
          <TableCell className={noHoverStyle}>
            <Skeleton />
          </TableCell>
        </TableRow>
      </TableFooter>
    </Table>
  );
};

export default DataTableClientSkeleton;
