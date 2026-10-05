'use client';

import { analytics } from '@whitbread-eos/utils';
import { useEffect } from 'react';

type Props = {
  pageName?: string;
  validation?: string;
};

export function Analytics({ pageName, validation = '' }: Props) {
  useEffect(() => {
    if (pageName === undefined) {
      return;
    }

    const currentData = window?.analyticsData ?? {};
    analytics.update({
      ...currentData,
      pageName,
      validation,
    });
  }, [pageName, validation]);

  return null;
}
