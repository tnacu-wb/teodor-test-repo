import { useQueryClient } from '@tanstack/react-query';
import { Channel, FT_PI_REDIS_RQ_CACHE, UnleashChannel } from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import { getPromotionsInformation } from '@whitbread-eos/molecules';
import {
  getServerSideCustomLocale,
  getI18nLabels,
  getUnleashToggles,
  useFeatureToggle,
  getHotelBrandFromSearchResults,
  extractMultiHotelAvailabilities,
  PromotionsInformation,
  getGQLClient,
  WB_SESSION_ID,
} from '@whitbread-eos/utils';
import { getPersistentQueryClient } from '@whitbread-eos/utils/server';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';
import { useRouter } from 'next/router';
import { ReactElement } from 'react';

import { DefaultLayout } from '~components';
import { auth0 } from '~lib/auth0';
import { PAGE } from '~utils/ccui-all-pages-constants';
import { getProxyOptions, setProxyOptionsCookies } from '~utils/proxyOptions';

import { createSearchResultsCCUIDataLoader, SearchPageCCUI } from '../page-helper/search';

interface Props {
  featureToggles: { [key: string]: boolean };
  promotionBannerData: PromotionsInformation;
}
export default function SearchPage(props: Readonly<Props>) {
  const router = useRouter();
  const queryClient = useQueryClient();
  const { featureToggles, promotionBannerData } = props;
  useFeatureToggle(featureToggles);

  return (
    <SearchPageCCUI
      queryClient={queryClient}
      router={router}
      promotionBannerData={promotionBannerData}
    />
  );
}

SearchPage.getLayout = function getLayout(page: ReactElement) {
  const containerStyles = {
    mx: 0,
    px: 0,
    maxWidth: '100% !important',
    height: '100%',
  };

  return (
    <DefaultLayout showFooter={false} containerStyles={containerStyles}>
      <ErrorBoundary>{page}</ErrorBoundary>
    </DefaultLayout>
  );
};

export async function getServerSideProps(context: GetServerSidePropsContext) {
  const { locale = 'gb', ...props } = context;
  const { req, res } = props;
  const session = await auth0.getSession(req);

  if (!session) {
    return {
      redirect: {
        destination: `/auth/login?returnTo=${encodeURIComponent(context.resolvedUrl)}`,
        permanent: false,
      },
    };
  }

  const proxyOptions = getProxyOptions({ req }, session);
  setProxyOptionsCookies(proxyOptions, { req, res });

  const { language, country } = getServerSideCustomLocale(locale);

  const featureToggles: { [key: string]: boolean } = await getUnleashToggles(
    props,
    PAGE.SEARCH.featureToggles.appPage,
    PAGE.SEARCH.featureToggles.flagsWithFallback,
    { channel: UnleashChannel.CCUI }
  );

  const queryClient = getPersistentQueryClient({
    page: PAGE.SEARCH.featureToggles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });

  const labels = await getI18nLabels({
    language,
    queryClient,
  });

  const loadedData = await createSearchResultsCCUIDataLoader({
    session,
    queryClient,
    language,
    country,
    proxyOptions,
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

  const cookies = new Cookies(req, res);
  const sessionId = cookies.get(WB_SESSION_ID);
  const client = getGQLClient(sessionId);

  const promotionBannerData = await getPromotionsInformation(
    arrival,
    departure,
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
      featureToggles,
      ...loadedData,
      ...labels,
      promotionBannerData,
    },
  };
}
