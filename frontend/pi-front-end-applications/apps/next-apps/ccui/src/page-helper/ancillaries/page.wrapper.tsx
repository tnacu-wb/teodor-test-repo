import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import {
  Claims,
  QueryBookingInformationArgs,
  QueryHotelInformationArgs,
  PackagesCriteria,
} from '@whitbread-eos/api';
import { NextRouter } from 'next/router';
import React from 'react';

import AncillariesPageCcui from './page.ccui';

interface Props {
  hiQueryInput: QueryHotelInformationArgs;
  pcksQueryInput: PackagesCriteria;
  biQueryInput: QueryBookingInformationArgs;
  user?: Claims;
  queryClient: QueryClient;
  router: NextRouter;
  setAnalyticsUser?: any;
}

export default function Page({
  pcksQueryInput,
  biQueryInput,
  hiQueryInput,
  user,
  queryClient,
  setAnalyticsUser,
  router,
}: Readonly<Props>) {
  return <QueryClientProvider client={queryClient}>{renderPage()}</QueryClientProvider>;
  function renderPage() {
    return (
      <AncillariesPageCcui
        user={user}
        router={router}
        hiQueryInput={hiQueryInput}
        pcksQueryInput={pcksQueryInput}
        biQueryInput={biQueryInput}
        queryClient={queryClient}
        setAnalyticsUser={setAnalyticsUser}
      />
    );
  }
}
