import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import React from 'react';

import SearchAccountPageCCUI from './page.ccui';

interface Props {
  queryClient: QueryClient;
  accessToken: string;
}

export default function Page({ queryClient, accessToken }: Readonly<Props>) {
  return <QueryClientProvider client={queryClient}>{renderPage()}</QueryClientProvider>;

  function renderPage() {
    return <SearchAccountPageCCUI queryClient={queryClient} accessToken={accessToken} />;
  }
}
