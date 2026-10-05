import {
  DYNATRACE_CONSENT_DISABLED,
  DYNATRACE_CONSENT_ENABLED,
  DYNATRACE_CONSENT_COOKIE_NAME,
  DYNATRACE_SYNC_COOKIE,
  getDynatraceConsentPaths,
  hasCustomDynatraceConsent,
  hasOneTrustDynatraceConsent,
  syncDynatraceConsent,
  syncDynatraceConsentFromOneTrust,
} from './dynatraceConsent';

describe('dynatraceConsent', () => {
  const originalDtrum = window.dtrum;

  beforeEach(() => {
    Object.defineProperty(document, 'cookie', {
      writable: true,
      value: '',
    });
    window.dtrum = {
      enableSessionReplay: jest.fn(),
      disableSessionReplay: jest.fn(),
    };
    window.OnetrustActiveGroups = undefined;
  });

  afterAll(() => {
    window.dtrum = originalDtrum;
  });

  it('enables Dynatrace Session Replay and stores synced state when consent is granted', () => {
    syncDynatraceConsent({ hasConsent: true, expiryMinutes: 10 });

    expect(window.dtrum?.enableSessionReplay).toHaveBeenCalledWith(false);
    expect(window.dtrum?.disableSessionReplay).not.toHaveBeenCalled();
    expect(document.cookie).toContain(`${DYNATRACE_SYNC_COOKIE}=${DYNATRACE_CONSENT_ENABLED}`);
  });

  it('disables Dynatrace Session Replay and stores synced state when consent is denied', () => {
    syncDynatraceConsent({ hasConsent: false, expiryMinutes: 10 });

    expect(window.dtrum?.disableSessionReplay).toHaveBeenCalledTimes(1);
    expect(window.dtrum?.enableSessionReplay).not.toHaveBeenCalled();
    expect(document.cookie).toContain(`${DYNATRACE_SYNC_COOKIE}=${DYNATRACE_CONSENT_DISABLED}`);
  });

  it('does not call Dynatrace when consent syncing is disabled', () => {
    const result = syncDynatraceConsent({ hasConsent: true, isEnabled: false });

    expect(result).toBe(false);
    expect(window.dtrum?.enableSessionReplay).not.toHaveBeenCalled();
    expect(window.dtrum?.disableSessionReplay).not.toHaveBeenCalled();
    expect(document.cookie).not.toContain(DYNATRACE_SYNC_COOKIE);
  });

  it('skips Dynatrace call when the same state has already been synced', () => {
    document.cookie = `${DYNATRACE_SYNC_COOKIE}=${DYNATRACE_CONSENT_ENABLED}`;

    syncDynatraceConsent({ hasConsent: true, expiryMinutes: 10 });

    expect(window.dtrum?.enableSessionReplay).not.toHaveBeenCalled();
    expect(window.dtrum?.disableSessionReplay).not.toHaveBeenCalled();
  });

  it('does not write the sync marker when the required dtrum method is unavailable', () => {
    window.dtrum = {};

    const result = syncDynatraceConsent({ hasConsent: true, expiryMinutes: 10 });

    expect(result).toBe(false);
    expect(document.cookie).not.toContain(DYNATRACE_SYNC_COOKIE);
  });

  it('reads custom Dynatrace consent from the shared cookie name constant', () => {
    document.cookie = `${DYNATRACE_CONSENT_COOKIE_NAME}=true`;

    expect(hasCustomDynatraceConsent()).toBe(true);
  });

  it('reads and syncs OneTrust Dynatrace consent', () => {
    window.OnetrustActiveGroups = ',C0001,C0002,';

    expect(hasOneTrustDynatraceConsent()).toBe(true);

    syncDynatraceConsentFromOneTrust();
    expect(window.dtrum?.enableSessionReplay).toHaveBeenCalledWith(false);
  });

  it('builds the language-region and region-only cookie paths', () => {
    expect(getDynatraceConsentPaths('en', 'gb')).toEqual(['/en-gb', '/gb']);
  });
});
