import { Table } from '@tanstack/react-table';
import { render, screen, fireEvent, renderHook } from '@testing-library/react';
import React from 'react';

import {
  DownloadButton,
  SortIcon,
  StatusCell,
  DesktopTable,
  HeaderSection,
  MobileCards,
  PaginationSection,
  getNextPageIndex,
  getDownloadButtonLabel,
  usePromoBatchColumns,
  getStickyStyles,
  formatEligibilityByRegion,
  EligibilityCell,
  EligibilityHeader,
} from './PromoBatchesTableComponents';
import * as promoTableContext from './usePromoBatchesTableContext';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  usePromoTranslation: () => ({
    downloadTitle: 'Download',
    generatingCodePlaceholder: 'Generating code…',
    awaitingFirstBatchMessage: 'Awaiting first batch…',
    batchFailedStatus: 'Failed',
    firstBatchInfoMessage: 'Awaiting first batch…',
    batchSuccessMessage: 'Success',
    batchProcessingMessage: 'Processing',
    batchErrorMessage: 'Error',
    codeExpiredMessage: 'Batch expired',
    campaignNameLabel: 'Campaign',
    createdAtTitle: 'Created',
    requestedByTitle: 'Requested By',
    promoCodeTableHeader: 'Promo Code',
    channelStatusLabel: 'Status',
    amountTitle: 'Amount',
    promoBatchesTitle: 'Promotion batches',
    generateNewBatchText: 'Generate new batch',
    noActiveBatchesMessage: 'No active batches',
  }),
}));

jest.mock('@whitbread-eos/atoms', () => ({
  Button: ({ children, isDisabled, onClick, ...props }: any) => (
    <button disabled={isDisabled} onClick={onClick} {...props}>
      {children}
    </button>
  ),
  LoadingSpinner: ({ loadingText }: any) => <div>{loadingText}</div>,
  Tooltip: ({ children }: any) => <div>{children}</div>,
}));

jest.mock('../../common', () => ({
  Pagination: ({ totalPages, onPageChange }: any) => (
    <div>
      {Array.from({ length: totalPages }).map((_, i) => (
        <button key={i} data-testid={`page-${i + 1}`} onClick={() => onPageChange(i + 1)}>
          {i + 1}
        </button>
      ))}
    </div>
  ),
}));

const mockSetBatchId = jest.fn();

jest.mock('./usePromoBatchesTableContext', () => ({
  usePromoTableContext: () => ({
    setBatchId: mockSetBatchId,
  }),
}));

const baseColumns = [{ accessorKey: 'campaign', header: 'Campaign' }];

const createMockTable = (rows: any[] = [], columns: any[] = baseColumns) => {
  return {
    getHeaderGroups: () => [
      {
        id: 'header-group-0',
        headers: columns.map((col, idx) => ({
          id: col.id || col.accessorKey || `col-${idx}`,
          column: {
            id: col.id || col.accessorKey || `col-${idx}`,
            columnDef: col,
            getSize: () => col.size || 150,
            getIsPinned: () => false,
            getStart: () => 0,
            getAfter: () => 0,
          },
          isPlaceholder: false,
          getContext: () => ({}),
        })),
      },
    ],
    getAllColumns: () =>
      columns.map((col, idx) => ({
        id: col.id || col.accessorKey || `col-${idx}`,
        columnDef: col,
        getSize: () => col.size || 150,
      })),
    getRowModel: () => ({
      rows: rows.map((row, index) => ({
        id: String(index),
        original: row,
        getVisibleCells: () =>
          columns.map((col, idx) => ({
            id: `cell-${index}-${idx}`,
            column: {
              id: col.id || col.accessorKey || `col-${idx}`,
              columnDef: col,
              getSize: () => col.size || 150,
              getIsPinned: () => false,
              getStart: () => 0,
              getAfter: () => 0,
            },
            getContext: () => ({
              row: { original: row },
            }),
          })),
      })),
    }),
  } as unknown as Table<any>;
};

const mockTable = createMockTable([
  {
    campaign: 'C1',
    created: '2024-01-01',
    requestedBy: 'User A',
    promoCode: 'PCODE',
    amount: 100,
    status: 'Complete',
    batchId: 'B123',
    downloaded: false,
    maxRedemptionLimit: 3,
  },
]);

describe('DownloadButton', () => {
  test('shows Download when status = Complete', () => {
    render(<DownloadButton status="Complete" batchId="B123" downloadStatus={false} />);
    expect(screen.getByText('Download')).toBeInTheDocument();
    expect(screen.getByRole('button')).not.toBeDisabled();
  });

  test('shows Generating code when status = Running', () => {
    render(<DownloadButton status="Running" batchId="B123" downloadStatus={false} />);
    expect(screen.getByText(/Generating code/i)).toBeInTheDocument();
  });

  test('shows Awaiting first batch on any other status', () => {
    render(<DownloadButton status="Pending" batchId="B123" downloadStatus={false} />);
    expect(screen.getByText(/Awaiting first batch/i)).toBeInTheDocument();
  });

  test('triggers setBatchId when clicked on complete status', () => {
    mockSetBatchId.mockClear();
    render(<DownloadButton status="Complete" batchId="B123" downloadStatus={false} />);
    fireEvent.click(screen.getByRole('button'));
    expect(mockSetBatchId).toHaveBeenCalledWith('B123');
  });

  test('does not trigger setBatchId when downloadStatus is true', () => {
    const setBatchIdMock = jest.fn();
    jest
      .spyOn(promoTableContext, 'usePromoTableContext')
      .mockReturnValue({ setBatchId: setBatchIdMock } as any);
    render(<DownloadButton status="Complete" batchId="B123" downloadStatus={true} />);
    fireEvent.click(screen.getByRole('button'));
    expect(setBatchIdMock).not.toHaveBeenCalled();
  });

  test('does not trigger click action when status is Running', () => {
    const setBatchIdMock = jest.fn();
    jest
      .spyOn(promoTableContext, 'usePromoTableContext')
      .mockReturnValue({ setBatchId: setBatchIdMock } as any);
    render(<DownloadButton status="Running" batchId="B123" downloadStatus={false} />);
    fireEvent.click(screen.getByRole('button'));
    expect(setBatchIdMock).not.toHaveBeenCalled();
  });
});

describe('StatusCell', () => {
  const mockT = {
    batchSuccessMessage: 'Success',
    batchProcessingMessage: 'Processing',
    firstBatchInfoMessage: 'Awaiting first batch',
    batchErrorMessage: 'Failed',
    codeExpiredMessage: 'Batch expired',
  };

  test('renders Complete', () => {
    render(<StatusCell status="Complete" t={mockT} />);
    expect(screen.getByText('Complete')).toBeInTheDocument();
  });

  test('renders Failed', () => {
    render(<StatusCell status="Failed" t={mockT} />);
    expect(screen.getByText('Failed')).toBeInTheDocument();
  });

  test('renders Running', () => {
    render(<StatusCell status="Running" t={mockT} />);
    expect(screen.getByText('Running')).toBeInTheDocument();
  });
  test('StatusCell handles unknown status', () => {
    render(<StatusCell status="Unknown" t={mockT} />);
    expect(screen.getByText('Unknown')).toBeInTheDocument();
  });

  test('renders Expired status with tooltip', () => {
    render(<StatusCell status="Expired" t={mockT} />);

    expect(screen.getByText('Expired')).toBeInTheDocument();

    expect(screen.getByTestId('PromoBatchesTableComponents-status-icon')).toBeInTheDocument();
  });
});
describe('DesktopTable', () => {
  test('shows loading state', () => {
    render(
      <DesktopTable
        columns={baseColumns}
        table={mockTable}
        sorting={[]}
        setSorting={jest.fn()}
        loading={true}
      />
    );
    expect(
      screen.getByTestId('PromoBatchesTableComponents-desktop-header-campaign')
    ).toBeInTheDocument();
    expect(screen.queryByTestId('PromoBatchesTableComponents-no-data')).not.toBeInTheDocument();
  });

  test('shows empty state', async () => {
    const emptyTable = createMockTable([]);
    render(
      <DesktopTable
        columns={baseColumns}
        table={emptyTable}
        sorting={[]}
        setSorting={jest.fn()}
        loading={false}
      />
    );
    expect(await screen.findByTestId('PromoBatchesTableComponents-no-data')).toBeInTheDocument();
  });

  test('renders table row data', () => {
    render(
      <DesktopTable
        columns={baseColumns}
        table={mockTable}
        sorting={[]}
        setSorting={jest.fn()}
        loading={false}
      />
    );
    expect(
      screen.getByTestId('PromoBatchesTableComponents-table-cell-campaign')
    ).toBeInTheDocument();
  });
});

describe('HeaderSection', () => {
  test('Generate button triggers handleToggle', () => {
    const toggle = jest.fn();
    render(
      <HeaderSection
        loadingTransition={false}
        handleToggle={toggle}
        sorting={[]}
        setSorting={jest.fn()}
      />
    );
    fireEvent.click(screen.getByTestId('PromoBatchesTableComponents-generate-button'));
    expect(toggle).toHaveBeenCalled();
  });

  test('Sort dropdown updates sorting', () => {
    const setSorting = jest.fn();
    render(
      <HeaderSection
        loadingTransition={false}
        handleToggle={jest.fn()}
        sorting={[]}
        setSorting={setSorting}
      />
    );
    fireEvent.change(screen.getByTestId('PromoBatchesTableComponents-mobile-sort'), {
      target: { value: 'campaign:asc' },
    });
    expect(setSorting).toHaveBeenCalledWith([{ id: 'campaign', desc: false }]);
  });
  test('clearing sort resets sorting', () => {
    const setSorting = jest.fn();
    render(
      <HeaderSection
        loadingTransition={false}
        handleToggle={jest.fn()}
        sorting={[{ id: 'campaign', desc: true }]}
        setSorting={setSorting}
      />
    );
    fireEvent.change(screen.getByTestId('PromoBatchesTableComponents-mobile-sort'), {
      target: { value: '' },
    });
    expect(setSorting).toHaveBeenCalledWith([]);
  });
});

describe('MobileCards', () => {
  const item = {
    batchId: 'PRB',
    campaign: 'C1',
    created: '2025-01-01',
    requestedBy: 'A',
    promoCode: 'PC',
    amount: 100,
    status: 'Complete',
    downloaded: false,
  };
  test('renders mobile card data', () => {
    render(<MobileCards data={[item]} />);
    expect(screen.getByText('C1')).toBeInTheDocument();
    expect(screen.getByText('A')).toBeInTheDocument();
    expect(screen.getByText('PC')).toBeInTheDocument();
  });
});

describe('PaginationSection', () => {
  test('renders nothing when totalRows <= pageSize', () => {
    const { container } = render(
      <PaginationSection totalRows={5} pageSize={5} pageIndex={0} handlePageChange={jest.fn()} />
    );
    expect(container.firstChild).toBeNull();
  });

  test('renders pagination when multiple pages exist', () => {
    render(
      <PaginationSection totalRows={20} pageSize={5} pageIndex={0} handlePageChange={jest.fn()} />
    );
    expect(screen.getByText('4')).toBeInTheDocument();
  });
  test('pagination calls handlePageChange', () => {
    const handler = jest.fn();
    render(
      <PaginationSection totalRows={20} pageSize={5} pageIndex={0} handlePageChange={handler} />
    );
    fireEvent.click(screen.getByTestId('page-2'));
    expect(handler).toHaveBeenCalledWith(2);
  });
});

describe('SortIcon', () => {
  test('renders unsorted icon', () => {
    render(<SortIcon isSorted={false} isDesc={false} />);
    expect(
      screen.getByTestId('PromoBatchesTableComponents-sort-icon-unsorted')
    ).toBeInTheDocument();
  });
  test('renders desc icon', () => {
    render(<SortIcon isSorted={true} isDesc={true} />);
    expect(screen.getByTestId('PromoBatchesTableComponents-sort-icon-desc')).toBeInTheDocument();
  });
  test('renders asc icon', () => {
    render(<SortIcon isSorted={true} isDesc={false} />);
    expect(screen.getByTestId('PromoBatchesTableComponents-sort-icon-asc')).toBeInTheDocument();
  });
});

describe('getNextPageIndex', () => {
  const pageSize = 5;

  test('object updater', () => {
    expect(getNextPageIndex(0, pageSize, { pageIndex: 3, pageSize })).toBe(3);
  });

  test('function updater increments page', () => {
    const updater = ({ pageIndex, pageSize }: any) => ({
      pageIndex: pageIndex + 2,
      pageSize,
    });

    expect(getNextPageIndex(1, pageSize, updater)).toBe(3);
  });

  test('function updater decrements page', () => {
    const updater = ({ pageIndex, pageSize }: any) => ({
      pageIndex: pageIndex - 1,
      pageSize,
    });

    expect(getNextPageIndex(4, pageSize, updater)).toBe(3);
  });
});
describe('getDownloadButtonLabel', () => {
  const t: any = {
    downloadTitle: 'Download',
    generatingCodePlaceholder: 'Generating…',
    awaitingFirstBatchMessage: 'Pending…',
    batchFailedStatus: 'Failed…',
  };

  test('returns correct labels for all statuses', () => {
    expect(getDownloadButtonLabel('Complete', t)).toBe('Download');
    expect(getDownloadButtonLabel('Running', t)).toBe('Generating…');
    expect(getDownloadButtonLabel('Pending', t)).toBe('Pending…');
    expect(getDownloadButtonLabel('Failed', t)).toBe('Failed…');
  });
});

describe('DesktopTable sorting behaviour', () => {
  const customColumns = [{ id: 'campaign', header: 'Campaign', accessorKey: 'campaign' }];

  test('does nothing when only one row', () => {
    const setSorting = jest.fn();
    const singleRowTable = createMockTable([{ campaign: 'C1' }], customColumns);
    render(
      <DesktopTable
        columns={customColumns}
        table={singleRowTable}
        sorting={[]}
        setSorting={setSorting}
        loading={false}
      />
    );
    fireEvent.click(screen.getByText('Campaign'));
    expect(setSorting).not.toHaveBeenCalled();
  });

  test('first click sets desc=true', () => {
    const setSorting = jest.fn();
    const multiRowTable = createMockTable([{ campaign: 'C1' }, { campaign: 'C2' }], customColumns);
    render(
      <DesktopTable
        columns={customColumns}
        table={multiRowTable}
        sorting={[]}
        setSorting={setSorting}
        loading={false}
      />
    );
    fireEvent.click(screen.getByText('Campaign'));
    expect(setSorting).toHaveBeenCalledWith([{ id: 'campaign', desc: true }]);
  });

  test('second click toggles desc', () => {
    const setSorting = jest.fn();
    const sorting = [{ id: 'campaign', desc: true }];
    const multiRowTable = createMockTable([{ campaign: 'C1' }, { campaign: 'C2' }], customColumns);
    render(
      <DesktopTable
        columns={customColumns}
        table={multiRowTable}
        sorting={sorting}
        setSorting={setSorting}
        loading={false}
      />
    );
    fireEvent.click(screen.getByText('Campaign'));
    expect(setSorting).toHaveBeenCalledWith([{ id: 'campaign', desc: false }]);
  });
});

describe('getStickyStyles branch coverage', () => {
  test('handles column pinned to left', () => {
    const mockColumnLeft = {
      getIsPinned: () => 'left',
      getStart: () => 100,
      getAfter: () => 0,
    } as any;
    const styles = getStickyStyles('customCol', false, mockColumnLeft);
    expect(styles).toEqual({
      position: 'sticky',
      left: '100px',
      right: undefined,
      boxShadow: '2px 0 5px rgba(0, 0, 0, 0.05)',
    });
  });

  test('handles column pinned to right', () => {
    const mockColumnRight = {
      getIsPinned: () => 'right',
      getStart: () => 0,
      getAfter: () => 150,
    } as any;
    const styles = getStickyStyles('customCol', false, mockColumnRight);
    expect(styles).toEqual({
      position: 'sticky',
      left: undefined,
      right: '150px',
      boxShadow: '-2px 0 5px rgba(0, 0, 0, 0.05)',
    });
  });

  test('handles download column pinned by default without column parameter', () => {
    const styles = getStickyStyles('download', false, undefined);
    expect(styles).toEqual({
      position: 'sticky',
      left: undefined,
      right: '0px',
      boxShadow: '-2px 0 5px rgba(0, 0, 0, 0.05)',
    });
  });

  test('handles unpinned non-header column', () => {
    const mockColumnUnpinned = {
      getIsPinned: () => false,
    } as any;
    const styles = getStickyStyles('campaign', false, mockColumnUnpinned);
    expect(styles).toEqual({});
  });
});

describe('usePromoBatchColumns', () => {
  type CellFn = (ctx: { row: { original: Record<string, any> } }) => React.ReactNode;

  test('renders StatusCell correctly via column cell renderer', () => {
    const { result } = renderHook(() => usePromoBatchColumns());
    const columns = result.current;
    const statusColumn = columns.find((col) => col.accessorKey === 'status');
    expect(statusColumn).toBeDefined();
    const mockContext = {
      row: {
        original: {
          status: 'Complete',
        },
      },
    };
    const cellRenderer = statusColumn?.cell as CellFn | undefined;
    const renderedCell = cellRenderer ? cellRenderer(mockContext) : null;
    render(renderedCell);
    expect(screen.getByText('Complete')).toBeInTheDocument();
    expect(screen.getByTestId('PromoBatchesTableComponents-status-cell')).toBeInTheDocument();
  });

  test('renders DownloadButton correctly via download column cell renderer', () => {
    const { result } = renderHook(() => usePromoBatchColumns());
    const columns = result.current;
    const downloadColumn = columns.find((col) => col.id === 'download');
    expect(downloadColumn).toBeDefined();
    const mockContext = {
      row: {
        original: {
          status: 'Complete',
          batchId: 'BATCH_999',
          downloaded: false,
        },
      },
    };
    const cellRenderer = downloadColumn?.cell as CellFn | undefined;
    const renderedCell = cellRenderer ? cellRenderer(mockContext) : null;
    render(renderedCell);
    expect(screen.getByText('Download')).toBeInTheDocument();
  });

  test('renders custom status cell from hook column definition', () => {
    const { result } = renderHook(() => usePromoBatchColumns());
    const realColumns = result.current;
    const mockRow = {
      campaign: 'Test Campaign',
      created: '2026-01-01',
      requestedBy: 'Admin',
      promoCode: 'PROMO2026',
      amount: 50,
      status: 'Complete',
      isGeneric: 'No',
      region: 'UK',
      platform: 'Web',
      channel: 'Direct',
      batchId: 'B123',
      downloaded: false,
      maxRedemptionLimit: 3,
    };
    const mockTableInstance = {
      getHeaderGroups: () => [
        {
          id: 'hg-0',
          headers: realColumns.map((col, idx) => ({
            id: col.id || col.accessorKey || `col-${idx}`,
            column: {
              id: col.id || col.accessorKey || `col-${idx}`,
              columnDef: col,
              getSize: () => 150,
              getIsPinned: () => false,
              getStart: () => 0,
              getAfter: () => 0,
            },
            isPlaceholder: false,
            getContext: () => ({}),
          })),
        },
      ],
      getAllColumns: () =>
        realColumns.map((col, idx) => ({
          id: col.id || col.accessorKey || `col-${idx}`,
          columnDef: col,
          getSize: () => 150,
        })),
      getRowModel: () => ({
        rows: [
          {
            id: '0',
            original: mockRow,
            getVisibleCells: () =>
              realColumns.map((col, idx) => ({
                id: `cell-0-${idx}`,
                column: {
                  id: col.id || col.accessorKey || `col-${idx}`,
                  columnDef: col,
                  getSize: () => 150,
                  getIsPinned: () => false,
                  getStart: () => 0,
                  getAfter: () => 0,
                },
                getContext: () => ({
                  row: { original: mockRow },
                }),
              })),
          },
        ],
      }),
    } as unknown as Table<any>;
    render(
      <DesktopTable
        columns={realColumns}
        table={mockTableInstance}
        sorting={[]}
        setSorting={jest.fn()}
        loading={false}
      />
    );
    expect(screen.getByTestId('PromoBatchesTableComponents-status-cell')).toBeInTheDocument();
  });
});

describe('formatEligibilityByRegion & EligibilityCell Coverage', () => {
  test('returns empty array when eligibilityList is empty', () => {
    const result = formatEligibilityByRegion([]);
    expect(result).toEqual([]);
  });

  test('skips items without region (covers !item.region branch)', () => {
    const mockData: any[] = [
      { region: null, channel: 'Web', platforms: ['iOS'] },
      { region: 'UK', channel: 'Direct', platforms: ['Android'] },
    ];
    const result = formatEligibilityByRegion(mockData);
    expect(result).toHaveLength(1);
    expect(result[0].region).toBe('UK');
  });

  test('handles array platforms, filtering out null/falsy values (covers Array.isArray & p && current.platforms)', () => {
    const mockData: any[] = [
      {
        region: 'UK',
        channel: 'Direct',
        platforms: ['iOS', null, 'Android', ''],
      },
    ];
    const result = formatEligibilityByRegion(mockData);
    expect(result[0].platforms).toBe('iOS, Android');
  });

  test('handles single non-array platform fallback (covers else if (item.platforms))', () => {
    const mockData: any[] = [
      {
        region: 'US',
        channel: 'Partner',
        platforms: 'WebPlatform',
        platform: 'WebPlatform',
      },
    ];
    const result = formatEligibilityByRegion(mockData);
    expect(result[0].platforms).toBe('WebPlatform');
  });

  test('aggregates multiple entries under the same region using Sets', () => {
    const mockData: any[] = [
      { region: 'UK', channel: 'Web', platforms: ['Desktop'] },
      { region: 'UK', channel: 'App', platforms: ['Mobile'] },
      { region: 'UK', channel: 'Web', platforms: ['Mobile'] }, // Duplicate channel & platform
    ];
    const result = formatEligibilityByRegion(mockData);
    expect(result).toHaveLength(1);
    expect(result[0]).toEqual({
      region: 'UK',
      channels: 'Web, App',
      platforms: 'Desktop, Mobile',
    });
  });

  test('EligibilityCell returns null when eligibilityData is empty or missing', () => {
    const { container: c1 } = render(<EligibilityCell eligibilityData={[]} />);
    expect(c1.firstChild).toBeNull();

    const { container: c2 } = render(<EligibilityCell eligibilityData={null as any} />);
    expect(c2.firstChild).toBeNull();
  });

  test('EligibilityCell renders aggregated eligibility data correctly', () => {
    const mockData: any[] = [{ region: 'UK', channel: 'Direct', platforms: ['iOS', 'Android'] }];
    render(<EligibilityCell eligibilityData={mockData} />);
    expect(screen.getByText('UK')).toBeInTheDocument();
    expect(screen.getByText('iOS, Android')).toBeInTheDocument();
    expect(screen.getByText('Direct')).toBeInTheDocument();
  });

  test('EligibilityHeader renders localized subheaders', () => {
    const mockT = {
      eligibilityLabel: 'Eligibility',
      regionLabel: 'Region Header',
      platformLabel: 'Platform Header',
      channelLabel: 'Channel Header',
    };
    render(<EligibilityHeader t={mockT} />);
    expect(screen.getByText('Eligibility')).toBeInTheDocument();
    expect(screen.getByText('Region Header')).toBeInTheDocument();
    expect(screen.getByText('Platform Header')).toBeInTheDocument();
    expect(screen.getByText('Channel Header')).toBeInTheDocument();
  });
});
