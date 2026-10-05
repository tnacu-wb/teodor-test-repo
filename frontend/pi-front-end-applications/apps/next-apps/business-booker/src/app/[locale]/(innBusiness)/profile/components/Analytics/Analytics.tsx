'use client';

import { analytics } from '@whitbread-eos/utils';
import { useEffect } from 'react';

type Props = {
  pageName?: string;
};

export function Analytics({ pageName }: Props) {
  useEffect(() => {
    if (pageName === undefined) {
      return;
    }

    const currentData = window?.analyticsData ?? {};
    const currentValidation = window?.analyticsData?.validation ?? '';
    analytics.update({
      ...currentData,
      pageName: pageName,
    });

    if (currentValidation) {
      analytics.remove(['validation']); // reset current validation when accessing the page
    }
  }, [pageName]);

  return null;
}
