import { ChakraProvider, extendTheme } from '@chakra-ui/react';
import { HydrationBoundary, QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import {
  FT_DYNATRACE_RUM_COOKIE_CONSENT,
  FT_ONE_TRUST_COOKIE_CONSENT,
  PI,
} from '@whitbread-eos/api';
import { ErrorBoundary, Fonts, ScriptsEmbed } from '@whitbread-eos/atoms';
import { CONSENT_COOKIE, CookiePoliciesModalContainer, LiveAssist } from '@whitbread-eos/molecules';
import {
  analytics,
  AnalyticsProvider,
  AppDataProvider,
  getCookie,
  getSecureTwoURL,
  isOneTrustCookieConsentActive,
  setPageAnalytics,
  useCustomLocale,
  UserContextProvider,
  FeatureToggleContextProvider,
} from '@whitbread-eos/utils';
import { format } from 'date-fns';
import { NextPage } from 'next';
import { appWithTranslation } from 'next-i18next';
import type { AppProps } from 'next/app';
import dynamic from 'next/dynamic';
import Head from 'next/head';
import { useRouter } from 'next/router';
import { ReactElement, ReactNode, useEffect, useState } from 'react';
import smoothscroll from 'smoothscroll-polyfill';

import { DefaultLayout } from '~components';

import nextI18NextConfig from '../../next-i18next.config';
import { theme } from '../../theme';
import '../app/global.css';

type NextPageWithLayout = NextPage & {
  getLayout?: (page: ReactElement) => ReactNode;
};

type Props = AppProps & {
  Component: NextPageWithLayout;
};

const extendedTheme = extendTheme(theme);
global.WB = global.WB ?? { cache: {} };

const AuthGuard = dynamic(
  async () => {
    const { AuthGuard } = await import('@whitbread-eos/organisms');
    return { default: AuthGuard };
  },
  {
    ssr: false,
  }
);

function App({ Component, pageProps }: Props) {
  const isWindowDefined = typeof window !== 'undefined';

  const [queryClient] = useState(
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
  const router = useRouter();
  const { pathname, query } = router;

  useEffect(() => {
    if (
      process?.env?.NEXT_PUBLIC_ASSETS_URL &&
      window.location?.origin?.includes(process.env.NEXT_PUBLIC_ASSETS_URL)
    ) {
      router.replace(process.env.NEXT_PUBLIC_WORLDLINE_RETURN_URL || '/');
    }
  }, [pathname]);

  const { language, country } = useCustomLocale();
  const [isCookieConsentModalOpen, setIsCookieConsentModalOpen] = useState(true);
  const consentCookie = getCookie(CONSENT_COOKIE);
  const isOneTrustCookieConsentEnabled = isOneTrustCookieConsentActive(
    pageProps.featureToggles?.[FT_ONE_TRUST_COOKIE_CONSENT],
    `${language}-${country}`
  );
  const isDynatraceRumCookieConsentEnabled =
    pageProps.featureToggles?.[FT_DYNATRACE_RUM_COOKIE_CONSENT] ?? false;
  const closeCookieConsentModal = () => {
    setIsCookieConsentModalOpen(false);
  };
  // const getBrandByPath = () => {
  //   return !pathname.includes(BUSINESS_BOOKER) ? PI : BUSINESS_BOOKER.replace('-', '');
  // }; Commented this due to a critical bug: https://whitbreadis.atlassian.net/browse/DNRQ-43262

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
    analytics.update({
      language,
    });
  }, [language]);

  useEffect(() => {
    setPageAnalytics(pathname, 'PIB', query, language);
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

  if (pathname && pathname.includes('business-booker/cookies')) {
    return <Component {...pageProps} />;
  }

  return (
    <>
      <Head>
        <meta name="robots" content="noindex, nofollow" />
      </Head>
      <AppDataProvider>
        <iframe
          src={`${getSecureTwoURL()}/${country}/${language}/business-booker/common/login.html`}
          style={{ display: 'none' }}
          id="authIframe"
          title="BB login"
        ></iframe>
        <QueryClientProvider client={queryClient}>
          <HydrationBoundary state={pageProps.dehydratedState}>
            <FeatureToggleContextProvider defaultFeatureToggles={pageProps.featureToggles}>
              <UserContextProvider>
                <AnalyticsProvider queryClient={queryClient} isBusinessBooker={true}>
                  <ChakraProvider theme={extendedTheme}>
                    <Fonts />

                    <AuthGuard hasRegisteredSuccessfully={pageProps.hasRegisteredSuccessfully} />
                    {getLayout(<Component {...pageProps} />)}
                    {!consentCookie && !isOneTrustCookieConsentEnabled && (
                      <CookiePoliciesModalContainer
                        onClose={closeCookieConsentModal}
                        isOpen={isCookieConsentModalOpen}
                        // brand={getBrandByPath()} Commented this due to a critical bug: https://whitbreadis.atlassian.net/browse/DNRQ-43262
                        brand={PI}
                        isDynatraceRumCookieConsentEnabled={isDynatraceRumCookieConsentEnabled}
                      />
                    )}
                    <LiveAssist />
                  </ChakraProvider>
                </AnalyticsProvider>
              </UserContextProvider>
            </FeatureToggleContextProvider>
          </HydrationBoundary>
          {process.env.NODE_ENV === 'development' && pathname !== '/graphql' && (
            <ReactQueryDevtools />
          )}
        </QueryClientProvider>
        <ScriptsEmbed
          language={language}
          locale={`${language}-${country}`}
          isOneTrustCookieConsentEnabled={isOneTrustCookieConsentEnabled}
          isDynatraceRumCookieConsentEnabled={isDynatraceRumCookieConsentEnabled}
        />
      </AppDataProvider>
    </>
  );
}

export default appWithTranslation(App, nextI18NextConfig);
