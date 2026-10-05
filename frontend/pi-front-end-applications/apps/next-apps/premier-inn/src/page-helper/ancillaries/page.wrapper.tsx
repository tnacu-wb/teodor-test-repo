import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import {
  Claims,
  QueryBookingInformationArgs,
  QueryHotelInformationArgs,
  PackagesCriteria,
} from '@whitbread-eos/api';
import { NextRouter } from 'next/router';
import React from 'react';

import AncillariesPagePi from './page.pi';

interface Props {
  hiQueryInput: QueryHotelInformationArgs;
  pcksQueryInput: PackagesCriteria;
  biQueryInput: QueryBookingInformationArgs;
  user?: Claims;
  queryClient: QueryClient;
  router: NextRouter;
  setAnalyticsUser?: any;
}

interface Mode {
  mode: string;
}

declare global {
  interface Window {
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

export default function Page({
  pcksQueryInput,
  biQueryInput,
  hiQueryInput,
  queryClient,
  router,
}: Readonly<Props>) {
  return <QueryClientProvider client={queryClient}>{renderPage()}</QueryClientProvider>;

  function renderPage() {
    return (
      <AncillariesPagePi
        queryClient={queryClient}
        pcksQueryInput={pcksQueryInput}
        hiQueryInput={hiQueryInput}
        biQueryInput={biQueryInput}
        router={router}
      />
    );
  }
}
