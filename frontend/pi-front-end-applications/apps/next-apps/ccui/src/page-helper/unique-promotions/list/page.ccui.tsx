import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import type { Claims } from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import { PromoBatchesTable } from '@whitbread-eos/molecules';

type PromoBatchListProps = {
  queryClient: QueryClient;
  user?: Claims;
};

export default function Page({ queryClient }: Readonly<PromoBatchListProps>) {
  return (
    <QueryClientProvider client={queryClient}>
      <ErrorBoundary errorMessage="We were unable to load the promotion batches. Please try again later.">
        <PromoBatchesTable />
      </ErrorBoundary>
    </QueryClientProvider>
  );
}
