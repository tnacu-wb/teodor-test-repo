'use client';

import {
  ONETRUST_GROUPS_UPDATED_EVENT,
  syncDynatraceConsentFromOneTrust,
} from '@whitbread-eos/utils';
import { useEffect } from 'react';

interface DynatraceConsentSyncProps {
  isDynatraceRumCookieConsentEnabled: boolean;
}

export default function DynatraceConsentSync({
  isDynatraceRumCookieConsentEnabled,
}: DynatraceConsentSyncProps) {
  useEffect(() => {
    const syncConsent = () =>
      syncDynatraceConsentFromOneTrust({ isEnabled: isDynatraceRumCookieConsentEnabled });

    window.addEventListener(ONETRUST_GROUPS_UPDATED_EVENT, syncConsent);
    syncConsent();

    return () => {
      window.removeEventListener(ONETRUST_GROUPS_UPDATED_EVENT, syncConsent);
    };
  }, [isDynatraceRumCookieConsentEnabled]);

  return null;
}
