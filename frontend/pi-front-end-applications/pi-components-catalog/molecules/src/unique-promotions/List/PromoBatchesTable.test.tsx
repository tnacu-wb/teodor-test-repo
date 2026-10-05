import { render, screen, fireEvent, waitFor } from '@testing-library/react';

import { downloadFileFromUrl } from '../PasswordModal/common';
import PromoBatchesTable from './PromoBatchesTable';
import { PromoBatch } from './types';

const mockReplace = jest.fn().mockResolvedValue({});

const mockUseRouter = jest.fn();

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const mockToast = jest.fn();

jest.mock('@chakra-ui/react', () => {
  const actual = jest.requireActual('@chakra-ui/react');
  return {
    ...actual,
    useToast: () => mockToast,
  };
});

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: () => ({ country: 'uk', language: 'en' }),
}));

jest.mock('next-i18next', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
}));
jest.mock('../PasswordModal/common', () => ({
  downloadFileFromUrl: jest.fn(),
}));
jest.mock('@whitbread-eos/atoms', () => ({
  Button: ({ children, isDisabled, ...props }: any) => (
    <button disabled={isDisabled} {...props}>
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
        <button
          key={i}
          data-testid={`pagination-page-${i + 1}`}
          onClick={() => onPageChange(i + 1)}
        >
          {i + 1}
        </button>
      ))}
    </div>
  ),
}));
let mockCapturedOnPaginationChange: ((updater: any) => void) | null = null;
jest.mock('./PromoBatchesTableComponents', () => {
  const actual = jest.requireActual('./PromoBatchesTableComponents');
  return {
    ...actual,
    HeaderSection: ({ handleToggle, setSorting }: any) => (
      <div>
        <button onClick={handleToggle}>promotions.generate.newBatch</button>

        <select
          data-testid="PromoBatchesTableComponents-mobile-sort"
          onChange={(e) => {
            const [id, direction] = e.target.value.split(':');
            setSorting([{ id, desc: direction === 'desc' }]);
          }}
        >
          <option value="campaign:asc">Campaign asc</option>
          <option value="campaign:desc">Campaign desc</option>
        </select>
      </div>
    ),

    TableSection: ({ data }: any) => (
      <div>
        <table>
          <thead>
            <tr>
              <th>promotions.campaign.name</th>
              <th>promotions.created</th>
              <th>promotions.requestedBy</th>
              <th>promotions.table.promoCode.header</th>
              <th>promotions.amount</th>
              <th>promotions.channel.status</th>
              <th>promotions.table.download</th>
            </tr>
          </thead>
          <tbody>
            {data.length === 0 ? (
              <tr>
                <td>promotions.no.activeBatches</td>
              </tr>
            ) : (
              data.map((row: any) => (
                <tr key={row.batchId}>
                  <td>{row.campaign}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
        <button
          data-testid="trigger-updater-func"
          onClick={() => {
            if (mockCapturedOnPaginationChange) {
              mockCapturedOnPaginationChange((old: any) => ({ ...old, pageIndex: 1 }));
            }
          }}
        >
          Trigger Functional Updater
        </button>
        <button
          data-testid="trigger-updater-value"
          onClick={() => {
            if (mockCapturedOnPaginationChange) {
              mockCapturedOnPaginationChange({ pageIndex: 2, pageSize: 5 });
            }
          }}
        >
          Trigger Value Updater
        </button>
      </div>
    ),

    PaginationSection: ({ totalRows, pageSize, handlePageChange }: any) => {
      const totalPages = Math.ceil(totalRows / pageSize);
      return (
        <div>
          {Array.from({ length: totalPages }).map((_, i) => (
            <button
              key={i}
              data-testid={`pagination-page-${i + 1}`}
              onClick={() => handlePageChange(i + 1)}
            >
              {i + 1}
            </button>
          ))}
        </div>
      );
    },

    usePromoBatchColumns: () => [],
    getNextPageIndex: (old: number, pageSize: number, updater: any) =>
      actual.getNextPageIndex(old, pageSize, updater),
  };
});
jest.mock('@tanstack/react-table', () => {
  const actual = jest.requireActual('@tanstack/react-table');
  return {
    ...actual,
    useReactTable: (options: any) => {
      mockCapturedOnPaginationChange = options.onPaginationChange;
      return actual.useReactTable(options);
    },
  };
});

const mockUseBatchById = jest.fn();
const mockUsePromoBatchAsDownload = jest.fn();

jest.mock('../hooks/use-batch-by-id', () => ({
  useBatchById: (...args: any[]) => mockUseBatchById(...args),
  usePromoBatchAsDownload: (...args: any[]) => mockUsePromoBatchAsDownload(...args),
}));
jest.mock('../PasswordModal', () => ({
  __esModule: true,
  default: () => <div data-testid="password-modal" />,
}));
jest.mock('../Notes', () => ({
  PromoNotes: () => <div data-testid="promo-notes" />,
}));

const mockUsePromoBatches = jest.fn();
jest.mock('../hooks', () => ({
  usePromoBatches: (args: any) => mockUsePromoBatches(args),
}));

const mockData: PromoBatch[] = [
  {
    batchId: 'BATCH1',
    campaign: 'Summer sale',
    created: '2025-12-01',
    requestedBy: 'User A',
    promoCode: 'PROMO1',
    amount: 100,
    status: 'Complete',
    downloaded: false,
  },
  {
    batchId: 'BATCH2',
    campaign: 'Winter sale',
    created: '2025-12-02',
    requestedBy: 'User B',
    promoCode: 'PROMO2',
    amount: 200,
    status: 'Running',
    downloaded: false,
  },
];

describe('PromoBatchesTable', () => {
  beforeEach(() => {
    mockCapturedOnPaginationChange = null;
    mockUsePromoBatches.mockReset();
    mockReplace.mockReset();

    mockUseBatchById.mockReset();
    mockUsePromoBatchAsDownload.mockReset();

    (downloadFileFromUrl as jest.Mock).mockClear();

    mockUseBatchById.mockReturnValue({
      batchData: null,
      isLoading: false,
      isError: false,
    });

    mockUsePromoBatchAsDownload.mockReturnValue({
      data: null,
      isLoading: false,
      isError: false,
    });
    mockUseRouter.mockReturnValue({
      replace: mockReplace,
      isReady: true,
    });

    sessionStorage.clear();
    mockToast.mockClear();
  });

  test('renders container, header, table, and pagination', async () => {
    mockUsePromoBatches.mockReturnValue({
      data: mockData,
      total: mockData.length,
      isLoading: false,
      isError: false,
      refetch: jest.fn(),
    });
    render(<PromoBatchesTable />);
    expect(await screen.findByTestId('PromoBatchesTable-container')).toBeInTheDocument();
  });

  test('renders table headers correctly', async () => {
    mockUsePromoBatches.mockReturnValue({
      data: [],
      total: 0,
      isLoading: false,
      isError: false,
      refetch: jest.fn(),
    });
    render(<PromoBatchesTable />);
    const headers = await screen.findAllByRole('columnheader');
    expect(headers.map((h) => h.textContent)).toEqual([
      'promotions.campaign.name',
      'promotions.created',
      'promotions.requestedBy',
      'promotions.table.promoCode.header',
      'promotions.amount',
      'promotions.channel.status',
      'promotions.table.download',
    ]);
  });

  test('sort dropdown triggers sorting updates', async () => {
    mockUsePromoBatches.mockReturnValue({
      data: mockData,
      total: mockData.length,
      isLoading: false,
      isError: false,
      refetch: jest.fn(),
    });
    render(<PromoBatchesTable />);
    const sortSelect = await screen.findByTestId('PromoBatchesTableComponents-mobile-sort');

    fireEvent.change(sortSelect, { target: { value: 'campaign:asc' } });
    await waitFor(() =>
      expect(mockUsePromoBatches).toHaveBeenLastCalledWith(
        expect.objectContaining({ sorting: [{ id: 'campaign', desc: false }] })
      )
    );

    fireEvent.change(sortSelect, { target: { value: 'campaign:desc' } });
    await waitFor(() =>
      expect(mockUsePromoBatches).toHaveBeenLastCalledWith(
        expect.objectContaining({ sorting: [{ id: 'campaign', desc: true }] })
      )
    );
  });

  test('empty state when no data', async () => {
    mockUsePromoBatches.mockReturnValue({
      data: [],
      total: 0,
      isLoading: false,
      isError: false,
      refetch: jest.fn(),
    });
    render(<PromoBatchesTable />);
    expect(await screen.findByText(/promotions.no.activeBatches/i)).toBeInTheDocument();
  });

  test('generate new batch triggers route replace', async () => {
    mockUsePromoBatches.mockReturnValue({
      data: [],
      total: 0,
      isLoading: false,
      isError: false,
      refetch: jest.fn(),
    });
    render(<PromoBatchesTable />);
    const btn = await screen.findByRole('button', { name: /promotions.generate.newBatch/i });
    fireEvent.click(btn);
    expect(mockReplace).toHaveBeenCalled();
  });

  test('calls fetch with updated pageIndex when clicking pagination', async () => {
    mockUsePromoBatches.mockReturnValue({
      data: mockData,
      total: 10,
      isLoading: false,
      isError: false,
      refetch: jest.fn(),
    });
    render(<PromoBatchesTable />);
    const page2 = await screen.findByTestId('pagination-page-2');
    fireEvent.click(page2);
    await waitFor(() =>
      expect(mockUsePromoBatches).toHaveBeenLastCalledWith(
        expect.objectContaining({ pageIndex: 1, pageSize: 5 })
      )
    );
  });

  test('initial fetch params are correct', async () => {
    mockUsePromoBatches.mockReturnValue({
      data: [],
      total: 0,
      isLoading: false,
      isError: false,
      refetch: jest.fn(),
    });
    render(<PromoBatchesTable />);
    await waitFor(() =>
      expect(mockUsePromoBatches).toHaveBeenCalledWith({
        pageIndex: 0,
        pageSize: 5,
        sorting: [{ id: 'created', desc: true }],
      })
    );
  });

  test('keeps previous data visible while loading new data', async () => {
    mockUsePromoBatches
      .mockReturnValueOnce({
        data: mockData,
        total: mockData.length,
        isLoading: false,
        isError: false,
        refetch: jest.fn(),
      })
      .mockReturnValueOnce({
        data: [],
        total: 0,
        isLoading: true,
        isError: false,
        refetch: jest.fn(),
      });

    const { rerender } = render(<PromoBatchesTable />);

    const initialRows = await screen.findAllByText('Summer sale');
    expect(initialRows.length).toBeGreaterThan(0);

    rerender(<PromoBatchesTable />);

    const loadingRows = screen.getAllByText('Summer sale');
    expect(loadingRows.length).toBeGreaterThan(0);
  });

  test('enables promo batch download when batchId and password exist', async () => {
    mockUsePromoBatches.mockReturnValue({
      data: mockData,
      total: mockData.length,
      isLoading: false,
      isError: false,
      refetch: jest.fn(),
    });

    mockUseBatchById.mockReturnValue({
      batchData: {
        batchId: 'BATCH1',
        password: 'secret123',
      },
      isLoading: false,
      isError: false,
    });

    mockUsePromoBatchAsDownload.mockReturnValue({
      data: { downloadUrl: '/file.csv' },
      isLoading: false,
      isError: false,
    });

    render(<PromoBatchesTable />);

    await waitFor(() => expect(screen.getByTestId('password-modal')).toBeInTheDocument());
  });

  test('does not enable promo batch download when password is missing', async () => {
    mockUsePromoBatches.mockReturnValue({
      data: mockData,
      total: mockData.length,
      isLoading: false,
      isError: false,
      refetch: jest.fn(),
    });

    mockUseBatchById.mockReturnValue({
      batchData: {
        batchId: 'BATCH1',
        password: '',
      },
      isLoading: false,
      isError: false,
    });

    mockUsePromoBatchAsDownload.mockReturnValue({
      data: null,
      isLoading: false,
      isError: false,
    });

    render(<PromoBatchesTable />);

    await waitFor(() => {
      expect(mockUsePromoBatchAsDownload).toHaveBeenCalledWith(
        'BATCH1',
        expect.objectContaining({
          enabled: false,
        })
      );
    });
  });

  test('clears loading transition after navigation timeout', async () => {
    jest.useFakeTimers();

    mockUsePromoBatches.mockReturnValue({
      data: [],
      total: 0,
      isLoading: false,
      isError: false,
      refetch: jest.fn(),
    });

    render(<PromoBatchesTable />);

    const btn = await screen.findByRole('button', {
      name: /promotions.generate.newBatch/i,
    });

    fireEvent.click(btn);

    expect(mockReplace).toHaveBeenCalled();

    await Promise.resolve();

    jest.advanceTimersByTime(150);

    await waitFor(() => {
      expect(screen.queryByText(/loading/i)).not.toBeInTheDocument();
    });

    jest.useRealTimers();
  });

  test('calls refetch on mount', async () => {
    const refetch = jest.fn();

    mockUsePromoBatches.mockReturnValue({
      data: [],
      total: 0,
      isLoading: false,
      isError: false,
      refetch,
    });

    render(<PromoBatchesTable />);

    await waitFor(() => {
      expect(refetch).toHaveBeenCalled();
    });
  });

  test('does not open password modal when download data is null', async () => {
    mockUsePromoBatches.mockReturnValue({
      data: mockData,
      total: mockData.length,
      isLoading: false,
      isError: false,
      refetch: jest.fn(),
    });

    mockUseBatchById.mockReturnValue({
      batchData: {
        batchId: 'BATCH1',
        password: 'secret123',
      },
      isLoading: false,
      isError: false,
    });

    mockUsePromoBatchAsDownload.mockReturnValue({
      data: null,
      isLoading: false,
      isError: false,
    });

    render(<PromoBatchesTable />);

    await waitFor(() => {
      expect(mockUsePromoBatchAsDownload).toHaveBeenCalled();
    });
  });

  test('does not update previous data when data is empty', async () => {
    mockUsePromoBatches.mockReturnValue({
      data: [],
      total: 0,
      isLoading: false,
      isError: false,
      refetch: jest.fn(),
    });

    render(<PromoBatchesTable />);

    expect(await screen.findByText(/promotions.no.activeBatches/i)).toBeInTheDocument();
  });

  test('handleToggle does not execute finally when router.replace returns undefined', async () => {
    jest.useFakeTimers();
    (mockReplace as jest.Mock).mockReturnValueOnce(undefined);
    mockUsePromoBatches.mockReturnValue({
      data: [],
      total: 0,
      isLoading: false,
      isError: false,
      refetch: jest.fn(),
    });
    render(<PromoBatchesTable />);
    const btn = await screen.findByRole('button', {
      name: /promotions.generate.newBatch/i,
    });

    fireEvent.click(btn);
    expect(mockReplace).toHaveBeenCalled();
    jest.advanceTimersByTime(200);
    jest.useRealTimers();
  });

  test('downloads file when s3Key is available and conditions are met', async () => {
    mockUsePromoBatches.mockReturnValue({
      data: mockData,
      total: mockData.length,
      isLoading: false,
      isError: false,
      refetch: jest.fn(),
    });

    mockUseBatchById.mockReturnValue({
      batchData: {
        batchId: 'BATCH1',
        s3Key: 'https://example.com/file.zip',
        campaignName: 'Winter Campaign',
      },
      isLoading: false,
      isError: false,
    });

    mockUsePromoBatchAsDownload.mockReturnValue({
      data: {},
      isLoading: false,
      isError: false,
    });

    render(<PromoBatchesTable />);

    await waitFor(() => {
      expect(downloadFileFromUrl).toHaveBeenCalledWith(
        'https://example.com/file.zip',
        'Winter Campaign.zip'
      );
    });
  });

  test('uses fallback filename when campaignName is missing', async () => {
    mockUsePromoBatches.mockReturnValue({
      data: mockData,
      total: mockData.length,
      isLoading: false,
      isError: false,
      refetch: jest.fn(),
    });

    mockUseBatchById.mockReturnValue({
      batchData: {
        batchId: 'BATCH1',
        s3Key: 'https://example.com/file.zip',
        campaignName: '',
      },
      isLoading: false,
      isError: false,
    });

    mockUsePromoBatchAsDownload.mockReturnValue({
      data: {},
      isLoading: false,
      isError: false,
    });

    render(<PromoBatchesTable />);

    await waitFor(() => {
      expect(downloadFileFromUrl).toHaveBeenCalledWith(
        'https://example.com/file.zip',
        'promo-batch.zip'
      );
    });
  });

  test('does not download file when s3Key is missing', async () => {
    mockUsePromoBatches.mockReturnValue({
      data: mockData,
      total: mockData.length,
      isLoading: false,
      isError: false,
      refetch: jest.fn(),
    });

    mockUseBatchById.mockReturnValue({
      batchData: {
        batchId: 'BATCH1',
        campaignName: 'Test',
      },
      isLoading: false,
      isError: false,
    });

    mockUsePromoBatchAsDownload.mockReturnValue({
      data: {},
      isLoading: false,
      isError: false,
    });

    render(<PromoBatchesTable />);

    await waitFor(() => {
      expect(downloadFileFromUrl).not.toHaveBeenCalled();
    });
  });

  test('does not download file when promoBatchAsDownload is loading', async () => {
    mockUsePromoBatches.mockReturnValue({
      data: mockData,
      total: mockData.length,
      isLoading: false,
      isError: false,
      refetch: jest.fn(),
    });

    mockUseBatchById.mockReturnValue({
      batchData: {
        batchId: 'BATCH1',
        s3Key: 'https://example.com/file.zip',
        campaignName: 'Campaign',
      },
      isLoading: false,
      isError: false,
    });

    mockUsePromoBatchAsDownload.mockReturnValue({
      data: {},
      isLoading: true,
      isError: false,
    });

    render(<PromoBatchesTable />);

    await waitFor(() => {
      expect(downloadFileFromUrl).not.toHaveBeenCalled();
    });
  });

  test('does not download file when promoBatchAsDownload has error', async () => {
    mockUsePromoBatches.mockReturnValue({
      data: mockData,
      total: mockData.length,
      isLoading: false,
      isError: false,
      refetch: jest.fn(),
    });

    mockUseBatchById.mockReturnValue({
      batchData: {
        batchId: 'BATCH1',
        s3Key: 'https://example.com/file.zip',
        campaignName: 'Campaign',
      },
      isLoading: false,
      isError: false,
    });

    mockUsePromoBatchAsDownload.mockReturnValue({
      data: null,
      isLoading: false,
      isError: true,
    });

    render(<PromoBatchesTable />);

    await waitFor(() => {
      expect(downloadFileFromUrl).not.toHaveBeenCalled();
    });
  });

  test('downloads file only once even after rerender', async () => {
    mockUsePromoBatches.mockReturnValue({
      data: mockData,
      total: mockData.length,
      isLoading: false,
      isError: false,
      refetch: jest.fn(),
    });

    mockUseBatchById.mockReturnValue({
      batchData: {
        batchId: 'BATCH1',
        s3Key: 'https://example.com/file.zip',
        campaignName: 'Campaign',
      },
      isLoading: false,
      isError: false,
    });

    mockUsePromoBatchAsDownload.mockReturnValue({
      data: {},
      isLoading: false,
      isError: false,
    });

    const { rerender } = render(<PromoBatchesTable />);

    await waitFor(() => {
      expect(downloadFileFromUrl).toHaveBeenCalledTimes(1);
    });

    rerender(<PromoBatchesTable />);

    await waitFor(() => {
      expect(downloadFileFromUrl).toHaveBeenCalledTimes(1);
    });
  });
  test('shows success toast when batch created and status is Complete', async () => {
    sessionStorage.setItem('promoBatchCreated', 'true');

    mockUsePromoBatches.mockReturnValue({
      data: [mockData[0]],
      total: 1,
      isLoading: false,
      isError: false,
      refetch: jest.fn(),
    });

    render(<PromoBatchesTable />);

    await waitFor(() => {
      expect(mockToast).toHaveBeenCalledWith(
        expect.objectContaining({
          status: 'success',
        })
      );
    });
  });

  test('does not execute session effect when router is not ready', async () => {
    mockUseRouter.mockReturnValue({
      replace: mockReplace,
      isReady: false,
    });

    sessionStorage.setItem('promoBatchCreated', 'true');

    mockUsePromoBatches.mockReturnValue({
      data: [mockData[0]],
      total: 1,
      isLoading: false,
      isError: false,
      refetch: jest.fn(),
    });

    render(<PromoBatchesTable />);

    await waitFor(() => {
      expect(mockToast).not.toHaveBeenCalled();
    });
  });
  test('does not execute session effect when loading is true', async () => {
    sessionStorage.setItem('promoBatchCreated', 'true');

    mockUsePromoBatches.mockReturnValue({
      data: [mockData[0]],
      total: 1,
      isLoading: true,
      isError: false,
      refetch: jest.fn(),
    });

    render(<PromoBatchesTable />);

    await waitFor(() => {
      expect(mockToast).not.toHaveBeenCalled();
    });
  });
  test('triggers onPaginationChange callback with functional updater', async () => {
    mockUsePromoBatches.mockReturnValue({
      data: mockData,
      total: 20,
      isLoading: false,
      isError: false,
      refetch: jest.fn(),
    });

    render(<PromoBatchesTable />);

    const updaterBtn = await screen.findByTestId('trigger-updater-func');
    fireEvent.click(updaterBtn);

    await waitFor(() =>
      expect(mockUsePromoBatches).toHaveBeenLastCalledWith(
        expect.objectContaining({ pageIndex: 1, pageSize: 5 })
      )
    );
  });

  test('triggers onPaginationChange callback with primitive value updater', async () => {
    mockUsePromoBatches.mockReturnValue({
      data: mockData,
      total: 20,
      isLoading: false,
      isError: false,
      refetch: jest.fn(),
    });

    render(<PromoBatchesTable />);

    const directValueBtn = await screen.findByTestId('trigger-updater-value');
    fireEvent.click(directValueBtn);

    await waitFor(() =>
      expect(mockUsePromoBatches).toHaveBeenLastCalledWith(
        expect.objectContaining({ pageIndex: 2, pageSize: 5 })
      )
    );
  });
});
