'use client';

import { FT_PI_AUTH0_LOGIN } from '@whitbread-eos/api';
import React, { ReactNode, useContext, useEffect, useMemo, useRef, useState } from 'react';

import { decodeIdToken, getAuthCookie, ID_TOKEN_COOKIE } from '../getters';
import { deleteCookie } from '../helpers';
import { useCustomLocale, useFeatureToggle } from '../hooks';
import noop from '../utils/noop';

export type UserDataContextType = {
  isLoggedIn: boolean;
  setIsLoggedIn: React.Dispatch<React.SetStateAction<boolean>>;
};

export const UserDataContext = React.createContext<UserDataContextType>({
  isLoggedIn: false,
  setIsLoggedIn: noop,
});

interface UserContextProviderProps {
  children: ReactNode;
  // Consumer-supplied slot for an Auth0 session watcher (e.g. wrapping the
  // SDK's useUser()). Kept out of this package so apps that never enable
  // FT_PI_AUTH0_LOGIN (CCUI, business-booker) don't need @auth0/nextjs-auth0
  // in their dependency/test graph at all.
  authWatcher?: (onChange: (isLoggedIn: boolean) => void) => ReactNode;
}

export function useUserData() {
  return useContext(UserDataContext);
}

export function UserContextProvider({ children, authWatcher }: Readonly<UserContextProviderProps>) {
  const { country } = useCustomLocale();
  const featureToggles = useFeatureToggle();
  const isAuth0Enabled = featureToggles?.[FT_PI_AUTH0_LOGIN] ?? false;

  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const isLoggedInRef = useRef(isLoggedIn);
  useEffect(() => {
    isLoggedInRef.current = isLoggedIn;
  }, [isLoggedIn]);

  const value = useMemo(
    () => ({
      isLoggedIn,
      setIsLoggedIn,
    }),
    [isLoggedIn]
  );

  useEffect(() => {
    if (!isAuth0Enabled) {
      const cookie = getAuthCookie();
      const { email } = decodeIdToken(cookie);
      setIsLoggedIn(!!email?.length);
    }
  }, [isAuth0Enabled]);

  // Legacy cookie flow: poll for cookie changes
  useEffect(() => {
    if (isAuth0Enabled) {
      return; // Skip legacy polling when Auth0 is enabled
    }

    const intervalId = setInterval(() => {
      const idToken = getAuthCookie();

      if (!idToken) {
        setIsLoggedIn(false);
      }
      if (idToken && !isLoggedInRef.current) {
        const { email: userEmail } = decodeIdToken(idToken);

        if (userEmail?.length) {
          setIsLoggedIn(true);
        }
      } else if (idToken && isLoggedInRef.current) {
        const { exp: expData } = decodeIdToken(idToken);
        const currentTimeInSeconds = new Date().getTime() / 1000;
        if (expData && currentTimeInSeconds > expData) {
          deleteCookie(ID_TOKEN_COOKIE);
        }
      }
    }, 1000);
    return () => {
      clearInterval(intervalId);
    };
  }, [country, isAuth0Enabled]);

  return (
    <UserDataContext.Provider value={value}>
      {isAuth0Enabled && authWatcher?.(setIsLoggedIn)}
      {children}
    </UserDataContext.Provider>
  );
}
