import { useQueryClient } from '@tanstack/react-query';
import { BUSINESS_BOOKER_USER_ROLES, Channel, FT_PI_REDIS_RQ_CACHE } from '@whitbread-eos/api';
import { Container, ErrorBoundary } from '@whitbread-eos/atoms';
import {
  ID_TOKEN_COOKIE,
  getLoggedInUserInfo,
  getServerSideCustomLocale,
  getI18nLabels,
  getUnleashToggles,
  useFeatureToggle,
  isInnBusinessApp,
} from '@whitbread-eos/utils';
import { getPersistentQueryClient } from '@whitbread-eos/utils/server';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';
import { ReactElement } from 'react';

import { SecondaryHDPLayout } from '~components';
import InnBusinessLayout from '~components/innBusiness/InnBusinessLayout';
import { useScreenSize } from '~hooks/use-screensize';
import {
  ChooseRoomTypePageBB,
  createChooseRoomTypeBbDataLoaderFn,
} from '~page-helper/hotel-details/choose-roomtype/index';
import { PAGE } from '~utils/bb-all-pages-constants';
import withPiTokenRedirect from '~utils/withPiTokenRedirect';

import { innBusinessLoginRedirect } from '../search';

interface Props {
  featureToggles: { [key: string]: boolean };
  isInnBusinessAppPage?: boolean;
}

export default function ChooseRoomTypePage({ featureToggles }: Props) {
  useFeatureToggle(featureToggles);
  const queryClient = useQueryClient();
  const { isLessThanXs, isLessThanSm, isLessThanMd, isLessThanLg } = useScreenSize();

  return (
    <ChooseRoomTypePageBB
      {...{
        queryClient,
        visualDisplayContext: {
          isLessThanXs,
          isLessThanSm,
          isLessThanMd,
          isLessThanLg,
        },
      }}
      channel={Channel.Bb}
    />
  );
}

ChooseRoomTypePage.getLayout = function getLayout(page: ReactElement<any>) {
  const secureUrl = process?.env?.NEXT_PUBLIC_SECURE2_URL ?? '';
  if (page.props.isInnBusinessAppPage) {
    return (
      <InnBusinessLayout
        serverSideProps={page.props.innBusiness}
        isBusinessBookerPage
        businessBookerPageOptions={{ isHotelDetailsPage: true }}
        headerLogoOnly
        showSidebar={false}
        secureUrl={secureUrl}
        featureToggle={page.props.featureToggles}
      >
        <Container>
          <ErrorBoundary>{page}</ErrorBoundary>
        </Container>
      </InnBusinessLayout>
    );
  }

  return (
    <SecondaryHDPLayout>
      <ErrorBoundary>{page}</ErrorBoundary>
    </SecondaryHDPLayout>
  );
};

export const getServerSideProps = withPiTokenRedirect(async function getServerSideProps({
  locale = 'gb',
  ...props
}: GetServerSidePropsContext) {
  let isGuestUser = false;
  const { language, country } = getServerSideCustomLocale(locale);
  const cookies = new Cookies(props.req, props.res);
  const idTokenCookie = cookies.get(ID_TOKEN_COOKIE);

  if (idTokenCookie) {
    const { accessLevel } = getLoggedInUserInfo(idTokenCookie);
    isGuestUser = accessLevel === BUSINESS_BOOKER_USER_ROLES.STAYER;
  }

  const featureToggles: { [key: string]: boolean } = await getUnleashToggles(
    props,
    PAGE.CHOOSE_ROOMTYPE.featureToggles.appPage,
    PAGE.CHOOSE_ROOMTYPE.featureToggles.flagsWithFallback
  );

  const queryClient = getPersistentQueryClient({
    page: PAGE.CHOOSE_ROOMTYPE.featureToggles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });

  const isInnBusinessAppPage = !!isInnBusinessApp(props?.req?.headers?.host ?? '');
  const shouldRedirectToInnBusinessLogin = !idTokenCookie && isInnBusinessAppPage;
  if (shouldRedirectToInnBusinessLogin) {
    return innBusinessLoginRedirect(locale);
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

  const loadedData = await createChooseRoomTypeBbDataLoaderFn({
    queryClient,
    language,
    country,
    featureToggles,
    isInnBusinessAppPage,
    ...props,
  });

  return {
    props: {
      ...labels,
      ...loadedData,
      isGuestUser,
      featureToggles,
      isInnBusinessAppPage,
    },
  };
});
