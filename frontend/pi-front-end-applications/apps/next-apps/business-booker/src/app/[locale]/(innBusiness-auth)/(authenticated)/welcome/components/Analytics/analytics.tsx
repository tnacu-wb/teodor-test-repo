'use client';

import { analytics } from '@whitbread-eos/utils';
import React, { useEffect } from 'react';

type Props = {
  pageName?: string;
};

const Analytics: React.FC<Props> = ({ pageName }: Props) => {
  useEffect(() => {
    if (pageName === undefined) {
      return;
    }

    const currentData = window?.analyticsData ?? {};
    const currentValidation = window?.analyticsData?.validation;
    analytics.update({
      ...currentData,
      pageName: pageName,
    });

    if (currentValidation) {
      analytics.remove(['validation']);
    }
  }, [pageName]);

  return <></>;
};

export default Analytics;
