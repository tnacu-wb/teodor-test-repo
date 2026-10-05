import { act, renderHook } from '@testing-library/react';

import * as cookiesHelper from '../helpers/cookies';
import useCookieWatcher from './use-cookie-watcher';

jest.useFakeTimers();

describe('useCookieWatcher', () => {
  const mockGetCookie = jest.spyOn(cookiesHelper, 'getCookie');

  afterEach(() => {
    jest.clearAllMocks();
    jest.clearAllTimers();
  });

  it('should return the initial cookie value', () => {
    mockGetCookie.mockReturnValue('initialValue');

    const { result } = renderHook(() => useCookieWatcher('testCookie'));

    expect(result.current).toBe('initialValue');
    expect(mockGetCookie).toHaveBeenCalledWith('testCookie');
  });

  it('should update the cookie value when it changes', () => {
    mockGetCookie.mockReturnValueOnce('initialValue').mockReturnValueOnce('newValue');

    const { result } = renderHook(() => useCookieWatcher('testCookie'));

    expect(result.current).toBe('initialValue');

    act(() => {
      jest.advanceTimersByTime(1000);
    });

    expect(result.current).toBe('newValue');
    expect(mockGetCookie).toHaveBeenCalledTimes(2);
  });

  it('should not update the state if the cookie value remains the same', () => {
    mockGetCookie.mockReturnValue('sameValue');

    const { result } = renderHook(() => useCookieWatcher('testCookie'));

    expect(result.current).toBe('sameValue');

    act(() => {
      jest.advanceTimersByTime(1000);
    });

    expect(result.current).toBe('sameValue');
    expect(mockGetCookie).toHaveBeenCalledTimes(2);
  });

  it('should clear the interval on unmount', () => {
    const clearIntervalSpy = jest.spyOn(global, 'clearInterval');

    const { unmount } = renderHook(() => useCookieWatcher('testCookie'));

    unmount();

    expect(clearIntervalSpy).toHaveBeenCalledTimes(1);
  });
});
