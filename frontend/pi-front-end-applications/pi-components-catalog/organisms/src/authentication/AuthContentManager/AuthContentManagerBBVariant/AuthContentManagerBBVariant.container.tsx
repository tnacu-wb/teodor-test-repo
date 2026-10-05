import {
  type AuthenticationLabels,
  DEFAULT_LOGIN_LABELS,
  GET_STATIC_CONTENT,
  Language,
  SITE_BB,
} from '@whitbread-eos/api';
import { useCustomLocale, useQueryRequest } from '@whitbread-eos/utils';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';

import AuthContentManagerBBVariant from './AuthContentManagerBBVariant.component';

export interface Props {
  isLoginModalOpen: boolean;
  showRegisterNotification?: boolean;
  hasRegisteredSuccessfully?: boolean;
  toggleLoginModal: () => void;
}

export default function AuthContentManagerBBVariantContainer({
  isLoginModalOpen,
  showRegisterNotification,
  hasRegisteredSuccessfully,
  toggleLoginModal,
}: Readonly<Props>) {
  const router = useRouter();
  const { redirect } = router.query;
  const { language: currentLanguage, country: currentCountry } = useCustomLocale();
  const { data } = useQueryRequest(
    ['GetStaticContent', currentLanguage, currentCountry],
    GET_STATIC_CONTENT,
    {
      language: currentLanguage as Language,
      country: currentCountry,
      site: SITE_BB,
      businessBooker: true,
    }
  );
  const [labels, setLabels] = useState<AuthenticationLabels>(DEFAULT_LOGIN_LABELS);

  useEffect(() => {
    if (
      data?.headerInformation?.content?.authentication &&
      data?.headerInformation?.config?.authentication
    ) {
      setLabels({
        ...data?.headerInformation?.content?.authentication,
        ...data?.headerInformation?.config?.authentication,
      });
    }
  }, [
    data?.headerInformation?.config?.authentication,
    data?.headerInformation?.content?.authentication,
  ]);

  return (
    <AuthContentManagerBBVariant
      {...{
        isLoginModalOpen,
        toggleLoginModal,
        showRegisterNotification,
        hasRegisteredSuccessfully,
        labels,
        onGoBack: redirect ? () => router.back() : undefined,
        goBackButtonText: redirect ? labels?.goBackButton : undefined,
      }}
    />
  );
}
