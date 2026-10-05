import type { QueryHotelInformationArgs, PackagesCriteria } from '@whitbread-eos/api';
import {
  Area,
  FT_PI_BB_NON_GUARANTEED_REMINDER,
  FT_PI_REDIS_RQ_CACHE,
  PageName,
  UnleashChannel,
} from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import {
  getI18nLabels,
  getServerSideCustomLocale,
  getUnleashToggles,
  useFeatureToggle,
  GLOBALS,
  isValidSecureBooking,
  isSecureBookingPage,
  secureBookingType,
} from '@whitbread-eos/utils';
import { GetServerSidePropsContext } from 'next';
import { ReactElement } from 'react';

import { PaymentLayout } from '~components';
import { createPaymentPiDataLoaderFn, PaymentPagePi } from '~page-helper/payment';
import { PAGE } from '~utils/pi-all-pages-constants';
import { clearServerQueryClient, createServerQueryClient } from '~utils/serverQueryClient';

interface Props {
  hiQueryInput: QueryHotelInformationArgs;
  pcksQueryInput: PackagesCriteria;
  basketReference: string | null;
  featureToggles: DynamicObject;
  isDatatransReturn?: boolean;
}

interface DynamicObject {
  [key: string]: boolean;
}

export default function PaymentPage(props: Readonly<Props>) {
  useFeatureToggle(props.featureToggles);

  return <PaymentPagePi {...props} />;
}

PaymentPage.getLayout = function getLayout(page: ReactElement) {
  return (
    <PaymentLayout>
      <ErrorBoundary>{page}</ErrorBoundary>
    </PaymentLayout>
  );
};

export async function getServerSideProps({ locale = 'gb', ...props }: GetServerSidePropsContext) {
  const { language, country } = getServerSideCustomLocale(locale);
  // Required feature toggles used by this page with fallback(safe value)
  // if the Unleash is down or this flag is not configured in unleash

  const featureToggles: DynamicObject = await getUnleashToggles(
    props,
    PAGE.PAYMENT.featureToggles.appPage,
    PAGE.PAYMENT.featureToggles.flagsWithFallback,
    {
      basketReference: props.query?.reservationId as string,
      country: country || GLOBALS.locale.GB,
      channel: UnleashChannel.PI,
      pageName: PageName.PAYMENT.toUpperCase(),
    }
  );

  const queryClient = createServerQueryClient({
    page: PAGE.PAYMENT.featureToggles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });

  try {
    const labels = await getI18nLabels({
      language,
      queryClient,
    });

    const basketReference = props.query?.reservationId as string | undefined;
    const isDatatransReturn = props.query?.source === 'datatrans';

    // On a 3DS return Datatrans redirects back with source=datatrans.
    // Skip all the heavy SSR queries — the wrapper only needs basketReference
    // to call authorize and then redirect.
    if (isDatatransReturn) {
      return {
        props: {
          ...labels,
          featureToggles,
          hiQueryInput: null,
          pcksQueryInput: null,
          basketReference: basketReference ?? null,
          isDatatransReturn: true,
        },
      };
    }

    const loadedData = await createPaymentPiDataLoaderFn({
      queryClient: queryClient,
      language: language,
      country: country,
      ...props,
    });

    const isSecureBookingFeatureEnabled = featureToggles[FT_PI_BB_NON_GUARANTEED_REMINDER];

    // check if the reservation is valid or not in case of secure booking flow. if not valid, show error message
    if (isSecureBookingPage(props?.query as secureBookingType, isSecureBookingFeatureEnabled)) {
      const isValidReservation = await isValidSecureBooking(featureToggles, loadedData, Area.PI);

      // if reservation is not valid, show error message
      if (isValidReservation?.error) {
        return { notFound: true };
      }
    }
    return {
      props: {
        ...loadedData,
        ...labels,
        featureToggles,
      },
    };
  } finally {
    clearServerQueryClient(queryClient);
  }
}
