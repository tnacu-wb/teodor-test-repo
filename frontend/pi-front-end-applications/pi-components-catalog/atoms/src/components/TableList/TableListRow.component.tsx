import { Box, Link, StyleProps, Td, Tr } from '@chakra-ui/react';
import { ScreenSizeValues } from '@whitbread-eos/api';
import { formatDataTestId } from '@whitbread-eos/utils';

import { ChevronDown, ChevronUp } from '../../assets/icons';
import type { TableListColumn, TableListRow as TableListRowType } from './';
import {
  cardRowStyle,
  cardWrapperStyle,
  linkStyles,
  resultRowCellStyle,
  resultRowCellRedesignStyle,
} from './';

interface Props {
  dataTestIdPrefix?: string;
  columns: TableListColumn[];
  row: TableListRowType;
  rowIndex: number;
  isExpandable: boolean;
  expandBtnText: string;
  collapseBtnText: string;
  screenSize?: ScreenSizeValues;
  isExpanded: boolean;
  onExpand: (rowIndex: number, shouldCollapse: boolean) => void;
  renderExpandedContent?: (row: TableListRowType) => React.ReactNode | undefined;
  rowStyles?: StyleProps;
  rowHoverStyles?: StyleProps;
  handleRowClicked?: (row: TableListRowType) => void;
  isBookingHistoryRedesign?: boolean;
}

export default function TableListRow({
  dataTestIdPrefix = '',
  columns,
  row,
  rowIndex,
  isExpandable,
  expandBtnText,
  collapseBtnText,
  screenSize,
  isExpanded,
  onExpand,
  renderExpandedContent,
  rowStyles,
  rowHoverStyles,
  handleRowClicked,
  isBookingHistoryRedesign,
}: Readonly<Props>) {
  const colSpan = screenSize?.isLessThanSm
    ? columns.length - columns.filter((col) => !!col?.hideOnMobile).length + 1
    : columns.length + 1;

  const rowCellStyle = isBookingHistoryRedesign
    ? resultRowCellRedesignStyle(isExpanded)
    : resultRowCellStyle(isExpanded);

  const displayExpandedValue = (
    expanded: string | React.ReactElement,
    collapsed: string | React.ReactElement
  ): string | React.ReactElement => {
    return isExpanded ? expanded : collapsed;
  };

  return (
    <>
      <Tr
        tabIndex={0}
        data-testid={formatDataTestId(dataTestIdPrefix, `Table-Row-${rowIndex}`)}
        key={rowIndex}
        onClick={
          isBookingHistoryRedesign
            ? () => onExpand(rowIndex, isExpanded)
            : () => handleRowClicked && handleRowClicked(row)
        }
        _hover={rowHoverStyles ?? {}}
        onKeyDown={(e) => {
          if (e.key === 'Enter' || e.key === ' ') {
            if (isBookingHistoryRedesign) {
              onExpand(rowIndex, isExpanded);
            } else if (handleRowClicked) {
              handleRowClicked(row);
            }
          }
        }}
        aria-label={displayExpandedValue('Collapse row', 'Expand row') as string}
      >
        {columns.map((column: TableListColumn, colIndex: number) => {
          return screenSize?.isLessThanSm && column?.hideOnMobile ? null : (
            <Td
              key={column.key}
              data-testid={formatDataTestId(dataTestIdPrefix, `Table-Cell-${rowIndex}-${colIndex}`)}
              {...{ ...rowCellStyle, ...rowStyles }}
              width={column.width}
            >
              {column?.render
                ? column.render(row, rowIndex, {
                    isExpanded,
                    onExpand,
                  })
                : (row[column?.key] ?? '')}
            </Td>
          );
        })}
        {isExpandable && !isBookingHistoryRedesign && (
          <Td
            key={'expand'}
            data-testid={formatDataTestId(
              dataTestIdPrefix,
              `Table-Cell-${rowIndex}-${displayExpandedValue('expanded', 'collapsed')}`
            )}
            {...{ ...rowCellStyle, ...rowStyles }}
          >
            <Link
              {...linkStyles}
              data-testid={formatDataTestId(
                dataTestIdPrefix,
                `Table-Cell-${rowIndex}-${displayExpandedValue('collapse', 'expand')}`
              )}
              onClick={() => onExpand(rowIndex, isExpanded)}
            >
              {/* expand/collpase link */}
              {!isBookingHistoryRedesign && displayExpandedValue(collapseBtnText, expandBtnText)}
              <Box ml="xmd">
                {displayExpandedValue(
                  <ChevronUp color="var(--chakra-colors-btnSecondaryEnabled)" />,
                  <ChevronDown color="var(--chakra-colors-btnSecondaryEnabled)" />
                )}
              </Box>
            </Link>
          </Td>
        )}
      </Tr>
      {/* Expanded Card here - when row clicked */}
      {isExpandable && isExpanded && (
        <Tr data-testid={formatDataTestId(dataTestIdPrefix, `Table-Row-expanded`)}>
          <Td
            colSpan={colSpan}
            {...cardRowStyle}
            data-testid={formatDataTestId(dataTestIdPrefix, `Table-Row-expanded-card`)}
          >
            <Box {...cardWrapperStyle}>{renderExpandedContent?.(row)}</Box>
          </Td>
        </Tr>
      )}
    </>
  );
}
