import { setCookieWithDefaultDomain, BUNDLE_CHOICE, TWO_MONTH_SEARCH } from '@whitbread-eos/utils';
import { useEffect } from 'react';

/**
 * Custom hook to handle custom event listeners for app-wide events
 * Sets up listeners for bundle choice and two-month search cookie updates
 */
export const useAppEventListeners = () => {
  useEffect(() => {
    function handleNoRoomTypeSearchSet(event: CustomEvent) {
      setCookieWithDefaultDomain(BUNDLE_CHOICE, event.detail.value, undefined);
    }

    function handleTwoMonthSearchSet(event: CustomEvent) {
      setCookieWithDefaultDomain(TWO_MONTH_SEARCH, event.detail.value, undefined);
    }

    window.addEventListener(BUNDLE_CHOICE, handleNoRoomTypeSearchSet as EventListener);
    window.addEventListener(TWO_MONTH_SEARCH, handleTwoMonthSearchSet as EventListener);

    return () => {
      window.removeEventListener(BUNDLE_CHOICE, handleNoRoomTypeSearchSet as EventListener);
      window.removeEventListener(TWO_MONTH_SEARCH, handleTwoMonthSearchSet as EventListener);
    };
  }, []);
};
