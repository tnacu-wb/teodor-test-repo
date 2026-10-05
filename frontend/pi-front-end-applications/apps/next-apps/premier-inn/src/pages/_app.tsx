import { QueryClient } from '@tanstack/react-query';
import {
  BUSINESS_BOOKER,
  PI,
  PROMO_CODE_COOKIE,
  SESSION_STORAGE_PROMO_COOKIE_SET,
  FT_DYNATRACE_RUM_COOKIE_CONSENT,
  FT_ONE_TRUST_COOKIE_CONSENT,
  FT_PI_AUTH0_LOGIN,
} from '@whitbread-eos/api';
import { ErrorBoundary, ScriptsEmbed } from '@whitbread-eos/atoms';
import { CookiePoliciesModalContainer } from '@whitbread-eos/molecules';
import {
  AppDataProvider,
  isOneTrustCookieConsentActive,
  setCookieWithDefaultDomain,
  useCustomLocale,
} from '@whitbread-eos/utils';
import type { NextPage } from 'next';
import { appWithTranslation } from 'next-i18next';
import type { AppProps } from 'next/app';
import dynamic from 'next/dynamic';
import { useRouter } from 'next/router';
import { memo, ReactElement, ReactNode, useCallback, useEffect, useMemo, useState } from 'react';

import { AppProviders, AuthIframe, DefaultLayout } from '~components';
import { useAmazonChat } from '~hooks/use-amazon-chat';
import { useAppAnalytics } from '~hooks/use-app-analytics';
import { useAppEventListeners } from '~hooks/use-app-event-listeners';
import { useCookieConsent } from '~hooks/use-cookie-consent';

import nextI18NextConfig from '../../next-i18next.config';

// Development-only imports - completely tree-shaken in production builds
// DevTools: ~200KB, I18NLabels: ~30KB - never included in production
const ReactQueryDevtools =
  process.env.NODE_ENV === 'development'
    ? dynamic(
        () => import('@tanstack/react-query-devtools').then((mod) => mod.ReactQueryDevtools),
        { ssr: false }
      )
    : () => null;

const I18NLabels =
  process.env.NODE_ENV === 'development'
    ? dynamic(() => import('~components/common/I18NLabels'), { ssr: false })
    : () => null;

// Type declarations
interface Mode {
  mode: string;
}

declare global {
  interface Window {
    amazon_connect: { (...args: unknown[]): void; ac?: unknown[] };
    piConfig: {
      [key: string]: Mode;
      paymentsRedesign: Mode;
      billingAddressCapture: Mode;
      digRegCard: Mode;
      ancillaries: Mode;
      roomPickerRedesign: Mode;
    };
  }
}

type NextPageWithLayout = NextPage & { getLayout?: (page: ReactElement) => ReactNode };
type Props = AppProps & { Component: NextPageWithLayout };

// Initialize global cache once - prevents recreation on hot reload
if (typeof global !== 'undefined') {
  global.WB = global.WB ?? { cache: {} };
}

// QueryClient factory - extracted for cleaner code and stable reference
const createQueryClient = () =>
  new QueryClient({
    defaultOptions: {
      queries: {
        refetchOnWindowFocus: false,
        refetchInterval: false,
        staleTime: 300_000, // 5 minutes
        gcTime: 600_000, // 10 minutes garbage collection
      },
    },
  });

function App({ Component, pageProps }: Props) {
  // Stable QueryClient instance - useState with factory prevents recreation
  const [queryClient] = useState(createQueryClient);

  const { pathname, query } = useRouter();
  const { language, country } = useCustomLocale();
  const isOneTrustCookieConsentEnabled = isOneTrustCookieConsentActive(
    pageProps.featureToggles?.[FT_ONE_TRUST_COOKIE_CONSENT],
    `${language}-${country}`
  );
  const isDynatraceRumCookieConsentEnabled =
    pageProps.featureToggles?.[FT_DYNATRACE_RUM_COOKIE_CONSENT] ?? false;
  const isAuth0Enabled = pageProps.featureToggles?.[FT_PI_AUTH0_LOGIN] ?? false;

  // Custom hooks for extracted logic
  const { isCookieConsentModalOpen, closeCookieConsentModal, consentCookie } = useCookieConsent();
  useAppAnalytics({ language, pathname, query });
  useAppEventListeners();

  // Amazon Chat integration - memoize props to prevent re-renders
  const amazonChatProps = useMemo(() => ({ pageProps, language }), [pageProps, language]);
  const { isAmazonChatBoxEnabled, amazonChatIcon } = useAmazonChat(amazonChatProps);

  // Memoize brand calculation
  const brand = useMemo(
    () => (!pathname.includes(BUSINESS_BOOKER) ? PI : BUSINESS_BOOKER.replace('-', '')),
    [pathname]
  );

  // Memoize default layout wrapper to prevent recreation
  const defaultLayoutWrapper = useCallback(
    (page: ReactElement) => (
      <DefaultLayout>
        <ErrorBoundary>{page}</ErrorBoundary>
      </DefaultLayout>
    ),
    []
  );

  const getLayout = Component.getLayout ?? defaultLayoutWrapper;

  // One-time initialization: polyfill and cookie cleanup
  useEffect(() => {
    // Lazy load polyfill - not needed in initial bundle
    import('smoothscroll-polyfill').then((m) => m.default.polyfill());

    // Clear stale promo cookie
    if (!sessionStorage.getItem(SESSION_STORAGE_PROMO_COOKIE_SET)) {
      setCookieWithDefaultDomain(PROMO_CODE_COOKIE, null, -1);
    }
  }, []);

  // Memoize rendered page to prevent unnecessary re-renders
  const renderedPage = useMemo(
    () => getLayout(<Component {...pageProps} />),
    [Component, pageProps, getLayout]
  );

  return (
    <AppDataProvider>
      <AppProviders
        queryClient={queryClient}
        dehydratedState={pageProps.dehydratedState}
        featureToggles={pageProps.featureToggles}
      >
        <AuthIframe country={country} language={language} isAuth0Enabled={isAuth0Enabled} />
        {renderedPage}

        {/* Cookie consent modal - only rendered when no consent */}
        {/* only show current whitbread cookie consent modal when OneTrust (3rd party) cookie consent flag is not enabled */}
        {!isOneTrustCookieConsentEnabled && !consentCookie && isCookieConsentModalOpen && (
          <CookiePoliciesModalContainer
            onClose={closeCookieConsentModal}
            isOpen={isCookieConsentModalOpen}
            brand={brand}
            isDynatraceRumCookieConsentEnabled={isDynatraceRumCookieConsentEnabled}
          />
        )}

        {/* Dev tools - completely excluded from production */}
        {process.env.NODE_ENV === 'development' && pathname !== '/graphql' && (
          <>
            <ReactQueryDevtools />
            <I18NLabels />
          </>
        )}
      </AppProviders>

      <ScriptsEmbed
        isAmazonChatBoxEnabled={isAmazonChatBoxEnabled}
        language={language}
        locale={`${language}-${country}`}
        amazonChatIcon={amazonChatIcon}
        isOneTrustCookieConsentEnabled={isOneTrustCookieConsentEnabled}
        isDynatraceRumCookieConsentEnabled={isDynatraceRumCookieConsentEnabled}
      />
    </AppDataProvider>
  );
}

// Memoize the entire App to prevent re-renders from parent
export default appWithTranslation(memo(App), nextI18NextConfig);
