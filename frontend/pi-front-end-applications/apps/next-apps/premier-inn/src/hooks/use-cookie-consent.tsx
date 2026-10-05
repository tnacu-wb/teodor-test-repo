import { CONSENT_COOKIE } from '@whitbread-eos/molecules';
import { getCookie } from '@whitbread-eos/utils';
import { useEffect, useState } from 'react';

/**
 * Custom hook to handle cookie consent modal state
 * Determines whether to show the cookie consent modal based on consent status
 */
export const useCookieConsent = () => {
  const [isCookieConsentModalOpen, setIsCookieConsentModalOpen] = useState(true);
  const consentCookie = getCookie(CONSENT_COOKIE);

  useEffect(() => {
    if (typeof window !== 'undefined') {
      const htmlElement = document.documentElement;
      const consentValue = htmlElement.getAttribute('data-consent');
      const hasConsent = consentValue === 'true';

      setIsCookieConsentModalOpen(!hasConsent);
    }
  }, []);

  const closeCookieConsentModal = () => {
    setIsCookieConsentModalOpen(false);
  };

  return {
    isCookieConsentModalOpen,
    closeCookieConsentModal,
    consentCookie,
  };
};
