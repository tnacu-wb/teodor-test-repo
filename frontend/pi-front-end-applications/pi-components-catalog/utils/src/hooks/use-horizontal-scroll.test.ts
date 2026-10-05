import { renderHook, act } from '@testing-library/react';

import { useHorizontalScroll } from './use-horizontal-scroll';

describe('useHorizontalScroll', () => {
  it('returns scrollRef and scroll functions', () => {
    const { result } = renderHook(() => useHorizontalScroll<HTMLDivElement>());
    expect(result.current.scrollRef).toBeDefined();
    expect(typeof result.current.scrollToLeft).toBe('function');
    expect(typeof result.current.scrollToRight).toBe('function');
    expect(typeof result.current.checkScrollButtons).toBe('function');
    expect(typeof result.current.canScrollLeft).toBe('boolean');
    expect(typeof result.current.canScrollRight).toBe('boolean');
    expect(typeof result.current.hasOverflow).toBe('boolean');
  });

  it('can call scroll functions without error', () => {
    const { result } = renderHook(() => useHorizontalScroll<HTMLDivElement>());
    act(() => {
      result.current.scrollToLeft();
      result.current.scrollToRight();
      result.current.checkScrollButtons();
    });
  });

  it('updates canScrollLeft, canScrollRight, and hasOverflow on scroll and resize', () => {
    const mockEl = {
      scrollLeft: 10,
      scrollWidth: 200,
      clientWidth: 100,
      addEventListener: jest.fn(),
      removeEventListener: jest.fn(),
      scrollBy: jest.fn(),
    } as any;
    const { result } = renderHook(() => useHorizontalScroll<HTMLDivElement>());
    (result.current.scrollRef as any).current = mockEl;
    act(() => {
      result.current.checkScrollButtons();
    });
    expect(result.current.canScrollLeft).toBe(true);
    expect(result.current.canScrollRight).toBe(true);
    expect(result.current.hasOverflow).toBe(true);
  });

  it('calls scrollBy with correct arguments for scrollToLeft and scrollToRight', () => {
    const mockEl = {
      scrollBy: jest.fn(),
      addEventListener: jest.fn(),
      removeEventListener: jest.fn(),
    } as any;
    const { result } = renderHook(() => useHorizontalScroll<HTMLDivElement>(150));
    (result.current.scrollRef as any).current = mockEl;
    act(() => {
      result.current.scrollToLeft();
    });
    expect(mockEl.scrollBy).toHaveBeenCalledWith({ left: -150, behavior: 'smooth' });
    act(() => {
      result.current.scrollToRight();
    });
    expect(mockEl.scrollBy).toHaveBeenCalledWith({ left: 150, behavior: 'smooth' });
  });
});
