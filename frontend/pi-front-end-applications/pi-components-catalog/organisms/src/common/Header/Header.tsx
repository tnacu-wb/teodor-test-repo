import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import type { Claims } from '@whitbread-eos/api';

import HeaderAgent from './HeaderAgent';
import HeaderBusiness from './HeaderBusiness';
import HeaderLeisure from './HeaderLeisure/HeaderLeisure.container';

export interface Props {
  variant: 'default' | 'step' | 'logo' | 'agent' | 'business-default' | 'business-step';
  isIcon?: boolean;
  user?: Claims;
  queryClient?: QueryClient;
  roles?: string[];
  bb?: boolean;
  useNextImage?: boolean;
}

export default function Header({
  variant,
  isIcon,
  queryClient,
  user,
  roles,
  bb = false,
  useNextImage,
}: Props) {
  return renderHeader(variant);
  function renderHeader(variant: string) {
    switch (variant) {
      case 'business-step':
      case 'business-default':
        return (
          <QueryClientProvider client={queryClient as QueryClient}>
            <HeaderBusiness variant={variant} queryClient={queryClient as QueryClient} />
          </QueryClientProvider>
        );

      case 'default':
      case 'step':
      case 'logo':
        return (
          <HeaderLeisure
            isIcon={isIcon}
            variant={variant}
            queryClient={queryClient as QueryClient}
            bb={bb}
            useNextImage={useNextImage}
          />
        );

      case 'agent':
      default:
        return <HeaderAgent user={user} roles={roles} queryClient={queryClient as QueryClient} />;
    }
  }
}
