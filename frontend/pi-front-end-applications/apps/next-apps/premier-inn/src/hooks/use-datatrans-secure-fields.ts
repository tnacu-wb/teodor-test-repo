import { useCallback, useEffect, useRef, useState } from 'react';

interface SecureFieldsState {
  isInitialized: boolean;
  isReady: boolean;
  /**
   * True from the moment isVisible becomes true until the iframes fire the
   * `ready` event. During this window the SDK instance is not yet usable and
   * calling submit() will produce a "Form expired" error from the library.
   */
  isInitialising: boolean;
  isValid: boolean;
  errors: Record<string, string>;
  sessionError: Error | null;
  /** Per-field validity. null = field has never been touched by the user. */
  fieldValidity: { cardNumber: boolean | null; cvv: boolean | null };
  /** Whether the user has interacted with each field at least once. */
  fieldTouched: { cardNumber: boolean; cvv: boolean };
}

interface SecureFieldsConfig {
  basketId: string;
  country: string;
  language: string;
  isVisible: boolean;
  onSuccess?: (data: { transactionId: string; redirect?: string }) => void;
  onError?: (error: Error) => void;
  /**
   * Called when the SDK's validate event fires with errors (i.e. the user
   * clicked Pay but the card number or CVV are invalid). Use this to clear
   * any submit-in-progress loading state so the button becomes clickable again.
   */
  onSubmitValidationFailed?: () => void;
  styles?: Record<string, string | Record<string, string>>;
}

interface SecureFieldsResult extends SecureFieldsState {
  submitPayment: (
    expiryMonth: string,
    expiryYear: string,
    cardholderName: string,
    guestEmail?: string,
    billingAddress?: {
      addressLine1?: string;
      cityName?: string;
      postalCode?: string;
      countryCode?: string | number;
    }
  ) => void;
  /**
   * Tears down the current SDK instance and starts a fresh session
   * (new transactionId fetch + iframe reinit). Call this after a failed
   * authorize so the user can retry — the SDK marks its session as consumed
   * after the first successful submit() and will error on any subsequent call.
   */
  reinit: () => void;
}

declare global {
  interface Window {
    SecureFields?: new () => SecureFieldsInstance;
  }
}

interface SecureFieldsInstance {
  init(
    transactionId: string,
    fields: Record<
      string,
      string | { placeholderElementId: string; inputType?: string; placeholder?: string }
    >,
    options?: {
      styles?: Record<string, string | Record<string, string>>;
      focus?: string;
      paymentMethods?: string[];
    }
  ): void;
  on(event: 'ready', callback: () => void): void;
  on(
    event: 'validate' | 'change',
    callback: (event: { fields: Record<string, { valid: boolean }> }) => void
  ): void;
  on(
    event: 'success',
    callback: (data: { transactionId: string; redirect?: string }) => void
  ): void;
  on(event: 'error', callback: (error: unknown) => void): void;
  on(event: string, callback: (...args: unknown[]) => void): void;
  setPlaceholder(field: 'cardNumber' | 'cvv', placeholder: string): void;
  submit(options: {
    expm: string;
    expy: string;
    '3D'?: {
      cardholder?: {
        cardholderName?: string;
        email?: string;
        billAddrLine1?: string;
        billAddrCity?: string;
        billAddrPostCode?: string;
        billAddrCountry?: string;
      };
    };
  }): void;
  destroy?(): void;
}

const INITIAL_STATE: SecureFieldsState = {
  isInitialized: false,
  isReady: false,
  isInitialising: false,
  isValid: false,
  errors: {},
  sessionError: null,
  fieldValidity: { cardNumber: null, cvv: null },
  fieldTouched: { cardNumber: false, cvv: false },
};

/**
 * Manages the Datatrans Secure Fields session lifecycle.
 *
 * DatatransSecureFieldsForm stays mounted for the lifetime of the payment page
 * and is only shown/hidden via CSS. This hook ties the session init to `isVisible`
 * rather than component mount, so:
 *
 * - First time isVisible becomes true → fetch a transactionId, init the iframes.
 * - isVisible goes false (user switches method) → session stays alive, iframes hidden.
 * - isVisible becomes true again → fetch a fresh transactionId, reinit the iframes.
 *
 * Because the component never unmounts, there is no StrictMode double-invoke
 * and no race between mount/unmount cycles.
 */
export function useDatatransSecureFields(config: SecureFieldsConfig): SecureFieldsResult {
  const {
    basketId,
    country,
    language,
    isVisible,
    onSuccess,
    onError,
    onSubmitValidationFailed,
    styles,
  } = config;

  const [state, setState] = useState<SecureFieldsState>(INITIAL_STATE);
  const secureFieldsRef = useRef<SecureFieldsInstance | null>(null);
  const isMountedRef = useRef(true);
  const onSuccessRef = useRef(onSuccess);
  const onErrorRef = useRef(onError);
  const onSubmitValidationFailedRef = useRef(onSubmitValidationFailed);
  // Incrementing this triggers the init effect to re-run, fetching a fresh
  // transactionId and reinitialising the iframes. Used after a failed authorize.
  const [reinitCounter, setReinitCounter] = useState(0);

  useEffect(() => {
    onSuccessRef.current = onSuccess;
  }, [onSuccess]);

  useEffect(() => {
    onErrorRef.current = onError;
  }, [onError]);

  useEffect(() => {
    onSubmitValidationFailedRef.current = onSubmitValidationFailed;
  }, [onSubmitValidationFailed]);

  // Session init runs when the card form becomes visible and basketId is present.
  // Fetches a fresh transactionId each time — the orchestrator cancels any prior
  // unauthorised transaction automatically.
  useEffect(() => {
    if (!isVisible || !basketId) return;

    // Mark as initialising immediately so the Pay button can be disabled while
    // we fetch the transactionId and wait for the iframes to become ready.
    setState({ ...INITIAL_STATE, isInitialising: true });

    let cancelled = false;

    const waitForSdk = (): Promise<new () => SecureFieldsInstance> =>
      new Promise((resolve, reject) => {
        if (window.SecureFields) {
          resolve(window.SecureFields);
          return;
        }
        // The script loads via <Script strategy="afterInteractive"> in DatatransPage
        // and will typically be ready within a few hundred ms.
        const interval = setInterval(() => {
          if (window.SecureFields) {
            clearInterval(interval);
            clearTimeout(timeout);
            resolve(window.SecureFields);
          }
        }, 100);
        const timeout = setTimeout(() => {
          clearInterval(interval);
          reject(new Error('Datatrans SecureFields SDK did not load within 10 seconds'));
        }, 10_000);
      });

    const initSession = async () => {
      try {
        const response = await fetch('/api/payments/secure-fields', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ basketId, country, language }),
        });

        if (!response.ok) {
          throw new Error(`Failed to initialize payment session: ${response.status}`);
        }

        const { transactionId } = await response.json();

        if (cancelled || !isMountedRef.current) return;

        const SecureFieldsCtor = await waitForSdk();

        if (cancelled || !isMountedRef.current) return;

        if (secureFieldsRef.current?.destroy) {
          secureFieldsRef.current.destroy();
        }

        const instance = new SecureFieldsCtor();
        // Assign a unique ID to this instance so we can detect stale-instance submits in logs.
        const instanceId = `sf-${Date.now()}`;
        (instance as SecureFieldsInstance & { _instanceId?: string })._instanceId = instanceId;
        secureFieldsRef.current = instance;
        // eslint-disable-next-line no-console
        console.log('[Datatrans] new instance created', { instanceId, transactionId });

        instance.on('ready', () => {
          // eslint-disable-next-line no-console
          console.log('[Datatrans] ready', { instanceId });
          instance.setPlaceholder('cardNumber', 'Card number');
          instance.setPlaceholder('cvv', 'CVV');
          if (isMountedRef.current)
            setState((prev) => ({ ...prev, isReady: true, isInitialising: false }));
        });

        instance.on('validate', (event: { fields: Record<string, { valid: boolean }> }) => {
          // eslint-disable-next-line no-console
          console.log('[Datatrans] validate', event.fields);
          if (isMountedRef.current) {
            // Some SDK validate payloads omit untouched fields; treat missing
            // required fields as invalid so submit-failed UX is consistent.
            const cardNumberValid = event.fields.cardNumber?.valid ?? false;
            const cvvValid = event.fields.cvv?.valid ?? false;
            const allValid = cardNumberValid && cvvValid;
            setState((prev) => ({
              ...prev,
              isValid: allValid,
              // On submit-triggered validate, mark all fields as touched
              fieldTouched: { cardNumber: true, cvv: true },
              fieldValidity: {
                cardNumber: cardNumberValid,
                cvv: cvvValid,
              },
            }));
            // If the SDK found invalid fields, submit is blocked — notify the parent
            // so it can clear the in-progress loading state on the Pay button.
            if (!allValid) {
              onSubmitValidationFailedRef.current?.();
            }
          }
        });

        instance.on(
          'change',
          (event: {
            fields: Record<string, { valid: boolean }>;
            event?: { field?: string; type?: string };
          }) => {
            // eslint-disable-next-line no-console
            console.log('[Datatrans] change', event.fields);
            if (isMountedRef.current) {
              const allValid = Object.values(event.fields).every((f) => f.valid);
              const changedField = event.event?.field as 'cardNumber' | 'cvv' | undefined;
              const isBlur = event.event?.type === 'blur';

              setState((prev) => ({
                ...prev,
                isValid: allValid,
                fieldValidity: {
                  cardNumber: event.fields.cardNumber?.valid ?? prev.fieldValidity.cardNumber,
                  cvv: event.fields.cvv?.valid ?? prev.fieldValidity.cvv,
                },
                // Mark a field as touched when the user blurs out of it
                fieldTouched: {
                  cardNumber:
                    prev.fieldTouched.cardNumber || (isBlur && changedField === 'cardNumber'),
                  cvv: prev.fieldTouched.cvv || (isBlur && changedField === 'cvv'),
                },
              }));
            }
          }
        );

        instance.on('success', (data: { transactionId: string; redirect?: string }) => {
          // eslint-disable-next-line no-console
          console.log('[Datatrans] success', {
            transactionId: data.transactionId,
            hasRedirect: !!data.redirect,
          });
          if (isMountedRef.current && onSuccessRef.current) onSuccessRef.current(data);
        });

        instance.on('error', (error: unknown) => {
          // eslint-disable-next-line no-console
          console.log('[Datatrans] error (raw)', error);
          if (!isMountedRef.current) return;

          // Structural errors emitted by the JS library — not field-level validation.
          // e.g. { error: "Form expired", action: "submit" } fires when submit() is
          // called against an instance whose iframes are no longer valid. This should
          // not reach the user; it indicates a stale-instance submit (see submitPayment
          // guard below) and is logged for diagnostics only.
          if (typeof error === 'object' && error !== null && 'error' in error) {
            // eslint-disable-next-line no-console
            console.warn('[Datatrans] library error — not surfaced to user', {
              error,
              instanceId: (instance as SecureFieldsInstance & { _instanceId?: string })._instanceId,
              currentInstanceId: (
                secureFieldsRef.current as (SecureFieldsInstance & { _instanceId?: string }) | null
              )?._instanceId,
              isInstanceStale: secureFieldsRef.current !== instance,
            });
            return;
          }

          if (typeof error === 'string') return;
          const fieldError = error as { field: string; message: string };
          if (fieldError.field !== 'cardNumber' && fieldError.field !== 'cvv') return;
          setState((prev) => ({
            ...prev,
            errors: { ...prev.errors, [fieldError.field]: fieldError.message },
          }));
          if (onErrorRef.current) {
            onErrorRef.current(new Error(`${fieldError.field}: ${fieldError.message}`));
          }
        });

        // eslint-disable-next-line no-console
        console.log('[Datatrans] init', { instanceId, transactionId });
        instance.init(
          transactionId,
          { cardNumber: 'datatrans-cardNumber', cvv: 'datatrans-cvv' },
          { ...(styles ? { styles } : {}) }
        );

        if (isMountedRef.current) {
          setState((prev) => ({ ...prev, isInitialized: true }));
        }
      } catch (error) {
        if (!cancelled && isMountedRef.current) {
          setState((prev) => ({
            ...prev,
            sessionError: error instanceof Error ? error : new Error(String(error)),
          }));
        }
      }
    };

    initSession();

    return () => {
      cancelled = true;
    };
  }, [isVisible, basketId, country, language, reinitCounter]); // eslint-disable-line react-hooks/exhaustive-deps
  // `styles` intentionally omitted — stable object, including it would restart the session on every render.

  useEffect(() => {
    isMountedRef.current = true;
    return () => {
      isMountedRef.current = false;
      if (secureFieldsRef.current?.destroy) {
        secureFieldsRef.current.destroy();
      }
      secureFieldsRef.current = null;
    };
  }, []);

  const submitPayment = useCallback(
    (
      expiryMonth: string,
      expiryYear: string,
      cardholderName: string,
      guestEmail?: string,
      billingAddress?: {
        addressLine1?: string;
        cityName?: string;
        postalCode?: string;
        countryCode?: string | number;
      }
    ) => {
      const instance = secureFieldsRef.current;
      if (!instance) {
        // eslint-disable-next-line no-console
        console.warn('[Datatrans] submitPayment called but no SecureFields instance exists');
        return;
      }

      // Guard against calling submit() before the iframes have fired `ready`.
      // This is the root cause of the "Form expired" library error: the SDK
      // instance exists but its internal iframe context is not yet valid.
      const instanceId = (instance as SecureFieldsInstance & { _instanceId?: string })._instanceId;
      if (!state.isReady) {
        // eslint-disable-next-line no-console
        console.warn('[Datatrans] submitPayment called before iframes are ready — aborting', {
          instanceId,
          isInitialising: state.isInitialising,
          isInitialized: state.isInitialized,
        });
        return;
      }

      const twoDigitYear = expiryYear.length === 4 ? expiryYear.slice(-2) : expiryYear;
      const submitOptions = {
        expm: expiryMonth,
        expy: twoDigitYear,
        '3D': {
          cardholder: {
            cardholderName,
            ...(guestEmail ? { email: guestEmail } : {}),
            ...(billingAddress?.addressLine1 ? { billAddrLine1: billingAddress.addressLine1 } : {}),
            ...(billingAddress?.cityName ? { billAddrCity: billingAddress.cityName } : {}),
            ...(billingAddress?.postalCode ? { billAddrPostCode: billingAddress.postalCode } : {}),
            ...(billingAddress?.countryCode
              ? { billAddrCountry: String(billingAddress.countryCode) }
              : {}),
          },
        },
      };
      // eslint-disable-next-line no-console
      console.log('[Datatrans] submit', { instanceId, ...submitOptions });
      instance.submit(submitOptions);
    },
    [state.isReady, state.isInitialising, state.isInitialized]
  );

  const reinit = useCallback(() => {
    // eslint-disable-next-line no-console
    console.log('[Datatrans] reinit triggered — fetching fresh transactionId');
    setReinitCounter((c) => c + 1);
  }, []);

  return { ...state, submitPayment, reinit };
}
