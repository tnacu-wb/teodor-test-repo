import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { SRMultiSearchParamsType } from '@whitbread-eos/api';
import { type PromotionsInformation } from '@whitbread-eos/utils';

import SearchResultsPIVariantContainer from './SearchResultsPIVariant.container';

interface Props {
  queryClient: QueryClient;
  multiSearchParams: SRMultiSearchParamsType;
  onNoHotelsWarning?: (value: boolean) => void;
  promotionBannerData?: PromotionsInformation;
}

export default function SearchResultsPIVariantQueryWrapper({
  queryClient,
  multiSearchParams,
  onNoHotelsWarning,
  promotionBannerData,
}: Readonly<Props>) {
  return (
    <QueryClientProvider client={queryClient}>
      <SearchResultsPIVariantContainer
        multiSearchParams={multiSearchParams}
        onNoHotelsWarning={onNoHotelsWarning}
        queryClient={queryClient}
        promotionBannerData={promotionBannerData}
      />
    </QueryClientProvider>
  );
}
