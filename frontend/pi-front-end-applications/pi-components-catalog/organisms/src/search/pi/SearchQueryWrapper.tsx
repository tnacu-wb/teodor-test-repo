import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

import Search, { Props as SearchProps } from './Search.container';

interface Props extends SearchProps {
  queryClient: QueryClient;
  handleLocationSearch?: (placeId: string) => void;
  setLocationName?: (locationName: string) => void;
}

export default function SearchQueryWrapper({ queryClient, ...rest }: Readonly<Props>) {
  return (
    <QueryClientProvider client={queryClient}>
      <Search {...rest} />
    </QueryClientProvider>
  );
}
