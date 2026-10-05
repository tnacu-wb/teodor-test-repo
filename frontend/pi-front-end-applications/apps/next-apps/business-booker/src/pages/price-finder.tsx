import { useQueryClient } from '@tanstack/react-query';
import { FT_PI_REDIS_RQ_CACHE, InnBusinessServerSideProps, PageName } from '@whitbread-eos/api';
import { Container, ErrorBoundary } from '@whitbread-eos/atoms';
import {
  getServerSideCustomLocale,
  getI18nLabels,
  getUnleashToggles,
  useFeatureToggle,
  ID_TOKEN_COOKIE,
  isInnBusinessApp,
} from '@whitbread-eos/utils';
import { getPersistentQueryClient } from '@whitbread-eos/utils/server';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';
import { useRouter } from 'next/router';
import { ReactElement } from 'react';

import { PriceFinderLayout } from '~components';
import InnBusinessLayout from '~components/innBusiness/InnBusinessLayout';
import { PriceFinderPageBB, createPriceFinderBBDataLoaderFn } from '~page-helper/price-finder';
import { PAGE } from '~utils/bb-all-pages-constants';
import withPiTokenRedirect from '~utils/withPiTokenRedirect';

import { innBusinessLoginRedirect } from '../pages/business-booker/search';

interface Props {
  featureToggles: { [key: string]: boolean };
  innBusiness?: InnBusinessServerSideProps;
  isInnBusinessAppPage?: boolean;
}

export default function PriceFinderPage(props: Readonly<Props>) {
  const router = useRouter();
  const queryClient = useQueryClient();
  const { featureToggles } = props;
  useFeatureToggle(featureToggles);

  return <PriceFinderPageBB router={router} queryClient={queryClient} />;
}

PriceFinderPage.getLayout = function getLayout(page: ReactElement<any>) {
  const secureUrl = process?.env?.NEXT_PUBLIC_SECURE2_URL ?? '';
  if (page.props.isInnBusinessAppPage) {
    return (
      <InnBusinessLayout
        secureUrl={secureUrl}
        featureToggle={page.props.featureToggles}
        showEditSearch={true}
        showFooter={true}
        isBusinessBookerPage
        serverSideProps={page.props.innBusiness}
        pageName={PageName.PRICE_FINDER}
      >
        <Container pageName={PageName.PRICE_FINDER}>
          <ErrorBoundary>{page}</ErrorBoundary>
        </Container>
      </InnBusinessLayout>
    );
  }

  return (
    <PriceFinderLayout>
      <ErrorBoundary>{page}</ErrorBoundary>
    </PriceFinderLayout>
  );
};

export const getServerSideProps = withPiTokenRedirect(async function getServerSideProps({
  locale = 'gb',
  ...props
}: GetServerSidePropsContext) {
  const cookies = new Cookies(props.req, props.res);
  const idTokenCookie = cookies.get(ID_TOKEN_COOKIE);
  const { language, country } = getServerSideCustomLocale(locale);

  const featureToggles: { [key: string]: boolean } = await getUnleashToggles(
    props,
    PAGE.PRICE_FINDER.featureToggles.appPage,
    PAGE.PRICE_FINDER.featureToggles.flagsWithFallback
  );

  const queryClient = getPersistentQueryClient({
    page: PAGE.PRICE_FINDER.featureToggles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });

  const isInnBusinessAppPage = !!isInnBusinessApp(props?.req?.headers?.host ?? '');
  const currentPath = props?.resolvedUrl || '';
  const shouldRedirectToInnBusinessLogin = !idTokenCookie && isInnBusinessAppPage;

  if (shouldRedirectToInnBusinessLogin) {
    return innBusinessLoginRedirect(locale, featureToggles, currentPath);
  }

  const loadedData = await createPriceFinderBBDataLoaderFn({
    queryClient,
    language,
    country,
    featureToggles,
    isInnBusinessAppPage,
    ...props,
  });

  const labels = await getI18nLabels({
    language,
    queryClient,
  });

  return {
    props: {
      featureToggles,
      isInnBusinessAppPage,
      ...loadedData,
      ...labels,
    },
  };
});
