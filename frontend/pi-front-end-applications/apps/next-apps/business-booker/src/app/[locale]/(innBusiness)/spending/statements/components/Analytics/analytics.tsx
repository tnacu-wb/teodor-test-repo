'use client';

import { analytics } from '@whitbread-eos/utils';
import React, { useEffect } from 'react';

const ACCOUNT_SUSPENDED = 'PIBA Account suspended,PIBA account exceeds limit';

type Props = {
  pageName?: string;
  invoices?: number;
  isSuspended?: boolean;
};

const Analytics: React.FC<Props> = ({ pageName, invoices, isSuspended }) => {
  useEffect(() => {
    if (pageName === undefined) return;

    analytics.update({
      pageName: pageName,
    });

    const currentValidation = window?.analyticsData?.validation ?? {};
    if (currentValidation) {
      analytics.remove(['validation']);
    }
  }, [pageName]);

  useEffect(() => {
    if (invoices === undefined) return;

    const currentData = window?.analyticsData?.innBusiness ?? {};
    analytics.update({
      innBusiness: {
        ...currentData,
        invoices: invoices,
      },
    });
  }, [invoices]);

  useEffect(() => {
    if (!isSuspended) return;

    analytics.update({
      validation: ACCOUNT_SUSPENDED,
    });
  }, [isSuspended]);

  return <></>;
};

export default Analytics;
