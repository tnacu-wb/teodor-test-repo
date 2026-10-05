import {
  Table,
  TableContainer,
  Tbody,
  Th,
  Thead,
  Tr,
  Td,
  StyleProps,
  TableColumnHeaderProps,
  Flex,
  Box,
  PopoverContentProps,
  BoxProps,
  TableContainerProps,
  useDisclosure,
  HStack,
} from '@chakra-ui/react';
import { ChevronDown24, ChevronUp24, Icon, Info, Notification } from '@whitbread-eos/atoms';
import { TableFilter } from '@whitbread-eos/molecules';
import { formatDataTestId } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useLayoutEffect, useMemo, useRef, useState } from 'react';

import { TABLE_FIELDS } from './tableFields.enum';

export interface ChangeLogTableColumn {
  key: string;
  title: string;
  isFilterable: boolean;
  filterOptions?: string[];
  clampOverflowingText?: boolean;
  render?: (row: ChangeLogTableRow) => string;
}

export interface ChangeLogTableRow {
  [key: string]: string;
}

interface TableFilters {
  [key: string]: string[];
}

interface Props {
  columns: ChangeLogTableColumn[];
  rows: ChangeLogTableRow[];
}

interface ExpandableTextProps {
  value: string;
  startingHeight: number;
  postFixtestId?: string;
}

export default function ChangeLogTable({ columns, rows }: Readonly<Props>) {
  const { t } = useTranslation();

  const [appliedFilters, setAppliedFilters] = useState<TableFilters>(() => {
    return columns
      .filter((col) => col.isFilterable)
      .reduce((obj, item: ChangeLogTableColumn) => {
        return {
          ...obj,
          [item.key]: [],
        };
      }, {});
  });

  const columnsWithFilterData = useMemo(() => {
    return columns.map((column) => {
      if (!column.isFilterable) {
        return { ...column };
      }

      const filterOptions = rows.reduce((acc: string[], current) => {
        const value = current[column.key] ?? '';
        if (value && !acc.includes(value)) {
          return [...acc, value];
        }
        return [...acc];
      }, []);

      return { ...column, filterOptions };
    });
  }, [columns, rows]);

  const filteredRows = useMemo(() => {
    return rows.filter((row) => {
      // iterate through each filter key and check if the row can be displayed
      return Object.keys(appliedFilters)
        .filter((key) => appliedFilters[key]?.length)
        .every((key) => {
          const rowValue = row[key] ?? '';
          return rowValue && appliedFilters[key].includes(rowValue);
        });
    });
  }, [appliedFilters, rows]);

  const handleFiltersApplied = (selectedFilters: string[], filterKey: string) => {
    if (appliedFilters[filterKey] !== undefined) {
      setAppliedFilters((current) => {
        return { ...current, [filterKey]: selectedFilters };
      });
    }
  };

  const filterChevron = (isPopoverOpen: boolean, columnKey: string) => {
    const chevronSvg = isPopoverOpen ? <ChevronUp24 /> : <ChevronDown24 />;
    return (
      <Box as="button" data-testid={`FilterExxpand-${columnKey}`}>
        <Icon svg={chevronSvg} />
      </Box>
    );
  };

  const tableHeaderRow = (
    <Tr>
      {columnsWithFilterData.map((column: ChangeLogTableColumn) => {
        return (
          <Th key={column.key} {...{ ...cellStyles, ...tableHeaderCellStyles }}>
            <Flex alignItems="center" gap={2}>
              {column.title}
              {column.isFilterable && (
                <TableFilter
                  popoverTrigger={filterChevron}
                  filterKey={column.key}
                  filterOptions={column.filterOptions ?? []}
                  selectedFilters={appliedFilters[column.key] ?? []}
                  onApplyFilters={handleFiltersApplied}
                  externalStyles={{ contentStyles }}
                />
              )}
            </Flex>
          </Th>
        );
      })}
    </Tr>
  );

  const createTableCell = (
    column: ChangeLogTableColumn,
    row: ChangeLogTableRow,
    rowIndex: number
  ) => {
    const cellValue = column.render ? column.render(row) : row[column.key];

    return (
      <Td key={column.key} {...tableCellStyles(column.key)}>
        {column.clampOverflowingText ? (
          <ExpandableText
            value={cellValue}
            startingHeight={48}
            postFixtestId={`col-${column.key}-row-${rowIndex}`}
          />
        ) : (
          cellValue
        )}
      </Td>
    );
  };

  return (
    <>
      <TableContainer {...tableContainerStyles}>
        <Table size="sm">
          <Thead {...headerTableStyle}>{tableHeaderRow}</Thead>
          <Tbody>
            {filteredRows.map((row: ChangeLogTableRow, rowIndex: number) => {
              return (
                <Tr key={rowIndex}>
                  {columnsWithFilterData.map((column: ChangeLogTableColumn) =>
                    createTableCell(column, row, rowIndex)
                  )}
                </Tr>
              );
            })}
          </Tbody>
        </Table>
      </TableContainer>
      {!!rows.length && !filteredRows.length && (
        <Notification
          maxWidth="full"
          variant="info"
          status="info"
          description={t('ccui.changeLogModal.noResults')}
          svg={<Info />}
          wrapperStyles={notificationStyles}
        />
      )}
    </>
  );
}

const ExpandableText = ({ value, startingHeight, postFixtestId = '' }: ExpandableTextProps) => {
  const { isOpen, onToggle } = useDisclosure();
  const [hasOverflow, setHasOverflow] = useState<boolean>(false);
  const [contentHeight, setContentHeight] = useState<number>(startingHeight);
  const contentRef = useRef<HTMLDivElement>(null);

  useLayoutEffect(() => {
    const updateContentHeight = () => {
      if (contentRef.current) {
        const { scrollHeight } = contentRef.current;

        setContentHeight(Math.min(scrollHeight, startingHeight));
        setHasOverflow(scrollHeight > startingHeight);
      }
    };
    window.addEventListener('resize', updateContentHeight);
    updateContentHeight();

    return () => {
      window.removeEventListener('resize', updateContentHeight);
    };
  }, [contentRef, startingHeight, value]);

  return (
    <HStack
      overflow="hidden"
      height={isOpen ? 'fit-content' : `${contentHeight}px`}
      alignItems="flex-start"
    >
      <Box ref={contentRef}>{value}</Box>
      {hasOverflow && (
        <Box
          as="button"
          onClick={onToggle}
          data-testid={formatDataTestId('Expand-Collapse-Chevron', postFixtestId)}
        >
          <Icon svg={isOpen ? <ChevronUp24 /> : <ChevronDown24 />} />
        </Box>
      )}
    </HStack>
  );
};

const headerTableStyle: StyleProps = {
  backgroundColor: 'lightGrey5',
};

const cellStyles = {
  borderBottom: 'var(--chakra-borders-1px)',
  borderColor: 'var(--chakra-colors-lightGrey2)',
  color: 'darkGrey1',
  fontWeight: 'normal',
  fontSize: {
    mobile: 'sm',
    md: 'md',
  },
  lineHeight: {
    mobile: '2',
    sm: '3',
  },
  padding: {
    mobile: 'var(--chakra-space-10) var(--chakra-space-1) var(--chakra-space-6)',
    xs: 'var(--chakra-space-8) var(--chakra-space-1) var(--chakra-space-6)',
    sm: 'var(--chakra-space-12) var(--chakra-space-1) var(--chakra-space-6)',
    md: 'var(--chakra-space-12) var(--chakra-space-4) var(--chakra-space-6)',
    lg: 'var(--chakra-space-6) var(--chakra-space-2) var(--chakra-space-6) var(--chakra-space-6)',
    xl: 'var(--chakra-space-6) var(--chakra-space-4) var(--chakra-space-6) var(--chakra-space-6)',
  },
  verticalAlign: 'top',

  _first: {
    paddingLeft: {
      lg: 'var(--chakra-space-10)',
      xl: 'var(--chakra-space-5xl)',
    },
  },
  _last: {
    paddingRight: {
      lg: ' var(--chakra-space-10)',
      xl: ' var(--chakra-space-5xl)',
    },
  },
};

const tableCellStyles = (columnKey: string) => {
  const cellWidthStyles =
    columnKey === TABLE_FIELDS.ACTION_DESCRIPTION
      ? {
          width: `33%`,
        }
      : {};

  return { ...cellStyles, ...cellWidthStyles };
};

const tableContainerStyles: TableContainerProps = {
  whiteSpace: 'normal',
};

const tableHeaderCellStyles: TableColumnHeaderProps = {
  color: 'darkGrey1',
  fontWeight: 'semibold',
  fontSize: {
    lg: 'lg',
  },
  textTransform: 'none',
  fontFamily: 'header',
  letterSpacing: 'initial',
};

const contentStyles: PopoverContentProps = {
  width: {
    mobile: 'full',
    lg: '365px',
  },
  height: {
    mobile: 'full',
    lg: '536px',
  },
};

const notificationStyles: BoxProps = {
  width: 'auto',
  margin: {
    mobile: 'var(--chakra-space-10) var(--chakra-space-2)',
    lg: 'var(--chakra-space-10) var(--chakra-space-10)',
    xl: 'var(--chakra-space-10) var(--chakra-space-5xl)',
  },
};
