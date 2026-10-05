'use client';

import { LOCALES, ShortCountry } from '@whitbread-eos/api';
import { getAuthCookie } from '@whitbread-eos/utils';
import { getDetailsFromToken, getPayApplicationDetails } from '@whitbread-eos/utils/server';
import { useRouter, usePathname } from 'next/navigation';
import { useCallback } from 'react';

export function usePayAppAccessValidation<T = any>(wizardState?: T) {
  const router = useRouter();
  const pathname = usePathname();

  const validatePayAppAccess = useCallback(async (): Promise<boolean> => {
    if (!pathname.includes('/business-pay/apply')) return true;

    try {
      const token = getAuthCookie();
      if (!token) return false;

      const { email } = getDetailsFromToken(token);
      const appState = wizardState as {
        applicationGuid: string;
        applicationId: string;
        scheme?: string;
      };

      const scheme =
        appState.scheme || (pathname.includes('/de/') ? ShortCountry.DE : ShortCountry.GB);
      const locale = scheme === ShortCountry.DE ? LOCALES.DE : LOCALES.EN;

      if (!appState?.applicationGuid || appState.applicationGuid === '') {
        return true;
      }
      const applicationDetails = await getPayApplicationDetails(
        token,
        appState.applicationGuid,
        appState.applicationId,
        scheme
      );

      const userHasAccess = applicationDetails?.participants?.some(
        (participant: any) => participant.email?.toLowerCase() === email?.toLowerCase()
      );

      if (!userHasAccess) {
        router.replace(`/${locale}/pay-application-access-restricted`);
        return false;
      }

      return true;
    } catch (error) {
      console.error('Error validating participant access:', error);
      return true;
    }
  }, [pathname, router, wizardState]);

  const withValidation = useCallback(
    async (fn: () => void | Promise<void>): Promise<void> => {
      const hasAccess = await validatePayAppAccess();
      if (hasAccess) await fn();
    },
    [validatePayAppAccess]
  );

  return {
    validatePayAppAccess,
    withValidation,
  };
}
