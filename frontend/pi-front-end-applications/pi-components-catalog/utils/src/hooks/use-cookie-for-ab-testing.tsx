'use client';

import { useState, useEffect } from 'react';

import { getCookie } from '../helpers';

// Custom hook to check cookie existence and mode (mostly `variant`)
export default function useCookieForABTesting(cookieName: string, expectedMode: string): boolean {
  const [hasCookie, setHasCookie] = useState(false);

  useEffect(() => {
    const cookieValue = getCookie(cookieName);
    setHasCookie(cookieValue && cookieValue === expectedMode);
  }, [cookieName, expectedMode]);

  return hasCookie;
}
