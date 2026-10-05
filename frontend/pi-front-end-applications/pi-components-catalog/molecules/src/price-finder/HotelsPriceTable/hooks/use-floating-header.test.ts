import { renderHook } from '@testing-library/react';
import { RefObject } from 'react';

import { useFloatingHeader } from './use-floating-header';

// Mock IntersectionObserver
const mockObserve = jest.fn();
const mockDisconnect = jest.fn();
const mockIntersectionObserver = jest.fn(() => ({
  observe: mockObserve,
  disconnect: mockDisconnect,
  unobserve: jest.fn(),
}));

Object.defineProperty(window, 'IntersectionObserver', {
  writable: true,
  configurable: true,
  value: mockIntersectionObserver,
});

describe('useFloatingHeader', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should initialize with showFloatingHeader as false', () => {
    const mockRef = { current: document.createElement('thead') };
    const { result } = renderHook(() => useFloatingHeader(mockRef));

    expect(result.current.showFloatingHeader).toBe(false);
  });

  it('should create IntersectionObserver when ref has current element', () => {
    const mockRef = { current: document.createElement('thead') };
    renderHook(() => useFloatingHeader(mockRef));

    expect(mockIntersectionObserver).toHaveBeenCalled();
    expect(mockObserve).toHaveBeenCalledWith(mockRef.current);
  });

  it('should not create observer when ref.current is null', () => {
    const nullRef: RefObject<HTMLTableSectionElement> = { current: null };
    renderHook(() => useFloatingHeader(nullRef));

    expect(mockIntersectionObserver).not.toHaveBeenCalled();
    expect(mockObserve).not.toHaveBeenCalled();
  });

  it('should cleanup observer on unmount', () => {
    const mockRef = { current: document.createElement('thead') };
    const { unmount } = renderHook(() => useFloatingHeader(mockRef));

    unmount();

    expect(mockDisconnect).toHaveBeenCalled();
  });
});
