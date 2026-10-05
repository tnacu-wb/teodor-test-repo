import { getCookie, setCookie } from './cookies';

export const COOKIE_MODAL_CLOSED_EVENT = 'cookieModalClosed';
export const DYNATRACE_CONSENT_COOKIE_NAME = 'permissionPerformance';
export const DYNATRACE_CONSENT_DISABLED = 'disabled';
export const DYNATRACE_CONSENT_ENABLED = 'enabled';
export const DYNATRACE_SYNC_COOKIE = 'dynatraceSessionReplayConsentSynced';
export const ONETRUST_DYNATRACE_CONSENT_GROUP = 'C0002';
export const ONETRUST_GROUPS_UPDATED_EVENT = 'OneTrustGroupsUpdated';

// Used when the caller has no consent cookie duration to mirror, e.g. existing consent on startup.
const DEFAULT_SYNC_EXPIRY_MINUTES = 60 * 24 * 365;

type SyncDynatraceConsentOptions = {
  hasConsent: boolean;
  isEnabled?: boolean;
  expiryMinutes?: number;
  paths?: string[];
  domain?: string;
};

declare global {
  interface Window {
    dtrum?: {
      enableSessionReplay?: (ignoreCostControl: boolean) => void;
      disableSessionReplay?: () => void;
    };
    OnetrustActiveGroups?: string;
  }
}

export const hasCustomDynatraceConsent = () => getCookie(DYNATRACE_CONSENT_COOKIE_NAME) === 'true';

export const hasOneTrustDynatraceConsent = () =>
  typeof window !== 'undefined' &&
  window.OnetrustActiveGroups?.includes(`,${ONETRUST_DYNATRACE_CONSENT_GROUP},`) === true;

// Both the language-region and region-only cookie paths need the same sync marker.
export const getDynatraceConsentPaths = (language: string, country: string): string[] => [
  `/${language}-${country}`,
  `/${country}`,
];

export const syncDynatraceConsent = ({
  hasConsent,
  isEnabled = true,
  expiryMinutes = DEFAULT_SYNC_EXPIRY_MINUTES,
  paths = ['/'],
  domain,
}: SyncDynatraceConsentOptions) => {
  if (!isEnabled || typeof window === 'undefined' || !window.dtrum) {
    return false;
  }

  const nextState = hasConsent ? DYNATRACE_CONSENT_ENABLED : DYNATRACE_CONSENT_DISABLED;
  const currentState = getCookie(DYNATRACE_SYNC_COOKIE);

  if (currentState === nextState) {
    return false;
  }

  const methodName = hasConsent ? 'enableSessionReplay' : 'disableSessionReplay';
  const dtrumMethod = window.dtrum[methodName];
  if (!dtrumMethod) {
    return false;
  }

  if (hasConsent) {
    window.dtrum.enableSessionReplay?.(false);
  } else {
    window.dtrum.disableSessionReplay?.();
  }

  paths.forEach((path) => {
    setCookie(DYNATRACE_SYNC_COOKIE, nextState, expiryMinutes, path, undefined, domain);
  });

  return true;
};

export const syncDynatraceConsentFromCookie = (
  options: Omit<SyncDynatraceConsentOptions, 'hasConsent'> = {}
) =>
  syncDynatraceConsent({
    ...options,
    hasConsent: hasCustomDynatraceConsent(),
  });

export const syncDynatraceConsentFromOneTrust = (
  options: Omit<SyncDynatraceConsentOptions, 'hasConsent'> = {}
) =>
  syncDynatraceConsent({
    ...options,
    hasConsent: hasOneTrustDynatraceConsent(),
  });
