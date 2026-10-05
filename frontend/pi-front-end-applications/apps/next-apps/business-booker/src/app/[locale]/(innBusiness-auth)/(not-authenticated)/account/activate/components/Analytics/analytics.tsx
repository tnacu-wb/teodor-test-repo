'use client';

import { analytics } from '@whitbread-eos/utils';
import React, { useEffect } from 'react';

type Props = {
  pageName?: string;
  validation?: string[];
};

const Analytics: React.FC<Props> = ({ pageName, validation }: Props) => {
  useEffect(() => {
    if (validation === undefined) {
      return;
    }

    const currentData = window?.analyticsData ?? {};
    analytics.update({
      ...currentData,
      validation: validation.join(','),
    });
  }, [validation]);

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
