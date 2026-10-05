import { useQueryClient } from '@tanstack/react-query';
import {
  Claims,
  FT_PI_REDIS_RQ_CACHE,
  Language,
  PageName,
  UnleashChannel,
} from '@whitbread-eos/api';
import {
  getServerSideCustomLocale,
  setAnalyticsUser,
  getI18nLabels,
  getUnleashToggles,
  useFeatureToggle,
  PromotionsInformation,
  readPromotionsInformation,
  GLOBALS,
  getBrandForUnleashContext,
} from '@whitbread-eos/utils';
import { getPersistentQueryClient } from '@whitbread-eos/utils/server';
import type { GetServerSidePropsContext } from 'next';
import { useRouter } from 'next/router';

import { useScreenSize } from '~hooks/use-screensize';
import { auth0 } from '~lib/auth0';
import {
  createHDPCcuiDataLoaderFn,
  HotelDetailsPageCCUI,
} from '~page-helper/hotel-details/hdp/index';
import { PAGE } from '~utils/ccui-all-pages-constants';
import { getProxyOptions, setProxyOptionsCookies } from '~utils/proxyOptions';

interface Props {
  user: Claims;
  featureToggles: DynamicObject;
  promotionBannerData?: PromotionsInformation;
}

interface DynamicObject {
  [key: string]: boolean;
}

export default function HotelDetailsPage({
  user,
  featureToggles,
  promotionBannerData,
}: Readonly<Props>) {
  useFeatureToggle(featureToggles);
  const router = useRouter();
  const queryClient = useQueryClient();
  const { isLessThanXs, isLessThanSm, isLessThanMd, isLessThanLg } = useScreenSize();

  return (
    <HotelDetailsPageCCUI
      {...{
        queryClient,
        router,
        user,
        setAnalyticsUser,
        visualDisplayContext: {
          isLessThanXs,
          isLessThanSm,
          isLessThanMd,
          isLessThanLg,
        },
        promotionBannerData,
      }}
    />
  );
}

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

  const hotelBrand = await getBrandForUnleashContext({
    language: language as Language,
    brand: props?.query?.BRAND?.toString(),
    slug: props?.query?.slug,
  });

  const featureToggles: DynamicObject = await getUnleashToggles(
    props,
    PAGE.HDP.featureToggles.appPage,
    PAGE.HDP.featureToggles.flagsWithFallback,
    {
      country: country ?? GLOBALS.locale.GB,
      channel: UnleashChannel.CCUI,
      pageName: PageName.HDP,
      ...(hotelBrand && { hotelBrand }),
    },
    session?.user.name
  );

  const queryClient = getPersistentQueryClient({
    page: PAGE.HDP.featureToggles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });

  const loadedData = await createHDPCcuiDataLoaderFn({
    queryClient,
    session,
    language,
    country,
    proxyOptions,
    featureToggles,
    ...props,
  });

  const labels = await getI18nLabels({
    language,
    queryClient,
  });

  const promotionBannerData = readPromotionsInformation(loadedData);

  return {
    props: {
      ...labels,
      ...loadedData,
      featureToggles,
      promotionBannerData,
    },
  };
}
