import { useQueryClient } from '@tanstack/react-query';
import {
  type QueryBookingInformationArgs,
  type QueryHotelInformationArgs,
  type PackagesCriteria,
  FT_PI_AUTH0_LOGIN,
  FT_PI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE,
  type HIBasicBasketDetails,
  UnleashChannel,
  PageName,
  FT_PI_REDIS_RQ_CACHE,
} from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import {
  decodeFromBase64,
  getI18nLabels,
  getServerSideCustomLocale,
  getUnleashToggles,
  useFeatureToggle,
  isJsonValid,
  GLOBALS,
  logger,
} from '@whitbread-eos/utils';
import { RedisKeyPrefix, RedisStorageServer } from '@whitbread-eos/utils/server';
import type { GetServerSidePropsContext } from 'next';
import { useRouter } from 'next/router';
import type { ReactElement } from 'react';

import { GuestDetailsLayout } from '~components';
import {
  createGuestDetailsPiDataLoaderFn,
  Page as GuestDetailsPagePi,
} from '~page-helper/guest-details';
import { PAGE } from '~utils/pi-all-pages-constants';
import { clearServerQueryClient, createServerQueryClient } from '~utils/serverQueryClient';

import { getAuth0TokenAndEmail } from '../lib/getAuth0Token';

interface DynamicObject {
  [key: string]: boolean;
}
interface Props {
  hiQueryInput: QueryHotelInformationArgs;
  pcksQueryInput: PackagesCriteria;
  biQueryInput: QueryBookingInformationArgs;
  featureToggles: DynamicObject;
  cachedGuestDetailsFormData?: Record<string, unknown>;
  userEmail?: string | null;
}

export default function GuestDetailsPage({
  pcksQueryInput,
  hiQueryInput,
  biQueryInput,
  featureToggles,
  cachedGuestDetailsFormData,
  userEmail,
}: Readonly<Props>) {
  const router = useRouter();
  useFeatureToggle(featureToggles);

  return (
    <GuestDetailsPagePi
      router={router}
      queryClient={useQueryClient()}
      biQueryInput={biQueryInput}
      pcksQueryInput={pcksQueryInput}
      hiQueryInput={hiQueryInput}
      cachedGuestDetailsFormData={cachedGuestDetailsFormData}
      userEmail={userEmail}
    />
  );
}

GuestDetailsPage.getLayout = function getLayout(page: ReactElement) {
  return (
    <GuestDetailsLayout>
      <ErrorBoundary>{page}</ErrorBoundary>
    </GuestDetailsLayout>
  );
};

export async function getServerSideProps({ locale = 'gb', ...props }: GetServerSidePropsContext) {
  const { language, country } = getServerSideCustomLocale(locale);
  const { query } = props;

  let featureToggles: DynamicObject = await getUnleashToggles(
    props,
    PAGE.GUEST_DETAILS.featureToogles.appPage,
    PAGE.GUEST_DETAILS.featureToogles.flagsWithFallback
  );

  const queryClient = createServerQueryClient({
    page: PAGE.GUEST_DETAILS.featureToogles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });
  try {
    const basketReference = query.reservationId ? String(query.reservationId) : '';
    let basicBasketDetails: string | null = null;
    try {
      const redisKey = RedisKeyPrefix.HRS + '::' + basketReference;
      const redisStorage = RedisStorageServer.getInstance();
      basicBasketDetails = await redisStorage.getItem(redisKey);
    } catch (error) {
      logger.error({ error }, 'PI_GUEST_DETAILS_PAGE_REDIS_ERROR');
    }
    const hasCachedBasketDetails = !!(basicBasketDetails && isJsonValid(basicBasketDetails));
    const basketDetails: HIBasicBasketDetails = hasCachedBasketDetails
      ? JSON.parse(basicBasketDetails || '{}')
      : null;
    featureToggles = await getUnleashToggles(
      props,
      PAGE.GUEST_DETAILS.featureToogles.appPage,
      PAGE.GUEST_DETAILS.featureToogles.flagsWithFallback,
      {
        DigitalRegistrationCardContext: basketDetails?.hotelId ?? '',
        country: country || GLOBALS.locale.GB,
        channel: UnleashChannel.PI,
        pageName: PageName.GUEST_DETAILS.toUpperCase(),
      }
    );

    const labels = await getI18nLabels({
      language,
      queryClient,
      fetchPreCheckInLabel: true,
      forceRefetch: true,
    });

    const isAuth0Enabled = featureToggles[FT_PI_AUTH0_LOGIN] ?? false;
    let authToken: string | undefined;
    let userEmail: string | undefined;

    if (isAuth0Enabled) {
      const { accessToken, email } = await getAuth0TokenAndEmail(props.req);
      authToken = accessToken ?? undefined;
      userEmail = email ?? undefined;
    }

    const loadedData = await createGuestDetailsPiDataLoaderFn({
      queryClient,
      language,
      country,
      featureToggles,
      passedBasketDetails: basketDetails,
      authToken,
      userEmail,
      hasCachedBasketDetails,
      ...props,
    });

    let cachedGuestDetailsFormData: Record<string, unknown> | undefined;
    const isRemovePIIDataFromLocalStorageEnabled =
      featureToggles[FT_PI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE] ?? false;

    if (isRemovePIIDataFromLocalStorageEnabled) {
      try {
        const guestDetailsRedisKey = `${RedisKeyPrefix.GUEST_DETAILS_FORM_DATA_PI}::${basketReference}`;
        const redisStorage = RedisStorageServer.getInstance();
        const encodedCachedFormData = await redisStorage.getItem(guestDetailsRedisKey);

        const decodedCachedFormData = decodeFromBase64(encodedCachedFormData);
        if (decodedCachedFormData && isJsonValid(decodedCachedFormData)) {
          cachedGuestDetailsFormData = JSON.parse(decodedCachedFormData);
          logger.info({ basketReference }, 'PI_GUEST_DETAILS_CACHED_FORM_DATA_FOUND');
        }
      } catch (error) {
        logger.error({ error }, 'PI_GUEST_DETAILS_CACHED_FORM_DATA_ERROR');
      }
    }

    return {
      props: {
        ...loadedData,
        ...labels,
        featureToggles,
        userEmail: userEmail ?? null,
        ...(cachedGuestDetailsFormData ? { cachedGuestDetailsFormData } : {}),
      },
    };
  } finally {
    clearServerQueryClient(queryClient);
  }
}
