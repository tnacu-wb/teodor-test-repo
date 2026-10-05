import { Skeleton, TableRow, TableCell } from '@whitbread-eos/atoms/ui';

import { DATA_TABLE_PAGE_SIZE } from './data-table';

type Props = {
  columnCount: number;
};

export function DataTableSkeleton({ columnCount }: Props) {
  return (
    <TableRow data-testid="DataTableSkeleton" className={rowStyle}>
      <TableCell colSpan={columnCount}>
        {[...Array(DATA_TABLE_PAGE_SIZE).keys()].map((key) => (
          <Skeleton key={key} className={skeletonStyle} />
        ))}
      </TableCell>
    </TableRow>
  );
}

const rowStyle = 'hover:bg-transparent';
const skeletonStyle = 'h-[31px] my-2';
