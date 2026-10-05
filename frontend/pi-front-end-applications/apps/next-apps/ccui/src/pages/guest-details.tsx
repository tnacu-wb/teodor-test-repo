//<editor-fold desc="Imports" defaultstate="collapsed">
import { useQueryClient } from '@tanstack/react-query';
import {
  type QueryBookingInformationArgs,
  type QueryHotelInformationArgs,
  type PackagesCriteria,
  FT_CCUI_PRE_CHECK_IN_LABEL,
  FT_CCUI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE,
  FT_PI_REDIS_RQ_CACHE,
  UnleashChannel,
  PageName,
} from '@whitbread-eos/api';
import { ErrorBoundary, FormProps } from '@whitbread-eos/atoms';
import {
  getServerSideCustomLocale,
  getI18nLabels,
  getUnleashToggles,
  useFeatureToggle,
  decodeFromBase64,
  isJsonValid,
  logger,
} from '@whitbread-eos/utils';
import {
  RedisKeyPrefix,
  RedisStorageServer,
  getPersistentQueryClient,
} from '@whitbread-eos/utils/server';
import type { GetServerSidePropsContext } from 'next';
import { useRouter } from 'next/router';
import type { ReactElement } from 'react';

import { GuestDetailsLayout } from '~components';
import { auth0 } from '~lib/auth0';
import {
  createGuestDetailsCcuiDataLoaderFn,
  Page as GuestDetailsPageCcui,
} from '~page-helper/guest-details';
import { PAGE } from '~utils/ccui-all-pages-constants';
import { getProxyOptions, setProxyOptionsCookies } from '~utils/proxyOptions';

interface DynamicObject {
  [key: string]: boolean;
}
interface Props {
  hiQueryInput: QueryHotelInformationArgs;
  pcksQueryInput: PackagesCriteria;
  biQueryInput: QueryBookingInformationArgs;
  featureToggles: DynamicObject;
  cachedGuestDetailsFormData?: FormProps['defaultValues'];
}

export default function GuestDetailsPage({
  pcksQueryInput,
  hiQueryInput,
  biQueryInput,
  featureToggles,
  cachedGuestDetailsFormData,
}: Readonly<Props>) {
  const router = useRouter();
  const queryClient = useQueryClient();

  useFeatureToggle(featureToggles);

  return (
    <GuestDetailsPageCcui
      router={router}
      queryClient={queryClient}
      pcksQueryInput={pcksQueryInput}
      hiQueryInput={hiQueryInput}
      biQueryInput={biQueryInput}
      cachedGuestDetailsFormData={cachedGuestDetailsFormData}
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

export async function getServerSideProps(context: GetServerSidePropsContext) {
  const { locale = 'gb', ...props } = context;
  const session = await auth0.getSession(props.req);

  if (!session) {
    return {
      redirect: {
        destination: `/auth/login?returnTo=${encodeURIComponent(context.resolvedUrl)}`,
        permanent: false,
      },
    };
  }

  const proxyOptions = getProxyOptions(props, session);
  setProxyOptionsCookies(proxyOptions, props);
  const { language, country } = getServerSideCustomLocale(locale);

  const featureToggles: DynamicObject = await getUnleashToggles(
    props,
    PAGE.GUEST_DETAILS.featureToggles.appPage,
    PAGE.GUEST_DETAILS.featureToggles.flagsWithFallback,
    {
      channel: UnleashChannel.CCUI,
      pageName: PageName.GUEST_DETAILS.toUpperCase(),
    },
    session?.user.name
  );

  const queryClient = getPersistentQueryClient({
    page: PAGE.GUEST_DETAILS.featureToggles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });

  const loadedData = await createGuestDetailsCcuiDataLoaderFn({
    queryClient,
    language,
    country,
    proxyOptions,
    session,
    ...props,
  });

  const { [FT_CCUI_PRE_CHECK_IN_LABEL]: fetchPreCheckInLabel } = featureToggles;

  const labels = await getI18nLabels({
    language,
    queryClient,
    fetchPreCheckInLabel,
    forceRefetch: fetchPreCheckInLabel,
  });

  let cachedGuestDetailsFormData: FormProps['defaultValues'] | undefined;
  const isRemovePIIDataFromLocalStorageEnabled =
    featureToggles[FT_CCUI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE] ?? false;
  const basketReference = props.query.reservationId ? String(props.query.reservationId) : '';

  if (isRemovePIIDataFromLocalStorageEnabled && basketReference) {
    try {
      const guestDetailsRedisKey = `${RedisKeyPrefix.GUEST_DETAILS_FORM_DATA_CCUI}::${basketReference}`;
      const redisStorage = RedisStorageServer.getInstance();
      const encodedCachedFormData = await redisStorage.getItem(guestDetailsRedisKey);
      const decodedCachedFormData = decodeFromBase64(encodedCachedFormData);

      if (decodedCachedFormData && isJsonValid(decodedCachedFormData)) {
        cachedGuestDetailsFormData = JSON.parse(decodedCachedFormData);
        logger.info({ basketReference }, 'CCUI_GUEST_DETAILS_CACHED_FORM_DATA_FOUND');
      }
    } catch (error) {
      logger.error({ error }, 'CCUI_GUEST_DETAILS_CACHED_FORM_DATA_ERROR');
    }
  }

  return {
    props: {
      ...loadedData,
      ...labels,
      featureToggles,
      ...(cachedGuestDetailsFormData ? { cachedGuestDetailsFormData } : {}),
    },
  };
}
