import { useQueryClient } from '@tanstack/react-query';
import { Channel, Claims, FT_PI_REDIS_RQ_CACHE } from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import {
  getServerSideCustomLocale,
  setAnalyticsUser,
  getI18nLabels,
  getUnleashToggles,
} from '@whitbread-eos/utils';
import { getPersistentQueryClient } from '@whitbread-eos/utils/server';
import { GetServerSidePropsContext } from 'next';
import { ReactElement } from 'react';

import { SecondaryHDPLayout } from '~components';
import { useScreenSize } from '~hooks/use-screensize';
import { auth0 } from '~lib/auth0';
import {
  ChooseTwinroomPageCCUI,
  createChooseTwinroomCcuiDataLoaderFn,
} from '~page-helper/hotel-details/choose-twinroom/index';
import { PAGE } from '~utils/ccui-all-pages-constants';
import { getProxyOptions, setProxyOptionsCookies } from '~utils/proxyOptions';

interface Props {
  user: Claims;
}

export default function ChooseTwinroomPage({ user }: Readonly<Props>) {
  const queryClient = useQueryClient();
  const { isLessThanXs, isLessThanSm, isLessThanMd, isLessThanLg } = useScreenSize();

  return (
    <ChooseTwinroomPageCCUI
      {...{
        queryClient,
        visualDisplayContext: {
          isLessThanXs,
          isLessThanSm,
          isLessThanMd,
          isLessThanLg,
        },
        user,
        setAnalyticsUser,
      }}
      channel={Channel.Ccui}
    />
  );
}

ChooseTwinroomPage.getLayout = function getLayout(page: ReactElement) {
  return (
    <SecondaryHDPLayout>
      <ErrorBoundary>{page}</ErrorBoundary>
    </SecondaryHDPLayout>
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

  const featureToggles = await getUnleashToggles(
    props,
    PAGE.HDP.featureToggles.appPage,
    PAGE.HDP.featureToggles.flagsWithFallback
  );

  const queryClient = getPersistentQueryClient({
    page: PAGE.HDP.featureToggles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });

  const labels = await getI18nLabels({
    language,
    queryClient,
  });

  const loadedData = await createChooseTwinroomCcuiDataLoaderFn({
    queryClient,
    session,
    language,
    country,
    proxyOptions,
    ...props,
  });

  return {
    props: {
      ...labels,
      ...loadedData,
    },
  };
}
