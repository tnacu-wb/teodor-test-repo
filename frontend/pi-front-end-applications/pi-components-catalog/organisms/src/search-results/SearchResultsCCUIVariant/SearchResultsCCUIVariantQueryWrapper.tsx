import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { SRMultiSearchParamsType } from '@whitbread-eos/api';
import { PromotionsInformation } from '@whitbread-eos/utils';

import SearchResultsCCUIVariantContainer from './SearchResultsCCUIVariant.container';

interface Props {
  queryClient: QueryClient;
  multiSearchParams: SRMultiSearchParamsType;
  onNoHotelsWarning?: (value: boolean) => void;
  isSearchError: boolean;
  promotionBannerData?: PromotionsInformation;
}

export default function SearchResultsCCUIVariantQueryWrapper({
  queryClient,
  multiSearchParams,
  onNoHotelsWarning,
  isSearchError,
  promotionBannerData,
}: Readonly<Props>) {
  return (
    <QueryClientProvider client={queryClient}>
      <SearchResultsCCUIVariantContainer
        multiSearchParams={multiSearchParams}
        onNoHotelsWarning={onNoHotelsWarning}
        queryClient={queryClient}
        isSearchError={isSearchError}
        promotionBannerData={promotionBannerData}
      />
    </QueryClientProvider>
  );
}
