import { Box } from '@chakra-ui/react';
import getConfig from 'next/config';
import Script from 'next/script';
import { useEffect, useRef, useState } from 'react';

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
 * 1. Loads the Datatrans Payment Button JS script via Next.js <Script>
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
  const { publicRuntimeConfig = {} } = getConfig() || {};
  const containerRef = useRef<HTMLDivElement>(null);
  const [isScriptReady, setIsScriptReady] = useState(false);
  const buttonInstanceRef = useRef<PaymentButtonInstance | null>(null);
  const onAuthRef = useRef(onAuthorization);
  const onErrorRef = useRef(onError);

  const scriptUrl = publicRuntimeConfig.NEXT_PUBLIC_DATATRANS_PAYMENT_BUTTON_URL;
  const merchantId = publicRuntimeConfig.NEXT_PUBLIC_DATATRANS_MERCHANT_ID;
  const googlePayMerchantId = publicRuntimeConfig.NEXT_PUBLIC_GOOGLE_PAY_MERCHANT_ID;

  useEffect(() => {
    onAuthRef.current = onAuthorization;
  }, [onAuthorization]);

  useEffect(() => {
    onErrorRef.current = onError;
  }, [onError]);

  // Initialize and render the Payment Button once script is ready
  useEffect(() => {
    console.log('APGP----', isScriptReady, window.PaymentButton);
    if (!isScriptReady || !window.PaymentButton || !containerRef.current) return;
    console.log('MID----', merchantId, walletType);

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
      console.log('called init method------');
      alert('init');

      if (containerRef.current) {
        paymentButton.create(containerRef.current, payment);
      }
    });
    console.log('inti444------', initConfig);

    // Listen for authorization — payment complete
    paymentButton.on('authorization', (data: unknown) => {
      alert('success');

      console.log('called authorization method------', data);
      onAuthRef.current(data as { transactionId: string });
    });
    console.log('inti555------', initConfig);

    // Listen for errors
    paymentButton.on('error', (data: unknown) => {
      console.log('called error method------', data);
      alert('error');

      onErrorRef.current(data instanceof Error ? data : new Error(`${walletType} payment failed`));
    });

    // Initialize
    console.log('Initializing Datatrans Payment Button', initConfig);
    paymentButton.init(initConfig);
  }, [isScriptReady, amount, currencyCode, basketId, walletType, merchantId, googlePayMerchantId]);

  if (!scriptUrl) {
    return null;
  }

  return (
    <>
      <Script
        src={scriptUrl}
        strategy="afterInteractive"
        onReady={() => setIsScriptReady(true)}
        onError={() => onError(new Error('Failed to load Datatrans Payment Button script'))}
      />
      <Box
        ref={containerRef}
        data-testid={`DatatransPaymentButton-${walletType}-Container`}
        minH="3rem"
        mt="lg"
        w={{ mobile: 'full', lg: '18rem', xl: '19.3125rem' }}
      />
    </>
  );
}
