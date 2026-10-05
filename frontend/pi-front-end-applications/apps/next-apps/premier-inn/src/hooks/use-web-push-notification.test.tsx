import { renderHook } from '@testing-library/react';
import { useCookieWatcher } from '@whitbread-eos/utils';

import useServiceWorker from './use-service-worker';
import { useWebPushNotification } from './use-web-push-notification';

// Mock dependencies
jest.mock('@whitbread-eos/utils', () => ({
  useCookieWatcher: jest.fn(),
  getNoOfDaysInYear: jest.fn(() => 365),
}));

jest.mock('@whitbread-eos/molecules', () => ({
  MAX_CLOSE_COUNT: 3,
  PUSH_REQUEST_CLOSED_COUNT: 'pushRequestClosedCount',
  PUSH_REQUEST_CLOSED_TIMESTAMP: 'pushRequestClosedTimestamp',
  CONSENT_COOKIE: 'userConsent',
}));

jest.mock('./use-service-worker', () => ({
  __esModule: true,
  default: jest.fn(),
}));

const mockUseCookieWatcher = useCookieWatcher as jest.MockedFunction<typeof useCookieWatcher>;
const mockUseServiceWorker = useServiceWorker as jest.MockedFunction<typeof useServiceWorker>;

describe('useWebPushNotification', () => {
  const mockHandleNotificationPermission = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();

    // Default mock implementations
    mockUseServiceWorker.mockReturnValue({
      handleNotificationPermission: mockHandleNotificationPermission,
    });

    // Mock window.Notification
    Object.defineProperty(window, 'Notification', {
      writable: true,
      configurable: true,
      value: {
        permission: 'default',
      },
    });
  });

  afterEach(() => {
    delete (window as any).Notification;
  });

  describe('shouldShowNotificationModal', () => {
    it('should return true when all conditions are met', () => {
      mockUseCookieWatcher.mockReturnValue('consent-value');

      const { result } = renderHook(() =>
        useWebPushNotification({
          featureToggles: { release_pi_web_push_notifications: true },
          pushRequestClosedCountCookie: 0,
          pushRequestClosedTimestampCookie: null,
        })
      );

      expect(result.current.shouldShowNotificationModal).toBe(true);
    });

    it('should return false when feature toggle is disabled', () => {
      mockUseCookieWatcher.mockReturnValue('consent-value');

      const { result } = renderHook(() =>
        useWebPushNotification({
          featureToggles: { release_pi_web_push_notifications: false },
          pushRequestClosedCountCookie: 0,
          pushRequestClosedTimestampCookie: null,
        })
      );

      expect(result.current.shouldShowNotificationModal).toBe(false);
    });

    it('should return false when feature toggle is undefined', () => {
      mockUseCookieWatcher.mockReturnValue('consent-value');

      const { result } = renderHook(() =>
        useWebPushNotification({
          featureToggles: {},
          pushRequestClosedCountCookie: 0,
          pushRequestClosedTimestampCookie: null,
        })
      );

      expect(result.current.shouldShowNotificationModal).toBe(false);
    });

    it('should return false when consent cookie is not present', () => {
      mockUseCookieWatcher.mockReturnValue(null);

      const { result } = renderHook(() =>
        useWebPushNotification({
          featureToggles: { release_pi_web_push_notifications: true },
          pushRequestClosedCountCookie: 0,
          pushRequestClosedTimestampCookie: null,
        })
      );

      expect(result.current.shouldShowNotificationModal).toBe(false);
    });

    it('should return false when pushRequestClosedCountCookie exceeds MAX_CLOSE_COUNT', () => {
      mockUseCookieWatcher.mockReturnValue('consent-value');

      const { result } = renderHook(() =>
        useWebPushNotification({
          featureToggles: { release_pi_web_push_notifications: true },
          pushRequestClosedCountCookie: 3, // MAX_CLOSE_COUNT is 3
          pushRequestClosedTimestampCookie: null,
        })
      );

      expect(result.current.shouldShowNotificationModal).toBe(false);
    });

    it('should return false when pushRequestClosedTimestampCookie exists', () => {
      mockUseCookieWatcher.mockReturnValue('consent-value');

      const { result } = renderHook(() =>
        useWebPushNotification({
          featureToggles: { release_pi_web_push_notifications: true },
          pushRequestClosedCountCookie: 0,
          pushRequestClosedTimestampCookie: '2024-01-01T00:00:00Z',
        })
      );

      expect(result.current.shouldShowNotificationModal).toBe(false);
    });

    it('should return false when Notification permission is granted', () => {
      mockUseCookieWatcher.mockReturnValue('consent-value');
      Object.defineProperty(window, 'Notification', {
        writable: true,
        configurable: true,
        value: {
          permission: 'granted',
        },
      });

      const { result } = renderHook(() =>
        useWebPushNotification({
          featureToggles: { release_pi_web_push_notifications: true },
          pushRequestClosedCountCookie: 0,
          pushRequestClosedTimestampCookie: null,
        })
      );

      expect(result.current.shouldShowNotificationModal).toBe(false);
    });

    it('should return false when Notification permission is denied', () => {
      mockUseCookieWatcher.mockReturnValue('consent-value');
      Object.defineProperty(window, 'Notification', {
        writable: true,
        configurable: true,
        value: {
          permission: 'denied',
        },
      });

      const { result } = renderHook(() =>
        useWebPushNotification({
          featureToggles: { release_pi_web_push_notifications: true },
          pushRequestClosedCountCookie: 0,
          pushRequestClosedTimestampCookie: null,
        })
      );

      expect(result.current.shouldShowNotificationModal).toBe(false);
    });

    it('should return false when Notification API is not available', () => {
      mockUseCookieWatcher.mockReturnValue('consent-value');
      delete (window as any).Notification;

      const { result } = renderHook(() =>
        useWebPushNotification({
          featureToggles: { release_pi_web_push_notifications: true },
          pushRequestClosedCountCookie: 0,
          pushRequestClosedTimestampCookie: null,
        })
      );

      expect(result.current.shouldShowNotificationModal).toBe(false);
    });

    it('should handle empty string consent cookie value correctly', () => {
      mockUseCookieWatcher.mockReturnValue('');

      const { result } = renderHook(() =>
        useWebPushNotification({
          featureToggles: { release_pi_web_push_notifications: true },
          pushRequestClosedCountCookie: 0,
          pushRequestClosedTimestampCookie: null,
        })
      );

      expect(result.current.shouldShowNotificationModal).toBe(false);
    });

    it('should work with close count just below MAX_CLOSE_COUNT', () => {
      mockUseCookieWatcher.mockReturnValue('consent-value');

      const { result } = renderHook(() =>
        useWebPushNotification({
          featureToggles: { release_pi_web_push_notifications: true },
          pushRequestClosedCountCookie: 2, // MAX_CLOSE_COUNT is 3
          pushRequestClosedTimestampCookie: null,
        })
      );

      expect(result.current.shouldShowNotificationModal).toBe(true);
    });
  });

  describe('handleNotificationPermission', () => {
    it('should return handleNotificationPermission function from useServiceWorker', () => {
      mockUseCookieWatcher.mockReturnValue('consent-value');

      const { result } = renderHook(() =>
        useWebPushNotification({
          featureToggles: { release_pi_web_push_notifications: true },
          pushRequestClosedCountCookie: 0,
          pushRequestClosedTimestampCookie: null,
        })
      );

      expect(result.current.handleNotificationPermission).toBe(mockHandleNotificationPermission);
    });

    it('should call useServiceWorker with feature toggle value', () => {
      mockUseCookieWatcher.mockReturnValue('consent-value');

      renderHook(() =>
        useWebPushNotification({
          featureToggles: { release_pi_web_push_notifications: true },
          pushRequestClosedCountCookie: 0,
          pushRequestClosedTimestampCookie: null,
        })
      );

      expect(mockUseServiceWorker).toHaveBeenCalledWith(true);
    });

    it('should call useServiceWorker with false when feature toggle is disabled', () => {
      mockUseCookieWatcher.mockReturnValue('consent-value');

      renderHook(() =>
        useWebPushNotification({
          featureToggles: { release_pi_web_push_notifications: false },
          pushRequestClosedCountCookie: 0,
          pushRequestClosedTimestampCookie: null,
        })
      );

      expect(mockUseServiceWorker).toHaveBeenCalledWith(false);
    });
  });

  describe('useCookieWatcher integration', () => {
    it('should call useCookieWatcher with CONSENT_COOKIE and 500ms interval', () => {
      mockUseCookieWatcher.mockReturnValue('consent-value');

      renderHook(() =>
        useWebPushNotification({
          featureToggles: { release_pi_web_push_notifications: true },
          pushRequestClosedCountCookie: 0,
          pushRequestClosedTimestampCookie: null,
        })
      );

      expect(mockUseCookieWatcher).toHaveBeenCalledWith('userConsent', 500);
    });

    it('should handle cookie value updates', () => {
      mockUseCookieWatcher.mockReturnValue(null);

      const { result, rerender } = renderHook(() =>
        useWebPushNotification({
          featureToggles: { release_pi_web_push_notifications: true },
          pushRequestClosedCountCookie: 0,
          pushRequestClosedTimestampCookie: null,
        })
      );

      expect(result.current.shouldShowNotificationModal).toBe(false);

      // Simulate cookie being set
      mockUseCookieWatcher.mockReturnValue('consent-value');
      rerender();

      expect(result.current.shouldShowNotificationModal).toBe(true);
    });
  });

  describe('edge cases', () => {
    it('should handle undefined featureToggles', () => {
      mockUseCookieWatcher.mockReturnValue('consent-value');

      const { result } = renderHook(() =>
        useWebPushNotification({
          featureToggles: undefined,
          pushRequestClosedCountCookie: 0,
          pushRequestClosedTimestampCookie: null,
        })
      );

      expect(result.current.shouldShowNotificationModal).toBe(false);
    });

    it('should handle negative pushRequestClosedCountCookie', () => {
      mockUseCookieWatcher.mockReturnValue('consent-value');

      const { result } = renderHook(() =>
        useWebPushNotification({
          featureToggles: { release_pi_web_push_notifications: true },
          pushRequestClosedCountCookie: -1,
          pushRequestClosedTimestampCookie: null,
        })
      );

      expect(result.current.shouldShowNotificationModal).toBe(true);
    });

    it('should handle zero pushRequestClosedCountCookie', () => {
      mockUseCookieWatcher.mockReturnValue('consent-value');

      const { result } = renderHook(() =>
        useWebPushNotification({
          featureToggles: { release_pi_web_push_notifications: true },
          pushRequestClosedCountCookie: 0,
          pushRequestClosedTimestampCookie: null,
        })
      );

      expect(result.current.shouldShowNotificationModal).toBe(true);
    });

    it('should handle empty string pushRequestClosedTimestampCookie', () => {
      mockUseCookieWatcher.mockReturnValue('consent-value');

      const { result } = renderHook(() =>
        useWebPushNotification({
          featureToggles: { release_pi_web_push_notifications: true },
          pushRequestClosedCountCookie: 0,
          pushRequestClosedTimestampCookie: '',
        })
      );

      // Empty string is falsy, so !'' is true, and modal should show
      expect(result.current.shouldShowNotificationModal).toBe(true);
    });
  });
});
