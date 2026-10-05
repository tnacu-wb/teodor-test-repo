import { Flex } from '@chakra-ui/react';
import { PayPalScriptProvider } from '@paypal/react-paypal-js';
import {
  Area,
  QueryHotelInformationArgs,
  PackagesCriteria,
  PaymentMethod,
  BASKET_STATUS,
} from '@whitbread-eos/api';
import { LoadingSpinner } from '@whitbread-eos/atoms';
import { useCustomLocale, usePaymentPaypal } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useRouter } from 'next/router';
import { useEffect, useRef } from 'react';

import { DatatransPage } from '~components/payment/datatrans/DatatransPage';

import {
  PAYMENT_NAVIGATION_SOURCE_GUEST_DETAILS,
  PAYMENT_NAVIGATION_SOURCE_QUERY_PARAM,
} from '../../utils/pi-all-pages-constants';

interface Props {
  hiQueryInput: QueryHotelInformationArgs | null;
  pcksQueryInput: PackagesCriteria | null;
  basketReference: string | null;
  paypalPaymentData?: PaymentMethod | null;
  paymentStatus?: { basket: { status: BASKET_STATUS } };
  isDatatransReturn?: boolean;
}

/**
 * Handles the 3DS return path.
 *
 * Renders nothing — fires authorize immediately, then:
 * - success  → router.replace(...confirmation)
 * - failure  → router.replace(...payment?3ds_failed=true)
 *
 * authorizeCalledRef is safe here because isDatatransReturn is set
 * server-side (getServerSideProps) so this component is stable for the
 * lifetime of the page — it never remounts due to client-side state changes.
 */
export function DatatransWith3dsReturn({
  basketReference,
}: Readonly<Pick<Props, 'basketReference'>>) {
  const router = useRouter();
  const { language, country } = useCustomLocale();
  const { t } = useTranslation(['common']);
  const authorizeCalledRef = useRef(false);

  useEffect(() => {
    if (!basketReference || authorizeCalledRef.current) return;
    authorizeCalledRef.current = true;

    const bookingFlowId = sessionStorage.getItem('bookingFlowId') ?? '';
    const base = `/${country}/${language}${bookingFlowId ? `/${bookingFlowId}` : ''}`;
    const confirmationPath = `${base}/confirmation?reservationId=${basketReference}`;
    const failureSearchParams = new URLSearchParams({
      reservationId: basketReference,
      '3ds_failed': 'true',
    });

    if (
      router.query[PAYMENT_NAVIGATION_SOURCE_QUERY_PARAM] ===
      PAYMENT_NAVIGATION_SOURCE_GUEST_DETAILS
    ) {
      failureSearchParams.set(
        PAYMENT_NAVIGATION_SOURCE_QUERY_PARAM,
        PAYMENT_NAVIGATION_SOURCE_GUEST_DETAILS
      );
    }

    const failurePath = `${base}/payment?${failureSearchParams.toString()}`;

    const authorize = async () => {
      // eslint-disable-next-line no-console
      console.log('[authorize] 3DS return', { basketReference, confirmationPath });
      try {
        const response = await fetch('/api/payments/authorize', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ basketId: basketReference }),
        });
        // eslint-disable-next-line no-console
        console.log('[authorize] 3DS response', { status: response.status, ok: response.ok });
        if (response.ok) {
          router.replace(confirmationPath);
        } else {
          // Hard navigation so getServerSideProps re-runs and pre-fetches booking data
          // for the full payment page. A client-side replace would skip SSR and leave
          // bkngData undefined since the original 3DS return skipped all queries.
          window.location.href = failurePath;
        }
      } catch (error) {
        // eslint-disable-next-line no-console
        console.error('[authorize] 3DS fetch error', error);
        window.location.href = failurePath;
      }
    };

    authorize();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [basketReference, country, language]);

  return (
    <Flex minH="50vh" align="center" justify="center">
      <LoadingSpinner loadingText={t('booking.loading')} />
    </Flex>
  );
}

export function DatatransPageWrapper({
  pcksQueryInput,
  hiQueryInput,
  basketReference,
  paypalPaymentData,
  isDatatransReturn,
}: Readonly<Props>) {
  if (isDatatransReturn) {
    return <DatatransWith3dsReturn basketReference={basketReference} />;
  }

  if (!hiQueryInput || !pcksQueryInput) {
    return null;
  }

  return (
    <DatatransPage {...{ pcksQueryInput, hiQueryInput, basketReference, paypalPaymentData }} />
  );
}

export default function PaymentPagePiNew(props: Readonly<Props>) {
  const { basketReference, isDatatransReturn } = props;
  const { language, country } = useCustomLocale();

  const { injectPaypalProvider, paypalPaymentData } = usePaymentPaypal(
    basketReference ?? '',
    Area.PI.toUpperCase()
  );

  // On a 3DS return there is no PayPal UI — render the wrapper directly
  // so the PayPalScriptProvider never mounts and can't cause remounts.
  if (isDatatransReturn) {
    return <DatatransPageWrapper {...props} />;
  }

  return (
    <>
      {injectPaypalProvider ? (
        <PayPalScriptProvider
          options={{
            clientId: paypalPaymentData?.clientId as string,
            dataUserIdToken: paypalPaymentData?.clientToken as string,
            intent: 'tokenize',
            vault: true,
            locale: `${language}_${country.toString().toUpperCase()}`,
            components: 'buttons',
          }}
        >
          <DatatransPageWrapper paypalPaymentData={paypalPaymentData} {...props} />
        </PayPalScriptProvider>
      ) : (
        <DatatransPageWrapper {...props} />
      )}
    </>
  );
}
