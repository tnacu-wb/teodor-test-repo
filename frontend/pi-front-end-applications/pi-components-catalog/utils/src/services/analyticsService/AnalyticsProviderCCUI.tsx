'use client';

import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AnalyticsData } from '@whitbread-eos/api';
import { ReactNode, useEffect } from 'react';

declare global {
  interface Window {
    analyticsData: AnalyticsData;
  }
}

interface Props {
  children: ReactNode;
  initialAnalyticsData?: AnalyticsData | null;
  queryClient: QueryClient;
}

let defaultAnalyticsData: AnalyticsData = {};

export default function AnalyticsProvider({
  children,
  initialAnalyticsData = defaultAnalyticsData,
  queryClient,
}: Readonly<Props>) {
  if (typeof window !== 'undefined') {
    defaultAnalyticsData = {
      browserTimeZone: Intl.DateTimeFormat().resolvedOptions().timeZone,
      environment: process.env.NODE_ENV === 'development' ? 'development' : 'production',
    };
  }

  useEffect(() => {
    if (typeof window !== 'undefined') {
      window.analyticsData = {
        ...window.analyticsData,
        ...initialAnalyticsData,
      };
    }
  }, [initialAnalyticsData]);

  return <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>;
}
