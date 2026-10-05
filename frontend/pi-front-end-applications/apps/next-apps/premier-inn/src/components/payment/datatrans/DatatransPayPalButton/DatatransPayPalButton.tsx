import { Box } from '@chakra-ui/react';
import Script from 'next/script';
import { useEffect, useRef, useState } from 'react';

import { containerStyle } from './DatatransPayPalButton.styles';

declare global {
  interface Window {
    PayPalButton?: {
      init(options: Record<string, unknown>): void;
      on(event: string, callback: (...args: unknown[]) => void): void;
      create(element: HTMLElement, configuration: Record<string, unknown>): void;
    };
  }
}

interface DatatransPayPalButtonProps {
  basketId: string;
  amount: string;
  currencyCode: string;
  onAuthorization: (data: { transactionId: string }) => void;
  onError: (error: Error) => void;
}

/**
 * Datatrans PayPal Button component.
 *
 * Implements: https://docs.datatrans.ch/docs/paypal-button
 *
 * 1. Loads the Datatrans PayPal Button JS script via Next.js <Script>
 * 2. Calls PayPalButton.init() with merchant config
 * 3. On "init" event, renders the button into the container div
 * 4. On "authorization" event, calls onAuthorization callback
 */
export function DatatransPayPalButton({
  basketId,
  amount,
  currencyCode,
  onAuthorization,
  onError,
}: Readonly<DatatransPayPalButtonProps>) {
  const containerRef = useRef<HTMLDivElement>(null);
  const [isScriptReady, setIsScriptReady] = useState(false);
  const onAuthRef = useRef(onAuthorization);
  const onErrorRef = useRef(onError);
  const isInitializedRef = useRef(false);

  const scriptUrl = process.env.NEXT_PUBLIC_DATATRANS_PAYPAL_BUTTON_URL;
  const merchantId = process.env.NEXT_PUBLIC_DATATRANS_MERCHANT_ID;

  useEffect(() => {
    onAuthRef.current = onAuthorization;
  }, [onAuthorization]);

  useEffect(() => {
    onErrorRef.current = onError;
  }, [onError]);

  // Initialize and render the PayPal button once script is ready.
  // Guard with isInitializedRef so listeners are only registered once —
  // re-registering on prop changes would stack duplicate handlers since
  // PayPalButton exposes no .off() method.
  useEffect(() => {
    if (!isScriptReady || !window.PayPalButton || !containerRef.current) return;
    if (isInitializedRef.current) return;
    isInitializedRef.current = true;

    if (!merchantId) {
      onErrorRef.current(new Error('Datatrans merchant ID is not configured'));
      return;
    }

    // Step 2: Init
    window.PayPalButton.init({
      merchantId,
      currencyCode,
      amount,
      autoSettle: false,
      createAlias: false,
      reqType: 'CAA',
    });

    // Step 3: On "init" event, render button into container
    window.PayPalButton.on('init', () => {
      if (containerRef.current) {
        window.PayPalButton!.create(containerRef.current, {
          paypaloptions: {
            button: {
              layout: 'vertical',
              color: 'gold',
              shape: 'rect',
              label: 'paypal',
            },
          },
          transaction: {
            refno: basketId,
            authenticationOnly: false,
            returnAddress: true,
          },
        });
      }
    });

    // Step 4: Listen for authorization result
    window.PayPalButton.on('authorization', (data: unknown) => {
      onAuthRef.current(data as { transactionId: string });
    });

    // Listen for errors
    window.PayPalButton.on('error', (data: unknown) => {
      onErrorRef.current(data instanceof Error ? data : new Error('PayPal payment failed'));
    });
  }, [isScriptReady, amount, currencyCode, basketId, merchantId]);

  if (!scriptUrl) {
    return null;
  }

  return (
    <>
      <Script
        src={scriptUrl}
        strategy="afterInteractive"
        onReady={() => setIsScriptReady(true)}
        onError={() =>
          onErrorRef.current(new Error('Failed to load Datatrans PayPal Button script'))
        }
      />
      <Box ref={containerRef} data-testid="DatatransPayPalButton-Container" {...containerStyle} />
    </>
  );
}
