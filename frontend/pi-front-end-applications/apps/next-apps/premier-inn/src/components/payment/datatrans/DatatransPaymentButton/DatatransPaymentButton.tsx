import { Box } from '@chakra-ui/react';
import { useEffect, useRef, useState } from 'react';

import { containerStyle } from './DatatransPaymentButton.styles';

declare global {
  interface Window {
    PaymentButton?: new () => PaymentButtonInstance;
  }
}

interface PaymentButtonInstance {
  init(options: Record<string, unknown>): void;
  on(event: string, callback: (...args: unknown[]) => void): void;
  create(element: HTMLElement, payment: Record<string, unknown>): void;
}

export type WalletType = 'APPLE_PAY' | 'GOOGLE_PAY';

interface DatatransPaymentButtonProps {
  basketId: string;
  amount: string;
  currencyCode: string;
  walletType: WalletType;
  onAuthorization: (data: { transactionId: string }) => void;
  onError: (error: Error) => void;
}

/**
 * Datatrans Payment Button component (v3).
 *
 * Implements: https://docs.datatrans.ch/docs/payment-button
 *
 * Renders either Apple Pay or Google Pay button via the Datatrans Payment Button
 * JS library. The library auto-detects device capability for the selected wallet type.
 *
 * The script (<Script src={NEXT_PUBLIC_DATATRANS_PAYMENT_BUTTON_URL}>) must be loaded
 * by the parent page (DatatransPage) so it is ready before the user selects a wallet
 * method. This component only handles initialisation once window.PaymentButton is available.
 *
 * 1. Polls window.PaymentButton until the SDK is present
 * 2. Creates a new PaymentButton() instance
 * 3. Calls .init() with merchant config and wallet configuration
 * 4. On "init" event, calls .create() to render the button into the container
 * 5. On "authorization" event, calls onAuthorization callback
 */
export function DatatransPaymentButton({
  basketId,
  amount,
  currencyCode,
  walletType,
  onAuthorization,
  onError,
}: Readonly<DatatransPaymentButtonProps>) {
  const containerRef = useRef<HTMLDivElement>(null);
  const [isScriptReady, setIsScriptReady] = useState(false);
  const buttonInstanceRef = useRef<PaymentButtonInstance | null>(null);
  const isInitializedRef = useRef(false);
  const onAuthRef = useRef(onAuthorization);
  const onErrorRef = useRef(onError);

  const merchantId = process.env.NEXT_PUBLIC_DATATRANS_MERCHANT_ID;
  const googlePayMerchantId = process.env.NEXT_PUBLIC_GOOGLE_PAY_MERCHANT_ID;

  useEffect(() => {
    onAuthRef.current = onAuthorization;
  }, [onAuthorization]);

  useEffect(() => {
    onErrorRef.current = onError;
  }, [onError]);

  // Poll for window.PaymentButton — the script is loaded by DatatransPage so it may
  // already be present on mount (payment method switch) or arrive shortly after.
  useEffect(() => {
    if (window.PaymentButton) {
      setIsScriptReady(true);
      return;
    }
    const interval = setInterval(() => {
      if (window.PaymentButton) {
        clearInterval(interval);
        setIsScriptReady(true);
      }
    }, 100);
    return () => clearInterval(interval);
  }, []);

  // Initialize and render the Payment Button once script is ready.
  // Guard with isInitializedRef so listeners are only registered once —
  // re-registering on prop changes would stack duplicate handlers since
  // PaymentButton exposes no .off() method.
  useEffect(() => {
    if (!isScriptReady || !window.PaymentButton || !containerRef.current) return;
    if (isInitializedRef.current) return;
    isInitializedRef.current = true;

    if (!merchantId) {
      onErrorRef.current(new Error('Datatrans merchant ID is not configured'));
      return;
    }

    // Create a new button instance
    const paymentButton = new window.PaymentButton();
    buttonInstanceRef.current = paymentButton;

    // Build the payment object per W3C Payment Request API
    const payment = {
      details: {
        total: {
          label: 'Premier Inn',
          amount: { value: amount, currency: currencyCode },
        },
      },
      options: {
        requestPayerEmail: false,
        requestPayerName: true,
        requestPayerPhone: false,
        requestShipping: false,
      },
      transaction: {
        countryCode: 'GB',
        refno: basketId,
      },
    };

    // Build init config — always provide BOTH configurations as the SDK
    // internally reads buttonStyle from both objects regardless of device.
    // The SDK renders whichever button the current browser/device supports.
    const initConfig: Record<string, unknown> = {
      merchantId,
      merchantName: 'Premier Inn',
      tokenOnly: false,
      autoSettle: false,
      useApplePay: true,
      useGooglePay: true,
      allowedCardNetworks: ['AMEX', 'MASTERCARD', 'VISA'],
      googlePayConfiguration: {
        buttonType: 'long',
        buttonStyle: 'black',
        ...(googlePayMerchantId && { merchantId: googlePayMerchantId }),
      },
      applePayConfiguration: {
        buttonType: 'plain',
        buttonStyle: 'black',
      },
    };

    // Listen for init event — render the button
    paymentButton.on('init', () => {
      if (containerRef.current) {
        paymentButton.create(containerRef.current, payment);
      }
    });

    // Listen for authorization — payment complete
    paymentButton.on('authorization', (data: unknown) => {
      onAuthRef.current(data as { transactionId: string });
    });

    // Listen for errors
    paymentButton.on('error', (data: unknown) => {
      onErrorRef.current(data instanceof Error ? data : new Error(`${walletType} payment failed`));
    });

    // Initialize
    paymentButton.init(initConfig);
  }, [isScriptReady, amount, currencyCode, basketId, walletType, merchantId, googlePayMerchantId]);

  return (
    <Box
      ref={containerRef}
      data-testid={`DatatransPaymentButton-${walletType}-Container`}
      {...containerStyle}
    />
  );
}
