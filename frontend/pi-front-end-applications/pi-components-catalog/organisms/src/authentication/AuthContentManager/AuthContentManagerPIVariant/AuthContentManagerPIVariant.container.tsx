import {
  type AuthenticationLabels,
  DEFAULT_LOGIN_LABELS,
  GET_STATIC_CONTENT,
  Language,
  SITE_LEISURE,
} from '@whitbread-eos/api';
import { useCustomLocale, useQueryRequest } from '@whitbread-eos/utils';
import { useEffect, useState } from 'react';

import AuthContentManagerPIVariant from './AuthContentManagerPIVariant.component';

export interface Props {
  isLoginModalOpen: boolean;
  toggleLoginModal: () => void;
}

export default function AuthContentManagerPIVariantContainer({
  isLoginModalOpen,
  toggleLoginModal,
}: Readonly<Props>) {
  const { language: currentLanguage, country: currentCountry } = useCustomLocale();
  const { data } = useQueryRequest(
    ['GetStaticContent', currentLanguage, currentCountry],
    GET_STATIC_CONTENT,
    {
      language: currentLanguage as Language,
      country: currentCountry,
      site: SITE_LEISURE,
      businessBooker: false,
    }
  );
  const [labels, setLabels] = useState<AuthenticationLabels>(DEFAULT_LOGIN_LABELS);

  useEffect(() => {
    if (data?.headerInformation?.content?.authentication) {
      setLabels(data?.headerInformation.content?.authentication);
    }
  }, [data?.headerInformation?.content?.authentication]);

  return (
    <AuthContentManagerPIVariant
      isLoginModalOpen={isLoginModalOpen}
      toggleLoginModal={toggleLoginModal}
      labels={labels}
      headerInfoData={data?.headerInformation}
    />
  );
}
