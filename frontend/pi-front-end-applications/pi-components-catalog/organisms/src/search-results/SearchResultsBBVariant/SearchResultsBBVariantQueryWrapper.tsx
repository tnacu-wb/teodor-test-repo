import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { SRMultiSearchParamsType } from '@whitbread-eos/api';
import { type PromotionsInformation } from '@whitbread-eos/utils';

import SearchResultsBBVariantContainer from './SearchResultsBBVariant.container';

interface Props {
  queryClient: QueryClient;
  multiSearchParams: SRMultiSearchParamsType;
  onNoHotelsWarning?: (value: boolean) => void;
  variant: string;
  innBusiness?: boolean;
  promotionBannerData?: PromotionsInformation;
}

export default function SearchResultsBBVariantQueryWrapper({
  queryClient,
  multiSearchParams,
  onNoHotelsWarning,
  variant,
  innBusiness,
  promotionBannerData,
}: Readonly<Props>) {
  return (
    <QueryClientProvider client={queryClient}>
      <SearchResultsBBVariantContainer
        multiSearchParams={multiSearchParams}
        onNoHotelsWarning={onNoHotelsWarning}
        queryClient={queryClient}
        variant={variant}
        innBusiness={innBusiness}
        promotionBannerData={promotionBannerData}
      />
    </QueryClientProvider>
  );
}
