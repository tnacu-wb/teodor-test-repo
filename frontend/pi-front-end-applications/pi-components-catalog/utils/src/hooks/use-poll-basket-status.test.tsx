import { act, renderHook } from '@testing-library/react';
import { BASKET_STATUS } from '@whitbread-eos/api';

import usePollBasketStatus from './use-poll-basket-status';

jest.spyOn(global.console, 'warn').mockImplementation(() => ({}));

jest.useFakeTimers();

const mockBasketStatusResponse = (status = 'PAY_PENDING') => ({
  isLoading: false,
  isError: false,
  error: { message: '' },
  data: {
    basketStatus: {
      basketStatus: status,
      basketError:
        status === 'FAILED'
          ? {
              code: 'code',
            }
          : null,
    },
  },
});

const mockQueryRequest = jest.fn();
jest.mock('./use-request', () => ({
  ...jest.requireActual('./use-request'),
  useQueryRequest: () => mockQueryRequest(),
}));

const mockSpinnerConfig = [
  {
    order: '1',
    seconds: '2',
    text: 'One moment...',
  },
  {
    order: '2 ',
    seconds: '4',
    text: 'Hold tight we are booking',
  },
  {
    order: '3',
    seconds: '6',
    text: 'Sorry for the time. Please bear with us',
  },
];

describe('use-poll-basket-status custom hook', () => {
  beforeEach(() => {
    jest.resetModules();
  });

  it('should return the correct polling status to hosted component (PAY_PENDING)', () => {
    mockQueryRequest.mockImplementation(() => {
      return mockBasketStatusResponse('PAY_PENDING');
    });
    const { result } = renderHook(() => usePollBasketStatus('basket-reference', mockSpinnerConfig));

    expect(result.current.pollingInProgress).toEqual(true);
  });

  it('should return the correct polling status to hosted component (PROCESSING)', () => {
    mockQueryRequest.mockImplementation(() => {
      return mockBasketStatusResponse('PROCESSING');
    });
    const { result } = renderHook(() => usePollBasketStatus('basket-reference', mockSpinnerConfig));

    expect(result.current.pollingInProgress).toEqual(true);
  });

  it('should return the correct polling status to hosted component (COMPLETED)', () => {
    mockQueryRequest.mockImplementation(() => {
      return mockBasketStatusResponse('COMPLETED');
    });

    const { result } = renderHook(() => usePollBasketStatus('basket-reference', mockSpinnerConfig));

    expect(result.current.pollingInProgress).toEqual(false);
  });

  it('should return the correct polling status to hosted component (FAILED)', () => {
    mockQueryRequest.mockImplementation(() => {
      return mockBasketStatusResponse('FAILED');
    });

    const { result } = renderHook(() => usePollBasketStatus('basket-reference', mockSpinnerConfig));

    expect(result.current.pollingInProgress).toEqual(false);
  });

  it('should return error code to hosted component (FAILED)', () => {
    mockQueryRequest.mockImplementation(() => {
      return mockBasketStatusResponse('FAILED');
    });

    const { result } = renderHook(() => usePollBasketStatus('basket-reference', mockSpinnerConfig));

    expect(result.current.errorCode).toEqual('code');
  });

  it('should return the correct basket status to hosted component', () => {
    mockQueryRequest.mockImplementation(() => {
      return mockBasketStatusResponse();
    });

    const { result } = renderHook(() => usePollBasketStatus('basket-reference', mockSpinnerConfig));

    expect(result.current.basketStatus).toEqual('PAY_PENDING');
  });

  it('should return the spinner label 1 state basis time', () => {
    mockQueryRequest.mockImplementation(() => {
      return mockBasketStatusResponse();
    });

    const { result } = renderHook(() => usePollBasketStatus('basket-reference', mockSpinnerConfig));

    act(() => {
      jest.advanceTimersByTime(2000);
    });

    expect(result.current.dynamicSpinnerLabel).toContain('One moment');
  });

  it('should update the spinner label 2 state basis time', () => {
    mockQueryRequest.mockImplementation(() => {
      return mockBasketStatusResponse();
    });

    const { result } = renderHook(() => usePollBasketStatus('basket-reference', mockSpinnerConfig));

    act(() => {
      jest.advanceTimersByTime(3000);
    });

    expect(result.current.dynamicSpinnerLabel).toBe('Hold tight we are booking');
  });

  it('should update the spinner label 3 state basis time', () => {
    mockQueryRequest.mockImplementation(() => {
      return mockBasketStatusResponse();
    });

    const { result } = renderHook(() => usePollBasketStatus('basket-reference', mockSpinnerConfig));

    act(() => {
      jest.advanceTimersByTime(7000);
    });

    expect(result.current.dynamicSpinnerLabel).toContain('Please bear with us');
  });
  it('should stop polling and return error code when basket status is SECURE_FAILED', () => {
    mockQueryRequest.mockImplementation(() => {
      return {
        isLoading: false,
        isError: false,
        error: { message: '' },
        data: {
          basketStatus: {
            basketStatus: BASKET_STATUS.SECURE_FAILED,
            basketError: {
              code: 'secure_fail_code',
              description: 'Payment authorization failed',
            },
          },
        },
      };
    });

    const { result } = renderHook(() => usePollBasketStatus('basket-reference', mockSpinnerConfig));

    expect(result.current.pollingInProgress).toEqual(false);
    expect(result.current.basketStatus).toEqual(BASKET_STATUS.SECURE_FAILED);
    expect(result.current.errorCode).toEqual('secure_fail_code');
  });
});
