'use client';

import { analytics } from '@whitbread-eos/utils';
import { useEffect } from 'react';

type Props = {
  pageName?: string;
  transactionsCount?: number;
};

const Analytics = ({ pageName, transactionsCount }: Props) => {
  useEffect(() => {
    if (pageName === undefined) {
      return;
    }

    const { validation, ...currentData } = window?.analyticsData ?? {};

    analytics.update({
      ...currentData,
      pageName,
    });

    if (validation) {
      analytics.remove(['validation']);
    }
  }, [pageName]);

  useEffect(() => {
    if (transactionsCount === undefined) {
      return;
    }

    const currentInnBusinessData = window?.analyticsData?.innBusiness ?? {};
    analytics.update({
      innBusiness: {
        ...currentInnBusinessData,
        transactions: transactionsCount,
      },
    });
  }, [transactionsCount]);

  return null;
};

export default Analytics;
