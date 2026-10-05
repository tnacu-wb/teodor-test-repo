import { QueryClientProvider } from '@tanstack/react-query';
import type { BookingSummaryVariantType } from '@whitbread-eos/api';

import BookingSummaryContainer from './BookingSummaryContainer.component';

export interface Props {
  isSoftBundlesVisible?: boolean;
  queryClient: any;
  packages: any;
  bkngData: any;
  hiData: any;
  biQueryInput: any;
  basketReferenceId: string;
  variant: BookingSummaryVariantType;
  t: (id: string) => string;
  language: string | undefined;
  taxesMessage?: string;
  area?: string;
  isExtrasDisplayed?: boolean;
  submitButtonDisabled?: boolean;
  isCityTaxBreakdownEnabled?: boolean;
}

export default function BookingSummaryContainerWrapper({ queryClient, ...rest }: Readonly<Props>) {
  return (
    <QueryClientProvider client={queryClient}>
      <BookingSummaryContainer {...rest} />
    </QueryClientProvider>
  );
}
