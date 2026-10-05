import { renderHook, act, waitFor } from '@testing-library/react';

import { useDatatransSecureFields } from './use-datatrans-secure-fields';

// Mock SecureFields instance
interface MockSecureFieldsInstance {
  init: jest.Mock;
  on: jest.Mock;
  submit: jest.Mock;
  destroy: jest.Mock;
  setPlaceholder: jest.Mock;
  _listeners: Record<string, (...args: any[]) => void>;
  _triggerEvent: (event: string, ...args: any[]) => void;
}

function createMockSecureFieldsInstance(): MockSecureFieldsInstance {
  const listeners: Record<string, (...args: any[]) => void> = {};

  return {
    init: jest.fn(),
    on: jest.fn((event: string, callback: (...args: any[]) => void) => {
      listeners[event] = callback;
    }),
    submit: jest.fn(),
    destroy: jest.fn(),
    setPlaceholder: jest.fn(),
    _listeners: listeners,
    _triggerEvent(event: string, ...args: any[]) {
      if (listeners[event]) {
        listeners[event](...args);
      }
    },
  };
}

describe('useDatatransSecureFields', () => {
  let mockSecureFieldsInstance: MockSecureFieldsInstance;
  let mockSecureFieldsConstructor: jest.Mock;

  // Minimal valid config — window.SecureFields is pre-set so the SDK is
  // immediately available when isVisible becomes true.
  const defaultConfig = {
    basketId: 'basket-123',
    country: 'gb',
    language: 'en',
    isVisible: false,
  };

  beforeEach(() => {
    jest.clearAllMocks();

    mockSecureFieldsInstance = createMockSecureFieldsInstance();
    mockSecureFieldsConstructor = jest.fn(() => mockSecureFieldsInstance);

    // SDK available on window from the start (injected by external Script component)
    (window as any).SecureFields = mockSecureFieldsConstructor;

    // Mock global fetch
    global.fetch = jest.fn();
  });

  afterEach(() => {
    delete (window as any).SecureFields;
    jest.restoreAllMocks();
  });

  // ─── Initial state ────────────────────────────────────────────────────────

  describe('Initial state', () => {
    it('returns default state when isVisible is false', () => {
      const { result } = renderHook(() => useDatatransSecureFields(defaultConfig));

      expect(result.current.isInitialized).toBe(false);
      expect(result.current.isReady).toBe(false);
      expect(result.current.isValid).toBe(false);
      expect(result.current.errors).toEqual({});
      expect(result.current.sessionError).toBeNull();
      expect(result.current.fieldValidity).toEqual({ cardNumber: null, cvv: null });
      expect(result.current.fieldTouched).toEqual({ cardNumber: false, cvv: false });
      expect(typeof result.current.submitPayment).toBe('function');
    });

    it('does not call fetch when isVisible is false', () => {
      renderHook(() => useDatatransSecureFields(defaultConfig));

      expect(global.fetch).not.toHaveBeenCalled();
    });
  });

  // ─── Session initialisation ───────────────────────────────────────────────

  describe('Session initialisation (isVisible becomes true)', () => {
    it('calls the secure-fields API with basketId, country and language when visible', async () => {
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ transactionId: 'txn-abc-123' }),
      });

      renderHook(() => useDatatransSecureFields({ ...defaultConfig, isVisible: true }));

      await waitFor(() => {
        expect(global.fetch).toHaveBeenCalledWith(
          '/api/payments/secure-fields',
          expect.objectContaining({
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ basketId: 'basket-123', country: 'gb', language: 'en' }),
          })
        );
      });
    });

    it('sets isInitialized to true after successful session init', async () => {
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ transactionId: 'txn-abc-123' }),
      });

      const { result } = renderHook(() =>
        useDatatransSecureFields({ ...defaultConfig, isVisible: true })
      );

      await waitFor(() => {
        expect(result.current.isInitialized).toBe(true);
      });

      expect(mockSecureFieldsConstructor).toHaveBeenCalled();
      expect(mockSecureFieldsInstance.init).toHaveBeenCalledWith(
        'txn-abc-123',
        { cardNumber: 'datatrans-cardNumber', cvv: 'datatrans-cvv' },
        {}
      );
    });

    it('does not initialise when basketId is missing', () => {
      renderHook(() =>
        useDatatransSecureFields({ ...defaultConfig, basketId: '', isVisible: true })
      );

      expect(global.fetch).not.toHaveBeenCalled();
    });
  });

  // ─── API failure ──────────────────────────────────────────────────────────

  describe('API call failure', () => {
    it('sets sessionError when the API returns a non-ok response', async () => {
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: false,
        status: 500,
      });

      const { result } = renderHook(() =>
        useDatatransSecureFields({ ...defaultConfig, isVisible: true })
      );

      await waitFor(() => {
        expect(result.current.sessionError).not.toBeNull();
      });

      expect(result.current.sessionError?.message).toContain(
        'Failed to initialize payment session'
      );
      expect(result.current.isInitialized).toBe(false);
    });

    it('does not set isInitialized when window.SecureFields is not available', async () => {
      delete (window as any).SecureFields;

      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ transactionId: 'txn-abc-123' }),
      });

      const { result } = renderHook(() =>
        useDatatransSecureFields({ ...defaultConfig, isVisible: true })
      );

      // Give the fetch time to resolve; SDK is absent so init cannot complete
      await act(async () => {
        await new Promise((r) => setTimeout(r, 50));
      });

      // isInitialized stays false because the SDK never appeared
      expect(result.current.isInitialized).toBe(false);
    });
  });

  // ─── SecureFields events ──────────────────────────────────────────────────

  describe('SecureFields ready event', () => {
    it('sets isReady to true when the ready event fires', async () => {
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ transactionId: 'txn-abc-123' }),
      });

      const { result } = renderHook(() =>
        useDatatransSecureFields({ ...defaultConfig, isVisible: true })
      );

      await waitFor(() => expect(result.current.isInitialized).toBe(true));

      act(() => {
        mockSecureFieldsInstance._triggerEvent('ready');
      });

      expect(result.current.isReady).toBe(true);
    });
  });

  describe('SecureFields validate / change event', () => {
    async function setupInitializedHook() {
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ transactionId: 'txn-abc-123' }),
      });

      const rendered = renderHook(() =>
        useDatatransSecureFields({ ...defaultConfig, isVisible: true })
      );

      await waitFor(() => expect(rendered.result.current.isInitialized).toBe(true));
      return rendered;
    }

    it('sets isValid to true when all fields are valid on a validate event', async () => {
      const { result } = await setupInitializedHook();

      act(() => {
        mockSecureFieldsInstance._triggerEvent('validate', {
          fields: { cardNumber: { valid: true }, cvv: { valid: true } },
        });
      });

      expect(result.current.isValid).toBe(true);
    });

    it('sets isValid to false when any field is invalid on a validate event', async () => {
      const { result } = await setupInitializedHook();

      act(() => {
        mockSecureFieldsInstance._triggerEvent('validate', {
          fields: { cardNumber: { valid: true }, cvv: { valid: false } },
        });
      });

      expect(result.current.isValid).toBe(false);
    });

    it('updates isValid on a change event', async () => {
      const { result } = await setupInitializedHook();

      act(() => {
        mockSecureFieldsInstance._triggerEvent('change', {
          fields: { cardNumber: { valid: true }, cvv: { valid: true } },
        });
      });

      expect(result.current.isValid).toBe(true);
    });

    it('marks fieldTouched on blur via change event', async () => {
      const { result } = await setupInitializedHook();

      act(() => {
        mockSecureFieldsInstance._triggerEvent('change', {
          fields: { cardNumber: { valid: true }, cvv: { valid: false } },
          event: { field: 'cardNumber', type: 'blur' },
        });
      });

      expect(result.current.fieldTouched.cardNumber).toBe(true);
      expect(result.current.fieldTouched.cvv).toBe(false);
    });
  });

  describe('SecureFields error event', () => {
    async function setupInitializedHook() {
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ transactionId: 'txn-abc-123' }),
      });

      const rendered = renderHook(() =>
        useDatatransSecureFields({ ...defaultConfig, isVisible: true })
      );

      await waitFor(() => expect(rendered.result.current.isInitialized).toBe(true));
      return rendered;
    }

    it('populates the errors map with a field error', async () => {
      const { result } = await setupInitializedHook();

      act(() => {
        mockSecureFieldsInstance._triggerEvent('error', {
          field: 'cardNumber',
          message: 'Invalid card number',
        });
      });

      expect(result.current.errors).toEqual({ cardNumber: 'Invalid card number' });
    });

    it('accumulates errors from multiple fields', async () => {
      const { result } = await setupInitializedHook();

      act(() => {
        mockSecureFieldsInstance._triggerEvent('error', {
          field: 'cardNumber',
          message: 'Invalid card number',
        });
      });

      act(() => {
        mockSecureFieldsInstance._triggerEvent('error', {
          field: 'cvv',
          message: 'Invalid CVV',
        });
      });

      expect(result.current.errors).toEqual({
        cardNumber: 'Invalid card number',
        cvv: 'Invalid CVV',
      });
    });

    it('calls the onError callback with a field-qualified error', async () => {
      const onError = jest.fn();

      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ transactionId: 'txn-abc-123' }),
      });

      const { result } = renderHook(() =>
        useDatatransSecureFields({ ...defaultConfig, isVisible: true, onError })
      );

      await waitFor(() => expect(result.current.isInitialized).toBe(true));

      act(() => {
        mockSecureFieldsInstance._triggerEvent('error', {
          field: 'cvv',
          message: 'Invalid CVV',
        });
      });

      expect(onError).toHaveBeenCalledWith(
        expect.objectContaining({ message: 'cvv: Invalid CVV' })
      );
    });
  });

  // ─── Success event ────────────────────────────────────────────────────────

  describe('SecureFields success event', () => {
    it('calls the onSuccess callback with transactionId data', async () => {
      const onSuccess = jest.fn();

      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ transactionId: 'txn-abc-123' }),
      });

      const { result } = renderHook(() =>
        useDatatransSecureFields({ ...defaultConfig, isVisible: true, onSuccess })
      );

      await waitFor(() => expect(result.current.isInitialized).toBe(true));

      act(() => {
        mockSecureFieldsInstance._triggerEvent('success', { transactionId: 'txn-success-789' });
      });

      expect(onSuccess).toHaveBeenCalledWith({ transactionId: 'txn-success-789' });
    });
  });

  // ─── submitPayment ────────────────────────────────────────────────────────

  describe('submitPayment', () => {
    it('calls secureFields.submit() with expm and expy (converts 4-digit year)', async () => {
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ transactionId: 'txn-abc-123' }),
      });

      const { result } = renderHook(() =>
        useDatatransSecureFields({ ...defaultConfig, isVisible: true })
      );

      await waitFor(() => expect(result.current.isInitialized).toBe(true));
      act(() => {
        mockSecureFieldsInstance._triggerEvent('ready');
      });

      act(() => {
        result.current.submitPayment('12', '2025', 'John Doe');
      });

      expect(mockSecureFieldsInstance.submit).toHaveBeenCalledWith(
        expect.objectContaining({ expm: '12', expy: '25' })
      );
    });

    it('passes a 2-digit year unchanged', async () => {
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ transactionId: 'txn-abc-123' }),
      });

      const { result } = renderHook(() =>
        useDatatransSecureFields({ ...defaultConfig, isVisible: true })
      );

      await waitFor(() => expect(result.current.isInitialized).toBe(true));
      act(() => {
        mockSecureFieldsInstance._triggerEvent('ready');
      });

      act(() => {
        result.current.submitPayment('06', '28', 'Jane Smith');
      });

      expect(mockSecureFieldsInstance.submit).toHaveBeenCalledWith(
        expect.objectContaining({ expm: '06', expy: '28' })
      );
    });

    it('does not throw when SecureFields instance is not yet initialised', () => {
      const { result } = renderHook(() => useDatatransSecureFields(defaultConfig));

      expect(() => {
        act(() => {
          result.current.submitPayment('01', '26', 'Test User');
        });
      }).not.toThrow();
    });
  });

  // ─── Cleanup on unmount ───────────────────────────────────────────────────

  describe('Cleanup on unmount', () => {
    it('calls destroy() on the SecureFields instance when unmounted', async () => {
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ transactionId: 'txn-abc-123' }),
      });

      const { result, unmount } = renderHook(() =>
        useDatatransSecureFields({ ...defaultConfig, isVisible: true })
      );

      await waitFor(() => expect(result.current.isInitialized).toBe(true));

      unmount();

      expect(mockSecureFieldsInstance.destroy).toHaveBeenCalled();
    });
  });

  // ─── Re-init when visible again ───────────────────────────────────────────

  describe('Re-initialisation on visibility change', () => {
    it('resets state and fetches a new transaction when isVisible flips back to true', async () => {
      (global.fetch as jest.Mock)
        .mockResolvedValueOnce({
          ok: true,
          json: async () => ({ transactionId: 'txn-first' }),
        })
        .mockResolvedValueOnce({
          ok: true,
          json: async () => ({ transactionId: 'txn-second' }),
        });

      let isVisible = true;
      const { result, rerender } = renderHook(() =>
        useDatatransSecureFields({ ...defaultConfig, isVisible })
      );

      await waitFor(() => expect(result.current.isInitialized).toBe(true));

      // Hide
      isVisible = false;
      rerender();

      // Show again
      isVisible = true;
      rerender();

      await waitFor(() => expect(global.fetch).toHaveBeenCalledTimes(2));
    });
  });

  // ─── isInitialising flag ──────────────────────────────────────────────────

  describe('isInitialising flag', () => {
    it('is false when isVisible is false', () => {
      const { result } = renderHook(() => useDatatransSecureFields(defaultConfig));
      expect(result.current.isInitialising).toBe(false);
    });

    it('is true immediately when isVisible becomes true (before ready fires)', async () => {
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ transactionId: 'txn-abc-123' }),
      });

      const { result } = renderHook(() =>
        useDatatransSecureFields({ ...defaultConfig, isVisible: true })
      );

      await waitFor(() => expect(result.current.isInitialized).toBe(true));

      // ready has not fired yet — still initialising
      expect(result.current.isInitialising).toBe(true);
    });

    it('becomes false once the ready event fires', async () => {
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ transactionId: 'txn-abc-123' }),
      });

      const { result } = renderHook(() =>
        useDatatransSecureFields({ ...defaultConfig, isVisible: true })
      );

      await waitFor(() => expect(result.current.isInitialized).toBe(true));

      act(() => {
        mockSecureFieldsInstance._triggerEvent('ready');
      });

      expect(result.current.isInitialising).toBe(false);
      expect(result.current.isReady).toBe(true);
    });
  });

  // ─── submitPayment isReady guard ──────────────────────────────────────────

  describe('submitPayment — isReady guard', () => {
    it('does not call secureFields.submit() when iframes are not ready yet', async () => {
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ transactionId: 'txn-abc-123' }),
      });

      const { result } = renderHook(() =>
        useDatatransSecureFields({ ...defaultConfig, isVisible: true })
      );

      await waitFor(() => expect(result.current.isInitialized).toBe(true));
      // ready event has NOT fired — isReady is still false

      act(() => {
        result.current.submitPayment('12', '25', 'John Doe');
      });

      expect(mockSecureFieldsInstance.submit).not.toHaveBeenCalled();
    });

    it('calls secureFields.submit() once the iframes are ready', async () => {
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ transactionId: 'txn-abc-123' }),
      });

      const { result } = renderHook(() =>
        useDatatransSecureFields({ ...defaultConfig, isVisible: true })
      );

      await waitFor(() => expect(result.current.isInitialized).toBe(true));

      act(() => {
        mockSecureFieldsInstance._triggerEvent('ready');
      });

      act(() => {
        result.current.submitPayment('06', '27', 'Jane Smith');
      });

      expect(mockSecureFieldsInstance.submit).toHaveBeenCalledTimes(1);
    });
  });

  // ─── onSubmitValidationFailed callback ────────────────────────────────────

  describe('onSubmitValidationFailed callback', () => {
    async function setupReadyHook(onSubmitValidationFailed?: () => void) {
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ transactionId: 'txn-abc-123' }),
      });

      const rendered = renderHook(() =>
        useDatatransSecureFields({ ...defaultConfig, isVisible: true, onSubmitValidationFailed })
      );

      await waitFor(() => expect(rendered.result.current.isInitialized).toBe(true));
      act(() => {
        mockSecureFieldsInstance._triggerEvent('ready');
      });
      return rendered;
    }

    it('is called when validate fires with at least one invalid field', async () => {
      const onSubmitValidationFailed = jest.fn();
      await setupReadyHook(onSubmitValidationFailed);

      act(() => {
        mockSecureFieldsInstance._triggerEvent('validate', {
          fields: { cardNumber: { valid: false }, cvv: { valid: true } },
        });
      });

      expect(onSubmitValidationFailed).toHaveBeenCalledTimes(1);
    });

    it('treats omitted validate fields as invalid and marks both hosted fields touched', async () => {
      const onSubmitValidationFailed = jest.fn();
      const { result } = await setupReadyHook(onSubmitValidationFailed);

      act(() => {
        mockSecureFieldsInstance._triggerEvent('validate', {
          fields: {},
        });
      });

      expect(onSubmitValidationFailed).toHaveBeenCalledTimes(1);
      expect(result.current.isValid).toBe(false);
      expect(result.current.fieldTouched).toEqual({ cardNumber: true, cvv: true });
      expect(result.current.fieldValidity).toEqual({ cardNumber: false, cvv: false });
    });

    it('is NOT called when all fields are valid', async () => {
      const onSubmitValidationFailed = jest.fn();
      await setupReadyHook(onSubmitValidationFailed);

      act(() => {
        mockSecureFieldsInstance._triggerEvent('validate', {
          fields: { cardNumber: { valid: true }, cvv: { valid: true } },
        });
      });

      expect(onSubmitValidationFailed).not.toHaveBeenCalled();
    });

    it('does not throw when onSubmitValidationFailed is not provided', async () => {
      await setupReadyHook(undefined);

      expect(() => {
        act(() => {
          mockSecureFieldsInstance._triggerEvent('validate', {
            fields: { cardNumber: { valid: false }, cvv: { valid: false } },
          });
        });
      }).not.toThrow();
    });
  });

  // ─── Library-error shape { error, action } ────────────────────────────────

  describe('SecureFields library error (structural, not field-level)', () => {
    async function setupReadyHook() {
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ transactionId: 'txn-abc-123' }),
      });

      const onError = jest.fn();
      const rendered = renderHook(() =>
        useDatatransSecureFields({ ...defaultConfig, isVisible: true, onError })
      );

      await waitFor(() => expect(rendered.result.current.isInitialized).toBe(true));
      act(() => {
        mockSecureFieldsInstance._triggerEvent('ready');
      });
      return { ...rendered, onError };
    }

    it('does not call onError when the library emits { error, action } shape', async () => {
      const { onError } = await setupReadyHook();

      act(() => {
        mockSecureFieldsInstance._triggerEvent('error', {
          error: 'Form expired',
          action: 'submit',
        });
      });

      expect(onError).not.toHaveBeenCalled();
    });

    it('does not update errors state for library-level errors', async () => {
      const { result } = await setupReadyHook();

      act(() => {
        mockSecureFieldsInstance._triggerEvent('error', {
          error: 'Form expired',
          action: 'submit',
        });
      });

      expect(result.current.errors).toEqual({});
    });
  });

  // ─── reinit ───────────────────────────────────────────────────────────────

  describe('reinit()', () => {
    it('triggers a fresh API fetch and re-initialises the SDK', async () => {
      (global.fetch as jest.Mock)
        .mockResolvedValueOnce({
          ok: true,
          json: async () => ({ transactionId: 'txn-first' }),
        })
        .mockResolvedValueOnce({
          ok: true,
          json: async () => ({ transactionId: 'txn-second' }),
        });

      const { result } = renderHook(() =>
        useDatatransSecureFields({ ...defaultConfig, isVisible: true })
      );

      await waitFor(() => expect(result.current.isInitialized).toBe(true));

      act(() => {
        result.current.reinit();
      });

      await waitFor(() => expect(global.fetch).toHaveBeenCalledTimes(2));
    });

    it('sets isInitialising back to true while the new session is being set up', async () => {
      (global.fetch as jest.Mock)
        .mockResolvedValueOnce({
          ok: true,
          json: async () => ({ transactionId: 'txn-first' }),
        })
        .mockResolvedValueOnce({
          ok: true,
          json: async () => ({ transactionId: 'txn-second' }),
        });

      const { result } = renderHook(() =>
        useDatatransSecureFields({ ...defaultConfig, isVisible: true })
      );

      await waitFor(() => expect(result.current.isInitialized).toBe(true));
      act(() => {
        mockSecureFieldsInstance._triggerEvent('ready');
      });
      expect(result.current.isInitialising).toBe(false);

      act(() => {
        result.current.reinit();
      });

      // After reinit the state resets to INITIAL_STATE with isInitialising: true
      await waitFor(() => expect(result.current.isInitialising).toBe(true));
    });

    it('destroys the previous SDK instance before creating a new one', async () => {
      (global.fetch as jest.Mock)
        .mockResolvedValueOnce({
          ok: true,
          json: async () => ({ transactionId: 'txn-first' }),
        })
        .mockResolvedValueOnce({
          ok: true,
          json: async () => ({ transactionId: 'txn-second' }),
        });

      const { result } = renderHook(() =>
        useDatatransSecureFields({ ...defaultConfig, isVisible: true })
      );

      await waitFor(() => expect(result.current.isInitialized).toBe(true));

      act(() => {
        result.current.reinit();
      });

      await waitFor(() => expect(global.fetch).toHaveBeenCalledTimes(2));
      expect(mockSecureFieldsInstance.destroy).toHaveBeenCalled();
    });
  });
});
