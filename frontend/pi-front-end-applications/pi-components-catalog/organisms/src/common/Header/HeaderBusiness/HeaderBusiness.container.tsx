import { Text } from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import { GET_STATIC_CONTENT, SITE_BB } from '@whitbread-eos/api';
import { useCustomLocale, useQueryRequest } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

import HeaderVariantStepContainer from '../HeaderLeisure/HeaderVariantStep/HeaderVariantStep.container';
import HeaderBusinessVariantDefault from './HeaderBusinessVariantDefault';

export interface Props {
  variant?: 'business-default' | 'business-step';
  queryClient: QueryClient;
}

export default function HeaderBusiness({ variant, queryClient }: Readonly<Props>) {
  const { language, country } = useCustomLocale();

  const { t } = useTranslation();

  const { data: BBHeaderInfo, isLoading } = useQueryRequest(
    ['GetStaticContent', language, country],
    GET_STATIC_CONTENT,
    {
      language,
      country,
      site: SITE_BB,
      businessBooker: true,
    }
  );

  if (isLoading) {
    return <Text style={{ display: 'none' }}>{t('searchresults.list.hotel.loading')}</Text>;
  }

  return renderHeader(BBHeaderInfo, variant, queryClient);
}

function renderHeader(BBHeaderInfo: any, variant: string | undefined, queryClient: QueryClient) {
  switch (variant) {
    case 'business-step': {
      return (
        <HeaderVariantStepContainer
          headerInfoData={BBHeaderInfo.headerInformation}
          queryClient={queryClient}
          bb={true}
        />
      );
    }
    case 'business-default':
    default: {
      return <HeaderBusinessVariantDefault BBHeaderInfo={BBHeaderInfo.headerInformation} />;
    }
  }
}
