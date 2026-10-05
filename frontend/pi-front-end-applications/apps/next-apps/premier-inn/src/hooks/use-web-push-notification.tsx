import {
  MAX_CLOSE_COUNT,
  PUSH_REQUEST_CLOSED_COUNT,
  PUSH_REQUEST_CLOSED_TIMESTAMP,
  CONSENT_COOKIE,
} from '@whitbread-eos/molecules';
import { useCookieWatcher } from '@whitbread-eos/utils';

import useServiceWorker from './use-service-worker';

interface UseWebPushNotificationProps {
  featureToggles?: { [key: string]: boolean };
  pushRequestClosedCountCookie: number;
  pushRequestClosedTimestampCookie: string | null;
}

interface UseWebPushNotificationReturn {
  shouldShowNotificationModal: boolean;
  handleNotificationPermission: (value: boolean) => void;
}

/**
 * Custom hook to manage web push notification consent modal
 *
 * Checks if the notification permission modal should be displayed based on:
 * - Feature toggle status
 * - Browser notification API availability
 * - User's notification permission status
 * - User's consent cookie
 * - Number of times user has closed the modal
 *
 * @param featureToggles - Feature toggles object
 * @param pushRequestClosedCountCookie - Number of times user closed the notification modal
 * @param pushRequestClosedTimestampCookie - Timestamp when user last closed the modal
 * @returns Object containing shouldShowNotificationModal flag and handleNotificationPermission function
 */
export function useWebPushNotification({
  featureToggles,
  pushRequestClosedCountCookie,
  pushRequestClosedTimestampCookie,
}: UseWebPushNotificationProps): UseWebPushNotificationReturn {
  const isNotificationPermissionEnabledForPI =
    featureToggles?.release_pi_web_push_notifications ?? false;
  const { handleNotificationPermission } = useServiceWorker(isNotificationPermissionEnabledForPI);
  const hasConsentForNotification = useCookieWatcher(CONSENT_COOKIE, 500);

  const shouldShowNotificationModal =
    typeof window !== 'undefined' &&
    'Notification' in window &&
    Notification.permission === 'default' &&
    pushRequestClosedCountCookie < MAX_CLOSE_COUNT &&
    !pushRequestClosedTimestampCookie &&
    !!hasConsentForNotification &&
    isNotificationPermissionEnabledForPI;

  return {
    shouldShowNotificationModal,
    handleNotificationPermission,
  };
}

export { PUSH_REQUEST_CLOSED_COUNT, PUSH_REQUEST_CLOSED_TIMESTAMP };
