import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import type { Claims } from '@whitbread-eos/api';
import { NextRouter } from 'next/router';
import React from 'react';

import BookingsPageCcui from './page.ccui';
import EnhancedSeaarchBookingsPageCcui from './page.ccui.enhancedSearch';

interface Props {
  user: Claims;
  variant?: 'pi' | 'ccui';
  queryClient: QueryClient;
  setAnalyticsUser: any;
  router: NextRouter;
}

export default function Page({
  variant,
  queryClient,
  router,
  setAnalyticsUser,
  user,
}: Readonly<Props>) {
  return (
    <QueryClientProvider client={queryClient}>{renderPage(variant as string)}</QueryClientProvider>
  );

  function renderPage(variant: string) {
    if (variant === 'ccui') {
      return (
        <EnhancedSeaarchBookingsPageCcui
          queryClient={queryClient}
          router={router}
          user={user}
          setAnalyticsUser={setAnalyticsUser}
        />
      );
    } else {
      return (
        <BookingsPageCcui
          queryClient={queryClient}
          router={router}
          user={user}
          setAnalyticsUser={setAnalyticsUser}
        />
      );
    }
  }
}
