import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import type { Claims } from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';

import CreatePromotionCodeCcui from './page.ccui';

interface Props {
  queryClient: QueryClient;
  user?: Claims;
}

export default function Page({ queryClient, user }: Readonly<Props>) {
  return (
    <QueryClientProvider client={queryClient}>
      <ErrorBoundary noContentBoundary>
        <CreatePromotionCodeCcui user={user} />
      </ErrorBoundary>
    </QueryClientProvider>
  );
}
