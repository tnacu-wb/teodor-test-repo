import { Query, useQueryClient } from '@tanstack/react-query';
import {
  HIRoomRate,
  DLP_AKAMAI_HEADER_NAME,
  type PromotionBanner,
  HIAvailabilityRates,
  HIAEMroomTypesInfo,
  Language,
  FT_PI_REDIS_RQ_CACHE,
  UnleashChannel,
  PageName,
} from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import {
  getI18nLabels,
  getServerSideCustomLocale,
  getUnleashToggles,
  useFeatureToggle,
  GLOBALS,
  readPromotionsInformation,
  shouldDisplayPromoBanner,
  type PromotionsInformation,
  getBrandForUnleashContext,
} from '@whitbread-eos/utils';
import Cookies from 'cookies';
import type { GetServerSidePropsContext } from 'next';
import dynamic from 'next/dynamic';
import { useRouter } from 'next/router';
import { ReactElement } from 'react';

import { DefaultLayout } from '~components';
import { useScreenSize } from '~hooks/use-screensize';
import {
  useWebPushNotification,
  PUSH_REQUEST_CLOSED_COUNT,
  PUSH_REQUEST_CLOSED_TIMESTAMP,
} from '~hooks/use-web-push-notification';
import {
  DestinationLandingPagePI,
  createDLPPiDataLoaderFn,
} from '~page-helper/hotel-details/dlp/index';
import { createHDPPiDataLoaderFn, HotelDetailsPagePI } from '~page-helper/hotel-details/hdp/index';
import { PAGE } from '~utils/pi-all-pages-constants';
import { clearServerQueryClient, createServerQueryClient } from '~utils/serverQueryClient';

interface Props {
  featureToggles: { [key: string]: boolean };
  pushRequestClosedCountCookie: number;
  pushRequestClosedTimestampCookie: string | null;
  dehydratedState: {
    queries: Query[];
  };
  dlpQueryKey: unknown[];
  hotelsInformationQueryKey: unknown[];
  searchInformationQueryKey: unknown[];
  error: string;
  promotionBannerData?: PromotionsInformation;
  hotelAvailability?: HIAvailabilityRates;
  roomTypeInformation?: HIAEMroomTypesInfo;
}

export const HDP_URL_SEGMENTS = 4;

const ConsentNotificationModal = dynamic(
  async () => {
    const { ConsentNotificationModal } = await import('@whitbread-eos/molecules');
    return { default: ConsentNotificationModal };
  },
  {
    ssr: false,
  }
);

export default function HotelDetailsPage(props: Readonly<Props>) {
  const {
    featureToggles,
    pushRequestClosedCountCookie,
    pushRequestClosedTimestampCookie,
    dlpQueryKey,
    hotelsInformationQueryKey,
    searchInformationQueryKey,
    error,
    promotionBannerData,
  } = props;
  useFeatureToggle(featureToggles);
  const router = useRouter();
  const queryClient = useQueryClient();
  const slug = router?.query?.slug;
  const isDestinationPage = slug && slug?.length < HDP_URL_SEGMENTS;

  const err = JSON.parse(error);

  const { shouldShowNotificationModal, handleNotificationPermission } = useWebPushNotification({
    featureToggles,
    pushRequestClosedCountCookie,
    pushRequestClosedTimestampCookie,
  });

  const { isLessThanXs, isLessThanSm, isLessThanMd, isLessThanLg } = useScreenSize();

  if (typeof window == 'undefined' && err !== null) {
    return <></>;
  }
  if (isDestinationPage && err !== null && typeof window !== 'undefined') {
    console.error('DLP error: ', err);
    throw err;
  }

  const hotelAvailability = props.dehydratedState?.queries.find(
    (query: any) => query.queryKey?.[0] === 'hotelAvailability'
  )?.state?.data as HIAvailabilityRates | undefined;

  const roomTypeInformation = props.dehydratedState?.queries.find(
    (query: any) => query.queryKey?.[0] === 'getRoomTypeInformation'
  )?.state?.data as HIAEMroomTypesInfo | undefined;

  const queries = props.dehydratedState.queries;
  const pageProps = {
    queryClient,
    router,
    visualDisplayContext: {
      isLessThanXs,
      isLessThanSm,
      isLessThanMd,
      isLessThanLg,
    },
    queries,
    dlpQueryKey,
    hotelsInformationQueryKey,
    searchInformationQueryKey,
    promotionBannerData,
    hotelAvailability,
    roomTypeInformation,
  };

  return (
    <>
      {isDestinationPage ? (
        <DestinationLandingPagePI {...pageProps} />
      ) : (
        <HotelDetailsPagePI {...pageProps} />
      )}
      {shouldShowNotificationModal && (
        <ConsentNotificationModal handleNotificationPermission={handleNotificationPermission} />
      )}
    </>
  );
}

HotelDetailsPage.getLayout = function getLayout(page: ReactElement) {
  const router = page?.props?.router;
  const roomRates: HIRoomRate[] = page.props.dehydratedState?.queries.find(
    (query: any) => query.queryKey?.[0] === 'hotelAvailability'
  )?.state?.data?.hotelAvailability?.roomRates;

  const promotionBanner: PromotionBanner = page.props.dehydratedState?.queries.find(
    (query: any) => query.queryKey?.[0] === 'GetStaticContent'
  ).state?.data?.headerInformation?.config?.promotionBanner;

  const promotionBannerData = page?.props?.promotionBannerData ?? {};

  return (
    <>
      <DefaultLayout
        showPromoCode={shouldDisplayPromoBanner(
          'breakfastPromo',
          promotionBannerData,
          promotionBanner,
          router
        )}
        roomRates={roomRates || []}
        containerStyles={
          page.props.isDestinationPage ? { px: { base: '1rem', xl: '4.125rem' } } : {}
        }
        showSummerPromo={shouldDisplayPromoBanner(
          'summerPromo',
          promotionBannerData,
          promotionBanner,
          router
        )}
        promotionBanner={promotionBanner || {}}
        showNotification={false}
      >
        <ErrorBoundary>{page}</ErrorBoundary>
      </DefaultLayout>
    </>
  );
};

export async function getServerSideProps({ locale = 'gb', ...props }: GetServerSidePropsContext) {
  const cookies = new Cookies(props.req, props.res);
  const { language, country } = getServerSideCustomLocale(locale);
  const slug = props?.query?.slug;

  const hotelBrand = await getBrandForUnleashContext({
    language: language as Language,
    brand: props?.query?.BRAND?.toString(),
    slug: props?.query?.slug,
  });

  const featureToggles = await getUnleashToggles(
    props,
    PAGE.HDP.featureToggles.appPage,
    PAGE.HDP.featureToggles.flagsWithFallback,
    {
      country: country ?? GLOBALS.locale.GB,
      channel: UnleashChannel.PI,
      pageName: PageName.HDP,
      ...(hotelBrand && { hotelBrand }),
    }
  );

  const queryClient = createServerQueryClient({
    page: PAGE.HDP.featureToggles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });
  try {
    const isDestinationPage = slug && slug?.length < HDP_URL_SEGMENTS;

    const labels = await getI18nLabels({
      language,
      queryClient,
    });

    const dataLoaderParams = {
      queryClient,
      language,
      country,
      featureToggles,
      ...props,
    };

    let loadedData;
    let error = null;
    if (isDestinationPage) {
      try {
        loadedData = await createDLPPiDataLoaderFn({ ...dataLoaderParams });
      } catch (err) {
        error = err;
      }
    } else {
      loadedData = await createHDPPiDataLoaderFn({ ...dataLoaderParams });
    }

    const pushRequestClosedCountCookie = Number(cookies.get(PUSH_REQUEST_CLOSED_COUNT) ?? 0);
    const pushRequestClosedTimestampCookie = cookies.get(PUSH_REQUEST_CLOSED_TIMESTAMP) ?? null;

    if (isDestinationPage) {
      const akamaiHeaderValue = `ttl=${
        process.env.NEXT_PUBLIC_DLP_AKAMAI_CACHE_TTL
      },tag=[dlp-${locale}, ${Array.isArray(slug) ? slug.join('/') : slug}]`;
      props.res.setHeader(DLP_AKAMAI_HEADER_NAME, akamaiHeaderValue);
    }

    const promotionBannerData = readPromotionsInformation(loadedData);

    return {
      props: {
        pushRequestClosedCountCookie,
        pushRequestClosedTimestampCookie,
        featureToggles, // keep featureToggles as first prop or will get overwritten
        ...loadedData,
        ...labels,
        isDestinationPage,
        error: JSON.stringify(error),
        promotionBannerData,
      },
    };
  } finally {
    clearServerQueryClient(queryClient);
  }
}
