import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import {
  type AuthenticationLabels,
  DEFAULT_LOGIN_LABELS,
  GET_STATIC_CONTENT,
  SITE_LEISURE,
} from '@whitbread-eos/api';
import { useCustomLocale, useQueryRequest } from '@whitbread-eos/utils';
import { useEffect, useState } from 'react';

import OptionalAuthentication from './OptionalAuthentication.component';

interface Props {
  queryClient: QueryClient;
  showIcon?: boolean;
  isRegisterSelected: boolean;
  setRegisterSectionSelected: (param: boolean) => void;
}

export default function OptionalAuthenticationContainer({
  queryClient,
  showIcon = true,
  isRegisterSelected,
  setRegisterSectionSelected,
}: Readonly<Props>) {
  const { country: currentCountry, language: currentLang } = useCustomLocale();
  const headerLabels = useQueryRequest(
    ['GetStaticContent', currentLang, currentCountry],
    GET_STATIC_CONTENT,
    {
      language: currentLang,
      country: currentCountry,
      site: SITE_LEISURE,
      businessBooker: false,
    }
  );

  const [labels, setLabels] = useState<AuthenticationLabels>(DEFAULT_LOGIN_LABELS);

  useEffect(() => {
    if (headerLabels?.data?.headerInformation?.content?.authentication) {
      setLabels(headerLabels?.data?.headerInformation.content?.authentication);
    }
  }, [headerLabels?.data?.headerInformation?.content?.authentication]);

  return (
    <QueryClientProvider client={queryClient}>
      <OptionalAuthentication
        labels={labels}
        showIcon={showIcon}
        isRegisterSelected={isRegisterSelected}
        setRegisterSectionSelected={setRegisterSectionSelected}
      />
    </QueryClientProvider>
  );
}
