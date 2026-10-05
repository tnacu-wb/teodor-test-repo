'use client';

import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import { z } from 'zod';

import { ID_TOKEN_COOKIE } from '../global-constants';
import useCookieWatcher from './use-cookie-watcher';

enum LoginActions {
  LOGIN_ERROR = 'loginError',
  LOGIN_SUCCESS = 'userLoggedIn',
}

type LoginReturn = {
  isError: boolean;
  isSubmitting: boolean;
  handleLogin: (data: z.infer<z.ZodSchema>) => void;
};

function useInnBusinessLogin(
  redirectTo: string,
  iframeSecureUrl: string,
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  schema: any
): LoginReturn {
  const [isError, setIsError] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [hasLoggedIn, setHasLoggedIn] = useState(false);
  const isTokenSet = useCookieWatcher(ID_TOKEN_COOKIE, 500);
  const router = useRouter();

  useEffect(() => {
    const handleMessages = (message: { origin: string; data: object }) => {
      if (message?.origin === iframeSecureUrl) {
        let data;
        try {
          data = typeof message?.data === 'string' ? JSON.parse(message.data) : message.data;
        } catch {
          data = message.data;
        }

        const action = data.action;
        if (action === LoginActions.LOGIN_ERROR) {
          setIsError(true);
          setIsSubmitting(false);
        } else if (action === LoginActions.LOGIN_SUCCESS) {
          setIsError(false);
          setHasLoggedIn(true);
        }
      }
    };

    window.addEventListener('message', handleMessages);

    return () => {
      window.removeEventListener('message', handleMessages);
    };
  }, [redirectTo]);

  useEffect(() => {
    if (hasLoggedIn && isTokenSet) {
      router.replace(redirectTo);
    }
  }, [isTokenSet, hasLoggedIn]);

  const handleLogin = (data: z.infer<typeof schema>) => {
    setIsError(false);
    const authIframe = document.getElementById('authIframe') as HTMLIFrameElement;
    if (authIframe?.contentWindow) {
      setIsSubmitting(true);
      const message = JSON.stringify({
        action: 'login',
        username: data.email,
        password: data.password,
        isBusiness: true,
      });

      try {
        authIframe.contentWindow.postMessage(message, iframeSecureUrl);
      } catch (error) {
        setIsError(true);
        setIsSubmitting(false);
      }
    } else {
      setIsError(true);
    }
  };

  return {
    isError,
    isSubmitting,
    handleLogin,
  };
}

export default useInnBusinessLogin;
