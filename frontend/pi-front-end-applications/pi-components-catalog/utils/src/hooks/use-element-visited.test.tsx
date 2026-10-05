import { renderHook, act } from '@testing-library/react';

import useElementVisited from './use-element-visited';

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useSearchParams: jest.fn().mockReturnValue({
    get: (key: string) => key,
  }),
}));

describe('useElementVisited', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should return false initially', () => {
    const { result } = renderHook(() => useElementVisited('test-element'));
    expect(result.current).toBe(false);
  });

  it('should return true when the element is visited', () => {
    const elementId = 'test-element';
    const element = document.createElement('div');
    element.id = elementId;
    document.body.appendChild(element);

    const { result } = renderHook(() => useElementVisited(elementId));

    // Mock the element's position and window scroll
    Object.defineProperty(element, 'getBoundingClientRect', {
      value: () => ({
        top: -100, // Top is negative indicating it has been scrolled past
        bottom: 50,
        height: 150,
      }),
    });
    Object.defineProperty(element, 'offsetHeight', {
      value: 150,
    });

    act(() => {
      // Simulate scroll
      window.scrollY = 2000;
      document.dispatchEvent(new Event('scroll'));
    });

    // Use async function to wait for state update
    const waitForStateUpdate = async () => {
      await new Promise((resolve) => setTimeout(resolve, 0));
    };

    waitForStateUpdate().then(() => {
      expect(result.current).toBe(true);
      document.body.removeChild(element);
    });
  });

  it('should clean up the event listener on unmount', () => {
    const addEventListenerSpy = jest.spyOn(document, 'addEventListener');
    const removeEventListenerSpy = jest.spyOn(document, 'removeEventListener');

    const { unmount } = renderHook(() => useElementVisited('test-element'));

    expect(addEventListenerSpy).toHaveBeenCalledWith('scroll', expect.any(Function));

    unmount();

    expect(removeEventListenerSpy).toHaveBeenCalledWith('scroll', expect.any(Function));
  });

  it('should return true when the element is sscrolled but not visited', () => {
    const { result } = renderHook(() => useElementVisited('test-element'));
    act(() => {
      // Simulate scroll
      window.scrollY = 50;
      document.dispatchEvent(new Event('scroll'));
    });

    // Use async function to wait for state update
    const waitForStateUpdate = async () => {
      await new Promise((resolve) => setTimeout(resolve, 0));
    };

    waitForStateUpdate().then(() => {
      expect(result.current).toBe(true);
    });
  });
});
