import { Box, Flex, Table, Thead, Tr, Th, Td, Text, Select } from '@chakra-ui/react';
import {
  ColumnDef,
  SortingState,
  flexRender,
  Table as ReactTable,
  Cell,
  Row,
  Header,
  Column,
} from '@tanstack/react-table';
import { BatchEligibility } from '@whitbread-eos/api/dist/types/graphql';
import { Button } from '@whitbread-eos/atoms';
import { usePromoTranslation } from '@whitbread-eos/utils';
import { ArrowUp, ArrowDown, DownloadIcon, Info, Tag } from 'lucide-react';
import dynamic from 'next/dynamic';
import React, { useEffect, CSSProperties } from 'react';

import { Pagination } from '../../common';
import { styles } from './styles';
import { PromoBatch, DesktopTablePropsType, PaginationUpdater, UpdaterFn } from './types';
import { usePromoTableContext } from './usePromoBatchesTableContext';

const Tooltip = dynamic(
  async () => {
    const { Tooltip } = await import('@whitbread-eos/atoms');
    return { default: Tooltip };
  },
  {
    ssr: false,
  }
);

const baseDataTestId = 'PromoBatchesTableComponents';

export function getDownloadButtonLabel(
  status: string,
  t: ReturnType<typeof usePromoTranslation>
): string {
  const labelMap: Record<string, string> = {
    Complete: t.downloadTitle,
    Running: t.generatingCodePlaceholder,
    Pending: t.awaitingFirstBatchMessage,
    Failed: t.batchFailedStatus,
    Expired: t.downloadTitle,
  };

  return labelMap[status];
}

export const DownloadButton = ({
  status,
  batchId,
  downloadStatus,
}: {
  status: string;
  batchId: string;
  downloadStatus: boolean;
}) => {
  const t = usePromoTranslation();
  const { setBatchId } = usePromoTableContext();

  const label = getDownloadButtonLabel(status, t);
  const isComplete = status === 'Complete' || status === 'Expired';

  const handleShowDownloadModal = () => {
    setBatchId(batchId);
  };

  return (
    <Button
      variant={isComplete ? 'secondary' : 'primary'}
      isDisabled={!isComplete || downloadStatus || status === 'Expired'}
      sx={isComplete ? styles.downloadButton : styles.disabledButton}
      onClick={handleShowDownloadModal}
    >
      {isComplete && <DownloadIcon />}
      {label}
    </Button>
  );
};

export const SortIcon = ({ isSorted, isDesc }: { isSorted: boolean; isDesc: boolean }) => {
  if (!isSorted)
    return <ArrowDown size={12} data-testid={`${baseDataTestId}-sort-icon-unsorted`} />;
  return isDesc ? (
    <ArrowDown size={12} data-testid={`${baseDataTestId}-sort-icon-desc`} />
  ) : (
    <ArrowUp size={12} data-testid={`${baseDataTestId}-sort-icon-asc`} />
  );
};

const tooltipStyle = {
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'flex-start',
  gap: '8px',
  padding: '8px',
  borderRadius: '4px',
} as const;

const statusVariantMap: Record<string, string> = {
  Complete: 'inlineSuccess',
  Failed: 'inlineError',
  Running: 'inlineInfo',
  Pending: 'inlineInfo',
  Expired: 'inlineInfo',
};

const statusColorMap: Record<string, string> = {
  Complete: 'success',
  Failed: 'error',
  Running: 'info',
  Pending: 'info',
  Expired: 'info',
};

const renderStatusTooltip = (title: string, description: string, variant: string) => {
  return (
    <Tooltip
      title={title}
      description={description}
      variant={variant}
      placement="bottom-start"
      closeOnClick={false}
      ml="-1.1rem"
      colorScheme="primary"
      alertElementStyles={tooltipStyle}
    >
      <Info
        size={18}
        style={{
          width: 18,
          height: 18,
          flexShrink: 0,
        }}
        data-testid={`${baseDataTestId}-status-icon`}
      />
    </Tooltip>
  );
};

export const StatusCell = ({ status, t }: { status: string; t: Record<string, string> }) => {
  const color = statusColorMap[status] || 'info';
  const variant = statusVariantMap[status] || 'standard';

  const labelMap: Record<string, string> = {
    Complete: t.batchSuccessMessage,
    Running: t.batchProcessingMessage,
    Pending: t.firstBatchInfoMessage,
    Failed: t.batchErrorMessage,
    Expired: t.codeExpiredMessage,
  };

  return (
    <Flex sx={{ ...styles.statusFlex, color }} data-testid={`${baseDataTestId}-status-cell`}>
      <Text sx={{ ...styles.statusText, color }}>{status}</Text>
      {renderStatusTooltip(status, labelMap[status], variant)}
    </Flex>
  );
};

export const getStickyStyles = (
  columnId: string,
  isHeader: boolean,
  column?: Column<PromoBatch, unknown>
): CSSProperties => {
  const isPinned = columnId === 'download' || (column && column.getIsPinned());

  if (isPinned) {
    const isPinnedLeft = column?.getIsPinned() === 'left';
    return {
      position: 'sticky',
      left: isPinnedLeft ? `${column?.getStart('left') ?? 0}px` : undefined,
      right: !isPinnedLeft ? `${column?.getAfter('right') ?? 0}px` : undefined,
      boxShadow: isPinnedLeft ? '2px 0 5px rgba(0, 0, 0, 0.05)' : '-2px 0 5px rgba(0, 0, 0, 0.05)',
    };
  }

  if (isHeader) {
    return {
      position: 'sticky',
      top: 0,
    };
  }

  return {};
};

export const formatEligibilityByRegion = (eligibilityList: BatchEligibility[]) => {
  const regionMap = new Map<string, { channels: Set<string>; platforms: Set<string> }>();

  eligibilityList.forEach((item) => {
    if (!item.region) return;

    if (!regionMap.has(item.region)) {
      regionMap.set(item.region, {
        channels: new Set(),
        platforms: new Set(),
      });
    }

    const current = regionMap.get(item.region)!;

    if (item.channel) {
      current.channels.add(item.channel);
    }

    if (Array.isArray(item.platforms)) {
      item.platforms.forEach((p) => p && current.platforms.add(p));
    } else if (item.platforms) {
      current.platforms.add((item as any).platform);
    }
  });

  return Array.from(regionMap.entries()).map(([region, data]) => ({
    region,
    channels: Array.from(data.channels).join(', '),
    platforms: Array.from(data.platforms).join(', '),
  }));
};

export const EligibilityCell = ({ eligibilityData }: { eligibilityData: BatchEligibility[] }) => {
  if (!eligibilityData || !eligibilityData.length) return null;
  const eligibilityList = formatEligibilityByRegion(eligibilityData);

  return (
    <Box sx={styles.eligibilityCellWrapper}>
      {eligibilityList.map((item) => (
        <Box key={item.region} sx={styles.eligibilityRowGrid}>
          <Text as="span" fontWeight="medium" sx={styles.eligibilityRowText}>
            {item.region}
          </Text>
          <Text as="span" sx={styles.eligibilityRowText}>
            {item.platforms}
          </Text>
          <Text as="span" sx={styles.eligibilityRowText}>
            {item.channels}
          </Text>
        </Box>
      ))}
    </Box>
  );
};

export const EligibilityHeader = ({ t }: { t: Record<string, string> }) => {
  return (
    <Box sx={styles.eligibilityHeaderWrapper}>
      <Text sx={styles.eligibilityTitle}>{t.eligibilityLabel}</Text>
      <Box sx={styles.eligibilitySubHeaderGrid}>
        <Text as="span">{t.regionLabel}</Text>
        <Text as="span">{t.platformLabel}</Text>
        <Text as="span">{t.channelLabel}</Text>
      </Box>
    </Box>
  );
};

export const usePromoBatchColumns = (): ColumnDef<PromoBatch>[] => {
  const t = usePromoTranslation();
  return React.useMemo(
    () => [
      { accessorKey: 'campaign', header: t.campaignNameLabel, size: 180 },
      { accessorKey: 'created', header: t.createdAtTitle, size: 200 },
      { accessorKey: 'requestedBy', header: t.requestedByTitle, size: 280 },
      { accessorKey: 'promoCode', header: t.promoCodeTableHeader, size: 180 },
      { accessorKey: 'amount', header: t.amountTitle, size: 80 },
      {
        accessorKey: 'status',
        header: t.channelStatusLabel,
        cell: (ctx) => <StatusCell status={ctx.row.original.status} t={t} />,
        size: 100,
      },
      { accessorKey: 'isGeneric', header: t.isGenericLabel, size: 80 },
      { accessorKey: 'maxRedemptionLimit', header: t.maximumRedemptionsLabel, size: 80 },
      {
        id: 'eligibility',
        header: () => <EligibilityHeader t={t} />,
        cell: (ctx) => <EligibilityCell eligibilityData={ctx.row.original.eligibility} />,
        size: 320,
      },
      {
        id: 'download',
        header: t.downloadTitle,
        cell: (ctx) => (
          <DownloadButton
            status={ctx.row.original.status}
            batchId={ctx.row.original.batchId}
            downloadStatus={ctx.row.original.downloaded}
          />
        ),
        size: 210,
        enablePinning: true,
      },
    ],
    [t]
  );
};

const CenteredTagCircle = () => {
  const { noActiveBatchesMessage } = usePromoTranslation();
  return (
    <Box sx={styles.nodata.wrapper} data-testid={`${baseDataTestId}-no-data`}>
      <Box sx={styles.nodata.circle}>
        <Tag size={24} color="#00798E" data-testid={`${baseDataTestId}-no-data-icon`} />
      </Box>
      <Text sx={styles.nodata.text}>{noActiveBatchesMessage}</Text>
    </Box>
  );
};

const renderTableBody = ({
  loading,
  sorting,
  table,
}: {
  loading: boolean;
  sorting: SortingState;
  table: ReactTable<PromoBatch>;
}) => {
  const rows = table.getRowModel()?.rows || [];
  const totalColumnsCount = table.getAllColumns().length || 8;

  if (rows.length === 0 && !loading) {
    return (
      <Tr>
        <Td colSpan={totalColumnsCount} sx={{ textAlign: 'center', py: 10 }}>
          <Flex align="center" justify="center" gap="8px" color="error">
            <CenteredTagCircle />
          </Flex>
        </Td>
      </Tr>
    );
  }

  return rows.map((row: Row<PromoBatch>, index: number) => {
    const rowBg = index % 2 === 0 ? 'gray.50' : 'white';

    return (
      <Tr key={row.id} sx={{ ...styles.rowStyle, bg: rowBg }}>
        {row.getVisibleCells().map((cell: Cell<PromoBatch, unknown>) => {
          const sortedColumn = sorting.find((s) => s.id === cell.column.id);
          const isSorted = Boolean(sortedColumn);
          const stickyStyle = getStickyStyles(cell.column.id, false, cell.column);

          return (
            <Td
              width={`${cell.column.getSize()}px`}
              maxWidth={`${cell.column.getSize()}px`}
              key={cell.id}
              sx={{
                whiteSpace: 'nowrap',
                bg: rowBg,
                ...(isSorted ? styles.sortByField : {}),
                ...stickyStyle,
              }}
              data-testid={`${baseDataTestId}-table-cell-${cell.column.id}`}
            >
              {flexRender(cell.column.columnDef.cell, cell.getContext())}
            </Td>
          );
        })}
      </Tr>
    );
  });
};

export const DesktopTable = ({ table, sorting, setSorting, loading }: DesktopTablePropsType) => {
  const [isClient, setIsClient] = React.useState(false);

  useEffect(() => {
    setIsClient(true);
  }, []);

  const hasMultipleRows = table?.getRowModel()?.rows?.length > 1;

  return (
    <Box sx={styles.desktopTableWrapper}>
      <Table variant="simple" width="100%" sx={{ tableLayout: 'auto' }}>
        <Thead>
          {table.getHeaderGroups().map((headerGroup) => (
            <Tr key={headerGroup.id}>
              {headerGroup.headers.map((header: Header<PromoBatch, unknown>) => {
                const accessorKey =
                  'accessorKey' in header.column.columnDef &&
                  typeof header.column.columnDef.accessorKey === 'string'
                    ? header.column.columnDef.accessorKey
                    : undefined;
                const sortState = sorting.find((s) => s.id === accessorKey);
                const isSorted = Boolean(sortState);
                const isDesc = sortState?.desc === false;
                const icon = <SortIcon isSorted={isSorted} isDesc={isDesc} />;
                const stickyStyle = getStickyStyles(header.column.id, true, header.column);

                return (
                  <Th
                    key={header.id}
                    cursor={hasMultipleRows && accessorKey ? 'pointer' : 'default'}
                    onClick={() => {
                      if (!accessorKey || !hasMultipleRows) return;
                      const existingSort = sorting.find((s) => s.id === accessorKey);
                      if (existingSort) {
                        setSorting([{ id: accessorKey, desc: !existingSort.desc }]);
                        return;
                      }
                      setSorting([{ id: accessorKey, desc: true }]);
                    }}
                    sx={{
                      ...styles.tableHeader,
                      bg: 'white',
                      ...(isSorted ? styles.sortByField : {}),
                      ...stickyStyle,
                    }}
                    data-testid={`${baseDataTestId}-desktop-header-${accessorKey || header.id}`}
                  >
                    <Flex align="center" gap="4px">
                      {header.isPlaceholder
                        ? null
                        : flexRender(header.column.columnDef.header, header.getContext())}
                      {accessorKey && <div key={accessorKey}>{icon}</div>}
                    </Flex>
                  </Th>
                );
              })}
            </Tr>
          ))}
        </Thead>
        {isClient && renderTableBody({ loading, sorting, table })}
      </Table>
    </Box>
  );
};

export const MobileCard = ({ item }: { item: PromoBatch }) => {
  const t = usePromoTranslation();
  return (
    <Box sx={styles.mobileCard} data-testid={`${baseDataTestId}-mobile-card`}>
      <Box>
        <Box sx={styles.mobileRow}>
          <Text sx={styles.mobileLabel}>{t.campaignNameLabel}</Text>
          <Text sx={styles.mobileDesc} className="mobileDesc">
            {item.campaign}
          </Text>
        </Box>
        <Box sx={styles.mobileRow}>
          <Text sx={styles.mobileLabel}>{t.createdAtTitle} </Text>
          <Text sx={styles.mobileDesc}>{item.created}</Text>
        </Box>
        <Box sx={styles.mobileRow}>
          <Text sx={styles.mobileLabel}>{t.requestedByTitle} </Text>
          <Text sx={styles.mobileDesc}>{item.requestedBy}</Text>
        </Box>
        <Box sx={styles.mobileRow}>
          <Text sx={styles.mobileLabel}>{t.promoCodeTableHeader} </Text>
          <Text sx={styles.mobileDesc}>{item.promoCode}</Text>
        </Box>
        <Box sx={styles.mobileRow}>
          <Text sx={styles.mobileLabel}>{t.channelStatusLabel}</Text>
          <StatusCell status={item.status} t={t} />
        </Box>
      </Box>

      <Box sx={styles.downloadButtonWrapper}>
        <DownloadButton
          status={item.status}
          batchId={item.batchId}
          downloadStatus={item.downloaded}
        />
      </Box>
    </Box>
  );
};

export const MobileCards = ({ data }: { data: PromoBatch[] }) => (
  <Box sx={styles.mobileCardWrapper} data-testid={`${baseDataTestId}-mobile-cards-wrapper`}>
    {data?.map((item, index) => (
      <MobileCard key={`${item.promoCode}-${index}`} item={item} />
    ))}
  </Box>
);

export const HeaderSection = ({
  loadingTransition,
  handleToggle,
  sorting,
  setSorting,
}: {
  loadingTransition: boolean;
  handleToggle: () => void;
  sorting: SortingState;
  setSorting: React.Dispatch<React.SetStateAction<SortingState>>;
}) => {
  const t = usePromoTranslation();

  const handleSortChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const value = e.target.value;
    if (!value) return setSorting([]);
    const [id, direction] = value.split(':');
    setSorting([{ id, desc: direction === 'desc' }]);
  };

  const currentSortValue = (() => {
    if (!sorting[0]) return '';
    const { id, desc } = sorting[0];
    return `${id}:${desc ? 'desc' : 'asc'}`;
  })();

  const sortOptions = [
    { key: 'campaign', label: t.campaignNameLabel },
    { key: 'created', label: t.createdAtTitle },
    { key: 'requestedBy', label: t.requestedByTitle },
    { key: 'promoCode', label: t.promoCodeTableHeader },
    { key: 'status', label: t.channelStatusLabel },
    { key: 'amount', label: t.amountTitle },
  ];

  return (
    <Box sx={styles.sticky}>
      <Flex sx={styles.headerRow}>
        <Text sx={styles.sectionTitle}>{t.promoBatchesTitle}</Text>
        <Button
          variant="primary"
          sx={styles.generateBatchBtn}
          onClick={handleToggle}
          isDisabled={loadingTransition}
          data-testid={`${baseDataTestId}-generate-button`}
        >
          {t.generateNewBatchText}
        </Button>
      </Flex>

      {/* Mobile Sort */}
      <Box sx={styles.mobileSortWrapper}>
        <Text sx={styles.mobileSortText}>Sort by</Text>
        <Select
          placeholder="Sort by"
          sx={styles.mobileSortSelect}
          value={currentSortValue}
          onChange={handleSortChange}
          data-testid={`${baseDataTestId}-mobile-sort`}
        >
          {sortOptions.flatMap(({ key, label }) => [
            <option key={`${key}-asc`} value={`${key}:asc`}>
              {label} ↑
            </option>,
            <option key={`${key}-desc`} value={`${key}:desc`}>
              {label} ↓
            </option>,
          ])}
        </Select>
      </Box>
    </Box>
  );
};

export const TableSection = ({
  columns,
  table,
  sorting,
  setSorting,
  data,
  loading,
}: {
  columns: ColumnDef<PromoBatch>[];
  table: ReactTable<PromoBatch>;
  sorting: SortingState;
  setSorting: React.Dispatch<React.SetStateAction<SortingState>>;
  data: PromoBatch[];
  loading: boolean;
}) => {
  return (
    <>
      <DesktopTable
        columns={columns}
        table={table}
        sorting={sorting}
        setSorting={setSorting}
        loading={loading}
      />
      <MobileCards data={data} />
    </>
  );
};

export const PaginationSection = ({
  totalRows,
  pageSize,
  pageIndex,
  handlePageChange,
}: {
  totalRows: number;
  pageSize: number;
  pageIndex: number;
  handlePageChange: (page: number) => void;
}) => {
  if (totalRows <= pageSize) return null;
  return (
    <Box sx={styles.paginationWrapper} data-testid={`${baseDataTestId}-pagination-section`}>
      <Pagination
        currentPage={pageIndex + 1}
        totalPages={Math.ceil(totalRows / pageSize)}
        onPageChange={handlePageChange}
        isDisabled={false}
      />
    </Box>
  );
};

export const getNextPageIndex = (
  oldPageIndex: number,
  pageSize: number,
  updater: UpdaterFn | PaginationUpdater
): number => {
  if (typeof updater === 'function') {
    return updater({ pageIndex: oldPageIndex, pageSize }).pageIndex;
  }
  return updater.pageIndex;
};
