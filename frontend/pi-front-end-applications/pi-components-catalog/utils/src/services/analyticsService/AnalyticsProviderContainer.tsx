import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AnalyticsData } from '@whitbread-eos/api';
import { ReactNode } from 'react';

import { AnalyticsProviderComponent } from './AnalyticsProvider.component';

interface Props {
  queryClient: QueryClient;
  children: ReactNode;
  initialAnalyticsData?: AnalyticsData | null;
  isBusinessBooker?: boolean;
}

export default function AnalyticsProviderContainer({
  children,
  initialAnalyticsData,
  queryClient,
  isBusinessBooker,
}: Readonly<Props>) {
  return (
    <QueryClientProvider client={queryClient}>
      <AnalyticsProviderComponent
        initialAnalyticsData={initialAnalyticsData}
        isBusinessBooker={isBusinessBooker}
      >
        {children}
      </AnalyticsProviderComponent>
    </QueryClientProvider>
  );
}
