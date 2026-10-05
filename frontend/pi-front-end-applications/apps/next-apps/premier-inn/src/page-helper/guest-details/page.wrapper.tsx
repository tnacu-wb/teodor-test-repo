import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import {
  QueryBookingInformationArgs,
  QueryHotelInformationArgs,
  PackagesCriteria,
} from '@whitbread-eos/api';
import { NextRouter } from 'next/router';
import React from 'react';

import GuestDetailsPagePi from './page.pi';

interface Props {
  hiQueryInput: QueryHotelInformationArgs;
  pcksQueryInput: PackagesCriteria;
  biQueryInput: QueryBookingInformationArgs;
  queryClient: QueryClient;
  router: NextRouter;
  cachedGuestDetailsFormData?: Record<string, unknown>;
  userEmail?: string | null;
}

export default function Page({
  pcksQueryInput,
  biQueryInput,
  hiQueryInput,
  queryClient,
  router,
  cachedGuestDetailsFormData,
  userEmail,
}: Props) {
  return (
    <QueryClientProvider client={queryClient}>
      <GuestDetailsPagePi
        queryClient={queryClient}
        router={router}
        pcksQueryInput={pcksQueryInput}
        hiQueryInput={hiQueryInput}
        biQueryInput={biQueryInput}
        cachedGuestDetailsFormData={cachedGuestDetailsFormData}
        userEmail={userEmail}
      />
    </QueryClientProvider>
  );
}
