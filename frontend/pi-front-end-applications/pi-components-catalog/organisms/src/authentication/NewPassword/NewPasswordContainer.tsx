import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import {
  DEFAULT_RESET_PASSWORD_LABELS,
  GET_STATIC_CONTENT,
  Language,
  ResetPasswordLabels,
  SITE_BB,
  SITE_LEISURE,
} from '@whitbread-eos/api';
import { FormProps } from '@whitbread-eos/atoms';
import { useCustomLocale, useQueryRequest } from '@whitbread-eos/utils';
import { useEffect, useState } from 'react';

import NewPassword from './NewPassword.component';

interface Props {
  queryClient: QueryClient;
  defaultValues: FormProps['defaultValues'];
  toggleLoginModal: () => void;
  token: string;
  isBusinessBooker: boolean;
}

export default function NewPasswordContainer({
  queryClient,
  isBusinessBooker,
  ...rest
}: Readonly<Props>) {
  const { language, country } = useCustomLocale();
  const { data } = useQueryRequest(
    ['GetStaticContent', language, country],
    GET_STATIC_CONTENT,
    {
      language: language as Language,
      country: country,
      site: isBusinessBooker ? SITE_BB : SITE_LEISURE,
      businessBooker: isBusinessBooker,
    },
    { enabled: isBusinessBooker }
  );
  const [labels, setLabels] = useState<ResetPasswordLabels>(DEFAULT_RESET_PASSWORD_LABELS);

  useEffect(() => {
    if (data?.headerInformation?.content?.authentication) {
      setLabels(data?.headerInformation.content?.authentication);
    }
  }, [data?.headerInformation?.content?.authentication]);

  return (
    <QueryClientProvider client={queryClient}>
      <NewPassword {...rest} isBusinessBooker={isBusinessBooker} labels={labels} />
    </QueryClientProvider>
  );
}
