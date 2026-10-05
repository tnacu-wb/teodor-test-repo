'use client';

import { useEffect, useState } from 'react';

import { getCookie } from '../helpers/cookies';

function useCookieWatcher(cookieName: string, intervalTime = 1000): string | null {
  const [cookieValue, setCookieValue] = useState<string | null>(() => {
    return getCookie(cookieName);
  });

  useEffect(() => {
    const interval = setInterval(() => {
      const currentValue = getCookie(cookieName);
      if (currentValue !== cookieValue) {
        setCookieValue(currentValue);
      }
    }, intervalTime);

    return () => clearInterval(interval);
  }, [cookieName, cookieValue]);

  return cookieValue;
}

export default useCookieWatcher;
