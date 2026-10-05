import {
  TableRow,
  TableCell,
  TableFooter,
  Pagination,
  PaginationContent,
  PaginationEllipsis,
  PaginationItem,
  PaginationButton,
  PaginationNext,
  PaginationPrevious,
} from '@whitbread-eos/atoms/ui';
import React from 'react';
import ultimatePagination, { ITEM_TYPES, PaginationModelItem } from 'ultimate-pagination';

interface DataTableSimplePaginationProps {
  baseTestId: string;
  columns: any[];
  page: number;
  totalPages: number;
  icons: Record<string, string>;
  onPageChange?: (page: number) => void;
  onPrevChange?: () => void;
  onNextChange?: () => void;
  mobile?: boolean;
  className?: string;
}

const DataTablePagesPagination: React.FC<DataTableSimplePaginationProps> = ({
  baseTestId,
  columns,
  page,
  totalPages,
  icons,
  onPageChange,
  onPrevChange,
  onNextChange,
  mobile,
  className,
}) => {
  const paginationModel = ultimatePagination.getPaginationModel({
    currentPage: page,
    totalPages,
    boundaryPagesRange: mobile ? 0 : 1,
    siblingPagesRange: 2,
    hideEllipsis: mobile,
    hidePreviousAndNextPageLinks: false,
    hideFirstAndLastPageLinks: true,
  });

  if (page === 1) {
    paginationModel.shift();
  }

  if (page === totalPages) {
    paginationModel.pop();
  }

  const renderPaginationItemContent = (item: PaginationModelItem) => {
    if (item.type === ITEM_TYPES.PAGE) {
      return (
        <PaginationButton isActive={item.isActive} onClick={() => onPageChange?.(item.value)}>
          {item.value}
        </PaginationButton>
      );
    }

    if (item.type === ITEM_TYPES.PREVIOUS_PAGE_LINK) {
      return <PaginationPrevious icon={icons['icon.chevron.left.purple']} onClick={onPrevChange} />;
    }

    if (item.type === ITEM_TYPES.NEXT_PAGE_LINK) {
      return <PaginationNext icon={icons['icon.chevron.right.purple']} onClick={onNextChange} />;
    }

    if (item.type === ITEM_TYPES.ELLIPSIS) {
      return <PaginationEllipsis />;
    }
  };

  return (
    <TableFooter data-testid={`${baseTestId}-Pagination`} className={'h-[3.75rem]'}>
      <TableRow>
        <TableCell colSpan={columns.length} style={{ textAlign: 'center', borderTop: 'none' }}>
          <Pagination className={className}>
            <PaginationContent className={paginationContentStyle}>
              {paginationModel.map((item) => (
                <PaginationItem data-testid={`${baseTestId}-link`} key={item.key}>
                  {renderPaginationItemContent(item)}
                </PaginationItem>
              ))}
            </PaginationContent>
          </Pagination>
        </TableCell>
      </TableRow>
    </TableFooter>
  );
};

export default DataTablePagesPagination;

const paginationContentStyle = 'mobile:w-full mobile:justify-center';
