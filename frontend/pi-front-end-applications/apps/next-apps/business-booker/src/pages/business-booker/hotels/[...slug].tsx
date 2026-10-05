import { useQueryClient } from '@tanstack/react-query';
import {
  BUSINESS_BOOKER_USER_ROLES,
  FT_PI_REDIS_RQ_CACHE,
  InnBusinessServerSideProps,
  Language,
  PageName,
  UnleashChannel,
} from '@whitbread-eos/api';
import { Container, ErrorBoundary } from '@whitbread-eos/atoms';
import {
  ID_TOKEN_COOKIE,
  getLoggedInUserInfo,
  getServerSideCustomLocale,
  getI18nLabels,
  getUnleashToggles,
  useFeatureToggle,
  isInnBusinessApp,
  readPromotionsInformation,
  type PromotionsInformation,
  GLOBALS,
  getBrandForUnleashContext,
} from '@whitbread-eos/utils';
import { getPersistentQueryClient } from '@whitbread-eos/utils/server';
import Cookies from 'cookies';
import type { GetServerSidePropsContext } from 'next';
import { useRouter } from 'next/router';
import { ReactElement } from 'react';

import { DefaultLayout } from '~components';
import InnBusinessLayout from '~components/innBusiness/InnBusinessLayout';
import { useScreenSize } from '~hooks/use-screensize';
import { HotelDetailsPageBB, createHDPBbDataLoaderFn } from '~page-helper/hotel-details/hdp/index';
import { PAGE } from '~utils/bb-all-pages-constants';
import withPiTokenRedirect from '~utils/withPiTokenRedirect';

import { innBusinessLoginRedirect } from '../search';

interface Props {
  isGuestUser: boolean;
  featureToggles: { [key: string]: boolean };
  innBusiness?: InnBusinessServerSideProps;
  isInnBusinessAppPage?: boolean;
  promotionBannerData?: PromotionsInformation;
}

export default function HotelDetailsPage({
  isGuestUser,
  featureToggles,
  innBusiness,
  promotionBannerData,
}: Readonly<Props>) {
  useFeatureToggle(featureToggles);
  const router = useRouter();
  const queryClient = useQueryClient();

  const { isLessThanXs, isLessThanSm, isLessThanMd, isLessThanLg } = useScreenSize();

  return (
    <>
      {!isGuestUser && (
        <HotelDetailsPageBB
          {...{
            showSearch: !innBusiness,
            queryClient,
            router,
            visualDisplayContext: {
              isLessThanXs,
              isLessThanSm,
              isLessThanMd,
              isLessThanLg,
            },
            promotionBannerData,
          }}
        />
      )}
    </>
  );
}

HotelDetailsPage.getLayout = function getLayout(page: ReactElement<any>) {
  const secureUrl = process?.env?.NEXT_PUBLIC_SECURE2_URL ?? '';
  if (page.props.isInnBusinessAppPage) {
    return (
      <InnBusinessLayout
        serverSideProps={page.props.innBusiness}
        isBusinessBookerPage
        businessBookerPageOptions={{ isHotelDetailsPage: true }}
        showEditSearch={true}
        showFooter={true}
        collapsedSidebar={true}
        featureToggle={page.props.featureToggles}
        secureUrl={secureUrl}
      >
        <Container>
          <ErrorBoundary>{page}</ErrorBoundary>
        </Container>
      </InnBusinessLayout>
    );
  }

  return (
    <DefaultLayout>
      <ErrorBoundary>{page}</ErrorBoundary>
    </DefaultLayout>
  );
};

export const getServerSideProps = withPiTokenRedirect(async function getServerSideProps({
  locale = 'gb',
  ...props
}: GetServerSidePropsContext) {
  const cookies = new Cookies(props.req, props.res);
  const { language, country } = getServerSideCustomLocale(locale);
  const idTokenCookie = cookies.get(ID_TOKEN_COOKIE);
  let isGuestUser = false;

  const hotelBrand = await getBrandForUnleashContext({
    language: language as Language,
    brand: props?.query?.BRAND?.toString(),
    slug: props?.query?.slug,
  });

  const featureToggles: { [key: string]: boolean } = await getUnleashToggles(
    props,
    PAGE.HDP.featureToggles.appPage,
    PAGE.HDP.featureToggles.flagsWithFallback,
    {
      country: country ?? GLOBALS.locale.GB,
      channel: UnleashChannel.PIB,
      pageName: PageName.HDP,
      ...(hotelBrand && { hotelBrand }),
    }
  );

  const queryClient = getPersistentQueryClient({
    page: PAGE.HDP.featureToggles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });

  const isInnBusinessAppPage = !!isInnBusinessApp(props?.req?.headers?.host ?? '');
  const currentPath = props?.resolvedUrl || '';
  const shouldRedirectToInnBusinessLogin = !idTokenCookie && isInnBusinessAppPage;

  if (shouldRedirectToInnBusinessLogin) {
    return innBusinessLoginRedirect(locale, featureToggles, currentPath);
  }

  if (idTokenCookie) {
    const { accessLevel } = getLoggedInUserInfo(idTokenCookie);
    isGuestUser = accessLevel === BUSINESS_BOOKER_USER_ROLES.STAYER;
  }

  if (isGuestUser) {
    return {
      redirect: {
        destination: `/${country}/${language}/business-booker`,
        permanent: false,
      },
    };
  }

  const labels = await getI18nLabels({
    language,
    queryClient,
  });

  const loadedData = await createHDPBbDataLoaderFn({
    queryClient,
    language,
    country,
    featureToggles,
    isInnBusinessAppPage,
    ...props,
  });
  const promotionBannerData = readPromotionsInformation(loadedData);

  return {
    props: {
      featureToggles,
      ...labels,
      ...loadedData,
      isGuestUser,
      isInnBusinessAppPage,
      promotionBannerData,
    },
  };
});
