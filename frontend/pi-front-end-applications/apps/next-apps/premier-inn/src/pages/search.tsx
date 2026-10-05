import { useQueryClient } from '@tanstack/react-query';
import {
  type PromotionBanner,
  Channel,
  PageName,
  UnleashChannel,
  FT_PI_REDIS_RQ_CACHE,
} from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import { getPromotionsInformation } from '@whitbread-eos/molecules';
import {
  getI18nLabels,
  getServerSideCustomLocale,
  getUnleashToggles,
  useFeatureToggle,
  GLOBALS,
  extractMultiHotelAvailabilities,
  shouldDisplayPromoBanner,
  type PromotionsInformation,
  getHotelBrandFromSearchResults,
  getGQLClient,
  WB_SESSION_ID,
} from '@whitbread-eos/utils';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';
import dynamic from 'next/dynamic';
import { useRouter } from 'next/router';
import { ReactElement } from 'react';

import { DefaultLayout } from '~components';
import {
  useWebPushNotification,
  PUSH_REQUEST_CLOSED_COUNT,
  PUSH_REQUEST_CLOSED_TIMESTAMP,
} from '~hooks/use-web-push-notification';
import { createSearchResultsPiDataLoader, SearchPagePI } from '~page-helper/search/index';
import { PAGE } from '~utils/pi-all-pages-constants';
import { clearServerQueryClient, createServerQueryClient } from '~utils/serverQueryClient';

interface Props {
  featureToggles: { [key: string]: boolean };
  pushRequestClosedCountCookie: number;
  pushRequestClosedTimestampCookie: string | null;
  fallbackSearchPlace?: { PLACEID?: string; 'searchModel.searchTerm'?: string };
  promotionBannerData?: PromotionsInformation;
}

const ConsentNotificationModal = dynamic(
  async () => {
    const { ConsentNotificationModal } = await import('@whitbread-eos/molecules');
    return { default: ConsentNotificationModal };
  },
  {
    ssr: false,
  }
);

export default function SearchPage(props: Readonly<Props>) {
  const {
    featureToggles,
    pushRequestClosedCountCookie,
    pushRequestClosedTimestampCookie,
    fallbackSearchPlace,
    promotionBannerData,
  } = props;
  useFeatureToggle(featureToggles);

  const { shouldShowNotificationModal, handleNotificationPermission } = useWebPushNotification({
    featureToggles,
    pushRequestClosedCountCookie,
    pushRequestClosedTimestampCookie,
  });

  return (
    <>
      <SearchPagePI
        router={useRouter()}
        queryClient={useQueryClient()}
        fallbackSearchPlace={fallbackSearchPlace}
        promotionBannerData={promotionBannerData}
      />
      {shouldShowNotificationModal && (
        <ConsentNotificationModal handleNotificationPermission={handleNotificationPermission} />
      )}
    </>
  );
}

SearchPage.getLayout = function getLayout(page: ReactElement) {
  const router = page?.props?.router;
  const promotionBanner: PromotionBanner = page.props.dehydratedState?.queries.find(
    (query: any) => query.queryKey?.[0] === 'GetStaticContent'
  ).state?.data?.headerInformation?.config?.promotionBanner;

  const containerStyles = {
    mx: 0,
    px: 0,
    maxWidth: '100% !important',
    height: '100%',
  };

  const mainStyles = {
    mb: '-0.5rem !important',
  };

  const promotionBannerData = page?.props?.promotionBannerData ?? {};

  return (
    <DefaultLayout
      showPromoCode={shouldDisplayPromoBanner(
        'breakfastPromo',
        promotionBannerData,
        promotionBanner,
        router
      )}
      showFooter={false}
      containerStyles={containerStyles}
      mainStyles={mainStyles}
      showPromotionsNotification={false}
      showSummerPromo={shouldDisplayPromoBanner(
        'summerPromo',
        promotionBannerData,
        promotionBanner,
        router
      )}
      promotionBanner={promotionBanner || {}}
      showNotification={true}
    >
      <ErrorBoundary>{page}</ErrorBoundary>
    </DefaultLayout>
  );
};

export async function getServerSideProps({ locale = 'gb', ...props }: GetServerSidePropsContext) {
  const cookies = new Cookies(props.req, props.res);
  const { language, country } = getServerSideCustomLocale(locale);

  const featureToggles: { [key: string]: boolean } = await getUnleashToggles(
    props,
    PAGE.SRP.featureToggles.appPage,
    PAGE.SRP.featureToggles.flagsWithFallback,
    { country: country || GLOBALS.locale.GB, channel: UnleashChannel.PI, pageName: PageName.SRP }
  );

  const queryClient = createServerQueryClient({
    page: PAGE.SRP.featureToggles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });
  try {
    const labels = await getI18nLabels({
      language,
      queryClient,
    });

    const pushRequestClosedCountCookie = Number(cookies.get(PUSH_REQUEST_CLOSED_COUNT) ?? 0);
    const pushRequestClosedTimestampCookie = cookies.get(PUSH_REQUEST_CLOSED_TIMESTAMP) ?? null;

    const loadedData = await createSearchResultsPiDataLoader({
      queryClient,
      language,
      country,
      featureToggles,
      ...props,
    });

    const {
      arrival = '',
      departure = '',
      channel = '',
      promotionCode = '',
    } = loadedData?.promoInformationQuery ?? {};

    const hotelsList = extractMultiHotelAvailabilities(loadedData);
    const hotelBrand = getHotelBrandFromSearchResults(hotelsList);

    const isPromoEnabled =
      Boolean(hotelBrand) && (loadedData?.promoInformationQuery?.isPromoEnabled ?? false);

    const sessionId = cookies.get(WB_SESSION_ID);
    const client = getGQLClient(sessionId);

    const promotionBannerData = await getPromotionsInformation(
      arrival as string,
      departure as string,
      country,
      language,
      hotelBrand as string,
      channel as Channel,
      '',
      queryClient,
      client,
      isPromoEnabled,
      promotionCode
    );

    return {
      props: {
        pushRequestClosedCountCookie,
        pushRequestClosedTimestampCookie,
        featureToggles,
        ...loadedData,
        ...labels,
        promotionBannerData,
      },
    };
  } finally {
    clearServerQueryClient(queryClient);
  }
}
