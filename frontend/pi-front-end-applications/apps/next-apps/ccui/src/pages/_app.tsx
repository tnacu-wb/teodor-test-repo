import { ChakraProvider, extendTheme } from '@chakra-ui/react';
import { HydrationBoundary, QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import { ErrorBoundary, Fonts, ScriptsEmbed } from '@whitbread-eos/atoms';
import {
  AgentMemoProvider,
  analytics,
  AnalyticsProviderCCUI,
  AppDataProvider,
  setPageAnalytics,
  handleManageBookingParamsCCUI,
  setAnalyticsUser,
  useCustomLocale,
  FeatureToggleContextProvider,
} from '@whitbread-eos/utils';
import { format } from 'date-fns';
import { GetServerSidePropsContext, NextPage } from 'next';
import { appWithTranslation } from 'next-i18next';
import type { AppProps } from 'next/app';
import { useRouter } from 'next/router';
import { ReactElement, ReactNode, useEffect, useState } from 'react';
import smoothscroll from 'smoothscroll-polyfill';

import { DefaultLayout } from '~components';
import I18NLabels from '~components/common/I18NLabels';
import { auth0 } from '~lib/auth0';
import { CCUI_ROLES, Claims } from '~types/general';
import { getProxyOptions, setProxyOptionsCookies } from '~utils/proxyOptions';

import nextI18NextConfig from '../../next-i18next.config';
import { theme } from '../../theme';

type NextPageWithLayout = NextPage & {
  getLayout?: (page: ReactElement) => ReactNode;
};

type Props = AppProps & {
  Component: NextPageWithLayout;
  user: Claims;
};

const extendedTheme = extendTheme(theme);
global.WB = global.WB ?? { cache: {} };

function App({ Component, pageProps }: Props) {
  const isWindowDefined = typeof window !== 'undefined';

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [queryClient, _] = useState(
    () =>
      new QueryClient({
        defaultOptions: {
          queries: {
            refetchOnWindowFocus: false, // don't automatically refetch data on window (re)-focus
            refetchInterval: false, // don't automatically refetch data after a duration
            staleTime: 1000 * 60 * 5, // after what time is data (including pre-fetched) considered stale
          },
        },
      })
  );
  const { pathname, query } = useRouter();
  const { language } = useCustomLocale();
  const { user } = pageProps;

  const getLayout =
    Component.getLayout ||
    ((page: any) => (
      <DefaultLayout>
        <ErrorBoundary>{page}</ErrorBoundary>
      </DefaultLayout>
    ));
  const currentTime = format(new Date(), 'HH:mm');
  const currencyCode = language === 'en' ? 'gbp' : 'eur';

  useEffect(() => {
    setAnalyticsUser(user, language);
    analytics.update({
      language,
    });
  }, [user, language]);

  useEffect(() => {
    setPageAnalytics(pathname, 'CCUI', query, language);
  }, [pathname, query, language]);

  useEffect(() => {
    analytics.update({
      currentTime: currentTime,
      currencyCode: currencyCode,
    });
  }, [currentTime, currencyCode]);

  useEffect(() => {
    analytics.update({
      pageURL: window.location.href,
    });
  }, [isWindowDefined && window?.location?.href]);

  useEffect(() => {
    smoothscroll.polyfill();
  }, []);

  useEffect(() => {
    handleManageBookingParamsCCUI();
  }, []);

  return (
    <FeatureToggleContextProvider defaultFeatureToggles={pageProps.featureToggles}>
      <AppDataProvider>
        <QueryClientProvider client={queryClient}>
          <AgentMemoProvider user={user}>
            <HydrationBoundary state={pageProps.dehydratedState}>
              <AnalyticsProviderCCUI queryClient={queryClient}>
                <ChakraProvider theme={extendedTheme}>
                  <Fonts />
                  {getLayout(<Component {...pageProps} />)}
                </ChakraProvider>
              </AnalyticsProviderCCUI>
            </HydrationBoundary>

            {process.env.NODE_ENV === 'development' && pathname !== '/graphql' && (
              <>
                <ReactQueryDevtools />
                <I18NLabels />
              </>
            )}
          </AgentMemoProvider>
        </QueryClientProvider>
        <ScriptsEmbed language={language} />
      </AppDataProvider>
    </FeatureToggleContextProvider>
  );
}

export async function getServerSideProps(context: GetServerSidePropsContext) {
  const { req, res } = context;
  const session = await auth0.getSession(req);

  if (!session) {
    return {
      redirect: {
        destination: '/auth/login?returnTo=/',
        permanent: false,
      },
    };
  }

  const proxyOptions = getProxyOptions({ req, res }, session);
  setProxyOptionsCookies(proxyOptions, { req, res });
  const user = session.user;
  // getting roles from session
  const roles = session.user[CCUI_ROLES];

  return {
    props: {
      roles,
      user,
    },
  };
}

export default appWithTranslation(App, nextI18NextConfig);
