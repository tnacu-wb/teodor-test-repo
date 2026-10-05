import {
  Button,
  Flex,
  StyleProps,
  Table,
  TableContainer,
  TableContainerProps,
  Tbody,
  Th,
  Thead,
  Tr,
} from '@chakra-ui/react';
import { ScreenSizeValues, PageName } from '@whitbread-eos/api';
import { formatDataTestId } from '@whitbread-eos/utils';
import { useEffect, useState } from 'react';

import type { TableListRow as TableListRowType } from './';
import {
  headerResultsTextStyle,
  headerResultsTextRedesignStyle,
  headerTableStyle,
  loadMoreStyles,
  resultContainerStyle,
  tableStyle,
} from './';
import TableListRowComponent from './TableListRow.component';

export interface TableListColumn {
  key: string;
  title: string;
  hideOnMobile?: boolean;
  render?: (
    row: TableListRowType,
    rowIndex: number,
    rowCollapse?: {
      isExpanded: boolean;
      onExpand: (rowIndex: number, shouldCollapse: boolean) => void;
    }
  ) => React.ReactNode | string | number;
  width?: string;
}

export interface TableListRow {
  [key: string]: string | number | null | undefined;
}

interface Props {
  dataTestIdPrefix?: string;
  columns: TableListColumn[];
  rows: TableListRow[];
  isExpandable?: boolean;
  expandBtnText?: string;
  collapseBtnText?: string;
  screenSize?: ScreenSizeValues;
  loadMoreText?: string;
  onLoadMore?: () => void;
  isLoadingMore?: boolean;
  renderExpandedContent?: (row: TableListRow) => React.ReactNode | undefined;
  containerStyles?: TableContainerProps;
  rowStyles?: StyleProps;
  externalHeaderStyles?: StyleProps;
  rowHoverStyles?: StyleProps;
  handleRowClicked?: (row: TableListRow) => void;
  isFixedLayout?: boolean;
  pageName?: string;
  isBookingHistoryRedesignPIAndBBEnabled?: boolean;
}

// TableList - used by BookingHistory (PI/BB/PIB) and also for Company List (CCUI)
export default function TableList({
  dataTestIdPrefix = '',
  columns,
  rows,
  isExpandable = true,
  expandBtnText = 'View',
  collapseBtnText = 'Close',
  renderExpandedContent,
  loadMoreText = '',
  onLoadMore,
  isLoadingMore = false,
  screenSize,
  containerStyles,
  externalHeaderStyles,
  rowStyles,
  handleRowClicked,
  rowHoverStyles,
  isFixedLayout = true,
  pageName,
  isBookingHistoryRedesignPIAndBBEnabled,
}: Readonly<Props>) {
  const [expandedRowIndex, setExpandedRowIndex] = useState<number | null>(null);

  const isBookingHistoryRedesign =
    isBookingHistoryRedesignPIAndBBEnabled && pageName === PageName.DASHBOARD;

  const fixedLayout = isBookingHistoryRedesign ? false : isFixedLayout;

  // control table layout width styles
  const tableStyling = tableStyle(fixedLayout);

  useEffect(() => {
    rows.length === 1 ? setExpandedRowIndex(0) : setExpandedRowIndex(null);
  }, [rows.length]);

  const handleExpand = (index: number, shouldCollapse: boolean) => {
    if (!renderExpandedContent) {
      return;
    }
    setExpandedRowIndex(shouldCollapse ? null : index);
  };

  const tableResultsHeadingStyle = isBookingHistoryRedesign
    ? headerResultsTextRedesignStyle
    : headerResultsTextStyle;

  return (
    <TableContainer
      data-testid={formatDataTestId(dataTestIdPrefix, 'Table-Container')}
      {...resultContainerStyle}
      {...containerStyles}
    >
      <Table size="sm" style={tableStyling}>
        <Thead {...headerTableStyle}>
          <Tr>
            {columns.map((column: TableListColumn) => {
              return screenSize?.isLessThanSm && column?.hideOnMobile ? null : (
                <Th
                  key={column.key}
                  {...{ ...tableResultsHeadingStyle, ...externalHeaderStyles }}
                  data-testid={formatDataTestId(dataTestIdPrefix, `TableHeader-${column.key}`)}
                >
                  {column.title}
                </Th>
              );
            })}
            {isExpandable && !isBookingHistoryRedesign && (
              <Th
                key={'expand'}
                {...{ ...tableResultsHeadingStyle, ...externalHeaderStyles }}
                data-testid={formatDataTestId(dataTestIdPrefix, `TableHeader-expand`)}
              />
            )}
          </Tr>
        </Thead>
        <Tbody>
          {rows.map((row: TableListRow, rowIndex: number) => {
            return (
              <TableListRowComponent
                key={row.bookingReference}
                row={row}
                rowIndex={rowIndex}
                columns={columns}
                dataTestIdPrefix={dataTestIdPrefix}
                isExpandable={isExpandable}
                expandBtnText={expandBtnText}
                collapseBtnText={collapseBtnText}
                screenSize={screenSize}
                isExpanded={expandedRowIndex === rowIndex}
                onExpand={handleExpand}
                renderExpandedContent={renderExpandedContent}
                rowStyles={rowStyles}
                rowHoverStyles={rowHoverStyles}
                handleRowClicked={handleRowClicked}
                isBookingHistoryRedesign={isBookingHistoryRedesign}
              />
            );
          })}
        </Tbody>
      </Table>
      {onLoadMore && loadMoreText && (
        <Flex direction="column" justify="center">
          <Button
            variant="tertiary"
            size="sm"
            onClick={onLoadMore}
            data-testid={formatDataTestId(dataTestIdPrefix, 'Table-LoadMore')}
            isLoading={isLoadingMore}
            {...loadMoreStyles}
          >
            {loadMoreText}
          </Button>
        </Flex>
      )}
    </TableContainer>
  );
}
