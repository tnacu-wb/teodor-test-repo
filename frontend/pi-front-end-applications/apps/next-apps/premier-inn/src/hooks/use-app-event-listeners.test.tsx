import { BUNDLE_CHOICE, TWO_MONTH_SEARCH } from '@whitbread-eos/utils';

import { render, waitFor } from '~utils/test-utils';

import { useAppEventListeners } from './use-app-event-listeners';

const mockSetCookieWithDefaultDomain = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  setCookieWithDefaultDomain: (...args: unknown[]) => mockSetCookieWithDefaultDomain(...args),
}));

// Test component that uses the hook
function TestComponent() {
  useAppEventListeners();
  return <div data-testid="event-listeners-consumer">Event listeners active</div>;
}

describe('useAppEventListeners', () => {
  let addEventListenerSpy: jest.SpyInstance;
  let removeEventListenerSpy: jest.SpyInstance;

  beforeEach(() => {
    jest.clearAllMocks();
    addEventListenerSpy = jest.spyOn(window, 'addEventListener');
    removeEventListenerSpy = jest.spyOn(window, 'removeEventListener');
  });

  afterEach(() => {
    addEventListenerSpy.mockRestore();
    removeEventListenerSpy.mockRestore();
  });

  describe('Event listener setup', () => {
    it('should add event listeners for BUNDLE_CHOICE and TWO_MONTH_SEARCH on mount', () => {
      render(<TestComponent />);

      expect(addEventListenerSpy).toHaveBeenCalledWith(BUNDLE_CHOICE, expect.any(Function));
      expect(addEventListenerSpy).toHaveBeenCalledWith(TWO_MONTH_SEARCH, expect.any(Function));
    });

    it('should remove event listeners on unmount', () => {
      const { unmount } = render(<TestComponent />);

      unmount();

      expect(removeEventListenerSpy).toHaveBeenCalledWith(BUNDLE_CHOICE, expect.any(Function));
      expect(removeEventListenerSpy).toHaveBeenCalledWith(TWO_MONTH_SEARCH, expect.any(Function));
    });
  });

  describe('BUNDLE_CHOICE event handling', () => {
    it('should set cookie when BUNDLE_CHOICE event is dispatched', async () => {
      render(<TestComponent />);

      const event = new CustomEvent(BUNDLE_CHOICE, {
        detail: { value: 'test-bundle-value' },
      });
      window.dispatchEvent(event);

      await waitFor(() => {
        expect(mockSetCookieWithDefaultDomain).toHaveBeenCalledWith(
          BUNDLE_CHOICE,
          'test-bundle-value',
          undefined
        );
      });
    });

    it('should handle different BUNDLE_CHOICE values', async () => {
      render(<TestComponent />);

      const event1 = new CustomEvent(BUNDLE_CHOICE, {
        detail: { value: 'variant-a' },
      });
      window.dispatchEvent(event1);

      await waitFor(() => {
        expect(mockSetCookieWithDefaultDomain).toHaveBeenCalledWith(
          BUNDLE_CHOICE,
          'variant-a',
          undefined
        );
      });

      mockSetCookieWithDefaultDomain.mockClear();

      const event2 = new CustomEvent(BUNDLE_CHOICE, {
        detail: { value: 'variant-b' },
      });
      window.dispatchEvent(event2);

      await waitFor(() => {
        expect(mockSetCookieWithDefaultDomain).toHaveBeenCalledWith(
          BUNDLE_CHOICE,
          'variant-b',
          undefined
        );
      });
    });
  });

  describe('TWO_MONTH_SEARCH event handling', () => {
    it('should set cookie when TWO_MONTH_SEARCH event is dispatched', async () => {
      render(<TestComponent />);

      const event = new CustomEvent(TWO_MONTH_SEARCH, {
        detail: { value: 'true' },
      });
      window.dispatchEvent(event);

      await waitFor(() => {
        expect(mockSetCookieWithDefaultDomain).toHaveBeenCalledWith(
          TWO_MONTH_SEARCH,
          'true',
          undefined
        );
      });
    });

    it('should handle TWO_MONTH_SEARCH with false value', async () => {
      render(<TestComponent />);

      const event = new CustomEvent(TWO_MONTH_SEARCH, {
        detail: { value: 'false' },
      });
      window.dispatchEvent(event);

      await waitFor(() => {
        expect(mockSetCookieWithDefaultDomain).toHaveBeenCalledWith(
          TWO_MONTH_SEARCH,
          'false',
          undefined
        );
      });
    });
  });

  describe('Multiple events', () => {
    it('should handle both event types independently', async () => {
      render(<TestComponent />);

      const bundleEvent = new CustomEvent(BUNDLE_CHOICE, {
        detail: { value: 'bundle-test' },
      });
      const twoMonthEvent = new CustomEvent(TWO_MONTH_SEARCH, {
        detail: { value: 'two-month-test' },
      });

      window.dispatchEvent(bundleEvent);
      window.dispatchEvent(twoMonthEvent);

      await waitFor(() => {
        expect(mockSetCookieWithDefaultDomain).toHaveBeenCalledTimes(2);
        expect(mockSetCookieWithDefaultDomain).toHaveBeenCalledWith(
          BUNDLE_CHOICE,
          'bundle-test',
          undefined
        );
        expect(mockSetCookieWithDefaultDomain).toHaveBeenCalledWith(
          TWO_MONTH_SEARCH,
          'two-month-test',
          undefined
        );
      });
    });
  });

  describe('Cleanup behavior', () => {
    it('should not respond to events after unmount', async () => {
      const { unmount } = render(<TestComponent />);

      unmount();
      mockSetCookieWithDefaultDomain.mockClear();

      const event = new CustomEvent(BUNDLE_CHOICE, {
        detail: { value: 'should-not-be-set' },
      });
      window.dispatchEvent(event);

      // Wait a tick and verify no calls were made
      await new Promise((resolve) => setTimeout(resolve, 0));
      expect(mockSetCookieWithDefaultDomain).not.toHaveBeenCalled();
    });
  });
});
