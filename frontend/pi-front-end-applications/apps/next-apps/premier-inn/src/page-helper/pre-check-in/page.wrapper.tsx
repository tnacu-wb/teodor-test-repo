import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

import PreCheckinPagePI from './page.pi';

interface Props {
  queryClient: QueryClient;
}
export default function Page({ queryClient }: Readonly<Props>) {
  return (
    <QueryClientProvider client={queryClient}>
      <PreCheckinPagePI />
    </QueryClientProvider>
  );
}
