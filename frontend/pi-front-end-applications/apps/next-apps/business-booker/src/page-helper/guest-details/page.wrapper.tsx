import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import {
  QueryBookingInformationArgs,
  QueryHotelInformationArgs,
  PackagesCriteria,
  Customer,
  CompanyDetailsResponse,
} from '@whitbread-eos/api';
import { NextRouter } from 'next/router';
import React from 'react';

import GuestDetailsPageBB from './page.bb';

interface Props {
  hiQueryInput: QueryHotelInformationArgs;
  pcksQueryInput: PackagesCriteria;
  biQueryInput: QueryBookingInformationArgs;
  queryClient: QueryClient;
  router: NextRouter;
  userDetails?: Customer;
  companyDetails?: CompanyDetailsResponse;
}

export default function Page({
  pcksQueryInput,
  biQueryInput,
  hiQueryInput,
  queryClient,
  router,
  userDetails,
  companyDetails,
}: Props) {
  return (
    <QueryClientProvider client={queryClient}>
      <GuestDetailsPageBB
        queryClient={queryClient}
        router={router}
        pcksQueryInput={pcksQueryInput}
        hiQueryInput={hiQueryInput}
        biQueryInput={biQueryInput}
        userDetails={userDetails}
        companyDetails={companyDetails}
      />
    </QueryClientProvider>
  );
}
