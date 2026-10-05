import { FT_PI_REDIS_RQ_CACHE } from '@whitbread-eos/api';
import type { InnBusinessServerSideProps, ScreenSizeValues } from '@whitbread-eos/api';
import { Container, ErrorBoundary } from '@whitbread-eos/atoms';
import {
  getServerSideCustomLocale,
  getI18nLabels,
  useFeatureToggle,
  getUnleashToggles,
  ID_TOKEN_COOKIE,
  WB_SESSION_ID,
  GLOBALS,
} from '@whitbread-eos/utils';
import {
  isInnBusinessApp,
  getInnBusinessServerSideProps,
  getPersistentQueryClient,
} from '@whitbread-eos/utils/server';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';
import { ReactElement } from 'react';

import { DefaultLayout } from '~components';
import InnBusinessLayout from '~components/innBusiness/InnBusinessLayout';
import { useScreenSize } from '~hooks/use-screensize';
import MyDashboardPageBb from '~page-helper/dashboard/page.bb';
import { PAGE } from '~utils/bb-all-pages-constants';
import withPiTokenRedirect from '~utils/withPiTokenRedirect';

import { innBusinessLoginRedirect } from '../search';

interface DynamicObject {
  [key: string]: boolean;
}
interface Props {
  featureToggles: DynamicObject;
  innBusiness?: InnBusinessServerSideProps;
  isInnBusinessAppPage?: boolean;
}

export default function Dashboard({ featureToggles, innBusiness }: Readonly<Props>) {
  const screenSize: ScreenSizeValues = useScreenSize();
  useFeatureToggle(featureToggles);
  return <MyDashboardPageBb screenSize={screenSize} showSearch={!innBusiness} />;
}
Dashboard.getLayout = function getLayout(page: ReactElement<any>) {
  const secureUrl = process?.env?.NEXT_PUBLIC_SECURE2_URL ?? '';
  if (page.props.isInnBusinessAppPage) {
    return (
      <InnBusinessLayout
        serverSideProps={page.props.innBusiness}
        isBusinessBookerPage
        showFooter={true}
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
    <DefaultLayout showFooter={false}>
      <ErrorBoundary>{page}</ErrorBoundary>
    </DefaultLayout>
  );
};

export const getServerSideProps = withPiTokenRedirect(async function getServerSideProps({
  locale = 'gb',
  ...props
}: GetServerSidePropsContext) {
  const cookies = new Cookies(props.req, props.res);
  const idTokenCookie = cookies.get(ID_TOKEN_COOKIE);
  const sessionId = cookies.get(WB_SESSION_ID);
  const { language, country } = getServerSideCustomLocale(locale);

  const featureToggles: DynamicObject = await getUnleashToggles(
    props,
    PAGE.DASHBOARD.featureToogles.appPage,
    PAGE.DASHBOARD.featureToogles.flagsWithFallback,
    { country: country ?? GLOBALS.locale.GB }
  );

  const queryClient = getPersistentQueryClient({
    page: PAGE.DASHBOARD.featureToogles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });

  const isInnBusinessAppPage = !!isInnBusinessApp(props?.req?.headers?.host ?? '');
  const shouldRedirectToInnBusinessLogin = !idTokenCookie && isInnBusinessAppPage;
  const currentPath = props?.resolvedUrl || '';

  if (shouldRedirectToInnBusinessLogin) {
    return innBusinessLoginRedirect(locale, featureToggles, currentPath);
  }

  const [labelsResult, innBusinessResult] = await Promise.allSettled([
    getI18nLabels({
      language,
      queryClient,
    }),
    isInnBusinessAppPage
      ? getInnBusinessServerSideProps(idTokenCookie, language, true, {
          ...props.req.headers,
          ...(sessionId && { [WB_SESSION_ID]: sessionId }),
        })
      : Promise.resolve(undefined),
  ]);

  const labels = labelsResult.status === 'fulfilled' ? labelsResult.value : {};
  const innBusiness =
    innBusinessResult.status === 'fulfilled' ? innBusinessResult.value : undefined;

  return {
    props: {
      ...labels,
      featureToggles,
      innBusiness,
      isInnBusinessAppPage,
    },
  };
});
