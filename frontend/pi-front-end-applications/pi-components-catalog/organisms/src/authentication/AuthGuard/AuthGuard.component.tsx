import { LOCALES } from '@whitbread-eos/api';
import {
  getPathForLocale,
  useCustomLocale,
  useUserData,
  isInnBusinessApp,
  PIB_MANUAL_LOGOUT_FLAG,
} from '@whitbread-eos/utils';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';

import AuthContentManagerBBVariant from '../AuthContentManager/AuthContentManagerBBVariant';

interface Props {
  hasRegisteredSuccessfully?: boolean;
}

const publicRoutes = [
  '/404',
  '/graphql',
  '/reset-password',
  '/business-booker/reset-password',
  '/business-booker/graphql',
  '/business-booker/404',
];

export default function AuthGuard({ hasRegisteredSuccessfully }: Readonly<Props>) {
  const router = useRouter();
  const { key } = router.query;
  const [isLoginModalOpen, setIsLoginModalOpen] = useState(true);
  const { isLoggedIn } = useUserData();
  const { language, country } = useCustomLocale();
  const isInnBusiness = isInnBusinessApp(window?.location?.host ?? '');

  useEffect(() => {
    if (isInnBusiness && !isLoggedIn && !publicRoutes.includes(router.route)) {
      const isManualLogout = sessionStorage?.getItem(PIB_MANUAL_LOGOUT_FLAG) === 'true';
      const loginPath = isManualLogout ? 'account/login' : 'account/login?timeout=true';
      sessionStorage?.removeItem(PIB_MANUAL_LOGOUT_FLAG);
      window.location.href = getPathForLocale(`${language}-${country}` as LOCALES, loginPath);
    }
  }, [isInnBusiness, isLoggedIn, router.route]);

  const toggleLoginModal = () => {
    setIsLoginModalOpen(!isLoginModalOpen);
  };

  if (isInnBusiness) {
    return <></>;
  }

  return (
    <>
      {!isLoggedIn && !publicRoutes.includes(router.route) && (
        <AuthContentManagerBBVariant
          isLoginModalOpen={isLoginModalOpen}
          showRegisterNotification={!!key}
          hasRegisteredSuccessfully={hasRegisteredSuccessfully}
          toggleLoginModal={toggleLoginModal}
        />
      )}
    </>
  );
}
