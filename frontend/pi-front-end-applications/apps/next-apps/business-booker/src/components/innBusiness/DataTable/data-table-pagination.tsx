import {
  Pagination,
  PaginationContent,
  PaginationEllipsis,
  PaginationItem,
  PaginationButton,
  PaginationNext,
  PaginationPrevious,
  SearchParamLink,
} from '@whitbread-eos/atoms/ui';
import { getCommonIcons, getSearchParams } from '@whitbread-eos/utils/server';
import ultimatePagination, { ITEM_TYPES, PaginationModelItem } from 'ultimate-pagination';

export type DataTablePaginationProps = {
  pageIndex: number;
  totalPages: number;
  mobile?: boolean;
  className?: string;
};

export async function DataTablePagination({
  pageIndex,
  totalPages,
  mobile,
  className,
}: DataTablePaginationProps) {
  const baseDataTestId = 'DataTablePagination';
  const [icons, searchParams] = await Promise.all([getCommonIcons('en'), getSearchParams()]);

  const renderPaginationItemContent = (item: PaginationModelItem) => {
    if (item.type === ITEM_TYPES.PAGE) {
      return (
        <SearchParamLink name="pageIndex" value={item.value} searchParams={searchParams}>
          <PaginationButton isActive={item.isActive}>{item.value}</PaginationButton>
        </SearchParamLink>
      );
    }

    if (item.type === ITEM_TYPES.PREVIOUS_PAGE_LINK) {
      return (
        <SearchParamLink name="pageIndex" value={item.value} searchParams={searchParams}>
          <PaginationPrevious icon={icons['icon.chevron.left.purple']} />
        </SearchParamLink>
      );
    }

    if (item.type === ITEM_TYPES.NEXT_PAGE_LINK) {
      return (
        <SearchParamLink name="pageIndex" value={item.value} searchParams={searchParams}>
          <PaginationNext icon={icons['icon.chevron.right.purple']} />
        </SearchParamLink>
      );
    }

    if (item.type === ITEM_TYPES.ELLIPSIS) {
      return <PaginationEllipsis />;
    }
  };

  const paginationModel = ultimatePagination.getPaginationModel({
    currentPage: pageIndex,
    totalPages,
    boundaryPagesRange: mobile ? 0 : 1,
    siblingPagesRange: 2,
    hideEllipsis: mobile,
    hidePreviousAndNextPageLinks: false,
    hideFirstAndLastPageLinks: true,
  });

  if (pageIndex === 1) {
    paginationModel.shift();
  }

  if (pageIndex === totalPages) {
    paginationModel.pop();
  }

  return (
    <Pagination data-testid={baseDataTestId} className={className}>
      <PaginationContent className={paginationContentStyle}>
        {paginationModel.map((item) => (
          <PaginationItem data-testid={`${baseDataTestId}-link`} key={item.key}>
            {renderPaginationItemContent(item)}
          </PaginationItem>
        ))}
      </PaginationContent>
    </Pagination>
  );
}

const paginationContentStyle = 'mobile:w-full mobile:justify-center';
