import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import {
  QueryBookingInformationArgs,
  QueryHotelInformationArgs,
  PackagesCriteria,
} from '@whitbread-eos/api';
import { FormProps } from '@whitbread-eos/atoms';
import { NextRouter } from 'next/router';
import React from 'react';

import GuestDetailsPageCcui from './page.ccui';

interface Props {
  hiQueryInput: QueryHotelInformationArgs;
  pcksQueryInput: PackagesCriteria;
  biQueryInput: QueryBookingInformationArgs;
  queryClient: QueryClient;
  router: NextRouter;
  cachedGuestDetailsFormData?: FormProps['defaultValues'];
}

export default function Page({
  pcksQueryInput,
  biQueryInput,
  hiQueryInput,
  queryClient,
  router,
  cachedGuestDetailsFormData,
}: Props) {
  return (
    <QueryClientProvider client={queryClient}>
      <GuestDetailsPageCcui
        queryClient={queryClient}
        router={router}
        pcksQueryInput={pcksQueryInput}
        hiQueryInput={hiQueryInput}
        biQueryInput={biQueryInput}
        cachedGuestDetailsFormData={cachedGuestDetailsFormData}
      />
    </QueryClientProvider>
  );
}
