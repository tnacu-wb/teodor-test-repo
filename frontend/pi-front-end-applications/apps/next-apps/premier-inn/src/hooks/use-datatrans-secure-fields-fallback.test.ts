import { renderHook, act, waitFor } from '@testing-library/react';

import { useDatatransSecureFields } from './use-datatrans-secure-fields';

/**
 * Graceful fallback behaviour: when the API call fails or window.SecureFields
 * is never available, the hook surfaces `sessionError` while keeping all
 * readiness flags false. The consuming component uses `sessionError` to decide
 * whether to fall back to the legacy IframeEmbed (web2Pay) flow.
 */

describe('useDatatransSecureFields — graceful fallback to legacy flow', () => {
  const defaultConfig = {
    basketId: 'basket-fallback-test',
    country: 'gb',
    language: 'en',
    isVisible: true,
  };

  beforeEach(() => {
    jest.clearAllMocks();
    (window as any).SecureFields = jest.fn(() => ({
      init: jest.fn(),
      on: jest.fn(),
      submit: jest.fn(),
      destroy: jest.fn(),
      setPlaceholder: jest.fn(),
    }));
    global.fetch = jest.fn();
  });

  afterEach(() => {
    delete (window as any).SecureFields;
    jest.restoreAllMocks();
  });

  // ─── API failure ──────────────────────────────────────────────────────────

  describe('API returns a non-ok response', () => {
    it('sets sessionError and leaves isInitialized false', async () => {
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: false,
        status: 503,
      });

      const { result } = renderHook(() => useDatatransSecureFields(defaultConfig));

      await waitFor(() => {
        expect(result.current.sessionError).not.toBeNull();
      });

      expect(result.current.sessionError).toBeInstanceOf(Error);
      expect(result.current.sessionError?.message).toContain(
        'Failed to initialize payment session'
      );
      expect(result.current.isInitialized).toBe(false);
      expect(result.current.isReady).toBe(false);
    });

    it('does not call the SecureFields constructor when the API fails', async () => {
      const ctor = jest.fn();
      (window as any).SecureFields = ctor;

      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: false,
        status: 500,
      });

      const { result } = renderHook(() => useDatatransSecureFields(defaultConfig));

      await waitFor(() => {
        expect(result.current.sessionError).not.toBeNull();
      });

      expect(ctor).not.toHaveBeenCalled();
    });
  });

  // ─── SDK not available on window ──────────────────────────────────────────

  describe('SDK not available on window (script failed to load externally)', () => {
    it('does not set isInitialized when window.SecureFields is absent after fetch resolves', async () => {
      delete (window as any).SecureFields;

      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ transactionId: 'txn-abc-123' }),
      });

      const { result } = renderHook(() => useDatatransSecureFields(defaultConfig));

      // Give the fetch time to resolve; SDK is absent so init cannot complete
      await act(async () => {
        await new Promise((r) => setTimeout(r, 50));
      });

      // isInitialized stays false because the SDK never appeared
      expect(result.current.isInitialized).toBe(false);
    });
  });

  // ─── submitPayment safety ─────────────────────────────────────────────────

  describe('submitPayment safety after failure', () => {
    it('does not throw when called before initialisation', async () => {
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: false,
        status: 503,
      });

      const { result } = renderHook(() => useDatatransSecureFields(defaultConfig));

      await waitFor(() => expect(result.current.sessionError).not.toBeNull());

      expect(() => {
        act(() => {
          result.current.submitPayment('12', '25', 'Test User');
        });
      }).not.toThrow();
    });
  });

  // ─── Error state contract for page component ──────────────────────────────

  describe('Error state contract for the page component', () => {
    /**
     * The page component checks sessionError to decide whether to show an
     * error notification and offer the legacy fallback flow.
     *
     * Contract:
     * - sessionError is truthy
     * - isInitialized, isReady, isValid are all false
     */
    it('produces the full error-state contract when the API fails', async () => {
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: false,
        status: 500,
      });

      const { result } = renderHook(() => useDatatransSecureFields(defaultConfig));

      await waitFor(() => expect(result.current.sessionError).not.toBeNull());

      expect(result.current.sessionError).toBeTruthy();
      expect(result.current.isInitialized).toBe(false);
      expect(result.current.isReady).toBe(false);
      expect(result.current.isValid).toBe(false);
    });
  });
});
