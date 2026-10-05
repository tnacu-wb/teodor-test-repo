import { renderHook } from '@testing-library/react';
import { useQueryRequest } from '@whitbread-eos/utils';

import { useBatchById, usePromoBatchAsDownload } from './use-batch-by-id';

jest.mock('@whitbread-eos/utils', () => ({
  useQueryRequest: jest.fn(),
}));

const mockUseQueryRequest = useQueryRequest as jest.Mock;

describe('useServiceWorker', () => {
  it('returns batch data when response is valid', () => {
    mockUseQueryRequest.mockReturnValue({
      data: {
        getBatchById: {
          batchId: '123',
          operaPromoCode: 'PROMO1',
        },
      },
      isLoading: false,
      isError: false,
      isFetching: false,
      refetch: jest.fn(),
    });

    const { result } = renderHook(() => useBatchById('123', { enabled: true }));

    expect(result.current.batchData).toEqual({
      batchId: '123',
      operaPromoCode: 'PROMO1',
    });
    expect(result.current.isError).toBe(false);
  });

  it('returns null batchData when response is invalid', () => {
    mockUseQueryRequest.mockReturnValue({
      data: { wrongKey: {} },
      isLoading: false,
      isError: false,
      isFetching: false,
      refetch: jest.fn(),
    });

    const { result } = renderHook(() => useBatchById('123', { enabled: true }));

    expect(result.current.batchData).toBeNull();
    expect(result.current.isError).toBe(true);
  });

  it('returns error when queryError is true', () => {
    mockUseQueryRequest.mockReturnValue({
      data: {
        getBatchById: { batchId: '123' },
      },
      isLoading: false,
      isError: true,
      isFetching: false,
      refetch: jest.fn(),
    });

    const { result } = renderHook(() => useBatchById('123', { enabled: true }));

    expect(result.current.isError).toBe(true);
  });

  it('passes enabled flag to useQueryRequest', () => {
    mockUseQueryRequest.mockReturnValue({
      data: null,
      isLoading: false,
      isError: false,
      isFetching: false,
      refetch: jest.fn(),
    });

    renderHook(() => useBatchById('123', { enabled: false }));

    expect(mockUseQueryRequest).toHaveBeenCalledWith(
      ['getBatchById', '123'],
      expect.anything(),
      { batchId: '123' },
      { enabled: false }
    );
  });

  it('returns true when markPromoBatchAsDownloaded is true', () => {
    mockUseQueryRequest.mockReturnValue({
      data: { markPromoBatchAsDownloaded: true },
      isLoading: false,
      isError: false,
      isFetching: false,
      refetch: jest.fn(),
    });

    const { result } = renderHook(() => usePromoBatchAsDownload('123', { enabled: true }));

    expect(result.current.data).toBe(true);
    expect(result.current.isError).toBe(false);
  });

  it('returns error when response is invalid', () => {
    mockUseQueryRequest.mockReturnValue({
      data: { markPromoBatchAsDownloaded: 'yes' },
      isLoading: false,
      isError: false,
      isFetching: false,
      refetch: jest.fn(),
    });

    const { result } = renderHook(() => usePromoBatchAsDownload('123', { enabled: true }));

    expect(result.current.data).toBeNull();
    expect(result.current.isError).toBe(true);
  });

  it('returns error when queryError is true', () => {
    mockUseQueryRequest.mockReturnValue({
      data: { markPromoBatchAsDownloaded: true },
      isLoading: false,
      isError: true,
      isFetching: false,
      refetch: jest.fn(),
    });

    const { result } = renderHook(() => usePromoBatchAsDownload('123', { enabled: true }));

    expect(result.current.isError).toBe(true);
  });

  it('passes enabled flag to promo download query', () => {
    mockUseQueryRequest.mockReturnValue({
      data: null,
      isLoading: false,
      isError: false,
      isFetching: false,
      refetch: jest.fn(),
    });

    renderHook(() => usePromoBatchAsDownload('123', { enabled: false }));

    expect(mockUseQueryRequest).toHaveBeenCalledWith(
      ['getPromoBatchAsDownload', '123'],
      expect.anything(),
      { batchId: '123' },
      { enabled: false }
    );
  });

  it('returns null data when response is valid but value is false', () => {
    mockUseQueryRequest.mockReturnValue({
      data: { markPromoBatchAsDownloaded: false },
      isLoading: false,
      isError: false,
      isFetching: false,
      refetch: jest.fn(),
    });

    const { result } = renderHook(() => usePromoBatchAsDownload('123', { enabled: true }));

    expect(result.current.data).toBeNull();
    expect(result.current.isError).toBe(false);
  });

  it('returns null batchData when getBatchById is null but response shape is valid', () => {
    mockUseQueryRequest.mockReturnValue({
      data: {
        getBatchById: null,
      },
      isLoading: false,
      isError: false,
      isFetching: false,
      refetch: jest.fn(),
    });

    const { result } = renderHook(() => useBatchById('123', { enabled: true }));

    expect(result.current.batchData).toBeNull();
    expect(result.current.isError).toBe(true);
  });
});
