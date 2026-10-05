import { act, renderHook } from '@testing-library/react';

import useDebounce from './use-debounce';

jest.useFakeTimers();

describe('useDebounce', () => {
  it('should debounce the callback', () => {
    const callback = jest.fn();
    const { result } = renderHook(() => useDebounce(callback));

    act(() => {
      result.current();
      result.current();
    });

    jest.runAllTimers();

    expect(callback).toHaveBeenCalledTimes(1);
  });

  it('should update the callback when the input changes', () => {
    const initialCallback = jest.fn();
    const { result, rerender } = renderHook(({ callback }) => useDebounce(callback), {
      initialProps: { callback: initialCallback },
    });

    act(() => {
      result.current();
    });

    jest.runAllTimers();

    expect(initialCallback).toHaveBeenCalledTimes(1);

    const newCallback = jest.fn();
    rerender({ callback: newCallback });

    act(() => {
      result.current();
    });
    jest.runAllTimers();

    expect(newCallback).toHaveBeenCalledTimes(1);
  });
});
