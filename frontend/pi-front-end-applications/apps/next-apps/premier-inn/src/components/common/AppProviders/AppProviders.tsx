import { Auth0Provider } from '@auth0/nextjs-auth0';
import { ChakraProvider } from '@chakra-ui/react';
import {
  DehydratedState,
  HydrationBoundary,
  QueryClient,
  QueryClientProvider,
} from '@tanstack/react-query';
import { Fonts } from '@whitbread-eos/atoms';
import {
  AnalyticsProvider,
  FeatureToggleContextProvider,
  UserContextProvider,
} from '@whitbread-eos/utils';
import { memo, ReactNode, useCallback, useMemo } from 'react';
import { SWRConfig } from 'swr';

import { theme } from '../../../../theme';
import { Auth0LoginWatcher } from './Auth0LoginWatcher';

interface AppProvidersProps {
  children: ReactNode;
  queryClient: QueryClient;
  dehydratedState?: DehydratedState;
  featureToggles?: Record<string, boolean>;
}

/**
 * AppProviders component centralizes all provider logic
 * This reduces the size of _app.tsx and improves maintainability
 *
 * Performance optimizations:
 * - Memoized to prevent unnecessary re-renders
 * - Theme is pre-extended in theme.ts (no runtime extension)
 * - Providers ordered for optimal rendering
 */
const AppProviders = memo(function AppProviders({
  children,
  queryClient,
  dehydratedState,
  featureToggles,
}: AppProvidersProps) {
  // Memoize featureToggles to prevent FeatureToggleContextProvider re-renders
  const memoizedToggles = useMemo(() => featureToggles, [featureToggles]);

  const renderAuth0Watcher = useCallback(
    (onChange: (isLoggedIn: boolean) => void) => <Auth0LoginWatcher onChange={onChange} />,
    []
  );

  return (
    <QueryClientProvider client={queryClient}>
      <HydrationBoundary state={dehydratedState}>
        <SWRConfig
          value={{
            shouldRetryOnError: false,
            revalidateOnFocus: false,
            revalidateOnReconnect: false,
          }}
        >
          <Auth0Provider>
            <FeatureToggleContextProvider defaultFeatureToggles={memoizedToggles}>
              <UserContextProvider authWatcher={renderAuth0Watcher}>
                <AnalyticsProvider queryClient={queryClient}>
                  <ChakraProvider theme={theme}>
                    <Fonts />
                    {children}
                  </ChakraProvider>
                </AnalyticsProvider>
              </UserContextProvider>
            </FeatureToggleContextProvider>
          </Auth0Provider>
        </SWRConfig>
      </HydrationBoundary>
    </QueryClientProvider>
  );
});

export { AppProviders };
